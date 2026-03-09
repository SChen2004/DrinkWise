package com.example.cauds.ui.account

import androidx.lifecycle.ViewModel
import com.example.cauds.data.model.AudRisk
import com.example.cauds.data.repository.AuthRepository
import com.example.cauds.data.repository.UserRepository
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue


class AccountTestViewModel : ViewModel() {
    private val authRepo = AuthRepository()
    private val userRepo = UserRepository()

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var audRisk by mutableStateOf(AudRisk.DEFAULT_RISK)
        private set

    fun performLogout() {
        authRepo.logout()
    }

    fun getAudRiskLevel() {
        val userId = authRepo.getUserId()!!

        userRepo.getAudRiskLevel(userId) { success, result, error ->
            if (success) {
                audRisk = result
            } else {
                errorMessage = error ?: "Unknown error"
            }
        }
    }

    /// DEBUG

    var userDebugInfo by mutableStateOf("")
        private set

    fun loadUserDebug() {
        val userId = authRepo.getUserId()!!
        userRepo.getUser(userId) { user ->
            userDebugInfo = """
            name: ${user.name}
            sex: ${user.sex.name}
            audScore: ${user.audScore.name}
            favouriteDrinks: ${user.favouriteDrinks}
            joinDate: ${user.joinDate.toDate()}
            onboardingCompleted: ${user.onboardingCompleted}
            supportingFriend: ${user.supportingFriend}
            notificationPreferences:
              dailyCheckin: ${user.notificationPreferences.dailyCheckin}
              dailyEncouragement: ${user.notificationPreferences.dailyEncouragement}
              weeklyReflection: ${user.notificationPreferences.weeklyReflection}
              monthlyProgress: ${user.notificationPreferences.monthlyProgress}
        """.trimIndent()
        }
    }

}