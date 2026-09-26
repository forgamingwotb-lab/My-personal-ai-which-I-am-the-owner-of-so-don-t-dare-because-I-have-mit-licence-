package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val JariditColorScheme = darkColorScheme(
    primary = ArcCyan,
    onPrimary = DarkVoid,
    primaryContainer = ArcCyanDark,
    onPrimaryContainer = ArcCyanLight,
    secondary = PlasmaBlue,
    onSecondary = DarkVoid,
    secondaryContainer = SurfaceCardElevated,
    onSecondaryContainer = ArcCyanLight,
    tertiary = ReactorMint,
    onTertiary = DarkVoid,
    background = DarkVoid,
    onBackground = TextPrimary,
    surface = DarkCharcoal,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceBorder,
    error = CriticalRed,
    onError = DarkVoid
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = JariditColorScheme,
        typography = Typography,
        content = content
    )
}
