package com.example.myfirstapp.navScreens.coroutines

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.myfirstapp.R
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.myfirstapp.components.coroutines.*
import com.example.myfirstapp.manager.CoroutineManager
import com.example.myfirstapp.utils.*
import com.example.myfirstapp.model.*
import kotlin.coroutines.cancellation.CancellationException

@Composable
fun CoroutinesManagerScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineManager = remember { CoroutineManager() }
    val stringProvider = remember { StringProvider(context) }

    var coroutineCount by remember { mutableStateOf(10f) }
    var selectedDispatcher by remember { mutableStateOf(DispatcherType.DEFAULT) }
    var sequential by remember { mutableStateOf(true) }
    var lazyLaunch by remember { mutableStateOf(false) }
    var backgroundWork by remember { mutableStateOf(true) }
    var isRunning by remember { mutableStateOf(false) }
    var cancelledCount by remember { mutableStateOf(0) }
    var expanded by remember { mutableStateOf(false) }

    var wasCancelledByBackground by remember { mutableStateOf(false) }
    var pendingCoroutines by remember { mutableStateOf(0) }

    val snackbarHostState = remember { SnackbarHostState() }

    val config = CoroutineConfig(
        count = coroutineCount.toInt(),
        dispatcherType = selectedDispatcher,
        executionMode = if (sequential) ExecutionMode.SEQUENTIAL else ExecutionMode.PARALLEL,
        launchMode = if (lazyLaunch) LaunchMode.LAZY else LaunchMode.IMMEDIATE,
        backgroundWork = backgroundWork
    )

    val onLaunchCoroutines: () -> Unit = {
        isRunning = true
        cancelledCount = 0

        scope.launch {
            var completedCount = 0

            try {
                coroutineManager.launchCoroutines(this, config) { result ->
                    when (result) {
                        is CoroutineResult.Success -> {
                            completedCount++
                        }
                        is CoroutineResult.ExceptionResult -> {
                            completedCount++

                            scope.launch {
                                when (result.type) {
                                    ExceptionType.TOAST -> {
                                        val message = stringProvider.getExceptionMessage(result.type, result.delaySeconds)
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    }
                                    ExceptionType.SNACKBAR -> {
                                        val message = stringProvider.getExceptionMessage(result.type, result.delaySeconds)
                                        snackbarHostState.showSnackbar(message)
                                    }
                                    ExceptionType.RESET -> {
                                        val message = stringProvider.getExceptionMessage(result.type, result.delaySeconds)
                                        snackbarHostState.showSnackbar(message)

                                        coroutineCount = 10f
                                        selectedDispatcher = DispatcherType.DEFAULT
                                        sequential = true
                                        lazyLaunch = false
                                        backgroundWork = true
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: CancellationException) {

            } catch (e: Exception) {

            } finally {
                isRunning = false

                if (!isRunning) {
                    val message = stringProvider.getCoroutinesCompletedMessage(completedCount)
                    snackbarHostState.showSnackbar(message)
                }
            }
        }
    }

    val onCancelCoroutines: () -> Unit = {
        isRunning = false

        scope.launch {
            val cancelled = coroutineManager.cancelAllCoroutines()
            cancelledCount = cancelled
            val message = stringProvider.getCoroutinesCancelledMessage(cancelledCount)

            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(lifecycleOwner, backgroundWork) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    if (!backgroundWork && isRunning) {
                        wasCancelledByBackground = true
                        pendingCoroutines = coroutineManager.getActiveCoroutinesCount()
                        coroutineManager.cancelAllCoroutines()
                        isRunning = false

                        scope.launch {
                            val message = stringProvider.getCoroutinesPausedMessage()
                            snackbarHostState.showSnackbar(message)
                        }
                    }
                }
                Lifecycle.Event.ON_RESUME -> {
                    if (wasCancelledByBackground && pendingCoroutines > 0) {
                        wasCancelledByBackground = false
                        scope.launch {
                            val message = stringProvider.getCoroutinesRestartingMessage(pendingCoroutines)
                            snackbarHostState.showSnackbar(message)
                            coroutineCount = pendingCoroutines.toFloat()
                            pendingCoroutines = 0
                            onLaunchCoroutines()
                        }
                    }
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.coroutines_manager_title),
                style = MaterialTheme.typography.headlineMedium
            )

            CoroutineCountSlider(
                coroutineCount = coroutineCount,
                onCoroutineCountChange = { coroutineCount = it }
            )

            DispatcherDropdown(
                selectedDispatcher = selectedDispatcher,
                onDispatcherSelected = { selectedDispatcher = it },
                expanded = expanded,
                onExpandedChange = { expanded = it }
            )

            SettingsSwitches(
                sequential = sequential,
                onSequentialChange = { sequential = it },
                lazyLaunch = lazyLaunch,
                onLazyLaunchChange = { lazyLaunch = it },
                backgroundWork = backgroundWork,
                onBackgroundWorkChange = { backgroundWork = it }
            )

            ActionButton(
                isRunning = isRunning,
                onLaunchClick = onLaunchCoroutines,
                onCancelClick = onCancelCoroutines
            )

            StatusInfo(cancelledCount = cancelledCount)

            DebugInfo(
                isRunning = isRunning,
                coroutineCount = coroutineCount.toInt(),
                selectedDispatcher = selectedDispatcher,
                executionMode = config.executionMode,
                launchMode = config.launchMode,
                backgroundWork = backgroundWork
            )
        }
    }
}