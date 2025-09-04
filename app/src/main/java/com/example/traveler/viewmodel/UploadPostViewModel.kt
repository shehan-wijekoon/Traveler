package com.example.traveler.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveler.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*

sealed class UploadPostUiState {
    object Idle : UploadPostUiState()
    object Loading : UploadPostUiState()
    object Success : UploadPostUiState()
    data class Error(val message: String) : UploadPostUiState()
}

class UploadPostViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _uiState = MutableStateFlow<UploadPostUiState>(UploadPostUiState.Idle)
    val uiState: StateFlow<UploadPostUiState> = _uiState.asStateFlow()

    fun uploadPost(imageUri: Uri, description: String) {
        val user = auth.currentUser
        if (user == null) {
            _uiState.value = UploadPostUiState.Error("User not authenticated.")
            return
        }

        _uiState.value = UploadPostUiState.Loading

        viewModelScope.launch {
            try {
                // Upload image to Firebase Storage
                val imageFileName = UUID.randomUUID().toString() + ".jpg"
                val imageRef = storage.reference.child("posts/${user.uid}/$imageFileName")
                imageRef.putFile(imageUri).await()
                val imageUrl = imageRef.downloadUrl.await().toString()

                // Create a new Post object
                val newPost = Post(
                    imageUrl = imageUrl,
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