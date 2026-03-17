package com.example.cauds.data.repository

import android.content.Context
import com.example.cauds.R
import com.example.cauds.data.model.Article
import com.example.cauds.data.model.ArticleBlock
import com.google.gson.Gson


class ArticleRepository(private val context: Context) {

    val articles: List<Article> = listOf(
        Article(
            id = "doggy-1",
            title = "Look at this dog he is spectacular",
            description = "he is amazig",
            imageRes = R.drawable.doggy_1,
            contentRes = R.raw.article_aud_basics
        ),
        Article(
            id = "doggy-2",
            title = "would you pet him",
            description = "please pet him",
            imageRes = R.drawable.doggy_2,
            contentRes = R.raw.article_getting_help
        )
    )

    fun getArticleById(id: String): Article? {
        return articles.find { it.id == id }
    }

    fun getArticleContent(article: Article): List<ArticleBlock> {
        val json = context.resources.openRawResource(article.contentRes)
            .bufferedReader()
            .use { it.readText() }

        return Gson().fromJson(json, Array<ArticleBlock>::class.java).toList()
    }
}