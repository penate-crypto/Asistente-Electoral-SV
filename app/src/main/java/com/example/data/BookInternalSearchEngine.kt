package com.example.data

import android.content.Context

/**
 * Resultado de búsqueda dentro de un libro de la Biblioteca.
 * [highlights] son los rangos de [snippet] que se subrayan en amarillo (ya sin importar tildes ni mayúsculas).
 */
data class BookSearchResult(
    val id: String,
    val documentId: String,
    val documentTitle: String,
    val sectionTitle: String,
    val articleRef: String?,
    val pageNumber: Int,
    val snippet: String,
    val fullContent: String,
    val score: Int,
    val highlights: List<IntRange> = emptyList(),
    val isArticleMatch: Boolean = false
)

/**
 * Fachada que usan las pantallas. La lógica real está en [BookSearch]
 * (índice generado de los PDF reales; sin resultados de respaldo con páginas escritas a mano).
 */
object BookInternalSearchEngine {

    fun init(context: Context) {
        // Carga perezosa: el índice se lee la primera vez que se busca. Aquí solo se guarda el contexto de aplicación.
        appContext = context.applicationContext
    }

    @Volatile private var appContext: Context? = null

    /** Búsqueda con mensaje (ej. «El Art. 9999 no aparece en este documento»). Llamar fuera del hilo principal. */
    fun searchOutcome(
        documentId: String,
        documentTitle: String,
        query: String,
        assetPath: String? = null,
        context: Context? = null
    ): Pair<List<BookSearchResult>, String?> {
        val ctx = context?.applicationContext ?: appContext ?: return emptyList<BookSearchResult>() to null
        val file = BookSearch.resolveFileName(documentId, assetPath)
            ?: return emptyList<BookSearchResult>() to "Este documento no tiene índice de búsqueda."
        val outcome = BookSearch.search(ctx, file, query)
        val results = outcome.hits.map { h ->
            BookSearchResult(
                id = "${h.pageNumber}_${h.articleRef ?: h.kind.name}",
                documentId = documentId,
                documentTitle = documentTitle,
                sectionTitle = h.title,
                articleRef = h.articleRef,
                pageNumber = h.pageNumber,
                snippet = h.snippet,
                fullContent = h.snippet,
                score = h.score,
                highlights = h.highlights,
                isArticleMatch = h.kind == BookSearch.Kind.ARTICLE
            )
        }
        return results to outcome.message
    }

    /** Misma búsqueda sin mensaje (compatibilidad con el código anterior). */
    fun searchDocument(
        documentId: String,
        documentTitle: String,
        query: String,
        assetPath: String? = null,
        context: Context? = null
    ): List<BookSearchResult> = searchOutcome(documentId, documentTitle, query, assetPath, context).first
}
