package com.example.ricescan.history

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DetectionHistoryDao {

    @Query("SELECT * FROM detection_history ORDER BY timestamp DESC")
    suspend fun getAll(): List<DetectionHistoryItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: DetectionHistoryItem)

    @Query("DELETE FROM detection_history")
    suspend fun clear()

    @Query("DELETE FROM detection_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM detection_history WHERE id NOT IN (SELECT id FROM detection_history ORDER BY timestamp DESC LIMIT :limit)")
    suspend fun trimTo(limit: Int)
}
