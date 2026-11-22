# PROMPT 8 para AI Studio — Buscador de la biblioteca: índice real, búsqueda exacta y subrayado amarillo (Bloque C, parte 1)

Aplica ANTES los Prompts 1, 5, 6 y 7. Los archivos vienen sueltos (sin carpetas) en `entrega_bloque_C_planos`; en `COMO_SUBIR_A_AI_STUDIO.md` está la ruta de destino de cada uno. Si tu proyecto está conectado a GitHub, ya están en la rama `claude/electoral-knowledge-base-json-ha2mpw`.

---

La búsqueda de la biblioteca (lector PDF) fallaba: el índice tenía páginas equivocadas, 5 libros sin texto buscable, "artículo 2" devolvía decenas de resultados, no había subrayado amarillo sobre la página, y la búsqueda corría en el hilo principal. Ya está resuelto en el código adjunto. Haz SOLO esto:

## 1) Datos (assets) — copiar tal cual, sin modificar ni resumir
- Reemplaza `app/src/main/assets/libros_indice_completo.json` por el adjunto (texto real de cada página de los 12 PDF e índice de artículos con su página real).
- Crea la carpeta `app/src/main/assets/libros_palabras/` con los 12 archivos adjuntos cuyo nombre termina en `_pdf.json` (posición de cada palabra, para subrayar sobre la imagen del PDF). Los nombres deben quedar IDÉNTICOS a los adjuntos (por ejemplo `codigo_electoral_pdf.json`, `codigo_penal_pdf.json`).

## 2) Código nuevo (copiar el archivo completo)
- `app/src/main/java/com/example/data/BookSearch.kt` (nuevo): motor de búsqueda. «artículo N» muestra solo ese artículo; palabras y frases sin importar tildes ni mayúsculas, sin palabras vacías; devuelve qué subrayar en amarillo.
- `app/src/main/java/com/example/ui/BookSearchUi.kt` (nuevo): `highlightedText`, `PdfHighlightOverlay` (amarillo sobre la imagen del PDF) y `PageTextSheet` (panel «Texto de la página»).
- `app/src/main/java/com/example/data/BookInternalSearchEngine.kt` (reemplazar completo): ahora es una fachada de `BookSearch`; se eliminaron los resultados de respaldo con páginas escritas a mano.

## 3) Cambios a archivos existentes (aplicar exactamente estos diffs)

### ui/PdfReaderScreen.kt
```diff
diff --git a/app/src/main/java/com/example/ui/PdfReaderScreen.kt b/app/src/main/java/com/example/ui/PdfReaderScreen.kt
index f17fdd3..de23b72 100644
--- a/app/src/main/java/com/example/ui/PdfReaderScreen.kt
+++ b/app/src/main/java/com/example/ui/PdfReaderScreen.kt
@@ -40,6 +40,7 @@ import com.example.viewmodel.ElectoralViewModel
 fun PdfReaderScreen(
     document: PdfDocument,
     initialPageIndex: Int = 0,
+    highlightQuery: String = "",
     onClose: () -> Unit,
     viewModel: ElectoralViewModel,
     modifier: Modifier = Modifier
@@ -52,6 +53,10 @@ fun PdfReaderScreen(
     var currentBitmap by remember { mutableStateOf<Bitmap?>(null) }
     var isRenderingPage by remember { mutableStateOf(false) }
     var showInReaderSearch by remember { mutableStateOf(false) }
+    // Lo buscado se subraya en amarillo sobre la página (viene de la búsqueda del libro) y en el panel de texto.
+    var activeHighlight by remember(document, highlightQuery) { mutableStateOf(highlightQuery) }
+    var highlightBoxes by remember { mutableStateOf<List<com.example.data.BookSearch.Box>>(emptyList()) }
+    var showPageText by remember { mutableStateOf(false) }
 
     // Touch gesture zoom and pan
     var scale by remember { mutableStateOf(1f) }
@@ -87,6 +92,15 @@ fun PdfReaderScreen(
 
     val totalPages = renderSession?.totalPages ?: document.pages.size.coerceAtLeast(1)
 
+    // Posición de las palabras a subrayar en la página actual (se calcula fuera del hilo principal)
+    LaunchedEffect(currentPageIndex, activeHighlight, document) {
+        highlightBoxes = if (activeHighlight.isBlank()) emptyList<com.example.data.BookSearch.Box>() else kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
+            val file = com.example.data.BookSearch.resolveFileName(document.id, document.assetPath)
+            if (file == null) emptyList<com.example.data.BookSearch.Box>()
+            else com.example.data.BookSearch.highlightBoxes(context.applicationContext, file, currentPageIndex + 1, activeHighlight)
+        }
+    }
+
     // Render bitmap when page or session changes
     LaunchedEffect(currentPageIndex, renderSession) {
         scale = 1f
@@ -163,6 +177,13 @@ fun PdfReaderScreen(
                         .testTag("rendered_pdf_page_image"),
                     contentScale = ContentScale.Fit
                 )
+                PdfHighlightOverlay(
+                    boxes = highlightBoxes,
+                    bitmapWidth = currentBitmap!!.width,
+                    bitmapHeight = currentBitmap!!.height,
+                    scale = scale,
+                    offset = offset
+                )
             } else if (isRenderingPage) {
                 CircularProgressIndicator(
                     color = MaterialTheme.colorScheme.primary,
@@ -302,6 +323,30 @@ fun PdfReaderScreen(
                 )
             }
 
+            // Texto de la página (con lo buscado en amarillo)
+            Button(
+                onClick = { showPageText = true },
+                colors = ButtonDefaults.buttonColors(
+                    containerColor = Color.Black.copy(alpha = 0.75f),
+                    contentColor = Color.White
+                ),
+                shape = RoundedCornerShape(20.dp),
+                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
+                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
+                modifier = Modifier
+                    .height(38.dp)
+                    .testTag("btn_reader_page_text")
+            ) {
+                Icon(
+                    imageVector = Icons.Default.Description,
+                    contentDescription = "Texto de la página",
+                    tint = SearchHighlightYellow,
+                    modifier = Modifier.size(18.dp)
+                )
+                Spacer(modifier = Modifier.width(6.dp))
+                Text(text = "TEXTO", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = Color.White)
+            }
+
             // Close button
             Button(
                 onClick = onClose,
@@ -337,13 +382,24 @@ fun PdfReaderScreen(
             InReaderSearchModal(
                 document = document,
                 onClose = { showInReaderSearch = false },
-                onSelectPage = { page ->
+                onSelectPage = { page, query ->
                     currentPageIndex = (page - 1).coerceIn(0, totalPages - 1)
+                    activeHighlight = query
                     showInReaderSearch = false
                 }
             )
         }
 
+        if (showPageText) {
+            PageTextSheet(
+                document = document,
+                pageNumber = currentPageIndex + 1,
+                query = activeHighlight,
+                onClearHighlight = { activeHighlight = "" },
+                onClose = { showPageText = false }
+            )
+        }
+
         // Minimalist Bottom Page Indicator & Navigation (Discreet overlay)
         if (totalPages > 1) {
             Surface(
@@ -406,21 +462,30 @@ fun PdfReaderScreen(
 private fun InReaderSearchModal(
     document: PdfDocument,
     onClose: () -> Unit,
-    onSelectPage: (Int) -> Unit
+    onSelectPage: (Int, String) -> Unit
 ) {
     val context = androidx.compose.ui.platform.LocalContext.current
     var query by remember { mutableStateOf("") }
-    val results = remember(query, document) {
-        if (query.trim().isNotEmpty()) {
-            BookInternalSearchEngine.searchDocument(
-                documentId = document.id,
-                documentTitle = document.title,
-                query = query,
-                assetPath = document.assetPath,
-                context = context
-            )
+    // Búsqueda en segundo plano, con una pequeña espera mientras se escribe
+    var results by remember { mutableStateOf<List<com.example.data.BookSearchResult>>(emptyList()) }
+    var searchMessage by remember { mutableStateOf<String?>(null) }
+    var isSearching by remember { mutableStateOf(false) }
+    LaunchedEffect(query, document) {
+        if (query.trim().isEmpty()) {
+            results = emptyList(); searchMessage = null; isSearching = false
         } else {
-            emptyList()
+            isSearching = true
+            kotlinx.coroutines.delay(250)
+            val (found, message) = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
+                BookInternalSearchEngine.searchOutcome(
+                    documentId = document.id,
+                    documentTitle = document.title,
+                    query = query,
+                    assetPath = document.assetPath,
+                    context = context
+                )
+            }
+            results = found; searchMessage = message; isSearching = false
         }
     }
 
@@ -499,6 +564,13 @@ private fun InReaderSearchModal(
                         modifier = Modifier.padding(24.dp)
                     )
                 }
+            } else if (results.isEmpty() && isSearching) {
+                Box(
+                    modifier = Modifier
+                        .fillMaxWidth()
+                        .weight(1f),
+                    contentAlignment = Alignment.Center
+                ) { CircularProgressIndicator(modifier = Modifier.size(32.dp)) }
             } else if (results.isEmpty()) {
                 Box(
                     modifier = Modifier
@@ -507,7 +579,7 @@ private fun InReaderSearchModal(
                     contentAlignment = Alignment.Center
                 ) {
                     Text(
-                        text = "No se encontraron coincidencias en ${document.title}.",
+                        text = searchMessage ?: "No se encontraron coincidencias en ${document.title}.",
                         fontSize = 13.5.sp,
                         color = MaterialTheme.colorScheme.onSurfaceVariant,
                         textAlign = androidx.compose.ui.text.style.TextAlign.Center
@@ -530,7 +602,7 @@ private fun InReaderSearchModal(
                     items(results.size) { index ->
                         val res = results[index]
                         Card(
-                            onClick = { onSelectPage(res.pageNumber) },
+                            onClick = { onSelectPage(res.pageNumber, query) },
                             shape = RoundedCornerShape(10.dp),
                             colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                             modifier = Modifier.fillMaxWidth()
@@ -563,7 +635,7 @@ private fun InReaderSearchModal(
                                 }
                                 Spacer(modifier = Modifier.height(4.dp))
                                 Text(
-                                    text = res.snippet,
+                                    text = highlightedText(res.snippet, res.highlights),
                                     fontSize = 12.sp,
                                     lineHeight = 16.sp,
                                     color = MaterialTheme.colorScheme.onSurfaceVariant
```

### ui/BookInternalSearchScreen.kt
```diff
diff --git a/app/src/main/java/com/example/ui/BookInternalSearchScreen.kt b/app/src/main/java/com/example/ui/BookInternalSearchScreen.kt
index 4608899..0b5c57d 100644
--- a/app/src/main/java/com/example/ui/BookInternalSearchScreen.kt
+++ b/app/src/main/java/com/example/ui/BookInternalSearchScreen.kt
@@ -19,6 +19,9 @@ import androidx.compose.ui.Modifier
 import androidx.compose.ui.draw.clip
 import androidx.compose.ui.graphics.Color
 import androidx.compose.ui.platform.testTag
+import kotlinx.coroutines.Dispatchers
+import kotlinx.coroutines.delay
+import kotlinx.coroutines.withContext
 import androidx.compose.ui.text.SpanStyle
 import androidx.compose.ui.text.buildAnnotatedString
 import androidx.compose.ui.text.font.FontWeight
@@ -34,24 +37,33 @@ import com.example.data.PdfDocument
 fun BookInternalSearchScreen(
     document: PdfDocument,
     onClose: () -> Unit,
-    onOpenPage: (Int) -> Unit,
+    onOpenPage: (Int, String) -> Unit,
     onConsultAi: (queryText: String) -> Unit,
     modifier: Modifier = Modifier
 ) {
     val context = androidx.compose.ui.platform.LocalContext.current
     var searchQuery by remember { mutableStateOf("") }
 
-    val searchResults = remember(searchQuery, document) {
-        if (searchQuery.trim().isNotEmpty()) {
-            BookInternalSearchEngine.searchDocument(
-                documentId = document.id,
-                documentTitle = document.title,
-                query = searchQuery,
-                assetPath = document.assetPath,
-                context = context
-            )
+    // La búsqueda corre en segundo plano y espera a que el usuario termine de escribir (no traba la pantalla).
+    var searchResults by remember { mutableStateOf<List<BookSearchResult>>(emptyList()) }
+    var searchMessage by remember { mutableStateOf<String?>(null) }
+    var isSearching by remember { mutableStateOf(false) }
+    LaunchedEffect(searchQuery, document) {
+        if (searchQuery.trim().isEmpty()) {
+            searchResults = emptyList(); searchMessage = null; isSearching = false
         } else {
-            emptyList()
+            isSearching = true
+            delay(250)
+            val (found, message) = withContext(Dispatchers.Default) {
+                BookInternalSearchEngine.searchOutcome(
+                    documentId = document.id,
+                    documentTitle = document.title,
+                    query = searchQuery,
+                    assetPath = document.assetPath,
+                    context = context
+                )
+            }
+            searchResults = found; searchMessage = message; isSearching = false
         }
     }
 
@@ -60,8 +72,8 @@ fun BookInternalSearchScreen(
         when (document.id) {
             "codigo_electoral_decreto_413" -> listOf(
                 "Art. 190 (Instalación)", "Art. 3 (Sufragio)", "Art. 7 (Inhábiles)",
-                "Art. 38 (Organismos)", "Art. 113 (Permiso laboral)", "Art. 284 (Armas)",
-                "Art. 205 (Voto nulo)", "Escrutinio", "Vigilantes"
+                "Art. 38 (Organismos)", "Art. 113 (Permiso laboral)", "Art. 290 (Armas)",
+                "Art. 207 (Voto nulo)", "Escrutinio", "Vigilantes"
             )
             "constitucion_republica_1983" -> listOf(
                 "Art. 72 (Derechos)", "Art. 73 (Deberes)", "Art. 75 (Pérdida derechos)",
@@ -74,9 +86,6 @@ fun BookInternalSearchScreen(
             "ley_de_partidos_politicos" -> listOf(
                 "Vigilantes", "Fiscalización", "Propaganda", "Derechos partidarios"
             )
-            "ley_genero_electoral" -> listOf(
-                "Violencia política", "Paridad", "Inclusión", "LEIV"
-            )
             else -> listOf("Artículos", "Votación", "Escrutinio", "JRV", "Sanciones")
         }
     }
@@ -255,6 +264,10 @@ fun BookInternalSearchScreen(
                     textAlign = androidx.compose.ui.text.style.TextAlign.Center
                 )
             }
+        } else if (searchResults.isEmpty() && isSearching) {
+            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
+                CircularProgressIndicator(modifier = Modifier.size(32.dp))
+            }
         } else if (searchResults.isEmpty()) {
             // No results state
             Column(
@@ -279,7 +292,7 @@ fun BookInternalSearchScreen(
                 )
                 Spacer(modifier = Modifier.height(6.dp))
                 Text(
-                    text = "No se encontraron coincidencias para «$searchQuery» en ${document.title}. Intenta con otras palabras clave o número de artículo.",
+                    text = searchMessage ?: "No se encontraron coincidencias para «$searchQuery» en ${document.title}. Intenta con otras palabras clave o número de artículo.",
                     fontSize = 12.5.sp,
                     color = MaterialTheme.colorScheme.onSurfaceVariant,
                     textAlign = androidx.compose.ui.text.style.TextAlign.Center
@@ -319,9 +332,12 @@ fun BookInternalSearchScreen(
                     BookSearchResultCard(
                         result = result,
                         searchQuery = searchQuery,
-                        onOpenPage = { onOpenPage(result.pageNumber) },
+                        onOpenPage = { onOpenPage(result.pageNumber, searchQuery) },
                         onConsultAi = {
-                            val prompt = "¿Qué establece el ${result.articleRef ?: result.sectionTitle} de '${result.documentTitle}' y cuál es su procedimiento?"
+                            // Si el libro tiene banco de artículos, se busca dentro del libro (el artículo o lo que escribió el usuario).
+                            val hasBank = com.example.data.search.codigo.LawLibrary.byPdfDocId(document.id) != null
+                            val prompt = if (hasBank) (result.articleRef ?: searchQuery)
+                            else "¿Qué establece el ${result.articleRef ?: result.sectionTitle} de '${result.documentTitle}' y cuál es su procedimiento?"
                             onConsultAi(prompt)
                         }
                     )
@@ -396,8 +412,8 @@ fun BookSearchResultCard(
             Spacer(modifier = Modifier.height(6.dp))
 
             // Snippet with highlighted search query
-            val annotatedSnippet = remember(result.snippet, searchQuery) {
-                buildHighlightedString(result.snippet, searchQuery)
+            val annotatedSnippet = remember(result.snippet, result.highlights) {
+                highlightedText(result.snippet, result.highlights)
             }
 
             Surface(
```

### ui/BibliotecaScreen.kt
```diff
diff --git a/app/src/main/java/com/example/ui/BibliotecaScreen.kt b/app/src/main/java/com/example/ui/BibliotecaScreen.kt
index 3f4951e..93720cc 100644
--- a/app/src/main/java/com/example/ui/BibliotecaScreen.kt
+++ b/app/src/main/java/com/example/ui/BibliotecaScreen.kt
@@ -30,14 +30,17 @@ fun BibliotecaScreen(
     val context = LocalContext.current
     val directDoc by viewModel.activePdfReaderDoc.collectAsState()
     val directPage by viewModel.activePdfReaderPage.collectAsState()
+    val directHighlight by viewModel.activePdfReaderHighlight.collectAsState()
     var viewingDocument by remember { mutableStateOf<PdfDocument?>(null) }
     var searchingDocument by remember { mutableStateOf<PdfDocument?>(null) }
     var readerInitialPage by remember { mutableStateOf(0) }
+    var readerHighlightQuery by remember { mutableStateOf("") }
 
     LaunchedEffect(directDoc, directPage) {
         if (directDoc != null) {
             viewingDocument = directDoc
             readerInitialPage = directPage
+            readerHighlightQuery = directHighlight
         }
     }
 
@@ -101,8 +104,9 @@ fun BibliotecaScreen(
         BookInternalSearchScreen(
             document = searchingDocument!!,
             onClose = { searchingDocument = null },
-            onOpenPage = { page ->
+            onOpenPage = { page, query ->
                 viewingDocument = searchingDocument
+                readerHighlightQuery = query
                 readerInitialPage = (page - 1).coerceAtLeast(0)
                 searchingDocument = null
             },
@@ -122,9 +126,11 @@ fun BibliotecaScreen(
         PdfReaderScreen(
             document = viewingDocument!!,
             initialPageIndex = readerInitialPage,
+            highlightQuery = readerHighlightQuery,
             onClose = {
                 viewingDocument = null
                 readerInitialPage = 0
+                readerHighlightQuery = ""
                 viewModel.closeDirectPdfReader()
             },
             viewModel = viewModel,
@@ -271,6 +277,7 @@ fun BibliotecaScreen(
                                 onClick = {
                                     viewingDocument = doc
                                     readerInitialPage = 0
+                                    readerHighlightQuery = ""
                                 },
                                 shape = RoundedCornerShape(10.dp),
                                 modifier = Modifier
```

## 4) No toques nada más. Pruebas (en el teléfono)
1. Biblioteca → Código Electoral → buscador del libro → escribe `artículo 2`: debe salir SOLO el Art. 2 (Pág. 1), no una lista larga. `198` → Art. 198, página 67. `artículos 200 al 202` → 3 resultados. `art 9999` → mensaje «no aparece en este documento».
2. Escribe `ebriedad`: resultado en la página 85 con la palabra en amarillo en el fragmento. Toca el resultado: se abre la página 85 con la palabra subrayada en amarillo SOBRE el PDF.
3. Escribe `bebidas embriagantes` (frase): resultado en la página 96 y las dos palabras en amarillo. Prueba con y sin tildes y en mayúsculas (`EBRIEDAD`): mismo resultado.
4. Botón «TEXTO» (arriba del lector): panel con el texto de la página y lo buscado en amarillo; «Quitar subrayado» lo limpia.
5. Haz zoom con dos dedos y mueve la página: el amarillo debe seguir pegado a las palabras.
6. Escribe despacio en el buscador: no debe trabarse la pantalla (la búsqueda espera un instante y corre en segundo plano).
7. Repite en el Código Penal, el Instructivo JRV (buscar `papeletas sobrantes` → páginas 34, 35, 37, 40, 46) y la Constitución (`72` → página 15).
8. Abre un PDF con «ABRIR PDF»: NO debe aparecer subrayado.
