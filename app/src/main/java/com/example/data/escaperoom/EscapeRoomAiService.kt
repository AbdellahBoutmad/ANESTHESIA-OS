package com.example.data.escaperoom

import android.util.Log
import com.example.data.ai.AiAssistantService
import org.json.JSONArray
import org.json.JSONObject

object EscapeRoomAiService {

    private const val TAG = "EscapeRoomAi"

    /**
     * Ask Gemini to generate a tailored clinical escape room scenario
     */
    suspend fun generateScenario(
        specialty: String,
        topic: String,
        difficulty: DifficultyLevel,
        studentYear: String,
        language: String = "Français / Darija"
    ): Result<EscapeRoomScenario> {
        val prompt = """
Tu es un concepteur pédagogique médical senior en Anesthésie-Réanimation (SFAR / ISPITS).
Génère un scénario de simulation clinique interactif (Escape Room clinique) sous forme de JSON STRICT.

PARAMÈTRES :
- Spécialité : $specialty
- Thème d'urgence : $topic
- Niveau : ${difficulty.label} ($studentYear)
- Langue des explications : $language

RÈGLES CRITIQUES :
1. Réponds UNIQUEMENT par un objet JSON valide, sans aucun bloc markdown ```json ni texte introductif.
2. Le scénario doit comporter au moins 3 phases progressives ("p1", "p2", "p_success", "p_failure").
3. Chaque phase doit comporter des constantes vitales chiffrées réalistes (FC, SpO2, PAS, PAD, FR, EtCO2).
4. Chaque action doit avoir : id, title, category, timeCostSeconds, scoreDelta, isCriticalCorrect, isHarmful, rationale, consequenceText, nextPhaseId.

FORMAT JSON STRICT ATTENDU :
{
  "id": "scenario_ai_generated",
  "title": "Titre du cas",
  "subtitle": "Sous-titre concis",
  "category": "AIRWAY", 
  "estimatedDurationMinutes": 10,
  "patient": {
    "age": 45,
    "gender": "Homme",
    "weightKg": 70.0,
    "heightCm": 175.0,
    "programmedSurgery": "Nom chirurgie",
    "asaScore": "ASA II",
    "allergies": "Allergies",
    "medicalHistory": "Antécédents"
  },
  "initialObservations": "Description de la situation au bloc ou en SSPI",
  "learningObjectives": ["Objectif 1", "Objectif 2", "Objectif 3"],
  "initialVitals": {
    "heartRate": 110,
    "spo2": 89,
    "systolicBp": 85,
    "diastolicBp": 50,
    "respiratoryRate": 24,
    "etco2": 26,
    "temperature": 37.0,
    "rhythmDescription": "Tachycardie Sinusale",
    "isCritical": true
  },
  "phases": [
    {
      "phaseId": "p1",
      "title": "Phase 1 : Titre",
      "narrative": "Ce qui se passe...",
      "vitals": {
        "heartRate": 110, "spo2": 89, "systolicBp": 85, "diastolicBp": 50, "respiratoryRate": 24, "etco2": 26, "temperature": 37.0, "rhythmDescription": "Tachycardie", "isCritical": true
      },
      "timeLimitSeconds": 60,
      "timeoutPhaseId": "p_failure",
      "criticalAlertMessage": "Alerte immédiate",
      "actions": [
        {
          "id": "act_1",
          "title": "Action appropriée",
          "category": "AIRWAY_OXYGEN",
          "timeCostSeconds": 15,
          "scoreDelta": 25,
          "isCriticalCorrect": true,
          "isHarmful": false,
          "rationale": "Pourquoi cette action est indiquée médicalement",
          "consequenceText": "Amélioration des paramètres",
          "nextPhaseId": "p_success"
        },
        {
          "id": "act_2",
          "title": "Action inappropriée",
          "category": "PHARMACO_DRUGS",
          "timeCostSeconds": 15,
          "scoreDelta": -20,
          "isCriticalCorrect": false,
          "isHarmful": true,
          "rationale": "Pourquoi cette action est délétère",
          "consequenceText": "Aggravation clinique",
          "nextPhaseId": "p_failure"
        }
      ]
    },
    {
      "phaseId": "p_success",
      "title": "Succès : Stabilisé",
      "narrative": "Le patient est sauvé !",
      "vitals": { "heartRate": 80, "spo2": 99, "systolicBp": 120, "diastolicBp": 70, "respiratoryRate": 15, "etco2": 38, "temperature": 37.0, "rhythmDescription": "Normal", "isCritical": false },
      "timeLimitSeconds": 60,
      "actions": []
    },
    {
      "phaseId": "p_failure",
      "title": "Échec : Arrêt Cardiorespiratoire",
      "narrative": "Évolution fatale par manque de traitement approprié.",
      "vitals": { "heartRate": 0, "spo2": 40, "systolicBp": 0, "diastolicBp": 0, "respiratoryRate": 0, "etco2": 5, "temperature": 37.0, "rhythmDescription": "Asystolie", "isCritical": true },
      "timeLimitSeconds": 60,
      "actions": []
    }
  ],
  "startPhaseId": "p1",
  "successPhaseIds": ["p_success"],
  "failurePhaseIds": ["p_failure"],
  "debriefing": {
    "coreDiagnosis": "Diagnostic final",
    "pathophysiologicalSummary": "Résumé physiopathologique",
    "criticalSuccessFactors": ["Facteur 1", "Facteur 2"],
    "fatalPitfalls": ["Piège 1", "Piège 2"],
    "clinicalReferences": ["SFAR Guidelines", "Urgences Médicales"]
  }
}
""".trimIndent()

        return try {
            val rawResponse = AiAssistantService.askAssistant(currentPrompt = prompt)
            val cleanedJson = cleanJsonString(rawResponse)
            val scenario = parseScenarioJson(cleanedJson, difficulty)
            Result.success(scenario)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to generate AI scenario: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Ask Gemini for an intelligent, pedagogical Moroccan Darija performance analysis
     * strictly based on the actual actions recorded in the session
     */
    suspend fun generateSessionDebriefing(
        scenario: EscapeRoomScenario,
        result: SimulationResult
    ): String {
        val actionsSummary = result.actionHistory.joinToString("\n") {
            "- t=${it.timestampSeconds}s : ${it.actionTitle} [${if (it.wasCritical) "CRITIQUE SALVATEUR" else if (it.wasHarmful) "DÉLÉTÈRE" else "NEUTRE"}] (${it.consequenceText})"
        }

        val prompt = """
Tu es BNaja AI, tuteur médical bienveillant et expert en Anesthésie-Réanimation.
L'étudiant vient de terminer le cas clinique suivant dans le simulateur "Clinical Escape Room" :

CAS : ${scenario.title} (${scenario.debriefing.coreDiagnosis})
SCORE OBTENU : ${result.finalScore}/100
RÉSULTAT FINAL : ${result.outcome.label}
DURÉE : ${result.totalTimeSeconds} secondes

ACTIONS EFFECTUÉES PAR L'ÉTUDIANT :
$actionsSummary

POINTS CRITIQUES DU CAS :
${scenario.debriefing.criticalSuccessFactors.joinToString("\n- ")}

PIÈGES À ÉVITER :
${scenario.debriefing.fatalPitfalls.joinToString("\n- ")}

MISSION :
Rédige un débriefing pédagogique, constructif et stimulant en DARIJA MAROCAINE (الدارجة المغربية) avec termes médicaux précis en français.
1. Salue l'étudiant et commente son score et l'issue du patient.
2. Analyse les décisions qu'il a prises : félicite ses bons réflexes, explique pourquoi telle action erronée a aggravé la situation.
3. Rappelle les 3 règles d'or à retenir pour ce type d'urgence au bloc ou en réa.
4. Encourage-le avec énergie et bienveillance ("تبارك الله عليك", "الله يعطيك الصحة", "عاود جرب باش تضبطها مزيان").
""".trimIndent()

        return try {
            AiAssistantService.askAssistant(currentPrompt = prompt)
        } catch (e: Exception) {
            "تبارك الله عليك على المحاولة! راجع خطوات التدبير السريع (O2 100%, Appel à l'aide, Drogues d'urgence) وعاود جرب هاد السيناريو باش تجيب 100% إن شاء الله."
        }
    }

    private fun cleanJsonString(raw: String): String {
        var str = raw.trim()
        if (str.startsWith("```json")) {
            str = str.removePrefix("```json").trim()
        } else if (str.startsWith("```")) {
            str = str.removePrefix("```").trim()
        }
        if (str.endsWith("```")) {
            str = str.removeSuffix("```").trim()
        }
        return str
    }

    private fun parseScenarioJson(jsonStr: String, difficulty: DifficultyLevel): EscapeRoomScenario {
        val obj = JSONObject(jsonStr)

        val id = obj.optString("id", "ai_scen_" + System.currentTimeMillis())
        val title = obj.getString("title")
        val subtitle = obj.optString("subtitle", "Cas généré par BNaja AI")
        val catStr = obj.optString("category", "AIRWAY")
        val category = try {
            ScenarioCategory.valueOf(catStr)
        } catch (e: Exception) {
            ScenarioCategory.AIRWAY
        }

        val duration = obj.optInt("estimatedDurationMinutes", 10)

        val patientObj = obj.getJSONObject("patient")
        val patient = PatientBrief(
            age = patientObj.optInt("age", 40),
            gender = patientObj.optString("gender", "Homme"),
            weightKg = patientObj.optDouble("weightKg", 70.0),
            heightCm = patientObj.optDouble("heightCm", 175.0),
            programmedSurgery = patientObj.optString("programmedSurgery", "Intervention chirurgicale"),
            asaScore = patientObj.optString("asaScore", "ASA II"),
            allergies = patientObj.optString("allergies", "Néant"),
            medicalHistory = patientObj.optString("medicalHistory", "Sans antécédent notable")
        )

        val initialObservations = obj.optString("initialObservations", "Présentation clinique initiale")
        val objectives = mutableListOf<String>()
        val objArray = obj.optJSONArray("learningObjectives")
        if (objArray != null) {
            for (i in 0 until objArray.length()) {
                objectives.add(objArray.getString(i))
            }
        }

        val initVitalsObj = obj.getJSONObject("initialVitals")
        val initialVitals = parseVitals(initVitalsObj)

        val phasesMap = mutableMapOf<String, ScenarioPhase>()
        val phasesArray = obj.getJSONArray("phases")
        for (i in 0 until phasesArray.length()) {
            val pObj = phasesArray.getJSONObject(i)
            val pId = pObj.getString("phaseId")
            val pTitle = pObj.getString("title")
            val pNarrative = pObj.getString("narrative")
            val pVitals = parseVitals(pObj.getJSONObject("vitals"))
            val pTimeLimit = pObj.optInt("timeLimitSeconds", 60)
            val pAlert = if (pObj.has("criticalAlertMessage")) pObj.optString("criticalAlertMessage") else null
            val pTimeoutTarget = if (pObj.has("timeoutPhaseId")) pObj.optString("timeoutPhaseId") else null

            val actionsList = mutableListOf<ClinicalAction>()
            val actArray = pObj.optJSONArray("actions")
            if (actArray != null) {
                for (j in 0 until actArray.length()) {
                    val aObj = actArray.getJSONObject(j)
                    val cat = try {
                        ActionCategory.valueOf(aObj.optString("category", "AIRWAY_OXYGEN"))
                    } catch (e: Exception) {
                        ActionCategory.AIRWAY_OXYGEN
                    }
                    actionsList.add(
                        ClinicalAction(
                            id = aObj.optString("id", "act_${i}_$j"),
                            title = aObj.getString("title"),
                            description = aObj.optString("description", ""),
                            category = cat,
                            timeCostSeconds = aObj.optInt("timeCostSeconds", 15),
                            scoreDelta = aObj.optInt("scoreDelta", 15),
                            isCriticalCorrect = aObj.optBoolean("isCriticalCorrect", false),
                            isHarmful = aObj.optBoolean("isHarmful", false),
                            rationale = aObj.optString("rationale", ""),
                            consequenceText = aObj.optString("consequenceText", "Action exécutée"),
                            nextPhaseId = if (aObj.has("nextPhaseId")) aObj.optString("nextPhaseId") else null
                        )
                    )
                }
            }

            phasesMap[pId] = ScenarioPhase(
                phaseId = pId,
                title = pTitle,
                narrative = pNarrative,
                vitals = pVitals,
                timeLimitSeconds = pTimeLimit,
                availableActions = actionsList,
                criticalAlertMessage = if (pAlert.isNullOrBlank()) null else pAlert,
                timeoutPhaseId = if (pTimeoutTarget.isNullOrBlank()) null else pTimeoutTarget
            )
        }

        val startPhaseId = obj.optString("startPhaseId", phasesMap.keys.firstOrNull() ?: "p1")

        val successSet = mutableSetOf<String>()
        val successArray = obj.optJSONArray("successPhaseIds")
        if (successArray != null) {
            for (i in 0 until successArray.length()) successSet.add(successArray.getString(i))
        } else {
            successSet.add("p_success")
        }

        val failureSet = mutableSetOf<String>()
        val failureArray = obj.optJSONArray("failurePhaseIds")
        if (failureArray != null) {
            for (i in 0 until failureArray.length()) failureSet.add(failureArray.getString(i))
        } else {
            failureSet.add("p_failure")
        }

        val debriefObj = obj.optJSONObject("debriefing")
        val debriefing = DebriefingContent(
            coreDiagnosis = debriefObj?.optString("coreDiagnosis", "Diagnostic clinique") ?: "Urgence vitale",
            pathophysiologicalSummary = debriefObj?.optString("pathophysiologicalSummary", "") ?: "",
            criticalSuccessFactors = jsonArrayToList(debriefObj?.optJSONArray("criticalSuccessFactors")),
            fatalPitfalls = jsonArrayToList(debriefObj?.optJSONArray("fatalPitfalls")),
            clinicalReferences = jsonArrayToList(debriefObj?.optJSONArray("clinicalReferences"))
        )

        return EscapeRoomScenario(
            id = id,
            title = title,
            subtitle = subtitle,
            category = category,
            difficulty = difficulty,
            estimatedDurationMinutes = duration,
            patient = patient,
            initialObservations = initialObservations,
            learningObjectives = objectives,
            initialVitals = initialVitals,
            phases = phasesMap,
            startPhaseId = startPhaseId,
            successPhaseIds = successSet,
            failurePhaseIds = failureSet,
            debriefing = debriefing,
            isVerified = false, // AI generated is flagged unverified by default
            tags = listOf("Généré par IA", category.label)
        )
    }

    private fun parseVitals(obj: JSONObject): SimulatedVitals {
        return SimulatedVitals(
            heartRate = obj.optInt("heartRate", 80),
            spo2 = obj.optInt("spo2", 98),
            systolicBp = obj.optInt("systolicBp", 120),
            diastolicBp = obj.optInt("diastolicBp", 70),
            respiratoryRate = obj.optInt("respiratoryRate", 16),
            etco2 = obj.optInt("etco2", 36),
            temperature = obj.optDouble("temperature", 37.0),
            rhythmDescription = obj.optString("rhythmDescription", "Rythme Sinusal"),
            isCritical = obj.optBoolean("isCritical", false)
        )
    }

    private fun jsonArrayToList(arr: JSONArray?): List<String> {
        if (arr == null) return emptyList()
        val list = mutableListOf<String>()
        for (i in 0 until arr.length()) {
            list.add(arr.getString(i))
        }
        return list
    }
}
