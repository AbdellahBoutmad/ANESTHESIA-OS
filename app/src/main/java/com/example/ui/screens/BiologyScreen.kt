package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Biotech
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import com.example.data.model.BiologyTestModel
import com.example.data.repository.MedicalKnowledgeRepository
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

@Composable
fun BiologyScreen() {
    val biologyList = MedicalKnowledgeRepository.biologyTests
    val categories = listOf("TOUS", "Hématologie", "Hémostase", "Ionogramme", "Fonction Rénale", "Métabolisme")
    var selectedCategory by remember { mutableStateOf("TOUS") }

    val filteredList = biologyList.filter {
        selectedCategory == "TOUS" || it.category == selectedCategory
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNightBlue)
            .testTag("biology_screen"),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp)
    ) {
        item {
            Text(
                "EXAMENS BIOLOGIQUES ET IMPLICATIONS ANESTHÉSIQUES",
                color = MedicalCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Valeurs usuelles, conséquences pour l'anesthésie et critères de report ou d'optimisation.",
                color = SecondaryGrey,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Section spécifique "Anomalies pouvant nécessiter une optimisation ou un report de chirurgie"
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = WarningOrange.copy(alpha = 0.12f)),
                border = BorderStroke(1.dp, WarningOrange.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(WarningOrange.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Warning, null, tint = WarningOrange, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            "ANOMALIES POUVANT NÉCESSITER OPTIMISATION OU REPORT",
                            color = WarningOrange,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "En chirurgie programmée, certaines anomalies (Hb < 10 g/dL non explorée, K+ < 3.0 ou > 5.5 mmol/L, plaquettes < 80 G/L avant ALR, INR > 1.5) imposent une évaluation conjointe et une optimisation préalable sans décision automatique unilatérale.",
                            color = PureWhite,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
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
                        onClick = { selectedCategory = cat },
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

        // Biology Test Cards
        items(filteredList) { test ->
            BiologyTestCard(test = test)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun BiologyTestCard(test: BiologyTestModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Biotech, null, tint = MedicalCyan, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(test.name, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MedicalNavySurface,
                    border = BorderStroke(1.dp, TurquoiseGreen.copy(alpha = 0.5f))
                ) {
                    Text(
                        test.category,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color = TurquoiseGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MedicalNavySurface,
                shape = RoundedCornerShape(6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Valeurs de référence :", color = SecondaryGrey, fontSize = 11.sp)
                    Text("${test.normalRange} ${test.unit}", color = MedicalCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Implications pour l'anesthésie :", color = TurquoiseGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text(test.anestheticImplications, color = PureWhite, fontSize = 12.sp, lineHeight = 16.sp)

            Spacer(modifier = Modifier.height(6.dp))
            Text("Causes fréquentes d'anomalie :", color = SecondaryGrey, fontWeight = FontWeight.SemiBold, fontSize = 11.sp)
            Text(test.frequentCausesOfAnomaly, color = SecondaryGrey, fontSize = 11.sp)

            if (test.requiresPostponementOrOptimization) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = WarningOrange.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, WarningOrange.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Critère d'optimisation / report :", color = WarningOrange, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text(test.optimizationCriteria, color = PureWhite, fontSize = 11.sp, lineHeight = 15.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text("Source : ${test.medicalSource}", color = SecondaryGrey, fontSize = 10.sp)
        }
    }
}
