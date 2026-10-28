package com.example.ui.screens.escaperoom

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.escaperoom.ClinicalAction
import com.example.data.escaperoom.DifficultyLevel
import com.example.data.escaperoom.EscapeRoomAiService
import com.example.data.escaperoom.EscapeRoomScenario
import com.example.data.escaperoom.PredefinedScenarios
import com.example.data.escaperoom.ScenarioCategory
import com.example.data.escaperoom.SessionOutcome
import com.example.ui.MainTab
import com.example.ui.MainViewModel
import com.example.ui.SubScreen
import com.example.ui.theme.AlertRed
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.DarkNightBlue
import com.example.ui.theme.MedicalCyan
import com.example.ui.theme.MedicalNavy
import com.example.ui.theme.MedicalNavyElevated
import com.example.ui.theme.MedicalNavySurface
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SecondaryGrey
import com.example.ui.theme.TurquoiseGreen
import com.example.ui.theme.WarningOrange
import kotlinx.coroutines.launch

@Composable
fun EscapeRoomScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val engine = viewModel.escapeRoomEngine
    val storage = viewModel.escapeRoomStorage

    BackHandler {
        if (engine.activeScenario.value != null && !engine.isFinished.value) {
            engine.exit()
        } else {
            onBack()
        }
    }

    val activeScenario by engine.activeScenario.value?.let { remember { mutableStateOf(it) } } ?: remember { mutableStateOf(null) }
    val currentScenarioState by engine.activeScenario.collectAsState()
    val currentPhase by engine.currentPhase.collectAsState()
    val currentVitals by engine.currentVitals.collectAsState()
    val timeRemaining by engine.timeRemainingSeconds.collectAsState()
    val elapsedSeconds by engine.elapsedSeconds.collectAsState()
    val currentScore by engine.currentScore.collectAsState()
    val isPaused by engine.isPaused.collectAsState()
    val isFinished by engine.isFinished.collectAsState()
    val lastFeedback by engine.lastActionFeedback.collectAsState()
    val simulationResult by engine.simulationResult.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val dynamicScenarios = remember { mutableStateListOf<EscapeRoomScenario>().apply { addAll(PredefinedScenarios.scenarios) } }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<ScenarioCategory?>(null) }

    // When a simulation finishes, automatically persist and navigate to Debriefing tab
    LaunchedEffect(isFinished) {
        if (isFinished && simulationResult != null && currentScenarioState != null) {
            val scenario = currentScenarioState!!
            val res = simulationResult!!
            storage.saveResult(res, scenario.category)
            selectedTabIndex = 2 // Debriefing tab
        }
    }

    val tabTitles = listOf(
        "Missions (5)",
        "Simulateur",
        "Débriefing",
        "Boîte Noire",
        "Générateur IA",
        "Performances"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNightBlue)
            .testTag("escape_room_screen")
    ) {
        // 1. Top Escape Room Header
        Surface(
            color = MedicalNavy,
            border = BorderStroke(0.5.dp, BorderSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            if (currentScenarioState != null && !isFinished) {
                                engine.exit()
                            }
                            onBack()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = MedicalCyan)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "BNaja Escape Room",
                                    color = PureWhite,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = ChampagneGold.copy(alpha = 0.2f),
                                    border = BorderStroke(0.5.dp, ChampagneGold.copy(alpha = 0.8f))
                                ) {
                                    Text(
                                        text = "SIMULATEUR CLINIQUE",
                                        color = ChampagneGold,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Prise de décision en temps réel • Gestion de crise au bloc",
                                color = SecondaryGrey,
                                fontSize = 11.sp
                            )
                        }
                    }

                    if (currentScenarioState != null && !isFinished) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (timeRemaining <= 15) AlertRed.copy(alpha = 0.2f) else MedicalCyan.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, if (timeRemaining <= 15) AlertRed else MedicalCyan)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Timer, null, tint = if (timeRemaining <= 15) AlertRed else MedicalCyan, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${timeRemaining}s",
                                    color = if (timeRemaining <= 15) AlertRed else MedicalCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Scrollable Navigation Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MedicalNavy,
                    contentColor = MedicalCyan,
                    edgePadding = 0.dp,
                    divider = {},
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = MedicalCyan
                        )
                    }
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            }
                        )
                    }
                }
            }
        }

        // 2. Tab Contents
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            when (selectedTabIndex) {
                0 -> MissionsLibraryTab(
                    scenarios = dynamicScenarios,
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it },
                    selectedCategory = selectedCategoryFilter,
                    onSelectCategory = { selectedCategoryFilter = it },
                    onStartScenario = { scenario ->
                        engine.startScenario(scenario)
                        selectedTabIndex = 1 // Switch to active simulator
                    }
                )

                1 -> ActiveSimulatorTab(
                    scenario = currentScenarioState,
                    phase = currentPhase,
                    vitals = currentVitals,
                    timeRemaining = timeRemaining,
                    elapsedSeconds = elapsedSeconds,
                    score = currentScore,
                    isPaused = isPaused,
                    lastFeedback = lastFeedback,
                    onPerformAction = { action -> engine.performAction(action) },
                    onTogglePause = { engine.togglePause() },
                    onRestart = { engine.restart() },
                    onExit = {
                        engine.exit()
                        selectedTabIndex = 0
                    },
                    onSelectMission = { selectedTabIndex = 0 }
                )

                2 -> DebriefingTab(
                    scenario = currentScenarioState,
                    result = simulationResult,
                    onReplayBlackBox = { selectedTabIndex = 3 },
                    onRestart = {
                        currentScenarioState?.let { engine.startScenario(it) }
                        selectedTabIndex = 1
                    },
                    onNewMission = {
                        engine.exit()
                        selectedTabIndex = 0
                    },
                    onRequestAiDebriefing = { scen, res ->
                        scope.launch {
                            Toast.makeText(context, "Analyse IA en cours...", Toast.LENGTH_SHORT).show()
                            val debriefing = EscapeRoomAiService.generateSessionDebriefing(scen, res)
                            engine.setAiDebriefing(debriefing)
                        }
                    }
                )

                3 -> {
                    if (simulationResult != null) {
                        BlackBoxReplayView(result = simulationResult!!)
                    } else {
                        EmptyStateView(
                            title = "Boîte Noire Inactive",
                            description = "Terminez d'abord un cas clinique pour déverrouiller la reconstitution chronologique de vos décisions.",
                            buttonLabel = "Choisir une Mission",
                            onAction = { selectedTabIndex = 0 }
                        )
                    }
                }

                4 -> AiScenarioGeneratorTab(
                    onScenarioCreated = { newScenario ->
                        dynamicScenarios.add(0, newScenario)
                        engine.startScenario(newScenario)
                        selectedTabIndex = 1
                    }
                )

                5 -> PerformanceAnalyticsTab(
                    stats = storage.getAdaptiveStats(),
                    onNavigateSection = { target ->
                        when (target) {
                            "PHARMACO" -> viewModel.setTab(MainTab.PHARMACO)
                            "PROTOCOLS" -> viewModel.setTab(MainTab.PROTOCOLS)
                            "CALCULATORS" -> viewModel.openSubScreen(SubScreen.CALCULATORS)
                            "QUIZ" -> viewModel.openSubScreen(SubScreen.QUIZ)
                            else -> selectedTabIndex = 0
                        }
                    }
                )
            }
        }
    }
}

/**
 * TAB 0: Missions Scenario Library
 */
@Composable
fun MissionsLibraryTab(
    scenarios: List<EscapeRoomScenario>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedCategory: ScenarioCategory?,
    onSelectCategory: (ScenarioCategory?) -> Unit,
    onStartScenario: (EscapeRoomScenario) -> Unit
) {
    val filteredScenarios = remember(scenarios, searchQuery, selectedCategory) {
        scenarios.filter { s ->
            val matchText = s.title.contains(searchQuery, ignoreCase = true) ||
                    s.subtitle.contains(searchQuery, ignoreCase = true) ||
                    s.patient.programmedSurgery.contains(searchQuery, ignoreCase = true)
            val matchCat = selectedCategory == null || s.category == selectedCategory
            matchText && matchCat
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Search Input
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Rechercher un cas clinique (ex: Laryngospasme, Anaphylaxie)...", fontSize = 12.sp, color = SecondaryGrey) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = MedicalCyan) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PureWhite,
                    unfocusedTextColor = PureWhite,
                    focusedContainerColor = MedicalNavySurface,
                    unfocusedContainerColor = MedicalNavySurface,
                    focusedBorderColor = MedicalCyan,
                    unfocusedBorderColor = BorderSubtle
                ),
                singleLine = true
            )
        }

        // Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { onSelectCategory(null) },
                        label = { Text("Tous les thèmes", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MedicalCyan,
                            selectedLabelColor = DarkNightBlue,
                            containerColor = MedicalNavy
                        )
                    )
                }
                items(ScenarioCategory.values()) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { onSelectCategory(if (selectedCategory == cat) null else cat) },
                        label = { Text(cat.label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MedicalCyan,
                            selectedLabelColor = DarkNightBlue,
                            containerColor = MedicalNavy
                        )
                    )
                }
            }
        }

        items(filteredScenarios) { scen ->
            ScenarioCardItem(scenario = scen, onStart = { onStartScenario(scen) })
        }
    }
}

@Composable
fun ScenarioCardItem(
    scenario: EscapeRoomScenario,
    onStart: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MedicalNavy),
        border = BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Badges row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(scenario.category.colorHex).copy(alpha = 0.2f),
                    border = BorderStroke(0.5.dp, Color(scenario.category.colorHex))
                ) {
                    Text(
                        text = scenario.category.label,
                        color = Color(scenario.category.colorHex),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (scenario.isVerified) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = TurquoiseGreen.copy(alpha = 0.15f),
                            border = BorderStroke(0.5.dp, TurquoiseGreen)
                        ) {
                            Text(
                                text = "VERIFIÉ CLINICIEN",
                                color = TurquoiseGreen,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = WarningOrange.copy(alpha = 0.15f),
                            border = BorderStroke(0.5.dp, WarningOrange)
                        ) {
                            Text(
                                text = "GÉNÉRÉ PAR IA",
                                color = WarningOrange,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${scenario.estimatedDurationMinutes} min",
                        color = SecondaryGrey,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = scenario.title,
                color = PureWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Text(
                text = scenario.subtitle,
                color = MedicalCyan,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Patient Brief Box
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = DarkNightBlue,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "Patient : ${scenario.patient.gender}, ${scenario.patient.age} ans, ${scenario.patient.weightKg} kg • ${scenario.patient.asaScore}",
                        color = PureWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Chirurgie : ${scenario.patient.programmedSurgery}",
                        color = SecondaryGrey,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Objectives tags
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                scenario.learningObjectives.take(2).forEach { obj ->
                    Row(verticalAlignment = Alignment.Top) {
                        Text("• ", color = ChampagneGold, fontSize = 12.sp)
                        Text(obj, color = SecondaryGrey, fontSize = 11.sp, maxLines = 1)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onStart,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MedicalCyan, contentColor = DarkNightBlue),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Lancer la Simulation Clinique", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

/**
 * TAB 1: Active Simulator Screen
 */
@Composable
fun ActiveSimulatorTab(
    scenario: EscapeRoomScenario?,
    phase: com.example.data.escaperoom.ScenarioPhase?,
    vitals: com.example.data.escaperoom.SimulatedVitals?,
    timeRemaining: Int,
    elapsedSeconds: Int,
    score: Int,
    isPaused: Boolean,
    lastFeedback: Pair<String, Boolean>?,
    onPerformAction: (ClinicalAction) -> Unit,
    onTogglePause: () -> Unit,
    onRestart: () -> Unit,
    onExit: () -> Unit,
    onSelectMission: () -> Unit
) {
    if (scenario == null || phase == null || vitals == null) {
        EmptyStateView(
            title = "Aucune Simulation Active",
            description = "Sélectionnez un cas clinique dans l'onglet Missions pour démarrer la gestion de crise.",
            buttonLabel = "Voir les Missions Disponibles",
            onAction = onSelectMission
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Controls Toolbar
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MedicalNavy),
                border = BorderStroke(1.dp, BorderSubtle),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (score >= 70) TurquoiseGreen.copy(alpha = 0.2f) else AlertRed.copy(alpha = 0.2f),
                            border = BorderStroke(0.5.dp, if (score >= 70) TurquoiseGreen else AlertRed)
                        ) {
                            Text(
                                text = "Score : $score/100",
                                color = if (score >= 70) TurquoiseGreen else AlertRed,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Temps : ${elapsedSeconds}s",
                            color = SecondaryGrey,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onTogglePause, modifier = Modifier.size(34.dp)) {
                            Icon(
                                if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                contentDescription = "Pause",
                                tint = PureWhite
                            )
                        }
                        IconButton(onClick = onRestart, modifier = Modifier.size(34.dp)) {
                            Icon(Icons.Default.Refresh, contentDescription = "Recommencer", tint = PureWhite)
                        }
                        OutlinedButton(
                            onClick = onExit,
                            border = BorderStroke(1.dp, AlertRed.copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertRed),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Quitter", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Critical Patient Scope Monitor
        item {
            ClinicalMonitorView(
                vitals = vitals,
                isCriticalAlarm = vitals.isCritical || timeRemaining <= 15
            )
        }

        // Narrative Clinical Situation Box
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MedicalNavyElevated),
                border = BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = phase.title,
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp
                        )

                        Text(
                            text = "Délai : ${timeRemaining}s",
                            color = if (timeRemaining <= 15) AlertRed else MedicalCyan,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = phase.narrative,
                        color = PureWhite,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    phase.criticalAlertMessage?.let { alert ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AlertRed.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, AlertRed.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Warning, null, tint = AlertRed, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(alert, color = AlertRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Last action outcome banner
        if (lastFeedback != null) {
            item {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (lastFeedback.second) TurquoiseGreen.copy(alpha = 0.15f) else AlertRed.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, if (lastFeedback.second) TurquoiseGreen else AlertRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Résultat précédent : ${lastFeedback.first}",
                        color = if (lastFeedback.second) TurquoiseGreen else AlertRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }

        // Available Clinical Decisions Header
        item {
            Text(
                text = "ACTIONS CLINIQUES DISPONIBLES (CLIQUEZ POUR EXÉCUTER) :",
                color = MedicalCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 0.8.sp
            )
        }

        // Interactive Action Cards
        items(phase.availableActions) { action ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !isPaused) { onPerformAction(action) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MedicalNavySurface),
                border = BorderStroke(1.dp, BorderSubtle)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MedicalCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (action.category) {
                                com.example.data.escaperoom.ActionCategory.AIRWAY_OXYGEN -> Icons.Default.CrisisAlert
                                com.example.data.escaperoom.ActionCategory.PHARMACO_DRUGS -> Icons.Default.Medication
                                com.example.data.escaperoom.ActionCategory.VENTILATION -> Icons.Default.Emergency
                                com.example.data.escaperoom.ActionCategory.CALL_ASSISTANCE -> Icons.Default.School
                                com.example.data.escaperoom.ActionCategory.FLUID_HEMODYNAMICS -> Icons.Default.Calculate
                                com.example.data.escaperoom.ActionCategory.DIAGNOSTIC_EXAM -> Icons.Default.Search
                            },
                            contentDescription = null,
                            tint = MedicalCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = action.title,
                            color = PureWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        if (action.description.isNotBlank()) {
                            Text(
                                text = action.description,
                                color = SecondaryGrey,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = DarkNightBlue
                    ) {
                        Text(
                            text = "+${action.timeCostSeconds}s",
                            color = SecondaryGrey,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * TAB 2: Debriefing & Smart Analysis
 */
@Composable
fun DebriefingTab(
    scenario: EscapeRoomScenario?,
    result: com.example.data.escaperoom.SimulationResult?,
    onReplayBlackBox: () -> Unit,
    onRestart: () -> Unit,
    onNewMission: () -> Unit,
    onRequestAiDebriefing: (EscapeRoomScenario, com.example.data.escaperoom.SimulationResult) -> Unit
) {
    if (scenario == null || result == null) {
        EmptyStateView(
            title = "Aucun Débriefing Disponible",
            description = "Terminez une session de simulation pour accéder au compte-rendu pédagogique et à l'analyse de vos décisions.",
            buttonLabel = "Lancer une Mission",
            onAction = onNewMission
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Outcome & Score Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MedicalNavyElevated),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, if (result.outcome.isSuccess) TurquoiseGreen else AlertRed),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (result.outcome.isSuccess) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = if (result.outcome.isSuccess) TurquoiseGreen else AlertRed,
                        modifier = Modifier.size(46.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = result.outcome.label,
                        color = if (result.outcome.isSuccess) TurquoiseGreen else AlertRed,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = scenario.title,
                        color = SecondaryGrey,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ScoreStatPill(title = "SCORE FINAL", value = "${result.finalScore}/100", color = if (result.finalScore >= 75) TurquoiseGreen else AlertRed)
                        ScoreStatPill(title = "DURÉE TOTALE", value = "${result.totalTimeSeconds}s", color = MedicalCyan)
                        ScoreStatPill(title = "ERREURS", value = "${result.incorrectDecisionsCount}", color = if (result.incorrectDecisionsCount == 0) TurquoiseGreen else AlertRed)
                    }
                }
            }
        }

        // Quick Action Buttons (Black Box Replay, Restart)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onReplayBlackBox,
                    colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = DarkNightBlue),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.History, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Rejouer Boîte Noire", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onRestart,
                    border = BorderStroke(1.dp, MedicalCyan),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MedicalCyan),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Recommencer", fontSize = 12.sp)
                }
            }
        }

        // BNaja AI Personalized Moroccan Darija Analysis
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MedicalNavy),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MedicalCyan.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, null, tint = MedicalCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Débriefing BNaja AI (الدارجة المغربية)", color = MedicalCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        if (result.aiPersonalizedDebriefing == null) {
                            Button(
                                onClick = { onRequestAiDebriefing(scenario, result) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MedicalCyan, contentColor = DarkNightBlue),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Générer l'Analyse", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (result.aiPersonalizedDebriefing != null) {
                        Text(
                            text = result.aiPersonalizedDebriefing,
                            color = PureWhite,
                            fontSize = 12.5.sp,
                            lineHeight = 18.sp
                        )
                    } else {
                        Text(
                            text = "Cliquez sur 'Générer l'Analyse' pour recevoir le retour pédagogique détaillé de BNaja AI en darija marocaine sur la pertinence de vos choix.",
                            color = SecondaryGrey,
                            fontSize = 11.5.sp
                        )
                    }
                }
            }
        }

        // Core Diagnosis & Educational Factors
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MedicalNavySurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("DIAGNOSTIC OFFICIEL :", color = ChampagneGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Text(scenario.debriefing.coreDiagnosis, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(scenario.debriefing.pathophysiologicalSummary, color = SecondaryGrey, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("FACTEURS CLÉS DE SUCCÈS :", color = TurquoiseGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    scenario.debriefing.criticalSuccessFactors.forEach { factor ->
                        Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                            Text("✓ ", color = TurquoiseGreen, fontWeight = FontWeight.Bold)
                            Text(factor, color = PureWhite, fontSize = 11.5.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("PIÈGES FATALS :", color = AlertRed, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    scenario.debriefing.fatalPitfalls.forEach { pitfall ->
                        Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                            Text("✗ ", color = AlertRed, fontWeight = FontWeight.Bold)
                            Text(pitfall, color = PureWhite, fontSize = 11.5.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("RÉFÉRENCES CLINIQUES :", color = MedicalCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    scenario.debriefing.clinicalReferences.forEach { ref ->
                        Text("• $ref", color = SecondaryGrey, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreStatPill(title: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, color = SecondaryGrey, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(value, color = color, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
    }
}

/**
 * TAB 4: AI Scenario Generator
 */
@Composable
fun AiScenarioGeneratorTab(
    onScenarioCreated: (EscapeRoomScenario) -> Unit
) {
    var specialty by remember { mutableStateOf("Anesthésie & Urgences") }
    var topic by remember { mutableStateOf("Intubation difficile imprévue et désaturation") }
    var selectedDifficulty by remember { mutableStateOf(DifficultyLevel.INTERMEDIATE_2ND_YEAR) }
    var studentYear by remember { mutableStateOf("2ème Année IAR") }
    var isGenerating by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MedicalNavyElevated),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, null, tint = ChampagneGold, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Générateur de Cas par Gemini AI", color = ChampagneGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Créez un scénario d'urgence clinique sur-mesure validé par le moteur de simulation déterministe.",
                        color = SecondaryGrey,
                        fontSize = 11.5.sp
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = specialty,
                onValueChange = { specialty = it },
                label = { Text("Discipline / Spécialité") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PureWhite,
                    unfocusedTextColor = PureWhite,
                    focusedBorderColor = MedicalCyan,
                    unfocusedBorderColor = BorderSubtle
                )
            )
        }

        item {
            OutlinedTextField(
                value = topic,
                onValueChange = { topic = it },
                label = { Text("Complication / Urgence Vitale") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PureWhite,
                    unfocusedTextColor = PureWhite,
                    focusedBorderColor = MedicalCyan,
                    unfocusedBorderColor = BorderSubtle
                )
            )
        }

        item {
            Text("Niveau de Difficulté :", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                DifficultyLevel.values().forEach { diff ->
                    FilterChip(
                        selected = selectedDifficulty == diff,
                        onClick = { selectedDifficulty = diff },
                        label = { Text(diff.label, fontSize = 10.sp) },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MedicalCyan,
                            selectedLabelColor = DarkNightBlue
                        )
                    )
                }
            }
        }

        if (errorMessage != null) {
            item {
                Surface(
                    color = AlertRed.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, AlertRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(errorMessage ?: "", color = AlertRed, fontSize = 11.5.sp, modifier = Modifier.padding(10.dp))
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = {
                    isGenerating = true
                    errorMessage = null
                    scope.launch {
                        val result = EscapeRoomAiService.generateScenario(
                            specialty = specialty,
                            topic = topic,
                            difficulty = selectedDifficulty,
                            studentYear = studentYear
                        )
                        isGenerating = false
                        if (result.isSuccess) {
                            onScenarioCreated(result.getOrThrow())
                        } else {
                            errorMessage = "Impossible de générer le cas via l'API (quota ou réseau). Veuillez réessayer ou utiliser l'un des cas vérifiés de la bibliothèque."
                        }
                    }
                },
                enabled = !isGenerating && topic.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = MedicalCyan, contentColor = DarkNightBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = DarkNightBlue, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Génération en cours...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Générer et Jouer le Cas", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * TAB 5: Adaptive Learning & Analytics
 */
@Composable
fun PerformanceAnalyticsTab(
    stats: com.example.data.escaperoom.AdaptiveLearningStats,
    onNavigateSection: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Summary Cards
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MedicalNavyElevated),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("ANALYSE DE PERFORMANCE & PROGRESSION", color = ChampagneGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        ScoreStatPill(title = "SIMULATIONS", value = "${stats.totalSimulations}", color = PureWhite)
                        ScoreStatPill(title = "TAUX DE SUCCÈS", value = "${stats.successRatePercent}%", color = TurquoiseGreen)
                        ScoreStatPill(title = "SCORE MOYEN", value = "${stats.averageScore}/100", color = MedicalCyan)
                    }
                }
            }
        }

        // Strengths & Weaknesses
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MedicalNavy),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("POINT FORT :", color = TurquoiseGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(stats.bestSpecialty, color = PureWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = MedicalNavy),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("POINT À TRAVAILLER :", color = WarningOrange, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(stats.weakestSpecialty, color = PureWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Recommendations
        item {
            Text(
                "RECOMMANDATIONS ADAPTATIVES BNaja :",
                color = MedicalCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 0.8.sp
            )
        }

        items(stats.recommendations) { rec ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MedicalNavySurface),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateSection(rec.targetSection) }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(MedicalCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.MenuBook, null, tint = MedicalCyan, modifier = Modifier.size(16.dp))
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(rec.title, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                        Text(rec.reason, color = SecondaryGrey, fontSize = 11.sp)
                    }

                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = SecondaryGrey, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(
    title: String,
    description: String,
    buttonLabel: String,
    onAction: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MedicalNavy),
        border = BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Psychology, null, tint = MedicalCyan, modifier = Modifier.size(46.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text(title, color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                description,
                color = SecondaryGrey,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(containerColor = MedicalCyan, contentColor = DarkNightBlue)
            ) {
                Text(buttonLabel, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}
