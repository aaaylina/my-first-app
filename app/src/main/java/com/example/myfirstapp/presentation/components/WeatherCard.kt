package com.example.myfirstapp.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myfirstapp.R
import com.example.myfirstapp.domain.models.Weather
import com.example.myfirstapp.presentation.extensions.formatDescription
import com.example.myfirstapp.presentation.extensions.formatLocation
import com.example.myfirstapp.presentation.extensions.formatTemperature
@Composable
fun WeatherCard(
    weather: Weather,
    onClick: () -> Unit,
    getIconUrl: (String) -> String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() }.padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        WeatherCardContent(
            weather = weather,
            getIconUrl = getIconUrl
        )
    }
}

@Composable
private fun WeatherCardContent(
    weather: Weather,
    getIconUrl: (String) -> String
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = getIconUrl(weather.iconCode),
            contentDescription = weather.description,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = weather.formatLocation(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = weather.formatDescription(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (weather.fromCache) {
                Text(
                    text = stringResource(R.string.from_cache),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Text(
            text = weather.formatTemperature(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

