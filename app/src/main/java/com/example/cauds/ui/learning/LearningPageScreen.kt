package com.example.cauds.ui.learning

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cauds.R
import com.example.cauds.data.model.Subject
import com.example.cauds.ui.navigation.Screen

// ── Reusable tile card ──────────────────────────────────────────
// Takes raw fields instead of a specific model, so it works for
// both Article and Subject (or anything else with a title/desc/image).
// The caller decides what happens on click via the lambda.
@Composable
fun LearningTileCard(
    title: String,
    description: String,
    imageRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier       // new parameter, defaults to empty
) {
    Column(
        modifier = modifier              // apply it here first
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        // Title row — text on left, arrow on right
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Open"
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Description below the title
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Image at the bottom (Figma layout: text on top, image below)
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = title,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

// ── Learning landing page ───────────────────────────────────────
@Composable
fun LearningPageScreen(
    navController: NavController,
    viewModel: LearningViewModel
) {
    // Define subject tiles right here — they're part of this screen's layout,
    // not dynamic data, so they don't need to live in the ViewModel.
    val understandingAud = Subject(
        title = "Understanding AUD",
        description = "Learn more about AUD, how it affects your health, where to get help, facts about alcohol, and how you can reduce your intake.",
        imageRes = R.drawable.aud_couch,
        route = Screen.UnderstandingAud.route
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Increase your\nknowledge\nof AUD",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // The "Understanding AUD" folder tile — hardcoded into this screen's layout
        item {
            LearningTileCard(
                title = understandingAud.title,
                description = understandingAud.description,
                imageRes = understandingAud.imageRes,
                onClick = { navController.navigate(understandingAud.route) }
            )
        }

        // You'll add more items here as the design grows —
        // more subjects, support links section, etc.
    }
}