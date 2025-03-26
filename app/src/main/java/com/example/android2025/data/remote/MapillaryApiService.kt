package com.example.android2025.data.remote

import com.example.android2025.data.model.MapillaryResponse
import retrofit2.http.GET
import retrofit2.http.Query

data class MapillaryImageResponse(
    val features: List<Feature>
)

data class Feature(
    val properties: Properties
)

data class Properties(
    val thumbnail_url: String
)

interface MapillaryApiService {
    @GET("images")
    suspend fun getImagesNearby(
        @Query("access_token") accessToken: String,
        @Query("bbox") bbox: String,
        @Query("fields") fields: String = "thumb_256_url",
        @Query("limit") limit: Int = 5
    ): MapillaryResponse

    companion object {
        fun create(): MapillaryApiService {
            return retrofit2.Retrofit.Builder()
                .baseUrl("https://graph.mapillary.com/")
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .build()
                .create(MapillaryApiService::class.java)
        }
    }
}
