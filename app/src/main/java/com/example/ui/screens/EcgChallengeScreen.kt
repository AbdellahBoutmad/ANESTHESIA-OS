package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.theme.MedicalTeal
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SoftWhite
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningOrange

data class EcgCase(
    val id: Int,
    val title: String,
    val hrBpm: Int,
    val isEmergency: Boolean,
    val options: List<String>,
    val correctRhythmIndex: Int,
    val semiology: String,
    val clinicalConduct: String,
    val patternType: String // "SINUS", "AFIB", "VT", "VF", "TORSADES", "AVB3", "STEMI", "HYPERKALEMIA", "FLUTTER", "ASYSTOLE"
)

val ecgCases = listOf(
    EcgCase(
        id = 1,
        title = "Patient 1 : Bilan peropératoire sous AG",
        hrBpm = 75,
        isEmergency = false,
        options = listOf("Rythme Sinusal Normal", "Fibrillation Atriale", "Tachycardie Jonctionnelle", "Flutter 2:1"),
        correctRhythmIndex = 0,
        semiology = "Ondes P d'origine sinusale visibles avant chaque QRS, intervalle PR constant à 160 ms, QRS fins (< 80 ms), repolarisation ST/T normale.",
        clinicalConduct = "Rythme physiologique normal. Poursuite de la surveillance standard (Scope continu, FC et PA régulières).",
        patternType = "SINUS"
    ),
    EcgCase(
        id = 2,
        title = "Patient 2 : Chute brutale de TA et pouls filant",
        hrBpm = 180,
        isEmergency = true,
        options = listOf("Tachycardie Sinusale", "Tachycardie Ventriculaire Monomorphe", "Bloc de Branche Gauche", "Flutter Auriculaire"),
        correctRhythmIndex = 1,
        semiology = "Tachycardie régulière à complexes QRS très larges (> 140 ms), dissociation ventriculo-atriale, absence d'onde P sinusale.",
        clinicalConduct = "URGENCE VITALE ! Si arrêt sans pouls -> Défibrillation 200J immédiate + MCE. Si pouls présent mais instable -> Cardioversion électrique synchronisée (100-200J) sous sédation + Amiodarone 300 mg IV.",
        patternType = "VT"
    ),
    EcgCase(
        id = 3,
        title = "Patient 3 : Alarme monitorage au réveil",
        hrBpm = 145,
        isEmergency = true,
        options = listOf("Fibrillation Atriale Rapide (AC/FA)", "Extrasystoles Auriculaires", "Torsades de Pointes", "Rythme Sinusal Tachycarde"),
        correctRhythmIndex = 0,
        semiology = "Absence complète d'ondes P, présence d'oscillations irrégulières de la ligne de base (ondes f), réponse ventriculaire totalement anarchique et irrégulière.",
        clinicalConduct = "Vérifier la tolérance hémodynamique (PAM). Recherche d'étiologie : hypoxie, douleur, hypovolémie, hypokaliémie. Contrôle de la cadence ventriculaire (Bêtabloquant ou Amiodarone après avis MAR).",
        patternType = "AFIB"
    ),
    EcgCase(
        id = 4,
        title = "Patient 4 : Perte de signal pulsatile per-incision",
        hrBpm = 0,
        isEmergency = true,
        options = listOf("Artefact de bistouri électrique", "Fibrillation Ventriculaire (FV)", "Asystolie", "Tachycardie Sinusale"),
        correctRhythmIndex = 1,
        semiology = "Ondulations anarchiques rapides et irrégulières, sans aucun complexe QRS identifiable ni ligne isoélectrique.",
        clinicalConduct = "URGENCE ABSOLUE (ACR DÉFIBRILLABLE) ! Défibrillation immédiate non synchronisée à 200J biphasique, début immédiat du Massage Cardiaque Externe (MCE 100-120/min), Adrénaline 1 mg après 3e choc.",
        patternType = "VF"
    ),
    EcgCase(
        id = 5,
        title = "Patient 5 : Bradycardie sévère et hypotension post-rachianesthésie",
        hrBpm = 32,
        isEmergency = true,
        options = listOf("Bradycardie Sinusale", "Bloc Auriculo-Ventriculaire Complet (BAV 3)", "Rythme d'échappement sinusal", "Flutter avec bloc 4:1"),
        correctRhythmIndex = 1,
        semiology = "Dissociation auriculo-ventriculaire complète : ondes P régulières à 80/min totalement indépendantes des QRS réguliers et larges à 32/min (échappement ventriculaire).",
        clinicalConduct = "URGENCE VITALE ! Isoprénaline (Isuprel®) au PSE ou Atropine 1 mg IV en attente d'entraînement électrosystolique externe ou sonde de stimulation temporaire.",
        patternType = "AVB3"
    ),
    EcgCase(
        id = 6,
        title = "Patient 6 : Douleur thoracique aiguë en SSPI",
        hrBpm = 95,
        isEmergency = true,
        options = listOf("Repolarisation précoce", "Syndrome Coronarien Aigu ST+ (STEMI)", "Péricardite aiguë", "Bloc de branche droit"),
        correctRhythmIndex = 1,
        semiology = "Sus-décalage majeur du segment ST convexe vers le haut (onde de Pardee) dans le territoire antérieur avec image en miroir inférieure.",
        clinicalConduct = "URGENCE VITALE ! Alerte coronarographie immédiate H24 (SAMU/Cardiologie interventionnelle), oxygénothérapie si SpO2 < 90%, double antiagrégation plaquettaire (Aspirine + Ticagrélor/Prasugrel) et héparine selon protocole.",
        patternType = "STEMI"
    ),
    EcgCase(
        id = 7,
        title = "Patient 7 : Insuffisant rénal anurique en préopératoire",
        hrBpm = 60,
        isEmergency = true,
        options = listOf("Ischémie sous-endocardique", "Hyperkaliémie sévère menaçante", "Hypothermie (ondes J)", "Variante normale"),
        correctRhythmIndex = 1,
        semiology = "Ondes T géantes, pointues, symétriques en 'tente', avec aplatissement progressif des ondes P et élargissement menaçant des complexes QRS.",
        clinicalConduct = "URGENCE VITALE (Risque d'arrêt en FV ou asystolie) ! Protection myocardique immédiate par Chlorure de Calcium 10% (1g IV en 2-3 min), puis transfert cellulaire (Insuline 10 UI + Glucose 30% 50 mL) et hémodialyse en urgence.",
        patternType = "HYPERKALEMIA"
    ),
    EcgCase(
        id = 8,
        title = "Patient 8 : QT long congénital et syncope",
        hrBpm = 220,
        isEmergency = true,
        options = listOf("Torsades de Pointes", "Tachycardie Sinusale", "Fibrillation Atriale", "Extrasystoles polymorphes"),
        correctRhythmIndex = 0,
        semiology = "Tachycardie ventriculaire polymorphe avec torsion cyclique caractéristique de la pointe des complexes QRS autour de la ligne isoélectrique.",
        clinicalConduct = "URGENCE VITALE ! Sulfate de Magnésium (MgSO4) 2 g IVD lente en 2 minutes, accélération de la fréquence de base par Isoprénaline ou sonde, correction de l'hypokaliémie. Choc électrique externe si dégénérescence en FV.",
        patternType = "TORSADES"
    ),
    EcgCase(
        id = 9,
        title = "Patient 9 : Palpitations régulières à 150 bpm",
        hrBpm = 150,
        isEmergency = false,
        options = listOf("Flutter Auriculaire avec conduction 2:1", "Tachycardie Ventriculaire", "Rythme Jonctionnel", "Fibrillation Ventriculaire"),
        correctRhythmIndex = 0,
        semiology = "Activité atriale rapide et régulière à 300/min en 'dents de scie' (ondes F) sans retour à la ligne isoélectrique, conduction AV fixe 2:1 donnant une fréquence ventriculaire exacte à 150 bpm.",
        clinicalConduct = "Tolérance généralement conservée au repos. Ralentissement par bêtabloquants ou anticalciques bradycardisants, anticoagulation efficace avant toute cardioversion.",
        patternType = "FLUTTER"
    ),
    EcgCase(
        id = 10,
        title = "Patient 10 : Tracé plat persistant au bloc",
        hrBpm = 0,
        isEmergency = true,
        options = listOf("Asystolie confirmée", "Artefact de déconnexion", "Bloc sino-auriculaire", "Fibrillation fine"),
        correctRhythmIndex = 0,
        semiology = "Ligne isoélectrique plate sans activité ventriculaire mesurable après vérification des électrodes, des dérivations et du gain maximal.",
        clinicalConduct = "URGENCE ABSOLUE (ACR NON DÉFIBRILLABLE) ! MCE immédiat et continu (100-120/min), Adrénaline 1 mg IVD immédiate répétée toutes les 3-5 minutes, FiO2 100%, recherche des 4H et 4T.",
        patternType = "ASYSTOLE"
    )
)

@Composable
fun EcgChallengeScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedRhythm by remember { mutableStateOf<Int?>(null) }
    var selectedEmergency by remember { mutableStateOf<Boolean?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var totalAnswered by remember { mutableIntStateOf(0) }

    val currentCase = ecgCases[currentIndex]

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNightBlue)
            .testTag("ecg_challenge_screen"),
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
                        text = "ECG Challenge",
                        color = SoftWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Diagnostic rythmique & Conduite d'urgence",
                        color = SlateGray,
                        fontSize = 12.sp
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "Score : $score / $totalAnswered",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Case Title & Progress
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cas ${currentIndex + 1} / ${ecgCases.size} : ${currentCase.title}",
                    color = ChampagneGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "FC : ${if (currentCase.hrBpm > 0) "${currentCase.hrBpm} bpm" else "--"}",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // ECG Strip Canvas (Realistic Millimetric Grid)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A0A0E)),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color(0xFF4A1A24)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Dérivation DII • 25 mm/s • 10 mm/mV",
                            color = Color(0xFFEF9A9A),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Scope Anesthésie",
                            color = Color(0xFFB0BEC5),
                            fontSize = 10.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    EcgStripCanvas(patternType = currentCase.patternType)
                }
            }
        }

        // Question 1 : Quel est ce rythme ?
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DeepNavyElevated),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "1. Quel est ce rythme cardiaque ?",
                        color = SoftWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    currentCase.options.forEachIndexed { idx, option ->
                        val isSelected = selectedRhythm == idx
                        val isCorrectOption = idx == currentCase.correctRhythmIndex
                        val btnColor = when {
                            !isSubmitted && isSelected -> MaterialTheme.colorScheme.primary
                            isSubmitted && isCorrectOption -> SuccessGreen
                            isSubmitted && isSelected && !isCorrectOption -> AlertRed
                            else -> DeepNavySurface
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = btnColor.copy(alpha = if (isSelected || (isSubmitted && isCorrectOption)) 0.25f else 0.5f),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected || (isSubmitted && isCorrectOption)) btnColor else SlateGray.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable(enabled = !isSubmitted) { selectedRhythm = idx }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${('A' + idx)}. ",
                                    color = ChampagneGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = option,
                                    color = SoftWhite,
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                if (isSubmitted) {
                                    if (isCorrectOption) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                                    } else if (isSelected) {
                                        Icon(Icons.Default.Close, contentDescription = null, tint = AlertRed, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Question 2 : Est-ce une urgence vitale ?
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DeepNavyElevated),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "2. S'agit-il d'une urgence vitale immédiate ?",
                        color = SoftWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf(true to "OUI, Urgence Vitale Immédiate", false to "NON, Pas d'Urgence Vitale Immédiate").forEach { (isEmerg, label) ->
                            val isSelected = selectedEmergency == isEmerg
                            val isCorrectAnswer = isEmerg == currentCase.isEmergency
                            val borderColor = when {
                                !isSubmitted && isSelected -> MaterialTheme.colorScheme.primary
                                isSubmitted && isCorrectAnswer -> SuccessGreen
                                isSubmitted && isSelected && !isCorrectAnswer -> AlertRed
                                else -> SlateGray.copy(alpha = 0.3f)
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else DeepNavySurface,
                                border = BorderStroke(1.dp, borderColor),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable(enabled = !isSubmitted) { selectedEmergency = isEmerg }
                            ) {
                                Text(
                                    text = label,
                                    modifier = Modifier.padding(10.dp),
                                    fontSize = 11.sp,
                                    color = if (isSelected) SoftWhite else SlateGray,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }

        // Action Buttons
        item {
            if (!isSubmitted) {
                Button(
                    onClick = {
                        isSubmitted = true
                        totalAnswered++
                        if (selectedRhythm == currentCase.correctRhythmIndex && selectedEmergency == currentCase.isEmergency) {
                            score++
                        }
                    },
                    enabled = selectedRhythm != null && selectedEmergency != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Valider la Réponse", color = DarkNightBlue, fontWeight = FontWeight.Bold)
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            selectedRhythm = null
                            selectedEmergency = null
                            isSubmitted = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = SoftWhite)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Recommencer", color = SoftWhite)
                    }

                    Button(
                        onClick = {
                            currentIndex = (currentIndex + 1) % ecgCases.size
                            selectedRhythm = null
                            selectedEmergency = null
                            isSubmitted = false
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Cas Suivant", color = DarkNightBlue, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = DarkNightBlue)
                    }
                }
            }
        }

        // Detailed Clinical Explanation & Management (Displayed after submission)
        if (isSubmitted) {
            val isFullSuccess = selectedRhythm == currentCase.correctRhythmIndex && selectedEmergency == currentCase.isEmergency
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isFullSuccess) SuccessGreen.copy(alpha = 0.15f) else AlertRed.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (isFullSuccess) SuccessGreen else AlertRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (isFullSuccess) Icons.Default.Check else Icons.Default.Close,
                                contentDescription = null,
                                tint = if (isFullSuccess) SuccessGreen else AlertRed
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isFullSuccess) "EXCELLENT DIAGNOSTIC CLINIQUE !" else "ATTENTION : CORRECTION NÉCESSAIRE",
                                color = if (isFullSuccess) SuccessGreen else AlertRed,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                        }

                        Text(
                            text = "Diagnostic exact : ${currentCase.options[currentCase.correctRhythmIndex]}",
                            color = SoftWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )

                        Text(
                            text = "Urgence vitale : ${if (currentCase.isEmergency) "OUI, Prise en charge immédiate !" else "Non, surveillance standard."}",
                            color = if (currentCase.isEmergency) AlertRed else ChampagneGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )

                        HorizontalDivider(color = SlateGray.copy(alpha = 0.3f))

                        Text(
                            text = "Sémiologie Électrocardiographique :",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Text(
                            text = currentCase.semiology,
                            color = SoftWhite,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )

                        Text(
                            text = "Conduite à Tenir Immédiate (Bloc / Réanimation) :",
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Text(
                            text = currentCase.clinicalConduct,
                            color = SoftWhite,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EcgStripCanvas(patternType: String) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(Color(0xFF120306), RoundedCornerShape(6.dp))
    ) {
        val width = size.width
        val height = size.height

        // Millimetric ECG Grid
        // Thin lines (1 mm approx = 5 dp)
        val thinGridColor = Color(0xFF330C12)
        val thickGridColor = Color(0xFF5A1822)

        val mmStep = 10f
        var xGrid = 0f
        while (xGrid < width) {
            val isThick = ((xGrid / mmStep).toInt() % 5) == 0
            drawLine(
                color = if (isThick) thickGridColor else thinGridColor,
                start = Offset(xGrid, 0f),
                end = Offset(xGrid, height),
                strokeWidth = if (isThick) 1.2f else 0.6f
            )
            xGrid += mmStep
        }

        var yGrid = 0f
        while (yGrid < height) {
            val isThick = ((yGrid / mmStep).toInt() % 5) == 0
            drawLine(
                color = if (isThick) thickGridColor else thinGridColor,
                start = Offset(0f, yGrid),
                end = Offset(width, yGrid),
                strokeWidth = if (isThick) 1.2f else 0.6f
            )
            yGrid += mmStep
        }

        // Draw Heart Tracing (Electric Green/Yellow)
        val ecgPath = Path()
        val midY = height * 0.52f
        ecgPath.moveTo(0f, midY)

        when (patternType) {
            "SINUS" -> {
                val cycleWidth = width / 4f
                for (i in 0..3) {
                    val startX = i * cycleWidth
                    // Isoelectric
                    ecgPath.lineTo(startX + 20f, midY)
                    // P wave
                    ecgPath.quadraticBezierTo(startX + 30f, midY - 14f, startX + 40f, midY)
                    // PR segment
                    ecgPath.lineTo(startX + 60f, midY)
                    // Q dip
                    ecgPath.lineTo(startX + 65f, midY + 8f)
                    // R peak
                    ecgPath.lineTo(startX + 75f, midY - 65f)
                    // S dip
                    ecgPath.lineTo(startX + 85f, midY + 18f)
                    // ST segment
                    ecgPath.lineTo(startX + 110f, midY)
                    // T wave
                    ecgPath.quadraticBezierTo(startX + 130f, midY - 22f, startX + 150f, midY)
                    ecgPath.lineTo(startX + cycleWidth, midY)
                }
            }
            "VT" -> {
                // Wide QRS tachycardia
                val cycleWidth = width / 7f
                for (i in 0..6) {
                    val startX = i * cycleWidth
                    ecgPath.lineTo(startX + 5f, midY + 25f)
                    ecgPath.lineTo(startX + (cycleWidth * 0.45f), midY - 60f)
                    ecgPath.lineTo(startX + (cycleWidth * 0.9f), midY + 30f)
                    ecgPath.lineTo(startX + cycleWidth, midY)
                }
            }
            "AFIB" -> {
                // Irregular baseline and random intervals
                val intervals = listOf(50f, 95f, 60f, 110f, 70f, 130f, 80f)
                var currentX = 0f
                intervals.forEach { span ->
                    // Fibrillatory waves
                    ecgPath.lineTo(currentX + 8f, midY - 4f)
                    ecgPath.lineTo(currentX + 16f, midY + 5f)
                    ecgPath.lineTo(currentX + 24f, midY - 3f)
                    // QRS
                    ecgPath.lineTo(currentX + 28f, midY + 6f)
                    ecgPath.lineTo(currentX + 34f, midY - 60f)
                    ecgPath.lineTo(currentX + 40f, midY + 12f)
                    // T wave
                    ecgPath.quadraticBezierTo(currentX + 50f, midY - 16f, currentX + 60f, midY)
                    currentX += span
                    if (currentX < width) ecgPath.lineTo(currentX, midY)
                }
            }
            "VF" -> {
                // Chaotic sine-like waveforms without QRS
                var cx = 0f
                var flip = 1
                while (cx < width) {
                    val step = kotlin.random.Random.Default.nextFloat() * 15f + 20f
                    val amp = (kotlin.random.Random.Default.nextFloat() * 30f + 25f) * flip
                    ecgPath.quadraticBezierTo(cx + (step / 2f), midY + amp, cx + step, midY)
                    cx += step
                    flip = -flip
                }
            }
            "STEMI" -> {
                val cycleWidth = width / 4f
                for (i in 0..3) {
                    val startX = i * cycleWidth
                    ecgPath.lineTo(startX + 20f, midY)
                    // P wave
                    ecgPath.quadraticBezierTo(startX + 30f, midY - 12f, startX + 40f, midY)
                    // Q wave (pathological)
                    ecgPath.lineTo(startX + 60f, midY + 22f)
                    // R peak
                    ecgPath.lineTo(startX + 75f, midY - 45f)
                    // S wave not returning: HUGE ST ELEVATION (Pardee wave)
                    ecgPath.lineTo(startX + 85f, midY - 35f)
                    ecgPath.quadraticBezierTo(startX + 120f, midY - 38f, startX + 145f, midY)
                    ecgPath.lineTo(startX + cycleWidth, midY)
                }
            }
            "HYPERKALEMIA" -> {
                val cycleWidth = width / 3.5f
                for (i in 0..3) {
                    val startX = i * cycleWidth
                    ecgPath.lineTo(startX + 30f, midY) // flattened P wave
                    // Widened QRS
                    ecgPath.lineTo(startX + 40f, midY + 10f)
                    ecgPath.lineTo(startX + 55f, midY - 50f)
                    ecgPath.lineTo(startX + 70f, midY + 15f)
                    // Giant peaked symmetrical T wave
                    ecgPath.lineTo(startX + 90f, midY)
                    ecgPath.lineTo(startX + 105f, midY - 65f) // Peaked T
                    ecgPath.lineTo(startX + 120f, midY)
                    ecgPath.lineTo(startX + cycleWidth, midY)
                }
            }
            "ASYSTOLE" -> {
                // Flat line with subtle baseline wander
                ecgPath.lineTo(width * 0.2f, midY)
                ecgPath.lineTo(width * 0.4f, midY + 2f)
                ecgPath.lineTo(width * 0.7f, midY - 2f)
                ecgPath.lineTo(width, midY)
            }
            else -> {
                // Default regular sinus
                val cycleWidth = width / 4f
                for (i in 0..3) {
                    val startX = i * cycleWidth
                    ecgPath.lineTo(startX + 20f, midY)
                    ecgPath.quadraticBezierTo(startX + 30f, midY - 12f, startX + 40f, midY)
                    ecgPath.lineTo(startX + 60f, midY)
                    ecgPath.lineTo(startX + 70f, midY - 55f)
                    ecgPath.lineTo(startX + 80f, midY + 14f)
                    ecgPath.lineTo(startX + 100f, midY)
                    ecgPath.quadraticBezierTo(startX + 120f, midY - 18f, startX + 140f, midY)
                    ecgPath.lineTo(startX + cycleWidth, midY)
                }
            }
        }

        drawPath(
            path = ecgPath,
            color = Color(0xFF00FF66), // Classic Phosphor ECG Green
            style = Stroke(width = 2.2f)
        )
    }
}
