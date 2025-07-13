package com.example.foodlens.data

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.example.foodlens.domain.OcrRepository
import com.googlecode.tesseract.android.TessBaseAPI
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class OcrRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : OcrRepository {

    companion object {
        private const val TAG = "OcrRepositoryImpl"
        private const val TESS_LANG = "rus"
        private const val TESS_DATA_FOLDER = "tessdata"
    }

    private val tessBaseAPI: TessBaseAPI by lazy {
        setupTesseract()
    }

    override suspend fun extractImage(photoUri: Uri): String = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(photoUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            tessBaseAPI.setImage(bitmap)
            val text = tessBaseAPI.utF8Text
            return@withContext text

        } catch (e: Exception) {
            return@withContext ""
        }
    }

    private fun setupTesseract(): TessBaseAPI {
        val tessDir = File(context.filesDir, "tesseract/$TESS_DATA_FOLDER")
        if (!tessDir.exists()) {
            tessDir.mkdirs()
        }

        val trainedDataFile = File(tessDir, "$TESS_LANG.traineddata")
        if (!trainedDataFile.exists()) {
            copyTrainedDataIfNeeded(trainedDataFile)
        }

        val tessBasePath = File(context.filesDir, "tesseract").absolutePath
        val tess = TessBaseAPI()
        tess.init(tessBasePath, TESS_LANG)
        return tess
    }

    private fun copyTrainedDataIfNeeded(destFile: File) {
        try {
            context.assets.open("$TESS_DATA_FOLDER/${destFile.name}").use { input ->
                FileOutputStream(destFile).use { output ->
                    val buffer = ByteArray(1024)
                    var length: Int
                    while (input.read(buffer).also { length = it } > 0) {
                        output.write(buffer, 0, length)
                    }
                    output.flush()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Не удалось скопировать файл распознавания: ${e.message}", e)
        }
    }
}
