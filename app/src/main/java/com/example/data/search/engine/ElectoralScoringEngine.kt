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
        var titleScore = 0.0
        for (kw in queryKeywords) {
            if (cleanTitle.contains(kw)) {
                titleScore += WEIGHT_TITLE_MATCH
            }
        }
        if (titleScore > 0) {
            breakdown["titulo"] = titleScore
            totalScore += titleScore
        }

        // 2. Descripción de la Situación Match
        if (situation.descripcionSituacion.isNotBlank()) {
            val cleanDesc = ElectoralNormalizer.stripAccents(situation.descripcionSituacion.lowercase())
            var descScore = 0.0
            for (kw in queryKeywords) {
                if (cleanDesc.contains(kw)) {
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
            if (queryKeywords.contains(cleanKw) || queryText.contains(cleanKw) || queryTokens.contains(cleanKw)) {
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
            if (queryText.contains(cleanSyn) || queryTokens.contains(cleanSyn) || queryKeywords.any { ElectoralSynonymRegistry.areSynonyms(it, cleanSyn) }) {
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
            queryText.contains(cleanActor) ||
            (cleanActor.contains("vigilante") && (queryText.contains("vigilante") || queryText.contains("fiscal"))) ||
            (cleanActor.contains("votante") && (queryText.contains("votante") || queryText.contains("elector") || queryText.contains("ciudadano"))) ||
            (cleanActor.contains("presidente") && queryText.contains("presidente")) ||
            (cleanActor.contains("secretario") && queryText.contains("secretario")) ||
            (cleanActor.contains("vocal") && queryText.contains("vocal")) ||
            (cleanActor.contains("pnc") && (queryText.contains("pnc") || queryText.contains("policia"))) ||
            (cleanActor.contains("fiscal") && (queryText.contains("fgr") || queryText.contains("fiscalia"))) ||
            (cleanActor.contains("patrono") && (queryText.contains("patrono") || queryText.contains("jefe") || queryText.contains("empresa") || queryText.contains("trabajo")))
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
            queryText.contains(cleanAction) ||
            (cleanAction.contains("amenaza") && (queryText.contains("amenaz") || queryText.contains("apunto") || queryText.contains("intimido"))) ||
            (cleanAction.contains("ingresar") && (queryText.contains("ingres") || queryText.contains("entro") || queryText.contains("llego"))) ||
            (cleanAction.contains("portar") && (queryText.contains("porta") || queryText.contains("lleva") || queryText.contains("tenia") || queryText.contains("saco"))) ||
            (cleanAction.contains("votar") && (queryText.contains("votar") || queryText.contains("voto"))) ||
            (cleanAction.contains("cotejo") && (queryText.contains("parece") || queryText.contains("foto") || queryText.contains("rasgos"))) ||
            (cleanAction.contains("instala") && (queryText.contains("instala") || queryText.contains("abrir") || queryText.contains("inicio"))) ||
            (cleanAction.contains("cerrar") && (queryText.contains("cierre") || queryText.contains("cierra") || queryText.contains("5 pm") || queryText.contains("5:00"))) ||
            (cleanAction.contains("romper") && (queryText.contains("romp") || queryText.contains("dano") || queryText.contains("mancho"))) ||
            (cleanAction.contains("fotografiar") && (queryText.contains("foto") || queryText.contains("celular") || queryText.contains("camara") || queryText.contains("grabar")))
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
            queryText.contains(cleanObj) ||
            (cleanObj.contains("arma") && (queryText.contains("arma") || queryText.contains("pistola") || queryText.contains("revolver") || queryText.contains("cuchillo"))) ||
            (cleanObj.contains("dui") && (queryText.contains("dui") || queryText.contains("cedula") || queryText.contains("identidad") || queryText.contains("foto"))) ||
            (cleanObj.contains("papeleta") && (queryText.contains("papeleta") || queryText.contains("boleta"))) ||
            (cleanObj.contains("padron") && (queryText.contains("padron") || queryText.contains("lista"))) ||
            (cleanObj.contains("tinta") && (queryText.contains("tinta") || queryText.contains("manchado"))) ||
            (cleanObj.contains("acta") && (queryText.contains("acta") || queryText.contains("conteo"))) ||
            (cleanObj.contains("urna") && queryText.contains("urna")) ||
            (cleanObj.contains("celular") && (queryText.contains("celular") || queryText.contains("telefono") || queryText.contains("camara")))
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
            if (queryText.contains(cleanCtx) || cleanCtx == entities.context) {
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
            (intent.contains("quien_puede") && (queryText.contains("quien puede") || queryText.contains("quienes pueden") || queryText.contains("autorizados"))) ||
            (intent.contains("prohibicion") && (queryText.contains("prohibido") || queryText.contains("sancion") || queryText.contains("delito") || queryText.contains("no hacer"))) ||
            (intent.contains("procedimiento") && (queryText.contains("que hacer") || queryText.contains("como se") || queryText.contains("que pasa") || queryText.contains("que se necesita") || queryText.contains("procedimiento")))
        }
        if (matchingIntents.isNotEmpty()) {
            val intentScore = WEIGHT_INTENT
            breakdown["intencion"] = intentScore
            totalScore += intentScore
        }

        // 10. Category & Subcategory Match (+5)
        val cleanCat = ElectoralNormalizer.stripAccents(situation.categoria.lowercase())
        val cleanSubcat = ElectoralNormalizer.stripAccents(situation.subcategoria.lowercase())
        if (queryText.contains(cleanCat) || (cleanSubcat.isNotBlank() && queryText.contains(cleanSubcat))) {
            breakdown["categoria"] = WEIGHT_CATEGORY
            totalScore += WEIGHT_CATEGORY
        }

        // 11. Response Content Match (situacion, que_hacer, que_no_hacer)
        val respContent = buildString {
            append(situation.respuesta.situacion)
            append(" ")
            situation.respuesta.procedimiento.forEach { append(it).append(" ") }
            situation.respuesta.noHacer.forEach { append(it).append(" ") }
        }.lowercase()
        val cleanRespContent = ElectoralNormalizer.stripAccents(respContent)
        var responseScore = 0.0
        for (kw in queryKeywords) {
            if (cleanRespContent.contains(kw)) {
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
            if (articleTokens.any { queryText.contains(it) && it.any { c -> c.isDigit() } }) {
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
        if (situation.id == "ARM-010" && queryText.contains("quien puede") && queryText.contains("arma")) {
            totalScore += 25.0
            breakdown["bonus_quien_puede"] = 25.0
        }
        if (situation.id == "ARM-001" && queryText.contains("vigilante") && queryText.contains("arma") && !queryText.contains("amenaz")) {
            totalScore += 20.0
            breakdown["bonus_vigilante_arma"] = 20.0
        }
        if (situation.id == "ARM-002" && (queryText.contains("votante") || queryText.contains("elector")) && queryText.contains("amenaz")) {
            totalScore += 25.0
            breakdown["bonus_amenaza_votante"] = 25.0
        }
        if (situation.id == "DUI-002" && (queryText.contains("foto") || queryText.contains("fotografia")) && (queryText.contains("parece") || queryText.contains("rasgos"))) {
            totalScore += 35.0
            breakdown["bonus_foto_dui"] = 35.0
        }
        if ((situation.id == "INST-001" || situation.id == "INS-001") && queryText.contains("instala") && (queryText.contains("mesa") || queryText.contains("jrv") || queryText.contains("necesita"))) {
            totalScore += 30.0
            breakdown["bonus_instalacion_mesa"] = 30.0
        }
        if (situation.id == "ESC-001" && ((queryText.contains("cierre") && (queryText.contains("voto") || queryText.contains("votacion") || queryText.contains("mesa"))) || (queryText.contains("5") && (queryText.contains("cierre") || queryText.contains("escrutinio") || queryText.contains("tarde") || queryText.contains("pm"))))) {
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
