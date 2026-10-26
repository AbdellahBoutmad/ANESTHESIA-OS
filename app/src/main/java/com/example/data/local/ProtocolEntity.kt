package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clinical_protocols")
data class ProtocolEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val category: String, // "CHIRURGIE", "URGENCE_CRISE", "TERRAIN", "OBSTETRIQUE", "REANIMATION"
    val urgencyLevel: String, // "VITALE", "HAUTE", "STANDARD"
    val subtitle: String,
    val description: String,
    val algorithmStepsText: String, // Delimited by "|"
    val keyDrugsText: String,        // Delimited by "|"
    val specificRisksText: String,   // Delimited by "|"
    val postOpMonitoringText: String,// Delimited by "|"
    val guidelinesSource: String,
    val isFavorite: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
