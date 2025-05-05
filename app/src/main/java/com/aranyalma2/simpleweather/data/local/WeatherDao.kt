package com.aranyalma2.simpleweather.data.local

import androidx.room.*

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHourlyWeather(hourly: List<HourlyWeatherEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyWeather(daily: List<DailyWeatherEntity>)

    @Transaction
    @Query("SELECT * FROM location WHERE id = :locationId")
    suspend fun getLocationWithWeather(locationId: Long): LocationWithWeather?
}
