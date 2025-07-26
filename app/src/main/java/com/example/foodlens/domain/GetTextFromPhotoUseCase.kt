package com.example.foodlens.domain

import android.net.Uri
import javax.inject.Inject

/**
 * UseCase для получения текста с изображения (OCR).
 *
 * Использует [OcrRepository] для распознавания текста с изображения.
 *
 * @property ocrRepository Репозиторий, предоставляющий OCR-функциональность.
 */
class GetTextFromPhotoUseCase @Inject constructor(
    private val ocrRepository: OcrRepository
){
    /**
     * Распознаёт текст на изображении по заданному URI.
     *
     * @param photoUri URI изображения.
     * @return Распознанный текст.
     */
    suspend operator fun invoke(photoUri: Uri): String {
        return ocrRepository.extractImage(photoUri)
    }
}