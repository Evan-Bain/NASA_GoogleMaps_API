package com.example.nasa_googlemaps_api_project.satellite_images.data.network

import com.example.nasa_googlemaps_api_project.satellite_images.data.EarthSatelliteModel
import retrofit2.http.GET
import retrofit2.http.Query

interface EarthSatelliteApiInterface {

    @GET("planetary/earth/assets?date=2014-02-01&dim=.15")
    suspend fun getSatelliteImage(
        @Query(value = "lat") lat: String,
        @Query(value = "lon") lng: String
    ): EarthSatelliteModel
}