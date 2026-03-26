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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cauds.data.repository.ArticleRepository
import com.example.cauds.screens.CreateEntryScreen
import com.example.cauds.screens.JournalScreen
import com.example.cauds.ui.account.AccountTestScreen
import com.example.cauds.ui.account.AccountScreen
import com.example.cauds.ui.calendar.CalendarScreen
import com.example.cauds.ui.calendar.CalendarViewModel
import com.example.cauds.ui.calendar.DaySummaryScreen
import com.example.cauds.ui.dashboard.DashboardViewModel
import com.example.cauds.ui.onboarding.OnboardingViewModel
import com.example.cauds.ui.drinklog.DrinkLogScreen
import com.example.cauds.ui.drinklog.DrinkLogViewModel
import com.example.cauds.ui.learning.ArticleScreen
import com.example.cauds.ui.learning.LearningPageScreen
import com.example.cauds.ui.learning.LearningViewModel
import com.example.cauds.ui.onboarding.AudQuizScreen
import com.example.cauds.ui.onboarding.FavouriteDrinksScreen
import com.example.cauds.ui.onboarding.NotificationPreferencesScreen
import com.example.cauds.ui.onboarding.OnboardingNameScreen
import com.example.cauds.ui.onboarding.OnboardingPurposeScreen
import com.example.cauds.ui.onboarding.OnboardingSexScreen
import com.example.cauds.ui.onboarding.QuizIntroScreen
import com.example.cauds.ui.onboarding.QuizLoadingScreen
import com.example.cauds.ui.onboarding.QuizResultScreen
import com.example.cauds.viewmodel.JournalViewModel

@Composable
fun NavGraph(navController: NavHostController, startDestination: String) {

    // viewmodel init
    val dashboardViewModel: DashboardViewModel = viewModel()
    val onboardingViewModel: OnboardingViewModel = viewModel()
    val journalViewModel: JournalViewModel = viewModel()
    val drinkLogViewModel: DrinkLogViewModel = viewModel()
    val context = LocalContext.current.applicationContext
    val articleRepo = ArticleRepository(context)
    val learningViewModel: LearningViewModel = viewModel(
        factory = viewModelFactory {
            initializer { LearningViewModel(articleRepo) }
        }
    )

    NavHost(navController = navController, startDestination = startDestination) {

        // Auth
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.SignUp.route) { SignUpScreen(navController) }
        composable(Screen.ForgotPassword.route) { com.example.cauds.ui.auth.ForgotPasswordScreen(navController) }

        // Onboarding
        composable(Screen.QuizIntro.route) { QuizIntroScreen(navController) }
        composable(Screen.AudQuiz.route) { AudQuizScreen(navController, onboardingViewModel) }
        composable(Screen.QuizLoading.route) { QuizLoadingScreen(navController) }
        composable(Screen.QuizResult.route) { QuizResultScreen(navController, onboardingViewModel) }
        composable(Screen.OnboardingName.route) { OnboardingNameScreen(navController, onboardingViewModel) }
        composable(Screen.OnboardingSex.route) { OnboardingSexScreen(navController, onboardingViewModel) }
        composable(Screen.OnboardingPurpose.route) { OnboardingPurposeScreen(navController, onboardingViewModel) }
        composable(Screen.NotificationPreferences.route) { NotificationPreferencesScreen(navController, onboardingViewModel)}
        composable(Screen.FavouriteDrinks.route) { FavouriteDrinksScreen(navController, onboardingViewModel) }

        // Dashboard / Home
        composable(Screen.Dashboard.route) { DashboardScreen(navController, dashboardViewModel) }

        // Main Sections (Accessible from Dashboard or Bottom Nav)
        composable(Screen.Tracking.route) { DrinkLogScreen(navController, drinkLogViewModel) }
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

        // Journal
        composable(Screen.Journal.route) { JournalScreen(navController, journalViewModel) }
        composable(Screen.CreateEntry.route) { CreateEntryScreen(navController, journalViewModel) }

        // Learning
        composable(Screen.LearningPage.route) { LearningPageScreen(navController, learningViewModel) }
        composable(Screen.ArticlePage.route) { ArticleScreen(navController, learningViewModel) }

        // Calendar
        composable(Screen.Calendar.route) { CalendarScreen(
            onDayClick = { date -> navController.navigate("day_summary/$date") },
            onBack = { navController.popBackStack() }
        ) }
        composable(Screen.Support.route) { Text("Support Screen") }
        composable(Screen.AccountTest.route) { AccountTestScreen(navController) }
        composable(Screen.Account.route) { AccountScreen(navController) }

        // Secondary Features
        composable(Screen.DaySummary.route) { backStackEntry ->
            val dateStr = backStackEntry.arguments?.getString("date") ?: return@composable

            // Own ViewModel instance — works whether we came from CalendarScreen or Dashboard.
            // If we came from CalendarScreen, the data might already be cached by Firestore
            // so the fetch is fast. If from Dashboard, it loads fresh.
            val calendarViewModel: CalendarViewModel = viewModel()

            DaySummaryScreen(
                date = dateStr,
                navController= navController,
                journalViewModel = journalViewModel,
                calendarViewModel = calendarViewModel,
                drinkLogViewModel = drinkLogViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}