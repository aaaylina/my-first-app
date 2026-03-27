package com.example.myfirstapp.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.myfirstapp.R
import com.example.myfirstapp.presentation.components.InfoRow
import com.example.myfirstapp.presentation.viewmodels.WeatherDetailsState
import com.example.myfirstapp.presentation.viewmodels.WeatherDetailsViewModel
import com.example.myfirstapp.utils.ErrorType
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherDetailsScreen(
    cityName: String,
    onBack: () -> Unit,
    viewModel: WeatherDetailsViewModel = hiltViewModel()
) {

    val errorRestoreFailed = stringResource(R.string.error_restore_data)
    val errorCityNotPassed = stringResource(R.string.error_city_not_passed)
    val errorNetwork = stringResource(R.string.error_network)
    val errorUnknown = stringResource(R.string.error_unknown)

    LaunchedEffect(cityName) {
        viewModel.loadWeatherForCity(cityName)
    }

    val cacheMessage = stringResource(R.string.cache_snackbar)

    val state by viewModel.state.collectAsStateWithLifecycle()
    var snackbarHostState by remember { mutableStateOf(SnackbarHostState()) }
    val scrollState = rememberScrollState()

    LaunchedEffect(state) {
        if (state is WeatherDetailsState.Success && (state as WeatherDetailsState.Success).fromCache) {
            snackbarHostState.showSnackbar(
                message = cacheMessage,
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.detail_weather)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    if (state is WeatherDetailsState.Success && state !is WeatherDetailsState.Loading) {
                        IconButton(onClick = { viewModel.refresh() }) {
                            Icon(Icons.Default.Refresh, null)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state) {
                is WeatherDetailsState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is WeatherDetailsState.Success -> {
                    val weather = (state as WeatherDetailsState.Success).weather
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = viewModel.getIconUrl(weather.iconCode),
                            contentDescription = weather.description,
                            modifier = Modifier.size(120.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "${weather.city}, ${weather.country}",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${weather.temperature.toInt()}°C",
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = weather.description.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                InfoRow(stringResource(R.string.feels_like), "${weather.feelsLike.toInt()}°C")
                                InfoRow(stringResource(R.string.humidity), "${weather.humidity}%")
                                InfoRow(stringResource(R.string.pressure), "${weather.pressure} гПа")
                                InfoRow(stringResource(R.string.wind_speed), "${weather.windSpeed} м/с")
                                InfoRow(stringResource(R.string.sunrise), viewModel.formatTime(weather.sunrise * 1000))
                                InfoRow(stringResource(R.string.sunset), viewModel.formatTime(weather.sunset * 1000))
                                InfoRow(stringResource(R.string.updated), viewModel.formatDateTime(weather.timestamp * 1000))
                            }
                        }

                        if (weather.fromCache) {
                            Spacer(modifier = Modifier.height(16.dp))
                            AssistChip(
                                onClick = {},
                                label = { Text(stringResource(R.string.local_cache)) },
                                enabled = false
                            )
                        }
                    }
                }

                is WeatherDetailsState.Error -> {
                    val error = state as WeatherDetailsState.Error
                    val errorMessage = when (error.errorType) {
                        ErrorType.RESTORE_FAILED -> errorRestoreFailed
                        ErrorType.CITY_NOT_PASSED -> errorCityNotPassed
                        ErrorType.NETWORK_ERROR -> errorNetwork
                        ErrorType.UNKNOWN -> errorUnknown
                        else -> errorUnknown
                    }
                    Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(stringResource(R.string.error_basic), style = MaterialTheme.typography.headlineSmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(errorMessage)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.refresh() }) {
                            Text(stringResource(R.string.to_repeat))
                        }
                    }
                }
            }
        }
    }
}