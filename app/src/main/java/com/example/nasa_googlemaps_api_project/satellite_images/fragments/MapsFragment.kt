package com.example.nasa_googlemaps_api_project.satellite_images.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.nasa_googlemaps_api_project.*
import com.example.nasa_googlemaps_api_project.databinding.FragmentMapsBinding
import com.example.nasa_googlemaps_api_project.satellite_images.SatelliteViewModel
import com.google.android.gms.location.LocationServices
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

    //determine if date is being entered in for first time
    //auto apply first dash
    private var firstDash = true

    //determine if date is being entered for the first time
    //auto apply second dash
    private var secondDash = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableLocation()
    }

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
        map.enableLocation()
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

        //If statement -
        // to display certain code on map loaded for first time when fragment created
        if(firstLoad) {

            //keep markers for orientation changes
            sharedViewModel.markerPositionList.observe(viewLifecycleOwner) {

                //saved markers have a positive tag
                var position = 0

                it.forEach { latLng ->

                    map.addMarker(MarkerOptions()
                        .position(latLng)
                        .title("View image"))
                        ?.tag = position

                    position++
                }
            }

            //As map loads certain areas display loading indicator at top
            map.setOnCameraMoveListener {
                map.setOnMapLoadedCallback(this@MapsFragment)
                binding.loadingIndicatorTopMap.scaleY(true, 250)
            }

            map.moveToLocation()

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

    /** ask for location permissions **/
    private fun enableLocation() {

        val requestMultiplePermissions =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}

        requestMultiplePermissions.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    /** enables current location if permission granted **/
    private fun GoogleMap.enableLocation() {

        val fineLocationGranted = ActivityCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted = ActivityCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED

        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        if (fineLocationGranted || coarseLocationGranted) {

            val lastLocation = fusedLocationProviderClient.lastLocation

            lastLocation.addOnCompleteListener(
                requireActivity()
            ) { task ->
                if (task.isSuccessful) {
                    this@enableLocation.isMyLocationEnabled = true
                    this@enableLocation.uiSettings.isMyLocationButtonEnabled = true

                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    /** moves to current location if enabled **/
    private fun GoogleMap.moveToLocation() {

        if(isMyLocationEnabled) {
            val fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())

            val lastLocation = fusedLocationProviderClient.lastLocation

            lastLocation.addOnCompleteListener(requireActivity()
            ) { task ->
                if (task.isSuccessful) {
                    animateCameraMove(LatLng(
                        lastLocation.result.latitude,
                        lastLocation.result.longitude))
                }
            }
        }
    }

    /** adds or removes all click listeners used in GoogleMap **/
    private fun GoogleMap.setClickListeners(setListeners: Boolean) {

        if(setListeners) {
            //check if the click is too close to a marker before adding marker
            this.setOnMapClickListener {

                val positionList = sharedViewModel.markerPositionList

                var inRange = false

                positionList.value?.let { list ->

                    val range = list.indices

                    for(i in range) {

                        //establish corners of boundaries surrounding markers
                        val points = list.toCoordinates(this)

                        //check if clicked position falls in one of the markers' bounds
                        val isInRangeLng = (points[i][2]..points[i][3]).contains(it.longitude)
                        val isInRangeLat = (points[i][0]..points[i][1]).contains(it.latitude)

                        //if click lands in marker's stop loop and return
                        if(isInRangeLat && isInRangeLng) {
                            inRange = true
                            break
                        }
                    }
                }

                //add marker if the click was not in another marker's range
                if(!inRange) {
                    sharedViewModel.addedMarker = this.createMarker(
                        it,
                        --sharedViewModel.markersAdded)
                    sharedViewModel.markerList.add(sharedViewModel.addedMarker!!)
                    animateCameraMove(it)
                }
            }

            //display enter_image_info view
            this.setOnInfoWindowClickListener {

                //allows current marker clicked to be visible to whole fragment
                sharedViewModel.currentMarker = it.tag as Int
                Log.i("MapsFragment", it.tag.toString())

                //sets up cardView preview or data depending on if the marker was just added or not
                if(sharedViewModel.currentMarker <= -1) {
                    setupDataCardView()
                } else {
                    setupPreviewCardView()
                }
                animateCameraMove(it.position)

                binding.enterImageInfoLayout?.fade(true)
                sharedViewModel.lat = it.position.latitude
                sharedViewModel.lng = it.position.longitude
            }

            //move to marker position and show the marker title
            this.setOnMarkerClickListener {
                animateCameraMove(it.position)

                it.showInfoWindow()
                true
            }
        } else {
            //info click listener not disabled as it can only be activated by marker listener
            this.setOnMapClickListener(null)
            this.setOnMarkerClickListener(null)
        }

    }

    /** setups logic and click listeners for the save image card view (dialog) **/
    private fun setupDataCardView() {

        binding.spaceFromEnd?.visibility = View.GONE

        //add action when viewModel's imageDataResult value is changed
        sharedViewModel.imageDataResult.observe(viewLifecycleOwner) {

            if(!sharedViewModel.previewCardActive) {

                //if success use glide to display image
                it.onSuccess { model ->
                    model?.let {
                        binding.imagePreviewMaps?.bindImageViewToUrl(model.url, binding.loadingIndicatorCardMaps)
                    }
                }

                //if failure alert user there is no data available
                it.onFailure {
                    binding.imagePreviewMaps?.visibility = View.GONE
                    binding.noDataLocation?.fade(true)
                    binding.loadingIndicatorCardMaps?.visibility = View.GONE
                }
            }
        }

        //logic for pressing "x" button on cardView
        binding.cardCloseButton?.setOnClickListener {

            //fade out and make current views disappear so they don't display next time cardView
            //is displayed
            binding.enterImageInfoLayout?.fade(false)
            binding.noDataLocation?.visibility = View.GONE
            binding.imagePreviewMaps?.visibility = View.GONE
            binding.editTextFieldBottomMaps?.editableText?.clear()
            binding.editTextFieldTopMaps?.editableText?.clear()
            sharedViewModel.clearImageData()

            for(i in sharedViewModel.markerList) {
                if(sharedViewModel.currentMarker == i.tag) {
                    i.remove()
                }
            }
        }

        //If card view was last in preview mode change to data mode
        if(binding.cardDoneIcon?.visibility == View.GONE) {

            binding.apply {
                cardDoneIcon?.visibility = View.VISIBLE
                editTextFieldBottomMaps?.isFocusableInTouchMode = true
                editTextFieldTopMaps?.isFocusableInTouchMode = true
                textFieldBottomMaps?.isHelperTextEnabled = true
                textFieldBottomMaps?.helperText = "Format: YYYY-MM-DD"
                textFieldTopMaps?.isCounterEnabled = true
                textFieldBottomMaps?.isCounterEnabled = true
            }
        }

        //logic for check button on cardView
        binding.cardDoneIcon?.setOnClickListener {

            try {
                //only add functionality if data was received successfully
                sharedViewModel.imageDataResult.value?.onSuccess {

                    it?.let {

                        //save image to room database
                        sharedViewModel.saveSatelliteImage(
                            it,
                            binding.textFieldTopMaps?.editText?.text.toString(),
                            binding.imagePreviewMaps?.drawable!!.toBitmap(),
                            sharedViewModel.lat.toString(),
                            sharedViewModel.lng.toString()
                        )

                        //make cardView fade out and views disappear so they aren't displayed when
                        //cardView is reopened
                        binding.enterImageInfoLayout?.fade(false)
                        binding.noDataLocation?.visibility = View.GONE
                        binding.imagePreviewMaps?.visibility = View.GONE
                        binding.editTextFieldBottomMaps?.editableText?.clear()
                        binding.editTextFieldTopMaps?.editableText?.clear()
                        sharedViewModel.clearImageData()

                        for(i in sharedViewModel.markerList) {
                            if(sharedViewModel.currentMarker == i.tag) {
                                i.remove()
                            }
                        }
                    }
                }
            } catch(e: Exception) {
                //Image not loaded yet
            }
        }

        //alter date edit text for user
        binding.editTextFieldBottomMaps?.addTextChangedListener {

            it?.let {
                when(it.length) {
                    4 -> {
                        if(firstDash) {
                            it.append("-")
                            firstDash = false
                        }
                    }
                    7 -> {
                        if(secondDash) {
                            it.append("-")
                            secondDash = false
                        }
                    }

                    in 1..3 -> firstDash = true

                    in 1..6 -> secondDash = true

                    10 -> {
                        binding.noDataLocation?.visibility = View.GONE
                        sharedViewModel.checkDateAndGetImage(
                            sharedViewModel.lat.toString(),
                            sharedViewModel.lng.toString(),
                            it.toString())
                    }
                }
            }
        }
    }

    private fun setupPreviewCardView() {

        sharedViewModel.previewCardActive = true
        binding.spaceFromEnd?.visibility = View.VISIBLE
        binding.imagePreviewMaps?.visibility = View.VISIBLE

        //Disable edit functionality
        binding.apply {
            cardDoneIcon?.visibility = View.GONE
            editTextFieldTopMaps?.isFocusableInTouchMode = false
            editTextFieldBottomMaps?.isFocusableInTouchMode = false
            textFieldBottomMaps?.isHelperTextEnabled = false
            textFieldTopMaps?.isCounterEnabled = false
            textFieldBottomMaps?.isCounterEnabled = false
        }

        sharedViewModel.imageEntities.value?.let {

            binding.apply {
                editTextFieldTopMaps?.setText(it[sharedViewModel.currentMarker].title)
                editTextFieldBottomMaps?.setText(it[sharedViewModel.currentMarker].dateTaken)
                imagePreviewMaps?.setImageBitmap(it[sharedViewModel.currentMarker].image)
            }
        }

        //logic for pressing "x" button on cardView
        binding.cardCloseButton?.setOnClickListener {

            sharedViewModel.previewCardActive = false
            //fade out and make current views disappear so they don't display next time cardView
            //is displayed
            binding.enterImageInfoLayout?.fade(false)
            binding.imagePreviewMaps?.visibility = View.GONE
            binding.editTextFieldBottomMaps?.editableText?.clear()
            binding.editTextFieldTopMaps?.editableText?.clear()
            sharedViewModel.clearImageData()
        }
    }
}