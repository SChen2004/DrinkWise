package com.example.cauds.ui.account

import androidx.lifecycle.ViewModel
import com.example.cauds.data.model.AudRisk
import com.example.cauds.data.model.User
import com.example.cauds.data.repository.AuthRepository
import com.example.cauds.data.repository.UserRepository
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue

enum class AudTestState {
    UNTAKEN, IN_PROGRESS, COMPLETED
}

class AccountViewModel : ViewModel() {
    private val authRepo = AuthRepository()
    private val userRepo = UserRepository()

    var user by mutableStateOf(User())
        private set
        
    var userEmail by mutableStateOf("")
        private set

    var audTestState by mutableStateOf(AudTestState.UNTAKEN)
        private set

    fun loadUserData() {
        val userId = authRepo.getUserId()
        if (userId != null) {
            userEmail = authRepo.getUserEmail() ?: ""
            userRepo.getUser(userId) { fetchedUser ->
                user = fetchedUser
                
                audTestState = when {
                    fetchedUser.audTestInProgress -> AudTestState.IN_PROGRESS
                    fetchedUser.audScore != AudRisk.DEFAULT_RISK -> AudTestState.COMPLETED
                    else -> AudTestState.UNTAKEN
                }
            }
        }
    }

    fun performLogout() {
        authRepo.logout()
    }
}
