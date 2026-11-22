# PROMPT 8 para AI Studio — Buscador de la biblioteca: índice real, búsqueda exacta y subrayado amarillo (Bloque C, parte 1)

Aplica ANTES los Prompts 1, 5, 6 y 7. Adjunta SOLO los 13 archivos de datos (.json) que se indican en el punto 1. El código va escrito dentro de este prompt (punto 2).

---

La búsqueda de la biblioteca (lector PDF) fallaba: el índice tenía páginas equivocadas, 5 libros sin texto buscable, "artículo 2" devolvía decenas de resultados, no había subrayado amarillo sobre la página, y la búsqueda corría en el hilo principal. Ya está resuelto en el código adjunto. Haz SOLO esto:

## 1) Datos (assets) — copiar tal cual, sin modificar ni resumir
- Reemplaza `app/src/main/assets/libros_indice_completo.json` por el adjunto (`libros_indice_completo.json`) (texto real de cada página de los 12 PDF e índice de artículos con su página real).
- Crea la carpeta `app/src/main/assets/libros_palabras/` con los 12 archivos adjuntos cuyo nombre termina en `_pdf.json` (posición de cada palabra, para subrayar sobre la imagen del PDF). Los nombres deben quedar IDÉNTICOS a los adjuntos (por ejemplo `codigo_electoral_pdf.json`, `codigo_penal_pdf.json`).

## 2) Código (crear o reemplazar con EXACTAMENTE este contenido)

### BookSearch.kt (nuevo)
`app/src/main/java/com/example/data/BookSearch.kt`

```kotlin
package com.example.data

import android.content.Context
import org.json.JSONObject
import java.text.Normalizer

/**
 * Buscador de la Biblioteca (PDF). Todo local, sin internet.
 *
 * Datos (generados leyendo los PDF reales con herramientas/biblioteca/generar_indice_y_palabras.py):
 *  - assets/libros_indice_completo.json : texto de cada página + índice de artículos con su página real
 *  - assets/libros_palabras/<archivo>.json : posición de cada palabra de cada página (para subrayar sobre el PDF)
 *
 * Reglas:
 *  - "artículo 2", "art. 2", "2", "artículos 5 al 8" -> muestra SOLO ese artículo (coincidencia exacta).
 *  - Palabras y frases: palabra completa, sin importar tildes ni mayúsculas, sin contar palabras vacías (de, la, el...).
 *    Primero las páginas con la frase completa, luego las que tienen todas las palabras y, solo si no hay, algunas.
 *  - Cada resultado trae las posiciones a subrayar en amarillo (en el fragmento y sobre la página del PDF).
 */
object BookSearch {

    enum class Kind { ARTICLE, PHRASE, ALL_WORDS, SOME_WORDS }

    data class Hit(
        val pageNumber: Int,
        val articleRef: String?,
        val title: String,
        val snippet: String,
        /** Rangos dentro de [snippet] que se subrayan en amarillo. */
        val highlights: List<IntRange>,
        val kind: Kind,
        val score: Int
    )

    data class Outcome(
        val hits: List<Hit>,
        val query: String,
        /** Mensaje para mostrar cuando no hay resultados (ej. "El Art. 9999 no existe en este documento"). */
        val message: String? = null
    )

    /** Caja normalizada (0..1) de una palabra sobre la página, para dibujar el subrayado sobre la imagen del PDF. */
    data class Box(val x: Float, val y: Float, val w: Float, val h: Float)

    // ---------------------------------------------------------------- datos
    private class PageData(val number: Int, val text: String) {
        val raw: List<String>
        val fold: List<String>
        val starts: IntArray
        init {
            val ws = ArrayList<String>(); val fs = ArrayList<String>(); val st = ArrayList<Int>()
            var i = 0
            while (i < text.length) {
                while (i < text.length && text[i] == ' ') i++
                if (i >= text.length) break
                val s = i
                while (i < text.length && text[i] != ' ') i++
                val w = text.substring(s, i)
                ws.add(w); fs.add(wordKey(w)); st.add(s)
            }
            raw = ws; fold = fs; starts = st.toIntArray()
        }
    }

    private class Article(val number: Int, val ref: String, val title: String, val page: Int, val snippet: String)

    private class Book(val fileName: String, val totalPages: Int, val articles: List<Article>, val pages: List<PageData>) {
        val articleByNumber: Map<Int, Article> = articles.associateBy { it.number }
    }

    @Volatile private var books: Map<String, Book>? = null
    private val lock = Any()

    fun init(context: Context) { ensureLoaded(context) }

    private fun ensureLoaded(context: Context): Map<String, Book> {
        books?.let { return it }
        synchronized(lock) {
            books?.let { return it }
            val out = HashMap<String, Book>()
            try {
                val json = context.assets.open("libros_indice_completo.json").bufferedReader().use { it.readText() }
                val root = JSONObject(json)
                val keys = root.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    val o = root.getJSONObject(key)
                    val fileName = o.optString("fileName", key)
                    val arts = ArrayList<Article>()
                    o.optJSONArray("articles")?.let { a ->
                        for (i in 0 until a.length()) {
                            val x = a.getJSONObject(i)
                            val ref = x.optString("article")
                            // Solo artículos numerados ("Art. 12"); los temas del Instructivo o del Ciclo no cuentan como artículo.
                            if (!Regex("""Art\. \d+""").matches(ref)) continue
                            arts.add(Article(x.optInt("articleNumber"), ref, x.optString("title"), x.optInt("page", 1),
                                x.optString("snippet").removePrefix("- ").trim()))
                        }
                    }
                    val pages = ArrayList<PageData>()
                    o.optJSONArray("pages")?.let { p ->
                        for (i in 0 until p.length()) {
                            val x = p.getJSONObject(i)
                            pages.add(PageData(x.optInt("pageNumber", i + 1), x.optString("text")))
                        }
                    }
                    val book = Book(fileName, o.optInt("totalPages", pages.size), arts, pages)
                    out[fileName.lowercase()] = book
                }
            } catch (e: Exception) {
                // sin índice: el buscador devuelve vacío y el lector PDF sigue funcionando
            }
            books = out
            return out
        }
    }

    /** Conocidos por id del documento en la app (ElectoralLibraryData) -> archivo PDF. */
    private val FILE_BY_DOC_ID = mapOf(
        "codigo_electoral_decreto_413" to "Codigo-Electoral.pdf",
        "constitucion_republica_1983" to "constitucion.pdf",
        "ley_de_partidos_politicos" to "ley-de-partidos-politicos.pdf",
        "ley_acceso_informacion_publica" to "Ley-de-Acceso-a-la-Informacion-Publica.pdf",
        "ley_sufragio_extranjero_542" to "LEY ESPECIAL PARA EL EJERCICIO DEL SUFRAGIO EN EL EXTRANJERO.pdf",
        "ley_reestructuracion_municipal_763" to "LEY ESPECIAL PARA LA REESTRUCTURACIÓN MUNICIPAL.pdf",
        "codigo_penal_delitos_electorales" to "Código Penal.pdf",
        "acuerdo_legislativo_reforma_electoral" to "ACUERDO LEGISLATIVO - REFORMA - SUPRECIÓN SEGUNDA ELECCIÓN.pdf",
        "ciclo_electoral_salvadoreno" to "CICLO ELECTORAL SALVADOREÑO.pdf",
        "disposiciones_candidaturas_no_partidarias" to "Disposciones-Especiales-Candidaturas-no-partidarias.pdf",
        "reglamento_observacion_electoral" to "REGLAMENTO-GENERAL-PARA-LA-OBSERVACION-ELECTORAL-NACIONAL-E-INTERNACIONAL-EN-EL-SALVADOR.pdf",
        "instructivo_jrv_tse" to "instructivo-JRV.pdf"
    )

    /** Nombre del archivo PDF a partir del id del documento y/o su ruta (assetPath tiene prioridad). */
    fun resolveFileName(documentId: String, assetPath: String?): String? {
        val fromPath = assetPath?.substringAfterLast("/")?.takeIf { it.isNotBlank() }
        if (fromPath != null) return fromPath
        return FILE_BY_DOC_ID[documentId]
    }

    // ---------------------------------------------------------------- texto
    private val FOLD_CACHE = HashMap<Char, Char>()

    private fun foldChar(c: Char): Char {
        if (c in 'a'..'z' || c in '0'..'9') return c
        if (c in 'A'..'Z') return c + 32
        synchronized(FOLD_CACHE) {
            return FOLD_CACHE.getOrPut(c) {
                val d = Normalizer.normalize(c.toString(), Normalizer.Form.NFD)
                d[0].lowercaseChar()
            }
        }
    }

    /** Minúsculas, sin tildes, sin signos: lo que se compara entre sí. */
    private fun wordKey(w: String): String {
        val sb = StringBuilder(w.length)
        for (c in w) { val f = foldChar(c); if (f.isLetterOrDigit()) sb.append(f) }
        return sb.toString()
    }

    private val STOP = setOf("de", "del", "la", "las", "el", "los", "lo", "y", "e", "o", "u", "a", "al", "en", "un", "una", "unos", "unas",
        "que", "se", "su", "sus", "por", "para", "con", "sin", "es", "son", "ser")

    private fun queryWords(query: String): List<String> =
        query.trim().split(Regex("""[\s,;:.¿?¡!"()\[\]-]+""")).map { wordKey(it) }.filter { it.isNotEmpty() }

    private fun sameWord(page: String, q: String): Boolean =
        page == q || page == q + "s" || page + "s" == q || page == q + "es" || page + "es" == q

    // ---------------------------------------------------------------- artículo por número
    private val ART_QUERY = Regex("""^\s*(?:art(?:iculos?|s?\.?)?\s*\.?\s*)?(\d{1,4})(?:\s*(?:al|a|hasta|-|y)\s*(\d{1,4}))?\s*\.?\s*$""", RegexOption.IGNORE_CASE)

    private fun parseArticleQuery(query: String): IntRange? {
        val folded = buildString { query.forEach { append(foldChar(it)) } }
        val m = ART_QUERY.matchEntire(folded) ?: return null
        val a = m.groupValues[1].toInt()
        val b = m.groupValues[2].takeIf { it.isNotEmpty() }?.toInt() ?: a
        return if (b >= a && b - a < 12) a..b else a..a
    }

    private val BEFORE_CROSSREF = setOf("de", "del", "al", "el", "los", "en", "y", "o", "por", "conforme", "segun", "este", "dicho")

    /** Índice de la palabra "Art." del encabezado "Art. N.-" en la página (no las citas "conforme al artículo N"). */
    private fun headingWordIndex(fold: List<String>, n: Int): Int {
        val num = n.toString()
        var fallback = -1
        for (i in 0 until fold.size - 1) {
            if ((fold[i] == "art" || fold[i] == "articulo") && fold[i + 1] == num) {
                if (fallback < 0) fallback = i
                val prev = if (i > 0) fold[i - 1] else ""
                if (prev !in BEFORE_CROSSREF && fold[i] == "art") return i
            }
        }
        return fallback
    }

    // ---------------------------------------------------------------- coincidencias en una página (índices de palabras)
    /** Rangos de índices de palabra donde aparece la frase completa, en orden. */
    private fun phraseMatches(fold: List<String>, q: List<String>): List<IntRange> {
        val out = ArrayList<IntRange>()
        if (q.isEmpty()) return out
        var i = 0
        while (i < fold.size) {
            var qi = 0; var j = i; var last = i
            while (j < fold.size && qi < q.size) {
                if (fold[j].isEmpty()) { j++; continue }
                if (sameWord(fold[j], q[qi])) { qi++; last = j; j++ } else break
            }
            if (qi == q.size) { out.add(i..last); i = last + 1 } else i++
        }
        return out
    }

    private fun wordMatches(fold: List<String>, q: List<String>): Map<String, List<Int>> {
        val res = LinkedHashMap<String, MutableList<Int>>()
        for (t in q) res[t] = ArrayList()
        for (i in fold.indices) { val f = fold[i]; if (f.isEmpty()) continue; for (t in q) if (sameWord(f, t)) res[t]!!.add(i) }
        return res
    }

    /** Palabras que se buscan: la consulta sin palabras vacías (si todas lo son, se usan todas). */
    private fun keyWords(q: List<String>): List<String> = q.filter { it !in STOP }.ifEmpty { q }.distinct()

    // ---------------------------------------------------------------- búsqueda
    fun search(context: Context, fileName: String, query: String, limit: Int = 40): Outcome {
        val q = query.trim()
        if (q.isEmpty()) return Outcome(emptyList(), q)
        val book = ensureLoaded(context)[fileName.lowercase()] ?: return Outcome(emptyList(), q, "Este documento no tiene índice de búsqueda.")

        // 1) Artículo por número
        parseArticleQuery(q)?.takeIf { book.articles.isNotEmpty() }?.let { range ->
            val hits = ArrayList<Hit>()
            val missing = ArrayList<Int>()
            for (n in range) {
                val art = book.articleByNumber[n]
                if (art == null) { missing.add(n); continue }
                val snippet = art.snippet.take(260).let { if (art.snippet.length > 260) "$it…" else it }
                hits.add(Hit(art.page, art.ref, art.title, snippet, emptyList(), Kind.ARTICLE, 10_000 - n))
            }
            if (hits.isEmpty()) return Outcome(emptyList(), q, "El " + (if (range.first == range.last) "Art. ${range.first}" else "rango pedido") + " no aparece en este documento.")
            return Outcome(hits, q, if (missing.isNotEmpty()) "No aparecen: " + missing.joinToString(", ") { "Art. $it" } else null)
        }

        // 2) Palabras y frases
        val all = queryWords(q)
        if (all.isEmpty()) return Outcome(emptyList(), q)
        val key = keyWords(all)
        val hits = ArrayList<Hit>()
        val used = HashSet<Int>()

        if (all.size >= 2) {
            for (p in book.pages) {
                val m = phraseMatches(p.fold, all)
                if (m.isNotEmpty()) { hits.add(makeHit(p, m, Kind.PHRASE, 1000 + 10 * m.size)); used.add(p.number) }
            }
        }
        if (key.size >= 2) {
            for (p in book.pages) {
                if (p.number in used) continue
                val wm = wordMatches(p.fold, key)
                if (wm.values.all { it.isNotEmpty() }) {
                    val idx = wm.values.flatten().sorted()
                    hits.add(makeHit(p, idx.map { it..it }, Kind.ALL_WORDS, 500 + idx.size)); used.add(p.number)
                }
            }
        }
        if (hits.isEmpty()) {
            // Parcial solo con 4 o más palabras (todas menos una); con 1 a 3 palabras se exige que estén todas.
            val need = if (key.size >= 4) key.size - 1 else key.size
            for (p in book.pages) {
                val wm = wordMatches(p.fold, key)
                val present = wm.values.count { it.isNotEmpty() }
                if (present >= need) {
                    val idx = wm.values.flatten().sorted()
                    hits.add(makeHit(p, idx.map { it..it }, if (key.size == 1) Kind.PHRASE else if (present == key.size) Kind.ALL_WORDS else Kind.SOME_WORDS, 100 * present + idx.size))
                }
            }
        }
        val sorted = hits.sortedWith(compareByDescending<Hit> { it.score }.thenBy { it.pageNumber }).take(limit)
        return Outcome(sorted, q, if (sorted.isEmpty()) "No se encontraron coincidencias." else null)
    }

    private fun makeHit(p: PageData, ranges: List<IntRange>, kind: Kind, score: Int): Hit {
        val first = ranges.first()
        val startChar = p.starts[first.first]
        val from = (startChar - 70).coerceAtLeast(0).let { s -> var k = s; while (k > 0 && p.text[k - 1] != ' ') k--; k }
        var to = (startChar + 230).coerceAtMost(p.text.length)
        while (to < p.text.length && p.text[to] != ' ') to++
        val prefix = if (from > 0) "… " else ""
        val suffix = if (to < p.text.length) " …" else ""
        val snippet = prefix + p.text.substring(from, to) + suffix
        val hl = ArrayList<IntRange>()
        for (r in ranges) {
            val s = p.starts[r.first]; val eIdx = r.last
            val e = p.starts[eIdx] + p.raw[eIdx].length
            if (s >= from && e <= to) hl.add((s - from + prefix.length) until (e - from + prefix.length))
        }
        return Hit(p.number, null, "Contenido en página ${p.number}", snippet, hl, kind, score)
    }

    // ---------------------------------------------------------------- texto de la página y subrayado sobre el PDF
    /** Texto completo de la página y rangos a subrayar (para el panel "Texto de la página"). */
    fun pageTextWithHighlights(context: Context, fileName: String, page: Int, query: String): Pair<String, List<IntRange>> {
        val p = ensureLoaded(context)[fileName.lowercase()]?.pages?.getOrNull(page - 1) ?: return "" to emptyList()
        val ranges = matchingWordRanges(p.fold, query, ensureLoaded(context)[fileName.lowercase()]!!)
        val out = ranges.map { p.starts[it.first] until (p.starts[it.last] + p.raw[it.last].length) }
        return p.text to out
    }

    private fun matchingWordRanges(fold: List<String>, query: String, book: Book?): List<IntRange> {
        val art = parseArticleQuery(query)
        if (art != null) {
            val out = ArrayList<IntRange>()
            for (n in art) {
                val i = headingWordIndex(fold, n)
                if (i >= 0) out.add(i..(i + 1))
            }
            return out
        }
        val all = queryWords(query)
        if (all.isEmpty()) return emptyList()
        if (all.size >= 2) {
            val ph = phraseMatches(fold, all)
            if (ph.isNotEmpty()) return ph
        }
        val key = keyWords(all)
        val wm = wordMatches(fold, key)
        return wm.values.flatten().sorted().map { it..it }
    }

    private class WordsFile(val pages: List<Triple<Pair<Float, Float>, List<String>, IntArray>>)

    /** Nombre simple del archivo de palabras: sin tildes ni espacios (ej. "Código Penal.pdf" -> "codigo_penal_pdf"). */
    private fun simpleName(fileName: String): String {
        val sb = StringBuilder()
        for (c in fileName) {
            val f = foldChar(c)
            if (f in 'a'..'z' || f in '0'..'9') sb.append(f) else if (sb.isNotEmpty() && sb.last() != '_') sb.append('_')
        }
        return sb.toString().trim('_')
    }

    @Volatile private var wordsCache: Pair<String, WordsFile>? = null

    private fun loadWords(context: Context, fileName: String): WordsFile? {
        wordsCache?.let { if (it.first == fileName) return it.second }
        return try {
            val json = context.assets.open("libros_palabras/${simpleName(fileName)}.json").bufferedReader().use { it.readText() }
            val arr = JSONObject(json).getJSONArray("pages")
            val pages = ArrayList<Triple<Pair<Float, Float>, List<String>, IntArray>>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val t = o.getString("t")
                val b = o.getJSONArray("b")
                val boxes = IntArray(b.length()) { b.getInt(it) }
                pages.add(Triple(o.getDouble("w").toFloat() to o.getDouble("h").toFloat(), t.split(' ').filter { it.isNotEmpty() }.map { wordKey(it) }, boxes))
            }
            WordsFile(pages).also { wordsCache = fileName to it }
        } catch (e: Exception) { null }
    }

    /** Cajas a subrayar en amarillo sobre la imagen de la página (coordenadas 0..1 respecto del ancho y alto de la página). */
    fun highlightBoxes(context: Context, fileName: String, page: Int, query: String): List<Box> {
        if (query.isBlank()) return emptyList()
        val wf = loadWords(context, fileName) ?: return emptyList()
        val (_, fold, b) = wf.pages.getOrNull(page - 1) ?: return emptyList()
        val boxes = ArrayList<Box>()
        for (r in matchingWordRanges(fold, query, null)) {
            for (i in r) {
                if (4 * i + 3 >= b.size) continue
                boxes.add(Box(b[4 * i] / 1000f, b[4 * i + 1] / 1000f, b[4 * i + 2] / 1000f, b[4 * i + 3] / 1000f))
            }
        }
        return boxes
    }
}
```

### BookSearchUi.kt (nuevo)
`app/src/main/java/com/example/ui/BookSearchUi.kt`

```kotlin
package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BookSearch
import com.example.data.PdfDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Amarillo de subrayado de búsqueda (el mismo en lista, panel de texto y sobre el PDF). */
val SearchHighlightYellow = Color(0xFFFFEB3B)

/** Texto con los rangos indicados subrayados en amarillo (fondo amarillo y letra oscura, legible en tema claro y oscuro). */
fun highlightedText(text: String, ranges: List<IntRange>): AnnotatedString {
    if (ranges.isEmpty()) return AnnotatedString(text)
    val merged = ArrayList<IntRange>()
    for (r in ranges.sortedBy { it.first }) {
        if (merged.isNotEmpty() && r.first <= merged.last().last + 1) {
            merged[merged.size - 1] = merged.last().first..maxOf(merged.last().last, r.last)
        } else merged.add(r)
    }
    return buildAnnotatedString {
        var cursor = 0
        for (r in merged) {
            val s = r.first.coerceIn(0, text.length)
            val e = (r.last + 1).coerceIn(s, text.length)
            if (s > cursor) append(text.substring(cursor, s))
            withStyle(SpanStyle(background = SearchHighlightYellow, color = Color(0xFF1E293B), fontWeight = FontWeight.Bold)) {
                append(text.substring(s, e))
            }
            cursor = e
        }
        if (cursor < text.length) append(text.substring(cursor))
    }
}

/**
 * Subrayado amarillo semitransparente SOBRE la imagen de la página del PDF.
 * Debe dibujarse con la misma transformación (zoom y desplazamiento) que la imagen y ocupar el mismo espacio
 * (la imagen usa ContentScale.Fit en un contenedor que llena la pantalla).
 */
@Composable
fun PdfHighlightOverlay(
    boxes: List<BookSearch.Box>,
    bitmapWidth: Int,
    bitmapHeight: Int,
    scale: Float,
    offset: Offset,
    modifier: Modifier = Modifier
) {
    if (boxes.isEmpty() || bitmapWidth <= 0 || bitmapHeight <= 0) return
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer(scaleX = scale, scaleY = scale, translationX = offset.x, translationY = offset.y)
    ) {
        val fit = minOf(size.width / bitmapWidth, size.height / bitmapHeight)
        val imgW = bitmapWidth * fit
        val imgH = bitmapHeight * fit
        val left = (size.width - imgW) / 2f
        val top = (size.height - imgH) / 2f
        val pad = 1.5f
        for (b in boxes) {
            drawRect(
                color = SearchHighlightYellow.copy(alpha = 0.75f),
                topLeft = Offset(left + b.x * imgW - pad, top + b.y * imgH - pad),
                size = Size(b.w * imgW + 2 * pad, b.h * imgH + 2 * pad),
                blendMode = BlendMode.Multiply // el texto negro sigue legible debajo del amarillo
            )
        }
    }
}

/** Panel con el texto de la página y lo buscado en amarillo. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageTextSheet(
    document: PdfDocument,
    pageNumber: Int,
    query: String,
    onClearHighlight: () -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var content by remember { mutableStateOf<AnnotatedString?>(null) }
    LaunchedEffect(document, pageNumber, query) {
        content = withContext(Dispatchers.Default) {
            val file = BookSearch.resolveFileName(document.id, document.assetPath)
            if (file == null) AnnotatedString("")
            else {
                val (text, ranges) = BookSearch.pageTextWithHighlights(context.applicationContext, file, pageNumber, query)
                highlightedText(text, ranges)
            }
        }
    }
    ModalBottomSheet(onDismissRequest = onClose, containerColor = MaterialTheme.colorScheme.surface) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Texto de la página $pageNumber", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(document.title, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, maxLines = 1)
                }
                if (query.isNotBlank()) {
                    TextButton(onClick = onClearHighlight) { Text("Quitar subrayado", fontSize = 12.sp) }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            val c = content
            if (c == null) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else if (c.text.isBlank()) {
                Text("Esta página no tiene texto disponible para mostrar.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                    Text(c, fontSize = 13.5.sp, lineHeight = 20.sp, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
```

### BookInternalSearchEngine.kt (reemplazar completo)
`app/src/main/java/com/example/data/BookInternalSearchEngine.kt`

```kotlin
package com.example.data

import android.content.Context

/**
 * Resultado de búsqueda dentro de un libro de la Biblioteca.
 * [highlights] son los rangos de [snippet] que se subrayan en amarillo (ya sin importar tildes ni mayúsculas).
 */
data class BookSearchResult(
    val id: String,
    val documentId: String,
    val documentTitle: String,
    val sectionTitle: String,
    val articleRef: String?,
    val pageNumber: Int,
    val snippet: String,
    val fullContent: String,
    val score: Int,
    val highlights: List<IntRange> = emptyList(),
    val isArticleMatch: Boolean = false
)

/**
 * Fachada que usan las pantallas. La lógica real está en [BookSearch]
 * (índice generado de los PDF reales; sin resultados de respaldo con páginas escritas a mano).
 */
object BookInternalSearchEngine {

    fun init(context: Context) {
        // Carga perezosa: el índice se lee la primera vez que se busca. Aquí solo se guarda el contexto de aplicación.
        appContext = context.applicationContext
    }

    @Volatile private var appContext: Context? = null

    /** Búsqueda con mensaje (ej. «El Art. 9999 no aparece en este documento»). Llamar fuera del hilo principal. */
    fun searchOutcome(
        documentId: String,
        documentTitle: String,
        query: String,
        assetPath: String? = null,
        context: Context? = null
    ): Pair<List<BookSearchResult>, String?> {
        val ctx = context?.applicationContext ?: appContext ?: return emptyList<BookSearchResult>() to null
        val file = BookSearch.resolveFileName(documentId, assetPath)
            ?: return emptyList<BookSearchResult>() to "Este documento no tiene índice de búsqueda."
        val outcome = BookSearch.search(ctx, file, query)
        val results = outcome.hits.map { h ->
            BookSearchResult(
                id = "${h.pageNumber}_${h.articleRef ?: h.kind.name}",
                documentId = documentId,
                documentTitle = documentTitle,
                sectionTitle = h.title,
                articleRef = h.articleRef,
                pageNumber = h.pageNumber,
                snippet = h.snippet,
                fullContent = h.snippet,
                score = h.score,
                highlights = h.highlights,
                isArticleMatch = h.kind == BookSearch.Kind.ARTICLE
            )
        }
        return results to outcome.message
    }

    /** Misma búsqueda sin mensaje (compatibilidad con el código anterior). */
    fun searchDocument(
        documentId: String,
        documentTitle: String,
        query: String,
        assetPath: String? = null,
        context: Context? = null
    ): List<BookSearchResult> = searchOutcome(documentId, documentTitle, query, assetPath, context).first
}
```

## 3) Cambios a archivos existentes (aplicar exactamente estos diffs)

### ui/PdfReaderScreen.kt
```diff
diff --git a/app/src/main/java/com/example/ui/PdfReaderScreen.kt b/app/src/main/java/com/example/ui/PdfReaderScreen.kt
index f17fdd3..de23b72 100644
--- a/app/src/main/java/com/example/ui/PdfReaderScreen.kt
+++ b/app/src/main/java/com/example/ui/PdfReaderScreen.kt
@@ -40,6 +40,7 @@ import com.example.viewmodel.ElectoralViewModel
 fun PdfReaderScreen(
     document: PdfDocument,
     initialPageIndex: Int = 0,
+    highlightQuery: String = "",
     onClose: () -> Unit,
     viewModel: ElectoralViewModel,
     modifier: Modifier = Modifier
@@ -52,6 +53,10 @@ fun PdfReaderScreen(
     var currentBitmap by remember { mutableStateOf<Bitmap?>(null) }
     var isRenderingPage by remember { mutableStateOf(false) }
     var showInReaderSearch by remember { mutableStateOf(false) }
+    // Lo buscado se subraya en amarillo sobre la página (viene de la búsqueda del libro) y en el panel de texto.
+    var activeHighlight by remember(document, highlightQuery) { mutableStateOf(highlightQuery) }
+    var highlightBoxes by remember { mutableStateOf<List<com.example.data.BookSearch.Box>>(emptyList()) }
+    var showPageText by remember { mutableStateOf(false) }
 
     // Touch gesture zoom and pan
     var scale by remember { mutableStateOf(1f) }
@@ -87,6 +92,15 @@ fun PdfReaderScreen(
 
     val totalPages = renderSession?.totalPages ?: document.pages.size.coerceAtLeast(1)
 
+    // Posición de las palabras a subrayar en la página actual (se calcula fuera del hilo principal)
+    LaunchedEffect(currentPageIndex, activeHighlight, document) {
+        highlightBoxes = if (activeHighlight.isBlank()) emptyList<com.example.data.BookSearch.Box>() else kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
+            val file = com.example.data.BookSearch.resolveFileName(document.id, document.assetPath)
+            if (file == null) emptyList<com.example.data.BookSearch.Box>()
+            else com.example.data.BookSearch.highlightBoxes(context.applicationContext, file, currentPageIndex + 1, activeHighlight)
+        }
+    }
+
     // Render bitmap when page or session changes
     LaunchedEffect(currentPageIndex, renderSession) {
         scale = 1f
@@ -163,6 +177,13 @@ fun PdfReaderScreen(
                         .testTag("rendered_pdf_page_image"),
                     contentScale = ContentScale.Fit
                 )
+                PdfHighlightOverlay(
+                    boxes = highlightBoxes,
+                    bitmapWidth = currentBitmap!!.width,
+                    bitmapHeight = currentBitmap!!.height,
+                    scale = scale,
+                    offset = offset
+                )
             } else if (isRenderingPage) {
                 CircularProgressIndicator(
                     color = MaterialTheme.colorScheme.primary,
@@ -302,6 +323,30 @@ fun PdfReaderScreen(
                 )
             }
 
+            // Texto de la página (con lo buscado en amarillo)
+            Button(
+                onClick = { showPageText = true },
+                colors = ButtonDefaults.buttonColors(
+                    containerColor = Color.Black.copy(alpha = 0.75f),
+                    contentColor = Color.White
+                ),
+                shape = RoundedCornerShape(20.dp),
+                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
+                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
+                modifier = Modifier
+                    .height(38.dp)
+                    .testTag("btn_reader_page_text")
+            ) {
+                Icon(
+                    imageVector = Icons.Default.Description,
+                    contentDescription = "Texto de la página",
+                    tint = SearchHighlightYellow,
+                    modifier = Modifier.size(18.dp)
+                )
+                Spacer(modifier = Modifier.width(6.dp))
+                Text(text = "TEXTO", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = Color.White)
+            }
+
             // Close button
             Button(
                 onClick = onClose,
@@ -337,13 +382,24 @@ fun PdfReaderScreen(
             InReaderSearchModal(
                 document = document,
                 onClose = { showInReaderSearch = false },
-                onSelectPage = { page ->
+                onSelectPage = { page, query ->
                     currentPageIndex = (page - 1).coerceIn(0, totalPages - 1)
+                    activeHighlight = query
                     showInReaderSearch = false
                 }
             )
         }
 
+        if (showPageText) {
+            PageTextSheet(
+                document = document,
+                pageNumber = currentPageIndex + 1,
+                query = activeHighlight,
+                onClearHighlight = { activeHighlight = "" },
+                onClose = { showPageText = false }
+            )
+        }
+
         // Minimalist Bottom Page Indicator & Navigation (Discreet overlay)
         if (totalPages > 1) {
             Surface(
@@ -406,21 +462,30 @@ fun PdfReaderScreen(
 private fun InReaderSearchModal(
     document: PdfDocument,
     onClose: () -> Unit,
-    onSelectPage: (Int) -> Unit
+    onSelectPage: (Int, String) -> Unit
 ) {
     val context = androidx.compose.ui.platform.LocalContext.current
     var query by remember { mutableStateOf("") }
-    val results = remember(query, document) {
-        if (query.trim().isNotEmpty()) {
-            BookInternalSearchEngine.searchDocument(
-                documentId = document.id,
-                documentTitle = document.title,
-                query = query,
-                assetPath = document.assetPath,
-                context = context
-            )
+    // Búsqueda en segundo plano, con una pequeña espera mientras se escribe
+    var results by remember { mutableStateOf<List<com.example.data.BookSearchResult>>(emptyList()) }
+    var searchMessage by remember { mutableStateOf<String?>(null) }
+    var isSearching by remember { mutableStateOf(false) }
+    LaunchedEffect(query, document) {
+        if (query.trim().isEmpty()) {
+            results = emptyList(); searchMessage = null; isSearching = false
         } else {
-            emptyList()
+            isSearching = true
+            kotlinx.coroutines.delay(250)
+            val (found, message) = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
+                BookInternalSearchEngine.searchOutcome(
+                    documentId = document.id,
+                    documentTitle = document.title,
+                    query = query,
+                    assetPath = document.assetPath,
+                    context = context
+                )
+            }
+            results = found; searchMessage = message; isSearching = false
         }
     }
 
@@ -499,6 +564,13 @@ private fun InReaderSearchModal(
                         modifier = Modifier.padding(24.dp)
                     )
                 }
+            } else if (results.isEmpty() && isSearching) {
+                Box(
+                    modifier = Modifier
+                        .fillMaxWidth()
+                        .weight(1f),
+                    contentAlignment = Alignment.Center
+                ) { CircularProgressIndicator(modifier = Modifier.size(32.dp)) }
             } else if (results.isEmpty()) {
                 Box(
                     modifier = Modifier
@@ -507,7 +579,7 @@ private fun InReaderSearchModal(
                     contentAlignment = Alignment.Center
                 ) {
                     Text(
-                        text = "No se encontraron coincidencias en ${document.title}.",
+                        text = searchMessage ?: "No se encontraron coincidencias en ${document.title}.",
                         fontSize = 13.5.sp,
                         color = MaterialTheme.colorScheme.onSurfaceVariant,
                         textAlign = androidx.compose.ui.text.style.TextAlign.Center
@@ -530,7 +602,7 @@ private fun InReaderSearchModal(
                     items(results.size) { index ->
                         val res = results[index]
                         Card(
-                            onClick = { onSelectPage(res.pageNumber) },
+                            onClick = { onSelectPage(res.pageNumber, query) },
                             shape = RoundedCornerShape(10.dp),
                             colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                             modifier = Modifier.fillMaxWidth()
@@ -563,7 +635,7 @@ private fun InReaderSearchModal(
                                 }
                                 Spacer(modifier = Modifier.height(4.dp))
                                 Text(
-                                    text = res.snippet,
+                                    text = highlightedText(res.snippet, res.highlights),
                                     fontSize = 12.sp,
                                     lineHeight = 16.sp,
                                     color = MaterialTheme.colorScheme.onSurfaceVariant
```

### ui/BookInternalSearchScreen.kt
```diff
diff --git a/app/src/main/java/com/example/ui/BookInternalSearchScreen.kt b/app/src/main/java/com/example/ui/BookInternalSearchScreen.kt
index 4608899..0b5c57d 100644
--- a/app/src/main/java/com/example/ui/BookInternalSearchScreen.kt
+++ b/app/src/main/java/com/example/ui/BookInternalSearchScreen.kt
@@ -19,6 +19,9 @@ import androidx.compose.ui.Modifier
 import androidx.compose.ui.draw.clip
 import androidx.compose.ui.graphics.Color
 import androidx.compose.ui.platform.testTag
+import kotlinx.coroutines.Dispatchers
+import kotlinx.coroutines.delay
+import kotlinx.coroutines.withContext
 import androidx.compose.ui.text.SpanStyle
 import androidx.compose.ui.text.buildAnnotatedString
 import androidx.compose.ui.text.font.FontWeight
@@ -34,24 +37,33 @@ import com.example.data.PdfDocument
 fun BookInternalSearchScreen(
     document: PdfDocument,
     onClose: () -> Unit,
-    onOpenPage: (Int) -> Unit,
+    onOpenPage: (Int, String) -> Unit,
     onConsultAi: (queryText: String) -> Unit,
     modifier: Modifier = Modifier
 ) {
     val context = androidx.compose.ui.platform.LocalContext.current
     var searchQuery by remember { mutableStateOf("") }
 
-    val searchResults = remember(searchQuery, document) {
-        if (searchQuery.trim().isNotEmpty()) {
-            BookInternalSearchEngine.searchDocument(
-                documentId = document.id,
-                documentTitle = document.title,
-                query = searchQuery,
-                assetPath = document.assetPath,
-                context = context
-            )
+    // La búsqueda corre en segundo plano y espera a que el usuario termine de escribir (no traba la pantalla).
+    var searchResults by remember { mutableStateOf<List<BookSearchResult>>(emptyList()) }
+    var searchMessage by remember { mutableStateOf<String?>(null) }
+    var isSearching by remember { mutableStateOf(false) }
+    LaunchedEffect(searchQuery, document) {
+        if (searchQuery.trim().isEmpty()) {
+            searchResults = emptyList(); searchMessage = null; isSearching = false
         } else {
-            emptyList()
+            isSearching = true
+            delay(250)
+            val (found, message) = withContext(Dispatchers.Default) {
+                BookInternalSearchEngine.searchOutcome(
+                    documentId = document.id,
+                    documentTitle = document.title,
+                    query = searchQuery,
+                    assetPath = document.assetPath,
+                    context = context
+                )
+            }
+            searchResults = found; searchMessage = message; isSearching = false
         }
     }
 
@@ -60,8 +72,8 @@ fun BookInternalSearchScreen(
         when (document.id) {
             "codigo_electoral_decreto_413" -> listOf(
                 "Art. 190 (Instalación)", "Art. 3 (Sufragio)", "Art. 7 (Inhábiles)",
-                "Art. 38 (Organismos)", "Art. 113 (Permiso laboral)", "Art. 284 (Armas)",
-                "Art. 205 (Voto nulo)", "Escrutinio", "Vigilantes"
+                "Art. 38 (Organismos)", "Art. 113 (Permiso laboral)", "Art. 290 (Armas)",
+                "Art. 207 (Voto nulo)", "Escrutinio", "Vigilantes"
             )
             "constitucion_republica_1983" -> listOf(
                 "Art. 72 (Derechos)", "Art. 73 (Deberes)", "Art. 75 (Pérdida derechos)",
@@ -74,9 +86,6 @@ fun BookInternalSearchScreen(
             "ley_de_partidos_politicos" -> listOf(
                 "Vigilantes", "Fiscalización", "Propaganda", "Derechos partidarios"
             )
-            "ley_genero_electoral" -> listOf(
-                "Violencia política", "Paridad", "Inclusión", "LEIV"
-            )
             else -> listOf("Artículos", "Votación", "Escrutinio", "JRV", "Sanciones")
         }
     }
@@ -255,6 +264,10 @@ fun BookInternalSearchScreen(
                     textAlign = androidx.compose.ui.text.style.TextAlign.Center
                 )
             }
+        } else if (searchResults.isEmpty() && isSearching) {
+            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
+                CircularProgressIndicator(modifier = Modifier.size(32.dp))
+            }
         } else if (searchResults.isEmpty()) {
             // No results state
             Column(
@@ -279,7 +292,7 @@ fun BookInternalSearchScreen(
                 )
                 Spacer(modifier = Modifier.height(6.dp))
                 Text(
-                    text = "No se encontraron coincidencias para «$searchQuery» en ${document.title}. Intenta con otras palabras clave o número de artículo.",
+                    text = searchMessage ?: "No se encontraron coincidencias para «$searchQuery» en ${document.title}. Intenta con otras palabras clave o número de artículo.",
                     fontSize = 12.5.sp,
                     color = MaterialTheme.colorScheme.onSurfaceVariant,
                     textAlign = androidx.compose.ui.text.style.TextAlign.Center
@@ -319,9 +332,12 @@ fun BookInternalSearchScreen(
                     BookSearchResultCard(
                         result = result,
                         searchQuery = searchQuery,
-                        onOpenPage = { onOpenPage(result.pageNumber) },
+                        onOpenPage = { onOpenPage(result.pageNumber, searchQuery) },
                         onConsultAi = {
-                            val prompt = "¿Qué establece el ${result.articleRef ?: result.sectionTitle} de '${result.documentTitle}' y cuál es su procedimiento?"
+                            // Si el libro tiene banco de artículos, se busca dentro del libro (el artículo o lo que escribió el usuario).
+                            val hasBank = com.example.data.search.codigo.LawLibrary.byPdfDocId(document.id) != null
+                            val prompt = if (hasBank) (result.articleRef ?: searchQuery)
+                            else "¿Qué establece el ${result.articleRef ?: result.sectionTitle} de '${result.documentTitle}' y cuál es su procedimiento?"
                             onConsultAi(prompt)
                         }
                     )
@@ -396,8 +412,8 @@ fun BookSearchResultCard(
             Spacer(modifier = Modifier.height(6.dp))
 
             // Snippet with highlighted search query
-            val annotatedSnippet = remember(result.snippet, searchQuery) {
-                buildHighlightedString(result.snippet, searchQuery)
+            val annotatedSnippet = remember(result.snippet, result.highlights) {
+                highlightedText(result.snippet, result.highlights)
             }
 
             Surface(
```

### ui/BibliotecaScreen.kt
```diff
diff --git a/app/src/main/java/com/example/ui/BibliotecaScreen.kt b/app/src/main/java/com/example/ui/BibliotecaScreen.kt
index 3f4951e..93720cc 100644
--- a/app/src/main/java/com/example/ui/BibliotecaScreen.kt
+++ b/app/src/main/java/com/example/ui/BibliotecaScreen.kt
@@ -30,14 +30,17 @@ fun BibliotecaScreen(
     val context = LocalContext.current
     val directDoc by viewModel.activePdfReaderDoc.collectAsState()
     val directPage by viewModel.activePdfReaderPage.collectAsState()
+    val directHighlight by viewModel.activePdfReaderHighlight.collectAsState()
     var viewingDocument by remember { mutableStateOf<PdfDocument?>(null) }
     var searchingDocument by remember { mutableStateOf<PdfDocument?>(null) }
     var readerInitialPage by remember { mutableStateOf(0) }
+    var readerHighlightQuery by remember { mutableStateOf("") }
 
     LaunchedEffect(directDoc, directPage) {
         if (directDoc != null) {
             viewingDocument = directDoc
             readerInitialPage = directPage
+            readerHighlightQuery = directHighlight
         }
     }
 
@@ -101,8 +104,9 @@ fun BibliotecaScreen(
         BookInternalSearchScreen(
             document = searchingDocument!!,
             onClose = { searchingDocument = null },
-            onOpenPage = { page ->
+            onOpenPage = { page, query ->
                 viewingDocument = searchingDocument
+                readerHighlightQuery = query
                 readerInitialPage = (page - 1).coerceAtLeast(0)
                 searchingDocument = null
             },
@@ -122,9 +126,11 @@ fun BibliotecaScreen(
         PdfReaderScreen(
             document = viewingDocument!!,
             initialPageIndex = readerInitialPage,
+            highlightQuery = readerHighlightQuery,
             onClose = {
                 viewingDocument = null
                 readerInitialPage = 0
+                readerHighlightQuery = ""
                 viewModel.closeDirectPdfReader()
             },
             viewModel = viewModel,
@@ -271,6 +277,7 @@ fun BibliotecaScreen(
                                 onClick = {
                                     viewingDocument = doc
                                     readerInitialPage = 0
+                                    readerHighlightQuery = ""
                                 },
                                 shape = RoundedCornerShape(10.dp),
                                 modifier = Modifier
```

## 4) No toques nada más. Pruebas (en el teléfono)
1. Biblioteca → Código Electoral → buscador del libro → escribe `artículo 2`: debe salir SOLO el Art. 2 (Pág. 1), no una lista larga. `198` → Art. 198, página 67. `artículos 200 al 202` → 3 resultados. `art 9999` → mensaje «no aparece en este documento».
2. Escribe `ebriedad`: resultado en la página 85 con la palabra en amarillo en el fragmento. Toca el resultado: se abre la página 85 con la palabra subrayada en amarillo SOBRE el PDF.
3. Escribe `bebidas embriagantes` (frase): resultado en la página 96 y las dos palabras en amarillo. Prueba con y sin tildes y en mayúsculas (`EBRIEDAD`): mismo resultado.
4. Botón «TEXTO» (arriba del lector): panel con el texto de la página y lo buscado en amarillo; «Quitar subrayado» lo limpia.
5. Haz zoom con dos dedos y mueve la página: el amarillo debe seguir pegado a las palabras.
6. Escribe despacio en el buscador: no debe trabarse la pantalla (la búsqueda espera un instante y corre en segundo plano).
7. Repite en el Código Penal, el Instructivo JRV (buscar `papeletas sobrantes` → páginas 34, 35, 37, 40, 46) y la Constitución (`72` → página 15).
8. Abre un PDF con «ABRIR PDF»: NO debe aparecer subrayado.
