package com.example.android2025.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.cloudinary.Cloudinary
import com.example.android2025.data.local.UserDao
import com.example.android2025.data.local.UserEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepository(private val context: Context, private val userDao: UserDao) {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "dqsufzkgt",
            "api_key" to "847374298814554",
            "api_secret" to "K5iU9t1dT2YxLGkzmQrt3ZilQgw"
        )
    )

    // Sign Up Function
    suspend fun register(
        email: String,
        password: String,
        username: String,
        firstName: String,
        lastName: String,
        imageUri: Uri?
    ): Result<UserEntity> {
        try {
            // Create user in Firebase Authentication
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("Firebase user not found")
            Log.d("AuthRepository", "Firebase user created with UID: ${firebaseUser.uid}")

            var photoUrl: String? = null

            // Upload image to Cloudinary if provided
            imageUri?.let { uri ->
                try {
                    Log.d("CloudinaryUpload", "Uploading image: $uri")
                    val inputStream = context.contentResolver.openInputStream(uri)
                        ?: throw Exception("Input stream is null")
                    inputStream.use {
                        val uploadResult = withContext(Dispatchers.IO) {
                            cloudinary.uploader().upload(it, mapOf("folder" to "profile_photos"))
                        }
                        photoUrl = uploadResult["secure_url"] as? String
                            ?: throw Exception("secure URL not found")
                        Log.d("CloudinaryUpload", "Image uploaded successfully: $photoUrl")
                    }
                } catch (e: Exception) {
                    throw Exception("Image upload error: ${e.message}")
                }
            } ?: Log.w("CloudinaryUpload", "No image URI provided.")

            // Create UserEntity
            val user = UserEntity(
                uid = firebaseUser.uid,
                email = email,
                username = username,
                firstName = firstName,
                lastName = lastName,
                photoUrl = photoUrl
            )

            // Save user to Firestore
            firestore.collection("users")
                .document(firebaseUser.uid)
                .set(user.toMap())
                .await()

            // Save user to Room Database (local)
            userDao.insertUser(user)
            return Result.success(user)
        } catch (e: Exception) {
            // Check for specific registration errors and provide custom messages
            val message = when (e) {
                is FirebaseAuthUserCollisionException -> "This email is already in use. Please use a different email."
                is FirebaseAuthWeakPasswordException -> "Password is too weak. Please choose a stronger password."
                else -> "Registration failed: ${e.message}"
            }
            return Result.failure(Exception(message))
        }
    }

    // Login Function
    suspend fun login(email: String, password: String): Result<UserEntity> {
        try {
            // Sign in with Firebase Authentication
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("Firebase user not found")

            // Retrieve user data from Firestore
            val snapshot = firestore.collection("users")
                .document(firebaseUser.uid)
                .get()
                .await()

            if (!snapshot.exists()) {
                throw Exception("Firestore user  not found")
            }

            val user = UserEntity(
                uid = firebaseUser.uid,
                email = snapshot.getString("email") ?: email,
                username = snapshot.getString("username") ?: "Unknown",
                firstName = snapshot.getString("firstName") ?: "Unknown",
                lastName = snapshot.getString("lastName") ?: "Unknown",
                photoUrl = snapshot.getString("photoUrl")
            )

            // Save user to Room Database (local)
            userDao.insertUser(user)
            return Result.success(user)
        } catch (e: Exception) {
            // Check for specific login errors and provide custom messages
            val message = when (e) {
                is FirebaseAuthInvalidUserException -> "Email or password is incorrect. Please try again."
                is FirebaseAuthInvalidCredentialsException -> "Email or password is incorrect. Please try again."
                else -> "Login failed: ${e.message}"
            }
            return Result.failure(Exception(message))
        }
    }

    // Logout Function
    suspend fun logout() {
        firebaseAuth.signOut()
        userDao.clearUsers()
    }

    // Helper function to convert UserEntity to Map
    private fun UserEntity.toMap(): Map<String, Any?> = mapOf(
        "uid" to uid,
        "email" to email,
        "username" to username,
        "firstName" to firstName,
        "lastName" to lastName,
        "photoUrl" to photoUrl
    )
}
