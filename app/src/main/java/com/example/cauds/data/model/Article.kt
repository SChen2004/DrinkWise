package com.example.cauds.data.model

data class Article(
    val id: String,
    val title: String,
    val description: String,
    val imageRes: Int,
    val contentRes: Int,
    val tidbit: String = ""
)
