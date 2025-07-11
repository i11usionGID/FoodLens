package com.example.foodlens.domain.model

data class ProductAnalysesResult(
    val score: Int,
    val category: HealthCategory,
    val reason: List<String>
)