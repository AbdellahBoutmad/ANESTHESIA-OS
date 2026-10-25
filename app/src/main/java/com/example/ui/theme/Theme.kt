package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MedicalCyan,
    onPrimary = DarkNightBlue,
    primaryContainer = MedicalNavyElevated,
    onPrimaryContainer = MedicalCyan,
    secondary = TurquoiseGreen,
    onSecondary = DarkNightBlue,
    secondaryContainer = MedicalNavySurface,
    onSecondaryContainer = TurquoiseGreen,
    tertiary = WarningOrange,
    onTertiary = DarkNightBlue,
    error = AlertRed,
    onError = PureWhite,
    background = DarkNightBlue,
    onBackground = PureWhite,
    surface = MedicalNavy,
    onSurface = PureWhite,
    surfaceVariant = MedicalNavySurface,
    onSurfaceVariant = SecondaryGrey,
    outline = BorderSubtle
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimaryCyan,
    onPrimary = Color.White,
    primaryContainer = LightSurfaceVariant,
    onPrimaryContainer = DarkNightBlue,
    secondary = TurquoiseGreen,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDFF8F4),
    onSecondaryContainer = DarkNightBlue,
    tertiary = WarningOrange,
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
fun AnesthesiaOsTheme(
    darkTheme: Boolean = true, // Default to deep medical dark mode as requested in specs
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    AnesthesiaOsTheme(darkTheme = darkTheme, content = content)
}
