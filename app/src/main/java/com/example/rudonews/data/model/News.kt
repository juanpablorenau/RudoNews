package com.example.rudonews.data.model

import java.io.Serializable

data class News(
    var id: String? = null,
    var creation_date: String? = null,
    var title: String? = null,
    var subtitle: String? = null,
    var short_description: String? = null,
    var category: Category? = null,
    var is_favorite: Boolean? = null,
    var image: String? = null
) : Serializable
