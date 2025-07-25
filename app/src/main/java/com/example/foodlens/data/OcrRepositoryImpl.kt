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

/**
 * Реализация интерфейса [OcrRepository], обеспечивающая:
 * - предобработку изображений с помощью OpenCV,
 * - распознавание текста с использованием Tesseract OCR.
 *
 * Использует Hilt для внедрения контекста приложения.
 *
 * @constructor Инициализирует класс и загружает библиотеку OpenCV.
 * @property context Контекст приложения, внедряется через Hilt.
 */
class OcrRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : OcrRepository {

    companion object {
        /** Язык OCR (русский), используется при инициализации Tesseract. */
        private const val TESS_LANG = "rus"

        /** Папка с .traineddata файлами в assets. */
        private const val TESS_DATA_FOLDER = "tessdata"

        /** Коэффициент, по которому определяется мусорный текст. */
        private const val GARBAGE_COEFFICIENT = 3
    }

    init {
        // Загрузка библиотеки OpenCV
        System.loadLibrary("opencv_java4")
    }

    /** Ленивая инициализация Tesseract OCR. */
    private val tess: TessBaseAPI by lazy { setupTesseract() }

    /**
     * Основной метод OCR. Извлекает текст из изображения, расположенного по [photoUri].
     * Выполняет поворот, предобработку и распознавание.
     *
     * @param photoUri URI изображения, полученного из галереи или камеры.
     * @return Распознанный и очищенный текст.
     * @throws IllegalArgumentException если изображение не найдено или текст не читаемый.
     */
    override suspend fun extractImage(photoUri: Uri): String = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(photoUri)
            ?: throw IllegalArgumentException("Не удалось открыть изображение")

        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        val correctedBitmap = fixRotationIfNeeded(photoUri, originalBitmap)
        val preprocessed = preprocess(correctedBitmap)

        tess.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO_OSD)

        tess.setImage(preprocessed)

        val text = tess.utF8Text?.trim().orEmpty()

        // Проверка на "мусорный" текст
        val letters = text.filter { it.isLetterOrDigit() }
        val isGarbageText = letters.length * GARBAGE_COEFFICIENT < text.length || letters.length < 5

        if (text.isBlank() || isGarbageText) {
            throw IllegalArgumentException("На изображении не найден читаемый текст")
        }
        return@withContext text
    }

    /**
     * Предобрабатывает изображение для повышения качества OCR.
     * Алгоритм включает увеличение размера, перевод в градации серого,
     * адаптивную бинаризацию и инвертирование при необходимости.
     *
     * Также определяет наличие текстурного фона и применяет морфологические операции.
     *
     * @param bitmap Входное изображение.
     * @return Обработанный bitmap, готовый для OCR.
     */
    private fun preprocess(bitmap: Bitmap): Bitmap {
        val src = Mat()
        Utils.bitmapToMat(bitmap, src)

        // Увеличение размера
        Imgproc.resize(src, src, Size(), 2.0, 2.0, Imgproc.INTER_CUBIC)

        val gray = Mat()
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY)

        val processedGray = Mat()
        val originalGray = gray.clone()

        // Анализ фона
        if (isTexturedBackground(gray)) {
            Imgproc.medianBlur(gray, gray, 3)
            Imgproc.adaptiveThreshold(
                gray, processedGray, 255.0,
                Imgproc.ADAPTIVE_THRESH_MEAN_C,
                Imgproc.THRESH_BINARY,
                15, 10.0
            )

            val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(2.0, 2.0))
            Imgproc.morphologyEx(processedGray, processedGray, Imgproc.MORPH_OPEN, kernel)
        } else {
            Imgproc.GaussianBlur(gray, processedGray, Size(3.0, 3.0), 0.0)
        }

        // Инверсия при тёмном фоне
        val mean = Core.mean(originalGray).`val`[0]
        if (mean < 127) Core.bitwise_not(processedGray, processedGray)

        val result =
            Bitmap.createBitmap(processedGray.cols(), processedGray.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(processedGray, result)

        return result
    }

    /**
     * Определяет, содержит ли изображение "шумный" или текстурный фон.
     * Основано на вычислении стандартного отклонения яркости.
     *
     * @param gray Серое изображение.
     * @return true, если фон шумный; false — если равномерный.
     */
    private fun isTexturedBackground(gray: Mat): Boolean {
        val blurred = Mat()
        Imgproc.GaussianBlur(gray, blurred, Size(5.0, 5.0), 0.0)

        val mean = MatOfDouble()
        val stddev = MatOfDouble()
        Core.meanStdDev(blurred, mean, stddev)

        val stddevVal = stddev.toArray()[0]
        Log.d("STDDEV_CHECK", "StdDev = $stddevVal")

        return stddevVal > 40
    }


    /**
     * Корректирует поворот изображения на основе EXIF-метаданных, если необходимо.
     *
     * @param uri URI изображения.
     * @param bitmap Битмап, полученный из URI.
     * @return Исправленное изображение.
     */
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
            androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(
                90f
            )

            androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(
                180f
            )

            androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(
                270f
            )

            else -> return bitmap
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * Инициализирует и настраивает Tesseract.
     *
     * @return Экземпляр [TessBaseAPI].
     */
    private fun setupTesseract(): TessBaseAPI {
        val tessDir = File(context.filesDir, "tesseract/$TESS_DATA_FOLDER")
        if (!tessDir.exists()) tessDir.mkdirs()
        val trained = File(tessDir, "$TESS_LANG.traineddata")
        if (!trained.exists()) copyTrainedDataIfNeeded(trained)
        return TessBaseAPI().apply {
            init(context.filesDir.resolve("tesseract").absolutePath, TESS_LANG)
        }
    }

    /**
     * Копирует .traineddata из assets в файловую систему приложения.
     *
     * @param dest Файл, в который будет произведено копирование.
     */
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
