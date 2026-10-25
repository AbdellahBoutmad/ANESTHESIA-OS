package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MedicalTeal,
    onPrimary = MidnightNavy,
    primaryContainer = DeepNavyElevated,
    onPrimaryContainer = MedicalTeal,
    secondary = ChampagneGold,
    onSecondary = MidnightNavy,
    secondaryContainer = DeepNavySurface,
    onSecondaryContainer = ChampagneGold,
    tertiary = ChampagneGold,
    onTertiary = MidnightNavy,
    error = AlertRed,
    onError = SoftWhite,
    background = MidnightNavy,
    onBackground = SoftWhite,
    surface = DeepNavy,
    onSurface = SoftWhite,
    surfaceVariant = DeepNavySurface,
    onSurfaceVariant = SlateGray,
    outline = BorderSubtle
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimaryTeal,
    onPrimary = Color.White,
    primaryContainer = LightSurfaceVariant,
    onPrimaryContainer = LightTextPrimary,
    secondary = LightGold,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF1E8D9),
    onSecondaryContainer = LightTextPrimary,
    tertiary = LightGold,
    onTertiary = Color.White,
    error = AlertRed,
    onError = Color.White,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = Color(0xFFCBD5E1)
)

@Composable
fun AnestheiaTroneTheme(
    darkTheme: Boolean = true, // Thème sombre par défaut pour l'esthétique Tron médicale
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Alias pour préserver les usages existants
@Composable
fun AnesthesiaOsTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    AnestheiaTroneTheme(darkTheme = darkTheme, content = content)
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    AnestheiaTroneTheme(darkTheme = darkTheme, content = content)
}
