package com.example.data

data class PdfDocument(
    val id: String,
    val title: String,
    val category: String,
    val summary: String,
    val authority: String,
    val releaseDate: String,
    val assetPath: String? = null,
    val pages: List<PdfPage>
)

data class PdfPage(
    val pageNumber: Int,
    val headerTitle: String,
    val subtitle: String,
    val sections: List<PdfSection>
)

data class PdfSection(
    val title: String,
    val content: String,
    val articleRef: String? = null,
    val isWarning: Boolean = false,
    val isOfficialSeal: Boolean = false
)

object ElectoralLibraryData {
    val categories = listOf(
        "Todos",
        "Disposiciones Fundamentales",
        "Organismos Electorales (JRV, JEM, JED, TSE)",
        "Fiscalización y Vigilancia",
        "Votación y Escrutinio",
        "Sanciones, Delitos y Recursos"
    )

    val documents: List<PdfDocument> = listOf(
        // 1. Código Electoral (Decreto 413)
        PdfDocument(
            id = "codigo_electoral_decreto_413",
            title = "Código Electoral de la República de El Salvador (Decreto 413)",
            category = "Disposiciones Fundamentales",
            summary = "Decreto No. 413 de la Asamblea Legislativa de la República de El Salvador (D.O. No. 138, Tomo No. 400). Texto oficial consolidado con todas sus reformas, interpretaciones auténticas y sentencias de la Sala de lo Constitucional.",
            authority = "Asamblea Legislativa de El Salvador • Tribunal Supremo Electoral",
            releaseDate = "D.O. No. 138, Tomo No. 400 (Actualizado)",
            assetPath = "libros/Codigo-Electoral.pdf",
            pages = listOf(
                PdfPage(
                    pageNumber = 1,
                    headerTitle = "ASAMBLEA LEGISLATIVA - REPÚBLICA DE EL SALVADOR",
                    subtitle = "DECRETO No. 413 • CONSIDERANDOS • TÍTULO I: DISPOSICIONES FUNDAMENTALES",
                    sections = listOf(
                        PdfSection(
                            title = "Decreto No. 413 - Considerandos",
                            content = "La Asamblea Legislativa emite el Código Electoral para consolidar un texto normativo actualizado y armónico con el voto residencial, adopción del DUI, modalidades de votación y concejos municipales plurales, garantizando la pureza del sistema democrático.",
                            isOfficialSeal = true
                        ),
                        PdfSection(
                            title = "Objeto de la Ley",
                            content = "Art. 1.- El presente Código tiene por objeto regular las actividades del cuerpo electoral, el registro electoral, los organismos electorales, así como la actividad del Estado en cuanto se refiere al proceso eleccionario.",
                            articleRef = "Art. 1"
                        ),
                        PdfSection(
                            title = "Elección de Funcionarios",
                            content = "Art. 2.- El proceso eleccionario regulado se refiere a las elecciones de: a) Presidente y Vicepresidente de la República; b) Diputados al Parlamento Centroamericano; c) Diputados a la Asamblea Legislativa; y d) Miembros de los Concejos Municipales.",
                            articleRef = "Art. 2"
                        )
                    )
                ),
                PdfPage(
                    pageNumber = 2,
                    headerTitle = "CAPÍTULO II: DEL SUFRAGIO",
                    subtitle = "DERECHOS, DEBERES, GARANTÍAS E INHABILIDADES",
                    sections = listOf(
                        PdfSection(
                            title = "Derecho y Deber del Sufragio",
                            content = "Art. 3.- El sufragio es un derecho y un deber de los ciudadanos y ciudadanas, su ejercicio es indelegable e irrenunciable. El voto es libre, directo, igualitario y secreto.",
                            articleRef = "Art. 3"
                        ),
                        PdfSection(
                            title = "Garantía de Libertad y Pureza",
                            content = "Art. 4.- Nadie podrá impedir, coartar o perturbar el ejercicio del sufragio. Las autoridades competentes están en la obligación de garantizar la libertad y pureza del sufragio y facilitar su ejercicio.",
                            articleRef = "Art. 4"
                        ),
                        PdfSection(
                            title = "Inhabilidades para Ejercer el Sufragio",
                            content = "Art. 7.- No pueden ejercer el sufragio: a) Quienes tengan auto de prisión formal; b) Enajenados mentales; c) Declarados en interdicción judicial; d) Quienes se negaren sin justa causa a desempeñar cargos de elección popular; e) De conducta viciada; f) Condenados por delito; g) Quienes compren o vendan votos; h) Quienes promuevan o apoyen la reelección presidencial inmediata; i) Quienes cometan fraude electoral.",
                            articleRef = "Art. 7",
                            isWarning = true
                        )
                    )
                ),
                PdfPage(
                    pageNumber = 3,
                    headerTitle = "TÍTULO IV: ORGANISMOS ELECTORALES Y JRV",
                    subtitle = "ESTRUCTURA, INTEGRACIÓN DE JRV Y PERMISOS LABORALES (ART. 113)",
                    sections = listOf(
                        PdfSection(
                            title = "Jerarquía de Organismos",
                            content = "Art. 38.- Son organismos electorales: a) Tribunal Supremo Electoral (TSE); b) Juntas Electorales Departamentales (JED); c) Juntas Electorales Municipales (JEM); d) Juntas Receptoras de Votos (JRV).",
                            articleRef = "Art. 38"
                        ),
                        PdfSection(
                            title = "Integración y Quórum de JRV",
                            content = "Arts. 99-100.- Integradas por un máximo de cinco miembros propietarios y suplentes (Presidente, Secretario, Primer Vocal, Segundo Vocal y Tercer Vocal). Pueden funcionar válidamente con un mínimo de tres miembros.",
                            articleRef = "Arts. 99-100"
                        ),
                        PdfSection(
                            title = "Permiso Laboral Remunerado (Art. 113)",
                            content = "Art. 113.- Todo empleador, público o privado, está obligado a conceder permiso con goce de sueldo a las personas designadas para integrar organismos electorales durante capacitaciones, el día de las elecciones y el día hábil siguiente.",
                            articleRef = "Art. 113"
                        )
                    )
                ),
                PdfPage(
                    pageNumber = 4,
                    headerTitle = "TÍTULO VII: PROCEDIMIENTO DE VOTACIÓN Y ESCRUTINIO",
                    subtitle = "HORARIOS, SEGURIDAD Y CONTEO DE VOTOS",
                    sections = listOf(
                        PdfSection(
                            title = "Horario de Votación",
                            content = "Arts. 190 y 198.- La instalación de la JRV inicia a las 06:00 AM. La votación ciudadana inicia a las 07:00 AM y finaliza a las 17:00 horas (05:00 PM), momento en que se declara cerrado el centro de votación salvo electores en fila.",
                            articleRef = "Arts. 190, 198"
                        ),
                        PdfSection(
                            title = "Calificación de Votos",
                            content = "Arts. 205-207.- Voto válido: marca clara en bandera o candidato. Voto nulo: doble marca entre partidos no coaligados o papeleta dañada. Voto impugnado: objeción legal formal tramitada en sobre cerrado ante el TSE.",
                            articleRef = "Arts. 205-207"
                        ),
                        PdfSection(
                            title = "Prohibición de Armas y Ley Seca",
                            content = "Arts. 284 y 290.- Prohibición total de portar armas de fuego en centros de votación (salvo PNC en servicio). Ley seca rige desde el día anterior, el día de la elección y el día posterior.",
                            articleRef = "Arts. 284, 290",
                            isWarning = true
                        )
                    )
                )
            )
        ),

        // 2. Constitución de la República de El Salvador
        PdfDocument(
            id = "constitucion_republica_1983",
            title = "Constitución de la República de El Salvador (1983)",
            category = "Disposiciones Fundamentales",
            summary = "Carta Magna y Ley Suprema de la República de El Salvador. Contiene los principios fundamentales del sistema político democrático, la soberanía ciudadana, los derechos fundamentales y las atribuciones del Tribunal Supremo Electoral.",
            authority = "Asamblea Constituyente de 1983 • República de El Salvador",
            releaseDate = "D.O. No. 234, Tomo No. 281 (1983)",
            assetPath = "libros/constitucion.pdf",
            pages = listOf(
                PdfPage(
                    pageNumber = 1,
                    headerTitle = "CONSTITUCIÓN DE LA REPÚBLICA DE EL SALVADOR",
                    subtitle = "TÍTULO II: LOS DERECHOS FUNDAMENTALES Y POLÍTICOS",
                    sections = listOf(
                        PdfSection(
                            title = "Principios del Sufragio Constitucional",
                            content = "Art. 72.- Los derechos políticos del ciudadano son: 1º Ejercer el sufragio; 2º Asociarse para constituir partidos políticos con arreglo a la ley; 3º Optar a cargos públicos cumpliendo con los requisitos que determinan esta Constitución y las leyes secundarias.",
                            articleRef = "Art. 72",
                            isOfficialSeal = true
                        ),
                        PdfSection(
                            title = "Deberes Políticos y Carácter del Voto",
                            content = "Art. 73.- Los deberes políticos del ciudadano son: 1º Ejercer el sufragio; 2º Cumplir y velar porque se cumpla la Constitución de la República; 3º Servir al Estado de conformidad con la ley.",
                            articleRef = "Art. 73"
                        ),
                        PdfSection(
                            title = "Naturaleza del Voto",
                            content = "Art. 78.- El voto será libre, directo, igualitario y secreto.",
                            articleRef = "Art. 78"
                        )
                    )
                ),
                PdfPage(
                    pageNumber = 2,
                    headerTitle = "TÍTULO VI: ÓRGANOS DEL GOBIERNO - SECCIÓN ELECTORAL",
                    subtitle = "AUTORIDAD Y ATRIBUCIONES DEL TRIBUNAL SUPREMO ELECTORAL",
                    sections = listOf(
                        PdfSection(
                            title = "Creación y Rango Constitucional del TSE",
                            content = "Art. 208.- Habrá un Tribunal Supremo Electoral que estará formado por cinco Magistrados que durarán cinco años en sus funciones y serán elegidos por la Asamblea Legislativa. Es la máxima autoridad en materia electoral.",
                            articleRef = "Art. 208"
                        ),
                        PdfSection(
                            title = "Jurisdicción y Competencia Electoral",
                            content = "Art. 209.- La ley establecerá los organismos necesarios para la recepción, recuento y fiscalización de votos y demás actividades concernientes al sufragio.",
                            articleRef = "Art. 209"
                        )
                    )
                )
            )
        ),

        // 3. Instructivo para Miembros de JRV (TSE)
        PdfDocument(
            id = "instructivo_jrv_tse",
            title = "Instructivo para Miembros de JRV (TSE)",
            category = "Organismos Electorales (JRV, JEM, JED, TSE)",
            summary = "Guía operativa oficial emitida por el Tribunal Supremo Electoral para los integrantes de las Juntas Receptoras de Votos (JRV). Describe paso a paso la instalación, apertura, recepción de sufragios, escrutinio preliminar y llenado de actas.",
            authority = "Tribunal Supremo Electoral (TSE)",
            releaseDate = "Instructivo Oficial de Capacitación",
            assetPath = "libros/instructivo-JRV.pdf",
            pages = listOf(
                PdfPage(
                    pageNumber = 1,
                    headerTitle = "INSTRUCTIVO PARA JUNTAS RECEPTORAS DE VOTOS (JRV)",
                    subtitle = "PASO 1: INSTALACIÓN Y REVISIÓN DEL PAQUETE ELECTORAL (06:00 AM)",
                    sections = listOf(
                        PdfSection(
                            title = "Presentación e Instalación",
                            content = "Los miembros de JRV deben presentarse al centro de votación a las 06:00 AM con su credencial del TSE y DUI vigente. La mesa se instala válidamente con al menos 3 miembros.",
                            articleRef = "Fase de Instalación",
                            isOfficialSeal = true
                        ),
                        PdfSection(
                            title = "Revisión del Paquete Electoral",
                            content = "Se abre el paquete en presencia de vigilantes acreditados, verificando: padrón de búsqueda y de firmas, papeletas de votación foliadas, sellos oficiales, tinta indeleble, actas de instalación y escrutinio, y útiles de escritorio.",
                            articleRef = "Inventario de Mesa"
                        )
                    )
                ),
                PdfPage(
                    pageNumber = 2,
                    headerTitle = "PASO 2: VOTACIÓN CIUDADANA Y PASO 3: ESCRUTINIO PRELIMINAR",
                    subtitle = "RECEPCIÓN DEL VOTO Y CONTEO PÚBLICO A LAS 05:00 PM",
                    sections = listOf(
                        PdfSection(
                            title = "Atención al Elector",
                            content = "1. Solicitar DUI y buscar en el padrón. 2. El Secretario firma la papeleta en el reverso y desprende la esquina foliada. 3. El ciudadano vota en secreto en el anaquel. 4. Deposita la papeleta en la urna. 5. Firma el padrón y se le aplica tinta indeleble en el dedo índice. 6. Se le devuelve el DUI.",
                            articleRef = "Procedimiento de Mesa"
                        ),
                        PdfSection(
                            title = "Escrutinio Preliminar (05:00 PM)",
                            content = "A las 17:00 horas se cierra la votación. Se cuentan e inutilizan papeletas no usadas. Se abre la urna, se confronta el total de papeletas con el padrón de firmas y se califican voto por voto de viva voz frente a los vigilantes.",
                            articleRef = "Conteo y Cierre"
                        )
                    )
                )
            )
        ),

        // 4. Ley de Partidos Políticos
        PdfDocument(
            id = "ley_de_partidos_politicos",
            title = "Ley de Partidos Políticos",
            category = "Fiscalización y Vigilancia",
            summary = "Regula la constitución, organización, funcionamiento, financiamiento público y privado, democracia interna y fiscalización de los partidos políticos en El Salvador.",
            authority = "Asamblea Legislativa • Tribunal Supremo Electoral",
            releaseDate = "Decreto Legislativo No. 307",
            assetPath = "libros/ley-de-partidos-politicos.pdf",
            pages = listOf(
                PdfPage(
                    pageNumber = 1,
                    headerTitle = "LEY DE PARTIDOS POLÍTICOS DE EL SALVADOR",
                    subtitle = "DISPOSICIONES GENERALES, DEMOCRACIA INTERNA Y FINANCIAMIENTO",
                    sections = listOf(
                        PdfSection(
                            title = "Objeto de la Ley",
                            content = "Art. 1.- La presente Ley tiene por objeto regular la constitución, organización, funcionamiento y financiamiento de los partidos políticos, así asentar las normas para garantizar la democracia interna y la transparencia financiera.",
                            articleRef = "Art. 1",
                            isOfficialSeal = true
                        ),
                        PdfSection(
                            title = "Elecciones Internas Obligatorias",
                            content = "Arts. 37 y 38.- Los candidatos a cargos de elección popular deben ser electos mediante voto libre, directo, igualitario y secreto de los afiliados del partido político, bajo supervisión de sus comisiones electorales internas.",
                            articleRef = "Arts. 37-38"
                        ),
                        PdfSection(
                            title = "Transparencia y Origen de Fondos",
                            content = "Arts. 67 y 68.- Los partidos están obligados a registrar contablemente todos los donativos privados y aportes de militantes, manteniendo reserva de donaciones anónimas que están prohibidas por la ley.",
                            articleRef = "Arts. 67-68",
                            isWarning = true
                        )
                    )
                )
            )
        ),

        // 5. Ley de Acceso a la Información Pública (LAIP)
        PdfDocument(
            id = "ley_acceso_informacion_publica",
            title = "Ley de Acceso a la Información Pública (LAIP)",
            category = "Disposiciones Fundamentales",
            summary = "Garantiza el derecho de toda persona a acceder a la información en poder de los órganos del Estado, instituciones electorales y partidos políticos receptores de fondos públicos.",
            authority = "Instituto de Acceso a la Información Pública (IAIP) • Asamblea Legislativa",
            releaseDate = "Decreto Legislativo No. 534",
            assetPath = "libros/Ley-de-Acceso-a-la-Informacion-Publica.pdf",
            pages = listOf(
                PdfPage(
                    pageNumber = 1,
                    headerTitle = "LEY DE ACCESO A LA INFORMACIÓN PÚBLICA (LAIP)",
                    subtitle = "PRINCIPIOS DE MÁXIMA PUBLICIDAD Y TRANSPARENCIA ELECTORAL",
                    sections = listOf(
                        PdfSection(
                            title = "Principio de Máxima Publicidad",
                            content = "Art. 1 y 4.- Toda información en poder de los entes obligados es pública, salvo las excepciones expresamente establecidas por la ley (información reservada o confidencial).",
                            articleRef = "Arts. 1, 4",
                            isOfficialSeal = true
                        ),
                        PdfSection(
                            title = "Partidos Políticos y TSE como Entes Obligados",
                            content = "Art. 7.- El Tribunal Supremo Electoral y los partidos políticos son entes obligados a rendir cuentas y publicar de oficio la información relativa a presupuestos, contratación y orígenes de financiamiento.",
                            articleRef = "Art. 7"
                        )
                    )
                )
            )
        ),

        // 6. Ley Especial para el Ejercicio del Sufragio en el Extranjero
        PdfDocument(
            id = "ley_sufragio_extranjero",
            title = "Ley Especial para el Ejercicio del Sufragio en el Extranjero",
            category = "Votación y Escrutinio",
            summary = "Normativa que regula los mecanismos para que los ciudadanos salvadoreños domiciliados en el exterior puedan emitir su voto mediante sufragio electrónico presencial o por internet en elecciones presidenciales y legislativas.",
            authority = "Asamblea Legislativa de El Salvador • TSE",
            releaseDate = "Decreto Legislativo Actualizado",
            assetPath = "libros/Ley-especial-para-el-sufragio-en-el-extranjero.pdf",
            pages = listOf(
                PdfPage(
                    pageNumber = 1,
                    headerTitle = "SUFRAGIO EN EL EXTRANJERO",
                    subtitle = "MODALIDADES DE VOTO ELECTRÓNICO POR INTERNET Y PRESENCIAL",
                    sections = listOf(
                        PdfSection(
                            title = "Derecho al Voto en el Exterior",
                            content = "Art. 1.- Garantiza el derecho de los salvadoreños residentes en el exterior a emitir su voto para elecciones de Presidente, Vicepresidente y Diputaciones a la Asamblea Legislativa.",
                            articleRef = "Art. 1",
                            isOfficialSeal = true
                        ),
                        PdfSection(
                            title = "Modalidades: Remoto por Internet y Presencial",
                            content = "Votación remota por internet: Para salvadoreños cuyo DUI tenga dirección en el extranjero. Votación electrónica presencial: Para salvadoreños con DUI emitido en El Salvador o pasaporte que se presenten a consulados y sedes habilitadas.",
                            articleRef = "Modalidades Electorales"
                        )
                    )
                )
            )
        ),

        // 7. Ley Especial Integral para una Vida Libre de Violencia / Ley de Género
        PdfDocument(
            id = "ley_genero_electoral",
            title = "Ley Especial Integral para una Vida Libre de Violencia / Ley de Género",
            category = "Sanciones, Delitos y Recursos",
            summary = "Protección legal especial para mujeres en el ejercicio de sus derechos políticos, sancionando la violencia política de género, coacciones y discriminación en organismos electorales y candidaturas.",
            authority = "Asamblea Legislativa • ISDEMU • TSE",
            releaseDate = "Decreto No. 520 (LEIV / LIE)",
            assetPath = "libros/ley_genero.pdf",
            pages = listOf(
                PdfPage(
                    pageNumber = 1,
                    headerTitle = "PROTECCIÓN DE LA MUJER Y VIOLENCIA POLÍTICA",
                    subtitle = "DERECHOS POLÍTICOS, NO DISCRIMINACIÓN Y CUOTAS DE PARTICIPACIÓN",
                    sections = listOf(
                        PdfSection(
                            title = "Violencia Política contra la Mujer",
                            content = "Art. 10 y 55.- Se sanciona toda acción o conducta dirigida a menoscabar, restringir o impedir el ejercicio de los derechos políticos de las mujeres miembros de mesas electorales, candidatas o funcionarias públicas.",
                            articleRef = "LEIV Arts. 10, 55",
                            isOfficialSeal = true
                        ),
                        PdfSection(
                            title = "Cuota de Participación en Planillas",
                            content = "Los partidos políticos deben integrar sus planillas con al menos un 30% de mujeres en cargos de elección popular de conformidad con el Art. 38 de la Ley de Partidos Políticos.",
                            articleRef = "Cuota de Género",
                            isWarning = true
                        )
                    )
                )
            )
        ),

        // 8. Reglamento General para la Observación Electoral
        PdfDocument(
            id = "reglamento_observacion_electoral",
            title = "Reglamento General para la Observación Electoral Nacional e Internacional",
            category = "Fiscalización y Vigilancia",
            summary = "Normas y procedimientos dictados por el TSE para la acreditación, derechos, deberes y código de conducta de las misiones de observación electoral nacionales e internacionales.",
            authority = "Tribunal Supremo Electoral (TSE)",
            releaseDate = "Reglamento Oficial TSE",
            assetPath = "libros/REGLAMENTO-GENERAL-PARA-LA-OBSERVACION-ELECTORAL-NACIONAL-E-INTERNACIONAL-EN-EL-SALVADOR.pdf",
            pages = listOf(
                PdfPage(
                    pageNumber = 1,
                    headerTitle = "REGLAMENTO DE OBSERVACIÓN ELECTORAL",
                    subtitle = "ACREDITACIÓN, FACULTADES Y CÓDIGO DE CONDUCTA",
                    sections = listOf(
                        PdfSection(
                            title = "Finalidad de la Observación Electoral",
                            content = "Presenciar de forma imparcial e independiente el desarrollo de todas las etapas del proceso eleccionario salvadoreño para formular recomendaciones que fortalezcan la confianza ciudadana.",
                            articleRef = "Art. 1",
                            isOfficialSeal = true
                        ),
                        PdfSection(
                            title = "Deber de Imparcialidad y No Injerencia",
                            content = "Los observadores acreditados no pueden intervenir en las decisiones de los organismos electorales (JRV, JEM, JED), no pueden sustituir a las autoridades ni manifestar preferencias político-partidarias.",
                            articleRef = "Código de Conducta",
                            isWarning = true
                        )
                    )
                )
            )
        ),

        // 9. Disposiciones Especiales para Candidaturas No Partidarias
        PdfDocument(
            id = "disposiciones_candidaturas_no_partidarias",
            title = "Disposiciones Especiales para Candidaturas No Partidarias",
            category = "Disposiciones Fundamentales",
            summary = "Normas que regulan los requisitos de inscripción, recolección de firmas ciudadanas, financiamiento y fiscalización para aspirantes independientes no afiliados a partidos políticos.",
            authority = "Asamblea Legislativa • Sentencias Sala de lo Constitucional",
            releaseDate = "Decreto Legislativo de Candidaturas Independientes",
            assetPath = "libros/Disposciones-Especiales-Candidaturas-no-partidarias.pdf",
            pages = listOf(
                PdfPage(
                    pageNumber = 1,
                    headerTitle = "CANDIDATURAS NO PARTIDARIAS (INDEPENDIENTES)",
                    subtitle = "INSCRIPCIÓN, REQUISITOS DE RESPALDO Y PAPELETAS",
                    sections = listOf(
                        PdfSection(
                            title = "Derecho a Postulación Independiente",
                            content = "En cumplimiento de la jurisprudencia constitucional, se reconoce el derecho de los ciudadanos salvadoreños a postularse a cargos de Diputaciones a la Asamblea Legislativa de forma no partidaria.",
                            articleRef = "Bases Constitucionales",
                            isOfficialSeal = true
                        ),
                        PdfSection(
                            title = "Respaldo Ciudadano y Reglas de Votación",
                            content = "Los aspirantes deben presentar las firmas de respaldo ciudadano en el porcentaje correspondiente a su circunscripción departamental y aparecen en las papeletas en el sector de candidaturas no partidarias.",
                            articleRef = "Procedimiento de Inscripción"
                        )
                    )
                )
            )
        )
    )
}
