package com.example.cauds.data.model

import com.google.firebase.Timestamp

data class User(
    val audScore: AudRisk = AudRisk.DEFAULT_RISK,             // AUD Test result level
    val joinDate: Timestamp = Timestamp.now(),                // joinDate
    val name: String = "",                                    // Username
    val sex: Sex = Sex.PREFER_NOT_TO_SAY,                           // sex
    val notificationPreferences: NotificationPreferences = NotificationPreferences(),
    val supportingFriend: Boolean = false,
    val onboardingCompleted: Boolean = false,
    val audTestInProgress: Boolean = false,
    val favouriteDrinks: List<String> = emptyList()
)