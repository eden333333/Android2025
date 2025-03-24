package com.example.android2025.data.repository

import android.util.Log
import com.example.android2025.data.remote.LocationIQApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CityRepository(private val apiService: LocationIQApiService) {

    private val apiKey = "pk.b90e2df3364388c588d8117b889448c9"

    suspend fun getCitySuggestions(query: String): List<String> {
        return try {
            val suggestions = apiService.getCitySuggestions(apiKey, query).map { it.display_name }
            Log.d("CityRepository", "Suggestions: $suggestions")
            suggestions
        } catch (e: Exception) {
            Log.e("CityRepository", "Error fetching city suggestions: ${e.message}", e)
            emptyList()
        }
    }
}
