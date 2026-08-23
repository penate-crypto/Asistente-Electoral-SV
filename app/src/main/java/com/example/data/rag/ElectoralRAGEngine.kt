package com.example.data.rag

import com.example.data.api.Content
import com.example.data.api.GenerateContentRequest
import com.example.data.api.GenerationConfig
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

        // Conversation History Context
        val historyBuilder = StringBuilder()
        if (conversationHistory.isNotEmpty()) {
            historyBuilder.append("\n=== HISTORIAL RECIENTE (CONTEXTO DE CONVERSACIÓN) ===\n")
            conversationHistory.takeLast(2).forEach { (q, a) ->
                historyBuilder.append("Usuario: $q\n")
                val shortAnswer = a.lines().take(2).joinToString(" ")
                historyBuilder.append("Capacitador: $shortAnswer\n")
            }
            historyBuilder.append("====================================================\n\n")
        }

        val promptContent = """
            $contextBuilder
            $historyBuilder
            CONSULTA DEL USUARIO / INTEGRANTE DE MESA ELECTORAL:
            "$userQuery"

            METADATOS DE ANÁLISIS:
            • Intención clasificada: ${analyzedQuery.intent}
            • Artículos identificados: ${analyzedQuery.targetArticles.joinToString(", ").ifEmpty { "Normativa electoral general" }}
            • Organismos / Ámbito: ${analyzedQuery.targetEntities.joinToString(", ").ifEmpty { "Electoral El Salvador" }}

            INSTRUCCIONES CLAVE PARA EL CAPACITADOR ELECTORAL VIRTUAL:
            1. FUNDAMENTACIÓN ESTRICTA EN LA BIBLIOTECA: Basa tu respuesta de forma prioritaria en los fragmentos documentales de la biblioteca electoral salvadoreña proporcionados arriba.
            2. CERO ALUCINACIONES: Si un dato o procedimiento no existe en la normativa oficial salvadoreña, decláralo con honestidad técnica («No se encontró suficiente información en los documentos disponibles para confirmar esta respuesta.»). No inventes plazos, sanciones ni artículos.
            3. NO MEZCLAR INFORMACIÓN NI COMPETENCIAS:
               - Distingue con exactitud las funciones de la JRV (mesa receptora), JEM (municipal), JED (departamental) y TSE (máxima autoridad).
               - No confundas las fases del evento (instalación 6:00 am, votación 7:00 am - 5:00 pm, cierre y escrutinio preliminar de mesa).
               - No confundas la elección nacional presencial con el voto en el extranjero ni las distintas elecciones (Presidencial, Diputados, Concejos Municipales, PARLACEN).
            4. FORMATO OBLIGATORIO:
               📌 **Respuesta:** (Directa y concisa)
               📝 **Explicación:** (Procedimiento pedagógico y secuencial)
               ⚖️ **Fundamento:** (Artículos y leyes salvadoreñas aplicables)
               📚 **Fuentes consultadas:** (Documentos oficiales citados)
            5. ADVERTENCIA ORIENTATIVA: Concluye siempre con "Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente."
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = promptContent)))
            ),
            generationConfig = GenerationConfig(
                temperature = 0.2f,
                topP = 0.95f
            ),
            systemInstruction = Content(
                parts = listOf(Part(text = systemInstruction))
            )
        )

        return Pair(promptContent, request)
    }

    fun buildLocalGroundedAnswer(
        analyzedQuery: AnalyzedQuery,
        retrievedChunks: List<ScoredChunk>
    ): String {
        if (analyzedQuery.intent == QueryIntentType.OUT_OF_SCOPE) {
            return """
                Como Asistente y Capacitador Electoral Virtual de El Salvador, mi función se limita exclusivamente a resolver consultas sobre el sistema electoral, organismos electorales (TSE, JED, JEM, JRV), normativa jurídica y procedimientos de votación en El Salvador. No dispongo de información sobre temas no electorales.

                Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente.
            """.trimIndent()
        }

        if (retrievedChunks.isEmpty()) {
            return """
                No encontré suficiente información en los documentos disponibles para confirmar esta respuesta.

                📌 **Respuesta:**
                No se localizó una disposición específica en la biblioteca electoral procesada para responder con certeza documental a este requerimiento.

                📝 **Explicación:**
                Para resolver cualquier contingencia operativa no prevista durante la jornada electoral, consulte la versión física de los instructivos oficiales del TSE o acuda de inmediato al Jefe de Centro de Votación o al Delegado de la Junta Electoral Municipal (JEM).

                ⚖️ **Fundamento:**
                Código Electoral de El Salvador y directrices del Tribunal Supremo Electoral (TSE).

                📚 **Fuentes consultadas:**
                Biblioteca Electoral Oficial de El Salvador.

                Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente.
            """.trimIndent()
        }

        val primary = retrievedChunks.first().chunk
        val secondary = retrievedChunks.getOrNull(1)?.chunk

        val docName = primary.documentTitle
        val artRef = primary.articleRef ?: primary.sectionTitle

        return when (analyzedQuery.intent) {
            QueryIntentType.ARTICLE_LOOKUP -> {
                """
                📌 **Respuesta:**
                El ${primary.articleRef ?: primary.sectionTitle} de $docName establece la regulación normativa aplicable a ${primary.sectionTitle}.

                📝 **Explicación:**
                ${primary.content}
                ${if (secondary != null && secondary.documentId == primary.documentId) "\n" + secondary.content else ""}

                ⚖️ **Fundamento:**
                • ${primary.articleRef ?: primary.sectionTitle} ($docName, Pág. ${primary.pageNumber}).

                📚 **Fuentes consultadas:**
                • $docName
                ${if (secondary != null && secondary.documentTitle != docName) "• " + secondary.documentTitle else ""}

                Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente.
                """.trimIndent()
            }
            QueryIntentType.INCIDENT_SECURITY, QueryIntentType.PROHIBITION, QueryIntentType.EXCEPTION_CONTINGENCY -> {
                """
                📌 **Respuesta:**
                Ante esta situación en la Junta Receptora de Votos (JRV), se debe actuar bajo la dirección del Presidente de la mesa aplicando el protocolo oficial del TSE, garantizando el orden y la continuidad del sufragio.

                📝 **Explicación:**
                **Procedimiento de Actuación Oficial:**
                1. **Evaluación de la Situación:** El Presidente de la JRV, como máxima autoridad del recinto de mesa, toma conocimiento directo del hecho.
                2. **Decisión Colegiada:** La JRV delibera y resuelve por mayoría simple conforme a las directrices de la ley e instructivo oficial.
                3. **Auxilio Institucional:** En caso de alteración del orden público, presuntos delitos o presencia de armas, el Presidente solicitará inmediatamente el auxilio de la Policía Nacional Civil (PNC) destacada en el centro o del Jefe de Centro del TSE.
                4. **Registro en Acta:** El Secretario de la JRV asienta el hecho de forma circunstanciada en el Acta de Incidentes.
                5. **Garantía del Sufragio:** Se reanuda la votación garantizando que todo ciudadano habilitado con DUI vigente ejerza su derecho libremente.

                ⚖️ **Fundamento:**
                • $artRef — $docName.
                • Disposición aplicable: ${primary.content}

                📚 **Fuentes consultadas:**
                • $docName
                • Instructivo Oficial para Juntas Receptoras de Votos (TSE)
                ${if (secondary != null && secondary.documentTitle != docName) "• " + secondary.documentTitle else ""}

                Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente.
                """.trimIndent()
            }
            else -> {
                """
                📌 **Respuesta:**
                De acuerdo con la normativa electoral salvadoreña, ${primary.sectionTitle.lowercase()} se rige por lo dispuesto en $docName.

                📝 **Explicación:**
                ${primary.content}
                ${if (secondary != null) "\n\n**Disposición complementaria:**\n" + secondary.content else ""}

                ⚖️ **Fundamento:**
                • ${primary.articleRef ?: primary.sectionTitle} ($docName, Pág. ${primary.pageNumber}).

                📚 **Fuentes consultadas:**
                • $docName
                ${if (secondary != null && secondary.documentTitle != docName) "• " + secondary.documentTitle else ""}

                Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente.
                """.trimIndent()
            }
        }
    }
}



