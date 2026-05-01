package com.example.ricescan.history

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "detection_history")
data class DetectionHistoryItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val diseaseName: String,
    val displayName: String,
    val timestamp: Long,
    val imageUri: String?
)
