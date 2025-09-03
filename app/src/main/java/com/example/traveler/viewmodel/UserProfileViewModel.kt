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

// ⚠️ Corrected sealed class to hold the profile data
sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val userProfile: UserProfile) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
    object Idle : ProfileUiState()
}

data class UserProfile(
    val name: String = "",
    val username: String = "",
    val description: String = "",
    val profilePictureUrl: String? = null
)

class UserProfileViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // ⚠️ Unified state flow
    private val _profileUiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val profileUiState: StateFlow<ProfileUiState> = _profileUiState.asStateFlow()

    init {
        fetchUserProfile()
    }

    // ⚠️ Updated fetch function to use the new sealed class
    fun fetchUserProfile() {
        val user = auth.currentUser ?: return
        _profileUiState.value = ProfileUiState.Loading
        viewModelScope.launch {
            try {
                val documentSnapshot = firestore.collection("users").document(user.uid).get().await()
                if (documentSnapshot.exists()) {
                    val profileData = documentSnapshot.toObject(UserProfile::class.java)
                    if (profileData != null) {
                        _profileUiState.value = ProfileUiState.Success(profileData)
                    } else {
                        _profileUiState.value = ProfileUiState.Error("Profile data not found.")
                    }
                } else {
                    _profileUiState.value = ProfileUiState.Error("User profile does not exist.")
                }
            } catch (e: Exception) {
                _profileUiState.value = ProfileUiState.Error(e.message ?: "An unexpected error occurred.")
            }
        }
    }

    fun saveUserProfile(
        name: String,
        username: String,
        description: String,
        imageUri: Uri?
    ) {
        // This function's logic remains the same, but it should be noted
        // that it should also eventually call fetchUserProfile() to update the state
        // after a successful save.
        _profileUiState.value = ProfileUiState.Loading // Changed to use the new state
        viewModelScope.launch {
            val user = auth.currentUser
            if (user == null) {
                _profileUiState.value = ProfileUiState.Error("User not authenticated.")
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
                            // After saving, reload the profile to get the new data
                            fetchUserProfile()
                        } else {
                            _profileUiState.value = ProfileUiState.Error(
                                task.exception?.message ?: "Failed to save profile."
                            )
                        }
                    }
            } catch (e: Exception) {
                _profileUiState.value = ProfileUiState.Error(e.message ?: "An unexpected error occurred.")
            }
        }
    }

    fun resetUiState() {
        _profileUiState.value = ProfileUiState.Idle
    }
}