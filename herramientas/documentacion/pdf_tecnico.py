#!/usr/bin/env python3
"""PDF técnico: arquitectura, herramientas, carpetas, auditoría de seguridad y rutas a web / iPhone."""
import sys
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, PageBreak, KeepTogether
from reportlab.graphics.shapes import Drawing, Rect, String, Line
from reportlab.lib.units import mm
from comun import *

salida = sys.argv[1]
doc = SimpleDocTemplate(salida, pagesize=A4, leftMargin=18*mm, rightMargin=18*mm, topMargin=18*mm, bottomMargin=20*mm,
                        title="Asistente Electoral SV — Documentación técnica y auditoría", author="Proyecto Asistente Electoral SV")
W = A4[0] - 36*mm
E = []
def h1(t): E.append(Paragraph(t, H1))
def h2(t): E.append(Paragraph(t, H2))
def p(t): E.append(Paragraph(t, P))
def bl(items): [E.append(Paragraph(x, BUL, bulletText="•")) for x in items]
def code(t): E.append(Paragraph(t.replace("\n", "<br/>").replace(" ", "&nbsp;"), CODE))
def tabla(filas, anchos, cab=True, zebra=True):
    data = [[Paragraph(str(c), CELLB if (cab and i == 0) else CELL) for c in f] for i, f in enumerate(filas)]
    t = Table(data, colWidths=[a * W for a in anchos], repeatRows=1 if cab else 0)
    st = [("VALIGN", (0,0), (-1,-1), "TOP"), ("GRID", (0,0), (-1,-1), 0.3, colors.HexColor("#C9D3D8")),
          ("TOPPADDING", (0,0), (-1,-1), 3), ("BOTTOMPADDING", (0,0), (-1,-1), 3)]
    if cab: st += [("BACKGROUND", (0,0), (-1,0), CLARO)]
    if zebra: st += [("ROWBACKGROUNDS", (0,1), (-1,-1), [colors.white, colors.HexColor("#F8FBFC")])]
    t.setStyle(TableStyle(st)); E.append(t); E.append(Spacer(1, 6))

# ---------------- Portada
E += [Spacer(1, 36*mm), Paragraph("Asistente Electoral SV", ParagraphStyle("t", fontName="DVB", fontSize=28, leading=32, textColor=NAVY)),
      Paragraph("Documentación técnica, auditoría de seguridad y hoja de ruta", ParagraphStyle("s", fontName="DV", fontSize=14, leading=18, textColor=TEAL, spaceAfter=16)),
      Paragraph("Para: ingeniería de sistemas y ciberseguridad. Contiene la arquitectura, las tecnologías, la estructura del repositorio, los hallazgos de seguridad con su prioridad, la verificación de los ajustes realizados y las rutas para llevar la aplicación a <b>web</b> y a <b>iPhone</b>.", P),
      Spacer(1, 8)]
tabla([["Repositorio", "penate-crypto/Asistente-Electoral-SV (rama claude/electoral-knowledge-base-json-ha2mpw)"],
       ["Aplicación", "Android (Kotlin + Jetpack Compose). Id: com.aistudio.asistenteelectoralsv.xkzqm · versión 1.1 (versionCode 2)"],
       ["SDK", "minSdk 24 · targetSdk 35 · compileSdk 36"],
       ["Alcance de la revisión", "Código Android, manifiesto, build, activos (JSON y PDF), versión web, herramientas y pruebas. Revisión estática: no se pudo compilar el APK ni probar en dispositivo en este entorno."],
       ["Principio de diseño", "100% local: sin internet y sin consumo de tokens de IA para responder."]], [0.22, 0.78], cab=False)
E.append(PageBreak())

# ---------------- 1 Resumen
h1("1. Resumen ejecutivo")
p("La aplicación responde consultas sobre procesos electorales de El Salvador con un <b>motor de búsqueda local</b> sobre un banco de conocimiento JSON (154 situaciones), cuyas citas legales se verificaron una por una contra los PDF oficiales incluidos. No llama a ningún servidor: no hay backend que atacar ni que «tumbar». Esa es su mayor fortaleza de disponibilidad y de privacidad.")
p("Los riesgos reales están en otro lado: el <b>acceso (login) no protege nada</b>, el APK de producción <b>no está ofuscado</b>, el contenido es copiable y existen restos de código y permisos de una arquitectura con IA en línea que ya no se usa. Ninguno impide funcionar; sí conviene resolver los de prioridad alta antes de publicar.")
tabla([["Prioridad", "Cantidad", "Detalle"],
       ["Alta", "2", "Login sin validación real · APK sin ofuscar (contenido copiable)"],
       ["Media", "5", "Respaldo automático activo · capa de red/Gemini muerta con logging BODY · permiso INTERNET sin uso · aviso legal que no coincide con el código · dependencias antiguas"],
       ["Baja", "5", "Logs en producción · historial sin cifrar · sin CSP en web · sin pruebas de los módulos nuevos en el proyecto Android · llave de depuración fuera del repo"],
       ["Higiene", "—", "Código sin uso (≈1.800 líneas), PDF duplicados en web, falta README raíz, LICENSE y CI"]], [0.14, 0.12, 0.74])

# ---------------- 2 Arquitectura
h1("2. Arquitectura")
d = Drawing(W, 150)
def caja(x, y, w, h, t, c=TEAL, tc=colors.white, fs=7.4):
    d.add(Rect(x, y, w, h, rx=4, ry=4, fillColor=c, strokeColor=c)); 
    for i, ln in enumerate(t.split("\n")): d.add(String(x + w/2, y + h/2 + (len(t.split("\n"))-1)*4.2 - i*8.6 - 2.5, ln, fontName="DVB" if i == 0 else "DV", fontSize=fs, fillColor=tc, textAnchor="middle"))
caja(0, 100, 105, 44, "INTERFAZ (Compose)\nChat · Biblioteca · Test\nCasos · Paso a paso · Voz", TEAL)
caja(125, 100, 105, 44, "VIEWMODEL\nElectoralViewModel\nestado de pantallas", NAVY)
caja(250, 100, 105, 44, "REPOSITORIO\nElectoralRepository\nHistorial (Room)", NAVY)
caja(0, 40, 105, 44, "MOTOR DE BÚSQUEDA\nNormaliza → entidades\n→ puntaje → respuesta", GRIS)
caja(125, 40, 105, 44, "BANCOS (assets JSON)\n154 situaciones · Código\nElectoral · Constitución", GRIS)
caja(250, 40, 105, 44, "BIBLIOTECA PDF\n12 documentos oficiales\nPdfRenderer + índice", GRIS)
caja(125, 0, 105, 28, "Sin internet · sin tokens", VERDE, fs=8)
for (x1, y1, x2, y2) in [(105, 122, 125, 122), (230, 122, 250, 122), (52, 100, 52, 84), (177, 100, 177, 84), (302, 100, 302, 84), (105, 62, 125, 62)]:
    d.add(Line(x1, y1, x2, y2, strokeColor=NAVY, strokeWidth=1.2))
E.append(d)
p("<b>Flujo de una consulta:</b> el usuario escribe o dicta → <i>ElectoralNormalizer</i> limpia el texto (tildes, jerga, errores) → <i>ElectoralEntityExtractor</i> detecta actores, acciones, objetos, contexto e intención → <i>ElectoralScoringEngine</i> puntúa las 154 situaciones (título, palabras clave, sinónimos, actores, etc.) → <i>ElectoralSearchEngine</i> decide si responde, pide aclarar o rechaza por estar fuera de tema → la pantalla muestra situación, qué hacer, qué no hacer, fundamento legal y el botón «VER FUENTE» que abre el PDF en la página.")
tabla([["Capa", "Archivos principales (app/src/main/java/com/example/…)", "Función"],
       ["UI", "ui/*.kt (14 pantallas, ≈9.400 líneas)", "Pantallas Compose: Login, Legal, Chat, Biblioteca, Lector PDF, Test, Simulación, Paso a paso, Capacitación, Ajustes"],
       ["Estado", "viewmodel/ElectoralViewModel.kt", "Estado de pantallas, voz, consultas, historial"],
       ["Motor", "data/search/{normalization, entities, engine, synonyms, knowledge}", "Búsqueda local por puntaje (sin IA). Pesos: título 12, descripción 8, palabra clave 6, sinónimo 4, actor 10, etc."],
       ["Bancos", "data/search/knowledge, data/search/codigo", "Carga de JSON de assets: situaciones, Código Electoral y Constitución"],
       ["Biblioteca", "data/BookInternalSearchEngine.kt, PdfRendererManager.kt", "Índice de artículos y renderizado de PDF por página"],
       ["Datos locales", "data/database (Room), AuthSessionManager, AppSettingsManager", "Historial de consultas, sesión y preferencias (SharedPreferences)"],
       ["Voz", "MainActivity.kt, data/ElectoralTtsManager.kt", "Reconocimiento de voz (SpeechRecognizer) y lectura en voz alta (TTS)"],
       ["Sin uso", "data/rag/* (4–6 archivos), data/api/Gemini*", "Restos de una versión anterior con IA en línea (ver sección 8)"]], [0.12, 0.40, 0.48])

# ---------------- 3 Tecnologías
h1("3. Lenguajes, tecnologías y versiones")
tabla([["Componente", "Tecnología / versión", "Uso"],
       ["Lenguaje principal", "Kotlin 2.2.10 (≈20.500 líneas, 53 archivos)", "Toda la app Android"],
       ["UI", "Jetpack Compose (BOM 2024.09.00) + Material 3", "Interfaz declarativa"],
       ["Build", "Gradle · Android Gradle Plugin 9.1.1 · KSP 2.3.5", "Compilación y generación de código"],
       ["Base de datos", "Room 2.7.0 (SQLite)", "Historial de consultas"],
       ["Red (sin uso)", "Retrofit 2.12.0 · OkHttp 4.10.0 · Moshi 1.15.2", "Cliente de Gemini; hoy no se llama"],
       ["Pruebas", "JUnit 4 · Robolectric 4.16.1 · Roborazzi 1.59.0", "Pruebas unitarias y de capturas"],
       ["Datos", "JSON (bancos, índice, sinónimos) y PDF (12 documentos, ≈12 MB)", "Conocimiento verificado"],
       ["Herramientas de datos", "Python 3 (pdftotext/poppler, reportlab)", "Generar y validar los bancos; generar PDF"],
       ["Prueba del motor", "Kotlin/JVM (harness propio) con org.json", "Ejecuta el motor real sin Android"],
       ["Versión web", "JavaScript ES modules (sin frameworks) · pdf.js 3.11.174 · Service Worker · Web Speech API", "Prueba en navegador y PWA"],
       ["Pruebas web", "Playwright (Chromium) · Node.js", "Recorrido automático de 25 pasos, 0 errores"]], [0.20, 0.45, 0.35])

# ---------------- 4 IA
h1("4. Herramientas de inteligencia artificial utilizadas")
tabla([["Herramienta", "Papel en el proyecto"],
       ["Google AI Studio", "Plataforma donde se creó y se mantiene el proyecto Android (genera el código Kotlin/Compose y compila). Recibe los «prompts» con los cambios. El proyecto declara la capacidad de Gemini del lado servidor en metadata.json."],
       ["Gemini (modelo de Google)", "Modelo detrás de AI Studio. El aviso legal de la app lo menciona como parte de las herramientas. La app instalada <b>no</b> consulta a Gemini: responde con su motor local."],
       ["ChatGPT (OpenAI)", "Usado según el aviso legal de la app y lo indicado por el responsable del proyecto, como apoyo de redacción/diseño. El repositorio no contiene código de ChatGPT."],
       ["Claude Code (Anthropic)", "Construyó y verificó el banco de conocimiento (154 situaciones con citas comprobadas contra los PDF), optimizó el motor (≈37× más rápido), arregló sinónimos y filtros, creó los bancos del Código Electoral y de la Constitución, corrigió el índice de la biblioteca, desarrolló la versión web, las pruebas, esta auditoría y los prompts para AI Studio."]], [0.24, 0.76])
p("<b>Flujo de trabajo:</b> Claude Code prepara cambios y los publica en GitHub; el responsable los lleva a AI Studio mediante un prompt o importando el repositorio; AI Studio compila la app. Por eso conviene que AI Studio y GitHub no diverjan (ver recomendaciones).")

# ---------------- 5 Estructura
h1("5. Estructura del repositorio (qué hay en cada carpeta)")
tabla([["Ruta", "Contenido", "Quién lo necesita"],
       ["app/", "Proyecto Android (módulo único)", "Android"],
       ["app/src/main/java/com/example/", "Código Kotlin: MainActivity, ui, viewmodel, data", "Android"],
       ["app/src/main/assets/json respuestas/", "banco_conocimientos.json (154 situaciones, v2.0)", "Android · Web · iPhone"],
       ["app/src/main/assets/codigo_electoral/", "banco_codigo_electoral.json (297 artículos)", "Android · Web · iPhone"],
       ["app/src/main/assets/constitucion/", "banco_constitucion.json (274 artículos)", "Android · Web · iPhone"],
       ["app/src/main/assets/sinonimos/", "sinonimos_globales.json (18 grupos: bolo = borracho = ebriedad…)", "Android · Web · iPhone"],
       ["app/src/main/assets/libros/", "12 PDF oficiales (≈12 MB)", "Todas las plataformas"],
       ["app/src/main/assets/libros_indice_completo.json", "Índice de artículos y texto por página para el buscador de la biblioteca", "Todas"],
       ["app/src/main/res/", "Íconos, tema, textos, reglas de backup", "Android"],
       ["app/src/test · androidTest", "Pruebas unitarias/instrumentadas (varias son plantilla)", "Android"],
       ["herramientas/banco_conocimientos/", "Generador y validador del banco, fichas fuente (k1…k8), PDF del banco, prueba_motor/ (JVM)", "Mantenimiento de datos"],
       ["herramientas/banco_codigo_electoral/ · banco_constitucion/", "Extractor, generador y validador de cada banco + baterías de preguntas", "Mantenimiento de datos"],
       ["herramientas/prompts_ai_studio/", "Prompts 1–4 y la imagen de ejemplos", "Operación con AI Studio"],
       ["herramientas/documentacion/", "Estos PDF y sus generadores", "Documentación"],
       ["web/", "Versión web (index.html, js/, css/, data/, libros/, vendor/pdfjs, tools/)", "Web"],
       ["public/assets/aistudio · metadata.json · .env.example", "Archivos del flujo de AI Studio", "AI Studio"],
       ["gradle/ · build.gradle.kts · settings.gradle.kts · gradle.properties", "Configuración de Gradle y catálogo de versiones (libs.versions.toml)", "Android"]], [0.30, 0.50, 0.20])

# ---------------- 6 Datos y pruebas
h1("6. Bancos de conocimiento y métricas de calidad")
tabla([["Banco", "Tamaño", "Verificación", "Resultado de pruebas (motor real en JVM)"],
       ["Situaciones (banco principal)", "154 situaciones · 13 categorías", "Cada artículo citado se comprobó en el PDF de su ley (validar_banco.py)", "Regresión 140/140; paráfrasis 37/37; 218 preguntas sin cambio tras optimizar"],
       ["Código Electoral", "297 artículos (3 derogados)", "Inicio y fin de cada texto hallados en la página indicada (validador)", "Prueba ciega 33/41 en 1.er lugar; 38/41 entre los 5 primeros"],
       ["Constitución", "274 artículos", "Mismo validador", "Prueba ciega 24/28 en 1.er lugar"],
       ["Sinónimos globales", "18 grupos de equivalencias", "Prueba de regresión del banco principal", "Jerga de ebriedad: 2/11 → 11/11 sin romper 140/140"],
       ["Índice de la biblioteca", "10 libros con artículos", "Páginas recalculadas leyendo cada PDF", "Corregidas 20 páginas del Código Electoral y errores en otras leyes"]], [0.20, 0.20, 0.30, 0.30])
p("Rendimiento del motor: primera consulta ≈150 ms, consultas calentadas ≈24 ms (antes ≈934 ms). La versión web replica el motor y se comparó contra la referencia en Kotlin: 2.174 consultas con resultados idénticos.")

# ---------------- 7 Ajustes
h1("7. Verificación de los ajustes realizados")
tabla([["#", "Ajuste", "Estado en el repositorio", "Falta"],
       ["1", "Banco de 154 situaciones con citas verificadas e IDs que el motor reconoce", "✔ Presente y válido", "—"],
       ["2", "Optimización del motor (normalizador sin regex repetidas, caché de sinónimos)", "✔ Presente", "—"],
       ["3", "Filtro de fútbol («partido») y fuera de tema", "✔ Presente", "—"],
       ["4", "Sinónimos globales (bolo/bola/borracho/tomado…)", "✔ Código y JSON en el repositorio, enlazado en la carga", "Confirmar que AI Studio aplicó el Prompt 1 en su proyecto"],
       ["5", "Banco Código Electoral + buscador (LawSearch)", "✔ Datos y clases presentes y probadas", "<b>La pantalla aún no lo llama</b>: falta el Prompt 2 en AI Studio"],
       ["6", "Banco Constitución + buscador", "✔ Datos y clases presentes y probadas", "<b>Falta conectar la pantalla</b> (Prompt 3)"],
       ["7", "Índice de la biblioteca corregido + detector de n.º de artículo", "✔ Presente", "Que AI Studio reemplace el JSON y la expresión regular"],
       ["8", "Texto invisible en el Test (tema oscuro)", "✔ Corregido en TestConocimientosScreen.kt", "Que AI Studio lo aplique (Prompt 4)"],
       ["9", "Versión web de prueba", "✔ Presente, 0 errores en 25 pasos", "—"],
       ["10", "Pruebas automáticas de los módulos nuevos dentro del proyecto Android", "✘ Solo existen como harness JVM externo", "Agregar pruebas JUnit en app/src/test"]], [0.055, 0.395, 0.29, 0.26])
p("Los «Faltantes» de los puntos 4–8 son pasos operativos: el código ya está en GitHub, pero la app que se compila vive en AI Studio y debe recibir los cambios.")

# ---------------- 8 Seguridad
h1("8. Auditoría de seguridad")
p("Metodología: revisión estática de manifiesto, Gradle, código y activos; búsqueda de secretos en el árbol y en el historial de Git (26 commits); revisión de dependencias; revisión del cliente web. <b>No se hizo</b> prueba dinámica en dispositivo ni análisis del APK compilado.")
def hall(i, sev, col, t, ev, imp, rec):
    cab = Table([[Paragraph("<font color='white'><b>%s</b></font>" % sev, CELL), Paragraph("<b>%s · %s</b>" % (i, t), CELL)]], colWidths=[18*mm, W - 18*mm])
    cab.setStyle(TableStyle([("BACKGROUND", (0,0), (0,0), col), ("BACKGROUND", (1,0), (1,0), CLARO), ("VALIGN", (0,0), (-1,-1), "MIDDLE")]))
    E.append(KeepTogether([cab, Paragraph("<b>Evidencia:</b> " + ev, CELL), Paragraph("<b>Riesgo:</b> " + imp, CELL), Paragraph("<b>Recomendación:</b> " + rec, CELL), Spacer(1, 7)]))
hall("H-1", "ALTA", ROJO, "El acceso (login) no protege nada",
     "AuthSessionManager.kt: <font name='DVM'>isValidGeneralAccount = digits.length == 9 &amp;&amp; password.length >= 6</font>: cualquier usuario de 9 dígitos con cualquier clave de 6 caracteres entra. Credenciales de prueba en el código (12345678-9 / abc123). «1 credencial = 1 dispositivo» se guarda solo en SharedPreferences del propio teléfono.",
     "Si se pretende limitar el acceso o cobrar, cualquiera puede entrar y compartir la app. Además se guarda el DUI en texto plano en el teléfono.",
     "Decidir: (a) quitar el login si no hay control real, o (b) validar contra un servicio (por ejemplo Firebase Authentication o un servidor propio con lista de usuarios autorizados y claves con hash). No guardar el DUI; eliminar la cuenta de prueba del código de producción.")
hall("H-2", "ALTA", ROJO, "APK de producción sin ofuscar y contenido copiable",
     "app/build.gradle.kts: <font name='DVM'>isMinifyEnabled = false</font> en release. Los bancos JSON y los PDF están en texto claro en assets (≈15 MB).",
     "El APK se descompila y se lee el código; el banco de conocimiento (el valor del proyecto) se copia con facilidad.",
     "Activar R8 (isMinifyEnabled = true, isShrinkResources = true) con reglas para Room/Moshi. Los activos no pueden protegerse del todo en el dispositivo: considerar cifrarlos y/o comprobar su huella SHA-256 al cargar; la protección real es legal (derechos de autor/licencia).")
hall("M-1", "MEDIA", NARANJA, "Respaldo automático activo con reglas de plantilla",
     "AndroidManifest.xml: <font name='DVM'>allowBackup=\"true\"</font>; backup_rules.xml y data_extraction_rules.xml son los de ejemplo sin personalizar.",
     "Sesión, DUI guardado e historial de consultas pueden extraerse por copia de seguridad (adb backup / respaldo en la nube).",
     "Poner allowBackup=\"false\" o excluir explícitamente las preferencias y la base de datos en ambos XML.")
hall("M-2", "MEDIA", NARANJA, "Capa de red hacia Gemini muerta pero con logging BODY y plugin de secretos",
     "data/api/GeminiApiService.kt: HttpLoggingInterceptor en nivel BODY. build.gradle.kts usa el plugin Secrets (GEMINI_API_KEY → BuildConfig). Ninguna pantalla llama a RetrofitClient.",
     "Hoy no hay fuga porque no se usa. Si se reactiva, la llave quedaría dentro del APK (extraíble) y el log mostraría peticiones completas.",
     "Eliminar data/api, data/rag sin uso, Retrofit/OkHttp/Moshi y el plugin de secretos. Si algún día se usa IA en línea: llamarla desde un servidor propio (nunca llave en el APK) y registrar sin cuerpo.")
hall("M-3", "MEDIA", NARANJA, "Permiso INTERNET declarado sin uso",
     "AndroidManifest.xml declara INTERNET; el código no hace llamadas de red. El requisito del proyecto es «0 internet».",
     "Superficie innecesaria y contradice el aviso «funciona sin internet». RECORD_AUDIO sí se usa (voz) y es correcto.",
     "Quitar INTERNET y probar que voz y TTS siguen funcionando (usan servicios del sistema con sus propios permisos).")
hall("M-4", "MEDIA", NARANJA, "El aviso legal no coincide con el comportamiento real",
     "LegalConsentManager.kt: el texto afirma que «algunas consultas pueden ser procesadas por servicios de IA» (Gemini, ChatGPT, AI Studio). El código actual responde solo en local.",
     "Información inexacta al usuario sobre tratamiento de sus datos; puede generar desconfianza o problemas con la política de la tienda.",
     "Redactar el aviso conforme a la realidad: las consultas se procesan en el dispositivo, no se envían a terceros; mencionar el historial local y el uso del micrófono. Alinear APP_VERSION («2.0.26») con versionName («1.1»).")
hall("M-5", "MEDIA", NARANJA, "Dependencias desactualizadas o sin uso",
     "OkHttp 4.10.0 (2022), Compose BOM 2024.09.00, firebase-bom incluido sin librerías de Firebase, varias librerías comentadas en el catálogo (cámara, ubicación, datastore, coil, navegación…).",
     "Riesgo de vulnerabilidades conocidas en componentes antiguos y APK más grande.",
     "Eliminar lo que no se usa y actualizar el resto. Activar Dependabot en GitHub y una verificación de vulnerabilidades (OWASP Dependency-Check) en CI.")
hall("B-1", "BAJA", AMARILLO, "Registros en producción", "16 usos de Log.* / printStackTrace en el código de la app.", "Pueden exponer rutas o contenido en logcat.", "Usar un wrapper que solo registre en debug.")
hall("B-2", "BAJA", AMARILLO, "Historial de consultas sin cifrar", "Room guarda cada pregunta del usuario en la base local sin cifrado.", "Las preguntas pueden ser sensibles; si el teléfono está comprometido o el respaldo está activo se leen.", "Permitir borrar historial (ya existe), excluirlo del backup (M-1) y, opcionalmente, cifrar con SQLCipher.")
hall("B-3", "BAJA", AMARILLO, "Versión web sin cabeceras de seguridad", "index.html no define Content-Security-Policy. El contenido dinámico sí se escapa (función esc()) y el service worker solo guarda recursos del mismo origen. La pantalla de acceso muestra el usuario de prueba.", "Sin CSP un fallo futuro de XSS tendría más impacto.", "Al publicar, usar hosting con cabeceras (CSP, X-Content-Type-Options, Referrer-Policy) y ocultar el usuario de prueba.")
hall("B-4", "BAJA", AMARILLO, "Firma de depuración fuera del repositorio", "debugConfig apunta a debug.keystore (ignorado por Git, contraseña estándar «android»). La firma de release usa variables de entorno (correcto: no hay secretos en el repo).", "Quien clone el proyecto no podrá compilar la variante debug sin crear la llave.", "Documentar cómo generar debug.keystore. Guardar la llave de subida (my-upload-key.jks) en un gestor seguro y con copia; perderla impide actualizar la app en Play Store.")
hall("B-5", "BAJA", AMARILLO, "Sin comprobación de integridad del dispositivo/APK", "No hay Play Integrity ni detección de root/emulador.", "Una copia modificada de la app (por ejemplo con respuestas alteradas) no se detectaría.", "Opcional según el riesgo: Play Integrity API y huellas SHA-256 de los bancos.")
h2("Lo que está bien")
bl(["<b>Sin secretos en el repositorio ni en su historial</b> (se buscaron llaves de API, claves privadas, .jks, .env, google-services): solo aparece la contraseña estándar de depuración.",
    "<b>Sin servidor propio</b>: no hay backend que atacar ni denegar. La disponibilidad no depende de terceros.",
    "Sin WebView, sin JavaScript interface, sin ContentProvider ni receptores exportados; una sola actividad exportada (el lanzador).",
    "Firma de release por variables de entorno (no hay contraseña de producción en el código).",
    "Versión web: todo texto variable se escapa antes de insertarse como HTML; 0 errores de consola/red en las pruebas.",
    "Aplicación declarada como <b>no oficial</b> del TSE y con consentimiento previo."])
h2("Sobre «que nos tiraran la aplicación» (disponibilidad)")
bl(["<b>En el teléfono</b>: nadie puede «tumbarla» a distancia porque no depende de un servidor. Sí puede dejar de estar disponible si Google Play la suspende: preparar política de privacidad, aclaración de «no oficial», fecha de datos y contacto, y evitar mensajes que parezcan información oficial.",
    "<b>En la web</b>: usar solo hosting estático con CDN (GitHub Pages, Cloudflare Pages, Netlify). Sin base de datos ni API, la superficie de ataque es mínima. Mantener el repositorio con autenticación en dos pasos y ramas protegidas.",
    "<b>Suplantación</b>: registrar el nombre de la app y del dominio; una copia falsa con respuestas alteradas es el riesgo de reputación más realista (de ahí H-2 y B-5)."])

# ---------------- 9 Higiene
h1("9. Estado del repositorio: lo que sobra y lo que falta")
h2("Sobra (candidato a eliminar tras confirmar)")
tabla([["Elemento", "Motivo", "Tamaño aprox."],
       ["ui/SimuladorScreen.kt", "Sin referencias; la app usa SimulacionScreen", "—"],
       ["data/rag/ElectoralRAGEngine, HybridRetriever, QueryAnalyzer, ResponseValidator", "Sin referencias (restos de la versión con IA en línea). ElectoralRAGIndexer sí lo usa la búsqueda de libros: conservar o migrar", "≈1.700 líneas"],
       ["data/api/GeminiApiService.kt, GeminiModels.kt + Retrofit/OkHttp/Moshi", "Sin uso (ver M-2)", "100 líneas + librerías"],
       ["app/src/test: ExampleUnitTest, ExampleRobolectricTest, GreetingScreenshotTest, screenshots/greeting.png", "Plantillas de AI Studio", "—"],
       ["Librerías comentadas en libs.versions.toml; firebase-bom", "Sin uso", "—"],
       ["web/libros/*.pdf y web/data/banco_conocimientos.json", "Copias de los PDF/bancos de la app (≈14 MB duplicados). Mantener solo si la web se publica por separado; si no, generarlos con un script", "≈14 MB"],
       ["herramientas/banco_conocimientos/Banco_de_Conocimientos…pdf", "Documento generado; puede regenerarse", "2,2 MB"]], [0.38, 0.47, 0.15])
h2("Falta")
tabla([["Elemento", "Por qué importa"],
       ["README.md en la raíz", "Hoy no hay: un ingeniero nuevo no sabe por dónde empezar"],
       ["LICENSE / aviso de derechos", "Define quién puede copiar el banco de conocimiento"],
       ["Política de privacidad", "Obligatoria en tiendas; debe coincidir con la realidad (M-4)"],
       ["Integración continua (.github/workflows)", "Compilar, correr pruebas y validadores de datos en cada cambio"],
       ["Pruebas JUnit de ElectoralConceptRegistry, LawSearch y los validadores", "Hoy solo existen como harness JVM fuera del proyecto"],
       ["Reglas R8 (proguard-rules.pro) reales", "Necesarias al activar la ofuscación"],
       ["Conexión de la pantalla con Código Electoral / Constitución", "Datos y motor listos; falta el uso desde el chat y la voz"],
       ["Sincronía AI Studio ↔ GitHub", "Si el proyecto de AI Studio no coincide con el repositorio, los arreglos pueden perderse al recompilar"]], [0.38, 0.62])

# ---------------- 10 Web
h1("10. Ruta para la versión web")
p("Ya existe una versión web funcional en <font name='DVM'>web/</font> (JavaScript puro, sin servidor). Para publicarla de forma profesional:")
bl(["<b>Qué se reutiliza tal cual:</b> los JSON de assets (bancos, índice, sinónimos) y los PDF. Es el mismo formato que usa Android.",
    "<b>Qué replica la web:</b> normalizador, extractor, puntaje y búsqueda (web/js/engine.js), comparados contra Kotlin con 2.174 consultas idénticas. <b>Pendiente de portar</b>: sinónimos globales (ElectoralConceptRegistry) y la búsqueda de Código Electoral / Constitución.",
    "<b>Pasos:</b> (1) copiar los JSON nuevos a web/data; (2) portar ConceptRegistry y LawSearch a JavaScript; (3) correr tools/comparar_con_kotlin.mjs y tools/prueba_navegador.cjs; (4) publicar la carpeta web/ en hosting estático (GitHub Pages, Cloudflare Pages o Netlify); (5) agregar cabeceras de seguridad (CSP) y dominio propio con HTTPS.",
    "<b>Datos del navegador:</b> el historial vive en localStorage del usuario; no hay cuentas ni servidor. Si se necesita login real en la web, requiere un servicio de autenticación externo.",
    "<b>Voz:</b> Web Speech API funciona en Chrome/Edge/Safari recientes; en Firefox no hay reconocimiento de voz."])
code("web/index.html        punto de entrada (módulo ES)\nweb/js/app.js         rutas (#chat, #biblioteca, …), tema, menú\nweb/js/engine.js      motor de búsqueda (réplica de Kotlin)\nweb/js/pdfreader.js   lector de PDF con pdf.js\nweb/data/*.json       bancos · web/libros/*.pdf documentos\nweb/sw.js             service worker (uso sin conexión)\nweb/tools/            comparación con Kotlin y pruebas Playwright")

# ---------------- 11 iPhone
h1("11. Ruta para iPhone")
p("Android (Kotlin/Compose) no se instala en iPhone. Hay tres caminos; la <b>recomendación</b> es el B para tener app real en la App Store con el menor riesgo, y el A como paso intermedio gratuito.")
tabla([["Opción", "Qué es", "Ventajas", "Desventajas", "Esfuerzo"],
       ["A. Web instalable (PWA)", "La versión web «agregada a la pantalla de inicio» desde Safari", "Ya existe; sin cuenta de Apple; sin tienda", "Sin presencia en App Store; límites de Safari (voz, almacenamiento)", "Bajo"],
       ["B. App nativa SwiftUI", "Reescribir las pantallas en Swift y reutilizar los JSON y PDF; portar el motor (≈2.000 líneas Kotlin) a Swift", "Experiencia nativa; publicable en App Store; voz con SFSpeechRecognizer", "Requiere Mac, Xcode y desarrollo nuevo; dos bases de código", "Medio-alto"],
       ["C. Multiplataforma (Kotlin Multiplatform + Compose Multiplatform)", "Compartir motor y pantallas Kotlin entre Android e iOS", "Un solo código", "Migrar el proyecto de AI Studio; curva técnica alta; AI Studio no lo genera", "Alto"],
       ["D. Envoltorio (Capacitor) de la web", "Empaquetar la web como app iOS", "Reutiliza la web; publicable", "Apple puede rechazar apps que son solo una web; rendimiento medio", "Medio"]], [0.17, 0.25, 0.20, 0.25, 0.13])
h2("Qué necesita el ingeniero para la opción B (SwiftUI)")
bl(["<b>Cuenta Apple Developer</b> (de pago anual) y un <b>Mac con Xcode</b> actualizado. Sin Mac no se puede compilar ni subir a la App Store.",
    "<b>Datos portables sin cambios:</b> los 4 JSON (banco_conocimientos, banco_codigo_electoral, banco_constitucion, sinonimos_globales), libros_indice_completo.json y los 12 PDF de assets/libros.",
    "<b>Lógica a portar a Swift</b> (carpeta app/src/main/java/com/example/data/search): ElectoralNormalizer, ElectoralEntityExtractor, ElectoralScoringEngine, ElectoralSearchEngine, ElectoralSynonymRegistry, ElectoralConceptRegistry, LawBank/LawSearch. Referencia ejecutable: web/js/engine.js y las baterías de preguntas en herramientas/ para comprobar que Swift da los mismos resultados.",
    "<b>Equivalencias Android → iOS:</b> Compose → SwiftUI · Room → SwiftData o Core Data · SharedPreferences → UserDefaults/Keychain · PdfRenderer → PDFKit · SpeechRecognizer → SFSpeechRecognizer · TextToSpeech → AVSpeechSynthesizer · assets → Bundle · ViewModel → ObservableObject.",
    "<b>Permisos iOS (Info.plist):</b> NSMicrophoneUsageDescription y NSSpeechRecognitionUsageDescription.",
    "<b>Publicación:</b> íconos y capturas en los tamaños exigidos, política de privacidad, aclaración de que no es oficial, clasificación por edades y revisión de Apple (las apps de contenido electoral/cívico se revisan con más cuidado).",
    "<b>Pruebas:</b> usar las baterías de preguntas (herramientas/…/preguntas) como criterio de aceptación: el motor en Swift debe igualar los resultados del de Kotlin."])
code("PASOS RESUMIDOS (opción B)\n1. Crear proyecto SwiftUI en Xcode · 2. Copiar assets (JSON + PDF) al Bundle\n3. Portar motor de búsqueda a Swift y validarlo con las baterías\n4. Pantallas: Chat, Biblioteca (PDFKit), Test, Casos, Paso a paso, Ajustes\n5. Voz y lectura · 6. Historial local · 7. TestFlight · 8. App Store")

# ---------------- 12 Plan
h1("12. Plan de acción recomendado (por prioridad)")
tabla([["Cuándo", "Acción"],
       ["Antes de publicar", "H-1 decidir/arreglar el login · H-2 activar R8 · M-1 allowBackup=false · M-3 quitar INTERNET · M-4 corregir aviso legal y política de privacidad"],
       ["Esta semana", "Aplicar en AI Studio los Prompts 1–4 y confirmar que su proyecto coincide con GitHub · conectar Código Electoral y Constitución en el chat"],
       ["Este mes", "Limpiar código sin uso y dependencias (M-2, M-5) · README y LICENSE · pruebas JUnit de módulos nuevos · integración continua"],
       ["Siguiente etapa", "Portar sinónimos y búsqueda de leyes a la web · publicar web estática · decidir ruta iPhone (A ahora, B después)"]], [0.18, 0.82])
p("Documento generado a partir de la revisión del repositorio. Los hallazgos son de revisión estática y deben confirmarse con el APK compilado antes de la publicación.")
doc.build(E, onFirstPage=pie("Asistente Electoral SV — Documentación técnica y auditoría"), onLaterPages=pie("Asistente Electoral SV — Documentación técnica y auditoría"))
print("ok", salida)
