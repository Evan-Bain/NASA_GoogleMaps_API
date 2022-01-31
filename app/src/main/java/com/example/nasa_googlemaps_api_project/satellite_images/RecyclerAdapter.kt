package com.example.nasa_googlemaps_api_project.satellite_images

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nasa_googlemaps_api_project.R
import com.example.nasa_googlemaps_api_project.satellite_images.data.room.SatelliteImageDatabase
import com.example.nasa_googlemaps_api_project.satellite_images.data.room.SatelliteImageEntities
import com.github.chrisbanes.photoview.PhotoView
import org.koin.core.component.getScopeId

class RecyclerAdapter(
    private val data: List<SatelliteImageEntities>
) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val photoView: PhotoView = view.findViewById(R.id.photo_view)
        val textView: TextView = view.findViewById(R.id.text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_list, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.photoView.setImageBitmap(data[position].image)
        holder.textView.text = data[position].title
    }

    override fun getItemCount() = data.size
}