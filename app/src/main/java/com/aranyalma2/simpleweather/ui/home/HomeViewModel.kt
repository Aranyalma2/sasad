package com.aranyalma2.simpleweather.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aranyalma2.simpleweather.data.local.LocationDao
import com.aranyalma2.simpleweather.data.local.LocationEntity
import com.aranyalma2.simpleweather.data.local.LocationWithWeather
import com.aranyalma2.simpleweather.data.local.WeatherDao
import com.aranyalma2.simpleweather.data.location.LocationProvider
import com.aranyalma2.simpleweather.data.mapper.toDailyEntities
import com.aranyalma2.simpleweather.data.mapper.toHourlyEntities
import com.aranyalma2.simpleweather.data.remote.LocationApiService
import com.aranyalma2.simpleweather.data.remote.WeatherApiService
import com.aranyalma2.simpleweather.data.repository.LocationRepository
import com.aranyalma2.simpleweather.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val locations: List<LocationWithWeather> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherApi: WeatherApiService,
    private val locationApi: LocationApiService,
    private val weatherDao: WeatherDao,
    private val locationDao: LocationDao,
    private val locationProvider: LocationProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val weatherRepository = WeatherRepository(weatherApi)
    private val locationRepository = LocationRepository(locationApi)

    init {
        loadSavedLocations()
    }

    private fun loadSavedLocations() {
        viewModelScope.launch {
            try {
                // Load locations from Room database
                val locations = locationDao.getAllLocation().first()

                // If no locations are saved, we could add default ones for first-time users
                if (locations.isEmpty()) {
                    _uiState.value = HomeUiState(
                        isLoading = false
                    )
                    return@launch
                }

                // Fetch weather data for all locations
                val locationsWithWeather = mutableListOf<LocationWithWeather>()

                for (location in locations) {
                    val locationWithWeather = weatherDao.getLocationWithWeather(location.id.toLong())
                    locationWithWeather?.let {
                        locationsWithWeather.add(it)
                    }
                }

                _uiState.value = HomeUiState(
                    isLoading = false,
                    locations = locationsWithWeather
                )

                // Refresh weather data in background for all locations
                refreshWeatherDataForAllLocations(locations)

            } catch (e: Exception) {
                _uiState.value = HomeUiState(
                    isLoading = false,
                    error = "Failed to load locations: ${e.message}"
                )
            }
        }
    }

    private suspend fun fetchAndSaveWeatherForLocation(locationId: Int, location: LocationEntity) {
        try {
            val weather = weatherRepository.getWeather(location.latitude, location.longitude)

            Log.d("weather", weather.toString())

            // Save hourly and daily weather data to database
            val hourlyEntities = weather.toHourlyEntities(locationId)
            val dailyEntities = weather.toDailyEntities(locationId)

            weatherDao.insertHourlyWeather(hourlyEntities)
            weatherDao.insertDailyWeather(dailyEntities)
        } catch (e: Exception) {
            // Log error but continue with other locations
            // In a production app, you might want to retry or notify the user
            Log.e("weather", "Unable to fetch weather for: " + location.name.toString())
        }
    }

    fun refreshWeatherData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val locations = locationDao.getAllLocation().first()
                refreshWeatherDataForAllLocations(locations)

                // Reload data from database with fresh weather
                loadSavedLocations()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to refresh weather data: ${e.message}"
                )
            }
        }
    }

    private suspend fun refreshWeatherDataForAllLocations(locations: List<LocationEntity>) {
        for (location in locations) {
            fetchAndSaveWeatherForLocation(location.id, location)
        }
    }

    fun toggleFavorite(locationId: Int) {
        // In a real app, this would update a favorite field in the location entity
        // For now this is just a placeholder as the database schema doesn't have a favorite field
        viewModelScope.launch {
            // This would be implemented with a dao method like:
            // locationDao.toggleFavorite(locationId)
            // Then we would reload the data
        }
    }

    fun addNewLocation(query: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                // Search for locations matching the query
                val searchResults = locationRepository.getLocation(query)

                if (searchResults.isNotEmpty()) {
                    // Take the first result and add it to database
                    val newLocation = searchResults.first()
                    val locationId = weatherDao.insertLocation(newLocation)

                    // Fetch and save weather for the new location
                    fetchAndSaveWeatherForLocation(locationId.toInt(), newLocation)

                    // Reload all locations
                    loadSavedLocations()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "No locations found for '$query'"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to add location: ${e.message}"
                )
            }
        }
    }

    fun deleteLocation(locationId: Int) {
        viewModelScope.launch {
            try {
                // This would delete the location (cascade delete will remove related weather)
                locationDao.deleteLocation(locationId)

                // For now, we'll just update the UI state to remove this location
                val updatedLocations = _uiState.value.locations.filter {
                    it.location.id != locationId
                }

                _uiState.value = _uiState.value.copy(
                    locations = updatedLocations
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to delete location: ${e.message}"
                )
            }
        }
    }
}