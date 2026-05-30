package com.example.myfirstapp.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
    var searchText by rememberSaveable { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    val cacheMessage = stringResource(R.string.details_cache_snackbar)
    val errorEmptyCity = stringResource(R.string.error_empty_city)
    val errorNetwork = stringResource(R.string.error_network)
    val errorRestoreFailed = stringResource(R.string.error_restore_data)

    val currentState = state

    LaunchedEffect(currentState) {
        when (currentState) {
            is WeatherListState.Success -> {
                if (currentState.fromCache) {
                    snackbarHostState.showSnackbar(cacheMessage)
                }
            }
            is WeatherListState.Error -> {
                val errorMessage = when (currentState.errorType) {
                    ErrorType.EMPTY_CITY -> errorEmptyCity
                    ErrorType.NETWORK_ERROR -> errorNetwork
                    ErrorType.RESTORE_FAILED -> errorRestoreFailed
                    else -> null
                }
                errorMessage?.let {
                    snackbarHostState.showSnackbar(it)
                    viewModel.clearError()
                }
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            WeatherListTopBar(
                sessionId = viewModel.sessionId,
                onAboutClick = onAboutClick
            )
        }
    ) { paddingValues ->
        WeatherListContent(
            modifier = Modifier.padding(paddingValues),
            state = state,
            lastCity = lastCity,
            searchText = searchText,
            onSearchTextChange = { searchText = it },
            onSearchClick = { if (searchText.isNotBlank()) viewModel.searchWeather(searchText) },
            onLastCityClick = { viewModel.searchWeather(it) },
            onCityClick = onCityClick,
            getIconUrl = viewModel::getIconUrl
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun WeatherListTopBar(
    sessionId: String,
    onAboutClick: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    stringResource(R.string.weather_app),
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(R.string.session_label, sessionId.take(8)),
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

@Composable
private fun WeatherListContent(
    modifier: Modifier = Modifier,
    state: WeatherListState,
    lastCity: String?,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onLastCityClick: (String) -> Unit,
    onCityClick: (String) -> Unit,
    getIconUrl: (String) -> String
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SearchTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            isError = state is WeatherListState.Error
        )

        Spacer(modifier = Modifier.height(16.dp))

        SearchButton(
            isLoading = state is WeatherListState.Loading,
            isEnabled = searchText.isNotBlank() && state !is WeatherListState.Loading,
            onClick = onSearchClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (lastCity != null && searchText.isBlank()) {
            LastSearchSection(
                lastCity = lastCity,
                onLastCityClick = onLastCityClick
            )
        }

        WeatherContentBox(
            state = state,
            onCityClick = onCityClick,
            getIconUrl = getIconUrl
        )
    }
}

@Composable
private fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(stringResource(R.string.enter_city_name)) },
        leadingIcon = { Icon(Icons.Default.Search, null) },
        trailingIcon = {
            if (value.isNotBlank()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(Icons.Default.Close, null)
                }
            }
        },
        singleLine = true,
        isError = isError
    )
}

@Composable
private fun SearchButton(
    isLoading: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = isEnabled
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(stringResource(R.string.find_out_weather))
    }
}

@Composable
private fun LastSearchSection(
    lastCity: String,
    onLastCityClick: (String) -> Unit
) {
    Text(
        text = stringResource(R.string.last_search),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(8.dp))
    AssistChip(
        onClick = { onLastCityClick(lastCity) },
        label = { Text(lastCity) }
    )
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun WeatherContentBox(
    state: WeatherListState,
    onCityClick: (String) -> Unit,
    getIconUrl: (String) -> String
) {
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
                WeatherCard(
                    weather = state.weather,
                    onClick = { onCityClick(state.weather.city) },
                    getIconUrl = getIconUrl,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            is WeatherListState.Error -> {
            }
        }
    }
}