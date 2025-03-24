package com.example.android2025.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.android2025.databinding.FragmentWeatherSearchBinding
import com.example.android2025.data.remote.LocationIQApiService
import com.example.android2025.data.repository.CityRepository
import com.example.android2025.data.repository.WeatherRepository
import com.example.android2025.viewmodel.CityViewModel
import com.example.android2025.viewmodel.CityViewModelFactory
import com.example.android2025.viewmodel.WeatherViewModel
import com.example.android2025.viewmodel.WeatherViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WeatherSearchFragment : Fragment() {

    private var _binding: FragmentWeatherSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var cityViewModel: CityViewModel
    private lateinit var weatherViewModel: WeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize repositories and viewmodels
        val cityRepo = CityRepository(LocationIQApiService.create())
        val weatherRepo = WeatherRepository()

        cityViewModel = ViewModelProvider(this, CityViewModelFactory(cityRepo))[CityViewModel::class.java]
        weatherViewModel = ViewModelProvider(this, WeatherViewModelFactory(weatherRepo))[WeatherViewModel::class.java]

        // Setup adapter for AutoCompleteTextView
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, ArrayList<String>())
        binding.cityInput.setAdapter(adapter)

        // Handle selection from suggestions
        binding.cityInput.setOnItemClickListener { parent, _, position, _ ->
            val selected = parent.getItemAtPosition(position).toString()
            val cleanCityString = selected.split(",")[0]
            weatherViewModel.fetchWeather(cleanCityString)
            Log.d("WeatherSearch", "Selected city from suggestion: $selected")
        }

        // Handle typing input
        binding.cityInput.addTextChangedListener {
            val query = it?.toString() ?: return@addTextChangedListener
            lifecycleScope.launch {
                cityViewModel.fetchCitySuggestions(query)
                Log.d("WeatherSearch", "User typing: $query")
            }
        }

        // Handle "Get Weather" button click
        binding.getWeatherButton.setOnClickListener {
            val city = binding.cityInput.text.toString()
            weatherViewModel.fetchWeather(city)
            Log.d("WeatherSearch", "Manual button clicked for city: $city")
        }

        // Observe city suggestions
        lifecycleScope.launch {
            cityViewModel.citySuggestions.collectLatest { suggestions ->
                adapter.clear()
                adapter.addAll(suggestions)
                adapter.notifyDataSetChanged()
            }
        }

        // Observe weather results
   lifecycleScope.launch {
    weatherViewModel.weather.collectLatest { weather ->
    Log.d("WeatherSearch", "Flow emitted: $weather")
        binding.weatherResult.visibility = View.VISIBLE //
        binding.weatherResult.text = if (weather != null) {
            """
                🌤 City: ${weather.city}
                🌡 Temperature: ${weather.temperature}°C
                💨 Wind Speed: ${weather.windSpeed} km/h
            """.trimIndent()
        } else {
            "No weather data found."
        }
    }
}

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
