package com.example.android2025.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.AdapterView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.android2025.R
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

        val cityRepo = CityRepository(LocationIQApiService.create())
        val weatherRepo = WeatherRepository()

        cityViewModel = ViewModelProvider(this, CityViewModelFactory(cityRepo))[CityViewModel::class.java]
        weatherViewModel = ViewModelProvider(this, WeatherViewModelFactory(weatherRepo))[WeatherViewModel::class.java]

        val cityInput = view.findViewById<AutoCompleteTextView>(R.id.cityInput)
        val getWeatherButton = view.findViewById<Button>(R.id.getWeatherButton)
        val weatherResult = view.findViewById<TextView>(R.id.weatherResult)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, ArrayList<String>())
        cityInput.setAdapter(adapter)

        cityInput.setOnItemClickListener { parent: AdapterView<*>, _: View, position: Int, _: Long ->
            val selected = parent.getItemAtPosition(position).toString()
            weatherViewModel.fetchWeather(selected)
        }

        cityInput.addTextChangedListener {
            val query = it?.toString() ?: return@addTextChangedListener
            lifecycleScope.launch {
                cityViewModel.fetchCitySuggestions(query)
            }
        }

        getWeatherButton.setOnClickListener {
            val city = cityInput.text.toString()
            weatherViewModel.fetchWeather(city)
        }

        // Observe city suggestions using Flow
        lifecycleScope.launch {
            cityViewModel.citySuggestions.collectLatest { suggestions ->
                adapter.clear()
                adapter.addAll(suggestions)
                adapter.notifyDataSetChanged()
            }
        }

        // Observe weather result using Flow
        lifecycleScope.launch {
            weatherViewModel.weather.collectLatest { weather ->
                weatherResult.text = if (weather != null) {
                    "City: ${weather.city}\nTemperature: ${weather.temperature}°C\nWind Speed: ${weather.windSpeed} km/h"
                } else {
                    ""
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
