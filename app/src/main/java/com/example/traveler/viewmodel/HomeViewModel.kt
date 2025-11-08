package com.example.traveler.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.traveler.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


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

    private val _selectedCategoryFilter = MutableStateFlow<String?>(null)

    init {
        fetchPosts()
    }

    fun selectCategory(category: String?) {
        _selectedCategoryFilter.value = category
        fetchPosts()
    }

    fun fetchPosts() {
        _uiState.value = HomeUiState.Loading
        viewModelScope.launch {
            try {
                val currentFilter = _selectedCategoryFilter.value


                var query: Query = firestore.collection("posts")


                if (currentFilter != null) {
                    query = query.whereEqualTo("category", currentFilter)
                }


                val snapshot = query
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get()
                    .await()


                val posts = snapshot.documents.mapNotNull { doc ->
                    val postData = doc.toObject(Post::class.java)

                    postData?.copy(id = doc.id)
                }

                _uiState.value = HomeUiState.Success(posts)

            } catch (e: Exception) {
                println("Firestore Error: ${e.message}")
                _uiState.value = HomeUiState.Error(e.message ?: "Failed to load posts.")
            }
        }
    }

    fun refreshPosts() {
        fetchPosts()
    }
}