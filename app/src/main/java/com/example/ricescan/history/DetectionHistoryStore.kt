package com.example.ricescan.history

import android.content.Context
import com.example.ricescan.ml.DiseaseResult

class DetectionHistoryStore(context: Context) {

    private val dao = AppDatabase.getInstance(context).detectionHistoryDao()

    suspend fun getHistory(): List<DetectionHistoryItem> {
        return dao.getAll()
    }

    suspend fun addResult(result: DiseaseResult, imageUri: String?) {
        if (result.diseaseName == "unknown") return

        dao.insert(
            DetectionHistoryItem(
                diseaseName = result.diseaseName,
                displayName = result.displayName,
                timestamp = System.currentTimeMillis(),
                imageUri = imageUri
            )
        )
        dao.trimTo(MAX_ITEMS)
    }

    suspend fun clear() {
        dao.clear()
    }

    companion object {
        private const val MAX_ITEMS = 20
    }
}
