package com.example.cauds.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.cauds.data.repository.AuthRepository

// Functions for UI to call on when authenticating
class AuthViewModel : ViewModel() {
    private val repo = AuthRepository()

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
}