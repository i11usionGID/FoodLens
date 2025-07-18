package com.example.foodlens.domain.model

data class RuleModel(
    val harmfulIngredients: Map<String, HarmfulIngredientFields>
)