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
import com.example.cauds.data.model.NotificationPreferences
import com.example.cauds.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationPreferencesScreen(
    navController: NavController,
    viewModel: OnboardingViewModel
) {

    var ready by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        navController.navigate(Screen.UserTestGreenScreen.route)
        ready = true
    }

    if (!ready) return

    var completedOnboarding by remember { mutableStateOf(false) }
    LaunchedEffect("loadOnboarding") {
        viewModel.getCompletedOnboarding { completed ->
            completedOnboarding = completed
        }
    }

    // State for each preference, matching your NotificationPreferences fields
    var dailyCheckin by remember { mutableStateOf(false) }
    var dailyEncouragement by remember { mutableStateOf(false) }
    var weeklyReflection by remember { mutableStateOf(false) }
    var monthlyProgress by remember { mutableStateOf(false) }

    // Pair each state with its label and description
    val options = listOf(
        Triple("Daily check-ins", "A gentle nudge to log or reflect on your day.", dailyCheckin) to { v: Boolean -> dailyCheckin = v },
        Triple("Daily encouragement", "A short message to keep you motivated.", dailyEncouragement) to { v: Boolean -> dailyEncouragement = v },
        Triple("Weekly reflections", "A thoughtful prompt to look back on your week.", weeklyReflection) to { v: Boolean -> weeklyReflection = v },
        Triple("Monthly progress", "A summary of patterns and changes over time.", monthlyProgress) to { v: Boolean -> monthlyProgress = v }
    )

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
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "How would you like to stay on track?",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1f))

        options.forEach { (triple, onToggle) ->
            val (title, description, checked) = triple
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle(!checked) }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "We can send gentle reminders and updates. You're always in control of how often you hear from us.",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val prefs = NotificationPreferences(
                    dailyCheckin = dailyCheckin,
                    dailyEncouragement = dailyEncouragement,
                    weeklyReflection = weeklyReflection,
                    monthlyProgress = monthlyProgress
                )
                viewModel.saveNotificationPreferences(prefs)
                val isFromAccount = navController.previousBackStackEntry?.destination?.route == Screen.Account.route
                if (isFromAccount || completedOnboarding) {
                    navController.popBackStack()
                } else {
                    navController.navigate(Screen.FavouriteDrinks.route)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("CONTINUE")
        }
    }
    }
}