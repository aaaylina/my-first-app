package com.example.myfirstapp.data.cache

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CacheEntry::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherCacheDao(): WeatherCacheDao
}