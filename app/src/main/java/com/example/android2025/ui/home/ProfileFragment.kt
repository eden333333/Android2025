package com.example.android2025.ui.home

import AuthViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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

        // Initialize ViewModel
        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]

        //Observers

        // Observe the user data from ViewModel
        authViewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                // Load user profile photo clearly using Glide
                Glide.with(this)
                    .load(it.photoUrl)
                    .placeholder(R.drawable.ic_default_profile)
                    .error(R.drawable.ic_default_profile)
                    .into(binding.ivProfileImage)

                binding.tvTitle.text = "User Info"

                // Update other user details
                binding.tvFullName.text = "${it.firstName} ${it.lastName}"
                binding.tvEmail.text = it.email
                binding.tvUsername.text = "@${it.username}"

                binding.ivSettings.setOnClickListener {
                    user.let {
                        val action = ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment(
                            uid = it.uid,
                            firstName = it.firstName,
                            lastName = it.lastName,
                            username = it.username,
                            photoUrl = it.photoUrl ?: ""
                        )
                        findNavController().navigate(action)
                    }
                }



            }
        }


    }
}
