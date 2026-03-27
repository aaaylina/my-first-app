package com.example.myfirstapp.domain.usecases

import com.example.myfirstapp.domain.models.Weather
import com.example.myfirstapp.domain.repository.IWeatherRepository
import com.example.myfirstapp.utils.NetworkResult
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val repository: IWeatherRepository
) {
    suspend operator fun invoke(city: String, forceRefresh: Boolean = false): NetworkResult<Weather> {
        return repository.getWeather(city, forceRefresh)
    }
}