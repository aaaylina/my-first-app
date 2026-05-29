package com.example.myfirstapp.data.repository

import com.example.myfirstapp.BuildConfig
import com.example.myfirstapp.data.api.WeatherApi
import com.example.myfirstapp.data.cache.CacheEntry
import com.example.myfirstapp.data.cache.WeatherCacheDao
import com.example.myfirstapp.data.mapper.WeatherMapper
import com.example.myfirstapp.domain.models.Weather
import com.example.myfirstapp.domain.repository.IWeatherRepository
import com.example.myfirstapp.utils.NetworkResult
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi,
    private val cacheDao: WeatherCacheDao,
    private val mapper: WeatherMapper,
) : IWeatherRepository {

    private val apiKey = BuildConfig.OPENWEATHER_API_KEY
    private val gson = Gson()
    private var requestCount = 0

    override suspend fun getWeather(city: String, forceRefresh: Boolean): NetworkResult<Weather> {
        requestCount++

        if (requestCount % 8 == 0) {
            return NetworkResult.Error(RepositoryConstants.ERROR_DEMO_MESSAGE)
        }

        return try {
            if (!forceRefresh) {
                val cached = cacheDao.getWeather(city.lowercase())
                if (cached != null && !isCacheExpired(cached.timestamp)) {
                    val weather = gson.fromJson(cached.weatherData, Weather::class.java)
                    return NetworkResult.Success(weather.copy(fromCache = true), fromCache = true)
                }
            }
            val response = api.getCurrentWeather(city, apiKey)
            val weather = mapper.mapToDomain(response)

            cacheDao.insertWeather(
                CacheEntry(
                    city = city.lowercase(),
                    weatherData = gson.toJson(weather),
                    timestamp = System.currentTimeMillis()
                )
            )

            NetworkResult.Success(weather, fromCache = false)
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: RepositoryConstants.ERROR_UNKNOWN_MESSAGE)
        }
    }

    private fun isCacheExpired(timestamp: Long): Boolean {
        val fiveMinutes = 5 * 60 * 1000L
        return System.currentTimeMillis() > timestamp + fiveMinutes
    }

}