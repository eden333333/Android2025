package com.example.android2025.ui.home

import AuthViewModel
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.android2025.databinding.FragmentHomeBinding
import androidx.lifecycle.ViewModelProvider

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {



    }
}