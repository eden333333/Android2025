package com.example.android2025.ui.postFragments

import AuthViewModel
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.android2025.R
import com.example.android2025.data.model.Post
import com.example.android2025.databinding.FragmentCreatePostBinding
import com.example.android2025.databinding.FragmentViewPostBinding
import com.example.android2025.ui.home.HomeFragmentDirections
import com.example.android2025.viewmodel.PostViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ViewPostFragment : Fragment() {

    private var _binding: FragmentViewPostBinding? = null
    private val binding get() = _binding!!

    private lateinit var postViewModel: PostViewModel
    private lateinit var authViewModel: AuthViewModel

    private val args: ViewPostFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_view_post, container, false)
        _binding = FragmentViewPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postViewModel = ViewModelProvider(requireActivity()).get(PostViewModel::class.java)
        authViewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)

        val post = args.post

        val tvUsername: TextView = binding.tvUsername
        val tvTime: TextView = binding.tvTime
        val tvContent: TextView = binding.tvContent
        val ivImage: ImageView = binding.ivImage
        val ivProfile: ImageView = binding.ivProfile
        val btnEdit : Button = binding.btnEditPost
        val btnDelete : Button = binding.btnDelete

        val sdf = SimpleDateFormat("d 'of' MMM 'at' HH:mm", Locale.getDefault())


        /** Setting the UI **/

        tvUsername.text = post.username
        tvContent.text = post.content
        tvTime.text = sdf.format(Date(post.timestamp))

        // Profile image
        if (!post.profileImageUrl.isNullOrEmpty()) {
            Glide.with(requireContext()).load(post.profileImageUrl).into(ivProfile)
        } else {
            ivProfile.setImageResource(R.drawable.ic_default_profile)
        }

        // Post image (if exists)
        if (!post.photoUrl.isNullOrEmpty()) {
            ivImage.visibility = View.VISIBLE
            Glide.with(requireContext()).load(post.photoUrl).into(ivImage)
        } else {
            ivImage.visibility = View.GONE
        }

        // Edit and delete buttons

        // Show buttons only if the post belongs to the current user
        if (authViewModel.user.value?.uid == post.uid) {
            btnEdit.visibility = View.VISIBLE
            btnDelete.visibility = View.VISIBLE
        } else {
            btnEdit.visibility = View.GONE
            btnDelete.visibility = View.GONE
        }

        /** Listeners **/

        // Edit button

        btnEdit.setOnClickListener {
            val action = ViewPostFragmentDirections.actionViewPostFragmentToEditPostFragment(post)
            findNavController().navigate(action)
        }








    }
}