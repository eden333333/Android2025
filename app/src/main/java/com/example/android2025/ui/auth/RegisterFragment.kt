package com.example.android2025.ui.auth

import AuthViewModel
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android2025.MainActivity
import com.example.android2025.R
import com.example.android2025.databinding.FragmentRegisterBinding
import com.example.android2025.viewmodel.PostViewModel


class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var postViewModel: PostViewModel

    private var imageUri: Uri? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
        postViewModel = ViewModelProvider(requireActivity())[PostViewModel::class.java]


        // Listeners

        // Handle Sign Up button click

        binding.btnSignUp.setOnClickListener {
            // Clear error message
            binding.tvError.text = ""

            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val username = binding.etUsername.text.toString()
            val firstName = binding.etFirstName.text.toString()
            val lastName = binding.etLastName.text.toString()

            // Validate input fields
            when {
                email.isEmpty() || password.isEmpty() || username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() -> {
                    binding.tvError.text = "Please fill in all fields."
                    binding.tvError.visibility = View.VISIBLE
                    return@setOnClickListener
                }
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    binding.tvError.text = "Please enter a valid email address."
                    binding.tvError.visibility = View.VISIBLE
                    return@setOnClickListener
                }
                password.length < 6 -> {
                    binding.tvError.text = "Password must be at least 6 characters."
                    binding.tvError.visibility = View.VISIBLE
                    return@setOnClickListener
                }
                else -> {
                    binding.tvError.visibility = View.GONE
                }
            }
            authViewModel.register(email, password, username, firstName, lastName, imageUri)
        }

        // Open gallery when image is clicked and set the image to the ImageView
        binding.ivProfileImage.setOnClickListener {
            (activity as? MainActivity)?.launchImagePicker { uri ->
                uri?.let {
                    imageUri = it
                    binding.ivProfileImage.setImageURI(it)
                }
            }
        }

        // Handle Sign In navigation
        binding.tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }



        // Observers

        // Observe loading status and show/hide progress bar
        authViewModel.loading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.tvLogin.isEnabled = false
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        // Observe error messages and display them
        authViewModel.errorRegister.observe(viewLifecycleOwner) { errorMsg ->
            if (errorMsg.isNullOrEmpty()) {
                binding.tvError.visibility = View.GONE
            } else {
                binding.tvError.text = errorMsg
                binding.tvError.visibility = View.VISIBLE
            }
        }

        // Observe user signup status and navigate to HomeFragment on success
        authViewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                postViewModel.refreshPosts()

            }
        }



    }
}
