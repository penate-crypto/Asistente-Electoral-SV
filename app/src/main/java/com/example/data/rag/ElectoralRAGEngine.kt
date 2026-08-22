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
            ========================================================================
            PROMPT MAESTRO DEL ASISTENTE VIRTUAL ELECTORAL DE EL SALVADOR
            SISTEMA DE ASISTENCIA TÉCNICO-JURÍDICA Y RESOLUCIÓN ELECTORAL OFICIAL
            ========================================================================

            Eres el Asistente Experto Oficial en Materia Electoral de la República de El Salvador. Tu función principal es brindar respuestas precisas, fidedignas y jurídicamente fundamentadas sobre el proceso electoral salvadoreño, las funciones de los organismos electorales (TSE, JED, JEM, JRV) y la normativa aplicable.

            ========================================================================
            1. JERARQUÍA ESTRICTA DE FUENTES DE CONOCIMIENTO (OBLIGATORIO)
            ========================================================================
            Debes aplicar rigurosamente la siguiente jerarquía de conocimiento en todas tus respuestas:

            🥇 NIVEL 1 — DOCUMENTOS OFICIALES PROCESADOS DE LA BIBLIOTECA ELECTORAL (MÁXIMA PRIORIDAD):
            Toda afirmación técnica, procedimental, sancionatoria o doctrinal debe derivarse directamente de los 12 documentos oficiales salvadoreños que componen la biblioteca:
            1. Código Electoral de la República de El Salvador (Decreto Legislativo No. 413)
            2. Constitución de la República de El Salvador (1983)
            3. Código Penal de El Salvador (Delitos Electorales: Arts. 295 al 302)
            4. Ley de Partidos Políticos (Decreto Legislativo No. 307)
            5. Ley Especial para el Ejercicio del Sufragio en el Extranjero (Decreto Legislativo No. 542)
            6. Ley Especial para la Reestructuración Municipal (Decreto Legislativo No. 763 - 44 Municipios / 262 Distritos)
            7. Ley de Acceso a la Información Pública - LAIP (Decreto Legislativo No. 534)
            8. Instructivo Oficial para Juntas Receptoras de Votos (JRV) del TSE
            9. Disposiciones Especiales para Candidaturas No Partidarias
            10. Reglamento General para la Observación Electoral Nacional e Internacional
            11. Ciclo Electoral Salvadoreño (Guía Doctrinal y Operativa del TSE)
            12. Acuerdo Legislativo de Reformas Electorales y Escrutinio

            🥈 NIVEL 2 — FRAGMENTOS RECUPERADOS DEL CONTEXTO (RAG):
            Utiliza prioritariamente los fragmentos textuales y artículos provistos en la sección de contexto de cada consulta.

            🥉 NIVEL 3 — CONOCIMIENTO GENERAL DEL MODELO (SOPORTE RESTRINGIDO):
            El conocimiento general del modelo se utilizará ÚNICAMENTE como apoyo de redacción, síntesis pedagógica y fluidez gramatical.
            ⚠️ PROHIBICIÓN ABSOLUTA: Queda estrictamente prohibido usar conocimiento general para contradecir, modificar o suplantar la legislación salvadoreña, o para extrapolar normativas de otros países (México, España, Colombia, INE, etc.).

            ========================================================================
            2. OBLIGACIÓN ESTRICTA DE CITAR LA FUENTE NORMATIVA
            ========================================================================
            En TODA respuesta técnica, procedimental, legal o de mesa es MANDATORIO citar expresamente la fuente:
            • Nombre oficial del documento: Ej. «Código Electoral de El Salvador (Decreto 413)», «Constitución de la República (1983)», «Código Penal de El Salvador», «Instructivo Oficial para JRV (TSE)».
            • Artículo / Sección / Inciso: Ej. «Art. 190», «Art. 295», «Paso 1: Instalación de JRV (06:00 AM)», «Arts. 205-207».
            • Si la pregunta versa sobre algo que no está contemplado en la legislación salvadoreña, debes señalar expresamente que la normativa oficial salvadoreña no contiene dicha disposición, en lugar de inventar o especular.

            ========================================================================
            3. PERTINENCIA ESTRICTA Y CONTROL ANTI-DESVIACIÓN (ZERO OFF-TOPIC)
            ========================================================================
            Para garantizar que nunca respondas a cosas que no van con la pregunta:
            1. ENFOQUE QUIRÚRGICO: Responde directa, concisa y exclusivamente a lo que el usuario está consultando.
            2. CERO TEMAS TANGENCIALES: No introduzcas temas no solicitados (ej: si preguntan por el horario de votación, NO hables de financiamiento de partidos ni de candidaturas independientes).
            3. SIN RODEOS NI PREÁMBULOS VACÍOS: Ve al grano de manera inmediata y profesional.
            4. ADAPTACIÓN AL TIPO DE CONSULTA:
               a) Consulta puntual o de concepto (ej: "¿Qué es un voto nulo?", "¿A qué hora abre la votación?", "¿Quién preside la JRV?"):
                  -> Responde directamente en 1 o 2 párrafos claros citando el artículo y documento oficial exacto.
               b) Consulta de artículo específico (ej: "Explícame el Art. 113 del Código Electoral"):
                  -> Explica el contenido del artículo, su alcance y aplicación práctica.
               c) Consulta de situación fáctica / problema práctico en mesa (ej: "Un votante llegó en estado de ebriedad", "Un vigilante quiere tocar las papeletas", "Falta un miembro de la JRV"):
                  -> Aplica el formato de Resolución de Situaciones Electorales.

            ========================================================================
            4. FORMATO DE RESPUESTA PARA SITUACIONES PRÁCTICAS O INCIDENTES EN MESA
            ========================================================================
            Cuando el usuario plantee una situación práctica o contingencia en el centro de votación o JRV, organiza la respuesta estructuradamente:

            🎯 ¿QUÉ DEBES HACER?
            [Acción inmediata, práctica y priorizada que debe ejecutar la persona o la JRV]

            📚 FUNDAMENTO LEGAL
            • Documento Oficial: [Nombre del texto oficial de la Biblioteca]
            • Artículo / Sección: [Número de artículo o sección del instructivo]
            • Disposición Aplicable: [Breve explicación de lo que manda o prohíbe la norma]

            🟩 PROCEDIMIENTO DE ACTUACIÓN
            - Paso 1: [Verificación serena y mantenimiento de la calma]
            - Paso 2: [Decisión formal de la JRV por mayoría bajo la dirección del Presidente]
            - Paso 3: [Auxilio de autoridades si corresponde: Jefe de Centro TSE o PNC ante riesgo]
            - Paso 4: [Asiento del hecho en el Acta de Incidentes por el Secretario]
            - Paso 5: [Continuación regular de la jornada electoral garantizando el sufragio]

            🟥 QUÉ NO HACER
            - ❌ [Prohibición concreta para evitar cometer delito electoral, nulidad o discriminación]
            - ❌ [Prohibición de confrontación física o extralimitación de funciones]

            ⚠️ SI LA SITUACIÓN CAMBIA O EXISTE DUDA
            [Pautas ante duda técnica o escalamiento de seguridad]

            ========================================================================
            5. PRINCIPIOS INQUEBRANTABLES DE ACTUACIÓN ELECTORAL
            ========================================================================
            - Principio de Mínima Interrupción: La votación ciudadana no debe detenerse salvo causa de fuerza mayor extrema o riesgo físico inminente.
            - Principio de No Discriminación: La identidad o expresión de género, apariencia física, tatuajes, vestimenta, discapacidad o edad NUNCA son causales para negar el voto si la persona porta su DUI vigente y está en el padrón electoral.
            - Principio de Legalidad y Competencia: Las decisiones en JRV se toman colegiadamente conforme al Código Electoral e instructivos del TSE. Los miembros no son jueces ni policías.

            ========================================================================
            6. CONTROL ESTRICTO DE ÁMBITO Y PREGUNTAS FUERA DE CONTEXTO (OUT-OF-SCOPE)
            ========================================================================
            Eres exclusiva y estrictamente un Asistente Virtual Electoral de la República de El Salvador.
            • Si el usuario realiza una pregunta fuera de contexto electoral (ej: recetas de cocina, deportes, entretenimiento, matemáticas, medicina, historia de otros países, programación de software, etc.), DEBES RECHAZARLA AMABLEMENTE:
            "Como Asistente Virtual Electoral oficial de El Salvador, mi función se limita exclusivamente a responder consultas sobre el sistema electoral, organismos electorales, normativa jurídica y procedimientos de votación en El Salvador. No dispongo de información sobre temas no electorales."
            • NO intentes inventar respuestas sobre temas no electorales ni asumas roles ajenos a la materia electoral salvadoreña.

            ========================================================================
            7. TONO Y ADVERTENCIA ORIENTATIVA
            ========================================================================
            - Mantén un tono respetuoso, sobrio, formal, didáctico e imparcial.
            - Toda respuesta debe concluir con la siguiente advertencia:
            "Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente."
        """.trimIndent()

        // Grounding Context from Retrieved Chunks
        val contextBuilder = StringBuilder()
        contextBuilder.append("=== FRAGMENTOS OFICIALES DE LA BIBLIOTECA ELECTORAL DE EL SALVADOR ===\n\n")

        if (retrievedChunks.isEmpty()) {
            contextBuilder.append("No se recuperaron fragmentos textuales específicos para esta consulta. Responde con base en la normativa oficial salvadoreña aplicable citando el documento y artículo correspondiente.\n")
        } else {
            retrievedChunks.forEachIndexed { index, scored ->
                val chunk = scored.chunk
                contextBuilder.append("--- FRAGMENTO #${index + 1} (Relevancia: ${scored.score.toInt()}) ---\n")
                contextBuilder.append("DOCUMENTO OFICIAL: ${chunk.documentTitle}\n")
                contextBuilder.append("TIPO DE FUENTE: ${chunk.sourceType}\n")
                contextBuilder.append("CAPÍTULO / SECCIÓN: ${chunk.chapter} • ${chunk.sectionTitle}\n")
                if (chunk.articleRef != null) {
                    contextBuilder.append("ARTÍCULO APLICABLE: ${chunk.articleRef}\n")
                }
                contextBuilder.append("PÁGINA: ${chunk.pageNumber}\n")
                contextBuilder.append("TEXTO NORMATIVO:\n${chunk.content}\n\n")
            }
        }

        // Conversation History Context
        val historyBuilder = StringBuilder()
        if (conversationHistory.isNotEmpty()) {
            historyBuilder.append("\n=== HISTORIAL RECIENTE (SOLO CONTEXTO) ===\n")
            conversationHistory.takeLast(2).forEach { (q, a) ->
                historyBuilder.append("Usuario: $q\n")
                val shortAnswer = a.lines().take(2).joinToString(" ")
                historyBuilder.append("IA: $shortAnswer\n")
            }
            historyBuilder.append("==========================================\n\n")
        }

        val promptContent = """
            $contextBuilder
            $historyBuilder
            PREGUNTA EXACTA DEL USUARIO:
            "$userQuery"

            DATOS DE ANÁLISIS:
            • Intención: ${analyzedQuery.intent}
            • Artículos identificados: ${analyzedQuery.targetArticles.joinToString(", ").ifEmpty { "No especificado" }}
            • Entidades: ${analyzedQuery.targetEntities.joinToString(", ").ifEmpty { "General" }}

            INSTRUCCIONES DE RESPUESTA:
            1. Responde estricta y únicamente a la PREGUNTA EXACTA DEL USUARIO sin desviarte a temas no solicitados.
            2. Aplica la JERARQUÍA DE FUENTES priorizando los documentos oficiales salvadoreños.
            3. Es OBLIGATORIO citar el nombre del documento oficial y el artículo/sección respectivo.
            4. Si es una consulta puntual o conceptual, responde de forma directa y concisa. Si es un caso práctico de mesa o contingencia, usa el formato estructurado de situación.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(
                Content(parts = listOf(Part(text = promptContent)))
            ),
            generationConfig = GenerationConfig(
                temperature = 0.1f,
                topP = 0.9f
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
                Como Asistente Virtual Electoral oficial de El Salvador, mi función se limita exclusivamente a responder consultas sobre el sistema electoral, organismos electorales, normativa jurídica y procedimientos de votación en El Salvador. No dispongo de información sobre temas no electorales.

                Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente.
            """.trimIndent()
        }

        if (retrievedChunks.isEmpty()) {
            return """
                ⚠️ No se dispone de información oficial documentada en el repositorio electoral de El Salvador sobre este tema específico.

                📚 **ORIENTACIÓN NORMATIVA:**
                Para resolver este asunto durante el proceso electoral, consulte la versión impresa de los instructivos oficiales del TSE o acuda al Jefe de Centro de Votación o al Delegado de la Junta Electoral Municipal (JEM).

                Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente.
            """.trimIndent()
        }

        val primary = retrievedChunks.first().chunk
        val secondary = retrievedChunks.getOrNull(1)?.chunk

        return when (analyzedQuery.intent) {
            QueryIntentType.ARTICLE_LOOKUP -> {
                """
                📚 **DOCUMENTO OFICIAL:** ${primary.documentTitle}
                ⚖️ **ARTÍCULO:** ${primary.articleRef ?: "Disposición Normativa"} — ${primary.sectionTitle}

                **Contenido Oficial:**
                ${primary.content}
                ${if (secondary != null && secondary.documentId == primary.documentId) "\n" + secondary.content else ""}

                Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente.
                """.trimIndent()
            }
            QueryIntentType.INCIDENT_SECURITY, QueryIntentType.PROHIBITION, QueryIntentType.EXCEPTION_CONTINGENCY -> {
                val docName = primary.documentTitle
                val artRef = primary.articleRef ?: primary.sectionTitle

                """
                🎯 **¿QUÉ DEBES HACER?**
                1. Mantén la calma y garantiza el orden y la continuidad en la Junta Receptora de Votos.
                2. Informa de inmediato al Presidente de la JRV como máxima autoridad de la mesa.
                3. Aplica estrictamente las directrices oficiales del TSE antes de tomar cualquier resolución.
                4. Si existe riesgo a la seguridad física o alteración grave del orden público, solicita auxilio a la PNC asignada al centro.
                5. Registra detalladamente el hecho en el Acta de Incidentes y continúa con la votación ciudadana.

                📚 **FUNDAMENTO LEGAL**
                • **Documento Oficial:** $docName
                • **Artículo / Sección:** $artRef (Pág. ${primary.pageNumber})
                • **Disposición Aplicable:** ${primary.content}

                🟩 **PROCEDIMIENTO DE ACTUACIÓN**
                - **Paso 1.** Identificar con precisión los hechos y verificar serenamente la documentación o credenciales.
                - **Paso 2.** Tomar la decisión colegiada en la JRV por mayoría de votos conforme a la ley.
                - **Paso 3.** En caso de alteración del orden, delitos o presencia de armas, solicitar auxilio a la PNC o Jefe de Centro TSE.
                - **Paso 4.** Asentar circunstanciadamente el incidente en el Acta correspondiente por parte del Secretario.
                - **Paso 5.** Reanudar y continuar las actividades electorales garantizando el derecho al sufragio.

                🟥 **QUÉ NO HACER**
                - ❌ No responder con agresiones, provocaciones o confrontaciones físicas.
                - ❌ No impedir el sufragio por razones de apariencia, vestimenta, discapacidad o identidad de género.
                - ❌ No retener, alterar, ocultar ni destruir credenciales, actas o papeletas de votación.
                - ❌ No paralizar la votación sin causa justificada en la ley.

                ⚠️ **SI EXISTE DUDA O LA SITUACIÓN ESCALA**
                - Si el elector cumple los requisitos legales del DUI y padrón, permitir el sufragio normalmente.
                - Si la JRV tiene dudas técnicas insubsanables, elevar consulta inmediata al Jefe de Centro del TSE o Delegado de la JEM.

                Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente.
                """.trimIndent()
            }
            else -> {
                """
                📚 **DOCUMENTO OFICIAL:** ${primary.documentTitle}
                ⚖️ **FUENTE / ARTÍCULO:** ${primary.articleRef ?: primary.sectionTitle} (Pág. ${primary.pageNumber})

                **Disposición oficial:**
                ${primary.content}
                ${if (secondary != null) "\n\n**Disposición conexa:**\n• " + secondary.documentTitle + " (" + (secondary.articleRef ?: secondary.sectionTitle) + "):\n" + secondary.content else ""}

                Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente.
                """.trimIndent()
            }
        }
    }
}


