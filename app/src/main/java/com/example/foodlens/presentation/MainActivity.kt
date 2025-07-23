package com.example.foodlens.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.foodlens.presentation.navigation.NavGraph
import com.example.foodlens.presentation.ui.theme.FoodLensTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var onImagePicked: ((Uri) -> Unit)? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                1001
            )
        }

        imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            uri?.let { onImagePicked?.invoke(it) }
        }

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                imagePickerLauncher.launch("image/*")
            }
        }

        setContent {
            FoodLensTheme {
                NavGraph(
                    onGalleryImagePicked = { callback ->
                        requestGalleryPermission(callback)
                    }
                )
            }
        }
    }

    private fun requestGalleryPermission(callback: (Uri) -> Unit) {
        val permission = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                Manifest.permission.READ_MEDIA_IMAGES
            }

            else -> {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            onImagePicked = callback
            imagePickerLauncher.launch("image/*")
        } else {
            onImagePicked = callback
            permissionLauncher.launch(permission)
        }
    }
}