package com.example.data.search.knowledge

import android.content.Context
import com.example.data.search.model.ElectoralSituation
import com.example.data.search.model.StructuredElectoralResponse

object ElectoralKnowledgeBase {

    val SITUATIONS: List<ElectoralSituation> = listOf(
        // ==========================================
        // 1. SEGURIDAD Y ARMAS DE FUEGO
        // ==========================================
        ElectoralSituation(
            id = "ARM-001",
            categoria = "seguridad",
            subcategoria = "armas",
            titulo = "Vigilante ingresa o porta arma de fuego en el centro de votación",
            keywords = listOf("arma", "pistola", "revolver", "vigilante", "fiscal", "seguridad", "ingresar", "portar", "centro de votacion"),
            sinonimos = listOf("arma de fuego", "escopeta", "fusil", "vigilante partidario", "representante"),
            actores = listOf("vigilante"),
            acciones = listOf("ingresar", "portar"),
            objetos = listOf("arma_de_fuego"),
            lugares = listOf("centro_de_votacion", "mesa_jrv"),
            momentos = listOf("votacion", "instalacion", "escrutinio"),
            intenciones = listOf("procedimiento", "prohibicion_sancion", "seguridad"),
            contexto = "seguridad_electoral",
            respuesta = StructuredElectoralResponse(
                situacion = "Un vigilante de partido político ingresa o es detectado portando un arma de fuego o artefacto letal en las instalaciones del centro de votación o mesa electoral.",
                fundamento = listOf(
                    "Art. 290 del Código Electoral: Prohibición absoluta de portar armas de fuego en centros de votación, excepto miembros de la PNC en servicio.",
                    "Art. 125 literal c) del Código Electoral: Prohibición expresa a los vigilantes de portar armas.",
                    "Instructivo para Juntas Receptoras de Votos (TSE) - Normas de Seguridad."
                ),
                procedimiento = listOf(
                    "1. La Presidencia de la JRV o el Jefe de Centro debe ordenar inmediatamente al vigilante que cese la portación y desaloje el arma del recinto.",
                    "2. Solicitar de inmediato el auxilio de los agentes de la Policía Nacional Civil (PNC) asignados a la seguridad del centro de votación.",
                    "3. La PNC procederá al decomiso del arma y/o retención conforme a la ley penal y electoral.",
                    "4. El Secretario de la JRV debe asentar detalladamente el incidente en el apartado de observaciones del Acta Oficial, consignando nombre del vigilante, partido político y número de credencial.",
                    "5. El vigilante queda revocado de su función en la mesa y se le prohíbe el reingreso."
                ),
                noHacer = listOf(
                    "NO permita bajo ninguna circunstancia que el vigilante permanezca armado en la mesa electoral.",
                    "NO confronte físicamente al individuo armado; delegue la neutralización y control estrictamente en la PNC.",
                    "NO omita registrar el hecho en el Acta Oficial de la JRV."
                ),
                fuentes = listOf("Código Electoral (Decreto 413, Art. 125, 290)", "Instructivo Oficial JRV TSE"),
                advertencia = "La portación ilegal de armas en centros de votación constituye delito electoral y delito común flagrante."
            ),
            respuestasRelacionadas = listOf("ARM-010", "ARM-011", "ARM-002", "VIG-002")
        ),

        ElectoralSituation(
            id = "ARM-002",
            categoria = "seguridad",
            subcategoria = "amenazas",
            titulo = "Votante amenaza con arma a miembros de JRV / al recibir papeleta",
            keywords = listOf("arma", "pistola", "revolver", "amenaza", "apunto", "apuntando", "votante", "elector", "papeleta", "intimidar"),
            sinonimos = listOf("arma de fuego", "ciudadano", "boleta", "agredir", "coaccionar"),
            actores = listOf("votante", "miembro_jrv"),
            acciones = listOf("amenaza", "portar", "entregar"),
            objetos = listOf("arma_de_fuego", "papeleta"),
            lugares = listOf("mesa_jrv", "centro_de_votacion"),
            momentos = listOf("votacion"),
            intenciones = listOf("procedimiento_emergencia", "procedimiento", "seguridad"),
            contexto = "seguridad_electoral",
            respuesta = StructuredElectoralResponse(
                situacion = "Un votante o ciudadano exhibe un arma de fuego o amenaza a los integrantes de la JRV durante el proceso de votación o al momento de entregarle las papeletas.",
                fundamento = listOf(
                    "Art. 290 del Código Electoral: Prohibición absoluta de armas en centros de sufragio.",
                    "Art. 395 y concordantes del Código Penal: Delitos de amenazas y coacción electoral.",
                    "Manual de Seguridad y Contingencias del TSE."
                ),
                procedimiento = listOf(
                    "1. Mantener la calma y priorizar la integridad física de los miembros de mesa y de los votantes presentes.",
                    "2. No entregar papeletas adicionales ni forcejear con la persona armada.",
                    "3. Dar aviso inmediato a la Policía Nacional Civil (PNC) destacada en el centro de votación para la detención inmediata del sospechoso en flagrancia.",
                    "4. Suspender provisionalmente la recepción de votos en la mesa hasta restablecer el perímetro seguro.",
                    "5. Registrar el hecho con hora exacta, identidad del agresor (si consta por su DUI) y nombres de testigos en el Acta Oficial.",
                    "6. Informar de inmediato al Delegado de la Fiscalía General de la República (FGR) presente en el centro."
                ),
                noHacer = listOf(
                    "NO intente desarmar a la persona por su cuenta.",
                    "NO entregue material electoral sin control ni permita que se destruyan las urnas o actas.",
                    "NO continúe la votación en situación de riesgo activo."
                ),
                fuentes = listOf("Código Electoral Art. 290", "Código Penal Art. 395", "Instructivo JRV"),
                advertencia = "Amenazar a una autoridad electoral con arma es un delito grave con detención inmediata e intervención de FGR."
            ),
            respuestasRelacionadas = listOf("ARM-001", "ARM-012", "AME-001")
        ),

        ElectoralSituation(
            id = "ARM-010",
            categoria = "seguridad",
            subcategoria = "armas",
            titulo = "Quiénes están autorizados a portar armas en centros de votación",
            keywords = listOf("quien", "quienes", "pueden", "portar", "armas", "autorizados", "pnc", "policia", "seguridad", "290"),
            sinonimos = listOf("arma de fuego", "pistola", "fuerza armada", "reglamento armas"),
            actores = listOf("pnc"),
            acciones = listOf("portar"),
            objetos = listOf("arma_de_fuego"),
            lugares = listOf("centro_de_votacion"),
            momentos = listOf("votacion", "instalacion", "escrutinio"),
            intenciones = listOf("competencia_quien_puede", "consulta_legal_normativa"),
            contexto = "seguridad_electoral",
            respuesta = StructuredElectoralResponse(
                situacion = "Consulta sobre qué personal o autoridades están legalmente facultados para portar armas en los centros de sufragio.",
                fundamento = listOf(
                    "Art. 290 del Código Electoral: 'Se prohíbe la portación de armas en los centros de votación... Únicamente podrán ingresar con sus armas de equipo los miembros de la Policía Nacional Civil que se encuentren de servicio en el centro electoral'.",
                    "Art. 125 literal c) Código Electoral: Prohibición terminante a vigilantes y miembros de mesa."
                ),
                procedimiento = listOf(
                    "1. Exclusividad: Solo los agentes uniformados de la PNC debidamente comisionados para el resguardo del centro pueden portar armas de equipo.",
                    "2. Exclusiones expresas: Ni vigilantes, ni miembros de JRV, ni votantes, ni personal de partidos, ni seguridad privada o escoltas pueden ingresar armados.",
                    "3. En caso de detectarse cualquier otra persona armada, la Presidencia de JRV o Jefatura de Centro debe ordenar a la PNC su retiro o desarme inmediato."
                ),
                noHacer = listOf(
                    "NO autorizar el ingreso de personal de seguridad privada, escoltas o militares no asignados con armas al recinto de votación.",
                    "NO permitir excepciones bajo ninguna credencial civil o política."
                ),
                fuentes = listOf("Código Electoral de El Salvador, Art. 290", "Reglamento de Seguridad Electoral TSE")
            ),
            respuestasRelacionadas = listOf("ARM-001", "ARM-011", "ARM-012")
        ),

        ElectoralSituation(
            id = "ARM-011",
            categoria = "seguridad",
            subcategoria = "armas",
            titulo = "Prohibición general de armas y consecuencias legales",
            keywords = listOf("prohibicion", "armas", "prohibido", "delito", "sancion", "codigo electoral", "articulo 290"),
            sinonimos = listOf("pistola", "revolver", "arma blanca", "cuchillo"),
            actores = listOf("votante", "vigilante", "miembro_jrv"),
            acciones = listOf("portar"),
            objetos = listOf("arma_de_fuego"),
            lugares = listOf("centro_de_votacion"),
            intenciones = listOf("prohibicion_sancion", "consulta_legal_normativa"),
            contexto = "seguridad_electoral",
            respuesta = StructuredElectoralResponse(
                situacion = "Alcance y prohibiciones generales sobre portación de armas de fuego y armas blancas en comicios.",
                fundamento = listOf(
                    "Art. 290 Código Electoral: Prohibición general de armas.",
                    "Ley de Control y Regulación de Armas de Fuego, Municiones, Explosivos y Artículos Similares."
                ),
                procedimiento = listOf(
                    "1. Rige veda de armas en el perímetro y recinto interior del centro electoral.",
                    "2. Las autoridades de seguridad pública (PNC) mantienen filtros y control en los accesos.",
                    "3. Cualquier ciudadano que intente ingresar armado debe dejar su arma bajo custodia legal fuera del recinto o abstenerse de ingresar."
                ),
                noHacer = listOf(
                    "NO permitir portación oculta ni visible dentro de las instalaciones electorales."
                ),
                fuentes = listOf("Código Electoral Art. 290")
            ),
            respuestasRelacionadas = listOf("ARM-001", "ARM-010")
        ),

        ElectoralSituation(
            id = "ARM-012",
            categoria = "seguridad",
            subcategoria = "armas",
            titulo = "Procedimiento general ante presencia de persona armada o disturbios",
            keywords = listOf("disturbio", "persona armada", "violencia", "seguridad", "pnc", "desalojo", "suspension"),
            sinonimos = listOf("pistolero", "agresion", "caos", "pelea"),
            actores = listOf("pnc", "presidente", "jefe_centro"),
            acciones = listOf("portar", "amenaza"),
            objetos = listOf("arma_de_fuego"),
            lugares = listOf("centro_de_votacion"),
            intenciones = listOf("procedimiento_emergencia", "seguridad"),
            contexto = "seguridad_electoral",
            respuesta = StructuredElectoralResponse(
                situacion = "Protocolo de emergencia ante presencia de personas armadas o conatos de violencia en el centro de votación.",
                fundamento = listOf(
                    "Art. 290 Código Electoral (Auxilio de la Fuerza Pública).",
                    "Art. 104 y 105 Código Electoral (Facultades de la Presidencia de JRV)."
                ),
                procedimiento = listOf(
                    "1. La Presidencia de la JRV o Jefatura de Centro solicita la intervención inmediata de la PNC.",
                    "2. La PNC desaloja al infractor del centro electoral y procede a la captura si hay delito flagrante.",
                    "3. Se levanta constancia escrita en el acta.",
                    "4. Si la seguridad está en riesgo grave, el Presidente de JRV puede suspender momentáneamente la votación hasta que la PNC restablezca el orden total."
                ),
                noHacer = listOf(
                    "NO suspender definitivamente la elección sin autorización formal de los organismos electorales superiores."
                ),
                fuentes = listOf("Código Electoral Arts. 104, 105, 290")
            ),
            respuestasRelacionadas = listOf("ARM-001", "ARM-002", "AME-001")
        ),

        // ==========================================
        // 2. IDENTIFICACIÓN Y DUI
        // ==========================================
        ElectoralSituation(
            id = "DUI-001",
            categoria = "votacion",
            subcategoria = "dui",
            titulo = "Votante presenta DUI vencido o deteriorado pero legible",
            keywords = listOf("dui", "vencido", "deteriorado", "vigencia", "vencimiento", "votar", "permitido", "aceptar"),
            sinonimos = listOf("documento unico", "cedula", "caducado"),
            actores = listOf("votante", "secretario", "presidente"),
            acciones = listOf("votar", "entregar"),
            objetos = listOf("dui"),
            lugares = listOf("mesa_jrv"),
            momentos = listOf("votacion"),
            intenciones = listOf("procedimiento", "consulta_permiso_prohibicion"),
            contexto = "identificacion_elector",
            respuesta = StructuredElectoralResponse(
                situacion = "Un ciudadano se presenta a votar a la JRV con un DUI que se encuentra vencido en su fecha o con desgaste visible.",
                fundamento = listOf(
                    "Art. 217 del Código Electoral: El único documento para emitir el sufragio en territorio nacional es el Documento Único de Identidad (DUI).",
                    "Decretos Legislativos Transitorios y Disposiciones del TSE sobre vigencia del DUI para fines de votación."
                ),
                procedimiento = listOf(
                    "1. El Primer Vocal y el Secretario verifican que los datos y la fotografía del DUI sean perfectamente legibles.",
                    "2. Verificar que el número de DUI y nombre exacto aparezcan en el Padrón Electoral (Lista de Votantes) de esa JRV específica.",
                    "3. Si los datos coinciden plenamente y el ciudadano está en el padrón, se le permite votar aunque el DUI esté vencido (si aplica decreto transitorio emitido por la Asamblea Legislativa/TSE para la elección).",
                    "4. En caso de deterioro grave que impida leer los datos o identificar el rostro, el Presidente consultará con el Jefe de Centro/Supervisor del TSE."
                ),
                noHacer = listOf(
                    "NO rechazar al elector si está en el padrón y existe decreto legal vigente que habilite votar con DUI vencido.",
                    "NO aceptar documentos distintos al DUI (licencia de conducir, pasaporte o carnets no son válidos para votar en territorio nacional)."
                ),
                fuentes = listOf("Código Electoral Art. 217", "Instructivo JRV TSE")
            ),
            respuestasRelacionadas = listOf("DUI-002", "DUI-003", "DUI-004")
        ),

        ElectoralSituation(
            id = "DUI-002",
            categoria = "votacion",
            subcategoria = "dui",
            titulo = "Foto o apariencia del DUI no coincide con la persona",
            keywords = listOf("dui", "foto", "apariencia", "foto no coincide", "suplantacion", "persona distinta", "rostro", "duda"),
            sinonimos = listOf("documento ajeno", "falso", "trampa", "suplantar"),
            actores = listOf("votante", "presidente", "secretario", "fgr"),
            acciones = listOf("votar", "negar"),
            objetos = listOf("dui", "padron"),
            lugares = listOf("mesa_jrv"),
            momentos = listOf("votacion"),
            intenciones = listOf("procedimiento", "prohibicion_sancion"),
            contexto = "identificacion_elector",
            respuesta = StructuredElectoralResponse(
                situacion = "Un individuo presenta un DUI cuya fotografía, rasgos físicos o firma difieren ostensiblemente de quien se presenta a la mesa.",
                fundamento = listOf(
                    "Art. 217 y 218 del Código Electoral: Obligación estricta de identificación fehaciente.",
                    "Art. 396 Código Penal: Delito de fraude electoral por suplantación de identidad."
                ),
                procedimiento = listOf(
                    "1. La JRV solicita amablemente al ciudadano corroborar datos personales complementarios (fecha de nacimiento, dirección, nombre de padres o firma en papel borrador).",
                    "2. Si la duda persiste o es evidente la suplantación de identidad, la JRV somete el caso a votación por mayoría simple.",
                    "3. Si se resuelve que no es el titular, NO se le entregan papeletas.",
                    "4. Se da aviso inmediato al Delegado de la Fiscalía General de la República (FGR) y a la PNC para investigar la posible suplantación.",
                    "5. Se asienta el incidente en el Acta de Votación."
                ),
                noHacer = listOf(
                    "NO entregar papeletas de votación si existe certeza fundamentada de que el portador no es el titular del DUI.",
                    "NO retener el DUI indebidamente si no hay intervención de la PNC o FGR."
                ),
                fuentes = listOf("Código Electoral Arts. 217, 218", "Código Penal Art. 396")
            ),
            respuestasRelacionadas = listOf("DUI-001", "DUI-003", "DEL-003")
        ),

        ElectoralSituation(
            id = "DUI-003",
            categoria = "votacion",
            subcategoria = "padron",
            titulo = "Ciudadano no aparece en el padrón electoral de la JRV",
            keywords = listOf("no aparece", "padron", "lista", "no esta", "elector", "buscar", "jrv equivocada", "padrón"),
            sinonimos = listOf("listado", "registro", "no figura"),
            actores = listOf("votante", "secretario", "jefe_centro"),
            acciones = listOf("votar", "negar"),
            objetos = listOf("dui", "padron"),
            lugares = listOf("mesa_jrv"),
            momentos = listOf("votacion"),
            intenciones = listOf("procedimiento"),
            contexto = "identificacion_elector",
            respuesta = StructuredElectoralResponse(
                situacion = "Un ciudadano acude con su DUI pero no figura en el padrón impreso de esa Junta Receptora de Votos.",
                fundamento = listOf(
                    "Art. 216 y 218 del Código Electoral: Nadie puede votar en una JRV si no está formalmente inscrito en su respectivo padrón electoral."
                ),
                procedimiento = listOf(
                    "1. El Secretario revisa minuciosamente el Padrón completo por número de DUI y por orden alfabético de apellidos.",
                    "2. Si no aparece, se le informa al ciudadano que no puede votar en esa JRV en particular.",
                    "3. Orientar al ciudadano para que consulte su JRV asignada en el Centro de Información del TSE o con los Facilitadores Electorales del centro.",
                    "4. Bajo ninguna circunstancia se puede agregar a mano a un ciudadano en el padrón de la mesa."
                ),
                noHacer = listOf(
                    "NO agregar nombres manuscritos en el Padrón Electoral.",
                    "NO entregar papeletas a personas que no figuren en la lista oficial de esa JRV."
                ),
                fuentes = listOf("Código Electoral Art. 216, 218", "Instructivo JRV TSE")
            ),
            respuestasRelacionadas = listOf("DUI-001", "DUI-004")
        ),

        ElectoralSituation(
            id = "DUI-004",
            categoria = "votacion",
            subcategoria = "tinta",
            titulo = "Ciudadano ya tiene el dedo manchado o aparece como 'VOTÓ' en el padrón",
            keywords = listOf("dedo manchado", "tinta", "ya voto", "doble voto", "mancha", "segunda vez", "padron marcado"),
            sinonimos = listOf("tinta indeleble", "fraude", "voto doble"),
            actores = listOf("votante", "presidente", "vocal", "pnc"),
            acciones = listOf("votar", "negar"),
            objetos = listOf("tinta", "padron", "dui"),
            lugares = listOf("mesa_jrv"),
            momentos = listOf("votacion"),
            intenciones = listOf("procedimiento", "prohibicion_sancion"),
            contexto = "identificacion_elector",
            respuesta = StructuredElectoralResponse(
                situacion = "Un ciudadano solicita votar pero al ser examinado por el Vocal presenta restos de tinta indeleble en sus dedos o en el padrón ya figura tachado/marcado.",
                fundamento = listOf(
                    "Art. 222 del Código Electoral: El voto es único y personal.",
                    "Art. 396 Código Penal: Delito de votar más de una vez (Fraude Electoral)."
                ),
                procedimiento = listOf(
                    "1. El Primer Vocal revisa minuciosamente las uñas y dedos de ambas manos.",
                    "2. Si tiene mancha de tinta indeleble o ya está tachado en el padrón, la Presidencia le niega la entrega de papeletas.",
                    "3. Si el votante insiste o intenta forzar el sufragio, se llama a la PNC y al delegado de la FGR por intento de fraude electoral.",
                    "4. Registrar la incidencia en el Acta Oficial."
                ),
                noHacer = listOf(
                    "NO permitir el voto doble bajo ninguna justificación.",
                    "NO omitir la revisión minuciosa del dedo antes de entregar la papeleta."
                ),
                fuentes = listOf("Código Electoral Art. 222", "Código Penal Art. 396")
            ),
            respuestasRelacionadas = listOf("DUI-002", "DUI-003", "DEL-003")
        ),

        // ==========================================
        // 3. INSTALACIÓN Y HORARIOS DE JRV
        // ==========================================
        ElectoralSituation(
            id = "INST-001",
            categoria = "instalacion",
            subcategoria = "horario_quorum",
            titulo = "Instalación e integración de la JRV a las 06:00 AM (quórum mínimo)",
            keywords = listOf("instalacion", "instalar", "6 am", "hora", "quorum", "cuantos miembros", "integracion", "mesa"),
            sinonimos = listOf("apertura", "armar mesa", "jrv"),
            actores = listOf("presidente", "secretario", "vocal", "miembro_jrv"),
            acciones = listOf("instalar"),
            objetos = listOf("acta", "urna", "papeleta"),
            lugares = listOf("mesa_jrv"),
            momentos = listOf("instalacion"),
            intenciones = listOf("procedimiento", "funciones_atribuciones"),
            contexto = "instalacion_jrv",
            respuesta = StructuredElectoralResponse(
                situacion = "Procedimiento y quórum legal para instalar e integrar la Junta Receptora de Votos a partir de las 6:00 AM.",
                fundamento = listOf(
                    "Art. 99, 100 y 190 del Código Electoral: Composición, quórum y hora de convocatoria para la instalación."
                ),
                procedimiento = listOf(
                    "1. Convocatoria: Los miembros propietarios y suplentes deben presentarse a las 06:00 AM al centro de votación con su DUI y credencial del TSE.",
                    "2. Quórum de instalación: La JRV se integra con un máximo de 5 miembros (Presidente, Secretario, Primer Vocal, Segundo Vocal, Tercer Vocal). Se instala válidamente con un mínimo de 3 miembros.",
                    "3. Revisión de paquete electoral: Verificar sellos, contar papeletas sin desprenderlas, revisar padrón, tinta, crayones, sellos y armar urnas transparentes.",
                    "4. Llenado del Acta de Instalación: El Secretario llena y todos los integrantes firman el Acta de Instalación antes de las 07:00 AM."
                ),
                noHacer = listOf(
                    "NO abrir la votación al público antes de las 07:00 AM.",
                    "NO iniciar la instalación sin verificar que las urnas se encuentren completamente vacías.",
                    "NO desprender ni marcar papeletas durante la fase de instalación."
                ),
                fuentes = listOf("Código Electoral Arts. 99, 100, 190", "Instructivo JRV TSE")
            ),
            respuestasRelacionadas = listOf("INST-002", "INST-003", "VOT-001")
        ),

        ElectoralSituation(
            id = "INST-002",
            categoria = "instalacion",
            subcategoria = "sustitucion",
            titulo = "Ausencia de miembros de JRV (sustitución por suplentes, vigilantes o fila)",
            keywords = listOf("falta miembro", "no vino", "ausente", "suplente", "sustitucion", "fila", "completar mesa", "sin miembros"),
            sinonimos = listOf("incompleta", "reemplazo", "ausencia"),
            actores = listOf("presidente", "jefe_centro", "vigilante", "miembro_jrv"),
            acciones = listOf("instalar"),
            lugares = listOf("mesa_jrv"),
            momentos = listOf("instalacion"),
            intenciones = listOf("procedimiento"),
            contexto = "instalacion_jrv",
            respuesta = StructuredElectoralResponse(
                situacion = "Qué hacer si a las 6:30 AM o 7:00 AM no se presentan los miembros propietarios suficientes para instalar la JRV.",
                fundamento = listOf(
                    "Art. 191 del Código Electoral: Mecanismo de sustitución e integración de emergencia por ausencia."
                ),
                procedimiento = listOf(
                    "1. A las 06:30 AM, los miembros suplentes presentes asumen la titularidad de los cargos vacantes según el orden de prelación.",
                    "2. Si aún no se alcanza el quórum mínimo (3 miembros), a las 06:45 AM la Jefatura de Centro/Delegados del TSE proceden a incorporar vigilantes acreditados de partidos políticos no representados en la vacante.",
                    "3. Si persiste la falta de quórum a las 07:00 AM, el Jefe de Centro puede designar a ciudadanos de la fila de electores pertenecientes a ese municipio para completar la mesa.",
                    "4. Asentar todas las sustituciones en el Acta de Instalación."
                ),
                noHacer = listOf(
                    "NO dejar la mesa cerrada si hay suplentes o vigilantes disponibles para integrarla.",
                    "NO integrar personas sin credencial válida del TSE o sin su respectivo DUI."
                ),
                fuentes = listOf("Código Electoral Art. 191", "Guía de Contingencias TSE")
            ),
            respuestasRelacionadas = listOf("INST-001", "INST-003")
        ),

        ElectoralSituation(
            id = "INST-003",
            categoria = "instalacion",
            subcategoria = "horarios",
            titulo = "Horario de votación: Apertura a las 07:00 AM y Cierre a las 05:00 PM",
            keywords = listOf("horario", "apertura", "cierre", "7 am", "5 pm", "a que hora abre", "a que hora cierra", "hora de votacion"),
            sinonimos = listOf("inicio votacion", "finalizacion", "tiempo"),
            actores = listOf("presidente", "miembro_jrv", "votante"),
            acciones = listOf("instalar", "votar"),
            lugares = listOf("centro_de_votacion", "mesa_jrv"),
            momentos = listOf("apertura", "cierre"),
            intenciones = listOf("consulta_legal_normativa", "procedimiento"),
            contexto = "proceso_votacion",
            respuesta = StructuredElectoralResponse(
                situacion = "Horarios oficiales regulados por la ley salvadoreña para el inicio y fin de la jornada de votación.",
                fundamento = listOf(
                    "Art. 194 del Código Electoral: La votación inicia a las 07:00 AM.",
                    "Art. 196 del Código Electoral: La votación finaliza a las 05:00 PM en punto."
                ),
                procedimiento = listOf(
                    "1. 07:00 AM: El Presidente declara solemnemente abierta la votación para todos los ciudadanos inscritos.",
                    "2. 05:00 PM: El Presidente declara cerrada la votación. Solo podrán votar los ciudadanos que a las 05:00 PM se encuentren ya en la fila dentro del recinto electoral.",
                    "3. Inmediatamente después de que vote el último de la fila, se cierran las instalaciones y da inicio el escrutinio preliminar."
                ),
                noHacer = listOf(
                    "NO abrir la mesa al público antes de las 07:00 AM.",
                    "NO permitir el ingreso de nuevas personas a la fila después de las 05:00 PM.",
                    "NO cerrar antes de las 05:00 PM, aunque no haya votantes en ese momento."
                ),
                fuentes = listOf("Código Electoral Arts. 194, 196", "Instructivo JRV TSE")
            ),
            respuestasRelacionadas = listOf("INST-001", "ESC-001", "VOT-001")
        ),

        // ==========================================
        // 4. VOTACIÓN Y PAPELETAS
        // ==========================================
        ElectoralSituation(
            id = "VOT-001",
            categoria = "votacion",
            subcategoria = "paso_a_paso",
            titulo = "Procedimiento paso a paso para la emisión del voto en la JRV",
            keywords = listOf("procedimiento", "pasos", "votar", "como se vota", "entrega", "papeleta", "firma", "tinta"),
            sinonimos = listOf("flujo de votacion", "atencion al elector", "sufragio"),
            actores = listOf("votante", "presidente", "secretario", "vocal"),
            acciones = listOf("votar", "entregar"),
            objetos = listOf("dui", "papeleta", "padron", "tinta", "urna"),
            lugares = listOf("mesa_jrv", "cabina_secreta"),
            momentos = listOf("votacion"),
            intenciones = listOf("procedimiento"),
            contexto = "proceso_votacion",
            respuesta = StructuredElectoralResponse(
                situacion = "Flujo secuencial que debe cumplir la Junta Receptora de Votos para atender a cada elector.",
                fundamento = listOf(
                    "Art. 195 a 201 del Código Electoral: Procedimiento del sufragio."
                ),
                procedimiento = listOf(
                    "1. Presentación y verificación: El elector entrega su DUI. El Primer Vocal revisa que los dedos no tengan tinta y el Secretario verifica su presencia en el Padrón.",
                    "2. Entrega de papeleta: El Presidente firma/sella la papeleta en el reverso y se la entrega al elector junto con el crayón.",
                    "3. Emisión secreta del voto: El elector se dirige a la cabina secreta (atril) y marca su preferencia.",
                    "4. Depósito: El elector dobla la papeleta de modo que no se vea el voto y la deposita en la urna correspondiente.",
                    "5. Firma y manchado: El elector firma o estampa su huella en el padrón, el Vocal le aplica la tinta indeleble en el dedo y el Secretario le devuelve su DUI."
                ),
                noHacer = listOf(
                    "NO devolver el DUI antes de haber firmado el padrón y aplicado la tinta indeleble.",
                    "NO acompañar al votante dentro de la cabina (salvo en casos de voto asistido legalmente autorizado)."
                ),
                fuentes = listOf("Código Electoral Arts. 195-201", "Instructivo JRV TSE")
            ),
            respuestasRelacionadas = listOf("VOT-002", "VOT-003", "VOT-004", "DUI-001")
        ),

        ElectoralSituation(
            id = "VOT-002",
            categoria = "votacion",
            subcategoria = "papeleta_danada",
            titulo = "Votante daña o rompe involuntariamente su papeleta y pide otra",
            keywords = listOf("papeleta rota", "dana papeleta", "rompio", "equivoco", "cambiar papeleta", "otra papeleta", "inutilizada"),
            sinonimos = listOf("boleta danada", "romper voto", "cambio de voto"),
            actores = listOf("votante", "presidente", "secretario"),
            acciones = listOf("romper", "entregar"),
            objetos = listOf("papeleta"),
            lugares = listOf("mesa_jrv"),
            momentos = listOf("votacion"),
            intenciones = listOf("procedimiento", "consulta_permiso_prohibicion"),
            contexto = "proceso_votacion",
            respuesta = StructuredElectoralResponse(
                situacion = "Un votante manifiesta haberse equivocado, roto o dañado por accidente su papeleta antes de depositarla en la urna y solicita una nueva.",
                fundamento = listOf(
                    "Art. 200 del Código Electoral: Procedimiento de inutilización y reposición de papeleta defectuosa."
                ),
                procedimiento = listOf(
                    "1. El votante debe entregar la papeleta dañada a la Presidencia de la JRV sin depositarla en la urna.",
                    "2. El Presidente y Secretario escriben en el reverso de la papeleta devuelta la palabra 'INUTILIZADA' o 'ANULADA POR ERROR', firmándola.",
                    "3. Se guarda en la bolsa/sobre especial de papeletas inutilizadas.",
                    "4. Se le entrega al elector una única nueva papeleta para que ejerza su derecho.",
                    "5. Se asienta la incidencia en el control de papeletas del acta."
                ),
                noHacer = listOf(
                    "NO entregar más de un reemplazo por elector.",
                    "NO permitir que la papeleta dañada sea introducida en la urna."
                ),
                fuentes = listOf("Código Electoral Art. 200", "Instructivo JRV TSE")
            ),
            respuestasRelacionadas = listOf("VOT-001", "ESC-002")
        ),

        ElectoralSituation(
            id = "VOT-003",
            categoria = "votacion",
            subcategoria = "celular_foto",
            titulo = "Votante usa teléfono celular o toma foto a su papeleta en cabina",
            keywords = listOf("celular", "foto", "fotografia", "camara", "video", "grabar", "cabina", "secreto del voto", "telefono"),
            sinonimos = listOf("fotografiar papeleta", "selfie voto", "violacion secreto"),
            actores = listOf("votante", "presidente", "pnc"),
            acciones = listOf("fotografiar", "votar"),
            objetos = listOf("celular", "papeleta"),
            lugares = listOf("cabina_secreta", "mesa_jrv"),
            momentos = listOf("votacion"),
            intenciones = listOf("prohibicion_sancion", "procedimiento"),
            contexto = "seguridad_electoral",
            respuesta = StructuredElectoralResponse(
                situacion = "Un elector es sorprendido utilizando su teléfono celular, cámara o tomando fotos de su papeleta dentro de la cabina de votación.",
                fundamento = listOf(
                    "Art. 78 de la Constitución: El voto es libre, directo, igualitario y SECRETO.",
                    "Disposiciones del TSE contra la coacción y compra de votos (prohibición de cámaras/celulares en atril)."
                ),
                procedimiento = listOf(
                    "1. La Presidencia de la JRV ordenará inmediatamente al elector guardar su dispositivo móvil.",
                    "2. Se le recordará la garantía constitucional del secreto del voto.",
                    "3. Si se evidencia que tomaba fotos con fines de compra/venta de votos o coacción, se llama a la PNC y Fiscalía para el procedimiento penal correspondiente.",
                    "4. Se asienta la observación en el acta de la mesa."
                ),
                noHacer = listOf(
                    "NO permitir que personas exhiban públicamente o transmitan fotos de sus votos dentro del centro electoral.",
                    "NO agredir físicamente al votante; ordenar a la PNC su intervención en caso de resistencia."
                ),
                fuentes = listOf("Constitución de El Salvador Art. 78", "Código Electoral", "Instructivo TSE")
            ),
            respuestasRelacionadas = listOf("VOT-001", "DEL-003")
        ),

        ElectoralSituation(
            id = "VOT-004",
            categoria = "votacion",
            subcategoria = "inclusion",
            titulo = "Voto asistido para personas con discapacidad, adultos mayores o embarazadas",
            keywords = listOf("voto asistido", "discapacidad", "ciego", "no vidente", "adulto mayor", "anciano", "silla de ruedas", "embarazada", "asistir"),
            sinonimos = listOf("ayuda para votar", "acompanante", "tercera edad"),
            actores = listOf("votante", "presidente", "miembro_jrv"),
            acciones = listOf("votar", "entregar"),
            objetos = listOf("papeleta", "dui"),
            lugares = listOf("mesa_jrv", "cabina_secreta"),
            momentos = listOf("votacion"),
            intenciones = listOf("procedimiento", "derechos"),
            contexto = "inclusion_asistencia",
            respuesta = StructuredElectoralResponse(
                situacion = "Protocolo de atención prioritaria y voto asistido para electores en condición de vulnerabilidad.",
                fundamento = listOf(
                    "Art. 198 del Código Electoral: Voto asistido e inclusión electoral.",
                    "Manual de Inclusión y Accesibilidad Electoral del TSE."
                ),
                procedimiento = listOf(
                    "1. Fila preferencial: Adultos mayores, mujeres embarazadas o con niños en brazos y personas con discapacidad tienen derecho a pasar directamente sin hacer fila regular.",
                    "2. Acompañamiento de confianza: La persona con discapacidad visual, motriz o cognitiva severa puede ingresar a la cabina con una persona de su estricta confianza y elección para que le auxilie a marcar.",
                    "3. Plantilla Braille: Si el votante con discapacidad visual lo solicita, se le proporcionará la plantilla electoral Braille oficial provista en el paquete electoral.",
                    "4. Ningún miembro de mesa ni vigilante de partido político puede ser el asistente de voto del ciudadano."
                ),
                noHacer = listOf(
                    "NO permitir que vigilantes partidarios acompañen a votar a ciudadanos con discapacidad.",
                    "NO obstaculizar el paso preferente a personas de la tercera edad o con discapacidad."
                ),
                fuentes = listOf("Código Electoral Art. 198", "Guía de Inclusión TSE")
            ),
            respuestasRelacionadas = listOf("VOT-001", "INST-003")
        ),

        // ==========================================
        // 5. ESCRUTINIO Y ACTAS
        // ==========================================
        ElectoralSituation(
            id = "ESC-001",
            categoria = "escrutinio",
            subcategoria = "cierre_inicio",
            titulo = "Cierre de votación a las 05:00 PM e inicio del escrutinio preliminar",
            keywords = listOf("escrutinio", "conteo", "cierre 5 pm", "iniciar conteo", "puertas cerradas", "abrir urna", "quienes pueden estar"),
            sinonimos = listOf("inicio de escrutinio", "recuento"),
            actores = listOf("presidente", "secretario", "vocal", "vigilante"),
            acciones = listOf("contar", "instalar"),
            objetos = listOf("urna", "acta", "papeleta"),
            lugares = listOf("mesa_jrv"),
            momentos = listOf("cierre", "escrutinio"),
            intenciones = listOf("procedimiento"),
            contexto = "escrutinio_conteo",
            respuesta = StructuredElectoralResponse(
                situacion = "Protocolo de transición de la fase de votación a la fase de escrutinio y recuento de votos a las 5:00 PM.",
                fundamento = listOf(
                    "Art. 196, 202 y 203 del Código Electoral: Cierre de votación y apertura de urnas para el escrutinio preliminar."
                ),
                procedimiento = listOf(
                    "1. 05:00 PM: Cierre del centro y votación del último en la fila. El Secretario cuenta y anota el total de ciudadanos que votaron en el padrón.",
                    "2. Inutilización de sobrantes: Se cuentan y se inutilizan (cortando o sellando) todas las papeletas que no se utilizaron.",
                    "3. Apertura de la urna: En presencia de los miembros de mesa y vigilantes acreditados, el Presidente abre la urna y extrae las papeletas.",
                    "4. Conteo de papeletas extraídas: Se cuenta el total de papeletas en la urna para verificar que coincida con el número de personas que votaron según el padrón."
                ),
                noHacer = listOf(
                    "NO permitir la presencia de personas no autorizadas (público general) en el área de escrutinio de la mesa.",
                    "NO iniciar el conteo de marcas antes de verificar la cantidad total de papeletas."
                ),
                fuentes = listOf("Código Electoral Arts. 196, 202, 203", "Instructivo JRV TSE")
            ),
            respuestasRelacionadas = listOf("ESC-002", "ESC-003", "ESC-004")
        ),

        ElectoralSituation(
            id = "ESC-002",
            categoria = "escrutinio",
            subcategoria = "calificacion_votos",
            titulo = "Clasificación de votos: Válidos, Nulos, Impugnados y Abstenciones",
            keywords = listOf("voto valido", "voto nulo", "voto impugnado", "abstencion", "como calificar", "calificacion", "marcas"),
            sinonimos = listOf("clasificar votos", "criterios de validez", "anular voto"),
            actores = listOf("presidente", "secretario", "vocal", "vigilante"),
            acciones = listOf("contar", "impugnar"),
            objetos = listOf("papeleta", "acta"),
            lugares = listOf("mesa_jrv"),
            momentos = listOf("escrutinio"),
            intenciones = listOf("procedimiento", "consulta_legal_normativa"),
            contexto = "escrutinio_conteo",
            respuesta = StructuredElectoralResponse(
                situacion = "Reglas legales para calificar cada papeleta durante el escrutinio preliminar.",
                fundamento = listOf(
                    "Art. 204 a 209 del Código Electoral: Validez y nulidad de los votos."
                ),
                procedimiento = listOf(
                    "1. Voto Válido: Muestra clara e indudable de la voluntad del elector sobre una sola bandera, o sobre candidatos de un mismo partido/coalición.",
                    "2. Voto Nulo: Aquel que contiene marcas sobre dos o más partidos no coligados, leyendas injuriosas, roturas intencionales o marcas que no permiten determinar la intención clara.",
                    "3. Voto en Blanco / Abstención: La papeleta no tiene ninguna marca.",
                    "4. Voto Impugnado: Aquel cuya validez o nulidad es objetada formalmente por un vigilante de partido; se coloca en sobre especial para que sea resuelto por el TSE en el escrutinio final."
                ),
                noHacer = listOf(
                    "NO decidir arbitrariamente la nulidad; la decisión de mesa se toma por mayoría simple de los miembros de la JRV.",
                    "NO permitir que los vigilantes toquen o manipulen las papeletas mientras se clasifican."
                ),
                fuentes = listOf("Código Electoral Arts. 204-209", "Instructivo JRV TSE")
            ),
            respuestasRelacionadas = listOf("ESC-001", "ESC-003", "ESC-004")
        ),

        ElectoralSituation(
            id = "ESC-003",
            categoria = "escrutinio",
            subcategoria = "coaliciones_preferente",
            titulo = "Calificación del voto por coalición y voto cruzado/fraccionario",
            keywords = listOf("coalicion", "voto cruzado", "marcas preferentes", "fraccionario", "dos banderas", "banderas coligadas"),
            sinonimos = listOf("voto por rostro", "papeleta legislativa"),
            actores = listOf("presidente", "secretario", "miembro_jrv"),
            acciones = listOf("contar"),
            objetos = listOf("papeleta", "acta"),
            lugares = listOf("mesa_jrv"),
            momentos = listOf("escrutinio"),
            intenciones = listOf("procedimiento", "consulta_legal_normativa"),
            contexto = "escrutinio_conteo",
            respuesta = StructuredElectoralResponse(
                situacion = "Cómo calificar papeletas con marcas en banderas de partidos coaligados o marcas de rostros en elecciones legislativas.",
                fundamento = listOf(
                    "Art. 205 y 207 del Código Electoral.",
                    "Sentencias de la Sala de lo Constitucional y normativa TSE sobre listas desbloqueadas."
                ),
                procedimiento = listOf(
                    "1. Banderas Coligadas: Si el elector marca dos o más banderas de partidos que van en COALICIÓN OFICIAL, el voto es VÁLIDO para la coalición.",
                    "2. Banderas No Coligadas: Si marca banderas de partidos distintos no coaligados, el voto es NULO.",
                    "3. Voto Preferente: En elecciones legislativas, las marcas en rostros de un mismo partido suman para el partido y definen preferencia de candidatos."
                ),
                noHacer = listOf(
                    "NO anular un voto coligado válido por el hecho de tener marcadas las banderas suscritas en el pacto de coalición."
                ),
                fuentes = listOf("Código Electoral Art. 205, 207", "Instructivo TSE")
            ),
            respuestasRelacionadas = listOf("ESC-002", "ESC-004")
        ),

        ElectoralSituation(
            id = "ESC-004",
            categoria = "escrutinio",
            subcategoria = "actas_transmision",
            titulo = "Llenado, firma, entrega y transmisión de actas de escrutinio",
            keywords = listOf("llenado de acta", "firmar acta", "copias acta", "transmision", "tse", "entrega", "escrutinio final"),
            sinonimos = listOf("hoja de conteo", "resultado de mesa", "acta de cierre"),
            actores = listOf("secretario", "presidente", "vigilante", "pnc", "fgr"),
            acciones = listOf("contar", "entregar"),
            objetos = listOf("acta"),
            lugares = listOf("mesa_jrv"),
            momentos = listOf("escrutinio"),
            intenciones = listOf("procedimiento"),
            contexto = "escrutinio_conteo",
            respuesta = StructuredElectoralResponse(
                situacion = "Cierre formal de la mesa: Consignación de resultados numéricos, firmas y distribución de copias oficiales.",
                fundamento = listOf(
                    "Art. 209 a 214 del Código Electoral: Requisitos del Acta de Cierre y Escrutinio Preliminar."
                ),
                procedimiento = listOf(
                    "1. Consignación numérica: El Secretario asienta los votos válidos, nulos, en blanco e impugnados en letras y números sin tachaduras.",
                    "2. Firma obligatoria: Firman los miembros de la JRV y los vigilantes acreditados que deseen hacerlo.",
                    "3. Distribución de copias: Una copia original va en el paquete electoral del TSE, una copia al Delegado de la FGR, una a la JED/JEM y copias legibles a los vigilantes acreditados.",
                    "4. Transmisión: Se escanea y transmite el acta mediante el sistema informático del TSE en el centro de votación."
                ),
                noHacer = listOf(
                    "NO alterar los números una vez firmado el documento.",
                    "NO negarle copia firmada del acta a los vigilantes acreditados de partidos políticos."
                ),
                fuentes = listOf("Código Electoral Arts. 209-214", "Instructivo JRV TSE")
            ),
            respuestasRelacionadas = listOf("ESC-001", "ESC-002", "VIG-001")
        ),

        // ==========================================
        // 6. VIGILANTES Y PARTIDOS POLÍTICOS
        // ==========================================
        ElectoralSituation(
            id = "VIG-001",
            categoria = "acreditacion",
            subcategoria = "derechos_vigilantes",
            titulo = "Derechos y facultades de los vigilantes de partidos políticos en mesa",
            keywords = listOf("derechos vigilante", "que puede hacer vigilante", "vigilancia partidaria", "fiscal de mesa", "facultades"),
            sinonimos = listOf("representante partidario", "funciones vigilante"),
            actores = listOf("vigilante", "miembro_jrv"),
            acciones = listOf("portar"),
            objetos = listOf("credencial", "acta"),
            lugares = listOf("mesa_jrv"),
            momentos = listOf("instalacion", "votacion", "escrutinio"),
            intenciones = listOf("derechos", "funciones_atribuciones"),
            contexto = "acreditacion_vigilancia",
            respuesta = StructuredElectoralResponse(
                situacion = "Facultades y prerrogativas legales que asisten a los vigilantes de partidos durante la jornada electoral.",
                fundamento = listOf(
                    "Art. 124 y 125 del Código Electoral: Derechos y funciones de los vigilantes de mesa."
                ),
                procedimiento = listOf(
                    "1. Presenciar todos los actos: Desde la instalación (06:00 AM), desarrollo de la votación, escrutinio preliminar (05:00 PM) y transmisión de resultados.",
                    "2. Exigir la revisión de credenciales y constatar que las urnas estén vacías al instalar la mesa.",
                    "3. Formular impugnaciones u observaciones sobre votos o votantes, las cuales deben constar en el acta.",
                    "4. Firmar el Acta de Escrutinio y recibir una copia oficial legible de la misma."
                ),
                noHacer = listOf(
                    "NO permitir más de un vigilante propietario por partido político al mismo tiempo en la mesa.",
                    "NO restringir injustificadamente la presencia visual de vigilantes debidamente acreditados."
                ),
                fuentes = listOf("Código Electoral Arts. 124, 125", "Instructivo JRV TSE")
            ),
            respuestasRelacionadas = listOf("VIG-002", "VIG-003", "ARM-001")
        ),

        ElectoralSituation(
            id = "VIG-002",
            categoria = "acreditacion",
            subcategoria = "prohibiciones_vigilantes",
            titulo = "Prohibiciones estrictas para los vigilantes (no tocar material ni mandar)",
            keywords = listOf("prohibiciones vigilante", "que no puede hacer", "tocar papeletas", "tocar urnas", "mandar mesa", "obstaculizar"),
            sinonimos = listOf("falta vigilante", "vigilante interfiriendo"),
            actores = listOf("vigilante", "presidente", "pnc"),
            acciones = listOf("romper", "portar"),
            objetos = listOf("papeleta", "urna", "padron"),
            lugares = listOf("mesa_jrv"),
            momentos = listOf("votacion", "escrutinio"),
            intenciones = listOf("prohibicion_sancion", "quien_no_puede"),
            contexto = "acreditacion_vigilancia",
            respuesta = StructuredElectoralResponse(
                situacion = "Acciones expresamente vedadas a la vigilancia de partidos políticos en las Juntas Receptoras de Votos.",
                fundamento = listOf(
                    "Art. 125 del Código Electoral: Prohibiciones a los vigilantes.",
                    "Art. 104 del Código Electoral: La máxima autoridad de la mesa es la Presidencia de la JRV."
                ),
                procedimiento = listOf(
                    "1. Prohibición de manipular material: Los vigilantes NO pueden tocar papeletas, urnas, padrones ni firmar por los electores.",
                    "2. Prohibición de coacción o propaganda: No pueden hacer proselitismo, sugerir votos ni intimidar a electores o integrantes de la mesa.",
                    "3. Prohibición de usurpación: No pueden ordenar órdenes a la mesa; sus observaciones deben canalizarse con respeto al Presidente.",
                    "4. Si un vigilante desacata y obstaculiza la mesa, el Presidente solicitará a la PNC su retiro."
                ),
                noHacer = listOf(
                    "NO permitir que los vigilantes toquen papeletas durante el conteo.",
                    "NO tolerar propaganda partidaria o presiones a votantes por parte de los vigilantes."
                ),
                fuentes = listOf("Código Electoral Arts. 104, 125")
            ),
            respuestasRelacionadas = listOf("VIG-001", "ARM-001", "DEL-002")
        ),

        // ==========================================
        // 7. LEY SECA Y DELITOS ELECTORALES
        // ==========================================
        ElectoralSituation(
            id = "DEL-001",
            categoria = "delitos",
            subcategoria = "ley_seca",
            titulo = "Aplicación de la Ley Seca (Art. 284 Código Electoral) y ebriedad",
            keywords = listOf("ley seca", "alcohol", "cerveza", "licor", "borracho", "ebrio", "estado de ebriedad", "sancion ley seca"),
            sinonimos = listOf("bebidas alcoholicas", "embriaguez", "prohibicion de alcohol"),
            actores = listOf("votante", "presidente", "pnc"),
            acciones = listOf("ingresar", "votar"),
            objetos = listOf("alcohol"),
            lugares = listOf("centro_de_votacion"),
            momentos = listOf("votacion"),
            intenciones = listOf("prohibicion_sancion", "consulta_legal_normativa"),
            contexto = "seguridad_electoral",
            respuesta = StructuredElectoralResponse(
                situacion = "Normas sobre la prohibición de venta, distribución y consumo de bebidas alcohólicas y atención a personas ebrias.",
                fundamento = listOf(
                    "Art. 284 del Código Electoral: 'El día anterior a la elección, el de la votación y el siguiente, se prohíbe la venta, distribución y consumo de bebidas embriagantes de cualquier naturaleza'.",
                    "Art. 285 Código Electoral: Régimen de multas por infracción a la Ley Seca."
                ),
                procedimiento = listOf(
                    "1. Vigencia: Rige durante todo el día previo al evento electoral, el día de la votación y el día posterior.",
                    "2. Persona en estado de ebriedad en el centro: Se le prohíbe el ingreso al centro de votación por alterar el orden público.",
                    "3. Si logra llegar a la mesa en estado inconveniente y altera el orden, el Presidente ordenará a la PNC su desalojo inmediato."
                ),
                noHacer = listOf(
                    "NO permitir el ingreso o permanencia de personas en evidente estado de ebriedad en las JRV.",
                    "NO tolerar el consumo de bebidas alcohólicas en el recinto electoral."
                ),
                fuentes = listOf("Código Electoral Arts. 284, 285")
            ),
            respuestasRelacionadas = listOf("DEL-002", "ARM-012")
        ),

        ElectoralSituation(
            id = "DEL-002",
            categoria = "delitos",
            subcategoria = "propaganda",
            titulo = "Prohibición de propaganda electoral y proselitismo el día de votación",
            keywords = listOf("propaganda", "proselitismo", "silencio electoral", "camisas de partido", "banderas", "musica", "dia de votacion"),
            sinonimos = listOf("publicidad politica", "distribucion volantes"),
            actores = listOf("votante", "vigilante", "pnc"),
            acciones = listOf("portar"),
            lugares = listOf("centro_de_votacion"),
            momentos = listOf("votacion"),
            intenciones = listOf("prohibicion_sancion", "consulta_legal_normativa"),
            contexto = "seguridad_electoral",
            respuesta = StructuredElectoralResponse(
                situacion = "Reglas sobre la prohibición de emitir o portar propaganda de partidos el día de la elección (Silencio Electoral).",
                fundamento = listOf(
                    "Art. 175 y 179 del Código Electoral: Prohibición de propaganda durante los 3 días anteriores a la elección y el propio día del sufragio."
                ),
                procedimiento = listOf(
                    "1. Silencio Electoral: Queda terminantemente prohibida toda propaganda electoral en medios, altavoces, vehículos o vallas.",
                    "2. En centros de votación: No se permite el uso de camisetas, distintivos o banderas partidarias a votantes dentro del recinto.",
                    "3. Vigilantes de mesa únicamente pueden portar su gafete/credencial reglamentario emitido por el TSE.",
                    "4. La PNC retirará cualquier propaganda o material proselitista dentro del perímetro del centro."
                ),
                noHacer = listOf(
                    "NO permitir que se repartan hojas volantes, afiches o artículos promocionales en el centro de sufragio."
                ),
                fuentes = listOf("Código Electoral Arts. 175, 179")
            ),
            respuestasRelacionadas = listOf("DEL-001", "VIG-002")
        ),

        // ==========================================
        // 8. PERMISOS LABORALES
        // ==========================================
        ElectoralSituation(
            id = "PER-001",
            categoria = "derechos_laborales",
            subcategoria = "permiso_113",
            titulo = "Permiso laboral con goce de sueldo y descanso para miembros de JRV (Art. 113)",
            keywords = listOf("permiso laboral", "trabajo", "sueldo", "goce de sueldo", "patrono", "empleador", "empresa", "art 113", "descanso"),
            sinonimos = listOf("permiso de trabajo", "licencia con sueldo", "remuneracion"),
            actores = listOf("patrono", "miembro_jrv"),
            acciones = listOf("entregar"),
            objetos = listOf("credencial", "acta"),
            intenciones = listOf("derechos", "consulta_legal_normativa"),
            contexto = "derechos_laborales",
            respuesta = StructuredElectoralResponse(
                situacion = "Garantías y derechos laborales que amparan a los ciudadanos nombrados para integrar organismos electorales temporales.",
                fundamento = listOf(
                    "Art. 113 del Código Electoral: Permiso obligatorio con goce de sueldo y compensación de descanso para miembros de JRV, JEM y JED."
                ),
                procedimiento = listOf(
                    "1. Permiso para capacitación y jornada: Todo patrono público o privado está obligado por ley a conceder permiso con goce de sueldo para las jornadas de capacitación y para el día de la elección.",
                    "2. Descanso compensatorio: Los ciudadanos que integren JRV tienen derecho a un día de descanso remunerado posterior a la jornada electoral.",
                    "3. Comprobación: El ciudadano presenta ante su empleador la credencial oficial del TSE o la constancia de asistencia extendida por la autoridad electoral.",
                    "4. Infracción: El patrono que niegue el permiso o descuente salario incurre en sanción y multa impuesta por el Ministerio de Trabajo y el TSE."
                ),
                noHacer = listOf(
                    "NO descontar el día de salario a los trabajadores que hayan servido como integrantes de mesa electoral.",
                    "NO aplicar sanciones laborales a los ciudadanos designados por el TSE."
                ),
                fuentes = listOf("Código Electoral de El Salvador, Art. 113", "Código de Trabajo")
            ),
            respuestasRelacionadas = listOf("INST-001", "VIG-001")
        ),

        // ==========================================
        // 9. INCLUSIÓN Y PROTECCIÓN DE LA MUJER
        // ==========================================
        ElectoralSituation(
            id = "INC-001",
            categoria = "inclusion",
            subcategoria = "leiv_violencia_politica",
            titulo = "Protección a la mujer y prevención de violencia política electoral (LEIV Art. 55)",
            keywords = listOf("mujer", "violencia politica", "leiv", "acoso", "agresion a mujer", "candidatas", "discriminacion", "proteccion"),
            sinonimos = listOf("violencia contra la mujer", "leiv articulo 55"),
            actores = listOf("votante", "miembro_jrv", "pnc", "fgr"),
            acciones = listOf("amenaza"),
            intenciones = listOf("procedimiento_emergencia", "prohibicion_sancion", "derechos"),
            contexto = "seguridad_electoral",
            respuesta = StructuredElectoralResponse(
                situacion = "Medidas de protección y denuncia ante actos de violencia política o acoso contra mujeres participantes en el proceso electoral.",
                fundamento = listOf(
                    "Art. 55 de la Ley Especial Integral para una Vida Libre de Violencia para las Mujeres (LEIV): Delito de Violencia Política contra las Mujeres.",
                    "Protocolo del TSE para la Atención de Violencia Política contra las Mujeres."
                ),
                procedimiento = listOf(
                    "1. Cero tolerancia: Se prohíbe todo acto de intimidación, descalificación sexista, agresión verbal o física contra votantes, integrantes de JRV o candidatas.",
                    "2. Denuncia y auxilio: La afectada o la Presidencia de JRV solicita el auxilio de la PNC y la intervención inmediata de la Fiscalía Electoral.",
                    "3. Asiento en actas: Se consignan los hechos y la identidad del agresor en el acta de incidencias para el respectivo proceso judicial."
                ),
                noHacer = listOf(
                    "NO desestimar agresiones o tratos discriminatorios por razones de género.",
                    "NO impedir la participación de mujeres en cargos de dirección de las mesas electorales."
                ),
                fuentes = listOf("LEIV Art. 55", "Código Electoral", "Protocolo TSE de Género")
            ),
            respuestasRelacionadas = listOf("AME-001", "VOT-004")
        )
    )

    private val _loadedSituations = mutableListOf<ElectoralSituation>()

    /**
     * Initializes the knowledge base by reading all JSON files in assets/json respuestas/
     */
    fun init(context: Context) {
        com.example.data.search.codigo.ElectoralCodeBank.init(context)
        com.example.data.search.codigo.ElectoralConstitutionBank.init(context)
        com.example.data.search.synonyms.ElectoralConceptRegistry.init(context)
        try {
            val jsonSituations = JsonKnowledgeLoader.loadAllFromAssets(context)
            synchronized(_loadedSituations) {
                _loadedSituations.clear()
                _loadedSituations.addAll(jsonSituations)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun findById(id: String): ElectoralSituation? {
        synchronized(_loadedSituations) {
            val fromJson = _loadedSituations.firstOrNull { it.id.equals(id, ignoreCase = true) }
            if (fromJson != null) return fromJson
        }
        return SITUATIONS.firstOrNull { it.id.equals(id, ignoreCase = true) }
    }

    fun getAll(): List<ElectoralSituation> {
        synchronized(_loadedSituations) {
            if (_loadedSituations.isNotEmpty()) {
                return _loadedSituations.toList()
            }
        }
        return SITUATIONS
    }
}
