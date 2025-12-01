package com.example.myfirstapp.model

data class CoroutineConfig(
    val count: Int,
    val dispatcherType: DispatcherType,
    val executionMode: ExecutionMode,
    val launchMode: LaunchMode,
    val backgroundWork: Boolean
)

enum class DispatcherType {
    DEFAULT, IO, MAIN
}

enum class ExecutionMode {
    SEQUENTIAL, PARALLEL
}

enum class LaunchMode {
    IMMEDIATE, LAZY
}