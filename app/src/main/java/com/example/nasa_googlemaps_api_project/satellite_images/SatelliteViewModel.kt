package com.example.nasa_googlemaps_api_project.satellite_images

import android.graphics.Bitmap
import androidx.lifecycle.*
import com.example.nasa_googlemaps_api_project.Globals.validDate
import com.example.nasa_googlemaps_api_project.satellite_images.data.EarthSatelliteModel
import com.example.nasa_googlemaps_api_project.satellite_images.data.EarthSatelliteRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class SatelliteViewModel(
    private val repository: EarthSatelliteRepository
) : ViewModel() {

    //Data for NASA api Satellite Images
    private val _imageDataResult = MutableLiveData<Result<EarthSatelliteModel?>>()
    val imageDataResult: LiveData<Result<EarthSatelliteModel?>>
        get() = _imageDataResult

    /** set imageData to null **/
   fun clearImageData() {
       _imageDataResult.value = Result.success(null)
   }

    private val _markerPositionList = mutableListOf<LatLng>()
    val markerPositionList: List<LatLng>
        get() = _markerPositionList

    /** Add position of a marker to markerPositionList **/
    fun addMarker(position: LatLng) {
        _markerPositionList.add(position)
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
        }
    }
}