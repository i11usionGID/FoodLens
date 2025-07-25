package com.example.foodlens.presentation.screens.welcome

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodlens.R

@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    onOpenCameraCLick: () -> Unit,
    onOpenGalleryClick: () -> Unit
) {

    Scaffold(
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "FoodLens",
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Мгновенно анализируйте ингредиенты\nваших продуктов",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp)
                ) {
                    Text(
                        text = "Как это работает",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InfoRow(
                        R.drawable.ic_camera,
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.primary,
                        "Сфотографируйте или загрузите фотографию"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InfoRow(
                        R.drawable.ic_brain,
                        MaterialTheme.colorScheme.secondaryContainer,
                        MaterialTheme.colorScheme.secondary,
                        "AI проанализирует ингридиенты состава"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InfoRow(
                        R.drawable.ic_report_analytics,
                        MaterialTheme.colorScheme.tertiaryContainer,
                        MaterialTheme.colorScheme.tertiary,
                        "Получите рекомендации по продукту"
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.height(16.dp))

            GetPhotoButton(
                onOpenCLick = onOpenCameraCLick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                iconId = R.drawable.ic_camera_filled,
                iconContentDescription = "Открыть камеру",
                text = "Сфотографировать"
            )

            Spacer(modifier = Modifier.height(16.dp))

            GetPhotoButton(
                onOpenCLick = onOpenGalleryClick,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                iconId = R.drawable.ic_upload,
                iconContentDescription = "Загрузить фото из галлереи",
                text = "Загрузить из галереи"
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun InfoRow(
    iconId: Int,
    backGroundColor: Color,
    iconColor: Color,
    text: String
) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    shape = CircleShape,
                    color = backGroundColor
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(iconId),
                contentDescription = "Маленькая иконка для красоты",
                modifier = Modifier.size(24.dp),
                tint = iconColor
            )
        }
        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
        Text(
            text = text,
            fontSize = 16.sp
        )
    }
}

@Composable
fun GetPhotoButton(
    onOpenCLick: () -> Unit,
    containerColor: Color,
    contentColor: Color,
    iconId: Int,
    iconContentDescription: String,
    text: String
) {
    val isOutlined = containerColor == Color.Transparent

    Button(
        onClick = onOpenCLick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(8.dp),
        border = if (isOutlined)
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        else null,
        elevation = if (isOutlined) null else ButtonDefaults.buttonElevation()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(iconId),
                contentDescription = iconContentDescription,
                modifier = Modifier.size(24.dp),
                tint = contentColor
            )

            Text(
                text = text,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = contentColor,
                textAlign = TextAlign.Center
            )
        }
    }
}