package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.ElectoralViewModel

@Composable
fun CapacitacionScreen(
    viewModel: ElectoralViewModel,
    modifier: Modifier = Modifier
) {
    val topics = listOf(
        CapacitacionTopic(
            title = "Constitución",
            icon = Icons.Default.MenuBook,
            tag = "Base Constitucional",
            summary = "La Constitución de la República establece en sus artículos 71 al 82 el sufragio como una obligación y derecho de los ciudadanos. Define al voto como libre, directo, igualitario y secreto. Asimismo, el artículo 208 crea al Tribunal Supremo Electoral (TSE) como máxima autoridad electoral jurisdiccional y administrativa.",
            didacticKey = "¿Qué nos dice la Constitución de El Salvador sobre el sufragio y el Tribunal Supremo Electoral?"
        ),
        CapacitacionTopic(
            title = "Código Electoral",
            icon = Icons.Default.Gavel,
            tag = "Cuerpo Normativo Principal",
            summary = "Es el cuerpo legal de El Salvador que reglamenta la convocatoria, los procedimientos, infracciones, propaganda y el escrutinio de votos. En sus más de 300 artículos estructura detalladamente las reglas de juego del proceso y asigna facultades sancionatorias en caso de incumplimientos.",
            didacticKey = "¿Cuáles son las funciones y regulaciones principales del Código Electoral de El Salvador?"
        ),
        CapacitacionTopic(
            title = "Juntas Receptoras de Votos",
            icon = Icons.Default.Groups,
            tag = "Autoridad Temporal en Mesa",
            summary = "Las JRV son organismos electorales temporales integrados por ciudadanos salvadoreños propuestos por partidos públicos contendientes y ciudadanos sorteados por el TSE. Ejercen la máxima autoridad en la mesa correspondiente durante el proceso de consignación de votos.",
            didacticKey = "Explica los roles, requisitos y prohibiciones de los integrantes de las Juntas Receptoras de Votos (JRV)."
        ),
        CapacitacionTopic(
            title = "Vigilancia Electoral",
            icon = Icons.Default.Visibility,
            tag = "Garantía de Transparencia",
            summary = "Los vigilantes de partidos políticos tienen la función de registrar y auditar el correcto desempeño de las JRV. Tienen derecho a impugnar decisiones, firmar actas, presenciar el escrutinio y poseer copias certificadas del acta preliminar, pero tienen prohibido interferir políticamente con el votante.",
            didacticKey = "¿Cuáles son los derechos, prohibiciones y roles de los vigilantes de partidos electorales?"
        ),
        CapacitacionTopic(
            title = "Apertura",
            icon = Icons.Default.HourglassTop,
            tag = "Inicio de la Jornada",
            summary = "La apertura involucra las actividades de instalación entre las 5:00 AM y las 7:00 AM. Implica contar las papeletas asignadas, revisar que las urnas se encuentren vacías, firmar los documentos oficiales y abrir las puertas a los electores puntualmente.",
            didacticKey = "¿Cuál es el protocolo legal obligatorio antes de abrir las urnas a las 7:00 AM?"
        ),
        CapacitacionTopic(
            title = "Votación",
            icon = Icons.Default.HowToVote,
            tag = "Emisión del Sufragio",
            summary = "Procedimiento secuencial de votación: El votante entrega su DUI vigente, el presidente firma la papeleta, el elector acude de forma secreta al estante, marca, dobla la papeleta, la deposita en la urna, firma el padrón electoral y entinta su dedo con nitrato de plata.",
            didacticKey = "¿Cómo es el flujo ordenado y legal paso a paso para que un salvadoreño emita su voto?"
        ),
        CapacitacionTopic(
            title = "Cierre",
            icon = Icons.Default.HourglassDisabled,
            tag = "Fin de la Jornada de Voto",
            summary = "Llegadas las 5:00 PM se cierran los accesos del centro de votación. Se atiende únicamente al personal que ya se encontraba adentro de la fila. Se procede a tachar las casillas sobrantes en el padrón electoral para inhabilitar votos restantes.",
            didacticKey = "¿Cómo se realiza el cierre ordenado de las votaciones a las 5:00 PM y qué se hace con las papeletas sobrantes?"
        ),
        CapacitacionTopic(
            title = "Escrutinio",
            icon = Icons.Default.Percent,
            tag = "Recuento Oficial",
            summary = "Inicia inmediatamente tras el cierre de votación. Se abren las urnas, se equiparan papeletas contra padrón electoral firmado, se clasifican los votos (válidos, nulos, impugnados) y se digitaliza el resultado oficial para la posterior transmisión de datos al TSE.",
            didacticKey = "¿Cuáles son las fases y reglas para realizar de forma correcta el escrutinio en El Salvador?"
        ),
        CapacitacionTopic(
            title = "Delitos Electorales",
            icon = Icons.Default.Warning,
            tag = "Conductas delictivas",
            summary = "El Código Penal tipifica delitos contra el sufragio del Art. 291-A al 291-I: la inducción al fraude de voto, compra de votos, alteración de urnas, usurpación de funciones electorales e intimidación armada. Conllevan severas penas de prisión de hasta 15 años.",
            didacticKey = "¿Cuáles son los delitos electorales estipulados en el Código Penal y cuáles son sus respectivas sanciones?"
        ),
        CapacitacionTopic(
            title = "Casos Prácticos",
            icon = Icons.Default.Quiz,
            tag = "Simulación Dinámica",
            summary = "La capacitación didáctica requiere el análisis de situaciones imprevistas: rotura accidental de papeletas, fallas mecánicas en cerraduras de urnas o cortes de fluido eléctrico. Aprender los mecanismos de justificación en actas y reportes al Fiscal Electoral.",
            didacticKey = "Dame 3 ejemplos prácticos comunes de incidentes en una JRV y su resolución correcta."
        )
    )

    var expandedTitle by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Welcome Training Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "MÓDULO DE CAPACITACIÓN",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Guías Temáticas Didácticas",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Estudie paso a paso regulaciones y bases jurídicas del TSE de forma sencilla.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        // List of didactic topics
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(topics) { topic ->
                val isExpanded = expandedTitle == topic.title
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isExpanded) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            expandedTitle = if (isExpanded) null else topic.title
                        }
                        .testTag("training_topic_${topic.title.replace(" ", "_").lowercase()}")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = topic.icon,
                                    contentDescription = topic.title,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = topic.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = topic.tag,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        AnimatedVisibility(
                            visible = isExpanded,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = topic.summary,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = {
                                        viewModel.onQueryInputChange(topic.didacticKey)
                                        viewModel.submitQuery(topic.didacticKey)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("ask_about_topic_${topic.title.replace(" ", "_").lowercase()}")
                                ) {
                                    Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Consultar a la IA sobre este tema", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

data class CapacitacionTopic(
    val title: String,
    val icon: ImageVector,
    val tag: String,
    val summary: String,
    val didacticKey: String
)
