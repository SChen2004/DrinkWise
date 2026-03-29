package com.example.cauds.ui.learning

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cauds.ui.navigation.Screen

// ── Generic article list screen ─────────────────────────────────
// Reads title and articles from the ViewModel (set before navigating).
// Used for all 4 subjects — one composable, one NavGraph entry.
@Composable
fun SubjectArticlesScreen(
    navController: NavController,
    viewModel: LearningViewModel
) {
    val title = viewModel.currentSubjectTitle
    val articles = viewModel.currentSubjectArticles

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Back button
        item {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }

        // Title and time badge
        item {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "3 minutes",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Article tiles — tapping opens the article reader
        items(articles) { article ->
            HorizontalDivider(thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(16.dp))

            LearningTileCard(
                title = article.title,
                description = article.description,
                imageRes = article.imageRes,
                onClick = {
                    viewModel.selectArticle(article)
                    navController.navigate(Screen.ArticlePage.route)
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}