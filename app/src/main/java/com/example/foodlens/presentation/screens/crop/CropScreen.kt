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

@Composable
fun CropScreen(
    inputUri: Uri,
    onCropFinished: (Uri) -> Unit,
    onCropCancelled: () -> Unit
) {
    val context = LocalContext.current

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
    LaunchedEffect(inputUri) {
        val destinationUri = Uri.fromFile(File(context.cacheDir, "cropped_${System.currentTimeMillis()}.jpg"))
        val options = UCrop.Options().apply {
            setCompressionQuality(80)
            setFreeStyleCropEnabled(true)
            setToolbarTitle("Обрезка")
        }

        val intent = UCrop.of(inputUri, destinationUri)
            .withOptions(options)
            .getIntent(context)

        launcher.launch(intent)
    }
}
