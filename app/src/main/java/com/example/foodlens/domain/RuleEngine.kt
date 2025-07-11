package com.example.foodlens.domain

import com.example.foodlens.domain.model.ProductAnalysesResult

interface RuleEngine {

    fun analise(ingredients: String): ProductAnalysesResult
}