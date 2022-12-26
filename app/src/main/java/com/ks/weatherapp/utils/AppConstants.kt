package com.ks.weatherapp.utils

interface AppConstants {

    interface APIEndPoints {
        companion object {
            const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

            const val WEATHER: String = "weather"
        }
    }

    interface RequestParameters {
        companion object {
            const val units = "units"
            const val query = "q"
            const val appid = "appid"
        }
    }
}
