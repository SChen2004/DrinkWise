package com.example.cauds.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.example.cauds.ui.navigation.Screen
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.example.cauds.data.model.Sex

@Composable
fun OnboardingNameScreen(
    navController: NavController,
    viewModel: OnboardingViewModel
) {
    var name by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }

    val nameRegex = Regex("^[\\p{L} '-]*$")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            TextButton(onClick = { navController.popBackStack() }) {
                Text("CANCEL")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "What should we call you?",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { newValue ->
                name = newValue  // Always accept the input
                hasError = !nameRegex.matches(newValue)  // Just flag it
            },
            label = { Text("Name") },
            singleLine = true,
            isError = hasError,
            supportingText = {
                if (hasError) {
                    Text("Must not contain numbers or special characters")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                viewModel.updateName(name.trim())
                navController.navigate(Screen.OnboardingSex.route)
            },
            enabled = name.isNotBlank() && !hasError,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("NEXT")
        }
    }
}

@Composable
fun OnboardingSexScreen(
    navController: NavController,
    viewModel: OnboardingViewModel
) {
    val options = Sex.entries
    var selectedOption by remember { mutableStateOf<Sex?>(null) }

    val helperText = when (selectedOption) {
        Sex.PREFER_NOT_TO_SAY -> "We'll provide standard recommendations."
        null -> ""
        else -> "This helps us personalize your recommended drink limits"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
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
            text = "What is your biological sex?",
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
                horizontalArrangement = Arrangement.Center
            ) {
                RadioButton(
                    selected = selectedOption == option,
                    onClick = { selectedOption = option }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = option.displayName,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = helperText,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.updateBiologicalSex(selectedOption!!)
                navController.navigate(Screen.OnboardingPurpose.route)
            },
            enabled = selectedOption != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("NEXT")
        }
    }
}

@Composable
fun OnboardingPurposeScreen(
    navController: NavController,
    viewModel: OnboardingViewModel
) {
    val options = listOf(
        "Cut back on drinking",
        "Understand my habits",
        "Just exploring",
        "Supporting someone else"
    )

    var selectedOption by remember { mutableStateOf<String?>(null) }

    val buttonText = if (selectedOption == "Supporting someone else") "CONTINUE" else "START ASSESSMENT"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
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
            text = "What brings you here today?",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "I want to...",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        options.forEach { option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                RadioButton(
                    selected = selectedOption == option,
                    onClick = { selectedOption = option }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = option,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (selectedOption == "Supporting someone else") {
                    viewModel.updateSupportingFriend(true)
                    viewModel.saveOnboardingData()
                    navController.navigate(Screen.Dashboard.route) // TODO: Change route to knowledge page
                } else {
                    navController.navigate(Screen.AudQuiz.route)
                }
            },
            enabled = selectedOption != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(buttonText)
        }
    }
}