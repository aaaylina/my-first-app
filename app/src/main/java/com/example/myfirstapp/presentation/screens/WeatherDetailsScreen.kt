package com.example.myfirstapp.presentation.screens

import androidx.compose.foundation.ScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.myfirstapp.R
import com.example.myfirstapp.domain.models.Weather
import com.example.myfirstapp.presentation.components.InfoRow
import com.example.myfirstapp.presentation.extensions.*
import com.example.myfirstapp.presentation.viewmodels.WeatherDetailsState
import com.example.myfirstapp.presentation.viewmodels.WeatherDetailsViewModel
import com.example.myfirstapp.utils.ErrorType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherDetailsScreen(
    onBack: () -> Unit,
    viewModel: WeatherDetailsViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    val errorRestoreFailed = stringResource(R.string.error_restore_data)
    val errorCityNotPassed = stringResource(R.string.error_city_not_passed)
    val errorNetwork = stringResource(R.string.error_network)
    val errorUnknown = stringResource(R.string.error_unknown)
    val cacheMessage = stringResource(R.string.cache_snackbar)

    LaunchedEffect(Unit) {
        viewModel.loadWeatherFromNavigation()
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    val currentState = state

    LaunchedEffect(currentState) {
        when (currentState) {
            is WeatherDetailsState.Success -> {
                if (currentState.fromCache) {
                    snackbarHostState.showSnackbar(cacheMessage)
                }
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            WeatherDetailsTopBar(
                onBack = onBack,
                onRefresh = { viewModel.refresh() },
                showRefresh = state is WeatherDetailsState.Success && state !is WeatherDetailsState.Loading
            )
        }
    ) { paddingValues ->
        WeatherDetailsContent(
            modifier = Modifier.padding(paddingValues),
            state = state,
            scrollState = scrollState,
            viewModel = viewModel,
            errorRestoreFailed = errorRestoreFailed,
            errorCityNotPassed = errorCityNotPassed,
            errorNetwork = errorNetwork,
            errorUnknown = errorUnknown
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun WeatherDetailsTopBar(
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    showRefresh: Boolean
) {
    TopAppBar(
        title = { Text(stringResource(R.string.detail_weather)) },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
        },
        actions = {
            if (showRefresh) {
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Default.Refresh, null)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}

@Composable
private fun WeatherDetailsContent(
    modifier: Modifier = Modifier,
    state: WeatherDetailsState,
    scrollState: ScrollState,
    viewModel: WeatherDetailsViewModel,
    errorRestoreFailed: String,
    errorCityNotPassed: String,
    errorNetwork: String,
    errorUnknown: String
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        when (state) {
            is WeatherDetailsState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is WeatherDetailsState.Success -> {
                WeatherDetailsSuccessContent(
                    weather = state.weather,
                    scrollState = scrollState,
                    viewModel = viewModel
                )
            }

            is WeatherDetailsState.Error -> {
                val errorMessage = when (state.errorType) {
                    ErrorType.RESTORE_FAILED -> errorRestoreFailed
                    ErrorType.CITY_NOT_PASSED -> errorCityNotPassed
                    ErrorType.NETWORK_ERROR -> errorNetwork
                    else -> errorUnknown
                }
                WeatherDetailsErrorContent(
                    errorMessage = errorMessage,
                    onRefresh = { viewModel.refresh() }
                )
            }
        }
    }
}

@Composable
private fun WeatherDetailsSuccessContent(
    weather: Weather,
    scrollState: ScrollState,
    viewModel: WeatherDetailsViewModel
) {
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
            text = weather.formatLocation(),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = weather.formatTemperature(),
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = weather.formatDescription(),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        WeatherDetailsCard(weather = weather, viewModel = viewModel)

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

@Composable
private fun WeatherDetailsCard(
    weather: Weather,
    viewModel: WeatherDetailsViewModel
) {
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
            InfoRow(stringResource(R.string.feels_like), weather.formatFeelsLike())
            InfoRow(stringResource(R.string.humidity), weather.formatHumidity())
            InfoRow(stringResource(R.string.pressure), weather.formatPressure())
            InfoRow(stringResource(R.string.wind_speed), weather.formatWindSpeed())
            InfoRow(stringResource(R.string.sunrise), viewModel.formatSunrise(weather))
            InfoRow(stringResource(R.string.sunset), viewModel.formatSunset(weather))
            InfoRow(stringResource(R.string.updated), viewModel.formatTimestamp(weather))
        }
    }
}

@Composable
private fun WeatherDetailsErrorContent(
    errorMessage: String,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.error_basic),
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(errorMessage)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRefresh) {
            Text(stringResource(R.string.to_repeat))
        }
    }
}