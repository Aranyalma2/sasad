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

    @Query("DELETE FROM hourlyweather WHERE locationId = :locationId")
    suspend fun deleteHourlyWeatherForLocation(locationId: Long)

    @Query("DELETE FROM dailyweather WHERE locationId = :locationId")
    suspend fun deleteDailyWeatherForLocation(locationId: Long)

    @Transaction
    @Query("SELECT * FROM location WHERE id = :locationId")
    suspend fun getLocationWithWeather(locationId: Long): LocationWithWeather?
}