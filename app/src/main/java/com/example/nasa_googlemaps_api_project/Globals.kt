package com.example.nasa_googlemaps_api_project

import android.util.Log
import com.example.nasa_googlemaps_api_project.satellite_images.data.room.SatelliteImageEntities
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

object Globals {
    const val BASE_URL = "https://api.nasa.gov/"
    const val API_KEY = "3q2XHjcF8hQVgUtvtTXOQ2pZ8vfkV4MxcbGHyevw"

    /** returns today's date in String format **/
    fun todayDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR).toString()
        var month = (calendar.get(Calendar.MONTH)).plus(1).toString()
        month = if(month.length == 1) {
            "0$month"
        } else {
            month
        }

        var day = calendar.get(Calendar.DAY_OF_MONTH).toString()
        day = if(day.length == 1) {
            "0$day"
        } else {
            day
        }

        return "$year-$month-$day"
    }

    /** returns a random date between 1995 and 2021 in String format **/
    fun randomDate(): String {
        val randomYear = (1995..2021).random().toString()
        var randomMonth = (1..12).random().toString()
        randomMonth = if(randomMonth.length == 1) {
            "0${randomMonth}"
        } else {
            randomMonth
        }

        var randomDay = (1..28).random().toString()
        randomDay = if(randomDay.length == 1) {
            "0${randomDay}"
        } else {
            randomDay
        }

        return "$randomYear-$randomMonth-$randomDay"
    }

    /** returns a valid date or null depending on if the string is a valid date
     *  FORMAT: YYYY-MM-DD **/
    fun validDate(text: String): String? {
        val date = SimpleDateFormat("yyyy-MM-DD", Locale.getDefault())
        return try {
            date.parse(text)

            //if date is chosen is equal to or less than 2013
            if(text.substring(0..3).toInt() <= 2013) {

                return if(text.substring(0..3).toInt() < 2013) {
                    //if date is less than 2013 return closest, valid date
                    "2013-04-01"
                } else {
                    //if date is equal to 2013
                    return if(text.substring(5..6).toInt() < 4) {
                        //if month is less than april change it to april
                        "2013-04-01"
                    } else {
                        //if month is greater than april or equal to april
                        val newDate = "2013-" + text.substring(5..6).toInt() + "-" +
                                text.substring(8..9).toInt()

                        //Handle exception for chosen date landed on the extra day in a leap year
                        try {
                            //wasn't a leap year date
                            date.parse(newDate)
                            newDate
                        } catch (e: Exception) {
                            //was a leap year
                            "2013-" + text.substring(5..6).toInt() + "-" +
                                    text.substring(8..9).toInt().minus(1)
                        }
                    }
                }
            }

            //if date fell into a date usable by api return it
            text
        } catch (e:Exception) {
            null
        }
    }
}
