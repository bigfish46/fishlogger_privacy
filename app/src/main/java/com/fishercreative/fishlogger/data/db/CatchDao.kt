package com.fishercreative.fishlogger.data.db

import androidx.room.*
import com.fishercreative.fishlogger.data.models.Catch
import kotlinx.coroutines.flow.Flow

@Dao
interface CatchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatch(fishCatch: Catch)

    @Query("SELECT * FROM catches ORDER BY createdAt DESC")
    fun getAllCatches(): Flow<List<Catch>>

    @Query("SELECT * FROM catches WHERE needsSync = 1 ORDER BY createdAt ASC")
    fun getUnsyncedCatches(): Flow<List<Catch>>

    @Query("SELECT * FROM catches WHERE id = :id")
    suspend fun getCatchById(id: String): Catch?

    @Update
    suspend fun updateCatch(fishCatch: Catch)

    @Delete
    suspend fun deleteCatch(fishCatch: Catch)
} 