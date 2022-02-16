package com.example.nasa_googlemaps_api_project.satellite_images.data.room

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.graphics.get
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nasa_googlemaps_api_project.R
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
    val calendar = Calendar.getInstance()

    for(i in this) {
        val date = format.parse(i.dateTaken)
        date?.let {
            dateList.add(it)
        }
    }
    dateList.sort()
    for((position, date) in dateList.withIndex()) {
        //get value of the date of the current entity
        calendar.time = date
        val currentYear = calendar.get(Calendar.YEAR)

        //get value of the date of the previous entity
        if(position > 0) {
            calendar.time = dateList[position-1]
        }
        val previousYear = calendar.get(Calendar.YEAR)

        //check which date from database comes next in order
        for(j in this) {
            //convert the entity's date string to a date Object
            val dateEntity = format.parse(j.dateTaken)
            if(date == dateEntity) {
                if(currentYear != previousYear || position == 0) {

                        //add blank entity with specific numerical value as title
                        //to identify as a header for recyclerView
                    newList.add(
                        SatelliteImageEntities(
                        "031583",
                        this[0].image,
                        "",
                        "",
                        j.dateTaken,
                        "",
                    ))
                }

                //add entity in database that matches the next
                //date in order
                newList.add(j)
            }
        }
    }

    return newList
}