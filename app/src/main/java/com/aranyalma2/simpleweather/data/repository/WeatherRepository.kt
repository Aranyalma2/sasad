package com.aranyalma2.simpleweather.data.repository

import android.util.Log
import com.aranyalma2.simpleweather.data.mapper.toDaily
import com.aranyalma2.simpleweather.data.mapper.toHourly
import com.aranyalma2.simpleweather.data.remote.WeatherApiService
import com.aranyalma2.simpleweather.data.model.CombinedWeather
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherApiService: WeatherApiService,
) {

    suspend fun getWeather(latitude: Double, longitude: Double): CombinedWeather {
        val response = weatherApiService.getWeather(
            latitude = latitude,
            longitude = longitude,
            daily = "weather_code,temperature_2m_max,temperature_2m_min,precipitation_sum,wind_speed_10m_max,wind_direction_10m_dominant",
            hourly = "temperature_2m,wind_speed_10m,wind_direction_10m,relative_humidity_2m,apparent_temperature,precipitation,precipitation_probability,weather_code",
            timezone = "auto",
            forecastDays = 1
        )

        Log.d("Weather-response", response.toString())

        return CombinedWeather(response.toDaily(), response.toHourly())
    }
}
