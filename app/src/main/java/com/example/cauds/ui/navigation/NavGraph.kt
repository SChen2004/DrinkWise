package com.example.cauds.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cauds.ui.auth.LoginScreen
import com.example.cauds.ui.auth.SignUpScreen
import com.example.cauds.ui.dashboard.DashboardScreen
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import com.example.cauds.ui.account.AccountTestScreen
import com.example.cauds.ui.calendar.CalendarScreen
import com.example.cauds.ui.calendar.CalendarViewModel
import com.example.cauds.ui.calendar.DaySummaryScreen
import com.example.cauds.ui.onboarding.OnboardingViewModel
import com.example.cauds.ui.drinklog.DrinkLogScreen
import com.example.cauds.ui.onboarding.AudQuizScreen
import com.example.cauds.ui.onboarding.FavouriteDrinksScreen
import com.example.cauds.ui.onboarding.NotificationPreferencesScreen
import com.example.cauds.ui.onboarding.OnboardingNameScreen
import com.example.cauds.ui.onboarding.OnboardingPurposeScreen
import com.example.cauds.ui.onboarding.OnboardingSexScreen
import com.example.cauds.ui.onboarding.QuizResultScreen

@Composable
fun NavGraph(navController: NavHostController, startDestination: String) {
    // Share the onboarding view model
    val onboardingViewModel: OnboardingViewModel = viewModel()

    NavHost(navController = navController, startDestination = startDestination) {

        // Auth
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.SignUp.route) { SignUpScreen(navController) }
        composable(Screen.ForgotPassword.route) { com.example.cauds.ui.auth.ForgotPasswordScreen(navController) }

        // Onboarding
        composable(Screen.AudQuiz.route) { AudQuizScreen(navController, onboardingViewModel) }
        composable(Screen.QuizResult.route) { QuizResultScreen(navController, onboardingViewModel) }
        composable(Screen.OnboardingName.route) { OnboardingNameScreen(navController, onboardingViewModel) }
        composable(Screen.OnboardingSex.route) { OnboardingSexScreen(navController, onboardingViewModel) }
        composable(Screen.OnboardingPurpose.route) { OnboardingPurposeScreen(navController, onboardingViewModel) }
        composable(Screen.NotificationPreferences.route) { NotificationPreferencesScreen(navController, onboardingViewModel)}
        composable(Screen.FavouriteDrinks.route) { FavouriteDrinksScreen(navController, onboardingViewModel) }



        // Dashboard / Home
        composable(Screen.Dashboard.route) { DashboardScreen(navController) }

        // Main Sections (Accessible from Dashboard or Bottom Nav)
        composable(Screen.Tracking.route) { DrinkLogScreen(navController) }
        composable(Screen.ManageDrinks.route) { backStackEntry ->
            val manageDrinksViewModel: com.example.cauds.ui.drinklog.ManageDrinksViewModel = viewModel()
            com.example.cauds.ui.drinklog.ManageDrinksScreen(navController, viewModel = manageDrinksViewModel)
        }
        composable(Screen.AddNewDrink.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.ManageDrinks.route)
            }
            val manageDrinksViewModel: com.example.cauds.ui.drinklog.ManageDrinksViewModel = viewModel(parentEntry)
            com.example.cauds.ui.drinklog.AddNewDrinkScreen(navController, viewModel = manageDrinksViewModel)
        }
        composable(Screen.Journal.route) { Text("Journal Screen") }
        composable(Screen.Calendar.route) { CalendarScreen(
            onDayClick = { date -> navController.navigate("day_summary/${date}") }
        ) }
        composable(Screen.Support.route) { Text("Support Screen") }
        composable(Screen.AccountTest.route) { AccountTestScreen(navController) }

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