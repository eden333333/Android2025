package com.example.android2025.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android2025.R
import com.example.android2025.databinding.FragmentLoginBinding
import com.example.android2025.viewmodel.AuthViewModel

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        // Observe authentication state
        authViewModel.authState.observe(viewLifecycleOwner) { isAuthenticated ->
            if (isAuthenticated) {
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            }
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            authViewModel.login(email, password)
        }
        // Handle Sign Up navigation
        binding.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        return binding.root
    }
}
