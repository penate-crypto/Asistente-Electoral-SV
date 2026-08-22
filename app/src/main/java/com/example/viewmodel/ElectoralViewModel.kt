package com.example.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppFontSize
import com.example.data.AppSettingsManager
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

    private val _currentScreen = MutableStateFlow(ElectoralScreen.CHAT)
    val currentScreen: StateFlow<ElectoralScreen> = _currentScreen.asStateFlow()

    private val _queryInput = MutableStateFlow("")
    val queryInput: StateFlow<String> = _queryInput.asStateFlow()

    // Specific Book Focus Mode
    private val _preferredBookId = MutableStateFlow<String?>(null)
    val preferredBookId: StateFlow<String?> = _preferredBookId.asStateFlow()

    private val _preferredBookTitle = MutableStateFlow<String?>(null)
    val preferredBookTitle: StateFlow<String?> = _preferredBookTitle.asStateFlow()

    // Voice conversation state machine
    private val _voiceState = MutableStateFlow(VoiceConversationState.IDLE)
    val voiceState: StateFlow<VoiceConversationState> = _voiceState.asStateFlow()

    private val _voiceStatusMessage = MutableStateFlow("Pulsa el micrófono para hablar")
    val voiceStatusMessage: StateFlow<String> = _voiceStatusMessage.asStateFlow()

    private val _isVoiceTurn = MutableStateFlow(false)
    val isVoiceTurn: StateFlow<Boolean> = _isVoiceTurn.asStateFlow()

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

    fun onVoiceSpeakingStarted() {
        _voiceState.value = VoiceConversationState.SPEAKING
        _voiceStatusMessage.value = "Respondiendo…"
    }

    fun onVoiceSpeakingFinished() {
        _voiceState.value = VoiceConversationState.IDLE
        _voiceStatusMessage.value = "Pulsa el micrófono para hablar"
        _isVoiceTurn.value = false
    }

    fun onVoiceError(errorMessage: String) {
        _voiceState.value = VoiceConversationState.ERROR
        _voiceStatusMessage.value = errorMessage
        _isVoiceTurn.value = false
    }

    fun submitQuery(customQuery: String? = null, isFromVoice: Boolean = false) {
        val queryToSend = customQuery ?: _queryInput.value
        if (queryToSend.isBlank()) return

        // Clear text field after taking input for manual typing
        if (customQuery == null) {
            _queryInput.value = ""
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
                        suggestions = result.suggestions
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
    data class Success(val question: String, val answer: String, val suggestions: List<String> = emptyList()) : ElectoralUiState()
    data class Error(val question: String, val message: String) : ElectoralUiState()
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
