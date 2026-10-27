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
    val description: String,
    val backgroundColor: Color,
    val surfaceColor: Color,
    val surfaceCardColor: Color,
    val primaryColor: Color,
    val accentColor: Color,
    val borderColor: Color
) {
    TRON_CYAN(
        title = "Tron Cyan",
        description = "Fond bleu nuit futuriste avec néon cyan",
        backgroundColor = Color(0xFF0B1220),
        surfaceColor = Color(0xFF111C2E),
        surfaceCardColor = Color(0xFF172338),
        primaryColor = Color(0xFF22D3C5),
        accentColor = Color(0xFF00E5FF),
        borderColor = Color(0xFF26364A)
    ),
    OBSIDIAN_NOIR(
        title = "Noir Onyx Pur",
        description = "Fond noir profond absolu ultra-contrasté",
        backgroundColor = Color(0xFF04060A),
        surfaceColor = Color(0xFF0C1017),
        surfaceCardColor = Color(0xFF141A24),
        primaryColor = Color(0xFF00E5FF),
        accentColor = Color(0xFF38BDF8),
        borderColor = Color(0xFF1F2937)
    ),
    SURGICAL_EMERALD(
        title = "Émeraude Bloc",
        description = "Ambiance chirurgicale vert sombre et émeraude",
        backgroundColor = Color(0xFF05120C),
        surfaceColor = Color(0xFF0B1E15),
        surfaceCardColor = Color(0xFF112D21),
        primaryColor = Color(0xFF10B981),
        accentColor = Color(0xFF34D399),
        borderColor = Color(0xFF1A4332)
    ),
    COBALT_SAPPHIRE(
        title = "Cobalt Saphir",
        description = "Bleu royal hospitalier intense et éclatant",
        backgroundColor = Color(0xFF071124),
        surfaceColor = Color(0xFF0E1E3C),
        surfaceCardColor = Color(0xFF152A52),
        primaryColor = Color(0xFF38BDF8),
        accentColor = Color(0xFF60A5FA),
        borderColor = Color(0xFF1E3A6E)
    ),
    CYBER_PURPLE(
        title = "Cyber Améthyste",
        description = "Fond pourpre sombre avec accents violets néon",
        backgroundColor = Color(0xFF0D0618),
        surfaceColor = Color(0xFF170C28),
        surfaceCardColor = Color(0xFF23143B),
        primaryColor = Color(0xFFA855F7),
        accentColor = Color(0xFFC084FC),
        borderColor = Color(0xFF361E58)
    ),
    CRIMSON_CRITICAL(
        title = "Carmin Urgences",
        description = "Fond rubis nocturne pour soins critiques",
        backgroundColor = Color(0xFF14060A),
        surfaceColor = Color(0xFF210B12),
        surfaceCardColor = Color(0xFF30121B),
        primaryColor = Color(0xFFF43F5E),
        accentColor = Color(0xFFFB7185),
        borderColor = Color(0xFF481B2B)
    ),
    IMPERIAL_GOLD(
        title = "Or Impérial",
        description = "Fond bronze sombre avec dorures prestigieuses",
        backgroundColor = Color(0xFF0E0C06),
        surfaceColor = Color(0xFF1A160D),
        surfaceCardColor = Color(0xFF282216),
        primaryColor = Color(0xFFD6B46A),
        accentColor = Color(0xFFF59E0B),
        borderColor = Color(0xFF3F3524)
    ),
    GRAPHITE_SLATE(
        title = "Graphite Minéral",
        description = "Gris anthracite neutre et moderne",
        backgroundColor = Color(0xFF0F1115),
        surfaceColor = Color(0xFF181B20),
        surfaceCardColor = Color(0xFF23272F),
        primaryColor = Color(0xFFCBD5E1),
        accentColor = Color(0xFF94A3B8),
        borderColor = Color(0xFF333945)
    ),
    CLINICAL_LIGHT(
        title = "Clinique Pur Clair",
        description = "Fond blanc neige et turquoise hospitalier",
        backgroundColor = Color(0xFFF8FAFC),
        surfaceColor = Color(0xFFFFFFFF),
        surfaceCardColor = Color(0xFFF1F5F9),
        primaryColor = Color(0xFF0D9488),
        accentColor = Color(0xFF0284C7),
        borderColor = Color(0xFFCBD5E1)
    )
}

fun createDarkColorScheme(palette: AppThemePalette): ColorScheme {
    val bg = palette.backgroundColor
    val surf = palette.surfaceColor
    val card = palette.surfaceCardColor
    val prim = palette.primaryColor
    val sec = palette.accentColor
    val border = palette.borderColor

    return darkColorScheme(
        primary = prim,
        onPrimary = bg,
        primaryContainer = card,
        onPrimaryContainer = prim,
        secondary = sec,
        onSecondary = bg,
        secondaryContainer = surf,
        onSecondaryContainer = sec,
        tertiary = ChampagneGold,
        onTertiary = bg,
        error = AlertRed,
        onError = SoftWhite,
        background = bg,
        onBackground = SoftWhite,
        surface = surf,
        onSurface = SoftWhite,
        surfaceVariant = card,
        onSurfaceVariant = SlateGray,
        outline = border
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
