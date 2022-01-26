package com.example.nasa_googlemaps_api_project.home

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.lifecycle.*
import com.example.nasa_googlemaps_api_project.home.data.ImageOfDayModel
import com.example.nasa_googlemaps_api_project.home.data.ImageOfDayRepository
import com.example.nasa_googlemaps_api_project.home.data.room.ImageOfDayEntities
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: ImageOfDayRepository
) : ViewModel() {

    private var imageOfDayData: ImageOfDayModel? = null

    private var imageOfDayCache: ImageOfDayEntities? = null

    private val _retrievalCompleted = MutableLiveData<Boolean>()
    val retrievalCompleted: LiveData<Boolean>
        get() = _retrievalCompleted

    val imageOfDayTitle : LiveData<String> = Transformations.map(retrievalCompleted) {
        imageOfDayData?.title ?: imageOfDayCache?.title
    }
    val imageOfDayUrl : LiveData<String> = Transformations.map(retrievalCompleted) {
        imageOfDayData?.url
    }
    val imageOfDayBitmap: LiveData<Bitmap> = Transformations.map(retrievalCompleted) {
        imageOfDayCache?.bitmap
    }

    init {
        getImageData()
    }

    /** Gets image data from either database or api depending on
     *  if the data has been cached **/
    fun getImageData() {
        viewModelScope.launch {
            val isCached = repository.isDataCached()

            isCached.onSuccess {
                if(it) {
                    getImageEntity()
                } else {
                    getImageModel()
                }
            }
            isCached.onFailure {
                getImageModel()
            }

        }
    }

    /** Gets the data from Room database and returns the data
     *  in imageOfDayData variable **/
    private suspend fun getImageModel() {
        val response = repository.imageOfTheDayModel()
        response.onSuccess {
            imageOfDayData = it
            _retrievalCompleted.postValue(true)
        }
        response.onFailure {
            _retrievalCompleted.postValue(false)
        }
    }

    /** Gets the data from Room database and returns the data
     *  in imageOfDayCache variable **/
    private suspend fun getImageEntity() {
        val response = repository.imageOfTheDayEntity()
        response.onSuccess {
            imageOfDayCache = it
            _retrievalCompleted.postValue(true)
        }
        response.onFailure {
            _retrievalCompleted.postValue(false)
        }
    }

    fun cacheData(
        drawable: Drawable
    ) {
        viewModelScope.launch {
            imageOfDayData?.let { repository.cacheData(it, drawable) }
        }
    }
}