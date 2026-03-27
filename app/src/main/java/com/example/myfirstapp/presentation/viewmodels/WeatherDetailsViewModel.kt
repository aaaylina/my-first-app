package com.example.myfirstapp.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfirstapp.domain.models.Weather
import com.example.myfirstapp.domain.usecases.GetWeatherUseCase
import com.example.myfirstapp.utils.ErrorType
import com.example.myfirstapp.utils.NetworkResult
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

sealed class WeatherDetailsState {
    object Loading : WeatherDetailsState()
    data class Success(val weather: Weather, val fromCache: Boolean) : WeatherDetailsState()
    data class Error(val errorType: ErrorType) : WeatherDetailsState()
}

@HiltViewModel
class WeatherDetailsViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val gson = Gson()

    private var currentCity: String = ""
    private val _state = MutableStateFlow<WeatherDetailsState>(WeatherDetailsState.Loading)
    val state: StateFlow<WeatherDetailsState> = _state.asStateFlow()

    init {
        restoreFullState()
    }

    private fun restoreFullState() {
        val savedCity = savedStateHandle.get<String>("detailsCity")
        val cachedWeatherJson = savedStateHandle.get<String>("detailsCachedWeather")

        if (cachedWeatherJson != null && savedCity != null) {
            try {
                val weather = gson.fromJson(cachedWeatherJson, Weather::class.java)
                currentCity = savedCity
                _state.value = WeatherDetailsState.Success(weather, fromCache = true)
            } catch (e: Exception) {
                _state.value = WeatherDetailsState.Error( ErrorType.RESTORE_FAILED)
            }
        } else if (savedCity != null) {
            currentCity = savedCity
            loadWeather()
        }
    }

    fun loadWeatherForCity(cityName: String) {
        if (currentCity != cityName) {
            currentCity = cityName
            saveCityToHandle(cityName)
            loadWeather()
        }
    }
    private fun saveCityToHandle(cityName: String) {
        savedStateHandle["detailsCity"] = cityName
    }

    private fun saveWeatherToHandle(weather: Weather) {
        val weatherJson = gson.toJson(weather)
        savedStateHandle["detailsCachedWeather"] = weatherJson
    }

    private fun loadWeather(forceRefresh: Boolean = false) {
        if (currentCity.isEmpty()) {
            _state.value = WeatherDetailsState.Error(ErrorType.CITY_NOT_PASSED)
            return
        }

        viewModelScope.launch {
            _state.value = WeatherDetailsState.Loading

            when (val result = getWeatherUseCase(currentCity, forceRefresh)) {
                is NetworkResult.Success -> {
                    _state.value = WeatherDetailsState.Success(result.data, result.fromCache)
                    saveWeatherToHandle(result.data)
                }
                is NetworkResult.Error -> {
                    val errorType = when {
                        result.message.contains("401") -> ErrorType.NETWORK_ERROR
                        result.message.contains("404") -> ErrorType.UNKNOWN
                        result.message.contains("демонстрация") -> ErrorType.UNKNOWN
                        else -> ErrorType.UNKNOWN
                    }
                    _state.value = WeatherDetailsState.Error(errorType)
                }
                else -> {}
            }
        }
    }

    fun refresh() {
        loadWeather(forceRefresh = true)
    }

    fun formatTime(timestamp: Long): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
    }

    fun formatDateTime(timestamp: Long): String {
        return SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(timestamp))
    }

    fun getIconUrl(iconCode: String): String {
        return "https://openweathermap.org/img/wn/${iconCode}@2x.png"
    }
}