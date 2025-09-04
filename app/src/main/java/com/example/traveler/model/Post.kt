package com.example.traveler.model

data class Post(
    val imageUrl: String = "",
    val description: String = "",
    val authorId: String = "",
    val timestamp: Long = 0,
    val likes: Int = 0,
    val comments: Int = 0,
    val rating: Double = 0.0
)