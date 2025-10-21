package com.example.traveler.viewmodel

// import android.net.Uri // ⚠️ COMMENTED: Not needed for URL string
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveler.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
// import com.google.firebase.storage.FirebaseStorage // ⚠️ COMMENTED: Not needed
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
// import java.util.* // ⚠️ COMMENTED: Not needed for UUID

sealed class UploadPostUiState {
    object Idle : UploadPostUiState()
    object Loading : UploadPostUiState()
    object Success : UploadPostUiState()
    data class Error(val message: String) : UploadPostUiState()
}

class UploadPostViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    // private val storage = FirebaseStorage.getInstance() // ⚠️ COMMENTED: Not needed

    private val _uiState = MutableStateFlow<UploadPostUiState>(UploadPostUiState.Idle)
    val uiState: StateFlow<UploadPostUiState> = _uiState.asStateFlow()

    // 🎯 UPDATED: Function now accepts String imageUrl instead of Uri
    fun uploadPost(imageUrl: String, description: String) {
        val user = auth.currentUser
        if (user == null) {
            _uiState.value = UploadPostUiState.Error("User not authenticated.")
            return
        }

        _uiState.value = UploadPostUiState.Loading

        viewModelScope.launch {
            try {

                // ⚠️ REMOVED: All Firebase Storage file upload logic:
                /*
                val imageFileName = UUID.randomUUID().toString() + ".jpg"
                val imageRef = storage.reference.child("posts/${user.uid}/$imageFileName")
                imageRef.putFile(imageUri).await()
                val imageUrl = imageRef.downloadUrl.await().toString()
                */

                // Basic validation for the URL
                if (imageUrl.isBlank()) {
                    throw IllegalArgumentException("Image URL cannot be empty.")
                }

                // Create a new Post object, using the passed imageUrl
                val newPost = Post(
                    imageUrl = imageUrl, // 🎯 USING PASSED URL
                    description = description,
                    authorId = user.uid,
                    timestamp = System.currentTimeMillis()
                )

                // Save post to Firestore
                firestore.collection("posts").add(newPost).await()
                _uiState.value = UploadPostUiState.Success
            } catch (e: Exception) {
                _uiState.value = UploadPostUiState.Error(e.message ?: "An unexpected error occurred.")
            }
        }
    }

    fun resetUiState() {
        _uiState.value = UploadPostUiState.Idle
    }
}