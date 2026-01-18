package com.example.offgrid.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val OffgridDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color.Black,

    secondary = AccentBlue,
    onSecondary = Color.White,

    background = DarkBackground,
    onBackground = LightText,

    surface = DarkSurface,
    onSurface = LightText,
    onSurfaceVariant = MutedText,

    outline = DarkOutline
)

@Composable
fun OFFGRIDTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = OffgridDarkColorScheme,
        typography = Typography,
        content = content
    )
}
