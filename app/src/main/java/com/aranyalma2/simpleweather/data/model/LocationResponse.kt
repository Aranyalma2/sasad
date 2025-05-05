package com.aranyalma2.simpleweather.data.model

data class LocationResponse (
    val result: List<LocationData>
)

data class LocationData (
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double
)