package com.example.nasa_googlemaps_api_project.satellite_images.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SatelliteImageDao {

    @Insert
    suspend fun insertEntity(entity: SatelliteImageEntities)

    @Query("SELECT * FROM satellite_image_table")
    suspend fun getEntities(): List<SatelliteImageEntities>
}