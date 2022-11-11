package com.example.rudonews.modules.change_password

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rudonews.App
import com.example.rudonews.api.ApiUtils
import com.example.rudonews.api.RetrofitClient
import com.example.rudonews.data.model.ChangePassword
import com.example.rudonews.utils.hasLowerCase
import com.example.rudonews.utils.hasNumber
import com.example.rudonews.utils.hasUpperCase
import com.example.rudonews.utils.isValidPasswordLength
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class ChangePasswordViewModel : ViewModel() {
    val currentPassword = MutableLiveData<String>()
    val newPassword = MutableLiveData<String>()
    val repeatNewPassword = MutableLiveData<String>()

    val areTheyEqual = MutableLiveData<Boolean>()
    val hasCorrectFormat = MutableLiveData<Boolean>()

    val eventSavePasswordSuccessful = MutableLiveData<Boolean>()
    val existsServerResponse = MutableLiveData<Boolean>()
    val isNetworkAvailable = MutableLiveData<Boolean>()

    fun saveNewPassword() {
        if (newPassword.value.equals(repeatNewPassword.value)) {
            if (checkNewPassword()) {
                if (ApiUtils.isNetworkAvailable(App.instance)) {
                    CoroutineScope(Dispatchers.IO).launch {
                        RetrofitClient().apiCall(
                            {
                                RetrofitClient().changePassword(
                                    ChangePassword(
                                        currentPassword.value,
                                        newPassword.value
                                    )
                                )
                            },
                            object : RetrofitClient.RemoteEmitter {
                                override fun onResponse(response: Response<Any>) {
                                    eventSavePasswordSuccessful.value = response.isSuccessful
                                }

                                override fun onError(
                                    errorType: RetrofitClient.ErrorType,
                                    msg: String
                                ) {
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
            } else {
                hasCorrectFormat.value = false
            }
        } else {
            areTheyEqual.value = false
        }
    }

    private fun checkNewPassword(): Boolean {
        return !newPassword.value.isNullOrEmpty() &&
            newPassword.value.toString().isValidPasswordLength() &&
            newPassword.value.toString().hasUpperCase() &&
            newPassword.value.toString().hasLowerCase() &&
            newPassword.value.toString().hasNumber()
    }
}
