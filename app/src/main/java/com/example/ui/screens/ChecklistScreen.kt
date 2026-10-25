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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
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
    var roomName by remember { mutableStateOf("Salle 2 (Bloc Central)") }
    var verifiedBy by remember { mutableStateOf("IAR de garde") }
    var saveSuccessMessage by remember { mutableStateOf<String?>(null) }

    val equipmentList by viewModel.equipmentChecklist.collectAsState()
    val whoList by viewModel.whoChecklist.collectAsState()
    val savedHistory by viewModel.savedChecklists.collectAsState()

    val currentList = if (selectedTab == 0) equipmentList else whoList
    val verifiedCount = currentList.count { it.status == "VERIFIED" }
    val totalCount = currentList.size
    val progress = if (totalCount > 0) verifiedCount.toFloat() / totalCount.toFloat() else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNightBlue)
            .testTag("checklist_screen")
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MedicalNavy,
            contentColor = MedicalCyan,
            divider = {}
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Matériel & Respirateur", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Sécurité OMS (3 Temps)", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Historique (${savedHistory.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
            )
        }

        if (selectedTab in 0..1) {
            // Live Progress Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MedicalNavy),
                border = BorderStroke(1.dp, MedicalCyan.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            if (selectedTab == 0) "Vérification Matériel Bloc" else "Check-list OMS Sécurité Chirurgicale",
                            color = PureWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            "$verifiedCount / $totalCount vérifiés (${(progress * 100).toInt()}%)",
                            color = if (progress == 1f) TurquoiseGreen else MedicalCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (progress == 1f) TurquoiseGreen else MedicalCyan,
                        trackColor = MedicalNavySurface,
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Appuyez sur un item pour changer son statut",
                            color = SecondaryGrey,
                            fontSize = 11.sp
                        )

                        Button(
                            onClick = {
                                val type = if (selectedTab == 0) "MATERIEL_BLOC" else "OMS_SECURITE"
                                viewModel.saveChecklistRecord(type, roomName, verifiedBy)
                                saveSuccessMessage = "Check-list enregistrée avec succès !"
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (progress == 1f) TurquoiseGreen else MedicalCyan,
                                contentColor = DarkNightBlue
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Save, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Enregistrer", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    if (saveSuccessMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(saveSuccessMessage!!, color = TurquoiseGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 96.dp)
        ) {
            if (selectedTab in 0..1) {
                items(currentList) { item ->
                    ChecklistItemCard(
                        item = item,
                        onToggle = {
                            if (selectedTab == 0) viewModel.toggleEquipmentItem(item.id)
                            else viewModel.toggleWhoItem(item.id)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else {
                // Historique
                if (savedHistory.isEmpty()) {
                    item {
                        Text("Aucune check-list enregistrée dans l'historique.", color = SecondaryGrey, fontSize = 12.sp)
                    }
                } else {
                    items(savedHistory) { record ->
                        val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE).format(Date(record.createdAt))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MedicalNavy),
                            border = BorderStroke(1.dp, BorderSubtle)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
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
                                            if (record.isComplete) "Complète" else "Partielle",
                                            color = if (record.isComplete) TurquoiseGreen else WarningOrange,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text("Lieu : ${record.roomName} • Par : ${record.verifiedBy}", color = SecondaryGrey, fontSize = 11.sp)
                                Text("Éléments : ${record.verifiedCount} / ${record.totalCount} vérifiés", color = MedicalCyan, fontSize = 11.sp)
                                Text("Date : $dateStr", color = SecondaryGrey, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChecklistItemCard(
    item: ChecklistItem,
    onToggle: () -> Unit
) {
    val (statusColor, statusIcon, statusLabel) = when (item.status) {
        "VERIFIED" -> Triple(TurquoiseGreen, Icons.Default.Check, "VÉRIFIÉ")
        "ACTION_REQUIRED" -> Triple(AlertRed, Icons.Default.Close, "MANQUANT / ACTION")
        else -> Triple(SecondaryGrey, Icons.Default.Pending, "EN ATTENTE")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.status == "VERIFIED") TurquoiseGreen.copy(alpha = 0.08f) else MedicalNavy
        ),
        border = BorderStroke(1.dp, if (item.status == "VERIFIED") TurquoiseGreen.copy(alpha = 0.5f) else BorderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(statusColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(statusIcon, null, tint = statusColor, modifier = Modifier.size(16.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(item.title, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
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
                Text(item.description, color = SecondaryGrey, fontSize = 11.sp, lineHeight = 15.sp)
            }

            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = statusColor.copy(alpha = 0.15f)
            ) {
                Text(
                    statusLabel,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    color = statusColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
