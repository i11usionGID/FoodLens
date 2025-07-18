package com.example.foodlens.presentation.screens.analise

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.delay

@Composable
fun FoodAnaliseScreen(
    modifier: Modifier = Modifier,
    photoUri: Uri,
    viewModel: FoodAnaliseViewModel = hiltViewModel(
        creationCallback = { factory: FoodAnaliseViewModel.Factory ->
            factory.create(photoUri)
        }
    ),
    onFoodAnaliseFinish: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var recommendationToBuying: List<String>
    var showResults by remember { mutableStateOf(false) }
    var selectedIngredient by remember { mutableStateOf<Pair<String, String>?>(null) }

    Scaffold(
        modifier = modifier
    ) { innerPadding ->
        Surface(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            when (val currentState = state) {
                is FoodAnaliseState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is FoodAnaliseState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentState.message,
                            color = Color.Red,
                            fontSize = 16.sp
                        )
                    }
                }

                is FoodAnaliseState.Success -> {
                    val result = currentState.result
                    var showTitle by remember { mutableStateOf(false) }
                    var showProgressBar by remember { mutableStateOf(false) }
                    var showFirstRecommendation by remember { mutableStateOf(false) }
                    var showSecondRecommendation by remember { mutableStateOf(false) }
                    var showUnhealthyIngredients by remember { mutableStateOf(false) }
                    var showFinishButton by remember { mutableStateOf(false) }
                    var startProgressAnimation by remember { mutableStateOf(false) }


                    val animatedProgress by animateFloatAsState(
                        targetValue = if (startProgressAnimation) result.healthPercent / 100f else 0f,
                        animationSpec = tween(durationMillis = 2500)
                    )
                    val barColor = when {
                        result.healthPercent < 40 -> {
                            recommendationToBuying = listOf(
                                "⚠\uFE0F Данный товар содержит большое количество вредных ингредиентов.",
                                "\uD83D\uDEAB К покупке не рекомендуется."
                            )
                            MaterialTheme.colorScheme.tertiaryContainer
                        }

                        result.healthPercent < 70 -> {
                            recommendationToBuying = listOf(
                                "❗ Данный товар содержит вредные ингредиенты.",
                                "🤔 Над покупкой стоит задуматься."
                            )
                            MaterialTheme.colorScheme.secondaryContainer
                        }

                        result.healthPercent == 100 -> {
                            recommendationToBuying = listOf(
                                "✅ Данный товар не содержит вредных ингредиентов.",
                                "\uD83D\uDED2 Рекомендуется к покупке."
                            )
                            MaterialTheme.colorScheme.primaryContainer
                        }

                        else -> {
                            recommendationToBuying = listOf(
                                "\uD83D\uDFE2 Данный товар содержит минимальное количество вредных ингредиентов.",
                                "\uD83D\uDC4D Рекомендуется к покупке."
                            )
                            MaterialTheme.colorScheme.primaryContainer
                        }
                    }

                    if (showResults) {
                        LaunchedEffect(Unit) {
                            delay(300)
                            showTitle = true
                            delay(1000)
                            showProgressBar = true
                            delay(500)
                            startProgressAnimation = true
                            delay(1500)
                            showFirstRecommendation = true
                            delay(1500)
                            showSecondRecommendation = true
                            delay(1500)
                            showUnhealthyIngredients = true
                            delay(1000)
                            showFinishButton = true
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {

                        Image(
                            painter = rememberAsyncImagePainter(photoUri),
                            contentDescription = "Фото ингредиентов",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        AnimatedVisibility(visible = !showResults) {
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 32.dp),
                                onClick = { showResults = true },
                                enabled = !showResults,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                    disabledContainerColor = MaterialTheme.colorScheme.primary,
                                    disabledContentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text(
                                    text = "Узнать результат анализа",
                                    fontSize = 16.sp
                                )
                            }
                        }

                        AnimatedVisibility(visible = showTitle) {
                            Column {
                                Text(
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    text = "Рекомендация к покупке",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        AnimatedVisibility(visible = showProgressBar) {
                            Row {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(30.dp)
                                        .background(
                                            MaterialTheme.colorScheme.secondary,
                                            RoundedCornerShape(16.dp)
                                        )
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(animatedProgress)
                                            .fillMaxHeight()
                                            .background(barColor, RoundedCornerShape(16.dp))
                                    )
                                }

                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    modifier = Modifier.align(Alignment.CenterVertically),
                                    text = "${result.healthPercent}%",
                                    fontSize = 20.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        AnimatedVisibility(visible = showFirstRecommendation) {
                            Text(
                                text = recommendationToBuying[0],
                                fontSize = 16.sp
                            )
                        }
                        AnimatedVisibility(visible = showSecondRecommendation) {
                            Text(
                                text = recommendationToBuying[1],
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        AnimatedVisibility(visible = showUnhealthyIngredients) {
                            Column {
                                if (result.unhealthyIngredients.isNotEmpty()) {
                                    Text(
                                        "Найдены вредные ингредиенты:",
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        result.unhealthyIngredients.forEach { (name, description) ->
                                            Button(
                                                onClick = {
                                                    selectedIngredient = name to description
                                                },
                                                shape = RoundedCornerShape(12.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.surface,
                                                    contentColor = MaterialTheme.colorScheme.onSurface
                                                )
                                            ) {
                                                Text(name)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        AnimatedVisibility(visible = showFinishButton) {
                            Button(
                                onClick = onFoodAnaliseFinish,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                    disabledContainerColor = MaterialTheme.colorScheme.primary,
                                    disabledContentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    text = "Вернуться на главный экран",
                                    fontSize = 16.sp
                                )
                            }
                        }
                        selectedIngredient?.let { (name, desc) ->
                            AlertDialog(
                                onDismissRequest = { selectedIngredient = null },
                                confirmButton = {
                                    Button(onClick = { selectedIngredient = null }) {
                                        Text("Закрыть")
                                    }
                                },
                                title = { Text(text = name) },
                                text = { Text(desc) },
                                containerColor = MaterialTheme.colorScheme.background
                            )
                        }
                    }
                }
            }
        }
    }
}