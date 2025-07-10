package com.example.foodlens.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.foodlens.presentation.navigation.NavGraph
import com.example.foodlens.presentation.ui.theme.FoodLensTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodLensTheme {
                NavGraph()
            }
        }
    }
}