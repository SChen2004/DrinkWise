package com.example.cauds.ui.onboarding

import androidx.lifecycle.ViewModel
import com.example.cauds.data.model.User
import com.example.cauds.data.repository.AuthRepository
import com.example.cauds.data.repository.UserRepository
import com.example.cauds.data.model.AUDRisk

class OnboardingViewModel : ViewModel() {
    private val userRepo = UserRepository()
    private val authRepo = AuthRepository()

    // Temp store data
    var audScore = AUDRisk.DEFAULT_RISK
    var name = ""


    // Upload the data
    fun submitData(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val userId = authRepo.getUserId() ?: run {
            onError("User not logged in!")
            return
        }
        val data = User(audScore, name=name)

        userRepo.saveOnboarding(userId, data) { success, errorMsg ->
            if (success) onSuccess() else onError(errorMsg ?: "Unknown Firestore error")
        }
    }
}