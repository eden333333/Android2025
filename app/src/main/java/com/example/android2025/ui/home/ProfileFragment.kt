package com.example.android2025.ui.home

import AuthViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.android2025.R
import com.example.android2025.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Initialize ViewModel clearly
        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]

        // Observe the user data from ViewModel
        authViewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.tvTitle.text = "Welcome, ${it.firstName}!"

                // Update other user details
                binding.tvFullName.text = "${it.firstName} ${it.lastName}"
                binding.tvEmail.text = it.email
                binding.tvUsername.text = "@${it.username}"

                // Load user profile photo clearly using Glide
                Glide.with(this)
                    .load(it.photoUrl)
                    .placeholder(R.drawable.ic_default_profile)
                    .error(R.drawable.ic_default_profile)
                    .into(binding.ivProfileImage)
            }
        }

    }
}
