package com.example.nasa_googlemaps_api_project.satellite_images.data.room

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "satellite_image_table")
data class SatelliteImageEntities(
    val title: String,
    val image: Bitmap,
    val latitude: String,
    val longitude: String,
    val dateTaken: String,
    val dateAdded: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)

/** sorts SatelliteImage Database by date the image was taken **/
fun List<SatelliteImageEntities>.sortByDate(): List<SatelliteImageEntities> {
    val newList = mutableListOf<SatelliteImageEntities>()
    val dateList = mutableListOf<Date>()
    val format = SimpleDateFormat("yyyy-MM-DD", Locale.getDefault())

    for(i in this) {
        val date = format.parse(i.dateTaken)
        date?.let {
            dateList.add(it)
        }
    }
    dateList.sort()
    for(i in dateList) {
        for(j in this) {
            val dateEntity = format.parse(j.dateTaken)
            if(i == dateEntity) {
                newList.add(j)
            }
        }
    }

    return newList
}