package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.data.local.PatientAssessment
import com.example.ui.MainViewModel
import com.example.ui.SubScreen
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

@Composable
fun PatientsScreen(viewModel: MainViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    val savedPatients by viewModel.savedAssessments.collectAsState()
    val activeSubScreen by viewModel.activeSubScreen.collectAsState()

    // If wizard is requested from sub-screen, display it directly
    if (activeSubScreen == SubScreen.CONSULTATION_WIZARD || selectedTab == 1) {
        ConsultationWizard(
            viewModel = viewModel,
            onClose = {
                viewModel.closeSubScreen()
                selectedTab = 0
            }
        )
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkNightBlue)
        ) {
            // Header Tab row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MedicalNavy,
                contentColor = MedicalCyan,
                divider = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "Dossiers Patients (${savedPatients.size})",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        viewModel.resetWizard()
                        selectedTab = 1
                    },
                    text = {
                        Text(
                            "+ Nouvelle Évaluation",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("patients_list"),
                contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp)
            ) {
                item {
                    Text(
                        "CONSULTATIONS PRÉ-ANESTHÉSIQUES ENREGISTRÉES",
                        color = SecondaryGrey,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (savedPatients.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MedicalNavySurface),
                            border = BorderStroke(1.dp, BorderSubtle)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Person, null, tint = MedicalCyan, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Aucune consultation enregistrée", color = PureWhite, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "Créez une évaluation pré-anesthésique complète en 5 étapes pour calculer automatiquement l'IMC, le score ASA et les risques.",
                                    color = SecondaryGrey,
                                    fontSize = 12.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { selectedTab = 1 },
                                    colors = ButtonDefaults.buttonColors(containerColor = MedicalCyan, contentColor = DarkNightBlue)
                                ) {
                                    Text("Lancer l'évaluation préopératoire", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    items(savedPatients) { patient ->
                        PatientCardItem(patient = patient, onDelete = { viewModel.deleteAssessment(patient) })
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }

        // Floating Action Button to launch assessment
        FloatingActionButton(
            onClick = {
                viewModel.resetWizard()
                selectedTab = 1
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 20.dp)
                .testTag("fab_add_patient"),
            containerColor = MedicalCyan,
            contentColor = DarkNightBlue
        ) {
            Icon(Icons.Default.Add, contentDescription = "Nouvelle évaluation")
        }
    }
}

@Composable
fun PatientCardItem(
    patient: PatientAssessment,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MedicalNavy),
        border = BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            patient.patientName,
                            color = PureWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "(${patient.patientId})",
                            color = SecondaryGrey,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "${patient.age} ans • ${patient.gender} • ${patient.weightKg} kg (${patient.heightCm} cm) • IMC : ${patient.bmi}",
                        color = SecondaryGrey,
                        fontSize = 12.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MedicalNavyElevated,
                    border = BorderStroke(1.dp, MedicalCyan)
                ) {
                    Text(
                        patient.asaScore,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MedicalCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Intervention : ${patient.surgery}",
                    color = TurquoiseGreen,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Détails",
                        tint = SecondaryGrey
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MedicalNavySurface,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Voies Aériennes : ${patient.mallampati} • Ouverture : ${patient.mouthOpening}", color = PureWhite, fontSize = 12.sp)
                            Text("Allergies : ${patient.allergies.ifBlank { "Aucune" }}", color = if (patient.allergies.isNotBlank() && !patient.allergies.contains("Aucun", ignoreCase = true)) AlertRed else PureWhite, fontSize = 12.sp)
                            Text("Traitements : ${patient.currentTreatments.ifBlank { "Néant" }}", color = SecondaryGrey, fontSize = 12.sp)
                            Text("Synthèse : ${patient.summaryNotes}", color = MedicalCyan, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = onDelete,
                            border = BorderStroke(1.dp, AlertRed.copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertRed),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Supprimer", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConsultationWizard(
    viewModel: MainViewModel,
    onClose: () -> Unit
) {
    val step by viewModel.wizardStep.collectAsState()

    val pId by viewModel.wizPatientId.collectAsState()
    val name by viewModel.wizName.collectAsState()
    val age by viewModel.wizAge.collectAsState()
    val gender by viewModel.wizGender.collectAsState()
    val weight by viewModel.wizWeight.collectAsState()
    val height by viewModel.wizHeight.collectAsState()
    val surgery by viewModel.wizSurgery.collectAsState()
    val surgeryType by viewModel.wizSurgeryType.collectAsState()
    val urgency by viewModel.wizUrgency.collectAsState()

    val medHistory by viewModel.wizMedicalHistory.collectAsState()
    val surgHistory by viewModel.wizSurgicalHistory.collectAsState()
    val anesthHistory by viewModel.wizAnesthesiaHistory.collectAsState()
    val allergies by viewModel.wizAllergies.collectAsState()
    val treatments by viewModel.wizTreatments.collectAsState()
    val fasting by viewModel.wizFastingStatus.collectAsState()

    val cvExam by viewModel.wizCvExam.collectAsState()
    val respExam by viewModel.wizRespExam.collectAsState()
    val mallampati by viewModel.wizMallampati.collectAsState()
    val mouthOpening by viewModel.wizMouthOpening.collectAsState()
    val thyromental by viewModel.wizThyromental.collectAsState()

    val asaScore by viewModel.wizAsaScore.collectAsState()
    val diffAirway by viewModel.wizDifficultAirwayRisk.collectAsState()
    val aspirationRisk by viewModel.wizAspirationRisk.collectAsState()
    val summaryNotes by viewModel.wizSummaryNotes.collectAsState()

    val weightVal = weight.toDoubleOrNull() ?: 70.0
    val heightVal = height.toDoubleOrNull() ?: 170.0
    val bmiResult = remember(weightVal, heightVal, gender) {
        ClinicalCalculators.calculateBmi(weightVal, heightVal, gender == "Femme")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNightBlue)
    ) {
        // Top header with Back and step progress
        Surface(
            color = MedicalNavy,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onClose) {
                            Icon(Icons.Default.ArrowBack, "Retour", tint = MedicalCyan)
                        }
                        Text(
                            "Évaluation Pré-anesthésique",
                            color = PureWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Text("Étape $step / 5", color = MedicalCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { step / 5f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MedicalCyan,
                    trackColor = MedicalNavySurface,
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // STEP 1 : IDENTIFICATION
            if (step == 1) {
                item {
                    Text("ÉTAPE 1 : IDENTIFICATION DU PATIENT & CHIRURGIE", color = MedicalCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { viewModel.wizName.value = it },
                        label = { Text("Nom et Prénom du patient") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedColors()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = age,
                            onValueChange = { viewModel.wizAge.value = it },
                            label = { Text("Âge (ans)") },
                            modifier = Modifier.weight(1f),
                            colors = outlinedColors()
                        )
                        OutlinedTextField(
                            value = pId,
                            onValueChange = { viewModel.wizPatientId.value = it },
                            label = { Text("Identifiant IPP") },
                            modifier = Modifier.weight(1.5f),
                            colors = outlinedColors()
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    // Sexe
                    Text("Sexe :", color = SecondaryGrey, fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        listOf("Homme", "Femme").forEach { s ->
                            FilterChip(
                                selected = gender == s,
                                onClick = { viewModel.wizGender.value = s },
                                label = { Text(s) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MedicalCyan,
                                    selectedLabelColor = DarkNightBlue,
                                    containerColor = MedicalNavySurface,
                                    labelColor = PureWhite
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    // Poids et Taille
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = weight,
                            onValueChange = { viewModel.wizWeight.value = it },
                            label = { Text("Poids (kg)") },
                            modifier = Modifier.weight(1f),
                            colors = outlinedColors()
                        )
                        OutlinedTextField(
                            value = height,
                            onValueChange = { viewModel.wizHeight.value = it },
                            label = { Text("Taille (cm)") },
                            modifier = Modifier.weight(1f),
                            colors = outlinedColors()
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    // IMC Calculé automatiquement
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MedicalNavySurface,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, BorderSubtle)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Indice de Masse Corporelle (IMC) :", color = SecondaryGrey, fontSize = 11.sp)
                                Text("${bmiResult.bmi} kg/m² — ${bmiResult.category}", color = TurquoiseGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Text("Poids idéal Devine : ${bmiResult.idealWeightDevine} kg", color = MedicalCyan, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = surgery,
                        onValueChange = { viewModel.wizSurgery.value = it },
                        label = { Text("Intervention chirurgicale prévue") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedColors()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Degré d'urgence :", color = SecondaryGrey, fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Programmée", "Urgence différée", "Urgence vitale").forEach { u ->
                            FilterChip(
                                selected = urgency == u,
                                onClick = { viewModel.wizUrgency.value = u },
                                label = { Text(u, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = if (u.contains("vitale")) AlertRed else MedicalCyan,
                                    selectedLabelColor = if (u.contains("vitale")) PureWhite else DarkNightBlue,
                                    containerColor = MedicalNavySurface,
                                    labelColor = PureWhite
                                )
                            )
                        }
                    }
                }
            }

            // STEP 2 : INTERROGATOIRE
            if (step == 2) {
                item {
                    Text("ÉTAPE 2 : INTERROGATOIRE ET ANTÉCÉDENTS", color = MedicalCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = medHistory,
                        onValueChange = { viewModel.wizMedicalHistory.value = it },
                        label = { Text("Antécédents médicaux (HTA, diabète, asthme, coronaropathie...)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedColors(),
                        minLines = 2
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = surgHistory,
                        onValueChange = { viewModel.wizSurgicalHistory.value = it },
                        label = { Text("Antécédents chirurgicaux") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedColors()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = anesthHistory,
                        onValueChange = { viewModel.wizAnesthesiaHistory.value = it },
                        label = { Text("Antécédents anesthésiques & intubation") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedColors()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = allergies,
                        onValueChange = { viewModel.wizAllergies.value = it },
                        label = { Text("Allergies (Médicaments, latex, aliments)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedColors()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = treatments,
                        onValueChange = { viewModel.wizTreatments.value = it },
                        label = { Text("Traitements habituels (Anticoagulants, IEC/ARA2, bêtabloquants...)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedColors()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = fasting,
                        onValueChange = { viewModel.wizFastingStatus.value = it },
                        label = { Text("État de jeûne") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedColors()
                    )
                }
            }

            // STEP 3 : EXAMEN CLINIQUE
            if (step == 3) {
                item {
                    Text("ÉTAPE 3 : EXAMEN CLINIQUE & VOIES AÉRIENNES", color = MedicalCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = cvExam,
                        onValueChange = { viewModel.wizCvExam.value = it },
                        label = { Text("Examen cardiovasculaire (PA, FC, auscultation)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedColors()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = respExam,
                        onValueChange = { viewModel.wizRespExam.value = it },
                        label = { Text("Examen respiratoire (Murmure, râles, auscultation)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedColors()
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Score de Mallampati :", color = TurquoiseGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Classe I", "Classe II", "Classe III", "Classe IV").forEach { m ->
                            FilterChip(
                                selected = mallampati == m,
                                onClick = { viewModel.wizMallampati.value = m },
                                label = { Text(m, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = if (m.contains("III") || m.contains("IV")) WarningOrange else MedicalCyan,
                                    selectedLabelColor = DarkNightBlue,
                                    containerColor = MedicalNavySurface,
                                    labelColor = PureWhite
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Ouverture buccale :", color = SecondaryGrey, fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Normale (> 35 mm)", "Limitée (< 35 mm)").forEach { o ->
                            FilterChip(
                                selected = mouthOpening == o,
                                onClick = { viewModel.wizMouthOpening.value = o },
                                label = { Text(o, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = if (o.contains("Limitée")) AlertRed else TurquoiseGreen,
                                    selectedLabelColor = if (o.contains("Limitée")) PureWhite else DarkNightBlue,
                                    containerColor = MedicalNavySurface,
                                    labelColor = PureWhite
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Distance thyro-mentonnière :", color = SecondaryGrey, fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Normale (> 65 mm)", "Courte (< 65 mm)").forEach { d ->
                            FilterChip(
                                selected = thyromental == d,
                                onClick = { viewModel.wizThyromental.value = d },
                                label = { Text(d, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = if (d.contains("Courte")) AlertRed else TurquoiseGreen,
                                    selectedLabelColor = if (d.contains("Courte")) PureWhite else DarkNightBlue,
                                    containerColor = MedicalNavySurface,
                                    labelColor = PureWhite
                                )
                            )
                        }
                    }
                }
            }

            // STEP 4 : ÉVALUATION DES RISQUES
            if (step == 4) {
                item {
                    Text("ÉTAPE 4 : CLASSIFICATION ASA & RISQUES", color = MedicalCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Classification ASA :", color = PureWhite, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    val asaDescriptions = listOf(
                        "ASA I" to "Patient sain sans pathologie sous-jacente",
                        "ASA II" to "Patient avec anomalie systémique modérée bien contrôlée (HTA équilibrée, tabac)",
                        "ASA III" to "Patient avec anomalie systémique sévère limitant l'activité (insuffisance coronarienne, BPCO)",
                        "ASA IV" to "Pathologie systémique sévère représentant une menace vitale permanente",
                        "ASA V" to "Patient moribond dont la survie est improbable sans intervention"
                    )

                    asaDescriptions.forEach { (code, desc) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.wizAsaScore.value = code }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (asaScore == code) MedicalCyan else MedicalNavySurface,
                                border = BorderStroke(1.dp, if (asaScore == code) MedicalCyan else BorderSubtle)
                            ) {
                                Text(
                                    code,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = if (asaScore == code) DarkNightBlue else PureWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(desc, color = if (asaScore == code) PureWhite else SecondaryGrey, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Risques spécifiques identifiés :", color = PureWhite, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = diffAirway,
                            onCheckedChange = { viewModel.wizDifficultAirwayRisk.value = it },
                            colors = CheckboxDefaults.colors(checkedColor = AlertRed)
                        )
                        Text("Risque d'intubation difficile prévisible", color = if (diffAirway) AlertRed else PureWhite, fontSize = 12.sp)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = aspirationRisk,
                            onCheckedChange = { viewModel.wizAspirationRisk.value = it },
                            colors = CheckboxDefaults.colors(checkedColor = AlertRed)
                        )
                        Text("Risque d'inhalation / Estomac plein (Induction Séquence Rapide)", color = if (aspirationRisk) AlertRed else PureWhite, fontSize = 12.sp)
                    }
                }
            }

            // STEP 5 : SYNTHÈSE ET COMPTE RENDU
            if (step == 5) {
                item {
                    Text("ÉTAPE 5 : COMPTE RENDU STRUCTURÉ & VALIDATION", color = MedicalCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MedicalNavySurface),
                        border = BorderStroke(1.dp, MedicalCyan.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("COMPTE-RENDU PRÉ-ANESTHÉSIQUE", color = TurquoiseGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MedicalCyan.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        asaScore,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        color = MedicalCyan,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Patient : ${name.ifBlank { "Non renseigné" }} (${age} ans, ${gender})", color = PureWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("Chirurgie : $surgery ($urgency)", color = SecondaryGrey, fontSize = 12.sp)
                            Text("Poids : ${weight} kg | Taille : ${height} cm | IMC : ${bmiResult.bmi} kg/m²", color = SecondaryGrey, fontSize = 12.sp)
                            Text("Voies Aériennes : $mallampati, ouverture $mouthOpening", color = SecondaryGrey, fontSize = 12.sp)
                            if (diffAirway) {
                                Text("⚠️ ALERTE : Intubation difficile prévisible", color = AlertRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            if (aspirationRisk) {
                                Text("⚠️ ALERTE : Risque d'inhalation (ISR requise)", color = AlertRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = summaryNotes,
                        onValueChange = { viewModel.wizSummaryNotes.value = it },
                        label = { Text("Stratégie anesthésique & Remarques pour le bloc") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = outlinedColors(),
                        minLines = 3
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = WarningOrange.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, WarningOrange.copy(alpha = 0.4f))
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, null, tint = WarningOrange, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Ce compte-rendu doit être vérifié et contresigné par le Médecin Anesthésiste-Réanimateur responsable.",
                                color = PureWhite,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Bottom Wizard Buttons
        Surface(
            color = MedicalNavy,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (step > 1) {
                    OutlinedButton(
                        onClick = { viewModel.prevWizardStep() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SecondaryGrey),
                        border = BorderStroke(1.dp, BorderSubtle),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.ArrowBack, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Précédent", fontSize = 12.sp)
                    }
                } else {
                    Spacer(modifier = Modifier.width(10.dp))
                }

                if (step < 5) {
                    Button(
                        onClick = { viewModel.nextWizardStep() },
                        colors = ButtonDefaults.buttonColors(containerColor = MedicalCyan, contentColor = DarkNightBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Suivant", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.ArrowForward, null, modifier = Modifier.size(16.dp))
                    }
                } else {
                    Button(
                        onClick = {
                            viewModel.saveAssessment(onSaved = onClose)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = TurquoiseGreen, contentColor = DarkNightBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Save, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Valider & Enregistrer", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun outlinedColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = MedicalNavySurface,
    unfocusedContainerColor = MedicalNavySurface,
    focusedBorderColor = MedicalCyan,
    unfocusedBorderColor = BorderSubtle,
    focusedTextColor = PureWhite,
    unfocusedTextColor = PureWhite,
    focusedLabelColor = MedicalCyan,
    unfocusedLabelColor = SecondaryGrey
)
