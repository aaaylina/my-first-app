package com.example.myfirstapp.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myfirstapp.R
import com.example.myfirstapp.presentation.components.WeatherCard
import com.example.myfirstapp.presentation.viewmodels.WeatherListState
import com.example.myfirstapp.presentation.viewmodels.WeatherListViewModel
import com.example.myfirstapp.utils.ErrorType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherListScreen(
    onCityClick: (String) -> Unit,
    onAboutClick: () -> Unit,
    viewModel: WeatherListViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lastCity by viewModel.lastCity.collectAsStateWithLifecycle()
    var searchText by remember { mutableStateOf("") }
    var snackbarHostState by remember { mutableStateOf(SnackbarHostState()) }

    val cacheMessage = stringResource(R.string.details_cache_snackbar)

    val errorEmptyCity = stringResource(R.string.error_empty_city)
    val errorNetwork = stringResource(R.string.error_network)
    val errorUnknown = stringResource(R.string.error_unknown)
    val errorRestoreFailed = stringResource(R.string.error_restore_data)

    LaunchedEffect(state) {
        val currentState = state
        when (currentState) {
            is WeatherListState.Error -> {
                val errorMessage = when (currentState.errorType) {
                    ErrorType.EMPTY_CITY -> errorEmptyCity
                    ErrorType.NETWORK_ERROR -> errorNetwork
                    ErrorType.RESTORE_FAILED -> errorRestoreFailed
                    ErrorType.UNKNOWN -> errorUnknown
                    else -> errorUnknown
                }
                snackbarHostState.showSnackbar(
                    message = errorMessage,
                    duration = SnackbarDuration.Long
                )
                viewModel.clearError()
            }
            is WeatherListState.Success -> {
                if (currentState.fromCache) {
                    snackbarHostState.showSnackbar(
                        message = cacheMessage,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            stringResource(R.string.weather_app),
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        )
                        Text(
                            text = stringResource(R.string.session_label, viewModel.sessionId.take(8)),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onAboutClick) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = stringResource(R.string.about_menu_content_description),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.enter_city_name)) },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                trailingIcon = {
                    if (searchText.isNotBlank()) {
                        IconButton(onClick = { searchText = "" }) {
                            Icon(Icons.Default.Close, null)
                        }
                    }
                },
                singleLine = true,
                isError = state is WeatherListState.Error
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { if (searchText.isNotBlank()) viewModel.searchWeather(searchText) },
                modifier = Modifier.fillMaxWidth(),
                enabled = searchText.isNotBlank() && state !is WeatherListState.Loading
            ) {
                if (state is WeatherListState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(stringResource(R.string.find_out_weather))
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (lastCity != null && searchText.isBlank()) {
                Text(
                    text = stringResource(R.string.last_search),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                AssistChip(
                    onClick = { viewModel.searchWeather(lastCity!!) },
                    label = { Text(lastCity!!) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (state) {
                    is WeatherListState.Idle -> {
                        Text(
                            text = stringResource(R.string.enter_city_and_tap_search),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    is WeatherListState.Loading -> {
                        CircularProgressIndicator()
                    }
                    is WeatherListState.Success -> {
                        val weather = (state as WeatherListState.Success).weather
                        WeatherCard(
                            weather = weather,
                            onClick = { onCityClick(weather.city) },
                            getIconUrl = viewModel::getIconUrl,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    is WeatherListState.Error -> {}
                }
            }
        }
    }
}