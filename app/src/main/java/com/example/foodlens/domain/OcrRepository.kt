package com.example.foodlens.domain

import android.net.Uri

interface OcrRepository {

    fun extractImage(photoUri: Uri): String
}