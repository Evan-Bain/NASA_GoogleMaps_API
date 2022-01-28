package com.example.nasa_googlemaps_api_project.satellite_images.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.nasa_googlemaps_api_project.*
import com.example.nasa_googlemaps_api_project.databinding.FragmentMapsBinding
import com.example.nasa_googlemaps_api_project.satellite_images.SatelliteViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    private lateinit var binding: FragmentMapsBinding

    private lateinit var mapView: MapView
    private lateinit var map: GoogleMap

    private val sharedViewModel by sharedViewModel<SatelliteViewModel>()

    //determine if map is loading up again
    private var firstLoad = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapsBinding.inflate(
            inflater,
            container,
            false
        )

        binding.viewModel = sharedViewModel

        mapView = binding.mapView

        mapView.apply {
            onCreate(savedInstanceState)

            getMapAsync(this@MapsFragment)
            alpha = 0F
        }

        //ADDED

        sharedViewModel.image.observe(viewLifecycleOwner) {
            binding.imagePreviewMaps?.bindImageViewToUrl(it.url, binding.loadingIndicatorCardMaps)
        }

        binding.cardCloseButton?.setOnClickListener {
            binding.enterImageInfoLayout?.fade(false, 1000)
        }

        //ADDED

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMapLoadedCallback(this)

        //keep markers for orientation changes
        sharedViewModel.markerPositionList.forEach {
            map.addMarker(MarkerOptions()
                .position(it)
                .title("View image"))
        }

        //As map loads certain areas display loading indicator at top
        map.setOnCameraMoveListener {
            map.setOnMapLoadedCallback(this@MapsFragment)
            binding.loadingIndicatorTopMap.scaleY(true, 250)
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onMapLoaded() {
        //If statement - to avoid unnecessary code running just for top loading indicator to disappear
        if(firstLoad) {
            //When map has loaded make loading indicator disappear and map fade in
            binding.loadingIndicatorMaps.visibility = View.GONE
            binding.mapView.fade(true)
            map.setClickListeners(true)
            firstLoad = false
        } else {
            //When map has finished loading certain areas hide loading indicator at top
            binding.loadingIndicatorTopMap.scaleY(false)
        }
    }

    /** animate map camera to a certain position **/
    private fun animateCameraMove(position: LatLng) {

        //animate camera position along with the same zoom level
        val cameraPosition = CameraPosition.Builder()
            .target(position)
            .zoom(map.cameraPosition.zoom)
            .build()

        val newCameraPosition = CameraUpdateFactory.newCameraPosition(cameraPosition)

        //disable click listeners during animation
        map.setClickListeners(false)
        map.animateCamera(newCameraPosition, object : GoogleMap.CancelableCallback {
            override fun onCancel() {
                map.setClickListeners(true)
            }

            override fun onFinish() {
                map.setClickListeners(true)
            }

        })
    }

    /** adds or removes all click listeners used in GoogleMap **/
    private fun GoogleMap.setClickListeners(setListeners: Boolean) {

        if(setListeners) {
            //check if the click is too close to a marker before adding marker
            this.setOnMapClickListener {

                val positionList = sharedViewModel.markerPositionList

                var inRange = false

                for(i in positionList.indices) {

                    //establish corners of boundaries surrounding markers
                    val points = positionList.toCoordinates(this)

                    //check if clicked position falls in one of the markers' bounds
                    val isInRangeLng = (points[i][2]..points[i][3]).contains(it.longitude)
                    val isInRangeLat = (points[i][0]..points[i][1]).contains(it.latitude)

                    //if click lands in marker's stop loop and return
                    if(isInRangeLat && isInRangeLng) {
                        inRange = true
                        break
                    }
                }

                //add marker if the click was not in another marker's range
                if(!inRange) {
                    this.createMarker(it)
                    animateCameraMove(it)
                    sharedViewModel.addMarker(it)
                }
            }

            //move to marker position and display enter_image_info view
            this.setOnInfoWindowClickListener {
                animateCameraMove(it.position)

                //ADDED

                binding.enterImageInfoLayout?.fade(true, 1000)

                sharedViewModel.getImage(
                    it.position.latitude.toString(),
                    it.position.longitude.toString()
                )

                //ADDED
            }

            //move to marker position and show the marker title
            this.setOnMarkerClickListener {
                animateCameraMove(it.position)
                it.showInfoWindow()
                true
            }
        } else {
            this.setOnMapClickListener(null)
            this.setOnInfoWindowClickListener(null)
            this.setOnMarkerClickListener(null)
        }

    }
}