package com.example.cauds.ui.navigation

sealed class Screen(val route: String) {
    // Authentication
    object Login : Screen("login")
    object SignUp : Screen("sign_up")
    object ForgotPassword : Screen("forgot_password")

    // Onboarding
    object AudTest : Screen("onboard_aud")
    object OnboardingInfo : Screen("onboard_info")
    object OnboardingPrivacy : Screen("onboard_privacy")
    object AudQuiz : Screen("AudQuiz")

    // Dashboard / Journal
    object Dashboard : Screen("dashboard")
    object Journal : Screen("journal")


    // -----    Tracker     -----
    object Tracking : Screen("tracking")
    object ManageDrinks : Screen("manage_drinks")
    object AddNewDrink : Screen("add_new_drink")

    // -----    Calendar    -----
    object Calendar : Screen("calendar")
    object DaySummary : Screen("day_summary/{date}")

    // -----    Support    -----
    object Support : Screen("support")


    // -----    Account/Setting    -----
    object Account : Screen("account")


}