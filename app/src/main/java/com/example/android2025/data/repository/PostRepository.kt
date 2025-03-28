package com.example.android2025.data.repository

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.cloudinary.Cloudinary
import com.example.android2025.data.local.PostDao
import com.example.android2025.data.local.PostEntity
import com.example.android2025.data.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PostRepository(private val context: Context, private val postDao: PostDao) {

    private val firestore = FirebaseFirestore.getInstance()
    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "dqsufzkgt",
            "api_key" to "847374298814554",
            "api_secret" to "K5iU9t1dT2YxLGkzmQrt3ZilQgw"
        )
    )
    // returns posts from the local Room database
     fun getPosts(): LiveData<List<PostEntity>> = postDao.getPosts()

    // loads posts from Firestore and update Room
    suspend fun loadPosts(): Result<List<PostEntity>> {
        try {
            val snapshot = firestore.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val posts = snapshot.documents.mapNotNull { doc ->
                // convert Firestore document to Post model and then to PostEntity
                doc.toObject(Post::class.java)?.let { post ->
                    PostEntity(
                        postId = post.postId,
                        uid = post.uid,
                        username = post.username,
                        profileImageUrl = post.profileImageUrl,
                        content = post.content,
                        photoUrl = post.photoUrl,
                        timestamp = post.timestamp
                    )
                }
            }
            // update local cache (room)
            withContext(Dispatchers.IO) {
                postDao.insertPosts(posts)
            }
            return Result.success(posts)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    suspend fun getPostById(postId: String): PostEntity?{
        return postDao.getPostById(postId)


    }
    fun postListener(postId: String): LiveData<PostEntity>?{
        return postDao.getPostByIdListener(postId)?.asLiveData()

    }

    suspend fun deletePostById(postId: String):Result<Unit>{
        try{
            firestore.collection("posts").document(postId).delete()
            withContext(Dispatchers.IO){
                postDao.deletePostById(postId)
            }
            return Result.success(Unit)
        }catch (e: Exception) {
            return Result.failure(e)
        }


    }

    // Upload a new post to Firestore and update Room locally.
    suspend fun uploadPost(post: Post): Result<PostEntity> {
         try {
            firestore.collection("posts").document(post.postId).set(post.toMap()).await()
            //  update local cache
            val postEntity = PostEntity(
                postId = post.postId,
                uid = post.uid,
                username = post.username,
                profileImageUrl = post.profileImageUrl,
                content = post.content,
                photoUrl = post.photoUrl,
                timestamp = post.timestamp
            )
            withContext(Dispatchers.IO) {
                postDao.insertPosts(listOf(postEntity))
            }
            return Result.success(postEntity)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    suspend fun updatePost(post: PostEntity): Result<Unit>{
        try{
            val updatedPost =  Post(
                postId = post.postId,
                uid = post.uid,
                content = post.content,
                photoUrl = post.photoUrl,
                username = post.username,
                profileImageUrl = post.profileImageUrl,
                timestamp = post.timestamp
            )
            withContext(Dispatchers.IO) {
                firestore.collection("posts").document(updatedPost.postId).update(updatedPost.toMap()).await()
                postDao.updatePost(post.content, post.photoUrl, post.postId);
            }
            return Result.success(Unit)
        }catch(e: Exception){
            return Result.failure(e)
        }
    }

    // Update the username and profile image of all posts in Firestore and Room
    suspend fun updatePostUsernameAndProfile(username: String, oldUsername: String, profileImageUrl: String?) {
        // update Firestore
        try {
        firestore.collection("posts")
            .whereEqualTo("username", oldUsername)
            .get()
            .await()
            .documents
            .forEach { doc ->
                doc.reference.update(
                    mapOf(
                        "username" to username,
                        "profileImageUrl" to profileImageUrl
                    )
                )
            }
        // update Room
        withContext(Dispatchers.IO) {
            postDao.updatePostUsernameAndProfile(username, oldUsername, profileImageUrl )
        }
        } catch (e: Exception) {
            throw e
        }
    }
    // Upload an image to Cloudinary and return the URL
    suspend fun uploadImageToCloudinary(imageUri: Uri): Result<String?> {
        try {
            var url: String? = null
            context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                val uploadResult = withContext(Dispatchers.IO) {
                    cloudinary.uploader().upload(inputStream, mapOf("folder" to "post_images"))
                }
                url = uploadResult["secure_url"] as String
            }
            return Result.success(url)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}
