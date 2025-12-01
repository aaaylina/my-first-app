package com.example.myfirstapp.components.coroutines

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.R

@Composable
fun ActionButton(
    isRunning: Boolean,
    onLaunchClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (isRunning) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator()
                Button(
                    onClick = onCancelClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.cancel_coroutines))
                }
            }
        } else {
            Button(
                onClick = onLaunchClick,
                enabled = !isRunning
            ) {
                Text(stringResource(R.string.launch_coroutines))
            }
        }
    }
}