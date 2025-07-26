package com.example.foodlens.data

import android.content.Context
import com.example.foodlens.domain.RuleEngine
import com.example.foodlens.domain.model.HarmfulIngredientFields
import com.example.foodlens.domain.model.ProductAnalysesResult
import com.example.foodlens.domain.model.RuleModel
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Реализация движка правил [RuleEngine], определяющего вредные ингредиенты
 * и процент полезности на основе конфигурационного JSON-файла.
 *
 * Загружает правила из `rules.json` из assets и выполняет поиск вредных
 * компонентов в строке с ингредиентами.
 *
 * @property context Контекст приложения, предоставляемый через Hilt.
 */
class RuleEngineImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : RuleEngine {

    /**
     * Словарь вредных ингредиентов и их описаний, загружается из `rules.json`.
     *
     * Ключ — имя ингредиента в нижнем регистре, значение — [HarmfulIngredientFields].
     */
    private var harmfulIngredients: Map<String, HarmfulIngredientFields> = emptyMap()

    init {
        loadRuleConfig(context)
    }

    /**
     * Выполняет анализ строки с ингредиентами на наличие вредных компонентов.
     *
     * @param ingredients Строка с перечнем ингредиентов (может быть результатом OCR).
     * @return [ProductAnalysesResult], содержащий:
     * - исходный текст,
     * - процент полезности,
     * - список найденных вредных веществ с описаниями.
     */
    override fun analise(ingredients: String): ProductAnalysesResult {
        val lowerCased = ingredients.lowercase()
        val harmfulIngredients = mutableMapOf<String, String>()
        var healthPercent = 100

        this.harmfulIngredients.forEach { (key, value) ->
            if (lowerCased.contains(key)) {
                healthPercent -= value.harmfulPercent
                harmfulIngredients[key] = value.description
            }
        }

        if (healthPercent < 0) healthPercent = 0

        return ProductAnalysesResult(
            ocrText = ingredients,
            healthPercent = healthPercent,
            harmfulIngredients = harmfulIngredients
        )
    }

    /**
     * Загружает JSON-файл `rules.json` из assets и преобразует его в [RuleModel].
     * Обновляет локальное поле [harmfulIngredients].
     *
     * @param context Контекст приложения для доступа к ресурсам.
     * @throws IllegalStateException Если файл не найден или структура некорректна.
     */
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
