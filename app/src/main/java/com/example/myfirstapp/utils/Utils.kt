package com.example.myfirstapp.utils

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T, val fromCache: Boolean = false) : NetworkResult<T>()
    data class Error(val message: String, val code: Int? = null) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
    object Idle : NetworkResult<Nothing>()
}