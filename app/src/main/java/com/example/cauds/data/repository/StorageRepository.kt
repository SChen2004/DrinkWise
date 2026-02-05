package com.example.cauds.data.repository

import com.example.cauds.data.model.OnboardingData
import com.google.firebase.firestore.FirebaseFirestore

class StorageRepository {
    private val db = FirebaseFirestore.getInstance()

    // Save onboarding data
    fun saveOnboarding(userId: String, data: OnboardingData, onResult: (Boolean, String?) -> Unit) {
        db.collection("users").document(userId).set(data)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }
}