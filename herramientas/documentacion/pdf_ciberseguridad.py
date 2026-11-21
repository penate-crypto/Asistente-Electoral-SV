#!/usr/bin/env python3
"""Informe de ciberseguridad de Asistente Electoral SV. Uso: python3 pdf_ciberseguridad.py <salida.pdf>"""
import sys
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, PageBreak, KeepTogether
from reportlab.lib.units import mm
from comun import *

salida = sys.argv[1]
doc = SimpleDocTemplate(salida, pagesize=A4, leftMargin=18*mm, rightMargin=18*mm, topMargin=18*mm, bottomMargin=20*mm,
                        title="Asistente Electoral SV — Informe de ciberseguridad", author="Proyecto Asistente Electoral SV")
W = A4[0] - 36*mm; E = []
def h1(t): E.append(Paragraph(t, H1))
def h2(t): E.append(Paragraph(t, H2))
def p(t): E.append(Paragraph(t, P))
def bl(items): [E.append(Paragraph(x, BUL, bulletText="•")) for x in items]
def code(t): E.append(Paragraph(t.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/>").replace(" ", "&nbsp;"), CODE))
def tabla(filas, anchos, cab=True):
    data = [[Paragraph(str(c), CELLB if (cab and i == 0) else CELL) for c in f] for i, f in enumerate(filas)]
    t = Table(data, colWidths=[a * W for a in anchos], repeatRows=1 if cab else 0)
    st = [("VALIGN", (0,0), (-1,-1), "TOP"), ("GRID", (0,0), (-1,-1), 0.3, colors.HexColor("#C9D3D8")), ("TOPPADDING", (0,0), (-1,-1), 3), ("BOTTOMPADDING", (0,0), (-1,-1), 3),
          ("ROWBACKGROUNDS", (0,1), (-1,-1), [colors.white, colors.HexColor("#F8FBFC")])]
    if cab: st.append(("BACKGROUND", (0,0), (-1,0), CLARO))
    t.setStyle(TableStyle(st)); E.append(t); E.append(Spacer(1, 6))
def nota(t, col=TEAL):
    tb = Table([[Paragraph(t, CELL)]], colWidths=[W]); tb.setStyle(TableStyle([("BACKGROUND", (0,0), (-1,-1), CLARO), ("LINEBEFORE", (0,0), (0,-1), 3, col), ("LEFTPADDING", (0,0), (-1,-1), 8), ("TOPPADDING", (0,0), (-1,-1), 5), ("BOTTOMPADDING", (0,0), (-1,-1), 5)])); E.append(tb); E.append(Spacer(1, 6))
def hall(i, sev, col, t, owasp, ev, imp, rec):
    cab = Table([[Paragraph("<font color='white'><b>%s</b></font>" % sev, CELL), Paragraph("<b>%s · %s</b>" % (i, t), CELL), Paragraph("<font color='#5B6573'>%s</font>" % owasp, SM)]], colWidths=[18*mm, W - 18*mm - 38*mm, 38*mm])
    cab.setStyle(TableStyle([("BACKGROUND", (0,0), (0,0), col), ("BACKGROUND", (1,0), (2,0), CLARO), ("VALIGN", (0,0), (-1,-1), "MIDDLE")]))
    E.append(KeepTogether([cab, Paragraph("<b>Evidencia:</b> " + ev, CELL), Paragraph("<b>Riesgo:</b> " + imp, CELL), Paragraph("<b>Solución:</b> " + rec, CELL), Spacer(1, 7)]))

# ---------- Portada
E += [Spacer(1, 32*mm), Paragraph("Asistente Electoral SV", ParagraphStyle("t", fontName="DVB", fontSize=28, leading=32, textColor=NAVY)),
      Paragraph("Informe de ciberseguridad", ParagraphStyle("s", fontName="DV", fontSize=16, leading=20, textColor=TEAL, spaceAfter=14)),
      Paragraph("Información completa de la aplicación, su tecnología, las herramientas con que se construyó y todas las vulnerabilidades y riesgos encontrados, con su solución.", P), Spacer(1, 8)]
tabla([["Aplicación", "Asistente Electoral SV — consulta y capacitación para el cuerpo electoral de El Salvador (herramienta independiente, no oficial del TSE)"],
       ["Identificador", "com.aistudio.asistenteelectoralsv.xkzqm · versión 1.1 (versionCode 2) · minSdk 24 · targetSdk 35"],
       ["Repositorio", "penate-crypto/Asistente-Electoral-SV (GitHub), rama claude/electoral-knowledge-base-json-ha2mpw"],
       ["Alcance", "Código fuente Android (Kotlin), manifiesto y Gradle, activos JSON/PDF, versión web, herramientas, historial de Git (26 commits)"],
       ["Tipo de revisión", "Estática (código y configuración). <b>No incluye</b> prueba dinámica en dispositivo ni análisis del APK compilado: debe hacerse antes de publicar"],
       ["Referencias", "OWASP Mobile Top 10 (2024) y OWASP MASVS como guía de clasificación"]], [0.2, 0.8], cab=False)
E.append(PageBreak())

# ---------- 1 Resumen
h1("1. Resumen ejecutivo")
p("Asistente Electoral SV es una aplicación <b>100 % local</b>: responde con un motor de búsqueda propio sobre un banco de conocimiento incluido en el APK, sin servidor, sin internet y sin consumir tokens de inteligencia artificial. Esto elimina las amenazas más graves de una app con backend (inyección en API, robo de base de datos, caída del servidor) y es su mayor fortaleza de seguridad.")
p("Sus riesgos están en el <b>acceso</b>, en la <b>protección del contenido y del código</b>, en <b>configuraciones heredadas</b> y en la <b>coherencia legal</b>. Se encontraron <b>2 hallazgos de prioridad alta, 6 medios y 5 bajos</b>. No se encontraron secretos expuestos.")
tabla([["Prioridad", "Hallazgos"],
       ["<font color='#C62828'><b>ALTA (2)</b></font>", "H-1 El login no valida nada · H-2 APK sin ofuscar y contenido copiable"],
       ["<font color='#E65100'><b>MEDIA (6)</b></font>", "M-1 Respaldo automático activo · M-2 Capa de red/Gemini muerta con logging BODY · M-3 Permiso INTERNET sin uso · M-4 Textos legales que afirman IA en línea · M-5 Dependencias antiguas y sin uso · M-6 Respuestas electorales no pertinentes ante preguntas ajenas"],
       ["<font color='#B8860B'><b>BAJA (5)</b></font>", "B-1 Logs en producción · B-2 Historial sin cifrar · B-3 Web sin cabeceras de seguridad · B-4 Llave de depuración fuera del repositorio · B-5 Sin comprobación de integridad"]], [0.18, 0.82])
nota("<b>Conclusión:</b> la app es segura frente a ataques remotos por no tener servidor. Antes de publicar conviene resolver H-1 y H-2 y las configuraciones M-1, M-3 y M-4. Con eso el riesgo residual es bajo.", VERDE)

# ---------- 2 Tecnología
h1("2. Lenguajes, tecnologías y herramientas")
h2("Lenguajes")
tabla([["Lenguaje", "Dónde se usa", "Volumen"],
       ["<b>Kotlin</b> (2.2.10)", "Toda la aplicación Android: interfaz, motor de búsqueda, datos", "53 archivos · ≈20.500 líneas"],
       ["<b>JSON</b>", "Bancos de conocimiento, sinónimos, índice de la biblioteca (datos de la app)", "≈4 MB de datos"],
       ["<b>Python 3</b>", "Herramientas que generan y validan los bancos y los PDF", "≈20 scripts"],
       ["<b>JavaScript</b> (ES modules)", "Versión web de prueba y sus pruebas automáticas (Node.js/Playwright)", "≈2.350 líneas"],
       ["<b>Kotlin/JVM</b>", "Banco de pruebas que ejecuta el motor real sin Android", "—"],
       ["XML y Gradle Kotlin DSL", "Manifiesto, recursos, compilación", "—"]], [0.24, 0.50, 0.26])
h2("Plataforma Android")
tabla([["Componente", "Versión / detalle"],
       ["Interfaz", "Jetpack Compose (BOM 2024.09.00) + Material 3"],
       ["Compilación", "Gradle · Android Gradle Plugin 9.1.1 · KSP 2.3.5"],
       ["Base de datos local", "Room 2.7.0 (SQLite) — historial de consultas"],
       ["Preferencias", "SharedPreferences — sesión, DUI recordado, aceptación legal"],
       ["Voz", "SpeechRecognizer y TextToSpeech del sistema (permiso de micrófono)"],
       ["PDF", "PdfRenderer del sistema + índice propio de artículos"],
       ["Red (sin uso)", "Retrofit 2.12.0 · OkHttp 4.10.0 · Moshi 1.15.2 (cliente de Gemini sin llamadas)"],
       ["Pruebas", "JUnit 4 · Robolectric 4.16.1 · Roborazzi 1.59.0"],
       ["Web", "pdf.js 3.11.174 · Service Worker · Web Speech API"]], [0.28, 0.72])
h2("Herramientas de inteligencia artificial utilizadas")
tabla([["Herramienta", "Qué hizo en el proyecto", "Implicación de seguridad"],
       ["<b>Google AI Studio</b>", "Plataforma donde se creó y se mantiene la app Android: genera el código Kotlin/Compose y compila. Recibe los cambios mediante prompts.", "El código lo escribe una IA: requiere revisión humana. Si AI Studio y GitHub se desincronizan, los arreglos pueden perderse."],
       ["<b>Gemini</b> (Google)", "Modelo de IA detrás de AI Studio. La app tiene el cliente preparado, pero no lo llama: responde en local.", "Si se activa en el futuro, la llave de API no debe ir dentro del APK (ver M-2)."],
       ["<b>ChatGPT</b> (OpenAI)", "Apoyo de redacción y diseño, según los textos legales de la app y lo indicado por el responsable. No hay código de ChatGPT en el repositorio.", "Evitar pegar datos personales (DUI, claves) en conversaciones con IA externas."],
       ["<b>Claude Code</b> (Anthropic)", "Construyó y verificó el banco (154 situaciones con citas comprobadas), optimizó y probó el motor, creó los bancos del Código Electoral y la Constitución, los sinónimos, la versión web, las pruebas, esta auditoría y los prompts para AI Studio.", "Trabaja sobre el repositorio de GitHub (rama dedicada, sin pull request). Su código también debe pasar revisión humana y pruebas en CI."]], [0.17, 0.46, 0.37])

# ---------- 3 Superficie de ataque
h1("3. Superficie de ataque y modelo de amenazas")
tabla([["Superficie", "Estado", "Observación"],
       ["Red / servidor propio", "<font color='#2E7D32'><b>No existe</b></font>", "Sin backend, sin API propia, sin base de datos remota. No hay nada que «tumbar»."],
       ["Permisos", "INTERNET (sin uso) · RECORD_AUDIO (voz)", "El de micrófono se justifica; INTERNET no (M-3)."],
       ["Componentes exportados", "1 (la actividad principal)", "Sin servicios, receptores ni proveedores de contenido; sin WebView."],
       ["Almacenamiento local", "SharedPreferences y Room en texto claro", "Sesión, DUI recordado e historial (M-1, B-2)."],
       ["Contenido en el APK", "Bancos JSON y 12 PDF (≈15 MB) en texto claro", "Copiables (H-2)."],
       ["Autenticación", "Local, sin validación real", "H-1."],
       ["Cadena de suministro", "AI Studio, Gradle, librerías, scripts Python", "M-5 y proceso de cambios."],
       ["Versión web", "Archivos estáticos + service worker", "Sin CSP (B-3). Contenido escapado."]], [0.25, 0.30, 0.45])
h2("Escenarios de amenaza considerados")
tabla([["Quién / qué", "Qué podría hacer", "¿Posible hoy?", "Control"],
       ["Copista", "Descargar el APK, extraer el banco y los PDF, publicarlos o usarlos en otra app", "<b>Sí</b>", "H-2: ofuscación, cifrado de assets, licencia"],
       ["Usuario no autorizado", "Entrar con cualquier DUI de 9 dígitos y compartir la app", "<b>Sí</b>", "H-1: autenticación real"],
       ["App falsa", "Publicar una copia con respuestas alteradas", "Posible", "B-5: integridad, registro de marca, canal oficial"],
       ["Robo o pérdida del teléfono", "Leer el DUI recordado y el historial de consultas", "Posible (sin bloqueo de pantalla)", "M-1, B-2: sin respaldo, borrado, cifrado"],
       ["Atacante remoto", "Explotar servidor, API o base de datos", "<font color='#2E7D32'><b>No</b></font> (no existen)", "Arquitectura 100 % local"],
       ["Denegación de servicio", "Dejar la app sin funcionar a distancia", "<font color='#2E7D32'><b>No</b></font>", "Sin dependencia de servidores. Riesgo: suspensión en la tienda (aviso legal, M-4)"],
       ["Error de contenido", "Respuesta electoral equivocada a una pregunta ajena", "<b>Sí</b>", "M-6: umbral de relevancia"]], [0.17, 0.38, 0.17, 0.28])
E.append(PageBreak())

# ---------- 4 Hallazgos
h1("4. Vulnerabilidades y riesgos encontrados")
hall("H-1", "ALTA", ROJO, "El login no valida nada y guarda el DUI en claro", "OWASP M3 · M1",
     "AuthSessionManager.kt línea 136: <font name='DVM'>isValidGeneralAccount = digits.length == 9 &amp;&amp; passwordInput.length >= 6</font> → cualquier usuario de 9 dígitos con cualquier clave de 6+ caracteres entra. Línea 22: cuenta de prueba <font name='DVM'>abc123</font> escrita en el código. Línea 152: «token» de sesión predecible (<font name='DVM'>sv_session_DUI_hora</font>). «1 credencial = 1 dispositivo» solo se guarda en SharedPreferences del propio teléfono. Línea 157: el DUI se guarda en texto plano.",
     "No hay control de acceso real: cualquiera entra y puede compartir la app; el DUI recordado queda legible en el teléfono.",
     "Decidir: (a) quitar el login si no hay control real, o (b) autenticar contra un servicio (Firebase Authentication o servidor propio con lista de usuarios autorizados y contraseñas con hash). Eliminar la cuenta de prueba, generar el token en el servidor, no guardar el DUI (o guardarlo cifrado con Android Keystore).")
hall("H-2", "ALTA", ROJO, "APK de producción sin ofuscar y contenido copiable", "OWASP M7",
     "app/build.gradle.kts línea 42: <font name='DVM'>isMinifyEnabled = false</font> en release. Bancos y PDF en assets sin cifrar.",
     "El APK se descompila y se lee el código y la lógica; el banco de conocimiento (el valor del proyecto) se copia con facilidad.",
     "Activar R8: <font name='DVM'>isMinifyEnabled = true</font> y <font name='DVM'>isShrinkResources = true</font>, con reglas para Room y Moshi. Cifrar los assets y verificar su huella SHA-256 al cargar. La protección definitiva es legal: licencia y derechos de autor.")
hall("M-1", "MEDIA", NARANJA, "Respaldo automático activo con reglas de plantilla", "OWASP M9 · M8",
     "AndroidManifest.xml línea 21: <font name='DVM'>allowBackup=\"true\"</font>; backup_rules.xml y data_extraction_rules.xml son los de ejemplo, sin personalizar.",
     "Sesión, DUI recordado e historial pueden extraerse por copia de seguridad (adb o nube).",
     "<font name='DVM'>android:allowBackup=\"false\"</font> y excluir en ambos XML las preferencias y la base de datos.")
hall("M-2", "MEDIA", NARANJA, "Capa de red hacia Gemini muerta, con logging BODY y llave embebida en build", "OWASP M1 · M5 (latente)",
     "data/api/GeminiApiService.kt línea 29: <font name='DVM'>HttpLoggingInterceptor.Level.BODY</font>. build.gradle.kts usa el plugin Secrets (GEMINI_API_KEY → BuildConfig). Ninguna pantalla llama a RetrofitClient.",
     "Hoy no hay fuga porque no se usa. Si se reactiva, la llave quedaría dentro del APK (extraíble) y el log mostraría peticiones completas.",
     "Eliminar data/api, el código RAG sin uso, Retrofit/OkHttp/Moshi y el plugin de secretos. Si algún día se usa IA en línea, llamarla desde un servidor propio y nunca con la llave en el APK.")
hall("M-3", "MEDIA", NARANJA, "Permiso INTERNET declarado sin uso", "OWASP M8",
     "AndroidManifest.xml línea 5. El código no hace llamadas de red; el requisito del proyecto es «0 internet».",
     "Superficie innecesaria; contradice lo que la app promete.",
     "Quitar el permiso y probar que voz y lectura siguen funcionando (usan servicios del sistema).")
hall("M-4", "MEDIA", NARANJA, "Los textos legales afirman un procesamiento que la app no hace", "OWASP M6",
     "LegalConsentManager.kt: línea 40 («algunas consultas pueden ser procesadas por servicios de IA»), línea 59 («integra modelos… Google Gemini… para procesar consultas») y línea 102 («el texto de la pregunta es procesado a través de las APIs de Google Gemini»). El código actual responde solo en local. Además APP_VERSION = «2.0.26» frente a versionName «1.1».",
     "Información inexacta al usuario sobre el tratamiento de sus datos: riesgo de reclamo, de desconfianza y de rechazo por la tienda.",
     "Reescribir términos y política de privacidad: las consultas se procesan en el dispositivo, no se envían a terceros; indicar el historial local, el micrófono y cómo borrarlo. Alinear los números de versión.")
hall("M-5", "MEDIA", NARANJA, "Dependencias antiguas y sin uso", "OWASP M2",
     "OkHttp 4.10.0 (2022), Compose BOM 2024.09.00, firebase-bom incluido sin librerías de Firebase y varias librerías comentadas en el catálogo (cámara, ubicación, datastore, coil, navegación).",
     "Componentes antiguos pueden tener vulnerabilidades conocidas; APK más grande.",
     "Eliminar lo que no se usa y actualizar el resto. Activar Dependabot y OWASP Dependency-Check en integración continua.")
hall("M-6", "MEDIA", NARANJA, "Respuestas electorales no pertinentes ante preguntas ajenas", "Integridad / confiabilidad",
     "El rechazo de temas ajenos usa una lista de unas 18 palabras. En una batería de 20 preguntas ajenas con el motor real, 15 recibieron una respuesta electoral (ej. «¿capital de Francia?» → mujer que cambió su apellido; «quiero comprar un celular» → celulares en la votación, confianza alta).",
     "En una herramienta de apoyo jurídico, mostrar una respuesta equivocada con apariencia oficial erosiona la confianza y puede inducir a error. No afecta a las preguntas electorales.",
     "Agregar un filtro de relevancia: medir cuántas palabras de la pregunta existen en el banco y rechazar cuando casi ninguna existe; ampliar la lista de temas ajenos; probar contra las baterías para no rechazar preguntas válidas.")
hall("B-1", "BAJA", AMARILLO, "Registros en producción", "OWASP M9",
     "15 usos de Log.* y printStackTrace en la app (por ejemplo MainActivity, BookInternalSearchEngine).", "Pueden exponer rutas o contenido en logcat.", "Registrar solo en builds de depuración.")
hall("B-2", "BAJA", AMARILLO, "Historial de consultas sin cifrar", "OWASP M9",
     "Room guarda cada pregunta del usuario en SQLite sin cifrado.", "Las preguntas pueden ser sensibles; se leen si el teléfono está comprometido o el respaldo está activo.", "Mantener el borrado de historial, excluirlo del respaldo (M-1) y, si se requiere, cifrar con SQLCipher.")
hall("B-3", "BAJA", AMARILLO, "Versión web sin cabeceras de seguridad", "OWASP Web",
     "index.html no define Content-Security-Policy. Todo texto variable se escapa (función esc()) y el service worker solo guarda recursos del mismo origen. La pantalla de acceso muestra el usuario de prueba.", "Sin CSP, un fallo futuro de XSS tendría más impacto.", "Hosting estático con cabeceras CSP, X-Content-Type-Options y Referrer-Policy; ocultar el usuario de prueba.")
hall("B-4", "BAJA", AMARILLO, "Llave de depuración fuera del repositorio", "Proceso",
     "debugConfig usa debug.keystore (ignorado por Git; clave estándar «android»). La firma de release usa variables de entorno: correcto.", "Quien clone el proyecto no puede compilar la variante debug sin crear la llave. Perder la llave de subida impide actualizar la app.", "Documentar cómo generarla. Guardar my-upload-key.jks y sus claves en un gestor seguro con copia de respaldo.")
hall("B-5", "BAJA", AMARILLO, "Sin comprobación de integridad del dispositivo ni del APK", "OWASP M7",
     "No hay Play Integrity ni detección de root o de APK modificado.", "Una copia alterada de la app (respuestas cambiadas) no se detectaría.", "Opcional según el riesgo: Play Integrity API y huellas SHA-256 de los bancos.")

# ---------- 5 Controles positivos
h1("5. Controles que están bien")
bl(["<b>Sin secretos</b> en el árbol ni en el historial de Git (se buscaron llaves de API, claves privadas, .jks, .env, google-services). Solo aparece la contraseña estándar de depuración.",
    "<b>Sin servidor ni API propia:</b> no hay inyección, robo de base de datos ni caída remota posibles.",
    "<b>Sin WebView</b>, sin interfaz JavaScript, sin proveedores de contenido ni receptores; una sola actividad exportada.",
    "<b>Firma de release por variables de entorno</b>: no hay contraseña de producción en el código.",
    "<b>Respuestas verificadas:</b> cada cita legal del banco se comprobó contra el PDF oficial con un validador automático.",
    "<b>Texto escapado en la web</b> y 0 errores de consola/red en 25 pasos de prueba.",
    "<b>Aviso de «no oficial»</b> y consentimiento previo antes de usar la app."])

# ---------- 6 Soluciones
h1("6. Soluciones listas para aplicar")
h2("Manifiesto (M-1, M-3)")
code('<!-- quitar -->\n<uses-permission android:name="android.permission.INTERNET" />\n\n<application\n    android:allowBackup="false"\n    android:fullBackupContent="false" ... >')
h2("Compilación de producción (H-2)")
code('buildTypes {\n  release {\n    isMinifyEnabled = true\n    isShrinkResources = true\n    proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")\n  }\n}')
h2("Reglas de respaldo (M-1)")
code('<data-extraction-rules>\n  <cloud-backup><exclude domain="sharedpref" path="."/>\n                <exclude domain="database" path="."/></cloud-backup>\n  <device-transfer><exclude domain="sharedpref" path="."/>\n                   <exclude domain="database" path="."/></device-transfer>\n</data-extraction-rules>')
h2("Login (H-1)")
p("No puede resolverse solo con código en el teléfono: requiere una decisión de producto. Opción A: eliminar el login. Opción B: autenticación con Firebase Authentication o un servidor propio, con lista de usuarios autorizados, contraseñas con hash y token emitido por el servidor. En ambos casos, borrar <font name='DVM'>TEST_USER_*</font> y no guardar el DUI.")

# ---------- 7 Plan
h1("7. Plan de remediación")
tabla([["Orden", "Acción", "Hallazgo", "Esfuerzo"],
       ["1", "Decidir y corregir el acceso (quitar login o autenticación real)", "H-1", "Medio"],
       ["2", "Activar R8 y reglas; probar el APK de release", "H-2", "Bajo"],
       ["3", "allowBackup=false y reglas de respaldo", "M-1", "Bajo"],
       ["4", "Quitar el permiso INTERNET y comprobar voz", "M-3", "Bajo"],
       ["5", "Reescribir términos y política de privacidad según la realidad", "M-4", "Bajo"],
       ["6", "Filtro de relevancia para preguntas ajenas, probado contra las baterías", "M-6", "Medio"],
       ["7", "Eliminar código y librerías sin uso; actualizar dependencias", "M-2, M-5", "Medio"],
       ["8", "Logs solo en debug; cifrado opcional del historial", "B-1, B-2", "Bajo"],
       ["9", "CSP y cabeceras al publicar la web", "B-3", "Bajo"],
       ["10", "Documentar llaves; integridad opcional; integración continua con pruebas y análisis de dependencias", "B-4, B-5", "Medio"]], [0.09, 0.57, 0.16, 0.18])
h2("Verificación posterior (lista de comprobación)")
bl(["Compilar el APK de release y descompilarlo (jadx): los nombres deben estar ofuscados.",
    "Intentar entrar con un DUI y clave inventados: debe rechazarse (o no existir el login).",
    "Ejecutar <font name='DVM'>adb backup</font>: no debe extraer datos de la app.",
    "Revisar el manifiesto final: sin INTERNET; micrófono solo si se usa la voz.",
    "Pasar las baterías de preguntas (herramientas/…/preguntas) y el filtro de preguntas ajenas.",
    "Ejecutar un análisis de dependencias y una prueba MASVS básica en un dispositivo real."])
nota("<b>Limitación de esta revisión:</b> es estática. No se compiló el APK ni se probó en un teléfono. Los hallazgos deben confirmarse con el APK real antes de la publicación.", GRIS)
doc.build(E, onFirstPage=pie("Asistente Electoral SV — Informe de ciberseguridad"), onLaterPages=pie("Asistente Electoral SV — Informe de ciberseguridad"))
print("ok", salida)
