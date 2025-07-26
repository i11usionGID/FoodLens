package com.example.foodlens.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.foodlens.presentation.navigation.NavGraph
import com.example.foodlens.presentation.ui.theme.FoodLensTheme
import dagger.hilt.android.AndroidEntryPoint


/**
 * Главная активность приложения **FoodLens**.
 *
 * Отвечает за:
 * - Запрос необходимых разрешений (в частности, на использование камеры),
 * - Инициализацию пользовательского интерфейса,
 * - Запуск основной навигационной структуры.
 *
 * Аннотирована как [AndroidEntryPoint], что позволяет использовать Hilt
 * для внедрения зависимостей внутри активити и дочерних компонентов.
 *
 * @constructor Создаёт экземпляр [MainActivity], унаследованный от [ComponentActivity].
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /**
     * Метод жизненного цикла, вызываемый при создании активности.
     *
     * Выполняет следующие действия:
     * 1. Включает поддержку режима edge-to-edge UI.
     * 2. Проверяет наличие разрешения на использование камеры; при необходимости запрашивает его у пользователя.
     * 3. Устанавливает тему приложения и инициализирует навигационный граф с помощью [NavGraph].
     *
     * @param savedInstanceState Состояние, сохранённое при предыдущем уничтожении активности (если было).
     */
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

        setContent {
            FoodLensTheme {
                NavGraph()
            }
        }
    }
}