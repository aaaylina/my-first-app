package com.example.myfirstapp.di.navigation

import javax.inject.Inject
import javax.inject.Singleton
@Singleton
class WeatherNavigationArgs @Inject constructor() {

    @Volatile
    private var selectedCity: String? = null

    fun setSelectedCity(cityName: String) {
        selectedCity = cityName
    }

    fun getSelectedCity(): String? = selectedCity

    fun clear() {
        selectedCity = null
    }
}
