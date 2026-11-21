#!/usr/bin/env python3
"""PDF 'Funcionamiento de la aplicación': objetivo, público, mapa conceptual, bancos, agente interno, ejemplos y capturas.
Uso: python3 pdf_funcionamiento.py <capturas_recorrido> <capturas_ejemplos> <salida.pdf>"""
import sys, os
from PIL import Image
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, PageBreak, Image as RLImage, KeepTogether
from reportlab.graphics.shapes import Drawing, Rect, String, Line, Polygon
from reportlab.lib.units import mm
from comun import *

cap, ej, salida = sys.argv[1:4]
doc = SimpleDocTemplate(salida, pagesize=A4, leftMargin=18*mm, rightMargin=18*mm, topMargin=18*mm, bottomMargin=20*mm,
                        title="Asistente Electoral SV — Funcionamiento de la aplicación", author="Proyecto Asistente Electoral SV")
W = A4[0] - 36*mm; E = []
def h1(t): E.append(Paragraph(t, H1))
def h2(t): E.append(Paragraph(t, H2))
def p(t): E.append(Paragraph(t, P))
def bl(items): [E.append(Paragraph(x, BUL, bulletText="•")) for x in items]
def tabla(filas, anchos, cab=True):
    data = [[Paragraph(str(c), CELLB if (cab and i == 0) else CELL) for c in f] for i, f in enumerate(filas)]
    t = Table(data, colWidths=[a * W for a in anchos], repeatRows=1 if cab else 0)
    st = [("VALIGN", (0,0), (-1,-1), "TOP"), ("GRID", (0,0), (-1,-1), 0.3, colors.HexColor("#C9D3D8")), ("TOPPADDING", (0,0), (-1,-1), 3), ("BOTTOMPADDING", (0,0), (-1,-1), 3),
          ("ROWBACKGROUNDS", (0,1), (-1,-1), [colors.white, colors.HexColor("#F8FBFC")])]
    if cab: st.append(("BACKGROUND", (0,0), (-1,0), CLARO))
    t.setStyle(TableStyle(st)); E.append(t); E.append(Spacer(1, 6))
def nota(t, col=TEAL):
    tb = Table([[Paragraph(t, CELL)]], colWidths=[W]); tb.setStyle(TableStyle([("BACKGROUND", (0,0), (-1,-1), CLARO), ("LINEBEFORE", (0,0), (0,-1), 3, col), ("LEFTPADDING", (0,0), (-1,-1), 8), ("TOPPADDING", (0,0), (-1,-1), 5), ("BOTTOMPADDING", (0,0), (-1,-1), 5)])); E.append(tb); E.append(Spacer(1, 6))
def fila_imgs(items, alto_max=95*mm):
    celdas = []
    for f, titulo, texto in items:
        ruta = os.path.join(*f); w, h = Image.open(ruta).size
        a = 52*mm; al = a * h / w
        if al > alto_max: al = alto_max; a = al * w / h
        celdas.append([RLImage(ruta, width=a, height=al), Spacer(1, 2), Paragraph("<b>%s</b>" % titulo, CELL), Paragraph(texto, SM)])
    while len(celdas) < 3: celdas.append("")
    t = Table([celdas], colWidths=[58*mm]*3); t.setStyle(TableStyle([("VALIGN", (0,0), (-1,-1), "TOP")])); E.append(KeepTogether([t, Spacer(1, 8)]))

# ============ Portada
E += [Spacer(1, 30*mm), Paragraph("Asistente Electoral SV", ParagraphStyle("t", fontName="DVB", fontSize=28, leading=32, textColor=NAVY)),
      Paragraph("Funcionamiento de la aplicación", ParagraphStyle("s", fontName="DV", fontSize=15, leading=19, textColor=TEAL, spaceAfter=14)),
      Paragraph("Objetivo, público, bancos de conocimiento, agente interno, consultas posibles y no posibles, ejemplos reales y capturas de pantalla.", P), Spacer(1, 10)]
tabla([["Plataforma", "Estado", "Detalle"],
       ["<b>Android</b>", "<font color='#2E7D32'><b>Operativa al 100 %</b></font>", "Según el equipo del proyecto, la aplicación funciona a plena capacidad y no presenta fallas: consulta, voz, biblioteca, test, casos, paso a paso y ajustes."],
       ["<b>iPhone (iOS)</b>", "<font color='#E65100'><b>En desarrollo</b></font>", "Versión en proceso. Se reutilizan los mismos bancos de conocimiento y documentos."],
       ["<b>Web</b>", "<font color='#E65100'><b>En desarrollo</b></font>", "Versión en proceso. Existe una versión de prueba funcional que sirvió para tomar las capturas de este documento."]], [0.17, 0.2, 0.63])
nota("<b>Principio fundamental:</b> toda la aplicación responde <b>sin internet y sin consumir tokens de inteligencia artificial</b>. Lo que contesta proviene únicamente de su banco de conocimiento verificado contra los documentos oficiales; <b>no inventa respuestas</b>.", VERDE)
nota("<b>Sobre las capturas:</b> se tomaron de la versión web de prueba, que replica la lógica, los bancos y las pantallas de la aplicación Android, en tamaño de teléfono. No son capturas de un teléfono físico; los detalles visuales de Android pueden variar.", GRIS)
E.append(PageBreak())

# ============ 1 Objetivo y público
h1("1. Objetivo de la aplicación")
p("Asistente Electoral SV es una <b>herramienta de consulta y capacitación para el personal que opera el proceso electoral</b>. Su objetivo es que, en el momento en que ocurre una situación en la Junta Receptora de Votos (JRV) o en el centro de votación, la persona encuentre en segundos <b>qué debe hacer, qué no debe hacer y en qué artículo se basa</b>, con acceso directo al documento oficial.")
bl(["<b>Responder con certeza jurídica:</b> cada respuesta cita su fundamento legal (artículo y documento) y abre el PDF en la página exacta.",
    "<b>Funcionar en el lugar de la votación:</b> sin señal de internet y sin costo por consulta.",
    "<b>Capacitar:</b> test de 125 preguntas, examen de 25, casos prácticos, guías paso a paso por cargo y temas de capacitación.",
    "<b>No sustituir a la autoridad:</b> es una herramienta independiente y <b>no oficial</b> del Tribunal Supremo Electoral; no reemplaza sus instructivos, capacitaciones ni resoluciones."])
h1("2. ¿A quiénes está dirigida?")
p("La aplicación <b>no está pensada para el votante</b>. Está dirigida al <b>cuerpo electoral</b>: las personas que organizan, vigilan y ejecutan la jornada electoral, incluidos los equipos electorales de nuevas ideas (ver nota al final).")
tabla([["Rol", "Para qué la usa"],
       ["Presidente de JRV", "Instalación, dirección de la votación, decisiones ante incidentes, cierre y escrutinio"],
       ["Secretario de JRV", "Actas, padrón, firmas, documentación y entrega"],
       ["Vocales 1.º, 2.º y 3.º", "Entrega de papeletas, tinta indeleble, control del padrón y apoyo al escrutinio"],
       ["Vigilantes de partido", "Derechos, límites y prohibiciones en la mesa"],
       ["Supervisores y Jefes de centro", "Coordinación del centro de votación y de las mesas"],
       ["Juntas Electorales (JED / JEM) y otros", "Consulta normativa, funciones y organismos electorales"]], [0.28, 0.72])
p("Por eso el lenguaje de la aplicación es operativo («qué hago si…») y no de orientación al votante.")
E.append(PageBreak())

# ============ 3 Mapa conceptual
h1("3. Mapa conceptual del funcionamiento")
d = Drawing(W, 330)
def caja(x, y, w, h, t, c=TEAL, tc=colors.white, fs=7.6):
    d.add(Rect(x, y, w, h, rx=5, ry=5, fillColor=c, strokeColor=c))
    ls = t.split("\n")
    for i, ln in enumerate(ls): d.add(String(x + w/2, y + h/2 + (len(ls)-1)*4.6 - i*9.2 - 2.6, ln, fontName="DVB" if i == 0 else "DV", fontSize=fs, fillColor=tc, textAnchor="middle"))
def flecha(x1, y1, x2, y2):
    d.add(Line(x1, y1, x2, y2, strokeColor=NAVY, strokeWidth=1.3))
    import math; a = math.atan2(y2-y1, x2-x1); s = 5
    d.add(Polygon([x2, y2, x2 - s*math.cos(a-0.45), y2 - s*math.sin(a-0.45), x2 - s*math.cos(a+0.45), y2 - s*math.sin(a+0.45)], fillColor=NAVY, strokeColor=NAVY))
caja(150, 290, 170, 36, "CUERPO ELECTORAL\nPresidente · Secretario · Vocales\nVigilantes · Supervisores", NAVY)
caja(0, 225, 140, 38, "PREGUNTA\nTexto o voz\n«un vigilante tiene una pistola»", TEAL)
caja(165, 225, 140, 38, "AGENTE INTERNO (local)\nNormaliza · Detecta · Puntúa\nSin internet · sin tokens", NAVY)
caja(330, 225, 140, 38, "RESPUESTA\nSituación · Qué hacer · Qué no\nFundamento legal · Fuente", VERDE)
flecha(235, 290, 70, 263); flecha(140, 244, 165, 244); flecha(305, 244, 330, 244)
caja(165, 160, 140, 38, "BANCOS DE CONOCIMIENTO\n154 situaciones · 297 art. Código\n274 art. Constitución · sinónimos", GRIS)
flecha(235, 225, 235, 198)
caja(330, 160, 140, 38, "BIBLIOTECA PDF\n12 documentos oficiales\nBuscador por artículo", GRIS)
flecha(400, 225, 400, 198); 
caja(0, 160, 140, 38, "SI NO ES ELECTORAL\nAviso de ámbito\nNo inventa respuestas", ROJO)
flecha(165, 235, 80, 198)
d.add(String(0, 138, "Funciones de capacitación (también sin internet):", fontName="DVB", fontSize=8.4, fillColor=NAVY))
for i, (t, s) in enumerate([("TEST 125", "corrección inmediata"), ("EXAMEN 25", "al azar + resultado"), ("CASOS", "23 casos prácticos"), ("PASO A PASO", "8 cargos por etapas"), ("CAPACITACIÓN", "10 temas")]):
    caja(i * 95, 96, 88, 36, t + "\n" + s, TEAL if i % 2 == 0 else NAVY, fs=7.2)
caja(0, 40, 470, 40, "DATOS Y SEGURIDAD EN EL TELÉFONO\nHistorial local de consultas · aviso legal · usuario y sesión · ajustes y tema claro/oscuro", GRIS)
E.append(d)
p("<b>Lectura del mapa:</b> el miembro del cuerpo electoral hace una pregunta; el agente interno la entiende y la compara con los bancos; devuelve una respuesta estructurada con su fuente. Si la consulta no es electoral, avisa el ámbito de la aplicación en vez de inventar. Las funciones de capacitación usan los mismos documentos.")
E.append(PageBreak())

# ============ 4 Bancos
h1("4. Cómo funcionan los bancos de conocimiento")
p("Un <b>banco de conocimiento</b> es un archivo JSON dentro de la aplicación con información ya redactada y verificada. La app no «piensa» ni «genera» texto: busca la ficha que corresponde y la muestra. Hay varios bancos:")
tabla([["Banco", "Contenido", "Cómo se verificó"],
       ["<b>Situaciones</b>", "154 fichas en 13 categorías: Instalación (12), Votación (31), Identificación DUI (8), Seguridad (16), Vigilancia (15), Escrutinio (25), Roles (12), Organismos (9), Recursos y nulidades (7), Delitos (4), Proceso electoral (4), Registro (4), Marco electoral (7)", "Cada artículo citado se comprobó en el PDF de su ley con un validador automático"],
       ["<b>Código Electoral</b>", "297 artículos con título, capítulo, texto literal del PDF y versión legible", "Inicio y final de cada texto hallados en la página indicada"],
       ["<b>Constitución</b>", "274 artículos con su sección o capítulo", "Mismo validador"],
       ["<b>Sinónimos globales</b>", "18 grupos de palabras con el mismo significado: ebriedad (ebrio, borracho, bolo, bola, tomado, alcoholizado…), DUI (cédula, documento), arma (pistola, fierro), PNC (policía)…", "Pruebas de regresión del buscador"],
       ["<b>Documentos (biblioteca)</b>", "12 PDF: Código Electoral, Constitución, Ley de Partidos, LAIP, Sufragio en el Exterior, Reestructuración Municipal, Código Penal, Acuerdo legislativo, Ciclo electoral, Candidaturas no partidarias, Reglamento de observación, Instructivo JRV", "Índice de artículos con la página real de cada uno"],
       ["<b>Capacitación</b>", "125 preguntas de test, 23 casos prácticos, guías paso a paso de 8 cargos y 10 temas", "Con su referencia normativa"]], [0.18, 0.52, 0.30])
h2("Qué lleva una ficha de situación")
tabla([["Campo", "Para qué sirve"],
       ["Título y descripción", "Nombre de la situación y cuándo ocurre"],
       ["Actores, objetos, acciones", "Quién, con qué y qué hace (ej. vigilante · arma · portar)"],
       ["Palabras clave y sinónimos", "Cómo lo diría la gente: «no sale en la lista», «anda tomado»"],
       ["Respuesta", "Situación detectada · Qué hacer (pasos) · Qué no hacer · Quién puede / no puede · Advertencia"],
       ["Fundamento legal", "Ley y artículos con lo que dice cada uno"],
       ["Fuentes", "Documento, página exacta y enlace para abrir el PDF"],
       ["Relacionadas", "Otras situaciones que el usuario puede consultar a continuación"]], [0.28, 0.72])
E.append(PageBreak())

# ============ 5 Agente interno
h1("5. Cómo funciona el agente interno")
p("El «agente interno» es el <b>motor de búsqueda local</b> de la aplicación: un conjunto de reglas y puntajes que corre en el propio teléfono. No es un modelo de inteligencia artificial en línea, por eso es rápido (≈24 ms por consulta), gratuito y funciona sin señal.")
tabla([["Paso", "Qué hace", "Ejemplo con «persona bolo»"],
       ["1. Normalizar", "Quita tildes y signos, corrige errores y jerga", "«persona bolo»"],
       ["2. Filtro de ámbito", "Si la pregunta es claramente ajena (deportes, recetas…), responde con el aviso de ámbito", "No aplica"],
       ["3. Detectar entidades", "Actor, acción, objeto, contexto e intención", "actor: persona · concepto: ebriedad"],
       ["4. Conceptos equivalentes", "Entiende que «bolo» = «borracho» = «estado de ebriedad» (sinónimos globales)", "+30 puntos a la situación de persona en estado de ebriedad"],
       ["5. Puntuar", "Compara con las 154 situaciones: título 12, palabra clave 6, sinónimo 4, actor 10, acción 8, objeto 8, contexto 8, intención 8…", "VOT-005 obtiene la puntuación más alta"],
       ["6. Decidir", "Alta (≥ 35): responde. Media (≥ 18): pide elegir entre opciones. Baja: no inventa", "Responde: Persona en estado de ebriedad"],
       ["7. Mostrar", "Estructura la respuesta con fundamento y fuente", "Qué hacer · Qué no hacer · Art. y página del PDF"]], [0.17, 0.46, 0.37])
h2("Niveles de confianza")
tabla([["Nivel", "Cuándo", "Qué ve el usuario"],
       ["<font color='#2E7D32'><b>Coincidencia encontrada</b></font>", "Puntaje alto y claro", "La respuesta completa"],
       ["<font color='#E65100'><b>Coincidencia probable</b></font>", "Puntaje medio o dos situaciones muy parecidas", "Lista «Selecciona la opción que describe tu caso»"],
       ["<font color='#C62828'><b>Sin coincidencia</b></font>", "Puntaje bajo", "Aviso: «no se encontró una respuesta… la aplicación no inventa respuestas»"]], [0.30, 0.34, 0.36])
h2("Otras capacidades del agente")
bl(["<b>Entiende errores de escritura y jerga:</b> «istalar la mesa», «anda bolo», «fierro», «cédula».",
    "<b>Voz:</b> la pregunta puede dictarse y la respuesta puede leerse en voz alta (reconocimiento y lectura del sistema).",
    "<b>Fuente directa:</b> el botón «VER FUENTE» abre el PDF en la página exacta; el buscador del libro encuentra un artículo por su número.",
    "<b>Preguntas sugeridas y «¿quisiste decir…?»</b> para ayudar a formular la consulta.",
    "<b>Historial local</b> de consultas, que el usuario puede borrar."])
E.append(PageBreak())

# ============ 6 Consultas posibles con ejemplos reales
h1("6. Consultas que se pueden hacer (con ejemplos reales)")
p("La aplicación responde sobre procedimientos, derechos, prohibiciones y normativa del proceso electoral. Estos son ejemplos <b>reales</b> y lo que respondió el agente:")
tabla([["Tipo de consulta", "Ejemplo de pregunta", "Respuesta del agente"],
       ["Incidente en la mesa", "«el votante olvidó su DUI»", "Votante se presenta sin DUI (lo olvidó, lo perdió o lo tiene en trámite) — procedimiento y fundamento"],
       ["Seguridad", "«un vigilante tiene una pistola en la mesa»", "Porte de arma en la JRV o el centro de votación — prohibición y qué hacer"],
       ["Calificación de votos", "«cuándo es nulo un voto»", "Voto nulo: cuándo se anula (dos partidos marcados y otras causales)"],
       ["Funciones por cargo", "«qué funciones tiene el secretario de la jrv»", "Paso a paso de la Secretaría de la JRV"],
       ["Normativa", "«hasta cuándo es la ley seca»", "Ley seca: prohibición de bebidas alcohólicas en las elecciones"],
       ["Horarios", "«a qué hora cierra la votación y qué se hace después»", "Ofrece opciones: cierre a las 5:00 p. m. e inicio del escrutinio, entre otras"],
       ["Consulta general", "«qué es la junta receptora de votos»", "Atribuciones legales de la JRV"],
       ["Lenguaje cotidiano", "«un joven no trajo su documento único de identidad»", "Mismo tema que el DUI olvidado, aunque no use la palabra «DUI»"],
       ["Por artículo (biblioteca)", "Buscar «198» en el Código Electoral", "Abre la página 67, donde empieza el Art. 198 (cierre de la votación)"]], [0.20, 0.34, 0.46])
fila_imgs([((ej, "a1.png"), "El votante olvidó su DUI", "Respuesta completa con pasos y fuente."), ((ej, "a2.png"), "Vigilante con pistola", "Prohibición y procedimiento."), ((ej, "a3.png"), "Cuándo es nulo un voto", "Causales del voto nulo.")], 88*mm)
fila_imgs([((ej, "a5.png"), "Funciones del Secretario", "Guía de la Secretaría de la JRV."), ((ej, "a4.png"), "Hora de cierre (varias opciones)", "Cuando hay dos temas cercanos, el usuario elige."), ((ej, "d7.png"), "Ley seca", "Normativa de bebidas en el período electoral.")], 88*mm)
E.append(PageBreak())

# ============ 7 No se pueden / límites
h1("7. Consultas que no se pueden hacer")
tabla([["No es el propósito de la aplicación", "Qué ocurre"],
       ["Temas ajenos a lo electoral: deportes, recetas, música, películas, chistes, criptomonedas, horóscopo, clima", "Aviso de ámbito de consulta (ver sección 8)"],
       ["Asesoría legal personalizada o interpretaciones que no estén en el banco", "No inventa: indica que no encontró respuesta y sugiere términos electorales"],
       ["Resultados, noticias, encuestas o datos en tiempo real de una elección", "No tiene conexión a internet ni a fuentes externas"],
       ["Información personal del votante (dónde vota, si está en el padrón)", "No consulta bases del TSE; la app es para el personal electoral, no para el votante"],
       ["Normas fuera de los 12 documentos incluidos", "Solo responde con lo que contiene su banco y su biblioteca"],
       ["Opiniones políticas o valoraciones de partidos o candidatos", "Fuera de su ámbito: es una herramienta de procedimiento"]], [0.56, 0.44])
nota("<b>Cobertura limitada a su banco:</b> si una situación real no está en los 154 casos o en los artículos indexados, la app no la responde. El banco se amplía y se corrige con cada revisión.", NARANJA)

# ============ 8 Consultas ajenas
h1("8. ¿Cómo reacciona ante una consulta que no es electoral?")
p("La aplicación tiene <b>tres comportamientos</b>, siempre sin inventar respuestas:")
tabla([["Caso", "Cuándo ocurre", "Reacción"],
       ["<b>A. Aviso de ámbito</b>", "La consulta contiene temas claramente ajenos (fútbol, receta, música, clima, chiste…)", "Muestra «Ámbito de consulta»: la app es exclusiva para consultas electorales, y pide formular una pregunta del proceso electoral"],
       ["<b>B. Sin coincidencia</b>", "La consulta no se parece a nada del banco (texto sin sentido, «gracias»)", "«No se encontró una respuesta relacionada… la aplicación no inventa respuestas», con sugerencias de cómo preguntar"],
       ["<b>C. Pregunta electoral mal formulada</b>", "Hay varias respuestas posibles", "Muestra opciones para elegir"]], [0.22, 0.38, 0.40])
fila_imgs([((ej, "c1.png"), "A. «¿quién ganó el partido de fútbol?»", "La palabra «partido» no se confunde con partido político."), ((ej, "c2.png"), "A. «dame una receta de pupusas»", "Aviso de ámbito."), ((ej, "d2.png"), "B. Texto sin sentido", "No inventa: pide reformular.")], 88*mm)
h2("Límite conocido")
nota("En las pruebas con el motor real se verificó que el aviso de ámbito (caso A) depende de una lista de temas ajenos. Algunas preguntas generales que <b>no</b> están en esa lista (por ejemplo «¿cuál es la capital de Francia?» o «hola buenos días») pueden ser relacionadas por el motor con la situación electoral más parecida y mostrarla como «coincidencia probable», en lugar de rechazarse. En una batería de 20 preguntas ajenas, 5 se rechazaron correctamente y 15 recibieron una respuesta electoral no pertinente. Es la mejora pendiente número 1 (ver sección 10); no afecta las consultas electorales.", ROJO)
E.append(PageBreak())

# ============ 9 Recorrido
h1("9. Recorrido de la aplicación (capturas)")
PASOS = [("01_intro.png", "1. Bienvenida", "Logo y arranque."), ("02_login.png", "2. Acceso", "Usuario de 9 dígitos y contraseña."), ("03_legal.png", "3. Consentimiento legal", "Aviso: herramienta no oficial."),
 ("04_chat_inicio.png", "4. Asistente (chat)", "Texto, voz y sugerencias."), ("05_chat_respuesta.png", "5. Respuesta", "«No aparece en el padrón»."), ("06_pdf.png", "6. «VER FUENTE»", "Abre el PDF oficial."),
 ("07_pdf_buscar.png", "7. Buscador del libro", "«198» → página 67."), ("08_biblioteca.png", "8. Biblioteca", "12 documentos oficiales."), ("09_paso.png", "9. Paso a paso", "Guía por cargo y etapa."),
 ("10_casos.png", "10. Casos y soluciones", "23 casos prácticos."), ("11_caso_detalle.png", "11. Detalle del caso", "Solución y fundamento."), ("12_examen.png", "12. Examen de 25", "Preguntas al azar."),
 ("13_resultado.png", "13. Resultado", "Calificación y repaso."), ("14_test125_papeleta.png", "14. Test de 125", "Corrección inmediata y papeleta."), ("15_capacitacion.png", "15. Capacitación", "10 temas."),
 ("16_menu.png", "16. Menú e historial", "Todas las secciones."), ("17_oscuro.png", "17. Modo oscuro", "Tema oscuro."), ("18_ajustes.png", "18. Ajustes", "Tema y documentos legales.")]
for i in range(0, len(PASOS), 3):
    fila_imgs([((cap, f), t, d_) for f, t, d_ in PASOS[i:i+3]], 100*mm)
    if (i // 3) % 2 == 1 and i + 3 < len(PASOS): E.append(PageBreak())
E.append(PageBreak())

# ============ 10 Estado y pendientes
h1("10. Estado actual y lo que falta")
tabla([["Componente", "Estado"],
       ["Aplicación Android", "<font color='#2E7D32'><b>Operativa al 100 %</b></font> (estado declarado por el equipo): consulta, voz, biblioteca, test, casos, paso a paso, capacitación y ajustes"],
       ["Banco de situaciones (154) y motor local", "<font color='#2E7D32'><b>Completo y verificado</b></font>"],
       ["Sinónimos globales (bolo = borracho = ebriedad)", "Listo en el repositorio; se activa en la app al aplicar el prompt correspondiente en AI Studio"],
       ["Búsqueda en Código Electoral y Constitución", "Datos y motor listos y probados; la conexión con la pantalla de chat se hace con los prompts 2 y 3"],
       ["Versión iPhone", "<font color='#E65100'><b>En desarrollo</b></font>"],
       ["Versión Web", "<font color='#E65100'><b>En desarrollo</b></font> (versión de prueba funcional)"]], [0.38, 0.62])
h2("Lo que falta (en orden de prioridad)")
tabla([["#", "Pendiente", "Por qué"],
       ["1", "<b>Rechazo general de preguntas ajenas</b> (no solo una lista de palabras)", "15 de 20 preguntas ajenas probadas recibieron una respuesta electoral no pertinente (sección 8)"],
       ["2", "<b>Login real</b> o retirarlo", "Hoy cualquier usuario de 9 dígitos con contraseña de 6 caracteres entra; no restringe el acceso"],
       ["3", "Ofuscar la app de producción y desactivar el respaldo automático", "Proteger el código y los datos locales"],
       ["4", "Corregir el aviso legal", "Menciona procesamiento por IA en línea, pero la app responde en local"],
       ["5", "Quitar el permiso de internet y el código sin uso", "Coherencia con «0 internet» y menor superficie de riesgo"],
       ["6", "Conectar Código Electoral y Constitución al chat; aplicar los prompts 1 a 4 en AI Studio", "Funciones ya listas en datos y motor"],
       ["7", "Ampliar el banco con nuevas situaciones y jerga", "Más cobertura de casos reales"],
       ["8", "Portar sinónimos y búsqueda de leyes a la web; versión iPhone", "Paridad entre plataformas"],
       ["9", "Pruebas automáticas dentro del proyecto Android, README, licencia y política de privacidad", "Mantenimiento y publicación en tiendas"]], [0.05, 0.50, 0.45])
nota("<b>Nota sobre el público:</b> este documento indica que la aplicación llega al cuerpo electoral y no al votante, «incluidos los equipos electorales de nuevas ideas». Si se refiere al partido Nuevas Ideas, conviene confirmarlo y cuidar que el texto sea coherente con el aviso de que la herramienta es independiente y no oficial.", GRIS)
doc.build(E, onFirstPage=pie("Asistente Electoral SV — Funcionamiento de la aplicación"), onLaterPages=pie("Asistente Electoral SV — Funcionamiento de la aplicación"))
print("ok", salida)
