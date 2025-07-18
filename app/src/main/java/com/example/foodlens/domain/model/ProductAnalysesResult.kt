package com.example.foodlens.domain.model

data class ProductAnalysesResult(
    val healthPercent: Int,
    val unhealthyIngredients: Map<String, String>
)