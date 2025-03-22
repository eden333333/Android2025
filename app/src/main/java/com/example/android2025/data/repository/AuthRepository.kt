package com.example.android2025.data.repository
import android.util.Log
import com.example.android2025.data.local.UserDao
import com.example.android2025.data.local.UserEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(private val userDao: UserDao) {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

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
            // create user in Firebase Authentication and get the user
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("Firebase user is null")

            // create UserEntity
            val user = UserEntity(
                uid = firebaseUser.uid,
                email = email,
                username = username,
                firstName = firstName,
                lastName = lastName
            )

            // save user to Firestore
            firestore.collection("users")
                .document(firebaseUser.uid)
                .set(user.toMap())
                .await()

            // save user to Room Database (local)
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

            // retrieve user data from Firestore
            val snapshot = firestore.collection("users")
                .document(firebaseUser.uid)
                .get()
                .await()

            if (!snapshot.exists()) {
                throw Exception("User not found")
            }

            val user = UserEntity(
                uid = firebaseUser.uid,
                email = snapshot.getString("email") ?: email,
                username = snapshot.getString("username") ?: "Unknown",
                firstName = snapshot.getString("firstName") ?: "Unknown",
                lastName = snapshot.getString("lastName") ?: "Unknown"
            )

            // Save user to Room Database (local)
            userDao.insertUser(user)

            return Result.success(user)



        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    // Logout Function
    suspend fun logout() {
        firebaseAuth.signOut()
        userDao.clearUsers()
    }

    // helper function to convert UserEntity to Map
    private fun UserEntity.toMap(): Map<String, Any> = mapOf(
        "uid" to uid,
        "email" to email,
        "username" to username,
        "firstName" to firstName,
        "lastName" to lastName
    )
}


