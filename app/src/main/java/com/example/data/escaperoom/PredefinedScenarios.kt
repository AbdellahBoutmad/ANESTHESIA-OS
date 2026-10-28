package com.example.data.escaperoom

object PredefinedScenarios {

    val scenarios: List<EscapeRoomScenario> by lazy {
        listOf(
            createLaryngospasmScenario(),
            createAnaphylaxisScenario(),
            createMalignantHyperthermiaScenario(),
            createSelectiveIntubationScenario(),
            createHemorrhagicShockScenario()
        )
    }

    /**
     * 1. Laryngospasme aigu & Désaturation critique au réveil
     */
    private fun createLaryngospasmScenario(): EscapeRoomScenario {
        val p1 = ScenarioPhase(
            phaseId = "p1_extubation",
            title = "Phase 1 : Extubation & Tirage Sus-sternal",
            narrative = "Fin d'une adénoïdectomie chez un enfant de 6 ans. 45 secondes après l'extubation, l'enfant présente un stridor inspiratoire aigu, un tirage sus-sternal intense avec battement des ailes du nez. Aucun murmure vésiculaire à l'auscultation.",
            vitals = SimulatedVitals(heartRate = 142, spo2 = 89, systolicBp = 110, diastolicBp = 65, respiratoryRate = 32, etco2 = 0, rhythmDescription = "Tachycardie Sinusale", isCritical = true),
            timeLimitSeconds = 60,
            availableActions = listOf(
                ClinicalAction(
                    id = "act_larson",
                    title = "Manœuvre de Larson (Pression bilatérale rétro-mandibulaire)",
                    description = "Pression vigoureuse au niveau de l'encoche de Larson derrière le lobe de l'oreille.",
                    category = ActionCategory.AIRWAY_OXYGEN,
                    timeCostSeconds = 10,
                    scoreDelta = 25,
                    isCriticalCorrect = true,
                    rationale = "La manœuvre de Larson stimule le nerf glossopharyngien et lève le spasme réflexe des cordes vocales.",
                    consequenceText = "Le spasme commence à céder partiellement, un flux d'air minimal réapparaît.",
                    nextPhaseId = "p2_cpap",
                    vitalsModifier = { it.copy(spo2 = 91, heartRate = 135) }
                ),
                ClinicalAction(
                    id = "act_o2_mask",
                    title = "Oxygène pur au masque avec FiO2 100% + CPAP légère",
                    description = "Application étanche du masque et fermeture partielle de la valve APL à 15-20 cmH2O.",
                    category = ActionCategory.AIRWAY_OXYGEN,
                    timeCostSeconds = 10,
                    scoreDelta = 20,
                    isCriticalCorrect = true,
                    rationale = "L'O2 à 100% avec pression positive continue (CPAP) force le passage à travers la glotte spastique et maintient l'oxygénation.",
                    consequenceText = "La pression positive permet de limiter la chute de la SpO2.",
                    nextPhaseId = "p2_cpap",
                    vitalsModifier = { it.copy(spo2 = 90) }
                ),
                ClinicalAction(
                    id = "act_force_air",
                    title = "Insufflation manuelle brutale sans lever le spasme",
                    description = "Pression excessive > 40 cmH2O au ballon sans subluxation.",
                    category = ActionCategory.VENTILATION,
                    timeCostSeconds = 15,
                    scoreDelta = -15,
                    isHarmful = true,
                    rationale = "Une insufflation forcée gonfle l'estomac d'air et aggrave le risque de régurgitation bronchique sans ouvrir les cordes vocales fermées.",
                    consequenceText = "Distension gastrique brutale, SpO2 chute à 82% !",
                    nextPhaseId = "p1_critical_hypoxia",
                    vitalsModifier = { it.copy(spo2 = 82, heartRate = 155) }
                )
            ),
            criticalAlertMessage = "ALERTE : Stridor inspiratoire + SpO2 en chute rapide !",
            timeoutPhaseId = "p1_critical_hypoxia"
        )

        val p1Critical = ScenarioPhase(
            phaseId = "p1_critical_hypoxia",
            title = "Phase Critique : Hypoxémie Grave & Bradycardie Vague",
            narrative = "L'enfant devient cyanosé. La SpO2 tombe à 74%. Chez l'enfant hypoxique, le nœud sinusal ralentit brutalement (Bradycardie d'hypoxie).",
            vitals = SimulatedVitals(heartRate = 58, spo2 = 74, systolicBp = 75, diastolicBp = 40, respiratoryRate = 12, etco2 = 0, rhythmDescription = "Bradycardie Hypoxique Sévère", isCritical = true),
            timeLimitSeconds = 40,
            availableActions = listOf(
                ClinicalAction(
                    id = "act_succinyl",
                    title = "Célocurine (Suxaméthonium) 0.5 mg/kg IVD + Atropine",
                    description = "Dose lytique de curare dépolarisant pour lever immédiatement le spasme glottique.",
                    category = ActionCategory.PHARMACO_DRUGS,
                    timeCostSeconds = 15,
                    scoreDelta = 30,
                    isCriticalCorrect = true,
                    rationale = "La dose sub-paralysante de suxaméthonium (0.5 mg/kg) lève le spasme glottique sans paralyser totalement le diaphragme. L'atropine prévient la bradycardie vagale.",
                    consequenceText = "Les cordes vocales s'ouvrent ! La ventilation au masque redevient facile.",
                    nextPhaseId = "p_success_stabilized",
                    vitalsModifier = { it.copy(spo2 = 98, heartRate = 120, systolicBp = 105, isCritical = false) }
                ),
                ClinicalAction(
                    id = "act_propofol_bolus",
                    title = "Bolus de Propofol 1 mg/kg IVD",
                    description = "Approfondissement anesthésique pour relâcher les réflexes laryngés.",
                    category = ActionCategory.PHARMACO_DRUGS,
                    timeCostSeconds = 15,
                    scoreDelta = 20,
                    isCriticalCorrect = true,
                    rationale = "Le propofol possède un fort pouvoir dépresseur des réflexes laryngés et peut suffire à lever le laryngospasme.",
                    consequenceText = "Relâchement du spasme glottique, reprise progressive d'une ventilation efficace.",
                    nextPhaseId = "p_success_stabilized",
                    vitalsModifier = { it.copy(spo2 = 97, heartRate = 110, systolicBp = 95, isCritical = false) }
                ),
                ClinicalAction(
                    id = "act_wait_observe",
                    title = "Attendre que l'enfant se réveille davantage",
                    description = "Stimulation cutanée sans traitement médical.",
                    category = ActionCategory.DIAGNOSTIC_EXAM,
                    timeCostSeconds = 25,
                    scoreDelta = -30,
                    isHarmful = true,
                    rationale = "Un laryngospasme hypoxiante chez l'enfant conduit rapidement à l'asystolie et à l'œdème pulmonaire a vacuo.",
                    consequenceText = "Arrêt respiratoire complet et asystolie !",
                    nextPhaseId = "p_failure_arrest"
                )
            ),
            criticalAlertMessage = "DANGER MORTEL : Bradycardie hypoxique pédiatrique !",
            timeoutPhaseId = "p_failure_arrest"
        )

        val p2Cpap = ScenarioPhase(
            phaseId = "p2_cpap",
            title = "Phase 2 : Évaluation sous Pression Positive Continue",
            narrative = "L'O2 100% avec CPAP 15 cmH2O et manœuvre de Larson ont amélioré le passage. Mais un stridor résiduel persiste et l'enfant lutte.",
            vitals = SimulatedVitals(heartRate = 125, spo2 = 94, systolicBp = 100, diastolicBp = 60, respiratoryRate = 24, etco2 = 38, rhythmDescription = "Tachycardie Sinusale modérée"),
            timeLimitSeconds = 50,
            availableActions = listOf(
                ClinicalAction(
                    id = "act_maintain_cpap",
                    title = "Maintenir FiO2 100% + CPAP jusqu'au réveil calme",
                    category = ActionCategory.AIRWAY_OXYGEN,
                    timeCostSeconds = 20,
                    scoreDelta = 25,
                    isCriticalCorrect = true,
                    rationale = "Le spasme cède spontanément dans 80% des cas si l'oxygénation sous CPAP est maintenue avec calme sans stimulation inutile.",
                    consequenceText = "L'enfant reprend une respiration régulière sans stridor.",
                    nextPhaseId = "p_success_stabilized",
                    vitalsModifier = { it.copy(spo2 = 99, heartRate = 105, isCritical = false) }
                ),
                ClinicalAction(
                    id = "act_reintubate_blind",
                    title = "Réintubation en urgence à l'aveugle sans curare",
                    category = ActionCategory.AIRWAY_OXYGEN,
                    timeCostSeconds = 30,
                    scoreDelta = -20,
                    isHarmful = true,
                    rationale = "Tenter de réintuber de force un patient vigile sans sédation ni myorelaxant aggrave les traumatismes glottiques et le spasme.",
                    consequenceText = "Traumatisme glottique, hémorragie buccale et reprise du spasme !",
                    nextPhaseId = "p1_critical_hypoxia"
                )
            )
        )

        val pSuccess = ScenarioPhase(
            phaseId = "p_success_stabilized",
            title = "Succès : Laryngospasme Levé & Enfant Stabilisé",
            narrative = "Les cordes vocales sont totalement libres. SpO2 à 99% sous 2L d'O2. Aucune séquelle neurologique. Surveillance prolongée de 2h en SSPI pour éliminer un œdème pulmonaire a vacuo.",
            vitals = SimulatedVitals(heartRate = 98, spo2 = 99, systolicBp = 102, diastolicBp = 62, respiratoryRate = 20, etco2 = 40, rhythmDescription = "Rythme Sinusal Régulier", isCritical = false),
            availableActions = emptyList()
        )

        val pFailure = ScenarioPhase(
            phaseId = "p_failure_arrest",
            title = "Échec : Arrêt Cardiorespiratoire Anoxique",
            narrative = "L'hypoxie prolongée a provoqué un arrêt cardiorespiratoire en asystolie. Début immédiat du protocole RCP pédiatrique.",
            vitals = SimulatedVitals(heartRate = 0, spo2 = 40, systolicBp = 0, diastolicBp = 0, respiratoryRate = 0, etco2 = 8, rhythmDescription = "Asystolie", isCritical = true),
            availableActions = emptyList()
        )

        return EscapeRoomScenario(
            id = "scenario_laryngospasm",
            title = "Laryngospasme Aigu au Réveil Pédiatrique",
            subtitle = "Désaturation brutale, stridor et tirage post-extubation",
            category = ScenarioCategory.AIRWAY,
            difficulty = DifficultyLevel.JUNIOR_1ST_YEAR,
            estimatedDurationMinutes = 8,
            patient = PatientBrief(
                age = 6,
                gender = "Garçon",
                weightKg = 22.0,
                heightCm = 115.0,
                programmedSurgery = "Adénoïdectomie + Pose d'aérateurs transtympaniques",
                asaScore = "ASA I",
                allergies = "Aucune allergie connue",
                medicalHistory = "Aucun antécédent particulier. Jeûne préopératoire respecté."
            ),
            initialObservations = "Enfant extubé en phase de réveil intermédiaire (stade II d'anesthésie). Apparition immédiate d'un silence auscultatoire et d'un tirage sus-sternal majeur.",
            learningObjectives = listOf(
                "Identifier immédiatement les signes cliniques du laryngospasme (tirage, stridor, absence de murmure vésiculaire).",
                "Appliquer la manœuvre de Larson et la CPAP à 100% d'oxygène.",
                "Connaître les indications et posologies de la Célocurine (0.5 mg/kg) et du Propofol en sauvetage."
            ),
            initialVitals = SimulatedVitals(heartRate = 142, spo2 = 89, systolicBp = 110, diastolicBp = 65, respiratoryRate = 32, etco2 = 0, rhythmDescription = "Tachycardie Sinusale", isCritical = true),
            phases = mapOf(
                "p1_extubation" to p1,
                "p1_critical_hypoxia" to p1Critical,
                "p2_cpap" to p2Cpap,
                "p_success_stabilized" to pSuccess,
                "p_failure_arrest" to pFailure
            ),
            startPhaseId = "p1_extubation",
            successPhaseIds = setOf("p_success_stabilized"),
            failurePhaseIds = setOf("p_failure_arrest"),
            debriefing = DebriefingContent(
                coreDiagnosis = "Laryngospasme aigu réflexe post-extubation chez le jeune enfant",
                pathophysiologicalSummary = "Fermeture réflexe involontaire des cordes vocales par stimulation sensitive des nerfs laryngés supérieurs au stade II de l'anesthésie, favorisée par des sécrétions ou du sang sur les cordes.",
                criticalSuccessFactors = listOf(
                    "Oxygénation immédiate à 100% au masque hermétique avec valve APL fermée (CPAP 15-20 cmH2O)",
                    "Manœuvre de Larson (pression vigoureuse de l'encoche rétro-mandibulaire)",
                    "Injection précoce de Célocurine 0.5 mg/kg IVD ou Propofol si persistance ou bradycardie",
                    "Surveillance de l'œdème pulmonaire a vacuo secondaire aux efforts inspiratoires contre glotte fermée"
                ),
                fatalPitfalls = listOf(
                    "Insufflation manuelle sous forte pression sans lever le spasme (distension gastrique majeure)",
                    "Attente passive face à une bradycardie pédiatrique d'hypoxie",
                    "Tentative de réintubation traumatique à l'aveugle sans curarisation"
                ),
                clinicalReferences = listOf(
                    "SFAR - Recommandations sur l'extubation et la surveillance en SSPI",
                    "ERC Guidelines - Management of Pediatric Perioperative Respiratory Arrest",
                    "Larson CP. Laryngospasm: the best treatment. Anesthesiology 1998"
                )
            ),
            isVerified = true,
            tags = listOf("Pédiatrie", "Voies Aériennes", "Extubation", "Urgence Vitale")
        )
    }

    /**
     * 2. Choc Anaphylactique Grade III peropératoire aux curares
     */
    private fun createAnaphylaxisScenario(): EscapeRoomScenario {
        val p1 = ScenarioPhase(
            phaseId = "p1_induction",
            title = "Phase 1 : Collapsus & Bronchospasme à l'Induction",
            narrative = "Femme de 38 ans pour appendicectomie. 3 minutes après l'injection de Propofol, Sufentanil et Rocuronium, la pression artérielle s'effondre à 55/30 mmHg. La pression de crête sur le respirateur monte à 44 cmH2O. Tachycardie à 150 bpm.",
            vitals = SimulatedVitals(heartRate = 150, spo2 = 86, systolicBp = 55, diastolicBp = 30, respiratoryRate = 16, etco2 = 14, rhythmDescription = "Tachycardie Sinusale", isCritical = true),
            timeLimitSeconds = 60,
            availableActions = listOf(
                ClinicalAction(
                    id = "act_adre_titrated",
                    title = "Adrénaline IVD titrée (100 à 200 µg) + Appel d'aide immédiat",
                    description = "Dilution à 10 µg/mL ou 100 µg/mL et injection IVD immédiate.",
                    category = ActionCategory.PHARMACO_DRUGS,
                    timeCostSeconds = 15,
                    scoreDelta = 35,
                    isCriticalCorrect = true,
                    rationale = "L'Adrénaline est le seul traitement étiologique et salvateur du choc anaphylactique (vasoconstriction alpha-1, inotropisme bêta-1, bronchodilatation bêta-2 et inhibition de la libération d'histamine).",
                    consequenceText = "Remontée rapide de la pression artérielle et baisse des résistances respiratoires !",
                    nextPhaseId = "p2_resuscitation",
                    vitalsModifier = { it.copy(systolicBp = 90, diastolicBp = 50, spo2 = 92, etco2 = 26) }
                ),
                ClinicalAction(
                    id = "act_ephedrine_only",
                    title = "Éphédrine 6 mg ou Phényléphrine seule",
                    description = "Vasopresseur indirect standard.",
                    category = ActionCategory.PHARMACO_DRUGS,
                    timeCostSeconds = 15,
                    scoreDelta = -15,
                    isHarmful = true,
                    rationale = "L'éphédrine et la phényléphrine sont totalement inefficaces dans le choc anaphylactique sévère de Grade III et retardent l'injection d'adrénaline vitale.",
                    consequenceText = "Aucune remontée tensionnelle ! Le patient s'enfonce dans le choc réfractaire.",
                    nextPhaseId = "p1_shock_worsens",
                    vitalsModifier = { it.copy(systolicBp = 45, diastolicBp = 20, heartRate = 165) }
                ),
                ClinicalAction(
                    id = "act_crystalloids_large",
                    title = "Remplissage vasculaire rapide (Cristalloïdes 20-30 mL/kg en débit libre)",
                    description = "Pose d'une 2ème voie veineuse de gros calibre et flush de Ringer Lactate.",
                    category = ActionCategory.FLUID_HEMODYNAMICS,
                    timeCostSeconds = 15,
                    scoreDelta = 20,
                    isCriticalCorrect = true,
                    rationale = "L'anaphylaxie provoque une fuite capillaire massive et une vasoplégie majeure nécessitant un remplissage massif rapide en complément de l'adrénaline.",
                    consequenceText = "Amélioration du retour veineux vers le cœur.",
                    nextPhaseId = "p2_resuscitation"
                )
            ),
            criticalAlertMessage = "COLLAPSUS MAJEUR : TA 55/30 + Pression de crête 44 cmH2O !",
            timeoutPhaseId = "p1_shock_worsens"
        )

        val p1Worse = ScenarioPhase(
            phaseId = "p1_shock_worsens",
            title = "Phase Critique : Désamorçage Cardiaque & Tachycardie Extrême",
            narrative = "La TA devient imprenable (35/15 mmHg). Tracé ECG en tachycardie ventriculaire polymorphe d'ischémie. Le temps est compté avant l'arrêt !",
            vitals = SimulatedVitals(heartRate = 175, spo2 = 78, systolicBp = 35, diastolicBp = 15, respiratoryRate = 12, etco2 = 8, rhythmDescription = "Tachycardie Extrême / TV imminente", isCritical = true),
            timeLimitSeconds = 40,
            availableActions = listOf(
                ClinicalAction(
                    id = "act_adre_bolus_urgent",
                    title = "Adrénaline 0.5 mg à 1 mg IV directe + Flush de Sérum",
                    category = ActionCategory.PHARMACO_DRUGS,
                    timeCostSeconds = 10,
                    scoreDelta = 30,
                    isCriticalCorrect = true,
                    rationale = "Devant un collapsus imminent d'anaphylaxie avec désamorçage, la dose d'adrénaline doit être majorée pour éviter l'arrêt cardiaque.",
                    consequenceText = "Le cœur réagit ! Reprise d'une onde de pouls palpable et PA à 80/45 mmHg.",
                    nextPhaseId = "p2_resuscitation",
                    vitalsModifier = { it.copy(systolicBp = 80, diastolicBp = 45, spo2 = 89, etco2 = 22) }
                ),
                ClinicalAction(
                    id = "act_corticoids_only",
                    title = "Solumédrol 120 mg seul sans adrénaline",
                    category = ActionCategory.PHARMACO_DRUGS,
                    timeCostSeconds = 25,
                    scoreDelta = -30,
                    isHarmful = true,
                    rationale = "Les corticoïdes n'ont aucun effet hémodynamique immédiat (délai d'action 4 à 6 heures). Compter sur eux dans l'urgence immédiate est fatal.",
                    consequenceText = "Arrêt cardiorespiratoire en désamorçage !",
                    nextPhaseId = "p_failure_arrest"
                )
            ),
            criticalAlertMessage = "URGENCE ABSOLUE : Arrêt cardiaque imminent !",
            timeoutPhaseId = "p_failure_arrest"
        )

        val p2Resuscitation = ScenarioPhase(
            phaseId = "p2_resuscitation",
            title = "Phase 2 : Optimisation & Relais Thérapeutique",
            narrative = "La PA est remontée à 95/55 mmHg grâce à l'adrénaline. Des plaques érythémateuses et un œdème palpébral apparaissent sur le visage et le thorax. Il faut consolider la stabilisation et préparer le bilan.",
            vitals = SimulatedVitals(heartRate = 118, spo2 = 95, systolicBp = 95, diastolicBp = 55, respiratoryRate = 16, etco2 = 32, rhythmDescription = "Rythme Sinusal Tachycarde"),
            timeLimitSeconds = 60,
            availableActions = listOf(
                ClinicalAction(
                    id = "act_adre_infusion",
                    title = "Mise en route Adrénaline continue à la seringue électrique (0.1 à 0.5 µg/kg/min)",
                    category = ActionCategory.PHARMACO_DRUGS,
                    timeCostSeconds = 20,
                    scoreDelta = 25,
                    isCriticalCorrect = true,
                    rationale = "L'anaphylaxie de Grade III nécessite une perfusion continue d'adrénaline pour éviter le rebond vasoplégique à l'arrêt des bolus.",
                    consequenceText = "Hémodynamique parfaitement stabilisée.",
                    nextPhaseId = "p_success_stabilized",
                    vitalsModifier = { it.copy(systolicBp = 115, diastolicBp = 70, spo2 = 98, isCritical = false) }
                ),
                ClinicalAction(
                    id = "act_tryptase_lab",
                    title = "Prescription du prélèvement de Tryptase sérique (H1 et H2)",
                    category = ActionCategory.DIAGNOSTIC_EXAM,
                    timeCostSeconds = 15,
                    scoreDelta = 15,
                    isCriticalCorrect = true,
                    rationale = "Le dosage de la tryptase sérique entre 30 min et 2h après le début des signes confirme le diagnostic biologique de choc anaphylactique.",
                    consequenceText = "Tubes prélevés et datés pour l'allergologie.",
                    nextPhaseId = "p_success_stabilized"
                )
            )
        )

        val pSuccess = ScenarioPhase(
            phaseId = "p_success_stabilized",
            title = "Succès : Choc Anaphylactique Contrôlé",
            narrative = "Patient stabilisé avec succès sous adrénaline continue et remplissage. Chirurgie reportée. Transfert en Réanimation et déclaration en pharmacovigilance avec consultation d'allergologie programmée.",
            vitals = SimulatedVitals(heartRate = 92, spo2 = 99, systolicBp = 118, diastolicBp = 72, respiratoryRate = 16, etco2 = 38, rhythmDescription = "Rythme Sinusal Régulier", isCritical = false),
            availableActions = emptyList()
        )

        val pFailure = ScenarioPhase(
            phaseId = "p_failure_arrest",
            title = "Échec : Arrêt Cardiorespiratoire Répondeur Négatif",
            narrative = "Désamorçage cardiorespiratoire irréversible par défaut d'adrénaline précoce. Début de la réanimation cardiopulmonaire lourde.",
            vitals = SimulatedVitals(heartRate = 0, spo2 = 30, systolicBp = 0, diastolicBp = 0, respiratoryRate = 0, etco2 = 6, rhythmDescription = "Asystolie", isCritical = true),
            availableActions = emptyList()
        )

        return EscapeRoomScenario(
            id = "scenario_anaphylaxis",
            title = "Choc Anaphylactique Grade III aux Curares",
            subtitle = "Collapsus cardiovasculaire et bronchospasme immédiat à l'induction",
            category = ScenarioCategory.ANAPHYLAXIS,
            difficulty = DifficultyLevel.INTERMEDIATE_2ND_YEAR,
            estimatedDurationMinutes = 10,
            patient = PatientBrief(
                age = 38,
                gender = "Femme",
                weightKg = 64.0,
                heightCm = 168.0,
                programmedSurgery = "Appendicectomie coelioscopique",
                asaScore = "ASA I",
                allergies = "Pas d'allergie médicamenteuse connue",
                medicalHistory = "Aucun antécédent médical ou chirurgical."
            ),
            initialObservations = "Effondrement tensionnel brutal et pic de pression d'insufflation 2 minutes après curarisation au Rocuronium.",
            learningObjectives = listOf(
                "Reconnaître le choc anaphylactique de Grade III per-anesthésique.",
                "Administrer l'Adrénaline IV titrée sans aucun retard.",
                "Associer un remplissage vasculaire massif et maintenir la perfusion continue.",
                "Initier le bilan biologique d'anaphylaxie (Tryptase sérique H1 et H2)."
            ),
            initialVitals = SimulatedVitals(heartRate = 150, spo2 = 86, systolicBp = 55, diastolicBp = 30, respiratoryRate = 16, etco2 = 14, rhythmDescription = "Tachycardie Sinusale", isCritical = true),
            phases = mapOf(
                "p1_induction" to p1,
                "p1_shock_worsens" to p1Worse,
                "p2_resuscitation" to p2Resuscitation,
                "p_success_stabilized" to pSuccess,
                "p_failure_arrest" to pFailure
            ),
            startPhaseId = "p1_induction",
            successPhaseIds = setOf("p_success_stabilized"),
            failurePhaseIds = setOf("p_failure_arrest"),
            debriefing = DebriefingContent(
                coreDiagnosis = "Choc anaphylactique IgE-dépendant de Grade III induit par les curares (Rocuronium)",
                pathophysiologicalSummary = "Dégranulation mastocytaire massive libérant histamine, tryptase et leucotriènes, responsable d'une vasoplégie artérielle et veineuse avec fuite capillaire aiguë et bronchospasme.",
                criticalSuccessFactors = listOf(
                    "Adrénaline IV immédiate (bolus répétés de 100 à 200 µg puis perfusion continue)",
                    "Arrêt de tout produit suspect et appel de renforts",
                    "Remplissage vasculaire massif par cristalloïdes isotoniques",
                    "Ventilation en FiO2 100% avec monitorage de l'EtCO2 pour évaluer le débit cardiaque",
                    "Prélèvement de Tryptase sérique à H1 et H2 post-réaction"
                ),
                fatalPitfalls = listOf(
                    "Retarder l'adrénaline au profit de corticoïdes ou d'éphédrine inefficaces",
                    "Sous-doser le remplissage vasculaire dans la phase initiale de vasoplégie",
                    "Omettre le relais par perfusion continue d'adrénaline"
                ),
                clinicalReferences = listOf(
                    "SFAR / SFA - Recommandations sur le choc anaphylactique peropératoire 2021",
                    "Mertes PM et al. Reducing the risk of anaphylaxis during anaesthesia. Br J Anaesth 2019",
                    "Ring J, Messmer K. Incidence and severity of anaphylactoid reactions. Lancet 1977"
                )
            ),
            isVerified = true,
            tags = listOf("Anaphylaxie", "Curares", "Adrénaline", "Hémodynamique")
        )
    }

    /**
     * 3. Hyperthermie Maligne peropératoire
     */
    private fun createMalignantHyperthermiaScenario(): EscapeRoomScenario {
        val p1 = ScenarioPhase(
            phaseId = "p1_hypercapnia",
            title = "Phase 1 : Hypercapnie Inexpliquée & Tachycardie",
            narrative = "Homme de 24 ans sous Sévoflurane pour ostéosynthèse du fémur. 30 minutes après le début, l'EtCO2 grimpe rapidement de 36 à 72 mmHg malgré l'augmentation de la ventilation minute. Tachycardie à 135 bpm. Rigidité musculaire généralisée du masséter et du tronc.",
            vitals = SimulatedVitals(heartRate = 135, spo2 = 94, systolicBp = 145, diastolicBp = 95, respiratoryRate = 22, etco2 = 72, temperature = 38.6, rhythmDescription = "Tachycardie Sinusale / Extrasystoles", isCritical = true),
            timeLimitSeconds = 60,
            availableActions = listOf(
                ClinicalAction(
                    id = "act_stop_halogen",
                    title = "Arrêt immédiat du Sévoflurane + Hyperventilation en FiO2 100%",
                    description = "Couper l'évaporateur d'halogéné, débit de gaz frais au maximum (15 L/min).",
                    category = ActionCategory.VENTILATION,
                    timeCostSeconds = 15,
                    scoreDelta = 35,
                    isCriticalCorrect = true,
                    rationale = "L'arrêt immédiat de l'agent gâchette (halogéné) et le flush des circuits à fort débit est la mesure d'extrême urgence n°1.",
                    consequenceText = "Agent volatil coupé, épuration du circuit lancée.",
                    nextPhaseId = "p2_dantrolene",
                    vitalsModifier = { it.copy(spo2 = 97) }
                ),
                ClinicalAction(
                    id = "act_increase_curare",
                    title = "Réinjecter un curare pour lever la rigidité",
                    description = "Injection supplémentaire de curare.",
                    category = ActionCategory.PHARMACO_DRUGS,
                    timeCostSeconds = 15,
                    scoreDelta = -25,
                    isHarmful = true,
                    rationale = "La rigidité musculaire de l'hyperthermie maligne est d'origine intracellulaire (récepteur RYR1) et ne répond absolument pas aux curares.",
                    consequenceText = "Rigidité inchangée ! Perte de temps critique, la température monte à 39.5°C.",
                    nextPhaseId = "p1_critical_temp",
                    vitalsModifier = { it.copy(temperature = 39.5, etco2 = 85, heartRate = 150) }
                ),
                ClinicalAction(
                    id = "act_call_help_kit",
                    title = "Appel à l'aide + Demande du Chariot d'Hyperthermie Maligne (Dantrolène)",
                    category = ActionCategory.CALL_ASSISTANCE,
                    timeCostSeconds = 10,
                    scoreDelta = 20,
                    isCriticalCorrect = true,
                    rationale = "La préparation du Dantrolène (Dantrium) nécessite plusieurs soignants pour reconstituer rapidement les flacons.",
                    consequenceText = "Équipe prévenue, chariot Dantrolène amené en salle.",
                    nextPhaseId = "p2_dantrolene"
                )
            ),
            criticalAlertMessage = "HYPERCAPNIE MAJEURE : EtCO2 72 mmHg + Rigidité musculaire !",
            timeoutPhaseId = "p1_critical_temp"
        )

        val p1Critical = ScenarioPhase(
            phaseId = "p1_critical_temp",
            title = "Phase Critique : Hyperthermie Fulminante & Rhabdomyolyse",
            narrative = "Température à 40.8°C. Urines couleur 'coca-cola' (myoglobinurie). Tracé ECG avec ondes T pointues symétriques (Hyperkaliémie menaçante).",
            vitals = SimulatedVitals(heartRate = 160, spo2 = 88, systolicBp = 90, diastolicBp = 50, respiratoryRate = 28, etco2 = 90, temperature = 40.8, rhythmDescription = "Tachycardie Sinusale + Ondes T amples", isCritical = true),
            timeLimitSeconds = 40,
            availableActions = listOf(
                ClinicalAction(
                    id = "act_dantrolene_bolus_urgent",
                    title = "Dantrolène 2.5 mg/kg IVD direct + Chlorure de Calcium IV",
                    category = ActionCategory.PHARMACO_DRUGS,
                    timeCostSeconds = 20,
                    scoreDelta = 35,
                    isCriticalCorrect = true,
                    rationale = "Le Dantrolène bloque la libération de calcium par les récepteurs RYR1. Le calcium antagonise l'effet cardiotoxique de l'hyperkaliémie.",
                    consequenceText = "Chute rapide de l'EtCO2 et début de normalisation métabolique !",
                    nextPhaseId = "p_success_stabilized",
                    vitalsModifier = { it.copy(temperature = 38.5, etco2 = 45, heartRate = 115, systolicBp = 110, isCritical = false) }
                ),
                ClinicalAction(
                    id = "act_paracetamol",
                    title = "Perfalgan (Paracétamol) 1g IVD",
                    category = ActionCategory.PHARMACO_DRUGS,
                    timeCostSeconds = 15,
                    scoreDelta = -30,
                    isHarmful = true,
                    rationale = "Le paracétamol agit sur l'hypothalamus et n'a aucune efficacité sur l'hyperthermie périphérique musculaire d'HM.",
                    consequenceText = "Inefficace ! Fibrillation ventriculaire sur hyperkaliémie aiguë !",
                    nextPhaseId = "p_failure_arrest"
                )
            ),
            criticalAlertMessage = "DANGER MORTEL : Hyperthermie 40.8°C + Hyperkaliémie menaçante !",
            timeoutPhaseId = "p_failure_arrest"
        )

        val p2Dantrolene = ScenarioPhase(
            phaseId = "p2_dantrolene",
            title = "Phase 2 : Administration du Dantrolène & Refroidissement",
            narrative = "L'halogéné a été arrêté et le filtre charbon posé. L'équipe prépare l'antidote spécifique.",
            vitals = SimulatedVitals(heartRate = 128, spo2 = 96, systolicBp = 130, diastolicBp = 85, respiratoryRate = 20, etco2 = 64, temperature = 39.1, rhythmDescription = "Tachycardie Sinusale"),
            timeLimitSeconds = 60,
            availableActions = listOf(
                ClinicalAction(
                    id = "act_inject_dantrolene",
                    title = "Injection de Dantrolène IVD (Dose initiale : 2.5 mg/kg)",
                    category = ActionCategory.PHARMACO_DRUGS,
                    timeCostSeconds = 20,
                    scoreDelta = 30,
                    isCriticalCorrect = true,
                    rationale = "Dose initiale de Dantrolène 2.5 mg/kg à répéter toutes les 5 à 10 minutes jusqu'à arrêt des signes d'hypercatabolisme (baisse EtCO2 et FC).",
                    consequenceText = "EtCO2 chute à 40 mmHg, relâchement musculaire visible !",
                    nextPhaseId = "p_success_stabilized",
                    vitalsModifier = { it.copy(temperature = 37.8, etco2 = 40, heartRate = 95, isCritical = false) }
                ),
                ClinicalAction(
                    id = "act_cooling_measures",
                    title = "Refroidissement externe (Glace aux creux axillaires/inguinaux + Cristalloïdes froids)",
                    category = ActionCategory.FLUID_HEMODYNAMICS,
                    timeCostSeconds = 15,
                    scoreDelta = 20,
                    isCriticalCorrect = true,
                    rationale = "Le refroidissement externe et le sérum froid à 4°C limitent l'élévation thermique cytotoxique.",
                    consequenceText = "Température stabilisée.",
                    nextPhaseId = "p_success_stabilized"
                )
            )
        )

        val pSuccess = ScenarioPhase(
            phaseId = "p_success_stabilized",
            title = "Succès : Crise d'Hyperthermie Maligne Maîtrisée",
            narrative = "L'hypercatabolisme musculaire a été enrayé par le Dantrolène. Température à 37.6°C, EtCO2 normalisé. Surveillance continue 48h en réanimation avec surveillance de la rhabdomyolyse (CPK, diurèse alcaline).",
            vitals = SimulatedVitals(heartRate = 88, spo2 = 99, systolicBp = 120, diastolicBp = 75, respiratoryRate = 15, etco2 = 38, temperature = 37.6, rhythmDescription = "Rythme Sinusal Régulier", isCritical = false),
            availableActions = emptyList()
        )

        val pFailure = ScenarioPhase(
            phaseId = "p_failure_arrest",
            title = "Échec : Arrêt Cardiaque Hyperkaliémique",
            narrative = "Arrêt cardiorespiratoire réfractaire par hyperkaliémie massive consécutive à la destruction musculaire.",
            vitals = SimulatedVitals(heartRate = 0, spo2 = 35, systolicBp = 0, diastolicBp = 0, respiratoryRate = 0, etco2 = 8, rhythmDescription = "Fibrillation Ventriculaire / Asystolie", isCritical = true),
            availableActions = emptyList()
        )

        return EscapeRoomScenario(
            id = "scenario_malignant_hyperthermia",
            title = "Hyperthermie Maligne Peropératoire",
            subtitle = "Hypercapnie inexpliquée, tachycardie et rigidité sous anesthésie générale",
            category = ScenarioCategory.MALIGNANT_HYPERTHERMIA,
            difficulty = DifficultyLevel.SENIOR_3RD_YEAR,
            estimatedDurationMinutes = 12,
            patient = PatientBrief(
                age = 24,
                gender = "Homme",
                weightKg = 76.0,
                heightCm = 180.0,
                programmedSurgery = "Ostéosynthèse du fémur après accident de la voie publique",
                asaScore = "ASA I-U",
                allergies = "Aucune allergie connue",
                medicalHistory = "Aucun antécédent anesthésique personnel. Notion de décès inexpliqué au bloc opératoire chez un cousin au 2ème degré."
            ),
            initialObservations = "30 minutes après induction avec Célocurine et entretien sous Sévoflurane : ascension foudroyante de l'EtCO2 et raideur du masséter.",
            learningObjectives = listOf(
                "Identifier l'hypercapnie exponentielle inexpliquée comme signe princeps d'hyperthermie maligne.",
                "Stopper immédiatement tous les halogénés et hyperventiler en FiO2 100%.",
                "Administrer sans délai le Dantrolène IV (2.5 mg/kg) et traiter l'hyperkaliémie.",
                "Débuter le refroidissement et organiser le suivi réanimatoire."
            ),
            initialVitals = SimulatedVitals(heartRate = 135, spo2 = 94, systolicBp = 145, diastolicBp = 95, respiratoryRate = 22, etco2 = 72, temperature = 38.6, rhythmDescription = "Tachycardie Sinusale / Extrasystoles", isCritical = true),
            phases = mapOf(
                "p1_hypercapnia" to p1,
                "p1_critical_temp" to p1Critical,
                "p2_dantrolene" to p2Dantrolene,
                "p_success_stabilized" to pSuccess,
                "p_failure_arrest" to pFailure
            ),
            startPhaseId = "p1_hypercapnia",
            successPhaseIds = setOf("p_success_stabilized"),
            failurePhaseIds = setOf("p_failure_arrest"),
            debriefing = DebriefingContent(
                coreDiagnosis = "Crise d'Hyperthermie Maligne peropératoire déclenchée par les halogénés (Sévoflurane)",
                pathophysiologicalSummary = "Anomalie génétique autosomique dominante des canaux calciques du réticulum sarcoplasmique (mutation RYR1), provoquant une libération massive et incontrôlée de calcium avec hypercatabolisme musculaire foudroyant.",
                criticalSuccessFactors = listOf(
                    "Arrêt immédiat de l'halogéné et changement/rinçage du circuit à 10-15 L/min d'O2 pur",
                    "Injection de Dantrolène (Dantrium) à 2.5 mg/kg IVD sans délai",
                    "Hyperventilation minute pour éliminer l'excès de CO2",
                    "Traitement énergique de l'hyperkaliémie (Sérum glucosé + insuline, chlorure de calcium)",
                    "Refroidissement actif externe et interne"
                ),
                fatalPitfalls = listOf(
                    "Attendre l'hyperthermie pour poser le diagnostic (l'hyperthermie est un signe tardif)",
                    "Administrer un antagoniste calcique (vérapamil/diltiazem) avec le dantrolène (collapsus mortel)",
                    "Réinjecter un curare pour tenter de lever la rigidité musculaire périphérique"
                ),
                clinicalReferences = listOf(
                    "EMHG (European Malignant Hyperthermia Group) Guidelines for Malignant Hyperthermia 2020",
                    "SFAR - Prise en charge de la crise d'Hyperthermie Maligne",
                    "Rosenberg H et al. Malignant hyperthermia. Orphanet J Rare Dis 2015"
                )
            ),
            isVerified = true,
            tags = listOf("Hyperthermie Maligne", "Dantrolène", "Halogénés", "Génétique", "Bloc Opératoire")
        )
    }

    /**
     * 4. Intubation sélective & Hypoxémie aiguë réfractaire
     */
    private fun createSelectiveIntubationScenario(): EscapeRoomScenario {
        val p1 = ScenarioPhase(
            phaseId = "p1_desat",
            title = "Phase 1 : Désaturation & Asymétrie Auscultatoire",
            narrative = "Après installation en décubitus latéral pour lobectomie pulmonaire, la SpO2 passe brutalement de 98% à 84%. Les pressions de crête augmentent à 38 cmH2O. L'auscultation montre un murmure présent uniquement à droite, quasi absent à gauche.",
            vitals = SimulatedVitals(heartRate = 120, spo2 = 84, systolicBp = 105, diastolicBp = 65, respiratoryRate = 16, etco2 = 28, rhythmDescription = "Tachycardie Sinusale", isCritical = true),
            timeLimitSeconds = 60,
            availableActions = listOf(
                ClinicalAction(
                    id = "act_check_depth_withdraw",
                    title = "Vérifier la profondeur de la sonde et reculer de 2 à 3 cm sous auscultation",
                    description = "Déballonner prudemment, reculer le tube jusqu'à symétrie auscultatoire bilatérale.",
                    category = ActionCategory.AIRWAY_OXYGEN,
                    timeCostSeconds = 15,
                    scoreDelta = 35,
                    isCriticalCorrect = true,
                    rationale = "Le changement de position (décubitus latéral ou flexion de tête) mobilise fréquemment la sonde dans la bronche souche droite. Le recul sous contrôle auscultatoire bilatéral repositionne le tube dans la trachée.",
                    consequenceText = "Le murmure vésiculaire redevient symétrique, la SpO2 remonte à 97% !",
                    nextPhaseId = "p_success_stabilized",
                    vitalsModifier = { it.copy(spo2 = 97, heartRate = 98, isCritical = false) }
                ),
                ClinicalAction(
                    id = "act_increase_peep_high",
                    title = "Augmenter la PEP à 15 cmH2O sans vérifier le tube",
                    category = ActionCategory.VENTILATION,
                    timeCostSeconds = 20,
                    scoreDelta = -20,
                    isHarmful = true,
                    rationale = "Appliquer une PEP élevée sur un seul poumon intubé sélectivement expose à un risque majeur de barotraumatisme et pneumothorax compressif.",
                    consequenceText = "Pression de crête à 48 cmH2O ! Chute tensionnelle par gêne au retour veineux.",
                    nextPhaseId = "p1_pneumothorax",
                    vitalsModifier = { it.copy(spo2 = 79, systolicBp = 70, diastolicBp = 40) }
                ),
                ClinicalAction(
                    id = "act_broncho_fibroscopy",
                    title = "Fibroscopie bronchique pour contrôle visuel de la carène",
                    category = ActionCategory.DIAGNOSTIC_EXAM,
                    timeCostSeconds = 25,
                    scoreDelta = 25,
                    isCriticalCorrect = true,
                    rationale = "La fibroscopie bronchique est le gold standard pour certifier que l'extrémité distale du tube est située à 3-4 cm au-dessus de la carène.",
                    consequenceText = "La caméra visualise l'enclavement dans la bronche droite. Tube replacé correctement.",
                    nextPhaseId = "p_success_stabilized",
                    vitalsModifier = { it.copy(spo2 = 98, heartRate = 92, isCritical = false) }
                )
            ),
            criticalAlertMessage = "ASYMÉTRIE AUSCULTATOIRE : SpO2 84% + Pression de crête 38 cmH2O !",
            timeoutPhaseId = "p1_pneumothorax"
        )

        val pPneumo = ScenarioPhase(
            phaseId = "p1_pneumothorax",
            title = "Phase Critique : Pneumothorax sous Tension / Choc Obstructif",
            narrative = "La pression excessive sur la bronche souche a provoqué un pneumothorax compressif unilatéral. Emphysème sous-cutané cervical, turgescence jugulaire et effondrement de la PA.",
            vitals = SimulatedVitals(heartRate = 145, spo2 = 74, systolicBp = 50, diastolicBp = 30, respiratoryRate = 18, etco2 = 12, rhythmDescription = "Tachycardie Sinusale / Pouls Paradoxal", isCritical = true),
            timeLimitSeconds = 40,
            availableActions = listOf(
                ClinicalAction(
                    id = "act_needle_decompression",
                    title = "Décompression à l'aiguille (2ème espace intercostal médio-claviculaire)",
                    category = ActionCategory.AIRWAY_OXYGEN,
                    timeCostSeconds = 15,
                    scoreDelta = 35,
                    isCriticalCorrect = true,
                    rationale = "La décompression immédiate à l'aiguille évacue l'air sous tension et lève le choc obstructif salvateur.",
                    consequenceText = "Bruit d'air sous pression qui s'échappe ! La pression artérielle remonte immédiatement.",
                    nextPhaseId = "p_success_stabilized",
                    vitalsModifier = { it.copy(systolicBp = 100, diastolicBp = 60, spo2 = 93, isCritical = false) }
                ),
                ClinicalAction(
                    id = "act_wait_chest_xray",
                    title = "Attendre la radiographie pulmonaire au lit",
                    category = ActionCategory.DIAGNOSTIC_EXAM,
                    timeCostSeconds = 30,
                    scoreDelta = -30,
                    isHarmful = true,
                    rationale = "Le pneumothorax suffocant sous tension est un diagnostic purement clinique qui ne doit JAMAIS attendre de confirmation radiologique.",
                    consequenceText = "Arrêt cardiaque hypoxique et obstructif !",
                    nextPhaseId = "p_failure_arrest"
                )
            ),
            criticalAlertMessage = "CHOC OBSTRUCTIF : Décompression pleurale immédiate requise !",
            timeoutPhaseId = "p_failure_arrest"
        )

        val pSuccess = ScenarioPhase(
            phaseId = "p_success_stabilized",
            title = "Succès : Positionnement Trachéal Rétabli",
            narrative = "La sonde est repositionnée à 21 cm de l'arcade dentaire, au-dessus de la carène. Murmure vésiculaire bilatéral parfaitement symétrique, SpO2 99%, pressions ventilatoires normalisées.",
            vitals = SimulatedVitals(heartRate = 84, spo2 = 99, systolicBp = 115, diastolicBp = 72, respiratoryRate = 14, etco2 = 36, rhythmDescription = "Rythme Sinusal Régulier", isCritical = false),
            availableActions = emptyList()
        )

        val pFailure = ScenarioPhase(
            phaseId = "p_failure_arrest",
            title = "Échec : Arrêt Cardiorespiratoire Anoxique & Barotraumatisme",
            narrative = "Désamorçage cardiaque complet sur pneumothorax suffocant non décomprimé.",
            vitals = SimulatedVitals(heartRate = 0, spo2 = 30, systolicBp = 0, diastolicBp = 0, respiratoryRate = 0, etco2 = 6, rhythmDescription = "Dissociation Électromécanique", isCritical = true),
            availableActions = emptyList()
        )

        return EscapeRoomScenario(
            id = "scenario_selective_intubation",
            title = "Intubation Sélective & Hypoxémie Réfractaire",
            subtitle = "Désaturation aiguë et asymétrie auscultatoire après changement de position",
            category = ScenarioCategory.HYPOXEMIA,
            difficulty = DifficultyLevel.JUNIOR_1ST_YEAR,
            estimatedDurationMinutes = 8,
            patient = PatientBrief(
                age = 52,
                gender = "Femme",
                weightKg = 60.0,
                heightCm = 162.0,
                programmedSurgery = "Lobectomie pulmonaire droite",
                asaScore = "ASA II",
                allergies = "Aucune",
                medicalHistory = "Tabagisme sevré, BPCO légère."
            ),
            initialObservations = "Installation en décubitus latéral gauche : chute rapide de la SpO2 et hausse des pressions d'insufflation.",
            learningObjectives = listOf(
                "Identifier l'intubation sélective comme cause fréquente de désaturation peropératoire.",
                "Savoir ausculter systématiquement les deux champs pulmonaires après tout changement de position.",
                "Maîtriser le recul du tube endotrachéal et le risque de barotraumatisme."
            ),
            initialVitals = SimulatedVitals(heartRate = 120, spo2 = 84, systolicBp = 105, diastolicBp = 65, respiratoryRate = 16, etco2 = 28, rhythmDescription = "Tachycardie Sinusale", isCritical = true),
            phases = mapOf(
                "p1_desat" to p1,
                "p1_pneumothorax" to pPneumo,
                "p_success_stabilized" to pSuccess,
                "p_failure_arrest" to pFailure
            ),
            startPhaseId = "p1_desat",
            successPhaseIds = setOf("p_success_stabilized"),
            failurePhaseIds = setOf("p_failure_arrest"),
            debriefing = DebriefingContent(
                coreDiagnosis = "Intubation bronchique sélective droite secondaire au changement de positionnement chirurgical",
                pathophysiologicalSummary = "L'anatomie trachéo-bronchique fait que la bronche souche droite est plus large et plus verticale. Lors de la mobilisation ou de la flexion cervicale, le tube migre préférentiellement à droite, excluant le poumon gauche qui s'atélectasie.",
                criticalSuccessFactors = listOf(
                    "Auscultation systématique bilatérale après toute mobilisation du patient",
                    "Recul prudent de la sonde de 2 à 3 cm sous auscultation continue",
                    "Contrôle fibroscopique de la position de la carène",
                    "Ne jamais forcer la PEP sur un poumon unique sous risque de pneumothorax sous tension"
                ),
                fatalPitfalls = listOf(
                    "Augmenter aveuglément les pressions de ventilation sans vérifier la position de la sonde",
                    "Retarder la prise en charge d'un pneumothorax compressif en attendant une radiographie",
                    "Négliger la surveillance auscultatoire lors des bascules de table"
                ),
                clinicalReferences = listOf(
                    "SFAR - Prise en charge des voies aériennes en anesthésie de l'adulte",
                    "ASA Difficult Airway Algorithm & Intraoperative Crisis Management",
                    "Benumof JL. Anesthesia for Thoracic Surgery. 2nd edition"
                )
            ),
            isVerified = true,
            tags = listOf("Ventilation", "Intubation", "Positionnement", "Thoracique")
        )
    }

    /**
     * 5. Choc Hémorragique Aigu Peropératoire
     */
    private fun createHemorrhagicShockScenario(): EscapeRoomScenario {
        val p1 = ScenarioPhase(
            phaseId = "p1_bleeding",
            title = "Phase 1 : Saignement Cataclysmique & Hypotension",
            narrative = "Patiente de 32 ans pour césarienne en urgence sur placenta praevia recouvrant. Rupture vasculaire brutale à l'incision : 1500 mL de sang aspirés en 2 minutes. La PA chute à 60/35 mmHg, le pouls grimpe à 145 bpm. Le chirurgien demande de l'aide immédiate pour l'hémostase.",
            vitals = SimulatedVitals(heartRate = 145, spo2 = 91, systolicBp = 60, diastolicBp = 35, respiratoryRate = 24, etco2 = 18, rhythmDescription = "Tachycardie Sinusale Prononcée", isCritical = true),
            timeLimitSeconds = 60,
            availableActions = listOf(
                ClinicalAction(
                    id = "act_massive_transfusion_call",
                    title = "Déclencher le Protocole de Transfusion Massive (CGR + PFC ratio 1:1) + Appel renfort",
                    category = ActionCategory.CALL_ASSISTANCE,
                    timeCostSeconds = 15,
                    scoreDelta = 35,
                    isCriticalCorrect = true,
                    rationale = "Dans l'hémorragie obstétricale massive, l'activation précoce du protocole de transfusion massive avec un ratio équilibré CGR/PFC (1:1 ou 4 CGR / 4 PFC) prévient la coagulopathie d'épuisement.",
                    consequenceText = "Banque du sang alertée, pack d'urgence vitale en cours d'acheminement.",
                    nextPhaseId = "p2_hemostasis",
                    vitalsModifier = { it.copy(systolicBp = 75, diastolicBp = 45) }
                ),
                ClinicalAction(
                    id = "act_tranexamic_acid",
                    title = "Acide Tranexamique (Exacyl) 1g IV lent",
                    category = ActionCategory.PHARMACO_DRUGS,
                    timeCostSeconds = 10,
                    scoreDelta = 25,
                    isCriticalCorrect = true,
                    rationale = "L'acide tranexamique précoce (dans les 3 heures) réduit significativement la mortalité par saignement obstétrical ou traumatique (étude WOMAN / CRASH-2).",
                    consequenceText = "Antifibrinolytique administré avec succès.",
                    nextPhaseId = "p2_hemostasis"
                ),
                ClinicalAction(
                    id = "act_excessive_crystalloids",
                    title = "Remplissage exclusif par 4 Litres de Sérum Physiologique 0.9%",
                    category = ActionCategory.FLUID_HEMODYNAMICS,
                    timeCostSeconds = 20,
                    scoreDelta = -20,
                    isHarmful = true,
                    rationale = "L'apport massif de cristalloïdes sans produits sanguins dilue les facteurs de coagulation et les plaquettes (coagulopathie de dilution), aggrave l'acidose hyperchlorémique et l'hypothermie.",
                    consequenceText = "Saignement en nappe aggravé par dilution des facteurs de coagulation !",
                    nextPhaseId = "p1_critical_collapse",
                    vitalsModifier = { it.copy(systolicBp = 50, diastolicBp = 25, heartRate = 160) }
                )
            ),
            criticalAlertMessage = "HÉMORRAGIE MASSIVE : Perte de 1.5L + Choc hypovolémique !",
            timeoutPhaseId = "p1_critical_collapse"
        )

        val pCritical = ScenarioPhase(
            phaseId = "p1_critical_collapse",
            title = "Phase Critique : Triade Létale (Hypothermie, Acidose, Coagulopathie)",
            narrative = "La température baisse à 35.1°C. Le sang ne coagule plus du tout dans le champ chirurgical. Collapsus profond 45/20 mmHg.",
            vitals = SimulatedVitals(heartRate = 160, spo2 = 82, systolicBp = 45, diastolicBp = 20, respiratoryRate = 28, etco2 = 12, temperature = 35.1, rhythmDescription = "Tachycardie Sinusale / Ondes amples d'ischémie", isCritical = true),
            timeLimitSeconds = 40,
            availableActions = listOf(
                ClinicalAction(
                    id = "act_noradrenalin_rapid_transfusion",
                    title = "Noradrénaline continue + Accélérateur de perfusion réchauffeur (CGR O négatif)",
                    category = ActionCategory.PHARMACO_DRUGS,
                    timeCostSeconds = 15,
                    scoreDelta = 35,
                    isCriticalCorrect = true,
                    rationale = "La noradrénaline restaure la pression de perfusion coronaire et cérébrale pendant la transfusion de sauvetage au réchauffeur rapide.",
                    consequenceText = "Remontée de la PAM > 65 mmHg, transfusion efficace sans hypothermie.",
                    nextPhaseId = "p_success_stabilized",
                    vitalsModifier = { it.copy(systolicBp = 95, diastolicBp = 55, spo2 = 96, isCritical = false) }
                ),
                ClinicalAction(
                    id = "act_vasopressin_alone",
                    title = "Attendre le résultat du bilan hémostase sans transfuser",
                    category = ActionCategory.DIAGNOSTIC_EXAM,
                    timeCostSeconds = 30,
                    scoreDelta = -30,
                    isHarmful = true,
                    rationale = "Dans le choc hémorragique décompensé, toute attente d'un bilan de laboratoire avant transfusion d'urgence vitale conduit à la mort par exsanguination.",
                    consequenceText = "Désamorçage cardiaque et asystolie !",
                    nextPhaseId = "p_failure_arrest"
                )
            ),
            criticalAlertMessage = "TRIADE LÉTALE : Choc décompensé, transfusion d'urgence vitale !",
            timeoutPhaseId = "p_failure_arrest"
        )

        val p2Hemostasis = ScenarioPhase(
            phaseId = "p2_hemostasis",
            title = "Phase 2 : Contrôle de l'Hémostase & Réchauffement",
            narrative = "Les 4 premiers culots globulaires et 4 PFC sont passés au réchauffeur. Le chirurgien a clampé les pédicules utérins. La tension se stabilise à 100/60 mmHg. Fibrinogène et calcium à surveiller.",
            vitals = SimulatedVitals(heartRate = 110, spo2 = 96, systolicBp = 100, diastolicBp = 60, respiratoryRate = 18, etco2 = 30, rhythmDescription = "Rythme Sinusal Tachycarde"),
            timeLimitSeconds = 60,
            availableActions = listOf(
                ClinicalAction(
                    id = "act_calcium_fibrinogen",
                    title = "Chlorure de Calcium 1g IVD + Fibrinogène 2g IV",
                    category = ActionCategory.PHARMACO_DRUGS,
                    timeCostSeconds = 15,
                    scoreDelta = 30,
                    isCriticalCorrect = true,
                    rationale = "Le citrate des poches de transfusion chélate le calcium ionisé (nécessaire à la coagulation et à la contractilité cardiaque). Le fibrinogène est le premier facteur consommé.",
                    consequenceText = "Correction du calcium ionisé, formation de caillots fermes.",
                    nextPhaseId = "p_success_stabilized",
                    vitalsModifier = { it.copy(systolicBp = 115, diastolicBp = 70, isCritical = false) }
                ),
                ClinicalAction(
                    id = "act_active_warming",
                    title = "Couverture chauffante à air pulsé (Bair Hugger) + T° cible > 36°C",
                    category = ActionCategory.AIRWAY_OXYGEN,
                    timeCostSeconds = 10,
                    scoreDelta = 20,
                    isCriticalCorrect = true,
                    rationale = "L'hypothermie inhibe sévèrement les cascades enzymatiques de la coagulation. Maintenir la normothermie est un pilier fondamental de la réanimation.",
                    consequenceText = "Température stabilisée à 36.8°C.",
                    nextPhaseId = "p_success_stabilized"
                )
            )
        )

        val pSuccess = ScenarioPhase(
            phaseId = "p_success_stabilized",
            title = "Succès : Hémostase Contrôlée & Patiente Sauvée",
            narrative = "Saignement tari, hémodynamique stable sans amines lourdes. Nouveau-né né vigoureux. Patiente transférée en réanimation chirurgicale pour surveillance hématologique continue.",
            vitals = SimulatedVitals(heartRate = 85, spo2 = 99, systolicBp = 118, diastolicBp = 72, respiratoryRate = 15, etco2 = 36, rhythmDescription = "Rythme Sinusal Régulier", isCritical = false),
            availableActions = emptyList()
        )

        val pFailure = ScenarioPhase(
            phaseId = "p_failure_arrest",
            title = "Échec : Décès par Choc Hémorragique Irréversible",
            narrative = "Arrêt cardiorespiratoire par exsanguination massive et coagulopathie incontrôlable.",
            vitals = SimulatedVitals(heartRate = 0, spo2 = 30, systolicBp = 0, diastolicBp = 0, respiratoryRate = 0, etco2 = 5, rhythmDescription = "Asystolie", isCritical = true),
            availableActions = emptyList()
        )

        return EscapeRoomScenario(
            id = "scenario_hemorrhagic_shock",
            title = "Choc Hémorragique Aigu Obstétrical",
            subtitle = "Saignement cataclysmique per-césarienne et déglobulisation foudroyante",
            category = ScenarioCategory.HEMODYNAMICS,
            difficulty = DifficultyLevel.INTERMEDIATE_2ND_YEAR,
            estimatedDurationMinutes = 10,
            patient = PatientBrief(
                age = 32,
                gender = "Femme",
                weightKg = 68.0,
                heightCm = 165.0,
                programmedSurgery = "Césarienne en urgence sur placenta praevia hémorragique à 36 SA",
                asaScore = "ASA II-U",
                allergies = "Aucune",
                medicalHistory = "Deuxième geste, antécédent d'une première césarienne."
            ),
            initialObservations = "Incision utérine compliquée d'une plaie vasculaire massive : 1500 mL de sang aspirés en moins de 3 minutes.",
            learningObjectives = listOf(
                "Activer sans retard le protocole de transfusion massive (CGR/PFC 1:1).",
                "Injecter l'acide tranexamique (Exacyl) dès la première heure.",
                "Prévenir la triade létale : hypothermie, acidose et coagulopathie de dilution.",
                "Compenser l'hypocalcémie induite par le citrate des transfusions."
            ),
            initialVitals = SimulatedVitals(heartRate = 145, spo2 = 91, systolicBp = 60, diastolicBp = 35, respiratoryRate = 24, etco2 = 18, rhythmDescription = "Tachycardie Sinusale Prononcée", isCritical = true),
            phases = mapOf(
                "p1_bleeding" to p1,
                "p1_critical_collapse" to pCritical,
                "p2_hemostasis" to p2Hemostasis,
                "p_success_stabilized" to pSuccess,
                "p_failure_arrest" to pFailure
            ),
            startPhaseId = "p1_bleeding",
            successPhaseIds = setOf("p_success_stabilized"),
            failurePhaseIds = setOf("p_failure_arrest"),
            debriefing = DebriefingContent(
                coreDiagnosis = "Choc hémorragique cataclysmique peropératoire sur placenta praevia recouvrant",
                pathophysiologicalSummary = "L'exsanguination brutale entraîne une hypovolémie absolue avec effondrement du retour veineux, baisse du volume d'éjection systolique, ischémie myocardique et défaillance multiviscérale rapide si non compensée par des produits sanguins labiles.",
                criticalSuccessFactors = listOf(
                    "Déclenchement immédiat de la transfusion massive équilibrée (CGR/PFC 1:1)",
                    "Acide tranexamique 1g précoce",
                    "Réchauffement actif de la patiente et des solutés de transfusion",
                    "Suppléance calcique (1g de CaCl2 pour 4 CGR transfusés)",
                    "Communication permanente en boucle fermée avec l'équipe chirurgicale"
                ),
                fatalPitfalls = listOf(
                    "Inonder la patiente de sérum salé 0.9% sans transfuser (coagulopathie de dilution mortelle)",
                    "Attendre les bilans biologiques avant d'engager la transfusion en urgence vitale",
                    "Négliger l'hypocalcémie secondaire aux citrates"
                ),
                clinicalReferences = listOf(
                    "SFAR / CNGOF - Recommandations pour l'Hémorragie du Post-Partum 2024",
                    "CRASH-2 & WOMAN Trials - Tranexamic acid in severe hemorrhage",
                    "European guidelines on management of major bleeding and coagulopathy following trauma"
                )
            ),
            isVerified = true,
            tags = listOf("Hémorragie", "Obstétrique", "Transfusion", "Choc", "Urgence Vitale")
        )
    }
}
