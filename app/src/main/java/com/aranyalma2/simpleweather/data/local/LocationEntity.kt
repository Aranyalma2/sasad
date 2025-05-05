package com.aranyalma2.simpleweather.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location")
data class LocationEntity (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val country: String,
    val name: String,
    val latitude: Double,
    val longitude: Double
)