package com.example.ui.screens.escaperoom

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.escaperoom.ActionRecord
import com.example.data.escaperoom.SimulationResult
import com.example.ui.theme.AlertRed
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.DarkNightBlue
import com.example.ui.theme.MedicalCyan
import com.example.ui.theme.MedicalNavy
import com.example.ui.theme.MedicalNavyElevated
import com.example.ui.theme.MedicalNavySurface
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SecondaryGrey
import com.example.ui.theme.TurquoiseGreen
import com.example.ui.theme.WarningOrange

@Composable
fun BlackBoxReplayView(
    result: SimulationResult,
    modifier: Modifier = Modifier
) {
    val history = result.actionHistory
    var selectedIndex by remember { mutableIntStateOf(history.lastIndex.coerceAtLeast(0)) }

    val currentRecord = history.getOrNull(selectedIndex)

    Column(modifier = modifier.fillMaxSize()) {
        // Black Box Title Bar
        Surface(
            color = MedicalNavyElevated,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ChampagneGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.History, "Black Box", tint = ChampagneGold, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("BLACK BOX REPLAY • BOÎTE NOIRE", color = ChampagneGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Reconstitution chronologique des décisions et constantes", color = SecondaryGrey, fontSize = 11.sp)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = DarkNightBlue
                ) {
                    Text(
                        text = "${history.size} Événements",
                        color = PureWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (history.isEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MedicalNavySurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Aucune décision enregistrée pour cette session.",
                    color = SecondaryGrey,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
            return
        }

        // Snapshot Monitor at Selected Timeline Point
        currentRecord?.let { rec ->
            Text(
                "ÉTAT DU PATIENT AU MOMENT SÉLECTIONNÉ (t = ${rec.timestampSeconds}s) :",
                color = MedicalCyan,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            ClinicalMonitorView(
                vitals = rec.vitalsSnapshot,
                isCriticalAlarm = rec.vitalsSnapshot.isCritical
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Interactive Timeline Scrubber Slider
            Card(
                colors = CardDefaults.cardColors(containerColor = MedicalNavy),
                border = BorderStroke(1.dp, BorderSubtle),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Curseur Temporel : Événement ${selectedIndex + 1} / ${history.size}",
                            color = PureWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${rec.timestampSeconds}s écoulées",
                            color = MedicalCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    if (history.size > 1) {
                        Slider(
                            value = selectedIndex.toFloat(),
                            onValueChange = { selectedIndex = it.toInt() },
                            valueRange = 0f..(history.size - 1).toFloat(),
                            steps = history.size - 2,
                            colors = SliderDefaults.colors(
                                thumbColor = ChampagneGold,
                                activeTrackColor = MedicalCyan,
                                inactiveTrackColor = BorderSubtle
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Selected Action Card with rationale and consequence
            Card(
                colors = CardDefaults.cardColors(containerColor = MedicalNavySurface),
                border = BorderStroke(
                    1.dp,
                    if (rec.wasHarmful) AlertRed.copy(alpha = 0.7f)
                    else if (rec.wasCritical) TurquoiseGreen.copy(alpha = 0.7f)
                    else BorderSubtle
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = when {
                                rec.wasHarmful -> AlertRed.copy(alpha = 0.2f)
                                rec.wasCritical -> TurquoiseGreen.copy(alpha = 0.2f)
                                else -> MedicalCyan.copy(alpha = 0.2f)
                            }
                        ) {
                            Text(
                                text = when {
                                    rec.wasHarmful -> "DÉCISION DÉLÉTÈRE (${rec.scoreDelta} pts)"
                                    rec.wasCritical -> "DÉCISION SALVATRICE (+${rec.scoreDelta} pts)"
                                    else -> "ACTION STANDARD (+${rec.scoreDelta} pts)"
                                },
                                color = when {
                                    rec.wasHarmful -> AlertRed
                                    rec.wasCritical -> TurquoiseGreen
                                    else -> MedicalCyan
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = rec.category.label,
                            color = SecondaryGrey,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = rec.actionTitle,
                        color = PureWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Conséquence immédiate : ${rec.consequenceText}",
                        color = if (rec.wasHarmful) AlertRed else MedicalCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Justification médicale : ${rec.rationale}",
                        color = SecondaryGrey,
                        fontSize = 11.5.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Text(
            "CHRONOLOGIE COMPLÈTE DE LA SESSION :",
            color = PureWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        // History items list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            itemsIndexed(history) { index, item ->
                val isSelected = index == selectedIndex
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedIndex = index },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MedicalNavyElevated else MedicalNavy
                    ),
                    border = BorderStroke(
                        if (isSelected) 1.5.dp else 0.5.dp,
                        if (isSelected) MedicalCyan else BorderSubtle
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${item.timestampSeconds}s",
                            color = if (isSelected) MedicalCyan else SecondaryGrey,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(42.dp)
                        )

                        Icon(
                            imageVector = when {
                                item.wasHarmful -> Icons.Default.Warning
                                item.wasCritical -> Icons.Default.CheckCircle
                                else -> Icons.Default.Info
                            },
                            contentDescription = null,
                            tint = when {
                                item.wasHarmful -> AlertRed
                                item.wasCritical -> TurquoiseGreen
                                else -> MedicalCyan
                            },
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.actionTitle,
                                color = if (isSelected) PureWhite else SecondaryGrey,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1
                            )
                            Text(
                                text = "FC ${item.vitalsSnapshot.heartRate} • SpO2 ${item.vitalsSnapshot.spo2}% • PA ${item.vitalsSnapshot.bloodPressureString()}",
                                color = SecondaryGrey.copy(alpha = 0.8f),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Text(
                            text = if (item.scoreDelta >= 0) "+${item.scoreDelta}" else "${item.scoreDelta}",
                            color = if (item.scoreDelta >= 0) TurquoiseGreen else AlertRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
