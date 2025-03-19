package com.example.android2025.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android2025.data.local.UserDao
import com.example.android2025.data.local.UserEntity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository(private val auth: FirebaseAuth, private val userDao: UserDao) {


    private val authStateLiveData = MutableLiveData<Boolean>()

    init {
        // Check if user is already logged in when the repository is created
        authStateLiveData.value = auth.currentUser != null
    }

    fun getAuthState(): LiveData<Boolean> {
        return authStateLiveData
    }

    // Sign Up Function
    suspend fun signUp(email: String, password: String, username: String, firstName: String, lastName: String) {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                // Create UserEntity and store in Room Database
                val user = UserEntity(
                    uid = firebaseUser.uid,
                    email = email,
                    username = username,
                    firstName = firstName,
                    lastName = lastName
                )
                userDao.insertUser(user)

                // Update LiveData - User is authenticated
                authStateLiveData.postValue(true)
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Sign Up Failed: ${e.message}")
            authStateLiveData.postValue(false)
        }
    }

    // Login Function
    suspend fun login(email: String, password: String) {
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            authStateLiveData.postValue(true) // User is authenticated
        } catch (e: Exception) {
            Log.e("AuthRepository", "Login Failed: ${e.message}")
            authStateLiveData.postValue(false)
        }
    }

    // Logout Function
    fun logout() {
        auth.signOut()
        authStateLiveData.postValue(false) // User is logged out
    }
}
