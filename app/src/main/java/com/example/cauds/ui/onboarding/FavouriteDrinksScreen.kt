package com.example.cauds.ui.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cauds.ui.navigation.Screen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteDrinksScreen(
    navController: NavController,
    viewModel: OnboardingViewModel
) {

    var ready by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        navController.navigate(Screen.UserTestGreenScreen.route)
        ready = true
    }

    if (!ready) return

    val options = listOf("Wine", "Beer", "Liquor", "Mixed", "Cider / Seltzer")
    var selectedOptions by remember { mutableStateOf(setOf<String>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 48.dp)
                .padding(bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                    .clickable {
                        val checked = !selectedOptions.contains(option)
                        if (checked && selectedOptions.size < 3) {
                            selectedOptions = selectedOptions + option
                        } else if (!checked) {
                            selectedOptions = selectedOptions - option
                        }
                    }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Checkbox(
                    checked = selectedOptions.contains(option),
                    onCheckedChange = null
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
                val isFromAccount = navController.previousBackStackEntry?.destination?.route == Screen.Account.route
                if (isFromAccount) {
                    navController.popBackStack()
                } else {
                    viewModel.completeOnboarding()
                    navController.navigate(Screen.UserTestBeginScreen.route) {
                        popUpTo("onboarding_name") { inclusive = true }
                    }
                }
            },
            enabled = selectedOptions.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("DONE")
        }

        TextButton(
            onClick = {
                val isFromAccount = navController.previousBackStackEntry?.destination?.route == Screen.Account.route
                if (isFromAccount) {
                    navController.popBackStack()
                } else {
                    viewModel.completeOnboarding()
                    navController.navigate(Screen.UserTestBeginScreen.route) {
                        popUpTo("onboarding_name") { inclusive = true }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("SKIP")
        }
    }
    }
}