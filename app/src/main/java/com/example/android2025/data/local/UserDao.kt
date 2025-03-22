package com.example.android2025.data.local
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.google.firebase.firestore.auth.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE uid = :uid LIMIT 1")
    suspend fun getUserById(uid: String): UserEntity?

    @Query("DELETE FROM users")
    suspend fun clearUsers()
}