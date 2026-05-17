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
import javax.inject.Inject

sealed class WeatherListState {
    object Idle : WeatherListState()
    object Loading : WeatherListState()
    data class Success(val weather: Weather, val fromCache: Boolean) : WeatherListState()
    data class Error(val errorType: ErrorType) : WeatherListState()
}

@HiltViewModel
class WeatherListViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val gson = Gson()

    private val _state = MutableStateFlow<WeatherListState>(WeatherListState.Idle)
    val state: StateFlow<WeatherListState> = _state.asStateFlow()

    private val _lastCity = MutableStateFlow<String?>(savedStateHandle["lastCity"])
    val lastCity: StateFlow<String?> = _lastCity.asStateFlow()

    init{
        restoreFullState()
    }

    private fun restoreFullState() {
        val lastCityValue: String? = savedStateHandle["lastCity"]
        val cachedWeatherJson: String? = savedStateHandle["cachedWeather"]

        if (cachedWeatherJson != null && lastCityValue != null) {
            try {
                val weather = gson.fromJson(cachedWeatherJson, Weather::class.java)
                _state.value = WeatherListState.Success(weather, fromCache = true)
                _lastCity.value = lastCityValue
            } catch (e: Exception) {
                _state.value = WeatherListState.Idle
            }
        } else if (lastCityValue != null) {
            searchWeather(lastCityValue)
        }
    }

    fun searchWeather(city: String) {
        if (city.isBlank()) {
            _state.value = WeatherListState.Error(ErrorType.EMPTY_CITY)
            return
        }

        viewModelScope.launch {
            _state.value = WeatherListState.Loading

            when (val result = getWeatherUseCase(city)) {
                is NetworkResult.Success -> {
                    _state.value = WeatherListState.Success(result.data, result.fromCache)
                    _lastCity.value = city
                    saveStateToHandle(city, result.data)
                }
                is NetworkResult.Error -> {
                    val errorType = when {
                        result.message.contains("401") -> ErrorType.NETWORK_ERROR
                        result.message.contains("404") -> ErrorType.UNKNOWN
                        result.message.contains("демонстрация") -> ErrorType.UNKNOWN
                        else -> ErrorType.UNKNOWN
                    }
                    _state.value = WeatherListState.Error(errorType)
                }
                else -> {}
            }
        }
    }

    private fun saveStateToHandle(city: String, weather: Weather) {
        savedStateHandle["lastCity"] = city

        val weatherJson = gson.toJson(weather)
        savedStateHandle["cachedWeather"] = weatherJson

        savedStateHandle["lastUpdateTime"] = System.currentTimeMillis()
    }

    fun clearError() {
        if (_state.value is WeatherListState.Error) {
            _state.value = WeatherListState.Idle
        }
    }

    fun getIconUrl(iconCode: String): String {
        return "https://openweathermap.org/img/wn/${iconCode}@2x.png"
    }
}