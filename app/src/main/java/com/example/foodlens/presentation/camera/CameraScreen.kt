package com.example.foodlens.presentation.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    onTakePhotoClick: () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color.Black)
            )
            Box(modifier = Modifier.weight(1f)) {
                // CameraPreview() или другое содержимое
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                TakePhotoButton(onTakePhotoClick)
            }
        }
    }
}

@Composable
fun TakePhotoButton(
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
                .background(Color.White, shape = CircleShape)
        )
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(Color.LightGray, shape = CircleShape)
        )
    }
}