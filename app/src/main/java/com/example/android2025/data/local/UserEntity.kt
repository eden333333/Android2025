package com.example.android2025.data.local


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val uid: String,
    val email: String,
    val username: String,
    val firstName: String,
    val lastName: String
)