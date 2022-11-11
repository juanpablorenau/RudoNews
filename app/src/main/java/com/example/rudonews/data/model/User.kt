package com.example.rudonews.data.model

data class User(
    var id: String? = null,
    var fullname: String? = null,
    var email: String? = null,
    var phone: String? = null,
    var departments: List<Department>?,
    var platform: String? = null,
    var device_id: String? = null
)
