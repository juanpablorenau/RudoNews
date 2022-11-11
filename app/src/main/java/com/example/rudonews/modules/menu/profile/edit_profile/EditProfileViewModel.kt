package com.example.rudonews.modules.menu.profile.edit_profile

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rudonews.App
import com.example.rudonews.api.ApiUtils
import com.example.rudonews.api.RetrofitClient
import com.example.rudonews.data.model.Department
import com.example.rudonews.data.model.User
import com.example.rudonews.data.model.calls.UpdateProfileRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class EditProfileViewModel : ViewModel() {

    val username = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val departmentsText = MutableLiveData<String>()

    val departmentList = MutableLiveData<MutableList<Department>>(mutableListOf())

    val eventLoggedUserSuccessful = MutableLiveData<Boolean>()
    val eventSaveChangeSuccessful = MutableLiveData<Boolean>()
    val existsServerResponse = MutableLiveData<Boolean>()
    val isNetworkAvailable = MutableLiveData<Boolean>()

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

    fun saveChanges() {
        val updateProfileRequest =
            UpdateProfileRequest(
                username.value,
                email.value,
                departmentList.value?.map { it.id }?.joinToString(" ")
            )
        if (ApiUtils.isNetworkAvailable(App.instance)) {
            CoroutineScope(Dispatchers.IO).launch {
                RetrofitClient().apiCall(
                    {
                        RetrofitClient().updateProfile(updateProfileRequest)
                    },
                    object : RetrofitClient.RemoteEmitter {
                        override fun onResponse(response: Response<Any>) {
                            if (response.isSuccessful) {
                                val user = response.body() as? User
                                App.user = user
                                eventSaveChangeSuccessful.value = true
                            } else {
                                eventSaveChangeSuccessful.value = false
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
