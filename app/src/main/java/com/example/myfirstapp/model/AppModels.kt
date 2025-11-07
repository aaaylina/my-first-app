package com.example.myfirstapp.model

import androidx.annotation.StringRes
import com.example.myfirstapp.R

data class Note(
    val id: Int,
    val title: String,
    val content: String
)

enum class AppTheme(
    val primaryColor: androidx.compose.ui.graphics.Color,
    val backgroundColor: androidx.compose.ui.graphics.Color,
    val surfaceColor: androidx.compose.ui.graphics.Color,
    @StringRes val displayNameRes: Int
) {
    PINK(
        primaryColor = androidx.compose.ui.graphics.Color(0xFFC2185B),
        backgroundColor = androidx.compose.ui.graphics.Color(0xFFFCE4EC),
        surfaceColor = androidx.compose.ui.graphics.Color(0xFFF8BBD0),
        displayNameRes = R.string.theme_pink
    ),
    PURPLE(
        primaryColor = androidx.compose.ui.graphics.Color(0xFF9C27B0),
        backgroundColor = androidx.compose.ui.graphics.Color(0xFFF3E5F5),
        surfaceColor = androidx.compose.ui.graphics.Color(0xFFE1BEE7),
        displayNameRes = R.string.theme_purple
    ),
    BLUE(
        primaryColor = androidx.compose.ui.graphics.Color(0xFF03A9F4),
        backgroundColor = androidx.compose.ui.graphics.Color(0xFFE1F5FE),
        surfaceColor = androidx.compose.ui.graphics.Color(0xFFB3E5FC),
        displayNameRes = R.string.theme_blue
    )
}

data class AppState(
    val userEmail: String = "",
    val notes: List<Note> = emptyList(),
    val selectedTheme: AppTheme = AppTheme.PINK,
    val userMessages: List<UserMessage> = emptyList()
) {
    fun login(email: String): AppState = copy(userEmail = email)
    fun addNote(title: String, content: String): AppState {
        val newNote = Note(
            id = notes.size + 1,
            title = title,
            content = content
        )
        return copy(notes = notes + newNote)
    }

    fun changeTheme(theme: AppTheme): AppState = copy(selectedTheme = theme)

    fun addUserMessage(text: String, isFromNotification: Boolean = false): AppState {
        val newMessage = UserMessage(text, System.currentTimeMillis(), isFromNotification)
        return copy(userMessages = userMessages + newMessage)
    }

    fun clearMessages(): AppState = copy(userMessages = emptyList())
}