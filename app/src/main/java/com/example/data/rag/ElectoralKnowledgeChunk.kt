package com.example.data.rag

data class ElectoralKnowledgeChunk(
    val id: String,
    val documentId: String,
    val documentTitle: String,
    val chapter: String,
    val sectionTitle: String,
    val articleRef: String? = null,
    val pageNumber: Int,
    val content: String,
    val sourceType: String, // "Ley Oficial", "Instructivo Operativo JRV", "Constitución de la República", "Manual de Escrutinio", "Reglamento de Seguridad"
    val keywords: List<String> = emptyList(),
    val concepts: List<String> = emptyList()
)

data class ScoredChunk(
    val chunk: ElectoralKnowledgeChunk,
    val score: Double,
    val matchReasons: List<String> = emptyList()
)

enum class QueryIntentType {
    PROCEDURAL,           // Procedimiento paso a paso
    PROHIBITION,          // Prohibiciones, sanciones, ley seca, armas
    DEFINITION,           // Definiciones de votos, cargos, organismos
    EXCEPTION_CONTINGENCY,// Contingencias, falta de material, ausencia de miembros
    INCIDENT_SECURITY,    // Incidentes, personas armadas, orden público, auxilio PNC
    ARTICLE_LOOKUP,       // Búsqueda de artículo legal específico
    VOTING_ASSISTANCE,    // Voto asistido, inclusión, LEIV mujer
    GENERAL_ELECTORAL,    // Consulta general
    OUT_OF_SCOPE          // Consulta fuera del ámbito electoral salvadoreño
}

data class AnalyzedQuery(
    val originalQuery: String,
    val normalizedQuery: String,
    val intent: QueryIntentType,
    val targetArticles: List<String>,
    val targetEntities: List<String>,
    val subQueries: List<String>,
    val keyTerms: List<String>
)
