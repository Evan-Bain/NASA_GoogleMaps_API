package com.example.nasa_googlemaps_api_project.home

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nasa_googlemaps_api_project.bindImageViewToUrl
import com.example.nasa_googlemaps_api_project.databinding.FragmentHomeBinding
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
                    ::fadeIn,
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
                fadeIn(binding.imageOfTheDay)
                fadeIn(binding.imageOfTheDayText)
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
                fadeIn(binding.errorTextHome as View)
                fadeIn(binding.errorTryAgainButtonHome as View)
            }
        }

        animateButton(binding.marsImagesButtonHome)
        binding.marsImagesButtonHome.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToMarsImagesFragment())
        }

        animateButton(binding.earthSatelliteViewButtonHome)
        binding.earthSatelliteViewButtonHome.setOnClickListener {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToSatelliteImagesFragment())
        }

        //Retries getting the Nasa Image of the day
        binding.errorTryAgainButtonHome.setOnClickListener {
            fadeOut(binding.errorTextHome as View)
            fadeOut(binding.errorTryAgainButtonHome as View)
            binding.loadingIndicatorHome.visibility = View.VISIBLE
            viewModel.getImageData()
        }

        return binding.root
    }

    /** animates button to 80% of its size on long click and
        returns to normal upon release **/
    //Suppressed because these button clicks are purely visual and irrelevant
    //for screen readers
    @SuppressLint("ClickableViewAccessibility")
    private fun animateButton(button: View) {
        button.setOnLongClickListener {
            val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, .8f)
            val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, .8f)

            ObjectAnimator.ofPropertyValuesHolder(button, scaleX, scaleY).apply {
                start()
            }
            true
        }

        button.setOnTouchListener { view, event ->
            when (event?.action) {
                //When finger is lifted return button to normal size
                MotionEvent.ACTION_UP -> {
                    val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f)
                    val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f)

                    ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY).apply {
                        start()
                    }
                }
            }
            view?.onTouchEvent(event) ?: false
        }
    }

    /** Fade in a view **/
    private fun fadeIn(view: View) {
        if(view.visibility == View.GONE) {
            view.visibility = View.VISIBLE
        }

        ObjectAnimator.ofFloat(view, View.ALPHA, 1f).apply {
            duration = 1000
            start()
        }
    }

    /** Fade out a view **/
    private fun fadeOut(view: View) {
        ObjectAnimator.ofFloat(view, View.ALPHA, 0f).apply {
            start()
        }
    }

    private fun cacheData(drawable: Drawable) {
        viewModel.cacheData(drawable)
    }
}