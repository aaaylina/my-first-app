package com.example.myfirstapp.model

sealed class CoroutineResult {
    object Success : CoroutineResult()
    data class ExceptionResult(
        val type: ExceptionType,
        val delaySeconds: Long
    ) : CoroutineResult()
}