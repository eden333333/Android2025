package com.example.android2025.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android2025.R
import com.example.android2025.data.model.Post
import com.example.android2025.databinding.FragmentHomeBinding
import com.example.android2025.ui.adapters.PostAdapter
import com.example.android2025.viewmodel.PostViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var postViewModel: PostViewModel
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postViewModel = ViewModelProvider(requireActivity())[PostViewModel::class.java]

        // recyclerView setup with a LinearLayoutManager and A PostAdapter

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        postAdapter = PostAdapter(emptyList()) {
            postId ->
            run {
                val bundle = Bundle().apply { putString("postId", postId) }
                findNavController().navigate(R.id.viewPostFragment, bundle)
            }
        }
        binding.recyclerView.adapter = postAdapter

        // LiveData Observers

        // Observe the loading LiveData to show/hide the ProgressBar
        postViewModel.loading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        // Observe the errorLoadingPosts LiveData to show/hide the error message
        postViewModel.errorLoadingPosts.observe(viewLifecycleOwner) { errorMsg  ->
            if (!errorMsg.isNullOrEmpty()) {
                binding.tvError.text = errorMsg
                binding.tvError.visibility = View.VISIBLE
            } else {
                binding.tvError.visibility = View.GONE
            }
        }

        // Observe the posts LiveData to update the adapter
        postViewModel.posts.observe(viewLifecycleOwner) { posts ->
            // Cast posts from PostEntity to Post and update the adapter
            postAdapter.updatePosts(posts.map { postEntity ->
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


        // setting FloatingActionButton listener to upload a new post
        binding.fabAddPost.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_createPostFragment)
        }




    }
}
