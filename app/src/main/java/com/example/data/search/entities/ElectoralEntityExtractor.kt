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

        // Extract Actors
        val actors = mutableListOf<String>()
        for ((actorKey, patterns) in ACTOR_PATTERNS) {
            if (patterns.any { clean.contains(ElectoralNormalizer.stripAccents(it)) }) {
                actors.add(actorKey)
            }
        }

        // Extract Objects
        val objects = mutableListOf<String>()
        for ((objectKey, patterns) in OBJECT_PATTERNS) {
            if (patterns.any { clean.contains(ElectoralNormalizer.stripAccents(it)) }) {
                objects.add(objectKey)
            }
        }

        // Extract Actions
        val actions = mutableListOf<String>()
        for ((actionKey, patterns) in ACTION_PATTERNS) {
            if (patterns.any { clean.contains(ElectoralNormalizer.stripAccents(it)) }) {
                actions.add(actionKey)
            }
        }

        // Extract Places
        val places = mutableListOf<String>()
        for ((placeKey, patterns) in PLACE_PATTERNS) {
            if (patterns.any { clean.contains(ElectoralNormalizer.stripAccents(it)) }) {
                places.add(placeKey)
            }
        }

        // Extract Moments
        val moments = mutableListOf<String>()
        for ((momentKey, patterns) in MOMENT_PATTERNS) {
            if (patterns.any { clean.contains(ElectoralNormalizer.stripAccents(it)) }) {
                moments.add(momentKey)
            }
        }

        // Detect Context
        val context = detectContext(clean, actors, objects, actions, moments)

        // Detect Intent
        val intent = detectIntent(clean, actions)

        val canonicalConcepts = ElectoralSynonymRegistry.findCanonicalConcepts(clean)

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
            "arma_de_fuego" in objects || "amenaza" in actions || "pnc" in actors || clean.contains("seguridad") || clean.contains("delito") -> "seguridad_electoral"
            "dui" in objects || clean.contains("cedula") || clean.contains("apariencia") || clean.contains("identidad") -> "identificacion_elector"
            "instalacion" in moments || clean.contains("instalar") || clean.contains("6 am") -> "instalacion_jrv"
            "escrutinio" in moments || "contar" in actions || "acta" in objects || clean.contains("escrutinio") -> "escrutinio_conteo"
            "tinta" in objects || clean.contains("manchado") -> "proceso_votacion"
            clean.contains("discapacidad") || clean.contains("asistido") || clean.contains("ciego") || clean.contains("embarazada") -> "inclusion_asistencia"
            "vigilante" in actors || "credencial" in objects -> "acreditacion_vigilancia"
            clean.contains("permiso") || clean.contains("trabajo") || clean.contains("sueldo") || clean.contains("patrono") -> "derechos_laborales"
            else -> "proceso_votacion"
        }
    }

    private fun detectIntent(clean: String, actions: List<String>): String {
        return when {
            clean.contains("que dice el reglamento") || clean.contains("que dice la ley") || clean.contains("segun el codigo") || clean.contains("fundamento") || clean.contains("articulo") -> "consulta_legal_normativa"
            clean.contains("que debo hacer") || clean.contains("que hago") || clean.contains("como actuar") || clean.contains("cual es el procedimiento") || clean.contains("pasos a seguir") || clean.contains("como se hace") -> "procedimiento"
            clean.contains("se puede") || clean.contains("esta permitido") || clean.contains("es permitido") || clean.contains("puedo") || clean.contains("puede un") -> "consulta_permiso_prohibicion"
            clean.contains("prohibido") || clean.contains("prohibicion") || clean.contains("sancion") || clean.contains("castigo") || clean.contains("delito") || clean.contains("multa") -> "prohibicion_sancion"
            clean.contains("quien puede") || clean.contains("quienes pueden") || clean.contains("autorizados") -> "competencia_quien_puede"
            clean.contains("quien no puede") || clean.contains("quienes no pueden") -> "quien_no_puede"
            clean.contains("funciones") || clean.contains("papel") || clean.contains("atribuciones") || clean.contains("rol") -> "funciones_atribuciones"
            clean.contains("derechos") -> "derechos"
            "amenaza" in actions -> "procedimiento_emergencia"
            else -> "procedimiento"
        }
    }
}
