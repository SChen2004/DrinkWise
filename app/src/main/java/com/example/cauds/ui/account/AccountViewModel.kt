package com.example.cauds.ui.account

import androidx.lifecycle.ViewModel
import com.example.cauds.data.model.AudRisk
import com.example.cauds.data.repository.AuthRepository
import com.example.cauds.data.repository.UserRepository
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue


class AccountViewModel : ViewModel() {
    private val authRepo = AuthRepository()
    private val userRepository = UserRepository()

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var audRisk by mutableStateOf(AudRisk.DEFAULT_RISK)
        private set

    fun performLogout() {
        authRepo.logout()
    }

    fun getAudRiskLevel() {
        val userId = authRepo.getUserId()!!

        userRepository.getAudRiskLevel(userId) { success, result, error ->
            if (success) {
                audRisk = result
            } else {
                errorMessage = error ?: "Unknown error"
            }
        }
    }

}