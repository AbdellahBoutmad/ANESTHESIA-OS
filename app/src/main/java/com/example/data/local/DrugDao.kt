package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DrugDao {
    @Query("SELECT * FROM clinical_drugs ORDER BY name ASC")
    fun getAllDrugs(): Flow<List<DrugEntity>>

    @Query("SELECT * FROM clinical_drugs WHERE category = :category ORDER BY name ASC")
    fun getDrugsByCategory(category: String): Flow<List<DrugEntity>>

    @Query("""
        SELECT * FROM clinical_drugs 
        WHERE name LIKE '%' || :query || '%' 
           OR brandNames LIKE '%' || :query || '%' 
           OR category LIKE '%' || :query || '%' 
           OR indications LIKE '%' || :query || '%'
        ORDER BY name ASC
    """)
    fun searchDrugs(query: String): Flow<List<DrugEntity>>

    @Query("SELECT * FROM clinical_drugs WHERE id = :id LIMIT 1")
    suspend fun getDrugById(id: String): DrugEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(drugs: List<DrugEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrug(drug: DrugEntity): Long

    @Query("UPDATE clinical_drugs SET isFavorite = :isFav WHERE id = :id")
    suspend fun updateFavorite(id: String, isFav: Boolean)

    @Query("SELECT COUNT(*) FROM clinical_drugs")
    suspend fun getCount(): Int
}
