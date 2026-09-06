package com.example.data

import com.example.data.search.engine.ElectoralSearchEngine
import com.example.data.search.model.MatchConfidenceLevel
import com.example.data.search.normalization.ElectoralNormalizer
import org.junit.Assert.*
import org.junit.Test

/**
 * Suite de validación de las directrices de la Actualización Mayor:
 * 1. Búsqueda con título idéntico o muy cercano.
 * 2. Búsqueda con lenguaje abreviado / SMS / coloquial ("istala", "q", "c", "foto").
 * 3. Búsqueda puramente factual/descriptiva (sin palabras técnicas).
 * 4. Consulta inexistente -> Sin invenciones, rechazo seguro y orientativo.
 */
class ElectoralJsonSearchEngineTest {

    @Test
    fun testNormalizer_SmsAbbreviationsAndTypos() {
        val query = "q pasa si el votante no c parece a la foto del dui"
        val normalized = ElectoralNormalizer.normalize(query)

        assertTrue("Debe contener votante", normalized.keywords.contains("votante"))
        assertTrue("Debe normalizar foto a fotografia", normalized.keywords.contains("fotografia") || normalized.tokens.contains("fotografia"))
        assertTrue("Debe contener dui", normalized.keywords.contains("dui"))
        assertTrue("Debe contener parece", normalized.keywords.contains("parece"))
    }

    @Test
    fun testNormalizer_MesaInstalacionAbbreviations() {
        val query = "como se istala la mesa y q se necesita"
        val normalized = ElectoralNormalizer.normalize(query)

        assertTrue("Debe corregir istala a instala", normalized.tokens.contains("instala"))
        assertTrue("Debe contener mesa", normalized.keywords.contains("mesa"))
        assertTrue("Debe corregir q a que", normalized.tokens.contains("que"))
    }

    @Test
    fun testPrueba1_ExactTitleMatch() {
        val query = "Instalación de la Junta Receptora de Votos y Verificación de Quórum"
        val result = ElectoralSearchEngine.search(query)

        assertEquals(MatchConfidenceLevel.HIGH, result.matchLevel)
        assertNotNull(result.topSituation)
        assertEquals("INST-001", result.topSituation?.id)
        assertTrue(result.formattedResponse.contains("Tema:", ignoreCase = true))
        assertTrue(result.formattedResponse.contains("SITUACIÓN DETECTADA"))
        assertTrue(result.formattedResponse.contains("¿QUÉ HACER?"))
    }

    @Test
    fun testPrueba2_ColloquialAndAbbreviated_FotoDui() {
        val query = "q pasa si el votante no c parece a la foto del dui"
        val result = ElectoralSearchEngine.search(query)

        assertTrue(result.matchLevel == MatchConfidenceLevel.HIGH || result.matchLevel == MatchConfidenceLevel.MEDIUM)
        assertNotNull(result.topSituation)
        assertEquals("DUI-002", result.topSituation?.id)
        assertTrue(result.formattedResponse.contains("DUI-002") || result.formattedResponse.contains("apariencia", ignoreCase = true) || result.formattedResponse.contains("fotografía", ignoreCase = true))
    }

    @Test
    fun testPrueba2_ColloquialAndAbbreviated_InstalacionMesa() {
        val query = "como se istala la mesa y q se necesita"
        val result = ElectoralSearchEngine.search(query)

        assertTrue(result.matchLevel == MatchConfidenceLevel.HIGH || result.matchLevel == MatchConfidenceLevel.MEDIUM)
        assertNotNull(result.topSituation)
        assertEquals("INST-001", result.topSituation?.id)
    }

    @Test
    fun testPrueba3_FactDescription_VigilanteConArma() {
        val query = "un vigilante de partido tiene una pistola en la mesa"
        val result = ElectoralSearchEngine.search(query)

        assertTrue(result.matchLevel == MatchConfidenceLevel.HIGH || result.matchLevel == MatchConfidenceLevel.MEDIUM)
        assertNotNull(result.topSituation)
        assertEquals("ARM-001", result.topSituation?.id)
        assertTrue(result.formattedResponse.contains("¿QUÉ NO HACER?"))
    }

    @Test
    fun testPrueba4_NonExistentQuery_NoHallucinations() {
        val nonElectoralQuery = "¿cuál es la receta tradicional para hornear un pastel de tres leches?"
        val result = ElectoralSearchEngine.search(nonElectoralQuery)

        assertEquals(MatchConfidenceLevel.LOW, result.matchLevel)
        assertNull("No debe asignar ninguna situación para preguntas no electorales", result.topSituation)
        assertTrue("Debe informar con claridad el ámbito o falta de coincidencia",
            result.formattedResponse.contains("Ámbito de Consulta", ignoreCase = true) || result.formattedResponse.contains("No se encontró", ignoreCase = true))
        assertFalse("No debe inventar leyes ni artículos electorales para pasteles",
            result.formattedResponse.contains("Art. 190 del Código Electoral"))
    }
}
