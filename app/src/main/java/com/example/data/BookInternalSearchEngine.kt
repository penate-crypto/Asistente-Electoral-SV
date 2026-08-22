package com.example.data

import android.content.Context
import com.example.data.rag.ElectoralRAGIndexer
import org.json.JSONObject
import java.text.Normalizer
import java.util.Locale
import java.util.regex.Pattern

data class BookSearchResult(
    val id: String,
    val documentId: String,
    val documentTitle: String,
    val sectionTitle: String,
    val articleRef: String?,
    val pageNumber: Int,
    val snippet: String,
    val fullContent: String,
    val score: Int
)

data class IndexedArticle(
    val article: String,
    val articleNumber: Int,
    val title: String,
    val page: Int,
    val snippet: String
)

data class IndexedPage(
    val pageNumber: Int,
    val text: String
)

data class IndexedBook(
    val fileName: String,
    val totalPages: Int,
    val articles: List<IndexedArticle>,
    val pages: List<IndexedPage>
)

object BookInternalSearchEngine {

    private val indexedBooks = mutableMapOf<String, IndexedBook>()
    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return
        synchronized(this) {
            if (isInitialized) return
            try {
                val jsonString = context.assets.open("libros_indice_completo.json").bufferedReader().use { it.readText() }
                val rootJson = JSONObject(jsonString)
                val keys = rootJson.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val bookObj = rootJson.getJSONObject(key)
                    val fileName = bookObj.optString("fileName", key)
                    val totalPages = bookObj.optInt("totalPages", 1)

                    val articlesList = mutableListOf<IndexedArticle>()
                    val articlesArr = bookObj.optJSONArray("articles")
                    if (articlesArr != null) {
                        for (i in 0 until articlesArr.length()) {
                            val artObj = articlesArr.getJSONObject(i)
                            articlesList.add(
                                IndexedArticle(
                                    article = artObj.optString("article", "Art. ${artObj.optInt("articleNumber", i + 1)}"),
                                    articleNumber = artObj.optInt("articleNumber", 0),
                                    title = artObj.optString("title", "Artículo ${artObj.optInt("articleNumber", i + 1)}"),
                                    page = artObj.optInt("page", 1),
                                    snippet = artObj.optString("snippet", "")
                                )
                            )
                        }
                    }

                    val pagesList = mutableListOf<IndexedPage>()
                    val pagesArr = bookObj.optJSONArray("pages")
                    if (pagesArr != null) {
                        for (i in 0 until pagesArr.length()) {
                            val pageObj = pagesArr.getJSONObject(i)
                            pagesList.add(
                                IndexedPage(
                                    pageNumber = pageObj.optInt("pageNumber", i + 1),
                                    text = pageObj.optString("text", "")
                                )
                            )
                        }
                    }

                    val indexedBook = IndexedBook(
                        fileName = fileName,
                        totalPages = totalPages,
                        articles = articlesList,
                        pages = pagesList
                    )
                    indexedBooks[key.lowercase()] = indexedBook
                    indexedBooks[fileName.lowercase()] = indexedBook
                }
                isInitialized = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun normalize(text: String): String {
        val nfd = Normalizer.normalize(text, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(nfd).replaceAll("").lowercase(Locale.ROOT)
    }

    private fun resolveBook(documentId: String, assetPath: String?): IndexedBook? {
        val idNorm = documentId.lowercase()
        val pathNorm = (assetPath ?: "").substringAfterLast("/").lowercase()

        // 1. Direct path/filename lookup
        if (pathNorm.isNotEmpty()) {
            indexedBooks[pathNorm]?.let { return it }
        }

        // 2. Exact match in indexedBooks keys
        indexedBooks[idNorm]?.let { return it }

        // 3. Known mappings for standard documents
        val targetFile = when {
            idNorm.contains("penal") || pathNorm.contains("penal") -> "código penal.pdf"
            idNorm.contains("reestructuracion") || idNorm.contains("municipal") || pathNorm.contains("reestructuracion") -> "ley especial para la reestructuración municipal.pdf"
            idNorm.contains("extranjero") || idNorm.contains("sufragio") || pathNorm.contains("extranjero") -> "ley especial para el ejercicio del sufragio en el extranjero.pdf"
            idNorm.contains("ciclo") || pathNorm.contains("ciclo") -> "ciclo electoral salvadoreño.pdf"
            idNorm.contains("acuerdo") || idNorm.contains("segunda") || pathNorm.contains("segunda") || pathNorm.contains("acuerdo") -> "acuerdo legislativo - reforma - supreción segunda elección.pdf"
            idNorm.contains("codigo") || idNorm.contains("electoral") || pathNorm.contains("codigo-electoral") -> "codigo-electoral.pdf"
            idNorm.contains("constitucion") || pathNorm.contains("constitucion") -> "constitucion.pdf"
            idNorm.contains("partidos") || pathNorm.contains("ley-de-partidos-politicos") -> "ley-de-partidos-politicos.pdf"
            idNorm.contains("acceso") || idNorm.contains("informacion") || pathNorm.contains("informacion-publica") -> "ley-de-acceso-a-la-informacion-publica.pdf"
            idNorm.contains("no_partidarias") || idNorm.contains("candidaturas") || pathNorm.contains("no-partidarias") -> "disposciones-especiales-candidaturas-no-partidarias.pdf"
            idNorm.contains("observacion") || pathNorm.contains("observacion") -> "reglamento-general-para-la-observacion-electoral-nacional-e-internacional-en-el-salvador.pdf"
            idNorm.contains("instructivo") || idNorm.contains("jrv") || pathNorm.contains("instructivo-jrv") -> "instructivo-jrv.pdf"
            else -> null
        }

        if (targetFile != null) {
            indexedBooks[targetFile.lowercase()]?.let { return it }
            indexedBooks[normalize(targetFile)]?.let { return it }
        }

        // 4. Fuzzy search across keys
        return indexedBooks.values.firstOrNull { book ->
            val fn = book.fileName.lowercase()
            fn == pathNorm || fn.contains(idNorm) || idNorm.contains(fn.removeSuffix(".pdf"))
        }
    }

    fun searchDocument(
        documentId: String,
        documentTitle: String,
        query: String,
        assetPath: String? = null,
        context: Context? = null
    ): List<BookSearchResult> {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isEmpty()) return emptyList()

        if (context != null && !isInitialized) {
            init(context)
        }

        val normalizedQuery = normalize(trimmedQuery)
        val queryWords = normalizedQuery.split("\\s+".toRegex()).filter { it.length > 1 }

        // Extract potential article numbers like "190", "art 190", "art. 190", "articulo 190"
        val articleNumberPattern = "(?:art(?:[íi]cu?lo|\\.)?|articulo)?\\s*\\.?\\s*(\\d+)".toRegex(RegexOption.IGNORE_CASE)
        val articleMatch = articleNumberPattern.find(trimmedQuery)
        val targetArticleInt = articleMatch?.groupValues?.getOrNull(1)?.toIntOrNull()

        val results = mutableListOf<BookSearchResult>()
        val seenSignatures = mutableSetOf<String>()

        // 1. Search in Comprehensive Indexed Book
        val book = resolveBook(documentId, assetPath)
        if (book != null) {
            // A. Exact & Partial Article matches from book.articles
            for (art in book.articles) {
                var score = 0
                var isMatch = false

                if (targetArticleInt != null) {
                    if (art.articleNumber == targetArticleInt) {
                        // EXACT article requested: highest priority
                        score += 5000
                        isMatch = true
                    } else if (art.article.contains(targetArticleInt.toString())) {
                        score += 1500
                        isMatch = true
                    }
                }

                val normTitle = normalize(art.title)
                val normSnippet = normalize(art.snippet)
                val normArticle = normalize(art.article)

                if (normSnippet.contains(normalizedQuery) || normTitle.contains(normalizedQuery) || normArticle.contains(normalizedQuery)) {
                    score += 500
                    isMatch = true
                }

                var matchedWords = 0
                for (w in queryWords) {
                    if (normTitle.contains(w) || normArticle.contains(w)) {
                        score += 40
                        matchedWords++
                    }
                    if (normSnippet.contains(w)) {
                        score += 25
                        matchedWords++
                    }
                }

                if (matchedWords == queryWords.size && queryWords.isNotEmpty()) {
                    score += 100
                    isMatch = true
                } else if (matchedWords > 0) {
                    isMatch = true
                }

                if (isMatch) {
                    val signature = "${art.page}_${art.article}"
                    if (signature !in seenSignatures) {
                        seenSignatures.add(signature)
                        val displaySnippet = if (art.snippet.isNotBlank()) {
                            extractSnippet(art.snippet, queryWords, trimmedQuery)
                        } else {
                            "Artículo ${art.articleNumber} de $documentTitle (Página ${art.page})"
                        }

                        results.add(
                            BookSearchResult(
                                id = signature,
                                documentId = documentId,
                                documentTitle = documentTitle,
                                sectionTitle = art.title,
                                articleRef = art.article,
                                pageNumber = art.page,
                                snippet = displaySnippet,
                                fullContent = art.snippet,
                                score = score
                            )
                        )
                    }
                }
            }

            // B. Full text search across pages in book.pages
            for (pg in book.pages) {
                val normText = normalize(pg.text)
                var score = 0
                var isMatch = false

                if (targetArticleInt != null) {
                    if (normText.contains("art. $targetArticleInt") || normText.contains("articulo $targetArticleInt") || normText.contains("art $targetArticleInt")) {
                        score += 800
                        isMatch = true
                    }
                }

                if (normText.contains(normalizedQuery)) {
                    score += 300
                    isMatch = true
                }

                var pageMatchedWords = 0
                for (w in queryWords) {
                    if (normText.contains(w)) {
                        score += 15
                        pageMatchedWords++
                    }
                }

                if (pageMatchedWords == queryWords.size && queryWords.isNotEmpty()) {
                    score += 60
                    isMatch = true
                } else if (pageMatchedWords > 1) {
                    isMatch = true
                }

                if (isMatch) {
                    val signature = "pg_${pg.pageNumber}"
                    if (signature !in seenSignatures) {
                        seenSignatures.add(signature)
                        val snippet = extractSnippet(pg.text, queryWords, trimmedQuery)
                        results.add(
                            BookSearchResult(
                                id = signature,
                                documentId = documentId,
                                documentTitle = documentTitle,
                                sectionTitle = "Contenido en Página ${pg.pageNumber}",
                                articleRef = if (targetArticleInt != null) "Art. $targetArticleInt" else null,
                                pageNumber = pg.pageNumber,
                                snippet = snippet,
                                fullContent = pg.text,
                                score = score
                            )
                        )
                    }
                }
            }
        }

        // 2. Secondary fallback: ElectoralLibraryData structured pages
        val matchingPdfDoc = ElectoralLibraryData.documents.firstOrNull { it.id == documentId }
        if (matchingPdfDoc != null) {
            matchingPdfDoc.pages.forEach { page ->
                page.sections.forEach { section ->
                    val normTitle = normalize(section.title)
                    val normContent = normalize(section.content)
                    val normArticleRef = normalize(section.articleRef ?: "")

                    var score = 0
                    var isMatch = false

                    if (targetArticleInt != null) {
                        val numStr = targetArticleInt.toString()
                        if (normArticleRef.contains(numStr) || normTitle.contains(numStr)) {
                            score += 1000
                            isMatch = true
                        } else if (normContent.contains("art. $numStr") || normContent.contains("artículo $numStr") || normContent.contains("articulo $numStr")) {
                            score += 500
                            isMatch = true
                        }
                    }

                    if (normContent.contains(normalizedQuery) || normTitle.contains(normalizedQuery)) {
                        score += 200
                        isMatch = true
                    }

                    var matchedWords = 0
                    for (word in queryWords) {
                        if (normTitle.contains(word)) {
                            score += 15
                            matchedWords++
                        }
                        if (normContent.contains(word)) {
                            score += 10
                            matchedWords++
                        }
                    }

                    if (matchedWords == queryWords.size && queryWords.isNotEmpty()) {
                        score += 30
                        isMatch = true
                    } else if (matchedWords > 0) {
                        isMatch = true
                    }

                    if (isMatch) {
                        val signature = "${page.pageNumber}_${section.title}"
                        if (signature !in seenSignatures) {
                            seenSignatures.add(signature)
                            val snippet = extractSnippet(section.content, queryWords, trimmedQuery)
                            results.add(
                                BookSearchResult(
                                    id = signature,
                                    documentId = matchingPdfDoc.id,
                                    documentTitle = matchingPdfDoc.title,
                                    sectionTitle = section.title,
                                    articleRef = section.articleRef,
                                    pageNumber = page.pageNumber,
                                    snippet = snippet,
                                    fullContent = section.content,
                                    score = score
                                )
                            )
                        }
                    }
                }
            }
        }

        // 3. Fallback: RAG chunks
        val ragChunks = ElectoralRAGIndexer.knowledgeChunks.filter { it.documentId == documentId }
        for (chunk in ragChunks) {
            val normTitle = normalize(chunk.sectionTitle)
            val normContent = normalize(chunk.content)
            val normArticleRef = normalize(chunk.articleRef ?: "")

            var score = 0
            var isMatch = false

            if (targetArticleInt != null) {
                val numStr = targetArticleInt.toString()
                if (normArticleRef.contains(numStr) || normTitle.contains(numStr)) {
                    score += 600
                    isMatch = true
                } else if (normContent.contains("art. $numStr") || normContent.contains("articulo $numStr")) {
                    score += 300
                    isMatch = true
                }
            }

            if (normContent.contains(normalizedQuery) || normTitle.contains(normalizedQuery)) {
                score += 150
                isMatch = true
            }

            var matchedWords = 0
            for (word in queryWords) {
                if (normTitle.contains(word) || normArticleRef.contains(word)) {
                    score += 15
                    matchedWords++
                }
                if (normContent.contains(word)) {
                    score += 10
                    matchedWords++
                }
            }

            if (matchedWords == queryWords.size && queryWords.isNotEmpty()) {
                score += 30
                isMatch = true
            } else if (matchedWords > 0) {
                isMatch = true
            }

            if (isMatch) {
                val signature = "${chunk.pageNumber}_${chunk.sectionTitle}"
                if (signature !in seenSignatures) {
                    seenSignatures.add(signature)
                    val snippet = extractSnippet(chunk.content, queryWords, trimmedQuery)
                    results.add(
                        BookSearchResult(
                            id = chunk.id,
                            documentId = chunk.documentId,
                            documentTitle = chunk.documentTitle,
                            sectionTitle = chunk.sectionTitle,
                            articleRef = chunk.articleRef,
                            pageNumber = chunk.pageNumber,
                            snippet = snippet,
                            fullContent = chunk.content,
                            score = score
                        )
                    )
                }
            }
        }

        return results.sortedByDescending { it.score }
    }

    private fun extractSnippet(content: String, queryWords: List<String>, rawQuery: String): String {
        if (content.isBlank()) return ""
        val normContent = normalize(content)
        val normRaw = normalize(rawQuery)

        var matchIndex = normContent.indexOf(normRaw)
        if (matchIndex == -1 && queryWords.isNotEmpty()) {
            for (w in queryWords) {
                val idx = normContent.indexOf(w)
                if (idx != -1) {
                    matchIndex = idx
                    break
                }
            }
        }

        if (matchIndex == -1) {
            return if (content.length <= 220) content else content.take(220) + "..."
        }

        val start = (matchIndex - 45).coerceAtLeast(0)
        val end = (matchIndex + 160).coerceAtMost(content.length)

        val prefix = if (start > 0) "... " else ""
        val suffix = if (end < content.length) " ..." else ""

        return prefix + content.substring(start, end).trim() + suffix
    }
}
