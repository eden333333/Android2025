package com.example.android2025.ui.home

import AuthViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android2025.R
import com.example.android2025.data.model.Post
import com.example.android2025.databinding.FragmentUserPostsBinding
import com.example.android2025.ui.adapters.PostAdapter
import com.example.android2025.viewmodel.PostViewModel

class UserPostsFragment: Fragment() {
    private lateinit var binding: FragmentUserPostsBinding
    private lateinit var postViewModel: PostViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postViewModel = ViewModelProvider(requireActivity())[PostViewModel::class.java]
        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]

        // recyclerView setup with a LinearLayoutManager and A PostAdapter

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        postAdapter = PostAdapter(emptyList()) { post ->
            val action = UserPostsFragmentDirections.actionUserPostsFragmentToViewPostFragment(post)
            findNavController().navigate(action)
        }
        binding.recyclerView.adapter = postAdapter


        // LiveData Observers

        // Observe the loading LiveData to show/hide the ProgressBar
        postViewModel.loading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        // observing the posts LiveData and updating the adapter when posts change

        postViewModel.posts.observe(viewLifecycleOwner) { posts ->
            // Retrieve the current user from the AuthViewModel
            val currentUser = authViewModel.user.value
            val userPosts = posts.filter { postEntity ->
                postEntity.uid == currentUser?.uid
            }
            // Cast posts from PostEntity to Post and update the adapter
            postAdapter.updatePosts(userPosts.map { postEntity ->
                Post(
                    postId = postEntity.postId,
                    uid = postEntity.uid,
                    username = postEntity.username,
                    profileImageUrl = postEntity.profileImageUrl,
                    content = postEntity.content,
                    photoUrl = postEntity.photoUrl,
                    timestamp = postEntity.timestamp
                )
            })

        }
    }
}