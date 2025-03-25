package com.example.android2025.ui.userFragments

import AuthViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.android2025.databinding.FragmentEditProfileBinding

class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var authViewModel: AuthViewModel
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

        // initial form values from SafeArgs
        binding.etUsername.setText(args.username)
        binding.etFirstName.setText(args.firstName)
        binding.etLastName.setText(args.lastName)

        // Load profile photo from args
        Glide.with(this)
            .load(args.photoUrl)
            .placeholder(com.example.android2025.R.drawable.ic_default_profile)
            .into(binding.ivProfileImage)

        }

}
