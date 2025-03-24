package com.example.android2025.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val postId: String,
    val uid: String,
    val username: String,
    val profileImageUrl: String? = null,
    val content: String,
    val photoUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis()

)