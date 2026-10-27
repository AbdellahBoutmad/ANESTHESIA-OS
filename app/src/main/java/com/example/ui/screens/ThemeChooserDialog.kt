package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppThemePalette
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SoftWhite

@Composable
fun ThemeChooserDialog(
    currentPalette: AppThemePalette,
    isDarkTheme: Boolean,
    onPaletteSelect: (AppThemePalette) -> Unit,
    onDarkThemeToggle: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Palette,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Thèmes & Couleurs Globales",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Change le fond d'écran et toutes les couleurs",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.5.sp
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Dark / Light Mode Switch
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isDarkTheme) "Mode Sombre" else "Mode Clair",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = onDarkThemeToggle,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.background,
                                checkedTrackColor = MaterialTheme.colorScheme.primary,
                                uncheckedThumbColor = MaterialTheme.colorScheme.primary,
                                uncheckedTrackColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                Text(
                    text = "CHOISIR LA PALETTE (FOND & ACTIONS) :",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                // List of 9 palettes
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(290.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(AppThemePalette.entries.toTypedArray()) { palette ->
                        val isSelected = currentPalette == palette

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPaletteSelect(palette) }
                                .testTag("palette_${palette.name.lowercase()}"),
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(
                                if (isSelected) 1.5.dp else 0.8.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 9.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = palette.title,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 12.5.sp
                                    )
                                    Text(
                                        text = palette.description,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 10.sp
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    // Swatch 1: Background Color
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(palette.backgroundColor)
                                            .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                                    )
                                    // Swatch 2: Surface Color
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(palette.surfaceColor)
                                            .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                                    )
                                    // Swatch 3: Primary Action Color
                                    Box(
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(CircleShape)
                                            .background(palette.primaryColor)
                                            .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                                    )

                                    if (isSelected) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "Actif",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.background
                )
            ) {
                Text("Appliquer & Fermer", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    )
}
