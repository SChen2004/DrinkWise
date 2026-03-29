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
    object QuizIntro : Screen("quiz_intro")
    object AudQuiz : Screen("aud_quiz")
    object QuizLoading : Screen("quiz_loading")
    object QuizResult : Screen("quiz_result")
    object NotificationPreferences : Screen("notification_preferences?fromAccount={fromAccount}") {
        fun createRoute(fromAccount: Boolean = false) = "notification_preferences?fromAccount=$fromAccount"
    }
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
    object DaySummary : Screen("day_summary/{date}") {
        fun createRoute(date: String) = "day_summary/$date"
    }

    // -----    Learning    -----
    object LearningPage : Screen("learning_page")
    object UnderstandingAud : Screen("understanding_aud")
    object SubjectArticles : Screen("subject_articles")
    object ArticlePage : Screen("article_page")


    // -----    Account/Setting    -----
    object AccountTest : Screen("account_test")
    object Account : Screen("account")
    object ChangePassword : Screen("change_password")


}