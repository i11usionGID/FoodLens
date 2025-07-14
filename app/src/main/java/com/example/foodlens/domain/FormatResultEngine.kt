package com.example.foodlens.domain

import com.example.foodlens.domain.model.ProductAnalysesResult
import com.example.foodlens.domain.model.UiModel

interface FormatResultEngine {

    fun formatResult(productAnalysesResult: ProductAnalysesResult): UiModel
}