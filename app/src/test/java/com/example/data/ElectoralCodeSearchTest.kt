package com.example.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.search.codigo.CodeMatchKind
import com.example.data.search.codigo.ElectoralCodeBank
import com.example.data.search.codigo.ElectoralCodeSearch
import com.example.data.search.engine.ElectoralSearchEngine
import com.example.data.search.knowledge.ElectoralKnowledgeBase
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ElectoralCodeSearchTest {

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        ElectoralKnowledgeBase.init(context)
    }

    @Test
    fun loadsAll297ArticlesFromAssets() {
        assertTrue("ElectoralCodeBank debe cargarse desde assets", ElectoralCodeBank.isLoaded())
        assertEquals(297, ElectoralCodeBank.all().size)
        val art198 = ElectoralCodeBank.findByNumber("198")
        assertNotNull(art198)
        assertEquals("codigo_electoral_decreto_413", art198!!.pdfDocId)
        assertTrue(art198.paginaPdf > 0)
    }

    @Test
    fun horaDeCierreReturnsOnlyArt198() {
        val result = ElectoralCodeSearch.search("código electoral hora de cierre")
        assertNotNull(result)
        assertEquals(CodeMatchKind.TOPIC, result!!.kind)
        assertEquals(1, result.articles.size)
        assertEquals("198", result.articles[0].numero)
    }

    @Test
    fun jrvTopicReturnsMultipleArticlesWithVerMas() {
        val result = ElectoralCodeSearch.search("en el código electoral dónde aparece lo de la JRV")
        assertNotNull(result)
        assertEquals(CodeMatchKind.TOPIC, result!!.kind)
        assertTrue("Debe mostrar varios artículos (hasta 5)", result.articles.size in 2..5)
        assertTrue("Debe tener artículos adicionales para Ver más", result.moreCount > 0)
        assertTrue(result.moreArticles.isNotEmpty())
    }

    @Test
    fun articulo290ReturnsDirectArticleNumberMatch() {
        val result = ElectoralCodeSearch.search("código electoral artículo 290")
        assertNotNull(result)
        assertEquals(CodeMatchKind.ARTICLE_NUMBER, result!!.kind)
        assertEquals(1, result.articles.size)
        assertEquals("290", result.articles[0].numero)
    }

    @Test
    fun boloMatchesArt284() {
        val result = ElectoralCodeSearch.search("código electoral bolo")
        assertNotNull(result)
        assertEquals(CodeMatchKind.TOPIC, result!!.kind)
        assertTrue(
            "Art. 284 debe aparecer en los resultados para 'código electoral bolo', obtenidos: ${result.articles.map { it.numero }}",
            result.articles.any { it.numero == "284" }
        )
        assertEquals("284", result.articles.first().numero)
    }

    @Test
    fun rangoArticulos200Al203ReturnsFourCards() {
        val result = ElectoralCodeSearch.search("código electoral artículos 200 al 203")
        assertNotNull(result)
        assertEquals(CodeMatchKind.ARTICLE_NUMBER, result!!.kind)
        assertEquals(4, result.articles.size)
        assertEquals(listOf("200", "201", "202", "203"), result.articles.map { it.numero })
    }

    @Test
    fun normalQueryWithoutCodigoElectoralFallsBackToMainEngine() {
        val query = "cómo se instala la mesa"
        val codeResult = ElectoralCodeSearch.search(query)
        assertNull("No debe activar el Banco Código Electoral si no se menciona", codeResult)

        val mainResult = ElectoralSearchEngine.search(query)
        assertNotNull(mainResult.topSituation)
        assertEquals("INST-001", mainResult.topSituation!!.id)
    }
}
