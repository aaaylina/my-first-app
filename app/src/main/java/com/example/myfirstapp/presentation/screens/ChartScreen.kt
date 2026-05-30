package com.example.myfirstapp.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.R
import com.example.myfirstapp.presentation.components.chart.PieChart
import com.example.myfirstapp.presentation.components.chart.PieChartItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartScreen(
    onBack: () -> Unit
) {

    val items = listOf(
        PieChartItem(1, 46, Color(0xFF4A90E2)),
        PieChartItem(2, 18, Color(0xFFF5A623)),
        PieChartItem(3, 9, Color(0xFF9B9B9B)),
        PieChartItem(4, 7, Color(0xFF50E3C2)),
        PieChartItem(5, 7, Color(0xFF7ED321)),
        PieChartItem(6, 5, Color(0xFF417505)),
        PieChartItem(7, 2, Color(0xFF4A4A4A)),
        PieChartItem(8, 2, Color(0xFF9013FE)),
        PieChartItem(9, 2, Color(0xFFD0021B)),
        PieChartItem(10, 2, Color(0xFF8B572A))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.chart_screen_title))
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {

            PieChart(
                modifier = Modifier.size(320.dp),
                items = items
            )
        }
    }
}