package com.example

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
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
import com.example.data.LegalConsentManager
import com.example.data.database.AppDatabase
import com.example.data.repository.ElectoralRepository
import com.example.ui.ElectoralMainScreen
import com.example.ui.LegalNoticeScreen
import com.example.ui.WelcomeIntroScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.ElectoralUiState
import com.example.viewmodel.ElectoralViewModel
import com.example.viewmodel.ElectoralViewModelFactory
import com.example.viewmodel.VoiceConversationState
import java.util.Locale

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    private var textToSpeech: TextToSpeech? = null
    private var isTtsInitialized by mutableStateOf(false)
    private var speechRecognizer: SpeechRecognizer? = null

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
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            textToSpeech = TextToSpeech(this, this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        initSpeechRecognizerSafely()

        setContent {
            MyApplicationTheme {
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
                    val voiceState by viewModel.voiceState.collectAsState()
                    val isVoiceTurn by viewModel.isVoiceTurn.collectAsState()
                    val showIntroSplash by viewModel.showIntroSplash.collectAsState()

                    // Reactive Voice Speaker: When AI responds to a voice question, automatically speak the answer!
                    LaunchedEffect(uiState) {
                        val state = uiState
                        if (state is ElectoralUiState.Success && isVoiceTurn) {
                            val spokenText = formatSpokenAnswer(state.answer)
                            speakAnswer(spokenText)
                        } else if (state is ElectoralUiState.Error && isVoiceTurn) {
                            viewModel.onVoiceError("No fue posible obtener una respuesta. Intenta nuevamente.")
                        }
                    }

                    if (showIntroSplash) {
                        WelcomeIntroScreen(
                            onDismiss = { viewModel.dismissIntroSplash() }
                        )
                    } else if (!hasAcceptedLegalConsent) {
                        LegalNoticeScreen(
                            onAcceptAndContinue = {
                                hasAcceptedLegalConsent = true
                            },
                            onReject = {
                                finish()
                            }
                        )
                    } else {
                        ElectoralMainScreen(
                            viewModel = viewModel,
                            onVoiceButtonTapped = {
                                handleVoiceButtonInteraction(micPermissionLauncher, speechIntentLauncher)
                            },
                            onSpeakText = { text ->
                                speakAnswer(formatSpokenAnswer(text))
                            },
                            onStopSpeaking = {
                                stopSpeaking()
                                viewModel.onVoiceSpeakingFinished()
                            }
                        )
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
                                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> {
                                        viewModel.onVoiceError("No hay conexión disponible para consultar la IA.")
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

        when (currentVoiceState) {
            VoiceConversationState.LISTENING -> {
                // User pressed again while listening -> Cancel listening
                stopSpeechRecognition()
                viewModel.cancelVoiceListening("Escucha cancelada.")
            }
            VoiceConversationState.SPEAKING -> {
                // User pressed while AI is speaking -> Stop audio and return to ready
                stopSpeaking()
                viewModel.onVoiceSpeakingFinished()
            }
            VoiceConversationState.PROCESSING -> {
                Toast.makeText(this, "Procesando respuesta con IA...", Toast.LENGTH_SHORT).show()
            }
            VoiceConversationState.IDLE,
            VoiceConversationState.ERROR -> {
                stopSpeaking()
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
        if (!isNetworkConnected()) {
            viewModel.onVoiceError("No hay conexión disponible para consultar la IA.")
            return
        }

        val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-SV")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "es-SV")
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "es-SV")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "¿Cuál es su consulta electoral?")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

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

    private fun stopSpeechRecognition() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.cancel()
        } catch (_: Exception) {}
    }

    private fun isNetworkConnected(): Boolean {
        return try {
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return true
            val activeNetwork = cm.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(activeNetwork) ?: return false
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } catch (e: Exception) {
            true
        }
    }

    override fun onInit(status: Int) {
        try {
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale("es", "SV"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    textToSpeech?.setLanguage(Locale("es"))
                }
                textToSpeech?.setSpeechRate(1.02f)
                textToSpeech?.setPitch(1.0f)
                isTtsInitialized = true
            }
        } catch (e: Exception) {
            isTtsInitialized = false
        }
    }

    private fun speakAnswer(spokenText: String) {
        if (isTtsInitialized && textToSpeech != null) {
            try {
                stopSpeaking()
                viewModel.onVoiceSpeakingStarted()

                textToSpeech?.speak(spokenText, TextToSpeech.QUEUE_FLUSH, null, "ElectoralVoiceSessionId")

                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        mainHandler.post {
                            viewModel.onVoiceSpeakingStarted()
                        }
                    }

                    override fun onDone(utteranceId: String?) {
                        mainHandler.post {
                            viewModel.onVoiceSpeakingFinished()
                        }
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        mainHandler.post {
                            viewModel.onVoiceSpeakingFinished()
                        }
                    }
                })
            } catch (e: Exception) {
                viewModel.onVoiceSpeakingFinished()
            }
        } else {
            viewModel.onVoiceSpeakingFinished()
        }
    }

    private fun stopSpeaking() {
        try {
            textToSpeech?.stop()
        } catch (_: Exception) {}
    }

    private fun formatSpokenAnswer(fullAnswer: String): String {
        var clean = fullAnswer

        if (clean.contains("ADVERTENCIA ORIENTATIVA", ignoreCase = true)) {
            clean = clean.substringBefore("ADVERTENCIA ORIENTATIVA")
        }
        if (clean.contains("Esta respuesta es únicamente orientativa", ignoreCase = true)) {
            clean = clean.substringBefore("Esta respuesta es únicamente orientativa")
        }

        clean = clean
            .replace(Regex("[#*_`\\[\\]()>\"]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()

        val sentences = clean.split(Regex("(?<=[.!?])\\s+"))
            .filter { it.isNotBlank() && it.length > 5 }

        val spokenCore = when {
            sentences.isEmpty() -> clean
            sentences.size <= 3 -> sentences.joinToString(" ")
            else -> sentences.take(3).joinToString(" ")
        }

        val finalSpoken = if (!spokenCore.contains("paso a paso", ignoreCase = true) && spokenCore.length < 240) {
            "$spokenCore Si deseas, puedo explicarte los detalles paso a paso."
        } else {
            spokenCore
        }

        return finalSpoken.trim()
    }

    override fun onDestroy() {
        try {
            stopSpeechRecognition()
            speechRecognizer?.destroy()
            speechRecognizer = null
        } catch (_: Exception) {}
        try {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
            textToSpeech = null
        } catch (_: Exception) {}
        super.onDestroy()
    }
}
