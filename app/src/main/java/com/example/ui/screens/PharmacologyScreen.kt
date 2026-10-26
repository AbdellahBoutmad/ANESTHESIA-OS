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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.data.model.DrugModel
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
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PharmacologyScreen(viewModel: MainViewModel) {
    val selectedCategory by viewModel.drugCategory.collectAsState()
    val selectedDrug by viewModel.selectedDrug.collectAsState()
    val patientWeight by viewModel.calculatorWeight.collectAsState()
    val activeSubScreen by viewModel.activeSubScreen.collectAsState()

    val categories = listOf(
        "TOUS",
        "Hypnotiques IV",
        "Morphiniques",
        "Curares",
        "Antagonistes",
        "Vasopresseurs & Inotropes",
        "Anesthésiques locaux",
        "Antiémétiques",
        "Analgésiques",
        "Hémostatiques & Antidotes",
        "Médicaments d'urgence",
        "Anesthésiques inhalés"
    )

    val roomDrugs by viewModel.offlineDrugs.collectAsState()
    val filteredDrugs = roomDrugs.filter {
        selectedCategory == "TOUS" || it.category == selectedCategory
    }

    Box(modifier = Modifier.fillMaxSize().background(DarkNightBlue)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("pharmacology_screen"),
            contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp)
        ) {
            // Header
            item {
                Text(
                    "ENCYCLOPÉDIE PHARMACOLOGIQUE ANESTHÉSIQUE",
                    color = MedicalCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Fiches complètes et adaptation instantanée des posologies selon le poids du patient.",
                    color = SecondaryGrey,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Real-Time Dose Calculator Widget by Patient Weight
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MedicalNavy),
                    border = BorderStroke(1.dp, MedicalCyan.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Calculate, null, tint = MedicalCyan, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Calculateur de Doses en Temps Réel", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MedicalCyan.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    "${patientWeight.roundToInt()} kg",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    color = MedicalCyan,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "Ajustez le curseur de poids pour recalculer instantanément les doses d'induction :",
                            color = SecondaryGrey,
                            fontSize = 11.sp
                        )

                        Slider(
                            value = patientWeight.toFloat(),
                            onValueChange = { viewModel.setCalculatorWeight(it.toDouble()) },
                            valueRange = 10f..150f,
                            steps = 140,
                            colors = SliderDefaults.colors(
                                thumbColor = MedicalCyan,
                                activeTrackColor = MedicalCyan,
                                inactiveTrackColor = MedicalNavySurface
                            ),
                            modifier = Modifier.testTag("weight_slider")
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Category Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { viewModel.setDrugCategory(cat) },
                            label = { Text(cat, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MedicalCyan,
                                selectedLabelColor = DarkNightBlue,
                                containerColor = MedicalNavySurface,
                                labelColor = PureWhite
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Drugs List
            items(filteredDrugs) { drug ->
                DrugCard(
                    drug = drug,
                    patientWeight = patientWeight,
                    onOpenDetails = { viewModel.selectDrug(drug) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Full Drug Sheet Details
        if (activeSubScreen == SubScreen.DRUG_DETAIL && selectedDrug != null) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { viewModel.closeSubScreen() },
                sheetState = sheetState,
                containerColor = MedicalNavyElevated,
                contentColor = PureWhite
            ) {
                DrugDetailContent(
                    drug = selectedDrug!!,
                    patientWeight = patientWeight,
                    onClose = { viewModel.closeSubScreen() }
                )
            }
        }
    }
}

@Composable
fun DrugCard(
    drug: DrugModel,
    patientWeight: Double,
    onOpenDetails: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenDetails() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MedicalNavy),
        border = BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            drug.name,
                            color = PureWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "(${drug.brandNames})",
                            color = SecondaryGrey,
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        drug.category,
                        color = TurquoiseGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MedicalNavySurface,
                    border = BorderStroke(1.dp, MedicalCyan.copy(alpha = 0.4f))
                ) {
                    Text(
                        "Détails",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MedicalCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            // Live calculated dose banner
            if (drug.minDosePerKg > 0.0) {
                val minDose = (drug.minDosePerKg * patientWeight * 10.0).roundToInt() / 10.0
                val maxDose = (drug.maxDosePerKg * patientWeight * 10.0).roundToInt() / 10.0
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = MedicalNavySurface
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Dose calculée pour ${patientWeight.roundToInt()} kg :",
                            color = SecondaryGrey,
                            fontSize = 11.sp
                        )
                        Text(
                            "$minDose à $maxDose ${drug.doseUnit.replace("/kg", "")}",
                            color = MedicalCyan,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                    }
                }
            } else if (drug.fixedDoseText.isNotBlank()) {
                Text(
                    "Posologie : ${drug.fixedDoseText}",
                    color = MedicalCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Délai : ${drug.onset}", color = SecondaryGrey, fontSize = 11.sp)
                Text("Durée : ${drug.duration}", color = SecondaryGrey, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun DrugDetailContent(
    drug: DrugModel,
    patientWeight: Double,
    onClose: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(drug.name, color = PureWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("${drug.brandNames} • ${drug.category}", color = TurquoiseGreen, fontSize = 12.sp)
                }

                IconButton(onClick = onClose) {
                    Icon(Icons.Default.ArrowBack, "Fermer", tint = MedicalCyan)
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Live Calculated Dose Box
        item {
            if (drug.minDosePerKg > 0.0) {
                val minDose = (drug.minDosePerKg * patientWeight * 10.0).roundToInt() / 10.0
                val maxDose = (drug.maxDosePerKg * patientWeight * 10.0).roundToInt() / 10.0
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MedicalNavy),
                    border = BorderStroke(1.dp, MedicalCyan)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("ADAPTATION PONDÉRALE POUR ${patientWeight.roundToInt()} KG", color = MedicalCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Dose d'induction : $minDose à $maxDose ${drug.doseUnit.replace("/kg", "")}",
                            color = PureWhite,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        )
                        Text("Formule de référence : ${drug.minDosePerKg} à ${drug.maxDosePerKg} ${drug.doseUnit}", color = SecondaryGrey, fontSize = 11.sp)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Presentations & Routes
        item {
            SectionTitle("PRÉSENTATION & VOIES")
            Text(drug.presentations, color = PureWhite, fontSize = 13.sp)
            Text("Voies : ${drug.routes}", color = SecondaryGrey, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Indications & Maintenance
        item {
            SectionTitle("INDICATIONS & ENTRETIEN")
            Text(drug.indications, color = PureWhite, fontSize = 13.sp)
            if (drug.maintenanceDose.isNotBlank()) {
                Text("Entretien : ${drug.maintenanceDose}", color = TurquoiseGreen, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Delays & Pharmacokinetics
        item {
            SectionTitle("DÉLAI & DURÉE D'ACTION")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Délai d'action :", color = SecondaryGrey, fontSize = 11.sp)
                    Text(drug.onset, color = PureWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Durée d'action :", color = SecondaryGrey, fontSize = 11.sp)
                    Text(drug.duration, color = PureWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Contraindications
        item {
            SectionTitle("CONTRE-INDICATIONS MAJEURES")
            drug.contraindications.forEach { ci ->
                Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                    Text("• ", color = AlertRed, fontWeight = FontWeight.Bold)
                    Text(ci, color = AlertRed, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Adverse Effects & Cautions
        item {
            SectionTitle("EFFETS INDÉSIRABLES & PRÉCAUTIONS")
            drug.adverseEffects.forEach { ae ->
                Text("• $ae", color = SecondaryGrey, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("Terrain : ${drug.terrainCautions}", color = WarningOrange, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Antagonist / Antidote
        if (drug.antidoteOrAntagonist.isNotBlank()) {
            item {
                SectionTitle("ANTIDOTE / ANTAGONISTE")
                Surface(
                    color = MedicalNavySurface,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, TurquoiseGreen.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        drug.antidoteOrAntagonist,
                        color = TurquoiseGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(10.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Sources
        item {
            SectionTitle("SOURCE MÉDICALE VALIDÉE")
            Text(drug.sources, color = SecondaryGrey, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        title,
        color = MedicalCyan,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}
