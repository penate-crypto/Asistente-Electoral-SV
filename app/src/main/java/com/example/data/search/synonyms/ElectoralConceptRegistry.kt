package com.example.data.search.synonyms

import android.content.Context
import com.example.data.search.model.ElectoralSituation
import com.example.data.search.normalization.ElectoralNormalizer
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap

/**
 * Grupos de palabras con el mismo significado (ej. "bolo" = "borracho" = "persona en estado de ebriedad").
 * Se cargan de assets/sinonimos/sinonimos_globales.json. Sin internet y sin costo de tokens.
 */
object ElectoralConceptRegistry {
    private class Concept(val id: String, val terms: List<String>)

    @Volatile private var concepts: List<Concept> = emptyList()
    private val situationCache = ConcurrentHashMap<String, Set<String>>()
    @Volatile private var lastQuery: Pair<String, Set<String>>? = null

    fun init(context: Context) {
        try {
            val json = context.assets.open("sinonimos/sinonimos_globales.json").bufferedReader().use { it.readText() }
            val arr = JSONObject(json).getJSONArray("conceptos")
            val list = ArrayList<Concept>()
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val t = o.getJSONArray("terminos")
                val terms = (0 until t.length()).map { norm(t.getString(it)) }.filter { it.isNotBlank() }
                list.add(Concept(o.getString("id"), terms))
            }
            concepts = list
            situationCache.clear(); lastQuery = null
        } catch (e: Exception) {
            concepts = emptyList() // sin archivo: el motor funciona igual, solo sin esta mejora
        }
    }

    private fun norm(s: String) = ElectoralNormalizer.stripAccents(s.lowercase())
        .replace("[^a-z0-9\\s]".toRegex(), " ").replace("\\s+".toRegex(), " ").trim()

    private fun conceptsIn(text: String): Set<String> {
        val res = HashSet<String>()
        for (c in concepts) if (c.terms.any { ElectoralNormalizer.containsWholeWord(text, it) }) res.add(c.id)
        return res
    }

    /** Conceptos que menciona la pregunta (ya normalizada). Se recuerda la última pregunta. */
    fun queryConcepts(cleanQuery: String): Set<String> {
        lastQuery?.let { if (it.first == cleanQuery) return it.second }
        val r = conceptsIn(cleanQuery)
        lastQuery = cleanQuery to r
        return r
    }

    /** Conceptos que menciona la ficha en título, palabras clave y sinónimos (no en la descripción). */
    fun situationConcepts(s: ElectoralSituation): Set<String> =
        situationCache.getOrPut(s.id) {
            val sb = StringBuilder(norm(s.titulo))
            s.keywords.forEach { sb.append(" | ").append(norm(it)) }
            s.sinonimos.forEach { sb.append(" | ").append(norm(it)) }
            conceptsIn(sb.toString().replace("|", " "))
        }
}
