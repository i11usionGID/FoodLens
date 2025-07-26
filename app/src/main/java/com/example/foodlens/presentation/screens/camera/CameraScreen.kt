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

/**
* Главный экран камеры. Включает:
 * - верхнюю черную панель [Box];
 * - превью камеры [CameraPreview];
 * - нижнюю черную панель [Box];
 * - кнопку для сохранения снимка [TakePhotoButton].
*
* @param modifier Модификатор для настройки внешнего вида.
* @param viewModel ViewModel, содержащий бизнес-логику камеры (инжектируется через Hilt).
* @param onTakePhotoClick Колбэк, вызываемый при успешной съёмке фотографии (возвращает Uri сделанной фотографии).
*/
@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel = hiltViewModel(),
    onTakePhotoClick: (Uri) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val extraColors = LocalExtendedColors.current

    //Следим за появлением URI изображения в viewModel и передаём его в onTakePhotoClick
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

/**
 * Компонент кнопки съёмки фотографии в виде двойного круга.
 *
 * @param extraColors Пользовательские цвета темы.
 * @param onTakePhotoClick Обработчик клика по кнопке.
 */
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

/**
 * Компонент предпросмотра камеры с инициализацией Capture и Preview.
 *
 * @param context Контекст, необходимый для инициализации камеры.
 * @param lifecycleOwner Владелец жизненного цикла, для привязки камеры.
 * @param onImageCaptureReady Колбэк, предоставляющий объект ImageCapture при готовности.
 */
@Composable
fun CameraPreview(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    onImageCaptureReady: (ImageCapture) -> Unit
) {
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    // Инициализация PreviewView (View, предоставляющий превью камеры)
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

    // Настройка камеры при инициализации
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