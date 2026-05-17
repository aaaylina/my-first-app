package com.example.myfirstapp.di

import android.content.Context
import androidx.room.Room
import com.example.myfirstapp.data.api.WeatherApi
import com.example.myfirstapp.data.cache.WeatherCacheDao
import com.example.myfirstapp.data.cache.WeatherDatabase
import com.example.myfirstapp.data.mapper.WeatherMapper
import com.example.myfirstapp.data.repository.WeatherRepositoryImpl
import com.example.myfirstapp.domain.repository.IWeatherRepository
import com.example.myfirstapp.domain.usecases.GetWeatherUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideWeatherApi(): WeatherApi {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }

    @Provides
    @Singleton
    fun provideWeatherDatabase(@ApplicationContext context: Context): WeatherDatabase {
        return Room.databaseBuilder(
            context,
            WeatherDatabase::class.java,
            "weather_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideWeatherCacheDao(database: WeatherDatabase): WeatherCacheDao {
        return database.weatherCacheDao()
    }


    @Provides
    @Singleton
    fun provideWeatherRepository(
        api: WeatherApi,
        cacheDao: WeatherCacheDao,
        mapper: WeatherMapper
    ): IWeatherRepository {
        return WeatherRepositoryImpl(api, cacheDao, mapper)
    }

    @Provides
    @Singleton
    fun provideGetWeatherUseCase(
        repository: IWeatherRepository
    ): GetWeatherUseCase {
        return GetWeatherUseCase(repository)
    }
}