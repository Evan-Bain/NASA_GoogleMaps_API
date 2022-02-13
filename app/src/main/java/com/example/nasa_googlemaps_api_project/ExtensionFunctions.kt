package com.example.nasa_googlemaps_api_project

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.TypeEvaluator
import android.annotation.SuppressLint
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.Property
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.Projection
import com.google.android.gms.maps.model.*
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlin.math.abs
import kotlin.math.sign

/** Converts a url into a drawable that is inserted into the imageView **/
fun ImageView.bindImageViewToUrl(
    url: String, loadingIndicator: CircularProgressIndicator? = null,
    function: ((Drawable) -> Unit?)? = null,
    imageView: ImageView? = null, textView: TextView? = null
) {
    loadingIndicator?.visibility = View.VISIBLE
    this.visibility = View.VISIBLE

    val imgUri = url.toUri().buildUpon().scheme("https").build()
    Glide.with(this.context)
        .load(imgUri)
        .addListener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                loadingIndicator?.visibility = View.GONE
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                loadingIndicator?.visibility = View.GONE

                //fade in views when ready
                imageView?.fade(true)
                textView?.fade(true)


                if (function != null) {
                    if (resource != null) {
                        function(resource)
                    }
                }
                return false
            }

        })
        .into(this)
}
//END OF BIND IMAGE TO URL

/** Create an animated marker on map click **/
fun GoogleMap.createMarker(
    position: LatLng,
    tag: Int): Marker? {

    val proj: Projection = this.projection
    val targetPoint = proj.toScreenLocation(position)
    val startPoint = proj.toScreenLocation(position)
    startPoint.y = targetPoint.y - 100
    val startLatLng = proj.fromScreenLocation(startPoint)

    val marker = this.addMarker(
        MarkerOptions()
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
            .position(startLatLng)
            .title("View image")
    )
    marker?.tag = tag

    animateMarker(marker, position, LatLngInterpolator.LinearFixed())

    return marker
}

/* Copyright 2013 Google Inc.
   Licensed under Apache 2.0: http://www.apache.org/licenses/LICENSE-2.0.html */
private fun animateMarker(
    marker: Marker?,
    finalPosition: LatLng?,
    latLngInterpolator: LatLngInterpolator
) {
    val typeEvaluator: TypeEvaluator<LatLng> =
        TypeEvaluator<LatLng> { fraction, startValue, endValue ->
            latLngInterpolator.interpolate(fraction, startValue, endValue)
        }
    val property: Property<Marker, LatLng> = Property.of(
        Marker::class.java,
        LatLng::class.java, "position"
    )
    ObjectAnimator.ofObject(marker, property, typeEvaluator, finalPosition).apply {
        duration = 1500
        interpolator = LinearOutSlowInInterpolator()
        start()
    }
}

/* Copyright 2013 Google Inc.
   Licensed under Apache 2.0: http://www.apache.org/licenses/LICENSE-2.0.html */
interface LatLngInterpolator {
    fun interpolate(fraction: Float, a: LatLng, b: LatLng): LatLng


    class LinearFixed : LatLngInterpolator {
        override fun interpolate(fraction: Float, a: LatLng, b: LatLng): LatLng {
            val lat = (b.latitude - a.latitude) * fraction + a.latitude
            var lngDelta = b.longitude - a.longitude

            // Take the shortest path across the 180th meridian.
            if (abs(lngDelta) > 180) {
                lngDelta -= sign(lngDelta) * 360
            }
            val lng = lngDelta * fraction + a.longitude
            return LatLng(lat, lng)
        }
    }
}
//END OF ANIMATE MARKER

//VIEW ANIMATIONS

/** Fades a view in or out **/
fun View.fade(fadeIn: Boolean, animDuration: Long? = null) {

    val view = this

    if (fadeIn) {
        //if view is gone make it visible
        if(this.visibility == View.GONE) {
            this.visibility = View.VISIBLE
        }

        ObjectAnimator.ofFloat(this, View.ALPHA, 1f).apply {

            duration = animDuration ?: duration
            setAutoCancel(true)
            start()
        }
    } else {
        ObjectAnimator.ofFloat(this, View.ALPHA, 0f).apply {

            duration = animDuration ?: duration

            //make view completely disappear after completion
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator?) {
                    //NOTHING
                }

                override fun onAnimationEnd(p0: Animator?) {
                    view.visibility = View.GONE
                }

                override fun onAnimationCancel(p0: Animator?) {
                    view.visibility = View.GONE
                }

                override fun onAnimationRepeat(p0: Animator?) {
                    //NOTHING
                }

            })

            setAutoCancel(true)
            start()
        }
    }
}
//END OF FADE

/** animates button to 80% of its size on long click and
returns to normal upon release **/
//Suppressed because these button clicks are purely visual and irrelevant
//for screen readers
@SuppressLint("ClickableViewAccessibility")
fun Button.animateScaleOnLongClick() {
    this.setOnLongClickListener {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, .8f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, .8f)

        ObjectAnimator.ofPropertyValuesHolder(it, scaleX, scaleY).apply {
            start()
        }
        true
    }

    this.setOnTouchListener { view, event ->
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
//END OF ANIMATE BUTTON

/** Scale the Y value of a view in or out **/
fun View.scaleY(start: Boolean, animDuration: Long? = null) {
    if (start) {
        ObjectAnimator.ofFloat(this, View.SCALE_Y, 1f).apply {
            duration = animDuration ?: duration
            start()
        }
    } else {
        ObjectAnimator.ofFloat(this, View.SCALE_Y, 0f).apply {
            duration = animDuration ?: duration
            start()
        }
    }
}
//END OF SCALE Y

/** Convert LatLng to coordinates on phone **/
fun List<LatLng>.toCoordinates(map: GoogleMap): List<List<Double>> {
    val proj: Projection = map.projection

    val coordinateList = mutableListOf<List<Double>>()

    //create bounds for each position in list
    this.forEach {
        //get screen coordinates at the current LatLng
        val point = proj.toScreenLocation(it)
        val left = point.x - 100
        val right = point.x + 100
        val top = point.y - 100
        val bottom = point.y + 100

        //convert bounds into two points diagonal of each other
        val topRight = Point(right, top)
        val bottomLeft = Point(left, bottom)

        //convert the two points into LatLng points and get the bounds in north, south, west, and east
        val northEast = proj.fromScreenLocation(topRight)
        val north = northEast.latitude
        val east = northEast.longitude

        val southWest = proj.fromScreenLocation(bottomLeft)
        val south = southWest.latitude
        val west = southWest.longitude

        //add the bounds to be returned in a list which corresponds to a marker
        coordinateList.add(listOf(
            south,
            north,
            west,
            east
        ))
    }

    return coordinateList
}
//END OF TO COORDINATES



