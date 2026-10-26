package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clinical_drugs")
data class DrugEntity(
    @PrimaryKey
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
    val contraindicationsText: String, // Delimited by "|"
    val adverseEffectsText: String,     // Delimited by "|"
    val terrainCautions: String,
    val monitoringPointsText: String,   // Delimited by "|"
    val antidoteOrAntagonist: String = "",
    val sources: String,
    val isFavorite: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
