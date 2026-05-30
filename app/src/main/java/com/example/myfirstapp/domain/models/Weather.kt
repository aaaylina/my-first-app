package com.example.myfirstapp.domain.models

import androidx.compose.runtime.Immutable

@Immutable
data class Weather(
    val city: String,
    val country: String,
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val pressure: Int,
    val windSpeed: Double,
    val description: String,
    val iconCode: String,
    val sunrise: Long,
    val sunset: Long,
    val timestamp: Long,
    val fromCache: Boolean = false
){
    val celsius: Int get() = temperature.toInt()
    val feelsLikeCelsius: Int get() = feelsLike.toInt()
}