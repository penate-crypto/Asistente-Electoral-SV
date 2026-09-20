package com.example.ui

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.TtsPlaybackState
import com.example.data.database.QueryHistory
import com.example.data.repository.ApiKeyStatus
import com.example.viewmodel.ElectoralUiState
import com.example.viewmodel.ElectoralViewModel
import com.example.viewmodel.ElectoralScreen
import com.example.viewmodel.VoiceConversationState
import com.example.ui.components.TransparentAssetImage
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElectoralMainScreen(
    viewModel: ElectoralViewModel,
    onVoiceButtonTapped: () -> Unit,
    onSpeakText: (String) -> Unit,
    onPauseSpeaking: () -> Unit = {},
    onResumeSpeaking: () -> Unit = {},
    onStopSpeaking: () -> Unit,
    onRestartSpeaking: () -> Unit = {},
    onToggleDarkTheme: () -> Unit = {},
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val queryInput by viewModel.queryInput.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val voiceState by viewModel.voiceState.collectAsState()
    val voiceStatusMessage by viewModel.voiceStatusMessage.collectAsState()
    val ttsPlaybackState by viewModel.ttsPlaybackState.collectAsState()
    val historyList by viewModel.historyList.collectAsState()
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val apiKeyStatus by viewModel.apiKeyStatus.collectAsState()
    val currentScreen by viewModel.currentScreen.collectAsState()
    val preferredBookTitle by viewModel.preferredBookTitle.collectAsState()

    // Loading rotation/quotes logic matching design aesthetic
    val loadingQuotes = listOf(
        "Consultando información...",
        "Consultando leyes y normativa electoral salvadoreña...",
        "Verificando instructivos de JRV y resoluciones del TSE...",
        "Estructurando base legal y orientación práctica..."
    )
    var currentQuoteIndex by remember { mutableStateOf(0) }
    
    // Cycle through loading quotes
    if (uiState is ElectoralUiState.Loading) {
        LaunchedEffect(Unit) {
            currentQuoteIndex = 0
            while (true) {
                delay(2000)
                currentQuoteIndex = (currentQuoteIndex + 1) % loadingQuotes.size
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .width(320.dp)
                    .fillMaxHeight()
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Drawer Header with Creator Signature
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF0D47A1),
                                    Color(0xFF1976D2),
                                    Color(0xFF0288D1)
                                )
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(18.dp)
                ) {
                    TransparentAssetImage(
                        assetPath = "imagenes/Isotipo-02.png",
                        contentDescription = "Logo Principal",
                        modifier = Modifier
                            .size(54.dp)
                            .padding(bottom = 2.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Asistente Virtual Electoral",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "App de IA para apoyo electoral",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.5.sp
                    )
                    Text(
                        text = "Creador: Rodrigo Peñate",
                        color = Color(0xFFFFD54F),
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Navigation Items inside Drawer
                Text(
                    text = "SECCIONES PRINCIPALES",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Forum, contentDescription = null) },
                    label = { Text("Asistente IA", fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold) },
                    selected = currentScreen == ElectoralScreen.CHAT,
                    onClick = {
                        viewModel.setScreen(ElectoralScreen.CHAT)
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = null) },
                    label = { Text("Biblioteca Electoral (PDF)", fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold) },
                    selected = currentScreen == ElectoralScreen.BIBLIOTECA,
                    onClick = {
                        viewModel.setScreen(ElectoralScreen.BIBLIOTECA)
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Psychology, contentDescription = null) },
                    label = { Text("Test Electoral", fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold) },
                    selected = currentScreen == ElectoralScreen.SIMULADOR,
                    onClick = {
                        viewModel.setScreen(ElectoralScreen.SIMULADOR)
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Route, contentDescription = null) },
                    label = { Text("Buscar información (Paso a Paso)", fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold) },
                    selected = currentScreen == ElectoralScreen.PASO_A_PASO,
                    onClick = {
                        viewModel.setScreen(ElectoralScreen.PASO_A_PASO)
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Configuración", fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold) },
                    selected = currentScreen == ElectoralScreen.CONFIGURACION,
                    onClick = {
                        viewModel.setScreen(ElectoralScreen.CONFIGURACION)
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Suggestion Header
                Text(
                    text = "CONSULTAS RECOMENDADAS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )

                // Recommended list
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .verticalScroll(rememberScrollState())
                        .weight(1f)
                ) {
                    val recommendedList = listOf(
                        "¿Cómo se integra una JRV?" to "¿Cómo se integra una Junta Receptora de Votos (JRV)?",
                        "Permisos de trabajo (Art. 113)" to "¿Qué establece el Art. 113 del Código Electoral sobre los permisos laborales con goce de sueldo?",
                        "Prohibición de armas en centros" to "¿Qué normativa regula la prohibición de armas en centros de votación en El Salvador?",
                        "Procedimiento de Escrutinio" to "¿Cuál es el procedimiento paso a paso para realizar el escrutinio de votos?",
                        "Protección a la mujer (LEIV)" to "¿Cómo protege la ley a las mujeres miembros de mesa contra violencia política?"
                    )
                    
                    recommendedList.forEach { (label, question) ->
                        NavigationDrawerItem(
                            icon = { Icon(Icons.AutoMirrored.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            label = { Text(label, fontSize = 13.sp) },
                            selected = false,
                            onClick = {
                                viewModel.onQueryInputChange(question)
                                scope.launch { drawerState.close() }
                                viewModel.submitQuery(question)
                            },
                            modifier = Modifier.padding(vertical = 1.dp)
                        )
                    }

                    if (historyList.isNotEmpty()) {
                        Divider(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                        
                        Text(
                            text = "HISTORIAL RECIENTE",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )

                        historyList.take(4).forEach { historyItem ->
                            NavigationDrawerItem(
                                icon = { Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                label = { 
                                    Text(
                                        text = historyItem.question,
                                        fontSize = 12.5.sp,
                                        maxLines = 1,
                                        modifier = Modifier.fillMaxWidth()
                                    ) 
                                },
                                selected = false,
                                onClick = {
                                    viewModel.selectHistoryItem(historyItem)
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(vertical = 1.dp)
                            )
                        }
                    }
                }

                Divider(modifier = Modifier.padding(horizontal = 12.dp))
                
                // Footer buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            viewModel.resetState()
                            viewModel.setScreen(ElectoralScreen.CHAT)
                            onStopSpeaking()
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.testTag("reset_button")
                    ) {
                        Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Inicio", fontSize = 13.sp)
                    }

                    TextButton(
                        onClick = {
                            scope.launch { drawerState.close() }
                            viewModel.logout(context)
                            onLogout()
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.testTag("drawer_logout_button")
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Salir", fontSize = 13.sp)
                    }

                    if (historyList.isNotEmpty()) {
                        TextButton(
                            onClick = { viewModel.clearHistory() },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                            modifier = Modifier.testTag("clear_history_button")
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Limpiar", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Limpiar", fontSize = 13.sp)
                        }
                    }
                }
            }
        },
        modifier = modifier
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Asistente Virtual Electoral",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "El Salvador - Apoyo electoral IA",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier.testTag("menu_button")
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir menú lateral")
                        }
                    },
                    actions = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            // BOTÓN DE TEMA CLARO / OSCURO (SWITCH MANUAL)
                            IconButton(
                                onClick = onToggleDarkTheme,
                                modifier = Modifier.testTag("topbar_theme_toggle_btn")
                            ) {
                                Icon(
                                    imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = if (isDarkTheme) "Cambiar a Modo Claro" else "Cambiar a Modo Oscuro",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(onClick = { viewModel.resetState() }) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "Limpiar Consulta",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            TransparentAssetImage(
                                assetPath = "imagenes/Isotipo-02.png",
                                contentDescription = "SV Identidad",
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                    )
                )
            },
            bottomBar = {
                val isKeyboardVisible = WindowInsets.ime.asPaddingValues().calculateBottomPadding() > 0.dp
                if (!isKeyboardVisible) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp,
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                            )
                            .testTag("electoral_navigation_bar")
                    ) {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Forum, contentDescription = "Asistente IA") },
                            label = { Text("Asistente IA", fontSize = 10.5.sp, fontWeight = FontWeight.Bold) },
                            selected = currentScreen == ElectoralScreen.CHAT,
                            onClick = { viewModel.setScreen(ElectoralScreen.CHAT) },
                            modifier = Modifier.testTag("nav_item_chat")
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.MenuBook, contentDescription = "Biblioteca") },
                            label = { Text("Biblioteca", fontSize = 10.5.sp, fontWeight = FontWeight.Bold) },
                            selected = currentScreen == ElectoralScreen.BIBLIOTECA,
                            onClick = { viewModel.setScreen(ElectoralScreen.BIBLIOTECA) },
                            modifier = Modifier.testTag("nav_item_biblioteca")
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Psychology, contentDescription = "Test") },
                            label = { Text("Test", fontSize = 10.5.sp, fontWeight = FontWeight.Bold) },
                            selected = currentScreen == ElectoralScreen.SIMULADOR,
                            onClick = { viewModel.setScreen(ElectoralScreen.SIMULADOR) },
                            modifier = Modifier.testTag("nav_item_simulador")
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Route, contentDescription = "Paso a Paso") },
                            label = { Text("Paso a Paso", fontSize = 10.5.sp, fontWeight = FontWeight.Bold) },
                            selected = currentScreen == ElectoralScreen.PASO_A_PASO,
                            onClick = { viewModel.setScreen(ElectoralScreen.PASO_A_PASO) },
                            modifier = Modifier.testTag("nav_item_paso_a_paso")
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Settings, contentDescription = "Configuración") },
                            label = { Text("Ajustes", fontSize = 10.5.sp, fontWeight = FontWeight.Bold) },
                            selected = currentScreen == ElectoralScreen.CONFIGURACION,
                            onClick = { viewModel.setScreen(ElectoralScreen.CONFIGURACION) },
                            modifier = Modifier.testTag("nav_item_configuracion")
                        )
                    }
                }
            },
            contentWindowInsets = WindowInsets.statusBars
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding()
                    )
                    .imePadding()
            ) {
                // Core content area
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    when (currentScreen) {
                        ElectoralScreen.CHAT -> {
                            when (val state = uiState) {
                                is ElectoralUiState.Idle -> {
                                    // Completely clean area initially prepared for consultations
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        // Clean empty canvas without auto-generated messages or predefined cards
                                    }
                                }

                                is ElectoralUiState.Loading -> {
                                    // Visual Indicator: "Consultando información..." with animated progress bar
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(24.dp)
                                            .testTag("loading_indicator_panel"),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(60.dp)
                                                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(36.dp),
                                                strokeWidth = 3.dp,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        
                                        Spacer(modifier = Modifier.height(20.dp))

                                        // Required visual indicator
                                        Text(
                                            text = "Consultando información...",
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            textAlign = TextAlign.Center
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Crossfade(
                                            targetState = currentQuoteIndex,
                                            animationSpec = tween(400)
                                        ) { index ->
                                            Text(
                                                text = loadingQuotes[index],
                                                fontSize = 12.5.sp,
                                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(horizontal = 16.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(18.dp))

                                        LinearProgressIndicator(
                                            modifier = Modifier
                                                .width(220.dp)
                                                .height(4.dp)
                                                .clip(CircleShape),
                                            color = MaterialTheme.colorScheme.primary,
                                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        )
                                    }
                                }

                                is ElectoralUiState.Success -> {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .verticalScroll(rememberScrollState())
                                            .padding(16.dp)
                                            .testTag("response_view_panel")
                                    ) {
                                        // User Question Card
                                        Card(
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)
                                            ),
                                            shape = RoundedCornerShape(14.dp),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 12.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Default.Help,
                                                    contentDescription = "Consulta",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text(
                                                    text = state.question,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onBackground,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 13.5.sp
                                                )
                                            }
                                        }

                                        // Structured Official & Orientative Answer Card
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                            shape = RoundedCornerShape(18.dp),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(16.dp)) {
                                                // Answer header toolbar
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(bottom = 10.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            imageVector = Icons.Default.Gavel,
                                                            contentDescription = null,
                                                            tint = MaterialTheme.colorScheme.primary,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            text = "ORIENTACIÓN LEGAL ELECTORAL",
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.primary,
                                                            letterSpacing = 0.5.sp
                                                        )
                                                    }

                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        // Voice playback controls
                                                        val isSpeaking = ttsPlaybackState == TtsPlaybackState.PLAYING
                                                        val isPaused = ttsPlaybackState == TtsPlaybackState.PAUSED
                                                        val isVoiceActive = isSpeaking || isPaused

                                                        if (isVoiceActive) {
                                                            // Pause / Resume
                                                            IconButton(
                                                                onClick = {
                                                                    if (isSpeaking) onPauseSpeaking() else onResumeSpeaking()
                                                                },
                                                                modifier = Modifier.testTag("audio_pause_resume_button")
                                                            ) {
                                                                Icon(
                                                                    imageVector = if (isSpeaking) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                                                    contentDescription = if (isSpeaking) "Pausar audio" else "Continuar audio",
                                                                    tint = MaterialTheme.colorScheme.primary,
                                                                    modifier = Modifier.size(22.dp)
                                                                )
                                                            }

                                                            // Stop
                                                            IconButton(
                                                                onClick = onStopSpeaking,
                                                                modifier = Modifier.testTag("audio_stop_button")
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Default.StopCircle,
                                                                    contentDescription = "Detener audio",
                                                                    tint = MaterialTheme.colorScheme.error,
                                                                    modifier = Modifier.size(22.dp)
                                                                )
                                                            }

                                                            // Restart from beginning
                                                            IconButton(
                                                                onClick = onRestartSpeaking,
                                                                modifier = Modifier.testTag("audio_restart_button")
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Default.Replay,
                                                                    contentDescription = "Reiniciar audio",
                                                                    tint = MaterialTheme.colorScheme.secondary,
                                                                    modifier = Modifier.size(20.dp)
                                                                )
                                                            }
                                                        } else {
                                                            // Play complete answer
                                                            IconButton(
                                                                onClick = { onSpeakText(state.answer) },
                                                                modifier = Modifier.testTag("audio_playback_button")
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Default.VolumeUp,
                                                                    contentDescription = "Escuchar respuesta completa",
                                                                    tint = MaterialTheme.colorScheme.primary,
                                                                    modifier = Modifier.size(22.dp)
                                                                )
                                                            }
                                                        }

                                                        // Copy
                                                        IconButton(
                                                            onClick = {
                                                                clipboardManager.setText(AnnotatedString(state.answer))
                                                            },
                                                            modifier = Modifier.testTag("copy_response_button")
                                                        ) {
                                                            Icon(
                                                                Icons.Default.ContentCopy,
                                                                contentDescription = "Copiar respuesta",
                                                                tint = MaterialTheme.colorScheme.secondary,
                                                                modifier = Modifier.size(20.dp)
                                                            )
                                                        }
                                                    }
                                                }

                                                Divider(modifier = Modifier.padding(bottom = 12.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))

                                                // Render Answer Body
                                                SelectionContainer {
                                                    FormattedElectoralText(text = state.answer)
                                                }

                                                Spacer(modifier = Modifier.height(14.dp))

                                                // "VER FUENTE" and Direct Action Buttons Bar
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    // Primary "📖 VER FUENTE" Button
                                                    Button(
                                                        onClick = {
                                                            // Determine most relevant doc and page from the answer
                                                            val docId = when {
                                                                state.answer.contains("Constitución", ignoreCase = true) -> "constitucion_republica_elsalvador"
                                                                state.answer.contains("Instructivo", ignoreCase = true) || state.answer.contains("JRV", ignoreCase = true) -> "instructivo_jrv_tse_2024"
                                                                state.answer.contains("Código Penal", ignoreCase = true) -> "codigo_penal_delitos_electorales"
                                                                state.answer.contains("LEIV", ignoreCase = true) -> "ley_especial_integral_mujeres_leiv"
                                                                else -> "codigo_electoral_decreto_413"
                                                            }
                                                            viewModel.openPdfDirectly(docId = docId, page = 1)
                                                        },
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = MaterialTheme.colorScheme.primary
                                                        ),
                                                        shape = RoundedCornerShape(10.dp),
                                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .testTag("btn_ver_fuente")
                                                    ) {
                                                        Icon(
                                                            Icons.Default.MenuBook,
                                                            contentDescription = null,
                                                            tint = Color.White,
                                                            modifier = Modifier.size(17.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            text = "📖 VER FUENTE",
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color.White
                                                        )
                                                    }

                                                    // Secondary "🔎 BUSCAR EN BIBLIOTECA" Button
                                                    OutlinedButton(
                                                        onClick = {
                                                            viewModel.setScreen(ElectoralScreen.BIBLIOTECA)
                                                        },
                                                        shape = RoundedCornerShape(10.dp),
                                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                                        modifier = Modifier.testTag("btn_buscar_biblioteca")
                                                    ) {
                                                        Icon(
                                                            Icons.Default.Search,
                                                            contentDescription = null,
                                                            tint = MaterialTheme.colorScheme.primary,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                            text = "Biblioteca",
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.SemiBold,
                                                            color = MaterialTheme.colorScheme.primary
                                                        )
                                                    }
                                                }

                                                if (ttsPlaybackState == TtsPlaybackState.PLAYING || ttsPlaybackState == TtsPlaybackState.PAUSED) {
                                                    Spacer(modifier = Modifier.height(12.dp))
                                                    Surface(
                                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                                                        shape = RoundedCornerShape(12.dp),
                                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.SpaceBetween
                                                        ) {
                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                modifier = Modifier.weight(1f)
                                                            ) {
                                                                Icon(
                                                                    imageVector = if (ttsPlaybackState == TtsPlaybackState.PLAYING) Icons.Default.VolumeUp else Icons.Default.Pause,
                                                                    contentDescription = null,
                                                                    tint = MaterialTheme.colorScheme.primary,
                                                                    modifier = Modifier.size(18.dp)
                                                                )
                                                                Spacer(modifier = Modifier.width(8.dp))
                                                                Text(
                                                                    text = if (ttsPlaybackState == TtsPlaybackState.PLAYING) "Reproduciendo respuesta completa..." else "Voz en pausa",
                                                                    fontSize = 12.sp,
                                                                    fontWeight = FontWeight.Medium,
                                                                    color = MaterialTheme.colorScheme.primary
                                                                )
                                                            }

                                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                                IconButton(
                                                                    onClick = {
                                                                        if (ttsPlaybackState == TtsPlaybackState.PLAYING) onPauseSpeaking() else onResumeSpeaking()
                                                                    },
                                                                    modifier = Modifier.size(32.dp)
                                                                ) {
                                                                    Icon(
                                                                        imageVector = if (ttsPlaybackState == TtsPlaybackState.PLAYING) Icons.Default.Pause else Icons.Default.PlayArrow,
                                                                        contentDescription = "Pausar/Reanudar",
                                                                        tint = MaterialTheme.colorScheme.primary
                                                                    )
                                                                }
                                                                IconButton(
                                                                    onClick = onStopSpeaking,
                                                                    modifier = Modifier.size(32.dp)
                                                                ) {
                                                                    Icon(
                                                                        Icons.Default.Stop,
                                                                        contentDescription = "Detener",
                                                                        tint = MaterialTheme.colorScheme.error
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        // "¿Quisiste decir...?" / Sugerencias de consulta
                                        if (state.suggestions.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Surface(
                                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                                                shape = RoundedCornerShape(12.dp),
                                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(modifier = Modifier.padding(12.dp)) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            Icons.Default.AutoFixHigh,
                                                            contentDescription = null,
                                                            tint = MaterialTheme.colorScheme.primary,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            text = "¿Quisiste decir...? (Sugerencias)",
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.primary
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    state.suggestions.take(3).forEach { suggestion ->
                                                        Surface(
                                                            color = MaterialTheme.colorScheme.surface,
                                                            shape = RoundedCornerShape(8.dp),
                                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(vertical = 3.dp)
                                                                .clickable {
                                                                    viewModel.onQueryInputChange(suggestion)
                                                                    viewModel.submitQuery(suggestion)
                                                                }
                                                        ) {
                                                            Row(
                                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.SpaceBetween
                                                            ) {
                                                                Text(
                                                                    text = suggestion,
                                                                    fontSize = 12.sp,
                                                                    color = MaterialTheme.colorScheme.onSurface,
                                                                    modifier = Modifier.weight(1f)
                                                                )
                                                                Icon(
                                                                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                                                    contentDescription = "Consultar",
                                                                    tint = MaterialTheme.colorScheme.primary,
                                                                    modifier = Modifier.size(16.dp)
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // Required Mandatory Warning Banner
                                        Surface(
                                            color = Color(0xFFFFF8E1),
                                            shape = RoundedCornerShape(12.dp),
                                            border = BorderStroke(1.dp, Color(0xFFFFB300).copy(alpha = 0.4f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Icon(
                                                    Icons.Default.WarningAmber,
                                                    contentDescription = "Aviso legal",
                                                    tint = Color(0xFFF57F17),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente.",
                                                    fontSize = 11.5.sp,
                                                    lineHeight = 16.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    color = Color(0xFF5D4037)
                                                )
                                            }
                                        }
                                    }
                                }

                                is ElectoralUiState.Error -> {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .verticalScroll(rememberScrollState())
                                            .padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            Icons.Default.ErrorOutline,
                                            contentDescription = "Error",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(52.dp)
                                        )
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Text(
                                            text = "Aviso de Consulta",
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = state.message,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(horizontal = 16.dp)
                                        )
                                        Spacer(modifier = Modifier.height(20.dp))
                                        Button(
                                            onClick = { viewModel.submitQuery(state.question) },
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                            modifier = Modifier.testTag("retry_button")
                                        ) {
                                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Reintentar")
                                        }
                                    }
                                }
                            }
                        }
                        ElectoralScreen.BIBLIOTECA -> {
                            BibliotecaScreen(viewModel = viewModel)
                        }
                        ElectoralScreen.SIMULADOR -> {
                            SimulacionScreen(viewModel = viewModel)
                        }
                        ElectoralScreen.PASO_A_PASO -> {
                            PasoAPasoScreen(viewModel = viewModel)
                        }
                        ElectoralScreen.CAPACITACION -> {
                            CapacitacionScreen(viewModel = viewModel)
                        }
                        ElectoralScreen.CONFIGURACION -> {
                            ConfiguracionScreen(
                                viewModel = viewModel,
                                onLogout = onLogout
                            )
                        }
                    }
                }

                // Chat Input Deck
                if (currentScreen == ElectoralScreen.CHAT) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Focus on specific book badge if selected
                        if (preferredBookTitle != null) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.MenuBook,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Priorizando: $preferredBookTitle",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.primary,
                                            maxLines = 1
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.clearPreferredBook() },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Quitar filtro de libro",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Voice conversation live status pill
                        val isVoiceActive = voiceState != VoiceConversationState.IDLE
                        val statusBgColor = when (voiceState) {
                            VoiceConversationState.LISTENING -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                            VoiceConversationState.PROCESSING -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                            VoiceConversationState.SPEAKING -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                            VoiceConversationState.ERROR -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
                            VoiceConversationState.IDLE -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        }

                        val statusTextColor = when (voiceState) {
                            VoiceConversationState.LISTENING -> MaterialTheme.colorScheme.error
                            VoiceConversationState.SPEAKING -> MaterialTheme.colorScheme.primary
                            VoiceConversationState.PROCESSING -> MaterialTheme.colorScheme.secondary
                            VoiceConversationState.ERROR -> MaterialTheme.colorScheme.error
                            VoiceConversationState.IDLE -> MaterialTheme.colorScheme.onSurfaceVariant
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp)
                                .clickable { onVoiceButtonTapped() },
                            shape = RoundedCornerShape(14.dp),
                            color = statusBgColor,
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (isVoiceActive) statusTextColor.copy(alpha = 0.35f) else Color.Transparent
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = when (voiceState) {
                                            VoiceConversationState.LISTENING -> Icons.Default.Mic
                                            VoiceConversationState.PROCESSING -> Icons.Default.AutoAwesome
                                            VoiceConversationState.SPEAKING -> Icons.Default.VolumeUp
                                            VoiceConversationState.ERROR -> Icons.Default.Info
                                            VoiceConversationState.IDLE -> Icons.Default.MicNone
                                        },
                                        contentDescription = null,
                                        tint = statusTextColor,
                                        modifier = Modifier.size(17.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = voiceStatusMessage,
                                        fontSize = 12.sp,
                                        fontWeight = if (isVoiceActive) FontWeight.Bold else FontWeight.Medium,
                                        color = statusTextColor
                                    )
                                }

                                if (voiceState == VoiceConversationState.LISTENING) {
                                    Text(
                                        text = "Cancelar",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                } else if (voiceState == VoiceConversationState.SPEAKING) {
                                    Text(
                                        text = "Detener",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        // Query Input Card (Single Unified Microphone Button + Text Field + Send Button)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 6.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // THE SINGLE VOICE CONVERSATION BUTTON
                                IconButton(
                                    onClick = onVoiceButtonTapped,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            color = when (voiceState) {
                                                VoiceConversationState.LISTENING -> MaterialTheme.colorScheme.errorContainer
                                                VoiceConversationState.SPEAKING -> MaterialTheme.colorScheme.primaryContainer
                                                VoiceConversationState.PROCESSING -> MaterialTheme.colorScheme.secondaryContainer
                                                VoiceConversationState.IDLE,
                                                VoiceConversationState.ERROR -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                            },
                                            shape = CircleShape
                                        )
                                        .testTag("microphone_button")
                                ) {
                                    Icon(
                                        imageVector = when (voiceState) {
                                            VoiceConversationState.LISTENING -> Icons.Default.Mic
                                            VoiceConversationState.SPEAKING -> Icons.Default.VolumeUp
                                            VoiceConversationState.PROCESSING -> Icons.Default.HourglassEmpty
                                            VoiceConversationState.IDLE,
                                            VoiceConversationState.ERROR -> Icons.Default.Mic
                                        },
                                        contentDescription = "Conversar por voz",
                                        tint = when (voiceState) {
                                            VoiceConversationState.LISTENING -> MaterialTheme.colorScheme.error
                                            VoiceConversationState.SPEAKING -> MaterialTheme.colorScheme.primary
                                            VoiceConversationState.PROCESSING -> MaterialTheme.colorScheme.secondary
                                            VoiceConversationState.IDLE,
                                            VoiceConversationState.ERROR -> MaterialTheme.colorScheme.primary
                                        },
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(4.dp))

                                // Text field (Written mode - untouched)
                                TextField(
                                    value = queryInput,
                                    onValueChange = { viewModel.onQueryInputChange(it) },
                                    placeholder = { Text("Escriba una pregunta o situación electoral...", fontSize = 13.sp) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .heightIn(min = 38.dp, max = 100.dp)
                                        .testTag("query_input_field"),
                                    maxLines = 3,
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        disabledContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        disabledIndicatorColor = Color.Transparent,
                                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    ),
                                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp)
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                // Send Button for written queries
                                IconButton(
                                    onClick = { viewModel.submitQuery() },
                                    enabled = queryInput.isNotEmpty() && uiState !is ElectoralUiState.Loading,
                                    modifier = Modifier
                                        .size(38.dp)
                                        .background(
                                            color = if (queryInput.isNotEmpty() && uiState !is ElectoralUiState.Loading) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                                            },
                                            shape = CircleShape
                                        )
                                        .testTag("send_button")
                                ) {
                                    Icon(
                                        Icons.Default.Send,
                                        contentDescription = "Enviar consulta",
                                        tint = if (queryInput.isNotEmpty() && uiState !is ElectoralUiState.Loading) {
                                            MaterialTheme.colorScheme.onPrimary
                                        } else {
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                        },
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}

/**
 * Clean styled topic card for electoral consultations.
 */
@Composable
fun ElectoralTopicCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(containerColor, shape = RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * Modern, responsive Markdown & Electoral Text renderer.
 * Formats headings, bold sections, numbered steps, bullet items, legal foundations,
 * and official disclaimers gracefully without misclassifying content into negative containers.
 */
@Composable
fun FormattedElectoralText(text: String) {
    val lines = remember(text) { text.split("\n") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        lines.forEach { line ->
            val trimmed = line.trim()
            if (trimmed.isBlank()) {
                Spacer(modifier = Modifier.height(3.dp))
                return@forEach
            }

            when {
                // Tema / Situación Header Banner
                trimmed.startsWith("Tema:", ignoreCase = true) || trimmed.startsWith("TEMA / SITUACIÓN:", ignoreCase = true) -> {
                    val topicTitle = trimmed.substringAfter(":").trim()
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "TEMA / SITUACIÓN",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 0.8.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = topicTitle,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }

                // Confidence Header Badges
                trimmed.startsWith("✅ **Coincidencia encontrada**") || trimmed.startsWith("✅ Coincidencia encontrada") -> {
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF81C784)),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "✅", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Coincidencia encontrada",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }

                trimmed.startsWith("ℹ️ **Coincidencia probable**") || trimmed.startsWith("ℹ️ Coincidencia probable") -> {
                    Surface(
                        color = Color(0xFFE3F2FD),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF90CAF9)),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "ℹ️", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Coincidencia probable",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1565C0)
                            )
                        }
                    }
                }

                trimmed.startsWith("⚠️ **Coincidencia baja**") || trimmed.startsWith("⚠️ Coincidencia baja") -> {
                    Surface(
                        color = Color(0xFFFFF8E1),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFFFD54F)),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "⚠️", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "No se encontró una coincidencia suficientemente precisa",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100)
                            )
                        }
                    }
                }

                // Section Headers (SITUACIÓN DETECTADA, ¿QUÉ HACER?, ¿QUÉ NO HACER?, FUNDAMENTO LEGAL, FUENTES, ¿QUIÉN PUEDE?, ¿QUIÉN NO PUEDE?, CONDICIONES)
                trimmed == "SITUACIÓN DETECTADA" || trimmed == "SITUACIÓN" -> {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "📌", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SITUACIÓN DETECTADA",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                trimmed == "¿QUÉ HACER?" || trimmed == "PROCEDIMIENTO" -> {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "📋", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "¿QUÉ HACER? — PROCEDIMIENTO",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                trimmed == "¿QUÉ NO HACER?" -> {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🚫", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "¿QUÉ NO HACER? — ACCIONES PROHIBIDAS",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.error,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                trimmed == "¿QUIÉN PUEDE?" -> {
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF81C784)),
                        modifier = Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "✔️", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "¿QUIÉN PUEDE? (FACULTADES)",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF2E7D32),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                trimmed == "¿QUIÉN NO PUEDE?" -> {
                    Surface(
                        color = Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFE57373)),
                        modifier = Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "❌", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "¿QUIÉN NO PUEDE? (PROHIBICIONES)",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFC62828),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                trimmed == "¿QUIÉN INTERVIENE?" || trimmed == "QUIÉN INTERVIENE" -> {
                    Surface(
                        color = Color(0xFFE0F2F1),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF80CBC4)),
                        modifier = Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "👥", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "¿QUIÉN INTERVIENE?",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF00695C),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                trimmed == "CONDICIONES" -> {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "⚖️", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CONDICIONES REQUERIDAS",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.secondary,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                trimmed == "FUNDAMENTO LEGAL" -> {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "⚖️", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "FUNDAMENTO LEGAL",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.secondary,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                trimmed == "ADVERTENCIA" -> {
                    Surface(
                        color = Color(0xFFFFF3E0),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFFFB74D)),
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "⚠️", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ADVERTENCIA DE SEGURIDAD / PENAL",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFE65100),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                trimmed == "FUENTES" -> {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "📚", fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "FUENTES DOCUMENTALES",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                // Section Badges with emojis (📌, 📝, ⚖️, 📚, 🚫)
                trimmed.startsWith("📌") || trimmed.startsWith("📝") || trimmed.startsWith("⚖️") || trimmed.startsWith("📚") || trimmed.startsWith("🚫") -> {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp, bottom = 2.dp)
                    ) {
                        Text(
                            text = trimmed.replace("**", ""),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                // Numbered Steps (e.g. "1. Paso uno...")
                trimmed.matches(Regex("^\\d+\\..*")) -> {
                    val dotIndex = trimmed.indexOf('.')
                    val number = trimmed.substring(0, dotIndex + 1)
                    val content = trimmed.substring(dotIndex + 1).trim()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                            modifier = Modifier.size(22.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = number.removeSuffix("."),
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = content.replace("**", ""),
                            fontSize = 13.sp,
                            lineHeight = 18.5.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Bullet Lists (•, -, *)
                trimmed.startsWith("•") || trimmed.startsWith("- ") || trimmed.startsWith("* ") -> {
                    val bulletContent = trimmed.removePrefix("•").removePrefix("- ").removePrefix("* ").trim()
                    val isNegative = bulletContent.startsWith("No ", ignoreCase = true) || bulletContent.startsWith("NO ")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 1.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = if (isNegative) "🚫" else "•",
                            fontSize = if (isNegative) 12.sp else 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isNegative) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = if (isNegative) 3.dp else 6.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = bulletContent.replace("**", ""),
                            fontSize = 13.sp,
                            lineHeight = 18.5.sp,
                            color = if (isNegative) MaterialTheme.colorScheme.error.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (isNegative) FontWeight.Medium else FontWeight.Normal,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Orientative Disclaimer Footer
                trimmed.startsWith("Esta respuesta es únicamente orientativa", ignoreCase = true) || trimmed.startsWith("Esta respuesta es de carácter orientativo", ignoreCase = true) -> {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = trimmed,
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Regular Text Line
                else -> {
                    Text(
                        text = trimmed.replace("**", ""),
                        fontSize = 13.sp,
                        lineHeight = 18.5.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

