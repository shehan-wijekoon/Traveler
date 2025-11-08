package com.example.traveler.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveler.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


sealed class ContentUiState {
    object Loading : ContentUiState()
    data class Success(val post: Post) : ContentUiState()
    data class Error(val message: String) : ContentUiState()
}

class ContentViewModel(private val postId: String) : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow<ContentUiState>(ContentUiState.Loading)
    val uiState: StateFlow<ContentUiState> = _uiState.asStateFlow()

    init {
        fetchPostDetails()
    }

    private fun fetchPostDetails() {
        viewModelScope.launch {
            try {
                val documentSnapshot = firestore.collection("posts")
                    .document(postId)
                    .get()
                    .await()

                val post = documentSnapshot.toObject(Post::class.java)?.copy(id = documentSnapshot.id)

                if (post != null) {
                    _uiState.value = ContentUiState.Success(post)
                } else {
                    _uiState.value = ContentUiState.Error("Post not found.")
                }
            } catch (e: Exception) {
                _uiState.value = ContentUiState.Error("Error loading post: ${e.message}")
            }
        }
    }
}