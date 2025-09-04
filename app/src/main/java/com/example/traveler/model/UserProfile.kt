package com.example.traveler.model

data class UserProfile(
    val name: String = "",
    val username: String = "",
    val description: String = "",
    val profilePictureUrl: String? = null
)