# PROMPT 9 para AI Studio — «Buscar en libros» con bancos de artículos (Bloque C, partes 2 a 5)

Aplica DESPUÉS del Prompt 8. Este prompt REEMPLAZA a los Prompts 2 y 3 (ya no se activa escribiendo «código electoral»: ahora hay un botón «Buscar en libros»). Si ya aplicaste el 2 o el 3, sobrescribe sus archivos con los de aquí. Los archivos vienen sueltos (sin carpetas) en `entrega_bloque_C_planos`; en `COMO_SUBIR_A_AI_STUDIO.md` está la ruta de destino de cada uno. Si tu proyecto está conectado a GitHub, ya están en la rama `claude/electoral-knowledge-base-json-ha2mpw`.

---

Quiero que el chat tenga un botón «Buscar en libros». Al tocarlo se elige un libro (10 disponibles); después, la pregunta escrita o dictada con el micrófono se busca DENTRO de ese libro y se muestran tarjetas con el artículo (título, página, texto, botón para abrir el PDF subrayado). Todo local: sin internet y sin inteligencia artificial. Los bancos se cargan solo la primera vez que se usan (no al abrir la app).

## 1) Datos (assets) — copiar tal cual
- `app/src/main/assets/leyes/` (nueva carpeta, 8 archivos): `ley_partidos.json`, `ley_acceso_informacion.json`, `ley_sufragio_exterior.json`, `ley_reestructuracion_municipal.json`, `reglamento_observacion.json`, `candidaturas_no_partidarias.json`, `acuerdo_legislativo.json`, `codigo_penal_electoral.json`.
- `app/src/main/assets/codigo_electoral/banco_codigo_electoral.json` y `app/src/main/assets/constitucion/banco_constitucion.json` (si ya los tienes, reemplázalos por los adjuntos).

## 2) Código — copiar los archivos completos
- `app/src/main/java/com/example/data/search/codigo/ElectoralCodeBank.kt` (reemplazar)
- `app/src/main/java/com/example/data/search/codigo/ElectoralCodeSearch.kt` (reemplazar)
- `app/src/main/java/com/example/data/search/codigo/LawLibrary.kt` (nuevo)
- `app/src/main/java/com/example/ui/BuscarEnLibros.kt` (nuevo): selector de libro, distintivo «Buscando en: …» y tarjetas de resultados.

## 3) Cambios a archivos existentes (aplicar exactamente estos diffs)

### viewmodel/ElectoralViewModel.kt
```diff
diff --git a/app/src/main/java/com/example/viewmodel/ElectoralViewModel.kt b/app/src/main/java/com/example/viewmodel/ElectoralViewModel.kt
index b94b382..70480eb 100644
--- a/app/src/main/java/com/example/viewmodel/ElectoralViewModel.kt
+++ b/app/src/main/java/com/example/viewmodel/ElectoralViewModel.kt
@@ -64,6 +64,16 @@ class ElectoralViewModel(private val repository: ElectoralRepository) : ViewMode
     private val _activePdfReaderDoc = MutableStateFlow<com.example.data.PdfDocument?>(null)
     val activePdfReaderDoc: StateFlow<com.example.data.PdfDocument?> = _activePdfReaderDoc.asStateFlow()
 
+    private val _activePdfReaderHighlight = MutableStateFlow("")
+    val activePdfReaderHighlight: StateFlow<String> = _activePdfReaderHighlight.asStateFlow()
+
+    // «Buscar en libros»: libro elegido (id, título). Mientras está activo, las preguntas (escritas o dictadas) buscan en ese libro.
+    private val _bookSearchBook = MutableStateFlow<Pair<String, String>?>(null)
+    val bookSearchBook: StateFlow<Pair<String, String>?> = _bookSearchBook.asStateFlow()
+
+    fun setBookSearchBook(id: String, title: String) { _bookSearchBook.value = id to title }
+    fun clearBookSearchBook() { _bookSearchBook.value = null }
+
     private val _activePdfReaderPage = MutableStateFlow<Int>(0)
     val activePdfReaderPage: StateFlow<Int> = _activePdfReaderPage.asStateFlow()
 
@@ -156,8 +166,16 @@ class ElectoralViewModel(private val repository: ElectoralRepository) : ViewMode
     }
 
     fun startDocumentSpecificQuery(docId: String, docTitle: String) {
-        _preferredBookId.value = docId
-        _preferredBookTitle.value = docTitle
+        val book = com.example.data.search.codigo.LawLibrary.byPdfDocId(docId)
+        if (book != null) {
+            // El libro tiene banco de artículos: la pregunta se busca DENTRO de ese libro.
+            _preferredBookId.value = null
+            _preferredBookTitle.value = null
+            _bookSearchBook.value = book.id to book.title
+        } else {
+            _preferredBookId.value = docId
+            _preferredBookTitle.value = docTitle
+        }
         _currentScreen.value = ElectoralScreen.CHAT
     }
 
@@ -166,12 +184,13 @@ class ElectoralViewModel(private val repository: ElectoralRepository) : ViewMode
         _preferredBookTitle.value = null
     }
 
-    fun openPdfDirectly(docId: String, page: Int = 1) {
+    fun openPdfDirectly(docId: String, page: Int = 1, highlight: String = "") {
         val foundDoc = com.example.data.ElectoralLibraryData.documents.firstOrNull { it.id == docId }
             ?: com.example.data.ElectoralLibraryData.documents.firstOrNull()
         if (foundDoc != null) {
             _activePdfReaderDoc.value = foundDoc
             _activePdfReaderPage.value = (page - 1).coerceAtLeast(0)
+            _activePdfReaderHighlight.value = highlight
             _currentScreen.value = ElectoralScreen.BIBLIOTECA
         }
     }
@@ -179,6 +198,7 @@ class ElectoralViewModel(private val repository: ElectoralRepository) : ViewMode
     fun closeDirectPdfReader() {
         _activePdfReaderDoc.value = null
         _activePdfReaderPage.value = 0
+        _activePdfReaderHighlight.value = ""
     }
 
     fun getSuggestionsForInput(input: String): List<String> {
@@ -281,6 +301,20 @@ class ElectoralViewModel(private val repository: ElectoralRepository) : ViewMode
             _queryInput.value = ""
         }
 
+        // Modo «Buscar en libros»: la pregunta se busca en el libro elegido (sin pasar por el asistente de situaciones).
+        val bookMode = _bookSearchBook.value
+        if (bookMode != null) {
+            _currentScreen.value = ElectoralScreen.CHAT
+            if (isFromVoice) {
+                // El dictado ya terminó: no hay respuesta que leer en voz alta, se muestran las tarjetas.
+                _voiceState.value = VoiceConversationState.IDLE
+                _voiceStatusMessage.value = "Pulsa el micrófono para hablar"
+                _isVoiceTurn.value = false
+            }
+            _uiState.value = ElectoralUiState.BookSearch(queryToSend, bookMode.first, bookMode.second)
+            return
+        }
+
         if (isFromVoice) {
             _isVoiceTurn.value = true
             _voiceState.value = VoiceConversationState.PROCESSING
@@ -357,6 +391,8 @@ sealed class ElectoralUiState {
         val primaryPdfPage: Int = 1
     ) : ElectoralUiState()
     data class Error(val question: String, val message: String) : ElectoralUiState()
+    /** Resultado de «Buscar en libros»: se muestran artículos del libro elegido. */
+    data class BookSearch(val question: String, val bookId: String, val bookTitle: String) : ElectoralUiState()
 }
 
 class ElectoralViewModelFactory(private val repository: ElectoralRepository) : ViewModelProvider.Factory {
```

### ui/ElectoralMainScreen.kt
```diff
diff --git a/app/src/main/java/com/example/ui/ElectoralMainScreen.kt b/app/src/main/java/com/example/ui/ElectoralMainScreen.kt
index 36d39b1..a0ad9f5 100644
--- a/app/src/main/java/com/example/ui/ElectoralMainScreen.kt
+++ b/app/src/main/java/com/example/ui/ElectoralMainScreen.kt
@@ -66,6 +66,8 @@ fun ElectoralMainScreen(
 
     val queryInput by viewModel.queryInput.collectAsState()
     val uiState by viewModel.uiState.collectAsState()
+    val bookSearchBook by viewModel.bookSearchBook.collectAsState()
+    var showBookPicker by remember { mutableStateOf(false) }
     val voiceState by viewModel.voiceState.collectAsState()
     val voiceStatusMessage by viewModel.voiceStatusMessage.collectAsState()
     val ttsPlaybackState by viewModel.ttsPlaybackState.collectAsState()
@@ -230,8 +232,7 @@ fun ElectoralMainScreen(
                         "¿Cómo se integra una JRV?" to "¿Cómo se integra una Junta Receptora de Votos (JRV)?",
                         "Permisos de trabajo (Art. 113)" to "¿Qué establece el Art. 113 del Código Electoral sobre los permisos laborales con goce de sueldo?",
                         "Prohibición de armas en centros" to "¿Qué normativa regula la prohibición de armas en centros de votación en El Salvador?",
-                        "Procedimiento de Escrutinio" to "¿Cuál es el procedimiento paso a paso para realizar el escrutinio de votos?",
-                        "Protección a la mujer (LEIV)" to "¿Cómo protege la ley a las mujeres miembros de mesa contra violencia política?"
+                        "Procedimiento de Escrutinio" to "¿Cuál es el procedimiento paso a paso para realizar el escrutinio de votos?"
                     )
                     
                     recommendedList.forEach { (label, question) ->
@@ -942,6 +943,16 @@ fun ElectoralMainScreen(
                                     }
                                 }
 
+                                is ElectoralUiState.BookSearch -> {
+                                    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
+                                        BookSearchResultsView(
+                                            question = state.question,
+                                            bookId = state.bookId,
+                                            bookTitle = state.bookTitle,
+                                            viewModel = viewModel
+                                        )
+                                    }
+                                }
                                 is ElectoralUiState.Error -> {
                                     Column(
                                         modifier = Modifier
@@ -1015,6 +1026,37 @@ fun ElectoralMainScreen(
                             .padding(horizontal = 14.dp, vertical = 4.dp),
                         horizontalAlignment = Alignment.CenterHorizontally
                     ) {
+                        // «Buscar en libros»: distintivo del libro elegido o botón para elegirlo
+                        if (bookSearchBook != null) {
+                            BookSearchBadge(
+                                bookTitle = bookSearchBook!!.second,
+                                onClear = { viewModel.clearBookSearchBook() },
+                                modifier = Modifier.padding(bottom = 6.dp)
+                            )
+                        } else {
+                            OutlinedButton(
+                                onClick = { showBookPicker = true },
+                                shape = RoundedCornerShape(10.dp),
+                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
+                                modifier = Modifier
+                                    .fillMaxWidth()
+                                    .height(34.dp)
+                                    .padding(bottom = 2.dp)
+                                    .testTag("btn_buscar_en_libros")
+                            ) {
+                                Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(15.dp))
+                                Spacer(modifier = Modifier.width(6.dp))
+                                Text("Buscar en libros", fontSize = 12.sp, fontWeight = FontWeight.Bold)
+                            }
+                            Spacer(modifier = Modifier.height(4.dp))
+                        }
+                        if (showBookPicker) {
+                            BookPickerSheet(
+                                onPick = { book -> viewModel.setBookSearchBook(book.id, book.title); showBookPicker = false },
+                                onClose = { showBookPicker = false }
+                            )
+                        }
+
                         // Focus on specific book badge if selected
                         if (preferredBookTitle != null) {
                             Surface(
```

### data/search/knowledge/ElectoralKnowledgeBase.kt
```diff
diff --git a/app/src/main/java/com/example/data/search/knowledge/ElectoralKnowledgeBase.kt b/app/src/main/java/com/example/data/search/knowledge/ElectoralKnowledgeBase.kt
index 4c7b5c4..e33b755 100644
--- a/app/src/main/java/com/example/data/search/knowledge/ElectoralKnowledgeBase.kt
+++ b/app/src/main/java/com/example/data/search/knowledge/ElectoralKnowledgeBase.kt
@@ -935,8 +935,6 @@ object ElectoralKnowledgeBase {
      */
     fun init(context: Context) {
         com.example.data.search.synonyms.ElectoralConceptRegistry.init(context)
-        com.example.data.search.codigo.ElectoralCodeBank.init(context)
-        com.example.data.search.codigo.ElectoralConstitutionBank.init(context)
         try {
             val jsonSituations = JsonKnowledgeLoader.loadAllFromAssets(context)
             synchronized(_loadedSituations) {
```

### data/repository/ElectoralRepository.kt (se quita la sugerencia de la LEIV; la Ley de Género no va en la app)
```diff
diff --git a/app/src/main/java/com/example/data/repository/ElectoralRepository.kt b/app/src/main/java/com/example/data/repository/ElectoralRepository.kt
index 3d6f5e5..7d0c5a3 100644
--- a/app/src/main/java/com/example/data/repository/ElectoralRepository.kt
+++ b/app/src/main/java/com/example/data/repository/ElectoralRepository.kt
@@ -43,7 +43,6 @@ class ElectoralRepository(
         "¿Qué derechos y prohibiciones tienen los vigilantes de partidos políticos?",
         "¿Cuáles son las atribuciones del Tribunal Supremo Electoral (TSE)?",
         "¿Quiénes están autorizados a portar armas y cómo aplica el Art. 290 del Código Electoral?",
-        "¿Cómo protege la ley LEIV (Art. 55) a las mujeres en el proceso electoral?",
         "¿Qué sanciones contempla la Ley Seca (Art. 284) y los delitos electorales?",
         "¿Qué hacer si un ciudadano no presenta su DUI o no aparece en el padrón?",
         "¿Cómo se garantiza el voto asistido para personas con discapacidad o adultos mayores?",
```

### data/ElectoralPdfData.kt (corrige el resumen del Código Penal: el único delito electoral del PDF es el Art. 295; los Arts. 296 a 298 que decía el resumen NO eran electorales)
```diff
diff --git a/app/src/main/java/com/example/data/ElectoralPdfData.kt b/app/src/main/java/com/example/data/ElectoralPdfData.kt
index 22a0708..a8ab61b 100644
--- a/app/src/main/java/com/example/data/ElectoralPdfData.kt
+++ b/app/src/main/java/com/example/data/ElectoralPdfData.kt
@@ -334,33 +334,21 @@ object ElectoralLibraryData {
             id = "codigo_penal_delitos_electorales",
             title = "Código Penal de El Salvador (Delitos Electorales)",
             category = "Sanciones, Delitos y Recursos",
-            summary = "Tipificación de delitos contra los derechos cívicos y electorales (Arts. 295 a 302): Fraude electoral, coacción del sufragio, falsedad documental, obstaculización, usurpación de funciones y penas de prisión de hasta 15 años.",
+            summary = "Delito electoral del Código Penal de El Salvador: Fraude electoral (Art. 295), con sus circunstancias y penas. El resto del Código Penal puede consultarse en el PDF.",
             authority = "Asamblea Legislativa • Órgano Judicial de El Salvador",
             releaseDate = "Código Penal Consolidado",
             assetPath = "libros/Código Penal.pdf",
             pages = listOf(
                 PdfPage(
                     pageNumber = 1,
-                    headerTitle = "CÓDIGO PENAL • LIBRO SEGUNDO • TÍTULO XIII",
-                    subtitle = "DELITOS CONTRA LOS DERECHOS CÍVICOS Y ELECTORALES",
+                    headerTitle = "CÓDIGO PENAL • LIBRO SEGUNDO • TÍTULO XIV",
+                    subtitle = "DELITOS RELATIVOS A LOS DERECHOS Y GARANTÍAS FUNDAMENTALES DE LA PERSONA",
                     sections = listOf(
                         PdfSection(
                             title = "Fraude Electoral (Art. 295)",
-                            content = "Art. 295.- Votar más de una vez, suplantar a otro elector, votar sin derecho o alterar padrones o actas. Pena: 4 a 6 años de prisión (aumenta si es miembro de organismo electoral).",
+                            content = "Art. 295.- Prisión de cuatro a seis años si el fraude electoral se comete, entre otras circunstancias, votando sin derecho, suplantando a otro elector o votando más de una vez; pagando u ofreciendo beneficios para inducir el voto o la abstención; alterando el registro electoral; sustrayendo o destruyendo el padrón, las papeletas o las urnas; impidiendo u obstaculizando el registro, el ejercicio del sufragio o el escrutinio; coaccionando a un elector o violando el secreto del voto; o cambiando de domicilio para votar en otra circunscripción. Si participan funcionarios públicos o electorales: prisión de siete a diez años e inhabilitación del cargo por igual período. Lea el texto completo en el PDF.",
                             articleRef = "Art. 295",
                             isWarning = true
-                        ),
-                        PdfSection(
-                            title = "Coacción del Sufragio (Art. 296)",
-                            content = "Art. 296.- Impedir, amenazar o coartar el voto de un elector o forzarlo a votar en determinado sentido o abstenerse. Pena: 3 a 6 años de prisión.",
-                            articleRef = "Art. 296",
-                            isWarning = true
-                        ),
-                        PdfSection(
-                            title = "Falsedad y Obstaculización (Arts. 297-298)",
-                            content = "Arts. 297 y 298.- Alterar o destruir credenciales, actas, papeletas o perturbar la instalación o escrutinio de JRV. Pena: 4 a 8 años de prisión.",
-                            articleRef = "Arts. 297-298",
-                            isWarning = true
                         )
                     )
                 )
```

(`ui/BibliotecaScreen.kt` y `ui/BookInternalSearchScreen.kt` ya traen sus cambios del Prompt 8; el botón «CONSULTAR ASISTENTE» de la Biblioteca ahora abre «Buscar en libros» con ese libro elegido.)

## 4) Pruebas (en el teléfono)
1. En el chat aparece el botón «Buscar en libros» sobre la caja de texto. Tócalo: lista de 10 libros.
2. Elige «Código Electoral» y escribe `198`: tarjeta «Art. 198 · Cierre de la Votación», Pág. 67. Escribe `bebidas embriagantes`: Art. 284. `artículos 200 al 203`: 4 tarjetas.
3. Toca «Abrir en el PDF»: se abre la página con lo buscado en amarillo.
4. Toca el micrófono y di «bebidas embriagantes»: debe mostrar las tarjetas (no lee la respuesta en voz alta; es normal).
5. Elige «Constitución»: `72` → Art. 72; `tribunal supremo electoral` → Art. 208. «Ley de Partidos Políticos»: `cuota de género` → Art. 38. «Código Penal (delitos electorales)»: `fraude electoral` → Art. 295.
6. Una búsqueda sin resultados (`zzzz`) muestra «Sin resultados en este libro» y no inventa.
7. El distintivo «Buscando en: …» se quita con la X y el chat vuelve a funcionar normal (cualquier pregunta sobre situaciones, como «el votante olvidó su DUI»).
8. En Biblioteca → un libro → buscador → «Preguntar al asistente» abre el chat con ese libro elegido y el artículo buscado.
9. El Instructivo JRV y el Ciclo Electoral NO aparecen en la lista (se buscan solo desde su lector PDF).
10. Abre la app: no debe notarse más lenta al arrancar.
