package com.example.dibays.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DibaysColors = lightColorScheme(
    primary = Color(0xFF1A2734),
    onPrimary = Color.White,
    secondary = Color(0xFF8162F3),
    onSecondary = Color.White,
    background = Color(0xFFF7F8FB),
    onBackground = Color(0xFF121A24),
    surface = Color.White,
    onSurface = Color(0xFF121A24),
    surfaceVariant = Color(0xFFE9ECF2),
    onSurfaceVariant = Color(0xFF6C7480),
    error = Color(0xFFC7443F),
)

@Composable
fun DibaysTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DibaysColors,
        content = content,
    )
}
