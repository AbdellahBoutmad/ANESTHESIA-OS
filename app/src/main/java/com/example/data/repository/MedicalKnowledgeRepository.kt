package com.example.data.repository

import com.example.data.model.BiologyTestModel
import com.example.data.model.ChecklistItem
import com.example.data.model.DrugModel
import com.example.data.model.ProtocolModel
import com.example.data.model.QuizQuestion

object MedicalKnowledgeRepository {

    val drugs: List<DrugModel> = listOf(
        DrugModel(
            id = "propofol",
            name = "Propofol",
            brandNames = "Diprivan®",
            category = "Hypnotiques IV",
            mechanism = "Agoniste sélectif des récepteurs GABA-A centraux entraînant une hyperpolarisation membranaire et une dépression corticale rapide.",
            indications = "Induction et entretien de l'anesthésie générale, sédation en réanimation et pour gestes diagnostiques.",
            presentations = "Émulsion lipidique injectable 1% (10 mg/mL) et 2% (20 mg/mL).",
            minDosePerKg = 1.5,
            maxDosePerKg = 2.5,
            doseUnit = "mg/kg",
            maintenanceDose = "4 à 12 mg/kg/h en perfusion continue ou selon protocole AIVOC (cible 2 à 5 µg/mL).",
            routes = "Voie intraveineuse stricte (IVD lente ou perfusion continue).",
            onset = "30 à 45 secondes (un temps de bras-cerveau).",
            duration = "5 à 10 minutes après bolus unique.",
            contraindications = listOf(
                "Hypersensibilité avérée au propofol ou au soja/arachide",
                "Instabilité hémodynamique sévère ou état de choc décompensé",
                "Sédation prolongée chez l'enfant < 16 ans (risque de syndrome de perfusion du propofol PRIS)"
            ),
            adverseEffects = listOf(
                "Hypotension artérielle par veinodilatation et baisse des RVS",
                "Dépression respiratoire dose-dépendante jusqu'à l'apnée",
                "Douleur vive à l'injection intraveineuse périphérique",
                "Bradycardie réflexe ou directe"
            ),
            terrainCautions = "Réduire la dose de 30 à 50% chez le sujet âgé, hypovolémique ou de classe ASA III-IV.",
            monitoringPoints = listOf(
                "Pression artérielle non invasive (PNI) toutes les 2 minutes à l'induction",
                "Tracé ECG continu",
                "Capnographie (EtCO2) et oxymétrie de pouls (SpO2)",
                "Profondeur de l'anesthésie (BIS / entropie si disponible)"
            ),
            antidoteOrAntagonist = "Aucun antidote direct (prise en charge symptomatique : vasopresseurs et ventilation assistée).",
            sources = "SFAR - Recommandations formalisées d'experts sur les hypnotiques intraveineux ; ASA Guidelines."
        ),
        DrugModel(
            id = "etomidate",
            name = "Étomidate",
            brandNames = "Hypnomidate®",
            category = "Hypnotiques IV",
            mechanism = "Modulateur allostérique positif des récepteurs GABA-A. Stabilité hémodynamique remarquable.",
            indications = "Induction anesthésique du patient instable sur le plan hémodynamique, insuffisant cardiaque ou polytraumatisé.",
            presentations = "Solution injectable 2 mg/mL (ampoules de 10 mL = 20 mg).",
            minDosePerKg = 0.2,
            maxDosePerKg = 0.3,
            doseUnit = "mg/kg",
            maintenanceDose = "Non recommandé pour l'entretien prolongé en raison du blocage surrénalien.",
            routes = "Voie intraveineuse lente (IVD).",
            onset = "30 à 60 secondes.",
            duration = "4 à 8 minutes.",
            contraindications = listOf(
                "Insuffisance surrénalienne connue ou choc septique réfractaire",
                "Porphyrie aiguë",
                "Hypersensibilité à l'étomidate"
            ),
            adverseEffects = listOf(
                "Blocage transitoire de la 11-bêta-hydroxylase (inhibition de la synthèse de cortisol pendant 12 à 24h)",
                "Myoclonies fréquentes à l'induction (prévenues par un pré-traitement morphinique)",
                "Douleur veineuse et thrombophlébite locale",
                "Nausées et vomissements postopératoires (NVPO)"
            ),
            terrainCautions = "Privilégié chez le coronarien et le patient valvulaire grâce à l'absence de dépression myocardique.",
            monitoringPoints = listOf("Scope ECG", "Surveillance tensionnelle rapprochée", "Surveillance cortisolémie si choc septique suspecté."),
            antidoteOrAntagonist = "Aucun antagoniste spécifique.",
            sources = "SFAR / Conférence d'experts anesthésie du coronarien."
        ),
        DrugModel(
            id = "ketamine",
            name = "Kétamine",
            brandNames = "Ketalar®",
            category = "Hypnotiques IV",
            mechanism = "Antagoniste non compétitif des récepteurs NMDA du glutamate, provoquant une anesthésie dissociative.",
            indications = "Induction chez l'asthmatique (bronchodilatation puissante), choc cardiogénique avec réserves sympathiques, analgésie multimodale préventive.",
            presentations = "Flacons de 10 mg/mL et 50 mg/mL.",
            minDosePerKg = 1.0,
            maxDosePerKg = 2.0,
            doseUnit = "mg/kg",
            maintenanceDose = "En analgésie peropératoire : bolus de 0.15 à 0.25 mg/kg puis 0.1 à 0.2 mg/kg/h.",
            routes = "IVD, IM, per os, intranasale.",
            onset = "30 à 60 secondes (IV), 3 à 4 min (IM).",
            duration = "10 à 20 minutes (anesthésie), analgésie résiduelle prolongée.",
            contraindications = listOf(
                "Hypertension artérielle sévère non contrôlée",
                "Cardiopathie ischémique instable (majoration de la consommation en O2 MVO2)",
                "Hypertension intracrânienne décompensée sans ventilation contrôlée",
                "Antécédents psychotiques sévères"
            ),
            adverseEffects = listOf(
                "Tachycardie et poussée hypertensive par stimulation sympathique centrale",
                "Hallucinations et réveils agités (prévenus par une benzodiazépine associée)",
                "Hypersialorrhée (peut nécessiter de l'atropine)",
                "Augmentation du tonus musculaire"
            ),
            terrainCautions = "Idéal chez le patient bronchospastique ou en état de mal asthmatique grâce à son effet bronchodilatateur direct.",
            monitoringPoints = listOf("FC, TA, SpO2, hallucinations au réveil"),
            antidoteOrAntagonist = "Aucun (sédation par benzodiazépines en cas de dysphorie).",
            sources = "SFAR Recommandations Analgésie périopératoire."
        ),
        DrugModel(
            id = "sufentanil",
            name = "Sufentanil",
            brandNames = "Sufenta®",
            category = "Morphiniques",
            mechanism = "Agoniste pur et sélectif des récepteurs opioïdes mu (µ) d'une puissance analgésique 5 à 10 fois supérieure au fentanyl et 500 à 1000 fois la morphine.",
            indications = "Analgésie peropératoire en chirurgie majeure, anesthésie balancée, analgésie obstétricale (péridurale/rachianesthésie).",
            presentations = "Ampoules de 5 µg/mL (2 mL = 10 µg) et 50 µg/mL (5 mL = 250 µg).",
            minDosePerKg = 0.15,
            maxDosePerKg = 0.3,
            doseUnit = "µg/kg",
            maintenanceDose = "Réinjections de 0.05 à 0.1 µg/kg selon signes hémodynamiques de nociception ou AIVOC.",
            routes = "Intraveineuse (IVD ou AIVOC), intrathécale, péridurale.",
            onset = "1 à 2 minutes.",
            duration = "30 à 45 minutes selon les doses cumulées.",
            contraindications = listOf(
                "Insuffisance respiratoire obstructive sévère sans ventilation mécanique",
                "Myasthénie grave non compensée",
                "Hypersensibilité aux dérivés morphiniques"
            ),
            adverseEffects = listOf(
                "Dépression respiratoire centrale prolongée",
                "Rigidité thoracique à l'injection rapide (syndrome du thorax en bois)",
                "Bradycardie vagale",
                "Nausées, vomissements, prurit, rétention aiguë d'urine"
            ),
            terrainCautions = "Accumulation possible en cas d'insuffisance rénale terminale ou d'insuffisance hépatique sévère.",
            monitoringPoints = listOf("Fréquence respiratoire", "SpO2", "Score de sédation", "EtCO2"),
            antidoteOrAntagonist = "Naloxone (Narcan®) titrée par paliers de 0.04 à 0.08 mg IV.",
            sources = "SFAR Recommandations sur les morphiniques périopératoires."
        ),
        DrugModel(
            id = "remifentanil",
            name = "Rémifentanil",
            brandNames = "Ultiva®",
            category = "Morphiniques",
            mechanism = "Agoniste mu pur à cinétique ultra-courte métabolisé par les estérases sanguines et tissulaires non spécifiques.",
            indications = "Analgésie peropératoire en anesthésie générale balancée ou intraveineuse totale (TIVA/AIVOC).",
            presentations = "Flacon de poudre lyophilisée à reconstituer : 1 mg, 2 mg ou 5 mg (dilution habituelle à 20 ou 50 µg/mL).",
            minDosePerKg = 0.5,
            maxDosePerKg = 1.0,
            doseUnit = "µg/kg",
            maintenanceDose = "Perfusion continue de 0.05 à 0.5 µg/kg/min (ou cible AIVOC Minto 2 à 6 ng/mL).",
            routes = "Perfusion intraveineuse continue exclusive (jamais en bolus non monitoré).",
            onset = "60 à 90 secondes.",
            duration = "3 à 5 minutes (demi-vie contextuelle constante de 3 à 4 min quelle que soit la durée de perfusion).",
            contraindications = listOf(
                "Absence de ventilation mécanique contrôlée",
                "Administration épidurale ou intrathécale (contient de la glycine)",
                "Absence d'anticipation de l'analgésie postopératoire relais"
            ),
            adverseEffects = listOf(
                "Rigidité musculaire majeure en cas de bolus rapide",
                "Hypotension artérielle et bradycardie dose-dépendantes",
                "Hyperalgésie postopératoire précoce si arrêt brutal sans relais morphinique"
            ),
            terrainCautions = "Ne s'accumule pas chez l'insuffisant rénal ou hépatique. Anticiper le relais antalgique 30 min avant l'extubation.",
            monitoringPoints = listOf("Hémodynamique continue", "EtCO2", "Planification stricte de l'analgésie de réveil"),
            antidoteOrAntagonist = "Naloxone (rarement utile en raison de la décroissance ultra-rapide en 5 min).",
            sources = "SFAR / Société Française d'Anesthésie Réanimation."
        ),
        DrugModel(
            id = "rocuronium",
            name = "Rocuronium",
            brandNames = "Esmeron®",
            category = "Curares",
            mechanism = "Curare non dépolarisant stéroïdien, bloqueur compétitif des récepteurs nicotiniques postsynaptiques de la plaque motrice.",
            indications = "Facilitation de l'intubation trachéale, relâchement musculaire peropératoire, alternative à la succinylcholine pour induction en séquence rapide (ISR).",
            presentations = "Solution injectable 10 mg/mL (flacons de 5 mL = 50 mg).",
            minDosePerKg = 0.6,
            maxDosePerKg = 1.2,
            doseUnit = "mg/kg",
            maintenanceDose = "0.15 mg/kg toutes les 25 à 40 minutes guidé par le Train-de-Quatre (TOF).",
            routes = "Intraveineuse directe (IVD).",
            onset = "60 secondes à dose d'ISR (1.0 à 1.2 mg/kg) ; 90 secondes à dose standard (0.6 mg/kg).",
            duration = "35 à 60 minutes.",
            contraindications = listOf("Hypersensibilité avérée au rocuronium ou aux curares."),
            adverseEffects = listOf(
                "Curarisation résiduelle postopératoire si décurarisation non monitorée",
                "Réactions anaphylactiques (première cause de choc anaphylactique au bloc opératoire)",
                "Légère tachycardie vagolytique à fortes doses"
            ),
            terrainCautions = "Prolongation de la durée d'action chez l'insuffisant hépatique ou rénal et le patient hypothermique.",
            monitoringPoints = listOf("Monitorage continu du Train-de-Quatre (TOF) obligatoire au niveau de l'adducteur du pouce ou de l'orbiculaire."),
            antidoteOrAntagonist = "Sugammadex (Bridion®) : 2 à 4 mg/kg pour décurarisation standard ; 16 mg/kg pour décurarisation immédiate post-ISR.",
            sources = "SFAR - Recommandations sur la curarisation et la décurarisation en anesthésie."
        ),
        DrugModel(
            id = "succinylcholine",
            name = "Succinylcholine (Célorcurine)",
            brandNames = "Célocurine®",
            category = "Curares",
            mechanism = "Curare dépolarisant, agoniste cholinergique provoquant une dépolarisation soutenue de la plaque motrice.",
            indications = "Intubation trachéale en urgence lors de l'induction en séquence rapide (estomac plein, occlusion, urgence chirurgicale).",
            presentations = "Ampoules de 100 mg / 2 mL (50 mg/mL), conservation impérative au réfrigérateur (+2°C à +8°C).",
            minDosePerKg = 1.0,
            maxDosePerKg = 1.5,
            doseUnit = "mg/kg",
            maintenanceDose = "Jamais d'entretien. Injection unique réservée à l'intubation.",
            routes = "IVD stricte.",
            onset = "30 à 45 secondes.",
            duration = "5 à 8 minutes.",
            contraindications = listOf(
                "Terrain d'Hyperthermie Maligne (antécédent personnel ou familial)",
                "Hyperkaliémie avérée ou risque d'hyperkaliémie aiguë (brûlés > 48h, polytraumatisés, paraplégiques/tétraplégiques, neuropathies, myopathies)",
                "Déficit en pseudocholinestérases plasmatiques",
                "Plaie ouverte du globe oculaire"
            ),
            adverseEffects = listOf(
                "Fasciculations musculaires diffuses et courbatures intenses au réveil",
                "Élévation de la kaliémie de 0.5 à 1.0 mmol/L",
                "Bradycardie sévère et arrêt sinusal (surtout lors d'une 2e injection ou chez l'enfant)",
                "Élévation des pressions intragastrique et intraoculaire"
            ),
            terrainCautions = "Remplacer systématiquement par le Rocuronium à 1.2 mg/kg si la moindre contre-indication existe.",
            monitoringPoints = listOf("Scope ECG continu (bradycardie)", "SpO2", "Surveillance ionique."),
            antidoteOrAntagonist = "Aucun (antagonistes cholinergiques contre-indiqués !). Attendre la clairance enzymatique.",
            sources = "Recommandations SFAR Induction en Séquence Rapide (ISR)."
        ),
        DrugModel(
            id = "sugammadex",
            name = "Sugammadex",
            brandNames = "Bridion®",
            category = "Antagonistes",
            mechanism = "Gamma-cyclodextrine modifiée qui chélate spécifiquement et irréversiblement les curares stéroïdiens (rocuronium, vécuronium).",
            indications = "Inversion rapide et complète du bloc neuromusculaire induit par le rocuronium ou le vécuronium.",
            presentations = "Flacons de 100 mg/mL (2 mL = 200 mg et 5 mL = 500 mg).",
            minDosePerKg = 2.0,
            maxDosePerKg = 16.0,
            doseUnit = "mg/kg",
            fixedDoseText = "2 mg/kg si réapparition de T2 au TOF ; 4 mg/kg si bloc profond (1 à 2 réponses au PTC) ; 16 mg/kg en sauvetage immédiat après induction ISR (3 min après 1.2 mg/kg de rocuronium).",
            routes = "IVD en bolus unique de 10 secondes.",
            onset = "Moins de 2 minutes (récupération d'un rapport T4/T1 > 0.9).",
            duration = "Action définitive par élimination urinaire du complexe stable.",
            contraindications = listOf(
                "Hypersensibilité connue au sugammadex",
                "Insuffisance rénale terminale sous dialyse (clairance < 30 mL/min : balance bénéfice/risque)"
            ),
            adverseEffects = listOf(
                "Bradycardie sinusale parfois sévère (surveillance ECG)",
                "Réactions d'hypersensibilité immédiate (rares)",
                "Interférence avec les contraceptifs oraux (équivalent à un oubli de pilule pendant 7 jours)"
            ),
            terrainCautions = "Avertir impérativement les patientes sous contraception hormonale d'utiliser une méthode mécanique barrière pendant 7 jours.",
            monitoringPoints = listOf("Confirmation du rapport TOF > 90% avant extubation", "Surveillance ECG."),
            antidoteOrAntagonist = "Non applicable.",
            sources = "SFAR Décurarisation au bloc opératoire."
        ),
        DrugModel(
            id = "ephedrine",
            name = "Éphédrine",
            brandNames = "Éphédrine HCl",
            category = "Vasopresseurs & Inotropes",
            mechanism = "Sympathomimétique indirect (libération de noradrénaline endogène) et direct alpha-1 et bêta-1.",
            indications = "Hypotension artérielle peropératoire après induction anesthésique ou rachianesthésie avec fréquence cardiaque normale ou basse.",
            presentations = "Ampoules de 30 mg / 1 mL, diluer impérativement à 3 mg/mL (1 ampoule + 9 mL de NaCl 0.9%).",
            minDosePerKg = 0.0,
            maxDosePerKg = 0.0,
            doseUnit = "mg",
            fixedDoseText = "3 à 9 mg par bolus IVD (1 à 3 mL de la dilution à 3 mg/mL), à renouveler toutes les 2-3 minutes. Dose maximale : 30 mg.",
            routes = "Intraveineuse directe titrée (IVD).",
            onset = "1 minute.",
            duration = "10 à 15 minutes.",
            contraindications = listOf(
                "Tachycardie sévère (> 110 bpm)",
                "Cardiopathie ischémique aiguë",
                "Tachyphylaxie (épuisement des stocks après 30-40 mg)"
            ),
            adverseEffects = listOf("Tachycardie réactionnelle", "Troubles du rythme ventriculaire", "Épuisement de l'effet vasopresseur"),
            terrainCautions = "Préférer la phényléphrine ou la noradrénaline si tachycardie préexistante.",
            monitoringPoints = listOf("Pression artérielle", "Fréquence cardiaque au scope continu"),
            antidoteOrAntagonist = "Bêtabloquants / vasodilatateurs si surdosage.",
            sources = "SFAR / Collège des Enseignants d'Anesthésie-Réanimation."
        ),
        DrugModel(
            id = "noradrenaline",
            name = "Noradrénaline",
            brandNames = "Lévophed®",
            category = "Vasopresseurs & Inotropes",
            mechanism = "Agoniste puissant alpha-1 adrénergique induisant une vasoconstriction artérielle et veineuse majeure et une augmentation de la postcharge.",
            indications = "Choc vasoplégique, hypotension réfractaire peropératoire, choc septique, maintien de la PAM cible.",
            presentations = "Ampoules de 8 mg / 4 mL (2 mg/mL) ou 4 mg / 2 mL. Dilution en seringue électrique (ex: 8 mg dans 50 mL = 0.16 mg/mL).",
            minDosePerKg = 0.05,
            maxDosePerKg = 1.0,
            doseUnit = "µg/kg/min",
            fixedDoseText = "0.05 à 1.0 µg/kg/min titrée selon la Pression Artérielle Moyenne cible (PAM > 65 mmHg).",
            routes = "Perfusion continue par voie veineuse centrale de préférence (ou voie périphérique dédiée de gros calibre en attente).",
            onset = "Immédiat (30 secondes).",
            duration = "1 à 2 minutes après arrêt de la perfusion.",
            contraindications = listOf("Hypovolémie non corrigée (remplissage vasculaire préalable indispensable)."),
            adverseEffects = listOf(
                "Ischémie périphérique et nécrose cutanée en cas d'extravasation",
                "Vasoconstriction mésentérique et rénale excessive si surdosage",
                "Bradycardie réflexe par élévation de la postcharge"
            ),
            terrainCautions = "Vérifier impérativement le reflux veineux et la perméabilité de la voie pour éviter tout risque de nécrose cutanée.",
            monitoringPoints = listOf("Pression artérielle sanglante (KT artériel) recommandée", "Diurèse horaire", "Lactatémie"),
            antidoteOrAntagonist = "Phentolamine en injection locale sous-cutanée en cas d'extravasation tissulaire.",
            sources = "Surviving Sepsis Campaign / SFAR Monitorage hémodynamique."
        ),
        DrugModel(
            id = "adrenaline",
            name = "Adrénaline (Épinéphrine)",
            brandNames = "Adrénaline Aguettant®",
            category = "Médicaments d'urgence",
            mechanism = "Agoniste adrénergique global puissant (alpha-1, alpha-2, bêta-1, bêta-2). Inotrope, chronotrope positif et vasoconstricteur puissant.",
            indications = "Arrêt cardiorespiratoire (ACR), choc anaphylactique (grade II à IV), bronchospasme réfractaire, bradycardie extrême.",
            presentations = "Ampoules de 1 mg / 1 mL (1:1 000) et seringues pré-remplies titrées à 0.1 mg / 1 mL (1:10 000) ou 0.01 mg/mL pour l'anaphylaxie.",
            minDosePerKg = 0.0,
            maxDosePerKg = 0.0,
            doseUnit = "mg",
            fixedDoseText = "ACR : 1 mg IVD toutes les 3-5 min. Anaphylaxie Grade II : 10 à 20 µg IV titrés ; Grade III : 100 à 200 µg IV titrés ; Grade IV : 1 mg IVD (protocole ACR). IM : 0.5 mg chez l'adulte.",
            routes = "Intraveineuse directe titrée, intramusculaire (face antérolatérale de la cuisse), perfusion continue.",
            onset = "Immédiat.",
            duration = "2 à 5 minutes.",
            contraindications = listOf("Aucune contre-indication absolue en situation d'urgence vitale."),
            adverseEffects = listOf(
                "Fibrillation ventriculaire et tachycardie supraventriculaire",
                "Crise hypertensive aiguë avec hémorragie cérébrale",
                "OAP cardiogénique et ischémie myocardique aiguë"
            ),
            terrainCautions = "Titration rigoureuse en cas d'anaphylaxie pour éviter les surdosages hypertensifs iatrogènes.",
            monitoringPoints = listOf("Scope ECG continu", "Défibrillateur prêt à l'emploi", "SpO2"),
            antidoteOrAntagonist = "Non applicable en réanimation vitale.",
            sources = "ERC / SFAR Recommandations Prise en charge du choc anaphylactique et de l'ACR."
        ),
        DrugModel(
            id = "sevoflurane",
            name = "Sévoflurane",
            brandNames = "Sevorane®",
            category = "Anesthésiques inhalés",
            mechanism = "Anesthésique volatil halogéné liquide administré via un vaporisateur spécifique calibré.",
            indications = "Induction inhalatoire chez l'enfant et l'adulte difficile à perfuser, entretien de l'anesthésie générale.",
            presentations = "Flacon de liquide volatil pour inhalation 250 mL.",
            minDosePerKg = 0.0,
            maxDosePerKg = 0.0,
            doseUnit = "vol%",
            fixedDoseText = "CAM 40 ans = 2.05 vol%. Induction : 4 à 8 vol% dans mélange O2/Air. Entretien : 1.2 à 2.5 vol% (0.8 à 1.2 CAM).",
            routes = "Voie respiratoire inhalée.",
            onset = "Rapide (faible coefficient de partage sang/gaz = 0.65).",
            duration = "Élimination pulmonaire rapide en 5 à 10 minutes après coupure du vaporisateur.",
            contraindications = listOf(
                "Sensibilité génétique connue à l'Hyperthermie Maligne (interdiction absolue des halogénés)",
                "Antécédent d'atteinte hépatique induite par les anesthésiques halogénés"
            ),
            adverseEffects = listOf(
                "Hypotension artérielle par vasodilatation systémique",
                "Dépression respiratoire",
                "Agitation au réveil chez le jeune enfant",
                "Déclencheur d'hyperthermie maligne"
            ),
            terrainCautions = "Vérifier l'absence de chaux sodée desséchée dans l'absorbeur de CO2 pour éviter la production de composé A ou de monoxyde de carbone.",
            monitoringPoints = listOf("Concentration télé-expiratoire de gaz (EtSevo)", "MAC affichée", "Température centrale continue"),
            antidoteOrAntagonist = "Dantrolène en cas de crise d'Hyperthermie Maligne.",
            sources = "SFAR / Collège Français d'Anesthésie et Réanimation."
        )
    )

    val protocols: List<ProtocolModel> = listOf(
        ProtocolModel(
            id = "cico_airway",
            title = "Intubation Impossible - Oxygénation Impossible (CICO)",
            category = "URGENCE_CRISE",
            urgencyLevel = "VITALE",
            subtitle = "Algorithme d'urgence vitale des Voies Aériennes Supérieures (VAS)",
            description = "Échec de ventilation au masque facial et échec d'intubation endotrachéale chez un patient anesthésié ou comateux. Risque d'anoxie cérébrale et d'arrêt cardiaque en 3 minutes.",
            algorithmSteps = listOf(
                "1. Déclarer l'urgence à haute voix dans la salle : « INTUBATION IMPOSSIBLE, OXYGÉNATION IMPOSSIBLE ».",
                "2. Demander de l'aide médicale et paramédicale immédiatement + amener le chariot de sauvetage VAS.",
                "3. Oxygéner à 100% à deux soignants avec canule de Guedel et subluxation mandibulaire maximale.",
                "4. Insérer un Dispositif Supraglottique de 2e génération (ex: Masque laryngé I-Gel ou ProSeal).",
                "5. Si échec du dispositif supraglottique -> OXYGÉNATION IMPOSSIBLE confirmée -> DÉCLENCHER LA CRICOTHYROÏDOTOMIE.",
                "6. Technique Scalpel-Bougie-Sonde : Incision cutanée horizontale au niveau de la membrane cricothyroïdienne, bascule à la verticale, insertion d'une bougie d'Eschmann, glissement d'une sonde d'intubation n° 6.0 à ballonnet.",
                "7. Gonfler le ballonnet, ventiler à 100% et vérifier immédiatement la courbe d'EtCO2."
            ),
            keyDrugs = listOf("Curarisation complète (Rocuronium 1.2 mg/kg pour relâcher les spasmes)", "Sugammadex 16 mg/kg si réveil envisageable"),
            specificRisks = listOf("Hypoxie majeure", "Arrêt cardiaque anoxique", "Traumatisme pharyngo-laryngé"),
            postOpMonitoring = listOf("Surveillance en réanimation", "Fibroscopie de contrôle des voies aériennes", "Déclaration incident"),
            guidelinesSource = "SFAR / Difficult Airway Society (DAS) Guidelines 2025."
        ),
        ProtocolModel(
            id = "anaphylaxis",
            title = "Choc Anaphylactique au Bloc Opératoire",
            category = "URGENCE_CRISE",
            urgencyLevel = "VITALE",
            subtitle = "Prise en charge immédiate de l'hypersensibilité immédiate peropératoire",
            description = "Réaction allergique systémique grave médiée par les IgE. Au bloc opératoire, les curares (50-60%), le latex et les antibiotiques sont les déclencheurs majeurs.",
            algorithmSteps = listOf(
                "1. ARRÊTER IMMÉDIATEMENT L'INJECTION OU L'AGENT SUSPECT (antibiotique, curare, colloïde, chlorhexidine, latex).",
                "2. Appeler du renfort médical et demander le chariot d'urgence.",
                "3. FiO2 100% et intubation trachéale si ce n'est pas déjà fait.",
                "4. ADMINISTRATION PRÉCOCE D'ADRÉNALINE (Pierre angulaire) :",
                "   - Grade II (hypotension modérée, érythème, tachycardie) : Bolus IV titrés de 10 à 20 µg (0.01 à 0.02 mg).",
                "   - Grade III (collapsus sévère, bronchospasme) : Bolus IV titrés de 100 à 200 µg toutes les 1-2 minutes.",
                "   - Grade IV (arrêt cardiaque) : 1 mg IVD immédiat répété toutes les 3 min selon algorithme ACR.",
                "5. Remplissage vasculaire massif et rapide par cristalloïdes isotoniques (NaCl 0.9% ou Ringer Lactate 20-30 mL/kg en débit libre).",
                "6. En cas de bronchospasme persistant : Salbutamol 5 à 10 bouffées ou 100 à 200 µg IV.",
                "7. Prélèvements biologiques obligatoires : Tube sec pour Tryptase sérique à T30-T60 min, T2h et T24h, et tube EDTA pour Histamine."
            ),
            keyDrugs = listOf("Adrénaline IV titrée", "Cristalloïdes isotoniques (remplissage massif)", "Salbutamol aérosol / IV"),
            specificRisks = listOf("Désamorçage cardiaque", "Oedème laryngé réfractaire", "Choc vasoplégique persistant"),
            postOpMonitoring = listOf("Transfert systématique en réanimation ou SSPI armée pour au moins 24h", "Consultation allergo-anesthésique à 6 semaines"),
            guidelinesSource = "Recommandations SFAR - Prise en charge des réactions anaphylactoïdes périopératoires."
        ),
        ProtocolModel(
            id = "last_toxicity",
            title = "Toxicité Systémique des Anesthésiques Locaux (LAST)",
            category = "URGENCE_CRISE",
            urgencyLevel = "VITALE",
            subtitle = "Complication majeure des anesthésies locorégionales (ALR)",
            description = "Passage intravasculaire accidentel ou résorption massive d'anesthésiques locaux (Bupivacaïne, Ropivacaïne, Lidocaïne) conduisant à une neurotoxicité puis à un collapsus cardiovasculaire réfractaire.",
            algorithmSteps = listOf(
                "1. STOPPER IMMÉDIATEMENT L'INJECTION D'ANESTHÉSIQUE LOCAL.",
                "2. Appel de renfort et acheminement du KIT ÉMULSION LIPIDIQUE 20% (Intralipide® / Médialipide®).",
                "3. Contrôle des voies aériennes : FiO2 100%, hyperventiler modérément (l'acidose et l'hypoxie aggravent la toxicité).",
                "4. Si convulsions : Midazolam 2 à 5 mg IV ou Propofol en faible dose titrée.",
                "5. ADMINISTRATION DE L'ÉMULSION LIPIDIQUE 20% (Piège lipidique) :",
                "   - Bolus initial de 1.5 mL/kg IV en 1 minute (soit environ 100 mL chez l'adulte de 70 kg).",
                "   - Relais immédiat par perfusion continue de 0.25 mL/kg/min (soit environ 1000 mL/h).",
                "   - Si collapsus persistant : répéter le bolus de 1.5 mL/kg (max 3 bolus) et doubler la perfusion à 0.5 mL/kg/min.",
                "6. Réanimation cardiopulmonaire : adapter l'adrénaline (petites doses titrées < 1 µg/kg), ÉVITER la vasopressine, les anticalciques et les bêtabloquants.",
                "7. Alerter l'équipe d'assistance circulatoire (ECLS / ECMO) si collapsus réfractaire."
            ),
            keyDrugs = listOf("Émulsion lipidique 20% (Intralipide®)", "Benzodiazépine (Midazolam)", "Adrénaline en doses très réduites"),
            specificRisks = listOf("Fibrillation ventriculaire réfractaire", "Arrêt cardiaque anoxique", "Coma convulsif"),
            postOpMonitoring = listOf("Surveillance continue en soins intensifs pendant au moins 12h", "Surveillance ECG et amylasémie"),
            guidelinesSource = "SFAR / ASRA Practice Advisory on Local Anesthetic Systemic Toxicity."
        ),
        ProtocolModel(
            id = "malignant_hyperthermia",
            title = "Hyperthermie Maligne (HM)",
            category = "URGENCE_CRISE",
            urgencyLevel = "VITALE",
            subtitle = "Crise pharmacogénétique métabolique aiguë au bloc opératoire",
            description = "Anomalie génétique des récepteurs de la ryanodine (RyR1) déclenchée par les halogénés (Sévoflurane, Desflurane) ou la succinylcholine. Relargage massif de calcium intracellulaire entraînant hypermétabolisme extrême.",
            algorithmSteps = listOf(
                "1. ARRÊTER IMMÉDIATEMENT LES HALOGÉNÉS ET COUPER LES VAPORISATEURS.",
                "2. Prévenir toute l'équipe, déclarer la crise et faire apporter le DANTROLÈNE.",
                "3. Hyperventiler le patient à FiO2 100% avec un débit de gaz frais maximal (> 10-15 L/min). Remplacer le circuit respiratoire ou placer des filtres charbon actif.",
                "4. DANTROLÈNE IV (Traitement étiologique spécifique) :",
                "   - Dose initiale : 2.5 mg/kg en IVD rapide.",
                "   - Reconstituer chaque flacon de 20 mg avec 60 mL d'eau pour préparation injectable (EPPI). Mobiliser plusieurs soignants pour la dissolution.",
                "   - Répéter par bolus de 1 mg/kg toutes les 5 à 10 minutes jusqu'à normalisation de l'EtCO2 et de la température.",
                "5. Refroidissement actif du patient : poches de glace aux plis inguinaux et axillaires, perfusion de cristalloïdes froids à 4°C, lavage gastrique froid (arrêter dès T° < 38.5°C).",
                "6. Traiter l'hyperkaliémie : Insuline rapide + sérum glucosé 30%, chlorure de calcium si anomalies ECG.",
                "7. Diurèse forcée pour prévenir l'insuffisance rénale par rhabdomyolyse (Mannitol, alcalinisation)."
            ),
            keyDrugs = listOf("Dantrolène sodique (Dantrium®)", "Insuline + Glucose 30%", "Cristalloïdes froids à 4°C"),
            specificRisks = listOf("Rhabdomyolyse massive", "Hyperkaliémie létale", "Coagulation Intravasculaire Disséminée (CIVD)"),
            postOpMonitoring = listOf("Hospitalisation en réanimation 48h", "Dantrolène relais 1 mg/kg toutes les 6h", "Biopsie musculaire ultérieure pour test de contracture"),
            guidelinesSource = "European Malignant Hyperthermia Group (EMHG) / SFAR."
        ),
        ProtocolModel(
            id = "laparoscopic_surgery",
            title = "Chirurgie Coelioscopique / Digestive",
            category = "CHIRURGIE",
            urgencyLevel = "STANDARD",
            subtitle = "Particularités anesthésiques du pneumopéritoine et de la position déclive/proclive",
            description = "Insufflation péritonéale de CO2 entraînant une augmentation des pressions intra-abdominales et intrathoraciques, baisse du retour veineux, et hypercapnie.",
            algorithmSteps = listOf(
                "1. Consultation : Évaluation cardiovasculaire et respiratoire, recherche d'un RGO.",
                "2. Prévention NVPO systématique (Score d'Apfel élevé dans la coelioscopie : Dexaméthasone + Ondansétron).",
                "3. Monitorage standard + Capnographie continue rigoureuse (surveillance de l'EtCO2).",
                "4. Induction : Anesthésie générale avec intubation orotrachéale systématique et sonde gastrique pour vidange avant insufflation.",
                "5. Ventilation protectrice : Volume courant 6 à 7 mL/kg de poids idéal, PEP 5 à 8 cmH2O, adapter la FR pour maintenir EtCO2 entre 35 et 40 mmHg.",
                "6. Curarisation continue nécessaire pour optimiser les conditions chirurgicales et limiter la pression d'insufflation (< 12 mmHg).",
                "7. Décurarisation monitorée obligatoire au Sugammadex ou Néostigmine (rapport T4/T1 > 90%)."
            ),
            keyDrugs = listOf("Propofol / Rémifentanil ou Sufentanil", "Rocuronium", "Dexaméthasone 4 à 8 mg", "Ondansétron 4 mg"),
            specificRisks = listOf("Embolie gazeuse", "Hypercapnie sévère", "Pneumothorax sous tension", "Douleur scapulaire réflexe"),
            postOpMonitoring = listOf("Surveillance respiratoire en SSPI", "EVA douleur", "Vérification de la reprise du transit"),
            guidelinesSource = "SFAR / Collège des Anesthésistes Réanimateurs."
        ),
        ProtocolModel(
            id = "cesarean_section",
            title = "Césarienne sous Rachianesthésie",
            category = "CHIRURGIE",
            urgencyLevel = "HAUTE",
            subtitle = "Prise en charge anesthésique obstétricale programmée et en urgence",
            description = "Considérations doubles : sécurité maternelle (estomac plein physiologique, risque d'hypotension artérielle brutale par compression cave) et passage foetal des drogues.",
            algorithmSteps = listOf(
                "1. Vérification du bilan préopératoire (NFS, plaquettes > 80 G/L, hémostase, RAI valides).",
                "2. Prévention du syndrome d'inhalation acide (Anti-H2 / Citrate de sodium si AG envisagée).",
                "3. Installation au bloc : décubitus dorsal avec inclinaison latérale gauche de 15° (coussin de décharge aortocave).",
                "4. Remplissage par cristalloïdes (co-remplissage 500-1000 mL) et perfusion préventive de vasopresseur (phényléphrine ou noradrénaline à faible débit).",
                "5. Réalisation de la rachianesthésie en L3-L4 ou L4-L5 avec aiguille à pointe crayon (25G ou 27G) :",
                "   - Bupivacaïne hyperbare 0.5% : 8 à 10 mg (1.6 à 2.0 mL).",
                "   - Sufentanil : 2.5 µg.",
                "   - Morphine sans conservateur : 100 µg (analgésie postopératoire des 24 premières heures).",
                "6. Contrôle du niveau sensitif (niveau T4 attendu avant incision au niveau des mamelons).",
                "7. Après clampage du cordon : Ocytocine 5 UI en IV lente puis perfusion continue de 10 à 20 UI/500 mL."
            ),
            keyDrugs = listOf("Bupivacaïne hyperbare 0.5%", "Sufentanil", "Morphine intrathécale 100 µg", "Ocytocine", "Éphédrine / Phényléphrine"),
            specificRisks = listOf("Hypotension maternelle brutale", "Détresse foetale par hypoperfusion placentaire", "Hémorragie de la délivrance"),
            postOpMonitoring = listOf("Surveillance hémodynamique et saignement utérin en SSPI pendant au moins 2h", "Surveillance respiratoire (morphine)"),
            guidelinesSource = "SFAR / CNGOF Recommandations pour la pratique clinique en obstétrique."
        )
    )

    val biologyTests: List<BiologyTestModel> = listOf(
        BiologyTestModel(
            id = "hemoglobin",
            name = "Hémoglobine & NFS",
            category = "Hématologie",
            normalRange = "Homme : 13.0 - 17.5 | Femme : 12.0 - 15.5",
            unit = "g/dL",
            anestheticImplications = "Détermine le transport en oxygène (TaO2). Une anémie non corrigée augmente la morbi-mortalité cardiovasculaire périopératoire et le risque transfusionnel.",
            frequentCausesOfAnomaly = "Carence martiale, saignement digestif occulte, insuffisance rénale, hémolyse, hémodilution.",
            requiresPostponementOrOptimization = true,
            optimizationCriteria = "En chirurgie programmée à risque hémorragique : traiter l'anémie si Hb < 13 g/dL (fer IV, érythropoïétine) et reporter la chirurgie non urgente pour correction préalable.",
            medicalSource = "SFAR Patient Blood Management (Gestion personnalisée du capital sanguin)."
        ),
        BiologyTestModel(
            id = "platelets",
            name = "Numération Plaquettaire",
            category = "Hématologie",
            normalRange = "150 - 400",
            unit = "G/L (x10^9/L)",
            anestheticImplications = "Conditionne l'hémostase primaire et la possibilité de réaliser une anesthésie périmédullaire (rachianesthésie, péridurale) en toute sécurité sans risque d'hématome compressif.",
            frequentCausesOfAnomaly = "Thrombopénie induite par l'héparine (TIH), cirrhose / hypersplénisme, PTI, chimiothérapie, pré-éclampsie.",
            requiresPostponementOrOptimization = true,
            optimizationCriteria = "Contre-indication relative aux ponctions périmédullaires si plaquettes < 80-100 G/L (seuil strict à 80 G/L pour rachianesthésie selon avis hémostase).",
            medicalSource = "GIHP / SFAR Prise en charge des anomalies de l'hémostase au bloc opératoire."
        ),
        BiologyTestModel(
            id = "hemostasis_tp_inr",
            name = "Taux de Prothrombine (TP) / INR",
            category = "Hémostase",
            normalRange = "TP > 70% | INR : 0.8 - 1.2 (sans anticoagulant)",
            unit = "% / ratio",
            anestheticImplications = "Évalue la voie extrinsèque de la coagulation et les facteurs vitamine K-dépendants (II, VII, IX, X). Risque hémorragique majeur et contre-indication à l'ALR neuraxiale.",
            frequentCausesOfAnomaly = "Traitement par antivitamine K (AVK), insuffisance hépatocellulaire, carence en vitamine K, CIVD.",
            requiresPostponementOrOptimization = true,
            optimizationCriteria = "Chirurgie programmée : INR doit être < 1.5 (ou < 1.2 pour chirurgie neurologique/ophtalmologique ou ALR). Si AVK, relais par héparine ou réversion par PPSB + Vitamine K en urgence.",
            medicalSource = "Recommandations GIHP - Gestion des agents anticoagulants en périopératoire."
        ),
        BiologyTestModel(
            id = "potassium",
            name = "Kaliémie (K+)",
            category = "Ionogramme",
            normalRange = "3.5 - 5.0",
            unit = "mmol/L",
            anestheticImplications = "Modifie le potentiel de repos transmembranaire myocardique. Risque de troubles du rythme ventriculaire mortels et potentialisation des curares.",
            frequentCausesOfAnomaly = "Insuffisance rénale, prise de diurétiques (furosémide), IEC/ARA2, vomissements, diarrhée, acidose.",
            requiresPostponementOrOptimization = true,
            optimizationCriteria = "Chirurgie réglée : reporter si K+ < 3.0 mmol/L (correction IV lente avec monitorage) ou si K+ > 5.5 mmol/L (risque d'arrêt cardiaque, CONTRE-INDICATION ABSOLUE à la Célocurine).",
            medicalSource = "SFAR - Troubles ioniques périopératoires."
        ),
        BiologyTestModel(
            id = "creatinine_dfg",
            name = "Créatininémie & Débit de Filtration Glomérulaire (DFG)",
            category = "Fonction Rénale",
            normalRange = "Créatinine : 50 - 110 µmol/L | DFG > 90 mL/min/1.73m²",
            unit = "µmol/L / mL/min",
            anestheticImplications = "Détermine l'adaptation posologique des médicaments à élimination rénale (curares, morphiniques, antibiotiques) et le risque d'insuffisance rénale aiguë postopératoire.",
            frequentCausesOfAnomaly = "Néphropathie diabétique, néphroangiosclérose, déshydratation aiguë, médicaments néphrotoxiques (AINS, produits de contraste).",
            requiresPostponementOrOptimization = true,
            optimizationCriteria = "Insuffisance rénale aiguë non explorée : bilan étiologique et hydratation préalable. Proscrire les AINS et adapter les doses de morphiniques/curares.",
            medicalSource = "SFAR / KDIGO Guidelines on Acute Kidney Injury."
        ),
        BiologyTestModel(
            id = "glycemia",
            name = "Glycémie & HbA1c",
            category = "Métabolisme",
            normalRange = "Glycémie à jeun : 3.9 - 6.1 mmol/L (0.7 - 1.1 g/L) | HbA1c < 7.0%",
            unit = "mmol/L / %",
            anestheticImplications = "L'hyperglycémie peropératoire favorise les infections de site opératoire, le retard de cicatrisation et la survenue d'accidents cardiovasculaires.",
            frequentCausesOfAnomaly = "Diabète de type 1 ou 2 déséquilibré, corticothérapie, stress chirurgical.",
            requiresPostponementOrOptimization = true,
            optimizationCriteria = "HbA1c > 8.5 - 9.0% en chirurgie réglée : envisager le report pour optimisation glycémique conjointe avec l'endocrinologue.",
            medicalSource = "SFAR - Prise en charge du patient diabétique en période périopératoire."
        )
    )

    val checklistEquipmentItems: List<ChecklistItem> = listOf(
        ChecklistItem("eq_1", "VENTILATEUR", "Alimentation en Oxygène", "Pression réseau 4 bars vérifiée et bascule automatique sur réserve testée."),
        ChecklistItem("eq_2", "VENTILATEUR", "Test d'étanchéité du respirateur", "Auto-test du respirateur réussi avec fuite < 150 mL/min sous 30 cmH2O."),
        ChecklistItem("eq_3", "VENTILATEUR", "Chaux sodée / Absorbeur de CO2", "Couleur non virée au violet, absence de dessiccation."),
        ChecklistItem("eq_4", "VENTILATEUR", "Vaporisateur halogéné", "Niveau de liquide vérifié, verrouillage mécanique assuré."),
        ChecklistItem("eq_5", "MONITORAGE", "Moniteur multiparamétrique", "Scope ECG 3/5 brins, PNI avec brassard adapté, capteur SpO2 fonctionnels."),
        ChecklistItem("eq_6", "MONITORAGE", "Capnographie (EtCO2)", "Ligne d'aspiration branchée, étalonnage du capteur infrarouge validé."),
        ChecklistItem("eq_7", "MONITORAGE", "Curarimètre (TOF)", "Électrodes positionnées et réponse électrique testée."),
        ChecklistItem("eq_8", "VOIES_AERIENNES", "Aspiration trachéale murale", "Dépression > -400 mmHg au manomètre et sonde d'aspiration branchée."),
        ChecklistItem("eq_9", "VOIES_AERIENNES", "Masque facial & Canule de Guedel", "Tailles adaptées au morphotype du patient disponibles sur le plateau."),
        ChecklistItem("eq_10", "VOIES_AERIENNES", "Laryngoscope & Vidéolaryngoscope", "Lames vérifiées, éclairage LED puissant et batterie chargée."),
        ChecklistItem("eq_11", "VOIES_AERIENNES", "Sondes d'intubation & Ballonnet", "Deux tailles préparées, étanchéité du ballonnet vérifiée à la seringue."),
        ChecklistItem("eq_12", "VOIES_AERIENNES", "Mandrin béquillé / Bougie d'Eschmann", "Présent sur le chariot d'intubation à portée immédiate."),
        ChecklistItem("eq_13", "URGENCE", "Défibrillateur cardiaque", "Batterie testée, palettes ou électrodes adhésives à jour."),
        ChecklistItem("eq_14", "URGENCE", "Drogues d'urgence prêtes", "Seringue d'Éphédrine diluée à 3 mg/mL et seringue d'Atropine étiquetées.")
    )

    val checklistOmsItems: List<ChecklistItem> = listOf(
        // SIGN IN
        ChecklistItem("oms_1", "SIGN_IN", "Identité du patient confirmée", "Vérification avec le bracelet d'hospitalisation et confirmation verbale par le patient."),
        ChecklistItem("oms_2", "SIGN_IN", "Site et côté opératoire marqués", "Marquage cutané visible par le chirurgien."),
        ChecklistItem("oms_3", "SIGN_IN", "Consentement chirurgical et anesthésique", "Dossier anesthésique complet et signé présent."),
        ChecklistItem("oms_4", "SIGN_IN", "Risque d'intubation difficile / inhalation", "Matériel d'intubation difficile prêt si risque identifié."),
        ChecklistItem("oms_5", "SIGN_IN", "Allergies vérifiées", "Absence d'allergie ou protocole spécifique engagé."),
        ChecklistItem("oms_6", "SIGN_IN", "Risque hémorragique évalué", "Voies veineuses adaptées et disponibilité des culots globulaires confirmée."),
        // TIME OUT
        ChecklistItem("oms_7", "TIME_OUT", "Présentation de toute l'équipe", "Chaque intervenant énonce son nom et sa fonction."),
        ChecklistItem("oms_8", "TIME_OUT", "Confirmation du patient et du geste", "Patient, acte opératoire et installation validés collectivement."),
        ChecklistItem("oms_9", "TIME_OUT", "Antibioprophylaxie administrée", "Injection terminée dans les 30 à 60 minutes précédant l'incision."),
        ChecklistItem("oms_10", "TIME_OUT", "Événements critiques anticipés", "Le chirurgien et l'anesthésiste partagent les points délicats et durées prévues."),
        // SIGN OUT
        ChecklistItem("oms_11", "SIGN_OUT", "Confirmation de l'intervention réalisée", "Enregistrement du titre exact du compte-rendu opératoire."),
        ChecklistItem("oms_12", "SIGN_OUT", "Compte des compresses, aiguilles et instruments", "Validé complet et exact par l'infirmière de bloc opératoire (IBODE)."),
        ChecklistItem("oms_13", "SIGN_OUT", "Prescription et destination postopératoire", "Consignes SSPI / Réanimation et analgésie postopératoire tracées.")
    )

    val quizQuestions: List<QuizQuestion> = listOf(
        QuizQuestion(
            id = 1,
            question = "Quel est l'antidote spécifique permettant l'inversion immédiate d'un bloc neuromusculaire induit par le Rocuronium à dose d'ISR (1.2 mg/kg) ?",
            domain = "Pharmacologie",
            options = listOf("Néostigmine 50 µg/kg", "Sugammadex 16 mg/kg", "Atropine 1 mg", "Flumazénil 0.2 mg"),
            correctIndex = 1,
            clinicalExplanation = "Le Sugammadex à la posologie de 16 mg/kg permet une inversion immédiate du bloc profond induit par le rocuronium en 3 minutes environ, notamment en situation d'intubation difficile imprévue."
        ),
        QuizQuestion(
            id = 2,
            question = "Quelle est la concentration sérique toxique minimale de potassium (kaliémie) constituant une contre-indication absolue à l'utilisation de la Succinylcholine (Célocurine) ?",
            domain = "Sécurité Anesthésique",
            options = listOf("K+ > 4.5 mmol/L", "K+ > 5.0 mmol/L", "K+ > 5.5 mmol/L", "K+ > 6.5 mmol/L"),
            correctIndex = 2,
            clinicalExplanation = "La succinylcholine provoque une libération physiologique de potassium de 0.5 à 1.0 mmol/L lors des fasciculations. Au-delà de 5.5 mmol/L, le risque d'arrêt cardiaque en fibrillation ventriculaire ou asystolie est majeur."
        ),
        QuizQuestion(
            id = 3,
            question = "Lors d'un choc anaphylactique peropératoire de Grade III (collapsus cardiovasculaire et bronchospasme), quelle est la posologie initiale d'adrénaline IV recommandée chez l'adulte ?",
            domain = "Urgences & Réanimation",
            options = listOf("10 à 20 µg IV titrés", "100 à 200 µg IV titrés", "1 mg IVD direct", "5 mg en aérosol"),
            correctIndex = 1,
            clinicalExplanation = "Pour le Grade III (choc sévère avec risque d'arrêt), la SFAR recommande des bolus d'adrénaline de 100 à 200 µg IV titrés toutes les 1 à 2 minutes avec remplissage massif. La dose de 1 mg est réservée au Grade IV (ACR)."
        ),
        QuizQuestion(
            id = 4,
            question = "Dans la classification de Mallampati, quel stade correspond à la visualisation du palais mou, de la base de la luette, mais pas des piliers amygdaliens ?",
            domain = "Voies Aériennes",
            options = listOf("Classe I", "Classe II", "Classe III", "Classe IV"),
            correctIndex = 2,
            clinicalExplanation = "Classe I : luette, voile du palais, piliers visibles. Classe II : luette et voile visibles. Classe III : voile du palais et base de la luette seuls visibles. Classe IV : seul le palais osseux (dur) est visible."
        ),
        QuizQuestion(
            id = 5,
            question = "Quel est le score d'Aldrete minimal requis pour autoriser la sortie d'un patient de la Salle de Surveillance Post-Interventionnelle (SSPI) vers le service de soins conventionnel ?",
            domain = "Surveillance Postopératoire",
            options = listOf("Score >= 7 / 10", "Score >= 9 / 10", "Score = 10 / 10 obligatoirement", "Score >= 5 / 10"),
            correctIndex = 1,
            clinicalExplanation = "Un score d'Aldrete modifié supérieur ou égal à 9 sur 10 (avec une note minimale de 2/2 pour la respiration et la saturation) est requis selon les recommandations SFAR pour autoriser la sortie de SSPI."
        )
    )
}
