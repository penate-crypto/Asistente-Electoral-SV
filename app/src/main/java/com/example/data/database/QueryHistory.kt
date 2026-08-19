package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "query_history")
data class QueryHistory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val question: String,
    val answer: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface QueryHistoryDao {
    @Query("SELECT * FROM query_history ORDER BY timestamp DESC")
    fun getAllHistoryFlow(): Flow<List<QueryHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(item: QueryHistory)

    @Query("DELETE FROM query_history")
    suspend fun clearAllHistory()
}
