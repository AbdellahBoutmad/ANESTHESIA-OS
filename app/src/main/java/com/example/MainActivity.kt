package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.Biotech
import androidx.compose.material.icons.outlined.Checklist
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainTab
import com.example.ui.MainViewModel
import com.example.ui.SubScreen
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.BiologyScreen
import com.example.ui.screens.CalculatorsScreen
import com.example.ui.screens.ChecklistScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PatientsScreen
import com.example.ui.screens.PharmacologyScreen
import com.example.ui.screens.ProtocolsScreen
import com.example.ui.screens.TrainingScreen
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AnesthesiaOsTheme
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.DarkNightBlue
import com.example.ui.theme.MedicalCyan
import com.example.ui.theme.MedicalNavy
import com.example.ui.theme.MedicalNavyElevated
import com.example.ui.theme.MedicalNavySurface
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SecondaryGrey
import com.example.ui.theme.TurquoiseGreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AnesthesiaOsTheme(darkTheme = true) {
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                val currentTab by viewModel.currentTab.collectAsState()
                val activeSubScreen by viewModel.activeSubScreen.collectAsState()

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
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(TurquoiseGreen)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = getScreenTitle(currentTab, activeSubScreen),
                                            color = PureWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
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
                                    IconButton(
                                        onClick = {
                                            viewModel.setTab(MainTab.PROTOCOLS)
                                            viewModel.setProtocolCategory("URGENCE_CRISE")
                                        },
                                        modifier = Modifier.testTag("top_emergency_button")
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(CircleShape)
                                                .background(AlertRed.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Emergency, contentDescription = "Urgences Vitales", tint = AlertRed, modifier = Modifier.size(20.dp))
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
                                    NavigationItem("IA", Icons.Default.Psychology, MainTab.AI, "tab_ai")
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
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
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

    private fun getScreenTitle(tab: MainTab, subScreen: SubScreen): String {
        return when (subScreen) {
            SubScreen.CHECKLIST -> "Check-list Bloc & OMS"
            SubScreen.BIOLOGY -> "Examens Biologiques"
            SubScreen.CALCULATORS -> "Calculateurs Cliniques"
            SubScreen.QUIZ -> "Formation IAR & QCM"
            SubScreen.CONSULTATION_WIZARD -> "Consultation Pré-anesthésique"
            SubScreen.DRUG_DETAIL -> "Fiche Médicament"
            SubScreen.PROTOCOL_DETAIL -> "Détail du Protocole"
            SubScreen.NONE -> {
                when (tab) {
                    MainTab.HOME -> "ANESTHESIA OS"
                    MainTab.PATIENTS -> "Dossiers Patients"
                    MainTab.PHARMACO -> "Pharmacologie"
                    MainTab.PROTOCOLS -> "Protocoles & Urgences"
                    MainTab.AI -> "ANESTHESIA AI"
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
    onOpenSubScreen: (SubScreen) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 24.dp)
            .verticalScroll(scrollState)
    ) {
        // App Drawer Header
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MedicalCyan.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.LocalHospital, null, tint = MedicalCyan, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    "ANESTHESIA OS",
                    color = PureWhite,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    "Plateforme d'Anesthésie-Réanimation",
                    color = SecondaryGrey,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
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
        DrawerItem("Assistant ANESTHESIA AI", Icons.Default.Psychology, currentTab == MainTab.AI && activeSubScreen == SubScreen.NONE) {
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

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(color = BorderSubtle, thickness = 1.dp)
        Spacer(modifier = Modifier.height(12.dp))

        // Medical Validation Footer
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = MedicalNavySurface,
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text("RÉFÉRENCES SCIENTIFIQUES :", color = TurquoiseGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text("• Recommandations SFAR / ASA / ESAIC", color = SecondaryGrey, fontSize = 10.sp)
                Text("• Difficult Airway Society (DAS)", color = SecondaryGrey, fontSize = 10.sp)
                Text("• Check-list Sécurité OMS", color = SecondaryGrey, fontSize = 10.sp)
            }
        }
    }
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
