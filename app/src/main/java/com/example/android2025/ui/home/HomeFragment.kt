package com.example.android2025.ui.home

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android2025.R
import com.example.android2025.databinding.FragmentHomeBinding
import com.example.android2025.viewmodel.AuthViewModel
import androidx.lifecycle.ViewModelProvider

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        authViewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the toolbar as the ActionBar
        (activity as? AppCompatActivity)?.apply {
            setSupportActionBar(binding.topToolbar)
            supportActionBar?.setDisplayShowTitleEnabled(false) // hide the title
        }

        setHasOptionsMenu(true) // enable menu
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_nav_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_feed) {
            findNavController().navigate(R.id.action_homeFragment_to_homeFragment)
            return true
        }

        if (item.itemId == R.id.action_profile) {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
            return true
        }

        if (item.itemId == R.id.action_weather) {
            findNavController().navigate(R.id.action_homeFragment_to_weatherFragment)
            return true
        }

        if (item.itemId == R.id.action_logout) {
            authViewModel.logout()
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
