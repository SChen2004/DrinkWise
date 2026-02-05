package com.example.cauds.data.repository

import com.google.firebase.auth.FirebaseAuth

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()

    // Check if user login yet
    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    // Get user ID
    fun getUserId(): String? = auth.currentUser?.uid

    // Login
    fun login(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }

    // Sign up
    fun signUp(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }

    // Log out
    fun logout() = auth.signOut()
}