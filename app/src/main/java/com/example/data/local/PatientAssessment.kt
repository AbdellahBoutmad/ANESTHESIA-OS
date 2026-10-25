package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patient_assessments")
data class PatientAssessment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientId: String,
    val patientName: String,
    val age: Int,
    val gender: String,
    val weightKg: Double,
    val heightCm: Double,
    val bmi: Double,
    val surgery: String,
    val surgeryType: String,
    val urgency: String,
    val medicalHistory: String,
    val surgicalHistory: String,
    val anesthesiaHistory: String,
    val allergies: String,
    val currentTreatments: String,
    val fastingStatus: String,
    val cvExam: String,
    val respExam: String,
    val mallampati: String,
    val mouthOpening: String,
    val thyromentalDistance: String,
    val cervicalMobility: String,
    val dentalStatus: String,
    val asaScore: String,
    val difficultAirwayRisk: Boolean,
    val aspirationRisk: Boolean,
    val ponvApfelScore: Int,
    val summaryNotes: String,
    val createdAt: Long = System.currentTimeMillis()
)
