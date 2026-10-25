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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AlertRed
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.DarkNightBlue
import com.example.ui.theme.MedicalCyan
import com.example.ui.theme.MedicalNavy
import com.example.ui.theme.MedicalNavyElevated
import com.example.ui.theme.MedicalNavySurface
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SecondaryGrey
import com.example.ui.theme.TurquoiseGreen
import com.example.ui.theme.WarningOrange
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNightBlue)
            .testTag("calculators_screen")
    ) {
        // Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MedicalNavy,
            contentColor = MedicalCyan,
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
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    }
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp)
        ) {
            when (selectedTabIndex) {
                0 -> item { BmiAndIdealWeightCalculator() }
                1 -> item { CockcroftCreatinineCalculator() }
                2 -> item { ApfelPonvCalculator() }
                3 -> item { StopBangCalculator() }
                4 -> item { AldretePacuCalculator() }
                5 -> item { AllowableBloodLossCalculator() }
                6 -> item { PediatricFormulasCalculator() }
            }
        }
    }
}

@Composable
fun BmiAndIdealWeightCalculator() {
    var weightText by remember { mutableStateOf("75") }
    var heightText by remember { mutableStateOf("175") }
    var isFemale by remember { mutableStateOf(false) }

    val w = weightText.toDoubleOrNull() ?: 0.0
    val h = heightText.toDoubleOrNull() ?: 0.0
    val result = remember(w, h, isFemale) { ClinicalCalculators.calculateBmi(w, h, isFemale) }

    Column {
        Text("CALCUL DE L'IMC & POIDS THÉORIQUES", color = MedicalCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = weightText,
                onValueChange = { weightText = it },
                label = { Text("Poids réel (kg)") },
                modifier = Modifier.weight(1f),
                colors = outlinedColors()
            )
            OutlinedTextField(
                value = heightText,
                onValueChange = { heightText = it },
                label = { Text("Taille (cm)") },
                modifier = Modifier.weight(1f),
                colors = outlinedColors()
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
            colors = CardDefaults.cardColors(containerColor = MedicalNavy),
            border = BorderStroke(1.dp, MedicalCyan)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("RÉSULTATS BIOMÉTRIQUES", color = MedicalCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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

    Column {
        Text("ESTIMATION DE LA CLAIRANCE DE LA CRÉATININE", color = MedicalCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = ageText,
                onValueChange = { ageText = it },
                label = { Text("Âge (ans)") },
                modifier = Modifier.weight(1f),
                colors = outlinedColors()
            )
            OutlinedTextField(
                value = weightText,
                onValueChange = { weightText = it },
                label = { Text("Poids (kg)") },
                modifier = Modifier.weight(1f),
                colors = outlinedColors()
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = crText,
            onValueChange = { crText = it },
            label = { Text("Créatininémie (µmol/L)") },
            modifier = Modifier.fillMaxWidth(),
            colors = outlinedColors()
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
            colors = CardDefaults.cardColors(containerColor = MedicalNavy),
            border = BorderStroke(1.dp, TurquoiseGreen)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("CLAIRANCE (COCKCROFT & GAULT)", color = TurquoiseGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text("${result.clearanceMlMin} mL/min", color = PureWhite, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                Text(result.stage, color = MedicalCyan, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(result.doseAdjustmentRecommendation, color = SecondaryGrey, fontSize = 12.sp)
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

    Column {
        Text("SCORE D'APFEL — RISQUE DE NAUSÉES ET VOMISSEMENTS (NVPO)", color = MedicalCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(10.dp))

        CheckRow("Sexe féminin (+1)", isFemale) { isFemale = it }
        CheckRow("Patient non-fumeur (+1)", isNonSmoker) { isNonSmoker = it }
        CheckRow("Antécédent de NVPO ou de mal des transports (+1)", historyPonv) { historyPonv = it }
        CheckRow("Utilisation prévue d'opioïdes postopératoires (+1)", opioidsPlanned) { opioidsPlanned = it }

        Spacer(modifier = Modifier.height(14.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MedicalNavy),
            border = BorderStroke(1.dp, WarningOrange)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("SCORE TOTAL : ${result.score} / 4", color = WarningOrange, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Risque estimé : ${result.riskPercentage}%", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(result.riskLevel, color = PureWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Conduite préventive recommandée :", color = TurquoiseGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(result.recommendation, color = SecondaryGrey, fontSize = 12.sp)
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

    Column {
        Text("SCORE STOP-BANG — DÉPISTAGE DU SYNDROME D'APNÉE DU SOMMEIL (SAOS)", color = MedicalCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
            colors = CardDefaults.cardColors(containerColor = MedicalNavy),
            border = BorderStroke(1.dp, if (result.score >= 5) AlertRed else MedicalCyan)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("SCORE : ${result.score} / 8", color = if (result.score >= 5) AlertRed else MedicalCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(result.riskLevel, color = PureWhite, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(result.recommendation, color = SecondaryGrey, fontSize = 12.sp)
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

    Column {
        Text("SCORE D'ALDRETE MODIFIÉ — CRITÈRES DE SORTIE DE SSPI", color = MedicalCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(10.dp))

        ScoreSelector("1. Activité motrice volontaire :", motor, listOf("0 = Immobile", "1 = Bouge 2 membres", "2 = Bouge les 4 membres")) { motor = it }
        ScoreSelector("2. Respiration & toux :", resp, listOf("0 = Apnée", "1 = Dyspnée modérée", "2 = Respiration ample & toux")) { resp = it }
        ScoreSelector("3. Pression artérielle :", circ, listOf("0 = Écart > 50%", "1 = Écart 20-50%", "2 = Écart < 20% réf.")) { circ = it }
        ScoreSelector("4. Conscience :", consci, listOf("0 = Non réveillable", "1 = Réveillable", "2 = Parfaitement lucide")) { consci = it }
        ScoreSelector("5. Saturation SpO2 :", spo2, listOf("0 = < 90% sous O2", "1 = O2 requis SpO2 > 90%", "2 = > 95% air ambiant")) { spo2 = it }

        Spacer(modifier = Modifier.height(14.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MedicalNavy),
            border = BorderStroke(1.dp, if (result.canDischarge) TurquoiseGreen else AlertRed)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("SCORE TOTAL : ${result.score} / 10", color = if (result.canDischarge) TurquoiseGreen else AlertRed, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    Text(if (result.canDischarge) "AUTORISÉE" else "REFUSÉE", color = if (result.canDischarge) TurquoiseGreen else AlertRed, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(result.decision, color = PureWhite, fontSize = 12.sp)
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

    Column {
        Text("PERTES SANGUINES MAXIMALES ADMISSIBLES (ABL)", color = MedicalCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text("Formule de Gross : estimation du volume de saignement toléré avant transfusion.", color = SecondaryGrey, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = weightText,
            onValueChange = { weightText = it },
            label = { Text("Poids (kg)") },
            modifier = Modifier.fillMaxWidth(),
            colors = outlinedColors()
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = initHct,
                onValueChange = { initHct = it },
                label = { Text("Hématocrite initial (%)") },
                modifier = Modifier.weight(1f),
                colors = outlinedColors()
            )
            OutlinedTextField(
                value = targetHct,
                onValueChange = { targetHct = it },
                label = { Text("Hématocrite seuil (%)") },
                modifier = Modifier.weight(1f),
                colors = outlinedColors()
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MedicalNavy),
            border = BorderStroke(1.dp, MedicalCyan)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("VOLUME SANGUIN TOLÉRÉ (ABL)", color = MedicalCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text("$abl mL", color = PureWhite, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                Text(
                    "Volume sanguin estimé total : ${(w * if (isFemale) 65 else 70).roundToInt()} mL. Au-delà de $abl mL de pertes, une transfusion de Concentrés de Globules Rouges (CGR) est à envisager.",
                    color = SecondaryGrey,
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

    Column {
        Text("RÉFÉRENCES PÉDIATRIQUES D'ANESTHÉSIE", color = MedicalCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = ageYearsText,
            onValueChange = { ageYearsText = it },
            label = { Text("Âge de l'enfant (années)") },
            modifier = Modifier.fillMaxWidth(),
            colors = outlinedColors()
        )

        Spacer(modifier = Modifier.height(14.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MedicalNavy),
            border = BorderStroke(1.dp, TurquoiseGreen)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("ESTIMATIONS POUR ENFANT DE $age ANS", color = TurquoiseGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = PureWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(hint, color = SecondaryGrey, fontSize = 10.sp)
        }
        Text(value, color = MedicalCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@Composable
fun CheckRow(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
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
            colors = CheckboxDefaults.colors(checkedColor = MedicalCyan)
        )
        Text(text, color = PureWhite, fontSize = 12.sp)
    }
}

@Composable
fun ScoreSelector(
    label: String,
    selectedScore: Int,
    options: List<String>,
    onSelect: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(label, color = PureWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
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
    selectedContainerColor = MedicalCyan,
    selectedLabelColor = DarkNightBlue,
    containerColor = MedicalNavySurface,
    labelColor = PureWhite
)
