package com.example.app_recordatorios.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = CreateBtn,
    secondary = AllBtn,
    tertiary = ScheduledBtn,
    primaryContainer = TodayCard,
    surfaceVariant = CompletedBox,
    background = LightBackground,
    surface = LightBackground,
    onBackground = Color.Black,
    onSurface = Color.Black
)

private val DarkColors = darkColorScheme(
    primary = DarkCreate,
    onPrimary = Color.Black,
    secondary = DarkAll,
    tertiary = DarkScheduled,
    primaryContainer = DarkToday,
    surfaceVariant = DarkCompletedBox,
    onSurfaceVariant = Color.Black,
    background = DarkBackground,
    surface = DarkBackground,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content
    )
}