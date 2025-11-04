package com.example.traveler.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = false)
    val userId: String, // Firebase UID or local unique ID
    val name: String? = null,
    val email: String? = null,
    val profileImage: String? = null, // URI or Cloud URL
    val bio: String? = null,
    val country: String? = null,
    val phone: String? = null,
    val themeMode: String? = "light",   // light / dark / system
    val language: String? = "en",       // default English
    val lastLoginDate: Long? = null,    // timestamp
    val syncedStatus: Boolean = false   // false = needs sync
)
