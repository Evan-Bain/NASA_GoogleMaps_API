package com.example.nasa_googlemaps_api_project.satellite_images.data

import android.graphics.Bitmap
import com.example.nasa_googlemaps_api_project.satellite_images.data.EarthSatelliteModel.Companion.map
import com.example.nasa_googlemaps_api_project.satellite_images.data.room.SatelliteImageDao
import com.example.nasa_googlemaps_api_project.satellite_images.data.room.SatelliteImageEntities
import com.example.nasa_googlemaps_api_project.satellite_images.network.EarthSatelliteApiInterface
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class EarthSatelliteRepository(
    private val api: EarthSatelliteApiInterface,
    private val database: SatelliteImageDao,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    suspend fun getSatelliteImage(
        lat: String, lng: String, date: String): Result<EarthSatelliteModel> {
        return withContext(defaultDispatcher) {
            val model = async {
                try{
                    Result.success(api.getSatelliteImage(lat, lng, date))
                } catch(e: Exception) {
                    Result.failure(e)
                }
            }
            model.await()
        }
    }

    suspend fun saveSatelliteImage(
        model: EarthSatelliteModel,
        title: String,
        bitmap: Bitmap,
        lat: String,
        lng: String
    ) = withContext(defaultDispatcher) {
        database.insertEntity(map(title, bitmap, lat, lng))
    }

    suspend fun getSatelliteImages(): List<SatelliteImageEntities> {
        return database.getEntities()
    }
}