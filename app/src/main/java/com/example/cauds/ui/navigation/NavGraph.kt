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
import androidx.compose.runtime.remember
import com.example.cauds.ui.account.AccountScreen
import com.example.cauds.ui.calendar.CalendarScreen
import com.example.cauds.ui.calendar.CalendarViewModel
import com.example.cauds.ui.calendar.DaySummaryScreen
import com.example.cauds.ui.onboarding.OnboardingViewModel
import com.example.cauds.ui.drinklog.DrinkLogScreen
import com.example.cauds.ui.onboarding.AudQuizScreen

@Composable
fun NavGraph(navController: NavHostController, startDestination: String) {
    // Share the onboarding view model
    val onboardingViewModel: OnboardingViewModel = viewModel()

    NavHost(navController = navController, startDestination = startDestination) {

        // Auth
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.SignUp.route) { SignUpScreen(navController) }

        // Onboarding
        composable(Screen.AudTest.route) { AudScreen(navController, onboardingViewModel) }
        composable(Screen.OnboardingInfo.route) { InfoScreen(navController, onboardingViewModel) }
        composable(Screen.OnboardingPrivacy.route) { PrivacyScreen(navController, onboardingViewModel) }
        composable(Screen.AudQuiz.route) { AudQuizScreen(navController, onboardingViewModel) }

        // Dashboard / Home
        composable(Screen.Dashboard.route) { DashboardScreen(navController) }

        // Main Sections (Accessible from Dashboard or Bottom Nav)
        composable(Screen.Tracking.route) { DrinkLogScreen(navController) }
        composable(Screen.Journal.route) { Text("Journal Screen") }
        composable(Screen.Calendar.route) { CalendarScreen(
            onDayClick = { date -> navController.navigate("day_summary/${date}") }
        ) }
        composable(Screen.Support.route) { Text("Support Screen") }
        composable(Screen.Account.route) { AccountScreen(navController) }

        // Secondary Features
        composable(Screen.DaySummary.route) { backStackEntry ->

            val dateStr = backStackEntry.arguments?.getString("date") ?: return@composable


            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Calendar.route)
            }

            val sharedCalendarViewModel: CalendarViewModel = viewModel(parentEntry)

            DaySummaryScreen(
                date = dateStr,
                calendarViewModel = sharedCalendarViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}