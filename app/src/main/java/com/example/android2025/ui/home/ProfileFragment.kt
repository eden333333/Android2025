package com.example.android2025.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android2025.R
import com.example.android2025.databinding.FragmentProfileBinding
import com.example.android2025.viewmodel.AuthViewModel

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        authViewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)

        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.tvTitle.text = "Welcome to Profile Page"
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.apply {
            setSupportActionBar(binding.topToolbar)
            supportActionBar?.setDisplayShowTitleEnabled(false) // ✅ Hide the title
        }

        setHasOptionsMenu(true) // Enables the menu
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_nav_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_feed) {
            findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
            return true
        }

        if (item.itemId == R.id.action_weather) {
            findNavController().navigate(R.id.action_profileFragment_to_weatherFragment)
            return true
        }

        if (item.itemId == R.id.action_logout) {
            authViewModel.logout()
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
            return true
        }

        return super.onOptionsItemSelected(item)
    }


}
