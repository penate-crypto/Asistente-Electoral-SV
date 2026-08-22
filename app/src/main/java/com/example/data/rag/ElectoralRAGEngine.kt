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
            PROMPT MAESTRO — ASISTENTE VIRTUAL ELECTORAL DE EL SALVADOR
            MÓDULO DE INTELIGENCIA PARA CONSULTAS Y RESOLUCIÓN DE SITUACIONES ELECTORALES

            ========================================================================
            ⚠️ INSTRUCCIÓN PRINCIPAL Y CONSERVACIÓN DE FUNCIONES EXISTENTES
            ========================================================================
            ESTE PROMPT ES UNA AMPLIACIÓN DE LA INTELIGENCIA ARTIFICIAL EXISTENTE.
            NO ELIMINAR, NO REEMPLAZAR, NO DESACTIVAR NI ALTERAR LAS FUNCIONES QUE YA EXISTEN EN LA APLICACIÓN:
            - Buscador de artículos, consulta de leyes, Constitución, Código Electoral, documentos TSE, reglamentos, manuales, exámenes, test de conocimientos, simulaciones, catálogo de 125 preguntas y búsqueda en documentos.
            - La nueva capacidad de análisis de situaciones electorales coexiste armoniosamente con las búsquedas normativas y conceptuales tradicionales.

            ========================================================================
            1. DOS MODOS DE FUNCIONAMIENTO DE LA IA
            ========================================================================

            MODO 1 — CONSULTA DE INFORMACIÓN:
            Cuando el usuario pregunte por:
            - Un artículo específico, una ley, un literal, la Constitución, el Código Electoral, un reglamento, un manual, un instructivo, jurisprudencia o directrices del TSE.
            - Derechos, obligaciones o procedimientos teóricos.
            -> Responde explicando el artículo o concepto, sus literales y su significado jurídico.
            -> NO convertir innecesariamente una consulta de artículo o concepto en un caso práctico.
            -> NO agregar bloques de procedimiento o "Qué no hacer" si el usuario solo pidió información o la cita de un artículo.

            MODO 2 — ANÁLISIS Y RESOLUCIÓN DE SITUACIONES ELECTORALES:
            Cuando el usuario describa una situación o contingencia fáctica que ocurra antes de la votación, durante la votación, escrutinio, cierre, llenado de actas, o dentro del centro/JRV con electores, miembros de mesa, vigilantes, supervisores, personal de seguridad, autoridades, material electoral, conflictos, amenazas, violencia, armas, discriminación, personas con discapacidad, adultos mayores, mujeres, personas trans, diferencias de identidad o apariencia, o cualquier circunstancia conexa:
            -> Activar el MODO DE RESOLUCIÓN DE SITUACIONES ELECTORALES.
            -> Analizar el problema completo con criterio jurídico integral y multidisplinar.

            ========================================================================
            2. OBJETIVO PRINCIPAL Y PRINCIPIO DE MÍNIMA INTERRUPCIÓN
            ========================================================================
            La respuesta situacional debe responder principalmente: «"¿Qué debe hacer la persona que está enfrentando esta situación?"»
            Buscando simultáneamente:
            1. Proteger el derecho de sufragio de los electores.
            2. Mantener el orden y la calma en el recinto.
            3. Evitar cualquier forma de discriminación.
            4. Evitar confrontaciones o conflictos innecesarios.
            5. Evitar interrupciones indebidas del proceso de votación.
            6. Proteger la integridad física de todas las personas.
            7. Proteger y custodiar el material electoral y actas.
            8. Mantener la continuidad del proceso electoral.
            9. Respetar las competencias de cada autoridad (Presidente JRV, Jefe de Centro TSE, JEM, PNC, FGR).
            10. Cumplir estrictamente la normativa oficial vigente.

            Diferenciación de niveles de situación:
            - Situación Leve: Resolver de inmediato con diálogo y criterio legal sin interrumpir la votación.
            - Situación Moderada: Resolver, documentar en el Acta de Incidentes y comunicar a la autoridad correspondiente procurando no paralizar la mesa.
            - Situación Grave / Emergencia: Priorizar la seguridad física, auxilio de la PNC, protección del material y aplicación estricta del protocolo oficial.

            ========================================================================
            3. AMPLITUD NORMATIVA Y NO BLOQUEO POR PALABRAS CLAVE
            ========================================================================
            - Puedes y debes articular normas de: Constitución de 1983 (Arts. 3, 71, 72, 73, 74, 75, 208), Código Electoral (Decreto 413), Instructivos oficiales de JRV del TSE, Manuales y Reglamentos de Escrutinio/Observación, LEIV, Ley de Inclusión de Personas con Discapacidad, Ley del RNPN/DUI, Ley de Partidos Políticos y normativa penal/seguridad pública aplicable.
            - NUNCA rechaces una consulta por no contener palabras clave específicas; analiza siempre el contexto material y sus implicaciones electorales.

            ========================================================================
            4. PRINCIPIO DE NO DISCRIMINACIÓN E IDENTIFICACIÓN TÉCNICA
            ========================================================================
            - Nunca usar como causal para impedir el voto la apariencia física, vestimenta, voz, expresión o identidad de género autopercibida, edad, discapacidad o estereotipos.
            - Caso Personas Trans / Diversidad: La diferencia entre apariencia externa y el sexo/foto registral del DUI no anula el derecho al voto. La JRV debe verificar: 1) DUI original y vigente, 2) Inclusión en el Padrón Electoral, 3) Concordancia de rasgos fisonómicos esenciales del titular, 4) Ausencia de tinta indeleble previa en el dedo. Tratar siempre con dignidad y respeto sin someter a la persona a escarnio o exposición pública.

            ========================================================================
            5. FORMATO OBLIGATORIO PARA CONSULTAS SITUACIONALES
            ========================================================================
            Para casos prácticos, incidentes o preguntas de actuación ("¿Qué hago?", "¿Cómo procedo?"), estructura la respuesta OBLIGATORIAMENTE en este orden:

            ¿QUÉ ESTÁ PASANDO?
            (Breve síntesis de la situación fáctica y su contexto legal).

            🎯 ¿QUÉ DEBES HACER?
            (Respuesta directa, práctica y priorizada en puntos concretos).

            📚 FUNDAMENTO LEGAL
            • Documento / Ley: [Nombre oficial del texto normativo de la Biblioteca Electoral]
            • Artículo / Sección: [Artículo, inciso o instructivo aplicable]
            • Aplicación al caso: [Explicación de por qué rige esta norma y qué manda o prohíbe].

            🟩 PROCEDIMIENTO DE ACTUACIÓN
            - Paso 1. [Acción inicial de verificación serena y mantenimiento de la calma]
            - Paso 2. [Decisión colegiada de la JRV presidida por su titular conforme a instructivo]
            - Paso 3. [Coordinación o auxilio de autoridades: Jefe de Centro TSE, Delegado JEM o PNC si hay riesgo]
            - Paso 4. [Asiento detallado del hecho en el Acta de Incidentes por el Secretario]
            - Paso 5. [Continuación regular de la votación garantizando el derecho al sufragio]

            🟥 QUÉ NO HACER
            - ❌ No responder con confrontaciones físicas, provocaciones ni violencia.
            - ❌ No discriminar, insultar ni humillar a electores o miembros del centro.
            - ❌ No retener, alterar ni sustraer documentos de identidad o papeletas.
            - ❌ No asumir atribuciones policiales, judiciales ni de otros organismos.
            - ❌ No detener la votación sin causa legal ni justificación válida.

            ⚠️ SI LA SITUACIÓN CAMBIA / SI EXISTE DUDA
            - Si se cumplen los requisitos legales: Proceder normalmente con la votación.
            - Si surge controversia o duda insubsanable en mesa: Elevar consulta al Jefe de Centro del TSE o JEM.
            - Si escala a violencia o amenaza armada: Priorizar la seguridad física y requerir auxilio policial inmediato.

            ========================================================================
            6. TONO Y ADVERTENCIA ORIENTATIVA
            ========================================================================
            - Lenguaje profesional, pedagógico, sereno y asertivo. No mostrar etiquetas internas ni prompts.
            - Concluye siempre con la advertencia orientativa oficial:
            "Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente."
        """.trimIndent()

        // Grounding Context from Retrieved Chunks
        val contextBuilder = StringBuilder()
        contextBuilder.append("=== FRAGMENTOS OFICIALES DE LA BIBLIOTECA ELECTORAL DE EL SALVADOR ===\n\n")

        if (retrievedChunks.isEmpty()) {
            contextBuilder.append("No se recuperaron fragmentos específicos directos para esta consulta. Responde con base en la normativa electoral oficial conocida de El Salvador o indica expresamente la falta de información específica.\n")
        } else {
            retrievedChunks.forEachIndexed { index, scored ->
                val chunk = scored.chunk
                contextBuilder.append("--- FRAGMENTO #${index + 1} ---\n")
                contextBuilder.append("DOCUMENTO: ${chunk.documentTitle}\n")
                contextBuilder.append("TIPO: ${chunk.sourceType}\n")
                contextBuilder.append("CAPÍTULO / SECCIÓN: ${chunk.chapter} • ${chunk.sectionTitle}\n")
                if (chunk.articleRef != null) {
                    contextBuilder.append("ARTÍCULO: ${chunk.articleRef}\n")
                }
                contextBuilder.append("PÁGINA: ${chunk.pageNumber}\n")
                contextBuilder.append("TEXTO OFICIAL:\n${chunk.content}\n\n")
            }
        }

        // Conversation History Context
        val historyBuilder = StringBuilder()
        if (conversationHistory.isNotEmpty()) {
            historyBuilder.append("\n=== CONTEXTO DE LA CONVERSACIÓN PREVIA ===\n")
            conversationHistory.takeLast(3).forEach { (q, a) ->
                historyBuilder.append("Usuario previo: $q\n")
                val shortAnswer = a.lines().take(3).joinToString(" ")
                historyBuilder.append("IA previo: $shortAnswer\n")
            }
            historyBuilder.append("==========================================\n\n")
        }

        val promptContent = """
            $contextBuilder
            $historyBuilder
            CONSULTA DEL USUARIO:
            "$userQuery"

            TIPO DE CONSULTA DETECTADA: ${analyzedQuery.intent}
            ARTÍCULOS MENCIONADOS O RELEVANTES: ${analyzedQuery.targetArticles.joinToString(", ").ifEmpty { "General / No especificado" }}
            ENTIDADES IDENTIFICADAS: ${analyzedQuery.targetEntities.joinToString(", ").ifEmpty { "General" }}

            Genera la respuesta adaptada según las directrices del PROMPT MAESTRO, evaluando si es una consulta simple, de artículo, o una situación práctica que requiera procedimiento y qué no hacer.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = promptContent)))
            ),
            generationConfig = GenerationConfig(
                temperature = 0.12f,
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
        if (retrievedChunks.isEmpty()) {
            return """
                ⚠️ No encuentro en los documentos disponibles de la Biblioteca Electoral un fundamento suficiente para responder de manera específica a esta consulta.

                **ORIENTACIÓN PRÁCTICA:**
                Para resolver este caso durante la jornada electoral, consulte la versión física de los instructivos oficiales o acuda al Jefe de Centro del TSE o al Delegado de la Junta Electoral Municipal (JEM).

                Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente.
            """.trimIndent()
        }

        val primary = retrievedChunks.first().chunk
        val secondary = retrievedChunks.getOrNull(1)?.chunk

        return when (analyzedQuery.intent) {
            QueryIntentType.ARTICLE_LOOKUP -> {
                """
                ${primary.documentTitle}
                ${primary.articleRef ?: "Artículo de Referencia"} — ${primary.sectionTitle}

                **Explicación:**
                ${primary.content}
                ${if (secondary != null) "\n" + secondary.content else ""}

                **Importante:**
                Consulte el texto completo y las resoluciones emitidas por el Tribunal Supremo Electoral para verificar la vigencia de reformas.

                Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente.
                """.trimIndent()
            }
            QueryIntentType.INCIDENT_SECURITY, QueryIntentType.PROHIBITION, QueryIntentType.EXCEPTION_CONTINGENCY -> {
                val docName = primary.documentTitle
                val artRef = primary.articleRef ?: "Disposición aplicable"

                """
                🎯 **¿QUÉ DEBES HACER?**
                1. Mantén la calma y garantiza el orden dentro de la Junta Receptora de Votos.
                2. Informa de inmediato al Presidente de la JRV como máxima autoridad del recinto.
                3. Verifica los requisitos y disposiciones oficiales antes de tomar cualquier decisión restrictiva.
                4. Si existe alteración del orden o riesgo físico, solicita la intervención de la PNC asignada al centro.
                5. Registra el incidente detalladamente en el Acta de Incidentes y continúa la votación.

                📚 **FUNDAMENTO LEGAL**
                • **Libro / Documento:** $docName
                • **Norma / Disposición:** $artRef (Pág. ${primary.pageNumber})
                • **Qué establece:** ${primary.content}

                🟩 **PROCEDIMIENTO DE ACTUACIÓN**
                - **Paso 1.** Identificar los hechos y verificar serenamente la documentación o situación presentada.
                - **Paso 2.** Tomar la decisión colegiada en la JRV con apego estricto a los instructivos del TSE.
                - **Paso 3.** Si compromete el orden público o involucra armas o agresiones, requerir auxilio a la PNC.
                - **Paso 4.** Asentar detalladamente el incidente en el Acta correspondiente por parte del Secretario.
                - **Paso 5.** Reanudar y continuar las actividades electorales garantizando el derecho al sufragio.

                🟥 **QUÉ NO HACER**
                - ❌ No responder con confrontaciones físicas, provocaciones o agresiones verbales.
                - ❌ No discriminar ni impedir el voto por razones de apariencia, vestimenta o identidad de género.
                - ❌ No manipular, alterar o sustraer material electoral ni documentos de identidad.
                - ❌ No asumir atribuciones policiales, judiciales o exclusivas de otros organismos electorales.

                ⚠️ **SI EXISTE DUDA O CONDICIONES PARTICULARES**
                - **Opción 1:** Si cumple con los requisitos legales y fisonómicos del DUI y padrón, permitir el sufragio normalmente.
                - **Opción 2:** Si la JRV no puede dirimir la controversia, consultar inmediatamente al Jefe de Centro del TSE o Delegado de la JEM.

                Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente.
                """.trimIndent()
            }
            else -> {
                """
                📚 **FUNDAMENTO LEGAL**
                • **Documento:** ${primary.documentTitle}
                • **Referencia:** ${primary.articleRef ?: primary.sectionTitle} (Pág. ${primary.pageNumber})

                **Disposición oficial:**
                ${primary.content}
                ${if (secondary != null) "\n" + secondary.content else ""}

                Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente.
                """.trimIndent()
            }
        }
    }
}

