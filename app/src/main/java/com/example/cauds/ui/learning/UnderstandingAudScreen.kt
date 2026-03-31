package com.example.cauds.ui.learning

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cauds.ui.navigation.Screen
import com.example.cauds.ui.theme.BowlbyOne

private val Cream = Color(0xFFFEF5DC)
private val DarkNavy = Color(0xFF121E30)

@Composable
fun UnderstandingAudScreen(
    navController: NavController,
    viewModel: LearningViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream),
        // Matches LearningPageScreen horizontal padding (28dp).
        // Vertical set to 0 so we control spacing manually with Spacers.
        contentPadding = PaddingValues(horizontal = 28.dp, vertical = 0.dp)
    ) {
        // Back button — pushed 24dp down from the top of the screen
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

        // Title — 24dp gap between back button and heading
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Understanding\nAUD",
                fontFamily = BowlbyOne,
                fontSize = 36.sp,
                color = DarkNavy,
                lineHeight = 36.sp
            )
            Spacer(modifier = Modifier.height(72.dp))
        }

        // Subject tiles separated by dividers
        itemsIndexed(subjects) { index, subject ->

            // no horizontal divider on top of first tile.
            if (index > 0) {
                HorizontalDivider(thickness = 0.5.dp, color = DarkNavy)
                Spacer(modifier = Modifier.height(24.dp))
            }

            LearningTileCard(
                title = subject.title,
                description = subject.description,
                imageRes = subject.imageRes,
                onClick = {
                    val articles = subjectArticlesMap[subject.route] ?: emptyList()
                    viewModel.selectSubject(subject.title, articles, subject.tidbit)
                    navController.navigate(Screen.SubjectArticles.route)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Bottom breathing room
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}