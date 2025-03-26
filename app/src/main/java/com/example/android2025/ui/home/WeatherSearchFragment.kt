package com.example.android2025.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.android2025.data.remote.LocationIQApiService
import com.example.android2025.data.repository.CityRepository
import com.example.android2025.data.repository.WeatherRepository
import com.example.android2025.databinding.FragmentWeatherSearchBinding
import com.example.android2025.viewmodel.CityViewModel
import com.example.android2025.viewmodel.CityViewModelFactory
import com.example.android2025.viewmodel.WeatherViewModel
import com.example.android2025.viewmodel.WeatherViewModelFactory
import com.example.android2025.data.remote.MapillaryApiService
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.HttpException

class WeatherSearchFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private var map: GoogleMap? = null
    private var lastLatLng: LatLng? = null
    private val locationPermissionCode = 1001

    private var _binding: FragmentWeatherSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var cityViewModel: CityViewModel
    private lateinit var weatherViewModel: WeatherViewModel

    private val mapillaryApi = MapillaryApiService.create()
    private val mapillaryToken = "MLY|9996696557049049|fcf315c2f6e4a940d04e6b9ffe1158ef"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkLocationPermission()

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
        }

        binding.cityInput.addTextChangedListener {
            val query = it?.toString() ?: return@addTextChangedListener
            lifecycleScope.launch {
                cityViewModel.fetchCitySuggestions(query)
            }
        }

        binding.getWeatherButton.setOnClickListener {
            val city = binding.cityInput.text.toString()
            weatherViewModel.fetchWeather(city)
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
                binding.weatherResult.visibility = View.VISIBLE
                binding.weatherResult.text = if (weather != null) {
                    lastLatLng = LatLng(weather.latitude, weather.longitude)
                    updateMap()
                    loadMapillaryImage(weather.latitude, weather.longitude, weather.city)
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

        binding.mapTypeButton.setOnClickListener {
            map?.let {
                it.mapType = when (it.mapType) {
                    GoogleMap.MAP_TYPE_NORMAL -> GoogleMap.MAP_TYPE_SATELLITE
                    GoogleMap.MAP_TYPE_SATELLITE -> GoogleMap.MAP_TYPE_TERRAIN
                    GoogleMap.MAP_TYPE_TERRAIN -> GoogleMap.MAP_TYPE_HYBRID
                    else -> GoogleMap.MAP_TYPE_NORMAL
                }
                binding.mapTypeButton.text = "Map: ${mapTypeLabel(it.mapType)}"
            }
        }
    }

    private fun mapTypeLabel(type: Int): String = when (type) {
        GoogleMap.MAP_TYPE_NORMAL -> "Normal"
        GoogleMap.MAP_TYPE_SATELLITE -> "Satellite"
        GoogleMap.MAP_TYPE_TERRAIN -> "Terrain"
        GoogleMap.MAP_TYPE_HYBRID -> "Hybrid"
        else -> "Unknown"
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        } else {
            enableMyLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == locationPermissionCode && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation()
        } else {
            Snackbar.make(binding.root, "Location permission denied", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map?.isMyLocationEnabled = true
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    val cameraPosition = CameraPosition.Builder()
                        .target(latLng)
                        .zoom(12f)
                        .build()
                    map?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
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

    private fun loadMapillaryImage(lat: Double, lon: Double, cityName: String) {
    val bbox = "${lon - 0.01},${lat - 0.01},${lon + 0.01},${lat + 0.01}"
    Log.d("Mapillary", "BBox: $bbox")

    lifecycleScope.launch {
        try {
            val response = mapillaryApi.getImagesNearby(mapillaryToken, bbox, limit = 5)
            val imageUrls = response.data?.mapNotNull { it.thumbUrl } ?: emptyList()

            if (imageUrls.isNotEmpty()) {
                binding.mapillaryRecycler.visibility = View.VISIBLE
                binding.mapillaryRecycler.adapter = MapillaryImageAdapter(imageUrls)
                binding.mapillaryRecycler.layoutManager =
                    androidx.recyclerview.widget.LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            } else {
                binding.mapillaryRecycler.visibility = View.GONE
                Snackbar.make(binding.root, "No Mapillary images found", Snackbar.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("Mapillary", "Failed to load images: ${e.localizedMessage}", e)
        }
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
