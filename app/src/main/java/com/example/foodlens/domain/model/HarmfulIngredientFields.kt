package com.example.foodlens.domain.model

/**
 * Модель описывает параметры вредного ингредиента.
 *
 * @property harmfulPercent Процент снижения оценки здоровья при обнаружении ингредиента.
 * @property description Текстовое описание, объясняющее вред ингредиента.
 */
data class HarmfulIngredientFields(
    val harmfulPercent: Int,
    val description: String
)
