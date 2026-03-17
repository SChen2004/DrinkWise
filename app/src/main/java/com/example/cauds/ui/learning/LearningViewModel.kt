package com.example.cauds.ui.learning

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.cauds.data.model.Article
import com.example.cauds.data.model.ArticleBlock
import com.example.cauds.data.repository.ArticleRepository

class LearningViewModel(
    private val articleRepo: ArticleRepository
) : ViewModel() {

    var articles by mutableStateOf<List<Article>>(emptyList())
        private set

    init {
        articles = articleRepo.articles
    }

    var currentArticle by mutableStateOf<Article?>(null)
        private set

    var currentArticleBlocks by mutableStateOf<List<ArticleBlock>>(emptyList())
        private set

    fun selectArticle(article: Article) {
        currentArticle = article
        currentArticleBlocks = articleRepo.getArticleContent(article)
    }
}