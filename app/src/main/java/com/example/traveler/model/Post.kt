package com.example.traveler.model

data class Post(
    val id: String = "",
    val imageUrl: String = "",
    val description: String = "",
    val authorId: String = "",
    val timestamp: Long = 0,
    val title: String = "",
    val category: String = "",
)