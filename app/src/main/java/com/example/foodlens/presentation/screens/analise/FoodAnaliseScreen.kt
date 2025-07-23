package com.example.foodlens.presentation.screens.analise

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.foodlens.R
import com.example.foodlens.presentation.ui.theme.ExtendedColors
import com.example.foodlens.presentation.ui.theme.LocalExtendedColors
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun FoodAnaliseScreen(
    modifier: Modifier = Modifier,
    photoUri: Uri,
    viewModel: FoodAnaliseViewModel = hiltViewModel(
        creationCallback = { factory: FoodAnaliseViewModel.Factory ->
            factory.create(photoUri)
        }
    ),
    onTryAgain: () -> Unit,
    onFoodAnaliseFinish: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showResults by remember { mutableStateOf(false) }
    var selectedIngredient by remember { mutableStateOf<Pair<String, String>?>(null) }
    val extraColors = LocalExtendedColors.current

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
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AnimatedMagnifierOverLogo()
                        Text(
                            text = "FoodLens",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Проверьте продукт на качество",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 16.sp
                        )
                    }
                }

                is FoodAnaliseState.Error -> {
                    var showError by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        delay(1000)
                        showError = true
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        AnimatedVisibility(visible = showError) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_scan_error),
                                contentDescription = "Не удалось распознать текст.",
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        AnimatedVisibility(visible = showError) {
                            Text(
                                text = "Не удалось найти текст на фотографии",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        AnimatedVisibility(visible = showError) {
                            Text(
                                text = "Убедитесь, что фото чёткое и хорошо освещено",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        AnimatedVisibility(visible = showError) {
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                onClick = onTryAgain,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                    disabledContainerColor = MaterialTheme.colorScheme.primary,
                                    disabledContentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text(
                                    text = "Попробовать еще раз",
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }

                is FoodAnaliseState.Success -> {
                    val result = currentState.result
                    var showPhotoBlock by remember { mutableStateOf(false) }
                    var showRecommendationBlock by remember { mutableStateOf(false) }
                    var showIndexDescription by remember { mutableStateOf(false) }
                    var showDividingLine by remember { mutableStateOf(false) }
                    var showUnhealthyIngredientsBlock by remember { mutableStateOf(false) }
                    showResults = true
                    val index = "${result.healthPercent}%"
                    val recommendation: String
                    val advice: List<String>
                    val indexColor: Color
                    val backgroundIndexColor: Color

                    when {
                        result.healthPercent <= 15 -> {
                            recommendation = "Не рекомендуется"
                            advice = listOf(
                                "Избегайте употребления этого продукта.",
                                "Рассмотрите более натуральные и полезные альтернативы.",
                                "Обратитесь к специалисту по питанию для подбора замены."
                            )
                            indexColor = extraColors.red300
                            backgroundIndexColor = extraColors.red100
                        }

                        result.healthPercent <= 35 -> {
                            recommendation = "Употребляйте умеренно"
                            advice = listOf(
                                "Ограничьте потребление до 1-2 порций в неделю.",
                                "Ищите альтернативы с натуральными ингредиентами.",
                                "Рассмотрите домашние варианты с более полезными ингредиентами."
                            )
                            indexColor = extraColors.red300
                            backgroundIndexColor = extraColors.red100
                        }

                        result.healthPercent <= 50 -> {
                            recommendation = "Употребляйте умеренно"
                            advice = listOf(
                                "Ограничьте потребление до 3-4 порций в неделю.",
                                "Обратите внимание на индивидуальные аллергены.",
                                "Следите за размером порций."
                            )
                            indexColor = extraColors.orange300
                            backgroundIndexColor = extraColors.orange100
                        }

                        result.healthPercent <= 75 -> {
                            recommendation = "Допустим"
                            advice = listOf(
                                "Можно употреблять регулярно, если сбалансирован с другими продуктами.",
                                "Следите за размером порций.",
                                "Обратите внимание на индивидуальные аллергены."
                            )
                            indexColor = extraColors.orange300
                            backgroundIndexColor = extraColors.orange100
                        }

                        result.healthPercent == 100 -> {
                            recommendation = "Отличный продукт!"
                            advice = emptyList()
                            indexColor = MaterialTheme.colorScheme.primary
                            backgroundIndexColor = extraColors.green300
                        }

                        else -> {
                            recommendation = "Хороший выбор"
                            advice = listOf(
                                "Подходит для регулярного употребления.",
                                "Содержит преимущественно натуральные ингредиенты.",
                                "Поддерживает общее здоровье при сбалансированном рационе."
                            )
                            indexColor = MaterialTheme.colorScheme.primary
                            backgroundIndexColor = extraColors.green300
                        }
                    }

                    val showRecommendations = remember(advice) {
                        List(advice.size) { mutableStateOf(false) }
                    }

                    if (showResults) {
                        LaunchedEffect(Unit) {
                            delay(1500)
                            showPhotoBlock = true
                            delay(1500)
                            showRecommendationBlock = true
                            delay(1000)
                            showIndexDescription = true
                            delay(1000)
                            showDividingLine = true
                            showRecommendations.forEachIndexed { _, state ->
                                state.value = true
                                delay(1000)
                            }
                            delay(500)
                            showUnhealthyIngredientsBlock = true
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "FoodLens",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Результаты анализа",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Box(
                                modifier = Modifier.clickable {
                                    onFoodAnaliseFinish()
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_exit),
                                    contentDescription = "Выход на главный экран",
                                    modifier = Modifier.size(24.dp),
                                    tint = extraColors.gray300
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        AnimatedVisibility(visible = showPhotoBlock) {
                            FoodAnaliseCard(
                                title = {
                                    CardTitle(
                                        textTitle = "Фотография",
                                        icon = R.drawable.ic_camera_filled,
                                        iconContentDescription = "Сделанная фотография",
                                        iconColor = MaterialTheme.colorScheme.primary
                                    )
                                },
                                content = {
                                    Image(
                                        painter = rememberAsyncImagePainter(photoUri),
                                        contentDescription = "Фотография состава",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        AnimatedVisibility(visible = showRecommendationBlock) {
                            FoodAnaliseCard(
                                title = {
                                    CardTitle(
                                        textTitle = "Рекомендации",
                                        icon = R.drawable.ic_bulb_filled,
                                        iconContentDescription = "Рекомендации",
                                        iconColor = MaterialTheme.colorScheme.secondary
                                    )
                                },
                                content = {
                                    Row(
                                        modifier.height(80.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(80.dp)
                                                .background(
                                                    color = backgroundIndexColor,
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = index,
                                                color = indexColor,
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.SemiBold,
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Column(
                                            modifier.fillMaxHeight(),
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "Индекс состава",
                                                color = extraColors.gray400,
                                                fontSize = 16.sp
                                            )
                                            AnimatedVisibility(visible = showIndexDescription) {
                                                Text(
                                                    text = recommendation,
                                                    color = indexColor,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }

                                    if (result.healthPercent < 100) {
                                        Spacer(modifier = Modifier.height(16.dp))

                                        AnimatedVisibility(visible = showDividingLine) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(2.dp)
                                                    .background(color = extraColors.gray100)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        advice.forEachIndexed { index, text ->
                                            AnimatedVisibility(visible = showRecommendations[index].value) {
                                                Column {
                                                    TextWithPoint(extraColors, text)
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        AnimatedVisibility(visible = showUnhealthyIngredientsBlock) {
                            FoodAnaliseCard(
                                title = {
                                    CardTitle(
                                        textTitle = "Вредные ингредиенты",
                                        icon = R.drawable.ic_exclamation_circle_filled,
                                        iconContentDescription = "Рекомендации",
                                        iconColor = extraColors.red200
                                    )
                                },
                                content = {
                                    Column {
                                        if (result.unhealthyIngredients.isEmpty()) {
                                            TextWithPoint(
                                                extraColors = extraColors,
                                                text = "Не обнаружено"
                                            )
                                        } else {
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
                                                            containerColor = extraColors.orange100,
                                                            contentColor = extraColors.orange300
                                                        )
                                                    ) {
                                                        Text(name)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            )
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

@Composable
fun FoodAnaliseCard(
    title: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 24.dp, horizontal = 16.dp)
        ) {
            title()
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun CardTitle(
    textTitle: String,
    icon: Int,
    iconContentDescription: String,
    iconColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = iconContentDescription,
            modifier = Modifier.size(24.dp),
            tint = iconColor
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = textTitle,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun TextWithPoint(
    extraColors: ExtendedColors,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_point),
            contentDescription = "Пункт рекомендации",
            modifier = Modifier
                .size(16.dp),
            tint = extraColors.green400
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            color = extraColors.gray400,
            fontSize = 16.sp
        )
    }
}

@Composable
fun AnimatedMagnifierOverLogo() {
    val angle = remember { Animatable(0f) }
    val density = LocalDensity.current

    LaunchedEffect(Unit) {
        while (true) {
            angle.animateTo(
                targetValue = 360f,
                animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
            )
            angle.snapTo(0f)
        }
    }

    val radiusPx = with(density) { 2.dp.toPx() }
    val angleRad = Math.toRadians(angle.value.toDouble())
    val offsetX = (cos(angleRad) * radiusPx).toFloat()
    val offsetY = (sin(angleRad) * radiusPx).toFloat()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_logo_milk),
            contentDescription = "Логотип",
            modifier = Modifier.size(120.dp),
            tint = Color.Unspecified
        )

        Icon(
            painter = painterResource(R.drawable.ic_logo_magnifier),
            contentDescription = "Лупа",
            modifier = Modifier
                .graphicsLayer {
                    translationX = offsetX
                    translationY = offsetY
                }
                .size(120.dp),
            tint = Color.Black
        )
    }
}
