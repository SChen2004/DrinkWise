package com.example.cauds.ui.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.cauds.data.model.User
import com.example.cauds.data.repository.AuthRepository
import com.example.cauds.data.repository.UserRepository
import com.example.cauds.data.model.AudRisk

class OnboardingViewModel : ViewModel() {
    private val userRepo = UserRepository()
    private val authRepo = AuthRepository()

    // Temp store data
    var audScore = AudRisk.DEFAULT_RISK
    var name = ""

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set


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

    fun scoreToAudRisk(score: Int): AudRisk {
        return when {
            score <= 1 -> AudRisk.NO_RISK
            score <= 4 -> AudRisk.LOW_RISK
            score <= 8 -> AudRisk.MODERATE_RISK
            else -> AudRisk.HIGH_RISK
        }
    }

    fun saveQuizResult(audRisk: AudRisk) {
        val userId = authRepo.getUserId()!!

        isLoading = true
        errorMessage = null

        userRepo.saveAudRiskLevel(userId, audRisk) { success, error ->
            isLoading = false
            if (!success) {
                errorMessage = error ?: "Unknown error"
            }
        }
    }
}