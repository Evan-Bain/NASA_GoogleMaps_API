package com.example.nasa_googlemaps_api_project.satellite_images.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nasa_googlemaps_api_project.R
import com.example.nasa_googlemaps_api_project.databinding.FragmentSatelliteImagesBinding
import com.example.nasa_googlemaps_api_project.satellite_images.SatelliteImagesAdapter
import com.example.nasa_googlemaps_api_project.satellite_images.SatelliteViewModel
import com.example.nasa_googlemaps_api_project.satellite_images.data.room.SatelliteImageEntities
import com.example.nasa_googlemaps_api_project.satellite_images.data.room.sortByDate
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

        val navHostFragment = requireActivity().supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        //setting up recyclerView
        val adapter = SatelliteImagesAdapter {
            adapterClick(it, navHostFragment.navController)
        }
        binding.satelliteImagesRecycler.adapter = adapter

        //allowing three images to be shown in one row with the title text only by itself
        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when(adapter.getItemViewType(position)) {
                    0 -> {
                        3
                    }
                    1 -> {
                        1
                    }
                    else -> -1
                }
            }
        }

        binding.satelliteImagesRecycler.layoutManager = gridLayoutManager
        return binding.root
    }

    private fun adapterClick(
        item: SatelliteImageEntities, navController: NavController) {
        sharedViewModel.setPassedData(item)

        navController.navigate(SatelliteImagesFragmentDirections
            .actionSatelliteImagesFragmentToSatelliteImagePreviewFragment())
    }
}

//setting the data in the recyclerView
@BindingAdapter("data")
fun setRecyclerData(recycler: RecyclerView, list: List<SatelliteImageEntities>) {

    if(recycler.adapter is SatelliteImagesAdapter) {
        (recycler.adapter as SatelliteImagesAdapter).submitList(list.sortByDate())
    }
}