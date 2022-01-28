package com.example.nasa_googlemaps_api_project.satellite_images.data.network

import com.example.nasa_googlemaps_api_project.satellite_images.data.EarthSatelliteModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class EarthSatelliteRepository(
    private val api: EarthSatelliteApiInterface,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    suspend fun getSatelliteImage(lat: String, lng: String): EarthSatelliteModel {
        return withContext(defaultDispatcher) {
            val model = async { api.getSatelliteImage(lat, lng) }
            model.await()
        }
    }
}