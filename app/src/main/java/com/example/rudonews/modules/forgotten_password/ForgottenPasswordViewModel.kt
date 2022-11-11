package com.example.rudonews.modules.forgotten_password

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ForgottenPasswordViewModel : ViewModel() {

    val email = MutableLiveData<String>()
    val eventResetPasswordSuccessful = MutableLiveData<Boolean>()

    fun resetPassword() {
        eventResetPasswordSuccessful.value = email.value?.isNotEmpty() == true
    }
}

