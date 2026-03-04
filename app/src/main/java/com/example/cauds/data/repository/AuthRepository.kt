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
            .addOnFailureListener { e ->
                // Email enumeration protection is enabled, disable it to show invalid user error message
                val errorMessage = when (e) {
                    is com.google.firebase.auth.FirebaseAuthInvalidUserException -> "Email does not exist"
                    is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "Invalid password"
                    else -> e.message ?: "Unknown error"
                }
                onResult(false, errorMessage)
            }
    }

    // Sign up
    fun signUp(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e ->
                val errorMessage = when (e) {
                    is com.google.firebase.auth.FirebaseAuthUserCollisionException -> "Email already exists"
                    else -> e.message ?: "Unknown error"
                }
                onResult(false, errorMessage)
            }
    }

    // Google Sign In
    fun signInWithGoogle(idToken: String, onResult: (Boolean, String?) -> Unit) {
        val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }

    // Facebook Sign In
    fun signInWithFacebook(token: com.facebook.AccessToken, onResult: (Boolean, String?) -> Unit) {
        val credential = com.google.firebase.auth.FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }

    // Log out
    fun logout() = auth.signOut()
}