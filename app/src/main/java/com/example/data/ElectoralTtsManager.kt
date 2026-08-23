package com.example.data

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

enum class TtsPlaybackState {
    IDLE,
    PLAYING,
    PAUSED,
    STOPPED
}

class ElectoralTtsManager(
    private val context: Context,
    private val onStateChanged: (TtsPlaybackState) -> Unit = {}
) : TextToSpeech.OnInitListener {

    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false
    private val mainHandler = Handler(Looper.getMainLooper())

    private val _playbackState = MutableStateFlow(TtsPlaybackState.IDLE)
    val playbackState: StateFlow<TtsPlaybackState> = _playbackState.asStateFlow()

    private var currentFullText: String = ""
    private var textChunks: List<String> = emptyList()
    private var currentChunkIndex: Int = 0
    private var sessionToken: Long = 0

    init {
        try {
            textToSpeech = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            try {
                val localeSv = Locale("es", "SV")
                val langResult = textToSpeech?.setLanguage(localeSv)
                if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    textToSpeech?.setLanguage(Locale("es", "ES"))
                }
                
                // Natural didactic speed and clear pitch for electoral training
                textToSpeech?.setSpeechRate(0.98f)
                textToSpeech?.setPitch(1.0f)
                
                textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        mainHandler.post {
                            updateState(TtsPlaybackState.PLAYING)
                        }
                    }

                    override fun onDone(utteranceId: String?) {
                        mainHandler.post {
                            handleUtteranceDone(utteranceId)
                        }
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        mainHandler.post {
                            handleUtteranceDone(utteranceId)
                        }
                    }

                    override fun onError(utteranceId: String?, errorCode: Int) {
                        mainHandler.post {
                            handleUtteranceDone(utteranceId)
                        }
                    }
                })

                isInitialized = true
            } catch (e: Exception) {
                isInitialized = false
            }
        }
    }

    /**
     * Cleans markdown formatting, symbols, and structures to produce natural, fluent spoken text.
     * Preserves all articles, laws, step-by-step procedures, disclaimers, and details in full.
     */
    fun cleanTextForNaturalSpeech(rawText: String): String {
        var clean = rawText

        // Clean headers (e.g. ### Título -> Título.)
        clean = clean.replace(Regex("(?m)^#{1,6}\\s*(.*?)$")) { matchResult ->
            val headerText = matchResult.groupValues[1].trim()
            "$headerText."
        }

        // Clean list items (e.g. * Paso 1: -> Paso 1:)
        clean = clean.replace(Regex("(?m)^[\\*\\-\\+]\\s+"), "")
        clean = clean.replace(Regex("(?m)^\\d+\\.\\s+")) { matchResult ->
            matchResult.value
        }

        // Clean markdown emphasis and brackets
        clean = clean
            .replace("**", "")
            .replace("__", "")
            .replace("*", "")
            .replace("`", "")
            .replace("~", "")
            .replace("[", "")
            .replace("]", "")
            .replace(Regex(">+\\s*"), "")

        // Normalize spaces and line breaks with natural punctuation pauses
        clean = clean.replace(Regex("\n{2,}"), ". ")
        clean = clean.replace("\n", ", ")
        clean = clean.replace(Regex("\\s+"), " ")
        clean = clean.trim()

        return clean
    }

    /**
     * Splits long text into natural chunks (sentences/paragraphs under 1200 characters)
     * so that Android TTS does not drop or truncate long answers.
     */
    private fun splitIntoSpeechChunks(text: String): List<String> {
        val maxChunkLength = 1200
        if (text.length <= maxChunkLength) {
            return listOf(text)
        }

        val chunks = mutableListOf<String>()
        val sentences = text.split(Regex("(?<=[.!?])\\s+"))
        var currentChunk = StringBuilder()

        for (sentence in sentences) {
            if (currentChunk.length + sentence.length + 1 > maxChunkLength) {
                if (currentChunk.isNotEmpty()) {
                    chunks.add(currentChunk.toString().trim())
                    currentChunk = StringBuilder()
                }
                if (sentence.length > maxChunkLength) {
                    // Split long sentence by commas or words
                    val parts = sentence.split(Regex("(?<=[,;])\\s+"))
                    for (part in parts) {
                        if (currentChunk.length + part.length + 1 > maxChunkLength) {
                            if (currentChunk.isNotEmpty()) chunks.add(currentChunk.toString().trim())
                            currentChunk = StringBuilder(part)
                        } else {
                            if (currentChunk.isNotEmpty()) currentChunk.append(" ")
                            currentChunk.append(part)
                        }
                    }
                } else {
                    currentChunk.append(sentence)
                }
            } else {
                if (currentChunk.isNotEmpty()) currentChunk.append(" ")
                currentChunk.append(sentence)
            }
        }

        if (currentChunk.isNotEmpty()) {
            chunks.add(currentChunk.toString().trim())
        }

        return if (chunks.isEmpty()) listOf(text) else chunks
    }

    /**
     * Speaks the complete answer from start to end without any truncation.
     */
    fun speakFullAnswer(answerText: String) {
        if (!isInitialized || textToSpeech == null) return

        stop()
        currentFullText = answerText
        val cleaned = cleanTextForNaturalSpeech(answerText)
        textChunks = splitIntoSpeechChunks(cleaned)
        currentChunkIndex = 0
        sessionToken = System.currentTimeMillis()

        playFromCurrentChunk()
    }

    private fun playFromCurrentChunk() {
        if (textChunks.isEmpty() || currentChunkIndex >= textChunks.size) {
            updateState(TtsPlaybackState.IDLE)
            return
        }

        val tts = textToSpeech ?: return
        updateState(TtsPlaybackState.PLAYING)

        // Queue all remaining chunks from current index
        for (i in currentChunkIndex until textChunks.size) {
            val chunk = textChunks[i]
            val utteranceId = "utterance_${sessionToken}_chunk_${i}_of_${textChunks.size}"
            val queueMode = if (i == currentChunkIndex) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD

            val params = Bundle().apply {
                putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId)
            }
            tts.speak(chunk, queueMode, params, utteranceId)
        }
    }

    private fun handleUtteranceDone(utteranceId: String?) {
        if (utteranceId != null && utteranceId.startsWith("utterance_${sessionToken}_chunk_")) {
            val parts = utteranceId.removePrefix("utterance_${sessionToken}_chunk_").split("_of_")
            val chunkIdx = parts.firstOrNull()?.toIntOrNull() ?: 0
            val totalChunks = parts.getOrNull(1)?.toIntOrNull() ?: 1

            currentChunkIndex = chunkIdx + 1
            if (currentChunkIndex >= totalChunks) {
                updateState(TtsPlaybackState.IDLE)
            }
        }
    }

    fun pause() {
        if (_playbackState.value == TtsPlaybackState.PLAYING) {
            textToSpeech?.stop()
            updateState(TtsPlaybackState.PAUSED)
        }
    }

    fun resume() {
        if (_playbackState.value == TtsPlaybackState.PAUSED) {
            playFromCurrentChunk()
        }
    }

    fun stop() {
        try {
            textToSpeech?.stop()
        } catch (_: Exception) {}
        currentChunkIndex = 0
        updateState(TtsPlaybackState.STOPPED)
    }

    fun restart() {
        if (currentFullText.isNotBlank()) {
            speakFullAnswer(currentFullText)
        }
    }

    private fun updateState(newState: TtsPlaybackState) {
        _playbackState.value = newState
        onStateChanged(newState)
    }

    fun shutdown() {
        try {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
            textToSpeech = null
        } catch (_: Exception) {}
    }
}
