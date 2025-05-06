package com.aranyalma2.simpleweather.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aranyalma2.simpleweather.data.local.LocationEntity
import com.aranyalma2.simpleweather.data.local.LocationWithWeather
import com.aranyalma2.simpleweather.data.local.WeatherDao
import com.aranyalma2.simpleweather.data.location.LocationProvider
import com.aranyalma2.simpleweather.data.remote.LocationApiService
import com.aranyalma2.simpleweather.data.remote.WeatherApiService
import com.aranyalma2.simpleweather.data.repository.LocationRepository
import com.aranyalma2.simpleweather.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
                // This is a placeholder - in a real app, you would fetch saved locations from Room
                // and then fetch their weather data
                val mockLocations = listOf(
                    LocationEntity(
                        id = 1,
                        country = "Hungary",
                        name = "Budapest",
                        latitude = 47.497913,
                        longitude = 19.040236
                    ),
                    LocationEntity(
                        id = 2,
                        country = "France",
                        name = "Paris",
                        latitude = 48.856613,
                        longitude = 2.352222
                    )
                )

                // Normally you would fetch real data and update the UI
                // For now, we'll just update the state with mock data
                _uiState.value = HomeUiState(
                    isLoading = false,
                    locations = mockLocations.map {
                        LocationWithWeather(
                            location = it,
                            hourly = emptyList(),
                            daily = emptyList()
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = HomeUiState(
                    isLoading = false,
                    error = "Failed to load locations: ${e.message}"
                )
            }
        }
    }

    fun refreshWeatherData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // In a real app, you would fetch fresh weather data for all locations
                // For now, we'll just simulate a delay and keep the same data
                kotlinx.coroutines.delay(1000)
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to refresh weather data: ${e.message}"
                )
            }
        }
    }

    fun toggleFavorite(locationId: Int) {
        // In a real app, this would update the database
        // For now, we'll just update the UI state
        val currentLocations = _uiState.value.locations.toMutableList()
        val index = currentLocations.indexOfFirst { it.location.id == locationId }
        if (index != -1) {
            // This is just a placeholder for demonstration
            // In a real app, you would update the favorite status in the database
        }
    }
}