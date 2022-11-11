package com.example.rudonews.api

class Token {
    var access_token: String = ""
    var expires_in: Long = 0
    var token_type: String = ""
    var scope: String = ""
    var refresh_token: String = ""

    override fun toString(): String {
        return "$access_token $refresh_token"
    }
}
