package com.example.ricescan.ml

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DiseaseRepository(private val context: Context) {

    private val diseaseMap: Map<String, DiseaseInfo> by lazy {
        loadFromJson()
    }

    private fun loadFromJson(): Map<String, DiseaseInfo> {
        return try {
            val json = context.assets.open("diseases.json")
                .bufferedReader()
                .use { it.readText() }

            val type = object : TypeToken<Map<String, DiseaseInfo>>() {}.type
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun getDisease(key: String): DiseaseInfo? {
        return diseaseMap[key] ?: diseaseMap["healthy"]
    }

    fun getAllDiseases(): Map<String, DiseaseInfo> = diseaseMap
}