package com.example.myfirstapp.data.cache

import androidx.room.*

@Entity(tableName = "weather_cache")
data class CacheEntry(
    @PrimaryKey
    val city: String,
    val weatherData: String,
    val timestamp: Long
)

@Dao
interface WeatherCacheDao {
    @Query("SELECT * FROM weather_cache WHERE city = :city")
    suspend fun getWeather(city: String): CacheEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(entry: CacheEntry)

    @Query("DELETE FROM weather_cache WHERE city = :city")
    suspend fun deleteWeather(city: String)
}

