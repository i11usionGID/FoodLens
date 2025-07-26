package com.example.foodlens.presentation.navigation

import android.net.Uri
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foodlens.presentation.screens.analise.FoodAnaliseScreen
import com.example.foodlens.presentation.screens.camera.CameraScreen
import com.example.foodlens.presentation.screens.crop.CropScreen
import com.example.foodlens.presentation.screens.welcome.WelcomeScreen
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


/**
 * Компонент навигационного графа приложения **FoodLens**.
 *
 * Отвечает за настройку маршрутов и управление переходами между экранами с помощью NavController.
 * Использует [NavHost] для определения маршрутов и сопоставленных им экранов.
 *
 * Включает следующие маршруты:
 * - [Screen.Welcome]: Стартовый экран, с которого пользователь может выбрать источник изображения.
 * - [Screen.Camera]: Экран камеры, где можно сделать снимок.
 * - [Screen.Crop]: Экран обрезки изображения с использованием библиотеки uCrop.
 * - [Screen.FoodAnalise]: Экран анализа изображения после обрезки.
 *
 * Каждый маршрут передаёт необходимые параметры (например, [Uri]) через аргументы навигации.
 *
 * Навигация осуществляется с помощью [rememberNavController], навигационные действия инициируются
 * через лямбды, передаваемые в соответствующие Composable-функции.
 *
 * @see Screen — объект, содержащий описание маршрутов.
 * @see WelcomeScreen — экран выбора источника изображения.
 * @see CameraScreen — экран камеры.
 * @see CropScreen — экран обрезки изображения.
 * @see FoodAnaliseScreen — экран анализа изображения.
 */
@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onOpenCameraClick = {
                    navController.navigate(Screen.Camera.route)
                },
                onTakePhotoFromGalleryClick = {
                    navController.navigate(Screen.Crop.createRoute(it))
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
                    navController.navigate(Screen.FoodAnalise.createRoute(croppedUri)) {
                        popUpTo(Screen.Crop.route) {
                            inclusive = true
                        }
                    }
                },
                onCropCancelled = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.FoodAnalise.route) {
            val croppedPhotoUri = Screen.FoodAnalise.getUri(it.arguments)
            FoodAnaliseScreen(
                photoUri = croppedPhotoUri,
                onFoodAnaliseFinish = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

/**
 * Список экранов навигации приложения.
 *
 * Каждый экран представляет отдельный маршрут.
 *
 * @property route Строка-маршрут, используемая навигационным контроллером.
 */
sealed class Screen(val route: String) {

    /**
     * Экран приветствия (стартовый экран).
     */
    data object Welcome : Screen("welcome")

    /**
     * Экран камеры для создания фотографии.
     */
    data object Camera : Screen("camera")

    /**
     * Экран обрезки изображения.
     *
     * Ожидает параметр маршрута: {photoUri}
     */
    data object Crop : Screen("crop/{photoUri}") {

        /**
         * Создаёт маршрут для перехода на экран обрезки, кодируя [photoUri].
         *
         * @param photoUri URI изображения.
         * @return Строка маршрута.
         */
        fun createRoute(photoUri: Uri): String {
            val encodedUri =
                URLEncoder.encode(photoUri.toString(), StandardCharsets.UTF_8.toString())
            return "crop/$encodedUri"
        }

        /**
         * Извлекает [Uri] из аргументов маршрута.
         *
         * @param arguments Аргументы, переданные в экран.
         * @return Декодированный URI изображения.
         */
        fun getUri(arguments: Bundle?): Uri {
            val encoded = arguments?.getString("photoUri") ?: return Uri.EMPTY
            val decoded = URLDecoder.decode(encoded, StandardCharsets.UTF_8.toString())
            return Uri.parse(decoded)
        }
    }

    /**
     * Экран анализа состава продукта.
     *
     * Ожидает параметр маршрута: {croppedPhotoUri}
     */
    data object FoodAnalise : Screen("food_analise/{croppedPhotoUri}") {

        /**
         * Создаёт маршрут для экрана анализа, кодируя [croppedPhotoUri].
         *
         * @param croppedPhotoUri URI обрезанного изображения.
         * @return Строка маршрута.
         */
        fun createRoute(croppedPhotoUri: Uri): String {
            val encodedUri =
                URLEncoder.encode(croppedPhotoUri.toString(), StandardCharsets.UTF_8.toString())
            return "food_analise/$encodedUri"
        }

        /**
         * Извлекает [Uri] из аргументов маршрута.
         *
         * @param arguments Аргументы, переданные в экран.
         * @return Декодированный URI изображения.
         */
        fun getUri(arguments: Bundle?): Uri {
            val encoded = arguments?.getString("croppedPhotoUri") ?: return Uri.EMPTY
            val decoded = URLDecoder.decode(encoded, StandardCharsets.UTF_8.toString())
            return Uri.parse(decoded)
        }
    }
}