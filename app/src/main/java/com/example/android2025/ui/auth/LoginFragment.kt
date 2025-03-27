package com.example.android2025.ui.auth

import AuthViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android2025.R
import com.example.android2025.databinding.FragmentLoginBinding
import com.example.android2025.viewmodel.PostViewModel


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var authViewModel: AuthViewModel
    private lateinit var postViewModel: PostViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
        postViewModel = ViewModelProvider(requireActivity())[PostViewModel::class.java]


        // Listeners

        // Handle Login button click
        binding.btnLogin.setOnClickListener {
            binding.tvError.text = ""
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            // Validate input fields
            when {
                email.isEmpty() || password.isEmpty() -> {
                    binding.tvError.text = "Please fill in all fields."
                    binding.tvError.visibility = View.VISIBLE
                    return@setOnClickListener
                }
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    binding.tvError.text = "Please enter a valid email address."
                    binding.tvError.visibility = View.VISIBLE
                    return@setOnClickListener
                }
                else -> {
                    binding.tvError.visibility = View.GONE
                }
            }
            authViewModel.login(email, password)
        }

        // Handle Sign Up navigation
        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        //Observers

        // Observe loading status and show/hide progress bar
        authViewModel.loading.observe(viewLifecycleOwner) { loading ->
            if (loading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnLogin.isEnabled = false
            } else {
                binding.progressBar.visibility = View.GONE
                binding.btnLogin.isEnabled = true
            }
        }

        // Observe error messages and display them
        authViewModel.errorLogin.observe(viewLifecycleOwner) { errorMsg ->
            if (errorMsg.isNullOrEmpty()) {
                binding.tvError.visibility = View.GONE
            } else {
                binding.tvError.text = errorMsg
                binding.tvError.visibility = View.VISIBLE
            }
        }



    }
}
