package com.ks.weatherapp.network

import android.util.Log
import com.ks.weatherapp.utils.AppConstants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit

object RetrofitFactory {

    private val retrofit = Retrofit.Builder()
        .baseUrl(AppConstants.APIEndPoints.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(
            OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(
                    object : Interceptor {
                        override fun intercept(chain: Interceptor.Chain): Response {
                            val request: Request = chain.request()
                                .newBuilder()
                                .build()
                            return chain.proceed(request)
                        }
                    }
                )
                .addNetworkInterceptor(
                    Interceptor { chain ->
                        val original = chain.request()
                        val requestBuilder = original.newBuilder()
                        val request = requestBuilder.build()
                        val response = chain.proceed(request)
                        Log.e("Request Headers", request.headers.toString())
                        Log.e("Response Body", response.body.toString())
                        when (response.code) {
                            HttpURLConnection.HTTP_OK -> return@Interceptor response
                            HttpURLConnection.HTTP_NOT_FOUND -> return@Interceptor response

                        }
                        response
                    }
                )
                .build()
        )
        .build()

    @JvmStatic
    fun <S> createService(serviceClass: Class<S>): S {
        return retrofit.create(serviceClass)
    }
}
