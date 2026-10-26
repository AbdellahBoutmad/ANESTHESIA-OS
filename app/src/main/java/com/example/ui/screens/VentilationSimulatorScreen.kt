package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.theme.AlertRed
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.DarkNightBlue
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.DeepNavyElevated
import com.example.ui.theme.DeepNavySurface
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MedicalTeal
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SoftWhite
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningOrange
import kotlin.math.exp
import kotlin.math.roundToInt

enum class VentMode(val label: String, val desc: String) {
    VC("VC (Volume Contrôlé)", "Débit inspiratoire constant, volume garanti."),
    VACI("VACI", "Cycles obligatoires synchronisés avec efforts spontanés."),
    VSAI("VSAI / AI + PEP", "Ventilation spontanée assistée avec aide inspiratoire.")
}

data class VentScenario(
    val title: String,
    val mode: VentMode,
    val vt: Float,
    val fr: Float,
    val peep: Float,
    val fio2: Float,
    val compliance: Float,
    val resistance: Float,
    val note: String
)

@Composable
fun VentilationSimulatorScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    var mode by remember { mutableStateOf(VentMode.VC) }
    var vt by remember { mutableFloatStateOf(450f) }           // mL
    var fr by remember { mutableFloatStateOf(14f) }            // cycles/min
    var peep by remember { mutableFloatStateOf(5f) }           // cmH2O
    var fio2 by remember { mutableFloatStateOf(40f) }          // %
    var ieRatioNum by remember { mutableFloatStateOf(2.0f) }   // 1:2
    var compliance by remember { mutableFloatStateOf(50f) }    // mL/cmH2O (normal 50-70)
    var resistance by remember { mutableFloatStateOf(10f) }    // cmH2O/L/s (normal 5-10)

    val scenarios = listOf(
        VentScenario("Poumon sain", VentMode.VC, 450f, 14f, 5f, 40f, 55f, 8f, "Mécanique respiratoire normale au bloc"),
        VentScenario("SDRA sévère", VentMode.VC, 360f, 22f, 12f, 80f, 22f, 12f, "Baisse drastique de compliance, Pplat élevée"),
        VentScenario("Bronchospasme / BPCO", VentMode.VC, 420f, 12f, 5f, 50f, 45f, 26f, "Résistance augmentée, expirium prolongé"),
        VentScenario("Sevrage VSAI", VentMode.VSAI, 400f, 16f, 5f, 30f, 50f, 9f, "Efforts spontanés déclenchés par le patient"),
        VentScenario("Obésité morbide", VentMode.VC, 420f, 16f, 8f, 50f, 30f, 16f, "Compliance réduite par la paroi thoracique")
    )

    // Calculated parameters
    val safeCompliance = compliance.coerceAtLeast(10f)
    val flowLitersPerSec = 0.6f // standard constant insp flow
    val pPlat = peep + (vt / safeCompliance)
    val pPeak = pPlat + (flowLitersPerSec * resistance)
    val drivingPressure = pPlat - peep
    val minuteVolume = (vt * fr) / 1000f // L/min

    val isHighPeak = pPeak > 35f
    val isHighDriving = drivingPressure > 14f
    val isHypoventilation = minuteVolume < 4.0f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNightBlue)
            .testTag("ventilation_simulator_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Retour",
                        tint = MedicalTeal
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Simulateur de Ventilation",
                        color = SoftWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "VC • VACI • VSAI • Courbes P-V-Q & Paramètres en direct",
                        color = SlateGray,
                        fontSize = 12.sp
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isHighPeak || isHighDriving) AlertRed else SuccessGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHighPeak || isHighDriving) "ALERTE" else "STABLE",
                            color = if (isHighPeak || isHighDriving) AlertRed else SuccessGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Clinical Scenarios Quick Bar
        item {
            Text(
                text = "Scénarios cliniques préconfigurés :",
                color = SoftWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(scenarios) { sc ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DeepNavyElevated,
                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                        modifier = Modifier.clickable {
                            mode = sc.mode
                            vt = sc.vt
                            fr = sc.fr
                            peep = sc.peep
                            fio2 = sc.fio2
                            compliance = sc.compliance
                            resistance = sc.resistance
                        }
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                            Text(text = sc.title, color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(text = "${sc.vt.roundToInt()}mL • PEP ${sc.peep.roundToInt()}", color = SlateGray, fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        // Waveforms Display (Canvas)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DeepNavy),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Courbes Dynamiques Respiratoires",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${mode.name} • 1:${ieRatioNum.toInt()}",
                            color = ChampagneGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // Waveform Canvas (Pressure Paw, Flow, Volume)
                    VentilationWaveformsCanvas(
                        pPeak = pPeak,
                        pPlat = pPlat,
                        peep = peep,
                        vt = vt,
                        fr = fr,
                        mode = mode
                    )
                }
            }
        }

        // Live Computed Metrics
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricBadge(
                    title = "P. Crête (Ppeak)",
                    value = "${pPeak.roundToInt()}",
                    unit = "cmH2O",
                    isWarning = isHighPeak,
                    modifier = Modifier.weight(1f)
                )
                MetricBadge(
                    title = "P. Plateau (Pplat)",
                    value = "${pPlat.roundToInt()}",
                    unit = "cmH2O",
                    isWarning = pPlat > 30f,
                    modifier = Modifier.weight(1f)
                )
                MetricBadge(
                    title = "ΔP (Driving P.)",
                    value = "${drivingPressure.roundToInt()}",
                    unit = "cmH2O",
                    isWarning = isHighDriving,
                    modifier = Modifier.weight(1f)
                )
                MetricBadge(
                    title = "V. Minute (VE)",
                    value = String.format("%.1f", minuteVolume),
                    unit = "L/min",
                    isWarning = isHypoventilation,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Alarms & Clinical Warnings
        if (isHighPeak || isHighDriving || isHypoventilation) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = AlertRed.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, AlertRed.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = AlertRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            if (isHighPeak) {
                                Text(
                                    text = "ALERTE : Pression de crête > 35 cmH2O (Risque de barotraumatisme ou obstruction des VAS)",
                                    color = SoftWhite,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (isHighDriving) {
                                Text(
                                    text = "ALERTE : Driving Pressure > 14 cmH2O (Augmentation du strain pulmonaire / ventilation protectrice compromise)",
                                    color = SoftWhite,
                                    fontSize = 11.sp
                                )
                            }
                            if (isHypoventilation) {
                                Text(
                                    text = "ATTENTION : Débit minute insuffisant (< 4 L/min), risque d'hypercapnie et d'acidose respiratoire",
                                    color = WarningOrange,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Ventilation Mode Selector
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DeepNavyElevated),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Mode de Ventilation :",
                        color = SoftWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        VentMode.values().forEach { m ->
                            val isSel = mode == m
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) MaterialTheme.colorScheme.primary else DeepNavySurface,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSel) MaterialTheme.colorScheme.primary else SlateGray.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { mode = m }
                            ) {
                                Text(
                                    text = m.name,
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    color = if (isSel) DarkNightBlue else SoftWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = mode.desc, color = SlateGray, fontSize = 11.sp)
                }
            }
        }

        // Adjustable Sliders
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DeepNavyElevated),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Réglages de la Machine :",
                        color = SoftWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // VT Slider
                    SliderRow(
                        label = "Volume Courant (Vt)",
                        valueText = "${vt.roundToInt()} mL (${String.format("%.1f", vt / 70f)} mL/kg PBW)",
                        value = vt,
                        range = 250f..800f,
                        step = 11,
                        onValueChange = { vt = it }
                    )

                    // FR Slider
                    SliderRow(
                        label = "Fréquence Respiratoire (FR)",
                        valueText = "${fr.roundToInt()} /min",
                        value = fr,
                        range = 8f..35f,
                        step = 27,
                        onValueChange = { fr = it }
                    )

                    // PEEP Slider
                    SliderRow(
                        label = "PEEP / PEP",
                        valueText = "${peep.roundToInt()} cmH2O",
                        value = peep,
                        range = 0f..20f,
                        step = 20,
                        onValueChange = { peep = it }
                    )

                    // FiO2 Slider
                    SliderRow(
                        label = "Fraction Inspirée en O2 (FiO2)",
                        valueText = "${fio2.roundToInt()} %",
                        value = fio2,
                        range = 21f..100f,
                        step = 79,
                        onValueChange = { fio2 = it }
                    )

                    // I:E Ratio
                    SliderRow(
                        label = "Rapport I:E (1 : E)",
                        valueText = "1 : ${ieRatioNum.toInt()}",
                        value = ieRatioNum,
                        range = 1f..4f,
                        step = 3,
                        onValueChange = { ieRatioNum = it }
                    )
                }
            }
        }

        // Patient Mechanics Modifiers
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DeepNavyElevated),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Mécanique Pulmonaire du Patient (Simulation) :",
                        color = ChampagneGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    SliderRow(
                        label = "Compliance Thoraco-Pulmonaire (Crs)",
                        valueText = "${compliance.roundToInt()} mL/cmH2O (N = 50-70)",
                        value = compliance,
                        range = 15f..80f,
                        step = 13,
                        accentColor = ChampagneGold,
                        onValueChange = { compliance = it }
                    )

                    SliderRow(
                        label = "Résistance des Voies Aériennes (Raw)",
                        valueText = "${resistance.roundToInt()} cmH2O/L/s (N = 5-10)",
                        value = resistance,
                        range = 5f..35f,
                        step = 30,
                        accentColor = ChampagneGold,
                        onValueChange = { resistance = it }
                    )
                }
            }
        }

        // Clinical Guidelines Summary
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DeepNavy),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Recommandations SFAR - Ventilation Protectrice :",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Volume courant : 6 à 8 mL/kg de poids idéal théorique (PBW).\n• Pression de plateau cible : < 28-30 cmH2O pour éviter le barotraumatisme.\n• Pression motrice (Driving Pressure) : < 14 cmH2O.\n• PEEP systématique d'au moins 5 cmH2O pour prévenir les atélectasies.",
                        color = SlateGray,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun VentilationWaveformsCanvas(
    pPeak: Float,
    pPlat: Float,
    peep: Float,
    vt: Float,
    fr: Float,
    mode: VentMode
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color(0xFF070D18), RoundedCornerShape(8.dp))
    ) {
        val width = size.width
        val height = size.height

        // Background subtle grid lines
        val gridColor = Color(0xFF14243B)
        for (i in 1..7) {
            val y = height * (i / 8f)
            drawLine(gridColor, Offset(0f, y), Offset(width, y), strokeWidth = 1f)
        }
        for (i in 1..9) {
            val x = width * (i / 10f)
            drawLine(gridColor, Offset(x, 0f), Offset(x, height), strokeWidth = 1f)
        }

        // Split canvas into two sections: Top = Pressure, Bottom = Flow
        val midY = height * 0.55f

        // Draw Pressure Waveform (Cyan/Teal)
        val pressurePath = Path()
        val numCycles = 3
        val cycleWidth = width / numCycles

        for (c in 0 until numCycles) {
            val startX = c * cycleWidth
            val inspEnd = startX + (cycleWidth * 0.33f)
            val platEnd = startX + (cycleWidth * 0.45f)
            val expEnd = startX + cycleWidth

            val peepY = midY - (peep * 1.8f).coerceAtMost(midY - 10f)
            val peakY = midY - (pPeak * 1.8f).coerceAtLeast(10f)
            val platY = midY - (pPlat * 1.8f).coerceAtLeast(15f)

            if (c == 0) {
                pressurePath.moveTo(startX, peepY)
            } else {
                pressurePath.lineTo(startX, peepY)
            }

            if (mode == VentMode.VC) {
                // VC : ramp up to peak, then drop to plateau
                pressurePath.lineTo(inspEnd, peakY)
                pressurePath.lineTo(platEnd, platY)
                pressurePath.lineTo(platEnd + 10f, peepY)
                pressurePath.lineTo(expEnd, peepY)
            } else {
                // PC / VSAI : rapid square rise, hold, then exp
                pressurePath.lineTo(startX + 15f, peakY)
                pressurePath.lineTo(platEnd, peakY)
                pressurePath.lineTo(platEnd + 15f, peepY)
                pressurePath.lineTo(expEnd, peepY)
            }
        }

        drawPath(
            path = pressurePath,
            color = Color(0xFF00E5FF),
            style = Stroke(width = 2.5f)
        )

        // Draw Flow Waveform (Yellow/Gold) in lower section
        val flowPath = Path()
        val flowBaselineY = height * 0.82f

        for (c in 0 until numCycles) {
            val startX = c * cycleWidth
            val inspPeakX = startX + 15f
            val inspEndX = startX + (cycleWidth * 0.35f)
            val expPeakX = inspEndX + 10f
            val expEndX = startX + cycleWidth

            val inspFlowY = flowBaselineY - 26f
            val expFlowY = flowBaselineY + 24f

            if (c == 0) flowPath.moveTo(startX, flowBaselineY)
            else flowPath.lineTo(startX, flowBaselineY)

            // Inspiration flow
            flowPath.lineTo(inspPeakX, inspFlowY)
            flowPath.lineTo(inspEndX, inspFlowY)
            flowPath.lineTo(inspEndX, flowBaselineY)

            // Expiration flow (exponential return)
            flowPath.lineTo(expPeakX, expFlowY)
            flowPath.quadraticBezierTo(
                inspEndX + (cycleWidth * 0.4f),
                flowBaselineY + 6f,
                expEndX,
                flowBaselineY
            )
        }

        drawPath(
            path = flowPath,
            color = Color(0xFFD6B46A),
            style = Stroke(width = 2.0f)
        )
    }
}

@Composable
fun MetricBadge(
    title: String,
    value: String,
    unit: String,
    isWarning: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isWarning) AlertRed.copy(alpha = 0.2f) else DeepNavyElevated
        ),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            1.dp,
            if (isWarning) AlertRed else SlateGray.copy(alpha = 0.3f)
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = if (isWarning) AlertRed else SlateGray,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = if (isWarning) AlertRed else SoftWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = unit,
                color = SlateGray,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
fun SliderRow(
    label: String,
    valueText: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    step: Int,
    accentColor: Color = MedicalTeal,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = SoftWhite, fontSize = 11.sp)
            Text(text = valueText, color = accentColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            steps = step,
            colors = SliderDefaults.colors(
                thumbColor = accentColor,
                activeTrackColor = accentColor,
                inactiveTrackColor = DeepNavySurface
            )
        )
    }
}
