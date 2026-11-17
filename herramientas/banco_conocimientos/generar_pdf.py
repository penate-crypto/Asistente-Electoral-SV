#!/usr/bin/env python3
"""Genera un PDF legible con todo el banco de conocimientos (como se vería cada respuesta en la app).
Uso: python3 herramientas/banco_conocimientos/generar_pdf.py [salida.pdf]   (requiere Node.js con playwright y Chromium)"""
import html, json, os, sys
from collections import OrderedDict
AQUI = os.path.dirname(os.path.abspath(__file__))
REPO = os.path.abspath(os.path.join(AQUI, "..", ".."))
JSON = os.path.join(REPO, "app", "src", "main", "assets", "json respuestas", "banco_conocimientos.json")
OUT = sys.argv[1] if len(sys.argv) > 1 else os.path.join(AQUI, "Banco_de_Conocimientos_Asistente_Electoral_SV.pdf")
d = json.load(open(JSON, encoding="utf-8"))
e = lambda s: html.escape(str(s))
titulos = {s["id"]: s["titulo"] for s in d["situaciones"]}
cats = OrderedDict()
for s in d["situaciones"]:
    cats.setdefault(s["categoria"], []).append(s)

def lista(items, cls=""):
    return "<ul class='%s'>%s</ul>" % (cls, "".join("<li>%s</li>" % e(i) for i in items)) if items else ""

def ficha(s):
    r = s["respuesta"]; p = []
    p.append(f"<section class='ficha' id='{e(s['id'])}'><div class='cab'><span class='id'>{e(s['id'])}</span>"
             f"<span class='sub'>{e(s['categoria'])} · {e(s['subcategoria'])}</span>"
             f"<span class='prio {e(s['nivel_prioridad'])}'>Prioridad {e(s['nivel_prioridad'])}</span></div>")
    p.append(f"<h3>{e(s['titulo'])}</h3><p class='desc'>{e(s['descripcion_situacion'])}</p>")
    p.append(f"<div class='blk sit'><h4>Situación detectada</h4><p>{e(r['situacion_detectada'])}</p></div>")
    p.append("<div class='blk si'><h4>¿Qué hacer?</h4><ol>%s</ol></div>" % "".join("<li>%s</li>" % e(x) for x in r["que_hacer"]))
    p.append("<div class='blk no'><h4>¿Qué NO hacer?</h4>%s</div>" % lista(r["que_no_hacer"]))
    grid = []
    if r.get("quien_interviene"): grid.append("<div><h5>Quién interviene</h5>%s</div>" % lista(r["quien_interviene"]))
    if r.get("quien_puede"): grid.append("<div><h5>Quién puede</h5>%s</div>" % lista(r["quien_puede"]))
    if r.get("quien_no_puede"): grid.append("<div><h5>Quién NO puede</h5>%s</div>" % lista(r["quien_no_puede"]))
    if r.get("condiciones"): grid.append("<div><h5>Condiciones</h5>%s</div>" % lista(r["condiciones"]))
    if grid: p.append("<div class='grid'>%s</div>" % "".join(grid))
    if r.get("advertencia"): p.append(f"<div class='blk adv'><h4>⚠ Advertencia</h4><p>{e(r['advertencia'])}</p></div>")
    fl = []
    for f in r["fundamento_legal"]:
        for a in f["articulos"]:
            fl.append(f"<li><b>{e(f['ley'])}, Art. {e(a['numero'])}:</b> {e(a['fuente'])}</li>")
    p.append("<div class='blk ley'><h4>Fundamento legal</h4><ul>%s</ul></div>" % "".join(fl))
    fu = "".join(f"<li>{e(f['nombre'])} — {e(f['seccion'])}" + (f" · pág. {e(f['pagina'])} del PDF" if f.get('pagina') else "") + "</li>" for f in r["fuentes"])
    p.append(f"<div class='blk fue'><h4>Fuentes en la Biblioteca</h4><ul>{fu}</ul></div>")
    if s.get("situaciones_relacionadas"):
        p.append("<p class='rel'><b>Temas relacionados:</b> %s</p>" % " · ".join(
            f"<a href='#{e(x)}'>{e(x)} {e(titulos.get(x, ''))}</a>" for x in s["situaciones_relacionadas"]))
    p.append("<p class='kw'><b>Se activa con preguntas como:</b> %s</p>" % e(", ".join(s["palabras_clave"])))
    p.append("</section>")
    return "".join(p)

indice = "".join(f"<h3 class='icat'>{e(c)} <small>({len(v)})</small></h3><ul class='idx'>" +
                 "".join(f"<li><a href='#{e(s['id'])}'><b>{e(s['id'])}</b> {e(s['titulo'])}</a></li>" for s in v) + "</ul>"
                 for c, v in cats.items())
cuerpo = "".join(f"<h2 class='cat'>{e(c)}</h2>" + "".join(ficha(s) for s in v) for c, v in cats.items())
resumen = "".join(f"<tr><td>{e(c)}</td><td>{len(v)}</td></tr>" for c, v in cats.items())
doc = f"""<!doctype html><html lang='es'><head><meta charset='utf-8'><style>
@page {{ size: Letter; margin: 16mm 14mm 16mm 14mm; }}
body {{ font-family: 'DejaVu Sans', Arial, sans-serif; font-size: 9.6pt; color:#1d2633; line-height:1.38; }}
a {{ color:#1b4f9c; text-decoration:none; }}
.portada {{ height: 235mm; display:flex; flex-direction:column; justify-content:center; page-break-after:always; }}
.portada h1 {{ font-size:30pt; color:#0d3b7a; margin:0 0 6px; }} .portada h2 {{ font-weight:normal; color:#3d5a80; margin:0 0 24px; }}
.portada table {{ border-collapse:collapse; width:60%; }} .portada td {{ border-bottom:1px solid #d5dce6; padding:4px 8px; }}
.nota {{ margin-top:18px; color:#4a5568; font-size:9pt; max-width:150mm; }}
h2.cat {{ page-break-before:always; color:#fff; background:#0d3b7a; padding:8px 12px; border-radius:6px; font-size:15pt; }}
.icat {{ color:#0d3b7a; margin:12px 0 4px; }} ul.idx {{ margin:0; padding-left:14px; columns:1; }} ul.idx li {{ margin:1px 0; }}
.ficha {{ border:1px solid #c9d3e0; border-radius:8px; padding:10px 12px; margin:10px 0 14px; page-break-inside:auto; }}
.cab {{ display:flex; gap:10px; align-items:center; font-size:8.5pt; }}
.id {{ background:#0d3b7a; color:#fff; padding:1px 7px; border-radius:4px; font-weight:bold; }}
.sub {{ color:#4a5568; flex:1; }} .prio {{ padding:1px 6px; border-radius:4px; }} .prio.alta {{ background:#fde2e1; color:#9b1c1c; }} .prio.normal {{ background:#e6edf6; color:#33475b; }}
h3 {{ margin:6px 0 2px; color:#0d3b7a; font-size:12pt; }} .desc {{ margin:0 0 6px; color:#4a5568; font-style:italic; }}
.blk {{ border-left:4px solid #9aa8b8; padding:3px 9px; margin:6px 0; border-radius:3px; }}
.blk h4 {{ margin:0 0 2px; font-size:9.8pt; }} .blk p {{ margin:0; }} .blk ul, .blk ol {{ margin:2px 0; padding-left:18px; }}
.sit {{ border-color:#3d6fb6; background:#f1f6fc; }} .si {{ border-color:#2f855a; background:#f0faf4; }} .no {{ border-color:#c53030; background:#fff5f5; }}
.adv {{ border-color:#d69e2e; background:#fffbea; }} .ley {{ border-color:#553c9a; background:#f6f3fc; }} .fue {{ border-color:#718096; background:#f7f9fb; font-size:8.8pt; }}
.grid {{ display:flex; flex-wrap:wrap; gap:8px; margin:6px 0; }} .grid > div {{ flex:1 1 30%; background:#f7f9fb; border-radius:4px; padding:4px 8px; }}
.grid h5 {{ margin:0; font-size:9pt; color:#33475b; }} .grid ul {{ margin:2px 0; padding-left:16px; }}
.rel {{ font-size:8.6pt; margin:6px 0 2px; }} .kw {{ font-size:8.2pt; color:#5a6778; margin:2px 0 0; }}
</style></head><body>
<div class='portada'><h1>Banco de Conocimientos</h1><h2>Asistente Electoral SV — versión {e(d['version'])} · {d['totalRegistros']} situaciones</h2>
<table>{resumen}<tr><td><b>Total</b></td><td><b>{d['totalRegistros']}</b></td></tr></table>
<p class='nota'>Este documento muestra el contenido del archivo <i>banco_conocimientos.json</i> tal como la app lo presenta al usuario:
situación detectada, qué hacer, qué no hacer, quién interviene, fundamento legal y fuentes de la Biblioteca (con la página del PDF).
Cada artículo citado fue verificado contra el texto de los PDF incluidos en la app. Las fichas con «Advertencia» contienen datos
que pueden cambiar en cada elección y deben revisarse con el instructivo vigente del TSE.</p></div>
<h2 class='cat' style='page-break-before:avoid'>Índice</h2>{indice}{cuerpo}</body></html>"""
html_path = OUT[:-4] + ".html"
open(html_path, "w", encoding="utf-8").write(doc)
import subprocess
js = """const { chromium } = require('playwright');
(async () => { const b = await chromium.launch(); const pg = await b.newPage();
  await pg.goto('file://' + process.argv[process.argv.length - 2], { waitUntil: 'load' });
  await pg.pdf({ path: process.argv[process.argv.length - 1], format: 'Letter', printBackground: true, displayHeaderFooter: true,
    headerTemplate: '<span></span>',
    footerTemplate: "<div style='font-size:7pt;width:100%;text-align:center;color:#888'>Banco de Conocimientos · Asistente Electoral SV · página <span class='pageNumber'></span> de <span class='totalPages'></span></div>",
    margin: { top: '14mm', bottom: '16mm', left: '12mm', right: '12mm' } });
  await b.close(); })();"""
subprocess.run(["node", "-e", js, "--", html_path, OUT], check=True)
os.remove(html_path)
print("PDF:", OUT)
