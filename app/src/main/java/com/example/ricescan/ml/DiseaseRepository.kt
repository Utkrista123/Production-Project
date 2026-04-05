package com.example.ricescan.ml

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DiseaseRepository(private val context: Context) {

    private val diseaseMap: Map<String, DiseaseInfo> by lazy {
        loadFromAssets()
    }

    private fun loadFromAssets(): Map<String, DiseaseInfo> {
        val assets = context.assets.list("")?.toSet().orEmpty()
        return when {
            assets.contains("diseases.json") -> loadFromJson()
            assets.contains("points.txt") -> loadFromPoints()
            else -> emptyMap()
        }.ifEmpty { defaultDiseases() }
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

    private fun loadFromPoints(): Map<String, DiseaseInfo> {
        return try {
            val lines = context.assets.open("points.txt")
                .bufferedReader()
                .use { it.readLines() }

            parsePoints(lines).ifEmpty { emptyMap() }
        } catch (e: Exception) {
            emptyMap()
        }
    }

    private enum class Section {
        NONE,
        SYMPTOMS,
        CAUSE,
        PREVENTION,
        TREATMENT
    }

    private data class ParsedDisease(
        val rawName: String,
        val symptoms: MutableList<String> = mutableListOf(),
        val causes: MutableList<String> = mutableListOf(),
        val prevention: MutableList<String> = mutableListOf(),
        val treatment: MutableList<String> = mutableListOf()
    )

    private fun parsePoints(lines: List<String>): Map<String, DiseaseInfo> {
        val headerRegex = Regex("^\\s*\\d+\\.\\s*(.+)$")
        val sectionSymptoms = Regex("(?i)\\bsymptoms\\b")
        val sectionCause = Regex("(?i)\\banalysis\\b|\\bcause\\b")
        val sectionPrevention = Regex("(?i)\\bprevention\\b")
        val sectionTreatment = Regex("(?i)\\btreatment\\b")

        val parsed = mutableListOf<ParsedDisease>()
        var current: ParsedDisease? = null
        var section = Section.NONE

        for (rawLine in lines) {
            val line = rawLine.trim()
            if (line.isBlank()) continue

            val headerMatch = headerRegex.matchEntire(line)
            if (headerMatch != null) {
                current?.let { parsed.add(it) }
                current = ParsedDisease(rawName = headerMatch.groupValues[1].trim())
                section = Section.NONE
                continue
            }

            if (current == null) continue

            when {
                sectionSymptoms.containsMatchIn(line) -> {
                    section = Section.SYMPTOMS
                    continue
                }
                sectionCause.containsMatchIn(line) -> {
                    section = Section.CAUSE
                    continue
                }
                sectionPrevention.containsMatchIn(line) -> {
                    section = Section.PREVENTION
                    continue
                }
                sectionTreatment.containsMatchIn(line) -> {
                    section = Section.TREATMENT
                    continue
                }
            }

            when (section) {
                Section.SYMPTOMS -> current.symptoms.add(line)
                Section.CAUSE -> current.causes.add(line)
                Section.PREVENTION -> current.prevention.add(line)
                Section.TREATMENT -> current.treatment.add(line)
                Section.NONE -> Unit
            }
        }
        current?.let { parsed.add(it) }

        return parsed.mapNotNull { disease ->
            val key = mapNameToKey(disease.rawName) ?: return@mapNotNull null
            DiseaseInfo(
                name = disease.rawName,
                severity = severityFor(key),
                cause = formatList(disease.causes),
                symptoms = disease.symptoms.toList(),
                treatment = formatList(disease.treatment),
                prevention = formatList(disease.prevention)
            ).let { key to it }
        }.toMap()
    }

    private fun formatList(items: List<String>): String {
        return items
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .joinToString("\n") { "• $it" }
    }

    private fun mapNameToKey(rawName: String): String? {
        val normalized = rawName
            .lowercase()
            .replace(Regex("\\(.*?\\)"), "")
            .replace(Regex("[^a-z\\s]"), " ")
            .trim()
            .replace(Regex("\\s+"), "_")

        return when (normalized) {
            "bacterial_leaf_blight" -> "bacterial_leaf_blight"
            "brown_spot" -> "brown_spot"
            "leaf_blast" -> "leaf_blast"
            "leaf_scald" -> "leaf_scald"
            "narrow_brown_spot" -> "narrow_brown_spot"
            else -> null
        }
    }

    private fun severityFor(key: String): String {
        return when (key) {
            "bacterial_leaf_blight" -> "High"
            "leaf_blast" -> "High"
            "leaf_scald" -> "Medium"
            "brown_spot" -> "Medium"
            "narrow_brown_spot" -> "Medium"
            "healthy" -> "None"
            else -> "Unknown"
        }
    }

    private fun defaultDiseases(): Map<String, DiseaseInfo> {
        return mapOf(
            "healthy" to DiseaseInfo(
                name = "Healthy Rice Leaf",
                severity = "None",
                cause = "• No disease detected.",
                symptoms = listOf("No visible symptoms."),
                treatment = "• No treatment required.",
                prevention = "• Maintain good field practices."
            )
        )
    }

    fun getDisease(key: String): DiseaseInfo? {
        return diseaseMap[key] ?: diseaseMap["healthy"]
    }

    fun getAllDiseases(): Map<String, DiseaseInfo> = diseaseMap
}
