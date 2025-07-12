package com.example.foodlens.data

import com.example.foodlens.domain.RuleEngine
import com.example.foodlens.domain.model.HealthCategory
import com.example.foodlens.domain.model.ProductAnalysesResult
import javax.inject.Inject

class RuleEngineImpl @Inject constructor() : RuleEngine {

    private val harmfulIngredients = listOf(
        "глюкозный сироп", "пальмовое масло", "консерванты", "усилитель вкуса",
        "глутамат натрия", "e", "подсластитель", "аспартам", "сахар"
    )

    private val healthyIndicators = listOf(
        "без консервантов", "натуральный", "цельнозерновой", "на закваске", "без сахара"
    )

    override fun analise(ingredients: String): ProductAnalysesResult {
        val lowerCased = ingredients.lowercase()
        val explanations = mutableListOf<String>()
        var score = 0

        harmfulIngredients.forEach { bad ->
            if (lowerCased.contains(bad)) {
                score -= 1
                explanations.add("Найден вредный ингредиент: \"$bad\"")
            }
        }

        healthyIndicators.forEach { good ->
            if (lowerCased.contains(good)) {
                score += 1
                explanations.add("Найден полезный признак: \"$good\"")
            }
        }

        val category = when {
            score <= -2 -> HealthCategory.UNHEALTHY
            score <= 0 -> HealthCategory.MODERATE
            else -> HealthCategory.HEALTH
        }

        return ProductAnalysesResult(
            score = score,
            category = category,
            reason = explanations
        )
    }
}
