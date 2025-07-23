package com.example.traveler.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel : ViewModel() {

    private val _authUiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)

    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    fun signUp(email: String, password: String) {
        _authUiState.value = AuthUiState.Loading

        viewModelScope.launch {

            delay(2000)


            if (email.contains("@") && password.length >= 6) {
                _authUiState.value = AuthUiState.Success
            } else {
                _authUiState.value = AuthUiState.Error("Invalid email or password (min 6 chars).")
            }
        }
    }

    // Function to simulate a login process
    fun login(email: String, password: String) {
        _authUiState.value = AuthUiState.Loading

        viewModelScope.launch {
            delay(1500)

            if (email == "test@example.com" && password == "password") {
                _authUiState.value = AuthUiState.Success
            } else {
                _authUiState.value = AuthUiState.Error("Invalid credentials.")
            }
        }
    }

    fun resetAuthUiState() {
        _authUiState.value = AuthUiState.Idle
    }
}