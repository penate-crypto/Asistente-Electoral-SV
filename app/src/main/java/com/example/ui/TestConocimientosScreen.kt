package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.*
import com.example.viewmodel.ElectoralViewModel

enum class TestMode {
    ESTUDIO, // Shows correct answer and explanation immediately
    EXAMEN   // Evaluates user answers and shows summary at the end
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestConocimientosScreen(
    onClose: () -> Unit,
    viewModel: ElectoralViewModel,
    modifier: Modifier = Modifier
) {
    val allQuestions = remember { ExamBankData.all125Questions }
    val totalCount = allQuestions.size // 125 questions

    var currentMode by remember { mutableStateOf(TestMode.ESTUDIO) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var userAnswers by remember { mutableStateOf<MutableMap<Int, Int>>(mutableMapOf()) } // questionIndex -> selectedOptionIndex
    var isExamSubmitted by remember { mutableStateOf(false) }
    var showIndexDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    var viewingPdfDoc by remember { mutableStateOf<Pair<PdfDocument, Int>?>(null) }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            icon = { Icon(Icons.Default.Refresh, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("¿Reiniciar Test?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Volverás a la pregunta 1 y se limpiarán todas las respuestas registradas en el catálogo actual de 125 preguntas.",
                    fontSize = 13.5.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showResetDialog = false
                        currentIndex = 0
                        userAnswers = mutableMapOf()
                        isExamSubmitted = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Reiniciar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    val onOpenSource: (ExamQuestion) -> Unit = { question ->
        val docId = question.sourceDocumentId ?: when {
            question.sourceDocument.contains("Código Electoral", ignoreCase = true) -> "codigo_electoral_decreto_413"
            question.sourceDocument.contains("Instructivo", ignoreCase = true) -> "instructivo_jrv_tse"
            question.sourceDocument.contains("Constitución", ignoreCase = true) -> "constitucion_republica_1983"
            question.sourceDocument.contains("Partidos", ignoreCase = true) -> "ley_de_partidos_politicos"
            question.sourceDocument.contains("Acceso", ignoreCase = true) -> "ley_acceso_informacion_publica"
            question.sourceDocument.contains("Extranjero", ignoreCase = true) || question.sourceDocument.contains("Exterior", ignoreCase = true) -> "ley_sufragio_extranjero"
            question.sourceDocument.contains("LEIV", ignoreCase = true) || question.sourceDocument.contains("Género", ignoreCase = true) || question.sourceDocument.contains("Mujer", ignoreCase = true) -> "ley_genero_electoral"
            question.sourceDocument.contains("Observación", ignoreCase = true) -> "reglamento_observacion_electoral"
            question.sourceDocument.contains("No Partidaria", ignoreCase = true) || question.sourceDocument.contains("No-partidaria", ignoreCase = true) -> "disposiciones_candidaturas_no_partidarias"
            else -> "codigo_electoral_decreto_413"
        }

        val targetDoc = ElectoralLibraryData.documents.firstOrNull { it.id == docId }
            ?: ElectoralLibraryData.documents.first()
        val pageIndex = ((question.sourcePage ?: 1) - 1).coerceAtLeast(0)
        viewingPdfDoc = Pair(targetDoc, pageIndex)
    }

    if (viewingPdfDoc != null) {
        PdfReaderScreen(
            document = viewingPdfDoc!!.first,
            initialPageIndex = viewingPdfDoc!!.second,
            onClose = { viewingPdfDoc = null },
            viewModel = viewModel,
            modifier = modifier
        )
        return
    }

    // Modal Sheet / Dialog to jump to any of the 125 questions
    if (showIndexDialog) {
        Dialog(onDismissRequest = { showIndexDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Índice de Preguntas (1 a $totalCount)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(onClick = { showIndexDialog = false }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Seleccione una pregunta para navegar directamente:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 48.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(totalCount) { idx ->
                            val isCurrent = idx == currentIndex
                            val isAnswered = userAnswers.containsKey(idx)
                            val isCorrect = userAnswers[idx] == allQuestions[idx].correctOptionIndex

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when {
                                    isCurrent -> MaterialTheme.colorScheme.primary
                                    isExamSubmitted && isCorrect -> Color(0xFF2E7D32)
                                    isExamSubmitted && isAnswered && !isCorrect -> Color(0xFFC62828)
                                    isAnswered -> MaterialTheme.colorScheme.secondaryContainer
                                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                },
                                modifier = Modifier
                                    .size(46.dp)
                                    .clickable {
                                        currentIndex = idx
                                        showIndexDialog = false
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${idx + 1}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when {
                                            isCurrent -> MaterialTheme.colorScheme.onPrimary
                                            isExamSubmitted && (isCorrect || !isCorrect) -> Color.White
                                            isAnswered -> MaterialTheme.colorScheme.onSecondaryContainer
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top App Bar - Redesigned compact header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                // Main Header Row: Back + Title & Subtitle + Mode Pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                    
                    Spacer(modifier = Modifier.width(6.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "TEST DE CONOCIMIENTOS ELECTORALES",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.3.sp,
                            maxLines = 1
                        )
                        Text(
                            text = "Banco Oficial Completo · 125 Preguntas",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Mode Switch Pill (Always horizontal, guaranteed no vertical breaking)
                    Surface(
                        color = if (currentMode == TestMode.ESTUDIO) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.tertiaryContainer,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                currentMode = if (currentMode == TestMode.ESTUDIO) TestMode.EXAMEN else TestMode.ESTUDIO
                            }
                            .testTag("btn_toggle_test_mode")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (currentMode == TestMode.ESTUDIO) Icons.Default.MenuBook else Icons.Default.Quiz,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (currentMode == TestMode.ESTUDIO) "Modo Estudio" else "Modo Examen",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Secondary Control Row: Question info, Index & Restart buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Pregunta ${currentIndex + 1} de $totalCount",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "${userAnswers.size}/$totalCount",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showIndexDialog = true },
                            contentPadding = PaddingValues(horizontal = 9.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("btn_indice_test")
                        ) {
                            Icon(Icons.Default.Apps, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Índice", fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
                        }

                        OutlinedButton(
                            onClick = { showResetDialog = true },
                            contentPadding = PaddingValues(horizontal = 9.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("btn_reiniciar_test_ajustes")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reiniciar", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { (currentIndex + 1).toFloat() / totalCount.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                )
            }
        }

        if (currentMode == TestMode.EXAMEN && isExamSubmitted) {
            // Exam Result Summary Screen
            Exam125ResultsView(
                questions = allQuestions,
                userAnswers = userAnswers,
                onRetake = {
                    userAnswers = mutableMapOf()
                    isExamSubmitted = false
                    currentIndex = 0
                },
                onOpenSource = onOpenSource
            )
        } else {
            // Active Question View
            val question = allQuestions[currentIndex]
            val selectedOption = userAnswers[currentIndex]
            val isAnswered = selectedOption != null

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Category Chip & ID
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = question.category,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = "ID: ${question.id}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Question Box
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(2.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Pregunta ${currentIndex + 1}",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = question.questionText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 23.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (!question.situationContext.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Contexto: ${question.situationContext}",
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }

                // Options List (A, B, C, D)
                Text(
                    text = "OPCIONES DE RESPUESTA:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.5.sp
                )

                val optionLetters = listOf("A", "B", "C", "D")

                question.options.forEachIndexed { optIndex, optionText ->
                    val letter = optionLetters.getOrElse(optIndex) { "${optIndex + 1}" }
                    val isSelected = selectedOption == optIndex
                    val isCorrectOption = optIndex == question.correctOptionIndex
                    val showAsCorrect = (currentMode == TestMode.ESTUDIO) || (isAnswered && isCorrectOption)

                    val containerColor = when {
                        currentMode == TestMode.ESTUDIO && isCorrectOption -> Color(0xFFE8F5E9)
                        isSelected && isCorrectOption -> Color(0xFFE8F5E9)
                        isSelected && !isCorrectOption -> Color(0xFFFFEBEE)
                        isSelected -> MaterialTheme.colorScheme.primaryContainer
                        else -> MaterialTheme.colorScheme.surface
                    }

                    val borderColor = when {
                        currentMode == TestMode.ESTUDIO && isCorrectOption -> Color(0xFF4CAF50)
                        isSelected && isCorrectOption -> Color(0xFF4CAF50)
                        isSelected && !isCorrectOption -> Color(0xFFE53935)
                        isSelected -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    }

                    Surface(
                        color = containerColor,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(if (isSelected || (currentMode == TestMode.ESTUDIO && isCorrectOption)) 2.dp else 1.dp, borderColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val updated = userAnswers.toMutableMap()
                                updated[currentIndex] = optIndex
                                userAnswers = updated
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(
                                        color = when {
                                            currentMode == TestMode.ESTUDIO && isCorrectOption -> Color(0xFF2E7D32)
                                            isSelected && isCorrectOption -> Color(0xFF2E7D32)
                                            isSelected && !isCorrectOption -> Color(0xFFC62828)
                                            isSelected -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.surfaceVariant
                                        },
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = letter,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected || (currentMode == TestMode.ESTUDIO && isCorrectOption)) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = optionText,
                                fontSize = 14.5.sp,
                                lineHeight = 21.sp,
                                fontWeight = if (isSelected || (currentMode == TestMode.ESTUDIO && isCorrectOption)) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (currentMode == TestMode.ESTUDIO && isCorrectOption) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )

                            if (currentMode == TestMode.ESTUDIO && isCorrectOption) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Correcta",
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Correct Answer Section (Always below options, highlighted in Light Green in Study Mode or when answered)
                if (currentMode == TestMode.ESTUDIO || isAnswered) {
                    val correctLetter = optionLetters.getOrElse(question.correctOptionIndex) { "A" }
                    val correctText = question.options.getOrElse(question.correctOptionIndex) { "" }

                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.5.dp, Color(0xFFA5D6A7)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF2E7D32),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "RESPUESTA CORRECTA",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF1B5E20),
                                        letterSpacing = 0.5.sp
                                    )
                                }

                                Button(
                                    onClick = { onOpenSource(question) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("VER FUENTE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }

                            Text(
                                text = "$correctLetter) $correctText",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20)
                            )

                            HorizontalDivider(color = Color(0xFFC8E6C9), modifier = Modifier.padding(vertical = 2.dp))

                            Text(
                                text = "Explicación: ${question.explanation}",
                                fontSize = 12.5.sp,
                                lineHeight = 18.sp,
                                color = Color(0xFF2E7D32)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Fundamento: ${question.sourceArticle ?: question.normativeReference} (${question.sourceDocument}${if (question.sourcePage != null) ", Pág. ${question.sourcePage}" else ""})",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF0D47A1)
                                )
                            }
                        }
                    }
                }

                // Bottom Navigation Row: Previous, Next, Submit
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { if (currentIndex > 0) currentIndex-- },
                        enabled = currentIndex > 0,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Anterior", fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    if (currentIndex < totalCount - 1) {
                        Button(
                            onClick = { currentIndex++ },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Siguiente", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    } else if (currentMode == TestMode.EXAMEN) {
                        Button(
                            onClick = { isExamSubmitted = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Finalizar Test", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (currentMode == TestMode.EXAMEN && !isExamSubmitted) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = { isExamSubmitted = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Evaluar Examen Ahora (${userAnswers.size}/$totalCount respondidas)", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun Exam125ResultsView(
    questions: List<ExamQuestion>,
    userAnswers: Map<Int, Int>,
    onRetake: () -> Unit,
    onOpenSource: (ExamQuestion) -> Unit
) {
    val totalCount = questions.size
    val correctCount = questions.indices.count { idx -> userAnswers[idx] == questions[idx].correctOptionIndex }
    val incorrectCount = totalCount - correctCount
    val percentage = (correctCount.toFloat() / totalCount.toFloat()) * 100f

    var filterMode by remember { mutableIntStateOf(0) } // 0: All, 1: Correct, 2: Incorrect

    val filteredIndices = questions.indices.filter { idx ->
        val isCorrect = userAnswers[idx] == questions[idx].correctOptionIndex
        when (filterMode) {
            1 -> isCorrect
            2 -> !isCorrect
            else -> true
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Result Score Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (percentage >= 70f) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                ),
                border = BorderStroke(1.5.dp, if (percentage >= 70f) Color(0xFF4CAF50) else Color(0xFFFFB74D)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (percentage >= 70f) Icons.Default.EmojiEvents else Icons.Default.HelpOutline,
                        contentDescription = null,
                        tint = if (percentage >= 70f) Color(0xFF2E7D32) else Color(0xFFE65100),
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "RESULTADO DEL TEST",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = if (percentage >= 70f) Color(0xFF1B5E20) else Color(0xFFBF360C)
                    )

                    Text(
                        text = "${String.format("%.1f", percentage)}%",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        color = if (percentage >= 70f) Color(0xFF2E7D32) else Color(0xFFE65100)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Surface(
                            color = Color(0xFF2E7D32).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Correctas", fontSize = 11.5.sp, color = Color(0xFF1B5E20))
                                Text("$correctCount / $totalCount", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                            }
                        }

                        Surface(
                            color = Color(0xFFC62828).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Incorrectas", fontSize = 11.5.sp, color = Color(0xFFB71C1C))
                                Text("$incorrectCount / $totalCount", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB71C1C))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onRetake,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Reiniciar Test Completo", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Filter Tabs
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterMode == 0,
                    onClick = { filterMode = 0 },
                    label = { Text("Todas ($totalCount)", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = filterMode == 1,
                    onClick = { filterMode = 1 },
                    label = { Text("Correctas ($correctCount)", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = filterMode == 2,
                    onClick = { filterMode = 2 },
                    label = { Text("Incorrectas ($incorrectCount)", fontSize = 12.sp) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Detailed Question List
        itemsIndexed(filteredIndices) { _, qIndex ->
            val q = questions[qIndex]
            val userSelected = userAnswers[qIndex]
            val isCorrect = userSelected == q.correctOptionIndex
            val optionLetters = listOf("A", "B", "C", "D")

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCorrect) Color(0xFFE8F5E9).copy(alpha = 0.5f) else Color(0xFFFFEBEE).copy(alpha = 0.5f)
                ),
                border = BorderStroke(1.dp, if (isCorrect) Color(0xFF81C784) else Color(0xFFE57373)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pregunta ${qIndex + 1}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )

                        Surface(
                            color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (isCorrect) "CORRECTA" else "INCORRECTA",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = q.questionText,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    val userLetter = if (userSelected != null) optionLetters.getOrElse(userSelected) { "-" } else "Sin responder"
                    val userText = if (userSelected != null) q.options.getOrElse(userSelected) { "" } else ""
                    val correctLetter = optionLetters.getOrElse(q.correctOptionIndex) { "A" }
                    val correctText = q.options.getOrElse(q.correctOptionIndex) { "" }

                    if (!isCorrect) {
                        Text(
                            text = "Tu respuesta: $userLetter) $userText",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFC62828)
                        )
                    }

                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "Respuesta Correcta: $correctLetter) $correctText",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = q.explanation,
                                fontSize = 11.5.sp,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Base: ${q.sourceArticle ?: q.normativeReference} (${q.sourceDocument})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF0D47A1),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedButton(
                            onClick = { onOpenSource(q) },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("VER FUENTE", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
