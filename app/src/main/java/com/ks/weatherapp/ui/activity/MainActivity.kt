package com.ks.weatherapp.ui.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.ks.weatherapp.R
import com.ks.weatherapp.databinding.ActivityMainBinding
import com.ks.weatherapp.network.NetworkResult
import com.ks.weatherapp.pojo.WeatherTodayResponse
import com.ks.weatherapp.utils.getCurrentDate
import com.ks.weatherapp.utils.hideKeyboard
import com.ks.weatherapp.utils.isConnected
import com.ks.weatherapp.utils.showSnackBar
import com.ks.weatherapp.viewmodel.WeatherViewModel
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var weatherViewModel: WeatherViewModel

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViews()
        initViewModel()
    }

    private fun initViews() {
        binding.tvDate.text = getCurrentDate()

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchWeather()
            }
            true
        }

        binding.btSearch.setOnClickListener {
            searchWeather()
        }
    }

    private fun searchWeather() {
        hideKeyboard(this)
        if (!TextUtils.isEmpty(binding.etSearch.text.toString())) {
            if (isConnected(this)) {
                //binding.pbSearch.visibility = View.VISIBLE
                weatherViewModel.weather(
                    binding.etSearch.text.toString()
                )
            } else {
                showSnackBar(getString(R.string.no_internet), this)
            }
        } else {
            showSnackBar(getString(R.string.invalid_input), this)
        }
    }

    private fun initViewModel() {
        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        weatherViewModel.weatherTodayResponse.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let {
                        binding.pbSearch.visibility = View.INVISIBLE
                        handleResponse(it)
                    }
                }
                is NetworkResult.Error -> {
                    binding.pbSearch.visibility = View.INVISIBLE
                    showSnackBar(getString(R.string.city_not_found), this)
                }

                is NetworkResult.Loading -> {
                    binding.pbSearch.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun handleResponse(weatherTodayResponse: WeatherTodayResponse?) {
        binding.pbSearch.visibility = View.INVISIBLE
        if (null != weatherTodayResponse) {
            if (weatherTodayResponse.cod != 200) {
                showSnackBar(weatherTodayResponse.message, this)
                return
            }

            Glide.with(binding.ivWeatherIc.context).load(
                "http://openweathermap.org/img/wn/" + weatherTodayResponse.weather[0].icon + "@2x.png"
            ).into(binding.ivWeatherIc)

            binding.tvLocation.text = getString(
                R.string.city_country, weatherTodayResponse.name, weatherTodayResponse.sys.country
            )

            binding.tvTemp.text =
                String.format(Locale.getDefault(), "%.0f°C", weatherTodayResponse.main.temp)

            binding.tvTempFeels.text =
                getString(R.string.feels_like, weatherTodayResponse.main.temp.toString())
        }
    }
}