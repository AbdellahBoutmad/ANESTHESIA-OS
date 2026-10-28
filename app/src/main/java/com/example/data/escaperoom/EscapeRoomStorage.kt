package com.example.data.escaperoom

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

data class AdaptiveLearningStats(
    val totalSimulations: Int,
    val successRatePercent: Int,
    val averageScore: Int,
    val bestSpecialty: String,
    val weakestSpecialty: String,
    val recommendations: List<LearningRecommendation>
)

data class LearningRecommendation(
    val title: String,
    val reason: String,
    val targetSection: String // "PHARMACO", "PROTOCOLS", "CALCULATORS", "QUIZ"
)

class EscapeRoomStorage(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("escape_room_prefs", Context.MODE_PRIVATE)

    fun saveResult(result: SimulationResult, category: ScenarioCategory) {
        val existingJson = prefs.getString("results_history", "[]") ?: "[]"
        val array = JSONArray(existingJson)

        val obj = JSONObject().apply {
            put("scenarioId", result.scenarioId)
            put("scenarioTitle", result.scenarioTitle)
            put("completedAt", result.completedAt)
            put("totalTimeSeconds", result.totalTimeSeconds)
            put("finalScore", result.finalScore)
            put("outcome", result.outcome.name)
            put("category", category.name)
            put("incorrectDecisions", result.incorrectDecisionsCount)
            put("criticalDecisions", result.criticalDecisionsAchieved)
            put("actionsCount", result.actionHistory.size)
        }

        array.put(obj)
        prefs.edit().putString("results_history", array.toString()).apply()
    }

    fun getAdaptiveStats(): AdaptiveLearningStats {
        val existingJson = prefs.getString("results_history", "[]") ?: "[]"
        val array = JSONArray(existingJson)

        if (array.length() == 0) {
            return AdaptiveLearningStats(
                totalSimulations = 0,
                successRatePercent = 0,
                averageScore = 0,
                bestSpecialty = "Aucune session",
                weakestSpecialty = "Aucune session",
                recommendations = listOf(
                    LearningRecommendation(
                        title = "Débutez par le Laryngospasme Pédiatrique",
                        reason = "Scénario fondamental de 1ère année pour assimiler la gestion des voies aériennes.",
                        targetSection = "ESCAPE_ROOM"
                    ),
                    LearningRecommendation(
                        title = "Révision des Drogues d'Urgence",
                        reason = "Consultez les fiches Adrénaline, Célocurine et Dantrolène dans la section Pharmaco.",
                        targetSection = "PHARMACO"
                    )
                )
            )
        }

        var totalScore = 0
        var successCount = 0
        val categoryScores = mutableMapOf<String, MutableList<Int>>()

        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val score = obj.optInt("finalScore", 50)
            val outcomeName = obj.optString("outcome", "")
            val cat = obj.optString("category", "AIRWAY")

            totalScore += score
            if (outcomeName.startsWith("SURVIVED")) {
                successCount++
            }

            categoryScores.getOrPut(cat) { mutableListOf() }.add(score)
        }

        val total = array.length()
        val avgScore = totalScore / total
        val successRate = (successCount * 100) / total

        // Find best and weakest specialty
        var bestCat = "Voies Aériennes"
        var bestAvg = -1.0
        var worstCat = "Voies Aériennes"
        var worstAvg = 999.0

        categoryScores.forEach { (cat, scores) ->
            val catAvg = scores.average()
            if (catAvg > bestAvg) {
                bestAvg = catAvg
                bestCat = cat
            }
            if (catAvg < worstAvg) {
                worstAvg = catAvg
                worstCat = cat
            }
        }

        val recs = mutableListOf<LearningRecommendation>()
        if (worstCat == "ANAPHYLAXIS" || worstAvg < 65) {
            recs.add(
                LearningRecommendation(
                    title = "Algorithme du Choc Anaphylactique Peropératoire",
                    reason = "Score perfectible sur la titration de l'Adrénaline et le remplissage précoce.",
                    targetSection = "PROTOCOLS"
                )
            )
        }
        if (worstCat == "MALIGNANT_HYPERTHERMIA") {
            recs.add(
                LearningRecommendation(
                    title = "Fiche Pharmacologique : Dantrolène (Dantrium)",
                    reason = "Maîtriser la posologie initiale de 2.5 mg/kg et le relais réanimatoire.",
                    targetSection = "PHARMACO"
                )
            )
        }
        if (worstCat == "HEMODYNAMICS") {
            recs.add(
                LearningRecommendation(
                    title = "Score de Choc & Calculateur de Pertes Sanguines (Gross)",
                    reason = "Renforcez l'estimation du volume sanguin et le déclenchement de la transfusion.",
                    targetSection = "CALCULATORS"
                )
            )
        }
        recs.add(
            LearningRecommendation(
                title = "Entraînement QCM IAR : Situations de Crise",
                reason = "Testez vos connaissances théoriques sur les complications anesthésiques.",
                targetSection = "QUIZ"
            )
        )

        return AdaptiveLearningStats(
            totalSimulations = total,
            successRatePercent = successRate,
            averageScore = avgScore,
            bestSpecialty = bestCat,
            weakestSpecialty = worstCat,
            recommendations = recs
        )
    }
}
