package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.ElectoralViewModel

data class InteractiveScenario(
    val id: String,
    val title: String,
    val stage: String,
    val severity: String,
    val icon: ImageVector,
    val situationDescription: String,
    val options: List<ScenarioOption>,
    val correctOptionIndex: Int,
    val officialExplanation: String,
    val stepByStepProcedure: List<String>,
    val legalArticles: String,
    val competentAuthority: String,
    val criticalErrorsToAvoid: List<String>
)

data class ScenarioOption(
    val id: String,
    val text: String,
    val feedback: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimuladorScreen(
    viewModel: ElectoralViewModel,
    modifier: Modifier = Modifier
) {
    val scenarios = remember {
        listOf(
            // 1. JRV INCOMPLETA (Requerido)
            InteractiveScenario(
                id = "jrv_incompleta",
                title = "JRV Incompleta a la Hora de Instalación",
                stage = "Instalación",
                severity = "Moderada",
                icon = Icons.Default.Groups,
                situationDescription = "Son las 6:30 AM y la Junta Receptora de Votos (JRV) solo cuenta con 2 miembros propietarios presentes. Faltan el Secretario y un Vocal. La votación ciudadana debe abrir puntualmente a las 7:00 AM.",
                options = listOf(
                    ScenarioOption(
                        id = "opt_a",
                        text = "Suspender la mesa y no abrir la votación durante todo el día.",
                        feedback = "Incorrecto. La ley prohíbe suspender arbitrariamente la votación sin agotar los mecanismos de suplencia legal."
                    ),
                    ScenarioOption(
                        id = "opt_b",
                        text = "Llamar a los suplentes presentes; aplicar el corrimiento jerárquico de cargos y, si aún faltan, avisar de inmediato a la JEM para nombramiento excepcional.",
                        feedback = "¡Correcto! Procedimiento exacto según los Arts. 109, 190 y 191 del Código Electoral."
                    ),
                    ScenarioOption(
                        id = "opt_c",
                        text = "Invitar a un vigilante de cualquier partido a que firme como Secretario sin autorización de la JEM.",
                        feedback = "Incorrecto. Los vigilantes no pueden asumir cargos de mesa por iniciativa propia; incurrirían en usurpación de cargo (Art. 250)."
                    )
                ),
                correctOptionIndex = 1,
                officialExplanation = "Las JRV pueden instalarse y funcionar válidamente con un mínimo de 3 miembros propietarios (Art. 100 C.E.). Ante la falta de titulares, se convoca a los suplentes presentes respetando la jerarquía (Presidente, Secretario, Vocales). Si aún así no se integra el quórum mínimo, la Junta Electoral Municipal (JEM) nombrará a cualquier ciudadano apto que se encuentre en el centro (Art. 109 y 190 C.E.).",
                stepByStepProcedure = listOf(
                    "1. Verificar asistencia de los miembros propietarios a las 6:00 AM.",
                    "2. Llamar formalmente a los suplentes debidamente acreditados presentes.",
                    "3. Aplicar el orden sucesorio legal de cargos (Presidente, Secretario, Primer Vocal, etc.).",
                    "4. Si aún no se alcanza el mínimo de 3 miembros a las 6:45 AM, dar aviso inmediato a la JEM o su delegado de centro.",
                    "5. La JEM nombrará a ciudadanos salvadoreños aptos presentes, consignándolo en el acta de instalación."
                ),
                legalArticles = "Arts. 100, 109, 190 y 191 del Código Electoral de El Salvador.",
                competentAuthority = "Presidente de JRV, Junta Electoral Municipal (JEM) y Tribunal Supremo Electoral.",
                criticalErrorsToAvoid = listOf(
                    "No abrir la mesa con menos de 3 integrantes sin aval de la JEM.",
                    "No permitir que un vigilante o civil tome posesión del cargo sin credencial ni acta oficial de la JEM."
                )
            ),

            // 2. MIEMBRO DE JRV ABANDONA SU PUESTO DURANTE EL CONTEO (Requerido)
            InteractiveScenario(
                id = "abandono_conteo",
                title = "Miembro de JRV Abandona su Puesto en el Conteo",
                stage = "Escrutinio",
                severity = "Grave",
                icon = Icons.Default.ExitToApp,
                situationDescription = "A las 6:00 PM, durante el escrutinio preliminar voto por voto, el Primer Vocal manifiesta estar cansado y quiere retirarse del centro electoral antes de firmar el acta oficial de cierre.",
                options = listOf(
                    ScenarioOption(
                        id = "opt_a",
                        text = "Permitirle retirarse sin dejar constancia y repartir sus funciones entre los restantes.",
                        feedback = "Incorrecto. El cargo electoral es obligatorio e irrenunciable según el Art. 118 del Código Electoral."
                    ),
                    ScenarioOption(
                        id = "opt_b",
                        text = "Advertirle que el cargo es obligatorio por ley, retener su DUI hasta el cierre, y si abandona, levantar constancia en el acta para la imposición de multas y sanciones del Art. 242.",
                        feedback = "¡Correcto! El abandono sin justa causa acarrea multa de $25 a $114.28 USD y restricciones civiles/migratorias de 3 a 5 años."
                    ),
                    ScenarioOption(
                        id = "opt_c",
                        text = "Anular todo el conteo de la mesa por la ausencia de ese miembro.",
                        feedback = "Incorrecto. La mesa continúa el escrutinio con los demás miembros presentes, dejando constancia del incidente en acta."
                    )
                ),
                correctOptionIndex = 1,
                officialExplanation = "El Art. 118 del Código Electoral establece que el cargo en organismos electorales es obligatorio e irrenunciable. El Art. 242 sanciona el abandono injustificado con multa de \$25 a \$114.28 USD, más restricciones en emisión de DUI, antecedentes policiales, licencia de conducir y salida migratoria del país.",
                stepByStepProcedure = listOf(
                    "1. Recordar al miembro el carácter obligatorio del cargo y las sanciones legales.",
                    "2. Si la causa es médica o fuerza mayor comprobada, llamar al suplente y notificar a la JEM.",
                    "3. Si abandona arbitrariamente, hacer constar en el Acta de Escrutinio la hora y motivo del abandono.",
                    "4. Los demás miembros continúan el escrutinio y firman el acta respectiva (Art. 114 C.E.).",
                    "5. El TSE procesará la sanción e inhabilitación correspondiente."
                ),
                legalArticles = "Arts. 113, 114, 118, 120 y 242 del Código Electoral.",
                competentAuthority = "Junta Receptora de Votos, Junta Electoral Municipal y Tribunal Supremo Electoral.",
                criticalErrorsToAvoid = listOf(
                    "No suspender ni anular el escrutinio por el abandono individual de un miembro.",
                    "No omitir la anotación en el acta de observaciones."
                )
            ),

            // 3. VIGILANTE ABANDONA SIN DEJAR SUPLENTE (Requerido)
            InteractiveScenario(
                id = "vigilante_abandona",
                title = "Vigilante se Retira sin Dejar Suplente",
                stage = "Votación",
                severity = "Leve",
                icon = Icons.Default.PersonOff,
                situationDescription = "A las 11:30 AM, el único vigilante acreditado por un partido político decide marcharse del centro de votación y no hay vigilante suplente que lo reemplace en la mesa.",
                options = listOf(
                    ScenarioOption(
                        id = "opt_a",
                        text = "La JRV continúa normalmente sus funciones de votación. La falta de concurrencia o firma de un vigilante no invalida los actos de la mesa.",
                        feedback = "¡Correcto! El Art. 128 inciso final del Código Electoral garantiza que la ausencia de un vigilante no paraliza la mesa ni anula el proceso."
                    ),
                    ScenarioOption(
                        id = "opt_b",
                        text = "Cerrar la urna hasta que el partido envíe un nuevo vigilante acreditado.",
                        feedback = "Incorrecto. La votación es un servicio público continuo que no se puede paralizar por decisiones partidarias privadas."
                    ),
                    ScenarioOption(
                        id = "opt_c",
                        text = "Nombrar a un votante de la fila como nuevo vigilante de ese partido.",
                        feedback = "Incorrecto. Solo los partidos políticos mediante sus representantes legales pueden acreditar vigilantes (Art. 123 C.E.)."
                    )
                ),
                correctOptionIndex = 0,
                officialExplanation = "La vigilancia electoral es un derecho de los partidos políticos, no una obligación para la validez de la mesa (Art. 121 y 128 C.E.). La ausencia de un vigilante o la falta de su firma en las actas no anula la votación ni el escrutinio, debiéndose únicamente consignar la circunstancia en el acta.",
                stepByStepProcedure = listOf(
                    "1. La JRV mantiene abierta la votación continua sin interrupciones.",
                    "2. Consignar en la hoja de incidencias que el vigilante se retiró voluntariamente.",
                    "3. Si el suplente se presenta más tarde con su credencial en regla, se le permite integrarse a la fiscalización.",
                    "4. Al momento del cierre, se hace constar en el acta la ausencia de firma por no estar presente."
                ),
                legalArticles = "Arts. 121, 123, 124 y 128 literal c e inciso final del Código Electoral.",
                competentAuthority = "Junta Receptora de Votos y Junta Electoral Municipal.",
                criticalErrorsToAvoid = listOf(
                    "Nunca detener la votación ciudadana por ausencia de vigilantes partidistas.",
                    "No negar el reingreso al suplente debidamente acreditado si se presenta más tarde."
                )
            ),

            // 4. VOTANTE MUESTRA SU PAPELETA (Requerido)
            InteractiveScenario(
                id = "voto_publico",
                title = "Votante Exhibe Públicamente su Papeleta Marcada",
                stage = "Votación",
                severity = "Moderada",
                icon = Icons.Default.Visibility,
                situationDescription = "Un ciudadano sale del anaquel de votación, levanta su papeleta abierta en alto y muestra a todos los presentes y cámaras de teléfono por quién votó antes de introducirla a la urna.",
                options = listOf(
                    ScenarioOption(
                        id = "opt_a",
                        text = "Felicitar al votante y permitirle depositar su papeleta sin ninguna advertencia.",
                        feedback = "Incorrecto. La Constitución establece el voto estrictamente secreto (Art. 78 Cn.)."
                    ),
                    ScenarioOption(
                        id = "opt_b",
                        text = "El Presidente de la JRV debe advertir la violación al principio del voto secreto, conminar al ciudadano a doblar la papeleta de inmediato, evitar que exhiba propaganda y consignar el incidente en acta.",
                        feedback = "¡Correcto! Debe resguardarse el secreto del sufragio y evitar alteraciones al orden público o coacción."
                    ),
                    ScenarioOption(
                        id = "opt_c",
                        text = "Quitarle la papeleta a la fuerza, destruirla en el suelo y llamar a la policía para que lo encarcele 10 años.",
                        feedback = "Incorrecto. La JRV no debe ejercer violencia física desproporcionada ni romper papeletas arbitrariamente."
                    )
                ),
                correctOptionIndex = 1,
                officialExplanation = "El Art. 78 de la Constitución de la República y el Art. 3 del Código Electoral consagran que el voto es libre, directo, igualitario y SECRETO. Mostrar públicamente el voto vulnera la secrecía y puede constituir coacción electoral o propaganda indebida dentro del recinto electoral (Art. 175 C.E.).",
                stepByStepProcedure = listOf(
                    "1. La Presidencia de la JRV ordena inmediatamente doblar la papeleta para proteger la secrecía.",
                    "2. Prohibir la toma de fotografías o videos que expongan el sentido del sufragio.",
                    "3. El votante deposita su voto doblado en la urna y firma el padrón.",
                    "4. Aplicar la tinta indeleble y devolver el DUI.",
                    "5. Anotar el incidente en la hoja de observaciones del padrón para conocimiento de la JEM."
                ),
                legalArticles = "Art. 78 Constitución de la República de El Salvador; Arts. 3, 196 y 197 del Código Electoral.",
                competentAuthority = "Presidente de la JRV, Delegado del TSE y Agentes de la PNC.",
                criticalErrorsToAvoid = listOf(
                    "No permitir que partidos políticos induzcan a sus seguidores a fotografiarse con el voto.",
                    "No generar disturbios en la mesa que retrasen a los demás electores en fila."
                )
            ),

            // 5. INTENTO DE ROBO DE LA CAJA DE VOTOS / URNA (Requerido)
            InteractiveScenario(
                id = "robo_urnas",
                title = "Intento de Sustracción o Robo de la Caja de Votos",
                stage = "Seguridad / Escrutinio",
                severity = "Delito Electoral Grave",
                icon = Icons.Default.Security,
                situationDescription = "Al finalizar la votación a las 5:05 PM, una persona o grupo intenta arrebatar la urna que contiene las papeletas de votación para sacarla corriendo del centro electoral.",
                options = listOf(
                    ScenarioOption(
                        id = "opt_a",
                        text = "Priorizar la seguridad física de las personas, asegurar el material restante, alertar de inmediato a los agentes de la PNC destacados en el centro y solicitar captura en flagrancia.",
                        feedback = "¡Correcto! La seguridad de las personas es primordial. El robo de urnas es un delito electoral grave tipificado en el Código Penal y Código Electoral (Art. 252)."
                    ),
                    ScenarioOption(
                        id = "opt_b",
                        text = "Pelear a golpes contra los asaltantes arriesgando la vida de los miembros de mesa.",
                        feedback = "Incorrecto. Los miembros de JRV no deben entablar enfrentamientos físicos que pongan en peligro sus vidas; la fuerza pública corresponde a la PNC."
                    ),
                    ScenarioOption(
                        id = "opt_c",
                        text = "Dar por terminada la elección y firmar actas en blanco para retirarse a casa.",
                        feedback = "Incorrecto. Se debe documentar el hecho ante la Fiscalía Electoral y el TSE."
                    )
                ),
                correctOptionIndex = 0,
                officialExplanation = "La sustracción, destrucción o apoderamiento de material electoral y urnas constituye delito contra el sufragio según el Código Penal de El Salvador (Arts. 295 y sig.) y el Art. 252 del Código Electoral. Se procede a la captura inmediata en flagrancia por la PNC y aviso a la Fiscalía General de la República.",
                stepByStepProcedure = listOf(
                    "1. Resguardar inmediatamente la integridad de las personas presentes.",
                    "2. Proteger las demás urnas, padrones y paquetes electorales en el aula.",
                    "3. Dar la voz de alarma inmediata al personal de la Policía Nacional Civil (PNC) destacado.",
                    "4. La PNC procederá a la persecución, captura en flagrancia y recuperación de los bienes.",
                    "5. Informar al Fiscal Electoral y levantar acta circunstanciada detallando lo ocurrido."
                ),
                legalArticles = "Arts. 252 Código Electoral; Arts. 295 y 296 Código Penal de El Salvador.",
                competentAuthority = "Policía Nacional Civil (PNC), Fiscalía General de la República (FGR) y Tribunal Supremo Electoral.",
                criticalErrorsToAvoid = listOf(
                    "No intentar repeler agresiones armadas con las manos desnudas.",
                    "No olvidar consignar en el acta cada detalle exacto del incidente con el Fiscal Electoral."
                )
            ),

            // 6. PERSONA QUE INTENTA CAMBIAR DE PUESTO CON DIFERENTES IDENTIFICACIONES (Requerido)
            InteractiveScenario(
                id = "usurpacion_credenciales",
                title = "Persona con Múltiples Credenciales para Cambiar de Cargo",
                stage = "Instalación / Votación",
                severity = "Delito Electoral",
                icon = Icons.Default.Badge,
                situationDescription = "Un ciudadano se presenta a las 6:00 AM como miembro propuesto de JRV, pero a las 10:00 AM regresa con otra credencial distinta pretendiendo actuar como vigilante y supervisor en otra mesa del mismo recinto.",
                options = listOf(
                    ScenarioOption(
                        id = "opt_a",
                        text = "Aceptar ambas credenciales y dejarle rotar libremente por todos los cargos.",
                        feedback = "Incorrecto. Cada funcionario o vigilante tiene un rol único acreditado conforme al Art. 191."
                    ),
                    ScenarioOption(
                        id = "opt_b",
                        text = "Verificar su identidad en el padrón/listado oficial del TSE; si porta credenciales falsas o usurpadas, retenerlas y remitir el caso a la JEM, Fiscalía Electoral y PNC por usurpación de funciones.",
                        feedback = "¡Correcto! El Art. 237 y 250 del Código Electoral castigan la extensión fraudulenta y usurpación de cargos en JRV."
                    ),
                    ScenarioOption(
                        id = "opt_c",
                        text = "Ignorar la situación si el ciudadano alega ser miembro directivo de su partido.",
                        feedback = "Incorrecto. La ley aplica a todos sin distinciones partidarias."
                    )
                ),
                correctOptionIndex = 1,
                officialExplanation = "Nadie puede ejercer cargos en una JRV sin estar formalmente nombrado y acreditado por el TSE (Art. 191 C.E.). La usurpación de funciones o el uso de credenciales fraudulentas constituye una infracción penal grave (Art. 237 y 250 C.E.), con detención inmediata por la autoridad competente.",
                stepByStepProcedure = listOf(
                    "1. Cotejar el DUI físico con la credencial presentada.",
                    "2. Verificar en los listados oficiales de acreditación provistos por el TSE.",
                    "3. Si existen credenciales duplicadas o fraudulentas, retenerlas como evidencia.",
                    "4. Notificar de inmediato a la Junta Electoral Municipal (JEM) y al Delegado Fiscal.",
                    "5. Solicitar a la PNC la custodia o aprehensión del infractor si existe flagrancia de falsedad."
                ),
                legalArticles = "Arts. 191, 237 y 250 del Código Electoral de El Salvador.",
                competentAuthority = "Junta Electoral Municipal (JEM), Fiscalía Electoral y PNC.",
                criticalErrorsToAvoid = listOf(
                    "No admitir acreditaciones manuscritas no oficiales o sin sellos originales del TSE.",
                    "No devolver credenciales fraudulentas al infractor."
                )
            ),

            // 7. PERSONAS ARMADAS (Requerido)
            InteractiveScenario(
                id = "personas_armadas",
                title = "Persona Armada en el Centro de Votación",
                stage = "Seguridad",
                severity = "Delito Electoral Grave",
                icon = Icons.Default.LocalPolice,
                situationDescription = "A las 2:00 PM, miembros de la JRV o votantes en fila divisan a un civil portando un arma de fuego visible en la cintura dentro del patio del centro de votación.",
                options = listOf(
                    ScenarioOption(
                        id = "opt_a",
                        text = "Pedirle que guarde el arma en su bolsillo y continúe votando tranquilamente.",
                        feedback = "Incorrecto. El Art. 290 del Código Electoral prohíbe terminantemente la portación de cualquier arma en los centros electorales."
                    ),
                    ScenarioOption(
                        id = "opt_b",
                        text = "Mantener la calma, no confrontar directamente al individuo y solicitar de inmediato la intervención de los agentes de la Policía Nacional Civil (PNC) destacados para su desarme y desalojo.",
                        feedback = "¡Correcto! Solo los agentes policiales en servicio de seguridad electoral pueden portar armas de fuego en los comicios."
                    ),
                    ScenarioOption(
                        id = "opt_c",
                        text = "Cerrar todo el centro de votación permanentemente y cancelar los resultados de las elecciones.",
                        feedback = "Incorrecto. Se aísla y neutraliza el riesgo con la PNC sin suspender innecesariamente el evento democrático."
                    )
                ),
                correctOptionIndex = 1,
                officialExplanation = "El Art. 290 del Código Electoral prohíbe de forma taxativa la portación de armas de cualquier naturaleza en los lugares de votación, con la única excepción de los miembros de la PNC encargados del plan de seguridad electoral. La infracción amerita incautación del arma y remisión penal.",
                stepByStepProcedure = listOf(
                    "1. Evitar provocaciones o pánico entre los votantes en la fila.",
                    "2. Informar discretamente y de inmediato a la Policía Nacional Civil (PNC) en el recinto.",
                    "3. Los agentes de la PNC requerirán la entrega del arma y retirarán a la persona del recinto.",
                    "4. Se elabora el parte policial e informe a la Junta Electoral Municipal (JEM).",
                    "5. La votación continúa con normalidad bajo resguardo policial."
                ),
                legalArticles = "Art. 290 del Código Electoral; Ley de Control y Regulación de Armas de Fuego.",
                competentAuthority = "Policía Nacional Civil (PNC) y Tribunal Supremo Electoral.",
                criticalErrorsToAvoid = listOf(
                    "No intentar desarmar por cuenta propia a personas armadas.",
                    "No permitir el ingreso a nadie con armas aunque muestre permisos ordinarios de portación civil."
                )
            ),

            // 8. FALTA DE REFRIGERIO (Requerido)
            InteractiveScenario(
                id = "falta_refrigerio",
                title = "Retraso o Falta de Refrigerio para la Mesa",
                stage = "Logística / Votación",
                severity = "Leve",
                icon = Icons.Default.Fastfood,
                situationDescription = "Son las 1:30 PM y el refrigerio/almuerzo previsto por la logística electoral del TSE para los miembros de la JRV y colaboradores aún no ha sido entregado.",
                options = listOf(
                    ScenarioOption(
                        id = "opt_a",
                        text = "Abandonar la mesa todos juntos y cerrar el aula para salir a almorzar a un restaurante.",
                        feedback = "Incorrecto. La mesa nunca debe quedar vacía ni cerrada durante el horario de votación legal (Art. 190 C.E.)."
                    ),
                    ScenarioOption(
                        id = "opt_b",
                        text = "Reportar la situación al Delegado de Centro del TSE o supervisor logístico, y organizar turnos individuales breves para alimentarse manteniendo siempre el quórum mínimo en la mesa.",
                        feedback = "¡Correcto! Se preserva el funcionamiento continuo del sufragio mientras la coordinación del TSE solventa la entrega de víveres."
                    ),
                    ScenarioOption(
                        id = "opt_c",
                        text = "Exigir a los votantes en la fila que paguen la comida de la mesa como condición para votar.",
                        feedback = "Incorrecto. Constituiría una falta ética y cobro ilegal gravísimo."
                    )
                ),
                correctOptionIndex = 1,
                officialExplanation = "La provisión alimentaria y logística corresponde al presupuesto del TSE (Art. 42 y 111 C.E.). Ante fallas en la distribución, los integrantes de la JRV deben coordinar con el Delegado de Centro y alternarse por turnos sin romper el quórum legal mínimo de tres miembros ni interrumpir el sufragio de los ciudadanos.",
                stepByStepProcedure = listOf(
                    "1. El Presidente de la JRV comunica el retraso al Coordinador del Centro de Votación del TSE.",
                    "2. El Coordinador gestiona la dotación con la bodega de logística local.",
                    "3. Si los miembros consumen refrigerios personales, se turnan de uno en uno.",
                    "4. Se garantiza que permanezcan al menos 3 miembros en todo momento en la mesa.",
                    "5. La votación sigue su curso fluido e ininterrumpido."
                ),
                legalArticles = "Arts. 42, 111 y 190 del Código Electoral.",
                competentAuthority = "Delegado de Centro del TSE y Presidente de JRV.",
                criticalErrorsToAvoid = listOf(
                    "No desintegrar la mesa dejando las urnas y papeletas sin vigilancia.",
                    "No interrumpir la fila de los ciudadanos."
                )
            ),

            // 9. INSULTOS, DISCUSIONES O PELEAS (Requerido)
            InteractiveScenario(
                id = "discusion_peleas",
                title = "Discusión, Insultos o Peleas en la Mesa",
                stage = "Votación / Seguridad",
                severity = "Grave",
                icon = Icons.Default.RecordVoiceOver,
                situationDescription = "Dos vigilantes o votantes en la fila comienzan a intercambiar insultos acalorados y amagos de agresión física, interrumpiendo el tránsito de votantes y alterando el orden.",
                options = listOf(
                    ScenarioOption(
                        id = "opt_a",
                        text = "Unirse a la discusión en defensa de uno de los bandos partidistas.",
                        feedback = "Incorrecto. Los miembros de JRV deben mantener estricta neutralidad institucional e imparcialidad."
                    ),
                    ScenarioOption(
                        id = "opt_b",
                        text = "El Presidente de JRV ejerce su autoridad, ordena calma y, si persiste el desorden, solicita el auxilio de la PNC para desalojar a los provocadores y garantizar la seguridad.",
                        feedback = "¡Correcto! El Presidente de JRV tiene la facultad de mantener el orden público y requerir a la PNC según los Arts. 65 lit. e, 129 y 243."
                    ),
                    ScenarioOption(
                        id = "opt_c",
                        text = "Suspender permanentemente las elecciones en ese municipio.",
                        feedback = "Incorrecto. Solo el organismo colegiado del TSE por mayoría calificada puede suspender elecciones en caso de fuerza mayor extrema (Art. 64 lit. a.iv)."
                    )
                ),
                correctOptionIndex = 1,
                officialExplanation = "El Presidente de la JRV tiene la dirección del aula y la facultad legal de mantener el orden y la disciplina. Ante agresiones o alteración del orden, los agentes de la PNC destacados están obligados a intervenir para desalojar a los infractores (Arts. 65 lit. e, 129 y 243 C.E.).",
                stepByStepProcedure = listOf(
                    "1. Llamado enérgico al orden y la compostura por parte de la Presidencia de mesa.",
                    "2. Advertir a los involucrados que perturbar el proceso es sancionado con multas y arresto (Art. 246 C.E.).",
                    "3. Si persisten los insultos o conatos de riña, llamar a los agentes de la PNC.",
                    "4. La PNC desaloja del centro a los infractores.",
                    "5. Se asienta la incidencia en el libro de actas con firma de los miembros de mesa."
                ),
                legalArticles = "Arts. 65 lit. e, 129, 243 y 246 del Código Electoral.",
                competentAuthority = "Presidente de la JRV, Policía Nacional Civil (PNC) y Fiscal Electoral.",
                criticalErrorsToAvoid = listOf(
                    "No tomar partido ni responder con violencia verbal o física.",
                    "No tolerar agresiones o acoso contra mujeres miembros de mesa (LEIV)."
                )
            )
        )
    }

    var selectedStage by remember { mutableStateOf("Todos") }
    var searchQuery by remember { mutableStateOf("") }
    
    // Track user choices per scenario: Map<ScenarioId, OptionIndex>
    val userAnswers = remember { mutableStateMapOf<String, Int>() }
    var activeScenarioId by remember { mutableStateOf<String?>(null) }

    val stages = listOf("Todos", "Instalación", "Votación", "Escrutinio", "Seguridad")

    val filteredScenarios = scenarios.filter { item ->
        val matchesStage = selectedStage == "Todos" || item.stage.contains(selectedStage, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.situationDescription.contains(searchQuery, ignoreCase = true) ||
                item.legalArticles.contains(searchQuery, ignoreCase = true)
        matchesStage && matchesSearch
    }

    val completedCount = userAnswers.size
    val correctCount = userAnswers.count { (id, chosenIdx) ->
        val sc = scenarios.find { it.id == id }
        sc != null && sc.correctOptionIndex == chosenIdx
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header Section
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "SIMULACIONES ELECTORALES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Práctica Interactiva de Situaciones",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Score pill
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$correctCount / ${scenarios.size} Aciertos",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar caso (ej: incompleta, armas, conteo...)", fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Limpiar")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("simulator_search_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Stages Filter Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    stages.forEach { stage ->
                        FilterChip(
                            selected = stage == selectedStage,
                            onClick = { selectedStage = stage },
                            label = { Text(stage, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.testTag("stage_chip_${stage.lowercase()}")
                        )
                    }
                }
            }
        }

        // List of Scenarios
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredScenarios) { scenario ->
                val chosenOptionIndex = userAnswers[scenario.id]
                val hasAnswered = chosenOptionIndex != null
                val isExpanded = activeScenarioId == scenario.id || hasAnswered

                InteractiveScenarioCard(
                    scenario = scenario,
                    chosenOptionIndex = chosenOptionIndex,
                    isExpanded = isExpanded,
                    onToggleExpand = {
                        activeScenarioId = if (activeScenarioId == scenario.id) null else scenario.id
                    },
                    onSelectOption = { optionIndex ->
                        userAnswers[scenario.id] = optionIndex
                        activeScenarioId = scenario.id
                    },
                    onAskAi = {
                        val prompt = "En una simulación electoral de El Salvador sobre '${scenario.title}': ¿Cuál es el procedimiento legal exacto y qué sanciones establece el Código Electoral si ocurre esta situación: '${scenario.situationDescription}'?"
                        viewModel.onQueryInputChange(prompt)
                        viewModel.submitQuery(prompt)
                    }
                )
            }
        }
    }
}

@Composable
fun InteractiveScenarioCard(
    scenario: InteractiveScenario,
    chosenOptionIndex: Int?,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onSelectOption: (Int) -> Unit,
    onAskAi: () -> Unit
) {
    val isCorrect = chosenOptionIndex != null && chosenOptionIndex == scenario.correctOptionIndex

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("scenario_card_${scenario.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            1.dp,
            when {
                chosenOptionIndex == null -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                isCorrect -> Color(0xFF2E7D32).copy(alpha = 0.6f)
                else -> Color(0xFFC62828).copy(alpha = 0.6f)
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() }
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = when {
                                chosenOptionIndex == null -> MaterialTheme.colorScheme.primaryContainer
                                isCorrect -> Color(0xFFE8F5E9)
                                else -> Color(0xFFFFEBEE)
                            },
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = scenario.icon,
                        contentDescription = null,
                        tint = when {
                            chosenOptionIndex == null -> MaterialTheme.colorScheme.primary
                            isCorrect -> Color(0xFF2E7D32)
                            else -> Color(0xFFC62828)
                        },
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = scenario.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = scenario.stage,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Surface(
                            color = if (scenario.severity.contains("Delito") || scenario.severity.contains("Grave")) {
                                Color(0xFFFFEBEE)
                            } else {
                                Color(0xFFFFF3E0)
                            },
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = scenario.severity,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (scenario.severity.contains("Delito") || scenario.severity.contains("Grave")) {
                                    Color(0xFFC62828)
                                } else {
                                    Color(0xFFE65100)
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                IconButton(onClick = onToggleExpand) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expandir caso"
                    )
                }
            }

            // Description of situation
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = scenario.situationDescription,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Interactive Options
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "¿QUÉ ACCIÓN DEBE REALIZARSE?",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Option Buttons
                    scenario.options.forEachIndexed { index, option ->
                        val isSelected = chosenOptionIndex == index
                        val isOptionCorrect = index == scenario.correctOptionIndex

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onSelectOption(index) }
                                .testTag("option_${scenario.id}_$index"),
                            color = when {
                                chosenOptionIndex == null -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                isSelected && isOptionCorrect -> Color(0xFFE8F5E9)
                                isSelected && !isOptionCorrect -> Color(0xFFFFEBEE)
                                isOptionCorrect && chosenOptionIndex != null -> Color(0xFFE8F5E9).copy(alpha = 0.5f)
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                            },
                            border = BorderStroke(
                                1.dp,
                                when {
                                    isSelected && isOptionCorrect -> Color(0xFF2E7D32)
                                    isSelected && !isOptionCorrect -> Color(0xFFC62828)
                                    else -> Color.Transparent
                                }
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(
                                            color = when {
                                                isSelected && isOptionCorrect -> Color(0xFF2E7D32)
                                                isSelected && !isOptionCorrect -> Color(0xFFC62828)
                                                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                            },
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = ('A' + index).toString(),
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = option.text,
                                        fontSize = 12.5.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (isSelected) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = option.feedback,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isOptionCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Detailed Legal Resolution if answered or expanded
                    if (chosenOptionIndex != null) {
                        Spacer(modifier = Modifier.height(14.dp))

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.MenuBook,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Fundamento Legal y Procedimiento Oficial",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = scenario.officialExplanation,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Pasos Oficiales:",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                scenario.stepByStepProcedure.forEach { step ->
                                    Text(
                                        text = "• $step",
                                        fontSize = 11.5.sp,
                                        lineHeight = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(vertical = 1.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Base Legal: ${scenario.legalArticles}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = "Autoridad: ${scenario.competentAuthority}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Consult with AI button
                        OutlinedButton(
                            onClick = onAskAi,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ask_ai_sim_${scenario.id}")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Consultar dudas de este caso a la IA", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
