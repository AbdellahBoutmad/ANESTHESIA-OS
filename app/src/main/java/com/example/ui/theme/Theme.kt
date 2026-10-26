package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppThemePalette(
    val title: String,
    val primaryColor: Color,
    val accentColor: Color
) {
    TRON_CYAN("Tron Cyan", Color(0xFF22D3C5), Color(0xFF00E5FF)),
    IMPERIAL_GOLD("Or Impérial", Color(0xFFE5B94E), Color(0xFFF59E0B)),
    EMERALD_SURGICAL("Émeraude Bloc", Color(0xFF10B981), Color(0xFF34D399)),
    COBALT_SAPPHIRE("Cobalt Saphir", Color(0xFF38BDF8), Color(0xFF60A5FA)),
    CRIMSON_CRITICAL("Carmin Urgence", Color(0xFFF43F5E), Color(0xFFFB7185))
}

fun createDarkColorScheme(palette: AppThemePalette): ColorScheme {
    val prim = palette.primaryColor
    val sec = palette.accentColor
    return darkColorScheme(
        primary = prim,
        onPrimary = MidnightNavy,
        primaryContainer = SurfaceCard,
        onPrimaryContainer = prim,
        secondary = sec,
        onSecondary = MidnightNavy,
        secondaryContainer = SurfaceCard,
        onSecondaryContainer = sec,
        tertiary = ChampagneGold,
        onTertiary = MidnightNavy,
        error = AlertRed,
        onError = SoftWhite,
        background = MidnightNavy,
        onBackground = SoftWhite,
        surface = DeepNavy,
        onSurface = SoftWhite,
        surfaceVariant = SurfaceCard,
        onSurfaceVariant = SlateGray,
        outline = BorderSubtle
    )
}

fun createLightColorScheme(palette: AppThemePalette): ColorScheme {
    val prim = palette.primaryColor
    val sec = palette.accentColor
    return lightColorScheme(
        primary = prim,
        onPrimary = Color.White,
        primaryContainer = LightSurfaceVariant,
        onPrimaryContainer = LightTextPrimary,
        secondary = sec,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFF1E8D9),
        onSecondaryContainer = LightTextPrimary,
        tertiary = ChampagneGold,
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
}

@Composable
fun AnestheiaTroneTheme(
    palette: AppThemePalette = AppThemePalette.TRON_CYAN,
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) createDarkColorScheme(palette) else createLightColorScheme(palette)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun AnesthesiaOsTheme(
    palette: AppThemePalette = AppThemePalette.TRON_CYAN,
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    AnestheiaTroneTheme(palette = palette, darkTheme = darkTheme, content = content)
}

@Composable
fun MyApplicationTheme(
    palette: AppThemePalette = AppThemePalette.TRON_CYAN,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    AnestheiaTroneTheme(palette = palette, darkTheme = darkTheme, content = content)
}
