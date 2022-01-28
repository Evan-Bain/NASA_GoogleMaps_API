package com.example.nasa_googlemaps_api_project

import java.util.*

object Globals {
    const val BASE_URL = "https://api.nasa.gov/"
    const val API_KEY = "3q2XHjcF8hQVgUtvtTXOQ2pZ8vfkV4MxcbGHyevw"

    /** returns today's date in String format **/
    fun todayDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR).toString()
        var month = (calendar.get(Calendar.MONTH) + 1).toString()
        month = if(month.length == 1) {
            "0$month"
        } else {
            month
        }

        var day = calendar.get(Calendar.DAY_OF_MONTH + 1).toString()
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
}
