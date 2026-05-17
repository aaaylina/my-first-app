package com.example.myfirstapp.data.mapper

import com.example.myfirstapp.data.models.WeatherResponse
import com.example.myfirstapp.domain.models.Weather
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherMapper @Inject constructor() {

    companion object{
        private const val DEFAULT_ICON_CODE = "01d"
    }

    fun mapToDomain(response: WeatherResponse): Weather {

        return Weather(
            city = response.name,
            country = response.sys.country,
            temperature = response.main.temp,
            feelsLike = response.main.feelsLike,
            humidity = response.main.humidity,
            pressure = response.main.pressure,
            windSpeed = response.wind.speed,
            description = response.weather.firstOrNull()?.description ?: "",
            iconCode = response.weather.firstOrNull()?.icon ?: DEFAULT_ICON_CODE,
            sunrise = response.sys.sunrise,
            sunset = response.sys.sunset,
            timestamp = response.timestamp
        )
    }
}