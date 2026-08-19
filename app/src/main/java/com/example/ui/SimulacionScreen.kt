package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.components.BallotVisualizerView
import com.example.viewmodel.ElectoralViewModel

enum class SimulacionTab {
    CASOS_Y_SOLUCIONES,
    EXAMEN_ELECTORAL
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimulacionScreen(
    viewModel: ElectoralViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(SimulacionTab.CASOS_Y_SOLUCIONES) }

    // State for viewing a specific case in detail
    var viewingCase by remember { mutableStateOf<CaseSolution?>(null) }

    // State for the 25-Question Exam
    var currentExamQuestions by remember { mutableStateOf(ElectoralSimulationRepository.getRandom25ExamQuestions()) }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var hasAnsweredCurrentQuestion by remember { mutableStateOf(false) }
    var userAnswers by remember { mutableStateOf<MutableMap<Int, Int>>(mutableMapOf()) } // questionIndex -> selectedOptionIndex
    var isExamFinished by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Module Top Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Column {
                    Text(
                        text = "MÓDULO DE SIMULACIÓN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Simulaciones y Examen Electoral",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Section Tabs: [Casos y soluciones] & [Examen Electoral]
                TabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    Tab(
                        selected = selectedTab == SimulacionTab.CASOS_Y_SOLUCIONES,
                        onClick = { selectedTab = SimulacionTab.CASOS_Y_SOLUCIONES },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Casos y soluciones", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        },
                        modifier = Modifier.testTag("tab_casos_soluciones")
                    )
                    Tab(
                        selected = selectedTab == SimulacionTab.EXAMEN_ELECTORAL,
                        onClick = { selectedTab = SimulacionTab.EXAMEN_ELECTORAL },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Examen Electoral", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        },
                        modifier = Modifier.testTag("tab_examen_electoral")
                    )
                }
            }
        }

        // Section Content
        Crossfade(
            targetState = selectedTab,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { tab ->
            when (tab) {
                SimulacionTab.CASOS_Y_SOLUCIONES -> {
                    if (viewingCase != null) {
                        // Detailed View of a Case
                        CaseDetailScreen(
                            caseSolution = viewingCase!!,
                            onClose = { viewingCase = null },
                            viewModel = viewModel
                        )
                    } else {
                        // List of Simulation Cases
                        CasosListView(
                            cases = ElectoralSimulationRepository.casesAndSolutions,
                            onSelectCase = { viewingCase = it },
                            viewModel = viewModel
                        )
                    }
                }

                SimulacionTab.EXAMEN_ELECTORAL -> {
                    if (isExamFinished) {
                        // Exam Summary Screen with approval status, review of wrong answers, retake
                        val correctCount = userAnswers.count { (index, optionIndex) ->
                            currentExamQuestions[index].correctOptionIndex == optionIndex
                        }
                        val incorrectCount = currentExamQuestions.size - correctCount
                        val scorePercentage = (correctCount.toFloat() / currentExamQuestions.size.toFloat()) * 100f
                        val isApproved = scorePercentage >= 70f // 18 out of 25

                        ExamResultsSummaryView(
                            questions = currentExamQuestions,
                            userAnswers = userAnswers,
                            correctCount = correctCount,
                            incorrectCount = incorrectCount,
                            scorePercentage = scorePercentage,
                            isApproved = isApproved,
                            onRetakeWithNewQuestions = {
                                currentExamQuestions = ElectoralSimulationRepository.getRandom25ExamQuestions()
                                currentQuestionIndex = 0
                                selectedOptionIndex = null
                                hasAnsweredCurrentQuestion = false
                                userAnswers = mutableMapOf()
                                isExamFinished = false
                            },
                            viewModel = viewModel
                        )
                    } else {
                        // Active 25-Question Exam View
                        ActiveExamQuestionView(
                            questions = currentExamQuestions,
                            currentIndex = currentQuestionIndex,
                            selectedOption = selectedOptionIndex,
                            hasAnswered = hasAnsweredCurrentQuestion,
                            onOptionSelected = { optionIndex ->
                                if (!hasAnsweredCurrentQuestion) {
                                    selectedOptionIndex = optionIndex
                                    hasAnsweredCurrentQuestion = true
                                    userAnswers[currentQuestionIndex] = optionIndex
                                }
                            },
                            onNextQuestion = {
                                if (currentQuestionIndex < currentExamQuestions.size - 1) {
                                    currentQuestionIndex++
                                    selectedOptionIndex = userAnswers[currentQuestionIndex]
                                    hasAnsweredCurrentQuestion = userAnswers.containsKey(currentQuestionIndex)
                                } else {
                                    isExamFinished = true
                                }
                            },
                            onPreviousQuestion = {
                                if (currentQuestionIndex > 0) {
                                    currentQuestionIndex--
                                    selectedOptionIndex = userAnswers[currentQuestionIndex]
                                    hasAnsweredCurrentQuestion = userAnswers.containsKey(currentQuestionIndex)
                                }
                            },
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// APARTADO A: LISTADO DE CASOS Y SOLUCIONES
// =========================================================================

@Composable
private fun CasosListView(
    cases: List<CaseSolution>,
    onSelectCase: (CaseSolution) -> Unit,
    viewModel: ElectoralViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterStage by remember { mutableStateOf("Todos") }

    val stages = listOf("Todos", "Instalación", "Votación", "Escrutinio", "Seguridad")

    val filteredCases = cases.filter { case ->
        val matchesStage = selectedFilterStage == "Todos" || case.stage.contains(selectedFilterStage, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                case.title.contains(searchQuery, ignoreCase = true) ||
                case.situationDescription.contains(searchQuery, ignoreCase = true) ||
                case.correctSolution.contains(searchQuery, ignoreCase = true)
        matchesStage && matchesSearch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Search & Filter
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Buscar caso por palabra clave (JRV, PNC, DUI...)", fontSize = 12.5.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("cases_search_input")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Stage Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            stages.forEach { stage ->
                FilterChip(
                    selected = stage == selectedFilterStage,
                    onClick = { selectedFilterStage = stage },
                    label = { Text(stage, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Cases List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(filteredCases) { caseItem ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectCase(caseItem) }
                        .testTag("case_item_${caseItem.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = caseItem.stage,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = caseItem.title,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = caseItem.situationDescription,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            maxLines = 2,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Entidades: ${caseItem.entitiesInvolved.joinToString(", ") { it.entityName.substringBefore(" (") }}",
                                fontSize = 10.5.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                modifier = Modifier.weight(1f)
                            )

                            Button(
                                onClick = { onSelectCase(caseItem) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Ver Caso y Solución", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// DETALLE DEL CASO (TODOS LOS PUNTOS REQUERIDOS)
// =========================================================================

@Composable
private fun CaseDetailScreen(
    caseSolution: CaseSolution,
    onClose: () -> Unit,
    viewModel: ElectoralViewModel
) {
    var isSolutionRevealed by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("case_detail_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Back / Close bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onClose,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Volver al listado de casos", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = caseSolution.stage,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        // Title
        Text(
            text = caseSolution.title,
            fontSize = 19.sp,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground
        )

        // 1. Situación Planteada
        DetailSectionCard(
            title = "1. Situación Planteada",
            icon = Icons.Default.Help,
            iconTint = Color(0xFF1976D2),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Text(
                text = caseSolution.situationDescription,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // 2. Contexto de lo que está ocurriendo
        DetailSectionCard(
            title = "2. Contexto de la Situación",
            icon = Icons.Default.AccessTime,
            iconTint = Color(0xFF0288D1),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Text(
                text = caseSolution.contextDescription,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // 3 y 4. Personas, Funcionarios o Entidades Involucradas y sus Funciones Específicas
        DetailSectionCard(
            title = "3 y 4. Entidades Involucradas y Funciones Específicas",
            icon = Icons.Default.Groups,
            iconTint = Color(0xFF388E3C),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                caseSolution.entitiesInvolved.forEach { entity ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "• ${entity.entityName}",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Función: ${entity.specificRole}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // 5. Qué debe hacerse
        DetailSectionCard(
            title = "5. Qué Debe Hacerse",
            icon = Icons.Default.CheckCircle,
            iconTint = Color(0xFF2E7D32),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Text(
                text = caseSolution.whatShouldBeDone,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }

        // 6. Procedimiento Correcto Paso a Paso
        DetailSectionCard(
            title = "6. Procedimiento Correcto Paso a Paso",
            icon = Icons.Default.FormatListNumbered,
            iconTint = Color(0xFF00796B),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                caseSolution.stepByStepProcedure.forEach { step ->
                    Text(
                        text = step,
                        fontSize = 12.5.sp,
                        lineHeight = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // 7. Qué acciones NO deben realizarse (Errores a evitar)
        DetailSectionCard(
            title = "7. Qué Acciones NO Deben Realizarse (Prohibiciones)",
            icon = Icons.Default.Cancel,
            iconTint = Color(0xFFD32F2F),
            containerColor = Color(0xFFFFEBEE).copy(alpha = 0.4f)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                caseSolution.actionsNotToDo.forEach { prohibited ->
                    Row(verticalAlignment = Alignment.Top) {
                        Text("❌ ", fontSize = 12.sp)
                        Text(
                            text = prohibited,
                            fontSize = 12.5.sp,
                            lineHeight = 17.sp,
                            color = Color(0xFFB71C1C),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // 8 y 9. Solución Correcta y Explicación Jurídica (con botón de revelación interactiva)
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Gavel, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "8 y 9. Solución Oficial y Fundamento Legal",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    TextButton(
                        onClick = { isSolutionRevealed = !isSolutionRevealed }
                    ) {
                        Text(if (isSolutionRevealed) "Ocultar" else "Ver Solución", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }

                AnimatedVisibility(visible = isSolutionRevealed) {
                    Column(modifier = Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))

                        Text(
                            text = "Solución Correcta:",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = caseSolution.correctSolution,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "¿Por qué esa solución es la correcta?:",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = caseSolution.whySolutionIsCorrect,
                            fontSize = 12.5.sp,
                            lineHeight = 17.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Base Legal & Normativa:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = caseSolution.legalNormativeRef,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Fuente en Biblioteca: ${caseSolution.libraryDocumentRef}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Consult with AI Action
        Button(
            onClick = {
                val prompt = "Analizar el caso electoral '${caseSolution.title}'. Procedimiento legal correcto para JRV, JEM, DOE y PNC según el Código Electoral de El Salvador:"
                onClose()
                viewModel.onQueryInputChange(prompt)
                viewModel.submitQuery(prompt)
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Consultar este caso con el Asistente IA", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun DetailSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    containerColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

// =========================================================================
// APARTADO B: EXAMEN ELECTORAL (25 PREGUNTAS CON REPRESENTACIÓN DE PAPELETAS)
// =========================================================================

@Composable
private fun ActiveExamQuestionView(
    questions: List<ExamQuestion>,
    currentIndex: Int,
    selectedOption: Int?,
    hasAnswered: Boolean,
    onOptionSelected: (Int) -> Unit,
    onNextQuestion: () -> Unit,
    onPreviousQuestion: () -> Unit,
    viewModel: ElectoralViewModel
) {
    val currentQuestion = questions[currentIndex]
    val totalCount = questions.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("active_exam_question_view"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Progress header: Pregunta X de 25
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(12.dp),
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pregunta ${currentIndex + 1} de $totalCount",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = currentQuestion.category,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { (currentIndex + 1).toFloat() / totalCount.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                )
            }
        }

        // Visual Ballot representation (if question type is BALLOT_VALIDITY)
        if (currentQuestion.type == QuestionType.BALLOT_VALIDITY && currentQuestion.ballotMarkType != null) {
            BallotVisualizerView(
                markType = currentQuestion.ballotMarkType,
                modifier = Modifier.fillMaxWidth()
            )

            // Explanatory text below ballot describing what is observed WITHOUT revealing answer
            currentQuestion.ballotVisualDescription?.let { desc ->
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Visibility,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Descripción de lo que se observa en la papeleta:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = desc,
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Question Prompt Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                currentQuestion.situationContext?.let { contextText ->
                    Text(
                        text = "Contexto: $contextText",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                Text(
                    text = currentQuestion.questionText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Options List (VOTO VÁLIDO / VOTO NULO or multiple choice)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            currentQuestion.options.forEachIndexed { optIndex, optionText ->
                val isSelected = selectedOption == optIndex
                val isCorrectOption = optIndex == currentQuestion.correctOptionIndex

                val borderColor = when {
                    hasAnswered && isCorrectOption -> Color(0xFF2E7D32) // Green
                    hasAnswered && isSelected && !isCorrectOption -> Color(0xFFC62828) // Red
                    isSelected -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                }

                val containerColor = when {
                    hasAnswered && isCorrectOption -> Color(0xFFE8F5E9)
                    hasAnswered && isSelected && !isCorrectOption -> Color(0xFFFFEBEE)
                    isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    else -> MaterialTheme.colorScheme.surface
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !hasAnswered) {
                            onOptionSelected(optIndex)
                        }
                        .testTag("exam_option_$optIndex"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = containerColor),
                    border = BorderStroke(if (isSelected || (hasAnswered && isCorrectOption)) 2.dp else 1.dp, borderColor)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(
                                    color = when {
                                        hasAnswered && isCorrectOption -> Color(0xFF2E7D32)
                                        hasAnswered && isSelected && !isCorrectOption -> Color(0xFFC62828)
                                        isSelected -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    },
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = ('A' + optIndex).toString(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected || (hasAnswered && isCorrectOption)) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = optionText,
                            fontSize = 13.5.sp,
                            fontWeight = if (isSelected || (hasAnswered && isCorrectOption)) FontWeight.Bold else FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )

                        if (hasAnswered) {
                            if (isCorrectOption) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Correcto", tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
                            } else if (isSelected) {
                                Icon(Icons.Default.Cancel, contentDescription = "Incorrecto", tint = Color(0xFFC62828), modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }

        // Answer Feedback Card (Shown immediately after responding)
        AnimatedVisibility(
            visible = hasAnswered,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            val wasCorrect = selectedOption == currentQuestion.correctOptionIndex
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (wasCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                ),
                border = BorderStroke(1.dp, if (wasCorrect) Color(0xFFA5D6A7) else Color(0xFFFFCDD2)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (wasCorrect) Icons.Default.CheckCircle else Icons.Default.Info,
                            contentDescription = null,
                            tint = if (wasCorrect) Color(0xFF2E7D32) else Color(0xFFC62828),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (wasCorrect) "¡Resultado Correcto!" else "Resultado Incorrecto",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = if (wasCorrect) Color(0xFF1B5E20) else Color(0xFFB71C1C)
                        )
                    }

                    Text(
                        text = "Explicación:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = currentQuestion.explanation,
                        fontSize = 12.5.sp,
                        lineHeight = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Surface(
                        color = Color.White.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "Referencia Normativa:",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0D47A1)
                            )
                            Text(
                                text = currentQuestion.normativeReference,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "Biblioteca Electoral: ${currentQuestion.sourceDocument}",
                                fontSize = 10.5.sp,
                                color = Color(0xFF546E7A)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Navigation Action Buttons (Anterior / Siguiente / Finalizar)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onPreviousQuestion,
                enabled = currentIndex > 0,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Anterior", fontSize = 12.sp)
            }

            Button(
                onClick = onNextQuestion,
                enabled = hasAnswered,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("exam_next_button")
            ) {
                Text(
                    text = if (currentIndex < totalCount - 1) "Siguiente Pregunta" else "Ver Resultados del Examen",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// =========================================================================
// RESUMEN FINAL DEL EXAMEN ELECTORAL (APROBADO/NO APROBADO Y REVISIÓN)
// =========================================================================

@Composable
private fun ExamResultsSummaryView(
    questions: List<ExamQuestion>,
    userAnswers: Map<Int, Int>,
    correctCount: Int,
    incorrectCount: Int,
    scorePercentage: Float,
    isApproved: Boolean,
    onRetakeWithNewQuestions: () -> Unit,
    viewModel: ElectoralViewModel
) {
    var filterOnlyIncorrect by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("exam_results_summary_view"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Summary Score Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isApproved) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
            ),
            border = BorderStroke(2.dp, if (isApproved) Color(0xFF4CAF50) else Color(0xFFFF9800)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(if (isApproved) Color(0xFF2E7D32) else Color(0xFFE65100), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isApproved) Icons.Default.EmojiEvents else Icons.Default.Refresh,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isApproved) "¡EXAMEN APROBADO!" else "EXAMEN NO APROBADO",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isApproved) Color(0xFF1B5E20) else Color(0xFFBF360C)
                )

                Text(
                    text = if (isApproved)
                        "Ha superado satisfactoriamente la evaluación de conocimientos electorales."
                    else
                        "Se requiere un mínimo de 70% (18 aciertos de 25) para aprobar.",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Score metrics Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "$correctCount/25", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF2E7D32))
                        Text(text = "Aciertos", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "$incorrectCount/25", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFFC62828))
                        Text(text = "Fallos", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${String.format("%.1f", scorePercentage)}%", fontSize = 18.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                        Text(text = "Calificación", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Action: "Realizar otro examen con preguntas diferentes"
        Button(
            onClick = onRetakeWithNewQuestions,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("retake_exam_button")
        ) {
            Icon(Icons.Default.Autorenew, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Realizar otro examen con preguntas diferentes", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Review Section Header & Filter
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "REVISIÓN DETALLADA DE PREGUNTAS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.5.sp
            )

            FilterChip(
                selected = filterOnlyIncorrect,
                onClick = { filterOnlyIncorrect = !filterOnlyIncorrect },
                label = { Text("Solo errores (${incorrectCount})", fontSize = 11.sp) }
            )
        }

        // List of question reviews
        questions.forEachIndexed { index, question ->
            val userOption = userAnswers[index]
            val isCorrect = userOption == question.correctOptionIndex

            if (!filterOnlyIncorrect || !isCorrect) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, if (isCorrect) Color(0xFF81C784) else Color(0xFFE57373)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Pregunta #${index + 1} • ${question.category}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Surface(
                                color = if (isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = if (isCorrect) "ACIERTO" else "INCORRECTO",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = question.questionText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (!isCorrect && userOption != null) {
                            Text(
                                text = "Su respuesta: ${question.options.getOrElse(userOption) { "" }}",
                                fontSize = 11.5.sp,
                                color = Color(0xFFC62828)
                            )
                        }

                        Text(
                            text = "Respuesta correcta: ${question.options.getOrElse(question.correctOptionIndex) { "" }}",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2E7D32)
                        )

                        Text(
                            text = "Explicación: ${question.explanation}",
                            fontSize = 11.5.sp,
                            lineHeight = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "Base legal: ${question.normativeReference} (${question.sourceDocument})",
                            fontSize = 10.5.sp,
                            color = Color(0xFF0D47A1)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
