package com.example.nasa_googlemaps_api_project.satellite_images.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.nasa_googlemaps_api_project.Converters
import com.example.nasa_googlemaps_api_project.home.data.room.ImageOfDayDao
import com.example.nasa_googlemaps_api_project.home.data.room.ImageOfDayEntities

@Database(entities = [SatelliteImageEntities::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class SatelliteImageDatabase : RoomDatabase() {

    abstract fun satelliteImageDao(): SatelliteImageDao

    //creating database
    companion object {
        @Volatile
        private var INSTANCE: SatelliteImageDatabase? = null

        /** creates and returns Satellite Image database using singleton to keep
        only one instance of the database open at a time **/
        fun getDatabase(context: Context): SatelliteImageDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SatelliteImageDatabase::class.java,
                    "image_of_day_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}