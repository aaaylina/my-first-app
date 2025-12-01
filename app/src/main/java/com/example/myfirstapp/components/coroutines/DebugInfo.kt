package com.example.myfirstapp.components.coroutines

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.R
import com.example.myfirstapp.model.DispatcherType
import com.example.myfirstapp.model.ExecutionMode
import com.example.myfirstapp.model.LaunchMode

@Composable
fun DebugInfo(
    isRunning: Boolean,
    coroutineCount: Int,
    selectedDispatcher: DispatcherType,
    executionMode: ExecutionMode,
    launchMode: LaunchMode,
    backgroundWork: Boolean
) {
    if (isRunning) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.debug_current_settings),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                CompactDebugRow(
                    coroutineCount = coroutineCount,
                    selectedDispatcher = selectedDispatcher,
                    executionMode = executionMode
                )

                CompactDebugRow(
                    launchMode = launchMode,
                    backgroundWork = backgroundWork
                )
            }
        }
    }
}

@Composable
private fun CompactDebugRow(
    coroutineCount: Int? = null,
    selectedDispatcher: DispatcherType? = null,
    executionMode: ExecutionMode? = null,
    launchMode: LaunchMode? = null,
    backgroundWork: Boolean? = null
) {
    val parts = mutableListOf<String>()

    coroutineCount?.let {
        parts.add(stringResource(R.string.debug_coroutines_count, it))
    }

    selectedDispatcher?.let {
        parts.add(stringResource(R.string.debug_dispatcher, getDispatcherDisplayName(it)))
    }

    executionMode?.let {
        parts.add(stringResource(R.string.debug_mode, getExecutionModeDisplayName(it)))
    }

    launchMode?.let {
        parts.add(stringResource(R.string.debug_launch, getLaunchModeDisplayName(it)))
    }

    backgroundWork?.let {
        parts.add(stringResource(R.string.debug_background, getBackgroundWorkDisplayName(it)))
    }

    Text(
        text = parts.joinToString(" | "),
        style = MaterialTheme.typography.labelSmall
    )
}

@Composable
private fun getDispatcherDisplayName(dispatcher: DispatcherType): String {
    return when (dispatcher) {
        DispatcherType.DEFAULT -> stringResource(R.string.dispatcher_default)
        DispatcherType.IO -> stringResource(R.string.dispatcher_io)
        DispatcherType.MAIN -> stringResource(R.string.dispatcher_main)
    }
}

@Composable
private fun getExecutionModeDisplayName(mode: ExecutionMode): String {
    return when (mode) {
        ExecutionMode.SEQUENTIAL -> stringResource(R.string.mode_sequential)
        ExecutionMode.PARALLEL -> stringResource(R.string.mode_parallel)
    }
}

@Composable
private fun getLaunchModeDisplayName(mode: LaunchMode): String {
    return when (mode) {
        LaunchMode.IMMEDIATE -> stringResource(R.string.launch_immediate)
        LaunchMode.LAZY -> stringResource(R.string.launch_lazy)
    }
}

@Composable
private fun getBackgroundWorkDisplayName(enabled: Boolean): String {
    return if (enabled) {
        stringResource(R.string.background_enabled)
    } else {
        stringResource(R.string.background_disabled)
    }
}