package com.example.foodlens.data

import android.net.Uri
import com.example.foodlens.domain.OcrRepository
import javax.inject.Inject

class OcrRepositoryImpl @Inject constructor() : OcrRepository {
    override fun extractImage(photoUri: Uri): String {
        return ""
    }
}