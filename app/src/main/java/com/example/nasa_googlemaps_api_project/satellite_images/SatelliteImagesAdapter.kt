package com.example.nasa_googlemaps_api_project.satellite_images

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nasa_googlemaps_api_project.databinding.RecyclerGalleryLayoutBinding
import com.example.nasa_googlemaps_api_project.databinding.RecyclerTitleLayoutBinding
import com.example.nasa_googlemaps_api_project.satellite_images.data.room.SatelliteImageEntities

//variables to determine different viewHolders
private const val TITLE = 0
private const val CARD_VIEW = 1

class SatelliteImagesAdapter :
    ListAdapter<SatelliteImageEntities, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        //if viewHolder is title return appropriate viewHolder otherwise don't
        return if(viewType == TITLE) {
            val binding = RecyclerTitleLayoutBinding.inflate(
                inflater, parent, false
            )
            TitleViewHolder(binding)
        } else {
            val binding = RecyclerGalleryLayoutBinding.inflate(
                inflater, parent, false
            )
            GalleryViewHolder(binding)
        }
    }

    override fun getItemViewType(position: Int): Int {

        //make certain position return a title viewHolder if the date changes between images
        return if(calculateHeaderLocations()[position]) {
            TITLE
        } else {
            CARD_VIEW
        }
    }

    private fun calculateHeaderLocations(): List<Boolean> {
        val headerLocations = mutableListOf<Boolean>()

        //for every item in list
        for(i in 0 until itemCount) {
            //if the title has unique number selected for titles
            if(getItem(i).title == "031583") {
                    //set position in headersLocation to "true" signifying at this location
                    //a header belongs
                headerLocations.add(true)
            } else {
                headerLocations.add(false)
            }
        }

        return headerLocations
    }

    //viewHolder for satellite images
    inner class GalleryViewHolder(private val binding: RecyclerGalleryLayoutBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SatelliteImageEntities) = with(binding) {

            recyclerImageSatellite.setImageBitmap(item.image)
            recyclerTextTitle.text = item.title
            recyclerTextDate.text = item.dateTaken
        }
    }

    //viewHolder for date title
    inner class TitleViewHolder(private val binding: RecyclerTitleLayoutBinding)
        : RecyclerView.ViewHolder(binding.root) {

            fun bind(item: SatelliteImageEntities) = with(binding) {
                recyclerDateLayout.text = item.dateTaken.subSequence(0..3)
            }
        }

    //determine what viewHolder to use
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is TitleViewHolder) {
            holder.bind(getItem(position))
        } else {
            (holder as GalleryViewHolder).bind(getItem(position))
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