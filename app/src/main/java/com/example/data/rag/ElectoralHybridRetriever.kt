package com.example.data.rag

import java.util.Locale

object ElectoralHybridRetriever {

    fun retrieve(
        analyzedQuery: AnalyzedQuery,
        preferredDocumentId: String? = null,
        topK: Int = 5
    ): List<ScoredChunk> {
        val allChunks = ElectoralRAGIndexer.knowledgeChunks

        // Pass 1: Hybrid retrieval across sub-queries and main query
        var scoredList = scoreChunks(allChunks, analyzedQuery, preferredDocumentId)

        // Pass 2: Iterative re-query if confidence is low
        val topScore = scoredList.firstOrNull()?.score ?: 0.0
        if (topScore < 20.0) {
            val expandedQuery = expandQueryTerms(analyzedQuery)
            scoredList = scoreChunks(allChunks, expandedQuery, preferredDocumentId)
        }

        // Re-ranking and deduping
        val distinctChunks = mutableListOf<ScoredChunk>()
        val seenIds = mutableSetOf<String>()

        for (scored in scoredList) {
            if (scored.chunk.id !in seenIds && scored.score > 5.0) {
                seenIds.add(scored.chunk.id)
                distinctChunks.add(scored)
                if (distinctChunks.size >= topK) break
            }
        }

        return distinctChunks
    }

    private fun scoreChunks(
        chunks: List<ElectoralKnowledgeChunk>,
        query: AnalyzedQuery,
        preferredDocumentId: String?
    ): List<ScoredChunk> {
        val scored = mutableListOf<ScoredChunk>()

        for (chunk in chunks) {
            var score = 0.0
            val matchReasons = mutableListOf<String>()

            // 1. Preferred Document Boost (When user enters from specific book)
            if (preferredDocumentId != null && chunk.documentId == preferredDocumentId) {
                score += 25.0
                matchReasons.add("Documento preferente seleccionado")
            }

            // 2. Exact Article Match Boost
            for (targetArticle in query.targetArticles) {
                if (chunk.articleRef?.contains(targetArticle, ignoreCase = true) == true ||
                    chunk.content.contains(targetArticle, ignoreCase = true) ||
                    chunk.sectionTitle.contains(targetArticle, ignoreCase = true)) {
                    score += 65.0
                    matchReasons.add("Coincidencia exacta de artículo: $targetArticle")
                }
            }

            // 3. Sub-queries matching
            for (sub in query.subQueries) {
                val subLower = sub.lowercase(Locale.ROOT)
                val chunkContentLower = chunk.content.lowercase(Locale.ROOT)
                val chunkTitleLower = chunk.sectionTitle.lowercase(Locale.ROOT)

                if (chunkContentLower.contains(subLower) || chunkTitleLower.contains(subLower)) {
                    score += 25.0
                    matchReasons.add("Coincidencia de subconsulta")
                }
            }

            // 4. Keyword and Concept overlap
            val chunkSearchableText = "${chunk.documentTitle} ${chunk.chapter} ${chunk.sectionTitle} ${chunk.content} ${chunk.keywords.joinToString(" ")} ${chunk.concepts.joinToString(" ")}".lowercase(Locale.ROOT)

            var matchedTermsCount = 0
            for (term in query.keyTerms) {
                if (chunkSearchableText.contains(term)) {
                    matchedTermsCount++
                    score += 4.5
                }
            }

            if (matchedTermsCount >= 3) {
                score += 15.0
                matchReasons.add("Múltiples términos clave coincidentes ($matchedTermsCount)")
            }

            // 5. Concept & Entity matches
            for (entity in query.targetEntities) {
                val entityLower = entity.lowercase(Locale.ROOT)
                if (chunkSearchableText.contains(entityLower.take(8))) {
                    score += 15.0
                    matchReasons.add("Entidad electoral coincidente: $entity")
                }
            }

            // 6. Domain-specific context boosts (e.g., Extranjero, Municipios, Delitos, LEIV)
            val normQuery = query.normalizedQuery
            if (normQuery.contains("extranjero") || normQuery.contains("exterior") || normQuery.contains("remoto")) {
                if (chunk.documentId == "voto_extranjero" || chunk.content.contains("extranjero", ignoreCase = true)) {
                    score += 30.0
                }
            }
            if (normQuery.contains("municipio") || normQuery.contains("distrito") || normQuery.contains("concejo") || normQuery.contains("alcalde")) {
                if (chunk.documentId == "reestructuracion_municipal" || chunk.content.contains("municipio", ignoreCase = true)) {
                    score += 25.0
                }
            }
            if (normQuery.contains("delito") || normQuery.contains("penal") || normQuery.contains("carcel") || normQuery.contains("prision") || normQuery.contains("fraude")) {
                if (chunk.documentId == "delitos_electorales" || chunk.chapter.contains("DELITOS", ignoreCase = true)) {
                    score += 30.0
                }
            }
            if (normQuery.contains("mujer") || normQuery.contains("violencia politica") || normQuery.contains("acoso")) {
                if (chunk.documentId == "ley_leiv" || chunk.content.contains("violencia", ignoreCase = true)) {
                    score += 30.0
                }
            }

            // 6. Intent alignment
            when (query.intent) {
                QueryIntentType.INCIDENT_SECURITY -> {
                    if (chunk.chapter.contains("SEGURIDAD", ignoreCase = true) ||
                        chunk.sectionTitle.contains("Armas", ignoreCase = true) ||
                        chunk.content.contains("PNC", ignoreCase = true)) {
                        score += 20.0
                    }
                }
                QueryIntentType.EXCEPTION_CONTINGENCY -> {
                    if (chunk.sectionTitle.contains("Faltante", ignoreCase = true) ||
                        chunk.sectionTitle.contains("Sustitución", ignoreCase = true) ||
                        chunk.sectionTitle.contains("Credenciales", ignoreCase = true)) {
                        score += 20.0
                    }
                }
                QueryIntentType.PROHIBITION -> {
                    if (chunk.chapter.contains("PROHIBICIONES", ignoreCase = true) ||
                        chunk.sectionTitle.contains("Ley Seca", ignoreCase = true) ||
                        chunk.sectionTitle.contains("Prohibición", ignoreCase = true)) {
                        score += 20.0
                    }
                }
                QueryIntentType.PROCEDURAL -> {
                    if (chunk.sectionTitle.contains("Procedimiento", ignoreCase = true) ||
                        chunk.sectionTitle.contains("Horarios", ignoreCase = true) ||
                        chunk.chapter.contains("ESCRUTINIO", ignoreCase = true)) {
                        score += 15.0
                    }
                }
                QueryIntentType.VOTING_ASSISTANCE -> {
                    if (chunk.sectionTitle.contains("Voto Asistido", ignoreCase = true) ||
                        chunk.keywords.contains("discapacidad")) {
                        score += 25.0
                    }
                }
                else -> {}
            }

            if (score > 0.0) {
                scored.add(ScoredChunk(chunk, score, matchReasons))
            }
        }

        return scored.sortedByDescending { it.score }
    }

    private fun expandQueryTerms(analyzedQuery: AnalyzedQuery): AnalyzedQuery {
        val expandedTerms = analyzedQuery.keyTerms.toMutableList()
        val queryText = analyzedQuery.normalizedQuery

        val synonyms = mapOf(
            "papeletas" to listOf("boletas", "paquete", "material", "inventario"),
            "falta" to listOf("ausencia", "incompleto", "faltante", "sustitucion"),
            "cuchillo" to listOf("objeto", "cortante", "cortopunzante", "arma", "art 290"),
            "armas" to listOf("arma de fuego", "art 290", "seguridad", "pnc"),
            "borrachos" to listOf("ley seca", "alcohol", "art 284"),
            "permiso" to listOf("art 113", "trabajo", "patrono", "goce de sueldo"),
            "abrir" to listOf("apertura", "7:00 am", "instalacion"),
            "cerrar" to listOf("cierre", "5:00 pm", "17:00", "escrutinio")
        )

        for ((key, synList) in synonyms) {
            if (queryText.contains(key)) {
                expandedTerms.addAll(synList)
            }
        }

        return analyzedQuery.copy(
            keyTerms = expandedTerms.distinct()
        )
    }
}
