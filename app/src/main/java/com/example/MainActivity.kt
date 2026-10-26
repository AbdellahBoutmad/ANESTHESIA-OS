package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Biotech
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainTab
import com.example.ui.MainViewModel
import com.example.ui.SubScreen
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.AnesthesiaTimelineScreen
import com.example.ui.screens.BiologyScreen
import com.example.ui.screens.CalculatorsScreen
import com.example.ui.screens.ChecklistScreen
import com.example.ui.screens.EcgChallengeScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MedicalTranslatorScreen
import com.example.ui.screens.PatientsScreen
import com.example.ui.screens.PharmacologyScreen
import com.example.ui.screens.ProtocolsScreen
import com.example.ui.screens.ThemeChooserDialog
import com.example.ui.screens.TrainingScreen
import com.example.ui.screens.VentilationSimulatorScreen
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AnesthesiaOsTheme
import com.example.ui.theme.AnestheiaTroneTheme
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.DarkNightBlue
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.DeepNavyElevated
import com.example.ui.theme.DeepNavySurface
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MedicalCyan
import com.example.ui.theme.MedicalNavy
import com.example.ui.theme.MedicalNavyElevated
import com.example.ui.theme.MedicalNavySurface
import com.example.ui.theme.MedicalTeal
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SecondaryGrey
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SoftWhite
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.InfoBlue
import com.example.ui.theme.TurquoiseGreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val currentPalette by viewModel.themePalette.collectAsState()
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()

            AnestheiaTroneTheme(palette = currentPalette, darkTheme = isDarkTheme) {
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                var showAboutDialog by remember { mutableStateOf(false) }
                var showThemeDialog by remember { mutableStateOf(false) }

                val currentTab by viewModel.currentTab.collectAsState()
                val activeSubScreen by viewModel.activeSubScreen.collectAsState()

                if (showAboutDialog) {
                    AboutDialog(onDismiss = { showAboutDialog = false })
                }

                if (showThemeDialog) {
                    ThemeChooserDialog(
                        currentPalette = currentPalette,
                        isDarkTheme = isDarkTheme,
                        onPaletteSelect = { viewModel.setThemePalette(it) },
                        onDarkThemeToggle = { viewModel.toggleDarkTheme(it) },
                        onDismiss = { showThemeDialog = false }
                    )
                }

                // Intercept Android Back Button for sub-screens
                BackHandler(enabled = activeSubScreen != SubScreen.NONE) {
                    viewModel.closeSubScreen()
                }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(
                            drawerContainerColor = MedicalNavyElevated,
                            drawerContentColor = PureWhite,
                            modifier = Modifier.width(320.dp)
                        ) {
                            DrawerContent(
                                currentTab = currentTab,
                                activeSubScreen = activeSubScreen,
                                onSelectTab = { tab ->
                                    viewModel.setTab(tab)
                                    scope.launch { drawerState.close() }
                                },
                                onOpenSubScreen = { sub ->
                                    viewModel.openSubScreen(sub)
                                    scope.launch { drawerState.close() }
                                },
                                onOpenAbout = {
                                    showAboutDialog = true
                                    scope.launch { drawerState.close() }
                                },
                                onOpenTheme = {
                                    showThemeDialog = true
                                    scope.launch { drawerState.close() }
                                }
                            )
                        }
                    }
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = DarkNightBlue,
                        topBar = {
                            CenterAlignedTopAppBar(
                                title = {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(horizontal = 4.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .clip(CircleShape)
                                                    .background(ChampagneGold)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Abdellah Boutmad",
                                                color = SoftWhite,
                                                fontWeight = FontWeight.ExtraBold,
                                                fontSize = 14.5.sp,
                                                letterSpacing = 0.3.sp,
                                                maxLines = 1
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = ChampagneGold.copy(alpha = 0.15f),
                                                border = BorderStroke(0.5.dp, ChampagneGold.copy(alpha = 0.5f))
                                            ) {
                                                Text(
                                                    text = "Anestheia Trone",
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp),
                                                    color = ChampagneGold,
                                                    fontSize = 8.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(1.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "IAR • Anesthésie-Réanimation",
                                                color = MedicalTeal,
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                letterSpacing = 0.2.sp
                                            )
                                            val badge = getScreenBadge(currentTab, activeSubScreen)
                                            if (badge.isNotBlank()) {
                                                Text(
                                                    text = " • $badge",
                                                    color = SlateGray,
                                                    fontSize = 10.sp,
                                                    maxLines = 1
                                                )
                                            }
                                        }
                                    }
                                },
                                navigationIcon = {
                                    if (activeSubScreen != SubScreen.NONE) {
                                        IconButton(onClick = { viewModel.closeSubScreen() }) {
                                            Icon(Icons.Default.Close, contentDescription = "Fermer", tint = MedicalCyan)
                                        }
                                    } else {
                                        IconButton(
                                            onClick = { scope.launch { drawerState.open() } },
                                            modifier = Modifier.testTag("drawer_menu_button")
                                        ) {
                                            Icon(Icons.Default.Menu, contentDescription = "Menu Principal", tint = MedicalCyan)
                                        }
                                    }
                                },
                                actions = {
                                    Surface(
                                        onClick = { showAboutDialog = true },
                                        shape = RoundedCornerShape(8.dp),
                                        color = ChampagneGold.copy(alpha = 0.18f),
                                        border = BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.7f)),
                                        modifier = Modifier
                                            .padding(end = 4.dp)
                                            .testTag("top_about_button")
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Info,
                                                contentDescription = "À propos",
                                                tint = ChampagneGold,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "À propos",
                                                color = ChampagneGold,
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { showThemeDialog = true },
                                        modifier = Modifier.testTag("top_theme_button")
                                    ) {
                                        Icon(Icons.Default.Palette, contentDescription = "Thème & Couleurs", tint = ChampagneGold, modifier = Modifier.size(19.dp))
                                    }
                                    IconButton(
                                        onClick = {
                                            viewModel.setTab(MainTab.PROTOCOLS)
                                            viewModel.setProtocolCategory("URGENCE_CRISE")
                                        },
                                        modifier = Modifier.testTag("top_emergency_button")
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(AlertRed.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Emergency, contentDescription = "Urgences Vitales", tint = AlertRed, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = MedicalNavy
                                )
                            )
                        },
                        bottomBar = {
                            NavigationBar(
                                containerColor = MedicalNavy,
                                contentColor = MedicalCyan
                            ) {
                                val navItems = listOf(
                                    NavigationItem("Accueil", Icons.Default.Home, MainTab.HOME, "tab_home"),
                                    NavigationItem("Patients", Icons.Default.Person, MainTab.PATIENTS, "tab_patients"),
                                    NavigationItem("Pharmaco", Icons.Default.Medication, MainTab.PHARMACO, "tab_pharmaco"),
                                    NavigationItem("Protocoles", Icons.Default.AssignmentTurnedIn, MainTab.PROTOCOLS, "tab_protocols"),
                                    NavigationItem("abdellahTron", Icons.Default.AutoAwesome, MainTab.AI, "tab_ai")
                                )

                                navItems.forEach { item ->
                                    val isSelected = currentTab == item.tab && activeSubScreen == SubScreen.NONE
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = { viewModel.setTab(item.tab) },
                                        icon = {
                                            Icon(
                                                imageVector = item.icon,
                                                contentDescription = item.label,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = item.label,
                                                fontSize = 10.5.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                maxLines = 1
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = DarkNightBlue,
                                            unselectedIconColor = SecondaryGrey,
                                            selectedTextColor = MedicalCyan,
                                            unselectedTextColor = SecondaryGrey,
                                            indicatorColor = MedicalCyan
                                        ),
                                        modifier = Modifier.testTag(item.testTag)
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            // Sub-screens routing
                            when (activeSubScreen) {
                                SubScreen.CHECKLIST -> ChecklistScreen(viewModel = viewModel)
                                SubScreen.BIOLOGY -> BiologyScreen()
                                SubScreen.CALCULATORS -> CalculatorsScreen()
                                SubScreen.QUIZ -> TrainingScreen(viewModel = viewModel)
                                SubScreen.VENTILATION_SIMULATOR -> VentilationSimulatorScreen(viewModel = viewModel, onBack = { viewModel.closeSubScreen() })
                                SubScreen.ECG_CHALLENGE -> EcgChallengeScreen(viewModel = viewModel, onBack = { viewModel.closeSubScreen() })
                                SubScreen.ANESTHESIA_TIMELINE -> AnesthesiaTimelineScreen(viewModel = viewModel, onBack = { viewModel.closeSubScreen() })
                                SubScreen.MEDICAL_TRANSLATOR -> MedicalTranslatorScreen(viewModel = viewModel, onBack = { viewModel.closeSubScreen() })
                                else -> {
                                    // Primary tabs
                                    when (currentTab) {
                                        MainTab.HOME -> HomeScreen(viewModel = viewModel)
                                        MainTab.PATIENTS -> PatientsScreen(viewModel = viewModel)
                                        MainTab.PHARMACO -> PharmacologyScreen(viewModel = viewModel)
                                        MainTab.PROTOCOLS -> ProtocolsScreen(viewModel = viewModel)
                                        MainTab.AI -> AiAssistantScreen(viewModel = viewModel)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getScreenBadge(tab: MainTab, subScreen: SubScreen): String {
        return when (subScreen) {
            SubScreen.CHECKLIST -> "Check-list"
            SubScreen.BIOLOGY -> "Biologie"
            SubScreen.CALCULATORS -> "Calculateurs"
            SubScreen.QUIZ -> "Formation"
            SubScreen.CONSULTATION_WIZARD -> "Consultation"
            SubScreen.DRUG_DETAIL -> "Pharmaco"
            SubScreen.PROTOCOL_DETAIL -> "Protocole"
            SubScreen.VENTILATION_SIMULATOR -> "Ventilation"
            SubScreen.ECG_CHALLENGE -> "ECG Challenge"
            SubScreen.ANESTHESIA_TIMELINE -> "Timeline"
            SubScreen.MEDICAL_TRANSLATOR -> "Traducteur"
            SubScreen.NONE -> {
                when (tab) {
                    MainTab.HOME -> "Tableau de bord"
                    MainTab.PATIENTS -> "Patients"
                    MainTab.PHARMACO -> "Pharmaco"
                    MainTab.PROTOCOLS -> "Protocoles"
                    MainTab.AI -> "abdellahTron"
                }
            }
        }
    }
}

data class NavigationItem(
    val label: String,
    val icon: ImageVector,
    val tab: MainTab,
    val testTag: String
)

@Composable
fun DrawerContent(
    currentTab: MainTab,
    activeSubScreen: SubScreen,
    onSelectTab: (MainTab) -> Unit,
    onOpenSubScreen: (SubScreen) -> Unit,
    onOpenAbout: () -> Unit,
    onOpenTheme: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 24.dp)
            .verticalScroll(scrollState)
    ) {
        // App Drawer Header with creator identity & Anestheia Trone brand
        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .clickable { onOpenAbout() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MedicalTeal.copy(alpha = 0.15f))
                    .border(BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.6f)), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.LocalHospital, null, tint = MedicalTeal, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    "Abdellah Boutmad",
                    color = SoftWhite,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.5.sp,
                    letterSpacing = 0.4.sp
                )
                Text(
                    "IAR • Anesthésie-Réanimation",
                    color = MedicalTeal,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(ChampagneGold)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        "Anestheia Trone",
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 0.8.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = BorderSubtle, thickness = 1.dp)
        Spacer(modifier = Modifier.height(12.dp))

        DrawerSectionHeader("NAVIGATION PRINCIPALE")
        DrawerItem("Tableau de bord", Icons.Default.Home, currentTab == MainTab.HOME && activeSubScreen == SubScreen.NONE) {
            onSelectTab(MainTab.HOME)
        }
        DrawerItem("Évaluation Pré-anesthésique", Icons.Default.Person, currentTab == MainTab.PATIENTS && activeSubScreen == SubScreen.NONE) {
            onSelectTab(MainTab.PATIENTS)
        }
        DrawerItem("Pharmacologie Médicale", Icons.Default.Medication, currentTab == MainTab.PHARMACO && activeSubScreen == SubScreen.NONE) {
            onSelectTab(MainTab.PHARMACO)
        }
        DrawerItem("Protocoles & Chirurgies", Icons.Default.AssignmentTurnedIn, currentTab == MainTab.PROTOCOLS && activeSubScreen == SubScreen.NONE) {
            onSelectTab(MainTab.PROTOCOLS)
        }
        DrawerItem("Assistant IA abdellahTron", Icons.Default.AutoAwesome, currentTab == MainTab.AI && activeSubScreen == SubScreen.NONE) {
            onSelectTab(MainTab.AI)
        }

        Spacer(modifier = Modifier.height(14.dp))
        DrawerSectionHeader("OUTILS DE SALLE D'OPÉRATION")
        DrawerItem("Check-list Matériel & OMS", Icons.Outlined.Checklist, activeSubScreen == SubScreen.CHECKLIST) {
            onOpenSubScreen(SubScreen.CHECKLIST)
        }
        DrawerItem("Calculateurs Cliniques (8 scores)", Icons.Default.Calculate, activeSubScreen == SubScreen.CALCULATORS) {
            onOpenSubScreen(SubScreen.CALCULATORS)
        }
        DrawerItem("Bilan & Examens Biologiques", Icons.Outlined.Biotech, activeSubScreen == SubScreen.BIOLOGY) {
            onOpenSubScreen(SubScreen.BIOLOGY)
        }

        Spacer(modifier = Modifier.height(14.dp))
        DrawerSectionHeader("FORMATION & ACADÉMIE")
        DrawerItem("Cas cliniques & QCM IAR/MAR", Icons.Default.School, activeSubScreen == SubScreen.QUIZ) {
            onOpenSubScreen(SubScreen.QUIZ)
        }

        Spacer(modifier = Modifier.height(14.dp))
        DrawerSectionHeader("SIMULATION & PRATIQUE CLINIQUE")
        DrawerItem("Simulateur de Ventilation", Icons.Default.Air, activeSubScreen == SubScreen.VENTILATION_SIMULATOR) {
            onOpenSubScreen(SubScreen.VENTILATION_SIMULATOR)
        }
        DrawerItem("ECG Challenge (Rythmes)", Icons.Outlined.MonitorHeart, activeSubScreen == SubScreen.ECG_CHALLENGE) {
            onOpenSubScreen(SubScreen.ECG_CHALLENGE)
        }
        DrawerItem("Déroulement de l'anesthésie (7 étapes)", Icons.Default.Timeline, activeSubScreen == SubScreen.ANESTHESIA_TIMELINE) {
            onOpenSubScreen(SubScreen.ANESTHESIA_TIMELINE)
        }
        DrawerItem("Medical Translator (FR/AR/Darija)", Icons.Default.Translate, activeSubScreen == SubScreen.MEDICAL_TRANSLATOR) {
            onOpenSubScreen(SubScreen.MEDICAL_TRANSLATOR)
        }
        DrawerItem("Thème & Couleurs", Icons.Default.Palette, false) {
            onOpenTheme()
        }

        Spacer(modifier = Modifier.height(14.dp))
        DrawerSectionHeader("INFORMATIONS & CRÉDITS")
        DrawerItem("À propos d'Anestheia Trone", Icons.Default.Info, false) {
            onOpenAbout()
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = BorderSubtle, thickness = 1.dp)
        Spacer(modifier = Modifier.height(12.dp))

        // Creator & Scientific Footer
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { onOpenAbout() },
            color = DeepNavySurface,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.25f))
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("CONÇU PAR :", color = ChampagneGold, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                    Text("anestheia_trone", color = SlateGray, fontSize = 9.sp)
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text("Abdellah Boutmad", color = SoftWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("IAR • Anesthésie-Réanimation", color = MedicalTeal, fontSize = 10.5.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text("RÉFÉRENCES SCIENTIFIQUES :", color = MedicalTeal, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                Text("• Recommandations SFAR / ASA / ESAIC", color = SlateGray, fontSize = 10.sp)
                Text("• Difficult Airway Society (DAS) & ERC", color = SlateGray, fontSize = 10.sp)
                Text("• Check-list Sécurité OMS", color = SlateGray, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    var emailStatusMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MedicalTeal.copy(alpha = 0.15f))
                        .border(BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.6f)), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Info, contentDescription = "À propos", tint = MedicalTeal, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("À propos — Anestheia Trone", color = SoftWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Abdellah Boutmad • IAR", color = ChampagneGold, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = SurfaceCard,
                    border = BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "hi there I'm abdellah thanks for using my app ila kan 3ndk chi chose a ajouter fhad application ola chi error \"bug\" lgitih sifto liya :boutabd6@gmail.com \nZERO COMPLICATIONS IS THE GOAL. PATIENT bikhir IS THE PRIORITY. \nCreated by Abdellah Boutmad\nIAR • Anesthésie-Réanimation",
                            color = SoftWhite,
                            fontSize = 13.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (emailStatusMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AlertRed.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, AlertRed.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = emailStatusMessage ?: "",
                            color = AlertRed,
                            fontSize = 11.5.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceCard,
                    border = BorderStroke(1.dp, BorderSubtle)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocalHospital, contentDescription = null, tint = MedicalTeal, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Plateforme d'anesthésie-réanimation • abdellahTron",
                            color = SlateGray,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    try {
                        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:boutabd6@gmail.com")
                            putExtra(Intent.EXTRA_SUBJECT, "Suggestion ou erreur — Anestheia Trone")
                        }
                        context.startActivity(emailIntent)
                    } catch (e: Exception) {
                        emailStatusMessage = "Aucune application de messagerie n'a pu être lancée sur cet appareil. Vous pouvez écrire manuellement à boutabd6@gmail.com."
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MedicalTeal, contentColor = MidnightNavy),
                modifier = Modifier.testTag("send_email_button")
            ) {
                Text("Envoyer un e-mail", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                border = BorderStroke(1.dp, BorderSubtle),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SoftWhite)
            ) {
                Text("Fermer")
            }
        },
        containerColor = DeepNavy
    )
}

@Composable
fun DrawerSectionHeader(title: String) {
    Text(
        text = title,
        color = MedicalCyan,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
    )
}

@Composable
fun DrawerItem(
    title: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(title, fontSize = 13.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
        icon = { Icon(icon, null, modifier = Modifier.size(20.dp)) },
        selected = selected,
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MedicalCyan.copy(alpha = 0.2f),
            selectedIconColor = MedicalCyan,
            selectedTextColor = MedicalCyan,
            unselectedContainerColor = MedicalNavyElevated,
            unselectedIconColor = SecondaryGrey,
            unselectedTextColor = PureWhite
        )
    )
}
