package com.example.data.search.engine

import com.example.data.search.entities.ExtractedEntities
import com.example.data.search.model.ElectoralSituation
import com.example.data.search.normalization.ElectoralNormalizer
import com.example.data.search.normalization.NormalizedQuery
import com.example.data.search.synonyms.ElectoralSynonymRegistry

data class ScoredSituation(
    val situation: ElectoralSituation,
    val totalScore: Double,
    val scoreBreakdown: Map<String, Double>
)

object ElectoralScoringEngine {

    const val WEIGHT_TITLE_MATCH = 12.0
    const val WEIGHT_DESCRIPTION_MATCH = 8.0
    const val WEIGHT_KEYWORD = 6.0
    const val WEIGHT_SYNONYM = 4.0
    const val WEIGHT_ACTOR = 10.0
    const val WEIGHT_ACTION = 8.0
    const val WEIGHT_OBJECT = 8.0
    const val WEIGHT_CONTEXT = 8.0
    const val WEIGHT_INTENT = 8.0
    const val WEIGHT_CATEGORY = 5.0
    const val WEIGHT_RESPONSE_CONTENT = 6.0
    const val WEIGHT_SYNERGY_BONUS = 15.0

    const val THRESHOLD_HIGH = 35.0
    const val THRESHOLD_MEDIUM = 18.0

    /**
     * Scores a candidate situation against the normalized query and extracted entities.
     * Evaluates all JSON elements:
     * - titulo
     * - categoria & subcategoria
     * - descripcion_situacion
     * - actores
     * - objetos
     * - acciones
     * - contextos
     * - intenciones
     * - palabras_clave
     * - sinonimos
     * - respuesta (situacion_detectada, que_hacer, que_no_hacer)
     */
    fun scoreSituation(
        situation: ElectoralSituation,
        normalizedQuery: NormalizedQuery,
        entities: ExtractedEntities
    ): ScoredSituation {
        var totalScore = 0.0
        val breakdown = mutableMapOf<String, Double>()

        val queryText = normalizedQuery.cleanedText
        val queryTokens = normalizedQuery.tokens
        val queryKeywords = normalizedQuery.keywords

        // 1. Título Match
        val cleanTitle = ElectoralNormalizer.stripAccents(situation.titulo.lowercase())
            .replace("[^a-z0-9\\s]".toRegex(), " ")
            .replace("\\s+".toRegex(), " ")
            .trim()
        val titleTokens = ElectoralNormalizer.tokenizeAndSingularize(cleanTitle).toSet()
        var titleScore = 0.0
        for (kw in queryKeywords) {
            if (kw in titleTokens || ElectoralNormalizer.containsWholeWord(cleanTitle, kw)) {
                titleScore += WEIGHT_TITLE_MATCH
            }
        }
        if (cleanTitle.length > 10 && (cleanTitle == queryText || ElectoralNormalizer.containsWholeWord(queryText, cleanTitle))) {
            titleScore += 60.0
        }
        if (titleScore > 0) {
            breakdown["titulo"] = titleScore
            totalScore += titleScore
        }

        // 2. Descripción de la Situación Match
        if (situation.descripcionSituacion.isNotBlank()) {
            val cleanDesc = ElectoralNormalizer.stripAccents(situation.descripcionSituacion.lowercase())
            val descTokens = ElectoralNormalizer.tokenizeAndSingularize(cleanDesc).toSet()
            var descScore = 0.0
            for (kw in queryKeywords) {
                if (kw in descTokens || ElectoralNormalizer.containsWholeWord(cleanDesc, kw)) {
                    descScore += WEIGHT_DESCRIPTION_MATCH
                }
            }
            if (descScore > 0) {
                breakdown["descripcion"] = descScore
                totalScore += descScore
            }
        }

        // 3. Palabras Clave (Keywords) (+6 each)
        var keywordScore = 0.0
        for (kw in situation.keywords) {
            val cleanKw = ElectoralNormalizer.stripAccents(kw.lowercase())
            if (queryKeywords.contains(cleanKw) ||
                queryTokens.contains(cleanKw) ||
                ElectoralNormalizer.containsWholeWord(queryText, cleanKw)
            ) {
                keywordScore += WEIGHT_KEYWORD
            }
        }
        if (keywordScore > 0) {
            breakdown["keywords"] = keywordScore
            totalScore += keywordScore
        }

        // 4. Sinónimos Matches (+4 each)
        var synonymScore = 0.0
        for (syn in situation.sinonimos) {
            val cleanSyn = ElectoralNormalizer.stripAccents(syn.lowercase())
            if (ElectoralNormalizer.containsWholeWord(queryText, cleanSyn) ||
                queryTokens.contains(cleanSyn) ||
                queryKeywords.any { ElectoralSynonymRegistry.areSynonyms(it, cleanSyn) }
            ) {
                synonymScore += WEIGHT_SYNONYM
            }
        }
        if (synonymScore > 0) {
            breakdown["sinonimos"] = synonymScore
            totalScore += synonymScore
        }

        // 5. Actor Match (+10 per matching actor)
        var actorScore = 0.0
        val matchingActors = situation.actores.filter { actor ->
            val cleanActor = ElectoralNormalizer.stripAccents(actor.lowercase())
            entities.actors.contains(actor) ||
            actor in entities.canonicalConcepts ||
            ElectoralNormalizer.containsWholeWord(queryText, cleanActor) ||
            (cleanActor.contains("vigilante") && (ElectoralNormalizer.containsWholeWord(queryText, "vigilante") || ElectoralNormalizer.containsWholeWord(queryText, "fiscal"))) ||
            (cleanActor.contains("votante") && (ElectoralNormalizer.containsWholeWord(queryText, "votante") || ElectoralNormalizer.containsWholeWord(queryText, "elector") || ElectoralNormalizer.containsWholeWord(queryText, "ciudadano"))) ||
            (cleanActor.contains("presidente") && ElectoralNormalizer.containsWholeWord(queryText, "presidente")) ||
            (cleanActor.contains("secretario") && ElectoralNormalizer.containsWholeWord(queryText, "secretario")) ||
            (cleanActor.contains("vocal") && ElectoralNormalizer.containsWholeWord(queryText, "vocal")) ||
            (cleanActor.contains("pnc") && (ElectoralNormalizer.containsWholeWord(queryText, "pnc") || ElectoralNormalizer.containsWholeWord(queryText, "policia"))) ||
            (cleanActor.contains("fiscal") && (ElectoralNormalizer.containsWholeWord(queryText, "fgr") || ElectoralNormalizer.containsWholeWord(queryText, "fiscalia"))) ||
            (cleanActor.contains("patrono") && (ElectoralNormalizer.containsWholeWord(queryText, "patrono") || ElectoralNormalizer.containsWholeWord(queryText, "jefe") || ElectoralNormalizer.containsWholeWord(queryText, "empresa") || ElectoralNormalizer.containsWholeWord(queryText, "trabajo"))) ||
            (cleanActor.contains("observador") && ElectoralNormalizer.containsWholeWord(queryText, "observador")) ||
            (cleanActor.contains("jefe de centro") && ElectoralNormalizer.containsWholeWord(queryText, "jefe de centro")) ||
            (cleanActor.contains("jem") && ElectoralNormalizer.containsWholeWord(queryText, "jem")) ||
            (cleanActor.contains("jed") && ElectoralNormalizer.containsWholeWord(queryText, "jed")) ||
            (cleanActor.contains("tse") && ElectoralNormalizer.containsWholeWord(queryText, "tse"))
        }
        if (matchingActors.isNotEmpty()) {
            actorScore = matchingActors.size * WEIGHT_ACTOR
            breakdown["actor"] = actorScore
            totalScore += actorScore
        }

        // 6. Action Match (+8 per matching action)
        var actionScore = 0.0
        val matchingActions = situation.acciones.filter { action ->
            val cleanAction = ElectoralNormalizer.stripAccents(action.lowercase())
            entities.actions.contains(action) ||
            ElectoralNormalizer.containsWholeWord(queryText, cleanAction) ||
            (cleanAction.contains("amenaza") && (ElectoralNormalizer.containsWordPrefix(queryText, "amenaz") || ElectoralNormalizer.containsWholeWord(queryText, "apunto") || ElectoralNormalizer.containsWholeWord(queryText, "intimido"))) ||
            (cleanAction.contains("ingresar") && (ElectoralNormalizer.containsWordPrefix(queryText, "ingres") || ElectoralNormalizer.containsWholeWord(queryText, "entro") || ElectoralNormalizer.containsWholeWord(queryText, "llego"))) ||
            (cleanAction.contains("portar") && (ElectoralNormalizer.containsWordPrefix(queryText, "porta") || ElectoralNormalizer.containsWholeWord(queryText, "lleva") || ElectoralNormalizer.containsWholeWord(queryText, "tenia") || ElectoralNormalizer.containsWholeWord(queryText, "saco"))) ||
            (cleanAction.contains("votar") && (ElectoralNormalizer.containsWholeWord(queryText, "votar") || ElectoralNormalizer.containsWholeWord(queryText, "voto"))) ||
            (cleanAction.contains("cotejo") && (ElectoralNormalizer.containsWholeWord(queryText, "parece") || ElectoralNormalizer.containsWholeWord(queryText, "foto") || ElectoralNormalizer.containsWholeWord(queryText, "fotografia") || ElectoralNormalizer.containsWholeWord(queryText, "rasgos"))) ||
            (cleanAction.contains("instala") && (ElectoralNormalizer.containsWordPrefix(queryText, "instala") || ElectoralNormalizer.containsWholeWord(queryText, "abrir") || ElectoralNormalizer.containsWholeWord(queryText, "inicio"))) ||
            (cleanAction.contains("cerrar") && (ElectoralNormalizer.containsWholeWord(queryText, "cierre") || ElectoralNormalizer.containsWholeWord(queryText, "cierra") || ElectoralNormalizer.containsWholeWord(queryText, "5 pm") || ElectoralNormalizer.containsWholeWord(queryText, "5 00"))) ||
            (cleanAction.contains("romper") && (ElectoralNormalizer.containsWordPrefix(queryText, "romp") || ElectoralNormalizer.containsWordPrefix(queryText, "dan") || ElectoralNormalizer.containsWholeWord(queryText, "mancho"))) ||
            (cleanAction.contains("fotografiar") && (ElectoralNormalizer.containsWholeWord(queryText, "foto") || ElectoralNormalizer.containsWholeWord(queryText, "fotografia") || ElectoralNormalizer.containsWholeWord(queryText, "celular") || ElectoralNormalizer.containsWholeWord(queryText, "camara") || ElectoralNormalizer.containsWholeWord(queryText, "grabar")))
        }
        if (matchingActions.isNotEmpty()) {
            actionScore = matchingActions.size * WEIGHT_ACTION
            breakdown["accion"] = actionScore
            totalScore += actionScore
        }

        // 7. Object Match (+8 per matching object)
        var objectScore = 0.0
        val matchingObjects = situation.objetos.filter { obj ->
            val cleanObj = ElectoralNormalizer.stripAccents(obj.lowercase())
            entities.objects.contains(obj) ||
            ElectoralNormalizer.containsWholeWord(queryText, cleanObj) ||
            (ElectoralNormalizer.containsWholeWord(cleanObj, "arma") && ("arma_de_fuego" in entities.objects || ElectoralNormalizer.containsWholeWord(queryText, "arma") || ElectoralNormalizer.containsWholeWord(queryText, "pistola") || ElectoralNormalizer.containsWholeWord(queryText, "revolver") || ElectoralNormalizer.containsWholeWord(queryText, "cuchillo"))) ||
            (ElectoralNormalizer.containsWholeWord(cleanObj, "dui") && (ElectoralNormalizer.containsWholeWord(queryText, "dui") || ElectoralNormalizer.containsWholeWord(queryText, "cedula") || ElectoralNormalizer.containsWholeWord(queryText, "identidad") || ElectoralNormalizer.containsWholeWord(queryText, "foto") || ElectoralNormalizer.containsWholeWord(queryText, "fotografia"))) ||
            (cleanObj.contains("papeleta") && (ElectoralNormalizer.containsWholeWord(queryText, "papeleta") || ElectoralNormalizer.containsWholeWord(queryText, "boleta"))) ||
            (cleanObj.contains("padron") && (ElectoralNormalizer.containsWholeWord(queryText, "padron") || ElectoralNormalizer.containsWholeWord(queryText, "lista"))) ||
            (ElectoralNormalizer.containsWholeWord(cleanObj, "tinta") && (ElectoralNormalizer.containsWholeWord(queryText, "tinta") || ElectoralNormalizer.containsWholeWord(queryText, "manchado"))) ||
            (ElectoralNormalizer.containsWholeWord(cleanObj, "acta") && (ElectoralNormalizer.containsWholeWord(queryText, "acta") || ElectoralNormalizer.containsWholeWord(queryText, "conteo"))) ||
            (ElectoralNormalizer.containsWholeWord(cleanObj, "urna") && ElectoralNormalizer.containsWholeWord(queryText, "urna")) ||
            (cleanObj.contains("celular") && (ElectoralNormalizer.containsWholeWord(queryText, "celular") || ElectoralNormalizer.containsWholeWord(queryText, "telefono") || ElectoralNormalizer.containsWholeWord(queryText, "camara")))
        }
        if (matchingObjects.isNotEmpty()) {
            objectScore = matchingObjects.size * WEIGHT_OBJECT
            breakdown["objeto"] = objectScore
            totalScore += objectScore
        }

        // 8. Contextos / Lugares / Momentos Match (+8)
        val allContexts = (situation.contextos + situation.lugares + situation.momentos + listOf(situation.contexto)).filter { it.isNotBlank() }
        var contextScore = 0.0
        for (ctx in allContexts) {
            val cleanCtx = ElectoralNormalizer.stripAccents(ctx.lowercase())
            if (ElectoralNormalizer.containsWholeWord(queryText, cleanCtx) || cleanCtx == entities.context) {
                contextScore += WEIGHT_CONTEXT
                break
            }
        }
        if (contextScore > 0) {
            breakdown["contexto"] = contextScore
            totalScore += contextScore
        }

        // 9. Intent Match (+8)
        val matchingIntents = situation.intenciones.filter { intent ->
            intent == entities.intent ||
            (intent.contains("quien_puede") && (ElectoralNormalizer.containsWholeWord(queryText, "quien puede") || ElectoralNormalizer.containsWholeWord(queryText, "quienes pueden") || ElectoralNormalizer.containsWholeWord(queryText, "autorizados"))) ||
            (intent.contains("prohibicion") && (ElectoralNormalizer.containsWholeWord(queryText, "prohibido") || ElectoralNormalizer.containsWholeWord(queryText, "sancion") || ElectoralNormalizer.containsWholeWord(queryText, "delito") || ElectoralNormalizer.containsWholeWord(queryText, "no hacer"))) ||
            (intent.contains("procedimiento") && (ElectoralNormalizer.containsWholeWord(queryText, "que hacer") || ElectoralNormalizer.containsWholeWord(queryText, "como se") || ElectoralNormalizer.containsWholeWord(queryText, "que pasa") || ElectoralNormalizer.containsWholeWord(queryText, "que se necesita") || ElectoralNormalizer.containsWholeWord(queryText, "procedimiento")))
        }
        if (matchingIntents.isNotEmpty()) {
            val intentScore = WEIGHT_INTENT
            breakdown["intencion"] = intentScore
            totalScore += intentScore
        }

        // 10. Category & Subcategory Match (+5)
        val cleanCat = ElectoralNormalizer.stripAccents(situation.categoria.lowercase())
        val cleanSubcat = ElectoralNormalizer.stripAccents(situation.subcategoria.lowercase())
        if (ElectoralNormalizer.containsWholeWord(queryText, cleanCat) ||
            (cleanSubcat.isNotBlank() && ElectoralNormalizer.containsWholeWord(queryText, cleanSubcat))
        ) {
            breakdown["categoria"] = WEIGHT_CATEGORY
            totalScore += WEIGHT_CATEGORY
        }

        // 11. Response Content Match (situacion, que_hacer, que_no_hacer, advertencia)
        val respContent = buildString {
            append(situation.respuesta.situacion)
            append(" ")
            situation.respuesta.procedimiento.forEach { append(it).append(" ") }
            situation.respuesta.noHacer.forEach { append(it).append(" ") }
            situation.respuesta.advertencia?.let { append(it).append(" ") }
        }.lowercase()
        val cleanRespContent = ElectoralNormalizer.stripAccents(respContent)
        val respTokens = ElectoralNormalizer.tokenizeAndSingularize(cleanRespContent).toSet()
        var responseScore = 0.0
        for (kw in queryKeywords) {
            if (kw in respTokens || ElectoralNormalizer.containsWholeWord(cleanRespContent, kw)) {
                responseScore += 2.0
            }
        }
        if (responseScore > 0) {
            val cappedResponse = responseScore.coerceAtMost(WEIGHT_RESPONSE_CONTENT * 2)
            breakdown["contenido_respuesta"] = cappedResponse
            totalScore += cappedResponse
        }

        // 12. Legal Reference Match (+12 if query mentions specific article or law in the situation)
        val allLegalRefs = situation.respuesta.fundamentosDetallados.map { "${it.ley} ${it.articulo} ${it.incisoOrNumeral}" } + situation.respuesta.fundamento
        for (ref in allLegalRefs) {
            val cleanRef = ElectoralNormalizer.stripAccents(ref.lowercase())
            val articleTokens = cleanRef.split(" ").filter { it.length > 2 }
            if (articleTokens.any { it.any { c -> c.isDigit() } && ElectoralNormalizer.containsWholeWord(queryText, it) }) {
                totalScore += 12.0
                breakdown["referencia_legal"] = 12.0
                break
            }
        }

        // 13. Synergy Bonus: Multi-Element Match (+15)
        if (matchingActors.isNotEmpty() && matchingActions.isNotEmpty() && matchingObjects.isNotEmpty()) {
            breakdown["sinergia_elementos"] = WEIGHT_SYNERGY_BONUS
            totalScore += WEIGHT_SYNERGY_BONUS
        }

        // 14. Disambiguation & Specific Scenario Rules (strictly bounded per situation ID):
        if (situation.id == "ARM-010" &&
            ElectoralNormalizer.containsWholeWord(queryText, "quien puede") &&
            ElectoralNormalizer.containsWholeWord(queryText, "arma")
        ) {
            totalScore += 25.0
            breakdown["bonus_quien_puede"] = 25.0
        }
        if (situation.id == "ARM-001" &&
            ElectoralNormalizer.containsWholeWord(queryText, "vigilante") &&
            ElectoralNormalizer.containsWholeWord(queryText, "arma") &&
            !ElectoralNormalizer.containsWordPrefix(queryText, "amenaz")
        ) {
            totalScore += 20.0
            breakdown["bonus_vigilante_arma"] = 20.0
        }
        if (situation.id == "ARM-002" &&
            (ElectoralNormalizer.containsWholeWord(queryText, "votante") || ElectoralNormalizer.containsWholeWord(queryText, "elector")) &&
            ElectoralNormalizer.containsWordPrefix(queryText, "amenaz")
        ) {
            totalScore += 25.0
            breakdown["bonus_amenaza_votante"] = 25.0
        }
        if (situation.id == "DUI-002" &&
            (ElectoralNormalizer.containsWholeWord(queryText, "foto") || ElectoralNormalizer.containsWholeWord(queryText, "fotografia")) &&
            (ElectoralNormalizer.containsWholeWord(queryText, "parece") || ElectoralNormalizer.containsWholeWord(queryText, "rasgos"))
        ) {
            totalScore += 35.0
            breakdown["bonus_foto_dui"] = 35.0
        }
        if ((situation.id == "INST-001" || situation.id == "INS-001") &&
            ElectoralNormalizer.containsWordPrefix(queryText, "instala") &&
            (ElectoralNormalizer.containsWholeWord(queryText, "mesa") || ElectoralNormalizer.containsWholeWord(queryText, "jrv") || ElectoralNormalizer.containsWholeWord(queryText, "necesita"))
        ) {
            totalScore += 30.0
            breakdown["bonus_instalacion_mesa"] = 30.0
        }
        if (situation.id == "ESC-001" &&
            ((ElectoralNormalizer.containsWholeWord(queryText, "cierre") && (ElectoralNormalizer.containsWholeWord(queryText, "voto") || ElectoralNormalizer.containsWholeWord(queryText, "votacion") || ElectoralNormalizer.containsWholeWord(queryText, "mesa"))) ||
                (ElectoralNormalizer.containsWholeWord(queryText, "5") && (ElectoralNormalizer.containsWholeWord(queryText, "cierre") || ElectoralNormalizer.containsWholeWord(queryText, "escrutinio") || ElectoralNormalizer.containsWholeWord(queryText, "tarde") || ElectoralNormalizer.containsWholeWord(queryText, "pm"))))
        ) {
            totalScore += 35.0
            breakdown["bonus_cierre_votacion"] = 35.0
        }

        return ScoredSituation(
            situation = situation,
            totalScore = totalScore,
            scoreBreakdown = breakdown
        )
    }
}
