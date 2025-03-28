package com.example.android2025.ui.postFragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.android2025.MainActivity
import com.example.android2025.R
import com.example.android2025.data.local.PostEntity
import com.example.android2025.data.model.Post
import com.example.android2025.databinding.FragmentEditPostBinding
import com.example.android2025.databinding.FragmentViewPostBinding
import com.example.android2025.viewmodel.PostViewModel
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 * Use the [EditPostFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditPostFragment : Fragment() {
    private var _binding: FragmentEditPostBinding? = null
    private val binding get() = _binding!!
    private var selectedImageUri: Uri? = null
    private lateinit var postViewModel: PostViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEditPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        postViewModel = ViewModelProvider(requireActivity()).get(PostViewModel::class.java)

        val etPostContent: EditText = binding.etPostContent
        val ivPostImage: ImageView = binding.ivPostImage
        val btnUpdatePost : Button = binding.btnUpdatePost
        val tvPostError: TextView = binding.tvPostError
        val tvCancelPost: TextView = binding.tvCancelPost


        val postId = arguments?.getString("postId")
        if(postId == null){
            etPostContent.setText("Post not found")
            btnUpdatePost.isEnabled = false
            return;
        }

        lifecycleScope.launch{
            val post: PostEntity? = postViewModel.getPostById(postId)
            if(post != null){
                etPostContent.setText(post.content)
                if (!post.photoUrl.isNullOrEmpty()) {
                    ivPostImage.visibility = View.VISIBLE
                    Glide.with(view.context)
                        .load(post.photoUrl)
                        .into(ivPostImage)
                } else {
                    ivPostImage.visibility = View.GONE
                }
                binding.btnUpdatePost.setOnClickListener {
                    binding.tvPostError.text = ""
                    val content = binding.etPostContent.text.toString().trim()
                    if (content.isEmpty()) {
                        binding.tvPostError.visibility = View.VISIBLE
                        binding.tvPostError.text = "Please enter some content for your post."
                        return@setOnClickListener
                    } else {
                        binding.tvPostError.visibility = View.GONE
                        val postEntity = PostEntity(
                            postId = post.postId,
                            uid = post.uid,
                            username = post.username,
                            profileImageUrl = post.profileImageUrl,
                            content = post.content,
                            photoUrl = post.photoUrl,
                            timestamp = post.timestamp
                        )
                        postViewModel.updatePost(postEntity, content, selectedImageUri)
                        postViewModel.refreshPosts()
                        // navigate back to the HomeFragment after upload.
                        findNavController().navigateUp()
                    }
                }
                binding.ivPostImage.setOnClickListener {
                    (activity as MainActivity).launchImagePicker { uri ->
                        if (uri != null) {
                            selectedImageUri = uri
                            // Display the selected image
                            binding.ivPostImage.setImageURI(uri)
                        }
                    }
                }
            }
        }

        binding.tvCancelPost.setOnClickListener {
            //  navigate back (pop the back stack)
            findNavController().navigateUp()
        }
    }
}