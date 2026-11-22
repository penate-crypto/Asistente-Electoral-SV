package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BookSearch
import com.example.data.PdfDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Amarillo de subrayado de búsqueda (el mismo en lista, panel de texto y sobre el PDF). */
val SearchHighlightYellow = Color(0xFFFFEB3B)

/** Texto con los rangos indicados subrayados en amarillo (fondo amarillo y letra oscura, legible en tema claro y oscuro). */
fun highlightedText(text: String, ranges: List<IntRange>): AnnotatedString {
    if (ranges.isEmpty()) return AnnotatedString(text)
    val merged = ArrayList<IntRange>()
    for (r in ranges.sortedBy { it.first }) {
        if (merged.isNotEmpty() && r.first <= merged.last().last + 1) {
            merged[merged.size - 1] = merged.last().first..maxOf(merged.last().last, r.last)
        } else merged.add(r)
    }
    return buildAnnotatedString {
        var cursor = 0
        for (r in merged) {
            val s = r.first.coerceIn(0, text.length)
            val e = (r.last + 1).coerceIn(s, text.length)
            if (s > cursor) append(text.substring(cursor, s))
            withStyle(SpanStyle(background = SearchHighlightYellow, color = Color(0xFF1E293B), fontWeight = FontWeight.Bold)) {
                append(text.substring(s, e))
            }
            cursor = e
        }
        if (cursor < text.length) append(text.substring(cursor))
    }
}

/**
 * Subrayado amarillo semitransparente SOBRE la imagen de la página del PDF.
 * Debe dibujarse con la misma transformación (zoom y desplazamiento) que la imagen y ocupar el mismo espacio
 * (la imagen usa ContentScale.Fit en un contenedor que llena la pantalla).
 */
@Composable
fun PdfHighlightOverlay(
    boxes: List<BookSearch.Box>,
    bitmapWidth: Int,
    bitmapHeight: Int,
    scale: Float,
    offset: Offset,
    modifier: Modifier = Modifier
) {
    if (boxes.isEmpty() || bitmapWidth <= 0 || bitmapHeight <= 0) return
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer(scaleX = scale, scaleY = scale, translationX = offset.x, translationY = offset.y)
    ) {
        val fit = minOf(size.width / bitmapWidth, size.height / bitmapHeight)
        val imgW = bitmapWidth * fit
        val imgH = bitmapHeight * fit
        val left = (size.width - imgW) / 2f
        val top = (size.height - imgH) / 2f
        val pad = 1.5f
        for (b in boxes) {
            drawRect(
                color = SearchHighlightYellow.copy(alpha = 0.75f),
                topLeft = Offset(left + b.x * imgW - pad, top + b.y * imgH - pad),
                size = Size(b.w * imgW + 2 * pad, b.h * imgH + 2 * pad),
                blendMode = BlendMode.Multiply // el texto negro sigue legible debajo del amarillo
            )
        }
    }
}

/** Panel con el texto de la página y lo buscado en amarillo. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PageTextSheet(
    document: PdfDocument,
    pageNumber: Int,
    query: String,
    onClearHighlight: () -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    var content by remember { mutableStateOf<AnnotatedString?>(null) }
    LaunchedEffect(document, pageNumber, query) {
        content = withContext(Dispatchers.Default) {
            val file = BookSearch.resolveFileName(document.id, document.assetPath)
            if (file == null) AnnotatedString("")
            else {
                val (text, ranges) = BookSearch.pageTextWithHighlights(context.applicationContext, file, pageNumber, query)
                highlightedText(text, ranges)
            }
        }
    }
    ModalBottomSheet(onDismissRequest = onClose, containerColor = MaterialTheme.colorScheme.surface) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Texto de la página $pageNumber", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(document.title, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, maxLines = 1)
                }
                if (query.isNotBlank()) {
                    TextButton(onClick = onClearHighlight) { Text("Quitar subrayado", fontSize = 12.sp) }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            val c = content
            if (c == null) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else if (c.text.isBlank()) {
                Text("Esta página no tiene texto disponible para mostrar.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                    Text(c, fontSize = 13.5.sp, lineHeight = 20.sp, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
