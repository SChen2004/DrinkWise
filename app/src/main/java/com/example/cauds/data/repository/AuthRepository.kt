package com.example.cauds.data.repository

import com.google.firebase.auth.FirebaseAuth

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()

    // Check if user login yet
    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    // Get user ID
    fun getUserId(): String? = auth.currentUser?.uid

    // Get user email
    fun getUserEmail(): String? = auth.currentUser?.email

    /**
     * Login Function
     * Attempts to authenticate a user using Firebase's email/password provider.
     * This function also catches Firebase exception types (like InvalidUser or InvalidCredentials)
     */
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

    /**
     * Sign Up Function
     * Registers a new user with Firebase using an email and password.
     * Crucially, it catches FirebaseAuthUserCollisionException to explicitly notify the user
     * if the email they are trying to register is already in the database ("Email already exists").
     */
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

    /**
     * Forgot Password Function
     * Triggers the native Firebase password reset flow. Firebase will automatically send
     * a secure, limited-time link to the provided email address for the user to reset their password
     * on a secure web page.
     */
    fun sendPasswordResetEmail(email: String, onResult: (Boolean, String?) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e ->
                val errorMessage = when (e) {
                    is com.google.firebase.auth.FirebaseAuthInvalidUserException -> "Email does not exist"
                    else -> e.message ?: "Unknown error"
                }
                onResult(false, errorMessage)
            }
    }

    /**
     * updatePassword - Updates the authenticated user's password to a new value.
     * Throws 'RecentLoginRequiredException' if the user hasn't authenticated recently.
     */
    fun updatePassword(newPass: String, onResult: (Boolean, String?) -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            user.updatePassword(newPass)
                .addOnSuccessListener { onResult(true, null) }
                .addOnFailureListener { e ->
                    val errorMessage = when (e) {
                         is com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException -> "Please log in again to change password"
                         else -> e.message ?: "Unknown error"
                    }
                    onResult(false, errorMessage)
                }
        } else {
            onResult(false, "User not logged in")
        }
    }

    /**
     * reauthenticate - Confirms the user's identity before performing account changes.
     * Uses the current email and provided password to refresh the authentication state.
     */
    fun reauthenticate(currentPass: String, onResult: (Boolean, String?) -> Unit) {
        val user = auth.currentUser
        val email = user?.email
        if (user != null && email != null) {
            val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(email, currentPass)
            user.reauthenticate(credential)
                .addOnSuccessListener { onResult(true, null) }
                .addOnFailureListener { onResult(false, it.message) }
        } else {
            onResult(false, "User not found")
        }
    }

    // Log out
    fun logout() = auth.signOut()
}