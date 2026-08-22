package com.example.data

object ElectoralSimulationRepository {

    // ==========================================
    // SECCIÓN 1: CASOS Y SOLUCIONES INTERACTIVAS
    // ==========================================
    val casesAndSolutions: List<CaseSolution> = listOf(
        // ----------------------------------------------------
        // ETAPA: INSTALACIÓN
        // ----------------------------------------------------
        CaseSolution(
            id = "inst_urnas_abiertas",
            title = "Urnas abiertas al llegar al centro de votación",
            stage = "Instalación",
            situationDescription = "Al presentarse los miembros de la JRV a las 6:00 AM para la preparación del centro de votación, encuentran que las urnas plásticas o cajas de votación asignadas a la mesa ya se encuentran armadas y abiertas sin precintos o en condiciones no selladas.",
            contextDescription = "Los vigilantes de partidos sospechan que las urnas pudieron haber sido alteradas o contener material no autorizado antes de la instalación formal.",
            entitiesInvolved = listOf(
                EntityInvolved("Presidente de JRV", "Máxima autoridad de la mesa", "Inspeccionar las urnas en presencia de todos los miembros y vigilantes."),
                EntityInvolved("Secretario de JRV", "Fedatario de la mesa", "Levantar el acta de incidencias y verificar que las urnas queden completamente vacías."),
                EntityInvolved("JEM / DOE", "Organismos de supervisión y logística", "Verificar la legalidad de los materiales y proveer nuevos marchamos de seguridad."),
                EntityInvolved("Vigilantes Partidarios", "Fiscalizadores acreditados", "Comprobar visualmente que el interior de las urnas esté vacío antes de su cierre.")
            ),
            whatShouldBeDone = "La JRV debe colocar las urnas frente a todos los presentes, abrirlas totalmente, colocarlas boca abajo a la vista pública para demostrar que están 100% vacías, y proceder a cerrarlas y sellarlas conforme al instructivo oficial del TSE.",
            stepByStepProcedure = listOf(
                "1. A las 6:00 AM, el Presidente exhibe públicamente la urna abierta ante todos los integrantes de mesa y vigilantes acreditados.",
                "2. Se verifica ocularmente que no exista ningún documento, papeleta u objeto en su interior.",
                "3. El Secretario hace constar en el Acta de Instalación que las urnas se comprobaron vacías.",
                "4. Se procede al cerrado y colocación de los sellos/precintos de seguridad oficiales del TSE.",
                "5. Se ubica la urna junto a la mesa de trabajo de la JRV a la vista de todos (Art. 194)."
            ),
            actionsNotToDo = listOf(
                "NO introducir papeletas ni votos previos bajo ninguna excusa.",
                "NO ocultar la urna de la vista de los vigilantes acreditados.",
                "NO rechazar el inicio de la votación si se comprueba fehacientemente que la urna está vacía y se sella debidamente."
            ),
            correctSolution = "Comprobar públicamente que el depósito de votos se encuentra completamente vacío, cerrarlo, asegurarlo con sus marchamos y hacerlo constar en el Acta de Instalación.",
            whySolutionIsCorrect = "El Art. 192 y Art. 194 del Código Electoral exigen expresamente que antes de iniciar la votación se compruebe que el depósito de los votos se encuentre vacío, garantizando la transparencia.",
            legalNormativeRef = "• Lo establecido por la normativa: Código Electoral Arts. 192 y 194.\n• Recomendación/procedimiento de actuación: Realizar la verificación de cara a los vigilantes y solicitar asistencia de la JEM si los marchamos o sellos están dañados.",
            libraryDocumentRef = "Código Electoral de El Salvador (Arts. 192 y 194)"
        ),

        CaseSolution(
            id = "inst_equipo_doe_incompleto",
            title = "El equipo entregado por la DOE está incompleto",
            stage = "Instalación",
            situationDescription = "Al abrir la caja o paquete electoral entregado por la Dirección de Organización Electoral (DOE), la JRV constata que falta parte del kit (por ejemplo: almohadilla de tinta, sellos oficiales de la JRV, crayones o formularios de actas).",
            contextDescription = "La jornada debe iniciar a las 7:00 AM y la falta de un insumo crítico puede retrasar la recepción de votantes.",
            entitiesInvolved = listOf(
                EntityInvolved("Presidente de JRV", "Coordinador de la mesa", "Hacer inventario minucioso del paquete y notificar de inmediato al delegado de la JEM/DOE."),
                EntityInvolved("Secretario de JRV", "Encargado de registros", "Anotar en la hoja de control qué insumos específicos faltan."),
                EntityInvolved("Delegado JEM / Auxiliar DOE", "Soporte logístico electoral", "Proveer inmediatamente los insumos de reserva del centro de votación."),
                EntityInvolved("Vigilantes Partidarios", "Fiscalizadores", "Acompañar la recepción de los materiales complementarios.")
            ),
            whatShouldBeDone = "El Presidente de la JRV debe dar aviso inmediato al delegado de la JEM y al personal de logística de la DOE en el centro de votación para que suministren los materiales faltantes del paquete de contingencia.",
            stepByStepProcedure = listOf(
                "1. Cotejar el contenido del paquete electoral contra la lista de empaque oficial provista por el TSE.",
                "2. Identificar el material faltante sin dispersar los demás elementos.",
                "3. El Presidente acude al Delegado de la JEM o Coordinador de Centro de la DOE.",
                "4. La JEM/DOE entrega el insumo faltante del stock de reserva con recibo firmado.",
                "5. Se asienta la entrega en el Acta de Instalación y se continúa con la apertura a las 7:00 AM."
            ),
            actionsNotToDo = listOf(
                "NO utilizar sellos o insumos particulares no autorizados por el TSE.",
                "NO suspender la jornada electoral sin haber solicitado auxilio a la JEM.",
                "NO recibir materiales de personas no acreditadas oficialmente como personal del TSE/JEM."
            ),
            correctSolution = "Solicitar de inmediato el suministro de los materiales faltantes al delegado de la Junta Electoral Municipal (JEM) o personal de la DOE del centro.",
            whySolutionIsCorrect = "El Art. 98 literal b y Art. 189 del Código Electoral ordenan que la JEM y la DOE tienen la responsabilidad de entregar y reponer de forma oportuna todos los materiales y papelería requeridos.",
            legalNormativeRef = "• Lo establecido por la normativa: Código Electoral Arts. 98 lit. b, 108 lit. a y 189.\n• Recomendación/procedimiento de actuación: Reportar la falta antes de las 6:30 AM para que la votación comience puntualmente a las 7:00 AM.",
            libraryDocumentRef = "Código Electoral de El Salvador (Arts. 98 y 189)"
        ),

        CaseSolution(
            id = "inst_faltan_papeletas",
            title = "Faltan papeletas de votación en el paquete electoral",
            stage = "Instalación",
            situationDescription = "Al realizar el conteo inicial de papeletas a las 6:15 AM, el Secretario de la JRV verifica que el padrón de la mesa tiene 600 electores, pero el paquete solo contiene 550 papeletas (o existe un faltante en la correlatividad numérica).",
            contextDescription = "Los vigilantes de mesa exigen resolver la diferencia antes de firmar el acta de instalación.",
            entitiesInvolved = listOf(
                EntityInvolved("Secretario de JRV", "Responsable del conteo de papeletas", "Contar, verificar la correlatividad y asentar el total exacto recibido."),
                EntityInvolved("Presidente de JRV", "Representante legal de mesa", "Solicitar reposición a la JEM y consignar el faltante exacto."),
                EntityInvolved("JEM (Junta Electoral Municipal)", "Autoridad superior en el centro", "Proveer papeletas de reposición del 1% de reserva o certificar el número exacto asignado (Art. 187)."),
                EntityInvolved("Fiscal Electoral", "Garante de legalidad", "Verificar la autenticidad y correlatividad de la entrega.")
            ),
            whatShouldBeDone = "El Secretario consigna en el Acta de Instalación el número exacto y correlativo de las papeletas realmente recibidas. El Presidente solicita de inmediato a la JEM la verificación y reposición con el fondo de reserva del 1% (Art. 187).",
            stepByStepProcedure = listOf(
                "1. Realizar un segundo conteo conjunto entre Presidente, Secretario y vigilantes para confirmar la cifra.",
                "2. Registrar en el Acta de Instalación la numeración correlativa inicial y final exacta de las papeletas recibidas.",
                "3. Notificar inmediatamente por escrito a la JEM y al Fiscal Electoral.",
                "4. La JEM provee las papeletas complementarias de su fondo de reserva con acta de entrega.",
                "5. Si no hubiere papeletas adicionales, se asienta la cifra exacta recibida en el Acta preliminar (Art. 202 lit. j)."
            ),
            actionsNotToDo = listOf(
                "NO inventar números ni alterar la correlatividad de las papeletas.",
                "NO tomar papeletas de otra JRV sin la autorización formal y acta de la JEM.",
                "NO negarse a firmar el acta; se debe consignar la verdad material con voto razonado si corresponde."
            ),
            correctSolution = "Consignar en el acta la correlatividad y cantidad exacta recibida, y solicitar a la JEM la reposición correspondiente del paquete de reserva.",
            whySolutionIsCorrect = "El Art. 187 y Art. 202 lit. j del Código Electoral contemplan que el TSE imprime un 1% adicional para reposición y obligan a registrar con precisión las papeletas faltantes y su causa en el acta.",
            legalNormativeRef = "• Lo establecido por la normativa: Código Electoral Arts. 187, 192 y 202 lit. j.\n• Recomendación/procedimiento de actuación: Asegurar que todo quede respaldado con firmas de los miembros y vigilantes en la hoja de incidencias.",
            libraryDocumentRef = "Código Electoral de El Salvador (Arts. 187 y 202)"
        ),

        CaseSolution(
            id = "inst_personas_sin_credenciales",
            title = "Personas que ingresaron al centro de votación pero no tienen sus credenciales correspondientes",
            stage = "Instalación",
            situationDescription = "A las 6:20 AM, durante la preparación de las mesas, se detectan personas dentro del centro escolar afirmando ser miembros de mesa o vigilantes de partido, pero carecen de credencial oficial extendida por el TSE o la JEM.",
            contextDescription = "Pretenden tomar asiento en la JRV o presenciar la apertura del paquete electoral.",
            entitiesInvolved = listOf(
                EntityInvolved("Presidente de JRV", "Dirección de la mesa", "Exigir la presentación obligatoria de credencial oficial y DUI vigente."),
                EntityInvolved("Delegado JEM / Jefe de Centro", "Administración del centro", "Verificar listados oficiales de acreditados y ordenar el desalojo de personas no acreditadas."),
                EntityInvolved("PNC (Policía Nacional Civil)", "Fuerza pública", "Retirar del recinto a quienes no posean acreditación electoral válida.")
            ),
            whatShouldBeDone = "El Presidente de JRV o la JEM deben impedir que personas sin credencial integren la mesa o permanezcan en el área restringida de instalación. Si se rehúsan a salir, se requiere el auxilio inmediato de la PNC.",
            stepByStepProcedure = listOf(
                "1. Solicitar la credencial oficial sellada por el TSE y el DUI original vigente.",
                "2. Cotejar los datos con la nómina oficial de la mesa.",
                "3. Si la persona no presenta credencial válida, se le indica cortésmente que debe abandonar el área de JRV.",
                "4. Si insiste en usurpar funciones, se notifica al Delegado de la JEM y al Fiscal Electoral.",
                "5. La PNC desaloja a la persona del centro (o procede a su detención si comete delito de usurpación, Art. 250)."
            ),
            actionsNotToDo = listOf(
                "NO permitir que nadie tome posesión de un cargo en JRV sin credencial legal (Art. 191).",
                "NO aceptar fotocopias simples sin certificación o cartas informales no emitidas por la autoridad electoral.",
                "NO permitir que permanezcan en pasillos interfiriendo con la instalación."
            ),
            correctSolution = "Denegar la integración e ingreso a la mesa y ordenar su salida del centro mediante la JEM y la PNC.",
            whySolutionIsCorrect = "El Art. 191 del Código Electoral prohíbe taxativamente que alguien forme parte de la JRV sin ser debidamente nombrado o acreditado, y el Art. 250 sanciona penalmente la usurpación de cargos electorales.",
            legalNormativeRef = "• Lo establecido por la normativa: Código Electoral Arts. 191, 237 y 250.\n• Recomendación/procedimiento de actuación: Mantener estricto control de acceso en portones desde las 5:00 AM junto con los delegados de la JEM.",
            libraryDocumentRef = "Código Electoral de El Salvador (Arts. 191 y 250)"
        ),

        CaseSolution(
            id = "inst_arma_cortante_abrir_cajas",
            title = "Miembro de mesa o vigilante porta un arma cortante con la excusa de abrir cajas de la DOE",
            stage = "Instalación",
            situationDescription = "Un integrante de la JRV o vigilante de partido saca un cuchillo, navaja u objeto cortopunzante de tamaño considerable, justificando que lo utilizará como herramienta para cortar los precintos y cintas de las cajas entregadas por la DOE.",
            contextDescription = "La presencia del arma genera inquietud y temor entre los demás miembros de mesa.",
            entitiesInvolved = listOf(
                EntityInvolved("Presidente de JRV", "Responsable de la disciplina", "Exigir el guardado o entrega inmediata del objeto y llamar a la PNC."),
                EntityInvolved("PNC (Policía Nacional Civil)", "Seguridad y control de armas", "Incautar el objeto cortopunzante y verificar que no existan armas en el recinto."),
                EntityInvolved("JEM / Fiscal Electoral", "Supervisión", "Asentar la infracción y evaluar sustitución si hubo amenaza.")
            ),
            whatShouldBeDone = "El Presidente de JRV debe ordenarle inmediatamente que guarde o entregue el arma cortante y requerir a la PNC destacada. Para abrir las cajas se deben utilizar exclusivamente tijeras de punta redonda o herramientas de oficina provistas en el kit del TSE.",
            stepByStepProcedure = listOf(
                "1. El Presidente de JRV le solicita abstenerse de exhibir armas cortantes en la mesa.",
                "2. Se solicita el apoyo del agente de la PNC destacado en el centro de votación.",
                "3. La PNC interviene, retira el objeto peligroso del área de votación y realiza la prevención legal.",
                "4. Se utilizan los implementos seguros suministrados en el paquete oficial de útiles electorales.",
                "5. Se deja constancia en la hoja de incidencias de la mesa."
            ),
            actionsNotToDo = listOf(
                "NO tolerar la portación ostentosa de armas blancas ni de fuego en la mesa (Art. 290).",
                "NO usar la fuerza física por cuenta de los miembros de JRV; debe actuar la PNC.",
                "NO permitir intimidación hacia otros miembros o electores."
            ),
            correctSolution = "Hacer retirar de inmediato el arma cortante mediante intervención de la PNC y utilizar únicamente los útiles escolares/oficina del kit electoral.",
            whySolutionIsCorrect = "El Art. 290 del Código Electoral prohíbe terminantemente la portación de armas de cualquier naturaleza en los lugares de votación, con la única excepción de los miembros de la PNC en servicio.",
            legalNormativeRef = "• Lo establecido por la normativa: Código Electoral Art. 290.\n• Recomendación/procedimiento de actuación: Solicitar al personal de la DOE tijeras de seguridad para la apertura de paquetes.",
            libraryDocumentRef = "Código Electoral de El Salvador (Art. 290)"
        ),

        // ----------------------------------------------------
        // ETAPA: VOTACIÓN
        // ----------------------------------------------------
        CaseSolution(
            id = "vot_muestra_voto",
            title = "El votante muestra su voto a las personas que se encuentran cerca",
            stage = "Votación",
            situationDescription = "Un ciudadano, tras marcar su papeleta en el anaquel de votación, sale exhibiendo públicamente la papeleta abierta mostrando a los vigilantes y público por cuál partido votó, antes de depositarla en la urna.",
            contextDescription = "Un vigilante exige que se anule el voto de inmediato por violar el secreto del sufragio.",
            entitiesInvolved = listOf(
                EntityInvolved("Presidente de JRV", "Garante del sufragio", "Prevenir al elector sobre el secreto del voto y ordenar que doble la papeleta."),
                EntityInvolved("Secretario de JRV", "Registro", "Verificar el proceso de firma y devolución de DUI."),
                EntityInvolved("Elector", "Votante", "Doblar debidamente su papeleta y depositarla en la urna."),
                EntityInvolved("Vigilantes Partidarios", "Observadores", "Consignar cualquier observación o protesta sin arrebatar la papeleta.")
            ),
            whatShouldBeDone = "El Presidente debe advertir de inmediato al votante que está prohibido exhibir el voto y exigirle que doble la papeleta antes de introducirla en la urna. Si el votante ya la introdujo, el voto se computará en el escrutinio según las causales de validez o nulidad del Art. 205 y 207.",
            stepByStepProcedure = listOf(
                "1. El Presidente ordena en el acto al ciudadano doblar la papeleta para proteger el secreto del voto.",
                "2. Si la papeleta no ha sido depositada, se le instruye colocarla en la urna debidamente doblada.",
                "3. El Vocal 1 hace que el elector firme el padrón y se le entinta el dedo (Art. 197).",
                "4. Se devuelve el DUI al ciudadano.",
                "5. Si un vigilante protesta, se anota el incidente en la hoja de observaciones, pero NO se extrae la papeleta de la urna en ese momento."
            ),
            actionsNotToDo = listOf(
                "NO meter la mano en la urna para intentar sacar la papeleta.",
                "NO agredir verbal ni físicamente al ciudadano.",
                "NO impedir la firma y entintado del ciudadano si este ya emitió su voto."
            ),
            correctSolution = "Conminar al elector a doblar su papeleta de inmediato y registrar la incidencia si un vigilante formula reclamo, resolviendo la calificación final en el escrutinio de las 5:00 PM.",
            whySolutionIsCorrect = "El Art. 3 y Art. 194 del Código Electoral disponen que el voto es secreto y los anaqueles garantizan esa privacidad; sin embargo, las urnas no pueden abrirse antes de las 17:00 horas (Art. 200).",
            legalNormativeRef = "• Lo establecido por la normativa: Código Electoral Arts. 3, 194, 197 y 200.\n• Recomendación/procedimiento de actuación: Exhortar activamente a todos los electores a doblar la papeleta en el anaquel antes de caminar hacia la urna.",
            libraryDocumentRef = "Código Electoral de El Salvador (Arts. 3 y 194)"
        ),

        CaseSolution(
            id = "vot_vigilante_niega_entrada",
            title = "Un vigilante intenta negar la entrada a un votante por ideales políticos",
            stage = "Votación",
            situationDescription = "Un vigilante de partido reconoce a un ciudadano en la fila, sabe por qué partido simpatiza y le bloquea el paso hacia la mesa afirmando que 'no es bienvenido' o cuestionando indebidamente su presencia.",
            contextDescription = "Se produce un altercado en la entrada del aula de la mesa electoral.",
            entitiesInvolved = listOf(
                EntityInvolved("Presidente de JRV", "Autoridad máxima de mesa", "Detener la acción abusiva del vigilante y garantizar el paso del elector."),
                EntityInvolved("Vigilante Infractor", "Fiscalizador", "Debe limitarse a sus funciones y cesar de inmediato la coacción."),
                EntityInvolved("Fiscal Electoral / JEM", "Control de legalidad", "Evaluar la expulsión y privación de funciones del vigilante (Art. 129)."),
                EntityInvolved("PNC", "Fuerza pública", "Garantizar la protección del elector y ejecutar captura si hay delito de coacción electoral (Art. 252).")
            ),
            whatShouldBeDone = "El Presidente de JRV debe ordenar al vigilante apartarse de inmediato y permitir que el ciudadano vote si porta su DUI y está en el padrón. Si el vigilante persiste, el Presidente y la JEM lo privan de su función y lo expulsan (Art. 129).",
            stepByStepProcedure = listOf(
                "1. El Presidente interviene enérgicamente recordando al vigilante que NO tiene autoridad para impedir el acceso.",
                "2. Se verifica el DUI del ciudadano en el padrón electoral de la mesa.",
                "3. Se le entrega la papeleta y se le permite votar con total normalidad.",
                "4. Si el vigilante continúa obstaculizando o amenaza al elector, la JRV aplica el Art. 129 (privación de funciones) llamando a su suplente.",
                "5. Se da cuenta inmediata a la PNC y a la Fiscalía General de la República por posible coacción electoral (Art. 252)."
            ),
            actionsNotToDo = listOf(
                "NO permitir que los vigilantes asuman funciones de control policial o de admisión.",
                "NO negar el sufragio a ningún ciudadano con DUI vigente que figure en el padrón (Art. 4).",
                "NO tolerar actos de discriminación o intolerancia política en el centro."
            ),
            correctSolution = "Garantizar el ejercicio del voto del ciudadano y expulsar/remover de sus funciones al vigilante infractor mediante el Art. 129 del Código Electoral.",
            whySolutionIsCorrect = "El Art. 4 del Código Electoral señala que nadie puede impedir, coartar o perturbar el ejercicio del sufragio. El Art. 129 faculta a privar de funciones inmediatamente al vigilante que interrumpa o interfiera con el proceso.",
            legalNormativeRef = "• Lo establecido por la normativa: Código Electoral Arts. 4, 129 y 252; Art. 72 de la Constitución.\n• Recomendación/procedimiento de actuación: Llamar de inmediato al Delegado de la JEM y al Fiscal Electoral para que asienten la denuncia penal.",
            libraryDocumentRef = "Código Electoral de El Salvador (Arts. 4 y 129)"
        ),

        CaseSolution(
            id = "vot_se_terminaron_crayones",
            title = "Se terminaron los crayones y no queda ninguno disponible en la mesa",
            stage = "Votación",
            situationDescription = "Debido al flujo continuo de votantes o extravío accidental, los crayones negros suministrados por el TSE en los anaqueles se terminan o desgastan por completo y ya no hay ninguno útil en la mesa.",
            contextDescription = "Hay varios votantes esperando en el anaquel para marcar su papeleta.",
            entitiesInvolved = listOf(
                EntityInvolved("Presidente de JRV", "Gestión de mesa", "Pausar momentáneamente la entrega de papeletas y solicitar repuestos a la JEM."),
                EntityInvolved("Delegado JEM / DOE", "Logística", "Entregar inmediatamente crayones o marcadores oficiales del paquete de contingencia."),
                EntityInvolved("Electores en espera", "Votantes", "Aguardar de forma ordenada mientras se repone el material.")
            ),
            whatShouldBeDone = "El Presidente acude de inmediato al Delegado de la JEM o al personal de la DOE para recibir crayones o bolígrafos oficiales del stock de reserva del centro de votación.",
            stepByStepProcedure = listOf(
                "1. Informar a los electores que esperan que se repondrán los marcadores en 2 minutos.",
                "2. El Presidente o Vocal solicita al Delegado de la JEM o Supervisor de la DOE los crayones de reserva.",
                "3. La JEM entrega los nuevos crayones oficiales del TSE.",
                "4. Se colocan en los anaqueles de votación y se reanuda de inmediato el flujo de votación."
            ),
            actionsNotToDo = listOf(
                "NO utilizar plumones con tinta que traspase la papeleta o manche otras casillas al doblarse.",
                "NO suspender la votación por tiempo prolongado.",
                "NO permitir que personas ajenas introduzcan materiales no autorizados."
            ),
            correctSolution = "Solicitar de inmediato los crayones o marcadores de reposición al delegado de la JEM o logística de la DOE del centro.",
            whySolutionIsCorrect = "El Art. 98 literal b del Código Electoral mandata a la JEM a garantizar la oportuna entrega y abastecimiento de todos los objetos y papelería requeridos durante la jornada.",
            legalNormativeRef = "• Lo establecido por la normativa: Código Electoral Arts. 98 lit. b y 189.\n• Recomendación/procedimiento de actuación: Mantener siempre a mano los útiles de reserva antes de que el último crayón se gaste por completo.",
            libraryDocumentRef = "Código Electoral de El Salvador (Arts. 98 y 189)"
        ),

        CaseSolution(
            id = "vot_olvido_solicitar_firma",
            title = "Al secretario se le olvidó solicitar la firma al votante y este ya emitió su voto y se retiró",
            stage = "Votación",
            situationDescription = "Un elector presentó su DUI, recibió la papeleta, votó en el anaquel y la depositó en la urna. Por distracción de la mesa, el ciudadano no firmó el padrón de firmas ni se le aplicó la tinta, y ya salió del centro de votación.",
            contextDescription = "Al revisar el padrón, el 1er Vocal nota el espacio de firma en blanco, pero el nombre está sellado en el padrón de búsqueda.",
            entitiesInvolved = listOf(
                EntityInvolved("Secretario de JRV", "Fedatario", "Asentar la incidencia de forma inmediata en la hoja de control de la mesa."),
                EntityInvolved("Primer Vocal", "Encargado del Padrón de Firmas", "Verificar el correlativo y confirmar el número de DUI del ciudadano que votó (Art. 197)."),
                EntityInvolved("Presidente de JRV", "Dirección", "Coordinar con los vigilantes el registro exacto para el cuadre del escrutinio final.")
            ),
            whatShouldBeDone = "La JRV debe levantar una nota inmediata en la Hoja de Incidencias/Observaciones describiendo el hecho, consignando el número de DUI y nombre del elector, para que al momento del escrutinio cuadre el conteo de papeletas con las firmas.",
            stepByStepProcedure = listOf(
                "1. Confirmar con todos los miembros y vigilantes presentes que el ciudadano efectivamente emitió su voto.",
                "2. Anotar en la casilla correspondiente del padrón y en la hoja de incidencias: 'Votó sin firmar por omisión involuntaria de mesa'.",
                "3. Registrar el nombre y número de DUI del elector.",
                "4. Todos los miembros de JRV y vigilantes firman la anotación en la hoja de incidencias.",
                "5. En el escrutinio de las 5:00 PM, considerar dicha papeleta en el cuadre matemático de votantes (Art. 202 lit. k)."
            ),
            actionsNotToDo = listOf(
                "NO falsificar la firma ni colocar la huella de otra persona en el padrón.",
                "NO pretender anular votos ya depositados en la urna abriéndola antes de las 5:00 PM.",
                "NO ocultar la omisión a los vigilantes de partidos."
            ),
            correctSolution = "Asentar de inmediato la observación en la hoja de incidencias con indicación del DUI del elector, firmada por la JRV y vigilantes, sin falsear jamás la firma del padrón.",
            whySolutionIsCorrect = "El Art. 197 y Art. 202 lit. l del Código Electoral estipulan que todas las incidencias deben constar por escrito en el acta, y falsear firmas constituye delito electoral grave sancionado por el Código Penal.",
            legalNormativeRef = "• Lo establecido por la normativa: Código Electoral Arts. 197, 202 lit. k y l; Código Penal Art. 293.\n• Recomendación/procedimiento de actuación: Reforzar la atención del Vocal 1 para que retenga el DUI hasta que el elector haya firmado y recibido la tinta indeleble.",
            libraryDocumentRef = "Código Electoral de El Salvador (Arts. 197 y 202)"
        ),

        CaseSolution(
            id = "vot_olvido_su_dui",
            title = "El votante olvidó su DUI y presenta otro documento (licencia o pasaporte)",
            stage = "Votación",
            situationDescription = "Un ciudadano llega a la mesa electoral indicando que dejó su DUI en casa, pero presenta su licencia de conducir vigente, pasaporte salvadoreño o carné de trabajo, solicitando que lo dejen votar porque aparece en el padrón con su foto.",
            contextDescription = "El ciudadano argumenta que su identidad está probada con la licencia y que tiene prisa.",
            entitiesInvolved = listOf(
                EntityInvolved("Secretario de JRV", "Revisión documental", "Verificar los documentos y explicar los requisitos legales."),
                EntityInvolved("Presidente de JRV", "Resolución de mesa", "Informar de manera respetuosa pero firme la obligatoriedad del DUI."),
                EntityInvolved("Elector", "Ciudadano", "Debe acudir a traer su DUI original vigente para poder votar.")
            ),
            whatShouldBeDone = "La JRV debe denegar cortésmente la entrega de la papeleta de votación y orientar al ciudadano para que vaya a traer su Documento Único de Identidad (DUI) original y vigente antes de las 5:00 PM.",
            stepByStepProcedure = listOf(
                "1. El Secretario examina la solicitud del ciudadano.",
                "2. Se le explica con amabilidad que el Art. 6, Art. 9 y Art. 31 del Código Electoral establecen que el DUI es el ÚNICO documento legal habilitante para votar en territorio nacional.",
                "3. Se le informa que ni la licencia de conducir, ni el pasaporte, ni carnés profesionales son válidos para sustituir el DUI en mesa.",
                "4. Se le motiva a regresar con su DUI antes de las 17:00 horas.",
                "5. No se retiene ningún documento al ciudadano y se continúa con el siguiente elector."
            ),
            actionsNotToDo = listOf(
                "NO permitir la entrega de papeleta bajo presentación de licencia, pasaporte o partida de nacimiento.",
                "NO permitir que vote con fotocopia de DUI (salvo disposición especial contraria).",
                "NO retener indebidamente la licencia ni tratar con descortesía al ciudadano."
            ),
            correctSolution = "Denegar la votación e informarle que el DUI vigente es el único documento legalmente autorizado para votar en territorio nacional.",
            whySolutionIsCorrect = "El Art. 6, Art. 9 lit. d y Art. 31 del Código Electoral establecen que el Documento Único de Identidad (DUI) vigente es el único que acredita al ciudadano para emitir el sufragio.",
            legalNormativeRef = "• Lo establecido por la normativa: Código Electoral Arts. 6, 9 lit. d, 31 y 34.\n• Recomendación/procedimiento de actuación: Explicar al votante el horario de cierre (5:00 PM) para que tenga tiempo suficiente de traer su documento.",
            libraryDocumentRef = "Código Electoral de El Salvador (Arts. 6, 9 y 31)"
        ),

        // ----------------------------------------------------
        // ETAPA: ESCRUTINIO
        // ----------------------------------------------------
        CaseSolution(
            id = "esc_marca_corazon_sobre_bandera",
            title = "Aparece un voto con una marca que no es una X (un corazón u otra marca) únicamente sobre una bandera",
            stage = "Escrutinio",
            situationDescription = "Al abrir la urna y revisar las papeletas durante el escrutinio preliminar, se encuentra una papeleta donde el elector dibujó un corazón, un círculo o un check mark ubicado claramente y de forma exclusiva sobre la bandera de un solo partido político.",
            contextDescription = "Un vigilante rival alega que debe ser voto nulo porque 'no es una cruz o una X'.",
            entitiesInvolved = listOf(
                EntityInvolved("Presidente de JRV", "Calificador de votos", "Evaluar la intención del votante y calificar el voto."),
                EntityInvolved("Secretario de JRV", "Registro de votos", "Asentar el voto en el rubro de votos válidos del partido correspondiente."),
                EntityInvolved("Vigilantes de Partidos", "Fiscalizadores", "Observar la papeleta y, si no están de acuerdo, solicitar voto impugnado razonado.")
            ),
            whatShouldBeDone = "La JRV debe calificar el voto como VOTO VÁLIDO a favor del partido cuya bandera fue marcada, ya que la ley ampara cualquier marca inequívoca que demuestre la voluntad del elector.",
            stepByStepProcedure = listOf(
                "1. El Presidente muestra la papeleta a todos los miembros y vigilantes.",
                "2. Se comprueba que la marca (corazón, check o raya) esté únicamente sobre una bandera y no toque banderas rivales.",
                "3. Se verifica que no contenga palabras o figuras obscenas (Art. 207 lit. h).",
                "4. El Presidente declara en voz alta: 'Voto Válido para [Partido]', fundamentándose en el Art. 197 y Art. 205.",
                "5. El Secretario registra el voto a la cuenta de dicho partido en la hoja de conteo."
            ),
            actionsNotToDo = listOf(
                "NO anular el voto por el simple hecho de no ser una X convencional.",
                "NO descalificar la voluntad del elector cuando no hay ambigüedad ni obscenidad.",
                "NO ocultar la papeleta a los vigilantes de los otros partidos."
            ),
            correctSolution = "Calificar el voto como VOTO VÁLIDO a favor del partido político marcado.",
            whySolutionIsCorrect = "El Art. 197 inc. 3° y Art. 205 inc. 1° del Código Electoral establecen que el voto se expresará haciendo 'CUALQUIER MARCA QUE INDIQUE INEQUÍVOCAMENTE SU PREFERENCIA sobre la bandera del partido político o coalición'.",
            legalNormativeRef = "• Lo establecido por la normativa: Código Electoral Arts. 197 inc. 3°, 205 inc. 1° y 207.\n• Recomendación/procedimiento de actuación: Si un vigilante objeta formalmente, se tramita como Voto Impugnado en sobre separado conforme al Art. 206.",
            libraryDocumentRef = "Código Electoral de El Salvador (Arts. 197 y 205)"
        ),

        CaseSolution(
            id = "esc_vigilante_quiere_retirarse",
            title = "Un vigilante quiere retirarse de su puesto sin justificación ni fuerza mayor",
            stage = "Escrutinio",
            situationDescription = "A las 6:30 PM, en pleno escrutinio preliminar de votos, un vigilante acreditado de un partido político manifiesta que está cansado y desea retirarse del centro sin justificación médica ni de fuerza mayor.",
            contextDescription = "Los otros miembros le preguntan si el escrutinio se anula o si debe suspenderse por su salida.",
            entitiesInvolved = listOf(
                EntityInvolved("Presidente de JRV", "Dirección del escrutinio", "Hacer constar el retiro en el acta y continuar el escrutinio sin detenerse."),
                EntityInvolved("Secretario de JRV", "Fedatario", "Asentar en el acta la hora y motivo del retiro del vigilante."),
                EntityInvolved("Vigilante Suplente", "Sustituto legal", "Asumir de inmediato la fiscalización si se encuentra presente (Art. 124).")
            ),
            whatShouldBeDone = "La JRV debe permitir la salida del vigilante, incorporar de inmediato a su suplente acreditado si está disponible, hacer constar en el acta que el vigilante se retiró voluntariamente, y CONTINUAR con el escrutinio con total normalidad.",
            stepByStepProcedure = listOf(
                "1. Se verifica si el vigilante suplente acreditado está presente en el centro para asumir la posición (Art. 124).",
                "2. Si no hay suplente, el Secretario consigna en el acta: 'El vigilante de [Partido] se retiró voluntariamente a las [Hora]'.",
                "3. La JRV continúa el conteo y llenado de actas con los miembros presentes.",
                "4. La falta de firma del vigilante por haberse retirado NO invalida el escrutinio ni las actas (Art. 128 y 209 inc. 4°)."
            ),
            actionsNotToDo = listOf(
                "NO suspender ni detener el escrutinio por el retiro de un vigilante partidario.",
                "NO retener contra su voluntad física al vigilante.",
                "NO declarar nula el acta por falta de su firma."
            ),
            correctSolution = "Hacer constar en el acta el retiro voluntario del vigilante, llamar a su suplente si está disponible y continuar ininterrumpidamente el escrutinio.",
            whySolutionIsCorrect = "El Art. 128 inc. final y Art. 209 inc. 4° del Código Electoral establecen expresamente que la falta de concurrencia de cualquier vigilante o la falta de su firma en el acta NO es motivo de nulidad.",
            legalNormativeRef = "• Lo establecido por la normativa: Código Electoral Arts. 124, 128 y 209 inc. 4°.\n• Recomendación/procedimiento de actuación: Entregar copia del acta al representante general o jefe de centro de dicho partido si lo solicitare.",
            libraryDocumentRef = "Código Electoral de El Salvador (Arts. 128 y 209)"
        ),

        CaseSolution(
            id = "esc_secretario_no_sabe_computadora",
            title = "El secretario considera que no tiene capacidad para ingresar datos en la computadora",
            stage = "Escrutinio",
            situationDescription = "Al iniciar el llenado y transmisión informática de los resultados de escrutinio, el Secretario de la JRV manifiesta que no sabe utilizar el equipo tecnológico provisto por el TSE (laptop/escáner) y se siente incapaz de digitar los datos.",
            contextDescription = "Surge la duda: ¿Quién debe ingresar o transmitir los datos al TSE y cuál es el procedimiento correcto?",
            entitiesInvolved = listOf(
                EntityInvolved("Secretario y Presidente de JRV", "Responsables del acta", "Validar los números y supervisar la digitación."),
                EntityInvolved("Otro miembro de JRV (Vocal)", "Apoyo técnico de mesa", "Asistir en la digitación en equipo con el Secretario."),
                EntityInvolved("Soporte Técnico / Auxiliar TSE", "Asistencia técnica oficial", "Brindar soporte técnico y guiar el uso del sistema sin alterar los datos decididos por la JRV."),
                EntityInvolved("Vigilantes Partidarios", "Fiscalizadores de transmisión", "Presenciar el proceso de digitación y escaneo de actas.")
            ),
            whatShouldBeDone = "El ingreso de los datos en el sistema lo realiza la JRV con apoyo de cualquier otro miembro capacitado (Vocal o Presidente) o con la asistencia técnica directa del Soporte Técnico/Auxiliar del TSE acreditado en el centro, siempre bajo la supervisión y fe pública del Secretario y Presidente.",
            stepByStepProcedure = listOf(
                "1. La JRV llena y revisa primero la Hoja de Trabajo y Borrador de Escrutinio en papel.",
                "2. Si el Secretario tiene dificultades con el software, otro miembro de la JRV con destreza digital apoya en el teclado.",
                "3. Se solicita la asistencia del Auxiliar de Soporte Técnico del TSE asignado al centro para soporte operativo.",
                "4. El Secretario dicta los datos acordados colegiadamente y verifica en pantalla que coincidan número por número.",
                "5. Se imprime el acta borrador, se revisa, se firma por la JRV y vigilantes, y se realiza la transmisión oficial (Art. 200 y 209)."
            ),
            actionsNotToDo = listOf(
                "NO permitir que vigilantes o personas ajenas digiten o modifiquen datos sin control de la JRV.",
                "NO transmitir actas sin antes confrontar y verificar la sumatoria matemática.",
                "NO dejar incompleta la transmisión de resultados al TSE."
            ),
            correctSolution = "¿Quién debe ingresar o transmitir los datos? La JRV como organismo colegiado, pudiendo delegar la digitación en otro miembro de mesa o apoyarse en el Soporte Técnico oficial del TSE, bajo estricta supervisión del Secretario.",
            whySolutionIsCorrect = "El Art. 200 y Art. 209 del Código Electoral señalan que el TSE pone a disposición sistemas tecnológicos para facilitar el escrutinio preliminar y transmisión de actas, manteniendo la JRV la plena responsabilidad del contenido.",
            legalNormativeRef = "• Lo establecido por la normativa: Código Electoral Arts. 200 y 209.\n• Recomendación/procedimiento de actuación: Realizar siempre la verificación cruzada en voz alta entre el Secretario y el Presidente antes de emitir la firma electrónica o física.",
            libraryDocumentRef = "Código Electoral de El Salvador (Arts. 200 y 209)"
        ),

        CaseSolution(
            id = "esc_pelea_fisica_vigilantes",
            title = "Conflicto entre dos vigilantes por la interpretación de un voto escala a pelea física violenta",
            stage = "Escrutinio",
            situationDescription = "Durante la discusión por la validez de una papeleta con marcas dudosas, dos vigilantes de partidos contrarios se insultan, pierden el control y comienzan a golpearse físicamente, arrojando sillas y poniendo en riesgo los materiales.",
            contextDescription = "La integridad física de los miembros de mesa y la custodia de los votos se ven gravemente amenazadas.",
            entitiesInvolved = listOf(
                EntityInvolved("Presidente de JRV", "Mando de la mesa", "Suspender momentáneamente la manipulación de papeletas, proteger las actas y llamar a la PNC."),
                EntityInvolved("PNC (Policía Nacional Civil)", "Fuerza pública", "Ingresar de inmediato, neutralizar y detener en flagrancia a los agresores."),
                EntityInvolved("Fiscal Electoral", "Acción penal", "Levantar acta de flagrancia y remitir a tribunales por delito electoral y lesiones."),
                EntityInvolved("JEM", "Autoridad superior", "Autorizar la incorporación de los vigilantes suplentes correspondientes.")
            ),
            whatShouldBeDone = "El Presidente de JRV resguarda de inmediato las papeletas y actas sobre la mesa y requiere el auxilio urgente de la Policía Nacional Civil (PNC). La PNC detiene a los agresores en flagrancia (Art. 252). La JRV los priva de funciones (Art. 129) y continúa el escrutinio con los suplentes.",
            stepByStepProcedure = listOf(
                "1. El Presidente de JRV ordena: '¡Alto al escrutinio!', y los miembros de JRV cubren y resguardan las papeletas y actas.",
                "2. Se llama inmediatamente a los agentes de la PNC destacados en el centro.",
                "3. La PNC interviene, separa y aprehende a los agresores en flagrancia por desórdenes públicos y delitos electorales (Art. 252).",
                "4. Se notifica al Fiscal Electoral para que inicie las diligencias penales pertinentes.",
                "5. Se asienta la privación de funciones en el acta (Art. 129), se llama a los vigilantes suplentes y se reanuda el escrutinio."
            ),
            actionsNotToDo = listOf(
                "NO involucrarse los miembros de mesa en la riña física.",
                "NO abandonar desatendidas las urnas ni las actas oficiales sobre la mesa.",
                "NO permitir que los agresores continúen fiscalizando en el centro."
            ),
            correctSolution = "Intervención y detención inmediata por la PNC en flagrancia, privación de funciones de ambos vigilantes conforme al Art. 129 y reanudación del escrutinio con los suplentes.",
            whySolutionIsCorrect = "El Art. 129 del Código Electoral dispone la privación inmediata de funciones por interrumpir de palabra o de obra a la JRV, y los Arts. 252 y 290 mandatan la captura inmediata por la PNC ante delitos en flagrancia.",
            legalNormativeRef = "• Lo establecido por la normativa: Código Electoral Arts. 129, 252 y 290; Código Penal (Delitos de desórdenes y lesiones).\n• Recomendación/procedimiento de actuación: Asegurar que las papeletas no se mezclen durante el incidente y verificar el conteo previo.",
            libraryDocumentRef = "Código Electoral de El Salvador (Arts. 129 y 252)"
        ),

        CaseSolution(
            id = "esc_vigilante_intenta_abrir_caja_votos",
            title = "Un vigilante intenta abrir la caja que contiene los votos",
            stage = "Escrutinio",
            situationDescription = "Al llegar las 5:00 PM para dar inicio al escrutinio, un vigilante de partido se aproxima a la urna electoral, retira los sellos por su cuenta y mete la mano en la caja para extraer papeletas antes de que el Presidente dé la orden.",
            contextDescription = "Los miembros de JRV le reclaman que solo la mesa tiene la potestad de manipular los votos.",
            entitiesInvolved = listOf(
                EntityInvolved("Presidente de JRV", "Autoridad exclusiva", "Exigir que suelte la urna y advertirle que comete delito electoral."),
                EntityInvolved("Secretario y Vocales de JRV", "Custodios de mesa", "Proteger el depósito y asentar la infracción en el acta."),
                EntityInvolved("Fiscal Electoral y PNC", "Acción penal y seguridad", "Intervenir por intento de manipulación ilícita de sufragios.")
            ),
            whatShouldBeDone = "El Presidente de JRV debe ordenarle al instante apartarse de la urna y advertirle que los vigilantes NO pueden tocar el material electoral. Si insiste o causó daño, se aplica la privación de funciones (Art. 129) y se entrega a la PNC por flagrancia de delito electoral (Art. 252).",
            stepByStepProcedure = listOf(
                "1. El Presidente y Vocales frenan la acción del vigilante y retiran la urna de su alcance.",
                "2. Se le recuerda que conforme al Art. 127 y 128 los vigilantes tienen derecho únicamente a voz y fiscalización visual, SIN manipulación física.",
                "3. El Presidente es quien oficialmente procede a abrir la urna en presencia de todos (Art. 200 lit. b).",
                "4. Si el vigilante forcejeó o intentó sustraer papeletas, se llama a la PNC y al Fiscal Electoral.",
                "5. Se asienta la conducta en el acta y se convoca a su suplente."
            ),
            actionsNotToDo = listOf(
                "NO permitir que ningún vigilante abra urnas o extraiga papeletas de voto.",
                "NO permitir la custodia de las urnas en manos de personas partidarias.",
                "NO omitir la denuncia si hubo intento de fraude o destrucción."
            ),
            correctSolution = "Impedir la manipulación física de la urna por el vigilante, recordar que su función es exclusivamente de fiscalización visual con derecho a voz, y sancionar con privación de funciones en caso de desacato.",
            whySolutionIsCorrect = "Los Arts. 127 y 128 del Código Electoral establecen que los vigilantes participan con derecho únicamente a voz para vigilar, siendo potestad exclusiva de la JRV abrir la urna y practicar el conteo (Art. 200).",
            legalNormativeRef = "• Lo establecido por la normativa: Código Electoral Arts. 127, 128, 129, 200 lit. b y 252.\n• Recomendación/procedimiento de actuación: Mantener la urna siempre en el centro de la mesa bajo control directo de los miembros de la JRV.",
            libraryDocumentRef = "Código Electoral de El Salvador (Arts. 127, 128 y 200)"
        ),

        CaseSolution(
            id = "esc_supervisor_quiere_imponer_calificacion",
            title = "Un supervisor quiere imponer o dar su opinión sobre cómo debe calificarse un voto",
            stage = "Escrutinio",
            situationDescription = "Durante el análisis de una papeleta con marcas complejas, un supervisor de partido político interviene enérgicamente sobre la mesa exigiendo al Presidente que declare el voto nulo, pretendiendo imponer su criterio por encima de la decisión de la JRV.",
            contextDescription = "El supervisor no es miembro de la mesa ni vigilante acreditado en esa JRV, sino supervisor partidario de zona.",
            entitiesInvolved = listOf(
                EntityInvolved("Presidente de JRV", "Autoridad resolutiva", "Recordarle al supervisor que la decisión de calificación corresponde exclusivamente a la JRV."),
                EntityInvolved("Supervisor Partidario", "Asesor legal partidario", "Puede brindar asesoría legal a su vigilante de mesa, pero NO votar ni imponer decisiones a la JRV (Art. 125)."),
                EntityInvolved("JRV Colegiada", "Cuerpo electoral", "Decidir por mayoría de votos de sus miembros la validez o nulidad (Art. 59 y 200).")
            ),
            whatShouldBeDone = "El Presidente debe reiterarle con respeto pero firmeza que la facultad legal de calificar los votos es competencia exclusiva y soberana de la JRV. El supervisor puede asesorar a su vigilante, y si este no concuerda, su vigilante puede solicitar que el voto sea calificado como impugnado (Art. 206).",
            stepByStepProcedure = listOf(
                "1. El Presidente aclara al supervisor que su rol según el Art. 125 es de asesoría a su vigilante, sin voto en la JRV.",
                "2. La JRV delibera y decide la calificación del voto por mayoría de sus integrantes (Art. 200 y 205).",
                "3. Si el vigilante de la mesa mantiene su discrepancia, se aplica el procedimiento de Voto Impugnado (Art. 206) colocándolo en sobre especial.",
                "4. Si el supervisor obstaculiza de forma reiterada, se solicita a la JEM su retiro de la mesa (Art. 129)."
            ),
            actionsNotToDo = listOf(
                "NO someter la decisión de la mesa a la voluntad o imposición de supervisores partidarios.",
                "NO alterar la calificación legítima por presiones externas.",
                "NO permitir que el supervisor tome la palabra como si fuera un miembro votante de la JRV."
            ),
            correctSolution = "Reafirmar la autonomía decisoria de la JRV, calificar el voto por mayoría de mesa y permitir que el vigilante acreditado ejerza su derecho de impugnación formal si lo estima conveniente.",
            whySolutionIsCorrect = "El Art. 125 del Código Electoral confiere a los supervisores la facultad de brindar asesoría legal a sus vigilantes, pero la potestad de calificar y resolver votos recae exclusivamente en la JRV (Arts. 108 y 200).",
            legalNormativeRef = "• Lo establecido por la normativa: Código Electoral Arts. 108 lit. b, 125, 127 y 206.\n• Recomendación/procedimiento de actuación: Explicar al supervisor el mecanismo formal de impugnación para que el TSE resuelva en el escrutinio final.",
            libraryDocumentRef = "Código Electoral de El Salvador (Arts. 108, 125 y 206)"
        ),

        CaseSolution(
            id = "esc_vigilante_quiere_llenar_documentos_secretario",
            title = "Un vigilante quiere llenar la hoja o documento que corresponde completar al secretario",
            stage = "Escrutinio",
            situationDescription = "Un vigilante partidario toma el bolígrafo y la hoja de borrador de escrutinio o formulario oficial de actas, aduciendo que 'tiene mejor letra' y que él se encargará de rellenar las casillas y cifras de los resultados electorales.",
            contextDescription = "El Secretario se siente intimidado o tentado a ceder la tarea documental.",
            entitiesInvolved = listOf(
                EntityInvolved("Secretario de JRV", "Fedatario legal exclusivo", "Redactar, llenar y autorizar personalmente todas las actas de la mesa."),
                EntityInvolved("Presidente de JRV", "Dirección de la mesa", "Prohibir que personas externas toquen los formularios de actas oficiales."),
                EntityInvolved("Vigilante Partidario", "Fiscalizador", "Verificar la exactitud de los números pero abstenerse de escribir en documentos oficiales.")
            ),
            whatShouldBeDone = "El Presidente y Secretario deben prohibir rotundamente que el vigilante llene las actas oficiales. La elaboración material de las actas y documentos electorales es atribución indelegable del Secretario de la JRV.",
            stepByStepProcedure = listOf(
                "1. El Presidente retira de inmediato los documentos oficiales de manos del vigilante.",
                "2. Se le informa que conforme a la ley el Secretario es el único fedatario legalmente facultado para redactar y consignar datos en las actas.",
                "3. El Secretario completa personalmente la hoja de trabajo y el acta oficial.",
                "4. Una vez concluido el llenado, se le da lectura en voz alta para que los vigilantes verifiquen y firmen (Art. 200 lit. d).",
                "5. Si el vigilante intentó alterar datos con dolo, se notifica a la Fiscalía Electoral."
            ),
            actionsNotToDo = listOf(
                "NO permitir que ningún vigilante ni supervisor escriba en los formularios de actas oficiales del TSE.",
                "NO descuidar los documentos oficiales sobre la mesa.",
                "NO firmar actas en blanco o llenadas por terceros no autorizados."
            ),
            correctSolution = "Impedir que el vigilante llene los documentos y exigir que el Secretario de la JRV cumpla personalmente su función fedataria de elaboración y firma de actas.",
            whySolutionIsCorrect = "El Art. 69 lit. c, Art. 192 y Art. 200 del Código Electoral establecen que la redacción y autorización de las actas corresponde con carácter de exclusividad al Secretario de la Junta.",
            legalNormativeRef = "• Lo establecido por la normativa: Código Electoral Arts. 69 lit. c, 192, 200 y 209; Código Penal Art. 293 (Falsedad documental).\n• Recomendación/procedimiento de actuación: Si el Secretario necesita auxilio, solo otro miembro juramentado de la JRV (como el Vocal) puede colaborar bajo su vista.",
            libraryDocumentRef = "Código Electoral de El Salvador (Arts. 192 y 200)"
        ),

        // ----------------------------------------------------
        // ETAPA: SEGURIDAD
        // ----------------------------------------------------
        CaseSolution(
            id = "seg_persona_bajo_efectos_drogas",
            title = "Una persona aparentemente bajo los efectos de drogas quiere ingresar al centro de votación",
            stage = "Seguridad",
            situationDescription = "En el portón principal del centro escolar se presenta un individuo con conducta errática, agresividad verbal y signos evidentes de intoxicación por sustancias estupefacientes o drogas, intentando ingresar a la fuerza a la zona de mesas.",
            contextDescription = "La persona genera alarma entre los ciudadanos que hacen fila para votar.",
            entitiesInvolved = listOf(
                EntityInvolved("Seguridad de Acceso / Delegado JEM", "Control perimetral", "Impedir el acceso al recinto e informar a la PNC."),
                EntityInvolved("PNC (Policía Nacional Civil)", "Orden público", "Intervenir de inmediato, contener a la persona y retirarla del centro."),
                EntityInvolved("Fiscal Electoral", "Supervisión", "Asentar reporte de la intervención preventiva.")
            ),
            whatShouldBeDone = "El personal de seguridad y delegados de la JEM deben impedirle el ingreso al centro de votación y requerir la intervención inmediata de los agentes de la PNC destacados en el portón para neutralizar el riesgo.",
            stepByStepProcedure = listOf(
                "1. Los encargados de acceso y la PNC apostada en el portón bloquean el paso a la persona intoxicada.",
                "2. Los agentes de la PNC le conminan a calmarse y lo retiran del perímetro de seguridad del centro.",
                "3. Si comete alteración grave del orden o desacato, la PNC procede a su conducción o detención conforme a la ley.",
                "4. Se restablece la fluidez y tranquilidad en la fila de electores.",
                "5. Se deja constancia en la bitácora de seguridad del centro escolar."
            ),
            actionsNotToDo = listOf(
                "NO permitir el ingreso de personas en estado de intoxicación que pongan en peligro la seguridad común.",
                "NO emplear violencia desmedida por parte de civiles; la contención es labor exclusiva de la PNC.",
                "NO descuidar el control del portón de entrada."
            ),
            correctSolution = "Impedir el ingreso al centro de votación mediante la intervención de la PNC para resguardar la seguridad de los votantes y el orden público.",
            whySolutionIsCorrect = "El Art. 4 y Art. 65 lit. e del Código Electoral facultan al mantenimiento del orden público y garantía de la libertad y seguridad del sufragio mediante el auxilio de la Policía Nacional Civil.",
            legalNormativeRef = "• Lo establecido por la normativa: Código Electoral Arts. 4, 65 lit. e y 246.\n• Recomendación/procedimiento de actuación: Aplicar el protocolo de seguridad perimetral de la PNC y brindar asistencia médica si la persona sufre una crisis de salud.",
            libraryDocumentRef = "Código Electoral de El Salvador (Arts. 4 y 65)"
        ),

        CaseSolution(
            id = "seg_persona_en_estado_ebriedad",
            title = "Una persona ingresó al centro de votación y posteriormente se descubre que está en estado de ebriedad",
            stage = "Seguridad",
            situationDescription = "Un ciudadano logró pasar el portón y llega a la mesa electoral evidenciando fuerte aliento a alcohol, dificultad para hablar y tambaleándose, provocando disturbios y discutiendo con los miembros de mesa.",
            contextDescription = "La Ley Seca prohíbe el consumo de alcohol y presentarse en estado de ebriedad a votar.",
            entitiesInvolved = listOf(
                EntityInvolved("Presidente de JRV", "Mando de mesa", "Denegar el voto por alteración del orden y requerir apoyo de la JEM/PNC."),
                EntityInvolved("JEM (Junta Electoral Municipal)", "Sanción administrativa", "Imponer la multa correspondiente conforme al Art. 243 lit. a."),
                EntityInvolved("PNC (Policía Nacional Civil)", "Fuerza pública", "Retirar a la persona del centro escolar y aplicar el protocolo por infracción a la Ley Seca.")
            ),
            whatShouldBeDone = "La JRV debe denegar la emisión del voto a la persona por presentarse en estado de ebriedad y solicitar a la JEM y PNC su retiro del centro, quedando sujeta a la sanción de multa del Art. 243.",
            stepByStepProcedure = listOf(
                "1. El Presidente de la JRV le notifica con serenidad que en estado de ebriedad no puede permanecer en la mesa ni emitir el sufragio.",
                "2. Se solicita la presencia del agente de la PNC y del delegado de la JEM.",
                "3. La PNC custodia a la persona y la acompaña fuera del recinto de votación.",
                "4. La JEM levanta acta para la imposición de la multa de cien a quinientos colones (o equivalente en dólares) conforme al Art. 243 lit. a.",
                "5. Se asienta el incidente en el registro de la mesa y se reanuda la votación regular."
            ),
            actionsNotToDo = listOf(
                "NO permitir que una persona ebria perturbe el secreto del voto ni agreda a la mesa.",
                "NO dejar pasar por alto la violación a la Ley Seca (Art. 284).",
                "NO generar confrontaciones físicas innecesarias."
            ),
            correctSolution = "Denegar la votación, solicitar el desalojo inmediato del individuo por la PNC y remitir a la JEM para la imposición de la multa establecida en el Art. 243 lit. a.",
            whySolutionIsCorrect = "El Art. 243 lit. a del Código Electoral sanciona específicamente a quienes se presenten en estado de ebriedad al lugar de votación, y el Art. 284 prohíbe el consumo y distribución de bebidas embriagantes.",
            legalNormativeRef = "• Lo establecido por la normativa: Código Electoral Arts. 243 lit. a, 253 y 284.\n• Recomendación/procedimiento de actuación: Asegurar la salida pacífica de la persona con asistencia de la PNC sin perturbar a las demás mesas.",
            libraryDocumentRef = "Código Electoral de El Salvador (Arts. 243 y 284)"
        ),

        CaseSolution(
            id = "seg_persona_armada_en_centro",
            title = "Una persona armada dentro del centro de votación",
            stage = "Seguridad",
            situationDescription = "Dentro de los pasillos o en la fila de una JRV se observa a un individuo portando un arma de fuego visible en la cintura o bajo la camisa, sin pertenecer a los elementos uniformados de la PNC en servicio.",
            contextDescription = "La situación representa un riesgo crítico e inminente para la vida de todos los presentes.",
            entitiesInvolved = listOf(
                EntityInvolved("Cualquier funcionario de JRV / JEM", "Alerta de seguridad", "Dar aviso discreto e inmediato a los agentes de la PNC destacados en el centro."),
                EntityInvolved("PNC (Policía Nacional Civil)", "Autoridad armada legal", "Intervenir táctica y profesionalmente, desarmar al sujeto y proceder a su captura inmediata."),
                EntityInvolved("Fiscal Electoral", "Procuración de justicia", "Iniciar proceso por delito de portación ilegal y amenazas al proceso electoral (Art. 252).")
            ),
            whatShouldBeDone = "Se debe dar aviso inmediato y reservado a los mandos de la PNC destacados en el centro. La PNC procede en el acto al desarme y detención de la persona, ya que en los centros de votación está estrictamente prohibida la portación de armas.",
            stepByStepProcedure = listOf(
                "1. Quien observe a la persona armada avisa de inmediato y sin causar pánico al Delegado de Centro o agente policial más cercano.",
                "2. Los agentes de la PNC se despliegan, interceptan a la persona y le exigen levantar las manos.",
                "3. Se le decomisa el arma de fuego y se le neutraliza.",
                "4. Se verifica su identidad y se procede a su captura en flagrancia y remisión inmediata ante los tribunales (Art. 252).",
                "5. El Fiscal Electoral levanta el acta correspondiente y se confirma la seguridad del centro."
            ),
            actionsNotToDo = listOf(
                "NO intentar desarmar al sospechoso por cuenta de miembros de mesa o civiles.",
                "NO permitir la presencia de personas armadas dentro de los locales de votación bajo ninguna excepción civil.",
                "NO retrasar el aviso a la Policía Nacional Civil."
            ),
            correctSolution = "Aviso inmediato a la PNC para el desarme, detención en flagrancia y consignación a la Fiscalía Electoral conforme al Art. 252 y Art. 290 del Código Electoral.",
            whySolutionIsCorrect = "El Art. 290 del Código Electoral prohíbe tajantemente la portación de armas de cualquier naturaleza en lugares donde deba emitirse el voto, excepto la PNC en servicio.",
            legalNormativeRef = "• Lo establecido por la normativa: Código Electoral Arts. 252 y 290; Ley de Control y Regulación de Armas de Fuego.\n• Recomendación/procedimiento de actuación: Mantener la calma en el centro y no generar estampidas mientras la PNC realiza la aprehensión.",
            libraryDocumentRef = "Código Electoral de El Salvador (Arts. 252 y 290)"
        ),

        CaseSolution(
            id = "seg_pelea_violenta_en_centro",
            title = "Se produce una pelea violenta dentro del centro de votación",
            stage = "Seguridad",
            situationDescription = "En el patio central del centro de votación estalla una riña colectiva entre simpatizantes o grupos de personas con agresiones físicas, piedras o palos, amenazando la continuidad de las votaciones en varias mesas.",
            contextDescription = "Los votantes corren a refugiarse y los miembros de JRV temen por la seguridad de las urnas.",
            entitiesInvolved = listOf(
                EntityInvolved("Presidentes de JRV", "Protección de mesas", "Resguardar las urnas y paquetes electorales dentro de las aulas cerradas."),
                EntityInvolved("PNC y Unidades Antimotines", "Fuerza y orden", "Intervenir con celeridad para dispersar el disturbio y detener a los causantes."),
                EntityInvolved("JEM y TSE Colegiado", "Autoridad máxima", "Evaluar suspensión temporal localizada si hubiere grave alteración del orden público (Art. 64 lit. a.iv).")
            ),
            whatShouldBeDone = "Las JRV deben poner a resguardo inmediato los paquetes y urnas dentro de sus aulas. La PNC interviene de inmediato para disolver la pelea y detener a los involucrados. Si el disturbio fuere generalizado, la JEM y el TSE evalúan la suspensión temporal preventiva (Art. 64 lit. a.iv).",
            stepByStepProcedure = listOf(
                "1. Los miembros de cada JRV aseguran de inmediato las urnas y papelería dentro de su respectiva aula.",
                "2. La PNC ingresa en bloque perimetral, controla el disturbio y aprehende a los causantes de la violencia en flagrancia (Art. 252).",
                "3. La JEM y el Fiscal Electoral verifican que las aulas y mesas no hayan sufrido invasión o robo de materiales.",
                "4. Una vez restablecido el orden por la PNC, el Presidente de cada JRV reanuda las votaciones.",
                "5. Si el orden público no puede garantizarse de inmediato, el TSE determina las medidas correspondientes conforme al Art. 64 lit. a.iv."
            ),
            actionsNotToDo = listOf(
                "NO dejar abandonadas las urnas o papeletas en mesas desiertas.",
                "NO tomar justicia por mano propia entre miembros de partidos.",
                "NO reanudar la votación mientras exista riesgo activo para la integridad de los votantes."
            ),
            correctSolution = "Resguardo de urnas en aulas por las JRV, restablecimiento del orden y detenciones por la PNC, y reanudación ordenada una vez asegurado el perímetro comicial.",
            whySolutionIsCorrect = "El Art. 4, Art. 64 lit. a.iv y Art. 252 del Código Electoral facultan la adopción de medidas de seguridad y garantizan la persecución penal inmediata contra quienes alteren el orden del sufragio.",
            legalNormativeRef = "• Lo establecido por la normativa: Código Electoral Arts. 4, 64 lit. a.iv, 65 lit. e y 252.\n• Recomendación/procedimiento de actuación: Asegurar el cierre temporal de los portones de acceso mientras la PNC restablece el control total.",
            libraryDocumentRef = "Código Electoral de El Salvador (Arts. 64, 65 y 252)"
        ),

        CaseSolution(
            id = "seg_violencia_contra_mujer",
            title = "Situación de violencia contra una mujer en el centro de votación",
            stage = "Seguridad",
            situationDescription = "Una ciudadana, miembra de JRV o vigilante es víctima de agresiones verbales denigrantes, amenazas, tocamientos no consentidos o violencia física por parte de un individuo dentro del recinto electoral.",
            contextDescription = "La agresión atenta directamente contra su dignidad e integridad física y política.",
            entitiesInvolved = listOf(
                EntityInvolved("Víctima (Mujer salvadoreña)", "Ciudadana / Funcionaria", "Denunciar y recibir protección integral inmediata."),
                EntityInvolved("Presidente de JRV / JEM", "Autoridad electoral", "Brindar auxilio inmediato y solicitar intervención de la PNC sin revictimizar."),
                EntityInvolved("PNC (Unidad de Género / Seguridad)", "Protección y captura", "Separar y detener al agresor en flagrancia por delitos tipificados en la LEIV."),
                EntityInvolved("FGR (Unidad Especializada de la Mujer)", "Persecución penal", "Iniciar diligencias judiciales sin admitir acuerdos conciliatorios (Art. 55 LEIV).")
            ),
            whatShouldBeDone = "La autoridad electoral debe brindar protección inmediata a la víctima, requerir la captura en flagrancia del agresor por la PNC y remitir el caso con prioridad a la Fiscalía General de la República bajo el marco de la LEIV.",
            stepByStepProcedure = listOf(
                "1. La víctima o testigos informan a la presidencia de la JRV o Delegado de la JEM.",
                "2. Se separa y pone a salvo a la ciudadana en un espacio seguro dentro del centro.",
                "3. La PNC interviene de inmediato y procede a la detención en flagrancia del agresor.",
                "4. Se notifica a la Fiscalía Electoral y a la Unidad de Atención a Mujeres Víctimas de Violencia de la FGR.",
                "5. Se asienta el hecho en acta, garantizando que la mujer pueda continuar ejerciendo sus funciones o su voto con resguardo policial."
            ),
            actionsNotToDo = listOf(
                "NO minimizar ni ignorar la agresión alegando que 'son cosas de la política'.",
                "NO forzar a la mujer a conciliar o disculpar al agresor (la LEIV prohíbe la conciliación).",
                "NO permitir que el agresor continúe en el centro de votación."
            ),
            correctSolution = "Protección inmediata de la mujer afectada, detención en flagrancia del agresor por la PNC y remisión inmediata a la Fiscalía bajo los rigores de la LEIV y el Código Electoral.",
            whySolutionIsCorrect = "La Ley Especial Integral para una Vida Libre de Violencia para las Mujeres (LEIV) y los protocolos electorales del TSE sancionan con penas de prisión la violencia física, psicológica y política contra la mujer, con prohibición expresa de mediación.",
            legalNormativeRef = "• Lo establecido por la normativa: Ley Especial Integral para una Vida Libre de Violencia para las Mujeres (LEIV) Arts. 9, 10 y 55; Código Electoral Arts. 4 y 252.\n• Recomendación/procedimiento de actuación: Ofrecer acompañamiento psicológico y legal a través de la delegada institucional de la PDDH/ISDEMU.",
            libraryDocumentRef = "Protocolo de Protección a la Mujer y LEIV (Pág. 1)"
        ),

        CaseSolution(
            id = "seg_discriminacion_persona_discapacidad",
            title = "Situación de discriminación hacia una persona con discapacidad",
            stage = "Seguridad",
            situationDescription = "Una persona con discapacidad visual, auditiva o movilidad reducida en silla de ruedas se presenta a votar y un miembro de mesa o vigilante le niega el voto asistido o le exige bajar escaleras inaccesibles sin permitirle la adecuación de la mesa.",
            contextDescription = "La ley electoral garantiza el voto asistido y el trato preferente a personas con discapacidad.",
            entitiesInvolved = listOf(
                EntityInvolved("Presidente de JRV", "Garante de derechos", "Habilitar el voto asistido y autorizar el acompañamiento de una persona de su confianza."),
                EntityInvolved("JEM y Gestores de Accesibilidad", "Logística inclusiva", "Facilitar rampas o trasladar la papeleta a un espacio accesible de planta baja si fuere necesario."),
                EntityInvolved("Votante con discapacidad", "Elector titular", "Ejercer libremente su derecho al sufragio con apoyo técnico o humano."),
                EntityInvolved("PDDH (Procuraduría de Derechos Humanos)", "Observación", "Verificar que se garantice la inclusión sin discriminación.")
            ),
            whatShouldBeDone = "La JRV debe garantizar el trato preferente y prioritario sin hacer fila, permitir el ingreso de su perro guía o persona de su absoluta confianza para el voto asistido (o el auxilio del Presidente de mesa si así lo pide expresamente el elector).",
            stepByStepProcedure = listOf(
                "1. Dar acceso preferencial e inmediato a la mesa sin demora en fila.",
                "2. Permitir que el elector ingrese con su acompañante de confianza de su elección para asistirlo en el anaquel.",
                "3. Si la mesa está en un piso superior no accesible, el Presidente y Secretario de JRV con autorización de la JEM trasladan la papeleta y padrón a un box de accesibilidad en planta baja.",
                "4. El ciudadano emite su voto de forma libre y se le devuelven sus documentos con dignidad.",
                "5. Se previene a cualquier vigilante que obstaculice sobre las sanciones legales por discriminación."
            ),
            actionsNotToDo = listOf(
                "NO obligar a la persona con discapacidad a subir escaleras peligrosas.",
                "NO prohibir el acompañante de confianza elegido por el propio elector.",
                "NO permitir burlas ni tratos vejatorios."
            ),
            correctSolution = "Brindar atención preferente inmediata, facilitar el voto asistido con persona de su confianza y acondicionar el acceso accesible conforme a la normativa electoral inclusiva.",
            whySolutionIsCorrect = "El Art. 3 de la Constitución, el Decreto Legislativo de Trato Preferente a Grupos Vulnerables y las directrices del TSE consagran el derecho al voto asistido y la accesibilidad universal.",
            legalNormativeRef = "• Lo establecido por la normativa: Constitución de la República Art. 3; Código Electoral Arts. 3 y 4; Disposición Especial de Trato Preferente a Personas Vulnerables.\n• Recomendación/procedimiento de actuación: Los gestores del TSE deben coordinar el apoyo con sillas de ruedas y señalética en braille.",
            libraryDocumentRef = "Código Electoral de El Salvador (Arts. 3 y 4)"
        )
    )

    fun getCasesByStage(stage: String): List<CaseSolution> {
        if (stage == "Todos") return casesAndSolutions
        return casesAndSolutions.filter { it.stage.equals(stage, ignoreCase = true) }
    }

    // ==========================================
    // SECCIÓN 2: BANCO DINÁMICO DE EXAMEN (125 PREGUNTAS: 25 PREVIAS + 100 NUEVAS)
    // ==========================================
    val examQuestionsBank: List<ExamQuestion> = ExamBankData.all125Questions

    fun getRandom25ExamQuestions(): List<ExamQuestion> {
        return ExamBankData.getRandom25ExamQuestions()
    }
}
