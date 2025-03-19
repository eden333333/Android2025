package com.example.android2025.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.android2025.data.local.AppDatabase
import com.example.android2025.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = AppDatabase.getDatabase(application).userDao()
    private val authRepository = AuthRepository(FirebaseAuth.getInstance(), userDao)

    // Exposing authentication state
    var authState: LiveData<Boolean> = authRepository.getAuthState()

    fun signUp(email: String, password: String, username: String, firstName: String, lastName: String) {
        viewModelScope.launch {
            authRepository.signUp(email, password, username, firstName, lastName)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            authRepository.login(email, password)
        }
    }

    fun logout() {
        authRepository.logout()
    }
}
