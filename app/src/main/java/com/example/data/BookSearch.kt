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

    @Volatile private var wordsCache: Pair<String, WordsFile>? = null

    private fun loadWords(context: Context, fileName: String): WordsFile? {
        wordsCache?.let { if (it.first == fileName) return it.second }
        return try {
            val json = context.assets.open("libros_palabras/$fileName.json").bufferedReader().use { it.readText() }
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
