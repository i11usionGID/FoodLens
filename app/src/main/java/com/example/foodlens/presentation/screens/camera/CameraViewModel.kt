package com.example.foodlens.presentation.screens.camera

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val appContext: Application
) : ViewModel() {

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri

    private lateinit var imageCapture: ImageCapture

    fun setImageCapture(capture: ImageCapture) {
        imageCapture = capture
    }

    fun takePhoto(onSaved: (Uri) -> Unit) {
        val photoFile = File(
            appContext.externalCacheDir,
            "photo_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
        )
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(appContext),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("CameraViewModel", "Не получилось сделать фотографию: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = photoFile.toUri()
                    _imageUri.value = savedUri
                    onSaved(savedUri)
                }
            }
        )
    }
}
