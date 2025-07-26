package com.example.foodlens.presentation.screens.crop

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.yalantis.ucrop.UCrop
import java.io.File

/**
 * Экран для обрезки изображения с использованием библиотеки uCrop.
 *
 * @param inputUri URI исходного изображения, которое нужно обрезать.
 * @param onCropFinished Колбэк, вызываемый при успешной обрезке (результирующий URI).
 * @param onCropCancelled Колбэк, вызываемый, если пользователь отменил обрезку.
 */
@Composable
fun CropScreen(
    inputUri: Uri,
    onCropFinished: (Uri) -> Unit,
    onCropCancelled: () -> Unit
) {
    val context = LocalContext.current

    // Регистрация launcher'а для запуска внешнего Activity (uCrop)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val resultUri = UCrop.getOutput(result.data!!)
            if (resultUri != null) {
                onCropFinished(resultUri)
            }
        } else {
            onCropCancelled()
        }
    }

    // Запускаем обрезку изображения, как только получим входной URI
    LaunchedEffect(inputUri) {
        // Создаём URI для обрезанного изображения во временной папке
        val destinationUri = Uri.fromFile(
            File(context.cacheDir, "cropped_${System.currentTimeMillis()}.jpg")
        )

        // Настраиваем параметры для uCrop
        val options = UCrop.Options().apply {
            setCompressionQuality(100)
            setFreeStyleCropEnabled(true)
            setToolbarTitle("Обрезка")
        }

        // Создаём Intent для запуска uCrop с исходным URI
        val intent = UCrop.of(inputUri, destinationUri)
            .withOptions(options)
            .getIntent(context)

        // Запускаем uCrop через ранее зарегистрированный launcher
        launcher.launch(intent)
    }
}
