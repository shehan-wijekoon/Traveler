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
    val username: String = "",
    val description: String = "",
    val title: String = "",
    val profilePictureUrl: String = ""
)

// ⭐ REQUIRED CHANGE: Post model updated to use imageUrls: List<String>
data class Post(
    val id: String = "",
    val authorId: String = "",
    val imageUrls: List<String> = emptyList(), // CHANGED from imageUrl: String
    val rating: Double = 0.0,
    val timestamp: Long = 0L,
    val title: String = ""
)

class UserProfileViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _profileUiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val profileUiState: StateFlow<ProfileUiState> = _profileUiState.asStateFlow()

    private val _isSaveCompleted = MutableStateFlow(false)
    val isSaveCompleted: StateFlow<Boolean> = _isSaveCompleted.asStateFlow()

    private val _userPosts = MutableStateFlow<List<Post>>(emptyList())
    val userPosts: StateFlow<List<Post>> = _userPosts.asStateFlow()


    init {
        fetchUserProfile()
    }

    // ⭐ NO CHANGE NEEDED HERE - Logic remains the same, but now uses the updated Post model
    fun fetchUserPosts(userId: String) {
        if (userId.isBlank()) return
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("posts")
                    .whereEqualTo("authorId", userId)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()

                // This line correctly deserializes the Firestore documents using the new Post class definition.
                val posts = snapshot.documents.mapNotNull { doc ->
                    val postData = doc.toObject(Post::class.java)
                    postData?.copy(id = doc.id)
                }

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

    fun saveUserProfile(
        name: String,
        username: String,
        description: String,
        profilePictureUrl: String
    ) {
        _profileUiState.value = ProfileUiState.Loading
        _isSaveCompleted.value = false

        viewModelScope.launch {
            val user = auth.currentUser
            if (user == null) {
                _profileUiState.value = ProfileUiState.Error("User not authenticated.")
                return@launch
            }

            try {
                val promotionData = hashMapOf(
                    "title" to name,
                    "username" to username,
                    "description" to description,
                    "profilePictureUrl" to profilePictureUrl
                )

                firestore.collection("users").document(user.uid)
                    .set(promotionData)
                    .await()

                val savedProfile = UserProfile(username, description, name, profilePictureUrl)

                _profileUiState.value = ProfileUiState.Success(savedProfile)
                _isSaveCompleted.value = true

            } catch (e: Exception) {
                _profileUiState.value = ProfileUiState.Error(e.message ?: "Failed to save promotion data.")
            }
        }
    }

    fun resetUiState() {
        _profileUiState.value = ProfileUiState.Idle
    }

    fun resetSaveCompleted() {
        _isSaveCompleted.value = false
    }
}