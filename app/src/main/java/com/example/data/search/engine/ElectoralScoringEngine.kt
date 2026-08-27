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

    const val WEIGHT_KEYWORD = 5.0
    const val WEIGHT_SYNONYM = 3.0
    const val WEIGHT_ACTOR = 10.0
    const val WEIGHT_ACTION = 8.0
    const val WEIGHT_OBJECT = 8.0
    const val WEIGHT_CONTEXT = 10.0
    const val WEIGHT_INTENT = 10.0
    const val WEIGHT_CATEGORY = 5.0
    const val WEIGHT_SYNERGY_BONUS = 15.0

    const val THRESHOLD_HIGH = 35.0
    const val THRESHOLD_MEDIUM = 20.0

    /**
     * Scores a candidate situation against the normalized query and extracted entities.
     */
    fun scoreSituation(
        situation: ElectoralSituation,
        normalizedQuery: NormalizedQuery,
        entities: ExtractedEntities
    ): ScoredSituation {
        var totalScore = 0.0
        val breakdown = mutableMapOf<String, Double>()

        val queryText = normalizedQuery.cleanedText
        val queryKeywords = normalizedQuery.keywords

        // 1. Keyword Matches (+5 each)
        var keywordScore = 0.0
        for (kw in situation.keywords) {
            val cleanKw = ElectoralNormalizer.stripAccents(kw.lowercase())
            if (queryKeywords.contains(cleanKw) || queryText.contains(cleanKw)) {
                keywordScore += WEIGHT_KEYWORD
            }
        }
        if (keywordScore > 0) {
            breakdown["keywords"] = keywordScore
            totalScore += keywordScore
        }

        // 2. Synonym Matches (+3 each)
        var synonymScore = 0.0
        for (syn in situation.sinonimos) {
            val cleanSyn = ElectoralNormalizer.stripAccents(syn.lowercase())
            if (queryText.contains(cleanSyn) || queryKeywords.any { ElectoralSynonymRegistry.areSynonyms(it, cleanSyn) }) {
                synonymScore += WEIGHT_SYNONYM
            }
        }
        if (synonymScore > 0) {
            breakdown["sinonimos"] = synonymScore
            totalScore += synonymScore
        }

        // 3. Actor Match (+10 per matching actor)
        var actorScore = 0.0
        val matchingActors = situation.actores.filter { actor ->
            entities.actors.contains(actor) ||
            actor in entities.canonicalConcepts ||
            (actor == "vigilante" && (queryText.contains("vigilante") || queryText.contains("fiscal"))) ||
            (actor == "votante" && (queryText.contains("votante") || queryText.contains("elector") || queryText.contains("ciudadano"))) ||
            (actor == "presidente" && queryText.contains("presidente")) ||
            (actor == "secretario" && queryText.contains("secretario")) ||
            (actor == "vocal" && queryText.contains("vocal")) ||
            (actor == "pnc" && (queryText.contains("pnc") || queryText.contains("policia"))) ||
            (actor == "fgr" && (queryText.contains("fgr") || queryText.contains("fiscalia"))) ||
            (actor == "patrono" && (queryText.contains("patrono") || queryText.contains("jefe") || queryText.contains("empresa") || queryText.contains("trabajo")))
        }
        if (matchingActors.isNotEmpty()) {
            actorScore = matchingActors.size * WEIGHT_ACTOR
            breakdown["actor"] = actorScore
            totalScore += actorScore
        }

        // 4. Action Match (+8 per matching action)
        var actionScore = 0.0
        val matchingActions = situation.acciones.filter { action ->
            entities.actions.contains(action) ||
            (action == "amenaza" && (queryText.contains("amenaz") || queryText.contains("apunto") || queryText.contains("intimido"))) ||
            (action == "ingresar" && (queryText.contains("ingres") || queryText.contains("entro") || queryText.contains("llego"))) ||
            (action == "portar" && (queryText.contains("porta") || queryText.contains("lleva") || queryText.contains("tenia") || queryText.contains("saco"))) ||
            (action == "votar" && (queryText.contains("votar") || queryText.contains("voto"))) ||
            (action == "romper" && (queryText.contains("romp") || queryText.contains("dano") || queryText.contains("mancho"))) ||
            (action == "fotografiar" && (queryText.contains("foto") || queryText.contains("celular") || queryText.contains("camara") || queryText.contains("grabar")))
        }
        if (matchingActions.isNotEmpty()) {
            actionScore = matchingActions.size * WEIGHT_ACTION
            breakdown["accion"] = actionScore
            totalScore += actionScore
        }

        // 5. Object Match (+8 per matching object)
        var objectScore = 0.0
        val matchingObjects = situation.objetos.filter { obj ->
            entities.objects.contains(obj) ||
            (obj == "arma_de_fuego" && (queryText.contains("arma") || queryText.contains("pistola") || queryText.contains("revolver") || queryText.contains("cuchillo"))) ||
            (obj == "dui" && (queryText.contains("dui") || queryText.contains("cedula") || queryText.contains("identidad"))) ||
            (obj == "papeleta" && (queryText.contains("papeleta") || queryText.contains("boleta"))) ||
            (obj == "padron" && (queryText.contains("padron") || queryText.contains("lista"))) ||
            (obj == "tinta" && (queryText.contains("tinta") || queryText.contains("manchado"))) ||
            (obj == "acta" && (queryText.contains("acta") || queryText.contains("conteo"))) ||
            (obj == "urna" && queryText.contains("urna")) ||
            (obj == "celular" && (queryText.contains("celular") || queryText.contains("telefono") || queryText.contains("camara")))
        }
        if (matchingObjects.isNotEmpty()) {
            objectScore = matchingObjects.size * WEIGHT_OBJECT
            breakdown["objeto"] = objectScore
            totalScore += objectScore
        }

        // 6. Context Match (+10)
        if (situation.contexto.isNotBlank() && situation.contexto == entities.context) {
            breakdown["contexto"] = WEIGHT_CONTEXT
            totalScore += WEIGHT_CONTEXT
        }

        // 7. Intent Match (+10)
        val matchingIntents = situation.intenciones.filter { intent ->
            intent == entities.intent ||
            (intent == "competencia_quien_puede" && (queryText.contains("quien puede") || queryText.contains("quienes pueden") || queryText.contains("autorizados"))) ||
            (intent == "prohibicion_sancion" && (queryText.contains("prohibido") || queryText.contains("sancion") || queryText.contains("delito"))) ||
            (intent == "procedimiento_emergencia" && (queryText.contains("amenaz") || queryText.contains("apunto")))
        }
        if (matchingIntents.isNotEmpty()) {
            val intentScore = WEIGHT_INTENT
            breakdown["intencion"] = intentScore
            totalScore += intentScore
        }

        // 8. Category Match (+5)
        if (queryText.contains(situation.categoria) || queryText.contains(situation.subcategoria)) {
            breakdown["categoria"] = WEIGHT_CATEGORY
            totalScore += WEIGHT_CATEGORY
        }

        // 9. Synergy Bonus: Multi-Element Match (+15)
        // If Actor + Action + Object all match, grant synergy boost
        if (matchingActors.isNotEmpty() && matchingActions.isNotEmpty() && matchingObjects.isNotEmpty()) {
            breakdown["sinergia_elementos"] = WEIGHT_SYNERGY_BONUS
            totalScore += WEIGHT_SYNERGY_BONUS
        }

        // 10. Distinctive Disambiguation:
        // If the query asks "who can carry weapons" (quienes pueden portar armas), penalize threat situations (ARM-002) and favor authorization rules (ARM-010)
        if (entities.intent == "competencia_quien_puede" && situation.id == "ARM-010") {
            totalScore += 20.0
            breakdown["bonus_quien_puede"] = 20.0
        }
        if (queryText.contains("vigilante") && !queryText.contains("votante") && situation.id == "ARM-001") {
            totalScore += 15.0
            breakdown["bonus_actor_vigilante"] = 15.0
        }
        if ((queryText.contains("votante") || queryText.contains("elector") || queryText.contains("ciudadano")) && queryText.contains("amenaz") && situation.id == "ARM-002") {
            totalScore += 20.0
            breakdown["bonus_amenaza_votante"] = 20.0
        }

        return ScoredSituation(
            situation = situation,
            totalScore = totalScore,
            scoreBreakdown = breakdown
        )
    }
}
