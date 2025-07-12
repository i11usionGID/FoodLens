package com.example.foodlens.domain

import android.net.Uri

interface OcrRepository {

    suspend fun extractImage(photoUri: Uri): String
}