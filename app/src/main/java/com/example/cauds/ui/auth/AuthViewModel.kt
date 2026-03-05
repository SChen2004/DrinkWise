package com.example.cauds.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.cauds.data.repository.AuthRepository
import com.example.cauds.data.repository.UserRepository

// Functions for UI to call on when authenticating
class AuthViewModel : ViewModel() {
    private val repo = AuthRepository()
    private val userRepo = UserRepository()

    /**
     * Validates email format using a standard Regex pattern.
     * This ensures the user input looks like a valid email string (e.g., name@domain.com)
     */
    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]+$".toRegex()
        return emailRegex.matches(email)
    }

    /**
     * Validates password complexity against these rules:
     * 1. At least 8 characters long
     * 2. Contains at least one digit (0-9)
     * 3. Contains at least one lowercase letter (a-z)
     * 4. Contains at least one uppercase letter (A-Z)
     * 5. Contains at least one special character (@#$%!^&+=_*-)
     */
    fun isValidPassword(password: String): Boolean {
        // At least 8 characters, 1 number, 1 uppercase, 1 lowercase, 1 special character
        val passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%!^&+=_*-]).{8,}$".toRegex()
        return passwordRegex.matches(password)
    }

    fun performLogin(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        repo.login(email, pass) { success, errorMsg ->
            if (success) onSuccess() else onError(errorMsg ?: "Unknown error")
        }
    }

    fun performSignUp(email: String, pass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        repo.signUp(email, pass) { success, errorMsg ->
            if (success) onSuccess() else onError(errorMsg ?: "Unknown error")
        }
    }

    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        repo.signInWithGoogle(idToken) { success, message ->
            if (success) onSuccess() else onError(message ?: "Unknown error")
        }
    }

    fun signInWithFacebook(token: com.facebook.AccessToken, onSuccess: () -> Unit, onError: (String) -> Unit) {
        repo.signInWithFacebook(token) { success, message ->
            if (success) onSuccess() else onError(message ?: "Unknown error")
        }
    }

    fun sendPasswordResetEmail(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        repo.sendPasswordResetEmail(email) { success, errorMsg ->
            if (success) onSuccess() else onError(errorMsg ?: "Unknown error")
        }
    }

    fun checkOnboardingStatus(onResult: (Boolean) -> Unit) {
        val userId = repo.getUserId()
        if (userId != null) {
            userRepo.getCompletedOnboarding(userId) { completed ->
                onResult(completed)
            }
        } else {
            onResult(false)
        }
    }

    fun isLoggedIn(): Boolean = repo.isUserLoggedIn()
}