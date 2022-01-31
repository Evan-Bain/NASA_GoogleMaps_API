package com.example.nasa_googlemaps_api_project.satellite_images.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nasa_googlemaps_api_project.databinding.FragmentSatelliteImagesBinding
import com.example.nasa_googlemaps_api_project.satellite_images.RecyclerAdapter
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

        sharedViewModel.getSatelliteImages()

        sharedViewModel.getCompleted.observe(viewLifecycleOwner) {

            if(it) {
                sharedViewModel.satelliteImages?.let { entity ->
                    binding.recyclerView.adapter = RecyclerAdapter(entity)
                    sharedViewModel.getCompleted.value = false
                }
            }
        }

        binding.recyclerView.layoutManager = GridLayoutManager(activity, 3)

        binding.viewModel = sharedViewModel

        return binding.root
    }

}