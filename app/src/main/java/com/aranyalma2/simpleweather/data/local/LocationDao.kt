package com.aranyalma2.simpleweather.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: LocationEntity): Long

    @Query("SELECT * FROM location")
    suspend fun getAllLocations(): List<LocationEntity>

    @Query("SELECT * FROM location WHERE name = :name AND country = :country AND latitude = :latitude AND longitude = :longitude LIMIT 1")
    suspend fun findByNameCountryLatLng(
        name: String,
        country: String,
        latitude: Double,
        longitude: Double
    ): LocationEntity?


    @Query("DELETE FROM location WHERE id = :locationId")
    suspend fun deleteLocation(locationId: Int)

    @Query("DELETE FROM location")
    suspend fun deleteAll()
}