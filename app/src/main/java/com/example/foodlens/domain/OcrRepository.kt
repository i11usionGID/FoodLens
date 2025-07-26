package com.example.foodlens.domain

import android.net.Uri

/**
 * Интерфейс репозитория OCR.
 *
 * Отвечает за извлечение текста с изображения.
 */
interface OcrRepository {
    /**
     * Выполняет OCR (распознавание текста) по изображению.
     *
     * @param photoUri URI изображения.
     * @return Распознанный текст.
     */
    suspend fun extractImage(photoUri: Uri): String
}