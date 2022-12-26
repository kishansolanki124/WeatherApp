package com.ks.weatherapp.network

import com.ks.weatherapp.pojo.WeatherTodayResponse
import com.ks.weatherapp.utils.AppConstants
import retrofit2.http.GET
import retrofit2.http.Query

interface APIEndPointsInterface {

    @GET(AppConstants.APIEndPoints.WEATHER)
    suspend fun weather(
        @Query(AppConstants.RequestParameters.query) query: String,
        @Query(AppConstants.RequestParameters.units) units: String = "metric",
        @Query(AppConstants.RequestParameters.appid) appId: String = "65002d69cfaa634d47485d12984deeae"
    ): WeatherTodayResponse
}