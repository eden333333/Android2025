package com.example.android2025.ui.postFragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.android2025.MainActivity
import com.example.android2025.R
import com.example.android2025.data.local.PostEntity
import com.example.android2025.data.model.Post
import com.example.android2025.databinding.FragmentEditPostBinding
import com.example.android2025.databinding.FragmentViewPostBinding
import com.example.android2025.viewmodel.PostViewModel
import kotlinx.coroutines.launch


class EditPostFragment : Fragment() {

    private var _binding: FragmentEditPostBinding? = null
    private val binding get() = _binding!!

    private var selectedImageUri: Uri? = null
    private lateinit var postViewModel: PostViewModel

    private val args: EditPostFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        postViewModel = ViewModelProvider(requireActivity()).get(PostViewModel::class.java)

        val etPostContent: EditText = binding.etPostContent
        val ivPostImage: ImageView = binding.ivPostImage
        val btnUpdatePost : Button = binding.btnUpdatePost
        val tvPostError: TextView = binding.tvPostError
        val tvCancelPost: TextView = binding.tvCancelPost

        val post = args.post

        /** UI setup **/

        // Load the post data into the UI
        etPostContent.setText(post.content)

        if (!post.photoUrl.isNullOrEmpty()) {
            ivPostImage.visibility = View.VISIBLE
            Glide.with(view.context)
                .load(post.photoUrl)
                .into(ivPostImage)
        } else {
            ivPostImage.visibility = View.GONE
        }

        /** Listeners **/

        // Save button

        binding.btnUpdatePost.setOnClickListener {
            binding.tvPostError.text = ""
            val content = binding.etPostContent.text.toString().trim()

            if (content.isEmpty()) {
                binding.tvPostError.visibility = View.VISIBLE
                binding.tvPostError.text = "Content field cannot be empty."
                return@setOnClickListener
            } else {
                binding.tvPostError.visibility = View.GONE
                postViewModel.updatePost(post, content, selectedImageUri)
                findNavController().navigateUp()
            }
        }

        // Image picker

        binding.ivPostImage.setOnClickListener {
                    (activity as MainActivity).launchImagePicker { uri ->
                        if (uri != null) {
                            selectedImageUri = uri
                            // Display the selected image
                            binding.ivPostImage.setImageURI(uri)
                        }
                    }
        }

        // Cancel button
        binding.tvCancelPost.setOnClickListener {
            findNavController().navigateUp()
        }

        // Observers

        // Observe loading state and show/hide progress bar
        postViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnUpdatePost.isEnabled = false
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }


        // Observe the error message when updating a post
        postViewModel.errorUpdatePost.observe(viewLifecycleOwner) { error ->
            if (error.isNotEmpty()) {
                tvPostError.text = error
                tvPostError.visibility = View.VISIBLE
            }
        }
    }
}

