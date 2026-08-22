package com.example.data

object ExamBankData {

    // =========================================================================
    // 25 PREGUNTAS EXISTENTES (CONSERVADAS ÍNTEGRAMENTE CON SUS METADATOS)
    // =========================================================================
    val initial25Questions: List<ExamQuestion> = listOf(
        // Pregunta 1
        ExamQuestion(
            id = "q1",
            category = "JRV e Instalación",
            questionText = "¿A qué hora deben presentarse obligatoriamente los miembros de la JRV para su instalación legal según el Código Electoral?",
            situationContext = "Conforme al Art. 190 del Código Electoral, la preparación de la mesa debe iniciar previo a la apertura ciudadana.",
            options = listOf("A las 05:00 AM", "A las 06:00 AM", "A las 06:30 AM", "A las 07:00 AM"),
            correctOptionIndex = 1,
            explanation = "El Art. 190 del Código Electoral establece que las Juntas Receptoras de Votos se instalarán a las 06:00 horas del día señalado para la elección.",
            normativeReference = "Código Electoral Art. 190",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 190",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta 2
        ExamQuestion(
            id = "q2",
            category = "JRV e Instalación",
            questionText = "¿Con qué número mínimo de miembros propietarios puede instalarse y funcionar válidamente una Junta Receptora de Votos?",
            situationContext = "En caso de inasistencia de algunos integrantes designados por el TSE.",
            options = listOf("Con 2 miembros", "Con mínimo 3 miembros", "Únicamente con los 5 miembros completos", "Con 4 miembros"),
            correctOptionIndex = 1,
            explanation = "El Art. 100 del Código Electoral señala que las Juntas Receptoras de Votos podrán instalarse y funcionar válidamente con un mínimo de tres miembros propietarios.",
            normativeReference = "Código Electoral Art. 100",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 100",
            sourcePage = 3,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta 3
        ExamQuestion(
            id = "q3",
            category = "Votación y Sufragio",
            questionText = "¿Cuál es el único documento legalmente válido para que un ciudadano vote en las elecciones nacionales en El Salvador?",
            situationContext = "Un elector se presenta a la mesa electoral solicitando emitir el sufragio.",
            options = listOf("Licencia de conducir vigente", "Pasaporte ordinario", "Documento Único de Identidad (DUI) vigente", "Carné electoral histórico"),
            correctOptionIndex = 2,
            explanation = "El Art. 31 del Código Electoral estipula que el Documento Único de Identidad (DUI) vigente es el único que acredita al ciudadano o ciudadana para emitir el voto.",
            normativeReference = "Código Electoral Art. 31",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 31",
            sourcePage = 2,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta 4: Ballot Validity
        ExamQuestion(
            id = "q4",
            category = "Calificación de Votos",
            questionText = "¿Cómo debe calificarse el siguiente voto según la legislación electoral salvadoreña?",
            situationContext = "En una papeleta de elección presidencial, el elector marcó con una cruz perfectamente clara sobre la bandera de un solo partido político contendiente.",
            type = QuestionType.BALLOT_VALIDITY,
            ballotMarkType = BallotMarkType.VALID_SINGLE_PARTY_CROSS,
            ballotVisualDescription = "Papeleta con una cruz (X) clara y delimitada sobre la bandera del Partido A, sin marcas en otras casillas.",
            options = listOf("VOTO VÁLIDO", "VOTO NULO", "VOTO IMPUGNADO", "ABSTENCIÓN"),
            correctOptionIndex = 0,
            explanation = "El Art. 205 del Código Electoral determina que es voto válido cuando la voluntad del votante esté claramente determinada por cualquier marca sobre la bandera del partido.",
            normativeReference = "Código Electoral Art. 205 lit. a",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 205 lit. a",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta 5: Ballot Validity
        ExamQuestion(
            id = "q5",
            category = "Calificación de Votos",
            questionText = "¿Cómo debe calificarse el siguiente voto según la legislación electoral salvadoreña?",
            situationContext = "El elector marcó con una cruz sobre la bandera del Partido A y también marcó con una cruz sobre la bandera del Partido B (ambos partidos NO están coaligados).",
            type = QuestionType.BALLOT_VALIDITY,
            ballotMarkType = BallotMarkType.NULL_TWO_RIVAL_PARTIES,
            ballotVisualDescription = "Papeleta con marcas simultáneas sobre banderas de dos partidos políticos rivales independientes.",
            options = listOf("VOTO VÁLIDO (se divide entre dos)", "VOTO NULO", "VOTO IMPUGNADO", "ABSTENCIÓN"),
            correctOptionIndex = 1,
            explanation = "El Art. 207 lit. a del Código Electoral establece que el voto será nulo cuando en la papeleta apareciere claramente marcada la intención de voto en dos o más banderas de partidos no coaligados.",
            normativeReference = "Código Electoral Art. 207 lit. a",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 207 lit. a",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta 6: Ballot Validity
        ExamQuestion(
            id = "q6",
            category = "Calificación de Votos",
            questionText = "¿Cómo debe calificarse una papeleta que contiene frases o dibujos obscenos e insultos?",
            situationContext = "Durante el escrutinio preliminar aparece una papeleta con insultos y dibujos grotescos sobre los candidatos.",
            type = QuestionType.BALLOT_VALIDITY,
            ballotMarkType = BallotMarkType.NULL_OBSCENE_INSULT_TEXT,
            ballotVisualDescription = "Papeleta con texto difamatorio, palabras soeces y dibujos obscenos en su anverso.",
            options = listOf("VOTO VÁLIDO", "VOTO NULO", "ABSTENCIÓN", "VOTO IMPUGNADO"),
            correctOptionIndex = 1,
            explanation = "El Art. 207 lit. h del Código Electoral establece expresamente la nulidad del voto si la papeleta contiene palabras o figuras obscenas.",
            normativeReference = "Código Electoral Art. 207 lit. h",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 207 lit. h",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta 7
        ExamQuestion(
            id = "q7",
            category = "Derechos y Permisos",
            questionText = "Según el Art. 113 del Código Electoral, ¿qué tipo de permiso laboral tienen por ley los ciudadanos nombrados para integrar una JRV?",
            situationContext = "Un empleador privado amenaza con descontar el día de salario a un empleado que asistirá a capacitarse y servir en la JRV.",
            options = listOf("Permiso sin goce de sueldo", "Permiso obligatorio con goce de sueldo durante capacitación, elección y día posterior", "Únicamente medio día de descanso", "No tienen ningún permiso garantizado"),
            correctOptionIndex = 1,
            explanation = "El Art. 113 obliga a todo empleador público o privado a conceder permiso con goce de sueldo durante la capacitación, la jornada electoral y el día hábil siguiente a la elección.",
            normativeReference = "Código Electoral Art. 113",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 113",
            sourcePage = 3,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta 8
        ExamQuestion(
            id = "q8",
            category = "Seguridad Electoral",
            questionText = "¿Quiénes son los únicos autorizados para portar armas dentro de un centro de votación en El Salvador?",
            situationContext = "Un vigilante o guardaespaldas privado desea ingresar armado al aula electoral.",
            options = listOf("Cualquier persona con matrícula y licencia de portación", "Los vigilantes de partidos políticos", "Únicamente los miembros de la Policía Nacional Civil (PNC) en servicio", "Los miembros de la JRV"),
            correctOptionIndex = 2,
            explanation = "El Art. 290 del Código Electoral prohíbe taxativamente la portación de armas de cualquier naturaleza, a excepción de los miembros de la PNC encargados del orden público.",
            normativeReference = "Código Electoral Art. 290",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 290",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta 9
        ExamQuestion(
            id = "q9",
            category = "Horarios y Procedimiento",
            questionText = "¿A qué hora concluye oficialmente la votación ciudadana según el Código Electoral salvadoreño?",
            situationContext = "Son las 17:00 horas (5:00 PM) y el Presidente de la JRV debe ordenar el cierre.",
            options = listOf("A las 16:00 horas (4:00 PM)", "A las 17:00 horas (5:00 PM)", "A las 18:00 horas (6:00 PM)", "Cuando se agote el padrón"),
            correctOptionIndex = 1,
            explanation = "El Art. 198 del Código Electoral establece que la votación será continua y terminará a las diecisiete horas (5:00 PM).",
            normativeReference = "Código Electoral Art. 198",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 198",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta 10: Ballot Validity
        ExamQuestion(
            id = "q10",
            category = "Calificación de Votos",
            questionText = "¿Cómo se califica el voto si el elector marca dos banderas que conforman una COALICIÓN legalmente inscrita?",
            situationContext = "El Partido X y el Partido Y van en coalición oficial. El elector marcó ambas banderas en la misma papeleta.",
            type = QuestionType.BALLOT_VALIDITY,
            ballotMarkType = BallotMarkType.VALID_COALITION_MARK,
            ballotVisualDescription = "Papeleta con dos banderas marcadas correspondientes a una misma coalición legal inscrita ante el TSE.",
            options = listOf("VOTO NULO", "VOTO VÁLIDO para la coalición", "VOTO IMPUGNADO", "ABSTENCIÓN"),
            correctOptionIndex = 1,
            explanation = "El Art. 207 inc. final del Código Electoral dispone expresamente que NO será nulo el voto cuando se hayan marcado dos o más banderas de partidos en coalición legal inscrita.",
            normativeReference = "Código Electoral Art. 207 inc. final",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 207 inc. final",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta 11
        ExamQuestion(
            id = "q11",
            category = "Escrutinio y Documentación",
            questionText = "¿Qué debe hacerse primero al cerrar la votación a las 5:00 PM antes de abrir la urna de votos?",
            situationContext = "La JRV inicia las operaciones preliminares del escrutinio (Art. 200).",
            options = listOf("Abrir la urna de inmediato", "Contar e inutilizar las papeletas sobrantes y las inutilizadas y guardarlas", "Firmar las actas en blanco", "Ingresar datos a la computadora"),
            correctOptionIndex = 1,
            explanation = "El Art. 200 lit. a del Código Electoral ordena que lo primero es contar las papeletas sobrantes e inutilizadas, consignar el número en el acta, inutilizarlas, empaquetarlas y guardarlas.",
            normativeReference = "Código Electoral Art. 200 lit. a",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 200 lit. a",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta 12
        ExamQuestion(
            id = "q12",
            category = "Seguridad y Sanciones",
            questionText = "¿Qué período comprende la prohibición de venta y consumo de bebidas embriagantes (Ley Seca)?",
            situationContext = "Aplicación del Art. 284 del Código Electoral para eventos comiciales.",
            options = listOf("Solo el domingo de votación", "El día anterior a la elección, el de la votación y el día siguiente", "Una semana antes", "Desde el viernes hasta el lunes"),
            correctOptionIndex = 1,
            explanation = "El Art. 284 del Código Electoral prohíbe la venta, distribución y consumo de bebidas embriagantes el día anterior a la elección, el de la votación y el siguiente.",
            normativeReference = "Código Electoral Art. 284",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 284",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta 13
        ExamQuestion(
            id = "q13",
            category = "Fiscalización Partidaria",
            questionText = "¿Qué derecho tienen los vigilantes de partidos políticos acreditados ante una JRV?",
            situationContext = "Un vigilante exige manipular directamente las papeletas de la urna.",
            options = listOf("Voto en las decisiones de la mesa y conteo de votos", "Derecho únicamente a voz y fiscalización visual, sin manipular papeletas ni urnas", "Mando sobre la Policía Nacional Civil", "Llenado de las actas oficiales"),
            correctOptionIndex = 1,
            explanation = "Los Arts. 127 y 128 señalan que los vigilantes participan con derecho únicamente a voz y fiscalización, siendo potestad exclusiva de la JRV la conducción del acto.",
            normativeReference = "Código Electoral Arts. 127 y 128",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 127 y 128",
            sourcePage = 3,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta 14
        ExamQuestion(
            id = "q14",
            category = "Protección de la Mujer (LEIV)",
            questionText = "¿Qué constituye la violencia política contra las mujeres según el Art. 55 de la LEIV?",
            situationContext = "Acciones u omisiones basadas en el género dirigidas a menoscabar o anular los derechos políticos de las mujeres.",
            options = listOf("Un debate acalorado de propuestas", "Toda acción o conducta tendiente a menoscabar, anular o restringir los derechos políticos de las mujeres en el ejercicio de cargos o candidaturas", "La propaganda electoral tradicional", "El cambio regular de integrantes"),
            correctOptionIndex = 1,
            explanation = "El Art. 55 de la Ley Especial Integral para una Vida Libre de Violencia para las Mujeres sanciona cualquier acción u omisión que limite o vulnere los derechos políticos de las mujeres.",
            normativeReference = "LEIV Art. 55",
            sourceDocument = "Ley Especial Integral para una Vida Libre de Violencia / Ley de Género",
            sourceArticle = "Art. 55",
            sourcePage = 1,
            sourceDocumentId = "ley_genero_electoral"
        ),
        // Pregunta 15
        ExamQuestion(
            id = "q15",
            category = "Organismos Electorales",
            questionText = "¿Cuál es la jerarquía correcta de los organismos electorales temporales en El Salvador?",
            situationContext = "Estructura establecida en el Art. 38 del Código Electoral.",
            options = listOf("JRV -> JEM -> JED -> TSE", "TSE -> JED -> JEM -> JRV", "JED -> TSE -> JRV -> JEM", "Alcaldías -> JEM -> JRV"),
            correctOptionIndex = 1,
            explanation = "El Art. 38 del Código Electoral establece la jerarquía descendente: a) TSE (permanente), b) JED (departamental), c) JEM (municipal) y d) JRV (mesas receptoras).",
            normativeReference = "Código Electoral Art. 38",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 38",
            sourcePage = 3,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta 16: Ballot Validity
        ExamQuestion(
            id = "q16",
            category = "Calificación de Votos",
            questionText = "¿Cómo se califica una papeleta introducida en la urna totalmente en blanco (sin ninguna marca)?",
            situationContext = "Escrutinio preliminar de una urna con papeletas depositadas sin señal.",
            type = QuestionType.BALLOT_VALIDITY,
            ballotMarkType = BallotMarkType.NULL_BLANK_BALLOT_WRITTEN,
            ballotVisualDescription = "Papeleta electoral limpia y sin ninguna marca sobre banderas ni nombres.",
            options = listOf("VOTO VÁLIDO para el partido oficialista", "VOTO EN BLANCO (se consigna en su casilla específica del acta)", "VOTO NULO", "VOTO IMPUGNADO"),
            correctOptionIndex = 1,
            explanation = "El Art. 203 del Código Electoral define el voto en blanco como aquel en el que no aparece ninguna marca en la papeleta, consignándose en el rubro de votos en blanco del acta.",
            normativeReference = "Código Electoral Art. 203",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 203",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta 17
        ExamQuestion(
            id = "q17",
            category = "Procedimiento de Votación",
            questionText = "¿En qué momento se le debe aplicar la tinta indeleble al votante?",
            situationContext = "Procedimiento regular de la mesa de votación conforme al Art. 197.",
            options = listOf("Al llegar al centro escolar", "Antes de recibir las papeletas de votación", "Inmediatamente después de haber depositado el voto en la urna y firmado el padrón", "Al salir a la calle"),
            correctOptionIndex = 2,
            explanation = "El Art. 197 inc. final manda que una vez emitido el sufragio y firmado el padrón, el Vocal aplicará la tinta indeleble en el dedo del ciudadano y se le devolverá el DUI.",
            normativeReference = "Código Electoral Art. 197 inc. final",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 197 inc. final",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta 18
        ExamQuestion(
            id = "q18",
            category = "Sanciones Electorales",
            questionText = "¿Qué consecuencia legal tiene para un ciudadano no concurrir a desempeñar el cargo en JRV habiendo sido legalmente notificado y sin excusa legal comprobada?",
            situationContext = "Inasistencia injustificada de miembros seleccionados por el TSE.",
            options = listOf("Ninguna, el cargo es totalmente opcional", "Multa administrativa impuesta por el TSE y restricciones administrativas temporales", "Detención policial inmediata el lunes", "Inhabilitación de por vida"),
            correctOptionIndex = 1,
            explanation = "El Art. 242 del Código Electoral establece multa a quienes sin causa legal no concurran a desempeñar los cargos para los que fueron designados en organismos electorales.",
            normativeReference = "Código Electoral Art. 242",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 242",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta 19
        ExamQuestion(
            id = "q19",
            category = "Escrutinio y Calificación",
            questionText = "¿Quién tiene la facultad legal exclusiva de calificar los votos durante el escrutinio de la JRV?",
            situationContext = "Discusión en mesa entre miembros y vigilantes de partidos políticos.",
            options = listOf("Los vigilantes de los partidos políticos", "El delegado de la PNC", "Los miembros propietarios de la JRV por mayoría de votos", "El observador de la OEA"),
            correctOptionIndex = 2,
            explanation = "El Art. 201 del Código Electoral confiere a los integrantes de la JRV la potestad exclusiva de calificar cada voto emitido en la mesa.",
            normativeReference = "Código Electoral Art. 201",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 201",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta 20: Ballot Validity
        ExamQuestion(
            id = "q20",
            category = "Calificación de Votos",
            questionText = "¿Cómo se califica el voto si la marca del votante sobrepasa ligeramente el marco de la bandera pero sin tocar ninguna otra bandera vecina?",
            situationContext = "El elector marcó con una 'X' grande sobre la bandera del partido pero una esquina del trazo sale unos milímetros fuera del recuadro.",
            type = QuestionType.BALLOT_VALIDITY,
            ballotMarkType = BallotMarkType.VALID_SLIGHT_OVERFLOW_CROSS,
            ballotVisualDescription = "Papeleta con trazo principal en la bandera deseada, sobresaliendo ligeramente al margen blanco sin invadir banderas colindantes.",
            options = listOf("VOTO NULO", "VOTO VÁLIDO (prevalece la intención indudable del elector)", "VOTO IMPUGNADO", "ABSTENCIÓN"),
            correctOptionIndex = 1,
            explanation = "El Art. 205 inc. final del Código Electoral establece que prevalece la intención del elector si la marca determina claramente su preferencia sin invadir otra opción partidaria.",
            normativeReference = "Código Electoral Art. 205",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 205 inc. final",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta 21
        ExamQuestion(
            id = "q21",
            category = "Voto Asistido",
            questionText = "¿En qué caso se permite el voto asistido para un ciudadano?",
            situationContext = "Un elector no vidente o con discapacidad motriz severa se presenta a votar a la JRV.",
            options = listOf("En ningún caso, el voto es estrictamente individual sin excepciones", "Cuando el elector tenga discapacidad física, motriz o visual comprobada que le impida marcar por sí mismo, acompañado por persona de su confianza", "Para cualquier persona que lo solicite por prisa", "Solo si el Presidente de la JRV marca la papeleta"),
            correctOptionIndex = 1,
            explanation = "El Art. 195 inc. 3° del Código Electoral autoriza el voto asistido por una persona de su estricta confianza a electores no videntes o con discapacidad física evidente.",
            normativeReference = "Código Electoral Art. 195 inc. 3°",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 195 inc. 3°",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta 22
        ExamQuestion(
            id = "q22",
            category = "Funciones del Secretario",
            questionText = "¿Quién es la única persona encargada de firmar y sellar la papeleta de votación al reverso antes de entregarla al ciudadano?",
            situationContext = "Art. 196 del Código Electoral respecto a las formalidades esenciales de la papeleta.",
            options = listOf("El Presidente de JRV", "El Secretario de la JRV", "El 1er Vocal", "El Vigilante del partido que obtuvo más votos"),
            correctOptionIndex = 1,
            explanation = "El Art. 196 inc. 2° del Código Electoral establece que el Secretario de la JRV deberá firmar y sellar la papeleta de votación y desprender la esquina correlativa.",
            normativeReference = "Código Electoral Art. 196",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 196 inc. 2°",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta 23
        ExamQuestion(
            id = "q23",
            category = "Propaganda Electoral",
            questionText = "¿Qué prohíbe el Art. 177 del Código Electoral a los miembros propietarios y suplentes de las JRV el día de la elección?",
            situationContext = "Un miembro de mesa viste una camiseta con los colores y logos de su partido político.",
            options = listOf("Almorzar en el centro de votación", "Portar cualquier clase de símbolo o distintivo alusivo a cualquier partido político o coalición", "Hablar con los vigilantes", "Usar teléfono celular"),
            correctOptionIndex = 1,
            explanation = "El Art. 177 prohíbe terminantemente a los miembros de JRV portar cualquier símbolo o distintivo partidario en los centros de votación el día de la elección.",
            normativeReference = "Código Electoral Art. 177",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 177",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta 24: Ballot Validity
        ExamQuestion(
            id = "q24",
            category = "Calificación de Votos",
            questionText = "¿Cómo se califica el voto en elecciones legislativas si el elector marca una bandera partidaria y además marca rostros de candidatos de ese mismo partido?",
            situationContext = "El elector marcó la bandera del Partido A y dos rostros de candidatos a diputados de la lista de ese mismo Partido A.",
            type = QuestionType.BALLOT_VALIDITY,
            ballotMarkType = BallotMarkType.VALID_PREFERENTIAL_CROSS_CANDIDATES,
            ballotVisualDescription = "Papeleta legislativa con marca en la bandera del Partido A y marcas preferenciales sobre 2 diputados del mismo Partido A.",
            options = listOf("VOTO NULO por doble marca", "VOTO VÁLIDO para el partido con marcas de preferencia para dichos candidatos", "VOTO IMPUGNADO", "ABSTENCIÓN"),
            correctOptionIndex = 1,
            explanation = "El Art. 185 lit. b.ii y Art. 205 lit. b y d del Código Electoral validan el voto entero para el partido y asignan las preferencias a los candidatos marcados.",
            normativeReference = "Código Electoral Arts. 185 y 205",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 185 y 205",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta 25
        ExamQuestion(
            id = "q25",
            category = "Entrega y Clausura",
            questionText = "¿A quién entrega personalmente la JRV los paquetes y las actas de escrutinio al finalizar el conteo?",
            situationContext = "Cierre de la jornada electoral conforme al Art. 210 del Código Electoral.",
            options = listOf("A los vigilantes de partidos para que se los lleven a su sede", "A la Junta Electoral Municipal (JEM) mediante acta por duplicado", "A la Alcaldía Municipal", "A la Fiscalía General"),
            correctOptionIndex = 1,
            explanation = "El Art. 210 del Código Electoral manda que las papeletas y actas se empacan y entregan personalmente por la JRV a la Junta Electoral Municipal (JEM) con acta por duplicado.",
            normativeReference = "Código Electoral Art. 210",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 210",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        )
    )

    // =========================================================================
    // 100 NUEVAS PREGUNTAS DEL BANCO JSON INTEGRADO (PREGUNTAS 1 A 100)
    // =========================================================================
    val json100Questions: List<ExamQuestion> = listOf(
        // Pregunta JSON 1
        ExamQuestion(
            id = "json_q1",
            category = "Miembros de la JRV",
            questionText = "¿Cuántos miembros propietarios integran legalmente una Junta Receptora de Votos (JRV)?",
            options = listOf("3 miembros propietarios.", "5 miembros propietarios.", "4 miembros propietarios.", "6 miembros propietarios."),
            correctOptionIndex = 2,
            explanation = "Según el Código Electoral de El Salvador, las JRV están compuestas por cuatro miembros propietarios: Presidente, Secretario, Primer Vocal y Segundo Vocal (o hasta 5 según decretos del TSE).",
            normativeReference = "Código Electoral Arts. 99 y 100",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 99-100",
            sourcePage = 3,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 2
        ExamQuestion(
            id = "json_q2",
            category = "Miembros de la JRV",
            questionText = "¿Quién es la máxima autoridad dentro de la JRV y se encarga del resguardo del paquete electoral?",
            options = listOf("El Secretario de la JRV.", "El Primer Vocal.", "El Presidente de la JRV.", "El Vigilante del partido oficialista."),
            correctOptionIndex = 2,
            explanation = "El Presidente de la JRV es la máxima autoridad de la mesa, coordina las funciones, mantiene el orden y es responsable de la custodia del paquete electoral.",
            normativeReference = "Código Electoral Arts. 102 y 191",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 102, 191",
            sourcePage = 3,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 3
        ExamQuestion(
            id = "json_q3",
            category = "Miembros de la JRV",
            questionText = "¿Cuál es el rol principal del Secretario de la JRV durante la recepción de votos?",
            options = listOf("Vigilar la puerta de entrada.", "Contar el dinero de viáticos.", "Gestionar los libros, padrón electoral y actas, firmando las papeletas al reverso.", "Aplicar la tinta indeleble al votante."),
            correctOptionIndex = 2,
            explanation = "El Secretario es responsable de llevar el control del padrón, firmar las papeletas al reverso en el espacio asignado y redactar las actas de instalación, cierre y escrutinio.",
            normativeReference = "Código Electoral Arts. 103 y 196",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 103, 196",
            sourcePage = 3,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 4
        ExamQuestion(
            id = "json_q4",
            category = "Miembros de la JRV",
            questionText = "¿A qué hora deben presentarse los miembros propietarios de la JRV para la fase de instalación?",
            options = listOf("A las 7:00 AM.", "A las 6:00 AM.", "A las 5:00 AM.", "A las 6:30 AM."),
            correctOptionIndex = 1,
            explanation = "El Código Electoral establece las 6:00 AM como la hora de convocatoria obligatoria para la instalación de las JRV y revisión del paquete electoral.",
            normativeReference = "Código Electoral Art. 190",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 190",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 5
        ExamQuestion(
            id = "json_q5",
            category = "Miembros de la JRV",
            questionText = "Si el Presidente propietario no se presenta a las 6:15 AM, ¿qué acción corresponde realizar?",
            options = listOf("Suspender la votación en esa mesa.", "Asumir las funciones el Secretario e incorporar al suplente correspondiente.", "Esperar hasta las 9:00 AM.", "Cerrar el centro de votación."),
            correctOptionIndex = 1,
            explanation = "Ante la ausencia del Presidente a la hora de instalación, el Secretario asume temporalmente la presidencia y se llama al suplente respectivo para completar la mesa.",
            normativeReference = "Instructivo para Miembros de JRV (TSE)",
            sourceDocument = "Instructivo para Miembros de JRV (TSE)",
            sourceArticle = "Fase de Instalación y Prelación de Cargos",
            sourcePage = 1,
            sourceDocumentId = "instructivo_jrv_tse"
        ),
        // Pregunta JSON 6
        ExamQuestion(
            id = "json_q6",
            category = "Miembros de la JRV",
            questionText = "¿Cuál es la función específica del Vocal encargado del entintado durante la votación?",
            options = listOf("Revisar el DUI del ciudadano.", "Revisar que el dedo meñique (o índice) no tenga tinta residual y aplicar la tinta indeleble tras emitir el voto.", "Entregar la papeleta.", "Doblar la papeleta."),
            correctOptionIndex = 1,
            explanation = "El Vocal asignado debe verificar previamente que el votante no tenga restos de tinta y aplicarla correctamente tras el depósito del voto para evitar doble sufragio.",
            normativeReference = "Código Electoral Art. 197 inc. final",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 197 inc. final",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 7
        ExamQuestion(
            id = "json_q7",
            category = "Miembros de la JRV",
            questionText = "¿Qué documento acredita formalmente a un ciudadano para desempeñarse como miembro de JRV?",
            options = listOf("El carné de afiliación a un partido.", "La credencial oficial emitida por el Tribunal Supremo Electoral (TSE).", "Una carta firmada por el alcalde municipal.", "El recibo de pago de viáticos."),
            correctOptionIndex = 1,
            explanation = "Solo la credencial oficial extendida y sellada por el TSE confiere la facultad legal para actuar como integrante de la JRV el día comicial.",
            normativeReference = "Código Electoral Arts. 101 y 191",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 101, 191",
            sourcePage = 3,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 8
        ExamQuestion(
            id = "json_q8",
            category = "Miembros de la JRV",
            questionText = "¿Qué papel desempeñan los suplentes de la JRV el día de las elecciones?",
            options = listOf("No asisten a menos que se les llame por teléfono.", "Deben presentarse a las 6:00 AM para sustituir a cualquier propietario ausente y asumir su cargo en propiedad si no asiste.", "Solo van a almorzar.", "Se encargan del conteo externo de votos."),
            correctOptionIndex = 1,
            explanation = "Los miembros suplentes están legalmente obligados a presentarse a la hora de instalación (6:00 AM) para sustituir a los propietarios ausentes.",
            normativeReference = "Código Electoral Arts. 100 y 190",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 100, 190",
            sourcePage = 3,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 9
        ExamQuestion(
            id = "json_q9",
            category = "Miembros de la JRV",
            questionText = "¿Qué ocurre si un miembro de JRV se abandona injustificadamente de la mesa antes del escrutinio?",
            options = listOf("No pasa nada.", "Incurre en infracción al Código Electoral, se llama al suplente y se deja constancia en el acta.", "Se anulan todos los votos de la mesa.", "La mesa se clausura de inmediato."),
            correctOptionIndex = 1,
            explanation = "El abandono de funciones constituye una falta sancionable por el TSE; la JRV debe asentar el incidente en el acta y llamar inmediatamente al suplente.",
            normativeReference = "Código Electoral Art. 242",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 242",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 10
        ExamQuestion(
            id = "json_q10",
            category = "Miembros de la JRV",
            questionText = "¿Qué autoridad tiene el Presidente de la JRV si un vigilante de partido interrumpe sistemáticamente el proceso?",
            options = listOf("Golpearlo físicamente.", "Amonestarlo y, si persiste la alteración grave, solicitar su retiro del aula mediante la fuerza pública (PNC).", "Renunciar a la mesa.", "Permitirle hacer lo que desee."),
            correctOptionIndex = 1,
            explanation = "El Presidente de JRV ostenta el poder de policía en la mesa electoral y puede recurrir a la PNC asignada para mantener el orden si hay interferencias graves.",
            normativeReference = "Código Electoral Arts. 102 y 128",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 102, 128",
            sourcePage = 3,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 11
        ExamQuestion(
            id = "json_q11",
            category = "Miembros de la JRV",
            questionText = "¿Pueden los miembros de la JRV vestir ropa con banderas o símbolos partidarios?",
            options = listOf("Sí, es su derecho constitucional.", "No, está estrictamente prohibido usar simbología o propaganda partidaria durante el ejercicio de sus funciones.", "Solo si es del partido ganador.", "Solo si el Presidente lo autoriza."),
            correctOptionIndex = 1,
            explanation = "El Art. 177 del Código Electoral prohíbe taxativamente a los miembros de organismos electorales portar distintivos partidarios el día de los comicios.",
            normativeReference = "Código Electoral Art. 177",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 177",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 12
        ExamQuestion(
            id = "json_q12",
            category = "Miembros de la JRV",
            questionText = "¿Cuál es la responsabilidad del Vocal encargado de la entrega de papeletas?",
            options = listOf("Marcar la opción por el elector.", "Entregar la papeleta firmada/sellada al elector explicándole brevemente la forma correcta de doblarla.", "Retener el DUI.", "Vigilar el parqueo del centro."),
            correctOptionIndex = 1,
            explanation = "El Vocal entrega la papeleta oficial al ciudadano, verificando que tenga la firma del Secretario y orientando al elector sobre el secreto del voto.",
            normativeReference = "Instructivo para Miembros de JRV (TSE)",
            sourceDocument = "Instructivo para Miembros de JRV (TSE)",
            sourceArticle = "Procedimiento de Mesa / Entrega de Papeletas",
            sourcePage = 2,
            sourceDocumentId = "instructivo_jrv_tse"
        ),
        // Pregunta JSON 13
        ExamQuestion(
            id = "json_q13",
            category = "Miembros de la JRV",
            questionText = "¿Quiénes firman las actas de instalación, cierre y escrutinio preliminar?",
            options = listOf("Solo el Presidente.", "Todos los miembros propietarios presentes de la JRV y opcionalmente los vigilantes acreditados.", "Únicamente el delegado del TSE.", "Los votantes de la fila."),
            correctOptionIndex = 1,
            explanation = "Las actas deben ser suscritas por todos los miembros de la JRV y pueden ser firmadas por los vigilantes de partidos políticos que así lo deseen.",
            normativeReference = "Código Electoral Arts. 190, 198 y 202",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 190, 198, 202",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 14
        ExamQuestion(
            id = "json_q14",
            category = "Miembros de la JRV",
            questionText = "¿Qué sucede si un miembro propietario se niega a firmar el acta de escrutinio por desacuerdo?",
            options = listOf("El acta se anula automáticamente.", "Debe firmar con reserva o consignar sus observaciones en el apartado de incidentes del acta.", "Se repite la votación completa.", "Es arrestado de inmediato."),
            correctOptionIndex = 1,
            explanation = "La negativa a firmar no invalida el acta; el miembro disconforme tiene derecho a asentar sus objeciones y fundamentos en el espacio de incidentes.",
            normativeReference = "Código Electoral Art. 202 inc. final",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 202 inc. final",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 15
        ExamQuestion(
            id = "json_q15",
            category = "Miembros de la JRV",
            questionText = "¿Quién entrega las copias de las actas de escrutinio a los partidos políticos y JED?",
            options = listOf("El Presidente y el Secretario de la JRV.", "El conserje de la escuela.", "La Policía Nacional Civil.", "Los observadores extranjeros."),
            correctOptionIndex = 0,
            explanation = "Las autoridades de la JRV (Presidente y Secretario) son las encargadas de desglosar y entregar las copias oficiales certificadas a los vigilantes acreditados.",
            normativeReference = "Código Electoral Arts. 209 y 210",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 209-210",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 16
        ExamQuestion(
            id = "json_q16",
            category = "Miembros de la JRV",
            questionText = "¿Qué requisito de instrucción exige el TSE para ser miembro capacitado de JRV?",
            options = listOf("Tener título universitario obligatorio.", "Haber completado y aprobado las jornadas de capacitación y evaluación impartidas por el TSE.", "Pertenecer a una directiva partidaria.", "Saber manejar computadora."),
            correctOptionIndex = 1,
            explanation = "El TSE exige la acreditación de asistencia y aprobación a los cursos de formación electoral oficiales para habilitar la credencial definitiva.",
            normativeReference = "Código Electoral Art. 101",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 101",
            sourcePage = 3,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 17
        ExamQuestion(
            id = "json_q17",
            category = "Miembros de la JRV",
            questionText = "Si faltan dos miembros propietarios a las 6:20 AM y solo hay un suplente, ¿cómo se completa la JRV?",
            options = listOf("Se busca en el listado de suplentes de las JRV contiguas o acreditados presentes en el centro designados por la JEM.", "Se pide a cualquier persona en la fila sin credencial.", "Se cierra la mesa.", "Se rifa entre los vigilantes."),
            correctOptionIndex = 0,
            explanation = "La Junta Electoral Municipal (JEM) o el delegado del TSE reubica suplentes acreditados de otras mesas del mismo centro para garantizar la instalación oportuna.",
            normativeReference = "Instructivo para Miembros de JRV (TSE)",
            sourceDocument = "Instructivo para Miembros de JRV (TSE)",
            sourceArticle = "Procedimiento de Integración por Ausencia (JEM)",
            sourcePage = 1,
            sourceDocumentId = "instructivo_jrv_tse"
        ),
        // Pregunta JSON 18
        ExamQuestion(
            id = "json_q18",
            category = "Miembros de la JRV",
            questionText = "¿A quién corresponde verificar el estado de los sellos de seguridad del paquete electoral al recibirlo?",
            options = listOf("Únicamente al motorista del TSE.", "A todos los integrantes de la JRV en presencia de los observadores y fiscalía.", "Al vigilante del partido en gobierno.", "A nadie."),
            correctOptionIndex = 1,
            explanation = "Al momento de la entrega a las 6:00 AM, la JRV en pleno debe constatar que los precintos y sellos de seguridad de la valija no hayan sido vulnerados.",
            normativeReference = "Código Electoral Arts. 190 y 192",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 190, 192",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 19
        ExamQuestion(
            id = "json_q19",
            category = "Miembros de la JRV",
            questionText = "¿Qué rol desempeña la Fiscalía General de la República (FGR) en relación a los miembros de JRV?",
            options = listOf("Dirigir el conteo de votos.", "Fiscalizar la legalidad del proceso y verificar que los miembros cumplan strictly el Código Electoral.", "Elegir al Presidente de la mesa.", "Votar dos veces."),
            correctOptionIndex = 1,
            explanation = "Los fiscales electorales de la FGR garantizan la legalidad del evento comicial y pueden levantar actas ante la presunción de delitos electorales.",
            normativeReference = "Código Electoral Arts. 132 y 133",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 132-133",
            sourcePage = 3,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 20
        ExamQuestion(
            id = "json_q20",
            category = "Miembros de la JRV",
            questionText = "¿Qué sanción aplica a los ciudadanos nombrados miembros de JRV que no se presenten a cumplir su deber cívico sin justa causa?",
            options = listOf("Pena de muerte.", "Multa económica impuesta por el TSE e impedimento temporal para ciertos trámites administrativos.", "Expulsión del país.", "Ninguna sanción."),
            correctOptionIndex = 1,
            explanation = "El Código Electoral estipula multas coercitivas que van desde \$25 hasta \$114.28 (o según actualización) y restricciones para tramitar antecedentes o renovar DUI hasta solventar la multa.",
            normativeReference = "Código Electoral Art. 242",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 242",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 21
        ExamQuestion(
            id = "json_q21",
            category = "Código Electoral",
            questionText = "Según la Constitución de El Salvador, ¿cuáles son las características fundamentales del voto?",
            options = listOf("Público, obligatorio, delegado y transferible.", "Libre, directo, igualitario y secreto.", "Condicionado, secreto, censitario y partidario.", "Voluntario solo para empleados públicos."),
            correctOptionIndex = 1,
            explanation = "El Art. 78 de la Constitución y el Art. 3 del Código Electoral establecen que el sufragio es un derecho y un deber cívico, siendo libre, directo, igualitario y secreto.",
            normativeReference = "Constitución de la República Art. 78 / Código Electoral Art. 3",
            sourceDocument = "Constitución de la República de El Salvador (1983)",
            sourceArticle = "Art. 78",
            sourcePage = 1,
            sourceDocumentId = "constitucion_republica_1983"
        ),
        // Pregunta JSON 22
        ExamQuestion(
            id = "json_q22",
            category = "Código Electoral",
            questionText = "¿Qué artículo constitucional prohíbe la reelección presidencial inmediata y el continuismo?",
            options = listOf("Art. 152 y artículos de alternabilidad de la Presidencia de la República.", "Art. 1.", "Art. 200.", "Art. 300."),
            correctOptionIndex = 0,
            explanation = "Diversos artículos de la Constitución (75, 88, 152, 239) consagran el principio de alternabilidad y los límites a la reelección consecutiva.",
            normativeReference = "Constitución de la República Arts. 75, 88, 152 y 239",
            sourceDocument = "Constitución de la República de El Salvador (1983)",
            sourceArticle = "Arts. 75, 88, 152, 239",
            sourcePage = 1,
            sourceDocumentId = "constitucion_republica_1983"
        ),
        // Pregunta JSON 23
        ExamQuestion(
            id = "json_q23",
            category = "Código Electoral",
            questionText = "¿Qué ciudadanías pierden o tienen suspendido el derecho al ejercicio del sufragio?",
            options = listOf("Quienes no paguen impuestos municipales.", "Aquellos contra quienes se dicte auto de prisión formal o condena penal ejecutoriada.", "Quienes vivan fuera de la capital.", "Los mayores de 70 años."),
            correctOptionIndex = 1,
            explanation = "El Art. 75 de la Constitución y Art. 7 del Código Electoral establecen la suspensión de derechos ciudadanos por auto de prisión formal, enajenación mental o sentencia judicial firme.",
            normativeReference = "Constitución de la República Art. 75 / Código Electoral Art. 7",
            sourceDocument = "Constitución de la República de El Salvador (1983)",
            sourceArticle = "Art. 75",
            sourcePage = 1,
            sourceDocumentId = "constitucion_republica_1983"
        ),
        // Pregunta JSON 24
        ExamQuestion(
            id = "json_q24",
            category = "Código Electoral",
            questionText = "¿Cuál es el organismo máximo con jurisdicción en materia electoral en El Salvador?",
            options = listOf("La Corte Suprema de Justicia.", "El Tribunal Supremo Electoral (TSE).", "El Ministerio de Gobernación.", "La Asamblea Legislativa."),
            correctOptionIndex = 1,
            explanation = "El Art. 208 de la Constitución de la República consagra al Tribunal Supremo Electoral como la máxima autoridad jurisdiccional y administrativa en materia comicial.",
            normativeReference = "Constitución de la República Art. 208 / Código Electoral Art. 38",
            sourceDocument = "Constitución de la República de El Salvador (1983)",
            sourceArticle = "Art. 208",
            sourcePage = 2,
            sourceDocumentId = "constitucion_republica_1983"
        ),
        // Pregunta JSON 25
        ExamQuestion(
            id = "json_q25",
            category = "Código Electoral",
            questionText = "¿Qué es el Padrón Electoral?",
            options = listOf("Una lista de candidatos inscritos.", "La nómina oficial de ciudadanos inscritos habilitados para ejercer el voto en cada JRV.", "El registro de gastos de los partidos.", "El inventario de urnas de plástico."),
            correctOptionIndex = 1,
            explanation = "El Padrón Electoral es el registro público elaborado por el TSE que contiene los datos y fotografías de los ciudadanos aptos para votar en cada circunscripción.",
            normativeReference = "Código Electoral Arts. 18 y 19",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 18-19",
            sourcePage = 2,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 26
        ExamQuestion(
            id = "json_q26",
            category = "Código Electoral",
            questionText = "¿Cuál es la función de las Juntas Electorales Departamentales (JED)?",
            options = listOf("Custodiar las fronteras.", "Supervisar e instituir el proceso electoral en sus respectivas circunscripciones departamentales.", "Elegir a los diputados directamente.", "Imprimir las papeletas."),
            correctOptionIndex = 1,
            explanation = "Las JED son organismos temporales intermedios que coordinan y supervisan a las JEM de su departamento bajo la dirección del TSE.",
            normativeReference = "Código Electoral Arts. 88 y 91",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 88, 91",
            sourcePage = 3,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 27
        ExamQuestion(
            id = "json_q27",
            category = "Código Electoral",
            questionText = "¿En qué consiste el principio de 'sufragio activo'?",
            options = listOf("Postularse a la presidencia.", "El derecho y deber de todo ciudadano habilitado para emitir su voto.", "Hacer campaña en redes sociales.", "Fiscalizar las urnas."),
            correctOptionIndex = 1,
            explanation = "El sufragio activo se refiere al derecho de los ciudadanos a votar (elegir), mientras que el pasivo es el derecho a ser votado (ser electo).",
            normativeReference = "Constitución de la República Arts. 72 y 73",
            sourceDocument = "Constitución de la República de El Salvador (1983)",
            sourceArticle = "Arts. 72-73",
            sourcePage = 1,
            sourceDocumentId = "constitucion_republica_1983"
        ),
        // Pregunta JSON 28
        ExamQuestion(
            id = "json_q28",
            category = "Código Electoral",
            questionText = "¿Qué establece el Código Electoral sobre la compra y venta de votos?",
            options = listOf("Es una práctica legal aceptada.", "Es un delito electoral grave tipificado que acarrea pena de prisión y pérdida de derechos políticos.", "Solo se sanciona si supera los \$100.", "Se resuelve con una disculpa pública."),
            correctOptionIndex = 1,
            explanation = "La compra, coacción o inducción ilegal del voto está severamente penada en el Código Penal y Código Electoral salvadoreño con prisión de 4 a 6 años.",
            normativeReference = "Código Electoral Art. 7 lit. g y Art. 250",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 7 lit. g, Art. 250",
            sourcePage = 2,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 29
        ExamQuestion(
            id = "json_q29",
            category = "Código Electoral",
            questionText = "¿Qué norma prohíbe a los funcionarios públicos utilizar bienes del Estado para campañas políticas?",
            options = listOf("La Ley de Tránsito.", "El Código Electoral y la Ley de Ética Gubernamental.", "El Reglamento de Deportes.", "No existe prohibición legal."),
            correctOptionIndex = 1,
            explanation = "El Art. 184 del Código Electoral prohíbe el uso de vehículos, edificios, fondos públicos y recursos estatales para fines de proselitismo partidario.",
            normativeReference = "Código Electoral Art. 184",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 184",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 30
        ExamQuestion(
            id = "json_q30",
            category = "Código Electoral",
            questionText = "¿Qué organismo temporal electoral está entre la JED y las JRV a nivel de municipio?",
            options = listOf("La Junta Electoral Municipal (JEM).", "La Alcaldía.", "El Concejo Municipal.", "El Juzgado de Paz."),
            correctOptionIndex = 0,
            explanation = "La JEM es el organismo encargado de la logística, distribución de paquetes y supervisión directa de los centros de votación en cada municipio.",
            normativeReference = "Código Electoral Art. 38 lit. c y Art. 93",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 38, 93",
            sourcePage = 3,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 31
        ExamQuestion(
            id = "json_q31",
            category = "Código Electoral",
            questionText = "¿Qué es la 'papeleta de votación'?",
            options = listOf("Un documento oficial impreso en papel de seguridad con las opciones electorales autorizadas por el TSE.", "Un volante publicitario.", "Un recibo de pago.", "Un acta provisional de escrutinio."),
            correctOptionIndex = 0,
            explanation = "Es el instrumento físico provisto de elementos de seguridad inviolables donde el ciudadano plasma su voluntad política soberana.",
            normativeReference = "Código Electoral Arts. 185 y 186",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 185-186",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 32
        ExamQuestion(
            id = "json_q32",
            category = "Código Electoral",
            questionText = "¿Qué estipula la ley respecto a la accesibilidad para personas con discapacidad o adultos mayores?",
            options = listOf("No pueden votar.", "Que se deben habilitar mesas en planta baja y permitir el voto asistido por una persona de su confianza si lo requieren.", "Deben votar por internet obligatoriamente.", "Solo pueden votar después de las 5:00 PM."),
            correctOptionIndex = 1,
            explanation = "El Código Electoral garantiza la accesibilidad física, rampas, mesas integradas accesibles y asistencia personalizada para garantizar el sufragio universal.",
            normativeReference = "Código Electoral Art. 195 inc. penúltimo",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 195 inc. penúltimo",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 33
        ExamQuestion(
            id = "json_q33",
            category = "Código Electoral",
            questionText = "¿Está permitido tomar fotos a la papeleta de votación marcada dentro de la cabina?",
            options = listOf("Sí, para publicarla en redes sociales.", "No, viola la secrecía del voto y está expresamente sancionado legalmente.", "Solo si el vigilante lo pide.", "Solo con celular de gama alta."),
            correctOptionIndex = 1,
            explanation = "Está prohibido el uso de cámaras o teléfonos celulares dentro del anaquel de votación para preservar la inviolabilidad del voto secreto.",
            normativeReference = "Código Electoral Arts. 4, 195 y 289",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 4, 195, 289",
            sourcePage = 2,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 34
        ExamQuestion(
            id = "json_q34",
            category = "Código Electoral",
            questionText = "¿Qué validez tienen las firmas y huellas en el padrón de búsqueda del elector?",
            options = listOf("Ninguna, solo es decorativo.", "Son prueba fehaciente e indispensable de que el ciudadano ya ejerció el sufragio en esa JRV.", "Solo sirven para cobrar viáticos.", "Son para el censo de población."),
            correctOptionIndex = 1,
            explanation = "La firma y huella en el padrón electoral constituyen la constancia legal irrebatible del ejercicio del sufragio de cada ciudadano.",
            normativeReference = "Código Electoral Art. 197 inc. final",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 197 inc. final",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 35
        ExamQuestion(
            id = "json_q35",
            category = "Código Electoral",
            questionText = "¿Quiénes componen la Junta Electoral Departamental (JED)?",
            options = listOf("Por representantes propuestos por los partidos políticos contendientes con mayor votación según la ley.", "Por militares retirados.", "Por jueces de tránsito.", "Por observadores de la ONU."),
            correctOptionIndex = 0,
            explanation = "Las JED se integran de conformidad al Art. 89 del Código Electoral con base en propuestas de los partidos políticos habilitados por ley.",
            normativeReference = "Código Electoral Art. 89",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 89",
            sourcePage = 3,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 36
        ExamQuestion(
            id = "json_q36",
            category = "Código Electoral",
            questionText = "¿Qué es el Escrutinio Final?",
            options = listOf("El conteo que hace la JRV a las 5:00 PM.", "El cómputo oficial y definitivo realizado por el TSE en los días posteriores a la elección con las actas físicas.", "La encuesta que sacan los canales de televisión.", "La juramentación de los ganadores."),
            correctOptionIndex = 1,
            explanation = "El Escrutinio Final es la etapa jurídica culminante donde los Magistrados del TSE revisan acta por acta para declarar los resultados oficiales irrevocables.",
            normativeReference = "Código Electoral Arts. 214 y 215",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 214-215",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 37
        ExamQuestion(
            id = "json_q37",
            category = "Código Electoral",
            questionText = "¿Qué autoridad resuelve en última instancia sobre las impugnaciones de votación en una JRV?",
            options = listOf("El Alcalde de la localidad.", "El Tribunal Supremo Electoral (TSE).", "La Policía Nacional Civil.", "La Asamblea General de las Naciones Unidas."),
            correctOptionIndex = 1,
            explanation = "El TSE tiene la jurisdicción privativa para dirimir en definitiva sobre recursos de nulidad, votos impugnados e irregularidades comiciales.",
            normativeReference = "Código Electoral Arts. 268 y 270",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 268, 270",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 38
        ExamQuestion(
            id = "json_q38",
            category = "Código Electoral",
            questionText = "¿Puede un ciudadano votar si se presenta con una fotocopia certificada por notario de su DUI?",
            options = listOf("Sí, si está bien notariada.", "No, el votante debe identificarse obligatoriamente con el DUI físico original y vigente.", "Solo si el Presidente de la JRV lo conoce.", "Solo en horas de la tarde."),
            correctOptionIndex = 1,
            explanation = "El Art. 31 del Código Electoral exige estrictamente el Documento Único de Identidad en su formato original emitido por el RNPN.",
            normativeReference = "Código Electoral Art. 31",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 31",
            sourcePage = 2,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 39
        ExamQuestion(
            id = "json_q39",
            category = "Código Electoral",
            questionText = "¿Qué es el 'Padrón de Inhabilitados'?",
            options = listOf("La lista de personas fallecidas, condenadas o suspendidas que no pueden ejercer el voto.", "La lista de miembros de mesa que faltaron.", "La nómina de candidatos no inscritos.", "El registro de deudores alimentarios."),
            correctOptionIndex = 0,
            explanation = "Es el registro oficial consolidado por el TSE que excluye a ciudadanos con derechos políticos suspendidos por causales constitucionales o defunción.",
            normativeReference = "Código Electoral Arts. 21 y 22",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 21-22",
            sourcePage = 2,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 40
        ExamQuestion(
            id = "json_q40",
            category = "Código Electoral",
            questionText = "¿Qué prohibición constitucional existe sobre los ministros de cultos religiosos en relación a la política?",
            options = listOf("No pueden votar nunca.", "No pueden realizar propaganda política ni afiliarse a partidos ni postularse a cargos de elección popular.", "Solo pueden votar vestidos de civil.", "No tienen prohibiciones."),
            correctOptionIndex = 1,
            explanation = "El Art. 82 de la Constitución de la República prohíbe expresamente a los ministros de cultos religiosos hacer propaganda política y pertenecer a partidos políticos.",
            normativeReference = "Constitución de la República Art. 82",
            sourceDocument = "Constitución de la República de El Salvador (1983)",
            sourceArticle = "Art. 82",
            sourcePage = 1,
            sourceDocumentId = "constitucion_republica_1983"
        ),
        // Pregunta JSON 41
        ExamQuestion(
            id = "json_q41",
            category = "Procesos Electorales",
            questionText = "¿A qué hora exacta se debe abrir el centro de votación para la entrada general de la ciudadanía?",
            options = listOf("A las 6:00 AM.", "A las 7:00 AM.", "A las 8:00 AM.", "A las 6:30 AM."),
            correctOptionIndex = 1,
            explanation = "La ley electoral salvadoreña fija de forma estricta las 7:00 AM para la apertura de puertas e inicio de la votación ciudadana.",
            normativeReference = "Código Electoral Art. 198",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 198",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 42
        ExamQuestion(
            id = "json_q42",
            category = "Procesos Electorales",
            questionText = "¿A qué hora se declara oficialmente cerrado el proceso de votación e ingreso de electores?",
            options = listOf("A las 4:00 PM.", "A las 5:00 PM.", "A las 6:00 PM.", "A las 7:00 PM."),
            correctOptionIndex = 1,
            explanation = "A las 17:00 horas (5:00 PM) se cierran las puertas del centro comicial, permitiendo el sufragio únicamente a quienes ya estén en la fila interna.",
            normativeReference = "Código Electoral Art. 198",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 198",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 43
        ExamQuestion(
            id = "json_q43",
            category = "Procesos Electorales",
            questionText = "Si a las 5:00 PM aún hay ciudadanos formados DENTRO de la fila del centro de votación, ¿qué se debe hacer?",
            options = listOf("Cerrarles la puerta y no dejarlos votar.", "Permitirles votar hasta que el último ciudadano que estaba dentro del centro a las 5:00 PM ejerza su derecho.", "Llamar a la PNC para desalojarlos.", "Hacer un sorteo."),
            correctOptionIndex = 1,
            explanation = "El Código Electoral ampara a todos los electores que ingresaron al recinto antes de las 5:00 PM; la JRV debe esperar a que completen su voto.",
            normativeReference = "Código Electoral Art. 198 inc. final",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 198 inc. final",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 44
        ExamQuestion(
            id = "json_q44",
            category = "Procesos Electorales",
            questionText = "Durante la fase de instalación (6:00 AM - 7:00 AM), ¿cuál es el primer paso operativo que realiza la JRV?",
            options = listOf("Contar los votos de la urna.", "Verificar el contenido del paquete electoral, contar papeletas en blanco y completar el Acta de Instalación.", "Abrir las urnas para el público.", "Comer desayuno."),
            correctOptionIndex = 1,
            explanation = "La JRV debe cotejar inventario, verificar sellos, contar papeletas asignadas y firmar el Acta de Instalación antes de recibir votos.",
            normativeReference = "Código Electoral Arts. 190 y 192",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 190, 192",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 45
        ExamQuestion(
            id = "json_q45",
            category = "Procesos Electorales",
            questionText = "¿Qué paso sigue inmediatamente después de que el ciudadano entrega su DUI al Vocal 1?",
            options = listOf("Darle la tinta indeleble.", "El Secretario busca al ciudadano en el padrón, verifica la foto y confirma que no haya votado previamente.", "Pedirle que cante el himno nacional.", "Retener su DUI indefinidamente."),
            correctOptionIndex = 1,
            explanation = "La identificación del votante en el padrón y el cotejo fisonómico con la fotografía del padrón es el filtro de seguridad primordial.",
            normativeReference = "Código Electoral Arts. 195 y 196",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 195-196",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 46
        ExamQuestion(
            id = "json_q46",
            category = "Procesos Electorales",
            questionText = "Antes de entregar las papeletas al ciudadano para que pase al estrado secreto, ¿qué firma/sello debe llevar la papeleta?",
            options = listOf("La firma del elector.", "El sello oficial de la JRV y la firma del miembro designado (Secretario) en el reverso.", "El sello de la alcaldía municipal.", "La firma del vigilante de partido."),
            correctOptionIndex = 1,
            explanation = "Sin la firma del Secretario y el sello reglamentario al reverso, la papeleta no posee validez legal al momento del escrutinio.",
            normativeReference = "Código Electoral Art. 196",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 196",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 47
        ExamQuestion(
            id = "json_q47",
            category = "Procesos Electorales",
            questionText = "Una vez que el elector deposita sus papeletas en las urnas correspondientes, ¿cuál es el último paso en la mesa?",
            options = listOf("Se le devuelve su DUI, firma o pone la huella en el padrón y se le aplica la tinta indeleble en el dedo.", "Se le da un premio en efectivo.", "Se le hace una entrevista grabada.", "Se le acompaña hasta su casa."),
            correctOptionIndex = 0,
            explanation = "El procedimiento concluye con la firma/huella en el padrón de firmas, la aplicación de tinta en el dedo índice o meñique y la devolución del documento.",
            normativeReference = "Código Electoral Art. 197",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 197",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 48
        ExamQuestion(
            id = "json_q48",
            category = "Procesos Electorales",
            questionText = "¿En qué momento del día electoral se procede al inicio del Escrutinio de Mesa (conteo de votos)?",
            options = listOf("A las 12:00 del mediodía.", "Inmediatamente después de cerrar la votación a las 5:00 PM y atender al último votante de la fila.", "A las 8:00 PM.", "Al día siguiente por la mañana."),
            correctOptionIndex = 1,
            explanation = "El escrutinio preliminar es ininterrumpido y debe iniciar de inmediato tras declarar clausurada la fase de sufragio a las 17:00 horas.",
            normativeReference = "Código Electoral Arts. 198 y 200",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 198, 200",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 49
        ExamQuestion(
            id = "json_q49",
            category = "Procesos Electorales",
            questionText = "Durante el conteo de votos en la mesa, ¿cuál es el orden correcto para clasificar las papeletas extraídas de la urna?",
            options = listOf("Votos válidos (por partido/candidato), votos nulos, votos en blanco e impugnados.", "Votos de amigos primero, los demás después.", "Solo se cuentan los votos del partido que preside la mesa.", "Se meten todos en una bolsa sin clasificar."),
            correctOptionIndex = 0,
            explanation = "El Código Electoral establece el procedimiento de separación metódica: papeleta por papeleta, determinando su validez, nulidad, voto en blanco o impugnación.",
            normativeReference = "Código Electoral Arts. 200 y 201",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 200-201",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 50
        ExamQuestion(
            id = "json_q50",
            category = "Procesos Electorales",
            questionText = "¿Qué define a un 'Voto Nulo' según la legislación electoral salvadoreña?",
            options = listOf("Cualquier voto a favor de la oposición.", "Aquel donde la papeleta presenta marcas cruzadas entre partidos no coaligados, tachaduras totales, insultos o roturas deliberadas.", "Un voto con tinta de color azul.", "Una papeleta doblada en cuatro partes."),
            correctOptionIndex = 1,
            explanation = "La nulidad se decreta cuando es imposible deducir la voluntad del elector, cuando se marcan partidos no coaligados o hay expresiones injuriosas.",
            normativeReference = "Código Electoral Art. 207",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 207",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 51
        ExamQuestion(
            id = "json_q51",
            category = "Procesos Electorales",
            questionText = "¿Qué se entiende por 'Voto en Blanco'?",
            options = listOf("Una papeleta sin ninguna marca realizada por el elector depositada dentro de la urna.", "Un voto a favor de una candidatura blanca.", "Una papeleta fotocopiada en blanco y negro.", "Un voto anulado por el Presidente de JRV."),
            correctOptionIndex = 0,
            explanation = "El voto en blanco es aquel donde el elector no expresó ninguna preferencia ni realizó marca alguna en la papeleta oficial.",
            normativeReference = "Código Electoral Art. 203",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 203",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 52
        ExamQuestion(
            id = "json_q52",
            category = "Procesos Electorales",
            questionText = "¿Qué es un 'Voto Impugnado'?",
            options = listOf("Un voto comprado en la calle.", "Un voto sobre el cual un vigilante o miembro de JRV cuestiona formalmente su validez, colocándose en un sobre especial para ser juzgado por el TSE.", "Un voto de un ciudadano extranjero.", "Un voto con crayón rojo."),
            correctOptionIndex = 1,
            explanation = "Cuando la calificación de un voto genera controversia formal insalvable en la JRV, se consigna como impugnado y se remite en sobre cerrado al TSE para el escrutinio final.",
            normativeReference = "Código Electoral Arts. 206 y 208",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 206, 208",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 53
        ExamQuestion(
            id = "json_q53",
            category = "Procesos Electorales",
            questionText = "¿Qué se hace con las papeletas inutilizadas o no utilizadas (sobrantes) al cerrar la votación a las 5:00 PM?",
            options = listOf("Se regalan a los observadores.", "Se cuentan, se inutilizan mediante el procedimiento oficial (trazo/corte) y se empacan en el sobre de papeletas no usadas.", "Se tiran a la basura escolar.", "Se queman en el patio."),
            correctOptionIndex = 1,
            explanation = "El primer paso del cierre es el recuento y anulación física de las papeletas sobrantes para evitar cualquier inserción fraudulenta en la urna.",
            normativeReference = "Código Electoral Art. 200 lit. a",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 200 lit. a",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 54
        ExamQuestion(
            id = "json_q54",
            category = "Procesos Electorales",
            questionText = "¿Qué es la 'Hoja de Borrador de Escrutinio'?",
            options = listOf("Un papel cualquiera donde se anota con lápiz.", "Un documento auxiliar oficial donde la JRV efectúa los conteos matemáticos preliminares antes de transcribirlos al Acta Definitiva.", "La lista del almuerzo.", "Un folleto de propaganda."),
            correctOptionIndex = 1,
            explanation = "Es el instrumento de apoyo provisto por el TSE para realizar sumas, marcas paloteadas y cuadres numéricos sin manchar el acta definitiva.",
            normativeReference = "Instructivo para Miembros de JRV (TSE)",
            sourceDocument = "Instructivo para Miembros de JRV (TSE)",
            sourceArticle = "Instructivo de Escrutinio Preliminar",
            sourcePage = 2,
            sourceDocumentId = "instructivo_jrv_tse"
        ),
        // Pregunta JSON 55
        ExamQuestion(
            id = "json_q55",
            category = "Procesos Electorales",
            questionText = "¿Qué procedimiento se realiza si el número de papeletas en la urna supera al número de electores que firmaron el padrón?",
            options = listOf("Se descartan papeletas al azar.", "Se anota la discrepancia en el Acta de Incidentes y se procede a verificar el conteo detallado sin alterar las papeletas reales.", "Se anula la elección en todo el país.", "Se oculta la información a los fiscales."),
            correctOptionIndex = 1,
            explanation = "Toda discrepancia numérica entre padrón de firmas y papeletas extraídas debe hacerse constar obligatoriamente en el acta de escrutinio para conocimiento del TSE.",
            normativeReference = "Código Electoral Art. 202 lit. j",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 202 lit. j",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 56
        ExamQuestion(
            id = "json_q56",
            category = "Procesos Electorales",
            questionText = "¿Qué contiene el Paquete Electoral que se devuelve al terminar el escrutinio?",
            options = listOf("Papeletas válidas, nulas, en blanco, inutilizadas, padrón firmado, actas y listas de incidentes en sobres sellados.", "Solo basura y vasos de café.", "Las pertenencias personales de los miembros.", "Piedras de contrapeso."),
            correctOptionIndex = 0,
            explanation = "El paquete electoral de devolución contiene todo el material comicial utilizado, debidamente clasificado en sobres rotulados y asegurados con precintos.",
            normativeReference = "Código Electoral Art. 210",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 210",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 57
        ExamQuestion(
            id = "json_q57",
            category = "Procesos Electorales",
            questionText = "¿Quién es responsable del traslado del Paquete Electoral desde la JRV hacia el centro de acopio/JEM?",
            options = listOf("El Presidente y Secretario custodiados por agentes de la PNC o personal autorizado por el TSE.", "Un vigilante de partido a solas.", "Cualquier persona que tenga vehículo propio.", "Un servicio de encomienda particular."),
            correctOptionIndex = 0,
            explanation = "La cadena de custodia exige que los directivos de la JRV acompañen el paquete bajo resguardo policial estricto hasta la entrega formal con acta.",
            normativeReference = "Código Electoral Arts. 210 y 211",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 210-211",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 58
        ExamQuestion(
            id = "json_q58",
            category = "Procesos Electorales",
            questionText = "¿Qué debe hacer la JRV con los sellos y la tinta indeleble sobrante al finalizar el escrutinio?",
            options = listOf("Guardarlos dentro del paquete electoral de devolución.", "Llevárselos como recuerdo a su casa.", "Venderlos a los partidos.", "Regalárselos a los alumnos de la escuela."),
            correctOptionIndex = 0,
            explanation = "Todos los útiles oficiales, sellos de mesa y frascos de tinta son propiedad del Estado y deben ser devueltos en la valija electoral.",
            normativeReference = "Instructivo para Miembros de JRV (TSE)",
            sourceDocument = "Instructivo para Miembros de JRV (TSE)",
            sourceArticle = "Embalaje y Devolución de Insumos",
            sourcePage = 2,
            sourceDocumentId = "instructivo_jrv_tse"
        ),
        // Pregunta JSON 59
        ExamQuestion(
            id = "json_q59",
            category = "Procesos Electorales",
            questionText = "¿A qué hora inicia habitualmente la transmisión de actas de escrutinio preliminar por medios tecnológicos?",
            options = listOf("A las 6:00 AM.", "Tan pronto como la JRV finaliza la digitación, revisión y firma del acta oficial tras el cierre de votación.", "A las 12:00 de la noche obligatoriamente.", "Tres días después."),
            correctOptionIndex = 1,
            explanation = "La transmisión tecnológica por scanner o dispositivo RTS inicia en cuanto el acta de escrutinio preliminar ha sido cerrada y firmada en la mesa.",
            normativeReference = "Código Electoral Art. 209",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 209",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 60
        ExamQuestion(
            id = "json_q60",
            category = "Procesos Electorales",
            questionText = "¿Qué es el 'Acta de Cierre de Votación'?",
            options = listOf("El documento donde se hace constar la hora exacta del fin de recepción de votos y la cantidad de votantes que ejercieron el sufragio.", "La renuncia de los miembros de mesa.", "Una solicitud de pago al TSE.", "La autorización para abrir el centro."),
            correctOptionIndex = 0,
            explanation = "El Acta de Cierre consigna el término legal de la votación ciudadana y prepara la mesa para dar paso inmediato al escrutinio preliminar.",
            normativeReference = "Código Electoral Art. 198",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 198",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 61
        ExamQuestion(
            id = "json_q61",
            category = "Situaciones en Centro de Voto",
            questionText = "SITUACIÓN: Un votante se presenta con su DUI vencido. ¿Qué DEBE HACER la JRV según las disposiciones legales?",
            options = listOf("Verificar los decretos de prórroga del TSE; si la ley vigente autoriza votar con DUI vencido se le permite, de lo contrario no puede votar.", "Dejarlo votar siempre sin importar la ley.", "Romperle el DUI.", "Pedirle dinero para dejarlo votar."),
            correctOptionIndex = 0,
            explanation = "Solo se permite el sufragio con DUI vencido si la Asamblea Legislativa y el TSE han emitido un decreto transitorio formal que lo autorice expresamente para esa elección.",
            normativeReference = "Código Electoral Art. 31 y Decretos de Prórroga Legislativa",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 31",
            sourcePage = 2,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 62
        ExamQuestion(
            id = "json_q62",
            category = "Situaciones en Centro de Voto",
            questionText = "SITUACIÓN: Un votante insiste en ingresar al recinto portando camisetas o banderas proselitistas. ¿Qué NO DEBE HACER la JRV o la seguridad del centro?",
            options = listOf("Entrar en insultos, agresión física o enfrentamiento partidario con el votante.", "Indicarle amablemente que se cubra la camiseta o retire la propaganda antes de entrar.", "Solicitar apoyo a la PNC si rehúsa cumplir la normativa pacíficamente.", "Mantener la calma institucional."),
            correctOptionIndex = 0,
            explanation = "Los miembros electorales deben actuar con estricta neutralidad, respeto y prudencia profesional, sin recurrir jamás a la violencia verbal ni agresiones.",
            normativeReference = "Código Electoral Arts. 177 y 179",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 177, 179",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 63
        ExamQuestion(
            id = "json_q63",
            category = "Situaciones en Centro de Voto",
            questionText = "SITUACIÓN: El nombre del elector no aparece en el padrón de la JRV pero insiste en votar en esa mesa. ¿Qué se debe hacer?",
            options = listOf("Anotarlo a mano al final del padrón y darle papeleta.", "Verificar en la consulta digital del TSE o mesa de información del centro el número correcto de JRV donde le corresponde votar.", "Quitarle el DUI.", "Hacerlo votar en una hoja en blanco."),
            correctOptionIndex = 1,
            explanation = "Ningún ciudadano puede votar en una JRV donde no aparezca legalmente registrado en el padrón electoral oficial emitido por el TSE.",
            normativeReference = "Instructivo para Miembros de JRV (TSE)",
            sourceDocument = "Instructivo para Miembros de JRV (TSE)",
            sourceArticle = "Procedimiento de Orientación al Elector",
            sourcePage = 2,
            sourceDocumentId = "instructivo_jrv_tse"
        ),
        // Pregunta JSON 64
        ExamQuestion(
            id = "json_q64",
            category = "Situaciones en Centro de Voto",
            questionText = "SITUACIÓN: Un vigilante de partido interrumpe constantemente la entrega de papeletas y exige revisar las firmas del padrón a cada instante. ¿Qué corresponde?",
            options = listOf("Cederle el control total de la mesa al vigilante.", "Llamar al orden formalmente, consignar la observación en el borrador/acta y, si la conducta persiste, solicitar asistencia al delegado o PNC.", "Pelear a golpes con el vigilante.", "Cerrar la votación a las 11:00 AM."),
            correctOptionIndex = 1,
            explanation = "Los vigilantes tienen derecho de fiscalización visual pero no pueden obstruir el normal desarrollo ni asumir funciones exclusivas de la JRV.",
            normativeReference = "Código Electoral Arts. 102 y 128",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 102, 128",
            sourcePage = 3,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 65
        ExamQuestion(
            id = "json_q65",
            category = "Situaciones en Centro de Voto",
            questionText = "SITUACIÓN: Un elector daña involuntariamente su papeleta al doblarla antes de marcarla y pide otra. ¿Qué debe hacer la JRV?",
            options = listOf("Negarle el voto y expulsarlo.", "Recibir la papeleta dañada, marcarla como 'Inutilizada por el elector', guardarla en el sobre respectivo y entregarle una nueva papeleta única.", "Cobrarle \$10 por la papeleta rota.", "Dejar que vote con dos papeletas a la vez."),
            correctOptionIndex = 1,
            explanation = "El Art. 197 inc. 2° del Código Electoral permite reponer la papeleta inutilizada accidentalmente por una sola vez, cancelando la anterior de inmediato.",
            normativeReference = "Código Electoral Art. 197 inc. 2°",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 197 inc. 2°",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 66
        ExamQuestion(
            id = "json_q66",
            category = "Situaciones en Centro de Voto",
            questionText = "SITUACIÓN: Se detecta que un elector intentó depositar dos papeletas en lugar de una. ¿Qué NO DEBE HACER la JRV ante este presunto fraude?",
            options = listOf("Ocultar la situación y dejar que introduzca ambas papeletas.", "Detener el depósito del voto anómalo.", "Retener el DUI y notificar de inmediato al delegado de la FGR y a la PNC.", "Asentar el hecho circunstanciado en el acta de incidentes."),
            correctOptionIndex = 0,
            explanation = "El intento de doble votación es un delito penal electoral flagrante; encubrirlo o permitirlo constituye complicidad criminal.",
            normativeReference = "Código Electoral Art. 7 lit. i y Art. 250",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 7 lit. i, Art. 250",
            sourcePage = 2,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 67
        ExamQuestion(
            id = "json_q67",
            category = "Situaciones en Centro de Voto",
            questionText = "SITUACIÓN: Un anciano con problemas de movilidad no puede subir al segundo piso donde está ubicada su JRV. ¿Qué solución inclusiva autoriza el protocolo del TSE?",
            options = listOf("Decirle que se regrese a su casa sin votar.", "Bajar la JRV portátil/Mesa Auxiliar autorizada por el TSE con custodia de los miembros e instructivos oficiales para que vote en planta baja.", "Subirlo cargado a la fuerza en una carretilla.", "Hacer que vote su hijo en su lugar."),
            correctOptionIndex = 1,
            explanation = "El protocolo de accesibilidad electoral faculta el descenso controlado de la urna y papeleta bajo estricta custodia de los miembros de mesa y vigilancia partidaria.",
            normativeReference = "Código Electoral Art. 195 inc. penúltimo",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 195 inc. penúltimo",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 68
        ExamQuestion(
            id = "json_q68",
            category = "Situaciones en Centro de Voto",
            questionText = "SITUACIÓN: Se desata una discusión acalorada entre votantes en la fila fuera de la JRV. ¿Qué NO DEBE HACER el Presidente de la mesa?",
            options = listOf("Salir de la JRV con las papeletas y la urna para discutir en la fila.", "Solicitar el apoyo inmediato del personal de seguridad de la PNC destacado en el pasillo.", "Llamar a la calma y mantener la custodia permanente de los materiales electorales.", "Continuar atendiendo a los votantes en la mesa si el ambiente lo permite."),
            correctOptionIndex = 0,
            explanation = "Bajo ninguna circunstancia los miembros de JRV deben desamparar las urnas ni llevar material electoral fuera del recinto designado.",
            normativeReference = "Código Electoral Arts. 102 y 290",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 102, 290",
            sourcePage = 3,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 69
        ExamQuestion(
            id = "json_q69",
            category = "Situaciones en Centro de Voto",
            questionText = "SITUACIÓN: A las 12:00 PM se acaba la tinta indeleble en la mesa. ¿Qué procede?",
            options = listOf("Usar marcador permanente común de oficina.", "Notificar inmediatamente a la Junta Electoral Municipal (JEM) / TSE para que provean un frasco oficial de repuesto antes de seguir entintando.", "Dejar de poner tinta a los siguientes 100 votantes.", "Usar café soluble."),
            correctOptionIndex = 1,
            explanation = "Solo la tinta indeleble provista por el TSE cumple con los reactivos químicos reglamentarios para evitar el fraude por doble sufragio.",
            normativeReference = "Código Electoral Arts. 98 lit. b y 189",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 98, 189",
            sourcePage = 3,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 70
        ExamQuestion(
            id = "json_q70",
            category = "Situaciones en Centro de Voto",
            questionText = "SITUACIÓN: Un votante se niega a mancharse el dedo con tinta indeleble tras haber votado. ¿Qué NO DEBE HACER la JRV?",
            options = listOf("Devolverle el DUI y dejarlo marchar sin registrar el suceso en ninguna parte.", "Explicarle que el entintado es un requisito obligatorio fijado por la ley.", "Asentar la negativa y los datos del ciudadano en el libro de incidentes.", "Informar a la Fiscalía General de la República destacada en el centro."),
            correctOptionIndex = 0,
            explanation = "El entintado es un mandato legal ineludible; ante la negativa, se debe levantar constancia formal en el acta de incidencias para las responsabilidades legales correspondientes.",
            normativeReference = "Código Electoral Art. 197 inc. final",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 197 inc. final",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 71
        ExamQuestion(
            id = "json_q71",
            category = "Situaciones en Centro de Voto",
            questionText = "SITUACIÓN: Se descubre a una persona fotografiando su papeleta marcada dentro de la cabina de votación. ¿Qué procede?",
            options = listOf("Felicitarlo y pedirle que comparta la foto.", "Hacerle saber de inmediato que está prohibido, notificar al Fiscal de la FGR en el centro y registrar la falta en el acta.", "Borrarle todas las fotos familiares del celular.", "Quitarle el teléfono y quedárselo."),
            correctOptionIndex = 1,
            explanation = "La toma de fotografías del voto vulnera el secreto constitucional del sufragio; debe intervenir la FGR para determinar si hubo coacción o delito electoral.",
            normativeReference = "Código Electoral Arts. 4, 195 y 289",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 4, 195, 289",
            sourcePage = 2,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 72
        ExamQuestion(
            id = "json_q72",
            category = "Situaciones en Centro de Voto",
            questionText = "SITUACIÓN: Un vigilante de partido quiere revisar por la fuerza el reverso de las papeletas mientras el ciudadano está marcando. ¿Qué NO DEBE HACER la JRV?",
            options = listOf("Permitirle al vigilante invadir la privacidad o acercarse al estrado secreto.", "Ordenar al vigilante que permanezca en su asiento asignado.", "Hacer respetar la distancia mínima de secrecía del voto.", "Llamar a la seguridad si el vigilante desobedece."),
            correctOptionIndex = 0,
            explanation = "El estrado secreto es inviolable; ningún fiscal o vigilante puede observar o coaccionar al elector en el momento de la marcación.",
            normativeReference = "Constitución de la República Art. 78 / Código Electoral Art. 4",
            sourceDocument = "Constitución de la República de El Salvador (1983)",
            sourceArticle = "Art. 78",
            sourcePage = 1,
            sourceDocumentId = "constitucion_republica_1983"
        ),
        // Pregunta JSON 73
        ExamQuestion(
            id = "json_q73",
            category = "Situaciones en Centro de Voto",
            questionText = "SITUACIÓN: Ocurre un corte de energía eléctrica durante el conteo de votos a las 6:30 PM. ¿Qué medida de seguridad se debe tomar?",
            options = listOf("Seguir contando a oscuras sin ver las papeletas.", "Pausar el conteo, resguardar las papeletas en la mesa y utilizar las lámparas/plantas de luz asignadas en el paquete del TSE en presencia de todos.", "Llevarse las urnas al automóvil de un directivo.", "Declarar desierta la mesa electoral."),
            correctOptionIndex = 1,
            explanation = "Ante contingencias de luz, se suspende momentáneamente el acto, se protegen los votos bajo la vista de todos y se encienden las luces de emergencia del TSE.",
            normativeReference = "Instructivo para Miembros de JRV (TSE)",
            sourceDocument = "Instructivo para Miembros de JRV (TSE)",
            sourceArticle = "Protocolo de Contingencia Lumínica y Seguridad de Mesa",
            sourcePage = 2,
            sourceDocumentId = "instructivo_jrv_tse"
        ),
        // Pregunta JSON 74
        ExamQuestion(
            id = "json_q74",
            category = "Situaciones en Centro de Voto",
            questionText = "SITUACIÓN: Hay un error en la suma de votos en la borrador de escrutinio al finalizar la noche. ¿Qué NO DEBE HACER la JRV?",
            options = listOf("Alterar o inventar números al azar en el Acta Definitiva para que 'cuadre' de cualquier forma.", "Volver a contar físicamente las papeletas agrupadas por montones.", "Verificar las operaciones aritméticas en la hoja auxiliar con calma.", "Pedir asesoría al facilitador del TSE si hay dudas de sumatoria."),
            correctOptionIndex = 0,
            explanation = "Falsificar datos en el acta de escrutinio constituye delito electoral de falsedad documental; la sumatoria debe reflejar exactamente el recuento físico real.",
            normativeReference = "Código Electoral Arts. 202 y 250",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 202, 250",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 75
        ExamQuestion(
            id = "json_q75",
            category = "Situaciones en Centro de Voto",
            questionText = "SITUACIÓN: Un ciudadano ebrio intenta ingresar al centro de votación exigiendo votar. ¿Qué debe hacerse?",
            options = listOf("Dejarlo votar para que no haga escándalo.", "Impedirle el ingreso conforme a la Ley Seca y solicitar la intervención de la PNC para retirarlo pacíficamente del perímetro.", "Regalarle una botella de licor.", "Cerrar el centro escolar."),
            correctOptionIndex = 1,
            explanation = "El estado de ebriedad vulnera el orden comicial y la Ley Seca; la PNC tiene la orden legal de no permitir el acceso a personas bajo efectos del alcohol o drogas.",
            normativeReference = "Código Electoral Arts. 284 y 290",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 284, 290",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 76
        ExamQuestion(
            id = "json_q76",
            category = "Situaciones en Centro de Voto",
            questionText = "SITUACIÓN: Falta el sello oficial de la JRV en el paquete entregado a las 6:00 AM. ¿Qué debe hacer la mesa?",
            options = listOf("Fabricar un sello con una papa o borrador.", "Reportar el hallazgo de inmediato a la JEM para que suministren el sello oficial correspondiente antes de las 7:00 AM.", "Empezar a votar sin sellar ninguna papeleta.", "Irse a casa."),
            correctOptionIndex = 1,
            explanation = "La JEM cuenta con material de reserva y debe subsanar inmediatamente la falta de sellos o insumos antes del inicio de la votación.",
            normativeReference = "Código Electoral Arts. 98 lit. b y 189",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 98, 189",
            sourcePage = 3,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 77
        ExamQuestion(
            id = "json_q77",
            category = "Situaciones en Centro de Voto",
            questionText = "SITUACIÓN: Un observador internacional solicita ver el padrón electoral de la mesa. ¿Cuál es el procedimiento adecuado?",
            options = listOf("Agredirlo verbalmente.", "Permitirle la observación visual a una distancia adecuada sin interrumpir la fluidez del flujo de electores ni manipular los documentos originales.", "Entregarle el padrón original para que se lo lleve.", "Prohibirle la entrada al centro de votación."),
            correctOptionIndex = 1,
            explanation = "Los observadores acreditados por el TSE tienen facultades de observación presencial no intrusiva conforme al Reglamento de Observación Electoral.",
            normativeReference = "Reglamento General para la Observación Electoral",
            sourceDocument = "Reglamento General para la Observación Electoral Nacional e Internacional",
            sourceArticle = "Código de Conducta y Facultades de Observación",
            sourcePage = 1,
            sourceDocumentId = "reglamento_observacion_electoral"
        ),
        // Pregunta JSON 78
        ExamQuestion(
            id = "json_q78",
            category = "Situaciones en Centro de Voto",
            questionText = "SITUACIÓN: Se presenta un elector cuyo dedo meñique ya tiene tinta indeleble aplicada previamente. ¿Qué NO DEBE HACER la JRV?",
            options = listOf("Permitirle emitir un segundo voto.", "Revisar cuidadosamente el dedo y corroborar en el padrón.", "Negarle la entrega de papeletas por presentar marca de votación previa.", "Llamar a la FGR para que investigue posible intento de doble voto."),
            correctOptionIndex = 0,
            explanation = "La presencia de tinta indeleble es causal suficiente para denegar la papeleta y poner al ciudadano a disposición fiscal por intento de doble sufragio.",
            normativeReference = "Código Electoral Art. 197 inc. final y Art. 250",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 197 inc. final, Art. 250",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 79
        ExamQuestion(
            id = "json_q79",
            category = "Situaciones en Centro de Voto",
            questionText = "SITUACIÓN: Un miembro de la JRV sufre un quebranto grave de salud a las 2:00 PM. ¿Cómo debe proceder la mesa?",
            options = listOf("Obligarlo a quedarse desmayado en la mesa.", "Brindarle asistencia médica, tramitar su retiro, incorporar al suplente acreditado y asentarlo en la hoja de incidentes.", "Clausurar definitivamente la JRV.", "Llamar a un familiar del enfermo sin credencial."),
            correctOptionIndex = 1,
            explanation = "Por razones de fuerza mayor o salud, el miembro es relevado por el suplente oficial, registrando la hora y motivo exacto en el acta de incidencias.",
            normativeReference = "Código Electoral Arts. 100 y 190",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 100, 190",
            sourcePage = 3,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 80
        ExamQuestion(
            id = "json_q80",
            category = "Situaciones en Centro de Voto",
            questionText = "SITUACIÓN: Un medio de comunicación intenta transmitir en vivo enfocado de cerca la marca que hace un ciudadano dentro de la cabina. ¿Qué NO DEBE HACER la JRV?",
            options = listOf("Permitir que la prensa filme dentro del estrado violando el secreto del voto.", "Hacer respetar el perímetro de secrecía garantizado por la Constitución.", "Permitir tomas generales del recinto sin enfocar papeletas marcadas.", "Pedir amablemente a los camarógrafos que retrocedan."),
            correctOptionIndex = 0,
            explanation = "La libertad de prensa no puede sobrepasar la garantía constitucional de secrecía del voto del ciudadano.",
            normativeReference = "Constitución de la República Art. 78 / Código Electoral Art. 4",
            sourceDocument = "Constitución de la República de El Salvador (1983)",
            sourceArticle = "Art. 78",
            sourcePage = 1,
            sourceDocumentId = "constitucion_republica_1983"
        ),
        // Pregunta JSON 81
        ExamQuestion(
            id = "json_q81",
            category = "Ley Seca y Reglamentos",
            questionText = "¿Cuál es la duración exacta de la conocida 'Ley Seca' durante los procesos electorales en El Salvador?",
            options = listOf("Solo 12 horas.", "Tres días consecutivamente: el día anterior a la elección, el día de la votación y el día siguiente a la elección.", "Una semana completa.", "Solo el domingo durante las horas de votación."),
            correctOptionIndex = 1,
            explanation = "El Art. 284 del Código Electoral fija la prohibición de venta y consumo de licor durante 3 días completos (víspera, día de comicios y día posterior).",
            normativeReference = "Código Electoral Art. 284",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 284",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 82
        ExamQuestion(
            id = "json_q82",
            category = "Ley Seca y Reglamentos",
            questionText = "¿A qué hora entra en vigor la Ley Seca previa al domingo de elecciones (suponiendo domingo de votación)?",
            options = listOf("A las 00:00 horas (medianoche) del día sábado.", "A las 6:00 PM del viernes.", "A las 7:00 AM del domingo.", "A las 12:00 PM del sábado."),
            correctOptionIndex = 0,
            explanation = "Inicia legalmente desde el primer segundo (00:00 horas) del día anterior a la jornada electoral fijada.",
            normativeReference = "Código Electoral Art. 284",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 284",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 83
        ExamQuestion(
            id = "json_q83",
            category = "Ley Seca y Reglamentos",
            questionText = "¿Qué sanciones contempla el Código Electoral para los establecimientos o personas que violen la Ley Seca?",
            options = listOf("Multas económicas significativas reguladas por el TSE y cierres de establecimientos según la gravedad.", "Cadena perpetua.", "Trabajo comunitario en la mesa electoral.", "Ninguna penalización legal."),
            correctOptionIndex = 0,
            explanation = "El incumplimiento acarrea multas pecuniarias tasadas en salarios mínimos y la clausura inmediata del local por las autoridades.",
            normativeReference = "Código Electoral Arts. 284 y 243",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 243, 284",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 84
        ExamQuestion(
            id = "json_q84",
            category = "Ley Seca y Reglamentos",
            questionText = "¿Qué es el período de 'Silencio Electoral'?",
            options = listOf("Un período de 3 días antes de las elecciones donde queda totalmente prohibida la propaganda partidaria en medios y lugares públicos.", "Un minuto de silencio por los héroes patrios.", "La hora en que no se puede hablar en la JRV.", "El tiempo en que se cuentan los votos."),
            correctOptionIndex = 0,
            explanation = "El Art. 175 del Código Electoral manda el cese de toda campaña o propaganda proselitista 3 días antes de la fecha fijada para las elecciones.",
            normativeReference = "Código Electoral Art. 175",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 175",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 85
        ExamQuestion(
            id = "json_q85",
            category = "Ley Seca y Reglamentos",
            questionText = "Durante los días de Silencio Electoral, ¿está permitido realizar mítines, manifestaciones o perifoneo partidario?",
            options = listOf("Sí, si es en colonias privadas.", "No, queda absolutamente vedada toda actividad proselitista en cualquier espacio público o medio masivo.", "Solo los partidos pequeños pueden.", "Solo de noche."),
            correctOptionIndex = 1,
            explanation = "La veda electoral rige con carácter general en todo el territorio nacional para permitir la reflexión libre de la ciudadanía.",
            normativeReference = "Código Electoral Arts. 175 y 176",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 175-176",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 86
        ExamQuestion(
            id = "json_q86",
            category = "Ley Seca y Reglamentos",
            questionText = "¿A qué hora deben estar constituidos los miembros de JRV en el recinto el día de la elección?",
            options = listOf("A las 5:00 AM.", "A las 6:00 AM.", "A las 7:00 AM.", "A las 6:45 AM."),
            correctOptionIndex = 1,
            explanation = "La hora reglamentaria establecida en el Art. 190 del Código Electoral son las 6:00 horas.",
            normativeReference = "Código Electoral Art. 190",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 190",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 87
        ExamQuestion(
            id = "json_q87",
            category = "Ley Seca y Reglamentos",
            questionText = "¿Cuál es el horario legal de atención al público para la emisión del voto presencial en los centros?",
            options = listOf("De 8:00 AM a 4:00 PM.", "De 7:00 AM a 5:00 PM.", "De 6:00 AM a 6:00 PM.", "De 7:00 AM a 7:00 PM."),
            correctOptionIndex = 1,
            explanation = "El horario oficial e improrrogable de apertura y cierre de urnas es de siete a diecisiete horas (7:00 AM a 5:00 PM).",
            normativeReference = "Código Electoral Art. 198",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 198",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 88
        ExamQuestion(
            id = "json_q88",
            category = "Ley Seca y Reglamentos",
            questionText = "¿Qué sucede si un medio de televisión difunde encuestas o proyecciones a 'boca de urna' a la 1:00 PM del día de comicios?",
            options = listOf("Recibe un premio periodístico.", "Comete una infracción grave al Código Electoral por difundir resultados/proyecciones antes del cierre oficial de las urnas.", "No pasa nada.", "Le dan una felicitación del TSE."),
            correctOptionIndex = 1,
            explanation = "El Art. 175 inc. final del Código Electoral prohíbe terminantemente publicar encuestas o bocas de urna durante los 15 días previos y durante el día de la votación.",
            normativeReference = "Código Electoral Art. 175 inc. final",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 175 inc. final",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 89
        ExamQuestion(
            id = "json_q89",
            category = "Ley Seca y Reglamentos",
            questionText = "¿Se permite la venta de bebidas alcohólicas en hoteles o restaurantes turísticos durante la Ley Seca?",
            options = listOf("Sí, si pagan un impuesto especial.", "No, la prohibición de venta y consumo aplica de forma pareja en todo el territorio nacional sin excepciones.", "Solo a turistas con pasaporte extranjero.", "Solo después de las 8:00 PM."),
            correctOptionIndex = 1,
            explanation = "La Ley Seca no contempla fueros ni excepciones para comercios turísticos, restaurantes o clubes privados.",
            normativeReference = "Código Electoral Art. 284",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 284",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 90
        ExamQuestion(
            id = "json_q90",
            category = "Ley Seca y Reglamentos",
            questionText = "¿Qué permiso otorga la ley a los trabajadores para ir a emitir su sufragio en día laboral?",
            options = listOf("Los patronos están obligados por ley a conceder permiso remunerado el tiempo necesario a sus empleados para ejercer el voto.", "Ningún permiso, si faltan los despiden.", "Solo media hora no pagada.", "Deben pedir vacaciones con un mes de anticipación."),
            correctOptionIndex = 0,
            explanation = "El Código de Trabajo y el Art. 113 del Código Electoral obligan a los empleadores a otorgar permiso con goce de salario para que los ciudadanos voten.",
            normativeReference = "Código Electoral Art. 113 / Código de Trabajo Art. 29",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 113",
            sourcePage = 3,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 91
        ExamQuestion(
            id = "json_q91",
            category = "Ley Seca y Reglamentos",
            questionText = "¿A qué distancia mínima de un centro de votación está prohibido colocar propaganda o puestos de partidos políticos?",
            options = listOf("A menos de 10 metros.", "A menos de 100 metros (o el perímetro de seguridad establecido legalmente alrededor del recinto).", "A menos de 5 kilómetros.", "No hay distancia mínima regulada."),
            correctOptionIndex = 1,
            explanation = "El Código Electoral prohíbe la fijación de banderas o propaganda y concentraciones proselitistas en un radio de al menos 100 metros del centro de votación.",
            normativeReference = "Código Electoral Arts. 177 y 179",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 177, 179",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 92
        ExamQuestion(
            id = "json_q92",
            category = "Ley Seca y Reglamentos",
            questionText = "¿Qué autoridad fiscaliza y hace cumplir en las calles el cumplimiento de la Ley Seca?",
            options = listOf("La Policía Nacional Civil (PNC) en coordinación con el TSE y los Cuerpos de Agentes Municipales (CAM).", "Los miembros de la JRV.", "Los vigilantes de partidos.", "El Ministerio de Hacienda."),
            correctOptionIndex = 0,
            explanation = "La fuerza pública y autoridades municipales son los órganos ejecutores para el decomiso de producto y cierre de locales infractores.",
            normativeReference = "Código Electoral Arts. 284 y 290",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 284, 290",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 93
        ExamQuestion(
            id = "json_q93",
            category = "Ley Seca y Reglamentos",
            questionText = "Si las elecciones son el domingo 4 de febrero, ¿cuándo finaliza formalmente la Ley Seca?",
            options = listOf("El domingo a las 5:00 PM.", "El lunes 5 de febrero a las 23:59 horas (11:59 PM).", "El martes a mediodía.", "El domingo a la medianoche."),
            correctOptionIndex = 1,
            explanation = "La prohibición abarca todo el día hábil siguiente a la votación, culminando a la medianoche del lunes.",
            normativeReference = "Código Electoral Art. 284",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 284",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 94
        ExamQuestion(
            id = "json_q94",
            category = "Ley Seca y Reglamentos",
            questionText = "¿Qué ocurre con los espectáculos públicos de carácter masivo el día de la elección?",
            options = listOf("Se realizan normalmente.", "Quedan totalmente prohibidos por el Código Electoral durante la jornada de votación para no alterar el orden público.", "Solo se permiten conciertos de rock.", "Solo se permiten partidos de fútbol."),
            correctOptionIndex = 1,
            explanation = "El Art. 285 del Código Electoral prohíbe funciones de espectáculos públicos o eventos deportivos masivos el día de las elecciones hasta el cierre de urnas.",
            normativeReference = "Código Electoral Art. 285",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 285",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 95
        ExamQuestion(
            id = "json_q95",
            category = "Ley Seca y Reglamentos",
            questionText = "¿Puede la Policía Nacional Civil ingresar armada a un aula de votación de una JRV sin llamamiento previo?",
            options = listOf("Sí, en cualquier momento que deseen.", "No, solo pueden ingresar al aula a requerimiento expreso del Presidente de la JRV o para emitir su propio voto si corresponde.", "Solo si hay cámaras de TV.", "Tienen prohibido entrar a los centros de votación."),
            correctOptionIndex = 1,
            explanation = "Para garantizar la autonomía civil de la mesa, las fuerzas policiales permanecen en los pasillos y solo acceden al aula a requerimiento del Presidente de la mesa.",
            normativeReference = "Código Electoral Arts. 102 y 290",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 102, 290",
            sourcePage = 3,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 96
        ExamQuestion(
            id = "json_q96",
            category = "Ley Seca y Reglamentos",
            questionText = "¿Qué vigencia debe tener el Documento Único de Identidad (DUI) para votar, salvo decreto legislativo en contrario?",
            options = listOf("Puede tener 20 años de vencido.", "Debe estar vigente a la fecha del evento electoral, salvo norma especial de prórroga aprobada por la Asamblea Legislativa.", "Solo importa que se vea la foto.", "No se necesita DUI."),
            correctOptionIndex = 1,
            explanation = "La regla general del Art. 31 del Código Electoral exige DUI vigente; solo decretos legislativos de excepción habilitan DUIs vencidos para comicios puntuales.",
            normativeReference = "Código Electoral Art. 31",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 31",
            sourcePage = 2,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 97
        ExamQuestion(
            id = "json_q97",
            category = "Ley Seca y Reglamentos",
            questionText = "Si un votante ingresa ebrio y alterando el orden a las 4:30 PM, ¿qué corresponde aplicar?",
            options = listOf("Sanción por infracción a la Ley Seca, desalojo del recinto por la PNC y privación de entrada.", "Permitirle votar rápido.", "Aplaudirle.", "Darlo por ganador de la mesa."),
            correctOptionIndex = 0,
            explanation = "El ciudadano en estado de ebriedad infringe la Ley Seca e incurre en alteración del orden, procediendo su remisión por parte de la PNC.",
            normativeReference = "Código Electoral Arts. 284 y 290",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 284, 290",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 98
        ExamQuestion(
            id = "json_q98",
            category = "Ley Seca y Reglamentos",
            questionText = "¿Con cuánto tiempo de antelación al día de las elecciones inicia el periodo del Silencio Electoral?",
            options = listOf("24 horas antes.", "72 horas antes (3 días completos antes del domingo).", "1 hora antes.", "1 mes antes."),
            correctOptionIndex = 1,
            explanation = "El Art. 175 del Código Electoral estipula taxativamente que la propaganda electoral se suspende 3 días (72 horas) antes del evento de votación.",
            normativeReference = "Código Electoral Art. 175",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Art. 175",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 99
        ExamQuestion(
            id = "json_q99",
            category = "Ley Seca y Reglamentos",
            questionText = "¿Qué tipo de propaganda está prohibida el propio día de las elecciones?",
            options = listOf("Solo la propaganda radial.", "Toda forma de propaganda: vallas, altoparlantes, entrega de hojas volantes, banderas o movilizaciones de persuasión.", "Solo la de candidatos independientes.", "Ninguna."),
            correctOptionIndex = 1,
            explanation = "El día de la elección está prohibida toda manifestación, sonido o difusión de propaganda política electoral en todo el territorio de la República.",
            normativeReference = "Código Electoral Arts. 175 y 177",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 175, 177",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        ),
        // Pregunta JSON 100
        ExamQuestion(
            id = "json_q100",
            category = "Ley Seca y Reglamentos",
            questionText = "¿Cuál es la consecuencia legal de que la JRV inicie la recepción de votos ANTES de las 7:00 AM?",
            options = listOf("Una felicitación por puntualidad.", "Incurre en nulidad o irregularidad procedimental que puede ser impugnada por los fiscales e incurre en sanción a la mesa.", "Ninguna consecuencia.", "El cierre a las 4:00 PM."),
            correctOptionIndex = 1,
            explanation = "El inicio anticipado vulnera las garantías de fiscalización partidaria e igualdad ciudadana, pudiendo viciar de nulidad la votación de la mesa.",
            normativeReference = "Código Electoral Arts. 198 y 270",
            sourceDocument = "Código Electoral de El Salvador",
            sourceArticle = "Arts. 198, 270",
            sourcePage = 4,
            sourceDocumentId = "codigo_electoral_decreto_413"
        )
    )

    // =========================================================================
    // BANCO TOTAL INTEGRADO (125 PREGUNTAS = 25 PREVIAS + 100 NUEVAS)
    // =========================================================================
    val all125Questions: List<ExamQuestion> = initial25Questions + json100Questions

    /**
     * Selecciona 25 preguntas aleatorias del banco de 125 preguntas asegurando
     * no repetición y una distribución equilibrada de categorías temáticas.
     */
    fun getRandom25ExamQuestions(): List<ExamQuestion> {
        val grouped = all125Questions.groupBy { it.category }
        val selected = mutableListOf<ExamQuestion>()

        // Tomar de manera balanceada de cada categoría disponible
        val categories = grouped.keys.shuffled()
        val perCategoryTarget = (25 / categories.size.coerceAtLeast(1)).coerceAtLeast(1)

        for (cat in categories) {
            val list = grouped[cat]?.shuffled() ?: emptyList()
            selected.addAll(list.take(perCategoryTarget))
        }

        // Si faltan para llegar a 25, rellenar del resto no seleccionado
        if (selected.size < 25) {
            val remaining = (all125Questions - selected.toSet()).shuffled()
            selected.addAll(remaining.take(25 - selected.size))
        }

        // Si excede 25, recortar a exactamente 25
        return selected.shuffled().take(25)
    }
}
