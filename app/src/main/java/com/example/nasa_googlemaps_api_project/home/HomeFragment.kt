package com.example.nasa_googlemaps_api_project.home

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nasa_googlemaps_api_project.animateScaleOnLongClick
import com.example.nasa_googlemaps_api_project.bindImageViewToUrl
import com.example.nasa_googlemaps_api_project.databinding.FragmentHomeBinding
import com.example.nasa_googlemaps_api_project.fade
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(
            inflater,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.imageOfDayUrl.observe(viewLifecycleOwner) {
            //if url is not null display data
            it?.let {
                binding.imageOfTheDay.bindImageViewToUrl(
                    it,
                    binding.loadingIndicatorHome,
                    ::cacheData,
                    binding.imageOfTheDay,
                    binding.imageOfTheDayText
                )

            }
        }

        viewModel.imageOfDayBitmap.observe(viewLifecycleOwner) {
            //if database already has data, load the data
            it?.let {
                binding.imageOfTheDay.setImageBitmap(it)

                binding.loadingIndicatorHome.visibility = View.GONE
                binding.imageOfTheDay.fade(true, 1000)
                binding.imageOfTheDayText.fade(true, 1000)
            }
        }

        viewModel.retrievalCompleted.observe(viewLifecycleOwner) {
            //if the api was received successfully or not
            if (it) {
                if (binding.errorTextHome.visibility == View.VISIBLE) {
                    binding.errorTextHome.visibility = View.GONE
                    binding.errorTryAgainButtonHome.visibility = View.GONE
                }
            } else {
                binding.loadingIndicatorHome.visibility = View.GONE
                binding.errorTextHome.fade(true, 1000)
                binding.errorTryAgainButtonHome.fade(true, 1000)
            }
        }

        binding.marsImagesButtonHome.animateScaleOnLongClick()
        binding.marsImagesButtonHome.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToMarsImagesFragment())
        }

        binding.earthSatelliteViewButtonHome.animateScaleOnLongClick()
        binding.earthSatelliteViewButtonHome.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToSatelliteImagesFragment())
        }

        //Retries getting the Nasa Image of the day
        binding.errorTryAgainButtonHome.setOnClickListener {
            binding.errorTextHome.fade(false)
            binding.errorTryAgainButtonHome.fade(false)

            binding.loadingIndicatorHome.visibility = View.VISIBLE
            viewModel.getImageData()
        }

        return binding.root
    }

    private fun cacheData(drawable: Drawable) {
        viewModel.cacheData(drawable)
    }
}