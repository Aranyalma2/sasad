package com.aranyalma2.simpleweather.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        LocationEntity::class,
        DailyWeatherEntity::class,
        HourlyWeatherEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun locationDao(): LocationDao
    abstract fun weatherDao(): WeatherDao
}
