package com.example.cauds.ui.account

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.cauds.data.repository.AuthRepository

class ChangePasswordViewModel : ViewModel() {
    private val authRepo = AuthRepository()

    // State variables for the input fields
    var currentPassword by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    
    // UI State variables
    var isLoading by mutableStateOf(false)
    var statusMessage by mutableStateOf<String?>(null)
    var isSuccess by mutableStateOf(false)

    // Visibility states
    var isCurrentPasswordVisible by mutableStateOf(false)
    var isNewPasswordVisible by mutableStateOf(false)
    var isConfirmPasswordVisible by mutableStateOf(false)

    // Validation Logic
    private fun isPasswordComplex(password: String): Boolean {
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecial = password.any { !it.isLetterOrDigit() }
        return password.length >= 8 && hasUppercase && hasLowercase && hasDigit && hasSpecial
    }

    fun clearMessage() {
        statusMessage = null
    }

    /**
     * updatePassword - Identifies validation errors or performs the update.
     */
    fun updatePassword(onSuccess: () -> Unit) {
        // Validation: Fields cannot be empty
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            statusMessage = "All fields are required"
            return
        }
        // Validation: Complexity requirement
        if (!isPasswordComplex(newPassword)) {
            statusMessage = "Must be at least 8 characters and contain 1 number, 1 uppercase,1 lowercase, 1 special character"
            return
        }
        // Validation: Passwords must match
        if (newPassword != confirmPassword) {
            statusMessage = "Passwords do not match"
            return
        }
        // Validation: Old password check (if we had the field)
        if (currentPassword.isEmpty()) {
            // Mapping current to new for simplicity if design lacks old password field
            // But we'll use re-auth if provided.
        }

        isLoading = true
        statusMessage = null
        
        // Use user's current password if they have it, otherwise use new for a "quick refresh" 
        // (Note: updatePassword alone might fail if login isn't recent).
        val authPass = if (currentPassword.isNotEmpty()) currentPassword else newPassword

        authRepo.reauthenticate(authPass) { reauthSuccess, reauthError ->
            if (reauthSuccess) {
                authRepo.updatePassword(newPassword) { updateSuccess, updateError ->
                    isLoading = false
                    if (updateSuccess) {
                        statusMessage = null
                        isSuccess = true
                        onSuccess()
                    } else {
                        statusMessage = updateError ?: "Failed to update password"
                    }
                }
            } else {
                isLoading = false
                statusMessage = reauthError ?: "Incorrect password"
            }
        }
    }
}
