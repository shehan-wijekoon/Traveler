package com.example.traveler.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
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

    private val auth = FirebaseAuth.getInstance()
    private val _authUiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    fun signUp(email: String, password: String) {
        _authUiState.value = AuthUiState.Loading
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _authUiState.value = AuthUiState.Success
                        } else {
                            _authUiState.value = AuthUiState.Error(
                                task.exception?.message ?: "Sign up failed."
                            )
                        }
                    }
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error(e.message ?: "An unexpected error occurred.")
            }
        }
    }

    fun login(email: String, password: String) {
        _authUiState.value = AuthUiState.Loading
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _authUiState.value = AuthUiState.Success
                        } else {
                            _authUiState.value = AuthUiState.Error(
                                task.exception?.message ?: "Login failed."
                            )
                        }
                    }
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error(e.message ?: "An unexpected error occurred.")
            }
        }
    }

    // Placeholder for Google login
    fun googleLogin() {
        // Implementation for Google Sign-In will go here
    }

    // Placeholder for Facebook login
    fun facebookLogin() {
        // Implementation for Facebook Sign-In will go here
    }

    fun resetAuthUiState() {
        _authUiState.value = AuthUiState.Idle
    }
}