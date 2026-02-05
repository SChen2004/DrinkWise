package com.example.cauds.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cauds.ui.navigation.Screen

// AUD test
@Composable
fun AudScreen(navController: NavController, vm: OnboardingViewModel = viewModel()) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("AUD Test (Alcohol Use Disorders Identification Test)", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Placeholder for AUD questions. For now, we'll mark as 'Moderate Risk'.")
        
        Button(onClick = {
            vm.audScore = "Moderate Risk"
            navController.navigate(Screen.OnboardingInfo.route)
        }) { Text("Next: Personal Info") }
    }
}

// Personal Information
@Composable
fun InfoScreen(navController: NavController, vm: OnboardingViewModel = viewModel()) {
    var name by remember { mutableStateOf(vm.name) }
    
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Personal Information", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = name,
            onValueChange = { name = it; vm.name = it },
            label = { Text("Your Name") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = {
            navController.navigate(Screen.OnboardingGoals.route)
        }) { Text("Next: Goals") }
    }
}

// Goals
@Composable
fun GoalScreen(navController: NavController, vm: OnboardingViewModel = viewModel()) {
    var goals by remember { mutableStateOf(vm.goals) }
    
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Your Goals", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = goals,
            onValueChange = { goals = it; vm.goals = it },
            label = { Text("What are your goals?") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = {
            navController.navigate(Screen.OnboardingPrivacy.route)
        }) { Text("Next: Privacy") }
    }
}

@Composable
fun PrivacyScreen(navController: NavController, vm: OnboardingViewModel = viewModel()) {
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Privacy Settings", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Text("We value your privacy. Your data will be stored securely.")
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (errorMsg.isNotEmpty()) {
            Text(errorMsg, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                isLoading = true
                errorMsg = ""
                vm.submitData(
                    onSuccess = {
                        isLoading = false
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onError = {
                        isLoading = false
                        errorMsg = it
                    }
                )
            },
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Finish & Go to Dashboard")
            }
        }
    }
}