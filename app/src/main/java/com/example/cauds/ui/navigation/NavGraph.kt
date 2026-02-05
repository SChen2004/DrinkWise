package com.example.cauds.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cauds.ui.auth.LoginScreen
import com.example.cauds.ui.auth.SignUpScreen
import com.example.cauds.ui.dashboard.DashboardScreen
import com.example.cauds.ui.onboarding.*
import androidx.compose.material3.Text
import com.example.cauds.ui.onboarding.OnboardingViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    // Share the onboarding view model
    val onboardingViewModel: OnboardingViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.Login.route) {

        // Auth
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.SignUp.route) { SignUpScreen(navController) }

        // Onboarding
        composable(Screen.OnboardingAud.route) { AudScreen(navController, onboardingViewModel) }
        composable(Screen.OnboardingInfo.route) { InfoScreen(navController, onboardingViewModel) }
        composable(Screen.OnboardingGoals.route) { GoalScreen(navController, onboardingViewModel) }
        composable(Screen.OnboardingPrivacy.route) { PrivacyScreen(navController, onboardingViewModel) }

        // Dashboard
        composable(Screen.Dashboard.route) { DashboardScreen(navController) }

        // Sub Features (Placeholders)
        composable(Screen.Tracking.route) { Text("Tracking Screen") }
        composable(Screen.Insight.route) { Text("Insight Screen") }
        composable(Screen.Account.route) { Text("Account Screen") }
        composable(Screen.Support.route) { Text("Support Screen") }
    }
}