package com.example.android2025.data.repository
import com.example.android2025.data.model.Weather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class WeatherRepository {

    suspend fun getWeather(city: String): Weather? {
        return withContext(Dispatchers.IO) {
            try {
                val geoUrl = "https://geocoding-api.open-meteo.com/v1/search?name=$city&count=1&format=json"
                val geoResponse = fetchResponse(geoUrl)
                val geoJson = JSONObject(geoResponse)
                val results = geoJson.optJSONArray("results")

                if (results == null || results.length() == 0) return@withContext null

                val lat = results.getJSONObject(0).getDouble("latitude")
                val lon = results.getJSONObject(0).getDouble("longitude")

                val weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current_weather=true"
                val weatherResponse = fetchResponse(weatherUrl)
                val weatherJson = JSONObject(weatherResponse)

                val currentWeather = weatherJson.getJSONObject("current_weather")
                val temperature = currentWeather.getDouble("temperature")
                val windSpeed = currentWeather.getDouble("windspeed")

                Weather(city, temperature, windSpeed)

            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun fetchResponse(url: String): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        return connection.inputStream.bufferedReader().use { it.readText() }
    }
}
