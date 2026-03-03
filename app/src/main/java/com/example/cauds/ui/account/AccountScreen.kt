package com.example.cauds.ui.account

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cauds.data.model.AudRisk
import com.example.cauds.ui.navigation.Screen

@Composable
fun AccountScreen(
    navController: NavController,
    viewModel: AccountViewModel = viewModel()
) {
    val errorMessage = viewModel.errorMessage

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Account", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // AudQuiz button
        viewModel.getAudRiskLevel()
        if (viewModel.audRisk == AudRisk.DEFAULT_RISK) {
            Button(
                onClick = { navController.navigate(Screen.AudQuiz.route) }
            ) {
                Text("Take AUD Questionnaire")
            }
        } else {
            Text("Your AUD Risk Level: ${viewModel.audRisk}")
        }
        Spacer(modifier = Modifier.height(12.dp))

        // onboarding debug
        Button(
            onClick = { navController.navigate(Screen.OnboardingName.route) }
        ) {
            Text("onboarding preview")
        }

        // logout button
        Button(
            onClick = {
                viewModel.performLogout()
                navController.navigate(Screen.Login.route) {
                    popUpTo(0)
                }
            }
        ) {
            Text("Logout")
        }
    }
}