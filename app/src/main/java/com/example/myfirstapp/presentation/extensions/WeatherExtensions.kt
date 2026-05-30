package com.example.myfirstapp.presentation.extensions

import com.example.myfirstapp.domain.models.Weather
import java.util.*

fun Weather.formatTemperature(): String = "${celsius}°C"
fun Weather.formatFeelsLike(): String = "${feelsLikeCelsius}°C"
fun Weather.formatWindSpeed(): String = String.format("%.1f м/с", windSpeed)
fun Weather.formatPressure(): String = "$pressure гПа"
fun Weather.formatHumidity(): String = "$humidity%"
fun Weather.formatDescription(): String = description.replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
}
fun Weather.formatLocation(): String = "$city, $country"