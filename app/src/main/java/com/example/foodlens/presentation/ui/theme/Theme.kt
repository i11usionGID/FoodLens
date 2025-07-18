package com.example.foodlens.presentation.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
)

private val LightColorScheme = lightColorScheme(
    primary = Orange100,
    onPrimary = White,
    secondary = Gray100,
    onSecondary = White,
    background = White,
    onBackground = Black,
    surface = MistyRose,
    onSurface = Black,
    surfaceVariant = Linen,
    onSurfaceVariant = Black,
    primaryContainer = Green100,
    secondaryContainer = Yellow100,
    tertiaryContainer = Red100
)

@Composable
fun FoodLensTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}