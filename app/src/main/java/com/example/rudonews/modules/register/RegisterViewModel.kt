package com.example.rudonews.modules.register

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rudonews.App
import com.example.rudonews.R
import com.example.rudonews.api.ApiUtils
import com.example.rudonews.api.RetrofitClient
import com.example.rudonews.api.Token
import com.example.rudonews.data.model.Department
import com.example.rudonews.data.model.User
import com.example.rudonews.data.model.calls.RegisterRequest
import com.example.rudonews.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class RegisterViewModel : ViewModel() {

    val username = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>("")

    val usernameError = MutableLiveData(0)
    val emailError = MutableLiveData(0)

    val departmentList = MutableLiveData<MutableList<Department>>(mutableListOf())

    val hasCapitalLetter = MutableLiveData<Boolean>(false)
    val hasLowercase = MutableLiveData<Boolean>(false)
    val hasNumber = MutableLiveData<Boolean>(false)
    val hasMinSize = MutableLiveData<Boolean>(false)
    val hasSpecialCharacter = MutableLiveData<Boolean>(false)

    val areAllParametersOk = MutableLiveData<Boolean>(false)

    val eventRegisterSuccessful = MutableLiveData<Boolean>()
    val eventLoggedUserSuccessful = MutableLiveData<Boolean>()
    val existsServerResponse = MutableLiveData<Boolean>()
    val isNetworkAvailable = MutableLiveData<Boolean>()

    fun checkRegister() {
        val checkUsername = checkUsername()
        val checkEmail = checkEmail()
        val checkPassword = checkPassword()

        areAllParametersOk.value = checkUsername && checkEmail && checkPassword
    }

    fun checkUsername(): Boolean {
        return when {
            username.value.isNullOrEmpty() -> {
                usernameError.value = R.string.error_empty
                false
            }
            !username.value.toString().isValidUsername() -> {
                usernameError.value = R.string.error_username
                false
            }
            else -> {
                usernameError.value = 0
                true
            }
        }
    }

    fun checkEmail(): Boolean {
        return when {
            email.value.isNullOrEmpty() -> {
                emailError.value = R.string.error_empty
                false
            }
            !email.value.toString().isValidEmail() -> {
                emailError.value = R.string.error_email
                false
            }
            else -> {
                emailError.value = 0
                true
            }
        }
    }

    fun checkPassword(): Boolean {
        if (password.value.toString().isNotEmpty()) {
            val checkCapitalLetter = checkCapitalLetter()
            val checkLowercase = checkLowercase()
            val checkMinSize = checkMinSize()
            val checkNumber = checkNumber()
            val checkSpecialCharacter = checkSpecialCharacter()

            return checkCapitalLetter &&
                checkLowercase &&
                checkMinSize &&
                checkNumber &&
                checkSpecialCharacter
        } else {
            hasCapitalLetter.value = false
            hasLowercase.value = false
            hasNumber.value = false
            hasMinSize.value = false
            hasSpecialCharacter.value = false

            return false
        }
    }

    private fun checkCapitalLetter(): Boolean {
        hasCapitalLetter.value = password.value.toString().hasUpperCase()
        return hasCapitalLetter.value == true
    }

    private fun checkLowercase(): Boolean {
        hasLowercase.value = password.value.toString().hasLowerCase()
        return hasLowercase.value == true
    }

    private fun checkMinSize(): Boolean {
        hasMinSize.value = password.value.toString().isValidPasswordLength()
        return hasMinSize.value == true
    }

    private fun checkNumber(): Boolean {
        hasNumber.value = password.value.toString().hasNumber()
        return hasNumber.value == true
    }

    private fun checkSpecialCharacter(): Boolean {
        hasSpecialCharacter.value = password.value.toString().hasSpecialCharacter()
        return hasSpecialCharacter.value == true
    }

    fun register(register: RegisterRequest) {
        if (ApiUtils.isNetworkAvailable(App.instance)) {
            CoroutineScope(Dispatchers.IO).launch {
                RetrofitClient().apiCall(
                    {
                        RetrofitClient().register(register)
                    },
                    object : RetrofitClient.RemoteEmitter {
                        override fun onResponse(response: Response<Any>) {
                            val token = response.body() as? Token
                            token?.let {
                                App.preferences.setAccessToken(it.access_token)
                                App.preferences.setRefreshToken(it.refresh_token)
                            }
                            eventRegisterSuccessful.value = response.isSuccessful
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
