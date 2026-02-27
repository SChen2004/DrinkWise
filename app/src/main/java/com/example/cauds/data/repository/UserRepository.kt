package com.example.cauds.data.repository

import com.example.cauds.data.model.User
import com.google.firebase.firestore.FirebaseFirestore

const val USERS = "users"

class UserRepository {
    private val db = FirebaseFirestore.getInstance()

    // Save onboarding data
    fun saveOnboarding(userId: String, data: User, onResult: (Boolean, String?) -> Unit) {
        db.collection(USERS).document(userId).set(data)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }

}