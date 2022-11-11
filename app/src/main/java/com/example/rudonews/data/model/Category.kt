package com.example.rudonews.data.model

import java.io.Serializable

data class Category(
    var id: Int,
    var name: String,
    var subcategories: List<Category>
) : Serializable
