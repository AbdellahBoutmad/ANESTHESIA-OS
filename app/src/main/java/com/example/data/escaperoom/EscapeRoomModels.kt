package com.example.data.escaperoom

enum class DifficultyLevel(val label: String, val levelNumber: Int) {
    JUNIOR_1ST_YEAR("1ère Année (Débutant)", 1),
    INTERMEDIATE_2ND_YEAR("2ème Année (Intermédiaire)", 2),
    SENIOR_3RD_YEAR("3ème Année & IAR Confirmé (Avancé)", 3)
}

enum class ScenarioCategory(val label: String, val colorHex: Long) {
    AIRWAY("Voies Aériennes & Intubation", 0xFF0284C7),
    ANAPHYLAXIS("Anaphylaxie & Allergies", 0xFFEF4444),
    MALIGNANT_HYPERTHERMIA("Hyperthermie Maligne", 0xFFF97316),
    HEMODYNAMICS("Instabilité Hémodynamique", 0xFFE11D48),
    HYPOXEMIA("Hypoxémie & Ventilation", 0xFF0D9488),
    POSTOP_SSPI("Complications SSPI / Réveil", 0xFF8B5CF6),
    PEDIATRIC("Anesthésie Pédiatrique", 0xFFD97706)
}

enum class ActionCategory(val label: String) {
    AIRWAY_OXYGEN("Oxygène & Voies Aériennes"),
    PHARMACO_DRUGS("Pharmacologie & Drogues"),
    VENTILATION("Ventilateur & Paramètres"),
    CALL_ASSISTANCE("Appel à l'Aide / MAR"),
    FLUID_HEMODYNAMICS("Remplissage & Hémodynamique"),
    DIAGNOSTIC_EXAM("Examen Clinique & Moniteur")
}

data class SimulatedVitals(
    val heartRate: Int,
    val spo2: Int,
    val systolicBp: Int,
    val diastolicBp: Int,
    val respiratoryRate: Int,
    val etco2: Int,
    val temperature: Double = 37.0,
    val rhythmDescription: String = "Rythme Sinusal",
    val isCritical: Boolean = false
) {
    fun bloodPressureString(): String = "$systolicBp/$diastolicBp"
}

data class ClinicalAction(
    val id: String,
    val title: String,
    val description: String = "",
    val category: ActionCategory,
    val timeCostSeconds: Int = 15,
    val scoreDelta: Int,
    val isCriticalCorrect: Boolean = false,
    val isHarmful: Boolean = false,
    val rationale: String,
    val consequenceText: String,
    val nextPhaseId: String? = null,
    val vitalsModifier: (SimulatedVitals) -> SimulatedVitals = { it }
)

data class ScenarioPhase(
    val phaseId: String,
    val title: String,
    val narrative: String,
    val vitals: SimulatedVitals,
    val timeLimitSeconds: Int = 120,
    val availableActions: List<ClinicalAction>,
    val criticalAlertMessage: String? = null,
    val timeoutPhaseId: String? = null
)

data class PatientBrief(
    val age: Int,
    val gender: String,
    val weightKg: Double,
    val heightCm: Double,
    val programmedSurgery: String,
    val asaScore: String,
    val allergies: String,
    val medicalHistory: String
)

data class DebriefingContent(
    val coreDiagnosis: String,
    val pathophysiologicalSummary: String,
    val criticalSuccessFactors: List<String>,
    val fatalPitfalls: List<String>,
    val clinicalReferences: List<String>
)

data class EscapeRoomScenario(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: ScenarioCategory,
    val difficulty: DifficultyLevel,
    val estimatedDurationMinutes: Int = 10,
    val patient: PatientBrief,
    val initialObservations: String,
    val learningObjectives: List<String>,
    val initialVitals: SimulatedVitals,
    val phases: Map<String, ScenarioPhase>,
    val startPhaseId: String,
    val successPhaseIds: Set<String>,
    val failurePhaseIds: Set<String>,
    val debriefing: DebriefingContent,
    val isVerified: Boolean = true,
    val tags: List<String> = emptyList()
)

data class ActionRecord(
    val timestampSeconds: Int,
    val actionId: String,
    val actionTitle: String,
    val category: ActionCategory,
    val scoreDelta: Int,
    val vitalsSnapshot: SimulatedVitals,
    val wasHarmful: Boolean,
    val wasCritical: Boolean,
    val rationale: String,
    val consequenceText: String
)

enum class SessionOutcome(val label: String, val isSuccess: Boolean) {
    SURVIVED_EXCELLENT("Stabilisation Complète • Diagnostic Posé", true),
    SURVIVED_COMPLICATED("Stabilisé avec Séquelles / Retard de Prise en Charge", true),
    TIME_EXPIRED("Délai Dépassé • Hypoxie / Choc Irréversible", false),
    CARDIAC_ARREST_FAILURE("Arrêt Cardiorespiratoire / Échec Thérapeutique", false)
}

data class SimulationResult(
    val scenarioId: String,
    val scenarioTitle: String,
    val completedAt: Long = System.currentTimeMillis(),
    val totalTimeSeconds: Int,
    val finalScore: Int,
    val outcome: SessionOutcome,
    val actionHistory: List<ActionRecord>,
    val missedObservations: List<String> = emptyList(),
    val incorrectDecisionsCount: Int = 0,
    val criticalDecisionsAchieved: Int = 0,
    val totalCriticalDecisions: Int = 0,
    val aiPersonalizedDebriefing: String? = null
)
