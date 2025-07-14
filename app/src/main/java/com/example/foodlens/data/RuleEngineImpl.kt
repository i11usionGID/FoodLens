package com.example.foodlens.data

import com.example.foodlens.domain.RuleEngine
import com.example.foodlens.domain.model.HealthCategory
import com.example.foodlens.domain.model.ProductAnalysesResult
import javax.inject.Inject


class RuleEngineImpl @Inject constructor() : RuleEngine {

    private val harmfulIngredients = listOf(
        "глюкозный сироп", "фруктозный сироп", "кукурузный сироп", "сахар", "добавленный сахар",
        "пальмовое масло", "маргарин", "трансжиры", "гидрогенизированный жир",
        "консерванты", "сорбат калия", "бензоат натрия", "е200", "е211",
        "усилитель вкуса", "глутамат", "е621",
        "подсластитель", "аспартам", "сукралоза", "ацесульфам", "сахарин", "цикламаты",
        "е102", "е104", "е110", "е120", "е122", "е124", "е129", "е132", "е133", "е150",
        "ароматизатор", "эмульгатор", "стабилизатор"
    )

    private val healthyIndicators = listOf(
        "без консервантов", "без красителей", "без сахара", "без глутамата",
        "натуральный", "цельнозерновой", "на закваске",
        "органический", "эко", "био", "фермерский",
        "на стевии", "на эритрите", "богат клетчаткой"
    )

    override fun analise(ingredients: String): ProductAnalysesResult {
        val lowerCased = ingredients.lowercase()
        val healthyReasons = mutableListOf<String>()
        val unhealthyReasons = mutableListOf<String>()
        var score = 0

        harmfulIngredients.forEach { bad ->
            if (lowerCased.contains(bad)) {
                score -= 1
                unhealthyReasons.add(bad)
            }
        }

        healthyIndicators.forEach { good ->
            if (lowerCased.contains(good)) {
                score += 1
                healthyReasons.add(good)
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
            healthyReasons = healthyReasons,
            unhealthyReasons = unhealthyReasons
        )
    }
}
