package com.example.data.rag

import java.util.Locale

/**
 * Capa de validación de respuestas del Asistente Virtual Electoral de El Salvador.
 *
 * Verifica:
 * 1. Detección y filtrado de consultas fuera del ámbito electoral (Out-of-scope / Off-topic).
 * 2. Presencia obligatoria de una fuente documentada de los 12 textos oficiales de la Biblioteca Electoral salvadoreña.
 * 3. Presencia de citas de artículos, secciones o directrices normativas.
 * 4. Si la respuesta carece de respaldo documental oficial verificable, fuerza a la IA a responder
 *    que no dispone de información oficial al respecto en el repositorio electoral salvadoreño.
 */
object ElectoralResponseValidator {

    private const val OUT_OF_SCOPE_MESSAGE =
        "Como Capacitador y Asistente Electoral Virtual de El Salvador, mi función se limita exclusivamente a responder consultas sobre el sistema electoral, organismos electorales (TSE, JED, JEM, JRV), normativa jurídica y procedimientos de votación en El Salvador. No dispongo de información sobre temas no electorales.\n\nEsta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente."

    private const val NO_OFFICIAL_INFO_MESSAGE =
        "No encontré suficiente información en los documentos disponibles para confirmar esta respuesta.\n\n📌 **Respuesta:**\nNo se localizó una disposición específica en la biblioteca electoral oficial para responder con certeza documental a esta consulta.\n\n📝 **Explicación:**\nPara resolver dudas no contempladas en los instructivos procesados, consulte la normativa física vigente o solicite la intervención del Delegado del TSE o Junta Electoral Municipal (JEM).\n\n⚖️ **Fundamento:**\nCódigo Electoral de la República de El Salvador.\n\n📚 **Fuentes consultadas:**\nBiblioteca Electoral Oficial de El Salvador.\n\nEsta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente."

    private const val MANDATORY_DISCLAIMER =
        "Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente."

    // Lista oficial de nombres y patrones clave de los 12 documentos oficiales salvadoreños
    val officialDocumentSignatures = listOf(
        "código electoral",
        "codigo electoral",
        "decreto no. 413",
        "decreto 413",
        "constitución de la república",
        "constitucion de la republica",
        "constitución de 1983",
        "constitucion de 1983",
        "constitución",
        "constitucion",
        "código penal",
        "codigo penal",
        "delitos electorales",
        "ley de partidos políticos",
        "ley de partidos politicos",
        "decreto no. 307",
        "decreto 307",
        "sufragio en el extranjero",
        "voto en el extranjero",
        "voto en el exterior",
        "decreto no. 542",
        "decreto 542",
        "reestructuración municipal",
        "reestructuracion municipal",
        "decreto no. 763",
        "decreto 763",
        "acceso a la información pública",
        "acceso a la informacion publica",
        "laip",
        "decreto no. 534",
        "decreto 534",
        "instructivo oficial para juntas receptoras de votos",
        "instructivo oficial para miembros de jrv",
        "instructivo jrv",
        "instructivo del tse",
        "instructivo oficial",
        "candidaturas no partidarias",
        "candidatos no partidarios",
        "observación electoral",
        "observacion electoral",
        "reglamento general para la observación",
        "ciclo electoral salvadoreño",
        "ciclo electoral salvadoreno",
        "ciclo electoral",
        "acuerdo legislativo",
        "tribunal supremo electoral",
        "tse",
        "leiv"
    )

    private val articlePatterns = listOf(
        "art.",
        "artículo",
        "articulo",
        "arts.",
        "artículos",
        "articulos",
        "decreto",
        "sección",
        "seccion",
        "capítulo",
        "capitulo",
        "paso 1",
        "paso 2",
        "disposición",
        "disposicion",
        "reglamento",
        "pág."
    )

    /**
     * Valida y procesa la respuesta antes de presentarla al usuario.
     */
    fun validateAndFormat(
        userQuery: String,
        analyzedQuery: AnalyzedQuery,
        rawAnswer: String,
        retrievedChunks: List<ScoredChunk>
    ): String {
        // 1. Control de Ámbito (Out-of-Scope)
        if (analyzedQuery.intent == QueryIntentType.OUT_OF_SCOPE || isOutOfScopeQueryText(userQuery)) {
            return OUT_OF_SCOPE_MESSAGE
        }

        // 2. Si no hay chunks recuperados y el texto no contiene fundamento oficial verificable
        if (retrievedChunks.isEmpty() && !hasOfficialSourceCitation(rawAnswer)) {
            return NO_OFFICIAL_INFO_MESSAGE
        }

        // 3. Si la respuesta está vacía o es muy corta
        if (rawAnswer.trim().length < 15) {
            return NO_OFFICIAL_INFO_MESSAGE
        }

        var finalAnswer = rawAnswer.trim()

        // 4. Asegurar disclaimer orientativo oficial
        if (!finalAnswer.contains("Esta respuesta es únicamente orientativa", ignoreCase = true)) {
            finalAnswer = "$finalAnswer\n\n$MANDATORY_DISCLAIMER"
        }

        return finalAnswer
    }

    /**
     * Comprueba si el texto contiene al menos una referencia a un documento oficial de la Biblioteca Electoral.
     */
    fun hasOfficialSourceCitation(text: String): Boolean {
        val lower = text.lowercase(Locale.ROOT)
        return officialDocumentSignatures.any { signature -> lower.contains(signature) }
    }

    /**
     * Comprueba si el texto contiene citas a artículos, incisos o secciones normativas.
     */
    fun hasArticleOrSectionCitation(text: String): Boolean {
        val lower = text.lowercase(Locale.ROOT)
        return articlePatterns.any { pattern -> lower.contains(pattern) }
    }

    /**
     * Detecta si una consulta textual está manifiestamente fuera del ámbito electoral.
     */
    fun isOutOfScopeQueryText(query: String): Boolean {
        val lower = query.lowercase(Locale.ROOT)
        val nonElectoralKeywords = listOf(
            "receta", "cocinar", "ingredientes", "pupusas", "pizza", "futbol", "fútbol", "campeonato",
            "messi", "ronaldo", "pelicula", "película", "cancion", "canción", "musica", "música",
            "videojuego", "clima", "temperatura", "programar", "javascript", "python", "kotlin", "java",
            "chatgpt", "medicina", "sintomas", "síntomas", "enfermedad", "horoscopo", "horóscopo",
            "astrologia", "astrología", "astronomia", "astronomía", "chiste", "poema", "cuento",
            "formula química", "fórmula química", "ecuacion", "ecuación matemática"
        )
        return nonElectoralKeywords.any { lower.contains(it) }
    }
}
