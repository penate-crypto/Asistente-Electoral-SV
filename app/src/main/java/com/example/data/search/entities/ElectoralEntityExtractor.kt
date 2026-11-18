package com.example.data.search.entities

import com.example.data.search.normalization.ElectoralNormalizer
import com.example.data.search.synonyms.ElectoralSynonymRegistry

data class ExtractedEntities(
    val actors: List<String>,
    val objects: List<String>,
    val actions: List<String>,
    val places: List<String>,
    val moments: List<String>,
    val context: String,
    val intent: String,
    val canonicalConcepts: Set<String>
)

object ElectoralEntityExtractor {

    private val ACTOR_PATTERNS = mapOf(
        "vigilante" to listOf("vigilante", "fiscal de mesa", "representante de partido", "fiscal partidario"),
        "votante" to listOf("votante", "elector", "ciudadano", "persona", "sujeto"),
        "miembro_jrv" to listOf("miembro de jrv", "jrv", "integrante de mesa", "mesa receptora"),
        "presidente" to listOf("presidente", "presidenta", "presidente de jrv"),
        "secretario" to listOf("secretario", "secretaria", "secretario de jrv"),
        "vocal" to listOf("vocal", "primer vocal", "segundo vocal", "tercer vocal"),
        "pnc" to listOf("pnc", "policia", "policia nacional civil", "agente policial", "fuerza armada"),
        "fgr" to listOf("fgr", "fiscal", "fiscal electoral", "fiscalia", "fiscal general"),
        "jefe_centro" to listOf("jefe de centro", "coordinador", "delegado tse", "supervisor tse"),
        "observador" to listOf("observador", "observador internacional", "observador nacional"),
        "patrono" to listOf("patrono", "empleador", "jefe", "empresa", "trabajo")
    )

    private val OBJECT_PATTERNS = mapOf(
        "arma_de_fuego" to listOf("arma", "pistola", "revolver", "rifle", "escopeta", "fusil", "arma de fuego", "cuchillo", "machete"),
        "dui" to listOf("dui", "documento de identidad", "cedula", "identificacion", "documento unico"),
        "papeleta" to listOf("papeleta", "boleta", "voto", "hoja de votacion"),
        "urna" to listOf("urna", "caja", "deposito"),
        "acta" to listOf("acta", "acta de escrutinio", "acta de instalacion", "acta de cierre", "hoja de conteo"),
        "padron" to listOf("padron", "padron electoral", "lista", "listado electoral"),
        "tinta" to listOf("tinta", "tinta indeleble", "marcador", "manchador"),
        "credencial" to listOf("credencial", "acreditacion", "gafete", "carnet"),
        "celular" to listOf("celular", "telefono", "camara", "foto", "fotografia", "grabar", "video"),
        "alcohol" to listOf("alcohol", "cerveza", "licor", "bebida", "ley seca")
    )

    private val ACTION_PATTERNS = mapOf(
        "amenaza" to listOf("amenazar", "amenazo", "amenaza", "apunto", "apuntando", "intimidar", "intimido", "coaccionar", "coacciono", "violencia", "agredir", "agredio"),
        "ingresar" to listOf("ingresar", "ingreso", "entrar", "entro", "acceder", "llego", "llegar"),
        "portar" to listOf("portar", "porta", "lleva", "llevaba", "cargar", "tenia", "traia", "saco", "mostrar"),
        "votar" to listOf("votar", "voto", "ejercer el sufragio", "sufragio", "emitir voto"),
        "entregar" to listOf("entregar", "entrego", "dar", "dio", "recibir", "paso", "entrega", "recepcion"),
        "negar" to listOf("negar", "nego", "rechazar", "rechazo", "impedir", "impidio", "no dejar"),
        "fotografiar" to listOf("tomar foto", "fotografiar", "foto", "filmar", "grabar", "usar celular"),
        "romper" to listOf("romper", "rompio", "manchar", "danar", "destruir", "alterar"),
        "impugnar" to listOf("impugnar", "impugno", "reclamar", "objetar", "anular"),
        "instalar" to listOf("instalar", "instalo", "armar", "apertura", "abrir", "instalacion"),
        "cerrar" to listOf("cerrar", "cierre", "cierre de votacion", "finalizar", "concluir", "5 pm", "5:00", "cinco de la tarde"),
        "contar" to listOf("contar", "conteo", "escrutar", "escrutinio", "clasificar", "separar"),
        "verificar" to listOf("verificar", "cotejar", "comparar", "revisar", "inspeccionar"),
        "sustituir" to listOf("sustituir", "sustitucion", "reemplazar", "suplente")
    )

    private val PLACE_PATTERNS = mapOf(
        "centro_de_votacion" to listOf("centro de votacion", "centro electoral", "escuela", "recinto", "instalaciones"),
        "mesa_jrv" to listOf("mesa", "jrv", "mesa receptora", "mesa electoral"),
        "cabina_secreta" to listOf("cabina", "recinto secreto", "atril", "espacio secreto")
    )

    private val MOMENT_PATTERNS = mapOf(
        "instalacion" to listOf("instalacion", "6 am", "seis de la mañana", "madrugada", "armar"),
        "apertura" to listOf("apertura", "7 am", "siete de la mañana", "abrir votacion"),
        "votacion" to listOf("durante el dia", "votando", "al votar", "al entregar", "votacion"),
        "cierre" to listOf("cierre", "5 pm", "cinco de la tarde", "cerrar"),
        "escrutinio" to listOf("escrutinio", "conteo", "despues de las 5", "al contar")
    )

    fun extract(normalizedText: String, rawQuery: String): ExtractedEntities {
        val clean = ElectoralNormalizer.stripAccents(rawQuery.lowercase())
            .replace("[^a-z0-9\\s]".toRegex(), " ")
            .replace("\\s+".toRegex(), " ")
            .trim()
        val isArmarMesaQuery = Regex("\\b(?:se\\s+)?arma(?:r)?\\s+(?:la\\s+)?(?:mesa|jrv)\\b").containsMatchIn(clean) &&
            !listOf("pistola", "revolver", "rifle", "escopeta", "fusil", "cuchillo", "machete", "arma de fuego", "arma blanca")
                .any { ElectoralNormalizer.containsWholeWord(clean, it) }

        // Extract Actors
        val actors = mutableListOf<String>()
        for ((actorKey, patterns) in ACTOR_PATTERNS) {
            if (patterns.any { ElectoralNormalizer.containsWholeWord(clean, it) }) {
                actors.add(actorKey)
            }
        }

        // Extract Objects
        val objects = mutableListOf<String>()
        for ((objectKey, patterns) in OBJECT_PATTERNS) {
            if (objectKey == "arma_de_fuego" && isArmarMesaQuery) continue
            if (patterns.any { ElectoralNormalizer.containsWholeWord(clean, it) }) {
                objects.add(objectKey)
            }
        }

        // Extract Actions
        val actions = mutableListOf<String>()
        for ((actionKey, patterns) in ACTION_PATTERNS) {
            if (patterns.any { ElectoralNormalizer.containsWholeWord(clean, it) }) {
                actions.add(actionKey)
            }
        }

        // Extract Places
        val places = mutableListOf<String>()
        for ((placeKey, patterns) in PLACE_PATTERNS) {
            if (patterns.any { ElectoralNormalizer.containsWholeWord(clean, it) }) {
                places.add(placeKey)
            }
        }

        // Extract Moments
        val moments = mutableListOf<String>()
        for ((momentKey, patterns) in MOMENT_PATTERNS) {
            if (patterns.any { ElectoralNormalizer.containsWholeWord(clean, it) }) {
                moments.add(momentKey)
            }
        }

        // Detect Context
        val context = detectContext(clean, actors, objects, actions, moments)

        // Detect Intent
        val intent = detectIntent(clean, actions)

        val canonicalConcepts = ElectoralSynonymRegistry.findCanonicalConcepts(normalizedText.ifBlank { clean })

        return ExtractedEntities(
            actors = actors,
            objects = objects,
            actions = actions,
            places = places,
            moments = moments,
            context = context,
            intent = intent,
            canonicalConcepts = canonicalConcepts
        )
    }

    private fun detectContext(
        clean: String,
        actors: List<String>,
        objects: List<String>,
        actions: List<String>,
        moments: List<String>
    ): String {
        return when {
            "arma_de_fuego" in objects || "amenaza" in actions || "pnc" in actors ||
                ElectoralNormalizer.containsWholeWord(clean, "seguridad") ||
                ElectoralNormalizer.containsWholeWord(clean, "delito") -> "seguridad_electoral"
            "dui" in objects ||
                ElectoralNormalizer.containsWholeWord(clean, "cedula") ||
                ElectoralNormalizer.containsWholeWord(clean, "apariencia") ||
                ElectoralNormalizer.containsWholeWord(clean, "identidad") -> "identificacion_elector"
            "instalacion" in moments ||
                ElectoralNormalizer.containsWholeWord(clean, "instalar") ||
                ElectoralNormalizer.containsWholeWord(clean, "6 am") -> "instalacion_jrv"
            "escrutinio" in moments || "contar" in actions || "acta" in objects ||
                ElectoralNormalizer.containsWholeWord(clean, "escrutinio") -> "escrutinio_conteo"
            "tinta" in objects ||
                ElectoralNormalizer.containsWholeWord(clean, "manchado") -> "proceso_votacion"
            ElectoralNormalizer.containsWholeWord(clean, "discapacidad") ||
                ElectoralNormalizer.containsWholeWord(clean, "asistido") ||
                ElectoralNormalizer.containsWholeWord(clean, "ciego") ||
                ElectoralNormalizer.containsWholeWord(clean, "embarazada") -> "inclusion_asistencia"
            "vigilante" in actors || "credencial" in objects -> "acreditacion_vigilancia"
            ElectoralNormalizer.containsWholeWord(clean, "permiso") ||
                ElectoralNormalizer.containsWholeWord(clean, "trabajo") ||
                ElectoralNormalizer.containsWholeWord(clean, "sueldo") ||
                ElectoralNormalizer.containsWholeWord(clean, "patrono") -> "derechos_laborales"
            else -> "proceso_votacion"
        }
    }

    private fun detectIntent(clean: String, actions: List<String>): String {
        return when {
            ElectoralNormalizer.containsWholeWord(clean, "que dice el reglamento") ||
                ElectoralNormalizer.containsWholeWord(clean, "que dice la ley") ||
                ElectoralNormalizer.containsWholeWord(clean, "segun el codigo") ||
                ElectoralNormalizer.containsWholeWord(clean, "fundamento") ||
                ElectoralNormalizer.containsWholeWord(clean, "articulo") -> "consulta_legal_normativa"
            ElectoralNormalizer.containsWholeWord(clean, "quien puede") ||
                ElectoralNormalizer.containsWholeWord(clean, "quienes pueden") ||
                ElectoralNormalizer.containsWholeWord(clean, "autorizados") -> "competencia_quien_puede"
            ElectoralNormalizer.containsWholeWord(clean, "quien no puede") ||
                ElectoralNormalizer.containsWholeWord(clean, "quienes no pueden") -> "quien_no_puede"
            ElectoralNormalizer.containsWholeWord(clean, "que debo hacer") ||
                ElectoralNormalizer.containsWholeWord(clean, "que hago") ||
                ElectoralNormalizer.containsWholeWord(clean, "como actuar") ||
                ElectoralNormalizer.containsWholeWord(clean, "cual es el procedimiento") ||
                ElectoralNormalizer.containsWholeWord(clean, "pasos a seguir") ||
                ElectoralNormalizer.containsWholeWord(clean, "como se hace") -> "procedimiento"
            ElectoralNormalizer.containsWholeWord(clean, "se puede") ||
                ElectoralNormalizer.containsWholeWord(clean, "esta permitido") ||
                ElectoralNormalizer.containsWholeWord(clean, "es permitido") ||
                ElectoralNormalizer.containsWholeWord(clean, "puedo") ||
                ElectoralNormalizer.containsWholeWord(clean, "puede un") -> "consulta_permiso_prohibicion"
            ElectoralNormalizer.containsWholeWord(clean, "prohibido") ||
                ElectoralNormalizer.containsWholeWord(clean, "prohibicion") ||
                ElectoralNormalizer.containsWholeWord(clean, "sancion") ||
                ElectoralNormalizer.containsWholeWord(clean, "castigo") ||
                ElectoralNormalizer.containsWholeWord(clean, "delito") ||
                ElectoralNormalizer.containsWholeWord(clean, "multa") -> "prohibicion_sancion"
            ElectoralNormalizer.containsWholeWord(clean, "funciones") ||
                ElectoralNormalizer.containsWholeWord(clean, "papel") ||
                ElectoralNormalizer.containsWholeWord(clean, "atribuciones") ||
                ElectoralNormalizer.containsWholeWord(clean, "rol") -> "funciones_atribuciones"
            ElectoralNormalizer.containsWholeWord(clean, "derechos") -> "derechos"
            "amenaza" in actions -> "procedimiento_emergencia"
            else -> "procedimiento"
        }
    }
}
