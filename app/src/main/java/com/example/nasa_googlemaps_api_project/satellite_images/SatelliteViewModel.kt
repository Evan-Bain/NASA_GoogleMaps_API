package com.example.nasa_googlemaps_api_project.satellite_images

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.*
import com.example.nasa_googlemaps_api_project.Globals.validDate
import com.example.nasa_googlemaps_api_project.satellite_images.data.EarthSatelliteModel
import com.example.nasa_googlemaps_api_project.satellite_images.data.EarthSatelliteRepository
import com.example.nasa_googlemaps_api_project.satellite_images.data.room.SatelliteImageEntities
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.launch
import java.lang.Exception

class SatelliteViewModel(
    private val repository: EarthSatelliteRepository
) : ViewModel() {

    //current lat and lng coordinates
    var lat: Double? = null
    var lng: Double? = null

    //currently added marker on map
    var addedMarker: Marker? = null

    //determine if preview card is active
    var previewCardActive = false

    //list of all data marker tags
    val markerList: MutableList<Marker> = mutableListOf()

    //integer used for unique marker tags
    var markersAdded: Int = 0

    //Data for NASA api Satellite Images
    private val _imageDataResult = MutableLiveData<Result<EarthSatelliteModel?>>()
    val imageDataResult: LiveData<Result<EarthSatelliteModel?>>
        get() = _imageDataResult

    private val _imageEntities = MutableLiveData<List<SatelliteImageEntities>>()
    val imageEntities: LiveData<List<SatelliteImageEntities>>
        get() =  _imageEntities

    //Holds value of the marker's tag that was clicked last
    var currentMarker: Int = 0

    /** set imageData to null **/
   fun clearImageData() {
       _imageDataResult.value = Result.success(null)
   }

    val markerPositionList: LiveData<List<LatLng>> = Transformations.map(imageEntities) {

        val markerList: MutableList<LatLng> = mutableListOf()

        for(i in it) {
            try {
                markerList.add(LatLng(
                    i.latitude.toDouble(),
                    i.longitude.toDouble()))
            } catch (e: Exception) {
                Log.e("SatelliteViewModel", e.toString())
            }
        }

        markerList
    }

    private val _googleMapsButton = MutableLiveData<Boolean?>()
    val googleMapsButton: LiveData<Boolean?>
        get() = _googleMapsButton

    /** False: navigate to SatelliteImagesFragment() /
     *  True: navigate to MapsFragment() **/
    fun activateGoogleMapsButton(value: Boolean) {
        _googleMapsButton.value = value
    }

    /** Reset googleMapsButton to original value **/
    fun resetButtons() {
        _googleMapsButton.value = null
    }

    /** Gets image from database and in case of failure returns a null value **/
    private fun getImage(lat: String, lng: String, date: String) {
        viewModelScope.launch {
            _imageDataResult.value = repository.getSatelliteImage(lat, lng, date)

        }
    }

    /** Checks if date is valid or not and gets image for the date if the date is valid **/
    fun checkDateAndGetImage(lat: String, lng: String, date: String) {
        validDate(date)?.let {
            getImage(lat, lng, it)
        }
    }

    /** Save satellite image and data to room database **/
    fun saveSatelliteImage(
        model: EarthSatelliteModel,
        title: String,
        bitmap: Bitmap,
        lat: String,
        lng: String
    ) {
        viewModelScope.launch {
            repository.saveSatelliteImage(model, title, bitmap, lat, lng)

            repository.getSatelliteImages().onSuccess {
                _imageEntities.value = it
            }
        }
    }

    init {
        viewModelScope.launch {

            repository.getSatelliteImages().onSuccess {
                _imageEntities.value = it
            }
        }
    }
}