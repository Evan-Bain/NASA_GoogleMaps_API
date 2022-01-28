package com.example.nasa_googlemaps_api_project.satellite_images

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nasa_googlemaps_api_project.home.data.ImageOfDayModel
import com.example.nasa_googlemaps_api_project.satellite_images.data.EarthSatelliteModel
import com.example.nasa_googlemaps_api_project.satellite_images.data.network.EarthSatelliteRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

class SatelliteViewModel(
    private val repository: EarthSatelliteRepository
) : ViewModel() {

    val image = MutableLiveData<EarthSatelliteModel>()

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

    fun getImage(lat: String, lng: String) {
        viewModelScope.launch {
            image.value = repository.getSatelliteImage(lat, lng)
        }
    }

}