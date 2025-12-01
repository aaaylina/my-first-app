package com.example.myfirstapp.components.coroutines

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.myfirstapp.R

@Composable
fun CoroutineCountSlider(
    coroutineCount: Float,
    onCoroutineCountChange: (Float) -> Unit
) {
    Column {
        Text(stringResource(R.string.coroutines_count, coroutineCount.toInt()))
        Slider(
            value = coroutineCount,
            onValueChange = onCoroutineCountChange,
            valueRange = 10f..100f,
            steps = 17
        )
    }
}