package com.example.myfirstapp.components.coroutines

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.myfirstapp.R

@Composable
fun StatusInfo(cancelledCount: Int) {
    if (cancelledCount > 0) {
        Text(
            text = stringResource(R.string.cancelled_coroutines, cancelledCount),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}