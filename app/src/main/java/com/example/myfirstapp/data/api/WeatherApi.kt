package com.example.myfirstapp.data.api

import com.example.myfirstapp.data.models.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    companion object{
        private const val METRIC = "metric"
        private const val LANGUAGE = "ru"
    }

    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = METRIC,
        @Query("lang") lang: String = LANGUAGE,
    ): WeatherResponse
}