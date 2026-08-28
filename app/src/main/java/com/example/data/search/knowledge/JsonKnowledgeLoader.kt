package com.example.data.search.knowledge

import android.content.Context
import android.util.Log
import com.example.data.search.model.*
import org.json.JSONArray
import org.json.JSONObject

object JsonKnowledgeLoader {

    private const val TAG = "JsonKnowledgeLoader"
    private const val ASSETS_FOLDER = "json respuestas"

    /**
     * Scans and loads all JSON knowledge base files from assets/json respuestas/
     */
    fun loadAllFromAssets(context: Context): List<ElectoralSituation> {
        val situations = mutableListOf<ElectoralSituation>()

        try {
            val assetManager = context.assets
            val files = assetManager.list(ASSETS_FOLDER) ?: emptyArray()

            for (fileName in files) {
                if (fileName.endsWith(".json", ignoreCase = true)) {
                    val fullPath = "$ASSETS_FOLDER/$fileName"
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
        } catch (e: Exception) {
            Log.e(TAG, "Error accessing assets folder: $ASSETS_FOLDER", e)
        }

        return situations
    }

    /**
     * Parses a knowledge base JSON file conforming to the official schema.
     */
    fun parseKnowledgeJson(jsonString: String): List<ElectoralSituation> {
        val result = mutableListOf<ElectoralSituation>()
        val root = JSONObject(jsonString)
        val situacionesArray = root.optJSONArray("situaciones") ?: JSONArray()

        for (i in 0 until situacionesArray.length()) {
            val obj = situacionesArray.optJSONObject(i) ?: continue

            val id = obj.optString("id", "SIT-${System.currentTimeMillis()}-${i}")
            val titulo = obj.optString("titulo", "")
            val categoria = obj.optString("categoria", "General")
            val subcategoria = obj.optString("subcategoria", "")
            val descripcion = obj.optString("descripcion_situacion", "")

            // Actores, objetos, acciones, contextos, intenciones, palabras_clave
            val actores = jsonArrayToStringList(obj.optJSONArray("actores"))
            val objetos = jsonArrayToStringList(obj.optJSONArray("objetos"))
            val acciones = jsonArrayToStringList(obj.optJSONArray("acciones"))
            val contextos = jsonArrayToStringList(obj.optJSONArray("contextos"))
            val intenciones = jsonArrayToStringList(obj.optJSONArray("intenciones"))
            val palabrasClave = jsonArrayToStringList(obj.optJSONArray("palabras_clave"))

            // Sinónimos
            val sinonimosList = mutableListOf<String>()
            val sinonimosObj = obj.optJSONObject("sinonimos")
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
            }

            // Parse Structured Response
            val respObj = obj.optJSONObject("respuesta")
            val situacionDetectada = respObj?.optString("situacion_detectada", descripcion) ?: descripcion
            val queHacer = jsonArrayToStringList(respObj?.optJSONArray("que_hacer"))
            val queNoHacer = jsonArrayToStringList(respObj?.optJSONArray("que_no_hacer"))
            val advertencia = respObj?.optString("advertencia", null)

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
                        }
                    }
                }
            }

            // Fuentes Documentales
            val fuentesDocumentales = mutableListOf<DocumentarySource>()
            val fuentesTexto = mutableListOf<String>()
            val fuentesArr = respObj?.optJSONArray("fuentes")
            if (fuentesArr != null) {
                for (j in 0 until fuentesArr.length()) {
                    val fuenteObj = fuentesArr.optJSONObject(j)
                    if (fuenteObj != null) {
                        val nombre = fuenteObj.optString("nombre", "TSE")
                        val tipo = fuenteObj.optString("tipo", "Oficial")
                        val paginaStr = fuenteObj.optString("pagina", "")
                        val seccion = fuenteObj.optString("seccion", "")
                        val pagInt = paginaStr.filter { it.isDigit() }.toIntOrNull() ?: 1

                        fuentesDocumentales.add(
                            DocumentarySource(
                                documento = nombre,
                                tipoDocumento = tipo,
                                pagina = pagInt,
                                seccion = seccion
                            )
                        )
                        fuentesTexto.add("$nombre ($tipo)${if (paginaStr.isNotBlank()) " — Pág. $paginaStr" else ""}")
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
                    keywords = palabrasClave,
                    sinonimos = sinonimosList,
                    actores = actores,
                    acciones = acciones,
                    objetos = objetos,
                    lugares = contextos,
                    momentos = contextos,
                    intenciones = intenciones,
                    contexto = contextos.firstOrNull() ?: "",
                    prioridadJerarquica = prioridadNum,
                    estado = estadoEnum,
                    respuesta = structuredResponse,
                    respuestasRelacionadas = emptyList()
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
