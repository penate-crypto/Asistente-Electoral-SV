package com.example.data.repository

import com.example.BuildConfig
import com.example.data.ElectoralLibraryData
import com.example.data.ElectoralSimulationRepository
import com.example.data.api.*
import com.example.data.database.QueryHistory
import com.example.data.database.QueryHistoryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ElectoralRepository(
    private val queryHistoryDao: QueryHistoryDao,
    private val apiService: GeminiApiService = RetrofitClient.service
) {
    val allHistory: Flow<List<QueryHistory>> = queryHistoryDao.getAllHistoryFlow()

    suspend fun getApiKeyStatus(): ApiKeyStatus {
        val key = BuildConfig.GEMINI_API_KEY
        return if (key.isBlank() || key == "MY_GEMINI_API_KEY") {
            ApiKeyStatus.MISSING
        } else {
            ApiKeyStatus.AVAILABLE
        }
    }

    suspend fun clearHistory() {
        withContext(Dispatchers.IO) {
            queryHistoryDao.clearAllHistory()
        }
    }

    /**
     * Common electoral vocabulary and questions to detect typos and generate up to 3 smart suggestions
     */
    private val electoralKnowledgeBank = listOf(
        "¿Cuáles son las funciones del Presidente de JRV?",
        "¿Cuáles son las funciones del Secretario de JRV?",
        "¿Cuáles son las funciones del Primer Vocal de JRV?",
        "¿Cuáles son las funciones del Segundo y Tercer Vocal?",
        "¿Con cuántos miembros se instala la JRV y qué pasa si faltan miembros?",
        "¿A qué hora se instala la mesa electoral y a qué hora abre la votación?",
        "¿Qué permisos de trabajo con goce de sueldo garantiza el Art. 113 del Código Electoral?",
        "¿Cuál es la diferencia entre voto válido, voto nulo y voto impugnado?",
        "¿Cómo se realiza el escrutinio preliminar paso a paso a partir de las 5:00 PM?",
        "¿Qué derechos y prohibiciones tienen los vigilantes de partidos políticos?",
        "¿Cuáles son las atribuciones del Tribunal Supremo Electoral (TSE)?",
        "¿Quiénes están autorizados a portar armas y cómo aplica el Art. 290 del Código Electoral?",
        "¿Cómo protege la ley LEIV (Art. 55) a las mujeres en el proceso electoral?",
        "¿Qué sanciones contempla la Ley Seca (Art. 284) y los delitos electorales?",
        "¿Qué hacer si un ciudadano no presenta su DUI o no aparece en el padrón?",
        "¿Cómo se garantiza el voto asistido para personas con discapacidad o adultos mayores?",
        "¿Qué funciones cumplen los Jefes de Centro y Supervisores del TSE?",
        "¿Qué papel cumple la Policía Nacional Civil (PNC) y la Fiscalía Electoral?",
        "¿Cómo se califica el voto por coalición en el escrutinio preliminar?",
        "¿Qué son y cómo funcionan las JED y JEM en cada departamento y municipio?"
    )

    /**
     * Generate up to 3 "¿Quisiste decir...?" suggestions for spelling mistakes or related electoral inquiries
     */
    fun getSpellingAndSmartSuggestions(rawQuery: String): List<String> {
        val q = rawQuery.lowercase().trim()
        if (q.isBlank()) return emptyList()

        val keywords = q.split(" ", "?", "¿", ",", ".", "-", "_").filter { it.length >= 3 }
        val matchedSuggestions = mutableListOf<String>()

        // 1. Direct keyword match
        for (sentence in electoralKnowledgeBank) {
            val sLower = sentence.lowercase()
            var matchScore = 0
            for (kw in keywords) {
                if (sLower.contains(kw)) {
                    matchScore += 2
                } else if (isFuzzyMatch(kw, sLower)) {
                    matchScore += 1
                }
            }
            if (matchScore > 0) {
                matchedSuggestions.add(sentence)
            }
            if (matchedSuggestions.size >= 3) break
        }

        // 2. Specific typo and intent corrections
        if (matchedSuggestions.isEmpty()) {
            if (q.contains("presi") || q.contains("precidente") || q.contains("presidete") || q.contains("jefe de mesa")) {
                matchedSuggestions.add("¿Cuáles son las funciones del Presidente de JRV?")
            }
            if (q.contains("secre") || q.contains("cecretario") || q.contains("secretaro") || q.contains("actas")) {
                matchedSuggestions.add("¿Cuáles son las funciones del Secretario de JRV?")
            }
            if (q.contains("vocal") || q.contains("bocal") || q.contains("vocales") || q.contains("tinta")) {
                matchedSuggestions.add("¿Cuáles son las funciones del Primer Vocal de JRV?")
                matchedSuggestions.add("¿Cuáles son las funciones del Segundo y Tercer Vocal?")
            }
            if (q.contains("tse") || q.contains("tribunal") || q.contains("magistrado") || q.contains("jem") || q.contains("jed")) {
                matchedSuggestions.add("¿Cuáles son las atribuciones del Tribunal Supremo Electoral (TSE)?")
            }
            if (q.contains("dui") || q.contains("cedula") || q.contains("padron") || q.contains("padrón") || q.contains("identidad")) {
                matchedSuggestions.add("¿Qué hacer si un ciudadano no presenta su DUI o no aparece en el padrón?")
            }
            if (q.contains("nulo") || q.contains("valido") || q.contains("válido") || q.contains("impugnado") || q.contains("marca") || q.contains("corazon") || q.contains("cruz")) {
                matchedSuggestions.add("¿Cuál es la diferencia entre voto válido, voto nulo y voto impugnado?")
            }
            if (q.contains("escrutinio") || q.contains("conteo") || q.contains("cierre") || q.contains("5:00") || q.contains("urna")) {
                matchedSuggestions.add("¿Cómo se realiza el escrutinio preliminar paso a paso a partir de las 5:00 PM?")
            }
            if (q.contains("arma") || q.contains("pistola") || q.contains("pnc") || q.contains("policia") || q.contains("seguridad")) {
                matchedSuggestions.add("¿Quiénes están autorizados a portar armas y cómo aplica el Art. 290 del Código Electoral?")
            }
            if (q.contains("permiso") || q.contains("sueldo") || q.contains("trabajo") || q.contains("empleo") || q.contains("patron") || q.contains("113")) {
                matchedSuggestions.add("¿Qué permisos de trabajo con goce de sueldo garantiza el Art. 113 del Código Electoral?")
            }
            if (q.contains("vigilante") || q.contains("partido") || q.contains("fiscal")) {
                matchedSuggestions.add("¿Qué derechos y prohibiciones tienen los vigilantes de partidos políticos?")
            }
        }

        return matchedSuggestions.distinct().take(3)
    }

    private fun isFuzzyMatch(shortTerm: String, fullText: String): Boolean {
        if (shortTerm.length < 4) return false
        val prefix = shortTerm.substring(0, 3)
        return fullText.contains(prefix)
    }

    /**
     * Build knowledge context from the built-in PDF Electoral Library for Gemini RAG grounding
     */
    private fun buildLibraryContextForRAG(): String {
        val sb = StringBuilder()
        sb.append("DOCUMENTOS Y LEYES OFICIALES DE LA BIBLIOTECA ELECTORAL DE EL SALVADOR:\n\n")
        ElectoralLibraryData.documents.forEach { doc ->
            sb.append("--- DOCUMENTO: ${doc.title} (${doc.category}) ---\n")
            sb.append("Autoridad Emisora: ${doc.authority} | ${doc.releaseDate}\n")
            sb.append("Resumen: ${doc.summary}\n")
            doc.pages.forEach { page ->
                sb.append("PÁGINA ${page.pageNumber}: ${page.headerTitle} - ${page.subtitle}\n")
                page.sections.forEach { sec ->
                    sb.append("• [${sec.articleRef ?: sec.title}] ${sec.title}: ${sec.content}\n")
                }
            }
            sb.append("\n")
        }
        return sb.toString()
    }

    /**
     * Comprehensive Local Grounded Electoral Knowledge Engine (Instant, Accurate & Offline)
     */
    private fun resolveLocalElectoralKnowledge(query: String): String {
        val q = query.lowercase().trim()

        // 1. PRESIDENTE DE JRV
        if (q.contains("presidente") || q.contains("precidente") || q.contains("presidete") || (q.contains("jefe") && q.contains("mesa"))) {
            return """
                **INFORMACIÓN OFICIAL:**
                • **Código Electoral de El Salvador**: Arts. 100, 101, 102, 118, 190, 195, 200 al 210.
                • **Manual Oficial de Instrucciones para JRV (TSE)**: Pág. 1 y 2.

                **ORIENTACIÓN GENERAL:**
                El **Presidente de la Junta Receptora de Votos (JRV)** es la máxima autoridad electoral en la mesa durante la jornada. Dirige el funcionamiento de la JRV, mantiene el orden, custodia las urnas y paquetes electorales, y conduce el escrutinio preliminar.

                **FUNCIONES Y ATRIBUCIONES CLAVE:**
                1. **Instalación (6:00 AM)**: Convoca a los miembros, verifica credenciales y DUIs, y revisa el paquete electoral entregado por la DOE.
                2. **Apertura (7:00 AM)**: Muestra la urna vacía a los miembros y vigilantes, la sella y declara abierta la votación.
                3. **Firma y Sello de Papeletas**: Firma y estampa el sello oficial de la JRV al reverso de cada papeleta antes de entregarla al votante (garantía de autenticidad).
                4. **Dirección del Orden**: Resuelve consultas de los electores, expulsa a personas que alteren el orden y tiene potestad legal para ordenar el auxilio inmediato de la PNC (Art. 290).
                5. **Escrutinio (5:00 PM)**: Declara cerrada la votación, extrae papeleta por papeleta de la urna, las desdobla, califica cada voto en voz alta (válido, nulo, impugnado, abstención) y firma el Acta Oficial de Escrutinio.

                **AUTORIDAD COMPETENTE:**
                Junta Receptora de Votos (JRV) y Tribunal Supremo Electoral (TSE).

                **ADVERTENCIA ORIENTATIVA:**
                Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente.
            """.trimIndent()
        }

        // 2. SECRETARIO DE JRV
        if (q.contains("secretario") || q.contains("cecretario") || q.contains("secretaria") || (q.contains("padron") && q.contains("llenar"))) {
            return """
                **INFORMACIÓN OFICIAL:**
                • **Código Electoral de El Salvador**: Arts. 100, 103, 195, 201, 208 y 209.
                • **Manual Oficial de Instrucciones para JRV (TSE)**: Pág. 1 y 2.

                **ORIENTACIÓN GENERAL:**
                El **Secretario de la JRV** es el responsable del registro administrativo, control del padrón electoral y redacción de todas las actas oficiales de la mesa.

                **FUNCIONES Y ATRIBUCIONES CLAVE:**
                1. **Revisión del Elector en el Padrón**: Recibe el DUI del ciudadano, verifica su número y fotografía en el Padrón Electoral de la mesa, y confirma que esté habilitado para votar.
                2. **Registro del Voto**: Una vez que el ciudadano deposita su papeleta en la urna, el Secretario estampa el sello "VOTÓ" y anota la firma o huella del elector en el padrón.
                3. **Levantamiento de Actas Oficiales**: Redacta y suscribe el Acta de Instalación (6:00 AM), el Acta de Cierre (5:00 PM) y el Acta de Escrutinio Preliminar.
                4. **Anotación de Incidentes e Impugnaciones**: Registra formalmente en el folio de observaciones cualquier impugnación de voto formulada por los vigilantes o cualquier incidente ocurrido en la mesa.
                5. **Copias y Certificaciones**: Elabora y entrega las copias certificadas del acta de escrutinio a los vigilantes de partidos debidamente acreditados.

                **AUTORIDAD COMPETENTE:**
                Junta Receptora de Votos (JRV) y Tribunal Supremo Electoral (TSE).

                **ADVERTENCIA ORIENTATIVA:**
                Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente.
            """.trimIndent()
        }

        // 3. PRIMER VOCAL DE JRV
        if (q.contains("primer vocal") || q.contains("1er vocal") || q.contains("1 vocal") || (q.contains("vocal") && q.contains("papeleta"))) {
            return """
                **INFORMACIÓN OFICIAL:**
                • **Código Electoral de El Salvador**: Arts. 100, 104 y 195.
                • **Manual Oficial de Instrucciones para JRV (TSE)**: Pág. 1 y 3.

                **ORIENTACIÓN GENERAL:**
                El **Primer Vocal de la JRV** es el encargado de la inspección física de los electores y del control en la entrega de las papeletas de votación.

                **FUNCIONES Y ATRIBUCIONES CLAVE:**
                1. **Inspección de Manos y Dedos**: Examina cuidadosamente las manos y dedos del votante antes de entregarle la papeleta para verificar que NO tenga mancha de tinta indeleble (evitando intentos de doble votación).
                2. **Entrega de Papeletas de Votación**: Recibe la papeleta debidamente firmada y sellada por el Presidente y Secretario, la dobla correctamente y se la entrega al elector.
                3. **Orientación al Elector**: Guía al votante hacia el atril de votación secreto, recordándole que su voto es libre y confidencial.
                4. **Asistencia en el Escrutinio**: Durante el conteo de votos a las 5:00 PM, asiste en la revisión de papeletas y registro correlativo de los resultados.
                5. **Sustitución Legal**: En caso de ausencia justificada o temporal del Presidente o Secretario, el Primer Vocal asume las funciones correspondientes según el orden legal.

                **AUTORIDAD COMPETENTE:**
                Junta Receptora de Votos (JRV) y Tribunal Supremo Electoral (TSE).

                **ADVERTENCIA ORIENTATIVA:**
                Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente.
            """.trimIndent()
        }

        // 4. SEGUNDO Y TERCER VOCAL DE JRV / TINTA INDELEBLE
        if (q.contains("segundo vocal") || q.contains("tercer vocal") || q.contains("2do vocal") || q.contains("3er vocal") || q.contains("tinta indeleble") || q.contains("entintar") || q.contains("liquido indeleble")) {
            return """
                **INFORMACIÓN OFICIAL:**
                • **Código Electoral de El Salvador**: Arts. 100, 104, 195 y 196.
                • **Manual Oficial de Instrucciones para JRV (TSE)**: Pág. 1 y 3.

                **ORIENTACIÓN GENERAL:**
                El **Segundo y Tercer Vocal de la JRV** son los encargados de la aplicación de la tinta indeleble, devolución de documentos, custodia de atriles y asistencia técnica en la mesa.

                **FUNCIONES Y ATRIBUCIONES CLAVE:**
                1. **Aplicación de Tinta Indeleble**: Una vez el elector ha depositado su voto en la urna, le aplican el líquido o tinta indeleble en el dedo pulgar (o índice) de la mano derecha hasta la cutícula para garantizar que quede marcada de forma inalterable.
                2. **Devolución del DUI**: Entregan el Documento Único de Identidad (DUI) al ciudadano tras constatar que el Secretario firmó el padrón y que el dedo fue entintado.
                3. **Custodia y Privacidad del Atril**: Verifican permanentemente que el atril o cabina de votación esté libre de propaganda, que cuente con crayón o marcador en buen estado y que nadie interfiera con el secreto del sufragio.
                4. **Apoyo en Escrutinio y Empaque**: Colaboran en el conteo público de votos, ordenamiento de papeletas por tipo de voto y empaque del material electoral en las bolsas de seguridad de la DOE.

                **AUTORIDAD COMPETENTE:**
                Junta Receptora de Votos (JRV) y Tribunal Supremo Electoral (TSE).

                **ADVERTENCIA ORIENTATIVA:**
                Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente.
            """.trimIndent()
        }

        // 5. SUPLENTES DE JRV Y SUSTITUCIÓN DE MIEMBROS AUSENTES
        if (q.contains("suplente") || q.contains("suplentes") || q.contains("ausen") || q.contains("falta miembro") || q.contains("sustitu") || q.contains("incomplet")) {
            return """
                **INFORMACIÓN OFICIAL:**
                • **Código Electoral de El Salvador**: Arts. 100, 105, 118, 191 y 192.
                • **Manual Oficial de Instrucciones para JRV (TSE)**: Pág. 1.

                **ORIENTACIÓN GENERAL:**
                Los miembros suplentes de la JRV son nombrados por el TSE para garantizar el funcionamiento continuo de la mesa electoral ante la ausencia de miembros propietarios.

                **PROCEDIMIENTO DE SUSTITUCIÓN CRONOLÓGICO:**
                1. **06:00 AM a 06:15 AM**: Si un miembro propietario no se presenta a la instalación, el Presidente convoca de inmediato a los suplentes acreditados de la mesa en el orden de prelación para asumir la titularidad.
                2. **06:30 AM**: Si persisten vacantes y no se alcanza el quórum legal mínimo de tres miembros, el Presidente informa a la Junta Electoral Municipal (JEM).
                3. **06:45 AM**: La JEM tiene la facultad legal de juramentar a ciudadanos salvadoreños aptos que se encuentren presentes en la fila o centro de votación para completar el quórum legal.
                4. **Funciones del Suplente**: Al asumir como propietario, tiene plenos derechos de voz y voto, firma de actas y responsabilidad legal de la mesa. Si todos los propietarios están presentes, los suplentes permanecen en el centro como relevos de apoyo.

                **AUTORIDAD COMPETENTE:**
                Junta Receptora de Votos (JRV) y Junta Electoral Municipal (JEM).

                **ADVERTENCIA ORIENTATIVA:**
                Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente.
            """.trimIndent()
        }

        // 6. INSTALACIÓN, QUÓRUM Y HORARIOS DE JRV
        if (q.contains("instal") || q.contains("integr") || q.contains("quórum") || q.contains("quorum") || q.contains("horario") || q.contains("6:00") || q.contains("7:00") || q.contains("hora de apertura") || q.contains("cuantos miembros")) {
            return """
                **INFORMACIÓN OFICIAL:**
                • **Código Electoral de El Salvador**: Arts. 100, 118, 190, 191 y 194.
                • **Manual Oficial de Instrucciones para JRV (TSE)**: Pág. 1.

                **ORIENTACIÓN GENERAL:**
                La Junta Receptora de Votos (JRV) es la máxima autoridad electoral en cada mesa. Se compone de hasta cinco miembros propietarios (Presidente, Secretario, 1er Vocal, 2do Vocal, 3er Vocal) y sus respectivos suplentes. Puede funcionar y tomar acuerdos válidos con un **quórum mínimo legal de 3 miembros**.

                **CRONOLOGÍA DE LA JORNADA ELECTORAL:**
                1. **06:00 AM (Instalación)**: Presentación obligatoria de miembros con DUI y credencial oficial. Revisión de paquetes electorales, urnas, padrón, sellos y tintas. Firma del Acta de Instalación.
                2. **06:15 AM - 06:45 AM (Ajuste de Quórum)**: Llamado a suplentes y, si fuese necesario, juramentación de ciudadanos por la JEM para completar al menos 3 miembros.
                3. **07:00 AM (Apertura)**: Se muestra la urna vacía a los presentes, se sella y el Presidente declara abierta la recepción del voto ciudadano.
                4. **05:00 PM (Cierre de Votación)**: Cierre de la mesa. Solo votan los electores que se encuentren formados en la fila de la JRV antes de las 5:00 PM. Inmediatamente inicia el escrutinio preliminar.

                **AUTORIDADES COMPETENTES:**
                Junta Receptora de Votos (JRV), JEM, JED y TSE.

                **ADVERTENCIA ORIENTATIVA:**
                Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente.
            """.trimIndent()
        }

        // 7. TRIBUNAL SUPREMO ELECTORAL (TSE), JED, JEM Y JVE
        if (q.contains("tse") || q.contains("tribunal supremo") || q.contains("jed") || q.contains("jem") || q.contains("jve") || q.contains("organismo electoral") || q.contains("autoridad electoral")) {
            return """
                **INFORMACIÓN OFICIAL:**
                • **Constitución de la República de El Salvador**: Arts. 208 y 209.
                • **Código Electoral de El Salvador**: Arts. 43 al 64 (TSE), Arts. 65 al 98 (JED y JEM), Arts. 131 al 142 (JVE).

                **ORIENTACIÓN GENERAL:**
                El **Tribunal Supremo Electoral (TSE)** es la máxima autoridad jurisdiccional y administrativa en materia electoral en El Salvador. Es un organismo colegiado e independiente encargado de organizar, dirigir y fiscalizar las elecciones.

                **JERARQUÍA Y ORGANISMOS ELECTORALES:**
                1. **Tribunal Supremo Electoral (TSE)**: Ejerce jurisdicción nacional, emite instructivos y acuerdos vinculantes, administra el Padrón Electoral, realiza el Escrutinio Final y proclama a los electos.
                2. **Juntas Electorales Departamentales (JED)**: Máxima autoridad en cada departamento; juramenta y supervisa a las JEM.
                3. **Juntas Electorales Municipales (JEM)**: Autoridad en cada municipio; supervisa y juramenta a las JRV y atiende contingencias de instalación.
                4. **Juntas Receptoras de Votos (JRV)**: Autoridad colegiada en cada mesa electoral el día de la votación.
                5. **Junta de Vigilancia Electoral (JVE)**: Órgano integrado por representantes de los partidos políticos inscritos para fiscalizar permanentemente los procesos del TSE.

                **AUTORIDAD COMPETENTE:**
                Tribunal Supremo Electoral (TSE).

                **ADVERTENCIA ORIENTATIVA:**
                Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente.
            """.trimIndent()
        }

        // 8. PERMISOS LABORALES CON GOCE DE SUELDO (ART. 113)
        if (q.contains("permiso") || q.contains("113") || q.contains("trabajo") || q.contains("sueldo") || q.contains("laboral") || q.contains("empleador") || q.contains("patron")) {
            return """
                **INFORMACIÓN OFICIAL:**
                • **Código Electoral de El Salvador**: Art. 113 y Art. 118.
                • **Constitución de la República**: Art. 72 inc. 3°.
                • **Biblioteca Electoral**: Código Electoral de El Salvador (Decreto 413).

                **ORIENTACIÓN GENERAL:**
                El Art. 113 del Código Electoral establece expresamente que **todo empleador, sea del sector público o de la empresa privada, está legalmente obligado a conceder permiso con goce de sueldo** a los ciudadanos nombrados por el TSE para integrar organismos electorales.

                **DURACIÓN Y ALCANCE DEL PERMISO CON GOCE DE SUELDO:**
                1. **Jornadas de Capacitación**: Permiso remunerado durante las fechas y horas de capacitación oficial programadas por el TSE.
                2. **Día de la Elección**: Permiso remunerado completo durante todo el día del evento electoral (instalación, votación y escrutinio).
                3. **Día Hábil Posterior a la Elección**: Permiso remunerado con goce de sueldo durante el día hábil inmediato siguiente al evento electoral, destinado legalmente al descanso de los miembros por la jornada nocturna de conteo.

                **SANCIONES POR INCUMPLIMIENTO:**
                El cargo de miembro de JRV es de interés público de obligatorio cumplimiento. Cualquier patrono que descuente salario, imponga sanciones laborales o niegue el permiso incurre en infracción legal sancionada por el Ministerio de Trabajo y la Fiscalía General de la República (FGR).

                **AUTORIDADES COMPETENTES:**
                TSE, Ministerio de Trabajo y Fiscalía General de la República (FGR).

                **ADVERTENCIA ORIENTATIVA:**
                Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente.
            """.trimIndent()
        }

        // 9. VOTO VÁLIDO, NULO, IMPUGNADO, ABSTENCIÓN Y ESCRUTINIO
        if (q.contains("nulo") || q.contains("valido") || q.contains("válido") || q.contains("impugnado") || q.contains("abstencion") || q.contains("abstención") || q.contains("en blanco") || q.contains("marca") || q.contains("corazon") || q.contains("cruz") || q.contains("papeleta rota")) {
            return """
                **INFORMACIÓN OFICIAL:**
                • **Código Electoral de El Salvador**: Arts. 197, 200, 205, 206 y 207.
                • **Guía de Procedimientos para el Escrutinio Preliminar (TSE)**: Pág. 1 y 2.

                **ORIENTACIÓN GENERAL:**
                La calificación de los votos en el escrutinio preliminar debe regirse por el principio de favorabilidad de la voluntad libre del elector:

                **CLASIFICACIÓN LEGAL DE VOTOS:**
                • **Voto Válido (Art. 205)**: Cualquier marca inequívoca (cruz, X, check, corazón, raya) hecha dentro de la bandera de un solo partido político, o sobre las fotografías de candidatos de un mismo partido o coalición formalmente inscrita.
                • **Voto Nulo (Art. 207)**: Papeleta con marcas sobre banderas de dos o más partidos que no integran coalición, papeleta rota o mutilada deliberadamente, escritos con palabras obscenas o insultos, o marcas contradictorias que impidan determinar la intención de voto.
                • **Voto Impugnado (Art. 206)**: Voto cuya validez es formalmente objetada por un vigilante de partido acreditado. No se cuenta en la mesa; se introduce en un sobre sellado especial de "Votos Impugnados" para que sea calificado definitivamente por los Magistrados del TSE en el Escrutinio Final.
                • **Voto en Blanco / Abstención**: Papeleta depositada en la urna sin ninguna marca en banderas ni candidatos.
                • **Papeletas Inutilizadas**: Papeletas que sobraron a las 5:00 PM; se cuentan y se inutilizan (cortándolas o marcándolas) antes de abrir la urna.

                **AUTORIDAD COMPETENTE:**
                Junta Receptora de Votos (JRV) y Tribunal Supremo Electoral (TSE).

                **ADVERTENCIA ORIENTATIVA:**
                Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente.
            """.trimIndent()
        }

        // 10. ESCRUTINIO PRELIMINAR PASO A PASO (5:00 PM)
        if (q.contains("escrutinio") || q.contains("conteo") || q.contains("5:00 pm") || q.contains("5:00") || (q.contains("cierre") && q.contains("mesa"))) {
            return """
                **INFORMACIÓN OFICIAL:**
                • **Código Electoral de El Salvador**: Arts. 197 al 215.
                • **Guía de Procedimientos para el Escrutinio Preliminar (TSE)**: Pág. 1, 2 y 3.

                **ORIENTACIÓN GENERAL:**
                A las **5:00 PM** concluye la votación ciudadana y la JRV procede a instalarse en sesión pública de Escrutinio Preliminar en presencia de los vigilantes de partidos acreditados y observadores.

                **PROCEDIMIENTO PASO A PASO:**
                1. **Paso 1 (Inutilización de Sobrantes)**: Se cuentan las papeletas sobrantes no utilizadas, se anota la cantidad en el borrador y se inutilizan inmediatamente.
                2. **Paso 2 (Cuadre con Padrón)**: El Secretario cuenta las firmas y sellos "VOTÓ" en el padrón electoral para determinar el número exacto de votantes.
                3. **Paso 3 (Apertura de Urna)**: El Presidente abre la urna, extrae las papeletas y cuenta el total de votos depositados (debe coincidir con las firmas del padrón).
                4. **Paso 4 (Calificación Pública Voto por Voto)**: El Presidente desdobla cada papeleta, la muestra a los miembros y vigilantes, y anuncia en voz alta: *Válido (partido/candidato), Nulo, Impugnado o Abstención*.
                5. **Paso 5 (Llenado y Firma del Acta)**: Se llena el Acta Oficial de Escrutinio Preliminar, se firman todos los ejemplares y se entregan copias certificadas a los vigilantes.
                6. **Paso 6 (Transmisión y Empaque)**: Se transmiten los resultados por el sistema del TSE y se sella el paquete electoral para su custodia y entrega a la DOE.

                **AUTORIDAD COMPETENTE:**
                Junta Receptora de Votos (JRV) y Dirección de Organización Electoral (DOE).

                **ADVERTENCIA ORIENTATIVA:**
                Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente.
            """.trimIndent()
        }

        // 11. VIGILANTES DE PARTIDOS POLÍTICOS
        if (q.contains("vigilante") || q.contains("vigilantes") || q.contains("fiscal partidario") || (q.contains("partido") && q.contains("derecho"))) {
            return """
                **INFORMACIÓN OFICIAL:**
                • **Código Electoral de El Salvador**: Arts. 123, 128, 129 y 130.
                • **Instructivo Oficial para Vigilantes de Partidos Políticos (TSE)**: Pág. 1 y 2.

                **ORIENTACIÓN GENERAL:**
                Cada partido político o coalición contendiente tiene derecho a nombrar un vigilante propietario y un suplente ante cada JRV para fiscalizar el proceso electoral y garantizar la pureza del sufragio.

                **DERECHOS Y FACULTADES (ART. 128):**
                • Presenciar la instalación de la mesa (6:00 AM), la verificación del paquete y la apertura de urnas.
                • Observar la identificación del elector y la entrega de papeletas.
                • Formular impugnaciones motivadas de votos durante el escrutinio preliminar.
                • Exigir y recibir copia certificada, firmada y sellada del Acta de Escrutinio Preliminar.
                • Acompañar el traslado y custodia del paquete electoral entregado a la DOE.

                **PROHIBICIONES ESTRICTAS (ART. 129):**
                • **NO manipular físicamente** las papeletas, urnas, padrón ni sellos oficiales (facultad exclusiva de la JRV).
                • **NO realizar propaganda partidaria**, lucir emblemas no autorizados o pedir el voto dentro del centro.
                • **NO obstaculizar ni coaccionar** el voto de ningún ciudadano ni interferir en las deliberaciones de la mesa.

                **AUTORIDADES COMPETENTES:**
                Junta Receptora de Votos (JRV), JVE y TSE.

                **ADVERTENCIA ORIENTATIVA:**
                Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente.
            """.trimIndent()
        }

        // 12. SEGURIDAD, PNC, PROHIBICIÓN DE ARMAS (ART. 290) Y LEY SECA (ART. 284)
        if (q.contains("arma") || q.contains("armas") || q.contains("pnc") || q.contains("policia") || q.contains("policía") || q.contains("ley seca") || q.contains("alcohol") || q.contains("delito") || q.contains("seguridad")) {
            return """
                **INFORMACIÓN OFICIAL:**
                • **Código Electoral de El Salvador**: Arts. 65 lit. e, 252, 284 y 290.
                • **Código Penal de El Salvador**: Arts. 291 al 296 (Delitos contra el Sufragio).
                • **Plan de Seguridad Electoral (PNC / TSE)**: Pág. 1.

                **ORIENTACIÓN GENERAL:**
                La seguridad perimetral, el orden público y el resguardo ininterrumpido de los paquetes electorales están a cargo de la Policía Nacional Civil (PNC) en coordinación con el TSE y la Fiscalía Electoral:

                **DISPOSICIONES DE SEGURIDAD OBLIGATORIAS:**
                1. **Prohibición Absoluta de Armas (Art. 290)**: Queda estrictamente prohibido el ingreso o portación de armas de fuego, armas blancas o artefactos explosivos en los centros de votación. **Únicamente los agentes de la PNC en servicio activo están autorizados.**
                2. **Ley Seca (Art. 284)**: Prohibición absoluta de venta, distribución y consumo de bebidas alcohólicas durante el día anterior a la elección, el día de la votación y el día siguiente.
                3. **Auxilio Policial Inmediato**: El Presidente de JRV o Jefe de Centro puede solicitar la intervención inmediata de la PNC destacada ante cualquier alteración del orden, violencia o intento de sustracción de papeletas.
                4. **Delitos Electorales en Flagrancia**: El fraude, la coacción de electores, la destrucción de papeletas o la alteración de actas conllevan captura inmediata en flagrancia y penas de 4 a 15 años de prisión.

                **AUTORIDADES COMPETENTES:**
                Policía Nacional Civil (PNC), Fiscalía General de la República (FGR) y TSE.

                **ADVERTENCIA ORIENTATIVA:**
                Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente.
            """.trimIndent()
        }

        // 13. LEY LEIV (ART. 55) Y PROTECCIÓN DE LA MUJER
        if (q.contains("mujer") || q.contains("leiv") || q.contains("violencia politica") || q.contains("violencia política") || q.contains("genero") || q.contains("género") || q.contains("acoso")) {
            return """
                **INFORMACIÓN OFICIAL:**
                • **Ley Especial Integral para una Vida Libre de Violencia para las Mujeres (LEIV)**: Arts. 9, 10 y 55.
                • **Constitución de la República de El Salvador**: Art. 3 y Art. 72.
                • **Protocolo de Protección de la Mujer y Prevención de Violencia Política (TSE / PDDH)**: Pág. 1.

                **ORIENTACIÓN GENERAL:**
                El Estado salvadoreño y el TSE garantizan el derecho fundamental de las mujeres a participar plenamente en las elecciones en condiciones de igualdad, dignidad y sin discriminación ni violencia de género.

                **VIOLENCIA POLÍTICA CONTRA LAS MUJERES (ART. 55 LEIV):**
                Constituye delito sancionado penalmente cualquier acción u omisión dirigida a:
                • Menoscabar, intimidar, amenazar o anular las funciones de una mujer como miembro de JRV, JEM, JED o vigilante.
                • Coaccionar o limitar su derecho al sufragio libre, secreto y directo.
                • Difamar, agredir o denigrar a candidatas o funcionarias electorales en razón de su género.

                **PROTOCOLO DE ATENCIÓN ANTE INCIDENTES:**
                1. La integrante de mesa o electora notifica de inmediato al Presidente de JRV o al Jefe de Centro.
                2. La PNC actúa de inmediato neutralizando la agresión y ejecutando la captura en flagrancia.
                3. La Unidad Fiscal Especializada de la Mujer (FGR) y la PDDH inician las diligencias penales correspondientes sin admitir conciliación.

                **AUTORIDADES COMPETENTES:**
                Fiscalía General de la República (FGR), Policía Nacional Civil (PNC), PDDH e ISDEMU.

                **ADVERTENCIA ORIENTATIVA:**
                Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente.
            """.trimIndent()
        }

        // 14. DUI, PADRÓN ELECTORAL Y SITUACIONES ESPECIALES
        if (q.contains("dui") || q.contains("cedula") || q.contains("padrón") || q.contains("padron") || q.contains("no esta en la lista") || q.contains("no aparece") || q.contains("vencido") || q.contains("voto asistido") || q.contains("discapacidad") || q.contains("adulto mayor")) {
            return """
                **INFORMACIÓN OFICIAL:**
                • **Código Electoral de El Salvador**: Arts. 195, 198, 199 y 200.
                • **Directrices de Accesibilidad y Voto Asistido del TSE**: Pág. 1.

                **ORIENTACIÓN GENERAL:**
                La emisión del sufragio es personal, directa y secreta, sujeta a la comprobación fehaciente de la identidad ciudadana:

                **REGLAS Y SITUACIONES FRECUENTES:**
                1. **Presentación de DUI**: El ciudadano debe presentar su Documento Único de Identidad (DUI) original en mano. No se admiten fotocopias, licencias de conducir ni pasaportes para votar en territorio nacional.
                2. **Verificación en el Padrón**: El Secretario verifica que el número de DUI y nombre figuren en el Padrón de la JRV correspondiente. Si el ciudadano no aparece registrado en el padrón de esa mesa, **no puede votar en esa JRV** y se le orienta al módulo de consulta del TSE.
                3. **Voto Asistido e Inclusivo (Art. 199)**: Las personas adultas mayores, personas no videntes o con discapacidad motriz que lo requieran pueden ingresar acompañadas de una persona de su estricta confianza o ser asistidas por el Presidente de JRV en el atril. Se garantiza paso preferente sin hacer fila.
                4. **PNC en Servicio (Art. 198)**: Los agentes de la PNC destacados en seguridad del centro de votación pueden emitir el sufragio en la JRV asignada conforme a la nómina especial autorizada por el TSE.

                **AUTORIDADES COMPETENTES:**
                Junta Receptora de Votos (JRV) y Tribunal Supremo Electoral (TSE).

                **ADVERTENCIA ORIENTATIVA:**
                Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente.
            """.trimIndent()
        }

        // 15. JEFES DE CENTRO Y SUPERVISORES DEL TSE
        if (q.contains("jefe de centro") || q.contains("supervisor") || q.contains("supervisores") || q.contains("centro de votacion") || q.contains("centro de votación")) {
            return """
                **INFORMACIÓN OFICIAL:**
                • **Código Electoral de El Salvador**: Arts. 99, 100, 118 y 190.
                • **Instructivo de Logística y Organización Electoral (TSE)**: Pág. 1 y 2.

                **ORIENTACIÓN GENERAL:**
                El **Jefe de Centro de Votación** y los **Supervisores del TSE** son los delegados de la Dirección de Organización Electoral (DOE) responsables de la logística, infraestructura y apertura general del recinto electoral (escuelas, institutos, polideportivos).

                **RESPONSABILIDADES PRINCIPALES:**
                1. **Apertura de Portones (5:00 AM - 5:30 AM)**: Coordinan la apertura del recinto para el ingreso ordenado de miembros de JRV, delegados de JEM/JED, vigilantes acreditados y agentes de la PNC.
                2. **Entrega de Paquetes Electorales**: Distribuyen los paquetes de material electoral sellados a los Presidentes de cada JRV debidamente acreditados.
                3. **Infraestructura y Servicios**: Aseguran el suministro de energía eléctrica, iluminación, conectividad para el equipo de transmisión y señalización de mesas.
                4. **Resolución de Controversias Logísticas**: Canalizan solicitudes urgentes de material adicional ante la DOE y coordinan el ingreso de observadores electorales y prensa acreditada.

                **AUTORIDADES COMPETENTES:**
                Tribunal Supremo Electoral (TSE) y Dirección de Organización Electoral (DOE).

                **ADVERTENCIA ORIENTATIVA:**
                Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente.
            """.trimIndent()
        }

        // 16. Fallback orientativo general con cortesía
        return """
            **INFORMACIÓN OFICIAL:**
            • **Código Electoral de El Salvador (Decreto No. 413)**
            • **Tribunal Supremo Electoral (TSE) y Manuales Oficiales de JRV**

            **ORIENTACIÓN GENERAL:**
            Como Asistente Virtual Electoral de El Salvador, estoy especializado en la normativa jurídica, protocolos y funciones de los organismos electorales salvadoreños.

            **TEMAS DE CONSULTA DISPONIBLES:**
            1. **Miembros de JRV**: Funciones del Presidente, Secretario, Primer Vocal, Segundo/Tercer Vocal y Suplentes.
            2. **Horarios y Quórum**: Instalación a las 6:00 AM, apertura a las 7:00 AM, sustituciones y cierre a las 5:00 PM.
            3. **Permisos Laborales (Art. 113)**: Permiso remunerado con goce de sueldo para capacitación, día de elección y día posterior de descanso.
            4. **Calificación de Votos y Escrutinio**: Votos válidos, nulos, impugnados, papeletas sobrantes y llenado de actas.
            5. **Seguridad y Normativa**: Prohibición de armas (Art. 290), Ley Seca (Art. 284), apoyo de la PNC y protección LEIV (Art. 55).

            **ADVERTENCIA ORIENTATIVA:**
            Esta respuesta es únicamente orientativa. Para una decisión oficial vinculante, consulte la normativa vigente y a la autoridad electoral correspondiente.
        """.trimIndent()
    }

    suspend fun askAssistant(question: String): AnswerResult = withContext(Dispatchers.IO) {
        val trimmedQuestion = question.trim()
        val suggestions = getSpellingAndSmartSuggestions(trimmedQuestion)

        val apiKey = BuildConfig.GEMINI_API_KEY
        val hasValidKey = apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY"

        if (hasValidKey) {
            val libraryGroundingContext = buildLibraryContextForRAG()

            val systemInstructionText = """
                Eres el "Asistente Virtual Electoral", una avanzada herramienta de apoyo e inteligencia artificial fundamentada estrictamente en la legislación electoral de la República de El Salvador.

                ÁREAS DE ESPECIALIZACIÓN Y CONOCIMIENTO AUTORITATIVO:
                1. Procesos electorales, elecciones presidenciales, legislativas, municipales y PARLACEN en El Salvador.
                2. Juntas Receptoras de Votos (JRV): Integración, instalación obligatoria a las 6:00 AM, apertura de votación a las 7:00 AM, cierre a las 5:00 PM, quórum legal mínimo de 3 miembros, sustitución escalonada de miembros ausentes (6:15 AM suplentes, 6:30 AM llamada a JEM, 6:45 AM juramentación de ciudadanos en fila).
                3. Cargos y funciones específicas de JRV:
                   - Presidente: Dirige la mesa, custodia la urna, firma y sella papeletas al reverso, mantiene el orden, conduce el escrutinio preliminar en voz alta y solicita auxilio de la PNC.
                   - Secretario: Revisa y busca al elector en el padrón, marca 'VOTÓ', redacta y resguarda las actas oficiales y anota impugnaciones.
                   - Primer Vocal: Inspecciona dedos para descartar tinta previa, dobla y entrega papeletas firmadas/selladas, orienta hacia el atril de votación.
                   - Segundo y Tercer Vocal: Aplican la tinta indeleble en el dedo pulgar/índice hasta la cutícula, devuelven el DUI, custodian la privacidad del atril y asisten en el conteo.
                   - Suplentes: Incorporación de suplentes ante vacantes con plenos derechos de voz y voto.
                4. Tribunal Supremo Electoral (TSE): Máxima autoridad jurisdiccional y administrativa (Arts. 208-209 Constitución), organismos temporales (JED Departamentales, JEM Municipales, JRV) y Junta de Vigilancia Electoral (JVE).
                5. Derechos laborales (Art. 113 Código Electoral): Obligación de patronos de conceder permiso con goce de sueldo durante capacitaciones, el día completo de la elección y el día hábil posterior de descanso obligatorio.
                6. Calificación de votos:
                   - Voto Válido (Art. 205): Marcación inequívoca dentro de una sola bandera o candidatos del mismo partido/coalición.
                   - Voto Nulo (Art. 207): Marcas en 2 o más partidos no coaligados, frases obscenas, boleta rota, firmas.
                   - Voto Impugnado (Art. 206): Objeción formal de vigilante que se reserva en sobre sellado para resolución del TSE en escrutinio definitivo.
                   - Voto en Blanco / Abstención: Papeleta sin marcas.
                   - Papeletas inutilizadas: Sobrantes al cerrar a las 5:00 PM cortadas antes de abrir la urna.
                7. Escrutinio preliminar paso a paso (5:00 PM): Inutilización de sobrantes, cotejo de firmas en padrón, apertura de urna, conteo voz a voz, llenado y firma de acta, entrega de copias certificadas a vigilantes.
                8. Vigilantes partidarios: Derechos (fiscalizar, impugnar, recibir actas), prohibiciones (NO tocar papeletas ni urnas, NO hacer propaganda en el centro).
                9. Seguridad y Policía Nacional Civil (PNC): Seguridad perimetral a 50m, auxilio inmediato al Presidente de JRV o Jefe de Centro, custodia de paquetes electorales, captura en flagrancia de delitos electorales.
                10. Normas restrictivas: Prohibición absoluta de armas (Art. 290, solo PNC autorizada) y Ley Seca (Art. 284).
                11. Protección a la mujer: LEIV (Arts. 9, 10, 55), sanción penal de violencia política contra la mujer.
                12. Inclusividad y voto asistido: Personas con discapacidad y adultos mayores acompañados de una persona de confianza.

                DIRECTRICES DE RESPUESTA:
                - Sé exhaustivo, claro, pedagógico, preciso y respetuoso.
                - No importa si la respuesta es extensa o detallada; prioriza la máxima exactitud jurídica y práctica.
                - Cita siempre los artículos correspondientes del Código Electoral (Decreto No. 413), la Constitución y manuales del TSE.
                - Si el usuario pregunta algo general, responde cortésmente y relaciona con el marco legal electoral.

                ESTRUCTURA DE CADA RESPUESTA:
                1. **INFORMACIÓN OFICIAL:** Cita explícita de artículos del Código Electoral, leyes aplicables y manuales del TSE.
                2. **ORIENTACIÓN GENERAL:** Explicación conceptual clara, completa y fundamentada.
                3. **PROCEDIMIENTO PASO A PASO / ATRIBUCIONES:** Desglose práctico, cronológico y operativo según corresponda.
                4. **AUTORIDAD COMPETENTE:** Organismos y entidades responsables (JRV, JEM, JED, TSE, PNC, FGR).
                5. **ADVERTENCIA ORIENTATIVA:** Concluye con:
                "Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente."

                BASE DOCUMENTAL DE RESPALDO:
                $libraryGroundingContext
            """.trimIndent()

            val request = GenerateContentRequest(
                contents = listOf(
                    Content(parts = listOf(Part(text = trimmedQuestion)))
                ),
                generationConfig = GenerationConfig(
                    temperature = 0.15f,
                    topP = 0.95f
                ),
                systemInstruction = Content(
                    parts = listOf(Part(text = systemInstructionText))
                )
            )

            try {
                val response = apiService.generateContent(apiKey, request)
                val textRes = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!textRes.isNullOrBlank()) {
                    val historyItem = QueryHistory(
                        question = trimmedQuestion,
                        answer = textRes
                    )
                    queryHistoryDao.insertHistory(historyItem)
                    return@withContext AnswerResult.Success(textRes, suggestions)
                }
            } catch (e: Exception) {
                // Fallback to grounded local knowledge engine if network fails or timeout occurs
                val fallbackAnswer = resolveLocalElectoralKnowledge(trimmedQuestion)
                val historyItem = QueryHistory(
                    question = trimmedQuestion,
                    answer = fallbackAnswer
                )
                queryHistoryDao.insertHistory(historyItem)
                return@withContext AnswerResult.Success(fallbackAnswer, suggestions)
            }
        }

        // If no API key is configured or during offline execution, use the comprehensive grounded knowledge engine
        val localAnswer = resolveLocalElectoralKnowledge(trimmedQuestion)
        val historyItem = QueryHistory(
            question = trimmedQuestion,
            answer = localAnswer
        )
        queryHistoryDao.insertHistory(historyItem)
        return@withContext AnswerResult.Success(localAnswer, suggestions)
    }
}

sealed class AnswerResult {
    data class Success(val text: String, val suggestions: List<String> = emptyList()) : AnswerResult()
    data class Error(val errorMessage: String) : AnswerResult()
}

enum class ApiKeyStatus {
    AVAILABLE, MISSING
}
