package com.example.android2025.viewmodel
import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.android2025.data.local.AppDatabase
import com.example.android2025.data.local.PostEntity
import com.example.android2025.data.local.UserEntity
import com.example.android2025.data.model.Post
import com.example.android2025.data.repository.PostRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.UUID




class PostViewModel(application: Application) : AndroidViewModel(application) {


    private val postDao = AppDatabase.getDatabase(application).postDao()
    private val repository = PostRepository(application, postDao)

    // LiveData from the local Room database
    val posts: LiveData<List<PostEntity>> = repository.getPosts()

    init {
        // loads posts from Firestore when the ViewModel is created.
        viewModelScope.launch {
            repository.loadPosts()
        }
    }
    fun createPost(
        content: String,
        imageUri: Uri?,
        currentUser: UserEntity
    ) {
        viewModelScope.launch {
            val photoUrl = imageUri?.let { uri ->
                repository.uploadImageToCloudinary(uri)
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
            viewModelScope.launch {
                repository.uploadPost(post)
            }
        }
    }
}

