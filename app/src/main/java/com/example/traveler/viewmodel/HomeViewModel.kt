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

// State to manage the list of posts
sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val posts: List<Post>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
    object Idle : HomeUiState()
}

class HomeViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        // Start fetching posts as soon as the ViewModel is created
        fetchPosts()
    }

    fun fetchPosts() {
        _uiState.value = HomeUiState.Loading
        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("posts")
                    .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING) // Sort by most recent
                    .get()
                    .await()

                // Convert all documents to Post objects
                val posts = snapshot.toObjects(Post::class.java)

                _uiState.value = HomeUiState.Success(posts)

            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Failed to load posts.")
            }
        }
    }

    fun refreshPosts() {
        fetchPosts()
    }
}