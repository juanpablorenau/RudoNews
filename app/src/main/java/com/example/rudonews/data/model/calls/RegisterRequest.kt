package com.example.rudonews.data.model.calls

data class RegisterRequest(
    var fullname: String? = null,
    var email: String? = null,
    var password: String? = null,
    var departments: String? = null
)
