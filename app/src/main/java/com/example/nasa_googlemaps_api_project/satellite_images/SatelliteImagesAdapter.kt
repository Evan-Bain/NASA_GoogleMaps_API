package com.example.nasa_googlemaps_api_project.satellite_images

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nasa_googlemaps_api_project.R
import com.example.nasa_googlemaps_api_project.databinding.RecyclerGalleryLayoutBinding
import com.example.nasa_googlemaps_api_project.satellite_images.data.room.SatelliteImageEntities
import java.text.SimpleDateFormat
import java.util.*

class SatelliteImagesAdapter :
    ListAdapter<SatelliteImageEntities, SatelliteImagesAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerGalleryLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: RecyclerGalleryLayoutBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SatelliteImageEntities) = with(binding) {
            recyclerImageSatellite.setImageBitmap(item.image)
            recyclerTextTitle.text = item.title
            recyclerTextDate.text = item.dateTaken
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<SatelliteImageEntities>() {
    override fun areItemsTheSame(
        oldItem: SatelliteImageEntities,
        newItem: SatelliteImageEntities
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: SatelliteImageEntities,
        newItem: SatelliteImageEntities
    ): Boolean {
        return oldItem == newItem
    }

}