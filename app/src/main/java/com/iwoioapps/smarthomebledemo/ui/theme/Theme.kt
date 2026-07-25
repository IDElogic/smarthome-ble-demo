package com.iwoioapps.smarthomebledemo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// The whole demo is designed dark-only
private val BleDemoColors = darkColorScheme(
    primary = Ros,
    onPrimary = AppBackground,
    secondary = Zuzmo,
    background = AppBackground,
    onBackground = Wheat,
    surface = CardDark,
    onSurface = Wheat,
    surfaceVariant = SurfaceMuted,
    error = StatusError
)


@Composable
fun SmartHomeBleDemoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BleDemoColors,
        typography = BleDemoTypography,
        content = content
    )
}