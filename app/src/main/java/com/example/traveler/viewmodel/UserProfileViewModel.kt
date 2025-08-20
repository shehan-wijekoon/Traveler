package com.example.traveler.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class UserProfileUiState {
    object Idle : UserProfileUiState()
    object Loading : UserProfileUiState()
    object Success : UserProfileUiState()
    data class Error(val message: String) : UserProfileUiState()
}

class UserProfileViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _uiState = MutableStateFlow<UserProfileUiState>(UserProfileUiState.Idle)
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    fun saveUserProfile(
        name: String,
        username: String,
        description: String,
        imageUri: Uri?
    ) {
        _uiState.value = UserProfileUiState.Loading
        viewModelScope.launch {
            val user = auth.currentUser
            if (user == null) {
                _uiState.value = UserProfileUiState.Error("User not authenticated.")
                return@launch
            }

            try {
                var imageUrl: String? = null
                if (imageUri != null) {
                    val imageRef = storage.reference.child("profile_pictures/${user.uid}")
                    imageRef.putFile(imageUri).await()
                    imageUrl = imageRef.downloadUrl.await().toString()
                }

                val userProfile = hashMapOf(
                    "name" to name,
                    "username" to username,
                    "description" to description,
                    "profilePictureUrl" to imageUrl
                )

                firestore.collection("users").document(user.uid)
                    .set(userProfile)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _uiState.value = UserProfileUiState.Success
                        } else {
                            _uiState.value = UserProfileUiState.Error(
                                task.exception?.message ?: "Failed to save profile."
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.value = UserProfileUiState.Error(e.message ?: "An unexpected error occurred.")
            }
        }
    }

    fun resetUiState() {
        _uiState.value = UserProfileUiState.Idle
    }
}
