package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProtocolDao {
    @Query("SELECT * FROM clinical_protocols ORDER BY CASE urgencyLevel WHEN 'VITALE' THEN 1 WHEN 'HAUTE' THEN 2 ELSE 3 END, title ASC")
    fun getAllProtocols(): Flow<List<ProtocolEntity>>

    @Query("SELECT * FROM clinical_protocols WHERE category = :category ORDER BY title ASC")
    fun getProtocolsByCategory(category: String): Flow<List<ProtocolEntity>>

    @Query("""
        SELECT * FROM clinical_protocols 
        WHERE title LIKE '%' || :query || '%' 
           OR subtitle LIKE '%' || :query || '%' 
           OR description LIKE '%' || :query || '%' 
           OR category LIKE '%' || :query || '%'
        ORDER BY title ASC
    """)
    fun searchProtocols(query: String): Flow<List<ProtocolEntity>>

    @Query("SELECT * FROM clinical_protocols WHERE id = :id LIMIT 1")
    suspend fun getProtocolById(id: String): ProtocolEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(protocols: List<ProtocolEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProtocol(protocol: ProtocolEntity): Long

    @Query("UPDATE clinical_protocols SET isFavorite = :isFav WHERE id = :id")
    suspend fun updateFavorite(id: String, isFav: Boolean)

    @Query("SELECT COUNT(*) FROM clinical_protocols")
    suspend fun getCount(): Int
}
