#!/usr/bin/env python3
"""PDF con capturas del funcionamiento (versión web de prueba, que replica la lógica de la app).
Uso: python3 pdf_capturas.py <carpeta_capturas> <salida.pdf>"""
import sys, os
from PIL import Image
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, PageBreak, Image as RLImage, KeepTogether
from reportlab.lib.units import mm
from comun import *

carpeta, salida = sys.argv[1], sys.argv[2]
PASOS = [
 ("01_intro.png", "1. Pantalla de bienvenida", "Logo y arranque de la aplicación."),
 ("02_login.png", "2. Acceso", "Usuario de 9 dígitos y contraseña de 6 o más caracteres. Valida formato antes de entrar."),
 ("03_legal.png", "3. Consentimiento legal", "Aviso obligatorio: herramienta independiente, no oficial del TSE."),
 ("04_chat_inicio.png", "4. Asistente (chat)", "Pantalla principal: caja de texto, botón de voz y preguntas sugeridas."),
 ("05_chat_respuesta.png", "5. Respuesta del banco de conocimientos", "Pregunta: «persona que fue a votar no aparece en el padrón». Respuesta estructurada: situación, qué hacer, qué no hacer, fundamento legal y fuente."),
 ("06_pdf.png", "6. «VER FUENTE» abre el PDF", "El botón de la fuente abre el documento oficial en la página exacta."),
 ("07_pdf_buscar.png", "7. Buscador dentro del libro", "Búsqueda de «198» en el Código Electoral: lista las coincidencias y salta a la página (la 67)."),
 ("08_biblioteca.png", "8. Biblioteca", "Los 12 documentos oficiales: Código Electoral, Constitución, leyes, instructivo JRV, etc."),
 ("09_paso.png", "9. Paso a paso por cargo", "Guía por etapas para Presidente, Secretario, Vocales y demás roles."),
 ("10_casos.png", "10. Casos y soluciones", "Casos prácticos con su solución."),
 ("11_caso_detalle.png", "11. Detalle de un caso", "Solución correcta con su fundamento."),
 ("12_examen.png", "12. Examen de 25 preguntas", "Tomadas al azar del banco de 125 preguntas."),
 ("13_resultado.png", "13. Resultado del examen", "Calificación y repaso de aciertos y errores."),
 ("14_test125_papeleta.png", "14. Test de 125 preguntas con papeleta", "Corrección inmediata y papeleta ilustrada en preguntas de votos válidos/nulos."),
 ("15_capacitacion.png", "15. Capacitación", "Temas de capacitación con botón «Preguntar al asistente»."),
 ("16_menu.png", "16. Menú lateral e historial", "Acceso a todas las secciones y a las consultas anteriores."),
 ("17_oscuro.png", "17. Modo oscuro", "Tema oscuro de la aplicación."),
 ("18_ajustes.png", "18. Ajustes", "Configuración, tema, accesos al test completo y documentos legales."),
]
doc = SimpleDocTemplate(salida, pagesize=A4, leftMargin=18*mm, rightMargin=18*mm, topMargin=18*mm, bottomMargin=20*mm,
                        title="Asistente Electoral SV — Funcionamiento (capturas)", author="Proyecto Asistente Electoral SV")
e = [Spacer(1, 40*mm), Paragraph("Asistente Electoral SV", ParagraphStyle("t", fontName="DVB", fontSize=28, leading=32, textColor=NAVY)),
     Paragraph("Funcionamiento de la aplicación — capturas de pantalla", ParagraphStyle("s", fontName="DV", fontSize=14, leading=18, textColor=TEAL, spaceAfter=18)),
     Paragraph("Este documento muestra, paso a paso, cómo funciona la aplicación: acceso, consulta al asistente, lectura de la fuente oficial en PDF, biblioteca, capacitación, exámenes y ajustes.", P),
     Spacer(1, 6),
     Paragraph("<b>Nota de transparencia:</b> las capturas se tomaron de la <b>versión web de prueba</b> (carpeta <font name='DVM'>web/</font>), que reproduce la misma lógica, los mismos bancos de conocimiento y las mismas pantallas que la aplicación Android, en un navegador con tamaño de teléfono. No son capturas del APK en un teléfono físico. Las pantallas de Android pueden diferir en detalles visuales.", P),
     Spacer(1, 6),
     Paragraph("Todo el funcionamiento de consulta es <b>local</b>: no usa internet ni consume tokens de inteligencia artificial. Durante la prueba automática de estas pantallas se registraron <b>0 errores</b> de consola o de red.", P),
     PageBreak()]
def celda(p):
    ruta = os.path.join(carpeta, p[0]); w, h = Image.open(ruta).size
    ancho = 56*mm; alto = ancho * h / w
    if alto > 118*mm: alto = 118*mm; ancho = alto * w / h
    return [RLImage(ruta, width=ancho, height=alto), Spacer(1, 3), Paragraph("<b>%s</b>" % p[1], CELL), Paragraph(p[2], SM)]
for i in range(0, len(PASOS), 3):
    fila = [celda(p) for p in PASOS[i:i+3]]
    while len(fila) < 3: fila.append("")
    t = Table([fila], colWidths=[58*mm]*3); t.setStyle(TableStyle([("VALIGN", (0,0), (-1,-1), "TOP"), ("BOX", (0,0), (-1,-1), 0, colors.white)]))
    e += [t, Spacer(1, 10)]
    if (i // 3) % 2 == 1 and i + 3 < len(PASOS): e.append(PageBreak())
doc.build(e, onFirstPage=pie("Asistente Electoral SV — Funcionamiento"), onLaterPages=pie("Asistente Electoral SV — Funcionamiento"))
print("ok", salida)
