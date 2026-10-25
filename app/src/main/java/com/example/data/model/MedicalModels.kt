package com.example.data.model

data class DrugModel(
    val id: String,
    val name: String,
    val brandNames: String,
    val category: String,
    val mechanism: String,
    val indications: String,
    val presentations: String,
    val minDosePerKg: Double = 0.0,
    val maxDosePerKg: Double = 0.0,
    val doseUnit: String = "mg/kg",
    val fixedDoseText: String = "",
    val maintenanceDose: String = "",
    val routes: String,
    val onset: String,
    val duration: String,
    val contraindications: List<String>,
    val adverseEffects: List<String>,
    val terrainCautions: String,
    val monitoringPoints: List<String>,
    val antidoteOrAntagonist: String = "",
    val sources: String
)

data class ProtocolModel(
    val id: String,
    val title: String,
    val category: String, // "CHIRURGIE", "URGENCE_CRISE", "TERRAIN"
    val urgencyLevel: String, // "VITALE", "HAUTE", "STANDARD"
    val subtitle: String,
    val description: String,
    val algorithmSteps: List<String>,
    val keyDrugs: List<String>,
    val specificRisks: List<String>,
    val postOpMonitoring: List<String>,
    val guidelinesSource: String
)

data class BiologyTestModel(
    val id: String,
    val name: String,
    val category: String,
    val normalRange: String,
    val unit: String,
    val anestheticImplications: String,
    val frequentCausesOfAnomaly: String,
    val requiresPostponementOrOptimization: Boolean,
    val optimizationCriteria: String,
    val medicalSource: String
)

data class QuizQuestion(
    val id: Int,
    val question: String,
    val domain: String,
    val options: List<String>,
    val correctIndex: Int,
    val clinicalExplanation: String
)

data class ChecklistItem(
    val id: String,
    val category: String,
    val title: String,
    val description: String,
    var status: String = "PENDING" // "VERIFIED", "PENDING", "ACTION_REQUIRED"
)
