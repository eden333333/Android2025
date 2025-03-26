package com.example.android2025.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android2025.R
import com.example.android2025.data.model.Post
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostAdapter(private var posts: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        val ivProfile: ImageView = itemView.findViewById(R.id.ivProfile)
        val tvContent: TextView = itemView.findViewById(R.id.tvContent)
        val ivImage: ImageView = itemView.findViewById(R.id.ivImage)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.tvUsername.text = post.username
        if (!post.profileImageUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(post.profileImageUrl)
                .into(holder.ivProfile)
        } else {
            // Default profile image
            holder.ivProfile.setImageResource(R.drawable.ic_default_profile)
        }
        // Bind post content
        holder.tvContent.text = post.content
        // Bind post image if provided
        if (!post.photoUrl.isNullOrEmpty()) {
            holder.ivImage.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                .load(post.photoUrl)
                .into(holder.ivImage)
        } else {
            holder.ivImage.visibility = View.GONE
        }
        // Bind post time
        // Format timestamp
        val sdf = SimpleDateFormat("d 'of' MMM 'at' HH:mm", Locale.getDefault())
        val formattedTime = sdf.format(Date(post.timestamp))
        holder.tvTime.text = formattedTime

    }

    override fun getItemCount(): Int = posts.size

    // Helper function to update the adapter's data
    fun updatePosts(newPosts: List<Post>) {
        posts = newPosts
        notifyDataSetChanged()
    }
}
