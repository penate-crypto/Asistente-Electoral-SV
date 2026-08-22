package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.TransparentAssetImage
import kotlinx.coroutines.delay

@Composable
fun WelcomeIntroScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Exactly 6-second controlled loading progression
    var progress by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        val totalDurationMs = 6000L
        val intervalMs = 50L
        val totalSteps = (totalDurationMs / intervalMs).toInt()

        for (step in 1..totalSteps) {
            delay(intervalMs)
            progress = step.toFloat() / totalSteps.toFloat()
        }
        delay(150)
        onDismiss()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A1128),
                        Color(0xFF0F1E36),
                        Color(0xFF081225)
                    )
                )
            )
            .testTag("welcome_intro_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            // 1. ICONO 2 — Nueva imagen de introducción (exact design preserved, white background removed)
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .heightIn(min = 180.dp, max = 260.dp)
                    .aspectRatio(1376f / 768f),
                contentAlignment = Alignment.Center
            ) {
                TransparentAssetImage(
                    assetPath = "imágenes/icono 2.jpeg",
                    contentDescription = "Introducción Asistente Electoral",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // 2. Barra de progreso lineal moderna y fluida
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(7.dp)
                    .clip(RoundedCornerShape(3.5.dp))
                    .testTag("intro_progress_bar"),
                color = Color(0xFF29B6F6),
                trackColor = Color.White.copy(alpha = 0.15f)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Porcentaje numérico únicamente
            Text(
                text = "${(progress * 100).toInt()}%",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF90CAF9),
                modifier = Modifier.testTag("intro_percentage_text")
            )
        }
    }
}
