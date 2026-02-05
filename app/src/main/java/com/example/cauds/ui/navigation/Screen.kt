package com.example.cauds.ui.navigation

sealed class Screen(val route: String) {
    // Authentication
    object Login : Screen("login")
    object SignUp : Screen("sign_up")

    // Onboarding
    object OnboardingAud : Screen("onboard_aud")
    object OnboardingInfo : Screen("onboard_info")
    object OnboardingGoals : Screen("onboard_goals")
    object OnboardingPrivacy : Screen("onboard_privacy")

    // Dashboard
    object Dashboard : Screen("dashboard")


    // -----    Tracker     -----
    object Tracking : Screen("tracking")
    // Daily Activities
    object DailyActivities : Screen("daily_activities")
    // Logging / Tracking
    object LogSelection : Screen("log_selection")
    object LogDrink : Screen("log_drink")
    object LogUrge : Screen("log_urge")
    object Calendar : Screen("calendar")

    // -----    Insight    -----
    object Insight : Screen("insight")

    // -----    Support    -----
    object Support : Screen("support")
    object AudTest : Screen("aud_test")

    // -----    Account/Setting    -----
    object Account : Screen("account")


    object Quote : Screen("quote")
    object MicroInsights : Screen("micro_insights")
    object MessageInABottle : Screen("message_bottle")
    object DidYouKnow : Screen("did_you_know")
}