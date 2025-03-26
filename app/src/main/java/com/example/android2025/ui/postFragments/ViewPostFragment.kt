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
import com.bumptech.glide.Glide
import com.example.android2025.R
import com.example.android2025.data.model.Post
import com.example.android2025.databinding.FragmentCreatePostBinding
import com.example.android2025.databinding.FragmentViewPostBinding
import com.example.android2025.viewmodel.PostViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ViewPostFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewPostFragment : Fragment() {
    private var _binding: FragmentViewPostBinding? = null
    private val binding get() = _binding!!

    private lateinit var postViewModel: PostViewModel
    private lateinit var authViewModel: AuthViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        postViewModel = ViewModelProvider(requireActivity()).get(PostViewModel::class.java)
        authViewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)
        val tvUsername: TextView = binding.tvUsername
        val tvContent: TextView = binding.tvContent
        val ivImage: ImageView = binding.ivImage
        val ivProfile: ImageView = binding.ivProfile
        val btnEdit : Button = binding.btnEditPost
        val btnDelete : Button = binding.btnDelete

        val sdf = SimpleDateFormat("d 'of' MMM 'at' HH:mm", Locale.getDefault())

        //holder.tvTime.text = formattedTime

        val postId = arguments?.getString("postId")

        if(postId != null){
            lifecycleScope.launch {
                val post: Post? = postViewModel.getPostById(postId)
                if(post != null){
                    tvUsername.text = post.username
                    tvContent.text = post.content
                    val formattedTime = sdf.format(Date(post.timestamp))
                    //TODO ad timestamp
                    if (!post.photoUrl.isNullOrEmpty()) {
                        ivImage.visibility = View.VISIBLE
                        Glide.with(view.context)
                            .load(post.photoUrl)
                            .into(ivImage)
                    } else {
                        ivImage.visibility = View.GONE
                    }
                    if (!post.profileImageUrl.isNullOrEmpty()) {
                        ivProfile.visibility = View.VISIBLE
                        Glide.with(view.context)
                            .load(post.profileImageUrl)
                            .into(ivImage)
                    } else {
                        ivProfile.visibility = View.GONE
                    }
                    authViewModel.user.observe(viewLifecycleOwner) { user ->
                        user?.let {
                            if(post.uid != user.uid){
                                btnEdit.visibility = View.GONE
                                btnDelete.visibility = View.GONE
                            }
                        }
                    }

                }
            }
        }else{
            tvContent.text = "Post not found"
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_view_post, container, false)
        _binding = FragmentViewPostBinding.inflate(inflater, container, false)
        return binding.root
    }


}