package com.example.cauds.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cauds.ui.navigation.Screen


@Composable
fun FavouriteDrinksScreen(
    navController: NavController,
    viewModel: OnboardingViewModel
) {
    val options = listOf("Wine", "Beer", "Liquor", "Mixed", "Cider / Seltzer")
    var selectedOptions by remember { mutableStateOf(setOf<String>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Almost done.",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "What do you usually drink?",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1f))

        options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Checkbox(
                    checked = selectedOptions.contains(option),
                    onCheckedChange = { checked ->
                        if (checked && selectedOptions.size < 3) {
                            selectedOptions = selectedOptions + option
                        } else if (!checked) {
                            selectedOptions = selectedOptions - option
                        }
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = option,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Choose up to 3 types",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                viewModel.saveFavouriteDrinks(selectedOptions.toList())
                viewModel.completeOnboarding()
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo("onboarding_name") { inclusive = true }
                }
            },
            enabled = selectedOptions.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("DONE")
        }

        TextButton(
            onClick = {
                viewModel.completeOnboarding()
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo("onboarding_name") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("SKIP")
        }
    }
}