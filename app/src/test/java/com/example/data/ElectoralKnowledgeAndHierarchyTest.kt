package com.example.data

import com.example.data.rag.*
import org.junit.Assert.*
import org.junit.Test

/**
 * Suite de pruebas unitarias para el Sistema de Conocimiento RAG y Jerarquía Normativa Oficial.
 *
 * Simula consultas de usuario sobre los 12 documentos de la Biblioteca Electoral salvadoreña
 * y valida que el motor responda siempre incluyendo la referencia documental oficial correspondiente,
 * cite los artículos normativos y filtre consultas fuera de contexto electoral.
 */
class ElectoralKnowledgeAndHierarchyTest {

    // =========================================================================
    // 1. SIMULACIÓN DE CONSULTAS SOBRE LOS 12 DOCUMENTOS OFICIALES
    // =========================================================================

    @Test
    fun testDoc1_CodigoElectoral_ReferenciaObligatoria() {
        val query = "¿Cuál es el horario de votación y apertura de las JRV según el Art. 190 del Código Electoral?"
        val analyzed = ElectoralQueryAnalyzer.analyze(query)
        val chunks = ElectoralHybridRetriever.retrieve(analyzed, preferredDocumentId = "codigo_electoral_decreto_413", topK = 3)
        val answer = ElectoralRAGEngine.buildLocalGroundedAnswer(analyzed, chunks)
        val validated = ElectoralResponseValidator.validateAndFormat(query, analyzed, answer, chunks)

        assertTrue("Debe citar el Código Electoral o Decreto 413", 
            validated.contains("Código Electoral", ignoreCase = true) || validated.contains("413"))
        assertTrue("Debe citar el artículo respectivo", 
            ElectoralResponseValidator.hasArticleOrSectionCitation(validated))
        assertTrue("Debe incluir la advertencia orientativa oficial", 
            validated.contains("Esta respuesta es únicamente orientativa", ignoreCase = true))
    }

    @Test
    fun testDoc2_ConstitucionRepublica_DerechosPoliticos() {
        val query = "¿Cuáles son los derechos políticos y el derecho al sufragio según el Art. 72 de la Constitución de la República?"
        val analyzed = ElectoralQueryAnalyzer.analyze(query)
        val chunks = ElectoralHybridRetriever.retrieve(analyzed, preferredDocumentId = "constitucion_republica_1983", topK = 3)
        val answer = ElectoralRAGEngine.buildLocalGroundedAnswer(analyzed, chunks)
        val validated = ElectoralResponseValidator.validateAndFormat(query, analyzed, answer, chunks)

        assertTrue("Debe citar la Constitución de la República", 
            validated.contains("Constitución", ignoreCase = true))
        assertTrue("Debe incluir referencia al Art. 72 u orden constitucional", 
            validated.contains("72") || validated.contains("Art.", ignoreCase = true))
    }

    @Test
    fun testDoc3_LeyPartidosPoliticos_DemocraciaInternaYVigilantes() {
        val query = "¿Qué derechos y prohibiciones tienen los vigilantes de partidos políticos en la JRV según la Ley de Partidos Políticos?"
        val analyzed = ElectoralQueryAnalyzer.analyze(query)
        val chunks = ElectoralHybridRetriever.retrieve(analyzed, preferredDocumentId = "ley_de_partidos_politicos", topK = 3)
        val answer = ElectoralRAGEngine.buildLocalGroundedAnswer(analyzed, chunks)
        val validated = ElectoralResponseValidator.validateAndFormat(query, analyzed, answer, chunks)

        assertTrue("Debe citar la Ley de Partidos Políticos", 
            validated.contains("Partidos Políticos", ignoreCase = true) || validated.contains("307"))
        assertTrue("Debe contener las prohibiciones de no manipular papeletas", 
            validated.contains("papeletas", ignoreCase = true) || validated.contains("vigilantes", ignoreCase = true))
    }

    @Test
    fun testDoc4_LeyAccesoInformacionPublica_TransparenciaPartidaria() {
        val query = "¿Están los partidos políticos y el TSE obligados a rendir cuentas según la Ley de Acceso a la Información Pública LAIP?"
        val analyzed = ElectoralQueryAnalyzer.analyze(query)
        val chunks = ElectoralHybridRetriever.retrieve(analyzed, preferredDocumentId = "ley_acceso_informacion_publica", topK = 3)
        val answer = ElectoralRAGEngine.buildLocalGroundedAnswer(analyzed, chunks)
        val validated = ElectoralResponseValidator.validateAndFormat(query, analyzed, answer, chunks)

        assertTrue("Debe citar la Ley de Acceso a la Información Pública o LAIP", 
            validated.contains("Información Pública", ignoreCase = true) || validated.contains("LAIP", ignoreCase = true))
    }

    @Test
    fun testDoc5_DisposicionesCandidaturasNoPartidarias_Requisitos() {
        val query = "¿Cuáles son los requisitos de firmas para inscribir una candidatura no partidaria independiente?"
        val analyzed = ElectoralQueryAnalyzer.analyze(query)
        val chunks = ElectoralHybridRetriever.retrieve(analyzed, preferredDocumentId = "disposiciones_candidaturas_no_partidarias", topK = 3)
        val answer = ElectoralRAGEngine.buildLocalGroundedAnswer(analyzed, chunks)
        val validated = ElectoralResponseValidator.validateAndFormat(query, analyzed, answer, chunks)

        assertTrue("Debe citar las Candidaturas No Partidarias", 
            validated.contains("Candidaturas No Partidarias", ignoreCase = true) || validated.contains("no partidarias", ignoreCase = true))
    }

    @Test
    fun testDoc6_ReglamentoObservacionElectoral_Deberes() {
        val query = "¿Cuáles son los deberes de imparcialidad y neutralidad de los observadores electorales nacionales e internacionales?"
        val analyzed = ElectoralQueryAnalyzer.analyze(query)
        val chunks = ElectoralHybridRetriever.retrieve(analyzed, preferredDocumentId = "reglamento_observacion_electoral", topK = 3)
        val answer = ElectoralRAGEngine.buildLocalGroundedAnswer(analyzed, chunks)
        val validated = ElectoralResponseValidator.validateAndFormat(query, analyzed, answer, chunks)

        assertTrue("Debe citar el Reglamento de Observación Electoral", 
            validated.contains("Observación Electoral", ignoreCase = true) || validated.contains("Observadores", ignoreCase = true))
    }

    @Test
    fun testDoc7_CodigoPenal_DelitosElectoralesYFraude() {
        val query = "¿Qué pena tiene el delito electoral de fraude o compra de votos según el Art. 295 del Código Penal de El Salvador?"
        val analyzed = ElectoralQueryAnalyzer.analyze(query)
        val chunks = ElectoralHybridRetriever.retrieve(analyzed, preferredDocumentId = "codigo_penal_delitos_electorales", topK = 3)
        val answer = ElectoralRAGEngine.buildLocalGroundedAnswer(analyzed, chunks)
        val validated = ElectoralResponseValidator.validateAndFormat(query, analyzed, answer, chunks)

        assertTrue("Debe citar el Código Penal o Delitos Electorales", 
            validated.contains("Código Penal", ignoreCase = true) || validated.contains("Delitos Electorales", ignoreCase = true))
        assertTrue("Debe citar el Art. 295", 
            validated.contains("295"))
    }

    @Test
    fun testDoc8_LeyReestructuracionMunicipal_44Municipios() {
        val query = "¿Cómo se organiza El Salvador en 44 municipios y 262 distritos según el Decreto 763 de Reestructuración Municipal?"
        val analyzed = ElectoralQueryAnalyzer.analyze(query)
        val chunks = ElectoralHybridRetriever.retrieve(analyzed, preferredDocumentId = "ley_reestructuracion_municipal_763", topK = 3)
        val answer = ElectoralRAGEngine.buildLocalGroundedAnswer(analyzed, chunks)
        val validated = ElectoralResponseValidator.validateAndFormat(query, analyzed, answer, chunks)

        assertTrue("Debe citar la Ley de Reestructuración Municipal o Decreto 763", 
            validated.contains("Reestructuración Municipal", ignoreCase = true) || validated.contains("763"))
    }

    @Test
    fun testDoc9_LeySufragioExtranjero_VotoElectronico() {
        val query = "¿Cómo se emite el voto electrónico por internet en el extranjero según la Ley Especial Decreto 542?"
        val analyzed = ElectoralQueryAnalyzer.analyze(query)
        val chunks = ElectoralHybridRetriever.retrieve(analyzed, preferredDocumentId = "ley_sufragio_extranjero_542", topK = 3)
        val answer = ElectoralRAGEngine.buildLocalGroundedAnswer(analyzed, chunks)
        val validated = ElectoralResponseValidator.validateAndFormat(query, analyzed, answer, chunks)

        assertTrue("Debe citar el Sufragio en el Extranjero o Decreto 542", 
            validated.contains("Extranjero", ignoreCase = true) || validated.contains("542"))
    }

    @Test
    fun testDoc10_CicloElectoralSalvadoreno_Fases() {
        val query = "¿Cuáles son las etapas pre-electoral, electoral y post-electoral del Ciclo Electoral salvadoreño?"
        val analyzed = ElectoralQueryAnalyzer.analyze(query)
        val chunks = ElectoralHybridRetriever.retrieve(analyzed, preferredDocumentId = "ciclo_electoral_salvadoreno", topK = 3)
        val answer = ElectoralRAGEngine.buildLocalGroundedAnswer(analyzed, chunks)
        val validated = ElectoralResponseValidator.validateAndFormat(query, analyzed, answer, chunks)

        assertTrue("Debe citar el Ciclo Electoral Salvadoreño", 
            validated.contains("Ciclo Electoral", ignoreCase = true))
    }

    @Test
    fun testDoc11_AcuerdoLegislativo_MayoriasYEscrutinio() {
        val query = "¿Cómo se calcula la mayoría absoluta para la elección presidencial en el Acuerdo Legislativo?"
        val analyzed = ElectoralQueryAnalyzer.analyze(query)
        val chunks = ElectoralHybridRetriever.retrieve(analyzed, preferredDocumentId = "acuerdo_legislativo_reforma_electoral", topK = 3)
        val answer = ElectoralRAGEngine.buildLocalGroundedAnswer(analyzed, chunks)
        val validated = ElectoralResponseValidator.validateAndFormat(query, analyzed, answer, chunks)

        assertTrue("Debe citar el Acuerdo Legislativo o Reforma Electoral", 
            validated.contains("Acuerdo Legislativo", ignoreCase = true) || validated.contains("Reforma", ignoreCase = true))
    }

    @Test
    fun testDoc12_InstructivoOficialJRV_InstalacionMesa() {
        val query = "¿A qué hora deben presentarse los miembros de JRV para la instalación de la mesa a las 6:00 AM según el instructivo del TSE?"
        val analyzed = ElectoralQueryAnalyzer.analyze(query)
        val chunks = ElectoralHybridRetriever.retrieve(analyzed, preferredDocumentId = "instructivo_jrv_tse", topK = 3)
        val answer = ElectoralRAGEngine.buildLocalGroundedAnswer(analyzed, chunks)
        val validated = ElectoralResponseValidator.validateAndFormat(query, analyzed, answer, chunks)

        assertTrue("Debe citar el Instructivo Oficial para JRV o TSE", 
            validated.contains("Instructivo", ignoreCase = true) || validated.contains("TSE", ignoreCase = true) || validated.contains("JRV", ignoreCase = true))
    }

    // =========================================================================
    // 2. VALIDACIÓN DE PREGUNTAS FUERA DE CONTEXTO (OUT-OF-SCOPE)
    // =========================================================================

    @Test
    fun testOutOfScopeQuery_RecetaDeCocina_RechazoCortés() {
        val query = "¿Cómo cocinar pupusas revueltas tradicionales salvadoreñas con masa de maíz?"
        val analyzed = ElectoralQueryAnalyzer.analyze(query)
        assertEquals("El intent debe ser OUT_OF_SCOPE", QueryIntentType.OUT_OF_SCOPE, analyzed.intent)

        val answer = ElectoralRAGEngine.buildLocalGroundedAnswer(analyzed, emptyList())
        val validated = ElectoralResponseValidator.validateAndFormat(query, analyzed, answer, emptyList())

        assertTrue("Debe indicar amablemente que no atiende temas no electorales", 
            validated.contains("No dispongo de información sobre temas no electorales", ignoreCase = true) ||
            validated.contains("se limita exclusivamente a responder consultas sobre el sistema electoral", ignoreCase = true))
    }

    @Test
    fun testOutOfScopeQuery_Futbol_RechazoCortés() {
        val query = "¿Quién ganó el último mundial de fútbol y cuántos goles anotó Messi?"
        val analyzed = ElectoralQueryAnalyzer.analyze(query)
        assertEquals("El intent debe ser OUT_OF_SCOPE", QueryIntentType.OUT_OF_SCOPE, analyzed.intent)

        val answer = ElectoralRAGEngine.buildLocalGroundedAnswer(analyzed, emptyList())
        val validated = ElectoralResponseValidator.validateAndFormat(query, analyzed, answer, emptyList())

        assertTrue("Debe rechazar la consulta deportiva", 
            validated.contains("No dispongo de información sobre temas no electorales", ignoreCase = true))
    }

    // =========================================================================
    // 3. VALIDACIÓN DE RESPUESTAS SIN FUENTE DOCUMENTADA (FALLBACK FORZADO)
    // =========================================================================

    @Test
    fun testValidationLayer_ForcedFallbackWhenNoDocumentedSource() {
        val query = "Consulta sobre un procedimiento inventado sin base legal"
        val analyzed = AnalyzedQuery(
            originalQuery = query,
            normalizedQuery = query.lowercase(),
            intent = QueryIntentType.GENERAL_ELECTORAL,
            targetArticles = emptyList(),
            targetEntities = emptyList(),
            subQueries = listOf(query),
            keyTerms = listOf("procedimiento")
        )

        // Respuesta ficticia sin citas oficiales ni artículos
        val ungroundedRawAnswer = "Debe presentarse a las 10 de la mañana y pedir un formulario genérico al encargado."
        val validated = ElectoralResponseValidator.validateAndFormat(query, analyzed, ungroundedRawAnswer, emptyList())

        assertTrue("Debe forzar la respuesta de no disponer de información oficial documentada", 
            validated.contains("No se dispone de información oficial documentada", ignoreCase = true))
    }
}
