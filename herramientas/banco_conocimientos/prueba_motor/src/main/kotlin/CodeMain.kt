// Prueba del buscador del Banco Código Electoral.
// Uso: MAINCLS=CodeMainKt gradle -q run --args="../../banco_codigo_electoral/preguntas/ce1_ejemplos.txt"
// LEY=constitucion para probar la Constitución (por defecto Código Electoral).
// Formato: "ARTICULOS_ESPERADOS|pregunta" (el primero mostrado debe estar en la lista); "-" = no debe responder el Código; "ONLY:n" = solo el artículo n.
import android.content.Context
import android.content.res.AssetManager
import com.example.data.search.codigo.*
import java.io.File

fun main(args: Array<String>) {
    val assets = if (args.size > 1) args[1] else "../../../app/src/main/assets"
    ElectoralCodeBank.init(Context(AssetManager(File(assets))))
    ElectoralConstitutionBank.init(Context(AssetManager(File(assets))))
    val search = if (System.getenv("LEY") == "constitucion") ElectoralConstitutionSearch else ElectoralCodeSearch
    println("Artículos cargados: " + ElectoralCodeBank.all().size + " (Código) / " + ElectoralConstitutionBank.all().size + " (Constitución)")
    var ok = 0; var total = 0
    for (line in File(args[0]).readLines()) {
        if (line.isBlank() || !line.contains("|")) continue
        val (expected, q) = line.split("|", limit = 2)
        val r = search.search(q.trim())
        val got = r?.articles?.map { it.numero } ?: emptyList()
        val exp = expected.trim()
        val pass = when {
            exp == "-" -> r == null
            exp.startsWith("ONLY:") -> got.size == 1 && got[0] == exp.removePrefix("ONLY:")
            else -> r != null && got.isNotEmpty() && got.first() in exp.split(",")
        }
        total++; if (pass) ok++
        println((if (pass) "OK  " else "XX  ") + "esperado=$exp obtenido=$got +${r?.moreCount ?: 0} [${r?.kind}] :: $q " + (r?.articles?.firstOrNull()?.titulo ?: ""))
    }
    println("RESULTADO: $ok / $total")
}
