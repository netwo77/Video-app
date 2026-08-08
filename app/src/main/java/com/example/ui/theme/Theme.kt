package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SleekBluePrimary,
    onPrimary = Color.White,
    primaryContainer = SleekCardSecondary,
    onPrimaryContainer = SleekBluePrimary,
    secondary = SleekCyanGlow,
    onSecondary = Color.Black,
    secondaryContainer = SleekCard,
    onSecondaryContainer = SleekCyanGlow,
    tertiary = SleekPurple,
    onTertiary = Color.White,
    tertiaryContainer = SleekCard,
    onTertiaryContainer = SleekPurple,
    background = SleekBgDark,
    onBackground = SleekTextPrimary,
    surface = SleekSurface,
    onSurface = SleekTextPrimary,
    surfaceVariant = SleekCard,
    onSurfaceVariant = SleekTextSecondary,
    outline = SleekBorder,
    outlineVariant = SleekBorder.copy(alpha = 0.6f)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

