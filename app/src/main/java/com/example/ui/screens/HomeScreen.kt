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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Biotech
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainTab
import com.example.ui.MainViewModel
import com.example.ui.SubScreen

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val savedPatients by viewModel.savedAssessments.collectAsState()
    val savedChecklists by viewModel.savedChecklists.collectAsState()
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 720.dp)
                .testTag("home_screen"),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // 1. Top Search Bar with Dynamic Colors
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("global_search_input"),
                        placeholder = {
                            Text(
                                "Rechercher un médicament, une chirurgie, un protocole...",
                                color = colors.onSurfaceVariant,
                                fontSize = 12.5.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Recherche",
                                tint = colors.primary
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Effacer",
                                        tint = colors.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = colors.surfaceVariant.copy(alpha = 0.5f),
                            unfocusedContainerColor = colors.surfaceVariant.copy(alpha = 0.35f),
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.outline.copy(alpha = 0.3f),
                            focusedTextColor = colors.onSurface,
                            unfocusedTextColor = colors.onSurface
                        ),
                        singleLine = true
                    )
                }
            }

            // Live Search Results Dropdown
            if (searchQuery.isNotBlank()) {
                val query = searchQuery.lowercase().trim()
                val matchingDrugs = viewModel.drugsList.filter {
                    it.name.lowercase().contains(query) || it.indications.lowercase().contains(query)
                }
                val matchingProtocols = viewModel.protocolsList.filter {
                    it.title.lowercase().contains(query) || it.subtitle.lowercase().contains(query)
                }
                val matchingBio = viewModel.biologyList.filter {
                    it.name.lowercase().contains(query)
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
                        border = BorderStroke(1.dp, colors.primary.copy(alpha = 0.6f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                "Résultats de recherche rapide (${matchingDrugs.size + matchingProtocols.size + matchingBio.size})",
                                color = colors.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            if (matchingDrugs.isNotEmpty()) {
                                Text("Médicaments :", color = colors.primary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                matchingDrugs.take(3).forEach { drug ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                viewModel.selectDrug(drug)
                                                viewModel.setSearchQuery("")
                                            }
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Medication, null, tint = colors.primary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(drug.name, color = colors.onSurface, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("(${drug.category})", color = colors.onSurfaceVariant, fontSize = 11.sp)
                                    }
                                }
                            }

                            if (matchingProtocols.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Protocoles :", color = colors.error, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                matchingProtocols.take(3).forEach { prot ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                viewModel.selectProtocol(prot)
                                                viewModel.setSearchQuery("")
                                            }
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Emergency, null, tint = colors.error, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(prot.title, color = colors.onSurface, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 2. Command Hub Hero Card with Real-Time KPI Metrics
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface),
                    border = BorderStroke(1.dp, colors.outline.copy(alpha = 0.35f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(9.dp)
                                            .clip(CircleShape)
                                            .background(colors.tertiary)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Anestheia Trone",
                                        color = colors.tertiary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    "Système d'Anesthésie-Réanimation",
                                    color = colors.onSurface,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = colors.primary.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, colors.primary.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(colors.primary)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "BLOC ACTIF",
                                        color = colors.primary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Real-time KPI Counters
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            KpiCounter(
                                count = savedPatients.size.toString(),
                                label = "Patients",
                                color = colors.primary
                            )
                            KpiCounter(
                                count = savedChecklists.size.toString(),
                                label = "Checklists",
                                color = colors.tertiary
                            )
                            KpiCounter(
                                count = "70+",
                                label = "Drogues",
                                color = colors.primary
                            )
                            KpiCounter(
                                count = "35+",
                                label = "Protocoles",
                                color = colors.tertiary
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.resetWizard()
                                    viewModel.openSubScreen(SubScreen.CONSULTATION_WIZARD)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_new_evaluation"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colors.primary,
                                    contentColor = colors.onPrimary
                                )
                            ) {
                                Icon(Icons.Default.PersonAdd, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Consultation", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = { viewModel.setTab(MainTab.PATIENTS) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_patients_home"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = colors.primary
                                ),
                                border = BorderStroke(1.dp, colors.primary.copy(alpha = 0.5f))
                            ) {
                                Icon(Icons.Default.FolderShared, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Dossiers Patients", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // 3. Quick Action Interactive Pills (Horizontal Flow)
            item {
                val actionPills = listOf(
                    Triple("Complications Post-Op", Icons.Default.CrisisAlert, colors.error) to {
                        viewModel.openSubScreen(SubScreen.POST_OP_COMPLICATIONS)
                    },
                    Triple("Urgences SFAR", Icons.Default.Emergency, colors.error) to {
                        viewModel.setTab(MainTab.PROTOCOLS)
                        viewModel.setProtocolCategory("URGENCE_CRISE")
                    },
                    Triple("Simulateur Respi", Icons.Default.Air, colors.primary) to {
                        viewModel.openSubScreen(SubScreen.VENTILATION_SIMULATOR)
                    },
                    Triple("ECG Challenge", Icons.Outlined.MonitorHeart, colors.tertiary) to {
                        viewModel.openSubScreen(SubScreen.ECG_CHALLENGE)
                    },
                    Triple("Timeline 7 Étapes", Icons.Default.Timeline, colors.tertiary) to {
                        viewModel.openSubScreen(SubScreen.ANESTHESIA_TIMELINE)
                    },
                    Triple("Calculateurs IMC/Gross", Icons.Default.Calculate, colors.primary) to {
                        viewModel.openSubScreen(SubScreen.CALCULATORS)
                    },
                    Triple("Traducteur Trilingue", Icons.Default.Translate, colors.primary) to {
                        viewModel.openSubScreen(SubScreen.MEDICAL_TRANSLATOR)
                    }
                )

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(actionPills) { (pill, action) ->
                        Surface(
                            modifier = Modifier.clickable { action() },
                            shape = RoundedCornerShape(12.dp),
                            color = colors.surfaceVariant,
                            border = BorderStroke(1.dp, pill.third.copy(alpha = 0.45f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(pill.second, null, tint = pill.third, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    pill.first,
                                    color = colors.onSurface,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // 4. BENTO GRID SECTION: Diverse Tile Formats (Eliminating boring stacked rectangles!)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        "MODULES CLINIQUES & INNOVATIONS",
                        color = colors.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // BENTO HERO 1: Complications Post-Op et leur Gestion (NEW USER REQUEST)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.openSubScreen(SubScreen.POST_OP_COMPLICATIONS) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
                        border = BorderStroke(1.2.dp, colors.error.copy(alpha = 0.65f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(colors.error.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Healing, null, tint = colors.error, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            "Complications Post-Op & SSPI",
                                            color = colors.onSurface,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            "Détection précoce & conduite à tenir",
                                            color = colors.onSurfaceVariant,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = colors.error.copy(alpha = 0.18f),
                                    border = BorderStroke(0.5.dp, colors.error)
                                ) {
                                    Text(
                                        "8 PROTOCOLES",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        color = colors.error,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            // Complication pills
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("Dépression Resp.", "Laryngospasme", "Choc", "NVPO").forEach { tag ->
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = colors.surface.copy(alpha = 0.7f),
                                        border = BorderStroke(0.5.dp, colors.outline.copy(alpha = 0.3f))
                                    ) {
                                        Text(
                                            tag,
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                            color = colors.onSurface,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // BENTO SPLIT ROW 1: Ventilation Simulator vs ECG Challenge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        BentoTile(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Air,
                            title = "Simulateur Ventilation",
                            subtitle = "VC, VACI, VSAI & PEP",
                            tag = "Simulation",
                            accentColor = colors.primary,
                            onClick = { viewModel.openSubScreen(SubScreen.VENTILATION_SIMULATOR) }
                        )

                        BentoTile(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.MonitorHeart,
                            title = "ECG Challenge",
                            subtitle = "Tracés, rythmes & quiz",
                            tag = "Cardio",
                            accentColor = colors.tertiary,
                            onClick = { viewModel.openSubScreen(SubScreen.ECG_CHALLENGE) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // BENTO SPLIT ROW 2: Check-list Bloc vs Timeline 7 Étapes
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        BentoTile(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.Checklist,
                            title = "Check-list Bloc",
                            subtitle = "Respirateur & Sign In",
                            tag = "Sécurité",
                            accentColor = colors.primary,
                            onClick = { viewModel.openSubScreen(SubScreen.CHECKLIST) }
                        )

                        BentoTile(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Timeline,
                            title = "7 Étapes Anesthésie",
                            subtitle = "Parcours peropératoire",
                            tag = "Workflow",
                            accentColor = colors.tertiary,
                            onClick = { viewModel.openSubScreen(SubScreen.ANESTHESIA_TIMELINE) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 4 Grid Tiles for Core Clinical Tools
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        BentoTile(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Medication,
                            title = "Pharmacologie",
                            subtitle = "70+ drogues pondérées",
                            tag = "Doses",
                            accentColor = colors.primary,
                            onClick = { viewModel.setTab(MainTab.PHARMACO) }
                        )

                        BentoTile(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Calculate,
                            title = "Calculateurs",
                            subtitle = "IMC, Apfel, Aldrete",
                            tag = "Biométrie",
                            accentColor = colors.tertiary,
                            onClick = { viewModel.openSubScreen(SubScreen.CALCULATORS) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        BentoTile(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Emergency,
                            title = "Urgences Vitales",
                            subtitle = "ACR, CICO, Anaphylaxie",
                            tag = "Critique",
                            accentColor = colors.error,
                            onClick = {
                                viewModel.setTab(MainTab.PROTOCOLS)
                                viewModel.setProtocolCategory("URGENCE_CRISE")
                            }
                        )

                        BentoTile(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Translate,
                            title = "Traducteur Médical",
                            subtitle = "FR / AR / Darija",
                            tag = "Trilingue",
                            accentColor = colors.primary,
                            onClick = { viewModel.openSubScreen(SubScreen.MEDICAL_TRANSLATOR) }
                        )
                    }
                }
            }

            // 5. Creator Signature Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface),
                    border = BorderStroke(1.dp, colors.tertiary.copy(alpha = 0.45f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(colors.tertiary)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Abdellah Boutmad • IAR Anesthésie-Réanimation",
                                color = colors.onSurface,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "ISPITS GUELMIM — Conçu pour l'excellence et la sécurité des patients au bloc opératoire.",
                                color = colors.onSurfaceVariant,
                                fontSize = 10.5.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KpiCounter(count: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count, color = color, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun BentoTile(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String,
    tag: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = modifier
            .heightIn(min = 108.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = accentColor, modifier = Modifier.size(18.dp))
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = accentColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        tag,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                        color = accentColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column {
                Text(
                    title,
                    color = colors.onSurface,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 15.sp,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    subtitle,
                    color = colors.onSurfaceVariant,
                    fontSize = 10.5.sp,
                    lineHeight = 13.sp,
                    maxLines = 2
                )
            }
        }
    }
}
