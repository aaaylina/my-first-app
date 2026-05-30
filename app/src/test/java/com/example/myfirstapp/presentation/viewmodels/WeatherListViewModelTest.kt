package com.example.myfirstapp.presentation.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.example.myfirstapp.di.session.AppSessionInfo
import com.example.myfirstapp.domain.models.Weather
import com.example.myfirstapp.domain.usecases.GetWeatherUseCase
import com.example.myfirstapp.utils.ErrorType
import com.example.myfirstapp.utils.NetworkResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.google.common.truth.Truth.assertThat

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherListViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var getWeatherUseCase: GetWeatherUseCase
    private lateinit var viewModel: WeatherListViewModel

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testCity = "Moscow"
    private val testWeather = Weather(
        city = "Moscow",
        country = "RU",
        temperature = 15.0,
        feelsLike = 14.0,
        humidity = 70,
        pressure = 1015,
        windSpeed = 3.0,
        description = "cloudy",
        iconCode = "04d",
        sunrise = 1640000000,
        sunset = 1640040000,
        timestamp = 1640020000,
        fromCache = false
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getWeatherUseCase = mockk()
        val appSessionInfo = AppSessionInfo("test-session-id", "test-user-id")
        val savedStateHandle = SavedStateHandle()
        viewModel = WeatherListViewModel(getWeatherUseCase, appSessionInfo, savedStateHandle)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Поиск погоды должен обновить state с успешным результатом когда погода найдена`() = runTest {
        coEvery { getWeatherUseCase(testCity, false) } returns NetworkResult.Success(testWeather)

        viewModel.searchWeather(testCity)

        val state = viewModel.state.value
        assertThat(state).isInstanceOf(WeatherListState.Success::class.java)
        val successState = state as WeatherListState.Success
        assertThat(successState.weather.city).isEqualTo(testCity)
        assertThat(successState.weather.temperature).isEqualTo(15.0)
        assertThat(successState.fromCache).isFalse()
    }

    @Test
    fun `Поиск погоды с пустым городом должен показать ошибку пустого города`() = runTest {
        viewModel.searchWeather("")

        val state = viewModel.state.value
        assertThat(state).isInstanceOf(WeatherListState.Error::class.java)
        val errorState = state as WeatherListState.Error
        assertThat(errorState.errorType).isEqualTo(ErrorType.EMPTY_CITY)
    }

    @Test
    fun `Поиск погоды должен показать сетевую ошибку когда происходит ошибка сети`() = runTest {
        coEvery { getWeatherUseCase(testCity, false) } returns NetworkResult.Error("HTTP 401 Unauthorized")

        viewModel.searchWeather(testCity)

        val state = viewModel.state.value
        assertThat(state).isInstanceOf(WeatherListState.Error::class.java)
        val errorState = state as WeatherListState.Error
        assertThat(errorState.errorType).isEqualTo(ErrorType.NETWORK_ERROR)
    }

    @Test
    fun `Очистка ошибки должна сбросить состояние в режим ожидания`() = runTest {
        coEvery { getWeatherUseCase(testCity, false) } returns NetworkResult.Error("Error")
        viewModel.searchWeather(testCity)
        assertThat(viewModel.state.value).isInstanceOf(WeatherListState.Error::class.java)

        viewModel.clearError()

        assertThat(viewModel.state.value).isInstanceOf(WeatherListState.Idle::class.java)
    }
}