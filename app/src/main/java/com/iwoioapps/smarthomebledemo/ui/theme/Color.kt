package com.iwoioapps.smarthomebledemo.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * "Smart home" premium dark palette
 */

// Deep charcoal app background.
val AppBackground = Color(0xFF15141A)
val AppBackgroundEnd = Color(0xFF1E1B22)

// Warm dark taupe used for the main "hero" card.
val CardDark = Color(0xFF3A342F)

// Muted graphite used for secondary surfaces / dividers.
val SurfaceMuted = Color(0xFF232228)

// Dusty rose accent - primary interactive color (switch track, slider, glow).
val Ros = Color(0xFFB68D82)

// Warm cream/lavender used for primary text & icons on dark surfaces.
val Wheat = Color(0xFFE6E8FA)

// Sage teal used for inactive slider track / secondary status.
val Zuzmo = Color(0xFF9AB5B0)

// Status colors.
val StatusReady = Color(0xFF7ED6A5)
val StatusPending = Color(0xFFE0B65C)
val StatusError = Color(0xFFE07A6B)

// Translucent overlay tints for the small stat cards.
val StatCardTintA = Color(0xFF9AB5B0).copy(alpha = 0.14f)
val StatCardTintB = Color(0xFFB68D82).copy(alpha = 0.14f)
