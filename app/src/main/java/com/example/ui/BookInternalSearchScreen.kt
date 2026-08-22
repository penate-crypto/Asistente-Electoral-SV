package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BookInternalSearchEngine
import com.example.data.BookSearchResult
import com.example.data.PdfDocument

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookInternalSearchScreen(
    document: PdfDocument,
    onClose: () -> Unit,
    onOpenPage: (Int) -> Unit,
    onConsultAi: (queryText: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    val searchResults = remember(searchQuery, document) {
        if (searchQuery.trim().isNotEmpty()) {
            BookInternalSearchEngine.searchDocument(
                documentId = document.id,
                documentTitle = document.title,
                query = searchQuery,
                assetPath = document.assetPath,
                context = context
            )
        } else {
            emptyList()
        }
    }

    // Contextual suggestion chips based on document
    val suggestedKeywords = remember(document) {
        when (document.id) {
            "codigo_electoral_decreto_413" -> listOf(
                "Art. 190 (Instalación)", "Art. 3 (Sufragio)", "Art. 7 (Inhábiles)",
                "Art. 38 (Organismos)", "Art. 113 (Permiso laboral)", "Art. 284 (Armas)",
                "Art. 205 (Voto nulo)", "Escrutinio", "Vigilantes"
            )
            "constitucion_republica_1983" -> listOf(
                "Art. 72 (Derechos)", "Art. 73 (Deberes)", "Art. 75 (Pérdida derechos)",
                "Art. 85 (Gobierno)", "Art. 208 (TSE)", "Soberanía", "Sufragio"
            )
            "instructivo_jrv_tse" -> listOf(
                "Instalación 06:00 AM", "Apertura 07:00 AM", "Cierre 17:00 PM",
                "Quórum mínimo (3)", "Voto nulo", "Voto válido", "PNC", "Sobre A y B"
            )
            "ley_de_partidos_politicos" -> listOf(
                "Vigilantes", "Fiscalización", "Propaganda", "Derechos partidarios"
            )
            "ley_genero_electoral" -> listOf(
                "Violencia política", "Paridad", "Inclusión", "LEIV"
            )
            else -> listOf("Artículos", "Votación", "Escrutinio", "JRV", "Sanciones")
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("book_internal_search_screen")
    ) {
        // Top App Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "BUSCADOR DEL LIBRO",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Text(
                            text = document.title,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Exclusivo",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Input Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            "Buscar artículos, números, palabras o temas...",
                            fontSize = 13.5.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
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
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("book_search_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Suggestion chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    items(suggestedKeywords) { keyword ->
                        val cleanTerm = keyword.substringBefore(" (").trim()
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { searchQuery = cleanTerm }
                        ) {
                            Text(
                                text = keyword,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }
            }
        }

        // Search Content & Results
        if (searchQuery.trim().isEmpty()) {
            // Empty state guide
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.FindInPage,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Buscador Interno de Documento",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Escribe un número de artículo (ej. «190» o «Art. 190») o cualquier término como «escrutinio», «vigilantes» o «instalación» para buscar exclusivamente dentro de:\n\n${document.title}",
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else if (searchResults.isEmpty()) {
            // No results state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.SearchOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Sin coincidencias en este libro",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "No se encontraron coincidencias para «$searchQuery» en ${document.title}. Intenta con otras palabras clave o número de artículo.",
                    fontSize = 12.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            // Results list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 14.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    // Header counter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔎 Resultados encontrados: ${searchResults.size}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Fuente: ${document.title.take(28)}...",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                items(searchResults) { result ->
                    BookSearchResultCard(
                        result = result,
                        searchQuery = searchQuery,
                        onOpenPage = { onOpenPage(result.pageNumber) },
                        onConsultAi = {
                            val prompt = "¿Qué establece el ${result.articleRef ?: result.sectionTitle} de '${result.documentTitle}' y cuál es su procedimiento?"
                            onConsultAi(prompt)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BookSearchResultCard(
    result: BookSearchResult,
    searchQuery: String,
    onOpenPage: () -> Unit,
    onConsultAi: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("result_card_${result.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Metadata header: Source badge + Location + Page
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "Fuente: ${result.documentTitle}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "Página: ${result.pageNumber}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Article/Section Title
            Text(
                text = "Ubicación: ${result.articleRef?.let { "$it — " } ?: ""}${result.sectionTitle}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Snippet with highlighted search query
            val annotatedSnippet = remember(result.snippet, searchQuery) {
                buildHighlightedString(result.snippet, searchQuery)
            }

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = annotatedSnippet,
                    fontSize = 13.sp,
                    lineHeight = 18.5.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: Open Page + Consult AI
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenPage,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                ) {
                    Icon(
                        Icons.Default.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Ir a Pág. ${result.pageNumber}",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onConsultAi,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(38.dp)
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Preguntar a IA",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun buildHighlightedString(text: String, query: String): androidx.compose.ui.text.AnnotatedString {
    val queryWords = query.trim().split("\\s+".toRegex()).filter { it.length > 1 }
    if (queryWords.isEmpty()) return buildAnnotatedString { append(text) }

    return buildAnnotatedString {
        var cursor = 0
        val lowerText = text.lowercase()

        // Find all match ranges
        val ranges = mutableListOf<IntRange>()
        for (w in queryWords) {
            val lowerWord = w.lowercase()
            var idx = lowerText.indexOf(lowerWord)
            while (idx != -1) {
                ranges.add(idx until (idx + lowerWord.length))
                idx = lowerText.indexOf(lowerWord, idx + lowerWord.length)
            }
        }

        // Merge overlapping ranges
        val sortedRanges = ranges.sortedBy { it.first }
        val mergedRanges = mutableListOf<IntRange>()
        for (r in sortedRanges) {
            if (mergedRanges.isEmpty()) {
                mergedRanges.add(r)
            } else {
                val last = mergedRanges.last()
                if (r.first <= last.last + 1) {
                    mergedRanges[mergedRanges.size - 1] = last.first..maxOf(last.last, r.last)
                } else {
                    mergedRanges.add(r)
                }
            }
        }

        for (range in mergedRanges) {
            if (range.first > cursor) {
                append(text.substring(cursor, range.first))
            }
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    background = Color(0xFFFFF59D),
                    color = Color(0xFF1E293B)
                )
            ) {
                append(text.substring(range.first, range.last + 1))
            }
            cursor = range.last + 1
        }

        if (cursor < text.length) {
            append(text.substring(cursor))
        }
    }
}
