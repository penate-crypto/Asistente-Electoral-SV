package com.example.data.search.engine

import com.example.data.ElectoralLibraryData
import com.example.data.search.entities.ElectoralEntityExtractor
import com.example.data.search.knowledge.ElectoralKnowledgeBase
import com.example.data.search.model.*
import com.example.data.search.normalization.ElectoralNormalizer

object ElectoralSearchEngine {

    private val OFF_TOPIC_KEYWORDS = listOf(
        "receta", "cocina", "pastel", "futbol", "messi", "ronaldo", "pelicula",
        "cancion", "musica", "chiste", "poema", "cuento", "bitcoin", "cripto",
        "videojuego", "clima manana", "horoscopo", "astrologia"
    )

    /**
     * Master Search Function:
     * Receives raw query (from text or speech-to-text) and returns a validated,
     * structured electoral result backed entirely by the local knowledge base.
     */
    fun search(rawQuery: String): ElectoralSearchResult {
        val trimmed = rawQuery.trim()
        if (trimmed.isBlank()) {
            return ElectoralSearchResult(
                matchLevel = MatchConfidenceLevel.LOW,
                topSituation = null,
                score = 0.0,
                formattedResponse = "Por favor, ingrese una pregunta o describa una situación electoral para realizar la consulta."
            )
        }

        // 0. Off-topic check
        val lowerRaw = trimmed.lowercase()
        if (OFF_TOPIC_KEYWORDS.any { lowerRaw.contains(it) } && !isElectoralQuery(lowerRaw)) {
            val offTopicMsg = """
                ℹ️ **Ámbito de Consulta:**
                Asistente Electoral SV está diseñado exclusivamente para consultas electorales, procedimientos de votación, normativa electoral, funciones de organismos electorales (JRV, JED, JEM, TSE, Fiscalía Electoral, PNC) y temas jurídicos electorales de la República de El Salvador.
                
                Por favor formule una pregunta relacionada con el proceso electoral.
            """.trimIndent()
            return ElectoralSearchResult(
                matchLevel = MatchConfidenceLevel.LOW,
                topSituation = null,
                score = 0.0,
                formattedResponse = offTopicMsg,
                isOffTopic = true,
                offTopicMessage = offTopicMsg
            )
        }

        // 1. Normalization
        val normalizedQuery = ElectoralNormalizer.normalize(trimmed)

        // 2. Entity, Context & Intent Extraction
        val entities = ElectoralEntityExtractor.extract(
            normalizedText = normalizedQuery.cleanedText,
            rawQuery = trimmed
        )

        // 3. Score against all active (non-obsolete) situations in the Knowledge Base
        val candidateSituations = ElectoralKnowledgeBase.getAll().filter { it.estado != RecordStatus.OBSOLETO }
        val scoredList = candidateSituations.map { situation ->
            ElectoralScoringEngine.scoreSituation(
                situation = situation,
                normalizedQuery = normalizedQuery,
                entities = entities
            )
        }.sortedWith(
            compareByDescending<ScoredSituation> { it.totalScore }
                .thenBy { it.situation.prioridadJerarquica }
        )

        val topCandidate = scoredList.firstOrNull()
        val secondCandidate = scoredList.getOrNull(1)
        val topScore = topCandidate?.totalScore ?: 0.0
        val secondScore = secondCandidate?.totalScore ?: 0.0

        // 4. Ambiguity Detection: If two top candidates have very close scores and are both medium/high
        val isAmbiguous = topCandidate != null && secondCandidate != null &&
                topScore >= ElectoralScoringEngine.THRESHOLD_MEDIUM &&
                secondScore >= ElectoralScoringEngine.THRESHOLD_MEDIUM &&
                (topScore - secondScore) < 5.0 &&
                topCandidate.situation.id != secondCandidate.situation.id

        // 5. Determine Confidence Level and Clarification Options
        return when {
            isAmbiguous -> {
                // Ambiguity: Present clarification options clearly
                val options = scoredList.take(3).map { it.situation }
                val clarificationText = buildAmbiguityClarificationMessage(options)
                ElectoralSearchResult(
                    matchLevel = MatchConfidenceLevel.MEDIUM,
                    topSituation = topCandidate?.situation,
                    score = topScore,
                    scoreBreakdown = topCandidate?.scoreBreakdown ?: emptyMap(),
                    clarificationOptions = options,
                    formattedResponse = clarificationText
                )
            }

            topScore >= ElectoralScoringEngine.THRESHOLD_HIGH && topCandidate != null -> {
                // High Confidence Match
                val relatedSituations = topCandidate.situation.respuestasRelacionadas
                    .mapNotNull { ElectoralKnowledgeBase.findById(it) }

                ElectoralSearchResult(
                    matchLevel = MatchConfidenceLevel.HIGH,
                    topSituation = topCandidate.situation,
                    score = topScore,
                    scoreBreakdown = topCandidate.scoreBreakdown,
                    clarificationOptions = relatedSituations.take(3),
                    formattedResponse = formatStructuredResponse(topCandidate.situation, MatchConfidenceLevel.HIGH)
                )
            }

            topScore >= ElectoralScoringEngine.THRESHOLD_MEDIUM && topCandidate != null -> {
                // Medium Confidence Match - Potential ambiguity or partial match
                val otherCloseCandidates = scoredList.drop(1)
                    .filter { it.totalScore >= ElectoralScoringEngine.THRESHOLD_MEDIUM }
                    .map { it.situation }
                    .take(3)

                ElectoralSearchResult(
                    matchLevel = MatchConfidenceLevel.MEDIUM,
                    topSituation = topCandidate.situation,
                    score = topScore,
                    scoreBreakdown = topCandidate.scoreBreakdown,
                    clarificationOptions = otherCloseCandidates,
                    formattedResponse = formatStructuredResponse(topCandidate.situation, MatchConfidenceLevel.MEDIUM)
                )
            }

            else -> {
                // Low Confidence Match - Do NOT invent answers. Search documentary library as backup.
                val documentaryResults = searchDocumentaryLibrary(normalizedQuery.cleanedText)
                ElectoralSearchResult(
                    matchLevel = MatchConfidenceLevel.LOW,
                    topSituation = null,
                    score = topScore,
                    clarificationOptions = emptyList(),
                    formattedResponse = buildLowMatchMessage(trimmed, documentaryResults),
                    documentaryResults = documentaryResults
                )
            }
        }
    }

    private fun isElectoralQuery(text: String): Boolean {
        val electoralTerms = listOf(
            "jrv", "voto", "votacion", "dui", "urna", "papeleta", "electoral",
            "padron", "escrutinio", "mesa", "vigilante", "fiscal", "pnc", "fgr",
            "tse", "acta", "tinta", "centro de votacion", "impugnado", "nulo"
        )
        return electoralTerms.any { text.contains(it) }
    }

    /**
     * Formats an ElectoralSituation into a clean, official, structured Markdown text
     * conforming strictly to the requested sections:
     * - SITUACIÓN DETECTADA
     * - ¿QUÉ HACER? (numbered steps)
     * - ¿QUÉ NO HACER? (prohibitions)
     * - ¿QUIÉN PUEDE? / ¿QUIÉN NO PUEDE? (if applicable)
     * - FUNDAMENTO LEGAL
     * - FUENTES
     */
    fun formatStructuredResponse(
        situation: ElectoralSituation,
        confidenceLevel: MatchConfidenceLevel
    ): String {
        val resp = situation.respuesta
        val sb = StringBuilder()

        // Nivel de confianza discreto
        when (confidenceLevel) {
            MatchConfidenceLevel.HIGH -> {
                sb.append("✅ **Coincidencia encontrada**\n\n")
            }
            MatchConfidenceLevel.MEDIUM -> {
                sb.append("ℹ️ **Coincidencia probable**\n\n")
            }
            MatchConfidenceLevel.LOW -> {
                sb.append("⚠️ **Coincidencia baja**\n\n")
            }
        }

        // 1. SITUACIÓN DETECTADA
        sb.append("SITUACIÓN DETECTADA\n")
        sb.append("${situation.titulo}\n")
        sb.append("${resp.situacion}\n\n")

        // 2. ¿QUIÉN PUEDE? / ¿QUIÉN NO PUEDE? (Si existe para la situación)
        resp.autorizaciones?.let { auth ->
            if (auth.quienPuede.isNotEmpty()) {
                sb.append("¿QUIÉN PUEDE?\n")
                auth.quienPuede.forEach { item ->
                    sb.append("• $item\n")
                }
                sb.append("\n")
            }
            if (auth.quienNoPuede.isNotEmpty()) {
                sb.append("¿QUIÉN NO PUEDE?\n")
                auth.quienNoPuede.forEach { item ->
                    sb.append("• $item\n")
                }
                sb.append("\n")
            }
            if (auth.condiciones.isNotEmpty()) {
                sb.append("CONDICIONES\n")
                auth.condiciones.forEach { item ->
                    sb.append("• $item\n")
                }
                sb.append("\n")
            }
        }

        // 3. ¿QUÉ HACER? (Procedimiento oficial numerado)
        sb.append("¿QUÉ HACER?\n")
        resp.procedimiento.forEachIndexed { index, proc ->
            val stepNumber = index + 1
            val cleanProc = proc.replace(Regex("^\\d+\\.\\s*"), "")
            sb.append("$stepNumber. $cleanProc\n")
        }
        sb.append("\n")

        // 4. ¿QUÉ NO HACER? (Prohibiciones y prevenciones de seguridad)
        if (resp.noHacer.isNotEmpty()) {
            sb.append("¿QUÉ NO HACER?\n")
            resp.noHacer.forEach { no ->
                val cleanNo = no.replace(Regex("^(NO\\s*|•\\s*)", RegexOption.IGNORE_CASE), "")
                sb.append("- No $cleanNo\n")
            }
            sb.append("\n")
        }

        // 5. FUNDAMENTO LEGAL
        sb.append("FUNDAMENTO LEGAL\n")
        if (resp.fundamentosDetallados.isNotEmpty()) {
            resp.fundamentosDetallados.forEach { fund ->
                sb.append("• ${fund.ley} — ${fund.articulo}${if (fund.incisoOrNumeral.isNotBlank()) " (${fund.incisoOrNumeral})" else ""}\n")
                if (fund.descripcion.isNotBlank()) {
                    sb.append("  ${fund.descripcion}\n")
                }
            }
        } else if (resp.fundamento.isNotEmpty()) {
            resp.fundamento.forEach { f ->
                sb.append("• $f\n")
            }
        } else {
            sb.append("• Normativa e instructivos vigentes del Tribunal Supremo Electoral.\n")
        }
        sb.append("\n")

        // 6. ADVERTENCIA (Si aplica)
        if (!resp.advertencia.isNullOrBlank()) {
            sb.append("ADVERTENCIA\n")
            sb.append("⚠️ ${resp.advertencia}\n\n")
        }

        // 7. FUENTES
        sb.append("FUENTES\n")
        if (resp.fuentesDocumentales.isNotEmpty()) {
            resp.fuentesDocumentales.forEach { f ->
                val pagInfo = if (f.pagina != null && f.pagina > 0) " (Pág. ${f.pagina})" else ""
                val artInfo = if (!f.articuloRelacionado.isNullOrBlank()) " — ${f.articuloRelacionado}" else ""
                sb.append("• ${f.documento}$pagInfo$artInfo\n")
            }
        } else if (resp.fuentes.isNotEmpty()) {
            resp.fuentes.forEach { f ->
                sb.append("• $f\n")
            }
        } else {
            sb.append("• Tribunal Supremo Electoral (TSE)\n")
        }

        sb.append("\nEsta respuesta es únicamente orientativa y basada en la normativa oficial salvadoreña.")

        return sb.toString().trim()
    }

    private fun buildAmbiguityClarificationMessage(options: List<ElectoralSituation>): String {
        val sb = StringBuilder()
        sb.append("ℹ️ **Coincidencia probable**\n\n")
        sb.append("Encontré varias situaciones relacionadas en la base de conocimientos, pero necesito identificar con precisión cuál corresponde a su consulta:\n\n")
        sb.append("Seleccione una de las siguientes opciones para ver el procedimiento exacto:\n\n")
        options.forEachIndexed { idx, opt ->
            sb.append("${idx + 1}. **${opt.titulo}**\n")
            sb.append("   ${opt.respuesta.situacion}\n\n")
        }
        sb.append("O bien, presione la opción de su interés o reformule su consulta con más detalles.")
        return sb.toString().trim()
    }

    private fun searchDocumentaryLibrary(cleanedText: String): List<DocumentarySearchResultItem> {
        val results = mutableListOf<DocumentarySearchResultItem>()
        val terms = cleanedText.split(" ").filter { it.length > 3 }

        for (doc in ElectoralLibraryData.documents) {
            for (page in doc.pages) {
                val fullPageContent = page.sections.joinToString(" ") { it.content }
                val pageTextLower = ElectoralNormalizer.stripAccents(fullPageContent.lowercase())
                val matchCount = terms.count { pageTextLower.contains(it) }
                if (matchCount >= 2 || (terms.size == 1 && matchCount == 1)) {
                    val snippet = fullPageContent.take(160).replace("\n", " ") + "..."
                    results.add(
                        DocumentarySearchResultItem(
                            docId = doc.id,
                            documentTitle = doc.title,
                            pageNumber = page.pageNumber,
                            matchedSnippet = snippet
                        )
                    )
                }
            }
        }
        return results.take(3)
    }

    private fun buildLowMatchMessage(query: String, documentaryResults: List<DocumentarySearchResultItem>): String {
        val sb = StringBuilder()
        sb.append("⚠️ **Coincidencia baja**\n\n")
        sb.append("No encontré una coincidencia suficientemente precisa en la base de conocimientos de Asistente Electoral SV para:\n")
        sb.append("\"$query\"\n\n")

        if (documentaryResults.isNotEmpty()) {
            sb.append("📚 **Coincidencias encontradas en la Biblioteca Electoral:**\n")
            documentaryResults.forEach { item ->
                sb.append("• **${item.documentTitle}** (Pág. ${item.pageNumber})\n")
                sb.append("  *\"${item.matchedSnippet}\"*\n\n")
            }
        }

        sb.append("📝 **Opciones disponibles:**\n")
        sb.append("1. 🔎 **Buscar en biblioteca electoral:** Consulte directamente los textos normativos completos.\n")
        sb.append("2. ✏️ **Reformular pregunta:** Describa el actor involucrado (ej. votante, vigilante, JRV) y la acción concreta.")

        return sb.toString().trim()
    }
}
