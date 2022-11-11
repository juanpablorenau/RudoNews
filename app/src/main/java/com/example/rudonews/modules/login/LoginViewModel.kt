package com.example.rudonews.modules.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rudonews.App
import com.example.rudonews.api.ApiUtils
import com.example.rudonews.api.RetrofitClient
import com.example.rudonews.api.Token
import com.example.rudonews.data.model.User
import com.example.rudonews.data.model.calls.LoginRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginViewModel : ViewModel() {

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    val eventLoginSuccessful = MutableLiveData<Boolean>()
    val eventLoggedUserSuccessful = MutableLiveData<Boolean>()
    val existsServerResponse = MutableLiveData<Boolean>()
    val isNetworkAvailable = MutableLiveData<Boolean>()

    fun logIn() {
        val loginRequest = email.value?.let { email ->
            password.value?.let { password ->
                LoginRequest(username = "juanpa@rudo.es", password = "123456789aA!")
            }
        }
        if (ApiUtils.isNetworkAvailable(App.instance)) {
            CoroutineScope(Dispatchers.IO).launch {
                RetrofitClient().apiCall(
                    {
                        loginRequest?.let { RetrofitClient().logIn(it) }
                    },
                    object : RetrofitClient.RemoteEmitter {
                        override fun onResponse(response: Response<Any>) {
                            if (response.isSuccessful) {
                                val token = response.body() as? Token
                                token?.let {
                                    App.preferences.setAccessToken(it.access_token)
                                    App.preferences.setRefreshToken(it.refresh_token)
                                }
                                eventLoginSuccessful.value = true
                            } else {
                                eventLoginSuccessful.value = false
                            }
                        }

                        override fun onError(errorType: RetrofitClient.ErrorType, msg: String) {
                            existsServerResponse.value = false
                            Log.e("Api errortype", errorType.toString())
                            Log.e("Api message", msg)
                        }
                    }
                )
            }
        } else {
            isNetworkAvailable.value = false
        }
    }

    fun getLoggedUser() {
        if (ApiUtils.isNetworkAvailable(App.instance)) {
            CoroutineScope(Dispatchers.IO).launch {
                RetrofitClient().apiCall(
                    {
                        RetrofitClient().getLoggedUser()
                    },
                    object : RetrofitClient.RemoteEmitter {
                        override fun onResponse(response: Response<Any>) {
                            if (response.isSuccessful) {
                                val user = response.body() as? User
                                user?.let {
                                    App.user = it
                                    eventLoggedUserSuccessful.value = true
                                }
                            } else {
                                eventLoggedUserSuccessful.value = false
                            }
                        }

                        override fun onError(errorType: RetrofitClient.ErrorType, msg: String) {
                            existsServerResponse.value = false
                            Log.e("Api errortype", errorType.toString())
                            Log.e("Api message", msg)
                        }
                    }
                )
            }
        } else {
            isNetworkAvailable.value = false
        }
    }
}
