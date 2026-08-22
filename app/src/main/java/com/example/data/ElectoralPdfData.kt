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
        "Sanciones, Delitos y Recursos",
        "Organización Territorial y Municipal"
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

        // 2. Constitución de la República de El Salvador (1983)
        PdfDocument(
            id = "constitucion_republica_1983",
            title = "Constitución de la República de El Salvador (1983)",
            category = "Disposiciones Fundamentales",
            summary = "Carta Magna y Ley Suprema de la República de El Salvador. Contiene los principios fundamentales del sistema político democrático, la soberanía ciudadana, los derechos políticos fundamentales (Arts. 71-89) y las atribuciones del Tribunal Supremo Electoral (Arts. 208-209).",
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

        // 3. Ley de Partidos Políticos (Decreto 307)
        PdfDocument(
            id = "ley_de_partidos_politicos",
            title = "Ley de Partidos Políticos (Decreto No. 307)",
            category = "Fiscalización y Vigilancia",
            summary = "Regula la constitución, organización, funcionamiento, financiamiento público y privado, democracia interna, cuotas de género y fiscalización de los partidos políticos en El Salvador.",
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
                            content = "Arts. 67 y 68.- Los partidos están obligados a registrar contablemente todos los donativos privados y aportes de militantes, manteniendo prohibidas las donaciones anónimas.",
                            articleRef = "Arts. 67-68",
                            isWarning = true
                        )
                    )
                )
            )
        ),

        // 4. Ley de Acceso a la Información Pública (LAIP)
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

        // 5. Ley Especial para el Ejercicio del Sufragio en el Extranjero (Decreto 542)
        PdfDocument(
            id = "ley_sufragio_extranjero_542",
            title = "Ley Especial para el Ejercicio del Sufragio en el Extranjero (Decreto No. 542)",
            category = "Votación y Escrutinio",
            summary = "Decreto Legislativo No. 542 que regula las modalidades de votación electrónica por internet (remota) y votación electrónica presencial en sedes diplomáticas y consulares para salvadoreños en el exterior, padrón electoral del exterior y JELVEX.",
            authority = "Asamblea Legislativa de El Salvador • Tribunal Supremo Electoral",
            releaseDate = "Decreto Legislativo No. 542 (Vigente)",
            assetPath = "libros/LEY ESPECIAL PARA EL EJERCICIO DEL SUFRAGIO EN EL EXTRANJERO.pdf",
            pages = listOf(
                PdfPage(
                    pageNumber = 1,
                    headerTitle = "LEY ESPECIAL PARA EL EJERCICIO DEL SUFRAGIO EN EL EXTRANJERO",
                    subtitle = "DECRETO No. 542 • MODALIDADES DE VOTO ELECTRÓNICO REMOTO Y PRESENCIAL",
                    sections = listOf(
                        PdfSection(
                            title = "Objeto de la Ley",
                            content = "Art. 1.- Garantizar el ejercicio del derecho a votar y a postularse a cargos de elección popular para los salvadoreños en el extranjero para elecciones presidenciales y legislativas.",
                            articleRef = "Art. 1",
                            isOfficialSeal = true
                        ),
                        PdfSection(
                            title = "Modalidades de Voto Electrónico",
                            content = "Art. 2.- Dos modalidades: 1) Voto electrónico remoto por internet (DUI con dirección en el exterior); 2) Voto electrónico presencial en consulados y centros habilitados (DUI con dirección en El Salvador o pasaporte vigente/vencido).",
                            articleRef = "Art. 2"
                        ),
                        PdfSection(
                            title = "Junta Electoral de Voto en el Extranjero (JELVEX)",
                            content = "Art. 5.- Organismo electoral temporal responsable de organizar, coordinar y fiscalizar el sufragio de los salvadoreños en el exterior y su escrutinio preliminar.",
                            articleRef = "Art. 5"
                        )
                    )
                )
            )
        ),

        // 6. Ley Especial para la Reestructuración Municipal (Decreto 763)
        PdfDocument(
            id = "ley_reestructuracion_municipal_763",
            title = "Ley Especial para la Reestructuración Municipal (Decreto No. 763)",
            category = "Organización Territorial y Municipal",
            summary = "Decreto Legislativo No. 763 que reorganiza la división político-administrativa de El Salvador en 44 Municipios y 262 Distritos Municipales, regulando los Concejos Municipales, cabeceras y circunscripciones electorales.",
            authority = "Asamblea Legislativa de El Salvador",
            releaseDate = "Decreto Legislativo No. 763 (D.O. No. 110, Tomo 439)",
            assetPath = "libros/LEY ESPECIAL PARA LA REESTRUCTURACIÓN MUNICIPAL.pdf",
            pages = listOf(
                PdfPage(
                    pageNumber = 1,
                    headerTitle = "LEY ESPECIAL PARA LA REESTRUCTURACIÓN MUNICIPAL",
                    subtitle = "44 MUNICIPIOS Y 262 DISTRITOS MUNICIPALES",
                    sections = listOf(
                        PdfSection(
                            title = "División en 44 Municipios",
                            content = "Arts. 1-2.- El territorio de la República de El Salvador se divide en 44 Municipios integrados por 262 Distritos Municipales en los 14 departamentos del país.",
                            articleRef = "Arts. 1-2",
                            isOfficialSeal = true
                        ),
                        PdfSection(
                            title = "Concejos Municipales y Elecciones",
                            content = "Arts. 3-5.- Cada municipio es gobernado por un Concejo Municipal integrado por Alcalde/sa, Síndico/a y Regidores. El TSE adecúa las circunscripciones y padrones electorales a la nueva estructura municipal.",
                            articleRef = "Arts. 3-5"
                        )
                    )
                )
            )
        ),

        // 7. Código Penal de El Salvador (Delitos Electorales)
        PdfDocument(
            id = "codigo_penal_delitos_electorales",
            title = "Código Penal de El Salvador (Delitos Electorales)",
            category = "Sanciones, Delitos y Recursos",
            summary = "Tipificación de delitos contra los derechos cívicos y electorales (Arts. 295 a 302): Fraude electoral, coacción del sufragio, falsedad documental, obstaculización, usurpación de funciones y penas de prisión de hasta 15 años.",
            authority = "Asamblea Legislativa • Órgano Judicial de El Salvador",
            releaseDate = "Código Penal Consolidado",
            assetPath = "libros/Código Penal.pdf",
            pages = listOf(
                PdfPage(
                    pageNumber = 1,
                    headerTitle = "CÓDIGO PENAL • LIBRO SEGUNDO • TÍTULO XIII",
                    subtitle = "DELITOS CONTRA LOS DERECHOS CÍVICOS Y ELECTORALES",
                    sections = listOf(
                        PdfSection(
                            title = "Fraude Electoral (Art. 295)",
                            content = "Art. 295.- Votar más de una vez, suplantar a otro elector, votar sin derecho o alterar padrones o actas. Pena: 4 a 6 años de prisión (aumenta si es miembro de organismo electoral).",
                            articleRef = "Art. 295",
                            isWarning = true
                        ),
                        PdfSection(
                            title = "Coacción del Sufragio (Art. 296)",
                            content = "Art. 296.- Impedir, amenazar o coartar el voto de un elector o forzarlo a votar en determinado sentido o abstenerse. Pena: 3 a 6 años de prisión.",
                            articleRef = "Art. 296",
                            isWarning = true
                        ),
                        PdfSection(
                            title = "Falsedad y Obstaculización (Arts. 297-298)",
                            content = "Arts. 297 y 298.- Alterar o destruir credenciales, actas, papeletas o perturbar la instalación o escrutinio de JRV. Pena: 4 a 8 años de prisión.",
                            articleRef = "Arts. 297-298",
                            isWarning = true
                        )
                    )
                )
            )
        ),

        // 8. Acuerdo Legislativo - Reforma - Supresión Segunda Elección
        PdfDocument(
            id = "acuerdo_legislativo_reforma_electoral",
            title = "Acuerdo Legislativo - Reforma y Normativa Electoral",
            category = "Fiscalización y Vigilancia",
            summary = "Acuerdo legislativo y reformas procesales relativas a los cómputos de escrutinio, mayorías absolutas, adjudicación de escaños y plazos para elecciones en la República de El Salvador.",
            authority = "Asamblea Legislativa de El Salvador",
            releaseDate = "Acuerdos Legislativos Oficiales",
            assetPath = "libros/ACUERDO LEGISLATIVO - REFORMA - SUPRECIÓN SEGUNDA ELECCIÓN.pdf",
            pages = listOf(
                PdfPage(
                    pageNumber = 1,
                    headerTitle = "ACUERDO LEGISLATIVO • REFORMAS ELECTORALES",
                    subtitle = "SISTEMA DE ESCRUTINIO, MAYORÍAS Y PLAZOS",
                    sections = listOf(
                        PdfSection(
                            title = "Reglas de Mayoría Absoluta",
                            content = "Establece las pautas para la declaratoria de elección conforme a la Constitución y la aplicación de fórmulas en escrutinio definitivo.",
                            articleRef = "Disposiciones Electorales",
                            isOfficialSeal = true
                        )
                    )
                )
            )
        ),

        // 9. Ciclo Electoral Salvadoreño
        PdfDocument(
            id = "ciclo_electoral_salvadoreno",
            title = "Ciclo Electoral Salvadoreño • Guía Doctrinal y Operativa",
            category = "Organismos Electorales (JRV, JEM, JED, TSE)",
            summary = "Guía integral del Tribunal Supremo Electoral sobre las tres macro-etapas del ciclo eleccionario salvadoreño: Pre-electoral (PLAGEL, padrón, candidaturas), Electoral (jornada y escrutinio preliminar) y Post-electoral (escrutinio final y credenciales).",
            authority = "Tribunal Supremo Electoral • Dirección de Capacitación",
            releaseDate = "Guía Metodológica TSE",
            assetPath = "libros/CICLO ELECTORAL SALVADOREÑO.pdf",
            pages = listOf(
                PdfPage(
                    pageNumber = 1,
                    headerTitle = "CICLO ELECTORAL SALVADOREÑO • FASES Y ETAPAS",
                    subtitle = "PRE-ELECTORAL, ELECTORAL Y POST-ELECTORAL",
                    sections = listOf(
                        PdfSection(
                            title = "Etapa Pre-electoral",
                            content = "Plan General de Elecciones (PLAGEL), conformación del Padrón Electoral con el RNPN, inscripción de candidatos, logística y conformación de JED, JEM y JRV.",
                            articleRef = "Fase I"
                        ),
                        PdfSection(
                            title = "Etapa Electoral y Post-electoral",
                            content = "Jornada de votación (07:00 a 17:00 hrs), escrutinio preliminar de mesa, escrutinio final ante el TSE, resolución de recursos y proclamación oficial.",
                            articleRef = "Fases II y III",
                            isOfficialSeal = true
                        )
                    )
                )
            )
        ),

        // 10. Disposiciones Especiales para Candidaturas No Partidarias
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
        ),

        // 11. Reglamento General para la Observación Electoral
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

        // 12. Instructivo Oficial para Miembros de JRV (TSE)
        PdfDocument(
            id = "instructivo_jrv_tse",
            title = "Instructivo Oficial para Juntas Receptoras de Votos (JRV)",
            category = "Organismos Electorales (JRV, JEM, JED, TSE)",
            summary = "Guía operativa oficial emitida por el Tribunal Supremo Electoral para los integrantes de las Juntas Receptoras de Votos (JRV). Describe paso a paso la instalación a las 06:00 AM, apertura a las 07:00 AM, recepción de sufragios, escrutinio preliminar a las 05:00 PM y llenado de actas.",
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
        )
    )
}
