package com.example.myfirstapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.example.myfirstapp.model.AppTheme

@Composable
fun NotesAppTheme(
    appTheme: AppTheme = AppTheme.BLUE,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = appTheme.primaryColor,
            secondary = appTheme.primaryColor.copy(alpha = 0.8f),
            background = appTheme.backgroundColor,
            surface = appTheme.surfaceColor,
        )
    } else {
        lightColorScheme(
            primary = appTheme.primaryColor,
            secondary = appTheme.primaryColor.copy(alpha = 0.8f),
            background = appTheme.backgroundColor,
            surface = appTheme.surfaceColor,
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}