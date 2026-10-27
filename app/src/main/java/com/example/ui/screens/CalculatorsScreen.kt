package com.example.ui.screens

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.ClinicalCalculators
import kotlin.math.roundToInt

@Composable
fun CalculatorsScreen() {
    val tabs = listOf(
        "IMC & Poids Idéal",
        "Clairance Créat",
        "Score Apfel (NVPO)",
        "STOP-BANG (SAOS)",
        "Score Aldrete (SSPI)",
        "Pertes Sanguines (ABL)",
        "Pédiatrie"
    )
    var selectedTabIndex by remember { mutableStateOf(0) }
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .testTag("calculators_screen")
    ) {
        // Tab Row with theme colors
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = colors.surface,
            contentColor = colors.primary,
            edgePadding = 16.dp,
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            title,
                            color = if (selectedTabIndex == index) colors.primary else colors.onSurfaceVariant,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 680.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
            ) {
                item {
                    when (selectedTabIndex) {
                        0 -> BmiAndIdealWeightCalculator()
                        1 -> CockcroftCreatinineCalculator()
                        2 -> ApfelPonvCalculator()
                        3 -> StopBangCalculator()
                        4 -> AldretePacuCalculator()
                        5 -> AllowableBloodLossCalculator()
                        6 -> PediatricFormulasCalculator()
                    }
                }
            }
        }
    }
}

@Composable
private fun dynamicOutlinedColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
)

@Composable
fun BmiAndIdealWeightCalculator() {
    var weightText by remember { mutableStateOf("75") }
    var heightText by remember { mutableStateOf("175") }
    var isFemale by remember { mutableStateOf(false) }

    val w = weightText.toDoubleOrNull() ?: 0.0
    val h = heightText.toDoubleOrNull() ?: 0.0
    val result = remember(w, h, isFemale) { ClinicalCalculators.calculateBmi(w, h, isFemale) }
    val colors = MaterialTheme.colorScheme

    Column {
        Text("CALCUL DE L'IMC & POIDS THÉORIQUES", color = colors.primary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = weightText,
                onValueChange = { weightText = it },
                label = { Text("Poids réel (kg)", fontSize = 12.sp) },
                modifier = Modifier.weight(1f),
                colors = dynamicOutlinedColors()
            )
            OutlinedTextField(
                value = heightText,
                onValueChange = { heightText = it },
                label = { Text("Taille (cm)", fontSize = 12.sp) },
                modifier = Modifier.weight(1f),
                colors = dynamicOutlinedColors()
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = !isFemale,
                onClick = { isFemale = false },
                label = { Text("Homme") },
                colors = filterChipColors()
            )
            FilterChip(
                selected = isFemale,
                onClick = { isFemale = true },
                label = { Text("Femme") },
                colors = filterChipColors()
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
            border = BorderStroke(1.dp, colors.primary)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("RÉSULTATS BIOMÉTRIQUES", color = colors.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))

                ResultRow("IMC (Indice Masse Corporelle)", "${result.bmi} kg/m²", result.category)
                ResultRow("Poids Idéal (Lorentz)", "${result.idealWeightLorentz} kg", "Formule historique")
                ResultRow("Poids Idéal (Devine)", "${result.idealWeightDevine} kg", "Recommandé pour drogues lipophiles")
                ResultRow("Poids Ajusté (40%)", "${result.adjustedWeight} kg", "Utile pour curares & antibiotiques")
                ResultRow("Surface Corporelle (Mosteller)", "${result.bsaMosteller} m²", "Calcul débit cardiaque indexé")
            }
        }
    }
}

@Composable
fun CockcroftCreatinineCalculator() {
    var ageText by remember { mutableStateOf("65") }
    var weightText by remember { mutableStateOf("70") }
    var crText by remember { mutableStateOf("95") }
    var isFemale by remember { mutableStateOf(false) }

    val age = ageText.toIntOrNull() ?: 0
    val w = weightText.toDoubleOrNull() ?: 0.0
    val cr = crText.toDoubleOrNull() ?: 0.0
    val result = remember(age, w, cr, isFemale) { ClinicalCalculators.calculateCockcroft(age, w, cr, isFemale) }
    val colors = MaterialTheme.colorScheme

    Column {
        Text("ESTIMATION DE LA CLAIRANCE DE LA CRÉATININE", color = colors.primary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = ageText,
                onValueChange = { ageText = it },
                label = { Text("Âge (ans)", fontSize = 12.sp) },
                modifier = Modifier.weight(1f),
                colors = dynamicOutlinedColors()
            )
            OutlinedTextField(
                value = weightText,
                onValueChange = { weightText = it },
                label = { Text("Poids (kg)", fontSize = 12.sp) },
                modifier = Modifier.weight(1f),
                colors = dynamicOutlinedColors()
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = crText,
            onValueChange = { crText = it },
            label = { Text("Créatininémie (µmol/L)", fontSize = 12.sp) },
            modifier = Modifier.fillMaxWidth(),
            colors = dynamicOutlinedColors()
        )

        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = !isFemale,
                onClick = { isFemale = false },
                label = { Text("Homme") },
                colors = filterChipColors()
            )
            FilterChip(
                selected = isFemale,
                onClick = { isFemale = true },
                label = { Text("Femme") },
                colors = filterChipColors()
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
            border = BorderStroke(1.dp, colors.tertiary)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("CLAIRANCE (COCKCROFT & GAULT)", color = colors.tertiary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text("${result.clearanceMlMin} mL/min", color = colors.onSurface, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                Text(result.stage, color = colors.primary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(result.doseAdjustmentRecommendation, color = colors.onSurfaceVariant, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun ApfelPonvCalculator() {
    var isFemale by remember { mutableStateOf(true) }
    var isNonSmoker by remember { mutableStateOf(true) }
    var historyPonv by remember { mutableStateOf(false) }
    var opioidsPlanned by remember { mutableStateOf(true) }

    val result = remember(isFemale, isNonSmoker, historyPonv, opioidsPlanned) {
        ClinicalCalculators.calculateApfel(isFemale, isNonSmoker, historyPonv, opioidsPlanned)
    }
    val colors = MaterialTheme.colorScheme

    Column {
        Text("SCORE D'APFEL — RISQUE DE NAUSÉES ET VOMISSEMENTS (NVPO)", color = colors.primary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(10.dp))

        CheckRow("Sexe féminin (+1)", isFemale) { isFemale = it }
        CheckRow("Patient non-fumeur (+1)", isNonSmoker) { isNonSmoker = it }
        CheckRow("Antécédent de NVPO ou mal de transports (+1)", historyPonv) { historyPonv = it }
        CheckRow("Utilisation prévue d'opioïdes postopératoires (+1)", opioidsPlanned) { opioidsPlanned = it }

        Spacer(modifier = Modifier.height(14.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
            border = BorderStroke(1.dp, colors.tertiary)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("SCORE TOTAL : ${result.score} / 4", color = colors.tertiary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Risque estimé : ${result.riskPercentage}%", color = colors.onSurface, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(result.riskLevel, color = colors.onSurface, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Conduite préventive recommandée :", color = colors.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(result.recommendation, color = colors.onSurfaceVariant, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun StopBangCalculator() {
    var snoring by remember { mutableStateOf(false) }
    var tired by remember { mutableStateOf(false) }
    var observedApnea by remember { mutableStateOf(false) }
    var pressureHta by remember { mutableStateOf(true) }
    var bmiOver35 by remember { mutableStateOf(false) }
    var ageOver50 by remember { mutableStateOf(true) }
    var neckOver40 by remember { mutableStateOf(false) }
    var genderMale by remember { mutableStateOf(true) }

    val criteria = listOf(snoring, tired, observedApnea, pressureHta, bmiOver35, ageOver50, neckOver40, genderMale)
    val result = remember(criteria) { ClinicalCalculators.calculateStopBang(criteria) }
    val colors = MaterialTheme.colorScheme

    Column {
        Text("SCORE STOP-BANG — DÉPISTAGE DU SAOS", color = colors.primary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(10.dp))

        CheckRow("S - Ronflement fort (Snoring)", snoring) { snoring = it }
        CheckRow("T - Fatigue diurne / somnolence (Tired)", tired) { tired = it }
        CheckRow("O - Pauses respiratoires observées (Observed apnea)", observedApnea) { observedApnea = it }
        CheckRow("P - Pression artérielle élevée / HTA traitée (Pressure)", pressureHta) { pressureHta = it }
        CheckRow("B - IMC > 35 kg/m² (BMI)", bmiOver35) { bmiOver35 = it }
        CheckRow("A - Âge > 50 ans (Age)", ageOver50) { ageOver50 = it }
        CheckRow("N - Tour de cou > 40 cm (Neck)", neckOver40) { neckOver40 = it }
        CheckRow("G - Sexe masculin (Gender male)", genderMale) { genderMale = it }

        Spacer(modifier = Modifier.height(14.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
            border = BorderStroke(1.dp, if (result.score >= 5) colors.error else colors.primary)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    "SCORE : ${result.score} / 8",
                    color = if (result.score >= 5) colors.error else colors.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(result.riskLevel, color = colors.onSurface, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(result.recommendation, color = colors.onSurfaceVariant, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun AldretePacuCalculator() {
    var motor by remember { mutableStateOf(2) }
    var resp by remember { mutableStateOf(2) }
    var circ by remember { mutableStateOf(2) }
    var consci by remember { mutableStateOf(2) }
    var spo2 by remember { mutableStateOf(2) }

    val result = remember(motor, resp, circ, consci, spo2) {
        ClinicalCalculators.calculateAldrete(motor, resp, circ, consci, spo2)
    }
    val colors = MaterialTheme.colorScheme

    Column {
        Text("SCORE D'ALDRETE MODIFIÉ — CRITÈRES DE SORTIE DE SSPI", color = colors.primary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(10.dp))

        ScoreSelector("1. Activité motrice volontaire :", motor, listOf("0 = Immobile", "1 = Bouge 2 membres", "2 = Bouge 4 membres")) { motor = it }
        ScoreSelector("2. Respiration & toux :", resp, listOf("0 = Apnée", "1 = Dyspnée modérée", "2 = Respiration ample & toux")) { resp = it }
        ScoreSelector("3. Pression artérielle :", circ, listOf("0 = Écart > 50%", "1 = Écart 20-50%", "2 = Écart < 20% réf.")) { circ = it }
        ScoreSelector("4. Conscience :", consci, listOf("0 = Non réveillable", "1 = Réveillable", "2 = Parfaitement lucide")) { consci = it }
        ScoreSelector("5. Saturation SpO2 :", spo2, listOf("0 = < 90% sous O2", "1 = O2 requis SpO2 > 90%", "2 = > 95% air ambiant")) { spo2 = it }

        Spacer(modifier = Modifier.height(14.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
            border = BorderStroke(1.dp, if (result.canDischarge) colors.primary else colors.error)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "SCORE TOTAL : ${result.score} / 10",
                        color = if (result.canDischarge) colors.primary else colors.error,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                    Text(
                        if (result.canDischarge) "SORTIE AUTORISÉE" else "SURVEILLANCE CONTINUE",
                        color = if (result.canDischarge) colors.primary else colors.error,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(result.decision, color = colors.onSurface, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun AllowableBloodLossCalculator() {
    var weightText by remember { mutableStateOf("70") }
    var initHct by remember { mutableStateOf("38") }
    var targetHct by remember { mutableStateOf("28") }
    var isFemale by remember { mutableStateOf(false) }

    val w = weightText.toDoubleOrNull() ?: 0.0
    val h1 = initHct.toDoubleOrNull() ?: 0.0
    val h2 = targetHct.toDoubleOrNull() ?: 0.0
    val abl = remember(w, h1, h2, isFemale) { ClinicalCalculators.calculateAllowableBloodLoss(w, h1, h2, isFemale) }
    val colors = MaterialTheme.colorScheme

    Column {
        Text("PERTES SANGUINES MAXIMALES ADMISSIBLES (ABL)", color = colors.primary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text("Formule de Gross : estimation du volume de saignement toléré avant transfusion.", color = colors.onSurfaceVariant, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = weightText,
            onValueChange = { weightText = it },
            label = { Text("Poids (kg)", fontSize = 12.sp) },
            modifier = Modifier.fillMaxWidth(),
            colors = dynamicOutlinedColors()
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = initHct,
                onValueChange = { initHct = it },
                label = { Text("Hématocrite initial (%)", fontSize = 12.sp) },
                modifier = Modifier.weight(1f),
                colors = dynamicOutlinedColors()
            )
            OutlinedTextField(
                value = targetHct,
                onValueChange = { targetHct = it },
                label = { Text("Hématocrite seuil (%)", fontSize = 12.sp) },
                modifier = Modifier.weight(1f),
                colors = dynamicOutlinedColors()
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
            border = BorderStroke(1.dp, colors.primary)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("VOLUME SANGUIN TOLÉRÉ (ABL)", color = colors.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text("$abl mL", color = colors.onSurface, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                Text(
                    "Volume sanguin estimé total : ${(w * if (isFemale) 65 else 70).roundToInt()} mL. Au-delà de $abl mL de pertes, une transfusion de Concentrés de Globules Rouges (CGR) est à envisager.",
                    color = colors.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun PediatricFormulasCalculator() {
    var ageYearsText by remember { mutableStateOf("6") }
    val age = ageYearsText.toIntOrNull() ?: 1

    val estWeight = 2 * (age + 4)
    val uncuffedTube = (age / 4.0) + 4.0
    val cuffedTube = uncuffedTube - 0.5
    val tubeDepth = (age / 2.0) + 12.0
    val colors = MaterialTheme.colorScheme

    Column {
        Text("RÉFÉRENCES PÉDIATRIQUES D'ANESTHÉSIE", color = colors.primary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = ageYearsText,
            onValueChange = { ageYearsText = it },
            label = { Text("Âge de l'enfant (années)", fontSize = 12.sp) },
            modifier = Modifier.fillMaxWidth(),
            colors = dynamicOutlinedColors()
        )

        Spacer(modifier = Modifier.height(14.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
            border = BorderStroke(1.dp, colors.tertiary)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("ESTIMATIONS POUR ENFANT DE $age ANS", color = colors.tertiary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))

                ResultRow("Poids estimé", "$estWeight kg", "Formule : 2 x (Âge + 4)")
                ResultRow("Taille sonde sans ballonnet", "N° $uncuffedTube", "Formule de Motoyama : Âge/4 + 4")
                ResultRow("Taille sonde à ballonnet", "N° $cuffedTube", "Taille sans ballonnet - 0.5")
                ResultRow("Repère d'insertion à l'arcade", "$tubeDepth cm", "Formule : Âge/2 + 12")
                ResultRow("Volume courant (Vt 6-8 mL/kg)", "${estWeight * 6} à ${estWeight * 8} mL", "Ventilation protectrice")
            }
        }
    }
}

@Composable
fun ResultRow(label: String, value: String, hint: String) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = colors.onSurface, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(hint, color = colors.onSurfaceVariant, fontSize = 10.sp)
        }
        Text(value, color = colors.primary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@Composable
fun CheckRow(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = colors.primary,
                checkmarkColor = colors.onPrimary,
                uncheckedColor = colors.outline
            )
        )
        Text(text, color = colors.onSurface, fontSize = 12.sp)
    }
}

@Composable
fun ScoreSelector(
    label: String,
    selectedScore: Int,
    options: List<String>,
    onSelect: (Int) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, color = colors.onSurface, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            options.forEachIndexed { idx, opt ->
                FilterChip(
                    selected = selectedScore == idx,
                    onClick = { onSelect(idx) },
                    label = { Text(opt, fontSize = 10.sp) },
                    colors = filterChipColors()
                )
            }
        }
    }
}

@Composable
fun filterChipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = MaterialTheme.colorScheme.primary,
    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
    containerColor = MaterialTheme.colorScheme.surfaceVariant,
    labelColor = MaterialTheme.colorScheme.onSurface
)
