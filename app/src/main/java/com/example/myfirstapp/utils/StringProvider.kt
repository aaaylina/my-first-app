package com.example.myfirstapp.utils

import android.content.Context
import androidx.annotation.StringRes
import com.example.myfirstapp.R
import com.example.myfirstapp.model.DispatcherType
import com.example.myfirstapp.model.ExceptionType

class StringProvider(private val context: Context) {

    fun getDispatcherName(dispatcherType: DispatcherType): String {
        return when (dispatcherType) {
            DispatcherType.DEFAULT -> getString(R.string.dispatcher_default)
            DispatcherType.IO -> getString(R.string.dispatcher_io)
            DispatcherType.MAIN -> getString(R.string.dispatcher_main)
        }
    }

    fun getExceptionMessage(exceptionType: ExceptionType, delaySeconds: Long): String {
        return when (exceptionType) {
            ExceptionType.TOAST -> getString(R.string.exception_toast, delaySeconds)
            ExceptionType.SNACKBAR -> getString(R.string.exception_snackbar, delaySeconds)
            ExceptionType.RESET -> getString(R.string.exception_reset, delaySeconds)
        }
    }

    fun getErrorMessage(exception: Throwable): String {
        return getString(
            R.string.exception_error,
            exception.message ?: getString(R.string.exception_unknown_error)
        )
    }

    fun getCoroutinesCompletedMessage(count: Int): String {
        return getString(R.string.coroutines_completed, count)
    }

    fun getCoroutinesCancelledMessage(count: Int): String {
        return getString(R.string.coroutines_cancelled, count)
    }

    fun getCoroutinesPausedMessage(): String {
        return getString(R.string.coroutines_paused_background)
    }

    fun getCoroutinesRestartingMessage(count: Int): String {
        return getString(R.string.coroutines_restarting, count)
    }

    private fun getString(@StringRes resId: Int): String {
        return context.getString(resId)
    }

    private fun getString(@StringRes resId: Int, vararg args: Any): String {
        return context.getString(resId, *args)
    }
}