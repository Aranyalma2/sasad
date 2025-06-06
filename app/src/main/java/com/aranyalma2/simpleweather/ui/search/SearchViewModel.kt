package com.aranyalma2.simpleweather.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aranyalma2.simpleweather.data.local.LocationDao
import com.aranyalma2.simpleweather.data.local.LocationEntity
import com.aranyalma2.simpleweather.domain.repository.WeatherPersistenceRepository
import com.aranyalma2.simpleweather.data.repository.LocationRepository
import com.aranyalma2.simpleweather.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log

data class LocationSearchItem(
    val id: Int = 0,
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val isFavorite: Boolean = false
)

data class SearchUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val locations: List<LocationSearchItem> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val weatherRepository: WeatherRepository,
    private val locationDao: LocationDao,
    private val weatherDao: WeatherPersistenceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }

        // If the query is empty, clear results
        if (query.isEmpty()) {
            _uiState.update { it.copy(locations = emptyList()) }
        }
    }

    fun searchLocations() {
        val query = uiState.value.searchQuery
        if (query.isBlank()) return

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                val locationEntities = locationRepository.getLocation(query)

                // Get all saved locations to determine favorites
                val savedLocations = locationDao.getAllLocations()

                val locationItems = locationEntities.map { entity ->
                    val isFavorite = savedLocations.any {
                        it.name == entity.name &&
                                it.country == entity.country &&
                                it.latitude == entity.latitude &&
                                it.longitude == entity.longitude
                    }

                    LocationSearchItem(
                        id = entity.id,
                        name = entity.name,
                        country = entity.country,
                        latitude = entity.latitude,
                        longitude = entity.longitude,
                        isFavorite = isFavorite
                    )
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        locations = locationItems
                    )
                }
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error searching for locations: ${e.localizedMessage}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Unable to load locations. Check internet connection..."
                    )
                }
            }
        }
    }

    fun toggleFavorite(location: LocationSearchItem) {
        val updatedLocations = _uiState.value.locations.map {
            if (it.name == location.name && it.country == location.country) {
                it.copy(isFavorite = !it.isFavorite)
            } else {
                it
            }
        }

        //Add or Remove location
        if (location.isFavorite != true) addLocation(location) else removeLocation(location)

        _uiState.update { it.copy(locations = updatedLocations) }
    }

    fun addLocation(location: LocationSearchItem) {
        viewModelScope.launch {
            try {
                // Convert to entity
                val locationEntity = LocationEntity(
                    name = location.name,
                    country = location.country,
                    latitude = location.latitude,
                    longitude = location.longitude
                )

                // Insert location and get the ID
                val locationId = weatherDao.insertLocation(locationEntity).toInt()

                // Fetch weather data for this location
                val weatherData = weatherRepository.getWeather(
                    latitude = location.latitude,
                    longitude = location.longitude
                )

                weatherDao.updateWeatherForLocation(locationId, weatherData)

            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error adding location: ${e.localizedMessage}")
            }
        }
    }

    fun removeLocation(location: LocationSearchItem) {
        viewModelScope.launch {
            try {
                val target = locationDao.findByNameCountryLatLng(
                    location.name,
                    location.country,
                    location.latitude,
                    location.longitude
                )

                target?.let { entity ->
                    weatherDao.deleteHourlyWeatherForLocation(entity.id)
                    weatherDao.deleteDailyWeatherForLocation(entity.id)
                    locationDao.deleteLocation(entity.id)
                }
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Error removing location: ${e.localizedMessage}")
            }
        }
    }



}