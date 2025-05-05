package com.aranyalma2.simpleweather.data.remote

import com.aranyalma2.simpleweather.data.model.LocationResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface LocationApiService {
    @GET("v1/search")
    suspend fun getLocation(
        @Query("name") name: String,
        @Query("count") count: Int,
        @Query("language") language: String,
        @Query("format") format: String,
    ): LocationResponse
}