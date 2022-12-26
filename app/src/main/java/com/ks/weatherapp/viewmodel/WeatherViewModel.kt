package com.ks.weatherapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ks.weatherapp.network.APIEndPointsInterface
import com.ks.weatherapp.network.NetworkResult
import com.ks.weatherapp.network.RetrofitFactory
import com.ks.weatherapp.pojo.WeatherTodayResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherViewModel : ViewModel() {

    var weatherTodayResponse: MutableLiveData<NetworkResult<WeatherTodayResponse>> =
        MutableLiveData()

    private var apiEndPointsInterface =
        RetrofitFactory.createService(APIEndPointsInterface::class.java)

    /**
     * Dispatchers.IO for network or disk operations that takes longer time and runs in background thread
     */
    fun weather(query: String) {
        weatherTodayResponse.value = NetworkResult.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val apiResponse = apiEndPointsInterface.weather(query)
                returnWeatherTodayResponse(NetworkResult.Success(apiResponse))
                //returnWeatherTodayResponse(apiResponse)
            } catch (e: Exception) {
                returnWeatherTodayResponse(NetworkResult.Error(e.message, null))
                e.printStackTrace()
            }
        }
    }

    private suspend fun returnWeatherTodayResponse(eMagazineResponse: NetworkResult<WeatherTodayResponse>) {
        withContext(Dispatchers.Main) {
            weatherTodayResponse.value = eMagazineResponse
        }
    }

//    fun weatherTodayResponse(): LiveData<NetworkResult<WeatherTodayResponse>> {
//        return weatherTodayResponse
//    }
}