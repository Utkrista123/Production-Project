package com.example.ricescan.ml

data class DiseaseInfo(
    val name: String,
    val severity: String,
    val cause: String,
    val symptoms: List<String>,
    val treatment: String,
    val prevention: String
)