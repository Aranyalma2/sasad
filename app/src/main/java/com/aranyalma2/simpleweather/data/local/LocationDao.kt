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

    @Query("SELECT * FROM location ORDER BY id DESC")
    fun getAllLocation(): Flow<List<LocationEntity>>

    @Query("DELETE FROM location WHERE id = :locationId")
    suspend fun deleteLocation(locationId: Int)

    @Query("DELETE FROM location")
    suspend fun deleteAll()
}