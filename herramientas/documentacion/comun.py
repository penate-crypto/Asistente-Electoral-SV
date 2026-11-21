from reportlab.lib.pagesizes import A4
from reportlab.lib.units import mm
from reportlab.lib import colors
from reportlab.lib.styles import ParagraphStyle
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
D = "/usr/share/fonts/truetype/dejavu/"
pdfmetrics.registerFont(TTFont("DV", D + "DejaVuSans.ttf")); pdfmetrics.registerFont(TTFont("DVB", D + "DejaVuSans-Bold.ttf"))
pdfmetrics.registerFont(TTFont("DVM", D + "DejaVuSansMono.ttf"))
pdfmetrics.registerFontFamily("DV", normal="DV", bold="DVB", italic="DV", boldItalic="DVB")
TEAL = colors.HexColor("#00A9B8"); NAVY = colors.HexColor("#0B1320"); GRIS = colors.HexColor("#5B6573"); CLARO = colors.HexColor("#EEF6F8")
ROJO = colors.HexColor("#C62828"); NARANJA = colors.HexColor("#E65100"); AMARILLO = colors.HexColor("#B8860B"); VERDE = colors.HexColor("#2E7D32")
H1 = ParagraphStyle("h1", fontName="DVB", fontSize=17, leading=21, textColor=NAVY, spaceBefore=6, spaceAfter=8)
H2 = ParagraphStyle("h2", fontName="DVB", fontSize=12, leading=15, textColor=TEAL, spaceBefore=10, spaceAfter=4)
P = ParagraphStyle("p", fontName="DV", fontSize=9.2, leading=13.2, textColor=colors.HexColor("#222"), spaceAfter=4)
SM = ParagraphStyle("sm", parent=P, fontSize=7.8, leading=10.5, textColor=GRIS)
CELL = ParagraphStyle("cell", parent=P, fontSize=7.9, leading=10.4, spaceAfter=0)
CELLB = ParagraphStyle("cellb", parent=CELL, fontName="DVB")
CODE = ParagraphStyle("code", fontName="DVM", fontSize=7.4, leading=9.6, backColor=CLARO, borderPadding=4, spaceAfter=6)
BUL = ParagraphStyle("bul", parent=P, leftIndent=11, bulletIndent=2, spaceAfter=2)

def pie(titulo):
    def f(c, d):
        c.saveState(); c.setFont("DV", 7.5); c.setFillColor(GRIS)
        c.drawString(18 * mm, 10 * mm, titulo); c.drawRightString(A4[0] - 18 * mm, 10 * mm, "Página %d" % d.page)
        c.setStrokeColor(TEAL); c.setLineWidth(1.2); c.line(18 * mm, 15 * mm, A4[0] - 18 * mm, 15 * mm); c.restoreState()
    return f
