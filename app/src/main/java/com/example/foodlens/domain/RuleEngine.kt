package com.example.foodlens.domain

import com.example.foodlens.domain.model.ProductAnalysesResult

/**
 * Интерфейс движка анализа состава продукта.
 *
 * Используется для определения вредных ингредиентов на основе текста.
 */
interface RuleEngine {
    /**
     * Анализирует состав продукта на наличие вредных ингредиентов.
     *
     * @param ingredients Текст с перечислением ингредиентов.
     * @return Результат анализа состава.
     */
    fun analise(ingredients: String): ProductAnalysesResult
}