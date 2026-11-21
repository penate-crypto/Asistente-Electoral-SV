package com.example.data.repository

import com.example.BuildConfig
import com.example.data.ElectoralLibraryData
import com.example.data.api.*
import com.example.data.database.QueryHistory
import com.example.data.database.QueryHistoryDao
import com.example.data.rag.*
import com.example.data.search.codigo.CodeMatchKind
import com.example.data.search.codigo.CodeSearchResult
import com.example.data.search.codigo.ElectoralCodeSearch
import com.example.data.search.engine.ElectoralSearchEngine
import com.example.data.search.model.MatchConfidenceLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ElectoralRepository(
    private val queryHistoryDao: QueryHistoryDao
) {
    val allHistory: Flow<List<QueryHistory>> = queryHistoryDao.getAllHistoryFlow()

    suspend fun getApiKeyStatus(): ApiKeyStatus {
        return ApiKeyStatus.AVAILABLE
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
     * Master Search Query Execution:
     * 1. Evaluates query against local pre-validated knowledge base using multi-element matching.
     * 2. If high/medium match is found, immediately delivers structured official response without hallucinations.
     * 3. Fully offline-first and deterministic.
     */
    suspend fun askAssistant(
        question: String,
        preferredDocumentId: String? = null,
        conversationHistory: List<Pair<String, String>> = emptyList()
    ): AnswerResult = withContext(Dispatchers.IO) {
        val trimmedQuestion = question.trim()

        // Paso 1: Si la consulta menciona "código electoral", buscar primero en el Banco Código Electoral (100% local)
        val codigo = ElectoralCodeSearch.search(trimmedQuestion)
        if (codigo != null) {
            val header = if (codigo.kind == CodeMatchKind.ARTICLE_NUMBER) {
                "Artículo(s) solicitado(s) del Código Electoral:"
            } else {
                "Artículos del Código Electoral sobre: ${codigo.topic}"
            }
            val readableText = buildString {
                appendLine(header)
                appendLine()
                codigo.articles.forEach { art ->
                    appendLine("Código Electoral · Art. ${art.numero}${if (art.derogado) " (Derogado)" else ""}")
                    if (art.titulo.isNotBlank()) appendLine(art.titulo)
                    if (art.capitulo.isNotBlank()) appendLine(art.capitulo)
                    appendLine(art.textoLegible)
                    appendLine()
                }
                append("Texto tomado del Código Electoral (Decreto 413). Verifícalo con el PDF de la biblioteca.")
            }.trim()

            val firstArt = codigo.articles.firstOrNull()
            val primaryDocId = firstArt?.pdfDocId?.ifBlank { "codigo_electoral_decreto_413" } ?: "codigo_electoral_decreto_413"
            val primaryPage = firstArt?.paginaPdf ?: 1

            queryHistoryDao.insertHistory(
                QueryHistory(
                    question = trimmedQuestion,
                    answer = readableText
                )
            )
            return@withContext AnswerResult.Success(
                text = readableText,
                suggestions = emptyList(),
                primaryPdfDocId = primaryDocId,
                primaryPdfPage = primaryPage,
                codeSearchResult = codigo
            )
        }

        val smartSuggestions = getSpellingAndSmartSuggestions(trimmedQuestion)

        // Búsqueda inteligente en el Banco JSON principal (154 situaciones)
        val searchResult = ElectoralSearchEngine.search(trimmedQuestion)

        val suggestions = if (searchResult.clarificationOptions.isNotEmpty()) {
            val clarificationTitles = searchResult.clarificationOptions.map { it.titulo }
            (clarificationTitles + smartSuggestions).distinct().take(3)
        } else {
            smartSuggestions
        }

        val finalAnswer = searchResult.formattedResponse
        val firstDocSource = searchResult.topSituation?.respuesta?.fuentesDocumentales?.firstOrNull()
        val primaryDocId = firstDocSource?.pdfDocId ?: searchResult.topSituation?.respuesta?.primaryPdfDocId
        val primaryPage = firstDocSource?.pagina ?: searchResult.topSituation?.respuesta?.primaryPdfPage ?: 1

        queryHistoryDao.insertHistory(
            QueryHistory(
                question = trimmedQuestion,
                answer = finalAnswer
            )
        )
        return@withContext AnswerResult.Success(
            text = finalAnswer,
            suggestions = suggestions,
            primaryPdfDocId = primaryDocId,
            primaryPdfPage = primaryPage
        )
    }
}

sealed class AnswerResult {
    data class Success(
        val text: String,
        val suggestions: List<String> = emptyList(),
        val primaryPdfDocId: String? = null,
        val primaryPdfPage: Int = 1,
        val codeSearchResult: CodeSearchResult? = null
    ) : AnswerResult()
    data class Error(val errorMessage: String) : AnswerResult()
}

enum class ApiKeyStatus {
    AVAILABLE, MISSING
}
