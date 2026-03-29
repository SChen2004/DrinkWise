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

    // ── Subject selection ───────────────────────────────────────
    // Set before navigating to SubjectArticlesScreen.
    // Same pattern as selectArticle — caller sets the data,
    // then the destination screen reads it.

    var currentSubjectTitle by mutableStateOf("")
        private set

    var currentSubjectArticles by mutableStateOf<List<Article>>(emptyList())
        private set

    fun selectSubject(title: String, articles: List<Article>) {
        currentSubjectTitle = title
        currentSubjectArticles = articles
    }

    // ── Article selection (unchanged) ───────────────────────────

    var currentArticle by mutableStateOf<Article?>(null)
        private set

    var currentArticleBlocks by mutableStateOf<List<ArticleBlock>>(emptyList())
        private set

    fun selectArticle(article: Article) {
        currentArticle = article
        currentArticleBlocks = articleRepo.getArticleContent(article)
    }
}