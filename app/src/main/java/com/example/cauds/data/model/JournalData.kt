package com.example.cauds.data.model

import com.google.firebase.Timestamp

data class JournalData(
    val userId: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null, // NULL ON SAVE!
    val entry: String = ""

    )
