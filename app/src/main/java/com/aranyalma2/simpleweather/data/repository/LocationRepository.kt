package com.aranyalma2.simpleweather.data.repository

import android.util.Log
import com.aranyalma2.simpleweather.data.local.LocationEntity
import com.aranyalma2.simpleweather.data.mapper.toLocationEntities
import com.aranyalma2.simpleweather.data.remote.LocationApiService
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val locationApiService: LocationApiService
) {
    suspend fun getLocation(name: String): List<LocationEntity> {
        val response = locationApiService.getLocation(
            name = name,
            count = 10,
            language = "en",
            format = "json"
        )
        return response.toLocationEntities()
    }
}
