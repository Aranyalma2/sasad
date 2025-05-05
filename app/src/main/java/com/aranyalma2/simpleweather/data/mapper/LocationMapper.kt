package com.aranyalma2.simpleweather.data.mapper

import com.aranyalma2.simpleweather.data.local.*
import com.aranyalma2.simpleweather.data.model.LocationResponse

fun LocationResponse.toLocationEntities(): List<LocationEntity> {
    return result.indices.map { i ->
        LocationEntity(
            country = result[i].country,
            name = result[i].name,
            latitude = result[i].latitude,
            longitude = result[i].longitude
        )
    }
}