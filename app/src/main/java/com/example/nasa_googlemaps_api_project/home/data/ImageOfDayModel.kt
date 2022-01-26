package com.example.nasa_googlemaps_api_project.home.data

import android.graphics.Bitmap
import com.example.nasa_googlemaps_api_project.home.HomeGlobals.todayDate
import com.example.nasa_googlemaps_api_project.home.data.room.ImageOfDayEntities
import com.squareup.moshi.Json

/** Model of the Nasa Api Image of the Day for the Json object**/
data class ImageOfDayModel(
    @field:Json(name = "date") val date: String,
    @field:Json(name="media_type") val media_type: String,
    @field:Json(name="title") val title: String,
    @field:Json(name="url") val url: String?,
) {
    companion object {
        fun ImageOfDayModel.map(bitmap: Bitmap): ImageOfDayEntities {
            return ImageOfDayEntities(
                0,
                todayDate(),
                this.media_type,
                this.title,
                bitmap
            )
        }
    }
}