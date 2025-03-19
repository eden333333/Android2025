package com.example.android2025.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android2025.R
import com.example.android2025.databinding.FragmentRegisterBinding
import com.example.android2025.viewmodel.AuthViewModel

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        // Observe authentication state
        authViewModel.authState.observe(viewLifecycleOwner) { isAuthenticated ->
            if (isAuthenticated) {
                findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
            }
        }

        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val username = binding.etUsername.text.toString()
            val firstName = binding.etFirstName.text.toString()
            val lastName = binding.etLastName.text.toString()

            authViewModel.signUp(email, password, username, firstName, lastName)
        }
        // Handle Sign In navigation
        binding.tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        return binding.root
    }
}
