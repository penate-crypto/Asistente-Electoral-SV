package com.example.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.search.codigo.CodeMatchKind
import com.example.data.search.codigo.ElectoralCodeSearch
import com.example.data.search.codigo.ElectoralConstitutionBank
import com.example.data.search.codigo.ElectoralConstitutionSearch
import com.example.data.search.knowledge.ElectoralKnowledgeBase
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ElectoralConstitutionSearchTest {

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        ElectoralKnowledgeBase.init(context)
    }

    private fun searchInAppOrder(pregunta: String) =
        ElectoralCodeSearch.search(pregunta) ?: ElectoralConstitutionSearch.search(pregunta)

    @Test
    fun loadsAll274ConstitutionArticlesFromAssets() {
        assertTrue("ElectoralConstitutionBank debe cargarse desde assets", ElectoralConstitutionBank.isLoaded())
        assertEquals(274, ElectoralConstitutionBank.all().size)
        val art72 = ElectoralConstitutionBank.findByNumber("72")
        assertNotNull(art72)
        assertEquals("constitucion_republica_1983", art72!!.pdfDocId)
        assertEquals("libros/constitucion.pdf", art72.ubicacionPdf)
        assertTrue(art72.paginaPdf > 0)
    }

    @Test
    fun constitucionArticulo72ReturnsDerechosPoliticos() {
        val result = searchInAppOrder("constitución artículo 72")
        assertNotNull(result)
        assertEquals(CodeMatchKind.ARTICLE_NUMBER, result!!.kind)
        assertEquals(1, result.articles.size)
        assertEquals("72", result.articles[0].numero)
        assertEquals("constitucion_republica_1983", result.articles[0].pdfDocId)
        assertEquals("libros/constitucion.pdf", result.articles[0].ubicacionPdf)
        assertTrue(result.articles[0].textoLegible.lowercase().contains("derechos políticos"))
    }

    @Test
    fun constitucionTribunalSupremoElectoralReturnsArt208First() {
        val result = searchInAppOrder("constitución tribunal supremo electoral")
        assertNotNull(result)
        assertEquals(CodeMatchKind.TOPIC, result!!.kind)
        assertEquals(
            "Art. 208 debe aparecer en primer lugar, obtenidos: ${result.articles.map { it.numero }}",
            "208",
            result.articles.firstOrNull()?.numero
        )
        assertEquals("constitucion_republica_1983", result.articles[0].pdfDocId)
        assertNotEquals(result.articles[0].textoLiteral, result.articles[0].textoLegible)
    }

    @Test
    fun constitucionDeberesDelCiudadanoReturnsArt73First() {
        val result = searchInAppOrder("en la constitución dónde dice los deberes del ciudadano")
        assertNotNull(result)
        assertEquals(CodeMatchKind.TOPIC, result!!.kind)
        assertEquals(
            "Art. 73 debe aparecer en primer lugar, obtenidos: ${result.articles.map { it.numero }}",
            "73",
            result.articles.firstOrNull()?.numero
        )
    }

    @Test
    fun constitucionArticulos75Al77ReturnsThreeCards() {
        val result = searchInAppOrder("constitución artículos 75 al 77")
        assertNotNull(result)
        assertEquals(CodeMatchKind.ARTICLE_NUMBER, result!!.kind)
        assertEquals(3, result.articles.size)
        assertEquals(listOf("75", "76", "77"), result.articles.map { it.numero })
    }

    @Test
    fun constitucionPastoresNoPuedenPertenecerAPartidosReturnsArt82First() {
        val result = searchInAppOrder("constitución pastores no pueden pertenecer a partidos")
        assertNotNull(result)
        assertEquals(CodeMatchKind.TOPIC, result!!.kind)
        assertEquals(
            "Art. 82 debe aparecer en primer lugar, obtenidos: ${result.articles.map { it.numero }}",
            "82",
            result.articles.firstOrNull()?.numero
        )
    }

    @Test
    fun constitucionAbreviacionYEquivalenciasFuncionan() {
        val abrev = searchInAppOrder("const art 72")
        assertNotNull(abrev)
        assertEquals("72", abrev!!.articles.first().numero)

        val equiv = searchInAppOrder("constitución obligaciones del ciudadano")
        assertNotNull(equiv)
        assertEquals("73", equiv!!.articles.first().numero)
    }

    @Test
    fun codigoElectoralHoraDeCierreStillReturnsOnlyArt198() {
        val result = searchInAppOrder("código electoral hora de cierre")
        assertNotNull(result)
        assertEquals(CodeMatchKind.TOPIC, result!!.kind)
        assertEquals(1, result.articles.size)
        assertEquals("198", result.articles[0].numero)
        assertEquals("codigo_electoral_decreto_413", result.articles[0].pdfDocId)
    }

    @Test
    fun normalQueryWithoutLawMentionReturnsNullFromBothLawSearches() {
        val result = searchInAppOrder("cómo se instala la mesa")
        assertNull("No debe activar Código Electoral ni Constitución si no se mencionan", result)
    }
}
