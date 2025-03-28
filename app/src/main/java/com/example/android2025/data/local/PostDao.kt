package com.example.android2025.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android2025.data.model.Post
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {

    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
     fun getPosts(): LiveData<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Query("DELETE FROM posts where postId = :postId")
    suspend fun deletePostById(postId: String)

    @Query("UPDATE posts SET  photoUrl = :photoUrl, content = :content WHERE postId = :postId")
    suspend fun updatePost(postId: String, photoUrl: String?, content: String)

    @Query("UPDATE posts SET username = :username, profileImageUrl = :profileImageUrl WHERE username = :oldUsername")
    suspend fun updatePostUsernameAndProfile(username: String, oldUsername: String, profileImageUrl: String?)

    @Query("DELETE FROM posts")
    suspend fun clearPosts()
}