package com.example.nasa_googlemaps_api_project.home.network

import com.example.nasa_googlemaps_api_project.home.data.ImageOfDayModel
import retrofit2.http.GET
import retrofit2.http.Query

interface ImageOfDayApiInterface {

    /** Returns the Nasa Image of the Day in Kotlin format **/
    @GET("planetary/apod")
    suspend fun getImageOfDay(@Query(value = "date") date: String) : ImageOfDayModel
}