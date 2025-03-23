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
import com.example.android2025.R
import com.example.android2025.databinding.FragmentRegisterBinding


class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var authViewModel: AuthViewModel
    private var imageUri: Uri? = null
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri = it
            binding.ivProfileImage.setImageURI(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        authViewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)
        // Open gallery when image is clicked
        binding.ivProfileImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
        binding.btnSignUp.setOnClickListener {
            Log.d("RegisterFragment", "Sign Up button clicked")

            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val username = binding.etUsername.text.toString()
            val firstName = binding.etFirstName.text.toString()
            val lastName = binding.etLastName.text.toString()
            authViewModel.register(email, password, username, firstName, lastName, imageUri)
        }

        // Handle Sign In navigation
        binding.tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        // Observe user signup status and navigate to HomeFragment on success
        authViewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
            }
        }
    }
}
