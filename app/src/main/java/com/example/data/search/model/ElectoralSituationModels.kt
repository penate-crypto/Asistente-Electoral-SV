package com.example.data.search.model

/**
 * Status of the knowledge record for versioning and deprecation control.
 */
enum class RecordStatus {
    VIGENTE,
    REVISAR,
    OBSOLETO
}

/**
 * Specific legal article foundation.
 */
data class LegalFoundationItem(
    val ley: String,
    val articulo: String,
    val incisoOrNumeral: String = "",
    val descripcion: String = "",
    val pdfDocId: String? = null,
    val pdfPage: Int = 1
)

/**
 * Specific documentary source with direct page link.
 */
data class DocumentarySource(
    val documento: String,
    val tipoDocumento: String = "Oficial",
    val pagina: Int? = null,
    val articuloRelacionado: String? = null,
    val seccion: String? = null,
    val pdfDocId: String = "codigo_electoral_decreto_413"
)

/**
 * Authorization rule specification: who can and who cannot perform an action.
 */
data class AuthorizationRules(
    val quienPuede: List<String> = emptyList(),
    val quienNoPuede: List<String> = emptyList(),
    val condiciones: List<String> = emptyList()
)

/**
 * Enhanced structured response format for an electoral situation.
 */
data class StructuredElectoralResponse(
    val situacion: String,
    val fundamento: List<String> = emptyList(),
    val fundamentosDetallados: List<LegalFoundationItem> = emptyList(),
    val procedimiento: List<String>,
    val noHacer: List<String> = emptyList(),
    val autorizaciones: AuthorizationRules? = null,
    val fuentes: List<String> = emptyList(),
    val fuentesDocumentales: List<DocumentarySource> = emptyList(),
    val advertencia: String? = null,
    val primaryPdfDocId: String? = "codigo_electoral_decreto_413",
    val primaryPdfPage: Int = 1
)

/**
 * Model representing a pre-validated, structured electoral situation.
 */
data class ElectoralSituation(
    val id: String,
    val categoria: String,
    val subcategoria: String,
    val titulo: String,
    val descripcionSituacion: String = "",
    val keywords: List<String>,
    val sinonimos: List<String> = emptyList(),
    val actores: List<String> = emptyList(),
    val acciones: List<String> = emptyList(),
    val objetos: List<String> = emptyList(),
    val lugares: List<String> = emptyList(),
    val momentos: List<String> = emptyList(),
    val contextos: List<String> = emptyList(),
    val intenciones: List<String> = emptyList(),
    val contexto: String = "",
    val prioridadJerarquica: Int = 2, // 1: Constitucion, 2: Codigo Electoral, 3: Leyes, 4: Reglamentos, 5: Manuales JRV
    val estado: RecordStatus = RecordStatus.VIGENTE,
    val fechaCreacion: String = "2024-01-01",
    val fechaActualizacion: String = "2024-02-01",
    val versionDocumento: String = "1.0",
    val respuesta: StructuredElectoralResponse,
    val respuestasRelacionadas: List<String> = emptyList()
)

/**
 * Match result representation with confidence levels and score.
 */
data class ElectoralSearchResult(
    val matchLevel: MatchConfidenceLevel,
    val topSituation: ElectoralSituation?,
    val score: Double,
    val scoreBreakdown: Map<String, Double> = emptyMap(),
    val clarificationOptions: List<ElectoralSituation> = emptyList(),
    val formattedResponse: String,
    val isOffTopic: Boolean = false,
    val offTopicMessage: String? = null,
    val documentaryResults: List<DocumentarySearchResultItem> = emptyList()
)

data class DocumentarySearchResultItem(
    val docId: String,
    val documentTitle: String,
    val pageNumber: Int,
    val matchedSnippet: String
)

enum class MatchConfidenceLevel {
    HIGH,     // Coincidencia Alta: Coincidencia encontrada con precisión
    MEDIUM,   // Coincidencia Media: Coincidencia probable / requiere aclaración
    LOW       // Coincidencia Baja: Sin coincidencia precisa en la base
}
