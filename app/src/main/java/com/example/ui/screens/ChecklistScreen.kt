package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.ReportProblem
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChecklistItem
import com.example.ui.MainViewModel
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChecklistScreen(viewModel: MainViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    var selectedCategoryFilter by remember { mutableStateOf("TOUS") }
    var roomName by remember { mutableStateOf("Salle 1 (Bloc Principal)") }
    var verifiedBy by remember { mutableStateOf("IAR en charge") }
    var showResetDialog by remember { mutableStateOf(false) }
    var saveSuccessMessage by remember { mutableStateOf<String?>(null) }

    val equipmentList by viewModel.equipmentChecklist.collectAsState()
    val whoList by viewModel.whoChecklist.collectAsState()
    val savedHistory by viewModel.savedChecklists.collectAsState()

    val currentRawList = if (selectedTab == 0) equipmentList else whoList
    val verifiedCount = currentRawList.count { it.status == "VERIFIED" }
    val totalCount = currentRawList.size
    val progress = if (totalCount > 0) verifiedCount.toFloat() / totalCount.toFloat() else 0f

    val equipmentCategories = listOf("TOUS", "VENTILATEUR", "VOIES_AERIENNES", "MONITORAGE", "URGENCE")
    val whoCategories = listOf("TOUS", "SIGN_IN", "TIME_OUT", "SIGN_OUT")
    val activeCategories = if (selectedTab == 0) equipmentCategories else whoCategories

    val displayedList = if (selectedCategoryFilter == "TOUS") {
        currentRawList
    } else {
        currentRawList.filter { it.category == selectedCategoryFilter }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNightBlue)
            .testTag("checklist_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Tabs Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MedicalNavy,
                contentColor = MedicalCyan,
                divider = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        selectedCategoryFilter = "TOUS"
                    },
                    text = {
                        Text(
                            "Matériel Bloc (${equipmentList.count { it.status == "VERIFIED" }}/${equipmentList.size})",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        selectedCategoryFilter = "TOUS"
                    },
                    text = {
                        Text(
                            "Sécurité OMS (${whoList.count { it.status == "VERIFIED" }}/${whoList.size})",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(
                            "Historique (${savedHistory.size})",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            if (selectedTab in 0..1) {
                // Compact Progress & Action Header Bar (No overflow, fully adaptive)
                Surface(
                    color = MedicalNavySurface,
                    border = BorderStroke(1.dp, BorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    if (selectedTab == 0) "ÉQUIPEMENTS & RESPIRATEUR" else "CHECK-LIST SÉCURITÉ OMS",
                                    color = MedicalCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    "$verifiedCount / $totalCount vérifiés (${(progress * 100).toInt()}%)",
                                    color = if (progress == 1f) TurquoiseGreen else PureWhite,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedButton(
                                    onClick = { showResetDialog = true },
                                    border = BorderStroke(1.dp, WarningOrange.copy(alpha = 0.5f)),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = WarningOrange),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Reset", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        val type = if (selectedTab == 0) "MATERIEL_BLOC" else "OMS_SECURITE"
                                        viewModel.saveChecklistRecord(type, roomName, verifiedBy)
                                        saveSuccessMessage = "Enregistré avec succès !"
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (progress == 1f) TurquoiseGreen else MedicalCyan,
                                        contentColor = DarkNightBlue
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(Icons.Default.Save, null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Sauvegarder", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (progress == 1f) TurquoiseGreen else MedicalCyan,
                            trackColor = MedicalNavyElevated,
                        )

                        if (saveSuccessMessage != null) {
                            Text(
                                saveSuccessMessage!!,
                                color = TurquoiseGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                // Category Filter Chips
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(activeCategories) { cat ->
                        val label = when (cat) {
                            "TOUS" -> "Tous (${currentRawList.size})"
                            "VENTILATEUR" -> "Respirateur"
                            "VOIES_AERIENNES" -> "Voies Aériennes"
                            "MONITORAGE" -> "Monitorage"
                            "URGENCE" -> "Urgences"
                            "SIGN_IN" -> "1. Sign In"
                            "TIME_OUT" -> "2. Time Out"
                            "SIGN_OUT" -> "3. Sign Out"
                            else -> cat
                        }
                        FilterChip(
                            selected = selectedCategoryFilter == cat,
                            onClick = { selectedCategoryFilter = cat },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MedicalCyan,
                                selectedLabelColor = DarkNightBlue,
                                containerColor = MedicalNavySurface,
                                labelColor = PureWhite
                            ),
                            border = BorderStroke(1.dp, if (selectedCategoryFilter == cat) MedicalCyan else BorderSubtle),
                            modifier = Modifier.height(32.dp)
                        )
                    }
                }
            }

            // Scrollable Content with weight(1f) to ALWAYS occupy available space on any phone
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(top = 4.dp, bottom = 96.dp)
            ) {
                if (selectedTab in 0..1) {
                    items(displayedList, key = { it.id }) { item ->
                        ChecklistItemCard(
                            item = item,
                            onToggle = {
                                if (selectedTab == 0) viewModel.toggleEquipmentItem(item.id)
                                else viewModel.toggleWhoItem(item.id)
                                saveSuccessMessage = null
                            },
                            onActionRequired = {
                                if (selectedTab == 0) viewModel.setEquipmentActionRequired(item.id)
                                else viewModel.setWhoActionRequired(item.id)
                                saveSuccessMessage = null
                            }
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                } else {
                    // Historique Tab
                    if (savedHistory.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                colors = CardDefaults.cardColors(containerColor = MedicalNavySurface),
                                border = BorderStroke(1.dp, BorderSubtle)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Outlined.Checklist, null, tint = MedicalCyan, modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Aucune check-list enregistrée", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Effectuez une vérification du matériel ou de la check-list OMS et appuyez sur Sauvegarder pour l'archiver.",
                                        color = SecondaryGrey,
                                        fontSize = 11.sp,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        items(savedHistory) { record ->
                            val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE).format(Date(record.createdAt))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MedicalNavy),
                                border = BorderStroke(1.dp, BorderSubtle),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            if (record.checklistType == "MATERIEL_BLOC") "Matériel de Salle" else "Check-list Sécurité OMS",
                                            color = PureWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Surface(
                                            color = if (record.isComplete) TurquoiseGreen.copy(alpha = 0.2f) else WarningOrange.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                if (record.isComplete) "Complète (100%)" else "${record.verifiedCount}/${record.totalCount}",
                                                color = if (record.isComplete) TurquoiseGreen else WarningOrange,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Lieu : ${record.roomName} • Par : ${record.verifiedBy}", color = SecondaryGrey, fontSize = 11.sp)
                                    Text("Date : $dateStr", color = MedicalCyan, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Reset Confirmation Dialog
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                containerColor = MedicalNavyElevated,
                icon = {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = WarningOrange)
                },
                title = {
                    Text("Réinitialiser la check-list ?", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                },
                text = {
                    Text("Voulez-vous remettre tous les éléments de cet onglet à l'état non vérifié ?", color = SecondaryGrey)
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (selectedTab == 0) viewModel.resetEquipmentChecklist()
                            else viewModel.resetWhoChecklist()
                            showResetDialog = false
                            saveSuccessMessage = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WarningOrange, contentColor = DarkNightBlue)
                    ) {
                        Text("Réinitialiser", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("Annuler", color = PureWhite)
                    }
                }
            )
        }
    }
}

@Composable
fun ChecklistItemCard(
    item: ChecklistItem,
    onToggle: () -> Unit,
    onActionRequired: () -> Unit
) {
    val isVerified = item.status == "VERIFIED"
    val isActionRequired = item.status == "ACTION_REQUIRED"

    val borderColor = when {
        isVerified -> TurquoiseGreen.copy(alpha = 0.6f)
        isActionRequired -> AlertRed.copy(alpha = 0.7f)
        else -> BorderSubtle
    }

    val cardBackground = when {
        isVerified -> TurquoiseGreen.copy(alpha = 0.08f)
        isActionRequired -> AlertRed.copy(alpha = 0.08f)
        else -> MedicalNavy
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Accessible Checkbox
            Checkbox(
                checked = isVerified,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = TurquoiseGreen,
                    checkmarkColor = DarkNightBlue,
                    uncheckedColor = SecondaryGrey
                )
            )

            Spacer(modifier = Modifier.width(6.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        item.title,
                        color = if (isVerified) TurquoiseGreen else PureWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(3.dp),
                        color = MedicalNavySurface
                    ) {
                        Text(
                            item.category,
                            color = MedicalCyan,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    item.description,
                    color = SecondaryGrey,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Action Required / Report Problem Button
            IconButton(
                onClick = onActionRequired,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ReportProblem,
                    contentDescription = "Signaler problème",
                    tint = if (isActionRequired) AlertRed else SecondaryGrey.copy(alpha = 0.4f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
