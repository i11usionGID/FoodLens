package com.example.foodlens.domain

import com.example.foodlens.domain.model.ProductAnalysesResult
import com.example.foodlens.domain.model.UiModel
import javax.inject.Inject

class FormatResultUseCase @Inject constructor(
    private val formatResultEngine: FormatResultEngine
) {

    operator fun invoke(productAnalysesResult: ProductAnalysesResult): UiModel {
        return formatResultEngine.formatResult(productAnalysesResult)
    }
}