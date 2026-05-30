package com.example.myfirstapp.presentation.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.example.myfirstapp.di.navigation.WeatherNavigationArgs
import com.example.myfirstapp.domain.models.Weather
import com.example.myfirstapp.domain.usecases.GetWeatherUseCase
import com.example.myfirstapp.presentation.utils.WeatherFormatter
import com.example.myfirstapp.utils.ErrorType
import com.example.myfirstapp.utils.NetworkResult
import io.mockk.coEvery
import io.mockk.every
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
import io.mockk.coVerify

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherDetailsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var getWeatherUseCase: GetWeatherUseCase
    private lateinit var navigationArgs: WeatherNavigationArgs
    private lateinit var formatter: WeatherFormatter
    private lateinit var viewModel: WeatherDetailsViewModel

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testCity = "Berlin"
    private val testWeather = Weather(
        city = "Berlin",
        country = "DE",
        temperature = 22.0,
        feelsLike = 21.0,
        humidity = 60,
        pressure = 1012,
        windSpeed = 4.0,
        description = "sunny",
        iconCode = "01d",
        sunrise = 1640000000,
        sunset = 1640040000,
        timestamp = 1640020000,
        fromCache = false
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getWeatherUseCase = mockk()
        navigationArgs = mockk()
        formatter = WeatherFormatter()
        val savedStateHandle = SavedStateHandle()
        viewModel = WeatherDetailsViewModel(
            getWeatherUseCase,
            navigationArgs,
            savedStateHandle,
            formatter
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Загрузка погоды из навигации должна успешно загрузить и отобразить погоду`() = runTest {
        every { navigationArgs.getSelectedCity() } returns testCity
        coEvery { getWeatherUseCase(testCity, false) } returns NetworkResult.Success(testWeather)

        viewModel.loadWeatherFromNavigation()

        val state = viewModel.state.value
        assertThat(state).isInstanceOf(WeatherDetailsState.Success::class.java)
        val successState = state as WeatherDetailsState.Success
        assertThat(successState.weather.city).isEqualTo(testCity)
        assertThat(successState.weather.temperature).isEqualTo(22.0)
    }

    @Test
    fun `Загрузка погоды из навигации должна показать ошибку когда сетевой запрос не удался`() = runTest {
        every { navigationArgs.getSelectedCity() } returns testCity
        coEvery { getWeatherUseCase(testCity, false) } returns NetworkResult.Error("Network error")

        viewModel.loadWeatherFromNavigation()

        val state = viewModel.state.value
        assertThat(state).isInstanceOf(WeatherDetailsState.Error::class.java)
        val errorState = state as WeatherDetailsState.Error
        assertThat(errorState.errorType).isEqualTo(ErrorType.UNKNOWN)
    }

    @Test
    fun `Обновление должно принудительно обновить данные погоды`() = runTest {
        every { navigationArgs.getSelectedCity() } returns testCity

        coEvery { getWeatherUseCase(testCity, false) } returns NetworkResult.Success(testWeather)

        coEvery { getWeatherUseCase(testCity, true) } returns NetworkResult.Success(testWeather)

        viewModel.loadWeatherFromNavigation()
        viewModel.refresh()

        coVerify(exactly = 1) { getWeatherUseCase(testCity, true) }

        val state = viewModel.state.value
        assertThat(state).isInstanceOf(WeatherDetailsState.Success::class.java)
    }

    @Test
    fun `Форматирование времени восхода должно вернуть корректный формат времени`() {
        val result = viewModel.formatSunrise(testWeather)

        assertThat(result).matches("\\d{2}:\\d{2}")
    }
}