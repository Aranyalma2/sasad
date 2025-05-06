package com.aranyalma2.simpleweather.data.mapper

import com.aranyalma2.simpleweather.data.local.*
import com.aranyalma2.simpleweather.data.model.WeatherResponse
import com.aranyalma2.simpleweather.data.model.CombinedWeather
import com.aranyalma2.simpleweather.data.model.DailyWeather
import com.aranyalma2.simpleweather.data.model.HourlyWeather

fun CombinedWeather.toHourlyEntities(locationId: Int): List<HourlyWeatherEntity> {
    return hourlyWeather.indices.map { i ->
        HourlyWeatherEntity(
            locationId = locationId,
            time = hourlyWeather[i].time,
            temperature =  hourlyWeather[i].temperature,
            windSpeed =  hourlyWeather[i].windSpeed,
            windDirection =  hourlyWeather[i].windDirection,
            humidity =  hourlyWeather[i].humidity,
            apparentTemperature =  hourlyWeather[i].apparentTemperature,
            precipitation =  hourlyWeather[i].precipitation,
            precipitationProbability =  hourlyWeather[i].precipitationProbability,
            weatherCode =  hourlyWeather[i].weatherCode
        )
    }
}

fun CombinedWeather.toDailyEntities(locationId: Int): List<DailyWeatherEntity> {
    return dailyWeather.indices.map {i ->
        DailyWeatherEntity(
            locationId = locationId,
            time = dailyWeather[i].time,
            temperatureMax = dailyWeather[i].temperatureMax,
            temperatureMin = dailyWeather[i].temperatureMin,
            windSpeed = dailyWeather[i].windSpeed,
            windDirection = dailyWeather[i].windDirection,
            precipitation = dailyWeather[i].precipitation,
            weatherCode = dailyWeather[i].weatherCode
        )
    }
}

fun WeatherResponse.toHourly(): List<HourlyWeather> {
    return hourly.time.indices.map { i ->
        HourlyWeather(
            time = hourly.time[i],
            temperature = hourly.temperature_2m[i],
            windSpeed = hourly.wind_speed_10m[i],
            windDirection = hourly.wind_direction_10m[i],
            humidity = hourly.relative_humidity_2m[i],
            apparentTemperature = hourly.apparent_temperature[i],
            precipitation = hourly.precipitation[i],
            precipitationProbability = hourly.precipitation_probability[i],
            weatherCode = hourly.weather_code[i]
        )
    }
}

fun WeatherResponse.toDaily(): List<DailyWeather> {
    return daily.time.indices.map { i ->
        DailyWeather(
            time = daily.time[i],
            temperatureMax = daily.temperature_2m_max[i],
            temperatureMin = daily.temperature_2m_min[i],
            windSpeed = daily.wind_speed_10m_max[i],
            windDirection = daily.wind_direction_10m_dominant[i],
            precipitation = daily.precipitation_sum[i],
            weatherCode = daily.weather_code[i]
        )
    }
}