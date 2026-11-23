package com.example

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognitionSupport
import android.speech.RecognitionSupportCallback
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
    private var voiceAttempts: List<VoiceAttempt> = emptyList()
    private var voiceAttemptIndex = -1
    private var voiceRunId = 0
    private var voiceIntentLauncher: ActivityResultLauncher<Intent>? = null

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
        // El reconocedor ya no se crea al abrir la app: se crea en cada dictado (ver beginVoiceAttempts),
        // con el idioma que el teléfono tenga realmente disponible (sin conexión o en línea).
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

    // ------------------------------------------------------------------ Dictado por voz
    // Idea: probar, en orden, las formas de reconocer la voz que el teléfono pueda tener, hasta que una funcione:
    //  1) Reconocedor EN EL TELÉFONO (Android 13+) con el español que tenga INSTALADO (se consulta cuál es).
    //  2) Reconocedor normal del sistema con es-SV, es-419, es-US y el idioma del teléfono (prefiriendo sin conexión).
    //  3) Último recurso: el cuadro de dictado del propio teléfono (muestra su propia pantalla de escucha).
    private fun startVoiceListeningFlow(speechIntentLauncher: ActivityResultLauncher<Intent>) {
        voiceIntentLauncher = speechIntentLauncher
        voiceRunId++
        val run = voiceRunId
        viewModel.startVoiceListening()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && SpeechRecognizer.isOnDeviceRecognitionAvailable(this)) {
            probeInstalledSpanish(run)
        } else {
            beginVoiceAttempts(run, emptyList())
        }
    }

    /** Pregunta al teléfono qué idiomas tiene instalados para reconocer voz SIN conexión (Android 13+). */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun probeInstalledSpanish(run: Int) {
        try {
            val probe = SpeechRecognizer.createOnDeviceSpeechRecognizer(this)
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            probe.checkRecognitionSupport(intent, mainExecutor, object : RecognitionSupportCallback {
                override fun onSupportResult(recognitionSupport: RecognitionSupport) {
                    val spanish = recognitionSupport.installedOnDeviceLanguages.filter { it.startsWith("es", ignoreCase = true) }
                    probe.destroy()
                    if (run == voiceRunId) beginVoiceAttempts(run, spanish)
                }

                override fun onError(error: Int) {
                    probe.destroy()
                    if (run == voiceRunId) beginVoiceAttempts(run, emptyList())
                }
            })
        } catch (e: Exception) {
            beginVoiceAttempts(run, emptyList())
        }
    }

    private fun beginVoiceAttempts(run: Int, installedSpanish: List<String>) {
        if (run != voiceRunId) return
        val preferencia = listOf("es-SV", "es-419", "es-US", "es-MX", "es-ES", "es")
        val attempts = ArrayList<VoiceAttempt>()
        installedSpanish
            .sortedBy { tag -> preferencia.indexOfFirst { it.equals(tag, ignoreCase = true) }.let { if (it < 0) 99 else it } }
            .forEach { attempts.add(VoiceAttempt(onDevice = true, language = it)) }
        listOf("es-SV", "es-419", "es-US", "").forEach { attempts.add(VoiceAttempt(onDevice = false, language = it)) }
        voiceAttempts = attempts
        voiceAttemptIndex = -1
        nextVoiceAttempt(run, lastError = -1)
    }

    private fun nextVoiceAttempt(run: Int, lastError: Int) {
        if (run != voiceRunId) return
        voiceAttemptIndex++
        if (voiceAttemptIndex >= voiceAttempts.size) {
            finishVoiceWithSystemDialog(lastError)
            return
        }
        val attempt = voiceAttempts[voiceAttemptIndex]
        try {
            speechRecognizer?.destroy()
            speechRecognizer = null
            val recognizer: SpeechRecognizer? = when {
                attempt.onDevice && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                    SpeechRecognizer.createOnDeviceSpeechRecognizer(this)
                SpeechRecognizer.isRecognitionAvailable(this) -> SpeechRecognizer.createSpeechRecognizer(this)
                else -> null
            }
            if (recognizer == null) {
                nextVoiceAttempt(run, ERROR_RECOGNIZER_UNAVAILABLE)
                return
            }
            recognizer.setRecognitionListener(buildVoiceListener(run))
            speechRecognizer = recognizer
            recognizer.startListening(buildSpeechIntent(attempt.language))
        } catch (e: Exception) {
            nextVoiceAttempt(run, ERROR_RECOGNIZER_UNAVAILABLE)
        }
    }

    private fun buildVoiceListener(run: Int) = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            mainHandler.post { if (run == voiceRunId) viewModel.startVoiceListening() }
        }

        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}
        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}

        override fun onError(error: Int) {
            mainHandler.post {
                if (run != voiceRunId) return@post
                when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH,
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT ->
                        viewModel.onVoiceError("No pude entender la consulta. Intenta nuevamente.")
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->
                        viewModel.onVoiceError("Falta el permiso del micrófono. Actívalo en los ajustes del teléfono.")
                    else ->
                        // Red, servidor, idioma no disponible, cliente, audio, ocupado...: se prueba la siguiente forma.
                        nextVoiceAttempt(run, error)
                }
            }
        }

        override fun onResults(results: Bundle?) {
            val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
            mainHandler.post {
                if (run != voiceRunId) return@post
                if (!text.isNullOrBlank()) viewModel.onVoiceQueryReceived(text)
                else viewModel.onVoiceError("No pude entender la consulta. Intenta nuevamente.")
            }
        }
    }

    /** Intent de dictado. Con [languageTag] vacío se usa el idioma del teléfono. Siempre prefiere reconocer SIN conexión. */
    private fun buildSpeechIntent(languageTag: String): Intent =
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            if (languageTag.isNotEmpty()) putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageTag)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
            putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "¿Cuál es su consulta electoral?")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

    /** Último recurso: el cuadro de dictado del propio teléfono. Si tampoco existe, se avisa con el código del error. */
    private fun finishVoiceWithSystemDialog(lastError: Int) {
        val launcher = voiceIntentLauncher
        if (launcher != null) {
            try {
                launcher.launch(buildSpeechIntent(""))
                return
            } catch (e: ActivityNotFoundException) {
                // sigue abajo
            } catch (e: Exception) {
                // sigue abajo
            }
        }
        viewModel.onVoiceError(voiceHelpMessage(lastError))
    }

    private fun stopSpeechRecognition() {
        voiceRunId++ // ignora lo que llegue de intentos anteriores
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

/** Una forma de reconocer voz: en el teléfono (Android 13+) o con el reconocedor normal, y el idioma ("" = el del teléfono). */
private data class VoiceAttempt(val onDevice: Boolean, val language: String)

// Códigos propios (los de SpeechRecognizer van de 1 a 13).
private const val ERROR_RECOGNIZER_UNAVAILABLE = 100

private fun voiceHelpMessage(code: Int): String =
    "No se pudo reconocer la voz (código $code). Descarga el español sin conexión (Ajustes › Voz sin conexión) o escribe tu pregunta."
