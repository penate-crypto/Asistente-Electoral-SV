# PROMPT 9 para AI Studio — «Buscar en libros» con bancos de artículos (Bloque C, partes 2 a 5)

Aplica DESPUÉS del Prompt 8. Este prompt REEMPLAZA a los Prompts 2 y 3 (ya no se activa escribiendo «código electoral»: ahora hay un botón «Buscar en libros»). Si ya aplicaste el 2 o el 3, sobrescribe sus archivos con los de aquí. Adjunta SOLO los 10 archivos de datos (.json) del punto 1. El código va escrito dentro de este prompt (punto 2).

---

Quiero que el chat tenga un botón «Buscar en libros». Al tocarlo se elige un libro (10 disponibles); después, la pregunta escrita o dictada con el micrófono se busca DENTRO de ese libro y se muestran tarjetas con el artículo (título, página, texto, botón para abrir el PDF subrayado). Todo local: sin internet y sin inteligencia artificial. Los bancos se cargan solo la primera vez que se usan (no al abrir la app).

## 1) Datos (assets) — copiar tal cual
- `app/src/main/assets/leyes/` (nueva carpeta, 8 archivos): `ley_partidos.json`, `ley_acceso_informacion.json`, `ley_sufragio_exterior.json`, `ley_reestructuracion_municipal.json`, `reglamento_observacion.json`, `candidaturas_no_partidarias.json`, `acuerdo_legislativo.json`, `codigo_penal_electoral.json`.
- `app/src/main/assets/codigo_electoral/banco_codigo_electoral.json` y `app/src/main/assets/constitucion/banco_constitucion.json` (si ya los tienes, reemplázalos por los adjuntos).

## 2) Código (crear o reemplazar con EXACTAMENTE este contenido)

### ElectoralCodeBank.kt (reemplazar completo)
`app/src/main/java/com/example/data/search/codigo/ElectoralCodeBank.kt`

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

    /** Carga el banco la primera vez que se necesita (no al abrir la app). Llamar fuera del hilo principal. */
    @Synchronized
    fun ensureLoaded(context: Context) {
        if (articles.isEmpty()) init(context)
    }

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

### ElectoralCodeSearch.kt (reemplazar completo)
`app/src/main/java/com/example/data/search/codigo/ElectoralCodeSearch.kt`

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

    /** Con palabra de activación ("código electoral ..."). Devuelve null si no la menciona. */
    fun search(rawQuery: String): CodeSearchResult? = run(rawQuery, requireTrigger = true)

    /** Búsqueda directa dentro de este banco (botón «Buscar en libros»): no necesita nombrar la ley. */
    fun searchDirect(rawQuery: String): CodeSearchResult? = run(rawQuery, requireTrigger = false)

    private fun run(rawQuery: String, requireTrigger: Boolean): CodeSearchResult? {
        if (!bank.isLoaded()) return null
        val clean = ElectoralNormalizer.stripAccents(rawQuery.lowercase())
            .replace("[^a-z0-9\\s-]".toRegex(), " ").replace("\\s+".toRegex(), " ").trim()
        if (clean.isBlank()) return null
        if (requireTrigger && !mentionsCode(clean)) return null

        // 1) Búsqueda directa por número: "artículo 198", "art. 200 al 203", "del 198 al 200" (en modo directo, también "198" o "5 al 8")
        val nums = articleNumbers(clean, direct = !requireTrigger)
        if (nums.isNotEmpty()) {
            val found = nums.mapNotNull { bank.findByNumber(it) }
            if (found.isNotEmpty()) {
                return CodeSearchResult(found.take(MAX_SHOWN), maxOf(0, found.size - MAX_SHOWN), found.drop(MAX_SHOWN),
                    CodeMatchKind.ARTICLE_NUMBER, nums.joinToString(", "))
            }
            return null // ese artículo no existe en este documento
        }

        // 2) Búsqueda por tema
        val all = clean.split(" ").filter { it.isNotBlank() }
        val trig = if (requireTrigger) triggerFn(all) else -1
        val topicTokens = all.filterIndexed { i, w ->
            (trig < 0 || i < trig || i >= trig + triggerLen) && (w !in TRIGGER_FILLER || w == "electoral")
        }
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
    fun articleNumbers(clean: String, direct: Boolean = false): List<String> {
        val out = LinkedHashSet<String>()
        val rango = Regex("""(\d{1,3})\s*(?:al|a|hasta|-)\s*(\d{1,3})\b""")
        val used = mutableListOf<IntRange>()
        for (m in rango.findAll(clean)) {
            val a = m.groupValues[1].toInt(); val b = m.groupValues[2].toInt()
            if (b >= a && b - a < RANGE_LIMIT && (direct || clean.contains("art") || clean.contains("del "))) {
                for (x in a..b) out.add(x.toString()); used.add(m.range)
            }
        }
        val rest = StringBuilder(clean)
        for (r in used) for (i in r) rest.setCharAt(i, ' ')
        val tieneArt = Regex("""\bart(?:iculos?)?\b""").containsMatchIn(clean)
        // En modo directo, una consulta que es solo un número ("198") es un artículo.
        val soloNumero = direct && Regex("""^\d{1,3}(?:-[a-z])?$""").matches(clean)
        if (tieneArt || soloNumero || Regex("""\b\d{1,3}-[a-z]\b""").containsMatchIn(clean)) {
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

### LawLibrary.kt (nuevo)
`app/src/main/java/com/example/data/search/codigo/LawLibrary.kt`

```kotlin
package com.example.data.search.codigo

import android.content.Context

/** Un libro que se puede consultar con el botón «Buscar en libros». */
data class LawBook(
    val id: String,
    val title: String,
    val bank: LawBank,
    val search: LawSearch,
    /** id del documento en la Biblioteca (ElectoralLibraryData). */
    val pdfDocId: String
)

/**
 * Catálogo de los libros con banco de artículos. Cada banco se carga SOLO la primera vez que se usa
 * (no al abrir la app), así que [search] debe llamarse fuera del hilo principal.
 * El Instructivo JRV y el Ciclo Electoral no tienen banco aquí: se buscan desde su propio lector PDF.
 */
object LawLibrary {

    private fun book(id: String, title: String, asset: String, pdfDocId: String, bodyWeight: Double = 3.0): LawBook {
        val bank = LawBank(asset)
        return LawBook(id, title, bank, LawSearch(bank, { -1 }, 0, bodyWeight = bodyWeight), pdfDocId)
    }

    val books: List<LawBook> = listOf(
        LawBook("codigo_electoral", "Código Electoral", ElectoralCodeBank, ElectoralCodeSearch, "codigo_electoral_decreto_413"),
        LawBook("constitucion", "Constitución de la República", ElectoralConstitutionBank, ElectoralConstitutionSearch, "constitucion_republica_1983"),
        book("ley_partidos", "Ley de Partidos Políticos", "leyes/ley_partidos.json", "ley_de_partidos_politicos"),
        book("ley_acceso_informacion", "Ley de Acceso a la Información Pública", "leyes/ley_acceso_informacion.json", "ley_acceso_informacion_publica"),
        book("ley_sufragio_exterior", "Ley de Sufragio en el Exterior", "leyes/ley_sufragio_exterior.json", "ley_sufragio_extranjero_542"),
        book("ley_reestructuracion_municipal", "Reestructuración Municipal", "leyes/ley_reestructuracion_municipal.json", "ley_reestructuracion_municipal_763"),
        book("reglamento_observacion", "Reglamento de Observación Electoral", "leyes/reglamento_observacion.json", "reglamento_observacion_electoral"),
        book("candidaturas_no_partidarias", "Candidaturas No Partidarias", "leyes/candidaturas_no_partidarias.json", "disposiciones_candidaturas_no_partidarias"),
        book("acuerdo_legislativo", "Acuerdo Legislativo (segunda elección)", "leyes/acuerdo_legislativo.json", "acuerdo_legislativo_reforma_electoral"),
        book("codigo_penal_electoral", "Código Penal (delitos electorales)", "leyes/codigo_penal_electoral.json", "codigo_penal_delitos_electorales")
    )

    fun byId(id: String): LawBook? = books.firstOrNull { it.id == id }

    fun byPdfDocId(docId: String): LawBook? = books.firstOrNull { it.pdfDocId == docId }

    /** Busca en un libro. Devuelve null si no hay resultados. Llamar fuera del hilo principal. */
    fun search(context: Context, bookId: String, query: String): CodeSearchResult? {
        val b = byId(bookId) ?: return null
        b.bank.ensureLoaded(context.applicationContext)
        return b.search.searchDirect(query)
    }
}
```

### BuscarEnLibros.kt (nuevo)
`app/src/main/java/com/example/ui/BuscarEnLibros.kt`

```kotlin
package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.search.codigo.CodeArticle
import com.example.data.search.codigo.CodeMatchKind
import com.example.data.search.codigo.CodeSearchResult
import com.example.data.search.codigo.LawBook
import com.example.data.search.codigo.LawLibrary
import com.example.data.search.knowledge.ElectoralKnowledgeBase
import com.example.viewmodel.ElectoralViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Hoja para elegir en qué libro buscar («Buscar en libros»). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookPickerSheet(onPick: (LawBook) -> Unit, onClose: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onClose, containerColor = MaterialTheme.colorScheme.surface) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 24.dp)) {
            Text("Buscar en libros", fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Text(
                "Elige el libro. Después escribe o dicta tu pregunta (por ejemplo «artículo 198» o «bebidas embriagantes»).",
                fontSize = 12.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(LawLibrary.books) { book ->
                    Card(
                        onClick = { onPick(book) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth().testTag("book_pick_${book.id}")
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(book.title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

/** Distintivo «Buscando en: <libro>» que se muestra sobre la caja de texto cuando el modo está activo. */
@Composable
fun BookSearchBadge(bookTitle: String, onClear: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "Buscando en: $bookTitle",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    maxLines = 1
                )
            }
            IconButton(onClick = onClear, modifier = Modifier.size(22.dp).testTag("btn_quitar_busqueda_libros")) {
                Icon(Icons.Default.Close, contentDescription = "Dejar de buscar en libros", tint = MaterialTheme.colorScheme.onTertiaryContainer, modifier = Modifier.size(14.dp))
            }
        }
    }
}

/** Resultados de «Buscar en libros»: tarjetas de artículos con su texto, su página y el botón para abrir el PDF subrayado. */
@Composable
fun BookSearchResultsView(
    question: String,
    bookId: String,
    bookTitle: String,
    viewModel: ElectoralViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var loading by remember(question, bookId) { mutableStateOf(true) }
    var result by remember(question, bookId) { mutableStateOf<CodeSearchResult?>(null) }
    var showMore by remember(question, bookId) { mutableStateOf(false) }

    LaunchedEffect(question, bookId) {
        loading = true
        result = withContext(Dispatchers.Default) { LawLibrary.search(context, bookId, question) }
        loading = false
    }

    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp)) {
        Text(
            text = "📖 $bookTitle",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Tu búsqueda: «$question»",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(10.dp))

        val r = result
        when {
            loading -> Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            r == null || r.articles.isEmpty() -> {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Sin resultados en este libro", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "No se encontró nada para «$question» en $bookTitle. Prueba con otras palabras, con un número de artículo (por ejemplo «198» o «artículos 5 al 7») o elige otro libro. La aplicación no inventa respuestas.",
                            fontSize = 12.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            else -> {
                val titulo = if (r.kind == CodeMatchKind.ARTICLE_NUMBER) "Artículo(s) solicitado(s)" else "Artículos sobre: ${r.topic}"
                Text(titulo, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                val highlight = if (r.kind == CodeMatchKind.ARTICLE_NUMBER) null else r.topic
                r.articles.forEach { a -> LawArticleCard(a, bookTitle, highlight, viewModel); Spacer(modifier = Modifier.height(10.dp)) }
                if (r.moreCount > 0) {
                    if (showMore) {
                        r.moreArticles.forEach { a -> LawArticleCard(a, bookTitle, highlight, viewModel); Spacer(modifier = Modifier.height(10.dp)) }
                        if (r.moreCount > r.moreArticles.size) {
                            Text("Hay ${r.moreCount - r.moreArticles.size} resultados más: afina tu búsqueda.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        OutlinedButton(onClick = { showMore = true }, modifier = Modifier.fillMaxWidth().testTag("btn_ver_mas_articulos")) {
                            Text("Ver más (${r.moreCount})", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Texto tomado del PDF de la biblioteca. Verifícalo en el documento oficial.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LawArticleCard(a: CodeArticle, bookTitle: String, topicHighlight: String?, viewModel: ElectoralViewModel) {
    var original by remember(a.id) { mutableStateOf(false) }
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth().testTag("law_card_${a.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Art. ${a.numero} · ${a.titulo}", fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(6.dp)) {
                    Text("Pág. ${a.paginaPdf}", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
            if (a.derogado) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("DEROGADO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
            }
            if (a.capitulo.isNotBlank()) {
                Text(a.capitulo, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (original) a.textoLiteral else a.textoLegible,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            TextButton(onClick = { original = !original }, contentPadding = PaddingValues(0.dp)) {
                Text(if (original) "Ver texto legible" else "Ver texto original del PDF", fontSize = 12.sp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { viewModel.openPdfDirectly(a.pdfDocId, a.paginaPdf, topicHighlight ?: "art. ${a.numero}") },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(38.dp).testTag("btn_abrir_pdf_${a.id}")
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Abrir en el PDF (pág. ${a.paginaPdf})", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }
            val relacionadas = a.situacionesRelacionadas.mapNotNull { id -> ElectoralKnowledgeBase.findById(id)?.let { id to it.titulo } }
            if (relacionadas.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("También en las situaciones:", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                relacionadas.take(4).forEach { (id, titulo) ->
                    Text(
                        text = "• $titulo",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable {
                                viewModel.clearBookSearchBook()
                                viewModel.submitQuery(titulo)
                            }
                            .padding(vertical = 3.dp)
                            .testTag("rel_$id")
                    )
                }
            }
        }
    }
}
```

## 3) Cambios a archivos existentes (aplicar exactamente estos diffs)

### viewmodel/ElectoralViewModel.kt
```diff
diff --git a/app/src/main/java/com/example/viewmodel/ElectoralViewModel.kt b/app/src/main/java/com/example/viewmodel/ElectoralViewModel.kt
index b94b382..70480eb 100644
--- a/app/src/main/java/com/example/viewmodel/ElectoralViewModel.kt
+++ b/app/src/main/java/com/example/viewmodel/ElectoralViewModel.kt
@@ -64,6 +64,16 @@ class ElectoralViewModel(private val repository: ElectoralRepository) : ViewMode
     private val _activePdfReaderDoc = MutableStateFlow<com.example.data.PdfDocument?>(null)
     val activePdfReaderDoc: StateFlow<com.example.data.PdfDocument?> = _activePdfReaderDoc.asStateFlow()
 
+    private val _activePdfReaderHighlight = MutableStateFlow("")
+    val activePdfReaderHighlight: StateFlow<String> = _activePdfReaderHighlight.asStateFlow()
+
+    // «Buscar en libros»: libro elegido (id, título). Mientras está activo, las preguntas (escritas o dictadas) buscan en ese libro.
+    private val _bookSearchBook = MutableStateFlow<Pair<String, String>?>(null)
+    val bookSearchBook: StateFlow<Pair<String, String>?> = _bookSearchBook.asStateFlow()
+
+    fun setBookSearchBook(id: String, title: String) { _bookSearchBook.value = id to title }
+    fun clearBookSearchBook() { _bookSearchBook.value = null }
+
     private val _activePdfReaderPage = MutableStateFlow<Int>(0)
     val activePdfReaderPage: StateFlow<Int> = _activePdfReaderPage.asStateFlow()
 
@@ -156,8 +166,16 @@ class ElectoralViewModel(private val repository: ElectoralRepository) : ViewMode
     }
 
     fun startDocumentSpecificQuery(docId: String, docTitle: String) {
-        _preferredBookId.value = docId
-        _preferredBookTitle.value = docTitle
+        val book = com.example.data.search.codigo.LawLibrary.byPdfDocId(docId)
+        if (book != null) {
+            // El libro tiene banco de artículos: la pregunta se busca DENTRO de ese libro.
+            _preferredBookId.value = null
+            _preferredBookTitle.value = null
+            _bookSearchBook.value = book.id to book.title
+        } else {
+            _preferredBookId.value = docId
+            _preferredBookTitle.value = docTitle
+        }
         _currentScreen.value = ElectoralScreen.CHAT
     }
 
@@ -166,12 +184,13 @@ class ElectoralViewModel(private val repository: ElectoralRepository) : ViewMode
         _preferredBookTitle.value = null
     }
 
-    fun openPdfDirectly(docId: String, page: Int = 1) {
+    fun openPdfDirectly(docId: String, page: Int = 1, highlight: String = "") {
         val foundDoc = com.example.data.ElectoralLibraryData.documents.firstOrNull { it.id == docId }
             ?: com.example.data.ElectoralLibraryData.documents.firstOrNull()
         if (foundDoc != null) {
             _activePdfReaderDoc.value = foundDoc
             _activePdfReaderPage.value = (page - 1).coerceAtLeast(0)
+            _activePdfReaderHighlight.value = highlight
             _currentScreen.value = ElectoralScreen.BIBLIOTECA
         }
     }
@@ -179,6 +198,7 @@ class ElectoralViewModel(private val repository: ElectoralRepository) : ViewMode
     fun closeDirectPdfReader() {
         _activePdfReaderDoc.value = null
         _activePdfReaderPage.value = 0
+        _activePdfReaderHighlight.value = ""
     }
 
     fun getSuggestionsForInput(input: String): List<String> {
@@ -281,6 +301,20 @@ class ElectoralViewModel(private val repository: ElectoralRepository) : ViewMode
             _queryInput.value = ""
         }
 
+        // Modo «Buscar en libros»: la pregunta se busca en el libro elegido (sin pasar por el asistente de situaciones).
+        val bookMode = _bookSearchBook.value
+        if (bookMode != null) {
+            _currentScreen.value = ElectoralScreen.CHAT
+            if (isFromVoice) {
+                // El dictado ya terminó: no hay respuesta que leer en voz alta, se muestran las tarjetas.
+                _voiceState.value = VoiceConversationState.IDLE
+                _voiceStatusMessage.value = "Pulsa el micrófono para hablar"
+                _isVoiceTurn.value = false
+            }
+            _uiState.value = ElectoralUiState.BookSearch(queryToSend, bookMode.first, bookMode.second)
+            return
+        }
+
         if (isFromVoice) {
             _isVoiceTurn.value = true
             _voiceState.value = VoiceConversationState.PROCESSING
@@ -357,6 +391,8 @@ sealed class ElectoralUiState {
         val primaryPdfPage: Int = 1
     ) : ElectoralUiState()
     data class Error(val question: String, val message: String) : ElectoralUiState()
+    /** Resultado de «Buscar en libros»: se muestran artículos del libro elegido. */
+    data class BookSearch(val question: String, val bookId: String, val bookTitle: String) : ElectoralUiState()
 }
 
 class ElectoralViewModelFactory(private val repository: ElectoralRepository) : ViewModelProvider.Factory {
```

### ui/ElectoralMainScreen.kt
```diff
diff --git a/app/src/main/java/com/example/ui/ElectoralMainScreen.kt b/app/src/main/java/com/example/ui/ElectoralMainScreen.kt
index 36d39b1..a0ad9f5 100644
--- a/app/src/main/java/com/example/ui/ElectoralMainScreen.kt
+++ b/app/src/main/java/com/example/ui/ElectoralMainScreen.kt
@@ -66,6 +66,8 @@ fun ElectoralMainScreen(
 
     val queryInput by viewModel.queryInput.collectAsState()
     val uiState by viewModel.uiState.collectAsState()
+    val bookSearchBook by viewModel.bookSearchBook.collectAsState()
+    var showBookPicker by remember { mutableStateOf(false) }
     val voiceState by viewModel.voiceState.collectAsState()
     val voiceStatusMessage by viewModel.voiceStatusMessage.collectAsState()
     val ttsPlaybackState by viewModel.ttsPlaybackState.collectAsState()
@@ -230,8 +232,7 @@ fun ElectoralMainScreen(
                         "¿Cómo se integra una JRV?" to "¿Cómo se integra una Junta Receptora de Votos (JRV)?",
                         "Permisos de trabajo (Art. 113)" to "¿Qué establece el Art. 113 del Código Electoral sobre los permisos laborales con goce de sueldo?",
                         "Prohibición de armas en centros" to "¿Qué normativa regula la prohibición de armas en centros de votación en El Salvador?",
-                        "Procedimiento de Escrutinio" to "¿Cuál es el procedimiento paso a paso para realizar el escrutinio de votos?",
-                        "Protección a la mujer (LEIV)" to "¿Cómo protege la ley a las mujeres miembros de mesa contra violencia política?"
+                        "Procedimiento de Escrutinio" to "¿Cuál es el procedimiento paso a paso para realizar el escrutinio de votos?"
                     )
                     
                     recommendedList.forEach { (label, question) ->
@@ -942,6 +943,16 @@ fun ElectoralMainScreen(
                                     }
                                 }
 
+                                is ElectoralUiState.BookSearch -> {
+                                    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
+                                        BookSearchResultsView(
+                                            question = state.question,
+                                            bookId = state.bookId,
+                                            bookTitle = state.bookTitle,
+                                            viewModel = viewModel
+                                        )
+                                    }
+                                }
                                 is ElectoralUiState.Error -> {
                                     Column(
                                         modifier = Modifier
@@ -1015,6 +1026,37 @@ fun ElectoralMainScreen(
                             .padding(horizontal = 14.dp, vertical = 4.dp),
                         horizontalAlignment = Alignment.CenterHorizontally
                     ) {
+                        // «Buscar en libros»: distintivo del libro elegido o botón para elegirlo
+                        if (bookSearchBook != null) {
+                            BookSearchBadge(
+                                bookTitle = bookSearchBook!!.second,
+                                onClear = { viewModel.clearBookSearchBook() },
+                                modifier = Modifier.padding(bottom = 6.dp)
+                            )
+                        } else {
+                            OutlinedButton(
+                                onClick = { showBookPicker = true },
+                                shape = RoundedCornerShape(10.dp),
+                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
+                                modifier = Modifier
+                                    .fillMaxWidth()
+                                    .height(34.dp)
+                                    .padding(bottom = 2.dp)
+                                    .testTag("btn_buscar_en_libros")
+                            ) {
+                                Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(15.dp))
+                                Spacer(modifier = Modifier.width(6.dp))
+                                Text("Buscar en libros", fontSize = 12.sp, fontWeight = FontWeight.Bold)
+                            }
+                            Spacer(modifier = Modifier.height(4.dp))
+                        }
+                        if (showBookPicker) {
+                            BookPickerSheet(
+                                onPick = { book -> viewModel.setBookSearchBook(book.id, book.title); showBookPicker = false },
+                                onClose = { showBookPicker = false }
+                            )
+                        }
+
                         // Focus on specific book badge if selected
                         if (preferredBookTitle != null) {
                             Surface(
```

### data/search/knowledge/ElectoralKnowledgeBase.kt
```diff
diff --git a/app/src/main/java/com/example/data/search/knowledge/ElectoralKnowledgeBase.kt b/app/src/main/java/com/example/data/search/knowledge/ElectoralKnowledgeBase.kt
index 4c7b5c4..e33b755 100644
--- a/app/src/main/java/com/example/data/search/knowledge/ElectoralKnowledgeBase.kt
+++ b/app/src/main/java/com/example/data/search/knowledge/ElectoralKnowledgeBase.kt
@@ -935,8 +935,6 @@ object ElectoralKnowledgeBase {
      */
     fun init(context: Context) {
         com.example.data.search.synonyms.ElectoralConceptRegistry.init(context)
-        com.example.data.search.codigo.ElectoralCodeBank.init(context)
-        com.example.data.search.codigo.ElectoralConstitutionBank.init(context)
         try {
             val jsonSituations = JsonKnowledgeLoader.loadAllFromAssets(context)
             synchronized(_loadedSituations) {
```

### data/repository/ElectoralRepository.kt (se quita la sugerencia de la LEIV; la Ley de Género no va en la app)
```diff
diff --git a/app/src/main/java/com/example/data/repository/ElectoralRepository.kt b/app/src/main/java/com/example/data/repository/ElectoralRepository.kt
index 3d6f5e5..7d0c5a3 100644
--- a/app/src/main/java/com/example/data/repository/ElectoralRepository.kt
+++ b/app/src/main/java/com/example/data/repository/ElectoralRepository.kt
@@ -43,7 +43,6 @@ class ElectoralRepository(
         "¿Qué derechos y prohibiciones tienen los vigilantes de partidos políticos?",
         "¿Cuáles son las atribuciones del Tribunal Supremo Electoral (TSE)?",
         "¿Quiénes están autorizados a portar armas y cómo aplica el Art. 290 del Código Electoral?",
-        "¿Cómo protege la ley LEIV (Art. 55) a las mujeres en el proceso electoral?",
         "¿Qué sanciones contempla la Ley Seca (Art. 284) y los delitos electorales?",
         "¿Qué hacer si un ciudadano no presenta su DUI o no aparece en el padrón?",
         "¿Cómo se garantiza el voto asistido para personas con discapacidad o adultos mayores?",
```

### data/ElectoralPdfData.kt (corrige el resumen del Código Penal: el único delito electoral del PDF es el Art. 295; los Arts. 296 a 298 que decía el resumen NO eran electorales)
```diff
diff --git a/app/src/main/java/com/example/data/ElectoralPdfData.kt b/app/src/main/java/com/example/data/ElectoralPdfData.kt
index 22a0708..a8ab61b 100644
--- a/app/src/main/java/com/example/data/ElectoralPdfData.kt
+++ b/app/src/main/java/com/example/data/ElectoralPdfData.kt
@@ -334,33 +334,21 @@ object ElectoralLibraryData {
             id = "codigo_penal_delitos_electorales",
             title = "Código Penal de El Salvador (Delitos Electorales)",
             category = "Sanciones, Delitos y Recursos",
-            summary = "Tipificación de delitos contra los derechos cívicos y electorales (Arts. 295 a 302): Fraude electoral, coacción del sufragio, falsedad documental, obstaculización, usurpación de funciones y penas de prisión de hasta 15 años.",
+            summary = "Delito electoral del Código Penal de El Salvador: Fraude electoral (Art. 295), con sus circunstancias y penas. El resto del Código Penal puede consultarse en el PDF.",
             authority = "Asamblea Legislativa • Órgano Judicial de El Salvador",
             releaseDate = "Código Penal Consolidado",
             assetPath = "libros/Código Penal.pdf",
             pages = listOf(
                 PdfPage(
                     pageNumber = 1,
-                    headerTitle = "CÓDIGO PENAL • LIBRO SEGUNDO • TÍTULO XIII",
-                    subtitle = "DELITOS CONTRA LOS DERECHOS CÍVICOS Y ELECTORALES",
+                    headerTitle = "CÓDIGO PENAL • LIBRO SEGUNDO • TÍTULO XIV",
+                    subtitle = "DELITOS RELATIVOS A LOS DERECHOS Y GARANTÍAS FUNDAMENTALES DE LA PERSONA",
                     sections = listOf(
                         PdfSection(
                             title = "Fraude Electoral (Art. 295)",
-                            content = "Art. 295.- Votar más de una vez, suplantar a otro elector, votar sin derecho o alterar padrones o actas. Pena: 4 a 6 años de prisión (aumenta si es miembro de organismo electoral).",
+                            content = "Art. 295.- Prisión de cuatro a seis años si el fraude electoral se comete, entre otras circunstancias, votando sin derecho, suplantando a otro elector o votando más de una vez; pagando u ofreciendo beneficios para inducir el voto o la abstención; alterando el registro electoral; sustrayendo o destruyendo el padrón, las papeletas o las urnas; impidiendo u obstaculizando el registro, el ejercicio del sufragio o el escrutinio; coaccionando a un elector o violando el secreto del voto; o cambiando de domicilio para votar en otra circunscripción. Si participan funcionarios públicos o electorales: prisión de siete a diez años e inhabilitación del cargo por igual período. Lea el texto completo en el PDF.",
                             articleRef = "Art. 295",
                             isWarning = true
-                        ),
-                        PdfSection(
-                            title = "Coacción del Sufragio (Art. 296)",
-                            content = "Art. 296.- Impedir, amenazar o coartar el voto de un elector o forzarlo a votar en determinado sentido o abstenerse. Pena: 3 a 6 años de prisión.",
-                            articleRef = "Art. 296",
-                            isWarning = true
-                        ),
-                        PdfSection(
-                            title = "Falsedad y Obstaculización (Arts. 297-298)",
-                            content = "Arts. 297 y 298.- Alterar o destruir credenciales, actas, papeletas o perturbar la instalación o escrutinio de JRV. Pena: 4 a 8 años de prisión.",
-                            articleRef = "Arts. 297-298",
-                            isWarning = true
                         )
                     )
                 )
```

(`ui/BibliotecaScreen.kt` y `ui/BookInternalSearchScreen.kt` ya traen sus cambios del Prompt 8; el botón «CONSULTAR ASISTENTE» de la Biblioteca ahora abre «Buscar en libros» con ese libro elegido.)

## 4) Pruebas (en el teléfono)
1. En el chat aparece el botón «Buscar en libros» sobre la caja de texto. Tócalo: lista de 10 libros.
2. Elige «Código Electoral» y escribe `198`: tarjeta «Art. 198 · Cierre de la Votación», Pág. 67. Escribe `bebidas embriagantes`: Art. 284. `artículos 200 al 203`: 4 tarjetas.
3. Toca «Abrir en el PDF»: se abre la página con lo buscado en amarillo.
4. Toca el micrófono y di «bebidas embriagantes»: debe mostrar las tarjetas (no lee la respuesta en voz alta; es normal).
5. Elige «Constitución»: `72` → Art. 72; `tribunal supremo electoral` → Art. 208. «Ley de Partidos Políticos»: `cuota de género` → Art. 38. «Código Penal (delitos electorales)»: `fraude electoral` → Art. 295.
6. Una búsqueda sin resultados (`zzzz`) muestra «Sin resultados en este libro» y no inventa.
7. El distintivo «Buscando en: …» se quita con la X y el chat vuelve a funcionar normal (cualquier pregunta sobre situaciones, como «el votante olvidó su DUI»).
8. En Biblioteca → un libro → buscador → «Preguntar al asistente» abre el chat con ese libro elegido y el artículo buscado.
9. El Instructivo JRV y el Ciclo Electoral NO aparecen en la lista (se buscan solo desde su lector PDF).
10. Abre la app: no debe notarse más lenta al arrancar.
