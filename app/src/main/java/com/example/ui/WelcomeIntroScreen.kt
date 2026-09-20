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
            .background(Color.White)
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
            // 1. LOGO FULL COLOR 02 — Imagen oficial de introducción
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .heightIn(min = 160.dp, max = 260.dp)
                    .aspectRatio(3251f / 989f),
                contentAlignment = Alignment.Center
            ) {
                TransparentAssetImage(
                    assetPath = "imagenes/Logo full color-02.png",
                    contentDescription = "Introducción Asistente Electoral",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // 2. Barra de progreso lineal moderna con azul institucional sobre fondo blanco
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(7.dp)
                    .clip(RoundedCornerShape(3.5.dp))
                    .testTag("intro_progress_bar"),
                color = Color(0xFF003366),
                trackColor = Color(0xFFE2E8F0)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Porcentaje numérico de avance
            Text(
                text = "${(progress * 100).toInt()}%",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF003366),
                modifier = Modifier.testTag("intro_percentage_text")
            )
        }
    }
}

