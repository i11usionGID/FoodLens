package com.example.foodlens.domain

import android.net.Uri
import javax.inject.Inject

class GetTextFromPhotoUseCase @Inject constructor(
    private val ocrRepository: OcrRepository
){

    operator fun invoke(photoUri: Uri): String {
        return ocrRepository.extractImage(photoUri)
    }
}