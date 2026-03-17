package com.example.cauds.ui.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.cauds.data.model.User
import com.example.cauds.data.repository.AuthRepository
import com.example.cauds.data.repository.UserRepository
import com.example.cauds.data.model.AudRisk
import com.example.cauds.data.model.NotificationPreferences
import com.example.cauds.data.model.Sex

class OnboardingViewModel : ViewModel() {
    private val userRepo = UserRepository()
    private val authRepo = AuthRepository()

    var name by mutableStateOf("")
        private set

    var biologicalSex by mutableStateOf(Sex.PREFER_NOT_TO_SAY)
        private set

    // Tracks current quiz question
    var currentQuizIndex by mutableStateOf(0)

    // Store user's selected options during the quiz
    var quizAnswers by mutableStateOf<List<Option?>>(emptyList())

    fun resetQuizState() {
        currentQuizIndex = 0
        quizAnswers = emptyList()
    }

    init {
        loadUserState()
    }

    fun loadUserState() {
        val userId = authRepo.getUserId() ?: return
        userRepo.getUser(userId) { user ->
            audRisk = user.audScore
            notificationPreferences = user.notificationPreferences
            if (user.audTestInProgress) {
                // If it was in progress, we don't have stored answers yet without another DB field,
                // but we at least don't reset the risk.
            }
        }
    }

    var supportingFriend by mutableStateOf(false)
        private set

    var notificationPreferences by mutableStateOf(NotificationPreferences())
        private set

    var audRisk by mutableStateOf(AudRisk.DEFAULT_RISK)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun updateName(value: String) { name = value }
    fun updateBiologicalSex(value: Sex) { biologicalSex = value }
    fun updateSupportingFriend(value: Boolean) { supportingFriend = value }
    fun updateAudRisk(value: AudRisk) { audRisk = value }

    fun saveOnboardingData() {
        val userId = authRepo.getUserId()!!

        isLoading = true
        errorMessage = null

        val user = User(
            name = name,
            sex = biologicalSex,
            supportingFriend = supportingFriend
        )

        userRepo.saveOnboarding(userId, user) { success, error ->
            isLoading = false
            if (!success) {
                errorMessage = error ?: "Unknown error"
            }
        }
    }

    fun scoreToAudRisk(score: Int): AudRisk {
        return when {
            score <= 1 -> AudRisk.NO_RISK
            score <= 11 -> AudRisk.LOW_RISK
            score <= 33 -> AudRisk.MODERATE_RISK
            else -> AudRisk.HIGH_RISK
        }
    }

    fun setAudTestInProgress(inProgress: Boolean) {
        val userId = authRepo.getUserId() ?: return
        userRepo.setAudTestInProgress(userId, inProgress) { _, _ -> }
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

    fun saveNotificationPreferences(prefs: NotificationPreferences) {
        val userId = authRepo.getUserId()!!
        isLoading = true
        errorMessage = null

        userRepo.saveNotificationPreferences(userId, prefs) { success, error ->
            isLoading = false
            if (!success) {
                errorMessage = error ?: "Unknown error"
            }
        }
    }

    fun saveFavouriteDrinks(drinks: List<String>) {
        val userId = authRepo.getUserId()!!
        userRepo.saveFavouriteDrinks(userId, drinks) { success, error ->
            if (!success) {
                errorMessage = error ?: "Unknown error"
            }
        }
    }

    fun completeOnboarding() {
        val userId = authRepo.getUserId()!!
        userRepo.completeOnboarding(userId) { success, error ->
            if (!success) {
                errorMessage = error ?: "Unknown error"
            }
        }
    }

    fun getCompletedOnboarding(onResult: (Boolean) -> Unit) {
        val userId = authRepo.getUserId()!!
        userRepo.getCompletedOnboarding(userId) { completed ->
            onResult(completed)
        }
    }
}
