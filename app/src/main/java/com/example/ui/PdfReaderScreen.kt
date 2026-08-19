package com.example.ui

import android.graphics.Bitmap
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PdfDocument
import com.example.data.PdfRenderSession
import com.example.data.PdfRendererManager
import com.example.viewmodel.ElectoralViewModel

@Composable
fun PdfReaderScreen(
    document: PdfDocument,
    onClose: () -> Unit,
    viewModel: ElectoralViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var renderSession by remember { mutableStateOf<PdfRenderSession?>(null) }
    var isLoadingPdf by remember { mutableStateOf(true) }
    var currentPageIndex by remember { mutableStateOf(0) }
    var currentBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isRenderingPage by remember { mutableStateOf(false) }

    // Touch gesture zoom and pan
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    // Load PDF session from assets (trying assetPath, libros/, and PDF LIBROS/)
    LaunchedEffect(document) {
        isLoadingPdf = true

        val candidatePaths = listOfNotNull(
            document.assetPath,
            document.assetPath?.let { if (!it.startsWith("libros/")) "libros/$it" else null },
            document.assetPath?.let { if (!it.startsWith("PDF LIBROS/")) "PDF LIBROS/$it" else null },
            document.assetPath?.substringAfterLast("/")
        ).distinct()

        var session: PdfRenderSession? = null
        for (path in candidatePaths) {
            session = PdfRendererManager.openAssetPdf(context, path)
            if (session != null) break
        }

        renderSession = session
        isLoadingPdf = false
    }

    // Dispose PDF session on screen exit
    DisposableEffect(Unit) {
        onDispose {
            renderSession?.close()
        }
    }

    val totalPages = renderSession?.totalPages ?: document.pages.size.coerceAtLeast(1)

    // Render bitmap when page or session changes
    LaunchedEffect(currentPageIndex, renderSession) {
        scale = 1f
        offset = Offset.Zero

        if (renderSession != null) {
            isRenderingPage = true
            currentBitmap = renderSession!!.renderPage(currentPageIndex, scaleFactor = 2.4f)
            isRenderingPage = false
        }
    }

    // Absolute full screen container
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121417))
            .testTag("pdf_reader_screen")
    ) {
        // Fullscreen viewport with pinch zoom and pan
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(1f, 5f)
                        if (scale > 1f) {
                            val maxOffsetX = (scale - 1f) * 700f
                            val maxOffsetY = (scale - 1f) * 1100f
                            offset = Offset(
                                x = (offset.x + pan.x).coerceIn(-maxOffsetX, maxOffsetX),
                                y = (offset.y + pan.y).coerceIn(-maxOffsetY, maxOffsetY)
                            )
                        } else {
                            offset = Offset.Zero
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            if (scale > 1f) {
                                scale = 1f
                                offset = Offset.Zero
                            } else {
                                scale = 2.2f
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            if (isLoadingPdf) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Cargando documento PDF oficial...", color = Color.White, fontSize = 13.sp)
                }
            } else if (renderSession != null && currentBitmap != null) {
                Image(
                    bitmap = currentBitmap!!.asImageBitmap(),
                    contentDescription = "Página ${currentPageIndex + 1} de ${document.title}",
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                        .testTag("rendered_pdf_page_image"),
                    contentScale = ContentScale.Fit
                )
            } else if (isRenderingPage) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            } else {
                // Fallback direct text render if asset session was not available
                val currentTextPage = document.pages.getOrElse(currentPageIndex.coerceIn(0, document.pages.size - 1)) {
                    document.pages.firstOrNull()
                }
                if (currentTextPage != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(0.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offset.x,
                                translationY = offset.y
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(24.dp)
                        ) {
                            Text(
                                text = currentTextPage.headerTitle,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0D47A1)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            currentTextPage.sections.forEach { sec ->
                                Text(
                                    text = "${sec.articleRef ?: sec.title}: ${sec.content}",
                                    fontSize = 12.5.sp,
                                    lineHeight = 18.sp,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                } else {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = document.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "El archivo PDF aún no se ha subido o no se encuentra en assets.",
                                fontSize = 12.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Para visualizarlo, sube el archivo PDF en:\napp/src/main/assets/PDF LIBROS/",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(18.dp))
                            Button(
                                onClick = onClose,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Volver a Biblioteca")
                            }
                        }
                    }
                }
            }
        }

        // Single Top-Corner Discreet "✕ CERRAR" Button (Mandatory requirement)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onClose,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F).copy(alpha = 0.92f),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(20.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier
                    .height(38.dp)
                    .testTag("pdf_close_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar visor PDF",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "CERRAR",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.5.sp,
                    color = Color.White
                )
            }
        }

        // Minimalist Bottom Page Indicator & Navigation (Discreet overlay)
        if (totalPages > 1) {
            Surface(
                color = Color.Black.copy(alpha = 0.75f),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (currentPageIndex > 0) currentPageIndex--
                        },
                        enabled = currentPageIndex > 0,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Página anterior",
                            tint = if (currentPageIndex > 0) Color.White else Color.Gray.copy(alpha = 0.4f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = "Pág. ${currentPageIndex + 1} / $totalPages",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = {
                            if (currentPageIndex < totalPages - 1) currentPageIndex++
                        },
                        enabled = currentPageIndex < totalPages - 1,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Página siguiente",
                            tint = if (currentPageIndex < totalPages - 1) Color.White else Color.Gray.copy(alpha = 0.4f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
