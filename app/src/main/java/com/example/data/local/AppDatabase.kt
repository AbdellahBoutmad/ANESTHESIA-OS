package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    @Query("SELECT * FROM patient_assessments ORDER BY createdAt DESC")
    fun getAllAssessments(): Flow<List<PatientAssessment>>

    @Query("SELECT * FROM patient_assessments WHERE id = :id LIMIT 1")
    suspend fun getAssessmentById(id: Long): PatientAssessment?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessment(assessment: PatientAssessment): Long

    @Update
    suspend fun updateAssessment(assessment: PatientAssessment)

    @Delete
    suspend fun deleteAssessment(assessment: PatientAssessment)

    @Query("SELECT COUNT(*) FROM patient_assessments")
    fun getCount(): Flow<Int>
}

@Dao
interface ChecklistDao {
    @Query("SELECT * FROM checklist_records ORDER BY createdAt DESC")
    fun getAllChecklists(): Flow<List<ChecklistRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklist(record: ChecklistRecord): Long

    @Query("SELECT COUNT(*) FROM checklist_records WHERE isComplete = 1")
    fun getCompletedCount(): Flow<Int>
}

@Database(entities = [PatientAssessment::class, ChecklistRecord::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun checklistDao(): ChecklistDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "anesthesia_os_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
