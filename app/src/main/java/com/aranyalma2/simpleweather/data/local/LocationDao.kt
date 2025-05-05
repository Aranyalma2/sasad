package com.aranyalma2.simpleweather.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Insert
    suspend fun insert(location: LocationEntity)

    @Query("SELECT * FROM location ORDER BY id DESC")
    fun getAllLocation(): Flow<List<LocationEntity>>

    @Query("DELETE FROM location")
    suspend fun deleteAll()
}