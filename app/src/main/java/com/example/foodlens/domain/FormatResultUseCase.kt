package com.example.foodlens.domain

import com.example.foodlens.domain.model.ProductAnalysesResult
import com.example.foodlens.domain.model.UiModel
import javax.inject.Inject

class FormatResultUseCase @Inject constructor() {
    operator fun invoke(result: ProductAnalysesResult): UiModel {
        return UiModel(
            score = result.score,
            rating = result.category.name,
            explanation = result.reason.joinToString("\n")
        )
    }
}
