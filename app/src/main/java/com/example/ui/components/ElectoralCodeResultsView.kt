package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TtsPlaybackState
import com.example.data.search.codigo.CodeArticle
import com.example.data.search.codigo.CodeMatchKind
import com.example.data.search.codigo.CodeSearchResult

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ElectoralCodeResultsView(
    codeResult: CodeSearchResult,
    plainAnswerText: String,
    ttsPlaybackState: TtsPlaybackState,
    onSpeakText: (String) -> Unit,
    onPauseSpeaking: () -> Unit,
    onResumeSpeaking: () -> Unit,
    onStopSpeaking: () -> Unit,
    onRestartSpeaking: () -> Unit,
    onOpenPdf: (docId: String, page: Int) -> Unit,
    onOpenSituation: (situationId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    var showMore by remember(codeResult) { mutableStateOf(false) }

    val displayedArticles = remember(codeResult, showMore) {
        if (showMore) codeResult.articles + codeResult.moreArticles else codeResult.articles
    }

    val headerLabel = remember(codeResult) {
        if (codeResult.kind == CodeMatchKind.ARTICLE_NUMBER) {
            "Artículo(s) solicitado(s)"
        } else {
            "Artículos del Código Electoral sobre: ${codeResult.topic}"
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("codigo_electoral_results_container"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section Header + Audio / Copy Controls
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = headerLabel,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.testTag("codigo_electoral_header_label")
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val isSpeaking = ttsPlaybackState == TtsPlaybackState.PLAYING
                    val isPaused = ttsPlaybackState == TtsPlaybackState.PAUSED
                    val isVoiceActive = isSpeaking || isPaused

                    if (isVoiceActive) {
                        IconButton(
                            onClick = { if (isSpeaking) onPauseSpeaking() else onResumeSpeaking() },
                            modifier = Modifier.testTag("codigo_audio_pause_resume_button")
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                contentDescription = if (isSpeaking) "Pausar audio" else "Continuar audio",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        IconButton(
                            onClick = onStopSpeaking,
                            modifier = Modifier.testTag("codigo_audio_stop_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.StopCircle,
                                contentDescription = "Detener audio",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        IconButton(
                            onClick = onRestartSpeaking,
                            modifier = Modifier.testTag("codigo_audio_restart_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Replay,
                                contentDescription = "Reiniciar audio",
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        IconButton(
                            onClick = { onSpeakText(plainAnswerText) },
                            modifier = Modifier.testTag("codigo_audio_playback_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Escuchar artículos",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = { clipboardManager.setText(AnnotatedString(plainAnswerText)) },
                        modifier = Modifier.testTag("codigo_copy_response_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copiar artículos",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Article Cards
        displayedArticles.forEach { article ->
            ElectoralCodeArticleCard(
                article = article,
                onOpenPdf = onOpenPdf,
                onOpenSituation = onOpenSituation
            )
        }

        // "Ver más (N)" button when there are additional matching articles
        if (codeResult.moreCount > 0 && !showMore) {
            OutlinedButton(
                onClick = { showMore = true },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .testTag("btn_codigo_ver_mas")
            ) {
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Ver más (${codeResult.moreCount})",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Fixed small disclaimer at the bottom
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Aviso",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Texto tomado del Código Electoral (Decreto 413). Verifícalo con el PDF de la biblioteca.",
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.testTag("codigo_electoral_disclaimer")
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ElectoralCodeArticleCard(
    article: CodeArticle,
    onOpenPdf: (docId: String, page: Int) -> Unit,
    onOpenSituation: (situationId: String) -> Unit
) {
    var showLiteral by remember(article.id) { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("codigo_article_card_${article.numero}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Card Header: "📖 Código Electoral · Art. {numero}" + optional "Derogado" badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📖 Código Electoral · Art. ${article.numero}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.3.sp,
                    modifier = Modifier.weight(1f)
                )

                if (article.derogado) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.35f))
                    ) {
                        Text(
                            text = "Derogado",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                .testTag("badge_derogado_${article.numero}")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Article Title
            if (article.titulo.isNotBlank()) {
                Text(
                    text = article.titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 20.sp
                )
            }

            // Chapter in small text
            if (article.capitulo.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = article.capitulo,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
            )

            // Article Body (legible by default, toggleable to literal)
            SelectionContainer {
                Text(
                    text = if (showLiteral) article.textoLiteral else article.textoLegible,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp,
                    modifier = Modifier.testTag("codigo_article_body_${article.numero}")
                )
            }

            // Toggle link: "Ver texto original del PDF" / "Ver texto legible"
            if (article.textoLiteral.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                TextButton(
                    onClick = { showLiteral = !showLiteral },
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .testTag("btn_toggle_literal_${article.numero}")
                ) {
                    Text(
                        text = if (showLiteral) "Ver texto legible" else "Ver texto original del PDF",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Button: "Abrir en la biblioteca (pág. {paginaPdf})"
            Button(
                onClick = {
                    val targetDocId = article.pdfDocId.ifBlank { "codigo_electoral_decreto_413" }
                    onOpenPdf(targetDocId, article.paginaPdf)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .testTag("btn_abrir_pdf_art_${article.numero}")
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(17.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Abrir en la biblioteca (pág. ${article.paginaPdf})",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Related situations chips
            if (article.situacionesRelacionadas.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "También en situaciones:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    article.situacionesRelacionadas.forEach { situationId ->
                        AssistChip(
                            onClick = { onOpenSituation(situationId) },
                            label = {
                                Text(
                                    text = situationId,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Gavel,
                                    contentDescription = "Abrir situación $situationId",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(15.dp)
                                )
                            },
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                            ),
                            modifier = Modifier.testTag("chip_situacion_${article.numero}_$situationId")
                        )
                    }
                }
            }
        }
    }
}
