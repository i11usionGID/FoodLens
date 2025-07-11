package com.example.foodlens.data

import com.example.foodlens.domain.RuleEngine
import com.example.foodlens.domain.model.HealthCategory
import com.example.foodlens.domain.model.ProductAnalysesResult
import javax.inject.Inject

class RuleEngineImpl @Inject constructor(): RuleEngine {
    override fun analise(ingredients: String): ProductAnalysesResult {
        return ProductAnalysesResult(
            1,
            HealthCategory.HEALTH,
            listOf("", "")
        )
    }
}