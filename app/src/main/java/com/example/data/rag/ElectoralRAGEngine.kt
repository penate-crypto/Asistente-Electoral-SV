package com.example.data.rag

import com.example.data.api.Content
import com.example.data.api.GenerateContentRequest
import com.example.data.api.GenerationConfig
import com.example.data.api.GoogleSearchTool
import com.example.data.api.Part

object ElectoralRAGEngine {

    fun buildRAGPrompt(
        userQuery: String,
        retrievedChunks: List<ScoredChunk>,
        analyzedQuery: AnalyzedQuery,
        conversationHistory: List<Pair<String, String>> = emptyList()
    ): Pair<String, GenerateContentRequest> {
        val systemInstruction = """
            ================================================================================
            SISTEMA OFICIAL DE INTELIGENCIA ARTIFICIAL: CAPACITADOR ELECTORAL VIRTUAL DE EL SALVADOR
            ROL EXCLUSIVO: CAPACITADOR ELECTORAL VIRTUAL OFICIAL (REPÚBLICA DE EL SALVADOR)
            ================================================================================

            Eres el CAPACITADOR ELECTORAL VIRTUAL OFICIAL DE LA REPÚBLICA DE EL SALVADOR.
            Tu función exclusiva es capacitar, orientar jurídicamente y guiar en procedimientos electorales a integrantes de mesa (JRV), organismos electorales temporales (JEM, JED), delegados, observadores, fiscales y ciudadanía, con apego riguroso al principio de legalidad electoral salvadoreña.

            ================================================================================
            1. PRIORIZACIÓN ESTRICTA DE LA BIBLIOTECA DOCUMENTAL LOCAL
            ================================================================================
            Debes fundamentar OBLIGATORIA Y PRIORITARIAMENTE todas tus respuestas en la Biblioteca Documental Electoral oficial de El Salvador que te es proporcionada en el contexto:
            1. Constitución de la República de El Salvador (1983) (Arts. 72-89, 208, 209).
            2. Código Electoral de la República de El Salvador (Decreto Legislativo No. 413 y sus reformas).
            3. Ley de Partidos Políticos (Decreto Legislativo No. 307).
            4. Ley Especial para el Ejercicio del Sufragio en el Extranjero (Decreto No. 542 - Voto electrónico remoto y presencial).
            5. Ley Especial para la Reestructuración Municipal (Decreto No. 763 - 44 Municipios y 262 Distritos).
            6. Ley de Acceso a la Información Pública - LAIP (Decreto No. 534).
            7. Ley Especial Integral para una Vida Libre de Violencia para las Mujeres - LEIV (Art. 55 Violencia Política).
            8. Código Penal de El Salvador (Delitos Electorales: Arts. 295 al 302).
            9. Instructivo Oficial para Juntas Receptoras de Votos (JRV) del Tribunal Supremo Electoral (TSE).
            10. Manual de Escrutinio Preliminar y Transmisión de Resultados del TSE.
            11. Reglamento General para la Observación Electoral Nacional e Internacional.
            12. Guía Doctrinal y Operativa del Ciclo Electoral Salvadoreño del TSE.

            ================================================================================
            2. POLÍTICA ESTRICTA DE CERO ALUCINACIONES (ZERO HALLUCINATIONS)
            ================================================================================
            • Prohibición absoluta de inventar artículos, decretos, plazos, porcentajes de votación, sanciones, facultades o trámites no existentes en la legislación salvadoreña.
            • Queda terminantemente prohibido usar leyes, normas o terminología foránea (como INE de México, CNE de Colombia, leyes de España u otros países).
            • Si la consulta trata de una situación no contemplada o no regulada en los documentos de la biblioteca, debes responder con honestidad técnica:
              «No se encontró suficiente información en los documentos disponibles para confirmar esta respuesta.»
            • Si el usuario hace preguntas ajenas a la materia electoral (deportes, recetas, entretenimiento, programación, etc.), recházalas cortésmente declarando tu rol exclusivo como Capacitador Electoral Virtual de El Salvador.

            ================================================================================
            3. CONTROL ESTRICTO CONTRA LA MEZCLA O CONFUSIÓN DE INFORMACIÓN
            ================================================================================
            Para no mezclar roles, competencias ni fases electorales, aplica rigurosamente las siguientes distinciones:

            A) DISTINCIÓN PRECISA DE ORGANISMOS Y ACTORES:
            • JRV (Junta Receptora de Votos): Máxima autoridad en la mesa el día de la elección (Presidente, Secretario, Vocales). Recibe sufragios, resuelve incidentes en mesa por mayoría y efectúa el escrutinio preliminar.
            • JEM (Junta Electoral Municipal): Organismo temporal municipal que coordina los centros de votación y entrega paquetes a las JRV en los 44 municipios / 262 distritos.
            • JED (Junta Electoral Departamental): Organismo temporal departamental que supervisa a las JEM y resuelve impugnaciones departamentales.
            • TSE (Tribunal Supremo Electoral): Máxima autoridad electoral y jurisdiccional del país a nivel permanente y nacional.
            • PNC (Policía Nacional Civil): Seguridad y custodia del orden público exterior y paquetes electorales. Actúa en el centro de votación SOLO a requerimiento del Presidente de la JRV o Jefe de Centro. No decide sobre votos ni actas.
            • FGR (Fiscalía General de la República / Fiscales Electorales): Vigilan la legalidad de los actos electorales.
            • Vigilantes Partidarios: Representan a sus partidos/candidaturas. Pueden observar y presentar protestas por escrito, pero NO pueden tocar papeletas, manipular material ni votar por los miembros de JRV.

            B) DISTINCIÓN DE FASES DEL DÍA DE LA ELECCIÓN:
            1. Instalación de JRV (06:00 AM): Revisión de credenciales, conteo inicial de papeletas y sellos, llenado del Acta de Instalación.
            2. Votación (07:00 AM a 05:00 PM): Verificación de DUI en padrón, entrega de papeleta firmada y sellada, marcación en padrón y entintado del dedo.
            3. Cierre de Votación (05:00 PM o hasta que vote el último ciudadano en fila a las 5:00 PM).
            4. Escrutinio Preliminar de Mesa: Apertura de urnas, clasificación de votos (válidos, nulos, abstenciones, impugnados), digitación y transmisión de actas con firmas.
            5. Empaque y Devolución: Empaque seguro del material sobrante y actas para la JEM/TSE.

            C) DISTINCIÓN DE TIPOS DE ELECCIÓN Y VOTO:
            • Distingue con precisión entre Elección Presidencial, Asamblea Legislativa, Concejos Municipales y PARLACEN.
            • Diferencia el Voto Nacional presencial del Voto en el Extranjero (Voto Electrónico Remoto por Internet y Voto Electrónico Presencial según Ley Especial No. 542).

            ================================================================================
            4. FORMATO DE RESPUESTA PEDAGÓGICO Y ESTRUCTURADO
            ================================================================================
            Organiza tus respuestas con claridad, orden y elegancia:

            📌 **Respuesta:**
            [Resolución ejecutiva, clara y directa de la consulta planteada].

            📝 **Explicación:**
            [Desarrollo pedagógico, pasos a seguir en orden secuencial, responsabilidades de cada funcionario y precisiones operativas sin mezclar etapas ni entidades].

            ⚖️ **Fundamento:**
            [Cita textual o de referencia de los artículos legales, decretos o secciones normativas salvadoreñas aplicables].

            📚 **Fuentes consultadas:**
            [Lista de los documentos oficiales utilizados de la biblioteca electoral salvadoreña].

            ================================================================================
            5. ADVERTENCIA ORIENTATIVA OBLIGATORIA
            ================================================================================
            Toda respuesta debe concluir obligatoriamente con la frase:
            "Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente."
        """.trimIndent()

        // Grounding Context from Retrieved Chunks
        val contextBuilder = StringBuilder()
        contextBuilder.append("=== FRAGMENTOS DOCUMENTALES RECUPERADOS DE LA BIBLIOTECA ELECTORAL DE EL SALVADOR ===\n\n")

        if (retrievedChunks.isEmpty()) {
            contextBuilder.append("No se recuperaron fragmentos textuales directos del repositorio para esta consulta específica.\n")
        } else {
            retrievedChunks.forEachIndexed { index, scored ->
                val chunk = scored.chunk
                contextBuilder.append("--- FRAGMENTO DOCUMENTAL #${index + 1} (Relevancia: ${scored.score.toInt()}) ---\n")
                contextBuilder.append("DOCUMENTO: ${chunk.documentTitle}\n")
                contextBuilder.append("JERARQUÍA / TIPO: ${chunk.sourceType}\n")
                contextBuilder.append("CAPÍTULO / SECCIÓN: ${chunk.chapter} • ${chunk.sectionTitle}\n")
                if (chunk.articleRef != null) {
                    contextBuilder.append("ARTÍCULO O DISPOSICIÓN: ${chunk.articleRef}\n")
                }
                contextBuilder.append("PÁGINA: ${chunk.pageNumber}\n")
                contextBuilder.append("TEXTO NORMATIVO:\n${chunk.content}\n\n")
            }
        }

        // Multi-turn conversation contents (ChatGPT style)
        val contentsList = mutableListOf<Content>()

        // Previous turns
        if (conversationHistory.isNotEmpty()) {
            conversationHistory.takeLast(4).forEach { (q, a) ->
                contentsList.add(Content(role = "user", parts = listOf(Part(text = q))))
                contentsList.add(Content(role = "model", parts = listOf(Part(text = a))))
            }
        }

        // Current turn with grounded context and query
        val currentPrompt = """
            $contextBuilder
            CONSULTA DEL USUARIO / INTEGRANTE DE MESA ELECTORAL:
            "$userQuery"

            METADATOS DE ANÁLISIS:
            • Intención clasificada: ${analyzedQuery.intent}
            • Artículos identificados: ${analyzedQuery.targetArticles.joinToString(", ").ifEmpty { "Normativa electoral general" }}
            • Organismos / Ámbito: ${analyzedQuery.targetEntities.joinToString(", ").ifEmpty { "Electoral El Salvador" }}

            INSTRUCCIONES CLAVE PARA EL CAPACITADOR ELECTORAL VIRTUAL:
            1. FUNDAMENTACIÓN RIGUROSA: Basa tu respuesta de forma prioritaria en los fragmentos documentales de la biblioteca electoral salvadoreña proporcionados arriba y en información oficial verificada.
            2. CERO ALUCINACIONES: Si un dato o procedimiento no existe en la normativa oficial salvadoreña, decláralo con honestidad técnica («No se encontró suficiente información en los documentos disponibles para confirmar esta respuesta.»). No inventes plazos, sanciones ni artículos.
            3. NO MEZCLAR INFORMACIÓN NI COMPETENCIAS:
               - Distingue con exactitud las funciones de la JRV (mesa receptora), JEM (municipal), JED (departamental) y TSE (máxima autoridad).
               - No confundas las fases del evento (instalación 6:00 am, votación 7:00 am - 5:00 pm, cierre y escrutinio preliminar de mesa).
               - No confundas la elección nacional presencial con el voto en el extranjero ni las distintas elecciones (Presidencial, Diputados, Concejos Municipales, PARLACEN).
            4. FORMATO OBLIGATORIO:
               📌 **Respuesta:** (Directa, fluida y precisa)
               📝 **Explicación:** (Procedimiento pedagógico y secuencial paso a paso)
               ⚖️ **Fundamento:** (Artículos y leyes salvadoreñas aplicables)
               📚 **Fuentes consultadas:** (Documentos oficiales citados)
            5. ADVERTENCIA ORIENTATIVA: Concluye siempre con "Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente."
        """.trimIndent()

        contentsList.add(Content(role = "user", parts = listOf(Part(text = currentPrompt))))

        val request = GenerateContentRequest(
            contents = contentsList,
            generationConfig = GenerationConfig(
                temperature = 0.35f,
                topP = 0.95f,
                maxOutputTokens = 4096
            ),
            systemInstruction = Content(
                parts = listOf(Part(text = systemInstruction))
            ),
            tools = listOf(GoogleSearchTool())
        )

        return Pair(currentPrompt, request)
    }

    fun buildLocalGroundedAnswer(
        analyzedQuery: AnalyzedQuery,
        retrievedChunks: List<ScoredChunk>
    ): String {
        if (analyzedQuery.intent == QueryIntentType.OUT_OF_SCOPE) {
            return """
                📌 **Respuesta:**
                Como Asistente y Capacitador Electoral Virtual de El Salvador, mi función se concentra exclusivamente en resolver consultas sobre el sistema electoral salvadoreño, organismos electorales (TSE, JED, JEM, JRV), normativa jurídica (Código Electoral, Constitución, instructivos) y procedimientos de votación y escrutinio en El Salvador.

                📝 **Explicación:**
                Para realizar una consulta efectiva, pregunte sobre:
                • Procedimientos de votación, apertura (6:00 AM) o cierre de mesas (5:00 PM).
                • Requisitos de identificación del elector con DUI.
                • Funciones de los miembros de la JRV, vigilantes de partidos y jefes de centro.
                • Escrutinio preliminar, clasificación de votos (válidos, nulos, impugnados, abstenciones).
                • Delitos electorales y régimen de sanciones según el Código Penal y Código Electoral.

                ⚖️ **Fundamento:**
                Constitución de la República de El Salvador y Código Electoral (Decreto Legislativo No. 413).

                📚 **Fuentes consultadas:**
                Biblioteca Electoral Oficial de El Salvador.

                Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente.
            """.trimIndent()
        }

        if (retrievedChunks.isEmpty()) {
            return """
                📌 **Respuesta:**
                No se encontró suficiente información en los documentos disponibles para confirmar una respuesta taxativa sobre este punto específico.

                📝 **Explicación:**
                En caso de presentarse una duda procedimental u operativa no prevista expresamente en el recinto de votación:
                1. El Presidente de la JRV somete el caso a resolución colegiada con los miembros de mesa.
                2. Si persiste la duda, se consulta inmediatamente al Delegado Municipal (JEM) o al Jefe de Centro del TSE asignado al recinto.
                3. Todo acuerdo y resolución debe asentarse fielmente en el Acta de Incidentes por el Secretario de la mesa.

                ⚖️ **Fundamento:**
                Código Electoral de El Salvador e Instructivo Oficial para Juntas Receptoras de Votos (TSE).

                📚 **Fuentes consultadas:**
                Biblioteca Electoral Oficial de El Salvador.

                Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente.
            """.trimIndent()
        }

        val primary = retrievedChunks.first().chunk
        val docName = primary.documentTitle
        val artRef = primary.articleRef ?: primary.sectionTitle

        // Build a cohesive explanation synthesizing all retrieved chunks
        val explanationBuilder = StringBuilder()
        explanationBuilder.append(primary.content)

        val additionalChunks = retrievedChunks.drop(1)
        if (additionalChunks.isNotEmpty()) {
            explanationBuilder.append("\n\n**Disposiciones y lineamientos complementarios:**\n")
            additionalChunks.forEach { scored ->
                val chunk = scored.chunk
                val ref = chunk.articleRef ?: chunk.sectionTitle
                explanationBuilder.append("• **$ref (${chunk.documentTitle}):** ${chunk.content}\n\n")
            }
        }

        val allSources = retrievedChunks.map { it.chunk.documentTitle }.distinct()
        val allArticles = retrievedChunks.mapNotNull { it.chunk.articleRef ?: it.chunk.sectionTitle }.distinct()

        val answerSummary = when (analyzedQuery.intent) {
            QueryIntentType.ARTICLE_LOOKUP -> "De acuerdo con el ${artRef} de $docName, la normativa salvadoreña regula de forma expresa los aspectos relativos a ${primary.sectionTitle.lowercase()}."
            QueryIntentType.INCIDENT_SECURITY -> "Ante situaciones de seguridad o alteración del orden público en el centro de votación, la autoridad exclusiva del recinto recae en el Presidente de la JRV, quien ordenará el auxilio de la Policía Nacional Civil (PNC)."
            QueryIntentType.PROHIBITION -> "La legislación electoral salvadoreña tipifica y prohíbe de manera terminante cualquier conducta que vulnere la pureza, libertad y secreto del sufragio."
            QueryIntentType.EXCEPTION_CONTINGENCY -> "Ante cualquier contingencia en mesa electoral, la JRV actúa como cuerpo colegiado por mayoría de votos, asentando todo hecho en el Acta de Incidentes."
            QueryIntentType.PROCEDURAL -> "El procedimiento electoral y de recepción del sufragio se ejecuta de manera secuencial y obligatoria por los miembros de la JRV conforme a la normativa oficial del TSE."
            QueryIntentType.VOTING_ASSISTANCE -> "La legislación electoral salvadoreña garantiza el voto asistido y medidas de accesibilidad e inclusión para personas adultas mayores y con discapacidad."
            QueryIntentType.DEFINITION -> "Conforme a la doctrina y normativa electoral salvadoreña, los conceptos y términos aplicables se definen según lo establecido en $docName."
            else -> "De acuerdo con la legislación salvadoreña aplicable, ${primary.sectionTitle.lowercase()} se rige taxativamente por las directrices establecidas en $docName."
        }

        return """
            📌 **Respuesta:**
            $answerSummary

            📝 **Explicación:**
            $explanationBuilder

            ⚖️ **Fundamento:**
            ${allArticles.take(4).joinToString("\n") { "• $it" }}

            📚 **Fuentes consultadas:**
            ${allSources.joinToString("\n") { "• $it" }}

            Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente.
        """.trimIndent()
    }
}



