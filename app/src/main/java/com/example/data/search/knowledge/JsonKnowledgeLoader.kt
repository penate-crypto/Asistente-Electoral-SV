package com.example.data.search.knowledge

import android.content.Context
import android.util.Log
import com.example.data.search.model.*
import org.json.JSONArray
import org.json.JSONObject

object JsonKnowledgeLoader {

    private const val TAG = "JsonKnowledgeLoader"
    private val CANDIDATE_FOLDERS = listOf(
        "json respuestas",
        "json_respuestas",
        "jsonrespuestas",
        "respuestas",
        "banco_conocimiento",
        "banco_conocimientos"
    )

    /**
     * Scans and loads all JSON knowledge base files from assets/json respuestas/ and candidate folders
     */
    fun loadAllFromAssets(context: Context): List<ElectoralSituation> {
        val situations = mutableListOf<ElectoralSituation>()
        val loadedFileNames = mutableSetOf<String>()

        try {
            val assetManager = context.assets

            // 1. Scan specific candidate folders
            for (folder in CANDIDATE_FOLDERS) {
                try {
                    val files = assetManager.list(folder) ?: emptyArray()
                    for (fileName in files) {
                        if (fileName.endsWith(".json", ignoreCase = true)) {
                            val fullPath = "$folder/$fileName"
                            if (loadedFileNames.add(fullPath)) {
                                try {
                                    val jsonString = assetManager.open(fullPath).bufferedReader().use { it.readText() }
                                    val parsed = parseKnowledgeJson(jsonString)
                                    situations.addAll(parsed)
                                    Log.d(TAG, "Successfully loaded ${parsed.size} situations from $fullPath")
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error parsing JSON file: $fullPath", e)
                                }
                            }
                        }
                    }
                } catch (ignored: Exception) {}
            }

            // 2. Scan root assets for any knowledge base JSON files
            try {
                val rootFiles = assetManager.list("") ?: emptyArray()
                for (fileName in rootFiles) {
                    if (fileName.endsWith(".json", ignoreCase = true) &&
                        (fileName.contains("conocimiento", ignoreCase = true) ||
                         fileName.contains("respuesta", ignoreCase = true) ||
                         fileName.contains("situacion", ignoreCase = true) ||
                         fileName.contains("banco", ignoreCase = true))
                    ) {
                        if (loadedFileNames.add(fileName)) {
                            try {
                                val jsonString = assetManager.open(fileName).bufferedReader().use { it.readText() }
                                val parsed = parseKnowledgeJson(jsonString)
                                situations.addAll(parsed)
                                Log.d(TAG, "Successfully loaded ${parsed.size} situations from root asset $fileName")
                            } catch (e: Exception) {
                                Log.e(TAG, "Error parsing root asset JSON: $fileName", e)
                            }
                        }
                    }
                }
            } catch (ignored: Exception) {}

        } catch (e: Exception) {
            Log.e(TAG, "Error accessing assets", e)
        }

        return situations
    }

    /**
     * Parses a knowledge base JSON file conforming to the official schema.
     * Supports both root JSONObject (with "situaciones", "registros", etc.) and root JSONArray.
     */
    fun parseKnowledgeJson(jsonString: String): List<ElectoralSituation> {
        val result = mutableListOf<ElectoralSituation>()
        val trimmed = jsonString.trim()
        if (trimmed.isEmpty()) return result

        val situacionesArray: JSONArray = when {
            trimmed.startsWith("[") -> {
                try {
                    JSONArray(trimmed)
                } catch (e: Exception) {
                    JSONArray()
                }
            }
            trimmed.startsWith("{") -> {
                try {
                    val root = JSONObject(trimmed)
                    root.optJSONArray("situaciones")
                        ?: root.optJSONArray("situacion")
                        ?: root.optJSONArray("registros")
                        ?: root.optJSONArray("banco")
                        ?: root.optJSONArray("items")
                        ?: JSONArray()
                } catch (e: Exception) {
                    JSONArray()
                }
            }
            else -> JSONArray()
        }

        for (i in 0 until situacionesArray.length()) {
            val obj = situacionesArray.optJSONObject(i) ?: continue

            val id = obj.optString("id", "SIT-${System.currentTimeMillis()}-${i}")
            val titulo = obj.optString("titulo").ifBlank { obj.optString("title", "") }
            val categoria = obj.optString("categoria").ifBlank { obj.optString("category", "General") }
            val subcategoria = obj.optString("subcategoria").ifBlank { obj.optString("subcategory", "") }
            val descripcion = obj.optString("descripcion_situacion").ifBlank { obj.optString("descripcion", "") }

            // Actores, objetos, acciones, contextos, intenciones, palabras_clave
            val actores = jsonArrayToStringList(obj.optJSONArray("actores") ?: obj.optJSONArray("actors"))
            val objetos = jsonArrayToStringList(obj.optJSONArray("objetos") ?: obj.optJSONArray("objects"))
            val acciones = jsonArrayToStringList(obj.optJSONArray("acciones") ?: obj.optJSONArray("actions"))
            val contextos = jsonArrayToStringList(obj.optJSONArray("contextos") ?: obj.optJSONArray("contexto"))
            val intenciones = jsonArrayToStringList(obj.optJSONArray("intenciones") ?: obj.optJSONArray("intents"))
            val palabrasClave = jsonArrayToStringList(obj.optJSONArray("palabras_clave") ?: obj.optJSONArray("keywords") ?: obj.optJSONArray("tags"))

            // Sinónimos (support object map or array)
            val sinonimosList = mutableListOf<String>()
            val sinonimosObj = obj.optJSONObject("sinonimos") ?: obj.optJSONObject("synonyms")
            if (sinonimosObj != null) {
                val keys = sinonimosObj.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    sinonimosList.add(key)
                    val arr = sinonimosObj.optJSONArray(key)
                    if (arr != null) {
                        sinonimosList.addAll(jsonArrayToStringList(arr))
                    }
                }
            } else {
                val sinArray = obj.optJSONArray("sinonimos") ?: obj.optJSONArray("synonyms")
                sinonimosList.addAll(jsonArrayToStringList(sinArray))
            }

            // Parse Structured Response
            val respObj = obj.optJSONObject("respuesta") ?: obj.optJSONObject("response")
            val situacionDetectada = respObj?.optString("situacion_detectada")
                ?.ifBlank { respObj.optString("situacion", descripcion) }
                ?: descripcion

            val queHacer = jsonArrayToStringList(
                respObj?.optJSONArray("que_hacer")
                    ?: respObj?.optJSONArray("procedimiento")
                    ?: respObj?.optJSONArray("pasos")
            )

            val queNoHacer = jsonArrayToStringList(
                respObj?.optJSONArray("que_no_hacer")
                    ?: respObj?.optJSONArray("prohibiciones")
                    ?: respObj?.optJSONArray("no_hacer")
            )

            val advertencia = if (respObj != null && respObj.has("advertencia")) respObj.optString("advertencia") else null

            // Autorizaciones (Quién puede / Quién no puede)
            val quienInterviene = jsonArrayToStringList(respObj?.optJSONArray("quien_interviene"))
            val quienPuede = jsonArrayToStringList(respObj?.optJSONArray("quien_puede"))
            val quienNoPuede = jsonArrayToStringList(respObj?.optJSONArray("quien_no_puede"))
            val condiciones = jsonArrayToStringList(respObj?.optJSONArray("condiciones"))

            val authRules = if (quienPuede.isNotEmpty() || quienNoPuede.isNotEmpty() || condiciones.isNotEmpty() || quienInterviene.isNotEmpty()) {
                AuthorizationRules(
                    quienPuede = if (quienPuede.isNotEmpty()) quienPuede else quienInterviene,
                    quienNoPuede = quienNoPuede,
                    condiciones = condiciones
                )
            } else null

            // Fundamento Legal
            val fundamentosDetallados = mutableListOf<LegalFoundationItem>()
            val fundamentosTexto = mutableListOf<String>()
            val fundArray = respObj?.optJSONArray("fundamento_legal")
                ?: respObj?.optJSONArray("fundamentos")
                ?: respObj?.optJSONArray("articulos")

            if (fundArray != null) {
                for (j in 0 until fundArray.length()) {
                    val fItem = fundArray.optJSONObject(j)
                    if (fItem != null) {
                        val ley = fItem.optString("ley", "Código Electoral")
                        val arts = fItem.optJSONArray("articulos")
                        if (arts != null) {
                            for (k in 0 until arts.length()) {
                                val artObj = arts.optJSONObject(k)
                                if (artObj != null) {
                                    val num = artObj.optString("numero", "")
                                    val fuenteDesc = artObj.optString("fuente", "")
                                    fundamentosDetallados.add(
                                        LegalFoundationItem(
                                            ley = ley,
                                            articulo = "Art. $num",
                                            descripcion = fuenteDesc
                                        )
                                    )
                                    fundamentosTexto.add("$ley — Art. $num: $fuenteDesc")
                                }
                            }
                        } else {
                            val artNum = fItem.optString("articulo", fItem.optString("numero", ""))
                            val desc = fItem.optString("descripcion", fItem.optString("fuente", ""))
                            if (artNum.isNotBlank() || desc.isNotBlank()) {
                                fundamentosDetallados.add(
                                    LegalFoundationItem(
                                        ley = ley,
                                        articulo = if (artNum.startsWith("Art", ignoreCase = true)) artNum else "Art. $artNum",
                                        descripcion = desc
                                    )
                                )
                                fundamentosTexto.add("$ley — $artNum: $desc")
                            }
                        }
                    } else {
                        val strVal = fundArray.optString(j)
                        if (strVal.isNotBlank()) {
                            fundamentosTexto.add(strVal)
                        }
                    }
                }
            }

            // Fuentes Documentales
            val fuentesDocumentales = mutableListOf<DocumentarySource>()
            val fuentesTexto = mutableListOf<String>()
            val fuentesArr = respObj?.optJSONArray("fuentes") ?: respObj?.optJSONArray("sources")
            if (fuentesArr != null) {
                for (j in 0 until fuentesArr.length()) {
                    val fuenteObj = fuentesArr.optJSONObject(j)
                    if (fuenteObj != null) {
                        val nombre = fuenteObj.optString("nombre", "TSE")
                        val tipo = fuenteObj.optString("tipo", "Oficial")
                        val paginaStr = fuenteObj.optString("pagina", "")
                        val seccion = fuenteObj.optString("seccion", "")
                        val pagInt = paginaStr.filter { it.isDigit() }.toIntOrNull() ?: 1

                        val pdfDocId = fuenteObj.optString("pdf_doc_id", "")
                            .ifBlank { "codigo_electoral_decreto_413" }
                        val articuloRel = fuenteObj.optString("documento_relacionado", "")
                            .ifBlank { null }
                        fuentesDocumentales.add(
                            DocumentarySource(
                                documento = nombre,
                                tipoDocumento = tipo,
                                pagina = pagInt,
                                articuloRelacionado = articuloRel,
                                seccion = seccion,
                                pdfDocId = pdfDocId
                            )
                        )
                        fuentesTexto.add("$nombre ($tipo)${if (paginaStr.isNotBlank()) " — Pág. $paginaStr" else ""}")
                    } else {
                        val strFuente = fuentesArr.optString(j)
                        if (strFuente.isNotBlank()) {
                            fuentesTexto.add(strFuente)
                        }
                    }
                }
            }

            val structuredResponse = StructuredElectoralResponse(
                situacion = situacionDetectada,
                fundamento = fundamentosTexto,
                fundamentosDetallados = fundamentosDetallados,
                procedimiento = queHacer,
                noHacer = queNoHacer,
                autorizaciones = authRules,
                fuentes = fuentesTexto,
                fuentesDocumentales = fuentesDocumentales,
                advertencia = advertencia
            )

            val estadoStr = obj.optString("estado", "vigente").lowercase()
            val estadoEnum = when (estadoStr) {
                "obsoleto" -> RecordStatus.OBSOLETO
                "revisar" -> RecordStatus.REVISAR
                else -> RecordStatus.VIGENTE
            }

            val nivelPrioridadStr = obj.optString("nivel_prioridad", "normal").lowercase()
            val prioridadNum = if (nivelPrioridadStr == "alta") 1 else 2

            result.add(
                ElectoralSituation(
                    id = id,
                    categoria = categoria,
                    subcategoria = subcategoria,
                    titulo = titulo,
                    descripcionSituacion = descripcion,
                    keywords = palabrasClave,
                    sinonimos = sinonimosList,
                    actores = actores,
                    acciones = acciones,
                    objetos = objetos,
                    lugares = contextos,
                    momentos = contextos,
                    contextos = contextos,
                    intenciones = intenciones,
                    contexto = contextos.firstOrNull() ?: "",
                    prioridadJerarquica = prioridadNum,
                    estado = estadoEnum,
                    respuesta = structuredResponse,
                    respuestasRelacionadas = jsonArrayToStringList(
                        obj.optJSONArray("situaciones_relacionadas")
                            ?: obj.optJSONArray("relacionadas")
                            ?: obj.optJSONArray("related")
                    )
                )
            )
        }

        return result
    }

    private fun jsonArrayToStringList(array: JSONArray?): List<String> {
        if (array == null) return emptyList()
        val list = mutableListOf<String>()
        for (i in 0 until array.length()) {
            val item = array.optString(i)
            if (!item.isNullOrBlank()) {
                list.add(item)
            }
        }
        return list
    }
}
