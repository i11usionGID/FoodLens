package com.example.foodlens.domain.model

/**
 * Модель для конфигурации правил оценки состава продукта.
 *
 * Используется при загрузке `rules.json`, где перечислены вредные ингредиенты.
 *
 * @property harmfulIngredients Словарь ингредиентов и соответствующих параметров вредности.
 * Ключ — название ингредиента, значение — параметры [HarmfulIngredientFields].
 */
data class RuleModel(
    val harmfulIngredients: Map<String, HarmfulIngredientFields>
)