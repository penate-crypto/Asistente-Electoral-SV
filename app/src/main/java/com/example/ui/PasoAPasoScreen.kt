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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.ElectoralViewModel

enum class PasoPasoMode {
    POR_INTEGRANTE,
    JORNADA_GENERAL
}

enum class JrvMemberRole(val title: String, val shortName: String, val icon: ImageVector, val badgeColor: Color) {
    PRESIDENTE("Presidente de JRV", "Presidente", Icons.Default.Gavel, Color(0xFF0D47A1)),
    SECRETARIO("Secretario de JRV", "Secretario", Icons.Default.EditNote, Color(0xFF00695C)),
    VOCAL_1("Primer Vocal", "Vocal 1", Icons.Default.HowToVote, Color(0xFFE65100)),
    VOCAL_2("Segundo Vocal", "Vocal 2", Icons.Default.FactCheck, Color(0xFF6A1B9A)),
    VOCAL_3("Tercer Vocal", "Vocal 3", Icons.Default.Groups, Color(0xFF2E7D32)),
    VIGILANTE("Vigilante de Partido", "Vigilante", Icons.Default.Visibility, Color(0xFFD97706)),
    SUPERVISOR("Supervisor de Centro", "Supervisor", Icons.Default.SupervisorAccount, Color(0xFF4338CA)),
    JEFE_DE_CENTRO("Jefe de Centro de Votación", "Jefe Centro", Icons.Default.Badge, Color(0xFF991B1B))
}

data class ChronoStage(
    val id: String,
    val stageName: String,
    val timeLabel: String,
    val overview: String,
    val items: List<ChronoItem>,
    val legalRef: String,
    val prohibitedOrWarning: String? = null
)

data class ChronoItem(
    val title: String,
    val detail: String,
    val icon: ImageVector = Icons.Default.CheckCircle
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasoAPasoScreen(
    viewModel: ElectoralViewModel,
    modifier: Modifier = Modifier
) {
    var selectedMode by remember { mutableStateOf(PasoPasoMode.POR_INTEGRANTE) }
    var selectedMember by remember { mutableStateOf(JrvMemberRole.PRESIDENTE) }
    var expandedStageId by remember { mutableStateOf<String?>(null) }

    val memberStages = remember(selectedMember) {
        getStagesForMember(selectedMember)
    }

    val generalStages = remember {
        getGeneralJornadaStages()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("paso_a_paso_screen")
    ) {
        // Top Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "GUÍA CRONOLÓGICA OFICIAL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Buscar información (Paso a Paso)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Guías oficiales para integrantes de JRV, Vigilantes, Supervisores, Jefes de Centro y proceso general de la jornada",
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Primary Mode Switcher (Por Integrante vs Jornada General)
                TabRow(
                    selectedTabIndex = selectedMode.ordinal,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    Tab(
                        selected = selectedMode == PasoPasoMode.POR_INTEGRANTE,
                        onClick = {
                            selectedMode = PasoPasoMode.POR_INTEGRANTE
                            expandedStageId = null
                        },
                        text = {
                            Text(
                                "Por Rol / Integrante",
                                fontSize = 12.5.sp,
                                fontWeight = if (selectedMode == PasoPasoMode.POR_INTEGRANTE) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                    Tab(
                        selected = selectedMode == PasoPasoMode.JORNADA_GENERAL,
                        onClick = {
                            selectedMode = PasoPasoMode.JORNADA_GENERAL
                            expandedStageId = null
                        },
                        text = {
                            Text(
                                "Jornada General",
                                fontSize = 12.5.sp,
                                fontWeight = if (selectedMode == PasoPasoMode.JORNADA_GENERAL) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }

                if (selectedMode == PasoPasoMode.POR_INTEGRANTE) {
                    Spacer(modifier = Modifier.height(10.dp))

                    // Horizontal Member / Role Selector
                    ScrollableTabRow(
                        selectedTabIndex = selectedMember.ordinal,
                        edgePadding = 0.dp,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                        contentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        JrvMemberRole.values().forEach { role ->
                            Tab(
                                selected = selectedMember == role,
                                onClick = {
                                    selectedMember = role
                                    expandedStageId = null
                                },
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = role.icon,
                                            contentDescription = null,
                                            tint = if (selectedMember == role) role.badgeColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = role.shortName,
                                            fontSize = 12.sp,
                                            fontWeight = if (selectedMember == role) FontWeight.Bold else FontWeight.Normal,
                                            color = if (selectedMember == role) role.badgeColor else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // Chronological Content List
        val currentStages = if (selectedMode == PasoPasoMode.POR_INTEGRANTE) memberStages else generalStages

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                if (selectedMode == PasoPasoMode.POR_INTEGRANTE) {
                    // Active Member / Role Banner
                    Surface(
                        color = selectedMember.badgeColor.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, selectedMember.badgeColor.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(selectedMember.badgeColor, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = selectedMember.icon,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = selectedMember.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = selectedMember.badgeColor
                                )
                                Text(
                                    text = getMemberSummaryRole(selectedMember),
                                    fontSize = 11.5.sp,
                                    lineHeight = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    // General Walkthrough Banner
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timeline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Recorrido Cronológico de la Jornada",
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Desde la llegada a las 06:00 AM hasta la entrega final del paquete electoral",
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            items(currentStages) { stage ->
                val isExpanded = expandedStageId == stage.id || expandedStageId == null

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Stage Header with time badge & expand toggle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedStageId = if (expandedStageId == stage.id) "" else stage.id
                                },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = stage.timeLabel,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stage.stageName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            IconButton(
                                onClick = {
                                    expandedStageId = if (expandedStageId == stage.id) "" else stage.id
                                }
                            ) {
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = "Expandir"
                                )
                            }
                        }

                        Text(
                            text = stage.overview,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )

                        AnimatedVisibility(visible = isExpanded) {
                            Column {
                                Divider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                )

                                // Action Checklist Items
                                stage.items.forEachIndexed { index, item ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .padding(top = 2.dp)
                                                .size(20.dp)
                                                .background(
                                                    MaterialTheme.colorScheme.primaryContainer,
                                                    CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${index + 1}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = item.title,
                                                fontSize = 12.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = item.detail,
                                                fontSize = 11.5.sp,
                                                lineHeight = 15.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                // Warning / Prohibition Box if present
                                stage.prohibitedOrWarning?.let { warning ->
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(
                                        color = Color(0xFFFFF3E0),
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, Color(0xFFFFB74D)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Icon(
                                                Icons.Default.WarningAmber,
                                                contentDescription = null,
                                                tint = Color(0xFFE65100),
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = "PROHIBICIÓN / PRECAUCIÓN:",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFE65100)
                                                )
                                                Text(
                                                    text = warning,
                                                    fontSize = 11.sp,
                                                    lineHeight = 14.5.sp,
                                                    color = Color(0xFF5D4037)
                                                )
                                            }
                                        }
                                    }
                                }

                                // Legal Grounding Footer
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.MenuBook,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Base legal: ${stage.legalRef}",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                // Quick Ask Button to AI
                                OutlinedButton(
                                    onClick = {
                                        val q = if (selectedMode == PasoPasoMode.POR_INTEGRANTE) {
                                            "¿Qué funciones específicas tiene ${selectedMember.title} en la fase de ${stage.stageName} según el Código Electoral de El Salvador?"
                                        } else {
                                            "¿Cuál es el procedimiento oficial para ${stage.stageName} de la jornada electoral según el Código Electoral de El Salvador?"
                                        }
                                        viewModel.onQueryInputChange(q)
                                        viewModel.submitQuery(q)
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Consultar detalles a la IA", fontSize = 11.5.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun getMemberSummaryRole(role: JrvMemberRole): String {
    return when (role) {
        JrvMemberRole.PRESIDENTE -> "Máxima autoridad de la mesa. Dirige la instalación, coordina las votaciones, canta los votos en el escrutinio y custodia el orden."
        JrvMemberRole.SECRETARIO -> "Responsable de la fe pública. Firma y sella cada papeleta, desprende la esquina con número y llena las actas oficiales."
        JrvMemberRole.VOCAL_1 -> "Encargado de la identificación y registro. Verifica el DUI en el padrón de firmas y aplica la tinta indeleble tras votar."
        JrvMemberRole.VOCAL_2 -> "Auxiliar en la búsqueda de electores en el padrón y entrega de crayones y auxilio de flujo en anaqueles."
        JrvMemberRole.VOCAL_3 -> "Custodia del anaquel y la urna. Garantiza que el voto sea secreto y orienta a adultos mayores y personas con discapacidad."
        JrvMemberRole.VIGILANTE -> "Fiscaliza las operaciones de mesa en nombre de su partido. Tiene derecho a voz, vigila conteo, impugna votos y recibe copia sellada del acta."
        JrvMemberRole.SUPERVISOR -> "Coordina y asesora a los vigilantes de su partido en hasta 20 JRV del centro. Vota en la 2ª JRV y apoya la logística."
        JrvMemberRole.JEFE_DE_CENTRO -> "Representante máximo del partido en el centro. Asesora a vigilantes, vigila instalación/transmisión y vota en la 1ª JRV del centro."
    }
}

private fun getStagesForMember(role: JrvMemberRole): List<ChronoStage> {
    return when (role) {
        JrvMemberRole.PRESIDENTE -> listOf(
            ChronoStage(
                id = "pres_1",
                stageName = "1. Llegada e Instalación (06:00 AM)",
                timeLabel = "06:00 AM - 07:00 AM",
                overview = "Apertura del paquete electoral, verificación de miembros y acondicionamiento del área de votación.",
                items = listOf(
                    ChronoItem("Presentación", "Presentarse a las 6:00 AM con DUI vigente y credencial oficial del TSE."),
                    ChronoItem("Recepción del Paquete", "Recibir de la JEM el paquete electoral sellado y cotejar su número de JRV."),
                    ChronoItem("Verificación de Quórum", "Constatar la presencia de al menos 3 miembros propietarios; de faltar, llamar suplentes a las 6:15 AM."),
                    ChronoItem("Revisión de Urnas", "Mostrar a los vigilantes que las urnas plásticas están vacías y cerrarlas con los sellos."),
                    ChronoItem("Firma del Acta de Instalación", "Firmar junto a los integrantes y vigilantes presentes el Acta de Instalación.")
                ),
                legalRef = "Código Electoral Arts. 100, 190, 191, 192",
                prohibitedOrWarning = "No puede iniciar la votación antes de las 7:00 AM ni con menos de 3 miembros acreditados."
            ),
            ChronoStage(
                id = "pres_2",
                stageName = "2. Apertura y Conducción de la Votación (07:00 AM)",
                timeLabel = "07:00 AM - 05:00 PM",
                overview = "Anuncio en voz alta del inicio de votación, orden en la mesa y resolución de incidencias.",
                items = listOf(
                    ChronoItem("Voto de Miembros y Vigilantes", "A las 7:00 AM convocar a miembros y vigilantes para votar primero y retener temporalmente sus DUI."),
                    ChronoItem("Anuncio de Apertura", "Declarar formalmente en voz alta abierta la votación para los ciudadanos en fila."),
                    ChronoItem("Verificación de Elector", "Constatar que el elector figure en el padrón y que su DUI coincida."),
                    ChronoItem("Mantenimiento del Orden", "Requerir auxilio inmediato de la PNC en caso de desórdenes, armas o propaganda.")
                ),
                legalRef = "Código Electoral Arts. 195, 196, 290",
                prohibitedOrWarning = "Prohibido admitir votantes sin DUI vigente o personas que ya tengan el dedo entintado."
            ),
            ChronoStage(
                id = "pres_3",
                stageName = "3. Cierre y Escrutinio Preliminar (05:00 PM)",
                timeLabel = "05:00 PM en adelante",
                overview = "Cierre de la mesa, conteo voto por voto, calificación y firma del acta oficial.",
                items = listOf(
                    ChronoItem("Cierre a las 5:00 PM", "Declarar cerrada la votación. Solo votan quienes ya estaban en la fila de la JRV."),
                    ChronoItem("Inutilización de Sobrantes", "Presenciar que el Secretario cuente e inutilice las papeletas sobrantes."),
                    ChronoItem("Conteo Voto por Voto", "Abrir la urna, extraer las papeletas y cantar en voz alta la calificación de cada voto."),
                    ChronoItem("Firma y Transmisión", "Firmar el Acta de Escrutinio, entregar copias a vigilantes y entregar el original al transmisor del TSE.")
                ),
                legalRef = "Código Electoral Arts. 198, 200, 205, 209, 211",
                prohibitedOrWarning = "La firma del acta de escrutinio es obligatoria e inexcusable para todos los miembros (Art. 114)."
            )
        )
        JrvMemberRole.SECRETARIO -> listOf(
            ChronoStage(
                id = "sec_1",
                stageName = "1. Conteo y Preparación de Papeletas (06:00 AM)",
                timeLabel = "06:00 AM - 07:00 AM",
                overview = "Revisión cuantitativa de paquetes, conteo de papeletas y firma previa en el reverso.",
                items = listOf(
                    ChronoItem("Inventario de Paquete", "Verificar cantidad exacta de papeletas recibidas y correlativos numéricos."),
                    ChronoItem("Firma y Sellado", "Firmar y estampar el sello oficial de la JRV en el reverso de cada papeleta."),
                    ChronoItem("Redacción del Acta de Instalación", "Llenar con letra clara el Acta de Instalación y recabar firmas.")
                ),
                legalRef = "Código Electoral Arts. 186, 192",
                prohibitedOrWarning = "No entregar ninguna papeleta sin que antes haya sido firmada y sellada por el Secretario."
            ),
            ChronoStage(
                id = "sec_2",
                stageName = "2. Entrega de Papeletas al Votante (07:00 AM)",
                timeLabel = "07:00 AM - 05:00 PM",
                overview = "Retiro de la esquina desprendible y entrega formal de la papeleta doblada al ciudadano.",
                items = listOf(
                    ChronoItem("Mostrar Reverso", "Mostrar al elector y vigilantes que la papeleta tiene la firma y el sello de la mesa."),
                    ChronoItem("Desprender Esquina", "Retirar la esquina perforada con el número correlativo y depositarla en la bolsa especial."),
                    ChronoItem("Entrega al Votante", "Entregar la papeleta al ciudadano indicándole que pase solo al anaquel secreto.")
                ),
                legalRef = "Código Electoral Arts. 186 inc. 3°, 196 inc. 2°",
                prohibitedOrWarning = "Nunca entregar la papeleta con la esquina correlativa pegada."
            ),
            ChronoStage(
                id = "sec_3",
                stageName = "3. Llenado del Acta de Escrutinio Preliminar",
                timeLabel = "05:00 PM en adelante",
                overview = "Inutilización de sobrantes, registro de votos y entrega de copias oficiales.",
                items = listOf(
                    ChronoItem("Inutilizar Papeletas Sobrantes", "Contar sobrantes, consignar número en el acta y rasgarlas o cruzarlas."),
                    ChronoItem("Llenado de Formulario", "Anotar votos válidos por partido/coalición, nulos, abstenciones e impugnados."),
                    ChronoItem("Copias a Vigilantes", "Llenar y sellar las copias en papel carbón/autocopiante para cada vigilante acreditado.")
                ),
                legalRef = "Código Electoral Arts. 200, 209, 211",
                prohibitedOrWarning = "La omisión de entregar copias selladas a vigilantes es sancionada legalmente (Art. 235)."
            )
        )
        JrvMemberRole.VOCAL_1 -> listOf(
            ChronoStage(
                id = "voc1_1",
                stageName = "1. Padrón de Búsqueda y Acondicionamiento (06:00 AM)",
                timeLabel = "06:00 AM - 07:00 AM",
                overview = "Colocación visible del padrón de consulta externa y organización del padrón de firmas.",
                items = listOf(
                    ChronoItem("Fijación del Padrón", "Pegar un ejemplar del padrón en un muro visible cercano a la mesa."),
                    ChronoItem("Preparación de Tinta", "Verificar el frasco de tinta indeleble y su almohadilla o aplicador.")
                ),
                legalRef = "Código Electoral Arts. 193, 197",
                prohibitedOrWarning = "El padrón debe colocarse donde no obstruya el paso ni sea dañado por la intemperie."
            ),
            ChronoStage(
                id = "voc1_2",
                stageName = "2. Padrón de Firmas y Tinta Indeleble (07:00 AM)",
                timeLabel = "07:00 AM - 05:00 PM",
                overview = "Recolección de firma/huella del ciudadano tras votar y aplicación de tinta indeleble.",
                items = listOf(
                    ChronoItem("Firma o Huella", "Ubicar la casilla del elector en el padrón de firmas y solicitar su firma o huella dactilar."),
                    ChronoItem("Aplicación de Tinta", "Manchar la cutícula del dedo pulgar derecho (o lugar visible) con tinta indeleble."),
                    ChronoItem("Devolución del DUI", "Devolver el Documento de Identidad al elector tras constatar el entintado.")
                ),
                legalRef = "Código Electoral Art. 197 inc. final",
                prohibitedOrWarning = "No devolver el DUI antes de que el elector haya firmado y recibido la tinta indeleble."
            ),
            ChronoStage(
                id = "voc1_3",
                stageName = "3. Cuadre del Padrón en el Escrutinio (05:00 PM)",
                timeLabel = "05:00 PM en adelante",
                overview = "Conteo exacto de firmas y huellas para cuadrar con las papeletas extraídas de la urna.",
                items = listOf(
                    ChronoItem("Conteo de Firmas", "Sumar el total de ciudadanos que efectivamente firmaron en el padrón."),
                    ChronoItem("Cotejo con Urna", "Cotejar que el total de firmas coincida con el total de votos dentro de la urna.")
                ),
                legalRef = "Código Electoral Arts. 200, 202 lit. k",
                prohibitedOrWarning = "Cualquier sobrante o faltante debe consignarse detalladamente en el acta."
            )
        )
        JrvMemberRole.VOCAL_2 -> listOf(
            ChronoStage(
                id = "voc2_1",
                stageName = "1. Búsqueda Rápida y Verificación de Electores",
                timeLabel = "07:00 AM - 05:00 PM",
                overview = "Revisión rápida en el padrón alfabético para agilizar la fila de votantes.",
                items = listOf(
                    ChronoItem("Búsqueda en Padrón", "Buscar el número de correlativo del votante en el padrón de búsqueda."),
                    ChronoItem("Constatación de Vigencia", "Confirmar que los datos del DUI coincidan con el padrón electoral.")
                ),
                legalRef = "Código Electoral Art. 196",
                prohibitedOrWarning = "Si el DUI no coincide o está vencido/alterado, avisar al Presidente para resolución."
            ),
            ChronoStage(
                id = "voc2_2",
                stageName = "2. Apoyo en Escrutinio y Clasificación de Papeletas",
                timeLabel = "05:00 PM en adelante",
                overview = "Separación ordenada de votos por partido, nulos, válidos y abstenciones.",
                items = listOf(
                    ChronoItem("Pilas de Votos", "Colocar las papeletas en montones según la bandera o candidatura calificada."),
                    ChronoItem("Reconteo de Seguridad", "Recontar en grupos de 10 papeletas para verificar la exactitud de los totales.")
                ),
                legalRef = "Código Electoral Arts. 200, 205",
                prohibitedOrWarning = "Manipular las papeletas siempre a la vista de todos los vigilantes presentes."
            )
        )
        JrvMemberRole.VOCAL_3 -> listOf(
            ChronoStage(
                id = "voc3_1",
                stageName = "1. Custodia de Anaqueles y Voto Accesible",
                timeLabel = "07:00 AM - 05:00 PM",
                overview = "Resguardo de la privacidad del voto y asistencia prioritaria a grupos vulnerables.",
                items = listOf(
                    ChronoItem("Privacidad del Anaquel", "Verificar que nadie observe ni tome fotografías a la papeleta dentro del anaquel."),
                    ChronoItem("Voto Preferente", "Facilitar acceso prioritario a adultos mayores, embarazadas y personas con discapacidad."),
                    ChronoItem("Crayones", "Constatar permanentemente que los crayones no se hayan agotado o roto en los anaqueles.")
                ),
                legalRef = "Código Electoral Arts. 194, 197",
                prohibitedOrWarning = "Prohibido el ingreso de acompañantes al anaquel salvo casos de asistencia a discapacidad severa."
            ),
            ChronoStage(
                id = "voc3_2",
                stageName = "2. Empaque y Sellado de Paquetes Finales",
                timeLabel = "Finalización de la jornada",
                overview = "Organización de sobres y custodia física del paquete electoral para entrega a la JEM.",
                items = listOf(
                    ChronoItem("Empaque de Sobres", "Introducir papeletas válidas, nulas, sobrantes e impugnadas en sus bolsas rotuladas."),
                    ChronoItem("Sellado de Caja", "Cerrar la caja del paquete con la cinta de seguridad adhesiva oficial."),
                    ChronoItem("Entrega a la JEM", "Acompañar al Presidente y Secretario a la entrega del paquete a la JEM y firmar recibo.")
                ),
                legalRef = "Código Electoral Art. 210",
                prohibitedOrWarning = "Nunca abandonar el paquete electoral desatendido en ningún momento."
            )
        )
        JrvMemberRole.VIGILANTE -> listOf(
            ChronoStage(
                id = "vig_1",
                stageName = "1. Acreditación e Instalación (06:00 AM)",
                timeLabel = "06:00 AM - 07:00 AM",
                overview = "Presentación de credencial sellada y fiscalización del armado de urnas y conteo inicial.",
                items = listOf(
                    ChronoItem("Acreditación ante JRV", "Presentar credencial emitida por el partido o TSE junto a su DUI."),
                    ChronoItem("Vigilancia de Urnas", "Comprobar visualmente que las urnas estén completamente vacías antes de sellarlas."),
                    ChronoItem("Firma de Instalación", "Firmar el Acta de Instalación de la mesa (o manifestar inconformidad si hubiere anomalía).")
                ),
                legalRef = "Código Electoral Arts. 123, 128, 192",
                prohibitedOrWarning = "Prohibido tocar físicamente papeletas o urnas (Art. 129)."
            ),
            ChronoStage(
                id = "vig_2",
                stageName = "2. Fiscalización de la Votación (07:00 AM)",
                timeLabel = "07:00 AM - 05:00 PM",
                overview = "Observar la verificación de identidad en padrón y reportar anomalías a su supervisor o jefe de centro.",
                items = listOf(
                    ChronoItem("Emisión del Voto", "Votar en la mesa donde está acreditado a las 7:00 AM reteniendo temporalmente su DUI."),
                    ChronoItem("Fiscalización de Votantes", "Exigir que todo elector presente su DUI original y no tenga el dedo manchado."),
                    ChronoItem("Derecho a Voz", "Intervenir con respeto ante irregularidades sin interrumpir las funciones de la mesa.")
                ),
                legalRef = "Código Electoral Arts. 127, 128, 195",
                prohibitedOrWarning = "Prohibido hacer propaganda política o portar distintivos partidarios dentro del centro (Arts. 175, 177)."
            ),
            ChronoStage(
                id = "vig_3",
                stageName = "3. Escrutinio, Impugnaciones y Copia del Acta",
                timeLabel = "05:00 PM en adelante",
                overview = "Fiscalización voto por voto, derecho a solicitar impugnaciones y recepción obligatoria de copia sellada.",
                items = listOf(
                    ChronoItem("Vigilancia del Conteo", "Presenciar la apertura de urna y la calificación de cada papeleta en voz alta."),
                    ChronoItem("Impugnación Fundada", "Solicitar la impugnación de votos dudosos para resolución en escrutinio final."),
                    ChronoItem("Recepción de Copia Sellada", "Exigir y recibir copia certificada, firmada y sellada del Acta de Escrutinio Preliminar.")
                ),
                legalRef = "Código Electoral Arts. 200, 206, 209, 211",
                prohibitedOrWarning = "La negativa de la JRV a entregar copia del acta es delito sancionable por ley (Art. 235)."
            )
        )
        JrvMemberRole.SUPERVISOR -> listOf(
            ChronoStage(
                id = "sup_1",
                stageName = "1. Coordinación e Instalación de Mesas (06:00 AM)",
                timeLabel = "06:00 AM - 07:00 AM",
                overview = "Supervisión de hasta 20 JRV asignadas, verificación de llegada de vigilantes y provisión de insumos.",
                items = listOf(
                    ChronoItem("Acreditación", "Portar credencial de Supervisor emitida por el partido político contendiente."),
                    ChronoItem("Pase de Lista de Vigilantes", "Verificar que cada mesa asignada cuente con su vigilante propietario o suplente."),
                    ChronoItem("Sustituciones Oportunas", "Coordinar con la JEM la incorporación de vigilantes suplentes ante ausencias.")
                ),
                legalRef = "Código Electoral Arts. 125, 127",
                prohibitedOrWarning = "No puede integrar una JRV como miembro de mesa; su rol es fiscalizador."
            ),
            ChronoStage(
                id = "sup_2",
                stageName = "2. Emisión del Voto y Asesoría Legal",
                timeLabel = "07:00 AM - 05:00 PM",
                overview = "Voto en la 2ª JRV del centro y asesoría legal continua a los vigilantes de mesa.",
                items = listOf(
                    ChronoItem("Voto en 2ª JRV", "Votar en la 2ª JRV del centro de votación si el centro tiene más de 20 mesas."),
                    ChronoItem("Asistencia Legal", "Asesorar a los vigilantes ante discrepancias en padrón o calificación de votos."),
                    ChronoItem("Coordinación con Jefe de Centro", "Reportar anomalías graves al Jefe de Centro y Fiscal Electoral.")
                ),
                legalRef = "Código Electoral Arts. 125, 195 inc. 2°",
                prohibitedOrWarning = "Prohibido interferir físicamente en el desarrollo de las votaciones."
            ),
            ChronoStage(
                id = "sup_3",
                stageName = "3. Recopilación de Copias de Actas de Escrutinio",
                timeLabel = "05:00 PM en adelante",
                overview = "Consolidar las copias de actas de las 20 JRV asignadas y entregarlas al Jefe de Centro.",
                items = listOf(
                    ChronoItem("Monitoreo de Escrutinio", "Supervisar que el conteo en todas las mesas a su cargo transcurra con normalidad."),
                    ChronoItem("Recolección de Actas", "Recibir de los vigilantes las copias originales selladas de las actas de escrutinio."),
                    ChronoItem("Entrega al Jefe de Centro", "Entregar el paquete de actas consolidadas al centro de cómputo partidario.")
                ),
                legalRef = "Código Electoral Arts. 125, 211",
                prohibitedOrWarning = "Asegurar que todas las copias cuenten con las firmas y sellos oficiales legibles."
            )
        )
        JrvMemberRole.JEFE_DE_CENTRO -> listOf(
            ChronoStage(
                id = "jefe_1",
                stageName = "1. Apertura del Centro de Votación y Logística (05:30 AM)",
                timeLabel = "05:30 AM - 07:00 AM",
                overview = "Máxima representación del partido en el centro escolar o sede de votación.",
                items = listOf(
                    ChronoItem("Presentación", "Acreditarse ante el Delegado de la JEM y la PNC con su credencial oficial."),
                    ChronoItem("Despliegue de Vigilancia", "Verificar la correcta distribución de supervisores y vigilantes en todas las mesas."),
                    ChronoItem("Colaboración en Instalación", "Colaborar con las autoridades electorales para facilitar la instalación ordenada.")
                ),
                legalRef = "Código Electoral Arts. 125, 127, 192",
                prohibitedOrWarning = "No puede realizar actividades proselitistas dentro del centro de votación."
            ),
            ChronoStage(
                id = "jefe_2",
                stageName = "2. Emisión del Voto y Representación Institucional",
                timeLabel = "07:00 AM - 05:00 PM",
                overview = "Voto en la 1ª JRV del centro e interlocución directa con Fiscalía, JEM y PNC.",
                items = listOf(
                    ChronoItem("Voto en 1ª JRV", "Votar en la primera Junta Receptora de Votos del centro donde está acreditado."),
                    ChronoItem("Interlocución con Autoridades", "Gestionar con la JEM y la Fiscalía Electoral cualquier incidencia grave o delito."),
                    ChronoItem("Resolución de Conflictos", "Apoyar la solución pacífica de discrepancias entre vigilantes y miembros de JRV.")
                ),
                legalRef = "Código Electoral Arts. 125, 195 inc. 2°, 252",
                prohibitedOrWarning = "Cualquier denuncia formal debe acompañarse de prueba documental o testimonial pertinente."
            ),
            ChronoStage(
                id = "jefe_3",
                stageName = "3. Transmisión de Resultados y Consolidación",
                timeLabel = "05:00 PM en adelante",
                overview = "Presenciar la transmisión de actas al TSE y custodiar el archivo partidario de actas.",
                items = listOf(
                    ChronoItem("Presenciar Transmisión", "Presenciar el escaneo y transmisión de actas por el operador del TSE (Art. 210 inc. 6°)."),
                    ChronoItem("Consolidación General", "Recibir las copias de actas de todos los supervisores del centro."),
                    ChronoItem("Custodia de Salida", "Acompañar el retiro y custodia policial de los paquetes electorales hacia la JEM.")
                ),
                legalRef = "Código Electoral Arts. 210, 211, 212",
                prohibitedOrWarning = "Verificar que ningún paquete electoral salga del centro sin custodia de la PNC."
            )
        )
    }
}

private fun getGeneralJornadaStages(): List<ChronoStage> {
    return listOf(
        ChronoStage(
            id = "gen_1",
            stageName = "Etapa 1: Llegada y Recepción de Paquetes (06:00 AM)",
            timeLabel = "06:00 AM - 06:15 AM",
            overview = "Reunión de integrantes acreditados, presentación de credenciales y recepción de materiales de la JEM.",
            items = listOf(
                ChronoItem("Presentación", "Llegada puntual a las 6:00 AM con DUI vigente y credencial oficial del TSE."),
                ChronoItem("Recepción de Material", "Recibir el paquete electoral sellado y comprobar que coincida con el número de JRV."),
                ChronoItem("Acreditación de Vigilantes", "Revisar credenciales de los vigilantes de partidos políticos presentes.")
            ),
            legalRef = "Código Electoral Arts. 100, 189, 190"
        ),
        ChronoStage(
            id = "gen_2",
            stageName = "Etapa 2: Instalación de Mesa y Armado de Urnas (06:15 - 07:00 AM)",
            timeLabel = "06:15 AM - 07:00 AM",
            overview = "Conteo y firma de papeletas por el Secretario, vaciado y sellado de urnas, y firma del Acta de Instalación.",
            items = listOf(
                ChronoItem("Conteo de Papeletas", "El Secretario cuenta y firma en el reverso las papeletas oficiales."),
                ChronoItem("Cierre de Urnas", "Verificar que las urnas estén vacías y colocar los sellos adhesivos de seguridad."),
                ChronoItem("Pegado de Padrón", "Pegar un ejemplar del padrón de electores en un lugar exterior visible."),
                ChronoItem("Acta de Instalación", "Llenar y firmar el Acta de Instalación por miembros de JRV y vigilantes.")
            ),
            legalRef = "Código Electoral Arts. 186, 192, 193, 194"
        ),
        ChronoStage(
            id = "gen_3",
            stageName = "Etapa 3: Apertura y Voto de Autoridades de Mesa (07:00 AM)",
            timeLabel = "07:00 AM",
            overview = "Inicio formal del sufragio. Votación de integrantes de mesa, vigilantes y apertura al público.",
            items = listOf(
                ChronoItem("Voto de JRV y Vigilantes", "Votan en primer lugar los integrantes de la mesa y vigilantes acreditados."),
                ChronoItem("Retención de DUI", "El Presidente retiene los DUI de los miembros de mesa hasta el cierre."),
                ChronoItem("Anuncio de Apertura", "El Presidente anuncia en voz alta el inicio de la votación ciudadana.")
            ),
            legalRef = "Código Electoral Art. 195"
        ),
        ChronoStage(
            id = "gen_4",
            stageName = "Etapa 4: Recepción Continua de Votos Ciudadanos (07:00 AM - 05:00 PM)",
            timeLabel = "07:00 AM - 05:00 PM",
            overview = "Flujo ininterrumpido de votación: verificación, papeleta sellada, voto secreto, firma y entintado.",
            items = listOf(
                ChronoItem("Identificación", "El elector presenta su DUI original vigente; se constata en el padrón de búsqueda."),
                ChronoItem("Papeleta y Desprendible", "El Secretario retira la esquina correlativa y entrega la papeleta sellada."),
                ChronoItem("Voto Secreto", "El ciudadano vota a solas en el anaquel y deposita la papeleta doblada en la urna."),
                ChronoItem("Firma y Tinta", "El ciudadano firma en el padrón de firmas, se mancha el pulgar con tinta indeleble y se le devuelve el DUI.")
            ),
            legalRef = "Código Electoral Arts. 196, 197"
        ),
        ChronoStage(
            id = "gen_5",
            stageName = "Etapa 5: Cierre de Votación (05:00 PM) e Inutilización de Sobrantes",
            timeLabel = "05:00 PM",
            overview = "Cierre formal de la votación ciudadana y conteo/inutilización inmediata de papeletas no utilizadas.",
            items = listOf(
                ChronoItem("Cierre de Puertas", "A las 5:00 PM se declara cerrada la votación. Solo votan electores ya formados en la fila."),
                ChronoItem("Voto de PNC y FAES", "Los agentes destacados votan en la última JRV del centro según Art. 195."),
                ChronoItem("Inutilización de Sobrantes", "Se cuentan y anulan las papeletas sobrantes antes de abrir la urna.")
            ),
            legalRef = "Código Electoral Arts. 195 inc. 3°, 198, 200 lit. a"
        ),
        ChronoStage(
            id = "gen_6",
            stageName = "Etapa 6: Escrutinio Preliminar Voto por Voto",
            timeLabel = "05:15 PM en adelante",
            overview = "Apertura de la urna, conteo público y calificación de votos válidos, nulos e impugnados.",
            items = listOf(
                ChronoItem("Apertura de Urna", "Abrir la urna y verificar que el total de papeletas cuadre con las firmas del padrón."),
                ChronoItem("Calificación en Voz Alta", "El Presidente muestra cada papeleta y canta el voto por partido/candidato."),
                ChronoItem("Clasificación de Votos", "Separación en montones: Votos Válidos, Votos Nulos y Votos Impugnados.")
            ),
            legalRef = "Código Electoral Arts. 200, 205, 206, 207"
        ),
        ChronoStage(
            id = "gen_7",
            stageName = "Etapa 7: Llenado de Actas, Copias a Vigilantes y Transmisión",
            timeLabel = "Conclusión del conteo",
            overview = "Redacción del Acta Oficial de Escrutinio, entrega de copias selladas y transmisión de resultados.",
            items = listOf(
                ChronoItem("Llenado del Acta", "Consignar resultados numéricos y firmar/sellar por todos los miembros."),
                ChronoItem("Entrega a Vigilantes", "Entregar obligatoriamente copia sellada y firmada a cada vigilante acreditado."),
                ChronoItem("Transmisión al TSE", "Entregar el acta original al técnico transmisor del TSE en presencia de Fiscales y Jefes de Centro.")
            ),
            legalRef = "Código Electoral Arts. 209, 210 inc. 6°, 211"
        ),
        ChronoStage(
            id = "gen_8",
            stageName = "Etapa 8: Empaque, Devolución y Custodia Policial",
            timeLabel = "Final de la jornada",
            overview = "Empaque de todo el material en la caja oficial, entrega a la JEM y custodia armada de la PNC.",
            items = listOf(
                ChronoItem("Empaque en Bolsas", "Guardar papeletas en sobres rotulados y sellar la caja con cinta de seguridad."),
                ChronoItem("Entrega a la JEM", "Entregar personalmente el paquete a la JEM y firmar el acta de entrega y recepción."),
                ChronoItem("Custodia de PNC", "La Policía Nacional Civil acompaña el traslado seguro de los paquetes.")
            ),
            legalRef = "Código Electoral Arts. 210, 212, 290"
        )
    )
}
