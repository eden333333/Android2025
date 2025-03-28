package com.example.android2025.data.model

import java.io.Serializable

data class Post(
    val postId: String = "",
    val uid: String = "",
    val username: String = "",
    val profileImageUrl: String? = null,
    val content: String = "",
    val photoUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "postId" to postId,
            "uid" to uid,
            "content" to content,
            "photoUrl" to photoUrl,
            "username" to username,
            "profileImageUrl" to profileImageUrl,
            "timestamp" to timestamp
        )
    }
}