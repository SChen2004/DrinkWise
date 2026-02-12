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
        composable(Screen.AudTest.route) { AudScreen(navController, onboardingViewModel) }
        composable(Screen.OnboardingInfo.route) { InfoScreen(navController, onboardingViewModel) }
        composable(Screen.OnboardingPrivacy.route) { PrivacyScreen(navController, onboardingViewModel) }

        // Dashboard / Home
        composable(Screen.Dashboard.route) { DashboardScreen(navController) }

        // Main Sections (Accessible from Dashboard or Bottom Nav)
        composable(Screen.Tracking.route) { Text("Drink Tracking Screen") }
        composable(Screen.Journal.route) { Text("Journal Screen") }
        composable(Screen.Calendar.route) { Text("Calendar Screen") }
        composable(Screen.Support.route) { Text("Support Screen") }
        composable(Screen.Account.route) { Text("Account Screen") }

        // Secondary Features
        // Not yet decide
    }
}