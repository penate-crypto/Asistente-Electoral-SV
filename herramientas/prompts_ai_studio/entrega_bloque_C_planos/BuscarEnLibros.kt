package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.search.codigo.CodeArticle
import com.example.data.search.codigo.CodeMatchKind
import com.example.data.search.codigo.CodeSearchResult
import com.example.data.search.codigo.LawBook
import com.example.data.search.codigo.LawLibrary
import com.example.data.search.knowledge.ElectoralKnowledgeBase
import com.example.viewmodel.ElectoralViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Hoja para elegir en qué libro buscar («Buscar en libros»). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookPickerSheet(onPick: (LawBook) -> Unit, onClose: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onClose, containerColor = MaterialTheme.colorScheme.surface) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 24.dp)) {
            Text("Buscar en libros", fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Text(
                "Elige el libro. Después escribe o dicta tu pregunta (por ejemplo «artículo 198» o «bebidas embriagantes»).",
                fontSize = 12.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(LawLibrary.books) { book ->
                    Card(
                        onClick = { onPick(book) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth().testTag("book_pick_${book.id}")
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(book.title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

/** Distintivo «Buscando en: <libro>» que se muestra sobre la caja de texto cuando el modo está activo. */
@Composable
fun BookSearchBadge(bookTitle: String, onClear: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "Buscando en: $bookTitle",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    maxLines = 1
                )
            }
            IconButton(onClick = onClear, modifier = Modifier.size(22.dp).testTag("btn_quitar_busqueda_libros")) {
                Icon(Icons.Default.Close, contentDescription = "Dejar de buscar en libros", tint = MaterialTheme.colorScheme.onTertiaryContainer, modifier = Modifier.size(14.dp))
            }
        }
    }
}

/** Resultados de «Buscar en libros»: tarjetas de artículos con su texto, su página y el botón para abrir el PDF subrayado. */
@Composable
fun BookSearchResultsView(
    question: String,
    bookId: String,
    bookTitle: String,
    viewModel: ElectoralViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var loading by remember(question, bookId) { mutableStateOf(true) }
    var result by remember(question, bookId) { mutableStateOf<CodeSearchResult?>(null) }
    var showMore by remember(question, bookId) { mutableStateOf(false) }

    LaunchedEffect(question, bookId) {
        loading = true
        result = withContext(Dispatchers.Default) { LawLibrary.search(context, bookId, question) }
        loading = false
    }

    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp)) {
        Text(
            text = "📖 $bookTitle",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Tu búsqueda: «$question»",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(10.dp))

        val r = result
        when {
            loading -> Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            r == null || r.articles.isEmpty() -> {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Sin resultados en este libro", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "No se encontró nada para «$question» en $bookTitle. Prueba con otras palabras, con un número de artículo (por ejemplo «198» o «artículos 5 al 7») o elige otro libro. La aplicación no inventa respuestas.",
                            fontSize = 12.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            else -> {
                val titulo = if (r.kind == CodeMatchKind.ARTICLE_NUMBER) "Artículo(s) solicitado(s)" else "Artículos sobre: ${r.topic}"
                Text(titulo, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                val highlight = if (r.kind == CodeMatchKind.ARTICLE_NUMBER) null else r.topic
                r.articles.forEach { a -> LawArticleCard(a, bookTitle, highlight, viewModel); Spacer(modifier = Modifier.height(10.dp)) }
                if (r.moreCount > 0) {
                    if (showMore) {
                        r.moreArticles.forEach { a -> LawArticleCard(a, bookTitle, highlight, viewModel); Spacer(modifier = Modifier.height(10.dp)) }
                        if (r.moreCount > r.moreArticles.size) {
                            Text("Hay ${r.moreCount - r.moreArticles.size} resultados más: afina tu búsqueda.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        OutlinedButton(onClick = { showMore = true }, modifier = Modifier.fillMaxWidth().testTag("btn_ver_mas_articulos")) {
                            Text("Ver más (${r.moreCount})", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Texto tomado del PDF de la biblioteca. Verifícalo en el documento oficial.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun LawArticleCard(a: CodeArticle, bookTitle: String, topicHighlight: String?, viewModel: ElectoralViewModel) {
    var original by remember(a.id) { mutableStateOf(false) }
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth().testTag("law_card_${a.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Art. ${a.numero} · ${a.titulo}", fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(6.dp)) {
                    Text("Pág. ${a.paginaPdf}", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
            if (a.derogado) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("DEROGADO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
            }
            if (a.capitulo.isNotBlank()) {
                Text(a.capitulo, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (original) a.textoLiteral else a.textoLegible,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            TextButton(onClick = { original = !original }, contentPadding = PaddingValues(0.dp)) {
                Text(if (original) "Ver texto legible" else "Ver texto original del PDF", fontSize = 12.sp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { viewModel.openPdfDirectly(a.pdfDocId, a.paginaPdf, topicHighlight ?: "art. ${a.numero}") },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(38.dp).testTag("btn_abrir_pdf_${a.id}")
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Abrir en el PDF (pág. ${a.paginaPdf})", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }
            val relacionadas = a.situacionesRelacionadas.mapNotNull { id -> ElectoralKnowledgeBase.findById(id)?.let { id to it.titulo } }
            if (relacionadas.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("También en las situaciones:", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                relacionadas.take(4).forEach { (id, titulo) ->
                    Text(
                        text = "• $titulo",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable {
                                viewModel.clearBookSearchBook()
                                viewModel.submitQuery(titulo)
                            }
                            .padding(vertical = 3.dp)
                            .testTag("rel_$id")
                    )
                }
            }
        }
    }
}
