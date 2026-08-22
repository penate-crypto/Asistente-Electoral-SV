package com.example.ui

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ElectoralLibraryData
import com.example.data.PdfDocument
import com.example.data.PdfRendererManager
import com.example.viewmodel.ElectoralScreen
import com.example.viewmodel.ElectoralViewModel

@Composable
fun BibliotecaScreen(
    viewModel: ElectoralViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var viewingDocument by remember { mutableStateOf<PdfDocument?>(null) }
    var searchingDocument by remember { mutableStateOf<PdfDocument?>(null) }
    var readerInitialPage by remember { mutableStateOf(0) }

    // Discover any additional PDFs in assets
    val discoveredPdfs = remember(context) {
        PdfRendererManager.discoverAssetPdfs(context)
    }

    // Dynamic document list: all 12 official library documents matched with discovered PDF asset paths
    val allDocuments = remember(discoveredPdfs) {
        val baseDocs = ElectoralLibraryData.documents.map { defaultDoc ->
            val matchPath = discoveredPdfs.firstOrNull { discovered ->
                val lowerDisc = discovered.lowercase()
                when (defaultDoc.id) {
                    "codigo_electoral_decreto_413" -> lowerDisc.contains("codigo") && lowerDisc.contains("electoral")
                    "constitucion_republica_1983" -> lowerDisc.contains("constitucion")
                    "instructivo_jrv_tse" -> lowerDisc.contains("instructivo") && lowerDisc.contains("jrv")
                    "ley_de_partidos_politicos" -> lowerDisc.contains("partidos")
                    "ley_acceso_informacion_publica" -> lowerDisc.contains("acceso") || lowerDisc.contains("informacion")
                    "ley_sufragio_extranjero_542" -> lowerDisc.contains("extranjero") || lowerDisc.contains("sufragio")
                    "ley_reestructuracion_municipal_763" -> lowerDisc.contains("reestructuracion") || lowerDisc.contains("municipal")
                    "codigo_penal_delitos_electorales" -> lowerDisc.contains("penal")
                    "acuerdo_legislativo_reforma_electoral" -> lowerDisc.contains("acuerdo") || lowerDisc.contains("reforma") || lowerDisc.contains("segunda")
                    "ciclo_electoral_salvadoreno" -> lowerDisc.contains("ciclo")
                    "disposiciones_candidaturas_no_partidarias" -> lowerDisc.contains("no-partidaria") || lowerDisc.contains("candidaturas")
                    "reglamento_observacion_electoral" -> lowerDisc.contains("observacion")
                    else -> false
                }
            } ?: defaultDoc.assetPath
            defaultDoc.copy(assetPath = matchPath)
        }.toMutableList()

        // If extra user-added PDFs exist that are not among the 9 defaults, add them dynamically
        for (pdfPath in discoveredPdfs) {
            val isAlreadyHandled = baseDocs.any { it.assetPath.equals(pdfPath, ignoreCase = true) }
            if (!isAlreadyHandled) {
                val fileName = pdfPath.substringAfterLast("/")
                val lowerPath = pdfPath.lowercase()
                val isElectoralCode = lowerPath.contains("codigo") && lowerPath.contains("electoral")
                if (!isElectoralCode) {
                    baseDocs.add(
                        PdfDocument(
                            id = "doc_${pdfPath.hashCode()}",
                            title = fileName.removeSuffix(".pdf").replace("-", " ").replace("_", " "),
                            category = "Documentos y Manuales",
                            summary = "Documento oficial disponible en PDF para consulta y estudio electoral.",
                            authority = "Tribunal Supremo Electoral • República de El Salvador",
                            releaseDate = "Oficial",
                            assetPath = pdfPath,
                            pages = emptyList()
                        )
                    )
                }
            }
        }
        baseDocs.distinctBy { it.id }
    }

    // 1. If searching within a specific document, open internal search view
    if (searchingDocument != null) {
        BookInternalSearchScreen(
            document = searchingDocument!!,
            onClose = { searchingDocument = null },
            onOpenPage = { page ->
                viewingDocument = searchingDocument
                readerInitialPage = (page - 1).coerceAtLeast(0)
                searchingDocument = null
            },
            onConsultAi = { queryText ->
                viewModel.startDocumentSpecificQuery(searchingDocument!!.id, searchingDocument!!.title)
                viewModel.onQueryInputChange(queryText)
                viewModel.submitQuery(queryText)
                searchingDocument = null
            },
            modifier = modifier
        )
        return
    }

    // 2. If viewing document, open absolute full-screen PDF reader
    if (viewingDocument != null) {
        PdfReaderScreen(
            document = viewingDocument!!,
            initialPageIndex = readerInitialPage,
            onClose = {
                viewingDocument = null
                readerInitialPage = 0
            },
            viewModel = viewModel,
            modifier = modifier
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Simple clean header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "BIBLIOTECA",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Documentos y Leyes Oficiales",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Cada libro cuenta con buscador interno exclusivo, visor PDF y consulta con IA",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Direct list of available books with 3 clear action buttons
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(allDocuments) { doc ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("doc_item_${doc.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Title of the book
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = doc.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = doc.authority,
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Primary Action Button: BUSCAR EN LIBRO
                        Button(
                            onClick = { searchingDocument = doc },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .testTag("btn_search_in_book_${doc.id}")
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "BUSCADOR DEL LIBRO (Artículos y Temas)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Secondary Action Buttons: [ ABRIR PDF ] and [ CONSULTAR CON IA ]
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // [ ABRIR PDF ]
                            OutlinedButton(
                                onClick = {
                                    viewingDocument = doc
                                    readerInitialPage = 0
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .testTag("btn_open_pdf_${doc.id}")
                            ) {
                                Icon(
                                    Icons.Default.MenuBook,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "ABRIR PDF",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // [ CONSULTAR CON IA ]
                            Button(
                                onClick = {
                                    val priorityPrompt = "¿Qué disposiciones y procedimientos principales establece el documento '${doc.title}'?"
                                    viewModel.startDocumentSpecificQuery(doc.id, doc.title)
                                    viewModel.onQueryInputChange(priorityPrompt)
                                    viewModel.submitQuery(priorityPrompt)
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .testTag("btn_consult_ai_${doc.id}")
                            ) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "CONSULTAR IA",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
