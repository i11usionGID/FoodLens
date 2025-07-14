package com.example.foodlens.presentation.navigation

import android.net.Uri
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foodlens.presentation.analise.FoodAnaliseScreen
import com.example.foodlens.presentation.camera.CameraScreen
import com.example.foodlens.presentation.crop.CropScreen
import com.example.foodlens.presentation.welcome.WelcomeScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onCameraButtonClick = {
                    navController.navigate(Screen.Camera.route)
                }
            )
        }

        composable(Screen.Camera.route) {
            CameraScreen(
                onTakePhotoClick = {
                    navController.navigate(Screen.Crop.createRoute(it))
                }
            )
        }

        composable(Screen.Crop.route) {
            val inputUri = Screen.Crop.getUri(it.arguments)
            CropScreen(
                inputUri = inputUri,
                onCropFinished = { croppedUri ->
                    navController.navigate(Screen.FoodAnalise.createRoute(croppedUri))
                }
            )
        }

        composable(Screen.FoodAnalise.route) {
            val croppedPhotoUri = Screen.FoodAnalise.getUri(it.arguments)
            FoodAnaliseScreen(
                photoUri = croppedPhotoUri
            )
        }
    }
}

sealed class Screen(val route: String) {

    data object Welcome: Screen("welcome")

    data object Camera: Screen("camera")

    data object Crop : Screen("crop/{photoUri}") {
        fun createRoute(photoUri: Uri): String {
            val encodedUri = URLEncoder.encode(photoUri.toString(), StandardCharsets.UTF_8.toString())
            return "crop/$encodedUri"
        }

        fun getUri(arguments: Bundle?): Uri {
            val encoded = arguments?.getString("photoUri") ?: return Uri.EMPTY
            val decoded = URLDecoder.decode(encoded, StandardCharsets.UTF_8.toString())
            return Uri.parse(decoded)
        }
    }


    data object FoodAnalise: Screen("food_analise/{croppedPhotoUri}") {

        fun createRoute(croppedPhotoUri: Uri): String {
            val encodedUri = URLEncoder.encode(croppedPhotoUri.toString(), StandardCharsets.UTF_8.toString())
            return "food_analise/$encodedUri"
        }

        fun getUri(arguments: Bundle?): Uri {
            val encoded = arguments?.getString("croppedPhotoUri") ?: return Uri.EMPTY
            val decoded = URLDecoder.decode(encoded, StandardCharsets.UTF_8.toString())
            return Uri.parse(decoded)
        }
    }
}