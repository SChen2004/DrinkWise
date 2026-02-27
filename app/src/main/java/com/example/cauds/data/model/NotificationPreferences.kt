package com.example.cauds.data.model

data class NotificationPreferences(
    val dailyCheckin: Boolean = false,
    val dailyEncouragement: Boolean = false,
    val weeklyReflection: Boolean = false,
    val monthlyProgress: Boolean = false
)
