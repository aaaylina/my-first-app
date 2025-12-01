package com.example.myfirstapp.manager

import com.example.myfirstapp.model.*
import kotlinx.coroutines.*
import kotlin.random.Random

class CoroutineManager {

    private var parentJob: Job? = null
    private var shouldStop = false
    private var totalCoroutinesCount = 0
    private var completedCoroutinesCount = 0
    private val lock = Any()

    companion object {
        private const val MIN_DELAY_MS = 1000L
        private const val MAX_DELAY_MS = 10000L
        private const val CANCELLATION_CHECK_INTERVAL_MS = 100L
        private const val EXCEPTION_THRESHOLD_MS = 7000L
        private const val EXCEPTION_PROBABILITY = 0.3
    }

    suspend fun launchCoroutines(
        scope: CoroutineScope,
        config: CoroutineConfig,
        onEachCoroutineComplete: (CoroutineResult) -> Unit
    ) {
        shouldStop = false
        synchronized(lock) {
            totalCoroutinesCount = config.count
            completedCoroutinesCount = 0
        }

        parentJob?.cancel()

        parentJob = scope.launch {
            try {
                if (config.executionMode == ExecutionMode.SEQUENTIAL) {
                    launchSequential(this, config, onEachCoroutineComplete)
                } else {
                    launchParallel(this, config, onEachCoroutineComplete)
                }
            } catch (e: CancellationException) {
                throw e
            }
        }

        parentJob?.join()
    }

    fun cancelAllCoroutines(): Int {
        shouldStop = true

        val remainingCount = synchronized(lock) {
            totalCoroutinesCount - completedCoroutinesCount
        }


        parentJob?.cancel()
        return remainingCount
    }


    fun getActiveCoroutinesCount(): Int {
        return synchronized(lock) {
            totalCoroutinesCount - completedCoroutinesCount
        }
    }

    private suspend fun launchSequential(
        scope: CoroutineScope,
        config: CoroutineConfig,
        onComplete: (CoroutineResult) -> Unit
    ) {
        for (i in 0 until config.count) {
            if (shouldStop) {
                return
            }

            scope.coroutineContext.ensureActive()
            launchSingleCoroutine(scope, config, onComplete).join()
            incrementCompletedCount()
        }
    }

    private suspend fun launchParallel(
        scope: CoroutineScope,
        config: CoroutineConfig,
        onComplete: (CoroutineResult) -> Unit
    ) {
        val jobs = mutableListOf<Job>()

        for (i in 0 until config.count) {
            if (shouldStop) {
                jobs.forEach { it.cancel() }
                return
            }

            scope.coroutineContext.ensureActive()
            jobs.add(launchSingleCoroutine(scope, config, onComplete))
        }

        jobs.forEach {
            it.join()
            incrementCompletedCount()
        }
    }

    private fun launchSingleCoroutine(
        scope: CoroutineScope,
        config: CoroutineConfig,
        onComplete: (CoroutineResult) -> Unit
    ): Job {
        return scope.launch(config.dispatcherType.toDispatcher()) {
            try {
                val delayTime = Random.nextLong(MIN_DELAY_MS, MAX_DELAY_MS + 1)

                var remaining = delayTime
                while (remaining > 0 && isActive && !shouldStop) {
                    val currentDelay = minOf(CANCELLATION_CHECK_INTERVAL_MS, remaining)
                    delay(currentDelay)
                    remaining -= currentDelay
                }

                if (shouldStop || !isActive) {
                    return@launch
                }

                val result = if (shouldThrowException(delayTime)) {
                    CoroutineResult.ExceptionResult(
                        type = getRandomExceptionType(),
                        delaySeconds = delayTime / 1000
                    )
                } else {
                    CoroutineResult.Success
                }

                onComplete(result)

            } catch (e: CancellationException) {
                throw e
            }
        }
    }

    private fun incrementCompletedCount() {
        synchronized(lock) {
            completedCoroutinesCount++
        }
    }

    private fun shouldThrowException(delayTime: Long): Boolean {
        return delayTime >= EXCEPTION_THRESHOLD_MS && Random.nextDouble() < EXCEPTION_PROBABILITY
    }

    private fun getRandomExceptionType(): ExceptionType {
        return ExceptionType.entries.random()
    }

    private fun DispatcherType.toDispatcher(): CoroutineDispatcher {
        return when (this) {
            DispatcherType.IO -> Dispatchers.IO
            DispatcherType.MAIN -> Dispatchers.Main
            DispatcherType.DEFAULT -> Dispatchers.Default
        }
    }
}