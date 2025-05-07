package com.aranyalma2.simpleweather.domain.repository

import android.util.Log
import com.aranyalma2.simpleweather.data.local.LocationEntity
import com.aranyalma2.simpleweather.data.local.LocationWithWeather
import com.aranyalma2.simpleweather.data.local.WeatherDao
import com.aranyalma2.simpleweather.data.mapper.toDailyEntities
import com.aranyalma2.simpleweather.data.mapper.toHourlyEntities
import com.aranyalma2.simpleweather.data.model.CombinedWeather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherPersistenceRepository @Inject constructor(
    private val weatherDao: WeatherDao,
) {
    private var currentLocationWeather: LocationWithWeather? = null
    /**
     * Updates the weather data for a specific location by:
     * 1. Deleting existing hourly and daily data for that location
     * 2. Inserting the new weather data
     */
    suspend fun updateWeatherForLocation(locationId: Int, weather: CombinedWeather) = withContext(Dispatchers.IO) {
        // Convert weather data to entities
        val hourlyEntities = weather.toHourlyEntities(locationId)
        val dailyEntities = weather.toDailyEntities(locationId)

        // Delete existing data for this location
        weatherDao.deleteHourlyWeatherForLocation(locationId.toLong())
        weatherDao.deleteDailyWeatherForLocation(locationId.toLong())

        // Insert new data
        weatherDao.insertHourlyWeather(hourlyEntities)
        weatherDao.insertDailyWeather(dailyEntities)
    }

    /**
     * Gets a location with its associated weather data
     */
    suspend fun getLocationWithWeather(locationId: Int): LocationWithWeather? {
        return weatherDao.getLocationWithWeather(locationId.toLong())
    }

    suspend fun insertLocation(location: LocationEntity): Long {
        return weatherDao.insertLocation(location)
    }

    suspend fun deleteHourlyWeatherForLocation(locationId: Int) {
        weatherDao.deleteHourlyWeatherForLocation(locationId.toLong())
    }

    suspend fun deleteDailyWeatherForLocation(locationId: Int) {
        weatherDao.deleteDailyWeatherForLocation(locationId.toLong())
    }

    fun setCurrentWeather(weather: LocationWithWeather) {
        currentLocationWeather = weather
    }

    fun getCurrentWeather(): LocationWithWeather? {
        return currentLocationWeather
    }
}