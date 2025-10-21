package com.example.traveler.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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

// 🎯 ADDED: Data model for a user's post (ensure this matches your Firestore 'posts' collection fields)
data class Post(
    // Firestore Document ID is not stored here, but should be handled in the calling function
    val authorId: String = "",
    val imageUrl: String = "",
    val rating: Double = 0.0, // Assuming rating is a Double
    val timestamp: Long = 0L // Used for ordering
    // Add other fields you need like title, description, etc.
)

class UserProfileViewModel : ViewModel() {

    // These fields are already defined, keeping them as they are
    val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _profileUiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val profileUiState: StateFlow<ProfileUiState> = _profileUiState.asStateFlow()

    // 🎯 NEW: State to hold the list of posts for the current user
    private val _userPosts = MutableStateFlow<List<Post>>(emptyList())
    val userPosts: StateFlow<List<Post>> = _userPosts.asStateFlow()


    init {
        fetchUserProfile()
    }

    // 🎯 NEW: Function to fetch posts by the current user
    fun fetchUserPosts(userId: String) {
        if (userId.isBlank()) return // Safety check
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("posts")
                    .whereEqualTo("authorId", userId)
                    .orderBy("timestamp", Query.Direction.DESCENDING) // Order by newest first
                    .get()
                    .await()

                val posts = snapshot.toObjects(Post::class.java)
                _userPosts.value = posts

            } catch (e: Exception) {
                Log.e("UserProfileViewModel", "Error fetching user posts: ${e.message}", e)
                _userPosts.value = emptyList()
            }
        }
    }


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

                        // 🎯 CRITICAL: Fetch posts immediately after profile loads successfully
                        fetchUserPosts(user.uid)

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

    // ... (saveUserProfile and resetUiState remain the same) ...

    fun saveUserProfile(
        name: String,
        username: String,
        description: String,
        imageUrl: String?
    ) {
        _profileUiState.value = ProfileUiState.Loading
        viewModelScope.launch {
            val user = auth.currentUser
            if (user == null) {
                _profileUiState.value = ProfileUiState.Error("User not authenticated.")
                return@launch
            }

            try {
                val finalImageUrl = if (imageUrl.isNullOrBlank()) null else imageUrl

                val userProfile = hashMapOf(
                    "name" to name,
                    "username" to username,
                    "description" to description,
                    "profilePictureUrl" to finalImageUrl
                )

                firestore.collection("users").document(user.uid)
                    .set(userProfile)
                    .await()

                fetchUserProfile()

            } catch (e: Exception) {
                _profileUiState.value = ProfileUiState.Error(e.message ?: "Failed to save profile.")
            }
        }
    }


    fun resetUiState() {
        _profileUiState.value = ProfileUiState.Idle
    }
}