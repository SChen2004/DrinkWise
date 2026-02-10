package com.example.cauds.data.repository

import com.example.cauds.data.model.OnboardingData
import com.example.cauds.data.model.LogData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp

class StorageRepository {
    private val db = FirebaseFirestore.getInstance()

    // Save onboarding data
    fun saveOnboarding(userId: String, data: OnboardingData, onResult: (Boolean, String?) -> Unit) {
        db.collection("users").document(userId).set(data)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }

     fun saveLog(userId: String, logData: LogData, onResult: (Boolean, String?) -> Unit) {
        val logWithMetadata = logData.copy(
            userId = userId,
            timestamp = logData.timestamp ?: Timestamp.now()
        )

        db.collection("logs")
            .add(logWithMetadata) // auto-generates document ID
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }

}