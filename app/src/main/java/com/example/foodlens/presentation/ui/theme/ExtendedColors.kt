package com.example.foodlens.presentation.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class ExtendedColors(
    val black: Color,
    val red100: Color,
    val red200: Color,
    val red300: Color,
    val orange100: Color,
    val orange200: Color,
    val orange300: Color,
    val green300: Color,
    val green400: Color,
    val blue100: Color,
    val blue200: Color,
    val purple100: Color,
    val purple200: Color,
    val gray100: Color,
    val gray200: Color,
    val gray300: Color,
    val gray400: Color
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        black = Black,
        red100 = Red100,
        red200 = Red200,
        red300 = Red300,
        orange100 = Orange100,
        orange200 = Orange200,
        orange300 = Orange300,
        green300 = Green300,
        green400 = Green400,
        blue100 = Blue100,
        blue200 = Blue200,
        purple100 = Purple100,
        purple200 = Purple200,
        gray100 = Gray100,
        gray200 = Gray200,
        gray300 = Gray300,
        gray400 = Gray400
    )
}