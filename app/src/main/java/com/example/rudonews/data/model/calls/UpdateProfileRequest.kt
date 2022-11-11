package com.example.rudonews.data.model.calls

data class UpdateProfileRequest(
    var fullname: String? = null,
    var email: String? = null,
    var departments: String?
)
