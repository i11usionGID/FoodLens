package com.example.foodlens.domain.model

data class ProductAnalysesResult(
    val score: Int,
    val category: HealthCategory,
    val healthyReasons: List<String>,
    val unhealthyReasons: List<String>
)