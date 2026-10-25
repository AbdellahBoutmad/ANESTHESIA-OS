package com.example.util

import kotlin.math.roundToInt
import kotlin.math.sqrt

object ClinicalCalculators {

    data class BmiResult(
        val bmi: Double,
        val category: String,
        val idealWeightLorentz: Double,
        val idealWeightDevine: Double,
        val adjustedWeight: Double,
        val bsaMosteller: Double
    )

    fun calculateBmi(weightKg: Double, heightCm: Double, isFemale: Boolean): BmiResult {
        if (weightKg <= 0 || heightCm <= 0) {
            return BmiResult(0.0, "Données invalides", 0.0, 0.0, 0.0, 0.0)
        }
        val heightM = heightCm / 100.0
        val bmi = (weightKg / (heightM * heightM) * 10.0).roundToInt() / 10.0

        val category = when {
            bmi < 18.5 -> "Dénutrition / Maigreur"
            bmi < 25.0 -> "Poids normal"
            bmi < 30.0 -> "Surpoids"
            bmi < 35.0 -> "Obésité de classe I (Modérée)"
            bmi < 40.0 -> "Obésité de classe II (Sévère)"
            else -> "Obésité de classe III (Morbide)"
        }

        // Lorentz Formula
        val idealLorentz = if (isFemale) {
            (heightCm - 100) - (heightCm - 150) / 2.5
        } else {
            (heightCm - 100) - (heightCm - 150) / 4.0
        }

        // Devine Formula
        val heightInches = heightCm / 2.54
        val inchesOver60 = maxOf(0.0, heightInches - 60.0)
        val idealDevine = if (isFemale) {
            45.5 + (2.3 * inchesOver60)
        } else {
            50.0 + (2.3 * inchesOver60)
        }

        // Adjusted body weight for lipophilic/hydrophilic drugs
        val adjusted = if (weightKg > idealDevine) {
            idealDevine + 0.4 * (weightKg - idealDevine)
        } else {
            weightKg
        }

        val bsa = (sqrt((heightCm * weightKg) / 3600.0) * 100.0).roundToInt() / 100.0

        return BmiResult(
            bmi = bmi,
            category = category,
            idealWeightLorentz = (idealLorentz * 10.0).roundToInt() / 10.0,
            idealWeightDevine = (idealDevine * 10.0).roundToInt() / 10.0,
            adjustedWeight = (adjusted * 10.0).roundToInt() / 10.0,
            bsaMosteller = bsa
        )
    }

    data class CockcroftResult(
        val clearanceMlMin: Double,
        val stage: String,
        val doseAdjustmentRecommendation: String
    )

    fun calculateCockcroft(
        age: Int,
        weightKg: Double,
        creatinineUmolL: Double,
        isFemale: Boolean
    ): CockcroftResult {
        if (age <= 0 || weightKg <= 0 || creatinineUmolL <= 0) {
            return CockcroftResult(0.0, "Données invalides", "Vérifiez les paramètres saisis.")
        }
        val constant = if (isFemale) 1.04 else 1.23
        val clearance = (((140 - age) * weightKg * constant) / creatinineUmolL * 10.0).roundToInt() / 10.0

        val (stage, recommendation) = when {
            clearance >= 90.0 -> Pair(
                "Fonction rénale normale (Stade 1)",
                "Posologies standard autorisées."
            )
            clearance >= 60.0 -> Pair(
                "Insuffisance rénale légère (Stade 2)",
                "Surveillance de l'hydratation, posologies usuelles avec précaution."
            )
            clearance >= 30.0 -> Pair(
                "Insuffisance rénale modérée (Stade 3)",
                "Adapter les drogues à élimination rénale (curares benzylisoquinolines à privilégier comme Cisatracurium, adapter morphiniques)."
            )
            clearance >= 15.0 -> Pair(
                "Insuffisance rénale sévère (Stade 4)",
                "Proscrire AINS, éviter morphine/sufentanil à forte dose, éviter sugammadex sans dialyse."
            )
            else -> Pair(
                "Insuffisance rénale terminale (Stade 5)",
                "Dialyse préalable nécessaire, risque majeur d'hyperkaliémie, monitorage hémodynamique lourd."
            )
        }

        return CockcroftResult(clearance, stage, recommendation)
    }

    data class ApfelResult(
        val score: Int,
        val riskPercentage: Int,
        val riskLevel: String,
        val recommendation: String
    )

    fun calculateApfel(
        isFemale: Boolean,
        isNonSmoker: Boolean,
        hasHistoryPonvOrMotionSickness: Boolean,
        postopOpioidsPlanned: Boolean
    ): ApfelResult {
        var score = 0
        if (isFemale) score++
        if (isNonSmoker) score++
        if (hasHistoryPonvOrMotionSickness) score++
        if (postopOpioidsPlanned) score++

        val percentage = when (score) {
            0 -> 10
            1 -> 21
            2 -> 39
            3 -> 61
            else -> 79
        }

        val level = when (score) {
            0, 1 -> "Risque faible de NVPO"
            2 -> "Risque intermédiaire de NVPO"
            else -> "Risque élevé de NVPO"
        }

        val recommendation = when (score) {
            0, 1 -> "Pas de prophylaxie systématique ou monothérapie simple (Dexaméthasone 4 mg à l'induction)."
            2 -> "Bithérapie recommandée : Dexaméthasone 4 à 8 mg à l'induction + Ondansétron 4 mg 30 min avant la fin d'intervention."
            else -> "Trithérapie recommandée : Dexaméthasone + Sétrons + Halopéridol/Dropéridol ou anesthésie totale intraveineuse (TIVA au Propofol) sans halogénés."
        }

        return ApfelResult(score, percentage, level, recommendation)
    }

    data class StopBangResult(
        val score: Int,
        val riskLevel: String,
        val recommendation: String
    )

    fun calculateStopBang(criteria: List<Boolean>): StopBangResult {
        val score = criteria.count { it }
        val (level, rec) = when {
            score >= 5 -> Pair(
                "Risque ÉLEVÉ de Syndrome d'Apnées Obstructives du Sommeil (SAOS)",
                "Anticiper une intubation difficile. Préférer l'ALR. Éviter les morphiniques résiduels, surveillance postopératoire continue en SSPI ou soins continus, poursuite de la PPC personnelle en postopératoire."
            )
            score in 3..4 -> Pair(
                "Risque INTERMÉDIAIRE de SAOS",
                "Prudence avec les sédatifs et morphiniques, monitorage oxymétrique nocturne prolongé."
            )
            else -> Pair(
                "Risque FAIBLE de SAOS",
                "Gestion périopératoire standard."
            )
        }
        return StopBangResult(score, level, rec)
    }

    data class AldreteResult(
        val score: Int,
        val canDischarge: Boolean,
        val decision: String
    )

    fun calculateAldrete(
        motorActivity: Int, // 0-2
        respiration: Int,   // 0-2
        circulation: Int,   // 0-2
        consciousness: Int, // 0-2
        saturation: Int     // 0-2
    ): AldreteResult {
        val total = motorActivity + respiration + circulation + consciousness + saturation
        val canDischarge = total >= 9 && respiration == 2 && saturation == 2
        val decision = if (canDischarge) {
            "APTITUDE À LA SORTIE DE SSPI VALIDÉE (Score >= 9 avec respiration et SpO2 normales)."
        } else {
            "MAINTIEN EN SSPI REQUIS : Score insuffisant (< 9) ou critères de sécurité respiratoire non atteints."
        }
        return AldreteResult(total, canDischarge, decision)
    }

    fun calculateAllowableBloodLoss(
        weightKg: Double,
        initialHematocrit: Double,
        targetHematocrit: Double,
        isFemale: Boolean
    ): Double {
        if (weightKg <= 0 || initialHematocrit <= targetHematocrit || initialHematocrit <= 0) return 0.0
        val ebv = weightKg * (if (isFemale) 65.0 else 70.0)
        val abl = ebv * (initialHematocrit - targetHematocrit) / initialHematocrit
        return (abl * 10.0).roundToInt() / 10.0
    }
}
