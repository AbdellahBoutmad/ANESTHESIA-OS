package com.example

import com.example.util.ClinicalCalculators
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ClinicalCalculatorsTest {

    @Test
    fun testBmiCalculation() {
        val res = ClinicalCalculators.calculateBmi(70.0, 175.0, isFemale = false)
        assertEquals(22.9, res.bmi, 0.1)
        assertEquals("Poids normal", res.category)
        assertTrue(res.idealWeightDevine > 60.0)
    }

    @Test
    fun testCockcroftCreatinineClearance() {
        // 60 years old, 70 kg, Cr = 88.4 umol/L (1.0 mg/dL), male
        val res = ClinicalCalculators.calculateCockcroft(60, 70.0, 88.4, isFemale = false)
        assertTrue(res.clearanceMlMin > 70.0)
        assertTrue(res.stage.contains("Stade"))
    }

    @Test
    fun testApfelScore() {
        val lowRisk = ClinicalCalculators.calculateApfel(isFemale = false, isNonSmoker = false, hasHistoryPonvOrMotionSickness = false, postopOpioidsPlanned = false)
        assertEquals(0, lowRisk.score)
        assertEquals(10, lowRisk.riskPercentage)

        val highRisk = ClinicalCalculators.calculateApfel(isFemale = true, isNonSmoker = true, hasHistoryPonvOrMotionSickness = true, postopOpioidsPlanned = true)
        assertEquals(4, highRisk.score)
        assertEquals(79, highRisk.riskPercentage)
    }

    @Test
    fun testAldreteScore() {
        val fitForDischarge = ClinicalCalculators.calculateAldrete(
            motorActivity = 2,
            respiration = 2,
            circulation = 2,
            consciousness = 2,
            saturation = 2
        )
        assertEquals(10, fitForDischarge.score)
        assertTrue(fitForDischarge.canDischarge)

        val notFit = ClinicalCalculators.calculateAldrete(
            motorActivity = 1,
            respiration = 1,
            circulation = 1,
            consciousness = 1,
            saturation = 1
        )
        assertEquals(5, notFit.score)
        assertFalse(notFit.canDischarge)
    }

    @Test
    fun testAllowableBloodLoss() {
        val abl = ClinicalCalculators.calculateAllowableBloodLoss(70.0, 40.0, 30.0, isFemale = false)
        // EBV = 70 * 70 = 4900 mL; ABL = 4900 * (40 - 30) / 40 = 1225 mL
        assertEquals(1225.0, abl, 1.0)
    }
}
