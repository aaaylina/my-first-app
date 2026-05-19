package com.example.myfirstapp.presentation.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfirstapp.di.session.AppSessionInfo
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
    appSessionInfo: AppSessionInfo,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val gson = Gson()

    val sessionId: String = appSessionInfo.sessionId

    private val _state = MutableStateFlow<WeatherListState>(WeatherListState.Idle)
    val state: StateFlow<WeatherListState> = _state.asStateFlow()

    private val _lastCity = MutableStateFlow<String?>(savedStateHandle[KEY_LAST_CITY])
    val lastCity: StateFlow<String?> = _lastCity.asStateFlow()

    init{
        restoreFullState()
    }

    private fun restoreFullState() {
        val lastCityValue: String? = savedStateHandle[KEY_LAST_CITY]
        val cachedWeatherJson: String? = savedStateHandle[KEY_CACHED_WEATHER]

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
                    val errorType = getErrorTypeFromMessage(result.message)
                    _state.value = WeatherListState.Error(errorType)
                }
                else -> {}
            }
        }
    }

    private fun getErrorTypeFromMessage(message: String): ErrorType {
        return when {
            message.contains(ERROR_CODE_401) -> ErrorType.NETWORK_ERROR
            message.contains(ERROR_CODE_404) -> ErrorType.UNKNOWN
            message.contains(ERROR_DEMO_MESSAGE) -> ErrorType.UNKNOWN
            else -> ErrorType.UNKNOWN
        }
    }

    private fun saveStateToHandle(city: String, weather: Weather) {
        savedStateHandle[KEY_LAST_CITY] = city

        val weatherJson = gson.toJson(weather)
        savedStateHandle[KEY_CACHED_WEATHER] = weatherJson

        savedStateHandle[KEY_LAST_UPDATE_TIME] = System.currentTimeMillis()
    }

    fun clearError() {
        if (_state.value is WeatherListState.Error) {
            _state.value = WeatherListState.Idle
        }
    }

    fun getIconUrl(iconCode: String): String {
        return "https://openweathermap.org/img/wn/${iconCode}@2x.png"
    }

    companion object {
        private const val KEY_LAST_CITY = "lastCity"
        private const val KEY_CACHED_WEATHER = "cachedWeather"
        private const val KEY_LAST_UPDATE_TIME = "lastUpdateTime"

        private const val ERROR_CODE_401 = "401"
        private const val ERROR_CODE_404 = "404"
        private const val ERROR_DEMO_MESSAGE = "демонстрация"
    }
}