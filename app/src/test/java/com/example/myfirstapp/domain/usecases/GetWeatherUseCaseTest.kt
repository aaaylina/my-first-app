package com.example.myfirstapp.domain.usecases

import com.example.myfirstapp.domain.models.Weather
import com.example.myfirstapp.domain.repository.IWeatherRepository
import com.example.myfirstapp.utils.NetworkResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import com.google.common.truth.Truth.assertThat

@OptIn(ExperimentalCoroutinesApi::class)
class GetWeatherUseCaseTest {

    private lateinit var repository: IWeatherRepository
    private lateinit var getWeatherUseCase: GetWeatherUseCase

    private val testCity = "London"
    private val testWeather = Weather(
        city = "London",
        country = "GB",
        temperature = 20.0,
        feelsLike = 19.0,
        humidity = 65,
        pressure = 1013,
        windSpeed = 5.0,
        description = "clear sky",
        iconCode = "01d",
        sunrise = 1640000000,
        sunset = 1640040000,
        timestamp = 1640020000,
        fromCache = false
    )

    @Before
    fun setUp() {
        repository = mockk()
        getWeatherUseCase = GetWeatherUseCase(repository)
    }

    @Test
    fun `Вызов useCase должен вызвать репозиторий один раз и вернуть успешный результат для валидного города`() = runTest {
        coEvery { repository.getWeather(testCity, false) } returns NetworkResult.Success(testWeather)

        val result = getWeatherUseCase(testCity)

        coVerify(exactly = 1) { repository.getWeather(testCity, false) }
        assertThat(result).isInstanceOf(NetworkResult.Success::class.java)
        val successResult = result as NetworkResult.Success
        assertThat(successResult.data.city).isEqualTo(testCity)
        assertThat(successResult.data.temperature).isEqualTo(20.0)
        assertThat(successResult.data.country).isEqualTo("GB")
    }

    @Test
    fun `Вызов useCase должен вернуть ошибку когда репозиторий возвращает ошибку`() = runTest {
        val errorMessage = "Network error occurred"
        coEvery { repository.getWeather(testCity, false) } returns NetworkResult.Error(errorMessage)

        val result = getWeatherUseCase(testCity)

        coVerify(exactly = 1) { repository.getWeather(testCity, false) }
        assertThat(result).isInstanceOf(NetworkResult.Error::class.java)
        val errorResult = result as NetworkResult.Error
        assertThat(errorResult.message).isEqualTo(errorMessage)
    }
}