package com.example.rudonews.data.model

data class Comment(
    var user: User? = null,
    var text: String? = null
) {
    constructor(id: String, user: User, text: String) : this(user, text)
}
