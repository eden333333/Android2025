package com.example.android2025.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android2025.databinding.ItemMapillaryImageBinding
import com.squareup.picasso.Picasso

class MapillaryImageAdapter(
    private val images: List<String>
) : RecyclerView.Adapter<MapillaryImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(val binding: ItemMapillaryImageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemMapillaryImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val url = images[position]
        Picasso.get().load(url).into(holder.binding.imageView)
    }

    override fun getItemCount(): Int = images.size
}
