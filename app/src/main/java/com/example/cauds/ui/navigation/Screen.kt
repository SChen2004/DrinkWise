package com.example.cauds.ui.navigation

sealed class Screen(val route: String) {
    // Authentication
    object Login : Screen("login")
    object SignUp : Screen("sign_up")
    object ForgotPassword : Screen("forgot_password")

    // Onboarding
    object OnboardingName : Screen("onboarding_name")
    object OnboardingSex : Screen("onboarding_sex")
    object OnboardingPurpose : Screen("onboarding_purpose")
    object AudQuiz : Screen("aud_quiz")
    object QuizResult : Screen("quiz_result")
    object NotificationPreferences : Screen("notification_preferences")
    object FavouriteDrinks : Screen("favourite_drinks")

    // Dashboard
    object Dashboard : Screen("dashboard")

    // Journaling
    object Journal : Screen("journal")
    object CreateEntry : Screen("create_entry")


    // -----    Tracker     -----
    object Tracking : Screen("tracking")
    object ManageDrinks : Screen("manage_drinks")
    object AddNewDrink : Screen("add_new_drink")

    // -----    Calendar    -----
    object Calendar : Screen("calendar")
    object DaySummary : Screen("day_summary/{date}")

    // -----    Learning    -----
    object LearningPage : Screen("learning_page")


    // -----    Support    -----
    object Support : Screen("support")


    // -----    Account/Setting    -----
    object AccountTest : Screen("account_test")
    object Account : Screen("account")


}