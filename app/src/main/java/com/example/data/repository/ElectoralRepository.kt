package com.example.data.repository

import com.example.BuildConfig
import com.example.data.ElectoralLibraryData
import com.example.data.api.*
import com.example.data.database.QueryHistory
import com.example.data.database.QueryHistoryDao
import com.example.data.rag.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ElectoralRepository(
    private val queryHistoryDao: QueryHistoryDao,
    private val apiService: GeminiApiService = RetrofitClient.service
) {
    val allHistory: Flow<List<QueryHistory>> = queryHistoryDao.getAllHistoryFlow()

    suspend fun getApiKeyStatus(): ApiKeyStatus {
        val key = BuildConfig.GEMINI_API_KEY
        return if (key.isBlank() || key == "MY_GEMINI_API_KEY") {
            ApiKeyStatus.MISSING
        } else {
            ApiKeyStatus.AVAILABLE
        }
    }

    suspend fun clearHistory() {
        withContext(Dispatchers.IO) {
            queryHistoryDao.clearAllHistory()
        }
    }

    /**
     * Common electoral vocabulary and questions to detect typos and generate up to 3 smart suggestions
     */
    private val electoralKnowledgeBank = listOf(
        "¿Cuáles son las funciones del Presidente de JRV?",
        "¿Cuáles son las funciones del Secretario de JRV?",
        "¿Cuáles son las funciones del Primer Vocal de JRV?",
        "¿Cuáles son las funciones del Segundo y Tercer Vocal?",
        "¿Con cuántos miembros se instala la JRV y qué pasa si faltan miembros?",
        "¿A qué hora se instala la mesa electoral y a qué hora abre la votación?",
        "¿Qué permisos de trabajo con goce de sueldo garantiza el Art. 113 del Código Electoral?",
        "¿Cuál es la diferencia entre voto válido, voto nulo y voto impugnado?",
        "¿Cómo se realiza el escrutinio preliminar paso a paso a partir de las 5:00 PM?",
        "¿Qué derechos y prohibiciones tienen los vigilantes de partidos políticos?",
        "¿Cuáles son las atribuciones del Tribunal Supremo Electoral (TSE)?",
        "¿Quiénes están autorizados a portar armas y cómo aplica el Art. 290 del Código Electoral?",
        "¿Cómo protege la ley LEIV (Art. 55) a las mujeres en el proceso electoral?",
        "¿Qué sanciones contempla la Ley Seca (Art. 284) y los delitos electorales?",
        "¿Qué hacer si un ciudadano no presenta su DUI o no aparece en el padrón?",
        "¿Cómo se garantiza el voto asistido para personas con discapacidad o adultos mayores?",
        "¿Qué funciones cumplen los Jefes de Centro y Supervisores del TSE?",
        "¿Qué papel cumple la Policía Nacional Civil (PNC) y la Fiscalía Electoral?",
        "¿Cómo se califica el voto por coalición en el escrutinio preliminar?",
        "¿Qué son y cómo funcionan las JED y JEM en cada departamento y municipio?"
    )

    /**
     * Generate up to 3 "¿Quisiste decir...?" suggestions for spelling mistakes or related electoral inquiries
     */
    fun getSpellingAndSmartSuggestions(rawQuery: String): List<String> {
        val q = rawQuery.lowercase().trim()
        if (q.isBlank()) return emptyList()

        val keywords = q.split(" ", "?", "¿", ",", ".", "-", "_").filter { it.length >= 3 }
        val matchedSuggestions = mutableListOf<String>()

        // 1. Direct keyword match
        for (sentence in electoralKnowledgeBank) {
            val sLower = sentence.lowercase()
            var matchScore = 0
            for (kw in keywords) {
                if (sLower.contains(kw)) {
                    matchScore += 2
                } else if (isFuzzyMatch(kw, sLower)) {
                    matchScore += 1
                }
            }
            if (matchScore > 0) {
                matchedSuggestions.add(sentence)
            }
            if (matchedSuggestions.size >= 3) break
        }

        // 2. Specific typo and intent corrections
        if (matchedSuggestions.isEmpty()) {
            if (q.contains("presi") || q.contains("precidente") || q.contains("presidete") || q.contains("jefe de mesa")) {
                matchedSuggestions.add("¿Cuáles son las funciones del Presidente de JRV?")
            }
            if (q.contains("secre") || q.contains("cecretario") || q.contains("secretaro") || q.contains("actas")) {
                matchedSuggestions.add("¿Cuáles son las funciones del Secretario de JRV?")
            }
            if (q.contains("vocal") || q.contains("bocal") || q.contains("vocales") || q.contains("tinta")) {
                matchedSuggestions.add("¿Cuáles son las funciones del Primer Vocal de JRV?")
                matchedSuggestions.add("¿Cuáles son las funciones del Segundo y Tercer Vocal?")
            }
            if (q.contains("tse") || q.contains("tribunal") || q.contains("magistrado") || q.contains("jem") || q.contains("jed")) {
                matchedSuggestions.add("¿Cuáles son las atribuciones del Tribunal Supremo Electoral (TSE)?")
            }
            if (q.contains("arma") || q.contains("pistola") || q.contains("cuchillo") || q.contains("seguridad") || q.contains("pnc")) {
                matchedSuggestions.add("¿Quiénes están autorizados a portar armas y cómo aplica el Art. 290 del Código Electoral?")
            }
            if (q.contains("alcohol") || q.contains("cerveza") || q.contains("borracho") || q.contains("ley seca")) {
                matchedSuggestions.add("¿Qué sanciones contempla la Ley Seca (Art. 284) y los delitos electorales?")
            }
            if (q.contains("trabajo") || q.contains("jefe") || q.contains("patron") || q.contains("patrono") || q.contains("sueldo") || q.contains("113")) {
                matchedSuggestions.add("¿Qué permisos de trabajo con goce de sueldo garantiza el Art. 113 del Código Electoral?")
            }
            if (q.contains("conteo") || q.contains("escrutinio") || q.contains("papeletas") || q.contains("acta") || q.contains("5 pm")) {
                matchedSuggestions.add("¿Cómo se realiza el escrutinio preliminar paso a paso a partir de las 5:00 PM?")
            }
        }

        return matchedSuggestions.distinct().take(3)
    }

    private fun isFuzzyMatch(keyword: String, target: String): Boolean {
        if (keyword.length < 4) return false
        val prefix = keyword.substring(0, (keyword.length * 0.75).toInt())
        return target.contains(prefix)
    }

    /**
     * RAG-Powered Query Execution Engine
     */
    suspend fun askAssistant(
        question: String,
        preferredDocumentId: String? = null,
        conversationHistory: List<Pair<String, String>> = emptyList()
    ): AnswerResult = withContext(Dispatchers.IO) {
        val trimmedQuestion = question.trim()
        val suggestions = getSpellingAndSmartSuggestions(trimmedQuestion)

        // 1. Query Analysis (Intention, Target Articles, Entity extraction, Decomposition)
        val analyzedQuery = ElectoralQueryAnalyzer.analyze(
            query = trimmedQuestion,
            conversationContext = conversationHistory.lastOrNull()?.first
        )

        // 2. Semantic & Lexical Hybrid Document Retrieval from Electoral Library
        val retrievedChunks = ElectoralHybridRetriever.retrieve(
            analyzedQuery = analyzedQuery,
            preferredDocumentId = preferredDocumentId,
            topK = 5
        )

        val apiKey = BuildConfig.GEMINI_API_KEY
        val hasValidKey = apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY"

        if (hasValidKey) {
            val (_, request) = ElectoralRAGEngine.buildRAGPrompt(
                userQuery = trimmedQuestion,
                retrievedChunks = retrievedChunks,
                analyzedQuery = analyzedQuery,
                conversationHistory = conversationHistory
            )

            try {
                val response = apiService.generateContent(apiKey, request)
                val rawTextRes = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!rawTextRes.isNullOrBlank()) {
                    val validatedAnswer = ElectoralResponseValidator.validateAndFormat(
                        userQuery = trimmedQuestion,
                        analyzedQuery = analyzedQuery,
                        rawAnswer = rawTextRes,
                        retrievedChunks = retrievedChunks
                    )
                    val historyItem = QueryHistory(
                        question = trimmedQuestion,
                        answer = validatedAnswer
                    )
                    queryHistoryDao.insertHistory(historyItem)
                    return@withContext AnswerResult.Success(validatedAnswer, suggestions)
                }
            } catch (e: Exception) {
                // Fallback to grounded local knowledge engine if network fails or timeout occurs
                val rawFallback = ElectoralRAGEngine.buildLocalGroundedAnswer(analyzedQuery, retrievedChunks)
                val validatedFallback = ElectoralResponseValidator.validateAndFormat(
                    userQuery = trimmedQuestion,
                    analyzedQuery = analyzedQuery,
                    rawAnswer = rawFallback,
                    retrievedChunks = retrievedChunks
                )
                val historyItem = QueryHistory(
                    question = trimmedQuestion,
                    answer = validatedFallback
                )
                queryHistoryDao.insertHistory(historyItem)
                return@withContext AnswerResult.Success(validatedFallback, suggestions)
            }
        }

        // 3. Grounded Local RAG Generation (when offline or without API key)
        val rawLocalAnswer = ElectoralRAGEngine.buildLocalGroundedAnswer(analyzedQuery, retrievedChunks)
        val validatedLocalAnswer = ElectoralResponseValidator.validateAndFormat(
            userQuery = trimmedQuestion,
            analyzedQuery = analyzedQuery,
            rawAnswer = rawLocalAnswer,
            retrievedChunks = retrievedChunks
        )
        val historyItem = QueryHistory(
            question = trimmedQuestion,
            answer = validatedLocalAnswer
        )
        queryHistoryDao.insertHistory(historyItem)
        return@withContext AnswerResult.Success(validatedLocalAnswer, suggestions)
    }
}

sealed class AnswerResult {
    data class Success(val text: String, val suggestions: List<String> = emptyList()) : AnswerResult()
    data class Error(val errorMessage: String) : AnswerResult()
}

enum class ApiKeyStatus {
    AVAILABLE, MISSING
}
