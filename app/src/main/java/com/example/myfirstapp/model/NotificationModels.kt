package com.example.myfirstapp.model

data class NotificationData(
    val title: String = "",
    val message: String = "",
    val isExpandable: Boolean = false,
    val priority: NotificationPriority = NotificationPriority.MEDIUM,
    val shouldOpenApp: Boolean = false,
    val hasReplyAction: Boolean = false,
    val notificationId: Int = System.currentTimeMillis().toInt()
)

enum class NotificationPriority {
    MIN, LOW, MEDIUM, HIGH
}

data class UserMessage(
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFromNotification: Boolean = false
)