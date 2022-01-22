package com.example.nasa_googlemaps_api_project.home.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface ImageOfDayDao {

    @Query("SELECT date FROM image_of_day_table WHERE date = :currentDate")
    suspend fun getCurrentDate(currentDate: String): String

    @Insert(onConflict = REPLACE)
    suspend fun insertEntity(entity: ImageOfDayEntities)

    @Query("SELECT * FROM image_of_day_table WHERE id = :integer")
    /** Returns cached image of day. Should only be called after getCurrentDate
        returns a success **/
    suspend fun getImageOfDay(integer: Int = 0): ImageOfDayEntities
}