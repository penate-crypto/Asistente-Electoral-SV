package com.example.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BookInternalSearchEngineTest {

    private lateinit var context: Context

    private val docId = "codigo_electoral_decreto_413"
    private val docTitle = "Código Electoral"
    private val assetPath = "libros/Codigo-Electoral.pdf"

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        BookInternalSearchEngine.init(context)
    }

    @Test
    fun search195OpensPage65() {
        val results = BookInternalSearchEngine.searchDocument(
            documentId = docId,
            documentTitle = docTitle,
            query = "195",
            assetPath = assetPath,
            context = context
        )
        assertTrue("Debe retornar resultados para '195'", results.isNotEmpty())
        val top = results.first()
        println("QUERY '195' -> top: page=${top.pageNumber}, articleRef=${top.articleRef}, title=${top.sectionTitle}, score=${top.score}")
        assertEquals(65, top.pageNumber)
        assertEquals("Art. 195", top.articleRef)
    }

    @Test
    fun searchArt198OpensPage67() {
        val results = BookInternalSearchEngine.searchDocument(
            documentId = docId,
            documentTitle = docTitle,
            query = "art 198",
            assetPath = assetPath,
            context = context
        )
        assertTrue("Debe retornar resultados para 'art 198'", results.isNotEmpty())
        val top = results.first()
        println("QUERY 'art 198' -> top: page=${top.pageNumber}, articleRef=${top.articleRef}, title=${top.sectionTitle}, score=${top.score}")
        assertEquals(67, top.pageNumber)
        assertEquals("Art. 198", top.articleRef)
    }

    @Test
    fun search191OpensPage64() {
        val results = BookInternalSearchEngine.searchDocument(
            documentId = docId,
            documentTitle = docTitle,
            query = "191",
            assetPath = assetPath,
            context = context
        )
        assertTrue("Debe retornar resultados para '191'", results.isNotEmpty())
        val top = results.first()
        println("QUERY '191' -> top: page=${top.pageNumber}, articleRef=${top.articleRef}, title=${top.sectionTitle}, score=${top.score}")
        assertEquals(64, top.pageNumber)
        assertEquals("Art. 191", top.articleRef)
    }

    @Test
    fun search290OpensPage97() {
        val results = BookInternalSearchEngine.searchDocument(
            documentId = docId,
            documentTitle = docTitle,
            query = "290",
            assetPath = assetPath,
            context = context
        )
        assertTrue("Debe retornar resultados para '290'", results.isNotEmpty())
        val top = results.first()
        println("QUERY '290' -> top: page=${top.pageNumber}, articleRef=${top.articleRef}, title=${top.sectionTitle}, score=${top.score}")
        assertEquals(97, top.pageNumber)
        assertEquals("Art. 290", top.articleRef)
    }

    @Test
    fun search2013IsNotTreatedAsArticleNumber() {
        val results = BookInternalSearchEngine.searchDocument(
            documentId = docId,
            documentTitle = docTitle,
            query = "2013",
            assetPath = assetPath,
            context = context
        )
        println("QUERY '2013' -> count=${results.size}, top3=${results.take(3).map { "page=${it.pageNumber}, articleRef=${it.articleRef}, title=${it.sectionTitle}, score=${it.score}" }}")
        assertFalse(
            "2013 no debe recibir puntaje de artículo exacto (>= 5000)",
            results.any { it.score >= 5000 }
        )
        assertFalse(
            "2013 no debe etiquetarse como Art. 2013",
            results.any { it.articleRef == "Art. 2013" }
        )
    }
}
