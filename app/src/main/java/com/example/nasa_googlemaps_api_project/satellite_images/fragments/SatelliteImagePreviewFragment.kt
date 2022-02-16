package com.example.nasa_googlemaps_api_project.satellite_images.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.transition.ChangeBounds
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.nasa_googlemaps_api_project.R
import com.example.nasa_googlemaps_api_project.databinding.FragmentSatelliteImagePreviewBinding
import com.example.nasa_googlemaps_api_project.satellite_images.SatelliteViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SatelliteImagePreviewFragment : Fragment() {

    private lateinit var binding: FragmentSatelliteImagePreviewBinding
    private val sharedViewModel by sharedViewModel<SatelliteViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSatelliteImagePreviewBinding.inflate(
            inflater,
            container,
            false
        )

        sharedElementEnterTransition = ChangeBounds().apply {
            duration = 750
        }
        sharedElementReturnTransition= ChangeBounds().apply {
            duration = 750
        }

        binding.viewModel = sharedViewModel

        return binding.root
    }
}

@BindingAdapter("setBitmap")
fun setBitmap(imageView: ImageView, bitmap: Bitmap) {
    imageView.setImageBitmap(bitmap)
}