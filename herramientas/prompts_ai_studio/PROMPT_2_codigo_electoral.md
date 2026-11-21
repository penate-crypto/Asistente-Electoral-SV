# PROMPT 2 para AI Studio — Banco Código Electoral (buscar artículos del código)

Copia todo lo que está debajo de la línea y pégalo en AI Studio. Adjunta también la imagen `ejemplos_codigo_electoral.png` (los 3 ejemplos de cómo se ve) y el archivo `banco_codigo_electoral.json`.

---

Quiero agregar un SEGUNDO banco de conocimiento: el Código Electoral completo (297 artículos). Todo funciona SIN internet y SIN tokens: es búsqueda local, igual que el banco principal. No cambies el banco principal ni el motor actual, solo agrega lo siguiente.

CÓMO DEBE FUNCIONAR PARA EL USUARIO
- Si el usuario escribe (o dice por voz) "código electoral" en su pregunta, la búsqueda va PRIMERO a este banco. Ejemplos:
  - "en el código electoral dónde aparece lo de la JRV" → muestra los artículos del tema (máximo 5) y un botón "Ver más (N)".
  - "código electoral hora de cierre" → como solo hay un artículo claro, muestra SOLO el Art. 198.
  - "código electoral artículo 198" o "artículos 200 al 203" → muestra esos artículos.
  - "código electoral borrachos / bolo / ley seca" → Art. 284 (bebidas embriagantes).
- Si el Código Electoral no tiene resultado, la app sigue con el flujo normal (banco de 154 situaciones). Si el usuario NO escribe "código electoral", no cambia nada.

PASOS (en este orden):

1) Crea `app/src/main/assets/codigo_electoral/banco_codigo_electoral.json` con el archivo adjunto (no lo modifiques ni lo resumas; son 297 artículos, ~750 KB).

2) Crea `app/src/main/java/com/example/data/search/codigo/ElectoralCodeBank.kt` con EXACTAMENTE este código:

```kotlin
package com.example.data.search.codigo

import android.content.Context
import com.example.data.search.normalization.ElectoralNormalizer
import org.json.JSONObject

/** Un artículo del Código Electoral, tal como está en assets/codigo_electoral/banco_codigo_electoral.json. */
data class CodeArticle(
    val id: String,
    val numero: String,
    val titulo: String,
    val tituloDelCodigo: String,
    val capitulo: String,
    val textoLiteral: String,
    val textoLegible: String,
    val derogado: Boolean,
    val paginaPdf: Int,
    val ubicacionPdf: String,
    val pdfDocId: String,
    val palabrasClave: List<String>,
    val sinonimos: List<String>,
    val articulosCitados: List<String>,
    val situacionesRelacionadas: List<String>,
    // campos normalizados (sin tildes, minúsculas) para buscar rápido
    internal val tituloN: String,
    internal val cuerpoN: String,
    internal val claveN: List<String>,
    internal val sinN: List<String>,
    internal val tituloSet: Set<String>,
    internal val cuerpoSet: Set<String>,
    internal val claveSet: Set<String>,
    internal val sinSet: Set<String>,
    internal val secN: List<String>,
    internal val secSet: Set<String>,
    internal val tituloStems: Set<String>,
    internal val claveStems: Set<String>,
    internal val sinStems: Set<String>
)

/** Un banco de artículos de una ley (Código Electoral, Constitución...), leído de un JSON en assets. */
class LawBank(private val assetPath: String) {
    @Volatile private var articles: List<CodeArticle> = emptyList()
    @Volatile private var byNumber: Map<String, CodeArticle> = emptyMap()

    fun init(context: Context) {
        try {
            val json = context.assets.open(assetPath).bufferedReader().use { it.readText() }
            val arr = JSONObject(json).getJSONArray("articulos")
            val list = ArrayList<CodeArticle>(arr.length())
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                fun strs(k: String) = o.optJSONArray(k)?.let { a -> (0 until a.length()).map { a.getString(it) } } ?: emptyList()
                val titulo = o.getString("titulo")
                val legible = o.getString("texto_legible")
                list.add(CodeArticle(
                    id = o.getString("id"), numero = o.getString("numero"), titulo = titulo,
                    tituloDelCodigo = o.optString("titulo_del_codigo"), capitulo = o.optString("capitulo"),
                    textoLiteral = o.getString("texto_literal"), textoLegible = legible,
                    derogado = o.optString("estado") == "derogado", paginaPdf = o.optInt("pagina_pdf", 1),
                    ubicacionPdf = o.optString("ubicacion_pdf"), pdfDocId = o.optString("pdf_doc_id"),
                    palabrasClave = strs("palabras_clave"), sinonimos = strs("sinonimos"),
                    articulosCitados = strs("articulos_citados"), situacionesRelacionadas = strs("situaciones_relacionadas"),
                    tituloN = n(titulo), cuerpoN = n(legible),
                    claveN = strs("palabras_clave").map { n(it) }, sinN = strs("sinonimos").map { n(it) },
                    tituloSet = ElectoralNormalizer.tokenizeAndSingularize(n(titulo)).toSet(),
                    cuerpoSet = ElectoralNormalizer.tokenizeAndSingularize(n(legible)).toSet(),
                    claveSet = strs("palabras_clave").flatMap { ElectoralNormalizer.tokenizeAndSingularize(n(it)) }.toSet(),
                    sinSet = strs("sinonimos").flatMap { ElectoralNormalizer.tokenizeAndSingularize(n(it)) }.toSet(),
                    secN = strs("sinonimos_secundarios").map { n(it) },
                    secSet = strs("sinonimos_secundarios").flatMap { ElectoralNormalizer.tokenizeAndSingularize(n(it)) }.toSet(),
                    tituloStems = ElectoralNormalizer.tokenizeAndSingularize(n(titulo)).map { stem(it) }.toSet(),
                    claveStems = strs("palabras_clave").flatMap { ElectoralNormalizer.tokenizeAndSingularize(n(it)) }.map { stem(it) }.toSet(),
                    sinStems = (strs("sinonimos") + strs("sinonimos_secundarios")).flatMap { ElectoralNormalizer.tokenizeAndSingularize(n(it)) }.map { stem(it) }.toSet()
                ))
            }
            articles = list
            dfCache.clear()
            byNumber = list.associateBy { it.numero.lowercase() }
        } catch (e: Exception) {
            articles = emptyList(); byNumber = emptyMap() // sin archivo: la app sigue con el banco principal
        }
    }

    /** En cuántos artículos aparece la palabra (para dar más peso a palabras raras como "armas" que a "votacion"). */
    private val dfCache = java.util.concurrent.ConcurrentHashMap<String, Int>()
    fun docFreq(word: String): Int = dfCache.getOrPut(word) {
        articles.count { word in it.cuerpoSet || word in it.tituloSet || word in it.sinSet || word in it.secSet }
    }

    fun all(): List<CodeArticle> = articles
    fun findByNumber(numero: String): CodeArticle? = byNumber[numero.lowercase()]
    fun isLoaded() = articles.isNotEmpty()

    /** Raíz aproximada: abre/abrir/abren, vota/votar/votan comparten sus primeras 5 letras. */
    fun stem(w: String) = if (w.length >= 6) w.take(5) else w

    private fun n(s: String) = ElectoralNormalizer.stripAccents(s.lowercase())
        .replace("[^a-z0-9\\s]".toRegex(), " ").replace("\\s+".toRegex(), " ").trim()
}

/** Código Electoral (297 artículos). */
val ElectoralCodeBank = LawBank("codigo_electoral/banco_codigo_electoral.json")

/** Constitución de la República (274 artículos). */
val ElectoralConstitutionBank = LawBank("constitucion/banco_constitucion.json")
```

3) Crea `app/src/main/java/com/example/data/search/codigo/ElectoralCodeSearch.kt` con EXACTAMENTE este código:

```kotlin
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
```

4) En `ElectoralKnowledgeBase.init(context)` agrega como primera línea (junto a la del Prompt 1 si ya existe):

```kotlin
com.example.data.search.codigo.ElectoralCodeBank.init(context)
```

5) Donde la pantalla de chat (y el botón de voz) hoy llama a `ElectoralSearchEngine.search(pregunta)`, haz esto ANTES:

```kotlin
val codigo = ElectoralCodeSearch.search(pregunta)
if (codigo != null) { /* mostrar la respuesta del Código Electoral (paso 6) */ }
else { /* flujo normal que ya existe */ }
```

6) PANTALLA (mira la imagen adjunta; mantén los mismos colores y estilo de las tarjetas que ya tiene la app). Para cada artículo de `codigo.articles` muestra una tarjeta:
- Encabezado: "📖 Código Electoral · Art. {numero}" y debajo el título del artículo (`titulo`) y el capítulo (`capitulo`) en letra pequeña.
- Cuerpo: `textoLegible`. Un enlace "Ver texto original del PDF" que cambia a `textoLiteral` (los artículos reformados vienen en MAYÚSCULAS en el PDF; `textoLegible` ya los trae en minúsculas normales).
- Si `derogado` es true, muestra la etiqueta "Derogado".
- Botón "Abrir en la biblioteca (pág. {paginaPdf})": abre el lector de PDF del Código Electoral (`pdfDocId` = "codigo_electoral_decreto_413") en la página `paginaPdf`, igual que ya se abren las fuentes del banco principal.
- Si `situacionesRelacionadas` no está vacía: chips "También en situaciones:" con cada ID; al tocar uno, abre esa situación con `ElectoralKnowledgeBase.findById(id)` como ya se muestran las situaciones relacionadas.
- Si `codigo.moreCount > 0`: botón "Ver más ({moreCount})" que muestra `codigo.moreArticles` con el mismo diseño.
- Si `codigo.kind == CodeMatchKind.ARTICLE_NUMBER`, arriba escribe "Artículo(s) solicitado(s)"; si es TOPIC escribe "Artículos del Código Electoral sobre: {codigo.topic}".
- Un aviso fijo pequeño al final: "Texto tomado del Código Electoral (Decreto 413). Verifícalo con el PDF de la biblioteca."

7) NO uses Gemini ni internet para nada de esto. NO resumas ni reescribas los artículos con IA en esta versión: se muestra el texto del Código tal cual.

8) Pruebas que debes hacer en la app: "código electoral hora de cierre" (solo Art. 198), "en el código electoral dónde aparece lo de la JRV" (varios + Ver más), "código electoral artículo 290", "código electoral bolo" (Art. 284), "código electoral artículos 200 al 203" (4 tarjetas), "cómo se instala la mesa" (debe seguir el flujo normal, sin tarjetas del Código).

Resultados de mis pruebas del motor real: en una prueba ciega de 41 preguntas, 33 acertaron el artículo en primer lugar y en 38 el artículo esperado salió entre los primeros 5.
