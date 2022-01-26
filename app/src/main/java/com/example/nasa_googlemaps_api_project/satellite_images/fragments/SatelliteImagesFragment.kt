package com.example.nasa_googlemaps_api_project.satellite_images.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.nasa_googlemaps_api_project.databinding.FragmentSatelliteImagesBinding
import com.example.nasa_googlemaps_api_project.satellite_images.SatelliteViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SatelliteImagesFragment : Fragment() {

    private lateinit var binding: FragmentSatelliteImagesBinding

    private val sharedViewModel by sharedViewModel<SatelliteViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSatelliteImagesBinding.inflate(
            inflater,
            container,
            false
        )

        binding.viewModel = sharedViewModel

        return binding.root
    }

}