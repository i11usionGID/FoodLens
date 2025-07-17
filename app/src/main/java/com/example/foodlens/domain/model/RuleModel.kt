package com.example.foodlens.domain.model

data class RuleModel(
    val healthyIndicators: List<String>,
    val harmfulIndicators: List<String>
)