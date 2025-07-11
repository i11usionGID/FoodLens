package com.example.foodlens.domain

import com.example.foodlens.domain.model.ProductAnalysesResult
import javax.inject.Inject

class AnaliseTextUseCase @Inject constructor(
    private val ruleEngine: RuleEngine
) {
    operator fun invoke(ingredients: String): ProductAnalysesResult {
        return ruleEngine.analise(ingredients)
    }
}