package com.example.nasa_googlemaps_api_project.satellite_images.data

import com.squareup.moshi.Json

data class EarthSatelliteModel(
    @field:Json(name = "date") val date: String,
    @field:Json(name = "url") val url: String
)