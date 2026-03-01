package com.example.cauds.data.model

import com.google.firebase.Timestamp

data class User(
    val audScore: AudRisk = AudRisk.DEFAULT_RISK,             // AUD Test result level
    val joinDate: Timestamp = Timestamp.now(),                // joinDate
    val name: String = "",                                    // Username
    val sex: Sex = Sex.DEFAULT_SEX,                           // sex
    val notificationPreferences: NotificationPreferences = NotificationPreferences(),
)