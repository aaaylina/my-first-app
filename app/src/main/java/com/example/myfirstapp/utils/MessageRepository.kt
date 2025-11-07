package com.example.myfirstapp.utils

import com.example.myfirstapp.model.UserMessage
import androidx.compose.runtime.mutableStateListOf

object MessageRepository {
    private val _messages = mutableStateListOf<UserMessage>()
    val messages: List<UserMessage>
        get() = _messages.sortedBy { it.timestamp }

    fun addMessage(text: String, isFromNotification: Boolean = false) {
        _messages.add(UserMessage(text, System.currentTimeMillis(), isFromNotification))
    }

    fun clearMessages() {
        _messages.clear()
    }
}