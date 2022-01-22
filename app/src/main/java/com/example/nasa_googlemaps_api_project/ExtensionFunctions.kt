package com.example.nasa_googlemaps_api_project

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.progressindicator.CircularProgressIndicator

fun ImageView.bindImageViewToUrl(
    url: String, loadingIndicator: CircularProgressIndicator,
    function: ((View) -> Unit?)? = null,
    function2: ((Drawable) -> Unit?)? = null,
    imageView: ImageView? = null, textView: TextView? = null) {

        val imgUri = url.toUri().buildUpon().scheme("https").build()
        Glide.with(this.context)
            .load(imgUri)
            .addListener(object: RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    loadingIndicator.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    loadingIndicator.visibility = View.GONE
                    if (function != null) {
                        if(imageView != null) {
                            function(imageView)
                        }
                        if(textView != null) {
                            function(textView)
                        }
                    }

                    if(function2 != null) {
                        if (resource != null) {
                            function2(resource)
                        }
                    }
                    return false
                }

            })
            .into(this)
}