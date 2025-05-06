package com.aranyalma2.simpleweather.data.mapper

import com.aranyalma2.simpleweather.data.local.*
import com.aranyalma2.simpleweather.data.model.LocationResponse

fun LocationResponse.toLocationEntities(): List<LocationEntity> {
    return results.indices.map { i ->
        LocationEntity(
            country = results[i].country,
            name = results[i].name,
            latitude = results[i].latitude,
            longitude = results[i].longitude
        )
    }
}