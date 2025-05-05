package com.aranyalma2.simpleweather.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class LocationWithWeather(
    @Embedded val location: LocationEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "locationId"
    )
    val hourly: List<HourlyWeatherEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "locationId"
    )
    val daily: List<DailyWeatherEntity>
)