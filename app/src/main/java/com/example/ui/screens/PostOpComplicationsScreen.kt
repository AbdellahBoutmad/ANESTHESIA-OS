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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
import com.example.ui.MainViewModel
import com.example.ui.theme.AlertRed
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.InfoBlue
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SoftWhite
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningOrange

data class PostOpComplication(
    val id: String,
    val title: String,
    val category: String,
    val urgencyLevel: String, // CRITIQUE, URGENT, FREQUENT
    val definition: String,
    val clinicalSigns: List<String>,
    val immediateActions: List<String>,
    val drugsAndDoses: List<String>,
    val checklist: List<String>,
    val redFlags: String
)

val postOpComplicationsList = listOf(
    PostOpComplication(
        id = "dep_resp",
        title = "Dépression Respiratoire & Hypoxémie Postopératoire",
        category = "Respiratoire",
        urgencyLevel = "CRITIQUE",
        definition = "Diminution de la fréquence respiratoire (< 10/min) ou du volume courant, entraînant une désaturation en O2 (SpO2 < 92%) et une hypercapnie. Principales causes : surdosage morphinique, curarisation résiduelle, atélectasies de décubitus.",
        clinicalSigns = listOf(
            "Fréquence respiratoire < 10 cycles/min (bradypnée)",
            "Somnolence excessive, sédation profonde (score de Ramsay > 4)",
            "Chute de la SpO2 (< 92% sous O2)",
            "Myosis bilatéral serré (évocateur de morphiniques)"
        ),
        immediateActions = listOf(
            "Stimulation verbale et nociceptive immédiate du patient",
            "Oxygénothérapie à fort débit au masque haute concentration (10-15 L/min)",
            "Libération manuelle des voies aériennes : subluxation mandibulaire, canule de Guedel",
            "Contrôle de la curarisation par monitorage objectif TOF à l'adducteur du pouce",
            "Ventilation manuelle au ballon BAVU si apnée ou hypoventilation sévère"
        ),
        drugsAndDoses = listOf(
            "Naloxone (Narcan®) : 0.4 mg dilué dans 10 mL de sérum physiologique. Titrer 1 mL (0.04 mg) toutes les 2 minutes jusqu'à FR ≥ 12/min et réveil.",
            "Sugammadex (Bridion®) si curarisation résiduelle sous Rocuronium : 2 à 4 mg/kg IVD.",
            "Néostigmine (Prostigmine®) : 40-50 µg/kg + Atropine 20 µg/kg si curare benzylisoquinoléine."
        ),
        checklist = listOf(
            "Patient stimulé et oxygéné à fort débit",
            "Fréquence respiratoire mesurée sur une minute entière",
            "Antagoniste titré prêt à portée de main",
            "Capnographe ou oxymétrie continue raccordée"
        ),
        redFlags = "Attention : La demi-vie de la Naloxone (45-60 min) est plus courte que celle des morphiniques. Risque de rechute de dépression respiratoire !"
    ),
    PostOpComplication(
        id = "laryngospasme",
        title = "Laryngospasme Aigu & Obstruction Haute",
        category = "Voies Aériennes",
        urgencyLevel = "CRITIQUE",
        definition = "Fermeture réflexe spastique des cordes vocales lors de l'extubation ou au réveil, déclenchée par une stimulation des sécrétions ou du sang sur des voies aériennes hyperréactives.",
        clinicalSigns = listOf(
            "Stridor inspiratoire aigu ou silence respiratoire complet (spasme total)",
            "Tirage sus-sternal, intercostal et balancement thoraco-abdominal",
            "Désaturation SpO2 rapide et cyanose",
            "Absence de passage d'air à l'auscultation et au masque"
        ),
        immediateActions = listOf(
            "Appel immédiat de renfort médical anesthésique",
            "Application d'une pression positive continue (PEEP) au masque facial étanche à 100% O2",
            "Pression rétro-mandibulaire bilatérale vigoureuse (encoche de Larson)",
            "Aspiration buccale douce des sécrétions sous vision directe",
            "Si persistance : injection d'hypnotique ou curare d'urgence pour lever le spasme"
        ),
        drugsAndDoses = listOf(
            "Propofol : bolus flash de 0.5 à 1 mg/kg IVD (lève le spasme dans 80% des cas).",
            "Succinylcholine (Célocurine®) : 0.5 à 1 mg/kg IVD (ou 2-4 mg/kg IM si voie veineuse perdue).",
            "Atropine : 0.5 mg IV si bradycardie réflexe hypoxique associée."
        ),
        checklist = listOf(
            "Masque facial plaqué avec étanchéité parfaite",
            "Valve PEEP du ballon d'anesthésie serrée à 15-20 cmH2O",
            "Matériel de réintubation prêt (laryngoscope, sonde 6.5-7.0)",
            "Propofol et curare aspirés"
        ),
        redFlags = "Risque d'œdème aigu pulmonaire a vacuo (PPOED) par dépression intrathoracique majeure après levée de l'obstacle."
    ),
    PostOpComplication(
        id = "choc_hemorr",
        title = "Hypotension Sévère & Choc Hémorragique Postopératoire",
        category = "Hémodynamique",
        urgencyLevel = "CRITIQUE",
        definition = "Chute de la PAM < 65 mmHg associée à des signes d'hypoperfusion périphérique et tachycardie. Doit faire suspecter en priorité un saignement chirurgical actif interne ou extériorisé.",
        clinicalSigns = listOf(
            "Pression artérielle systolique < 90 mmHg ou chute de PAM > 30%",
            "Tachycardie sinusale compensatrice (FC > 100 bpm)",
            "Marbrures cutanées, extrémités froides, allongement du temps de recoloration cutanée (> 3s)",
            "Écoulement abondant dans les redons/drains chirurgicaux ou déglobulisation à la gazométrie"
        ),
        immediateActions = listOf(
            "Mise en position de Trendelenburg ou surélévation des membres inférieurs",
            "Remplissage vasculaire rapide par cristalloïdes isotoniques (10-20 mL/kg en débit libre)",
            "Vérification du pansement et des drains chirurgicaux",
            "Pose d'une 2e voie veineuse périphérique de gros calibre (16G ou 14G)",
            "Gazométrie artérielle / Hémocue immédiat et bilan d'hémostase en urgence",
            "Alerte chirurgicale pour reprise au bloc en urgence si hémorragie active"
        ),
        drugsAndDoses = listOf(
            "Noradrénaline : titration continue à la seringue électrique (0.1 à 0.5 µg/kg/min) pour cible PAM ≥ 65 mmHg.",
            "Éphédrine : bolus de 6 à 9 mg IVD en attendant la noradrénaline.",
            "Acide Tranexamique (Exacyl®) : 1 g IV en 10-15 min si saignement actif.",
            "Concentrés de globules rouges (CGR) et Plasma frais congelé (PFC) selon ratio 1:1."
        ),
        checklist = listOf(
            "Remplissage cristalloïde tiédi en cours",
            "Hémocue et gazométrie réalisés",
            "Dossier transfusionnel et RAI vérifiés",
            "Chirurgien prévenu pour avis hémostatique"
        ),
        redFlags = "Ne jamais attribuer une hypotension persistante en SSPI au seul effet résiduel de la rachianesthésie sans avoir éliminé un choc hémorragique !"
    ),
    PostOpComplication(
        id = "nvpo_gestion",
        title = "Nausées et Vomissements Postopératoires (NVPO)",
        category = "Confort & SSPI",
        urgencyLevel = "FREQUENT",
        definition = "Complication fréquente (20 à 30% des opérés), majorée chez la femme jeune, non fumeuse, avec antécédent de cinétose ou utilisation de morphiniques (score d'Apfel).",
        clinicalSigns = listOf(
            "Nausées intenses, haut-le-cœur répétés, vomissements bilieux ou alimentaires",
            "Inconfort majeur, anxiété, risque de désunion des sutures chirurgicales abdominales",
            "Risque d'inhalation bronchique si patient somnolent"
        ),
        immediateActions = listOf(
            "Mise en décubitus latéral de sécurité si patient encore sédaté",
            "Vérification de l'hydratation et du maintien d'une volémie correcte",
            "Escalade thérapeutique multimodale avec classe différente de la prophylaxie peropératoire"
        ),
        drugsAndDoses = listOf(
            "Ondansétron (Zophren®) : 4 mg IV lente sur 2 à 5 min (antagoniste 5-HT3).",
            "Dropéridol (Droleptan®) : 0.625 mg à 1.25 mg IV lente (anti-D2 puissant).",
            "Dexaméthasone : 4 à 8 mg IV (si non administrée en induction peropératoire).",
            "Métoclopramide (Primpéran®) : 10 mg IV lente si rétention gastrique."
        ),
        checklist = listOf(
            "Bassin réniforme et aspiration à proximité",
            "Score d'Apfel et drogues peropératoires vérifiés",
            "Antiémétique de 2e intention administré"
        ),
        redFlags = "Surveillance du tracé ECG lors de l'association d'antiémétiques en raison du risque d'allongement de l'espace QTc."
    ),
    PostOpComplication(
        id = "douleur_rebelle",
        title = "Douleur Aiguë Rebelle & Titration Morphinique",
        category = "Douleur",
        urgencyLevel = "URGENT",
        definition = "Douleur intense persistante (EVA > 6/10 ou EN > 6/10) malgré les antalgiques multimodaux peropératoires. Nécessite une titration morphinique intraveineuse stricte en SSPI.",
        clinicalSigns = listOf(
            "Score EVA ou EN ≥ 6/10",
            "Tachycardie réactionnelle, hypertension artérielle, sueurs",
            "Respiration superficielle antalgique et polypnée"
        ),
        immediateActions = listOf(
            "Évaluation précise du siège et du type de la douleur (chirurgicale vs positionnelle/globe)",
            "Vérification de la perméabilité de la voie veineuse",
            "Début du protocole de titration morphinique IV sous surveillance continue scopée"
        ),
        drugsAndDoses = listOf(
            "Morphine IV : Bolus de 2 à 3 mg toutes les 5 à 7 minutes (1.5 mg chez le sujet âgé) jusqu'à EVA ≤ 3/10 ou sédation (Ramsay ≥ 2).",
            "Kétamine (Kétalar®) : bolus sub-anesthésique de 0.15 mg/kg IV (effet anti-hyperalgésique majeur).",
            "Paracétamol 1 g IV + Néfopam (Acupan®) 20 mg en perfusion lente sur 30 min.",
            "Kétoprofène (Profénid®) 100 mg IV si non contre-indiqué."
        ),
        checklist = listOf(
            "Fréquence respiratoire et score de sédation notés avant chaque bolus",
            "Ampoule de Naloxone immédiatement disponible",
            "Objectif EVA ≤ 3/10 atteint"
        ),
        redFlags = "Arrêt immédiat de la titration morphinique si la FR chute à moins de 10/min ou si le patient devient difficilement réveillable."
    ),
    PostOpComplication(
        id = "retard_reveil",
        title = "Retard de Réveil & Troubles de la Conscience",
        category = "Neurologique",
        urgencyLevel = "CRITIQUE",
        definition = "Absence de reprise de conscience adaptée plus de 60 à 90 minutes après l'arrêt des agents anesthésiques. Diagnostic différentiel exhaustif nécessaire.",
        clinicalSigns = listOf(
            "Coma non réactif, aréactivité aux stimuli verbaux et douloureux",
            "Hypotonie généralisée, abolition ou asymétrie des réflexes",
            "Mydriase ou myosis, mouvements anormaux"
        ),
        immediateActions = listOf(
            "Mesure immédiate de la glycémie capillaire (éliminer une hypoglycémie)",
            "Mesure de la température corporelle centrale (éliminer une hypothermie sévère < 35°C)",
            "Gazométrie artérielle : recherche d'une hypercapnie majeure (narcose au CO2)",
            "Examen neurologique ciblé : recherche d'une anisocorie ou d'un déficit moteur focal (AVC peropératoire)",
            "Évaluation de la curarisation résiduelle par stimulateur de nerf (TOF)"
        ),
        drugsAndDoses = listOf(
            "Sérum Glucosé 30% : 20 à 30 mL IVD si glycémie < 3.5 mmol/L.",
            "Flumazénil (Anexate®) : 0.2 mg IVD titré si benzodiazépines administrées.",
            "Naloxone : 0.08 à 0.1 mg IVD si suspicion de surdosage morphinique.",
            "Sugammadex 4 mg/kg si bloc neuromusculaire résiduel."
        ),
        checklist = listOf(
            "Glycémie capillaire mesurée et normale",
            "Température > 36°C vérifiée",
            "Pupilles symétriques et réactives",
            "Scanner cérébral programmé si coma inexpliqué persistant"
        ),
        redFlags = "Ne jamais administrer d'antagonistes sans avoir préalablement écarté une hypothermie sévère, une hypoglycémie ou un AVC peropératoire."
    ),
    PostOpComplication(
        id = "globe_vesical",
        title = "Rétention Aiguë d'Urine & Globe Vésical",
        category = "Urologique",
        urgencyLevel = "FREQUENT",
        definition = "Incapacité à vider la vessie après l'intervention, favorisée par la rachianesthésie, les morphiniques, les anticholinergiques et les chirurgies pelviennes ou proctologiques.",
        clinicalSigns = listOf(
            "Douleur et pesanteur hypogastrique angoissante",
            "Masse arrondie sus-pubienne mate à la percussion",
            "Agitation au réveil, poussée hypertensive inexpliquée et tachycardie"
        ),
        immediateActions = listOf(
            "Échographie vésicale au lit du patient (Bladder Scan) : confirmation du volume (> 400-500 mL)",
            "Essai de miction spontanée au calme (position assise ou debout si autorisée)",
            "Sondage vésical évacuateur atraumatique si échec ou distension > 500 mL"
        ),
        drugsAndDoses = listOf(
            "Arrêt des apports hydriques excessifs non indispensables.",
            "Gel anesthésique urétral à la lidocaïne lors du sondage pour soulagement immédiat."
        ),
        checklist = listOf(
            "Volume au Bladder Scan mesuré et consigné",
            "Sondage évacuateur réalisé en conditions d'asepsie stricte",
            "Surveillance du syndrome de levée d'obstacle si évacuation > 1000 mL"
        ),
        redFlags = "Un globe vésical peut être la cause unique et méconnue d'une agitation extrême ou d'une hypertension sévère au réveil."
    ),
    PostOpComplication(
        id = "frisson_postop",
        title = "Frisson Postopératoire & Hypothermie",
        category = "Thermique",
        urgencyLevel = "FREQUENT",
        definition = "Secousses musculaires involontaires observées chez 40 à 60% des patients au réveil. Augmente la consommation d'oxygène myocardique de 200 à 400% et la production de CO2.",
        clinicalSigns = listOf(
            "Tremblements musculaires intenses généralisés ou localisés aux masséters",
            "Température centrale < 36°C",
            "Tachycardie, hypertension, augmentation de la consommation myocardique en O2"
        ),
        immediateActions = listOf(
            "Réchauffement cutané externe actif par couverture à air pulsé (Bair Hugger)",
            "Oxygénothérapie systématique au masque pour couvrir la surconsommation en O2",
            "Perfusion de solutés cristalloïdes tiédis"
        ),
        drugsAndDoses = listOf(
            "Clonidine (Catapressan®) : 0.5 à 1.5 µg/kg IV lente (agoniste alpha-2 très efficace).",
            "Tramadol (Topalgic®) : 0.5 à 1 mg/kg IV lente.",
            "Néfopam (Acupan®) : 20 mg en perfusion sur 30 minutes."
        ),
        checklist = listOf(
            "Température centrale mesurée",
            "Couverture à air pulsé installée à 38-43°C",
            "O2 systématique au masque en place"
        ),
        redFlags = "Le frisson chez le coronarien peut déclencher un syndrome coronarien aigu par déséquilibre majeur entre apports et besoins en O2."
    )
)

@Composable
fun PostOpComplicationsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("TOUS") }
    val expandedItems = remember { mutableStateMapOf<String, Boolean>().apply { put("dep_resp", true) } }
    val checkedMap = remember { mutableStateMapOf<String, Boolean>() }

    val categories = listOf("TOUS", "Respiratoire", "Voies Aériennes", "Hémodynamique", "Douleur", "Confort & SSPI", "Neurologique", "Thermique", "Urologique")

    val filteredComplications = postOpComplicationsList.filter { comp ->
        (selectedCategory == "TOUS" || comp.category.contains(selectedCategory, ignoreCase = true)) &&
        (searchQuery.isBlank() || comp.title.contains(searchQuery, ignoreCase = true) || comp.definition.contains(searchQuery, ignoreCase = true) || comp.drugsAndDoses.any { it.contains(searchQuery, ignoreCase = true) })
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("post_op_complications_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Header
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = MaterialTheme.colorScheme.primary)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Text(
                                text = "COMPLICATIONS POSTOPÉRATOIRES",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.5.sp,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Surveillance SSPI & Conduites à tenir d'urgence",
                                color = ChampagneGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AlertRed.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, AlertRed.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "SSPI / RÉA",
                                color = AlertRed,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Search input
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Rechercher une complication (laryngospasme, naloxone, NVPO...)", color = SlateGray, fontSize = 11.5.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Category Chips
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(categories) { cat ->
                            val isSel = (cat == "TOUS" && selectedCategory == "TOUS") || (cat != "TOUS" && selectedCategory == cat)
                            Surface(
                                modifier = Modifier.clickable { selectedCategory = cat },
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSel) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                border = BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
                            ) {
                                Text(
                                    text = cat,
                                    color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.5.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Summary disclaimer
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = InfoBlue, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Toute anomalie en SSPI impose l'alerte immédiate du médecin anesthésiste-réanimateur responsable.",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Complications list
        items(filteredComplications) { comp ->
            val isExpanded = expandedItems[comp.id] ?: false

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 5.dp)
                    .testTag("comp_card_${comp.id}"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(
                    1.dp,
                    if (comp.urgencyLevel == "CRITIQUE") AlertRed.copy(alpha = 0.5f)
                    else if (isExpanded) MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.outline
                )
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header Clickable
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedItems[comp.id] = !isExpanded }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    when (comp.urgencyLevel) {
                                        "CRITIQUE" -> AlertRed.copy(alpha = 0.2f)
                                        "URGENT" -> WarningOrange.copy(alpha = 0.2f)
                                        else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (comp.urgencyLevel) {
                                    "CRITIQUE" -> Icons.Default.Emergency
                                    "URGENT" -> Icons.Default.Warning
                                    else -> Icons.Default.Medication
                                },
                                contentDescription = null,
                                tint = when (comp.urgencyLevel) {
                                    "CRITIQUE" -> AlertRed
                                    "URGENT" -> WarningOrange
                                    else -> MaterialTheme.colorScheme.primary
                                },
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = comp.title,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = when (comp.urgencyLevel) {
                                        "CRITIQUE" -> AlertRed.copy(alpha = 0.2f)
                                        "URGENT" -> WarningOrange.copy(alpha = 0.2f)
                                        else -> SuccessGreen.copy(alpha = 0.2f)
                                    }
                                ) {
                                    Text(
                                        text = comp.urgencyLevel,
                                        color = when (comp.urgencyLevel) {
                                            "CRITIQUE" -> AlertRed
                                            "URGENT" -> WarningOrange
                                            else -> SuccessGreen
                                        },
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = comp.category,
                                color = ChampagneGold,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = SlateGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Expanded Section
                    AnimatedVisibility(visible = isExpanded) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = comp.definition,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Signes Cliniques
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("SIGNES CLINIQUES D'ALERTE :", color = WarningOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    comp.clinicalSigns.forEach { sign ->
                                        Text("• $sign", color = MaterialTheme.colorScheme.onSurface, fontSize = 10.5.sp, lineHeight = 14.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Conduite à tenir immédiate
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("CONDUITE À TENIR IMMÉDIATE :", color = MaterialTheme.colorScheme.primary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    comp.immediateActions.forEachIndexed { idx, act ->
                                        Text("${idx + 1}. $act", color = MaterialTheme.colorScheme.onSurface, fontSize = 10.5.sp, lineHeight = 14.sp, modifier = Modifier.padding(vertical = 1.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Drogues & Posologies
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(0.8.dp, ChampagneGold.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("DROGUES & POSOLOGIES DE SAUVETAGE :", color = ChampagneGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    comp.drugsAndDoses.forEach { drug ->
                                        Text("💊 $drug", color = SoftWhite, fontSize = 10.5.sp, lineHeight = 14.sp, modifier = Modifier.padding(vertical = 1.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Red Flags
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = AlertRed.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, AlertRed.copy(alpha = 0.35f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "⚠️ " + comp.redFlags,
                                    color = AlertRed,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    lineHeight = 14.sp,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Interactive Checklist
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(0.8.dp, SuccessGreen.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("LISTE DE VÉRIFICATION SSPI :", color = SuccessGreen, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    comp.checklist.forEach { item ->
                                        val key = "${comp.id}_$item"
                                        val isChecked = checkedMap[key] ?: false
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { checkedMap[key] = !isChecked }
                                                .padding(vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = isChecked,
                                                onCheckedChange = { checkedMap[key] = it },
                                                colors = CheckboxDefaults.colors(
                                                    checkedColor = SuccessGreen,
                                                    uncheckedColor = SlateGray
                                                ),
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = item,
                                                color = if (isChecked) MaterialTheme.colorScheme.onSurface else SlateGray,
                                                fontSize = 10.5.sp
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    }
}
