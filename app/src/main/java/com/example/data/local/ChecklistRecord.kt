package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checklist_records")
data class ChecklistRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val checklistType: String, // "MATERIEL_BLOC", "OMS_SECURITE"
    val roomName: String,
    val verifiedBy: String,
    val verifiedCount: Int,
    val totalCount: Int,
    val isComplete: Boolean,
    val details: String,
    val createdAt: Long = System.currentTimeMillis()
)
