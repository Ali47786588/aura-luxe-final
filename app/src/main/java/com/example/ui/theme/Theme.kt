package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AuraDarkColorScheme = darkColorScheme(
    primary = GoldChampagne,
    onPrimary = ObsidianDark,
    primaryContainer = GoldChampagneDark,
    onPrimaryContainer = GoldChampagneLight,
    secondary = GoldChampagneLight,
    onSecondary = ObsidianDark,
    background = ObsidianDark,
    onBackground = TextPrimaryDark,
    surface = ObsidianSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = ObsidianCard,
    onSurfaceVariant = TextSecondaryDark,
    outline = ObsidianBorder
)

private val AuraLightColorScheme = lightColorScheme(
    primary = GoldChampagneDark,
    onPrimary = White,
    primaryContainer = GoldChampagneLight,
    onPrimaryContainer = ObsidianDark,
    secondary = ObsidianDark,
    onSecondary = White,
    background = White,
    onBackground = ObsidianDark,
    surface = Color(0xFFF9FAFB),
    onSurface = ObsidianDark,
    surfaceVariant = Color(0xFFF3F4F6),
    onSurfaceVariant = Color(0xFF4B5563),
    outline = Color(0xFFE5E7EB)
)

@Composable
fun AuraLuxeTheme(
    darkTheme: Boolean = true, // Luxury default dark
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) AuraDarkColorScheme else AuraLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AuraTypography,
        content = content
    )
}
