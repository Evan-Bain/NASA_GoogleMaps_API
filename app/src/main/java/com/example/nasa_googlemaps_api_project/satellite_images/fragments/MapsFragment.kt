package com.example.nasa_googlemaps_api_project.satellite_images.fragments

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.nasa_googlemaps_api_project.databinding.FragmentMapsBinding
import com.example.nasa_googlemaps_api_project.satellite_images.SatelliteViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
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

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMapLoadedCallback(this)

        //As map loads certain areas display loading indicator at top
        map.setOnCameraMoveListener {
            map.setOnMapLoadedCallback(this)
            scaleY(binding.loadingIndicatorTopMap, true)
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        //Start the map on onResume to prevent lag spikes prior to loading fragment
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

    /** Fade in a view **/
    private fun fadeIn(view: View) {
        ObjectAnimator.ofFloat(view, View.ALPHA, 1f).apply {
            //default duration is .5s if duration not set
            duration = 1000
            setAutoCancel(true)
            start()
        }
    }

    /** Scale the Y value of a view in or out **/
    private fun scaleY(view: View, start: Boolean) {
        if(start) {
            ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f).apply {
                duration = 250
                start()
            }
        } else {
            ObjectAnimator.ofFloat(view, View.SCALE_Y, 0f).apply {
                start()
            }
        }
    }

    override fun onMapLoaded() {
        //If statement - to avoid unnecessary code running just for top loading indicator to disappear
        if(firstLoad) {
            //When map has loaded make loading indicator disappear and map fade in
            binding.loadingIndicatorMaps.visibility = View.GONE
            fadeIn(binding.mapView)
            firstLoad = false
        } else {
            //When map has finished loading certain areas hide loading indicator at top
            scaleY(binding.loadingIndicatorTopMap, false)
        }
    }
}