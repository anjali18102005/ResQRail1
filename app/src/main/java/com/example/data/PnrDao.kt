package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PnrDao {
    @Query("SELECT * FROM pnr_searches ORDER BY timestamp DESC")
    fun getAllSearches(): Flow<List<PnrEntity>>

    @Query("SELECT * FROM pnr_searches WHERE pnr = :pnr LIMIT 1")
    suspend fun getPnr(pnr: String): PnrEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPnr(pnr: PnrEntity)

    @Delete
    suspend fun deletePnr(pnr: PnrEntity)

    @Query("DELETE FROM pnr_searches")
    suspend fun clearHistory()
}
