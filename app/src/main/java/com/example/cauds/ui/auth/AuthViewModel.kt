package com.example.cauds.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.cauds.data.repository.AuthRepository

// Functions for UI to call on when authenticating
class AuthViewModel : ViewModel() {
    private val repo = AuthRepository()

    // check for email format
    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]+$".toRegex()
        return emailRegex.matches(email)
    }

    // check for password format
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

    fun isLoggedIn(): Boolean = repo.isUserLoggedIn()
}