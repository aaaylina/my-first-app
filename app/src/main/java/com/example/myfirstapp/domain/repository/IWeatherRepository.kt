package com.example.myfirstapp.domain.repository

import com.example.myfirstapp.domain.models.Weather
import com.example.myfirstapp.utils.NetworkResult

interface IWeatherRepository {
    suspend fun getWeather(city: String, forceRefresh: Boolean = false): NetworkResult<Weather>
}