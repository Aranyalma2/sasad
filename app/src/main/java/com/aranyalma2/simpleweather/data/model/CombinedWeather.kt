package com.aranyalma2.simpleweather.data.model

data class DailyWeather (
    val time: String,
    val temperature: Double,
    val windSpeed: Double,
    val windDirection: Int,
    val precipitation: Double,
    val weatherCode: Int
)

data class HourlyWeather (
    val time: String,
    val temperature: Double,
    val windSpeed: Double,
    val windDirection: Int,
    val humidity: Int,
    val apparentTemperature: Double,
    val precipitation: Double,
    val precipitationProbability: Int,
    val weatherCode: Int
)

data class CombinedWeather (
    val dailyWeather : List<DailyWeather>,
    val hourlyWeather : List<HourlyWeather>
)