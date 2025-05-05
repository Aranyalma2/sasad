package com.aranyalma2.simpleweather.domain.usecase

import com.aranyalma2.simpleweather.domain.repository.WeatherRepository

class GetHourlyWeatherUseCase(
    private val repository: WeatherRepository
)