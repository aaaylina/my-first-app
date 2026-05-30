package com.example.myfirstapp.presentation.viewmodels

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfirstapp.di.navigation.WeatherNavigationArgs
import com.example.myfirstapp.domain.models.Weather
import com.example.myfirstapp.domain.usecases.GetWeatherUseCase
import com.example.myfirstapp.presentation.utils.WeatherFormatter
import com.example.myfirstapp.utils.ErrorType
import com.example.myfirstapp.utils.NetworkResult
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class WeatherDetailsState {
    object Loading : WeatherDetailsState()

    @Immutable
    data class Success(
        val weather: Weather,
        val fromCache: Boolean
    ) : WeatherDetailsState()

    @Immutable
    data class Error(val errorType: ErrorType) : WeatherDetailsState()
}

@HiltViewModel
class WeatherDetailsViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val navigationArgs: WeatherNavigationArgs,
    private val savedStateHandle: SavedStateHandle,
    private val formatter: WeatherFormatter,
) : ViewModel() {

    private val gson = Gson()
    private var currentCity: String = ""
    private val _state = MutableStateFlow<WeatherDetailsState>(WeatherDetailsState.Loading)
    val state: StateFlow<WeatherDetailsState> = _state.asStateFlow()

    init {
        restoreFullState()
    }

    fun loadWeatherFromNavigation() {
        val cityFromDi = navigationArgs.getSelectedCity()
        if (!cityFromDi.isNullOrBlank()) {
            loadWeatherForCity(cityFromDi)
        } else if (currentCity.isEmpty()) {
            restoreFullState()
        }
    }

    private fun restoreFullState() {
        val savedCity = savedStateHandle.get<String>(KEY_DETAILS_CITY)
        val cachedWeatherJson = savedStateHandle.get<String>(KEY_DETAILS_CACHED_WEATHER)

        if (cachedWeatherJson != null && savedCity != null) {
            try {
                val weather = gson.fromJson(cachedWeatherJson, Weather::class.java)
                currentCity = savedCity
                _state.value = WeatherDetailsState.Success(weather, fromCache = true)
            } catch (e: Exception) {
                _state.value = WeatherDetailsState.Error(ErrorType.RESTORE_FAILED)
            }
        } else if (savedCity != null) {
            currentCity = savedCity
            loadWeather()
        }
    }

    private fun loadWeatherForCity(cityName: String) {
        if (currentCity != cityName) {
            currentCity = cityName
            saveCityToHandle(cityName)
            loadWeather()
        }
    }

    private fun saveCityToHandle(cityName: String) {
        savedStateHandle[KEY_DETAILS_CITY] = cityName
    }

    private fun saveWeatherToHandle(weather: Weather) {
        savedStateHandle[KEY_DETAILS_CACHED_WEATHER] = gson.toJson(weather)
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
                    _state.value = WeatherDetailsState.Error(getErrorTypeFromMessage(result.message))
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

    fun refresh() {
        loadWeather(forceRefresh = true)
    }

    fun formatSunrise(weather: Weather): String = formatter.formatUnixTime(weather.sunrise)
    fun formatSunset(weather: Weather): String = formatter.formatUnixTime(weather.sunset)
    fun formatTimestamp(weather: Weather): String = formatter.formatUnixDateTime(weather.timestamp)
    fun formatTime(timestamp: Long): String = formatter.formatTime(timestamp)
    fun formatDateTime(timestamp: Long): String = formatter.formatDateTime(timestamp)
    fun getIconUrl(iconCode: String): String = "https://openweathermap.org/img/wn/${iconCode}@2x.png"

    companion object {
        private const val KEY_DETAILS_CITY = "detailsCity"
        private const val KEY_DETAILS_CACHED_WEATHER = "detailsCachedWeather"
        private const val ERROR_CODE_401 = "401"
        private const val ERROR_CODE_404 = "404"
        private const val ERROR_DEMO_MESSAGE = "демонстрация"
    }
}