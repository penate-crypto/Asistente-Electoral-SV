# PROMPT 1 para AI Studio — Arreglar los sinónimos (bolo = borracho = persona en estado de ebriedad)

Copia todo lo que está debajo de la línea y pégalo en AI Studio.

---

Necesito arreglar un problema en el buscador local de la app (sin internet, sin tokens, sin Gemini).

PROBLEMA: si el usuario escribe "persona bola", "un bolo quiere votar", "señor tomado", "borracha" o "alcoholizado", el buscador NO lo toma como lo mismo que "persona en estado de ebriedad". Los sinónimos solo funcionan frase por frase dentro de cada situación y las palabras genéricas ("persona", "votar", "mesa") le ganan a la palabra importante.

SOLUCIÓN: una lista GLOBAL de grupos de palabras con el mismo significado, que vive en un JSON y le da puntos extra a las situaciones que mencionan cualquiera de las palabras del grupo. NO cambies nada más de la app. NO uses internet ni la API de Gemini para esto.

PASOS (hazlos en este orden y no toques otros archivos):

1) Crea el archivo `app/src/main/assets/sinonimos/sinonimos_globales.json`. Su contenido exacto es el archivo `sinonimos_globales.json` que te adjunto (o que está en el repositorio, rama `claude/electoral-knowledge-base-json-ha2mpw`, ruta `app/src/main/assets/sinonimos/`). Cada grupo tiene "terminos": todas las formas de decir lo mismo (ebrio, borracho, bolo, bola, bolito, tomado, alcoholizado, "pasado de tragos", "aliento a licor"...).

2) Crea el archivo `app/src/main/java/com/example/data/search/synonyms/ElectoralConceptRegistry.kt` con EXACTAMENTE este código:

```kotlin
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
```

3) En `ElectoralKnowledgeBase.kt`, dentro de `fun init(context: Context)`, como PRIMERA línea del método agrega:

```kotlin
com.example.data.search.synonyms.ElectoralConceptRegistry.init(context)
```

4) En `ElectoralScoringEngine.kt`: agrega `import com.example.data.search.synonyms.ElectoralConceptRegistry` y, justo ANTES del `return ScoredSituation(` del final de `scoreSituation`, agrega este bloque:

```kotlin
// 15. Conceptos equivalentes (bolo = borracho = estado de ebriedad): +30 por concepto compartido (máx. 2)
val sharedConcepts = ElectoralConceptRegistry.queryConcepts(queryText)
    .intersect(ElectoralConceptRegistry.situationConcepts(situation))
if (sharedConcepts.isNotEmpty()) {
    val conceptBonus = 30.0 * minOf(sharedConcepts.size, 2)
    totalScore += conceptBonus
    breakdown["conceptos_equivalentes"] = conceptBonus
}
```

5) Prueba (no cambies el código si todo da bien). Estas preguntas deben abrir la situación "VOT-005" (persona en estado de ebriedad):
- persona bolo / persona bola / un bolo quiere votar / hay un señor tomado en la fila / una borracha llegó a votar / alguien alcoholizado en la mesa / un tipo ebrio / llegó un bolito a votar / un votante que anda pasado de tragos / persona con aliento a licor quiere votar
Y estas NO deben cambiar de respuesta: "se puede llevar un arma a la JRV", "el votante olvidó su DUI", "cuándo se cierra la votación", "cómo se instala la mesa".

6) Regla para el futuro: para agregar una palabra nueva con el mismo significado, SOLO se agrega al grupo correspondiente de `sinonimos_globales.json` (no hay que tocar el código). No agregues palabras genéricas (persona, mesa, votar, papeleta) a los grupos porque se roban las respuestas.

Resultado de mis pruebas del motor real: las 11 frases anteriores pasaron de 2/11 a 11/11 y las 140 preguntas de regresión siguen 140/140.
