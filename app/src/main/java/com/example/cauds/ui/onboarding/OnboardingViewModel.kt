package com.example.cauds.ui.onboarding

import androidx.lifecycle.ViewModel
import com.example.cauds.data.model.OnboardingData
import com.example.cauds.data.repository.AuthRepository
import com.example.cauds.data.repository.StorageRepository

class OnboardingViewModel : ViewModel() {
    private val storageRepo = StorageRepository()
    private val authRepo = AuthRepository()

    // Temp store data
    var audScore = ""
    var name = ""


    // Upload the data
    fun submitData(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val userId = authRepo.getUserId() ?: run {
            onError("User not logged in!")
            return
        }
        val data = OnboardingData(audScore, name)

        storageRepo.saveOnboarding(userId, data) { success, errorMsg ->
            if (success) onSuccess() else onError(errorMsg ?: "Unknown Firestore error")
        }
    }
}