package com.example.android2025.data.remote

import com.example.android2025.data.model.LocationIQCitySuggestion
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface LocationIQApiService {
    @GET("autocomplete")
    suspend fun getCitySuggestions(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("limit") limit: Int = 5
    ): List<LocationIQCitySuggestion>

    companion object {
        fun create(): LocationIQApiService {
            val client = OkHttpClient.Builder().build()

            return Retrofit.Builder()
                .baseUrl("https://api.locationiq.com/v1/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(LocationIQApiService::class.java)
        }
    }
}