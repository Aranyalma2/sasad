package com.aranyalma2.simpleweather.data.model

data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    val generationtime_ms: Double,
    val utc_offset_seconds: Int,
    val timezone: String,
    val timezone_abbreviation: String,
    val elevation: Double,
    val daily_units: DailyUnits,
    val daily: DailyData,
    val hourly_units: HourlyUnits,
    val hourly: HourlyData
)

data class HourlyUnits(
    val time: String,
    val temperature_2m: String,
    val wind_speed_10m: String,
    val wind_direction_10m: String,
    val relative_humidity_2m: String,
    val apparent_temperature: String,
    val precipitation: String,
    val precipitation_probability: String,
    val weather_code: String
)

data class HourlyData(
    val time: List<String>,
    val temperature_2m: List<Double>,
    val wind_speed_10m: List<Double>,
    val wind_direction_10m: List<Int>,
    val relative_humidity_2m: List<Int>,
    val apparent_temperature: List<Double>,
    val precipitation: List<Double>,
    val precipitation_probability: List<Int>,
    val weather_code: List<Int>
)

data class DailyUnits(
    val time: String,
    val temperature_2m_max: String,
    val temperature_2m_min: String,
    val wind_speed_10m_max: String,
    val wind_direction_10m_dominant: String,
    val precipitation_sum: String,
    val weather_code: String
)

data class DailyData(
    val time: List<String>,
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>,
    val wind_speed_10m_max: List<Double>,
    val wind_direction_10m_dominant: List<Int>,
    val precipitation_sum: List<Double>,
    val weather_code: List<Int>
)
