package com.example.foodlens.domain

import com.example.foodlens.domain.model.ProductAnalysesResult
import javax.inject.Inject

/**
 * UseCase (слой домена) для анализа текста состава продукта.
 *
 * Использует [RuleEngine] для анализа распознанного текста на наличие вредных ингредиентов.
 *
 * @property ruleEngine Движок правил, определяющий вредные ингредиенты.
 */
class AnaliseTextUseCase @Inject constructor(
    private val ruleEngine: RuleEngine
) {
    /**
     * Выполняет анализ текста ингредиентов.
     *
     * @param ingredients Текст, содержащий список ингредиентов.
     * @return Результат анализа состава продукта.
     */
    operator fun invoke(ingredients: String): ProductAnalysesResult {
        return ruleEngine.analise(ingredients)
    }
}