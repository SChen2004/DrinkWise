package com.example.cauds.data.model

data class OnboardingData(
    val audScore: String = "",             // AUD Test result level
    val name: String = "",             // Username
    val goals: String = "",            // Goal
    val privacy: String = "default"    // Privacy
)