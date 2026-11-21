package com.example.data.search.codigo

import com.example.data.search.normalization.ElectoralNormalizer

enum class CodeMatchKind { ARTICLE_NUMBER, TOPIC, NONE }

data class CodeSearchResult(
    /** Artículos a mostrar (1 si el tema tiene un solo artículo claro; hasta 5 si hay varios). */
    val articles: List<CodeArticle>,
    /** Cuántos artículos más coinciden y no se muestran (botón "ver más"). */
    val moreCount: Int,
    val moreArticles: List<CodeArticle>,
    val kind: CodeMatchKind,
    /** Lo que se buscó, sin la frase "en el código electoral" (para mostrar al usuario). */
    val topic: String
)

/**
 * Búsqueda dentro de un banco de artículos (Código Electoral o Constitución). Todo local: sin internet y sin tokens.
 * Uso: val r = ElectoralCodeSearch.search(pregunta); si r != null, mostrar sus artículos;
 * si es null (no mencionó la ley o no hubo coincidencias), probar el siguiente banco o el buscador normal.
 * [triggerFn] devuelve la posición de la(s) palabra(s) que nombran la ley ("codigo electoral") o -1.
 */
class LawSearch(private val bank: LawBank, private val triggerFn: (List<String>) -> Int, private val triggerLen: Int, private val bodyWeight: Double = 2.0) {
    /** Palabras que la gente usa y la ley dice distinto (funciones = atribuciones). */
    private val QUERY_EQUIV = mapOf("funcion" to "atribucion", "reformar" to "reforma", "obligacion" to "deber", "requisito" to "requiere")
    private val MAX_SHOWN = 5
    private val RANGE_LIMIT = 12

    private val TRIGGER_FILLER = setOf(
        "en", "el", "la", "los", "las", "del", "de", "al", "codigo", "electoral", "donde", "aparece", "aparecen", "dice", "dicen",
        "que", "cual", "cuales", "es", "esta", "estan", "hay", "habla", "hablan", "menciona", "mencionan", "regula", "regulan",
        "articulo", "articulos", "art", "numero", "segun", "busca", "buscame", "muestrame", "dime", "necesito", "quiero", "ver",
        "saber", "sobre", "acerca", "con", "respecto", "referente", "relacionado", "relacionados", "lo", "y", "o", "un", "una",
        "por", "para", "me", "mi", "si", "se", "ce", "como", "cuando", "puedo", "encontrar", "encuentro", "ubica", "ubicado",
        "viene", "vienen", "republica", "constitucion", "hasta", "dia", "tal", "tiene", "tienen", "puede", "pueden", "esta", "escrito", "establece", "establecen", "indica", "indican", "tema", "ley"
    )

    fun mentionsCode(cleanText: String): Boolean = triggerFn(cleanText.split(" ").filter { it.isNotBlank() }) >= 0

    fun search(rawQuery: String): CodeSearchResult? {
        if (!bank.isLoaded()) return null
        val clean = ElectoralNormalizer.stripAccents(rawQuery.lowercase())
            .replace("[^a-z0-9\\s-]".toRegex(), " ").replace("\\s+".toRegex(), " ").trim()
        if (!mentionsCode(clean)) return null

        // 1) Búsqueda directa por número: "artículo 198", "art. 200 al 203", "del 198 al 200"
        val nums = articleNumbers(clean)
        if (nums.isNotEmpty()) {
            val found = nums.mapNotNull { bank.findByNumber(it) }
            if (found.isNotEmpty()) {
                return CodeSearchResult(found.take(MAX_SHOWN), maxOf(0, found.size - MAX_SHOWN), found.drop(MAX_SHOWN),
                    CodeMatchKind.ARTICLE_NUMBER, nums.joinToString(", "))
            }
            return null // ese artículo no existe en el Código: que responda el buscador normal
        }

        // 2) Búsqueda por tema
        val all = clean.split(" ").filter { it.isNotBlank() }
        val trig = triggerFn(all)
        val topicTokens = all.filterIndexed { i, w -> (i < trig || i >= trig + triggerLen) && (w !in TRIGGER_FILLER || w == "electoral") }
        val topic = topicTokens.joinToString(" ")
        if (topic.isBlank()) return null
        val keywords = keywordsOf(topicTokens)
        if (keywords.isEmpty()) return null

        val scored = bank.all().mapNotNull { a ->
            val s = score(a, clean, topic, keywords)
            if (s > 0.0) a to s else null
        }.sortedByDescending { it.second }
        if (scored.isEmpty()) return null
        val top = scored.first().second
        // Si el mejor artículo pesa mucho más que el resto, solo se muestra ese (un artículo = una respuesta).
        val second = scored.getOrNull(1)?.second ?: 0.0
        // Tema amplio (una sola palabra que aparece en muchísimos artículos, ej. "JRV"): nunca se reduce a uno.
        val broad = keywords.size == 1 && bank.docFreq(keywords[0]) >= 25
        val relevant = if (top >= 2.0 * second && !broad) scored.take(1) else if (broad) scored.take(MAX_SHOWN) else scored.filter { it.second >= 0.55 * top }
        val shown = relevant.take(MAX_SHOWN).map { it.first }
        val more = scored.drop(shown.size).filter { broad || it.second >= 0.35 * top }.map { it.first }
        return CodeSearchResult(shown, more.size, more.take(20), CodeMatchKind.TOPIC, topic)
    }

    /** Palabras de la consulta: sin tildes, en singular y sin palabras vacías. No usa reescrituras del buscador general (fiscal→vigilante). */
    private fun keywordsOf(tokens: List<String>): List<String> =
        tokens.map { ElectoralNormalizer.singularize(it) }.filter { it.length > 2 && it !in TRIGGER_FILLER }
            .flatMap { w -> listOfNotNull(w, QUERY_EQUIV[w]) }.distinct()

    /** Solo para pruebas: puntaje de los mejores artículos. */
    fun debugScores(rawQuery: String): List<Pair<String, Double>> {
        val clean = ElectoralNormalizer.stripAccents(rawQuery.lowercase()).replace("[^a-z0-9\\s-]".toRegex(), " ").replace("\\s+".toRegex(), " ").trim()
        val all = clean.split(" ").filter { it.isNotBlank() }
        val trig = triggerFn(all)
        val toks = all.filterIndexed { i, w -> (i < trig || i >= trig + triggerLen) && (w !in TRIGGER_FILLER || w == "electoral") }
        val kws = keywordsOf(toks)
        println("   kws=$kws")
        return bank.all().map { it.numero to score(it, clean, toks.joinToString(" "), kws) }.sortedByDescending { it.second }.take(6)
    }

    private fun score(a: CodeArticle, clean: String, topic: String, kws: List<String>): Double {
        if (a.derogado) return 0.0
        var s = 0.0
        var matchedW = 0.0; var totalW = 0.0
        for (kw in kws) {
            var h = 0.0
            val nArt = bank.all().size + 1.0
            val idf = (kotlin.math.ln(nArt / (bank.docFreq(kw) + 1.0)) / kotlin.math.ln(nArt)).let { it * it * 4.0 }.coerceIn(0.15, 4.0)
            totalW += idf
            if (kw in a.tituloSet || ElectoralNormalizer.containsWholeWord(a.tituloN, kw)) h += 12.0
            if (kw in a.claveSet) h += 6.0
            if (kw in a.sinSet) h += 12.0 else if (kw in a.secSet) h += 4.0
            if (kw in a.cuerpoSet) h += bodyWeight
            if (h == 0.0 && kw.length >= 6) { // misma raíz (inscribo ~ inscripción): vale menos que la palabra exacta
                val st = bank.stem(kw)
                if (st in a.tituloStems) h += 7.0
                if (st in a.sinStems) h += 6.0 else if (st in a.claveStems) h += 3.0
            }
            if (h > 0) matchedW += idf
            s += h * idf
        }
        // frases completas (jerga o varias palabras): "estado de ebriedad", "junta receptora"
        for (p in a.sinN) if (p.contains(' ') && ElectoralNormalizer.containsWholeWord(clean, p)) s += 20.0
        for (p in a.secN) if (p.contains(' ') && ElectoralNormalizer.containsWholeWord(clean, p)) s += 8.0
        for (p in a.claveN) if (p.contains(' ') && ElectoralNormalizer.containsWholeWord(clean, p)) s += 10.0
        if (topic.isNotBlank() && ElectoralNormalizer.containsWholeWord(a.tituloN, topic)) s += 15.0
        // premiar artículos que cubren más palabras de la pregunta
        if (kws.size > 1 && totalW > 0) s *= (0.4 + 0.6 * matchedW / totalW)
        return s
    }

    /** Extrae números de artículo y rangos: "198", "200 al 203", "198 y 199", "214-a". */
    fun articleNumbers(clean: String): List<String> {
        val out = LinkedHashSet<String>()
        val rango = Regex("""(\d{1,3})\s*(?:al|a|hasta|-)\s*(\d{1,3})\b""")
        val used = mutableListOf<IntRange>()
        for (m in rango.findAll(clean)) {
            val a = m.groupValues[1].toInt(); val b = m.groupValues[2].toInt()
            if (b >= a && b - a < RANGE_LIMIT && (clean.contains("art") || clean.contains("del "))) {
                for (x in a..b) out.add(x.toString()); used.add(m.range)
            }
        }
        val rest = StringBuilder(clean)
        for (r in used) for (i in r) rest.setCharAt(i, ' ')
        val tieneArt = Regex("""\bart(?:iculos?)?\b""").containsMatchIn(clean)
        if (tieneArt || Regex("""\b\d{1,3}-[a-z]\b""").containsMatchIn(clean)) {
            for (m in Regex("""\b(\d{1,3}(?:-[a-z])?)\b""").findAll(rest.toString())) out.add(m.groupValues[1].uppercase().let { if (it.contains('-')) it else it })
        }
        return out.toList()
    }
}

// ---- Instancias: una por ley ----
private val T_CODIGO: (List<String>) -> Int = { t ->
    var r = -1
    for (i in t.indices) {
        val w = t[i]
        if (w == "ce" && i + 1 < t.size && t[i + 1] == "electoral") { r = i; break }
        if (isNear(w, "codigo", 2) || w == "cod") {
            val nx = t.getOrNull(i + 1) ?: continue
            if (isNear(nx, "electoral", 3) || nx == "elec") { r = i; break }
        }
    }
    r
}
private val T_CONSTITUCION: (List<String>) -> Int = { t -> t.indexOfFirst { it == "const" || isNear(it, "constitucion", 2) } }

/** "código electoral ..." */
val ElectoralCodeSearch = LawSearch(ElectoralCodeBank, T_CODIGO, 2)

/** "constitución ..." (también "constitución de la república") */
val ElectoralConstitutionSearch = LawSearch(ElectoralConstitutionBank, T_CONSTITUCION, 1, bodyWeight = 5.0)

internal fun isNear(a: String, b: String, maxDist: Int): Boolean {
    if (kotlin.math.abs(a.length - b.length) > maxDist || a.length < 3) return false
    return lev(a, b) <= maxDist
}

internal fun lev(a: String, b: String): Int {
    var prev = IntArray(b.length + 1) { it }
    for (i in 1..a.length) {
        val cur = IntArray(b.length + 1); cur[0] = i
        for (j in 1..b.length) cur[j] = minOf(prev[j] + 1, cur[j - 1] + 1, prev[j - 1] + if (a[i - 1] == b[j - 1]) 0 else 1)
        prev = cur
    }
    return prev[b.length]
}
