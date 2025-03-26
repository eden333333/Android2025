package com.example.android2025.data.model
import com.google.gson.annotations.SerializedName


data class MapillaryResponse(
    val data: List<MapillaryImage>?
)

data class MapillaryImage(
    @SerializedName("thumb_256_url")
    val thumbUrl: String?
)
