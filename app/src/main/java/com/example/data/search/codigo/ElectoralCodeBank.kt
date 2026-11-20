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

object ElectoralCodeBank {
    @Volatile private var articles: List<CodeArticle> = emptyList()
    @Volatile private var byNumber: Map<String, CodeArticle> = emptyMap()

    fun init(context: Context) {
        try {
            val json = context.assets.open("codigo_electoral/banco_codigo_electoral.json").bufferedReader().use { it.readText() }
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
