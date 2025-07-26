package com.example.foodlens.domain.model

/**
 * Результат анализа продукта на основе распознанного текста ингредиентов.
 *
 * @property ocrText Исходный текст, полученный после OCR (распознавания текста).
 * @property healthPercent Процент "здоровости" продукта (100 — полностью безопасен).
 * @property harmfulIngredients Список вредных ингредиентов с пояснениями.
 * Ключ — название ингредиента, значение — описание вреда.
 */
data class ProductAnalysesResult(
    val ocrText: String,
    val healthPercent: Int,
    val harmfulIngredients: Map<String, String>
)