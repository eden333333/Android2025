package com.example.android2025.ui.userFragments

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
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.android2025.MainActivity
import com.example.android2025.databinding.FragmentEditProfileBinding
import com.example.android2025.viewmodel.PostViewModel

class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var postViewModel: PostViewModel
    private var imageUri: Uri? = null

    private val args: EditProfileFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
        postViewModel = ViewModelProvider(requireActivity())[PostViewModel::class.java]

        // fill in the form with the user's current information
        // initial form values from SafeArgs

        binding.etUsername.setText(args.username)
        binding.etFirstName.setText(args.firstName)
        binding.etLastName.setText(args.lastName)
        Glide.with(this) // Load profile photo from args
            .load(args.photoUrl)
            .placeholder(com.example.android2025.R.drawable.ic_default_profile)
            .into(binding.ivProfileImage)


        // Listeners

        // Save button listener
        binding.btnSave.setOnClickListener {
            binding.tvError.text = ""

            val username = binding.etUsername.text.toString()
            val firstName = binding.etFirstName.text.toString()
            val lastName = binding.etLastName.text.toString()

            // Validate input fields
            when {
                username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() -> {
                    binding.tvError.text = "Please fill in all fields."
                    binding.tvError.visibility = View.VISIBLE
                    return@setOnClickListener
                }
                else -> {
                    binding.tvError.visibility = View.GONE
                }
            }
            authViewModel.updateUser(username, firstName, lastName, imageUri, args.photoUrl) { newPhotoUrl ->
                postViewModel.updatePostUsernameAndProfile(
                    username,
                    args.username,
                    newPhotoUrl ?: args.photoUrl
                )
            }
            findNavController().navigateUp()
        }
        // Profile image listener to launch image picker
        binding.ivProfileImage.setOnClickListener {
            (activity as? MainActivity)?.launchImagePicker { uri ->
                uri?.let {
                    imageUri = it
                    binding.ivProfileImage.setImageURI(it)
                }
            }
        }

        // Navigate back to ProfileFragment
        binding.tvCancelPost.setOnClickListener {
            findNavController().navigateUp()
        }

        // Observers


        // Observe error messages and display them
        authViewModel.errorUpdateUser.observe(viewLifecycleOwner) { errorMsg ->
            if (errorMsg.isNullOrEmpty()) {
                binding.tvError.visibility = View.GONE
            } else {
                binding.tvError.text = errorMsg
                binding.tvError.visibility = View.VISIBLE
            }
        }

        // Observe loading state and show/hide progress bar
        authViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnSave.isEnabled = false
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

    }
}




