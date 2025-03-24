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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WeatherSearchFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private var map: GoogleMap? = null
    private var lastLatLng: LatLng? = null

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

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, ArrayList<String>())
        binding.cityInput.setAdapter(adapter)

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        binding.cityInput.setOnItemClickListener { parent, _, position, _ ->
            val selected = parent.getItemAtPosition(position).toString()
            val cleanCityString = selected.split(",")[0]
            weatherViewModel.fetchWeather(cleanCityString)
            Log.d("WeatherSearch", "Selected city from suggestion: $selected")
        }

        binding.cityInput.addTextChangedListener {
            val query = it?.toString() ?: return@addTextChangedListener
            lifecycleScope.launch {
                cityViewModel.fetchCitySuggestions(query)
                Log.d("WeatherSearch", "User typing: $query")
            }
        }

        binding.getWeatherButton.setOnClickListener {
            val city = binding.cityInput.text.toString()
            weatherViewModel.fetchWeather(city)
            Log.d("WeatherSearch", "Manual button clicked for city: $city")
        }

        lifecycleScope.launch {
            cityViewModel.citySuggestions.collectLatest { suggestions ->
                adapter.clear()
                adapter.addAll(suggestions)
                adapter.notifyDataSetChanged()
            }
        }

        lifecycleScope.launch {
            weatherViewModel.weather.collectLatest { weather ->
                Log.d("WeatherSearch", "Flow emitted: $weather")
                binding.weatherResult.visibility = View.VISIBLE
                binding.weatherResult.text = if (weather != null) {
                    lastLatLng = LatLng(weather.latitude, weather.longitude)
                    updateMap()
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

    private fun updateMap() {
        lastLatLng?.let {
            map?.clear()
            map?.addMarker(MarkerOptions().position(it).title("Weather Location"))
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 10f))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        updateMap()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
        _binding = null
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
