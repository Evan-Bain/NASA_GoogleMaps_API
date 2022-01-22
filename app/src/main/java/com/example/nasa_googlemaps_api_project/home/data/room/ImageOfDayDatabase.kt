package com.example.nasa_googlemaps_api_project.home.data.room

import android.content.Context
import androidx.room.*
import com.example.nasa_googlemaps_api_project.Converters

@Database(entities = [ImageOfDayEntities::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ImageOfDayDatabase : RoomDatabase() {

    abstract fun imageOfDayDao(): ImageOfDayDao

    //creating database
    companion object {
        @Volatile
        private var INSTANCE: ImageOfDayDatabase? = null

        /** creates and returns Image of Day database using singleton to keep
            only one instance of the database open at a time **/
        fun getDatabase(context: Context): ImageOfDayDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ImageOfDayDatabase::class.java,
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