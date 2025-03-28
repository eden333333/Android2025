package com.example.android2025.ui.postFragments

import AuthViewModel
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.android2025.R
import com.example.android2025.data.local.PostEntity
import com.example.android2025.data.model.Post
import com.example.android2025.databinding.FragmentViewPostBinding
import com.example.android2025.viewmodel.PostViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ViewPostFragment : Fragment() {

    private var _binding: FragmentViewPostBinding? = null
    private val binding get() = _binding!!

    private lateinit var postViewModel: PostViewModel
    private lateinit var authViewModel: AuthViewModel

    private val args: ViewPostFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postViewModel = ViewModelProvider(requireActivity())[PostViewModel::class.java]
        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]

        // initial binds
        val post = args.post
        bindPost(post)

        // observe post
        postViewModel.posts.observe(viewLifecycleOwner) { posts ->
            val postEntity = posts.find { it.postId == post.postId }
            if (postEntity != null) {
                bindPost(postEntity.toModel())
            }
        }

    }

    private fun bindPost(post: Post) {
        val sdf = SimpleDateFormat("d 'of' MMM 'at' HH:mm", Locale.getDefault())

        // Profile image
        if (!post.profileImageUrl.isNullOrEmpty()) {
            Glide.with(requireContext()).load(post.profileImageUrl).into(binding.ivProfile)
        } else {
            binding.ivProfile.setImageResource(R.drawable.ic_default_profile)
        }

        binding.tvUsername.text = post.username

        // Post image
        if (!post.photoUrl.isNullOrEmpty()) {
            binding.ivImage.visibility = View.VISIBLE
            Glide.with(requireContext()).load(post.photoUrl).into(binding.ivImage)
        } else {
            binding.ivImage.visibility = View.GONE
        }

        // Post timestamp
        binding.tvTime.text = sdf.format(Date(post.timestamp))

        // Post content
        binding.tvContent.text = post.content



        // Show edit/delete buttons if current user owns the post
        val isCurrentUser = authViewModel.user.value?.uid == post.uid
        binding.btnEditPost.visibility = if (isCurrentUser) View.VISIBLE else View.GONE
        binding.btnDelete.visibility = if (isCurrentUser) View.VISIBLE else View.GONE

        // edit button listener
        binding.btnEditPost.setOnClickListener {
            val action = ViewPostFragmentDirections.actionViewPostFragmentToEditPostFragment(post)
            findNavController().navigate(action)
        }

        // delete button listener
        binding.btnDelete.setOnClickListener {
            postViewModel.deletePost(post.postId)
            findNavController().navigateUp()
        }
    }

    // Helper: Convert PostEntity to Post
    private fun PostEntity.toModel(): Post {
        return Post(
            postId = postId,
            uid = uid,
            content = content,
            photoUrl = photoUrl,
            username = username,
            profileImageUrl = profileImageUrl,
            timestamp = timestamp
        )
    }

}
