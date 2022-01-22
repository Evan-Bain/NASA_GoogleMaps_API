package com.example.nasa_googlemaps_api_project

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class Converters {

    @TypeConverter
    fun fromByte(value: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(value, 0,
            value.size)
    }

    @TypeConverter
    fun toByte(value: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        value.compress(Bitmap.CompressFormat.PNG, 90, stream)

        return stream.toByteArray()
    }
}