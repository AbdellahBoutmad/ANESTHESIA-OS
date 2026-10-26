package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.InfoBlue
import com.example.ui.theme.MedicalTeal
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SoftWhite
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.WarningOrange

data class AnesthesiaStep(
    val id: Int,
    val stepNumber: Int,
    val title: String,
    val subtitle: String,
    val timing: String,
    val keyNotions: List<String>,
    val theoreticalSummary: String,
    val safetyPoints: List<String>,
    val checklist: List<String>,
    val clinicalObjectives: List<String>
)

data class GlossaryTerm(
    val term: String,
    val category: String,
    val definition: String,
    val clinicalUtility: String
)

val procedureSteps = listOf(
    AnesthesiaStep(
        id = 1,
        stepNumber = 1,
        title = "Étape 1 : Consultation préanesthésique",
        subtitle = "Anamnèse, stratification du risque, voies aériennes et consentement",
        timing = "Plusieurs jours à 48 heures avant l'intervention programmée",
        keyNotions = listOf(
            "Anamnèse exhaustive et antécédents médicaux, chirurgicaux, anesthésiques et familiaux (HM).",
            "Recensement précis des allergies médicamenteuses et alimentaires (latex, curares, antibiotiques).",
            "Bilan des traitements en cours : règles d'arrêt ou de poursuite des anticoagulants, antiagrégants et antihypertenseurs.",
            "Évaluation clinique ciblée et dépistage des voies aériennes difficiles (Mallampati, DTM, ouverture de bouche, mobilité cervicale).",
            "Évaluation du risque anesthésique global (Score ASA I à V, risque d'inhalation gastrique, score d'Apfel NVPO).",
            "Prescription raisonnée des examens complémentaires selon l'indication chirurgicale et les comorbidités.",
            "Information loyale du patient sur les techniques anesthésiques et recueil du consentement éclairé.",
            "Vérification des consignes de jeûne préopératoire (6h solides/lait, 2h liquides clairs stricts)."
        ),
        theoreticalSummary = "La consultation préanesthésique (CPA) est un acte médicolégal obligatoire. Elle permet de définir la stratégie anesthésique la plus adaptée, d'anticiper les difficultés d'intubation, d'optimiser l'état hémodynamique et d'informer le patient des bénéfices et des risques encourus.",
        safetyPoints = listOf(
            "Le jeûne préopératoire doit respecter la règle 6h pour les solides et 2h pour les liquides clairs stricts (eau, café sans lait).",
            "Toute suspicion d'intubation difficile impose la consignation écrite et la préparation de matériel spécialisé au bloc.",
            "Attention aux antécédents familiaux d'hyperthermie maligne ou de déficit en pseudocholinestérases."
        ),
        checklist = listOf(
            "Interrogatoire complet et antécédents validés",
            "Score ASA et score d'Apfel calculés et consignés",
            "Examen des voies aériennes réalisé (Mallampati, DTM, ouverture bouche)",
            "État dentaire vérifié et consigné (prothèses, couronnes)",
            "Consignes de jeûne remises par écrit au patient",
            "Consentement éclairé et information signés"
        ),
        clinicalObjectives = listOf(
            "Stratification précise du risque chirurgical et anesthésique",
            "Prévention du risque d'inhalation pulmonaire peropératoire",
            "Information personnalisée et réduction de l'anxiété du patient"
        )
    ),
    AnesthesiaStep(
        id = 2,
        stepNumber = 2,
        title = "Étape 2 : Préparation préopératoire",
        subtitle = "Sécurité documentaire, contrôle du respirateur, matériel et pharmacologie",
        timing = "Au bloc opératoire, avant l'arrivée du patient en salle",
        keyNotions = listOf(
            "Vérification de l'identité du patient, de l'intervention prévue et du dossier anesthésique complet.",
            "Contrôle de conformité des documents de sécurité et bilans récents (groupe sanguin valide, RAI).",
            "Préparation et agencement de la salle d'opération selon les règles d'hygiène et d'ergonomie.",
            "Contrôle rigoureux de l'appareil d'anesthésie selon les instructions du fabricant et le protocole local.",
            "Disponibilité immédiate du système d'aspiration fonctionnel (dépression < -500 mmHg) et du matériel de secours.",
            "Vérification minutieuse des médicaments, concentrations, étiquetage normalisé SFAR et dates de péremption.",
            "Préparation du monitorage cardiovasculaire et respiratoire adapté à l'intervention."
        ),
        theoreticalSummary = "La préparation de salle garantit que l'environnement technique est opérationnel avant d'endormir le patient. Elle repose sur le test complet du respirateur (étanchéité, valves, chaux sodée, alimentation gaz O2/Air), l'aspiration et la préparation individualisée du plateau de drogues d'induction et de secours hémodynamique.",
        safetyPoints = listOf(
            "Ne jamais débuter une induction sans aspiration vérifiée et opérationnelle à portée de main.",
            "Chaque seringue doit comporter l'étiquette normalisée SFAR avec nom du produit, concentration, date et heure.",
            "Vérifier la présence d'une source d'oxygène de secours (obus d'O2) et d'un insufflateur manuel (BAVU)."
        ),
        checklist = listOf(
            "Dossier médical, CPA et bilans biologiques vérifiés",
            "Autotest respirateur validé (étanchéité, circuit, chaux sodée)",
            "Aspiration buccale testée et étanche (< -500 mmHg)",
            "Matériel de ventilation prêt (masques adaptés, filtres, canules de Guedel)",
            "Laryngoscope testé avec lumière vive et lames fonctionnelles",
            "Sondes d'intubation adaptées au patient et ballonnet vérifié",
            "Seringues d'induction préparées, étiquetées et datées",
            "Vasopresseurs de secours préparés (Éphédrine, Noradrénaline)"
        ),
        clinicalObjectives = listOf(
            "Zéro défaut matériel au moment de l'induction",
            "Prévention absolue des erreurs d'étiquetage médicamenteux",
            "Disponibilité immédiate de l'alternative d'intubation difficile"
        )
    ),
    AnesthesiaStep(
        id = 3,
        stepNumber = 3,
        title = "Étape 3 : Accueil et installation au bloc opératoire",
        subtitle = "Identitovigilance, installation chirurgicale, monitorage et confort",
        timing = "À l'entrée du patient au bloc opératoire",
        keyNotions = listOf(
            "Vérification de l'identité du patient par question ouverte et contrôle du bracelet d'identification.",
            "Contrôle des éléments de sécurité préopératoire (absence de prothèse dentaire, jeûne confirmé, côté opéré marqué).",
            "Installation adaptée sur table d'opération et prévention active des lésions de positionnement.",
            "Protection scrupuleuse des points d'appui (talons, coudes, plexus brachial, nerf sciatique poplité externe, cornées).",
            "Mise en place du monitorage indiqué (ECG 3 ou 5 brins, PNI, oxymétrie de pouls SpO2, capnographie).",
            "Vérification de la perméabilité et de la fixation de la voie veineuse périphérique."
        ),
        theoreticalSummary = "L'accueil au bloc opératoire concrétise le temps d'identitovigilance et la première étape de la Check-list Sécurité OMS ('Sign In'). L'installation doit concilier les impératifs d'exposition chirurgicale avec la protection nerveuse, cutanée et oculaire du patient anesthésié.",
        safetyPoints = listOf(
            "Prévenir toute élongation du plexus brachial : abduction des bras limitée à 90 degrés maximum.",
            "Protéger systématiquement les yeux avec occlusion palpébrale adhésive pour prévenir les ulcères cornéens.",
            "Vérifier le reflux veineux et la bonne diffusion du soluté de perfusion avant toute injection."
        ),
        checklist = listOf(
            "Identitovigilance active : Nom, prénom, date de naissance, intervention",
            "Vérification du côté opéré et du marquage cutané chirurgical",
            "Check-list OMS Sign In réalisée et validée",
            "Points d'appui capitonnés et protection oculaire en place",
            "Monitorage scopique initial raccordé (ECG, PNI, SpO2)",
            "Constantes hémodynamiques de base relevées et stables",
            "Voie veineuse périphérique de bon calibre vérifiée et perméable"
        ),
        clinicalObjectives = listOf(
            "Prévention des erreurs d'identité ou de côté opératoire",
            "Prévention des compressions nerveuses et des escarres de décubitus",
            "Rassurer le patient et optimiser son confort thermique et psychologique"
        )
    ),
    AnesthesiaStep(
        id = 4,
        stepNumber = 4,
        title = "Étape 4 : Induction anesthésique",
        subtitle = "Pré-oxygénation, administration séquencée, contrôle des VAS et confirmation",
        timing = "Phase critique d'endormissement et de contrôle des voies aériennes",
        keyNotions = listOf(
            "Pré-oxygénation rigoureuse au masque étanche (FeO2 > 90% ou 3 minutes en ventilation courante / 8 capacités vitales).",
            "Administration séquencée et titrée : analgésique morphinique puissant, hypnotique d'action rapide, puis curare si indiqué.",
            "Ventilation manuelle douce au masque sans surpression gastrique (pression de crête < 20 cmH2O).",
            "Surveillance rapprochée de la tolérance hémodynamique (chute tensionnelle, bradycardie) et de la profondeur d'anesthésie.",
            "Laryngoscopie atraumatique et intubation orotrachéale ou mise en place d'un dispositif supraglottique (LMA).",
            "Confirmation objective immédiate de l'intubation trachéale par capnographie (EtCO2 sur > 4 cycles expiratoires normaux).",
            "Auscultation pulmonaire bilatérale et symétrique (champs supérieurs et axillaires) et fixation sécurisée de la sonde."
        ),
        theoreticalSummary = "L'induction anesthésique fait passer le patient de la veille à l'inconscience complète et à l'apnée. Cette étape nécessite une anticipation absolue. Les médicaments, les doses et la stratégie sont individualisés par le médecin anesthésiste et l'équipe en fonction du terrain clinique (induction standard vs induction en séquence rapide - ISR).",
        safetyPoints = listOf(
            "Avis pédagogique : Les protocoles médicamenteux ne sont pas universels ; ils sont prescrits et adaptés par le médecin anesthésiste.",
            "La capnographie (courbe d'EtCO2) est le seul critère formel confirmant le positionnement intratrachéal de la sonde.",
            "En cas d'estomac plein ou d'urgence : manoeuvre de Sellick (si indiquée) et induction en séquence rapide sans ventilation au masque."
        ),
        checklist = listOf(
            "Pré-oxygénation validée avec FeO2 > 90%",
            "Hypnotique et morphinique administrés selon posologie clinique",
            "Curarisation contrôlée si intubation requise",
            "Intubation réalisée sans traumatisme dentaire",
            "Capnographie EtCO2 confirmée sur plus de 4 cycles expiratoires",
            "Auscultation pulmonaire bilatérale symétrique validée",
            "Pression du ballonnet réglée entre 20 et 30 cmH2O",
            "Sonde fixée solidement et raccordée au respirateur"
        ),
        clinicalObjectives = listOf(
            "Assurer une oxygénation ininterrompue sans hypoxémie",
            "Prévenir l'inhalation pulmonaire de liquide gastrique (syndrome de Mendelson)",
            "Maintenir une stabilité hémodynamique satisfaisante lors de l'endormissement"
        )
    ),
    AnesthesiaStep(
        id = 5,
        stepNumber = 5,
        title = "Étape 5 : Entretien de l'anesthésie",
        subtitle = "Maintien de la narcose, ventilation protectrice, hémodynamique et homéostasie",
        timing = "Pendant toute la durée de l'intervention chirurgicale",
        keyNotions = listOf(
            "Maintien adapté de l'anesthésie par halogénés (Sévoflurane, Desflurane) ou par voie intraveineuse (AIVOC Propofol).",
            "Surveillance continue de la profondeur anesthésique pour éviter tout réveil peropératoire non désiré (BIS 40-60).",
            "Ventilation mécanique protectrice (Vt 6 à 8 ml/kg de poids idéal théorique, PEP 5-8 cmH2O, Pplat < 30, Driving pressure < 14).",
            "Surveillance hémodynamique continue : pression artérielle moyenne (cible PAM ≥ 65 mmHg), fréquence cardiaque, perfusion tissulaire.",
            "Évaluation précise des pertes sanguines, de la diurèse et compensation volémique par cristalloïdes tiédis.",
            "Maintien rigoureux de la normothermie (T° > 36°C) par réchauffement externe à air pulsé et fluides tiédis.",
            "Détection précoce et prise en charge collégiale des complications peropératoires (bronchospasme, hémorragie, hypotension)."
        ),
        theoreticalSummary = "L'entretien anesthésique vise à garantir la stabilité des grandes fonctions vitales tout au long du geste chirurgical. Il associe le contrôle de la composante hypnotique, analgésique et neuromusculaire, tout en protégeant les organes cibles (cerveau, coeur, poumons, reins) contre l'hypoxie, l'hypotension et l'hypothermie.",
        safetyPoints = listOf(
            "Maintenir une PAM ≥ 65 mmHg pour préserver la perfusion cérébrale, myocardique et rénale.",
            "Surveiller la Driving Pressure (Pplat - PEP) : une valeur > 14 cmH2O signale un risque accru de volotraumatisme pulmonaire.",
            "Prévenir l'hypothermie peropératoire, facteur majeur de troubles de l'hémostase et de retard de réveil."
        ),
        checklist = listOf(
            "Time Out chirurgical validé avant incision (Check-list OMS)",
            "Profondeur anesthésique stable (halogéné ou TIVA titré)",
            "Paramètres ventilatoires protecteurs vérifiés (Vt, PEP, Pplat)",
            "Pression artérielle moyenne PAM maintenue ≥ 65 mmHg",
            "Curarisation entretenue et monitorée par TOF si requise",
            "Température corporelle surveillée et réchauffement actif en cours",
            "Anticipation de l'analgésie postopératoire multimodale (avant réveil)"
        ),
        clinicalObjectives = listOf(
            "Assurer une analgésie et une inconscience peropératoires parfaites",
            "Protéger le parenchyme pulmonaire par une ventilation adaptée",
            "Préserver l'équilibre hydro-électrolytique et la température corporelle"
        )
    ),
    AnesthesiaStep(
        id = 6,
        stepNumber = 6,
        title = "Étape 6 : Réveil et extubation",
        subtitle = "Décurarisation objective, autonomie respiratoire, réflexes de protection et critères de sécurité",
        timing = "Fin d'intervention chirurgicale et reprise de conscience",
        keyNotions = listOf(
            "Arrêt anticipé des hypnotiques et halogénés pour permettre l'élimination des agents anesthésiques.",
            "Évaluation de la décurarisation par monitorage neuromusculaire objectif TOF (ratio T4/T1 > 90% requis).",
            "Décurarisation pharmacologique si nécessaire (Néostigmine + Atropine ou Sugammadex selon le curare utilisé).",
            "Reprise prouvée d'une ventilation spontanée efficace, régulière, avec fréquence respiratoire et EtCO2 satisfaisants.",
            "Récupération des réflexes protecteurs des voies aériennes (toux efficace, déglutition, ouverture des yeux aux ordres simples).",
            "Aspiration pharyngée sous vision directe avant extubation et dégonflage complet du ballonnet.",
            "Extubation en pression positive après pré-oxygénation à 100% pour éviter le collapsus alvéolaire post-extubation.",
            "Surveillance post-extubation immédiate : recherche d'un stridor, d'un spasme laryngé ou d'un tirage respiratoire."
        ),
        theoreticalSummary = "Le réveil anesthésique est une phase à haut risque d'accidents respiratoires (laryngospasme, obstruction haute, hypoxémie). L'extubation ne doit être décidée que sur des critères cliniques et paracliniques objectifs, garantissant que le patient peut assurer seul sa liberté des voies aériennes et son oxygénation.",
        safetyPoints = listOf(
            "Ne jamais extuber un patient partiellement curarisé : le ratio TOF T4/T1 doit impérativement dépasser 90%.",
            "Une température inférieure à 35.5°C retarde l'élimination des drogues et majore la curarisation résiduelle.",
            "Avoir un masque facial adapté et du matériel de réintubation immédiatement accessible à portée de main."
        ),
        checklist = listOf(
            "Décurarisation prouvée par TOF > 90% (ou antagonisation réalisée)",
            "Patient conscient, réactif et obéissant aux ordres simples",
            "Ventilation spontanée ample et régulière avec Vt suffisant",
            "Cavité buccale aspirée soigneusement",
            "Oxygénation à 100% en pression positive lors de l'extubation",
            "Ballonnet entièrement dégonflé et sonde retirée avec douceur",
            "Respiration spontanée post-extubation calme, sans stridor ni tirage"
        ),
        clinicalObjectives = listOf(
            "Prévention formelle de la curarisation résiduelle postopératoire",
            "Sécurisation de la liberté respiratoire et prévention du laryngospasme",
            "Transfert sécurisé vers la salle de surveillance post-interventionnelle"
        )
    ),
    AnesthesiaStep(
        id = 7,
        stepNumber = 7,
        title = "Étape 7 : Transfert en SSPI",
        subtitle = "Transmission formalisée, surveillance continue, contrôle de la douleur et score d'Aldrete",
        timing = "Prise en charge postopératoire immédiate en salle de réveil",
        keyNotions = listOf(
            "Transmission orale structurée à l'équipe soignante de SSPI (terrain du patient, déroulement chirurgical, drogues administrées, incidents éventuels).",
            "Surveillance respiratoire continue : fréquence respiratoire, saturation SpO2, liberté des voies aériennes sous oxygénothérapie.",
            "Surveillance hémodynamique régulière : pression artérielle, fréquence et rythme cardiaque.",
            "Évaluation systématique et protocolisée de l'intensité de la douleur (échelle visuelle analogique EVA ou échelle numérique EN).",
            "Dépistage et traitement précoce des nausées et vomissements postopératoires (NVPO).",
            "Surveillance de la température, réchauffement actif et prévention du frisson post-anesthésique.",
            "Surveillance du pansement chirurgical, des drains et des pertes sanguines extériorisées.",
            "Application rigoureuse des critères de sortie de SSPI selon le protocole de l'établissement (Score d'Aldrete modifié ≥ 9/10)."
        ),
        theoreticalSummary = "La salle de surveillance post-interventionnelle (SSPI) assure la transition sécurisée entre le bloc opératoire et le service d'hospitalisation. La décision de sortie repose sur des scores cliniques standardisés attestant de la stabilité respiratoire, circulatoire, neurologique et de l'analgésie du patient.",
        safetyPoints = listOf(
            "Transmission obligatoire selon un schéma formalisé (type SBAR : Situation, Background, Assessment, Recommendation).",
            "Toute plainte douloureuse doit faire l'objet d'une titration antalgique immédiate selon le protocole médical.",
            "La sortie de SSPI ne peut être validée que par le médecin anesthésiste-réanimateur responsable."
        ),
        checklist = listOf(
            "Transmission effectuée et signée avec l'infirmier(ère) de SSPI",
            "Monitorage scopique, PNI et SpO2 réinstallé immédiatement",
            "Oxygénothérapie adaptée mise en place",
            "Évaluation de la douleur réalisée (score EVA/EN consigné)",
            "Panssement chirurgical propre, non hémorragique",
            "Température corporelle mesurée et stabilisée (> 36°C)",
            "Score d'Aldrete modifié calculé et supérieur ou égal à 9/10 avant sortie",
            "Prescription médicale postopératoire signée et expliquée"
        ),
        clinicalObjectives = listOf(
            "Prévention et dépistage des complications respiratoires et hémorragiques précoces",
            "Soulagement optimal de la douleur postopératoire (EVA < 3/10)",
            "Autorisation de sortie médicale basée sur des critères objectifs et sécuritaires"
        )
    )
)

val medicalGlossary = listOf(
    GlossaryTerm(
        term = "CPA",
        category = "Consultation",
        definition = "Consultation Pré-Anesthésique. Obligation légale réalisée par un médecin anesthésiste plusieurs jours avant l'acte programmé pour évaluer le patient et choisir la technique anesthésique.",
        clinicalUtility = "Stratification du risque, prescription ciblée des bilans et recueil du consentement."
    ),
    GlossaryTerm(
        term = "Score ASA",
        category = "Classification",
        definition = "Classification de l'American Society of Anesthesiologists évaluant l'état de santé physique préopératoire du patient (de la classe I : patient sain, à la classe V : moribond, avec le suffixe U en urgence).",
        clinicalUtility = "Indicateur universel de comorbidité et d'évaluation du risque périopératoire."
    ),
    GlossaryTerm(
        term = "Score de Mallampati",
        category = "Voies Aériennes",
        definition = "Évaluation visuelle de la cavité oropharyngée bouche ouverte sans phonation (Classes I à IV selon la visibilité du voile du palais, de la luette et des piliers).",
        clinicalUtility = "Dépistage de l'intubation difficile (les classes III et IV sont prédictives de difficulté)."
    ),
    GlossaryTerm(
        term = "DTM (Distance Thyro-Mentonnière)",
        category = "Voies Aériennes",
        definition = "Distance entre la proéminence du cartilage thyroïde et la pointe du menton, tête en hyperextension. Normale > 65 mm.",
        clinicalUtility = "Une DTM < 60-65 mm est un facteur prédictif d'exposition glottique difficile lors de la laryngoscopie."
    ),
    GlossaryTerm(
        term = "Score de Cormack-Lehane",
        category = "Laryngoscopie",
        definition = "Classification de la vision glottique obtenue lors de la laryngoscopie directe (Grade I : orifice glottique visible en entier, à Grade IV : seul le palais mou est visible).",
        clinicalUtility = "Formalisation du degré de difficulté d'exposition de la glotte per-laryngoscopie."
    ),
    GlossaryTerm(
        term = "CAM / MAC (Concentration Alvéolaire Minimale)",
        category = "Pharmacologie",
        definition = "Concentration d'un anesthésique volatil dans les alvéoles empêchant 50% des patients de bouger lors d'une incision cutanée standard.",
        clinicalUtility = "Repère posologique fondamental pour titrer les halogénés (Sévoflurane, Desflurane) selon l'âge."
    ),
    GlossaryTerm(
        term = "BIS (Bispectral Index)",
        category = "Monitorage",
        definition = "Traitement mathématique de l'électroencéphalogramme (EEG) fournissant une valeur numérique adimensionnelle de 0 (silence cérébral) à 100 (éveil complet).",
        clinicalUtility = "Cible entre 40 et 60 sous anesthésie générale pour prévenir la mémorisation peropératoire."
    ),
    GlossaryTerm(
        term = "Curarisation & TOF (Train of Four)",
        category = "Monitorage Neuromusculaire",
        definition = "Stimulation de 4 impulsions électriques sur le nerf ulnaire évaluant la réponse motrice du pouce. Le ratio T4/T1 quantifie la profondeur du bloc neuromusculaire.",
        clinicalUtility = "Un ratio TOF T4/T1 > 90% est indispensable avant toute extubation pour éviter la curarisation résiduelle."
    ),
    GlossaryTerm(
        term = "Pression de Crête (Ppeak) & Pression de Plateau (Pplat)",
        category = "Ventilation",
        definition = "Ppeak reflète la pression maximale lors de l'insufflation (résistance voies aériennes + compliance pulmonaire). Pplat reflète la pression statique alvéolaire télé-inspiratoire.",
        clinicalUtility = "Pplat doit être maintenue < 30 cmH2O pour éviter le barotraumatisme."
    ),
    GlossaryTerm(
        term = "Driving Pressure (Pression Motrice)",
        category = "Ventilation",
        definition = "Différence entre la pression de plateau et la PEP expiratoire (ΔP = Pplat - PEP).",
        clinicalUtility = "Cible < 14 cmH2O. Associée à la réduction de la mortalité pulmonaire en ventilation protectrice."
    ),
    GlossaryTerm(
        term = "Score d'Aldrete Modifié",
        category = "SSPI / Réveil",
        definition = "Score standardisé coté sur 10 évaluant 5 paramètres : motricité, respiration, hémodynamique, conscience et coloration/SpO2.",
        clinicalUtility = "Un score ≥ 9/10 est requis pour autoriser la sortie du patient de la salle de réveil."
    ),
    GlossaryTerm(
        term = "Score d'Apfel",
        category = "Prévention NVPO",
        definition = "Score prédictif des nausées et vomissements postopératoires fondé sur 4 critères : sexe féminin, non-fumeur, antécédent de NVPO/cinétose et utilisation de morphiniques postopératoires.",
        clinicalUtility = "Détermine la stratégie de prophylaxie antiémétique multimodale (Dexaméthasone, Ondansétron)."
    ),
    GlossaryTerm(
        term = "ISR (Induction en Séquence Rapide)",
        category = "Urgence / Voies Aériennes",
        definition = "Protocole d'induction anesthésique d'urgence indiqué chez le patient avec estomac plein ou à risque de reflux gastrique pour prévenir le syndrome de Mendelson.",
        clinicalUtility = "Pré-oxygénation maximale, hypnotique d'action rapide + curare à délai bref (Célocurine ou Rocuronium forte dose) sans ventilation au masque intermédiaire."
    ),
    GlossaryTerm(
        term = "CICO (Cannot Intubate Cannot Oxygenate)",
        category = "Urgence Vitale",
        definition = "Situation critique d'extrême urgence où il est impossible à la fois d'intuber la trachée et d'oxygéner le patient au masque ou au dispositif supraglottique.",
        clinicalUtility = "Déclenchement immédiat de l'algorithme de sauvetage avec abord sous-glottique chirurgical (cricothyroïdotomie)."
    )
)

@Composable
fun AnesthesiaTimelineScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Chronologie 7 étapes, 1 = Glossaire médical
    val checkedItems = remember { mutableStateMapOf<String, Boolean>() }
    val expandedSteps = remember { mutableStateMapOf<Int, Boolean>().apply { put(1, true) } }
    var glossarySearch by remember { mutableStateOf("") }
    var selectedGlossaryCategory by remember { mutableStateOf("TOUS") }

    // Educational progress tracker
    val totalChecklistItems = procedureSteps.sumOf { it.checklist.size }
    val completedItems = checkedItems.values.count { it }
    val progressPercentage = if (totalChecklistItems > 0) completedItems.toFloat() / totalChecklistItems.toFloat() else 0f

    val completedStepsCount = procedureSteps.count { step ->
        step.checklist.isNotEmpty() && step.checklist.all { item -> checkedItems["${step.id}_$item"] == true }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightNavy)
            .testTag("anesthesia_procedure_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Top Header
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = DeepNavy,
                border = BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = MedicalTeal)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                            Text(
                                text = "DÉROULEMENT DE L'ANESTHÉSIE",
                                color = SoftWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Guide chronologique périopératoire • Formation clinique",
                                color = ChampagneGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MedicalTeal.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, MedicalTeal.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "7 ÉTAPES",
                                color = MedicalTeal,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Educational Progress Bar
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SurfaceCard,
                        border = BorderStroke(1.dp, BorderSubtle)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Progression pédagogique : $completedStepsCount / 7 étapes complétées",
                                    color = SoftWhite,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${(progressPercentage * 100).toInt()}% vérifié",
                                    color = ChampagneGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { progressPercentage },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = MedicalTeal,
                                trackColor = DeepNavy
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Mode Switcher Tabs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = 0 },
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedTab == 0) MedicalTeal.copy(alpha = 0.2f) else SurfaceCard,
                            border = BorderStroke(1.dp, if (selectedTab == 0) MedicalTeal else BorderSubtle)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Timeline, contentDescription = null, tint = if (selectedTab == 0) MedicalTeal else SlateGray, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Chronologie des 7 étapes",
                                    color = if (selectedTab == 0) MedicalTeal else SoftWhite,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = 1 },
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedTab == 1) ChampagneGold.copy(alpha = 0.2f) else SurfaceCard,
                            border = BorderStroke(1.dp, if (selectedTab == 1) ChampagneGold else BorderSubtle)
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.MenuBook, contentDescription = null, tint = if (selectedTab == 1) ChampagneGold else SlateGray, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Glossaire Médical",
                                    color = if (selectedTab == 1) ChampagneGold else SoftWhite,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        if (selectedTab == 0) {
            // Pedagogical Medical Disclaimer
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, BorderSubtle)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Default.Info, contentDescription = "Avis", tint = InfoBlue, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Cadre Pédagogique et Responsabilité Clinique :",
                                color = InfoBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Ce module présente les principes généraux du parcours anesthésique à des fins de formation et d'aide à la décision. Il ne constitue pas un protocole médical universel et ne remplace en aucun cas les protocoles institutionnels de votre établissement ni le jugement clinique du médecin anesthésiste-réanimateur responsable.",
                                color = SlateGray,
                                fontSize = 10.5.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // Quick Collapse/Expand Controls
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ÉTAPES DU PARCOURS OPÉRATOIRE",
                        color = MedicalTeal,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            onClick = {
                                procedureSteps.forEach { expandedSteps[it.id] = true }
                            },
                            shape = RoundedCornerShape(4.dp),
                            color = SurfaceCard,
                            border = BorderStroke(0.5.dp, BorderSubtle)
                        ) {
                            Text(
                                text = "Tout développer",
                                color = SlateGray,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }

                        Surface(
                            onClick = {
                                expandedSteps.clear()
                            },
                            shape = RoundedCornerShape(4.dp),
                            color = SurfaceCard,
                            border = BorderStroke(0.5.dp, BorderSubtle)
                        ) {
                            Text(
                                text = "Tout réduire",
                                color = SlateGray,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // The 7 Chronological Step Cards
            items(procedureSteps) { step ->
                val isExpanded = expandedSteps[step.id] ?: false
                val isStepCompleted = step.checklist.all { item -> checkedItems["${step.id}_$item"] == true }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .testTag("step_card_${step.stepNumber}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(
                        1.dp,
                        if (isStepCompleted) SuccessGreen.copy(alpha = 0.6f)
                        else if (isExpanded) MedicalTeal.copy(alpha = 0.6f)
                        else BorderSubtle
                    )
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Clickable Header to Expand/Collapse
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedSteps[step.id] = !isExpanded }
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Step Number Circle Badge
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isStepCompleted) SuccessGreen.copy(alpha = 0.2f)
                                        else if (isExpanded) MedicalTeal.copy(alpha = 0.2f)
                                        else DeepNavy
                                    )
                                    .border(
                                        1.dp,
                                        if (isStepCompleted) SuccessGreen
                                        else if (isExpanded) MedicalTeal
                                        else BorderSubtle,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isStepCompleted) {
                                    Icon(Icons.Default.Check, contentDescription = "Validé", tint = SuccessGreen, modifier = Modifier.size(20.dp))
                                } else {
                                    Text(
                                        text = "${step.stepNumber}",
                                        color = if (isExpanded) MedicalTeal else SoftWhite,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = step.title,
                                    color = SoftWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = step.timing,
                                    color = ChampagneGold,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (isExpanded) "Réduire" else "Développer",
                                tint = SlateGray,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Expanded Content with Animations
                        AnimatedVisibility(visible = isExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                HorizontalDivider(color = BorderSubtle, thickness = 0.8.dp)
                                Spacer(modifier = Modifier.height(10.dp))

                                // Subtitle Description
                                Text(
                                    text = step.subtitle,
                                    color = MedicalTeal,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = step.theoreticalSummary,
                                    color = SlateGray,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Section 1: Notions Clés Théoriques
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = DeepNavy,
                                    border = BorderStroke(1.dp, BorderSubtle),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = "NOTIONS CLÉS & BASES THÉORIQUES",
                                            color = ChampagneGold,
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.5.sp
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        step.keyNotions.forEach { notion ->
                                            Row(
                                                modifier = Modifier.padding(vertical = 3.dp),
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .padding(top = 5.dp)
                                                        .size(5.dp)
                                                        .clip(CircleShape)
                                                        .background(ChampagneGold)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = notion,
                                                    color = SoftWhite,
                                                    fontSize = 11.sp,
                                                    lineHeight = 15.sp
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Section 2: Points de Vigilance & Alertes
                                if (step.safetyPoints.isNotEmpty()) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = AlertRed.copy(alpha = 0.1f),
                                        border = BorderStroke(1.dp, AlertRed.copy(alpha = 0.4f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Warning, contentDescription = "Alerte", tint = AlertRed, modifier = Modifier.size(15.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "POINTS DE VIGILANCE & SÉCURITÉ",
                                                    color = AlertRed,
                                                    fontSize = 10.5.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            step.safetyPoints.forEach { point ->
                                                Text(
                                                    text = "• $point",
                                                    color = SoftWhite,
                                                    fontSize = 10.5.sp,
                                                    lineHeight = 15.sp,
                                                    modifier = Modifier.padding(vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                }

                                // Section 3: Liste de Vérification Pratique (Checklist Distincte)
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = DeepNavy,
                                    border = BorderStroke(1.dp, MedicalTeal.copy(alpha = 0.35f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, tint = MedicalTeal, modifier = Modifier.size(15.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "LISTE DE VÉRIFICATION PRATIQUE",
                                                    color = MedicalTeal,
                                                    fontSize = 10.5.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Text(
                                                text = "${step.checklist.count { checkedItems["${step.id}_$it"] == true }}/${step.checklist.size}",
                                                color = if (isStepCompleted) SuccessGreen else SlateGray,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        step.checklist.forEach { itemText ->
                                            val key = "${step.id}_$itemText"
                                            val isChecked = checkedItems[key] ?: false

                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable { checkedItems[key] = !isChecked }
                                                    .padding(vertical = 3.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Checkbox(
                                                    checked = isChecked,
                                                    onCheckedChange = { checkedItems[key] = it },
                                                    colors = CheckboxDefaults.colors(
                                                        checkedColor = SuccessGreen,
                                                        uncheckedColor = SlateGray,
                                                        checkmarkColor = MidnightNavy
                                                    ),
                                                    modifier = Modifier.size(24.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = itemText,
                                                    color = if (isChecked) SoftWhite else SlateGray,
                                                    fontSize = 11.sp,
                                                    fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal,
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Section 4: Objectifs Cliniques Cibles
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = SurfaceCard,
                                    border = BorderStroke(0.5.dp, BorderSubtle),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = "OBJECTIFS CLINIQUES CIBLES :",
                                            color = SlateGray,
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        step.clinicalObjectives.forEach { obj ->
                                            Text(
                                                text = "• $obj",
                                                color = MedicalTeal,
                                                fontSize = 10.5.sp,
                                                modifier = Modifier.padding(vertical = 1.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }
        } else {
            // Mode 1: Medical Glossary
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(
                        text = "GLOSSAIRE DES TERMES MÉDICAUX D'ANESTHÉSIE",
                        color = ChampagneGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Définitions et implications cliniques des abréviations, scores et concepts majeurs.",
                        color = SlateGray,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = glossarySearch,
                        onValueChange = { glossarySearch = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Rechercher un terme (ex: TOF, Mallampati, BIS, ASA...)", color = SlateGray, fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = ChampagneGold) },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceCard,
                            unfocusedContainerColor = SurfaceCard,
                            focusedBorderColor = ChampagneGold,
                            unfocusedBorderColor = BorderSubtle,
                            focusedTextColor = SoftWhite,
                            unfocusedTextColor = SoftWhite
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Categories Chips
                    val categories = listOf("TOUS", "Voies Aériennes", "Classification", "Monitorage", "Ventilation", "Pharmacologie", "Urgence")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(categories) { cat ->
                            val isSel = (cat == "TOUS" && selectedGlossaryCategory == "TOUS") || (cat != "TOUS" && selectedGlossaryCategory == cat)
                            Surface(
                                modifier = Modifier.clickable { selectedGlossaryCategory = cat },
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSel) ChampagneGold.copy(alpha = 0.2f) else SurfaceCard,
                                border = BorderStroke(1.dp, if (isSel) ChampagneGold else BorderSubtle)
                            ) {
                                Text(
                                    text = cat,
                                    color = if (isSel) ChampagneGold else SlateGray,
                                    fontSize = 10.5.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            val filteredTerms = medicalGlossary.filter { term ->
                (selectedGlossaryCategory == "TOUS" || term.category.contains(selectedGlossaryCategory, ignoreCase = true)) &&
                (glossarySearch.isBlank() || term.term.contains(glossarySearch, ignoreCase = true) || term.definition.contains(glossarySearch, ignoreCase = true))
            }

            items(filteredTerms) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, BorderSubtle)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.term,
                                color = ChampagneGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = DeepNavy,
                                border = BorderStroke(0.5.dp, BorderSubtle)
                            ) {
                                Text(
                                    text = item.category,
                                    color = MedicalTeal,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = item.definition,
                            color = SoftWhite,
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.Top) {
                            Text(
                                text = "Utilité clinique : ",
                                color = MedicalTeal,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = item.clinicalUtility,
                                color = SlateGray,
                                fontSize = 10.5.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
