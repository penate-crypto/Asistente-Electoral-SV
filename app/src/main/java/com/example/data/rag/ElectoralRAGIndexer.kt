package com.example.data.rag

object ElectoralRAGIndexer {

    val knowledgeChunks: List<ElectoralKnowledgeChunk> by lazy {
        buildAllElectoralChunks()
    }

    private fun buildAllElectoralChunks(): List<ElectoralKnowledgeChunk> {
        val list = mutableListOf<ElectoralKnowledgeChunk>()

        // =========================================================================
        // DOCUMENTO 1: CÓDIGO ELECTORAL DE EL SALVADOR (DECRETO No. 413)
        // =========================================================================
        val doc1Id = "codigo_electoral_decreto_413"
        val doc1Title = "Código Electoral de la República de El Salvador (Decreto No. 413)"

        list.add(
            ElectoralKnowledgeChunk(
                id = "ce_art_1_objeto",
                documentId = doc1Id,
                documentTitle = doc1Title,
                chapter = "TÍTULO I: DISPOSICIONES FUNDAMENTALES • CAPÍTULO I",
                sectionTitle = "Objeto del Código Electoral",
                articleRef = "Art. 1",
                pageNumber = 1,
                content = "Art. 1.- El presente Código tiene por objeto regular las actividades del cuerpo electoral, el registro electoral, los organismos electorales, así como la actividad del Estado en cuanto se refiere al proceso eleccionario. Todas las autoridades de la República están obligadas a colaborar con los organismos electorales en el desempeño de sus funciones.",
                sourceType = "Ley Oficial",
                keywords = listOf("objeto", "codigo", "electoral", "regulacion", "organismos", "registro"),
                concepts = listOf("marco legal", "objeto de la ley", "jurisdicción electoral", "colaboración institucional")
            )
        )

        list.add(
            ElectoralKnowledgeChunk(
                id = "ce_art_2_elecciones",
                documentId = doc1Id,
                documentTitle = doc1Title,
                chapter = "TÍTULO I • CAPÍTULO I: ÁMBITO DE APLICACIÓN",
                sectionTitle = "Elecciones de Funcionarios Públicos",
                articleRef = "Art. 2",
                pageNumber = 1,
                content = "Art. 2.- El proceso eleccionario regulado por este Código se refiere a las elecciones de: a) Presidente y Vicepresidente de la República; b) Diputados y Diputadas al Parlamento Centroamericano (PARLACEN); c) Diputados y Diputadas a la Asamblea Legislativa; y d) Miembros de los Concejos Municipales.",
                sourceType = "Ley Oficial",
                keywords = listOf("presidente", "vicepresidente", "diputados", "parlacen", "asamblea", "concejos", "municipales", "alcaldes"),
                concepts = listOf("cargos de elección popular", "tipos de elección", "diputaciones", "presidencia", "alcaldías")
            )
        )

        list.add(
            ElectoralKnowledgeChunk(
                id = "ce_art_3_4_sufragio",
                documentId = doc1Id,
                documentTitle = doc1Title,
                chapter = "TÍTULO I • CAPÍTULO II: DEL SUFRAGIO",
                sectionTitle = "Naturaleza, Garantía y Pureza del Sufragio",
                articleRef = "Arts. 3 y 4",
                pageNumber = 2,
                content = "Art. 3.- El sufragio es un derecho y un deber de los ciudadanos y ciudadanas, su ejercicio es indelegable e irrenunciable. El voto es libre, directo, igualitario y secreto. Art. 4.- Nadie podrá impedir, coartar o perturbar el ejercicio del sufragio. Las autoridades competentes y la fuerza pública están en la obligación de garantizar la libertad y pureza del sufragio y facilitar su ejercicio a todo ciudadano habilitado.",
                sourceType = "Ley Oficial",
                keywords = listOf("sufragio", "voto", "secreto", "libre", "directo", "igualitario", "garantia", "pureza", "obligacion"),
                concepts = listOf("derecho al voto", "características del voto", "pureza electoral", "delito de coacción")
            )
        )

        list.add(
            ElectoralKnowledgeChunk(
                id = "ce_art_7_inhabilitados",
                documentId = doc1Id,
                documentTitle = doc1Title,
                chapter = "TÍTULO I • CAPÍTULO II: DEL SUFRAGIO",
                sectionTitle = "Inhabilidades para Ejercer el Sufragio",
                articleRef = "Art. 7",
                pageNumber = 2,
                content = "Art. 7.- No pueden ejercer el sufragio ni votar: a) Quienes tengan auto de prisión formal o condena penal ejecutoriada; b) Enajenados mentales o personas declaradas judicialmente incapaces; c) Quienes se negaren sin justa causa a desempeñar cargos en organismos electorales; d) Personas de conducta notoriamente viciada; e) Quienes compren o vendan votos o faciliten el fraude; f) Quienes promuevan o apoyen la reelección presidencial inmediata contraria a la Constitución.",
                sourceType = "Ley Oficial",
                keywords = listOf("inhabiles", "prision", "condena", "incapaces", "fraude", "compra de votos", "inhabilitacion", "no puede votar"),
                concepts = listOf("pérdida de derechos ciudadanos", "inhabilitación para votar", "suspensión de ciudadanía")
            )
        )

        list.add(
            ElectoralKnowledgeChunk(
                id = "ce_art_38_jerarquia",
                documentId = doc1Id,
                documentTitle = doc1Title,
                chapter = "TÍTULO IV: ORGANISMOS ELECTORALES • CAPÍTULO I",
                sectionTitle = "Jerarquía de los Organismos Electorales",
                articleRef = "Art. 38",
                pageNumber = 3,
                content = "Art. 38.- Son organismos electorales en orden jerárquico: 1. Tribunal Supremo Electoral (TSE), máxima autoridad; 2. Juntas Electorales Departamentales (JED); 3. Juntas Electorales Municipales (JEM); 4. Juntas Receptoras de Votos (JRV). Las decisiones de los organismos superiores son de acatamiento obligatorio para los organismos subordinados.",
                sourceType = "Ley Oficial",
                keywords = listOf("organismos", "jerarquia", "tse", "jed", "jem", "jrv", "autoridad", "tribunal"),
                concepts = listOf("estructura orgánica", "autoridad electoral", "jerarquía institucional")
            )
        )

        list.add(
            ElectoralKnowledgeChunk(
                id = "ce_art_99_100_jrv_integracion",
                documentId = doc1Id,
                documentTitle = doc1Title,
                chapter = "TÍTULO IV • CAPÍTULO IV: DE LAS JUNTAS RECEPTORAS DE VOTOS",
                sectionTitle = "Integración, Cargos y Quórum Legal de JRV",
                articleRef = "Arts. 99 y 100",
                pageNumber = 3,
                content = "Arts. 99-100.- Las Juntas Receptoras de Votos se integran por un máximo de cinco miembros propietarios y sus respectivos suplentes: Presidente, Secretario, Primer Vocal, Segundo Vocal y Tercer Vocal, propuestos por los partidos políticos contendientes o designados por sorteo ciudadano del TSE sin afiliación partidaria. La JRV se instala e inicia funciones válidamente con un quórum legal mínimo de tres miembros.",
                sourceType = "Ley Oficial",
                keywords = listOf("jrv", "integracion", "presidente", "secretario", "vocal", "suplente", "miembros", "quorum", "tres miembros", "cinco"),
                concepts = listOf("integración de mesa", "cargos de JRV", "quórum de instalación", "ciudadanos sorteados")
            )
        )

        list.add(
            ElectoralKnowledgeChunk(
                id = "ce_art_102_105_atribuciones_cargos",
                documentId = doc1Id,
                documentTitle = doc1Title,
                chapter = "TÍTULO IV • CAPÍTULO IV: FUNCIONES DE LOS MIEMBROS DE JRV",
                sectionTitle = "Atribuciones Específicas de Presidente, Secretario y Vocales",
                articleRef = "Arts. 102 a 105",
                pageNumber = 3,
                content = "Atribuciones por cargo en la JRV:\n" +
                        "• Presidente de JRV: Máxima autoridad de la mesa, coordina la instalación, custodia la urna, firma y sella papeletas al reverso, mantiene el orden, dirige el escrutinio preliminar voz por voz y solicita auxilio directo de la PNC en caso de alteración.\n" +
                        "• Secretario: Revisa y busca al elector en el padrón oficial, estampa 'VOTÓ', redacta y resguarda el Acta de Instalación, Cierre y Escrutinio, y anota formalmente impugnaciones.\n" +
                        "• Primer Vocal: Inspecciona manos y dedos para verificar ausencia de tinta previa, dobla y entrega papeletas autorizadas al votante y orienta al atril secreto.\n" +
                        "• Segundo y Tercer Vocal: Aplican tinta indeleble indeleble en el dedo pulgar/índice hasta la cutícula, devuelven el DUI al elector, custodian la privacidad del atril y asisten activamente en el conteo de votos.",
                sourceType = "Ley Oficial",
                keywords = listOf("presidente", "secretario", "primer vocal", "segundo vocal", "tercer vocal", "atribuciones", "funciones", "tinta", "padron", "urna"),
                concepts = listOf("roles de mesa", "responsabilidades JRV", "custodia de urna", "manejo de actas", "aplicación de tinta")
            )
        )

        list.add(
            ElectoralKnowledgeChunk(
                id = "ce_art_113_permiso_laboral",
                documentId = doc1Id,
                documentTitle = doc1Title,
                chapter = "TÍTULO IV • CAPÍTULO IV: DERECHOS LABORALES DE INTEGRANTES",
                sectionTitle = "Permiso Laboral Obligatorio y Remunerado (Art. 113)",
                articleRef = "Art. 113",
                pageNumber = 3,
                content = "Art. 113.- Todo empleador o patrono, sea del sector público, autónomo o privado, está en la obligación legal estricta de conceder permiso con goce de sueldo (remunerado) a los ciudadanos designados por el TSE para integrar organismos electorales (JED, JEM, JRV):\n" +
                        "1. Durante los días de capacitación electoral oficial previa convocada por el TSE.\n" +
                        "2. Durante todo el día domingo de la elección.\n" +
                        "3. Durante el día hábil inmediato posterior a la elección como descanso obligatorio remunerado.\n" +
                        "El despido, sanción salarial o represalia patronal constituye infracción legal grave sancionada por el Ministerio de Trabajo y el TSE.",
                sourceType = "Ley Oficial",
                keywords = listOf("permiso laboral", "art 113", "trabajo", "patrono", "empleador", "goce de sueldo", "remunerado", "descanso", "dia habil siguiente", "lunes"),
                concepts = listOf("derechos laborales electorales", "permiso remunerado", "día libre", "protección al trabajador")
            )
        )

        list.add(
            ElectoralKnowledgeChunk(
                id = "ce_art_190_198_horarios_apertura_cierre",
                documentId = doc1Id,
                documentTitle = doc1Title,
                chapter = "TÍTULO VII: PROCESO DE VOTACIÓN • CAPÍTULO I",
                sectionTitle = "Horarios Electorales: Instalación 6:00 AM, Apertura 7:00 AM y Cierre 5:00 PM",
                articleRef = "Arts. 190, 191 y 198",
                pageNumber = 4,
                content = "Horarios legales de la jornada electoral:\n" +
                        "• 06:00 AM: Presentación obligatoria de miembros de JRV e inicio de instalación y revisión del paquete electoral.\n" +
                        "• 07:00 AM: Apertura oficial de votación para la ciudadanía.\n" +
                        "• 17:00 Horas (05:00 PM): Cierre definitivo de la votación. Nadie más puede ingresar al centro de votación, pero todos los ciudadanos que ya se encuentren en fila dentro del centro tienen derecho a emitir su voto antes del cierre de urna.",
                sourceType = "Ley Oficial",
                keywords = listOf("horario", "6:00 am", "7:00 am", "5:00 pm", "17:00", "instalacion", "apertura", "cierre", "fila", "hora"),
                concepts = listOf("cronograma de jornada", "hora de apertura", "hora de cierre", "derecho de votantes en fila")
            )
        )

        list.add(
            ElectoralKnowledgeChunk(
                id = "ce_art_205_207_calificacion_votos",
                documentId = doc1Id,
                documentTitle = doc1Title,
                chapter = "TÍTULO VII • CAPÍTULO II: DEL ESCRUTINIO Y CALIFICACIÓN DE VOTOS",
                sectionTitle = "Calificación de Votos: Válidos, Nulos, Impugnados, Abstenciones y Sobrantes",
                articleRef = "Arts. 205, 206 y 207",
                pageNumber = 4,
                content = "Reglas legales de calificación de votos (Arts. 205 a 207):\n" +
                        "1. Voto Válido (Art. 205): Papeleta con marca clara (cruz, equis o trazo) dentro de la bandera de un solo partido político, coalición o sobre candidatos a diputados de una misma lista/partido o candidato no partidario.\n" +
                        "2. Voto Nulo (Art. 207): Papeleta que contiene marcas sobre dos o más partidos políticos que no participan en coalición, papeleta rota o mutilada deliberadamente, papeleta con inscripciones ofensivas, palabras soeces o firmas que violen el secreto del voto.\n" +
                        "3. Voto Impugnado (Art. 206): Voto cuya validez o identidad del elector es objetada formalmente por un Vigilante de partido acreditado. No se cuenta en la mesa; se coloca en sobre sellado especial de 'VOTOS IMPUGNADOS' para resolución definitiva de los Magistrados del TSE en el escrutinio final.\n" +
                        "4. Voto en Blanco / Abstención: Papeleta depositada sin ninguna marca.\n" +
                        "5. Papeletas Inutilizadas / Sobrantes: Papeletas que quedaron sin usar a las 5:00 PM, las cuales se cuentan, se cortan/inutilizan por la mitad antes de abrir la urna y se empacan en su sobre oficial.",
                sourceType = "Ley Oficial",
                keywords = listOf("voto valido", "voto nulo", "voto impugnado", "abstencion", "blanco", "sobrantes", "inutilizadas", "calificacion", "art 205", "art 207", "art 206"),
                concepts = listOf("criterios de calificación", "reglas de escrutinio", "votos controvertidos", "impugnaciones")
            )
        )

        list.add(
            ElectoralKnowledgeChunk(
                id = "ce_art_284_ley_seca",
                documentId = doc1Id,
                documentTitle = doc1Title,
                chapter = "TÍTULO IX: PROHIBICIONES Y SANCIONES ELECTORALES",
                sectionTitle = "Ley Seca: Prohibición de Venta y Consumo de Alcohol",
                articleRef = "Art. 284",
                pageNumber = 4,
                content = "Art. 284.- Queda terminantemente prohibida la venta, distribución, expendio y consumo de bebidas alcohólicas, embriagantes o fermentadas durante el día anterior a la elección, el día completo de la elección y el día siguiente. Las personas o comercios infractores serán sancionados con multas administrativas y el decomiso inmediato del producto por parte de la Policía Nacional Civil (PNC) y delegados municipales.",
                sourceType = "Ley Oficial",
                keywords = listOf("ley seca", "alcohol", "bebidas", "cerveza", "licor", "prohibicion", "dia anterior", "dia siguiente", "art 284", "multa"),
                concepts = listOf("orden público electoral", "restricción de alcohol", "sanciones ley seca")
            )
        )

        list.add(
            ElectoralKnowledgeChunk(
                id = "ce_art_290_prohibicion_armas",
                documentId = doc1Id,
                documentTitle = doc1Title,
                chapter = "TÍTULO IX: SEGURIDAD Y ORDEN PÚBLICO",
                sectionTitle = "Prohibición Absoluta de Armas y Objetos Peligrosos (Art. 290)",
                articleRef = "Art. 290",
                pageNumber = 4,
                content = "Art. 290.- Se prohíbe terminantemente a toda persona portar armas de fuego, armas blancas, objetos cortopunzantes o cualquier instrumento peligroso dentro de los centros de votación y en un radio perimetral de cien metros alrededor del mismo. La única excepción autorizada por la ley son los agentes uniformados de la Policía Nacional Civil (PNC) y Fuerza Armada asignados exclusivamente a la seguridad electoral del centro. Quien ingrese o intente ingresar armado o con objetos peligrosos (incluso miembros de mesa o vigilantes) será desarmado, retirado y puesto a la orden de la Fiscalía General de la República (FGR) por delito electoral en flagrancia.",
                sourceType = "Ley Oficial",
                keywords = listOf("armas", "prohibicion de armas", "art 290", "arma de fuego", "cuchillo", "objeto cortante", "pnc", "seguridad", "flagrancia", "centro de votacion"),
                concepts = listOf("seguridad del centro", "delitos electorales", "porte de armas", "intervención policial")
            )
        )

        // =========================================================================
        // DOCUMENTO 2: CONSTITUCIÓN DE LA REPÚBLICA DE EL SALVADOR (1983)
        // =========================================================================
        val doc2Id = "constitucion_republica_1983"
        val doc2Title = "Constitución de la República de El Salvador (1983)"

        list.add(
            ElectoralKnowledgeChunk(
                id = "const_art_72_73_derechos_politicos",
                documentId = doc2Id,
                documentTitle = doc2Title,
                chapter = "TÍTULO II: LOS DERECHOS FUNDAMENTALES Y POLÍTICOS • CAPÍTULO I",
                sectionTitle = "Derechos y Deberes Políticos Fundamentales",
                articleRef = "Arts. 72 y 73",
                pageNumber = 1,
                content = "Art. 72.- Son derechos políticos del ciudadano salvadoreño: 1º Ejercer el sufragio; 2º Asociarse libremente para constituir partidos políticos o ingresar a los ya constituidos; 3º Optar a cargos públicos. Art. 73.- Son deberes políticos del ciudadano: 1º Ejercer el sufragio; 2º Cumplir y velar porque se cumpla la Constitución de la República; 3º Servir al Estado en organismos electorales de conformidad con la ley.",
                sourceType = "Constitución de la República",
                keywords = listOf("constitucion", "derechos politicos", "deberes", "art 72", "art 73", "sufragio", "partidos"),
                concepts = listOf("derechos constitucionales", "deber cívico", "servicio obligatorio en mesa")
            )
        )

        list.add(
            ElectoralKnowledgeChunk(
                id = "const_art_208_209_tse",
                documentId = doc2Id,
                documentTitle = doc2Title,
                chapter = "TÍTULO VI: ÓRGANOS DEL GOBIERNO • SECCIÓN TERCERA: TRIBUNAL SUPREMO ELECTORAL",
                sectionTitle = "Rango, Jurisdicción y Atribuciones Constitucionales del TSE",
                articleRef = "Arts. 208 y 209",
                pageNumber = 2,
                content = "Arts. 208-209.- El Tribunal Supremo Electoral (TSE) es la autoridad máxima en materia electoral en la República de El Salvador, con jurisdicción nacional, autonomía administrativa y jurisdiccional. Sus resoluciones en materia contencioso-electoral son inapelables y de obligatorio cumplimiento. La ley establece los organismos electorales temporales (JED, JEM, JRV) bajo la dirección del TSE para la recepción, recuento y fiscalización de votos.",
                sourceType = "Constitución de la República",
                keywords = listOf("tse", "tribunal supremo electoral", "art 208", "art 209", "constitucional", "maxima autoridad", "magistrados"),
                concepts = listOf("autoridad electoral suprema", "autonomía jurisdiccional", "organismos temporales")
            )
        )

        // =========================================================================
        // DOCUMENTO 3: INSTRUCTIVO Y PROTOCOLO OPERATIVO DE JRV (TSE)
        // =========================================================================
        val doc3Id = "instructivo_jrv_tse"
        val doc3Title = "Instructivo Oficial para Miembros de JRV (Tribunal Supremo Electoral)"

        list.add(
            ElectoralKnowledgeChunk(
                id = "ins_sustitucion_escalonada",
                documentId = doc3Id,
                documentTitle = doc3Title,
                chapter = "FASE 1: INSTALACIÓN DE MESA (06:00 AM - 07:00 AM)",
                sectionTitle = "Protocolo de Sustitución Escalonada por Ausencia de Miembros de JRV",
                articleRef = "Protocolo de Quórum e Instalación TSE",
                pageNumber = 1,
                content = "Protocolo oficial cronológico ante inasistencia o falta de quórum (menos de 3 miembros) a las 06:00 AM:\n" +
                        "1. 06:15 AM: Incorporar a los Miembros Suplentes presentes acreditados de los partidos correspondientes para asumir los cargos propietarios vacantes.\n" +
                        "2. 06:30 AM: Si aún no se alcanza el quórum mínimo de 3 miembros, el Presidente presente notifica inmediatamente a la Junta Electoral Municipal (JEM) o Jefe de Centro para reubicar suplentes de otras mesas del mismo centro.\n" +
                        "3. 06:45 AM: Si persiste la falta de miembros, la JEM juramenta en el acto a ciudadanos salvadoreños aptos presentes en la fila del centro que acepten voluntariamente integrar la mesa.\n" +
                        "4. 07:00 AM: La mesa debe quedar instalada y aperturar la votación puntualmente.",
                sourceType = "Instructivo Operativo JRV",
                keywords = listOf("falta de miembros", "ausencia", "sustitucion", "suplentes", "6:15", "6:30", "6:45", "jem", "ciudadanos en fila", "no llego nadie", "falta gente"),
                concepts = listOf("contingencia de instalación", "sustitución escalonada", "quórum de mesa", "asistencia JRV")
            )
        )

        list.add(
            ElectoralKnowledgeChunk(
                id = "ins_falta_material_papeletas",
                documentId = doc3Id,
                documentTitle = doc3Title,
                chapter = "FASE 1: RECEPCIÓN Y REVISIÓN DEL PAQUETE ELECTORAL",
                sectionTitle = "Procedimiento ante Faltante de Papeletas, Sellos o Material Electoral",
                articleRef = "Instructivo JRV - Sección Inventario",
                pageNumber = 1,
                content = "Procedimiento ante faltante de material electoral al abrir el paquete:\n" +
                        "1. Conteo e Inventario Inicial: Contar las papeletas en presencia obligatoria de los vigilantes acreditados antes de que inicie la votación.\n" +
                        "2. Consignar en Acta: Registrar la cantidad exacta de papeletas recibidas en el Acta de Instalación (ejemplo: si el padrón indica 500 pero llegaron 480, anotar 'Recibidas 480 papeletas').\n" +
                        "3. Solicitud a la DOE/JEM: El Presidente de JRV informa de inmediato al Delegado del TSE / JEM para solicitar el paquete de reserva de contingencia de la Dirección de Organización Electoral (DOE).\n" +
                        "4. No Paralizar la Mesa: Si hay material para iniciar, la votación inicia normalmente a las 7:00 AM mientras la JEM gestiona el suministro complementario oficial.",
                sourceType = "Instructivo Operativo JRV",
                keywords = listOf("faltan papeletas", "falta material", "menos papeletas", "paquete incompleto", "no llegaron completas", "acta de instalacion", "doe", "contingencia"),
                concepts = listOf("faltante de material", "inventario de papeletas", "solicitud a la DOE", "registro en acta")
            )
        )

        list.add(
            ElectoralKnowledgeChunk(
                id = "ins_credenciales_vigilantes_acceso",
                documentId = doc3Id,
                documentTitle = doc3Title,
                chapter = "SEGURIDAD Y CONTROL DE ACCESO A LA MESA",
                sectionTitle = "Requisitos de Acceso a JRV: Credenciales Obligatorias y Prohibición de Acceso Irregular",
                articleRef = "Instructivo JRV - Fiscalización y Acreditaciones",
                pageNumber = 1,
                content = "Reglas estrictas de acceso al recinto de la JRV:\n" +
                        "1. Ninguna persona puede instalarse en la mesa, actuar como miembro de JRV o ejercer como vigilante partidario sin portar su credencial original oficial emitida y sellada por el TSE junto con su DUI vigente.\n" +
                        "2. Si una persona llega sin credencial alegando ser miembro o vigilante, el Presidente de JRV le negará el acceso a la mesa.\n" +
                        "3. Si la persona insiste o intenta perturbar el funcionamiento de la mesa, el Presidente de JRV solicitará de inmediato la intervención de la Policía Nacional Civil (PNC) para desalojarla del centro.",
                sourceType = "Instructivo Operativo JRV",
                keywords = listOf("sin credencial", "no tiene credencial", "acceso a jrv", "entrar a la mesa", "vigilante sin carnet", "pnc desalojo", "acreditacion"),
                concepts = listOf("control de credenciales", "acreditación TSE", "acceso a mesa", "prohibición sin credencial")
            )
        )

        list.add(
            ElectoralKnowledgeChunk(
                id = "ins_voto_asistido_inclusion",
                documentId = doc3Id,
                documentTitle = doc3Title,
                chapter = "FASE 2: ATENCIÓN A CIUDADANOS Y MODALIDADES DE VOTO",
                sectionTitle = "Voto Asistido para Adultos Mayores y Personas con Discapacidad",
                articleRef = "Instructivo JRV - Voto Inclusivo TSE",
                pageNumber = 2,
                content = "Normas de voto asistido e inclusivo:\n" +
                        "1. Las personas adultas mayores, personas con discapacidad visual, motriz o de cualquier tipo tienen derecho prioritario en la fila sin esperar turno.\n" +
                        "2. Si el elector con discapacidad requiere asistencia, tiene derecho exclusivo a ser acompañado al atril secreto por una persona de su absoluta confianza o solicitar apoyo de un miembro de JRV.\n" +
                        "3. Se encuentra prohibido que vigilantes de partidos políticos asistan o ingresen al atril con el votante.",
                sourceType = "Instructivo Operativo JRV",
                keywords = listOf("voto asistido", "discapacidad", "ancianos", "adultos mayores", "ciegos", "silla de ruedas", "acompañante", "fila preferencial"),
                concepts = listOf("inclusión electoral", "voto asistido", "atención prioritaria", "privacidad del voto")
            )
        )

        // =========================================================================
        // DOCUMENTO 4: MANUAL DE ESCRUTINIO Y ACTAS (TSE)
        // =========================================================================
        val doc4Id = "manual_escrutinio_tse"
        val doc4Title = "Manual Oficial de Escrutinio Preliminar y Actas (TSE)"

        list.add(
            ElectoralKnowledgeChunk(
                id = "man_escrutinio_paso_a_paso",
                documentId = doc4Id,
                documentTitle = doc4Title,
                chapter = "ESCRUTINIO PRELIMINAR PASO A PASO (05:00 PM)",
                sectionTitle = "Procedimiento Cronológico del Conteo de Votos y Llenado de Acta",
                articleRef = "Manual de Escrutinio TSE - Paso 1 a 6",
                pageNumber = 1,
                content = "Procedimiento oficial de escrutinio preliminar (05:00 PM):\n" +
                        "Paso 1: Declarar cerrada la votación e inutilizar/cortar papeletas sobrantes contando el total.\n" +
                        "Paso 2: Contar las firmas estampadas en el Padrón Electoral para conocer cuántos ciudadanos votaron.\n" +
                        "Paso 3: Abrir la urna y contar las papeletas depositadas sin desdoblarlas para verificar que el número de papeletas coincida exactamente con las firmas del padrón.\n" +
                        "Paso 4: El Presidente abre cada papeleta, muestra a los vigilantes y canta el voto de viva voz, mientras el Secretario y Vocales registran en la hoja de conteo.\n" +
                        "Paso 5: Llenar el Acta de Escrutinio en borrador, verificar sumas y transcribir al acta oficial/sistema de transmisión.\n" +
                        "Paso 6: Firmar el acta por los miembros de JRV y entregar copias oficiales certificadas a los vigilantes de partidos políticos acreditados.",
                sourceType = "Manual de Escrutinio",
                keywords = listOf("escrutinio", "conteo", "paso a paso", "acta", "urna", "viva voz", "5:00 pm", "firmas padron", "papeletas sobrantes", "copias vigilantes"),
                concepts = listOf("escrutinio preliminar", "conteo de votos", "llenado de actas", "entrega de copias")
            )
        )

        list.add(
            ElectoralKnowledgeChunk(
                id = "man_voto_cruzado_preferencial",
                documentId = doc4Id,
                documentTitle = doc4Title,
                chapter = "CALIFICACIÓN EN ELECCIÓN DE DIPUTADOS",
                sectionTitle = "Reglas de Voto Preferencial, Voto Cruzado y Voto por Bandera",
                articleRef = "Manual de Escrutinio TSE - Elección Legislativa",
                pageNumber = 2,
                content = "Reglas de votación para Diputados a la Asamblea Legislativa:\n" +
                        "1. Voto por Bandera: Marca única en la bandera partidaria; el voto se distribuye proporcionalmente a la lista del partido.\n" +
                        "2. Voto Preferencial: Marcas sobre rostros de candidatos de un mismo partido político.\n" +
                        "3. Voto Cruzado / Fraccionario: El elector puede marcar rostros de candidatos de diferentes partidos políticos, siempre que la suma de marcas no supere el número total de diputados a elegir por el departamento.\n" +
                        "4. Nulidad por exceso: Si el ciudadano marca más candidatos que los escaños asignados al departamento, el voto es nulo.",
                sourceType = "Manual de Escrutinio",
                keywords = listOf("voto cruzado", "diputados", "preferencial", "fraccionario", "rostros", "bandera", "marcas", "escaños", "asamblea"),
                concepts = listOf("voto legislativo", "voto fraccionario", "voto preferencial", "límite de marcas")
            )
        )

        // =========================================================================
        // DOCUMENTO 5: LEY ESPECIAL INTEGRAL PARA UNA VIDA LIBRE DE VIOLENCIA (LEIV) - MATERIA ELECTORAL
        // =========================================================================
        val doc5Id = "leiv_proteccion_mujer_electoral"
        val doc5Title = "Ley Especial Integral para una Vida Libre de Violencia para las Mujeres (LEIV) • Ámbito Electoral"

        list.add(
            ElectoralKnowledgeChunk(
                id = "leiv_art_55_violencia_politica",
                documentId = doc5Id,
                documentTitle = doc5Title,
                chapter = "PROTECCIÓN DE DERECHOS POLÍTICOS DE LA MUJER",
                sectionTitle = "Sanción y Prevención de Violencia Política contra la Mujer (LEIV Art. 55)",
                articleRef = "LEIV Arts. 9, 10 y 55",
                pageNumber = 1,
                content = "La LEIV y el Código Electoral sancionan penal y administrativamente cualquier acto de intimidación, acoso, agresión física, verbal o psicológica dirigida contra mujeres que ejerzan cargos en organismos electorales (JRV, JEM, JED), candidatas, vigilantes o votantes. Las autoridades de JRV y la PNC tienen la obligación de proteger la dignidad y seguridad de las mujeres en el centro de votación, procediendo a la denuncia y captura inmediata ante agresiones.",
                sourceType = "Ley Oficial",
                keywords = listOf("mujer", "leiv", "violencia politica", "acoso", "intimidacion", "derechos de la mujer", "art 55", "genero"),
                concepts = listOf("violencia política", "protección a la mujer", "paridad e igualdad", "seguridad en mesa")
            )
        )

        // =========================================================================
        // DOCUMENTO 6: LEY DE PARTIDOS POLÍTICOS
        // =========================================================================
        val doc6Id = "ley_partidos_politicos_sv"
        val doc6Title = "Ley de Partidos Políticos de El Salvador"

        list.add(
            ElectoralKnowledgeChunk(
                id = "lpp_derechos_prohibiciones_vigilantes",
                documentId = doc6Id,
                documentTitle = doc6Title,
                chapter = "FISCALIZACIÓN ELECTORAL Y VIGILANCIA",
                sectionTitle = "Derechos y Prohibiciones Estrictas de Vigilantes de Partidos Políticos",
                articleRef = "Ley de Partidos Políticos y Código Electoral",
                pageNumber = 1,
                content = "Derechos y Prohibiciones de Vigilantes Partidarios en JRV:\n" +
                        "• DERECHOS: Presenciar la instalación, apertura, votación y conteo; formular objeciones e impugnaciones; firmar las actas y recibir copia certificada legible del Acta de Escrutinio.\n" +
                        "• PROHIBICIONES ESTRICTAS: Se prohíbe tajantemente a los vigilantes tocar o manipular papeletas electorales o urnas, intervenir en el atril con los electores, hacer propaganda o proselitismo político dentro del centro de votación, o dar órdenes a los miembros de JRV. Si un vigilante transgrede estas normas, el Presidente de JRV puede ordenar su expulsión del centro.",
                sourceType = "Ley Oficial",
                keywords = listOf("vigilantes", "partidos politicos", "derechos vigilantes", "prohibiciones", "no tocar papeletas", "propaganda", "expulsion"),
                concepts = listOf("fiscalización partidaria", "rol de vigilantes", "prohibiciones a partidos", "autonomía de JRV")
            )
        )

        // =========================================================================
        // DOCUMENTO 7: CÓDIGO PENAL DE EL SALVADOR (DELITOS ELECTORALES)
        // =========================================================================
        val doc7Id = "codigo_penal_delitos_electorales"
        val doc7Title = "Código Penal de El Salvador (Delitos Electorales)"

        list.add(
            ElectoralKnowledgeChunk(
                id = "cp_art_295_fraude",
                documentId = doc7Id,
                documentTitle = doc7Title,
                chapter = "LIBRO SEGUNDO • TÍTULO XIII: DELITOS CONTRA LOS DERECHOS CÍVICOS Y ELECTORALES",
                sectionTitle = "Fraude Electoral (Art. 295)",
                articleRef = "Art. 295 Código Penal",
                pageNumber = 1,
                content = "Art. 295.- El que votare más de una vez en la misma elección, suplantare a otro elector, votare sin tener derecho o adulterare el padrón electoral o el resultado de la votación, será sancionado con prisión de cuatro a seis años. Si el autor fuere miembro de un organismo electoral o funcionario público, la pena se aumentará hasta en una tercera parte.",
                sourceType = "Código Penal",
                keywords = listOf("fraude electoral", "votar dos veces", "suplantacion", "adulterar padron", "penas de prision", "art 295"),
                concepts = listOf("delito electoral", "fraude en urnas", "responsabilidad penal agravada")
            )
        )

        list.add(
            ElectoralKnowledgeChunk(
                id = "cp_art_296_coaccion",
                documentId = doc7Id,
                documentTitle = doc7Title,
                chapter = "LIBRO SEGUNDO • TÍTULO XIII: DELITOS CONTRA LOS DERECHOS CÍVICOS Y ELECTORALES",
                sectionTitle = "Coacción del Sufragio (Art. 296)",
                articleRef = "Art. 296 Código Penal",
                pageNumber = 2,
                content = "Art. 296.- El que mediante violencia, amenazas, intimidación o engaño impidiere o coartare a un elector el ejercicio del derecho de sufragio, o le obligare a votar en determinado sentido o a abstenerse, será sancionado con prisión de tres a seis años.",
                sourceType = "Código Penal",
                keywords = listOf("coaccion del voto", "amenazas electorales", "intimidacion", "obligar a votar", "art 296", "violencia"),
                concepts = listOf("libertad del sufragio", "coacción del votante", "delito contra el voto")
            )
        )

        list.add(
            ElectoralKnowledgeChunk(
                id = "cp_art_297_298_falsedad_obstaculizacion",
                documentId = doc7Id,
                documentTitle = doc7Title,
                chapter = "LIBRO SEGUNDO • TÍTULO XIII: DELITOS CONTRA LOS DERECHOS CÍVICOS Y ELECTORALES",
                sectionTitle = "Falsedad Documental y Obstaculización Electoral (Arts. 297-298)",
                articleRef = "Arts. 297 y 298 Código Penal",
                pageNumber = 3,
                content = "Arts. 297 y 298.- Se sanciona con prisión de cuatro a ocho años a quien alterare, destruyere, sustrajere o falsificare credenciales, actas, papeletas o paquetes electorales, o impidiere o perturbare la instalación o funcionamiento de JRV, el desarrollo de la votación, el escrutinio o la transmisión de resultados.",
                sourceType = "Código Penal",
                keywords = listOf("falsedad de actas", "destruccion de papeletas", "obstaculizar jrv", "art 297", "art 298", "prision"),
                concepts = listOf("custodia de actas", "falsedad electoral", "obstrucción de votación")
            )
        )

        list.add(
            ElectoralKnowledgeChunk(
                id = "cp_art_299_301_usurpacion_compra_votos",
                documentId = doc7Id,
                documentTitle = doc7Title,
                chapter = "LIBRO SEGUNDO • TÍTULO XIII: DELITOS CONTRA LOS DERECHOS CÍVICOS Y ELECTORALES",
                sectionTitle = "Usurpación de Funciones y Compra de Votos (Arts. 299, 301)",
                articleRef = "Arts. 299 y 301 Código Penal",
                pageNumber = 4,
                content = "Arts. 299 y 301.- Ejercer funciones como miembro de JRV, JEM o JED sin credencial oficial legítima del TSE se sanciona con prisión de tres a seis años (Art. 299). Dar, ofrecer, prometer o solicitar dinero o beneficios a cambio del voto se sanciona con prisión de cuatro a seis años (Art. 301).",
                sourceType = "Código Penal",
                keywords = listOf("usurpacion de mesa", "credencial falsa", "compra de votos", "dadivas", "art 299", "art 301"),
                concepts = listOf("autenticidad de credenciales", "soborno electoral", "usurpación de autoridad")
            )
        )

        // =========================================================================
        // DOCUMENTO 8: LEY ESPECIAL PARA LA REESTRUCTURACIÓN MUNICIPAL (DECRETO 763)
        // =========================================================================
        val doc8Id = "ley_reestructuracion_municipal_763"
        val doc8Title = "Ley Especial para la Reestructuración Municipal (Decreto No. 763)"

        list.add(
            ElectoralKnowledgeChunk(
                id = "lerm_44_municipios_262_distritos",
                documentId = doc8Id,
                documentTitle = doc8Title,
                chapter = "REORGANIZACIÓN TERRITORIAL Y CONCEJOS MUNICIPALES",
                sectionTitle = "44 Municipios, 262 Distritos y Elección de Concejos Municipales",
                articleRef = "Decreto No. 763 Arts. 1-5",
                pageNumber = 1,
                content = "La Ley Especial para la Reestructuración Municipal reorganiza a El Salvador en 44 Municipios integrados por 262 Distritos Municipales distribuidos en los 14 departamentos. Cada municipio elige un Concejo Municipal integrado por Alcalde/sa, Síndico/a y Regidores propietarios y suplentes conforme al Código Electoral, manteniendo los servicios del estado familiar en cada sede distrital.",
                sourceType = "Ley Especial",
                keywords = listOf("44 municipios", "262 distritos", "decreto 763", "reestructuracion municipal", "alcaldes", "concejos municipales"),
                concepts = listOf("nueva división municipal", "elección de concejos", "distritos electorales")
            )
        )

        // =========================================================================
        // DOCUMENTO 9: LEY ESPECIAL PARA EL EJERCICIO DEL SUFRAGIO EN EL EXTRANJERO (DECRETO 542)
        // =========================================================================
        val doc9Id = "ley_sufragio_extranjero_542"
        val doc9Title = "Ley Especial para el Ejercicio del Sufragio en el Extranjero (Decreto No. 542)"

        list.add(
            ElectoralKnowledgeChunk(
                id = "lsee_voto_remoto_presencial_exterior",
                documentId = doc9Id,
                documentTitle = doc9Title,
                chapter = "MODALIDADES DE VOTO ELECTRÓNICO EN EL EXTERIOR",
                sectionTitle = "Voto Electrónico Remoto por Internet y Presencial en el Extranjero",
                articleRef = "Decreto No. 542 Arts. 1-10",
                pageNumber = 1,
                content = "Regula el sufragio de los salvadoreños en el exterior mediante dos modalidades de votación electrónica: 1) Votación electrónica remota por internet durante los 30 días previos a la elección para ciudadanos con DUI emitido con dirección en el extranjero; 2) Votación electrónica presencial en sedes diplomáticas, consulares y centros autorizados por el TSE para salvadoreños con DUI emitido en El Salvador o pasaporte.",
                sourceType = "Ley Especial",
                keywords = listOf("sufragio en el extranjero", "voto electronico", "voto por internet", "consulados", "jelvex", "decreto 542"),
                concepts = listOf("voto en el exterior", "voto por internet", "padron de salvadoreños en el mundo")
            )
        )

        // =========================================================================
        // DOCUMENTO 10: CICLO ELECTORAL SALVADOREÑO
        // =========================================================================
        val doc10Id = "ciclo_electoral_salvadoreno"
        val doc10Title = "Ciclo Electoral Salvadoreño • Guía Doctrinal y Operativa"

        list.add(
            ElectoralKnowledgeChunk(
                id = "ces_macro_fases_electorales",
                documentId = doc10Id,
                documentTitle = doc10Title,
                chapter = "METODOLOGÍA DEL PROCESO ELECCIONARIO",
                sectionTitle = "Fases Pre-electoral, Electoral y Post-electoral del Ciclo Salvadoreño",
                articleRef = "Guía del Ciclo Electoral TSE",
                pageNumber = 1,
                content = "El Ciclo Electoral Salvadoreño comprende tres macro-etapas secuenciales:\n" +
                        "1. Etapa Pre-electoral: Plan General de Elecciones (PLAGEL), conformación del Padrón Electoral, inscripción de candidaturas y conformación de JED, JEM y JRV.\n" +
                        "2. Etapa Electoral: Jornada de votación (07:00 a 17:00 hrs), votación ciudadana y escrutinio preliminar en JRV.\n" +
                        "3. Etapa Post-electoral: Escrutinio final por el TSE, resolución de recursos, declaratoria de resultados y entrega de credenciales.",
                sourceType = "Guía Doctrinal TSE",
                keywords = listOf("ciclo electoral", "etapa pre-electoral", "etapa electoral", "etapa post-electoral", "plagel", "escrutinio final"),
                concepts = listOf("ciclo electoral completo", "fases de elecciones", "planificación electoral")
            )
        )

        // =========================================================================
        // DOCUMENTO 11: ACUERDO LEGISLATIVO - REFORMA ELECTORAL
        // =========================================================================
        val doc11Id = "acuerdo_legislativo_reforma_electoral"
        val doc11Title = "Acuerdo Legislativo - Reforma y Normativa Electoral"

        list.add(
            ElectoralKnowledgeChunk(
                id = "alre_acuerdos_escrutinio_plazos",
                documentId = doc11Id,
                documentTitle = doc11Title,
                chapter = "REFORMAS LEGISLATIVAS Y NORMAS PROCESALES",
                sectionTitle = "Cómputo de Mayorías, Escrutinio y Plazos Electorales",
                articleRef = "Acuerdos Legislativos Oficiales",
                pageNumber = 1,
                content = "Disposiciones legislativas que regulan las fórmulas de cómputo en escrutinio, exigencias de mayoría absoluta (mitad más uno de los votos válidos) para elecciones presidenciales, y plazos reglamentarios para el TSE.",
                sourceType = "Acuerdo Legislativo",
                keywords = listOf("acuerdo legislativo", "reforma electoral", "mayoria absoluta", "segunda eleccion", "escrutinio"),
                concepts = listOf("reformas de escrutinio", "mayorías constitucionales", "plazos de elección")
            )
        )

        return list
    }
}
