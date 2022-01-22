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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.nasa_googlemaps_api_project.R
import com.example.nasa_googlemaps_api_project.bindImageViewToUrl
import com.example.nasa_googlemaps_api_project.databinding.FragmentHomeBinding
import com.example.nasa_googlemaps_api_project.home.data.ImageOfDayRepository
import com.example.nasa_googlemaps_api_project.home.data.room.ImageOfDayDatabase
import com.example.nasa_googlemaps_api_project.home.network.ImageOfDayApiClient
import kotlinx.coroutines.Dispatchers


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel by lazy {

        val database = ImageOfDayDatabase.getDatabase(requireContext())
            .imageOfDayDao()
        val repository = ImageOfDayRepository(
            ImageOfDayApiClient.createApi(), database, Dispatchers.Default)

        val viewModelFactory = HomeViewModelFactory(repository)
        ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]
    }

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

        viewModel.imageOfDayUrl.observe(viewLifecycleOwner, {
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
        })

        viewModel.imageOfDayBitmap.observe(viewLifecycleOwner, {
            //if database already has data, load the data
            it?.let {
                binding.imageOfTheDay.setImageBitmap(it)

                binding.loadingIndicatorHome.visibility = View.GONE
                fadeIn(binding.imageOfTheDay)
                fadeIn(binding.imageOfTheDayText)
            }
        })

        viewModel.retrievalCompleted.observe(viewLifecycleOwner, {
            //if the api was received successfully or not
            if(it) {
                if(binding.errorTextHome.visibility == View.VISIBLE) {
                    binding.errorTextHome.visibility = View.GONE
                    binding.errorTryAgainButtonHome.visibility = View.GONE
                }
            } else {
                binding.loadingIndicatorHome.visibility = View.GONE
                fadeIn(binding.errorTextHome as View)
                fadeIn(binding.errorTryAgainButtonHome as View)
            }
        })

        animateButton(binding.marsImagesButtonHome)
        animateButton(binding.earthSatelliteViewButtonHome)
        binding.marsImagesButtonHome.setOnClickListener {
            findNavController().navigate(R.id.marsImagesFragment)
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
    @SuppressLint("ClickableViewAccessibility")
    private fun animateButton(button: View) {
        button.setOnLongClickListener {
            val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, .8f)
            val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, .8f)
            val animator = ObjectAnimator.ofPropertyValuesHolder(
                button, scaleX, scaleY
            )
            animator.start()
            true
        }

        button.setOnTouchListener { view, event ->
            when (event?.action) {
                MotionEvent.ACTION_UP -> {
                    val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f)
                    val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f)
                    val animator = ObjectAnimator.ofPropertyValuesHolder(
                        view, scaleX, scaleY
                    )
                    animator.start()
                }
            }
            view?.onTouchEvent(event) ?: false
        }
    }

    private fun fadeIn(view: View) {
        if(view.visibility == View.GONE) {
            view.visibility = View.VISIBLE
        }
        val animator = ObjectAnimator.ofFloat(view, View.ALPHA, 1f)
        animator.duration = 1000L
        animator.start()
    }

    private fun fadeOut(view: View) {
        val animator = ObjectAnimator.ofFloat(view, View.ALPHA, 0f)
        animator.start()
    }

    private fun cacheData(drawable: Drawable) {
        viewModel.cacheData(drawable)
    }

}