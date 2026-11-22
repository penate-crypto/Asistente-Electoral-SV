package com.example

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.data.BookInternalSearchEngine
import com.example.data.ElectoralTtsManager
import com.example.data.LegalConsentManager
import com.example.data.TtsPlaybackState
import com.example.data.database.AppDatabase
import com.example.data.repository.ElectoralRepository
import com.example.ui.ElectoralMainScreen
import com.example.ui.LegalNoticeScreen
import com.example.ui.LoginScreen
import com.example.ui.WelcomeIntroScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.ElectoralUiState
import com.example.viewmodel.ElectoralViewModel
import com.example.viewmodel.ElectoralViewModelFactory
import com.example.viewmodel.VoiceConversationState

class MainActivity : ComponentActivity() {

    private lateinit var ttsManager: ElectoralTtsManager
    private var speechRecognizer: SpeechRecognizer? = null
    private var voiceRetryDone = false

    private lateinit var viewModel: ElectoralViewModel
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        try {
            val database = AppDatabase.getDatabase(this)
            val repository = ElectoralRepository(database.queryHistoryDao())
            val factory = ElectoralViewModelFactory(repository)
            viewModel = ViewModelProvider(this, factory)[ElectoralViewModel::class.java]
            viewModel.initSettings(this)
            BookInternalSearchEngine.init(this)
            com.example.data.search.knowledge.ElectoralKnowledgeBase.init(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Initialize advanced chunked Text-To-Speech engine
        ttsManager = ElectoralTtsManager(
            context = this,
            onStateChanged = { state ->
                viewModel.updateTtsPlaybackState(state)
            }
        )

        initSpeechRecognizerSafely()

        setContent {
            val currentFontSize by viewModel.appFontSize.collectAsState()
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            val isLoggedIn by viewModel.isLoggedIn.collectAsState()

            MyApplicationTheme(
                darkTheme = isDarkTheme,
                appFontSize = currentFontSize
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    var hasAcceptedLegalConsent by remember {
                        mutableStateOf(LegalConsentManager.isConsentAccepted(context))
                    }

                    // Native Fallback Intent Launcher for devices/emulators without background speech service
                    val speechIntentLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartActivityForResult()
                    ) { result ->
                        if (result.resultCode == Activity.RESULT_OK) {
                            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                            val text = matches?.firstOrNull()
                            if (!text.isNullOrBlank()) {
                                viewModel.onVoiceQueryReceived(text)
                            } else {
                                viewModel.onVoiceError("No pude entender la consulta. Intenta nuevamente.")
                            }
                        } else {
                            viewModel.cancelVoiceListening("Escucha cancelada.")
                        }
                    }

                    // Runtime Microphone Permission Launcher
                    val micPermissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission()
                    ) { isGranted ->
                        if (isGranted) {
                            startVoiceListeningFlow(speechIntentLauncher)
                        } else {
                            viewModel.onVoiceError("Se requiere permiso de micrófono para conversar por voz.")
                            Toast.makeText(this, "Permiso de micrófono denegado.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    val uiState by viewModel.uiState.collectAsState()
                    val isVoiceTurn by viewModel.isVoiceTurn.collectAsState()
                    val showIntroSplash by viewModel.showIntroSplash.collectAsState()

                    // Reactive Voice Speaker: When AI responds to a voice question, automatically speak the full answer!
                    LaunchedEffect(uiState) {
                        val state = uiState
                        if (state is ElectoralUiState.Success && isVoiceTurn) {
                            ttsManager.speakFullAnswer(state.answer)
                        } else if (state is ElectoralUiState.Error && isVoiceTurn) {
                            viewModel.onVoiceError("No fue posible obtener una respuesta. Intenta nuevamente.")
                        }
                    }

                    when {
                        showIntroSplash -> {
                            WelcomeIntroScreen(
                                onDismiss = { viewModel.dismissIntroSplash() }
                            )
                        }
                        !isLoggedIn -> {
                            LoginScreen(
                                onLoginSuccess = {
                                    viewModel.onLoginSuccess()
                                },
                                isDarkTheme = isDarkTheme,
                                onToggleDarkTheme = {
                                    viewModel.toggleDarkTheme(this)
                                }
                            )
                        }
                        !hasAcceptedLegalConsent -> {
                            LegalNoticeScreen(
                                onAcceptAndContinue = {
                                    hasAcceptedLegalConsent = true
                                },
                                onReject = {
                                    finish()
                                }
                            )
                        }
                        else -> {
                            ElectoralMainScreen(
                                viewModel = viewModel,
                                onVoiceButtonTapped = {
                                    handleVoiceButtonInteraction(micPermissionLauncher, speechIntentLauncher)
                                },
                                onSpeakText = { text ->
                                    ttsManager.speakFullAnswer(text)
                                },
                                onPauseSpeaking = {
                                    ttsManager.pause()
                                },
                                onResumeSpeaking = {
                                    ttsManager.resume()
                                },
                                onStopSpeaking = {
                                    ttsManager.stop()
                                },
                                onRestartSpeaking = {
                                    ttsManager.restart()
                                },
                                onToggleDarkTheme = {
                                    viewModel.toggleDarkTheme(this)
                                },
                                onLogout = {
                                    ttsManager.stop()
                                    viewModel.logout(this)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun initSpeechRecognizerSafely() {
        try {
            if (SpeechRecognizer.isRecognitionAvailable(this)) {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
                    setRecognitionListener(object : RecognitionListener {
                        override fun onReadyForSpeech(params: Bundle?) {
                            mainHandler.post {
                                viewModel.startVoiceListening()
                            }
                        }

                        override fun onBeginningOfSpeech() {}

                        override fun onRmsChanged(rmsdB: Float) {}

                        override fun onBufferReceived(buffer: ByteArray?) {}

                        override fun onEndOfSpeech() {}

                        override fun onError(error: Int) {
                            mainHandler.post {
                                when (error) {
                                    SpeechRecognizer.ERROR_NO_MATCH,
                                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                                        viewModel.onVoiceError("No pude entender la consulta. Intenta nuevamente.")
                                    }
                                    SpeechRecognizer.ERROR_NETWORK,
                                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT,
                                    SpeechRecognizer.ERROR_SERVER,
                                    ERROR_LANGUAGE_NOT_SUPPORTED,
                                    ERROR_LANGUAGE_UNAVAILABLE -> {
                                        // Primer intento: es-SV con preferencia sin conexión. Segundo intento: español latino (es-419).
                                        if (!voiceRetryDone && retryVoiceInLatinSpanish()) {
                                            return@post
                                        }
                                        viewModel.onVoiceError(VOICE_OFFLINE_HELP_MESSAGE)
                                    }
                                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> {
                                        viewModel.onVoiceError("Falta el permiso del micrófono. Actívalo en los ajustes del teléfono.")
                                    }
                                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> {
                                        viewModel.onVoiceError("El reconocimiento de voz está ocupado. Intenta nuevamente en unos segundos.")
                                    }
                                    SpeechRecognizer.ERROR_CLIENT -> {
                                        viewModel.cancelVoiceListening("Escucha cancelada.")
                                    }
                                    else -> {
                                        viewModel.onVoiceError("No pude entender la consulta. Intenta nuevamente.")
                                    }
                                }
                            }
                        }

                        override fun onResults(results: Bundle?) {
                            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            val text = matches?.firstOrNull()
                            mainHandler.post {
                                if (!text.isNullOrBlank()) {
                                    viewModel.onVoiceQueryReceived(text)
                                } else {
                                    viewModel.onVoiceError("No pude entender la consulta. Intenta nuevamente.")
                                }
                            }
                        }

                        override fun onPartialResults(partialResults: Bundle?) {}

                        override fun onEvent(eventType: Int, params: Bundle?) {}
                    })
                }
            }
        } catch (e: Exception) {
            speechRecognizer = null
        }
    }

    private fun handleVoiceButtonInteraction(
        micPermissionLauncher: ActivityResultLauncher<String>,
        speechIntentLauncher: ActivityResultLauncher<Intent>
    ) {
        val currentVoiceState = viewModel.voiceState.value
        val ttsState = viewModel.ttsPlaybackState.value

        when {
            currentVoiceState == VoiceConversationState.LISTENING -> {
                stopSpeechRecognition()
                viewModel.cancelVoiceListening("Escucha cancelada.")
            }
            ttsState == TtsPlaybackState.PLAYING -> {
                ttsManager.stop()
            }
            currentVoiceState == VoiceConversationState.PROCESSING -> {
                Toast.makeText(this, "Buscando la respuesta...", Toast.LENGTH_SHORT).show()
            }
            else -> {
                ttsManager.stop()
                val hasPermission = ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED

                if (hasPermission) {
                    startVoiceListeningFlow(speechIntentLauncher)
                } else {
                    micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
            }
        }
    }

    private fun startVoiceListeningFlow(speechIntentLauncher: ActivityResultLauncher<Intent>) {
        // Ya no se bloquea el micrófono cuando no hay internet: el reconocedor del teléfono puede tener el
        // español descargado y trabajar sin conexión. Si no puede, avisa por onError (VOICE_OFFLINE_HELP_MESSAGE).
        voiceRetryDone = false
        val speechIntent = buildSpeechIntent("es-SV")

        viewModel.startVoiceListening()

        // Try direct speech recognizer first if available
        var startedDirectly = false
        if (speechRecognizer != null) {
            try {
                speechRecognizer?.cancel()
                speechRecognizer?.startListening(speechIntent)
                startedDirectly = true
            } catch (e: Exception) {
                startedDirectly = false
            }
        }

        // If direct speech recognizer is unavailable or failed, launch intent dialog safely
        if (!startedDirectly) {
            try {
                speechIntentLauncher.launch(speechIntent)
            } catch (e: ActivityNotFoundException) {
                viewModel.onVoiceError("Reconocimiento de voz no disponible en este dispositivo.")
            } catch (e: Exception) {
                viewModel.onVoiceError("No fue posible activar el micrófono. Intenta nuevamente.")
            }
        }
    }

    /** Intent de dictado que prefiere el reconocimiento SIN conexión (si el teléfono tiene el idioma descargado). */
    private fun buildSpeechIntent(languageTag: String): Intent =
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageTag)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languageTag)
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, languageTag)
            putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "¿Cuál es su consulta electoral?")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

    /** Segundo y último intento con español latino. Devuelve true si logró iniciarlo. */
    private fun retryVoiceInLatinSpanish(): Boolean {
        voiceRetryDone = true
        val recognizer = speechRecognizer ?: return false
        return try {
            recognizer.cancel()
            recognizer.startListening(buildSpeechIntent("es-419"))
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun stopSpeechRecognition() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.cancel()
        } catch (_: Exception) {}
    }

    override fun onDestroy() {
        try {
            stopSpeechRecognition()
            speechRecognizer?.destroy()
            speechRecognizer = null
        } catch (_: Exception) {}
        try {
            ttsManager.shutdown()
        } catch (_: Exception) {}
        super.onDestroy()
    }
}

// Códigos de SpeechRecognizer disponibles desde Android 12 (se usan como números para que compile en versiones anteriores).
private const val ERROR_LANGUAGE_NOT_SUPPORTED = 12
private const val ERROR_LANGUAGE_UNAVAILABLE = 13

private const val VOICE_OFFLINE_HELP_MESSAGE =
    "No se pudo reconocer la voz sin internet. Descarga el español sin conexión (Ajustes › Voz sin conexión) o escribe tu pregunta."
