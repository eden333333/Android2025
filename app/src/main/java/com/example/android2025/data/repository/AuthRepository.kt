package com.example.android2025.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android2025.data.local.UserDao
import com.example.android2025.data.local.UserEntity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository(private val userDao: UserDao) {

    private val firebaseAuth = FirebaseAuth.getInstance()

    // Sign Up Function
    suspend fun register(
        email: String,
        password: String,
        username: String,
        firstName: String,
        lastName: String
    )
            : Result<UserEntity> {
        try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("Firebase user is null")
            // Create UserEntity and store in Room Database
            val user = UserEntity(
                uid = firebaseUser.uid,
                email = email,
                username = username,
                firstName = firstName,
                lastName = lastName
            )
            userDao.insertUser(user)
            return Result.success(user)

        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    // Login Function
    suspend fun login(email: String, password: String): Result<UserEntity> {
        try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("Firebase user is null")
            // Check if user exists in Room Database
            val user = userDao.getUserById(firebaseUser.uid)
            if (user != null) {
                return Result.success(user)
            } else {
                // If not available, create a default user record. Adjust as needed.
                val newUser = UserEntity(
                    firebaseUser.uid,
                    firebaseUser.email ?: email,
                    "DefaultUsername",
                    "DefaultFirstName",
                    "DefaultLastName"
                )
                userDao.insertUser(newUser)
                return Result.success(newUser)

            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    // Logout Function
    suspend fun logout() {

        firebaseAuth.signOut()

    }

}
