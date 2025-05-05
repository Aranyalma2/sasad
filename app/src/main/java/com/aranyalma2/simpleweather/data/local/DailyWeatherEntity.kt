package com.aranyalma2.simpleweather.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "dailyweather",
    foreignKeys = [ForeignKey(
        entity = LocationEntity::class,
        parentColumns = ["id"],
        childColumns = ["locationId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class DailyWeatherEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val locationId: Int,
    val time: String,
    val temperature: Double,
    val windSpeed: Double,
    val windDirection: Int,
    val precipitation: Double,
    val weatherCode: Int
)