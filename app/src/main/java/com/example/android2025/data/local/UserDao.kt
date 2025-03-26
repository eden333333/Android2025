package com.example.android2025.data.local
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.google.firebase.firestore.auth.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // get user
    @Query("SELECT * FROM users LIMIT 1")
    fun getUser(): LiveData<UserEntity?>


    @Query("DELETE FROM users")
    suspend fun clearUsers()
}