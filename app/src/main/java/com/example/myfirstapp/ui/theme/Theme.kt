package com.example.myfirstapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.myfirstapp.model.AppTheme

private val LightPinkColorScheme = lightColorScheme(
    primary = PrimaryPink,
    secondary = SecondaryPink,
    tertiary = Pink80,
    background = BackgroundPink,
    surface = SurfacePink,
    onPrimary = OnPrimaryPink,
    onSecondary = OnSecondaryPink,
    onBackground = OnBackgroundPink,
    onSurface = OnSurfacePink,
)

private val DarkPinkColorScheme = darkColorScheme(
    primary = Pink80,
    secondary = Pink40,
    tertiary = Pink40,
    background = Color(0xFF1A1A1A),
    surface = Color(0xFF2D2D2D),
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
)

@Composable
fun AppTheme(
    appTheme: AppTheme = AppTheme.PINK,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when (appTheme) {
        AppTheme.PINK -> if (darkTheme) DarkPinkColorScheme else LightPinkColorScheme
        AppTheme.PURPLE -> if (darkTheme) darkColorScheme() else lightColorScheme()
        AppTheme.BLUE -> if (darkTheme) darkColorScheme() else lightColorScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )

}