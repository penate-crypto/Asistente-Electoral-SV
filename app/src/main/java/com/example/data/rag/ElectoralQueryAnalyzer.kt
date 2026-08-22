package com.example.data.rag

import java.util.Locale

object ElectoralQueryAnalyzer {

    private val commonSpellingFixes = mapOf(
        "jente" to "gente",
        "papeleta" to "papeleta",
        "papeletas" to "papeletas",
        "escrutinio" to "escrutinio",
        "escrutino" to "escrutinio",
        "escrutinio" to "escrutinio",
        "tinta" to "tinta",
        "credensial" to "credencial",
        "credensiales" to "credenciales",
        "padron" to "padrón",
        "voto" to "voto",
        "votos" to "votos",
        "inhabilitado" to "inhabilitado",
        "patrono" to "patrono",
        "empleador" to "empleador",
        "permiso" to "permiso",
        "capacitacion" to "capacitación",
        "vigilante" to "vigilante",
        "vigilantes" to "vigilantes",
        "presi" to "presidente",
        "precidente" to "presidente",
        "secre" to "secretario",
        "vocal" to "vocal",
        "suplente" to "suplente",
        "urna" to "urna",
        "acta" to "acta",
        "actas" to "actas"
    )

    fun analyze(query: String, conversationContext: String? = null): AnalyzedQuery {
        val raw = query.trim()
        val normalized = normalizeQueryText(raw)

        val fullTextForAnalysis = if (!conversationContext.isNullOrBlank()) {
            "$normalized (Contexto previo: $conversationContext)"
        } else {
            normalized
        }

        val targetArticles = extractArticleReferences(fullTextForAnalysis)
        val targetEntities = extractEntities(fullTextForAnalysis)
        val intent = determineIntent(fullTextForAnalysis, targetArticles)
        val subQueries = decomposeComplexQuery(normalized, intent)
        val keyTerms = extractKeyTerms(normalized)

        return AnalyzedQuery(
            originalQuery = raw,
            normalizedQuery = normalized,
            intent = intent,
            targetArticles = targetArticles,
            targetEntities = targetEntities,
            subQueries = subQueries,
            keyTerms = keyTerms
        )
    }

    private fun normalizeQueryText(text: String): String {
        var clean = text.lowercase(Locale.ROOT)
            .replace("¿", "")
            .replace("?", "")
            .replace("¡", "")
            .replace("!", "")

        // Fix misspelled words
        for ((wrong, right) in commonSpellingFixes) {
            clean = clean.replace(Regex("\\b$wrong\\b"), right)
        }

        return clean.trim()
    }

    private fun extractArticleReferences(text: String): List<String> {
        val found = mutableListOf<String>()
        val regex = Regex("(?:art(?:iculo|ículo|\\.)?\\s*)(\\d+)(?:\\s*(?:al?|y|-)\\s*(\\d+))?", RegexOption.IGNORE_CASE)
        val matches = regex.findAll(text)
        for (m in matches) {
            val num1 = m.groupValues[1]
            found.add("Art. $num1")
            val num2 = m.groupValues.getOrNull(2)
            if (!num2.isNullOrBlank()) {
                found.add("Art. $num2")
            }
        }
        return found.distinct()
    }

    private fun extractEntities(text: String): List<String> {
        val entities = mutableListOf<String>()
        val checkMap = mapOf(
            "jrv" to "Junta Receptora de Votos (JRV)",
            "mesa" to "JRV / Mesa de Votación",
            "presidente" to "Presidente de JRV",
            "secretario" to "Secretario de JRV",
            "vocal" to "Vocal de JRV",
            "suplente" to "Miembro Suplente de JRV",
            "vigilante" to "Vigilante Partidario",
            "pnc" to "Policía Nacional Civil (PNC)",
            "policia" to "Policía Nacional Civil (PNC)",
            "fiscal" to "Fiscalía General de la República (FGR)",
            "fgr" to "Fiscalía General de la República (FGR)",
            "tse" to "Tribunal Supremo Electoral (TSE)",
            "jem" to "Junta Electoral Municipal (JEM)",
            "jed" to "Junta Electoral Departamental (JED)",
            "doe" to "Dirección de Organización Electoral (DOE)",
            "elector" to "Ciudadano / Elector",
            "votante" to "Ciudadano / Elector",
            "patrono" to "Empleador / Patrono",
            "mujer" to "Mujeres / LEIV",
            "trans" to "Identidad de Género / No Discriminación",
            "género" to "Identidad y Trato Digno",
            "genero" to "Identidad y Trato Digno",
            "apariencia" to "Identificación y Trato Digno",
            "discapacidad" to "Inclusión y Accesibilidad",
            "ciego" to "Voto Asistido / Discapacidad Visual",
            "adulto mayor" to "Atención Preferencial Adultos Mayores",
            "anciano" to "Atención Preferencial Adultos Mayores",
            "embarazada" to "Atención Preferencial",
            "observador" to "Observación Electoral",
            "prensa" to "Medios de Comunicación y Prensa"
        )

        for ((term, entityName) in checkMap) {
            if (text.contains(term)) {
                entities.add(entityName)
            }
        }
        return entities.distinct()
    }

    private fun determineIntent(text: String, articles: List<String>): QueryIntentType {
        if (isOutOfScopeQuery(text)) {
            return QueryIntentType.OUT_OF_SCOPE
        }

        if (articles.isNotEmpty()) return QueryIntentType.ARTICLE_LOOKUP

        if (text.contains("arma") || text.contains("cuchillo") || text.contains("objeto cortante") ||
            text.contains("violencia") || text.contains("amenaza") || text.contains("delito") ||
            text.contains("pnc") || text.contains("desalojo") || text.contains("intervenir")) {
            return QueryIntentType.INCIDENT_SECURITY
        }

        if (text.contains("falta") || text.contains("faltan") || text.contains("faltante") ||
            text.contains("menos") || text.contains("ausencia") || text.contains("no llego") ||
            text.contains("no llegó") || text.contains("sin credencial") || text.contains("incompleto")) {
            return QueryIntentType.EXCEPTION_CONTINGENCY
        }

        if (text.contains("ley seca") || text.contains("alcohol") || text.contains("prohibido") ||
            text.contains("prohibicion") || text.contains("no se puede") || text.contains("sancion")) {
            return QueryIntentType.PROHIBITION
        }

        if (text.contains("paso a paso") || text.contains("como se hace") || text.contains("procedimiento") ||
            text.contains("como instalar") || text.contains("como contar") || text.contains("conteo") ||
            text.contains("horario") || text.contains("apertura") || text.contains("cierre")) {
            return QueryIntentType.PROCEDURAL
        }

        if (text.contains("discapacidad") || text.contains("asistido") || text.contains("anciano") ||
            text.contains("ciego") || text.contains("silla de ruedas")) {
            return QueryIntentType.VOTING_ASSISTANCE
        }

        if (text.contains("que es") || text.contains("definicion") || text.contains("voto nulo") ||
            text.contains("voto valido") || text.contains("voto impugnado") || text.contains("abstencion") ||
            text.contains("como se integra") || text.contains("quienes integran")) {
            return QueryIntentType.DEFINITION
        }

        return QueryIntentType.GENERAL_ELECTORAL
    }

    private fun isOutOfScopeQuery(text: String): Boolean {
        val nonElectoralKeywords = listOf(
            "receta", "cocinar", "ingredientes", "pupusas", "pizza", "futbol", "fútbol", "campeonato",
            "messi", "ronaldo", "pelicula", "cancion", "musica", "videojuego", "clima", "temperatura",
            "programar", "javascript", "python", "kotlin", "java", "chatgpt", "medicina", "sintomas",
            "enfermedad", "horoscopo", "astrologia", "astronomia", "chiste", "poema", "cuento"
        )
        for (kw in nonElectoralKeywords) {
            if (text.contains(kw)) return true
        }
        return false
    }

    private fun decomposeComplexQuery(normalizedText: String, intent: QueryIntentType): List<String> {
        val subQueries = mutableListOf<String>()
        subQueries.add(normalizedText)

        // Decompose multi-clause or multi-concept queries
        if (normalizedText.contains("sin credencial") && (normalizedText.contains("armad") || normalizedText.contains("cuchillo") || normalizedText.contains("objeto"))) {
            subQueries.add("requisito credencial tse acceso jrv mesa")
            subQueries.add("prohibicion armas objetos cortopunzantes art 290 auxilio pnc")
            subQueries.add("atribuciones presidente jrv mantener orden y pedir pnc")
        } else if (normalizedText.contains("falta") && normalizedText.contains("papeleta") && (normalizedText.contains("esperando") || normalizedText.contains("votantes"))) {
            subQueries.add("procedimiento faltante de papeletas paquete electoral jrv")
            subQueries.add("consignar en acta de instalacion y solicitar reserva doe jem")
            subQueries.add("apertura de votacion a las 7:00 am con material disponible")
        } else if (normalizedText.contains("trans") || normalizedText.contains("apariencia") || normalizedText.contains("identidad de género") || normalizedText.contains("genero") || normalizedText.contains("sexo")) {
            subQueries.add("identificacion elector requisitos votar dui padron fotografia jrv")
            subQueries.add("principio no discriminacion e igualdad art 3 constitucion de el salvador")
            subQueries.add("procedimiento jrv comprobacion de identidad titular de documento")
        } else if (normalizedText.contains("discapacidad") || normalizedText.contains("ciego") || normalizedText.contains("silla de ruedas")) {
            subQueries.add("voto asistido personas con discapacidad codigo electoral")
            subQueries.add("accesibilidad atril especial jrv mesa de votacion")
        } else if (normalizedText.contains("objeto cortante") || normalizedText.contains("abrir cajas") || normalizedText.contains("cuchillo")) {
            subQueries.add("prohibicion armas y objetos cortantes centro votacion art 290")
            subQueries.add("material oficial tijeras utiles de escritorio paquete electoral tse")
            subQueries.add("intervencion presidente jrv y seguridad pnc")
        } else if (normalizedText.contains("quien interviene") || normalizedText.contains("quién interviene") || normalizedText.contains("que autoridad")) {
            subQueries.add("autoridad competente presidente de jrv tse pnc fiscalia fgr")
        }

        return subQueries.distinct()
    }

    private fun extractKeyTerms(normalizedText: String): List<String> {
        val stopWords = setOf(
            "el", "la", "los", "las", "un", "una", "unos", "unas", "de", "del", "a", "al", "en", "por", "para",
            "con", "sin", "sobre", "que", "qué", "como", "cómo", "y", "o", "u", "si", "sí", "se", "es", "son",
            "hacer", "hago", "pasa", "puede", "puedo", "tengo", "este", "esta", "estos", "estas", "caso",
            "persona", "dice", "diciendo", "necesita", "llegó", "llego", "llega"
        )

        return normalizedText.split(Regex("[^a-záéíóúüñ0-9]+"))
            .filter { it.length > 2 && it !in stopWords }
            .distinct()
    }
}
