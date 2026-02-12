package com.example.cauds.ui.account

import androidx.lifecycle.ViewModel
import com.example.cauds.data.repository.AuthRepository


class AccountViewModel : ViewModel() {
    private val authRepo = AuthRepository()

    fun performLogout() {
        authRepo.logout()
    }
}