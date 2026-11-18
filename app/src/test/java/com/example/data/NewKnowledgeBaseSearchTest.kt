package com.example.data

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.search.engine.ElectoralSearchEngine
import com.example.data.search.entities.ElectoralEntityExtractor
import com.example.data.search.knowledge.ElectoralKnowledgeBase
import com.example.data.search.model.MatchConfidenceLevel
import com.example.data.search.normalization.ElectoralNormalizer
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class NewKnowledgeBaseSearchTest {

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        ElectoralKnowledgeBase.init(context)
    }

    @Test
    fun loadsAll154SituationsWithFullMetadata() {
        val all = ElectoralKnowledgeBase.getAll()
        assertEquals(154, all.size)

        val inst001 = ElectoralKnowledgeBase.findById("INST-001")
        assertNotNull(inst001)
        assertTrue(inst001!!.respuestasRelacionadas.isNotEmpty())
        assertEquals("codigo_electoral_decreto_413", inst001.respuesta.primaryPdfDocId)
        assertTrue(inst001.respuesta.autorizaciones?.quienPuede?.isNotEmpty() == true)
        assertTrue(inst001.respuesta.autorizaciones?.quienInterviene?.isNotEmpty() == true)

        val dui005 = ElectoralKnowledgeBase.findById("DUI-005")
        assertNotNull(dui005)
        assertFalse(dui005!!.respuesta.advertencia.isNullOrBlank())

        val formattedDui005 = ElectoralSearchEngine.formatStructuredResponse(dui005, MatchConfidenceLevel.HIGH)
        assertTrue(formattedDui005.contains("ADVERTENCIA"))
        assertTrue(formattedDui005.contains("Elecciones 2024"))
    }

    @Test
    fun seArmaLaMesaDoesNotMatchWeapons() {
        val query = "como se arma la mesa electoral"
        val normalized = ElectoralNormalizer.normalize(query)
        val entities = ElectoralEntityExtractor.extract(normalized.cleanedText, query)

        assertFalse("No debe extraer arma_de_fuego para 'se arma la mesa'", "arma_de_fuego" in entities.objects)

        val result = ElectoralSearchEngine.search(query)
        assertNotNull(result.topSituation)
        assertFalse("No debe responder con situación de armas", result.topSituation!!.id.startsWith("ARM-"))
        assertEquals("INST-001", result.topSituation!!.id)
    }

    @Test
    fun papeletaDoesNotTriggerPapelFunctionsIntent() {
        val query = "que pasa si el votante rompe la papeleta"
        val normalized = ElectoralNormalizer.normalize(query)
        val entities = ElectoralEntityExtractor.extract(normalized.cleanedText, query)

        assertNotEquals("No debe clasificar 'papeleta' como 'funciones_atribuciones' por contener 'papel'", "funciones_atribuciones", entities.intent)
    }

    @Test
    fun musicaDePartidoAfueraDeEscuelaIsNotRejectedAsOffTopic() {
        val query = "musica de un partido afuera de la escuela"
        val result = ElectoralSearchEngine.search(query)

        assertFalse("No debe rechazar consulta electoral por la palabra música", result.isOffTopic)
        assertNotNull(result.topSituation)
        assertEquals("SEG-001", result.topSituation!!.id)
    }

    @Test
    fun recuentoDeVotosIsNotRejectedBecauseOfCuento() {
        val query = "como se hace el recuento de votos"
        val result = ElectoralSearchEngine.search(query)

        assertFalse(result.isOffTopic)
        assertNotNull(result.topSituation)
    }

    @Test
    fun coreEngineQueriesMatchExpectedIds() {
        val cases = listOf(
            "Instalación de la Junta Receptora de Votos y Verificación de Quórum" to "INST-001",
            "q pasa si el votante no c parece a la foto del dui" to "DUI-002",
            "un vigilante de partido tiene una pistola en la mesa" to "ARM-001",
            "que pasa con el cierre de votos alas 5" to "ESC-001",
            "quien puede portar armas en el centro de votacion" to "ARM-010",
            "persona que fue a votar no aparece en el padron" to "VOT-002"
        )

        for ((q, expectedId) in cases) {
            val result = ElectoralSearchEngine.search(q)
            assertNotNull("Debe encontrar resultado para: $q", result.topSituation)
            assertEquals("Consulta '$q' debe coincidir con $expectedId", expectedId, result.topSituation!!.id)
        }
    }

    @Test
    fun offTopicQueryRemainsRejected() {
        val result = ElectoralSearchEngine.search("como hacer una receta de pastel de chocolate")
        assertTrue(result.isOffTopic)
        assertNull(result.topSituation)
    }
}
