import android.content.Context
import android.content.res.AssetManager
import com.example.data.search.engine.ElectoralSearchEngine
import com.example.data.search.knowledge.ElectoralKnowledgeBase
import java.io.File
import com.example.data.search.engine.ElectoralScoringEngine
import com.example.data.search.normalization.ElectoralNormalizer
import com.example.data.search.entities.ElectoralEntityExtractor

/**
 * Ejecuta cada pregunta de un archivo "ID_ESPERADO[,ID_ALTERNATIVO]|pregunta" contra el
 * motor real de la app y reporta aciertos. "-" como ID esperado = debe rechazarse (LOW).
 * Argumentos: <archivo_preguntas> [carpeta_assets]. Con DBG=1 muestra el desglose de puntos.
 */
fun main(args: Array<String>) {
    val assets = if (args.size > 1) args[1] else "../../../app/src/main/assets"
    ElectoralKnowledgeBase.init(Context(AssetManager(File(assets))))
    println("Situaciones cargadas: " + ElectoralKnowledgeBase.getAll().size)
    var ok = 0; var total = 0
    for (line in File(args[0]).readLines()) {
        if (line.isBlank() || !line.contains("|")) continue
        val (expected, q) = line.split("|", limit = 2)
        val r = ElectoralSearchEngine.search(q.trim())
        val got = r.topSituation?.id ?: "-"
        val alts = r.clarificationOptions.map { it.id }
        val exp = expected.trim().split(",")
        val pass = (exp == listOf("-") && r.matchLevel.name == "LOW") || got in exp || (r.matchLevel.name == "MEDIUM" && alts.any { it in exp })
        total++; if (pass) ok++
        if (!pass && System.getenv("DBG") != null) {
            val nq = ElectoralNormalizer.normalize(q.trim()); val en = ElectoralEntityExtractor.extract(nq.cleanedText, q.trim())
            println("   ctx=${en.context} intent=${en.intent} kws=${nq.keywords}")
            for (id in listOf(got) + exp) { val s = ElectoralKnowledgeBase.findById(id) ?: continue
                val sc = ElectoralScoringEngine.scoreSituation(s, nq, en); println("   $id ${sc.totalScore} ${sc.scoreBreakdown}") }
        }
        println("${if (pass) "OK " else "XX "} [${r.matchLevel}] ${"%.0f".format(r.score)} esperado=$expected obtenido=$got alts=$alts :: $q")
    }
    val withRel = ElectoralKnowledgeBase.getAll().count { it.respuestasRelacionadas.isNotEmpty() }
    println("RELACIONADAS cargadas en $withRel situaciones; ejemplo ESC-001 -> " + ElectoralKnowledgeBase.findById("ESC-001")?.respuestasRelacionadas)
    val f = ElectoralKnowledgeBase.findById("ESC-001")!!.respuesta
    println("ESC-001 fundamento: " + f.fundamento.take(2) + " fuentes: " + f.fuentesDocumentales.take(1))
    println("RESULTADO: $ok / $total")
}
