package com.example.foodlens.presentation.screens.analise

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.draw.clip
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
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Экран показа результатов анализа продуктов. Отображает одно из трёх состояний в зависимости от результата анализа:
 * - [FoodAnaliseState.Loading] — загрузка. Отображается анимированный с помощью функции [AnimatedMagnifierOverLogo] логотип, название и слоган приложения.
 * - [FoodAnaliseState.Error] — ошибка. Отображается иконка ошибки, советы по исправлению и кнопка для возвращения на главный экран.
 * - [FoodAnaliseState.Success] — успешно. Отображаются карточки с результатами анализа, включая [FoodAnaliseCard].
 *
 * @param modifier модификатор внешнего вида.
 * @param photoUri URI исходной фотографии.
 * @param viewModel ViewModel с состоянием анализа, создаётся с помощью Hilt.
 * @param onFoodAnaliseFinish колбэк при завершении анализа или ошибке — возвращает на главный экран.
 */
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
    /**
     * - state — состояние экрана, получаемое из [FoodAnaliseViewModel].
     * - extraColors — дополнительные цвета, не входящие в [MaterialTheme.colorScheme].
     */
    val state by viewModel.state.collectAsState()
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
                        modifier = Modifier
                            .fillMaxSize(),
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
                    /**
                     * showError — флаг для начала анимации и показа элементов экрана. Запускается по истечении 1 секунды.
                     */
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
                                onClick = onFoodAnaliseFinish,
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
                    /**
                     * - result — результат анализа состава ([com.example.foodlens.domain.model.ProductAnalysesResult]), полученный из [FoodAnaliseViewModel] при успешной обработке.
                     * - showResults — флаг для начала анимации и показа карточек.
                     * - advice — список рекомендаций, определяемый по индексу полезности.
                     * - recommendation — текст рекомендации по покупке товара, определяемый по индексу полезности.
                     */
                    val result = currentState.result
                    var showResults by remember { mutableStateOf(false) }
                    val advice: List<String>
                    val recommendation: String

                    when {
                        result.healthPercent <= 15 -> {
                            recommendation = "Не рекомендуется"
                            advice = listOf(
                                "Избегайте употребления этого продукта.",
                                "Рассмотрите более натуральные и полезные альтернативы.",
                                "Обратитесь к специалисту по питанию для подбора замены."
                            )
                        }

                        result.healthPercent <= 35 -> {
                            recommendation = "Употребляйте умеренно"
                            advice = listOf(
                                "Ограничьте потребление до 1-2 порций в неделю.",
                                "Ищите альтернативы с натуральными ингредиентами.",
                                "Рассмотрите домашние варианты с более полезными ингредиентами."
                            )
                        }

                        result.healthPercent <= 50 -> {
                            recommendation = "Употребляйте умеренно"
                            advice = listOf(
                                "Ограничьте потребление до 3-4 порций в неделю.",
                                "Обратите внимание на индивидуальные аллергены.",
                                "Следите за размером порций."
                            )
                        }

                        result.healthPercent <= 75 -> {
                            recommendation = "Допустим"
                            advice = listOf(
                                "Можно употреблять регулярно, если сбалансирован с другими продуктами.",
                                "Следите за размером порций.",
                                "Обратите внимание на индивидуальные аллергены."
                            )
                        }

                        result.healthPercent == 100 -> {
                            recommendation = "Отличный продукт!"
                            advice = emptyList()
                        }

                        else -> {
                            recommendation = "Хороший выбор"
                            advice = listOf(
                                "Подходит для регулярного употребления.",
                                "Содержит преимущественно натуральные ингредиенты.",
                                "Поддерживает общее здоровье при сбалансированном рационе."
                            )
                        }
                    }

                    /**
                     * - showOcrText — флаг для показа распознанного текста.
                     * - showPhotoBlock — флаг для начала анимации и показа карточки с фотографией.
                     * - showRecommendationBlock — флаг для начала анимации и показа карточки с рекомендациями.
                     * - animatedPercent — анимированный процент полезности продукта (от 0 до значения, пришедшего из [com.example.foodlens.domain.model.ProductAnalysesResult]).
                     * - showAdvice — флаг для начала анимации и показа рекомендаций.
                     * - showDividingLine — флаг для начала анимации и показа разделительной черты.
                     * - showRecommendation — флаг для начала анимации и показа рекомендации к покупке товара.
                     * - showUnhealthyIngredientsBlock — флаг для начала анимации и показа карточки с найденными вредными ингредиентами.
                     */
                    var showOcrText by remember { mutableStateOf(false) }
                    var showPhotoBlock by remember { mutableStateOf(false) }
                    var showRecommendationBlock by remember { mutableStateOf(false) }
                    val animatedPercent by animateFloatAsState(
                        targetValue = if (showRecommendationBlock) result.healthPercent.toFloat() else 0f,
                        animationSpec = tween(durationMillis = 1500),
                        label = "AnimatedPercent"
                    )
                    val showAdvice = remember(advice) {
                        List(advice.size) { mutableStateOf(false) }
                    }
                    var showDividingLine by remember { mutableStateOf(false) }
                    var showRecommendation by remember { mutableStateOf(false) }
                    var showUnhealthyIngredientsBlock by remember { mutableStateOf(false) }

                    if (showResults) {
                        LaunchedEffect(Unit) {
                            delay(1500)
                            showPhotoBlock = true
                            delay(1500)
                            showRecommendationBlock = true
                            if (result.healthPercent < 10) {
                                delay(1000)
                            } else {
                                delay(2500)
                            }
                            showRecommendation = true
                            delay(1000)
                            showDividingLine = true
                            showAdvice.forEachIndexed { _, state ->
                                state.value = true
                                delay(1000)
                            }
                            delay(500)
                            showUnhealthyIngredientsBlock = true
                        }
                    }

                    showResults = true

                    /**
                     * Часть экрана, которая отрисовывается без задержки. Включает:
                     * - [Text] с названием приложения;
                     * - [Text] "Результаты анализа";
                     * - кнопку выхода ([Icon], крестик).
                     */
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

                        /**
                         * Карточка с исходной фотографией. Включает:
                         * - заголовок [CardTitle];
                         * - контент:
                         *   - [Image] — исходное изображение;
                         *   - [Box], при нажатии на который появляется [AlertDialog] с распознанным текстом.
                         */
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
                                            .aspectRatio(16f / 9f),
                                        contentScale = ContentScale.Fit
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                color = MaterialTheme.colorScheme.surface
                                            ),
                                        contentAlignment = Alignment.BottomEnd
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .background(
                                                    color = extraColors.gray100,
                                                    shape = CircleShape
                                                )
                                                .clickable {
                                                    showOcrText = true
                                                }
                                        ) {
                                            Text(
                                                text = "Распознанный текст",
                                                modifier = Modifier.padding(horizontal = 8.dp),
                                                color = extraColors.gray400,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }

                                    if (showOcrText) {
                                        AlertDialog(
                                            onDismissRequest = { showOcrText = false },
                                            confirmButton = {
                                                Button(onClick = { showOcrText = false }) {
                                                    Text("Закрыть")
                                                }
                                            },
                                            title = {
                                                Text(
                                                    text = "Распознанный текст",
                                                    fontSize = 16.sp
                                                )
                                            },
                                            text = { Text(text = result.ocrText) },
                                            containerColor = MaterialTheme.colorScheme.background
                                        )
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        /**
                         * Карточка с рекомендациями. Включает:
                         * - заголовок [CardTitle];
                         * - контент:
                         *   - [Row], в котором находятся:
                         *     - [Box] с динамическим фоном (по [backgroundIndexColor] и [getAnimatedBackgroundColor]) и индикатором полезности (по [indexColor] и [getAnimatedIndexColor]);
                         *     - [Column] с [Text] "Индекс состава" и [Text], содержащим [recommendation].
                         *   - Если индекс полезности равен 100, рекомендации не отображаются; иначе с анимацией показываются рекомендации в виде [TextWithPoint].
                         */

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
                                        val backgroundIndexColor by animateColorAsState(
                                            targetValue = if (showRecommendationBlock) getAnimatedBackgroundColor(
                                                extraColors,
                                                animatedPercent
                                            ) else Color.Transparent,
                                            animationSpec = tween(durationMillis = 1500),
                                            label = "AnimatedBackgroundColor"
                                        )

                                        val indexColor by animateColorAsState(
                                            targetValue = if (showRecommendationBlock) getAnimatedIndexColor(
                                                extraColors,
                                                animatedPercent
                                            ) else Color.Transparent,
                                            animationSpec = tween(durationMillis = 1500),
                                            label = "AnimatedIndexColor"
                                        )

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
                                                text = "${animatedPercent.roundToInt()}%",
                                                color = indexColor,
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Column(
                                            modifier.fillMaxHeight(),
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                text = "Индекс состава",
                                                color = extraColors.gray500,
                                                fontSize = 16.sp
                                            )
                                            AnimatedVisibility(visible = showRecommendation) {
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
                                                    .background(color = extraColors.gray150)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        advice.forEachIndexed { index, text ->
                                            AnimatedVisibility(visible = showAdvice[index].value) {
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

                        /**
                         * Карточка с вредными ингредиентами. Включает:
                         * - заголовок [CardTitle];
                         * - контент:
                         *   - если вредные ингредиенты не обнаружены, отображается [TextWithPoint] с текстом "Не обнаружено";
                         *   - иначе используется [FlowRow] с [Button] (вредные ингредиенты), при нажатии на которые открывается [AlertDialog] с описанием.
                         */
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
                                        if (result.harmfulIngredients.isEmpty()) {
                                            TextWithPoint(
                                                extraColors = extraColors,
                                                text = "Не обнаружено"
                                            )
                                        } else {
                                            var selectedIngredient by remember {
                                                mutableStateOf<Pair<String, String>?>(
                                                    null
                                                )
                                            }
                                            FlowRow(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                result.harmfulIngredients.forEach { (name, description) ->
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

                                            selectedIngredient?.let { (name, desc) ->
                                                AlertDialog(
                                                    onDismissRequest = {
                                                        selectedIngredient = null
                                                    },
                                                    confirmButton = {
                                                        Button(onClick = {
                                                            selectedIngredient = null
                                                        }) {
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
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

/**
 * Карточка для отображения контента с заголовком и телом.
 *
 * @param title заголовок карточки.
 * @param content содержимое карточки.
 */
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
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
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

/**
 * Заголовок карточки с иконкой.
 *
 * @param textTitle текст заголовка.
 * @param icon ресурс иконки.
 * @param iconContentDescription описание иконки для доступности.
 * @param iconColor цвет иконки.
 */
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

/**
 * Элемент текста с зелёной галочкой перед ним — используется для рекомендаций.
 *
 * @param extraColors расширенная цветовая палитра.
 * @param text текст рекомендации.
 */
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
            color = extraColors.gray500,
            fontSize = 16.sp
        )
    }
}

/**
 * Анимация увеличительного стекла, вращающегося над "строчками "логотипа.
 * Используется в состоянии загрузки данного экрана.
 */
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
            .height(120.dp),
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

/**
 * Возвращает цвет фона индикатора процента полезности продукта.
 *
 * @param extraColors расширенная цветовая палитра.
 * @param percent процент полезности (0-100).
 * @return цвет: красный при низком %, оранжевый при среднем и зелёный при высоком.
 */
fun getAnimatedBackgroundColor(
    extraColors: ExtendedColors,
    percent: Float
): Color {
    return when {
        percent <= 35 -> extraColors.red100
        percent <= 75 -> extraColors.orange100
        else -> extraColors.green300
    }
}

/**
 * Возвращает цвет текста индекса состава продукта.
 *
 * @param extraColors расширенная цветовая палитра.
 * @param percent процент полезности (0-100).
 * @return цвет текста в зависимости от процента.
 */
@Composable
fun getAnimatedIndexColor(
    extraColors: ExtendedColors,
    percent: Float
): Color {
    return when {
        percent <= 35 -> extraColors.red300
        percent <= 75 -> extraColors.orange300
        else -> MaterialTheme.colorScheme.primary
    }
}
