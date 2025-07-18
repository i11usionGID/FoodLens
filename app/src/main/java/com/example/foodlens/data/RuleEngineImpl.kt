package com.example.foodlens.data

import android.content.Context
import com.example.foodlens.domain.RuleEngine
import com.example.foodlens.domain.model.HarmfulIngredientFields
import com.example.foodlens.domain.model.ProductAnalysesResult
import com.example.foodlens.domain.model.RuleModel
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class RuleEngineImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : RuleEngine {

    private var harmfulIngredients: Map<String, HarmfulIngredientFields> = emptyMap()


    init {
        loadRuleConfig(context)
    }

    override fun analise(ingredients: String): ProductAnalysesResult {
        val lowerCased = ingredients.lowercase()
        val unhealthyIngredients = mutableMapOf<String, String>()
        var healthPercent = 100

        harmfulIngredients.forEach { (key, value) ->
            if (lowerCased.contains(key)) {
                healthPercent -= value.harmfulPercent
                unhealthyIngredients[key] = value.description
            }
        }

        if (healthPercent < 0) healthPercent = 0

        return ProductAnalysesResult(
            healthPercent = healthPercent,
            unhealthyIngredients = unhealthyIngredients
        )
    }

    private fun loadRuleConfig(context: Context) {
        try {
            val inputStream = context.assets.open("rules.json")
            val json = inputStream.bufferedReader().use { it.readText() }
            val ruleModel = Gson().fromJson(json, RuleModel::class.java)
            harmfulIngredients = ruleModel.harmfulIngredients
        } catch (e: Exception) {
            throw IllegalStateException("Не удалось загрузить файл с правилами: ${e.message}", e)
        }
    }
}
