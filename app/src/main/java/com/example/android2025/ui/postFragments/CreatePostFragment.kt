package com.example.android2025.ui.post

import AuthViewModel
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android2025.MainActivity
import com.example.android2025.databinding.FragmentCreatePostBinding
import com.example.android2025.viewmodel.PostViewModel

class CreatePostFragment : Fragment() {

    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!
    private lateinit var postViewModel: PostViewModel
    private lateinit var authViewModel: AuthViewModel
    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postViewModel = ViewModelProvider(requireActivity()).get(PostViewModel::class.java)
        authViewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)

        // set click listener for image selection
        binding.ivPostImage.setOnClickListener {
            (activity as MainActivity).launchImagePicker { uri ->
                if (uri != null) {
                    selectedImageUri = uri
                    // Display the selected image
                    binding.ivPostImage.setImageURI(uri)
                }
            }
        }

        // set click listener for the upload button
        binding.btnUploadPost.setOnClickListener {
            val content = binding.etPostContent.text.toString().trim()
            if (content.isEmpty()) {
                binding.tvPostError.visibility = View.VISIBLE
                binding.tvPostError.text = "Please enter some content for your post."
            } else {
                binding.tvPostError.visibility = View.GONE

                // retrieve current user details from AuthViewModel
                val currentUser = authViewModel.user.value
                if (currentUser != null) {
                    postViewModel.createPost(
                        content = content,
                        imageUri = selectedImageUri,
                        currentUser = currentUser
                    )
                }

                // navigate back to the HomeFragment after upload.
                findNavController().navigateUp()
            }
        }

        // set click listener for the cancel option
        binding.tvCancelPost.setOnClickListener {
            //  navigate back (pop the back stack)
            findNavController().navigateUp()
        }
    }


}
