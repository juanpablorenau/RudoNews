package com.example.rudonews.data.model.calls

import com.example.rudonews.api.Config

data class LoginRequest(
    var grant_type: String = Config.GRANT_TYPE_LOGIN,
    val client_id: String = Config.CLIENT_ID,
    val client_secret: String = Config.CLIENT_SECRET,
    var access_token: String? = null,
    var refresh_token: String? = null,
    var username: String? = null,
    var password: String? = null
)
