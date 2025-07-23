package com.example.foodlens.domain.model

data class ProductAnalysesResult(
    val ocrText: String,
    val healthPercent: Int,
    val harmfulIngredients: Map<String, String>
)