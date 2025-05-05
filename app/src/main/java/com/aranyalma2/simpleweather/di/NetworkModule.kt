package com.aranyalma2.simpleweather.di

import com.aranyalma2.simpleweather.data.remote.LocationApiService
import com.aranyalma2.simpleweather.data.remote.WeatherApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val WEATHER_BASE_URL = "https://api.open-meteo.com/"
    private const val LOCATION_BASE_URL = "https://geocoding-api.open-meteo.com/"

    @Provides
    @Singleton
    fun provideWeatherApi(): WeatherApiService {
        return Retrofit.Builder()
            .baseUrl(WEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideLocationApi(): LocationApiService {
        return Retrofit.Builder()
            .baseUrl(LOCATION_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LocationApiService::class.java)
    }
}
