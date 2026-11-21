package com.example.data.search.synonyms

import com.example.data.search.normalization.ElectoralNormalizer

object ElectoralSynonymRegistry {

    private val SYNONYM_GROUPS: Map<String, List<String>> = mapOf(
        "arma" to listOf(
            "arma", "arma de fuego", "pistola", "revolver", "revólver", "rifle",
            "escopeta", "fusil", "arma blanca", "cuchillo", "machete", "navaja",
            "armado", "armada", "puñal", "artefacto"
        ),
        "votante" to listOf(
            "votante", "elector", "ciudadano", "persona que vota", "votantes",
            "electores", "ciudadanos", "persona", "individuo"
        ),
        "amenaza" to listOf(
            "amenaza", "amenazo", "amenazó", "amenazar", "apunto", "apuntó",
            "apuntando", "intimido", "intimidó", "intimidar", "intimidacion",
            "intimidación", "coacciono", "coaccionó", "coaccionar", "coaccion",
            "coacción", "agredio", "agredió", "agredir", "violencia", "agresion", "agresión"
        ),
        "papeleta" to listOf(
            "papeleta", "boleta", "papeleta de votacion", "papeletas", "boletas",
            "hoja de votacion", "voto"
        ),
        "centro_votacion" to listOf(
            "centro de votacion", "centro de votación", "centro electoral", "lugar de votacion",
            "lugar de votación", "centro", "escuela", "recinto electoral", "sede de votacion"
        ),
        "jrv" to listOf(
            "jrv", "junta receptora de votos", "junta receptora", "miembros de jrv",
            "mesa electoral", "mesa de votacion", "integrantes de jrv", "mesa"
        ),
        "vigilante" to listOf(
            "vigilante", "vigilantes", "vigilante de partido", "fiscal de mesa",
            "representante de partido", "vigilancia partidaria", "vigilante suplente"
        ),
        "pnc" to listOf(
            "pnc", "policia", "policía", "policia nacional civil", "agente",
            "agentes", "fuerza publica", "seguridad publica", "agente policial"
        ),
        "dui" to listOf(
            "dui", "documento unico de identidad", "documento de identidad",
            "cedula", "identificacion", "documento unico", "carnet"
        ),
        "acta" to listOf(
            "acta", "actas", "acta de escrutinio", "acta de instalacion",
            "acta de cierre", "hoja de conteo", "documento de escrutinio"
        ),
        "urna" to listOf(
            "urna", "urnas", "caja de votos", "deposito de votos", "caja receptora"
        ),
        "escrutinio" to listOf(
            "escrutinio", "conteo", "conteo de votos", "escrutinio preliminar",
            "contar votos", "calificacion de votos", "escrutinio final"
        ),
        "instalacion" to listOf(
            "instalacion", "instalación", "apertura", "armar mesa", "instalar mesa",
            "hora de instalacion", "6 am", "7 am", "inicio de votacion"
        ),
        "tinta" to listOf(
            "tinta", "tinta indeleble", "marcado de dedo", "dedo manchado",
            "manchador", "liquido indeleble"
        ),
        "padron" to listOf(
            "padron", "padrón", "padron electoral", "padrón electoral",
            "lista de electores", "registro electoral", "listado"
        ),
        "jefe_centro" to listOf(
            "jefe de centro", "jefes de centro", "coordinador de centro",
            "delegado tse", "supervisor tse", "soporte tse"
        ),
        "fiscalia" to listOf(
            "fiscalia", "fiscalía", "fgr", "fiscal electoral", "representante fiscal",
            "ministerio publico"
        ),
        "ley_seca" to listOf(
            "ley seca", "alcohol", "bebidas alcoholicas", "cerveza", "licor",
            "ebrio", "embriaguez", "estado de ebriedad", "borracho"
        ),
        "voto_nulo" to listOf(
            "voto nulo", "nulo", "anulado", "voto invalido", "estropeado",
            "papeleta rota", "marca fuera"
        ),
        "voto_impugnado" to listOf(
            "voto impugnado", "impugnado", "impugnacion", "impugnar voto",
            "reclamado", "objecion"
        ),
        "voto_asistido" to listOf(
            "voto asistido", "asistido", "discapacidad", "ciego", "no vidente",
            "silla de ruedas", "adulto mayor", "anciano", "tercera edad", "embarazada", "gravidez"
        ),
        "permiso_laboral" to listOf(
            "permiso laboral", "trabajo", "patrono", "empleador", "empresa",
            "sueldo", "goce de sueldo", "permiso", "art 113", "descanso"
        )
    )

    /**
     * Finds matching canonical concepts from words in text.
     */
    fun findCanonicalConcepts(text: String): Set<String> {
        val clean = ElectoralNormalizer.stripAccents(text.lowercase())
        val matchedConcepts = mutableSetOf<String>()

        for ((concept, synonyms) in SYNONYM_GROUPS) {
            for (synonym in synonyms) {
                if (ElectoralNormalizer.containsWholeWord(clean, synonym)) {
                    matchedConcepts.add(concept)
                    break
                }
            }
        }
        return matchedConcepts
    }

    private val NORMALIZED_GROUPS: List<List<String>> by lazy {
        SYNONYM_GROUPS.values.map { group -> group.map { ElectoralNormalizer.stripAccents(it.lowercase()) } }
    }

    private val groupsCache = java.util.concurrent.ConcurrentHashMap<String, Set<Int>>()

    private fun groupsOf(cleanTerm: String): Set<Int> = groupsCache.getOrPut(cleanTerm) {
        val result = mutableSetOf<Int>()
        NORMALIZED_GROUPS.forEachIndexed { index, group ->
            if (group.any { it == cleanTerm || ElectoralNormalizer.containsWholeWord(cleanTerm, it) }) {
                result.add(index)
            }
        }
        result
    }

    /**
     * Checks if two terms or tokens share a synonym group
     */
    fun areSynonyms(termA: String, termB: String): Boolean {
        val cleanA = ElectoralNormalizer.stripAccents(termA.lowercase())
        val cleanB = ElectoralNormalizer.stripAccents(termB.lowercase())
        if (cleanA == cleanB) return true
        val a = groupsOf(cleanA)
        if (a.isEmpty()) return false
        val b = groupsOf(cleanB)
        return a.any { it in b }
    }

    /**
     * Expands a list of tokens with their corresponding synonyms
     */
    fun expandSynonyms(tokens: List<String>): List<String> {
        val expanded = mutableListOf<String>()
        expanded.addAll(tokens)

        for (token in tokens) {
            for ((_, synonyms) in SYNONYM_GROUPS) {
                val isMatch = synonyms.any { ElectoralNormalizer.stripAccents(it.lowercase()) == token }
                if (isMatch) {
                    expanded.addAll(synonyms.map { ElectoralNormalizer.stripAccents(it.lowercase()) })
                }
            }
        }
        return expanded.distinct()
    }
}
