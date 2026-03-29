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
import com.example.cauds.R
import com.example.cauds.data.model.Subject

@Composable
fun UnderstandingAudScreen(
    navController: NavController
) {
    // These 4 subjects are hardcoded layout — each one navigates
    // to a sub-screen that will show a list of articles.
    // You'll need to add these routes to your Screen sealed class.
    val subjects = listOf(
        Subject(
            title = "Learning More About AUD",
            description = "What is AUD, what are the symptoms and risks, how can you treat it, and what can you do to help your loved ones who have it?",
            imageRes = R.drawable.learning_more_aud,
            route = "learning_more_aud"
        ),
        Subject(
            title = "Getting Help",
            description = "Plan what to do in an emergency, guides for finding treatment, the do's and don'ts of talking about AUD, how to help others, and types of help!...",
            imageRes = R.drawable.getting_help,
            route = "getting_help"
        ),
        Subject(
            title = "Health Information",
            description = "Nutritional tips, information on medications, and facts about alcohol's effects on the body.",
            imageRes = R.drawable.health_info,
            route = "health_info"
        ),
        Subject(
            title = "Facts About Alcohol",
            description = "What is alcohol, and facts about what is considered a standard drink.",
            imageRes = R.drawable.facts_alcohol,
            route = "facts_alcohol"
        )
    )

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
                text = "Understanding\nAUD",
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

        // Render each subject tile with a divider above it.
        // The divider sits outside the padded content to span full width —
        // remember, negative padding crashes Compose, so we handle
        // the divider separately from the padded tile content.
        items(subjects) { subject ->
            HorizontalDivider(thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // The tile itself gets horizontal padding
            LearningTileCard(
                title = subject.title,
                description = subject.description,
                imageRes = subject.imageRes,
                onClick = { navController.navigate(subject.route) },
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}