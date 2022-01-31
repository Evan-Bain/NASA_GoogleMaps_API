package com.example.nasa_googlemaps_api_project.satellite_images.data.room

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "satellite_image_table")
data class SatelliteImageEntities(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val title: String,
    val image: Bitmap
)