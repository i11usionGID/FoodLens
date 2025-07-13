package com.example.foodlens.presentation.crop

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.yalantis.ucrop.UCrop
import java.io.File

@Composable
fun CropScreen(
    inputUri: Uri,
    onCropFinished: (Uri) -> Unit
) {
    val context = LocalContext.current
    var croppedImageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val resultUri = UCrop.getOutput(result.data!!)
            if (resultUri != null) {
                croppedImageUri = resultUri
                onCropFinished(resultUri)
            }
        } else if (result.resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(result.data!!)
            println("Crop error: $cropError")
        }
    }
    LaunchedEffect(inputUri) {
        val destinationUri = Uri.fromFile(File(context.cacheDir, "cropped_${System.currentTimeMillis()}.jpg"))
        val options = UCrop.Options().apply {
            setCompressionQuality(80)
            setFreeStyleCropEnabled(true)
        }

        val intent = UCrop.of(inputUri, destinationUri)
            .withOptions(options)
            .getIntent(context)

        launcher.launch(intent)
    }
}
