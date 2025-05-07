package com.aranyalma2.simpleweather.ui.forecast

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aranyalma2.simpleweather.data.local.LocationWithWeather
import com.aranyalma2.simpleweather.data.local.WeatherDao
import com.aranyalma2.simpleweather.data.mapper.toDailyEntities
import com.aranyalma2.simpleweather.data.mapper.toHourlyEntities
import com.aranyalma2.simpleweather.data.model.CombinedWeather
import com.aranyalma2.simpleweather.data.repository.WeatherRepository
import com.aranyalma2.simpleweather.data.model.DailyWeather
import com.aranyalma2.simpleweather.data.model.HourlyWeather
import com.aranyalma2.simpleweather.domain.repository.WeatherPersistenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ForecastUiState(
    val isLoading: Boolean = false,
    val locationName: String = "",
    val country: String = "",
    val hourlyWeather: List<HourlyWeather> = emptyList(),
    val dailyWeather: List<DailyWeather> = emptyList(),
    val currentWeather: HourlyWeather? = null,
    val error: String? = null
)

@HiltViewModel
class ForecastViewModel @Inject constructor(
    private val weatherDao: WeatherPersistenceRepository,
    private val weatherRepository: WeatherRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForecastUiState(isLoading = true))
    val uiState: StateFlow<ForecastUiState> = _uiState.asStateFlow()

    init {
        savedStateHandle.get<Int>("locationId")?.let { locationId ->
            loadWeatherForLocation(locationId)
        } ?: run {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = "Location ID not provided"
            )
        }
    }

    private fun loadWeatherForLocation(locationId: Int) {
        viewModelScope.launch {
            try {
                val locationWithWeather: LocationWithWeather? = if (locationId == -1) {
                    weatherDao.getCurrentWeather()
                } else {
                    weatherDao.getLocationWithWeather(locationId)
                }

                if (locationWithWeather != null) {
                    processLocationData(locationWithWeather)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Location not found"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load weather data: ${e.message}"
                )
            }
        }
    }

    private fun processLocationData(locationWithWeather: LocationWithWeather) {
        val location = locationWithWeather.location

        // Convert database entities to domain models
        val hourlyWeather = locationWithWeather.hourly.map { hourly ->
            HourlyWeather(
                time = hourly.time,
                temperature = hourly.temperature,
                apparentTemperature = hourly.apparentTemperature,
                humidity = hourly.humidity,
                precipitation = hourly.precipitation,
                precipitationProbability = hourly.precipitationProbability,
                windSpeed = hourly.windSpeed,
                windDirection = hourly.windDirection,
                weatherCode = hourly.weatherCode
            )
        }

        val dailyWeather = locationWithWeather.daily.map { daily ->
            DailyWeather(
                time = daily.time,
                temperatureMax = daily.temperatureMax,
                temperatureMin = daily.temperatureMin,
                windSpeed = daily.windSpeed,
                windDirection = daily.windDirection,
                precipitation = daily.precipitation,
                weatherCode = daily.weatherCode
            )
        }

        // Update the UI state with the loaded data
        _uiState.value = ForecastUiState(
            isLoading = false,
            locationName = location.name,
            country = location.country,
            hourlyWeather = hourlyWeather,
            dailyWeather = dailyWeather,
            currentWeather = hourlyWeather.firstOrNull()
        )

        Log.d("f", _uiState.value.dailyWeather.size.toString())
    }

    fun refreshWeatherData(locationId: Int) {
        val currentState = _uiState.value
        if (currentState.isLoading) return

        _uiState.value = currentState.copy(isLoading = true)

        viewModelScope.launch {
            try {
                // Get current location latitude and longitude from the UI state
                val locationWithWeather: LocationWithWeather? = if (locationId == -1) {
                    weatherDao.getCurrentWeather()
                } else {
                    weatherDao.getLocationWithWeather(locationId)
                }

                if (locationWithWeather != null) {
                    val location = locationWithWeather.location

                    // Fetch fresh weather data from API
                    val freshWeatherData = weatherRepository.getWeather(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )

                    // Update local database with new weather data
                    if (locationId == -1) {
                        val hourlyEntities = freshWeatherData.toHourlyEntities(locationId)
                        val dailyEntities = freshWeatherData.toDailyEntities(locationId)
                        val tempEntry = LocationWithWeather(
                            location = location,
                            hourly = hourlyEntities,
                            daily = dailyEntities
                        )
                        weatherDao.setCurrentWeather(tempEntry)
                    } else {
                        weatherDao.updateWeatherForLocation(locationId, freshWeatherData)
                    }

                    // Reload data from database to get the updated weather
                    loadWeatherForLocation(location.id)
                } else {
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        error = "Location not found"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isLoading = false,
                    error = "Failed to refresh weather data: ${e.message}"
                )
            }
        }
    }
}