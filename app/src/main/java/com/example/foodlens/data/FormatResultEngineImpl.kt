package com.example.foodlens.data

import com.example.foodlens.domain.FormatResultEngine
import com.example.foodlens.domain.model.ProductAnalysesResult
import com.example.foodlens.domain.model.UiModel
import javax.inject.Inject

class FormatResultEngineImpl @Inject constructor(): FormatResultEngine {

    override fun formatResult(productAnalysesResult: ProductAnalysesResult): UiModel {
        val healthCategory = "Категория полезности: ${productAnalysesResult.category.categoryName}"
        val healthyReasons = "Полезные ингредиенты: ${formatReasons(productAnalysesResult.healthyReasons)}"
        val unhealthyReasons = "Вредные ингредиенты: ${formatReasons(productAnalysesResult.unhealthyReasons)}"
        return UiModel(
            healthCategory = healthCategory,
            healthyReasons = healthyReasons,
            unhealthyReasons = unhealthyReasons
        )
    }

    private fun formatReasons(reasons: List<String>): String {
        val result = when (reasons.size) {
            0 -> "отсутсвуют"
            else -> {
                var ingredients = ""
                for (reason in reasons) {
                    ingredients += "\n" + reason
                }
                ingredients
            }
        }
        return result
    }
}