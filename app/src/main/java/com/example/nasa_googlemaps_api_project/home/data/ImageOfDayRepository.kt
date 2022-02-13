package com.example.nasa_googlemaps_api_project.home.data

import android.graphics.drawable.Drawable
import android.util.Log
import com.example.nasa_googlemaps_api_project.Globals.randomDate
import com.example.nasa_googlemaps_api_project.Globals.todayDate
import com.example.nasa_googlemaps_api_project.home.data.ImageOfDayModel.Companion.map
import com.example.nasa_googlemaps_api_project.home.data.room.ImageOfDayDao
import com.example.nasa_googlemaps_api_project.home.data.room.ImageOfDayEntities
import com.example.nasa_googlemaps_api_project.home.network.ImageOfDayApiInterface
import kotlinx.coroutines.*
import android.graphics.drawable.BitmapDrawable


class ImageOfDayRepository(
    private val api: ImageOfDayApiInterface,
    private val database: ImageOfDayDao,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {

    //All functions are on the default dispatcher

    /** Returns the image of the day from api with Success or Failure result **/
    suspend fun imageOfTheDayModel(): Result<ImageOfDayModel> {
        return try {
            withContext(defaultDispatcher) {
                val currentDateData = async {
                    api.getImageOfDay(todayDate())
                }

                if (currentDateData.await().media_type == "image") {
                    return@withContext Result.success(currentDateData.await())
                } else {
                    var randomDateData = async{
                        api.getImageOfDay(randomDate())
                    }
                    while (randomDateData.await().media_type != "image") {
                        randomDateData = async{
                            api.getImageOfDay(randomDate())
                        }
                    }
                    return@withContext Result.success(randomDateData.await())
                }
            }

        } catch (e: Exception) {
            Log.e("ImageOfDayRepository", "imageOfTheDayModel: $e")
            Result.failure(e)
        }
    }

    /** Returns the image of the day from database with Success or Failure result **/
    suspend fun imageOfTheDayEntity(): Result<ImageOfDayEntities> {
        return try {
            withContext(defaultDispatcher) {
                val entity = async {
                    database.getImageOfDay()
                }
                Result.success(entity.await())
            }
        } catch(e: Exception) {
            Log.e("ImageOfDayRepository", "imageOfTheDayEntity: $e")
            Result.failure(e)
        }
    }

    /** Inserts the data from api into Room database**/
    suspend fun cacheData(
        model: ImageOfDayModel,
        imageOfDay: Drawable) = withContext(defaultDispatcher) {
        database.insertEntity(model.map(
            (imageOfDay as BitmapDrawable).bitmap))
    }

    /** Returns true or false depending on if Room has an instance of
     *  today's date **/
    suspend fun isDataCached(): Result<Boolean> = withContext(defaultDispatcher) {
        val data = database.getImageOfDay()
        return@withContext try{
            Result.success(data.date == todayDate())
        } catch(e: Exception) {
            Log.e("ImageOfDayRepository", "isDataCached: $e")
            Result.failure(e)
        }
    }
}