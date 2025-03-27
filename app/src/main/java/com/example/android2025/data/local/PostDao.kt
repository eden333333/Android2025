package com.example.android2025.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
     fun getPosts(): LiveData<List<PostEntity>>

     @Query("SELECT * FROM posts WHERE postId = :postId")
     suspend fun getPostById(postId: String): PostEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Query("UPDATE posts SET username = :username, profileImageUrl = :profileImageUrl WHERE username = :oldUsername")
    suspend fun updatePostUsernameAndProfile(username: String, oldUsername: String, profileImageUrl: String?)


    @Query("DELETE FROM posts")
    suspend fun clearPosts()

    @Query("DELETE FROM posts where postId = :postId")
    suspend fun deletePostById(postId: String)
}