package com.example.nasa_googlemaps_api_project.home.data.room

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image_of_day_table")
data class ImageOfDayEntities(
    @PrimaryKey val id: Int,
    val date: String,
    val media_type: String,
    val title: String,
    val bitmap: Bitmap
)