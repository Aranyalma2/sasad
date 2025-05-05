package com.aranyalma2.simpleweather.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "hourlyweather",
    foreignKeys = [ForeignKey(
        entity = LocationEntity::class,
        parentColumns = ["id"],
        childColumns = ["locationId"],
        onDelete = ForeignKey.CASCADE,
    )]
)
data class HourlyWeatherEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val locationId: Int,
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
