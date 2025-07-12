package com.example.foodlens.presentation.camera

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import com.yalantis.ucrop.UCrop
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context get() = getApplication<Application>().applicationContext

    fun createPhotoFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        return File(context.externalCacheDir, "photo_$timeStamp.jpg")
    }

    fun getUriFromFile(file: File): Uri = file.toUri()

    fun startCrop(originalUri: Uri, launcher: ActivityResultLauncher<Intent>) {
        val destinationUri = Uri.fromFile(
            File(context.cacheDir, "cropped_${System.currentTimeMillis()}.jpg")
        )

        val intent = UCrop.of(originalUri, destinationUri)
            .withAspectRatio(0f, 0f)
            .withMaxResultSize(1080, 1920)
            .withOptions(UCrop.Options().apply {
                setFreeStyleCropEnabled(true)
            })
            .getIntent(context)

        launcher.launch(intent)
    }
}
