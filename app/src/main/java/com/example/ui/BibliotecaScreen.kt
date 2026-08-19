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

    // Discover any additional PDFs in assets
    val discoveredPdfs = remember(context) {
        PdfRendererManager.discoverAssetPdfs(context)
    }

    // Dynamic document list: all 9 official library documents matched with discovered PDF asset paths
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
                    "ley_sufragio_extranjero" -> lowerDisc.contains("extranjero") || lowerDisc.contains("sufragio")
                    "ley_genero_electoral" -> lowerDisc.contains("genero") || lowerDisc.contains("mujer")
                    "reglamento_observacion_electoral" -> lowerDisc.contains("observacion")
                    "disposiciones_candidaturas_no_partidarias" -> lowerDisc.contains("no-partidaria") || lowerDisc.contains("candidaturas")
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

    // If viewing document, open absolute full-screen PDF reader
    if (viewingDocument != null) {
        PdfReaderScreen(
            document = viewingDocument!!,
            onClose = { viewingDocument = null },
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
                    fontSize = 11.sp,
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
            }
        }

        // Direct list of available books with simplified cards
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
                        modifier = Modifier.padding(18.dp)
                    ) {
                        // Title of the book
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = doc.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Two distinct action buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // [ ABRIR PDF ]
                            Button(
                                onClick = { viewingDocument = doc },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("btn_open_pdf_${doc.id}"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    Icons.Default.MenuBook,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ABRIR PDF",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // [ CONSULTAR CON IA ]
                            OutlinedButton(
                                onClick = {
                                    val priorityPrompt = "Consulta basada prioritariamente en el documento '${doc.title}': ¿Qué normativas, atribuciones y procedimientos establece respecto a los procesos electorales de El Salvador?"
                                    viewModel.onQueryInputChange(priorityPrompt)
                                    viewModel.setScreen(ElectoralScreen.CHAT)
                                    viewModel.submitQuery(priorityPrompt)
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("btn_consult_ai_${doc.id}")
                            ) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "CONSULTAR CON IA",
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
