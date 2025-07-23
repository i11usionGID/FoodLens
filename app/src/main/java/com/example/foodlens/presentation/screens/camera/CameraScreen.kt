package com.example.foodlens.presentation.screens.camera

import android.content.Context
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.foodlens.presentation.ui.theme.ExtendedColors
import com.example.foodlens.presentation.ui.theme.LocalExtendedColors

@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel = hiltViewModel(),
    onTakePhotoClick: (Uri) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val extraColors = LocalExtendedColors.current

    LaunchedEffect(viewModel.imageUri.collectAsState().value) {
        viewModel.imageUri.value?.let(onTakePhotoClick)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(extraColors.black)
            )
            Box(modifier = Modifier.weight(1f)) {
                CameraPreview(
                    context = context,
                    lifecycleOwner = lifecycleOwner,
                    onImageCaptureReady = { viewModel.setImageCapture(it) }
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(extraColors.black),
                contentAlignment = Alignment.Center
            ) {
                TakePhotoButton(
                    extraColors = extraColors,
                    onTakePhotoClick = {
                        viewModel.takePhoto {}
                    }
                )
            }
        }
    }
}

@Composable
fun TakePhotoButton(
    extraColors: ExtendedColors,
    onTakePhotoClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .size(80.dp)
            .clickable { onTakePhotoClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(
                    color = extraColors.gray200,
                    shape = CircleShape
                )
        )
    }
}

@Composable
fun CameraPreview(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    onImageCaptureReady: (ImageCapture) -> Unit
) {
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val previewView = remember {
        PreviewView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    AndroidView(
        factory = {
            previewView
        }
    )

    LaunchedEffect(cameraProviderFuture) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }
        val imageCapture = ImageCapture.Builder().build()
        onImageCaptureReady(imageCapture)

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )
    }
}