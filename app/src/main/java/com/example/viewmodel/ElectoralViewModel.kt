package com.example.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppFontSize
import com.example.data.AppSettingsManager
import com.example.data.AuthSessionManager
import com.example.data.TtsPlaybackState
import com.example.data.database.QueryHistory
import com.example.data.repository.AnswerResult
import com.example.data.repository.ApiKeyStatus
import com.example.data.repository.ElectoralRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ElectoralScreen {
    CHAT,
    BIBLIOTECA,
    SIMULADOR,
    PASO_A_PASO,
    CAPACITACION,
    CONFIGURACION
}

enum class VoiceConversationState {
    IDLE,          // Pulsa el micrófono para hablar
    LISTENING,     // Escuchando…
    PROCESSING,    // Procesando…
    SPEAKING,      // Respondiendo…
    ERROR          // Mensaje de error / cancelado
}

class ElectoralViewModel(private val repository: ElectoralRepository) : ViewModel() {

    private val _showIntroSplash = MutableStateFlow(true)
    val showIntroSplash: StateFlow<Boolean> = _showIntroSplash.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _currentScreen = MutableStateFlow(ElectoralScreen.CHAT)
    val currentScreen: StateFlow<ElectoralScreen> = _currentScreen.asStateFlow()

    private val _queryInput = MutableStateFlow("")
    val queryInput: StateFlow<String> = _queryInput.asStateFlow()

    // Specific Book Focus Mode
    private val _preferredBookId = MutableStateFlow<String?>(null)
    val preferredBookId: StateFlow<String?> = _preferredBookId.asStateFlow()

    private val _preferredBookTitle = MutableStateFlow<String?>(null)
    val preferredBookTitle: StateFlow<String?> = _preferredBookTitle.asStateFlow()

    // Direct PDF Navigation State from Response Cards
    private val _activePdfReaderDoc = MutableStateFlow<com.example.data.PdfDocument?>(null)
    val activePdfReaderDoc: StateFlow<com.example.data.PdfDocument?> = _activePdfReaderDoc.asStateFlow()

    private val _activePdfReaderHighlight = MutableStateFlow("")
    val activePdfReaderHighlight: StateFlow<String> = _activePdfReaderHighlight.asStateFlow()

    // «Buscar en libros»: libro elegido (id, título). Mientras está activo, las preguntas (escritas o dictadas) buscan en ese libro.
    private val _bookSearchBook = MutableStateFlow<Pair<String, String>?>(null)
    val bookSearchBook: StateFlow<Pair<String, String>?> = _bookSearchBook.asStateFlow()

    fun setBookSearchBook(id: String, title: String) { _bookSearchBook.value = id to title }
    fun clearBookSearchBook() { _bookSearchBook.value = null }

    private val _activePdfReaderPage = MutableStateFlow<Int>(0)
    val activePdfReaderPage: StateFlow<Int> = _activePdfReaderPage.asStateFlow()

    // Voice conversation state machine
    private val _voiceState = MutableStateFlow(VoiceConversationState.IDLE)
    val voiceState: StateFlow<VoiceConversationState> = _voiceState.asStateFlow()

    private val _voiceStatusMessage = MutableStateFlow("Pulsa el micrófono para hablar")
    val voiceStatusMessage: StateFlow<String> = _voiceStatusMessage.asStateFlow()

    private val _isVoiceTurn = MutableStateFlow(false)
    val isVoiceTurn: StateFlow<Boolean> = _isVoiceTurn.asStateFlow()

    // Enhanced TTS Playback state
    private val _ttsPlaybackState = MutableStateFlow(TtsPlaybackState.IDLE)
    val ttsPlaybackState: StateFlow<TtsPlaybackState> = _ttsPlaybackState.asStateFlow()

    private val _currentlySpeakingAnswer = MutableStateFlow<String?>(null)
    val currentlySpeakingAnswer: StateFlow<String?> = _currentlySpeakingAnswer.asStateFlow()

    private val _uiState = MutableStateFlow<ElectoralUiState>(ElectoralUiState.Idle)
    val uiState: StateFlow<ElectoralUiState> = _uiState.asStateFlow()

    private val _apiKeyStatus = MutableStateFlow(ApiKeyStatus.AVAILABLE)
    val apiKeyStatus: StateFlow<ApiKeyStatus> = _apiKeyStatus.asStateFlow()

    private val _appFontSize = MutableStateFlow(AppFontSize.MEDIANO)
    val appFontSize: StateFlow<AppFontSize> = _appFontSize.asStateFlow()

    val historyList: StateFlow<List<QueryHistory>> = repository.allHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        checkApiKeyStatus()
    }

    fun initSettings(context: Context) {
        _appFontSize.value = AppSettingsManager.getAppFontSize(context)
        _isDarkTheme.value = AppSettingsManager.isDarkTheme(context)
        _isLoggedIn.value = AuthSessionManager.isUserLoggedIn(context)
    }

    fun toggleDarkTheme(context: Context) {
        val newTheme = AppSettingsManager.toggleDarkTheme(context)
        _isDarkTheme.value = newTheme
    }

    fun setDarkTheme(context: Context, isDark: Boolean) {
        AppSettingsManager.setDarkTheme(context, isDark)
        _isDarkTheme.value = isDark
    }

    fun onLoginSuccess() {
        _isLoggedIn.value = true
    }

    fun logout(context: Context) {
        AuthSessionManager.logout(context)
        _isLoggedIn.value = false
    }

    fun initFontSize(context: Context) {
        _appFontSize.value = AppSettingsManager.getAppFontSize(context)
    }

    fun updateAppFontSize(context: Context, fontSize: AppFontSize) {
        _appFontSize.value = fontSize
        AppSettingsManager.setAppFontSize(context, fontSize)
    }

    fun setScreen(screen: ElectoralScreen) {
        _currentScreen.value = screen
    }

    fun dismissIntroSplash() {
        _showIntroSplash.value = false
    }

    fun onQueryInputChange(newInput: String) {
        _queryInput.value = newInput
    }

    fun setPreferredBook(docId: String?, docTitle: String?) {
        _preferredBookId.value = docId
        _preferredBookTitle.value = docTitle
    }

    fun startDocumentSpecificQuery(docId: String, docTitle: String) {
        _preferredBookId.value = docId
        _preferredBookTitle.value = docTitle
        _currentScreen.value = ElectoralScreen.CHAT
    }

    fun clearPreferredBook() {
        _preferredBookId.value = null
        _preferredBookTitle.value = null
    }

    fun openPdfDirectly(docId: String, page: Int = 1, highlight: String = "") {
        val foundDoc = com.example.data.ElectoralLibraryData.documents.firstOrNull { it.id == docId }
            ?: com.example.data.ElectoralLibraryData.documents.firstOrNull()
        if (foundDoc != null) {
            _activePdfReaderDoc.value = foundDoc
            _activePdfReaderPage.value = (page - 1).coerceAtLeast(0)
            _activePdfReaderHighlight.value = highlight
            _currentScreen.value = ElectoralScreen.BIBLIOTECA
        }
    }

    fun closeDirectPdfReader() {
        _activePdfReaderDoc.value = null
        _activePdfReaderPage.value = 0
        _activePdfReaderHighlight.value = ""
    }

    fun getSuggestionsForInput(input: String): List<String> {
        return repository.getSpellingAndSmartSuggestions(input)
    }

    private fun checkApiKeyStatus() {
        viewModelScope.launch {
            _apiKeyStatus.value = repository.getApiKeyStatus()
        }
    }

    // Voice Conversation Lifecycle Management
    fun startVoiceListening() {
        _isVoiceTurn.value = true
        _voiceState.value = VoiceConversationState.LISTENING
        _voiceStatusMessage.value = "Escuchando…"
    }

    fun cancelVoiceListening(message: String = "Escucha cancelada.") {
        _isVoiceTurn.value = false
        _voiceState.value = VoiceConversationState.IDLE
        _voiceStatusMessage.value = message
    }

    fun onVoiceQueryReceived(recognizedText: String) {
        if (recognizedText.isBlank()) {
            _voiceState.value = VoiceConversationState.ERROR
            _voiceStatusMessage.value = "No pude entender la consulta. Intenta nuevamente."
            _isVoiceTurn.value = false
            return
        }
        _isVoiceTurn.value = true
        _voiceState.value = VoiceConversationState.PROCESSING
        _voiceStatusMessage.value = "Procesando…"
        submitQuery(customQuery = recognizedText, isFromVoice = true)
    }

    fun onVoiceSpeakingStarted(text: String? = null) {
        _voiceState.value = VoiceConversationState.SPEAKING
        _voiceStatusMessage.value = "Respondiendo con voz completa…"
        _ttsPlaybackState.value = TtsPlaybackState.PLAYING
        if (text != null) {
            _currentlySpeakingAnswer.value = text
        }
    }

    fun onVoiceSpeakingPaused() {
        _ttsPlaybackState.value = TtsPlaybackState.PAUSED
        _voiceStatusMessage.value = "Audio en pausa"
    }

    fun onVoiceSpeakingResumed() {
        _ttsPlaybackState.value = TtsPlaybackState.PLAYING
        _voiceState.value = VoiceConversationState.SPEAKING
        _voiceStatusMessage.value = "Reproduciendo respuesta…"
    }

    fun onVoiceSpeakingFinished() {
        _voiceState.value = VoiceConversationState.IDLE
        _voiceStatusMessage.value = "Pulsa el micrófono para hablar"
        _ttsPlaybackState.value = TtsPlaybackState.IDLE
        _isVoiceTurn.value = false
        _currentlySpeakingAnswer.value = null
    }

    fun onVoiceError(errorMessage: String) {
        _voiceState.value = VoiceConversationState.ERROR
        _voiceStatusMessage.value = errorMessage
        _ttsPlaybackState.value = TtsPlaybackState.IDLE
        _isVoiceTurn.value = false
        _currentlySpeakingAnswer.value = null
    }

    fun updateTtsPlaybackState(state: TtsPlaybackState) {
        _ttsPlaybackState.value = state
        when (state) {
            TtsPlaybackState.PLAYING -> {
                _voiceState.value = VoiceConversationState.SPEAKING
                _voiceStatusMessage.value = "Reproduciendo respuesta…"
            }
            TtsPlaybackState.PAUSED -> {
                _voiceStatusMessage.value = "Audio en pausa"
            }
            TtsPlaybackState.STOPPED, TtsPlaybackState.IDLE -> {
                _voiceState.value = VoiceConversationState.IDLE
                _voiceStatusMessage.value = "Pulsa el micrófono para hablar"
                _currentlySpeakingAnswer.value = null
            }
        }
    }

    fun submitQuery(customQuery: String? = null, isFromVoice: Boolean = false) {
        val rawQuery = customQuery ?: _queryInput.value
        val queryToSend = rawQuery.trim().replace("\\s+".toRegex(), " ")
        if (queryToSend.isBlank()) return

        // Clear text field after taking input for manual typing
        if (customQuery == null) {
            _queryInput.value = ""
        }

        // Modo «Buscar en libros»: la pregunta se busca en el libro elegido (sin pasar por el asistente de situaciones).
        val bookMode = _bookSearchBook.value
        if (bookMode != null) {
            _currentScreen.value = ElectoralScreen.CHAT
            if (isFromVoice) {
                // El dictado ya terminó: no hay respuesta que leer en voz alta, se muestran las tarjetas.
                _voiceState.value = VoiceConversationState.IDLE
                _voiceStatusMessage.value = "Pulsa el micrófono para hablar"
                _isVoiceTurn.value = false
            }
            _uiState.value = ElectoralUiState.BookSearch(queryToSend, bookMode.first, bookMode.second)
            return
        }

        if (isFromVoice) {
            _isVoiceTurn.value = true
            _voiceState.value = VoiceConversationState.PROCESSING
            _voiceStatusMessage.value = "Procesando…"
        }

        // Always navigate back to Chat Screen on new query
        _currentScreen.value = ElectoralScreen.CHAT
        _uiState.value = ElectoralUiState.Loading(queryToSend)

        val conversationHistory = historyList.value.takeLast(4).map { it.question to it.answer }
        val preferredDoc = _preferredBookId.value

        viewModelScope.launch {
            when (val result = repository.askAssistant(
                question = queryToSend,
                preferredDocumentId = preferredDoc,
                conversationHistory = conversationHistory
            )) {
                is AnswerResult.Success -> {
                    _uiState.value = ElectoralUiState.Success(
                        question = queryToSend,
                        answer = result.text,
                        suggestions = result.suggestions,
                        primaryPdfDocId = result.primaryPdfDocId,
                        primaryPdfPage = result.primaryPdfPage
                    )
                }
                is AnswerResult.Error -> {
                    _uiState.value = ElectoralUiState.Error(
                        question = queryToSend,
                        message = result.errorMessage
                    )
                    if (isFromVoice || _isVoiceTurn.value) {
                        _voiceState.value = VoiceConversationState.ERROR
                        _voiceStatusMessage.value = "No fue posible obtener una respuesta. Intenta nuevamente."
                        _isVoiceTurn.value = false
                    }
                }
            }
            checkApiKeyStatus()
        }
    }

    fun selectHistoryItem(item: QueryHistory) {
        _currentScreen.value = ElectoralScreen.CHAT
        val suggestions = repository.getSpellingAndSmartSuggestions(item.question)
        _uiState.value = ElectoralUiState.Success(
            question = item.question,
            answer = item.answer,
            suggestions = suggestions
        )
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun resetState() {
        _uiState.value = ElectoralUiState.Idle
    }
}

sealed class ElectoralUiState {
    object Idle : ElectoralUiState()
    data class Loading(val question: String) : ElectoralUiState()
    data class Success(
        val question: String,
        val answer: String,
        val suggestions: List<String> = emptyList(),
        val primaryPdfDocId: String? = null,
        val primaryPdfPage: Int = 1
    ) : ElectoralUiState()
    data class Error(val question: String, val message: String) : ElectoralUiState()
    /** Resultado de «Buscar en libros»: se muestran artículos del libro elegido. */
    data class BookSearch(val question: String, val bookId: String, val bookTitle: String) : ElectoralUiState()
}

class ElectoralViewModelFactory(private val repository: ElectoralRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ElectoralViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ElectoralViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
