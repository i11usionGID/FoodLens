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
    }

    init {
        System.loadLibrary("opencv_java4")
    }

    private val tess: TessBaseAPI by lazy { setupTesseract() }

    override suspend fun extractImage(photoUri: Uri): String = withContext(Dispatchers.IO) {
        try {
            val isStream = context.contentResolver.openInputStream(photoUri)!!
            val bmp = BitmapFactory.decodeStream(isStream)
            val pre = preprocess(bmp)
            tess.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_BLOCK)
            tess.setImage(pre)
            val text = tess.utF8Text
            Log.d(TAG, text)
            return@withContext text
        } catch (e: Exception) {
            Log.e(TAG, "OCR failed", e)
            return@withContext ""
        }
    }

    private fun preprocess(bitmap: Bitmap): Bitmap {
        val src = Mat()
        Utils.bitmapToMat(bitmap, src)
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY)
        Imgproc.GaussianBlur(src, src, Size(5.0, 5.0), 0.0)
        Imgproc.threshold(src, src, 0.0, 255.0, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU)
        val processed = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(src, processed)
        return processed
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
            Log.e(TAG, "Copy tessdata failed", e)
        }
    }
}
