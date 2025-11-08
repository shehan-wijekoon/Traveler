package com.example.traveler.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveler.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class UploadPostUiState {
    object Idle : UploadPostUiState()
    object Loading : UploadPostUiState()
    object Success : UploadPostUiState()
    data class Error(val message: String) : UploadPostUiState()
}

class UploadPostViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow<UploadPostUiState>(UploadPostUiState.Idle)
    val uiState: StateFlow<UploadPostUiState> = _uiState.asStateFlow()


    fun uploadPost(
        imageUrl: String,
        description: String,
        title: String,
        category: String
    ) {
        val user = auth.currentUser
        if (user == null) {
            _uiState.value = UploadPostUiState.Error("User not authenticated.")
            return
        }

        _uiState.value = UploadPostUiState.Loading

        viewModelScope.launch {
            try {

                if (imageUrl.isBlank()) {
                    throw IllegalArgumentException("Image URL cannot be empty.")
                }
                if (title.isBlank() || category.isBlank()) {
                    throw IllegalArgumentException("Title and Category must be provided.")
                }

                val newPost = Post(
                    imageUrl = imageUrl,
                    description = description,
                    title = title,
                    category = category,
                    authorId = user.uid,
                    timestamp = System.currentTimeMillis()
                )


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