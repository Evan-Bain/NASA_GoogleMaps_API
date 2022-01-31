package com.example.nasa_googlemaps_api_project.satellite_images.data

import android.graphics.Bitmap
import com.example.nasa_googlemaps_api_project.Globals.todayDate
import com.example.nasa_googlemaps_api_project.satellite_images.data.room.SatelliteImageEntities
import com.squareup.moshi.Json

data class EarthSatelliteModel(
    @field:Json(name = "date") val date: String,
    @field:Json(name = "url") val url: String
) {
    companion object {

        fun EarthSatelliteModel.map(
            title: String,
            bitmap: Bitmap,
            lat: String,
            lng: String
        ): SatelliteImageEntities {
            return SatelliteImageEntities(
                title,
                bitmap,
                lat,
                lng,
                this.date.substring(0..9),
                todayDate()
            )
        }
    }
}