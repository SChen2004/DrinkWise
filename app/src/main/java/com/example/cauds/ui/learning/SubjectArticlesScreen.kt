package com.example.cauds.ui.learning

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cauds.ui.navigation.Screen
import com.example.cauds.ui.theme.BowlbyOne
import com.example.cauds.ui.theme.Poppins

private val Cream = Color(0xFFFEF5DC)
private val DarkNavy = Color(0xFF121E30)

@Composable
fun SubjectArticlesScreen(
    navController: NavController,
    viewModel: LearningViewModel
) {
    val title = viewModel.currentSubjectTitle
    val articles = viewModel.currentSubjectArticles
    val tidbit = viewModel.currentSubjectTidbit

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream),
        contentPadding = PaddingValues(horizontal = 28.dp, vertical = 0.dp)
    ) {
        // Back button
        item {
            Spacer(modifier = Modifier.height(24.dp))
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.offset(x = (-12).dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = DarkNavy
                )
            }
        }

        // Title
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = title,
                fontFamily = BowlbyOne,
                fontSize = 52.sp,
                color = DarkNavy,
                lineHeight = 52.sp
            )
            if (tidbit.isNotEmpty()) {
//                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = tidbit,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = DarkNavy,
                    modifier = Modifier
                        .background(Color(0xFFAFC9DC))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(72.dp))
        }

        // Article tiles — no divider above the first one
        itemsIndexed(articles) { index, article ->
            if (index > 0) {
                HorizontalDivider(thickness = 0.5.dp, color = DarkNavy)
                Spacer(modifier = Modifier.height(16.dp))
            }

            LearningTileCard(
                title = article.title,
                description = article.description,
                imageRes = article.imageRes,
                onClick = {
                    viewModel.selectArticle(article)
                    navController.navigate(Screen.ArticlePage.route)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Bottom breathing room
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}