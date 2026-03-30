package com.example.cauds.data.model

data class ArticleBlock(
    val type: String,
    val content: String? = null,
    val name: String? = null,
    val items: List<String>? = null,
    val blocks: List<ArticleBlock>? = null
)