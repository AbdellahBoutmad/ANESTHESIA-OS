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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProtocolModel
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProtocolsScreen(viewModel: MainViewModel) {
    val selectedCategory by viewModel.protocolCategory.collectAsState()
    val selectedProtocol by viewModel.selectedProtocol.collectAsState()
    val activeSubScreen by viewModel.activeSubScreen.collectAsState()

    val categories = listOf("TOUS", "URGENCE_CRISE", "CHIRURGIE", "OBSTETRIQUE", "REANIMATION")

    val roomProtocols by viewModel.offlineProtocols.collectAsState()
    val filteredProtocols = roomProtocols.filter {
        selectedCategory == "TOUS" || it.category == selectedCategory
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNightBlue)
            .testTag("protocols_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp)
        ) {
            item {
                Text(
                    "PROTOCOLES CLINIQUES & CRISES PÉRIOPÉRATOIRES",
                    color = MedicalCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Algorithmes d'urgence validés et démarches standardisées par spécialité chirurgicale.",
                    color = SecondaryGrey,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Category Filter
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        val label = when (cat) {
                            "URGENCE_CRISE" -> "Urgences & Crises"
                            "CHIRURGIE" -> "Chirurgies"
                            else -> "Tous les protocoles"
                        }
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { viewModel.setProtocolCategory(cat) },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = if (cat == "URGENCE_CRISE") AlertRed else MedicalCyan,
                                selectedLabelColor = if (cat == "URGENCE_CRISE") PureWhite else DarkNightBlue,
                                containerColor = MedicalNavySurface,
                                labelColor = PureWhite
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Protocol List Items
            items(filteredProtocols) { protocol ->
                ProtocolCardItem(
                    protocol = protocol,
                    onClick = { viewModel.selectProtocol(protocol) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        // Detail Sheet
        if (activeSubScreen == SubScreen.PROTOCOL_DETAIL && selectedProtocol != null) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { viewModel.closeSubScreen() },
                sheetState = sheetState,
                containerColor = MedicalNavyElevated,
                contentColor = PureWhite
            ) {
                ProtocolDetailContent(
                    protocol = selectedProtocol!!,
                    onClose = { viewModel.closeSubScreen() }
                )
            }
        }
    }
}

@Composable
fun ProtocolCardItem(
    protocol: ProtocolModel,
    onClick: () -> Unit
) {
    val isEmergency = protocol.urgencyLevel == "VITALE"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEmergency) AlertRed.copy(alpha = 0.08f) else MedicalNavy
        ),
        border = BorderStroke(
            1.dp,
            if (isEmergency) AlertRed.copy(alpha = 0.6f) else BorderSubtle
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (isEmergency) AlertRed.copy(alpha = 0.2f) else MedicalCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isEmergency) Icons.Default.Emergency else Icons.Default.LocalHospital,
                            contentDescription = null,
                            tint = if (isEmergency) AlertRed else MedicalCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        protocol.title,
                        color = PureWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (isEmergency) AlertRed else TurquoiseGreen
                ) {
                    Text(
                        protocol.urgencyLevel,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color = if (isEmergency) PureWhite else DarkNightBlue,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(protocol.subtitle, color = MedicalCyan, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                protocol.description,
                color = SecondaryGrey,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                maxLines = 2
            )
        }
    }
}

@Composable
fun ProtocolDetailContent(
    protocol: ProtocolModel,
    onClose: () -> Unit
) {
    val isEmergency = protocol.urgencyLevel == "VITALE"

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        protocol.title,
                        color = if (isEmergency) AlertRed else PureWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(protocol.subtitle, color = MedicalCyan, fontSize = 12.sp)
                }

                IconButton(onClick = onClose) {
                    Icon(Icons.Default.ArrowBack, "Fermer", tint = MedicalCyan)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MedicalNavySurface,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, BorderSubtle)
            ) {
                Text(
                    protocol.description,
                    color = PureWhite,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        item {
            Text(
                "ALGORITHME DÉCISIONNEL & ÉTAPES D'ACTION",
                color = if (isEmergency) AlertRed else MedicalCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            protocol.algorithmSteps.forEach { stepText ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MedicalNavy),
                    border = BorderStroke(1.dp, BorderSubtle)
                ) {
                    Text(
                        stepText,
                        color = PureWhite,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Text(
                "MÉDICAMENTS & DROGUES CLÉS",
                color = TurquoiseGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            protocol.keyDrugs.forEach { drug ->
                Text("• $drug", color = PureWhite, fontSize = 12.sp, modifier = Modifier.padding(vertical = 2.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            Text(
                "SOURCE MÉDICALE OFFICIELLE",
                color = SecondaryGrey,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
            Text(protocol.guidelinesSource, color = SecondaryGrey, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
