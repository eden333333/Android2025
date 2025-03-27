package com.example.android2025.viewmodel
import android.app.Application
import android.net.Uri
import android.support.v4.os.IResultReceiver._Parcel
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android2025.data.local.AppDatabase
import com.example.android2025.data.local.PostEntity
import com.example.android2025.data.local.UserEntity
import com.example.android2025.data.model.Post
import com.example.android2025.data.repository.PostRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.UUID




class PostViewModel(application: Application) : AndroidViewModel(application) {


    private val postDao = AppDatabase.getDatabase(application).postDao()
    private val repository = PostRepository(application, postDao)

    // LiveData from the local Room database

    val posts: LiveData<List<PostEntity>> = repository.getPosts()

    // Errors LiveData

    private val _errorLoadingPosts = MutableLiveData<String>()
    val errorLoadingPosts: LiveData<String> = _errorLoadingPosts

    private val _errorUploadPost = MutableLiveData<String>()
    val errorUploadPost: LiveData<String> = _errorUploadPost


    fun refreshPosts() {
        _errorLoadingPosts.value = ""
        viewModelScope.launch {
            val result = repository.loadPosts()
            result.onSuccess {
                Log.d("PostViewModel", "Posts loaded successfully")
            }.onFailure {
                Log.e("PostViewModel", "Failed to load posts: ${it.message}")
                _errorLoadingPosts.value = it.message
            }
        }
    }
    fun createPost(
        content: String,
        imageUri: Uri?,
        currentUser: UserEntity
    ) {
        viewModelScope.launch {
            val photoUrl = imageUri?.let { uri ->
                repository.uploadImageToCloudinary(uri).getOrNull()
            }

            val postId = UUID.randomUUID().toString()
            val post = Post(
                postId = postId,
                uid = currentUser.uid,
                content = content,
                photoUrl = photoUrl,
                username = currentUser.username,
                profileImageUrl = currentUser.photoUrl,
                timestamp = System.currentTimeMillis()
            )
            val result = repository.uploadPost(post)
            result.onSuccess {
                Log.d("PostViewModel", "Post uploaded successfully")
                _errorUploadPost.value = ""
            }.onFailure {
                Log.e("PostViewModel", "Failed to upload post: ${it.message}")
                _errorUploadPost.value = it.message
            }
        }
    }
    fun updatePostUsernameAndProfile(username: String, oldUsername: String,profileImageUrl: String?) {
        viewModelScope.launch {
            repository.updatePostUsernameAndProfile(username, oldUsername,profileImageUrl)
        }
    }
    fun getPostById(postId: String): Post?{
        return runBlocking { repository.getPostById(postId) }
    }

    fun deletePost(postId: String){
        viewModelScope.launch {

            repository.deletePostById(postId)
        }
    }

}

