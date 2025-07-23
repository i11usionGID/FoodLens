package com.example.foodlens.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.example.foodlens.domain.OcrRepository
import com.googlecode.tesseract.android.TessBaseAPI
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
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
        private const val GARBAGE_COEFFICIENT = 5
    }

    init {
        System.loadLibrary("opencv_java4")
    }

    private val tess: TessBaseAPI by lazy { setupTesseract() }

    override suspend fun extractImage(photoUri: Uri): String = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(photoUri)
            ?: throw IllegalArgumentException("Не удалось открыть изображение")

        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        val correctedBitmap = fixRotationIfNeeded(photoUri, originalBitmap)
        val preprocessed = preprocess(correctedBitmap)

        tess.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO_OSD)

        tess.setImage(preprocessed)

        val text = tess.utF8Text?.trim().orEmpty()
        Log.d(TAG, "OCR result: $text")

        val letters = text.filter { it.isLetterOrDigit() }
        val isGarbageText = letters.length * GARBAGE_COEFFICIENT < text.length || letters.length < 5

        if (text.isBlank() || isGarbageText) {
            throw IllegalArgumentException("На изображении не найден читаемый текст")
        }

        return@withContext text
    }

    private fun preprocess(bitmap: Bitmap): Bitmap {
        val src = Mat()
        Utils.bitmapToMat(bitmap, src)
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY)
        Imgproc.GaussianBlur(src, src, Size(5.0, 5.0), 0.0)
        Imgproc.threshold(src, src, 0.0, 255.0, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU)
        val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(3.0, 3.0))
        Imgproc.morphologyEx(src, src, Imgproc.MORPH_OPEN, kernel)
        val mean = Core.mean(src).`val`[0]
        if (mean < 127) Core.bitwise_not(src, src)
        val processed = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(src, processed)
        return processed
    }

    private fun fixRotationIfNeeded(uri: Uri, bitmap: Bitmap): Bitmap {
        val exif = context.contentResolver.openInputStream(uri)?.use {
            androidx.exifinterface.media.ExifInterface(it)
        }

        val orientation = exif?.getAttributeInt(
            androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION,
            androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL
        )

        val matrix = android.graphics.Matrix()
        when (orientation) {
            androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            else -> return bitmap
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun setupTesseract(): TessBaseAPI {
        val tessDir = File(context.filesDir, "tesseract/$TESS_DATA_FOLDER")
        if (!tessDir.exists()) tessDir.mkdirs()
        val trained = File(tessDir, "$TESS_LANG.traineddata")
        if (!trained.exists()) copyTrainedDataIfNeeded(trained)
        return TessBaseAPI().apply {
            init(context.filesDir.resolve("tesseract").absolutePath, TESS_LANG)
        }
    }

    private fun copyTrainedDataIfNeeded(dest: File) {
        try {
            context.assets.open("$TESS_DATA_FOLDER/${dest.name}").use { input ->
                FileOutputStream(dest).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: Exception) {
            throw IllegalStateException("Ошибка загрузки Tesseract traineddata: ${e.message}", e)
        }
    }
}
