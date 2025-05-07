package com.aranyalma2.simpleweather.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aranyalma2.simpleweather.data.local.LocationDao
import com.aranyalma2.simpleweather.data.local.LocationEntity
import com.aranyalma2.simpleweather.data.local.LocationWithWeather
import com.aranyalma2.simpleweather.data.location.LocationProvider
import com.aranyalma2.simpleweather.data.mapper.toDailyEntities
import com.aranyalma2.simpleweather.data.mapper.toHourlyEntities
import com.aranyalma2.simpleweather.data.model.CombinedWeather
import com.aranyalma2.simpleweather.data.model.LocationData
import com.aranyalma2.simpleweather.data.model.LocationResponse
import com.aranyalma2.simpleweather.data.remote.LocationApiService
import com.aranyalma2.simpleweather.data.remote.WeatherApiService
import com.aranyalma2.simpleweather.data.repository.LocationRepository
import com.aranyalma2.simpleweather.data.repository.WeatherRepository
import com.aranyalma2.simpleweather.domain.repository.WeatherPersistenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val locations: List<LocationWithWeather> = emptyList(),
    val hasCurrentLocation: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherApi: WeatherApiService,
    private val locationApi: LocationApiService,
    private val weatherDao: WeatherPersistenceRepository,
    private val locationDao: LocationDao,
    private val locationProvider: LocationProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val weatherRepository = WeatherRepository(weatherApi)
    private val locationRepository = LocationRepository(locationApi)

    companion object {
        // Using a negative ID to ensure it doesn't conflict with database IDs
        const val CURRENT_LOCATION_ID = -1
    }

    init {
        loadSavedLocations()
    }

    private fun loadSavedLocations() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                // Try to get current location first
                if (locationProvider.hasLocationPermission()) {
                    fetchCurrentLocation()
                }
                else {
                    Log.w("Location", "No permission granted")
                }

                // Load saved locations from Room database
                val locations = locationDao.getAllLocation().first()

                // If no locations are saved, we could add default ones for first-time users
                if (locations.isEmpty() && !_uiState.value.hasCurrentLocation) {
                    _uiState.value = HomeUiState(
                        isLoading = false
                    )
                    return@launch
                }

                // Fetch weather data for all saved locations
                val locationsWithWeather = mutableListOf<LocationWithWeather>()

                // Add any existing current location to the top of the list
                if (_uiState.value.hasCurrentLocation && _uiState.value.locations.isNotEmpty()) {
                    val currentLocationWeather = _uiState.value.locations.find {
                        it.location.id == CURRENT_LOCATION_ID
                    }
                    currentLocationWeather?.let {
                        locationsWithWeather.add(it)
                    }
                }

                // Add saved locations
                for (location in locations) {
                    val locationWithWeather = weatherDao.getLocationWithWeather(location.id.toLong())
                    locationWithWeather?.let {
                        locationsWithWeather.add(it)
                    }
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    locations = locationsWithWeather,
                    hasCurrentLocation = _uiState.value.hasCurrentLocation
                )

                // Refresh weather data in background for all locations
                refreshWeatherDataForAllLocations(locations)

            } catch (e: Exception) {
                _uiState.value = HomeUiState(
                    isLoading = false,
                    error = "Failed to load locations: ${e.message}",
                    hasCurrentLocation = _uiState.value.hasCurrentLocation,
                    locations = _uiState.value.locations
                )
            }
        }
    }

    private suspend fun fetchCurrentLocation() {
        try {
            val location = locationProvider.getCurrentLocation()
            if (location != null) {
                val currentLocationEntity = LocationEntity(
                    id = CURRENT_LOCATION_ID,
                    country = String.format(Locale.US, "%.2f %.2f", location.latitude, location.longitude),
                    name = "Current",
                    latitude = location.latitude,
                    longitude = location.longitude
                )

                // Get weather for current location
                val weather = weatherRepository.getWeather(location.latitude, location.longitude)

                // Convert to entities
                val hourlyEntities = weather.toHourlyEntities(CURRENT_LOCATION_ID)
                val dailyEntities = weather.toDailyEntities(CURRENT_LOCATION_ID)

                // Create LocationWithWeather for current location
                val currentLocationWithWeather = LocationWithWeather(
                    location = currentLocationEntity,
                    hourly = hourlyEntities,
                    daily = dailyEntities
                )

                weatherDao.setCurrentWeather(currentLocationWithWeather)

                // Update state with current location
                _uiState.value = _uiState.value.copy(
                    hasCurrentLocation = true,
                    locations = listOf(currentLocationWithWeather) + _uiState.value.locations.filter {
                        it.location.id != CURRENT_LOCATION_ID
                    }
                )
            }
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Failed to fetch current location: ${e.message}")
        }
    }

    private suspend fun fetchAndSaveWeatherForLocation(locationId: Int, location: LocationEntity) {
        try {
            val weather = weatherRepository.getWeather(location.latitude, location.longitude)
            weatherDao.updateWeatherForLocation(locationId,
                CombinedWeather(weather.dailyWeather, weather.hourlyWeather))
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Unable to fetch weather for: ${location.name}", e)
        }
    }

    fun refreshWeatherData(download: Boolean = true) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // Try to update current location if we have permission
                if (locationProvider.hasLocationPermission()) {
                    fetchCurrentLocation()
                }

                if (download) {
                    val locations = locationDao.getAllLocation().first()
                    refreshWeatherDataForAllLocations(locations)
                }

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
        // Don't delete current location, it's not a saved location
        if (locationId == CURRENT_LOCATION_ID) return

        deleteLocation(locationId)
        refreshWeatherData(false)
    }

    fun deleteLocation(locationId: Int) {
        viewModelScope.launch {
            try {
                // Current location is not in the database, just remove from state
                if (locationId == CURRENT_LOCATION_ID) {
                    val updatedLocations = _uiState.value.locations.filter {
                        it.location.id != CURRENT_LOCATION_ID
                    }
                    _uiState.value = _uiState.value.copy(
                        locations = updatedLocations,
                        hasCurrentLocation = false
                    )
                    return@launch
                }

                // Delete from database for saved locations
                locationDao.deleteLocation(locationId)

                // Update the UI state to remove this location
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