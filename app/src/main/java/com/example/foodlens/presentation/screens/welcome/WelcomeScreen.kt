package com.example.foodlens.presentation.screens.welcome

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodlens.R

@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    onCameraButtonClick: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCameraButtonClick,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onBackground,
                shape = CircleShape
            ) {
                Icon(
                    painter = painterResource(R.drawable.scan),
                    contentDescription = "Сканировать этикетку"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "FoodLens",
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.height(24.dp))

            ExpandableInfoBlock(
                title = "Информация о приложении",
                content = "FoodLens - это приложение, которое может определить полезность продукта" +
                        " питания по фотографии его состава. Приложение использует OCR для " +
                        "распознавания состава продукта и определения его полезности по " +
                        "заданным правилам и паттернам. Чтобы начать, нажмите кнопку в правом " +
                        "нижнем углу экрана."
            )

            ExpandableInfoBlock(
                title = "Пример полезного состава",
                content = "Состав: цельнозерновая мука, закваска, вода, соль.\n" +
                        "Категория полезности: полезный\n" +
                        "Полезные ингредиенты: цельнозерновая мука, закваска\n" +
                        "Вредные ингредиенты: отсутствуют"
            )

            ExpandableInfoBlock(
                title = "Пример вредного состава",
                content = "Состав: глюкозный сироп, пальмовое масло, e621, красители.\n" +
                        "Категория полезности: вредный\n" +
                        "Полезные ингредиенты: отсутствуют\n" +
                        "Вредные ингредиенты: глюкозный сироп, пальмовое масло, e621, красители"
            )
        }
    }
}

@Composable
fun ExpandableInfoBlock(
    title: String,
    content: String
) {
    val expanded = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { expanded.value = !expanded.value },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )

            AnimatedVisibility(visible = expanded.value) {
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
