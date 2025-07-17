package com.example.foodlens.data

import android.content.Context
import com.example.foodlens.domain.RuleEngine
import com.example.foodlens.domain.model.HealthCategory
import com.example.foodlens.domain.model.ProductAnalysesResult
import com.example.foodlens.domain.model.RuleModel
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class RuleEngineImpl @Inject constructor(
    @ApplicationContext private val context: Context
): RuleEngine {

    private var harmfulIndicators: List<String> = emptyList()

    private var healthyIndicators: List<String> = emptyList()

    init {
        loadRuleConfig(context)
    }

    override fun analise(ingredients: String): ProductAnalysesResult {
        val lowerCased = ingredients.lowercase()
        val healthyReasons = mutableListOf<String>()
        val unhealthyReasons = mutableListOf<String>()
        var score = 0

        harmfulIndicators.forEach { bad ->
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
            score <= 1 -> HealthCategory.MODERATE
            else -> HealthCategory.HEALTH
        }

        return ProductAnalysesResult(
            score = score,
            category = category,
            healthyReasons = healthyReasons,
            unhealthyReasons = unhealthyReasons
        )
    }

    private fun loadRuleConfig(context: Context) {
        try {
            val inputStream = context.assets.open("rules.json")
            val json = inputStream.bufferedReader().use { it.readText() }
            val ruleModel = Gson().fromJson(json, RuleModel::class.java)
            harmfulIndicators = ruleModel.harmfulIndicators
            healthyIndicators = ruleModel.healthyIndicators
        } catch (e: Exception) {
            throw IllegalStateException("Не удалось загрузить файл с правилами.")
        }

    }
}
