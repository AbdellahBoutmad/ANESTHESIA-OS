package com.example.ui.screens.escaperoom

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.escaperoom.SimulatedVitals
import com.example.ui.theme.AlertRed
import com.example.ui.theme.DarkNightBlue
import com.example.ui.theme.MedicalCyan
import com.example.ui.theme.MedicalNavy
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SecondaryGrey
import com.example.ui.theme.TurquoiseGreen
import com.example.ui.theme.WarningOrange

val MonitorNeonGreen = Color(0xFF00E676)
val MonitorCyan = Color(0xFF00E5FF)
val MonitorYellow = Color(0xFFFFEA00)
val MonitorOrange = Color(0xFFFF9100)
val MonitorDarkBg = Color(0xFF060B12)

@Composable
fun ClinicalMonitorView(
    vitals: SimulatedVitals,
    isCriticalAlarm: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MonitorDarkBg),
        border = BorderStroke(
            1.5.dp,
            if (isCriticalAlarm || vitals.isCritical) AlertRed else MedicalCyan.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Bar with Disclaimer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (vitals.isCritical) AlertRed else MonitorNeonGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "MONITEUR DE SALLE D'OPÉRATION • SCOPE",
                        color = PureWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = WarningOrange.copy(alpha = 0.2f),
                    border = BorderStroke(0.5.dp, WarningOrange.copy(alpha = 0.7f))
                ) {
                    Text(
                        text = "DONNÉES FICTIVES • SIMULATION",
                        color = WarningOrange,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Monitor Dashboard: Waveform on left (or top), Digital tiles on right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Waveform Display Canvas
                Box(
                    modifier = Modifier
                        .weight(1.3f)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF03060A))
                        .border(BorderStroke(0.5.dp, Color(0xFF1B2C42)), RoundedCornerShape(8.dp))
                ) {
                    AnimatedEcgWaveformCanvas(
                        heartRate = vitals.heartRate,
                        isCritical = vitals.isCritical,
                        rhythm = vitals.rhythmDescription
                    )

                    // Waveform label
                    Text(
                        text = "ECG DII • ${vitals.rhythmDescription.uppercase()}",
                        color = MonitorNeonGreen.copy(alpha = 0.7f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                    )
                }

                // Digital Numerical Gauges
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Row 1: HR (Green) & SpO2 (Cyan/Yellow)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        VitalDigitTile(
                            label = "FC",
                            unit = "bpm",
                            value = "${vitals.heartRate}",
                            color = MonitorNeonGreen,
                            isWarning = vitals.heartRate > 130 || vitals.heartRate < 50,
                            modifier = Modifier.weight(1f)
                        )
                        VitalDigitTile(
                            label = "SpO2",
                            unit = "%",
                            value = "${vitals.spo2}",
                            color = MonitorCyan,
                            isWarning = vitals.spo2 < 90,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Row 2: NIBP (White) & EtCO2 (Yellow)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        VitalDigitTile(
                            label = "PANI",
                            unit = "mmHg",
                            value = vitals.bloodPressureString(),
                            color = PureWhite,
                            isWarning = vitals.systolicBp < 80 || vitals.systolicBp > 160,
                            modifier = Modifier.weight(1f)
                        )
                        VitalDigitTile(
                            label = "EtCO2",
                            unit = "mmHg",
                            value = "${vitals.etco2}",
                            color = MonitorYellow,
                            isWarning = vitals.etco2 > 50 || vitals.etco2 < 20,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Secondary telemetry strip (RR, Temp, Alarms)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF0F1724))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FR : ${vitals.respiratoryRate}/min",
                    color = MonitorYellow,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "T° : ${vitals.temperature}°C",
                    color = if (vitals.temperature > 38.5) AlertRed else PureWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                if (vitals.isCritical) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = "Alarme",
                            tint = AlertRed,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ALARME CRITIQUE",
                            color = AlertRed,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                } else {
                    Text(
                        text = "TRACE NORMALISÉE",
                        color = MonitorNeonGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun VitalDigitTile(
    label: String,
    unit: String,
    value: String,
    color: Color,
    isWarning: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0B1320))
            .border(
                BorderStroke(
                    0.5.dp,
                    if (isWarning) AlertRed else color.copy(alpha = 0.3f)
                ),
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    color = SecondaryGrey,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = unit,
                    color = SecondaryGrey.copy(alpha = 0.7f),
                    fontSize = 8.sp
                )
            }
            Text(
                text = value,
                color = if (isWarning) AlertRed else color,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun AnimatedEcgWaveformCanvas(
    heartRate: Int,
    isCritical: Boolean,
    rhythm: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ecg_transition")

    // Duration scales with heart rate (e.g. 120 bpm = 2Hz, 60 bpm = 1Hz)
    val cycleDurationMs = ((60000 / heartRate.coerceIn(40, 180))).coerceIn(350, 1500)

    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = cycleDurationMs * 2, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ecg_progress"
    )

    Canvas(modifier = modifier.fillMaxSize().padding(horizontal = 6.dp, vertical = 10.dp)) {
        val width = size.width
        val height = size.height
        val midY = height * 0.55f

        // Draw faint background oscilloscope grid
        val gridStepX = width / 12f
        val gridStepY = height / 6f
        for (i in 0..12) {
            drawLine(
                color = Color(0xFF0D1E2D),
                start = Offset(i * gridStepX, 0f),
                end = Offset(i * gridStepX, height),
                strokeWidth = 0.5f
            )
        }
        for (j in 0..6) {
            drawLine(
                color = Color(0xFF0D1E2D),
                start = Offset(0f, j * gridStepY),
                end = Offset(width, j * gridStepY),
                strokeWidth = 0.5f
            )
        }

        // Draw P-Q-R-S-T wave pattern across the canvas
        val path = Path()
        val numCycles = 3
        val cycleWidth = width / numCycles

        for (c in 0 until numCycles) {
            val startX = c * cycleWidth
            val endX = (c + 1) * cycleWidth

            if (c == 0) path.moveTo(startX, midY)

            // P wave
            path.lineTo(startX + cycleWidth * 0.15f, midY)
            path.cubicTo(
                startX + cycleWidth * 0.18f, midY - height * 0.12f,
                startX + cycleWidth * 0.22f, midY - height * 0.12f,
                startX + cycleWidth * 0.25f, midY
            )

            // PR interval
            path.lineTo(startX + cycleWidth * 0.32f, midY)

            // Q dip
            path.lineTo(startX + cycleWidth * 0.34f, midY + height * 0.08f)

            // R peak
            val rPeakHeight = if (isCritical) height * 0.85f else height * 0.75f
            path.lineTo(startX + cycleWidth * 0.38f, midY - rPeakHeight)

            // S dip
            path.lineTo(startX + cycleWidth * 0.42f, midY + height * 0.22f)

            // ST segment
            path.lineTo(startX + cycleWidth * 0.48f, midY)

            // T wave
            val tHeight = if (rhythm.contains("Hyperkaliémie", ignoreCase = true)) height * 0.45f else height * 0.2f
            path.cubicTo(
                startX + cycleWidth * 0.55f, midY - tHeight,
                startX + cycleWidth * 0.65f, midY - tHeight,
                startX + cycleWidth * 0.72f, midY
            )

            // Baseline to end of cycle
            path.lineTo(endX, midY)
        }

        // Render the wave in glowing medical green or alert red
        val waveColor = if (isCritical) AlertRed else MonitorNeonGreen
        drawPath(
            path = path,
            color = waveColor,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw current sweeping pulse beacon
        val sweepX = (progress * width) % width
        drawCircle(
            color = PureWhite,
            radius = 3.dp.toPx(),
            center = Offset(sweepX, midY)
        )
    }
}
