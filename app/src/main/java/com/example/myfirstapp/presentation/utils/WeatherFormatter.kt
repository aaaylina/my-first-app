package com.example.myfirstapp.presentation.utils

import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherFormatter @Inject constructor() {

    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    fun formatTime(timestamp: Long): String = timeFormat.format(Date(timestamp))
    fun formatDateTime(timestamp: Long): String = dateTimeFormat.format(Date(timestamp))
    fun formatUnixTime(unixTimestamp: Long): String = formatTime(unixTimestamp * 1000)
    fun formatUnixDateTime(unixTimestamp: Long): String = formatDateTime(unixTimestamp * 1000)
}