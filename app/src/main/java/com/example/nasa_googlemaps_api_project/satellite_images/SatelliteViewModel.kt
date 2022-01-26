package com.example.nasa_googlemaps_api_project.satellite_images

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SatelliteViewModel : ViewModel() {

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
}