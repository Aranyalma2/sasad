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
import com.aranyalma2.simpleweather.data.remote.LocationApiService
import com.aranyalma2.simpleweather.data.remote.WeatherApiService
import com.aranyalma2.simpleweather.data.repository.LocationRepository
import com.aranyalma2.simpleweather.data.repository.WeatherRepository
import com.aranyalma2.simpleweather.domain.repository.WeatherPersistenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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

    // For one-time UI events like snackbar messages
    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage

    private val weatherRepository = WeatherRepository(weatherApi)
    private val locationRepository = LocationRepository(locationApi)

    companion object {
        const val CURRENT_LOCATION_ID = -1
    }

    init {
        loadSavedLocations()
    }

    private fun loadSavedLocations() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                val locations = locationDao.getAllLocations()

                if (locations.isEmpty() && !_uiState.value.hasCurrentLocation) {
                    _uiState.value = HomeUiState(isLoading = false)
                    return@launch
                }

                val locationsWithWeather = mutableListOf<LocationWithWeather>()

                // Retain current location at top
                if (_uiState.value.hasCurrentLocation && _uiState.value.locations.isNotEmpty()) {
                    _uiState.value.locations.find { it.location.id == CURRENT_LOCATION_ID }?.let {
                        locationsWithWeather.add(it)
                    }
                }

                for (location in locations) {
                    weatherDao.getLocationWithWeather(location.id)?.let {
                        locationsWithWeather.add(it)
                    }
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    locations = locationsWithWeather,
                    hasCurrentLocation = _uiState.value.hasCurrentLocation
                )

                refreshWeatherDataForAllLocations(locations)

            } catch (e: Exception) {
                Log.e("HomeViewModel", "Failed to load locations: ${e.message}")
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
                    name = "Local",
                    latitude = location.latitude,
                    longitude = location.longitude
                )

                val weather = weatherRepository.getWeather(location.latitude, location.longitude)

                val hourlyEntities = weather.toHourlyEntities(CURRENT_LOCATION_ID)
                val dailyEntities = weather.toDailyEntities(CURRENT_LOCATION_ID)

                val currentLocationWithWeather = LocationWithWeather(
                    location = currentLocationEntity,
                    hourly = hourlyEntities,
                    daily = dailyEntities
                )

                weatherDao.setCurrentWeather(currentLocationWithWeather)

                Log.d("curr", currentLocationWithWeather.toString())

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
        val weather = weatherRepository.getWeather(location.latitude, location.longitude)
        weatherDao.updateWeatherForLocation(locationId, weather)
    }

    fun refreshWeatherData(download: Boolean = true) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                if (locationProvider.hasLocationPermission()) {
                    fetchCurrentLocation()
                }

                if (download) {
                    val locations = locationDao.getAllLocations()
                    try {
                        refreshWeatherDataForAllLocations(locations)
                    } catch (e: Exception) {
                        Log.e("HomeViewModel", "Network error: ${e.message}")
                        _snackbarMessage.emit("Network error. Showing saved data only.")
                    }
                }

                loadSavedLocations()

            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error refreshing data: ${e.message}")
                _snackbarMessage.emit("Failed to refresh weather data.")
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    private suspend fun refreshWeatherDataForAllLocations(locations: List<LocationEntity>) {
        for (location in locations) {
            fetchAndSaveWeatherForLocation(location.id, location)
        }
    }

    fun toggleFavorite(locationId: Int) {
        if (locationId == CURRENT_LOCATION_ID) return
        deleteLocation(locationId)
        refreshWeatherData(false)
    }

    fun deleteLocation(locationId: Int) {
        viewModelScope.launch {
            try {
                if (locationId == CURRENT_LOCATION_ID) {
                    _uiState.value = _uiState.value.copy(
                        locations = _uiState.value.locations.filter {
                            it.location.id != CURRENT_LOCATION_ID
                        },
                        hasCurrentLocation = false
                    )
                    return@launch
                }

                locationDao.deleteLocation(locationId)
                _uiState.value = _uiState.value.copy(
                    locations = _uiState.value.locations.filter {
                        it.location.id != locationId
                    }
                )
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Failed to delete location: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    error = "Failed to delete location: ${e.message}"
                )
            }
        }
    }
}
