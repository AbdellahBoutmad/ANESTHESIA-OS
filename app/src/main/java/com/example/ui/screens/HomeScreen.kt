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
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Biotech
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.ui.theme.AlertRed
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.DarkNightBlue
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MedicalCyan
import com.example.ui.theme.MedicalNavy
import com.example.ui.theme.MedicalNavyElevated
import com.example.ui.theme.MedicalNavySurface
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SecondaryGrey
import com.example.ui.theme.TurquoiseGreen
import com.example.ui.theme.WarningOrange

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val savedPatients by viewModel.savedAssessments.collectAsState()
    val savedChecklists by viewModel.savedChecklists.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // 1. Top Search Bar
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkNightBlue)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
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
                            color = SecondaryGrey,
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Recherche",
                            tint = MedicalCyan
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Effacer",
                                    tint = SecondaryGrey
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MedicalNavySurface,
                        unfocusedContainerColor = MedicalNavySurface,
                        focusedBorderColor = MedicalCyan,
                        unfocusedBorderColor = BorderSubtle,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite
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
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MedicalNavyElevated),
                    border = BorderStroke(1.dp, MedicalCyan.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "Résultats de recherche rapide (${matchingDrugs.size + matchingProtocols.size + matchingBio.size})",
                            color = MedicalCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (matchingDrugs.isNotEmpty()) {
                            Text("Médicaments :", color = TurquoiseGreen, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
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
                                    Icon(Icons.Default.Medication, null, tint = MedicalCyan, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(drug.name, color = PureWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("(${drug.category})", color = SecondaryGrey, fontSize = 11.sp)
                                }
                            }
                        }

                        if (matchingProtocols.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Protocoles :", color = AlertRed, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
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
                                    Icon(Icons.Default.Emergency, null, tint = AlertRed, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(prot.title, color = PureWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Welcome Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MedicalNavy),
                border = BorderStroke(1.dp, MedicalCyan.copy(alpha = 0.3f))
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
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(ChampagneGold)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Anestheia Trone",
                                    color = ChampagneGold,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Système d'Anesthésie-Réanimation",
                                color = PureWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MedicalNavySurface,
                            border = BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.45f))
                        ) {
                            Text(
                                "Bloc Opératoire",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = ChampagneGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Accédez instantanément aux protocoles validés, pharmacologie avec adaptation pondérale, check-list du respirateur et outils d'aide à la décision clinique.",
                        color = SecondaryGrey,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

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
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MedicalCyan,
                                contentColor = DarkNightBlue
                            )
                        ) {
                            Icon(Icons.Default.PersonAdd, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Nouvelle évaluation", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                viewModel.setTab(MainTab.PROTOCOLS)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_protocols"),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, TurquoiseGreen),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TurquoiseGreen)
                        ) {
                            Icon(Icons.Default.AssignmentTurnedIn, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Protocoles cliniques", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Creator Signature Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MedicalNavySurface),
                border = BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(ChampagneGold)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Conçu et développé par Abdellah Boutmad, étudiant en anesthésie-réanimation (IAR).",
                        color = PureWhite,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // 3. Quick Emergency Alert Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clickable {
                        viewModel.setTab(MainTab.PROTOCOLS)
                        viewModel.setProtocolCategory("URGENCE_CRISE")
                    }
                    .testTag("emergency_banner"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AlertRed.copy(alpha = 0.15f)),
                border = BorderStroke(1.dp, AlertRed.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AlertRed.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Emergency, "Urgences", tint = AlertRed, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "URGENCES ET CRISES PEROPÉRATOIRES",
                            color = AlertRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Accès rapide CICO, Choc Anaphylactique, Arrêt Cardiaque, LAST, Hyperthermie Maligne",
                            color = PureWhite,
                            fontSize = 11.sp
                        )
                    }
                    Text("OUVRIR", color = AlertRed, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }

        // 8 Emergency Protocol Quick Chips
        item {
            val emergencyProtocols = listOf(
                "acr_protocol" to "ACR",
                "anaphylaxis_protocol" to "Anaphylaxie",
                "cico_protocol" to "CICO",
                "hypoxemia_protocol" to "Hypoxémie",
                "hypotension_shock_protocol" to "Choc & TA",
                "opioid_overdose_protocol" to "Opioïdes",
                "hyperthermia_protocol" to "Hyperth. Maligne",
                "respiratory_bronchospasm_protocol" to "Bronchospasme"
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(emergencyProtocols) { (protoId, label) ->
                    Surface(
                        modifier = Modifier.clickable {
                            val p = viewModel.protocolsList.firstOrNull { it.id == protoId }
                            if (p != null) {
                                viewModel.selectProtocol(p)
                            } else {
                                viewModel.setTab(MainTab.PROTOCOLS)
                                viewModel.setProtocolCategory("URGENCE_CRISE")
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = AlertRed.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, AlertRed.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(AlertRed)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                label,
                                color = PureWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 4. Les 8 Cartes Principales du Dashboard (Grille 2 colonnes)
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    "MODULES CLINIQUES PRINCIPAUX",
                    color = MedicalCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Ligne 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ModuleGridCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.PersonAdd,
                        title = "Évaluation Pré-anesthésique",
                        subtitle = "Consultation, Mallampati & Score ASA",
                        badge = "Étape 1-5",
                        badgeColor = MedicalCyan,
                        onClick = { viewModel.setTab(MainTab.PATIENTS) }
                    )
                    ModuleGridCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Outlined.Checklist,
                        title = "Check-list du Bloc",
                        subtitle = "Respirateur, aspiration & OMS Sign In",
                        badge = "Sécurité",
                        badgeColor = TurquoiseGreen,
                        onClick = { viewModel.openSubScreen(SubScreen.CHECKLIST) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Ligne 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ModuleGridCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Medication,
                        title = "Pharmacologie",
                        subtitle = "Hypnotiques, curares, morphiniques & doses",
                        badge = "Calculateur",
                        badgeColor = MedicalCyan,
                        onClick = { viewModel.setTab(MainTab.PHARMACO) }
                    )
                    ModuleGridCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Outlined.Biotech,
                        title = "Examens Biologiques",
                        subtitle = "NFS, hémostase, iono & règles de report",
                        badge = "Analyses",
                        badgeColor = WarningOrange,
                        onClick = { viewModel.openSubScreen(SubScreen.BIOLOGY) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Ligne 3
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ModuleGridCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Calculate,
                        title = "Calculateurs Cliniques",
                        subtitle = "IMC, Devine, Cockcroft, Apfel & Aldrete",
                        badge = "8 Scores",
                        badgeColor = TurquoiseGreen,
                        onClick = { viewModel.openSubScreen(SubScreen.CALCULATORS) }
                    )
                    ModuleGridCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.LocalHospital,
                        title = "Protocoles Chirurgicaux",
                        subtitle = "Digestif, orthopédie, césarienne & rachis",
                        badge = "Spécialités",
                        badgeColor = MedicalCyan,
                        onClick = {
                            viewModel.setTab(MainTab.PROTOCOLS)
                            viewModel.setProtocolCategory("CHIRURGIE")
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Ligne 4
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ModuleGridCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Emergency,
                        title = "Urgences & Réanimation",
                        subtitle = "Algorithmes de sauvetage SFAR/DAS",
                        badge = "Critique",
                        badgeColor = AlertRed,
                        onClick = {
                            viewModel.setTab(MainTab.PROTOCOLS)
                            viewModel.setProtocolCategory("URGENCE_CRISE")
                        }
                    )
                    ModuleGridCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Psychology,
                        title = "Assistant IA abdellahTron",
                        subtitle = "Parler à abdellahTron • Intelligence clinique",
                        badge = "abdellahTron",
                        badgeColor = ElectricCyan,
                        onClick = { viewModel.setTab(MainTab.AI) }
                    )
                }
            }
        }

        // 5. Parcours Périopératoire Interactif
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MedicalNavySurface),
                border = BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        "PARCOURS PÉRIOPÉRATOIRE INTÉGRÉ",
                        color = TurquoiseGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    val steps = listOf(
                        "1. Consultation" to "Évaluation ASA & Risques",
                        "2. Préparation" to "Check-list Respirateur & Drogues",
                        "3. Sign In" to "Check-list Sécurité OMS",
                        "4. Bloc Opératoire" to "Induction & Time Out",
                        "5. SSPI Réveil" to "Score d'Aldrete >= 9/10"
                    )

                    steps.forEachIndexed { idx, pair ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(MedicalCyan.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("${idx + 1}", color = MedicalCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(pair.first, color = PureWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("— ${pair.second}", color = SecondaryGrey, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // 6. Activité Récente (Dernières évaluations et check-lists)
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                Text(
                    "ACTIVITÉ RÉCENTE AU BLOC",
                    color = MedicalCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                if (savedPatients.isEmpty()) {
                    Text("Aucune consultation enregistrée pour l'instant.", color = SecondaryGrey, fontSize = 12.sp)
                } else {
                    savedPatients.take(2).forEach { p ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MedicalNavy),
                            border = BorderStroke(1.dp, BorderSubtle)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(p.patientName, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("${p.age} ans • ${p.surgery}", color = SecondaryGrey, fontSize = 12.sp)
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MedicalNavySurface,
                                    border = BorderStroke(1.dp, MedicalCyan.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        p.asaScore,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        color = MedicalCyan,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModuleGridCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String,
    badge: String,
    badgeColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .heightIn(min = 104.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MedicalNavy),
        border = BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = badgeColor, modifier = Modifier.size(18.dp))
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = badgeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        badge,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                        color = badgeColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Column {
                Text(
                    title,
                    color = PureWhite,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 15.sp,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    subtitle,
                    color = SecondaryGrey,
                    fontSize = 10.sp,
                    lineHeight = 13.sp,
                    maxLines = 2
                )
            }
        }
    }
}
