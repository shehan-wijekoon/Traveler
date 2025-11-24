package com.example.traveler.model

data class Post(
    val id: String = "",
    val imageUrls: List<String> = emptyList(),
    val description: String = "",
    val authorId: String = "",
    val timestamp: Long = 0,
    val title: String = "",
    val category: String = "",
    val googleMapLink: String = "",
    val rulesGuidance: String = "",
    val hashtags: List<String> = emptyList()
)