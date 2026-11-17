#!/usr/bin/env python3
"""Genera app/src/main/assets/json respuestas/banco_conocimientos.json a partir de
las fichas en ./fuente/*.py y VERIFICA cada artículo citado contra el texto real
de los PDF de la biblioteca de la app (requiere el comando pdftotext / poppler-utils).

Uso:  python3 herramientas/banco_conocimientos/generar_banco.py [ruta_salida.json]
"""
import glob, importlib, json, os, re, subprocess, sys, tempfile, time, unicodedata
AQUI = os.path.dirname(os.path.abspath(__file__))
REPO = os.path.abspath(os.path.join(AQUI, "..", ".."))
LIBROS = os.path.join(REPO, "app", "src", "main", "assets", "libros")
SALIDA_POR_DEFECTO = os.path.join(REPO, "app", "src", "main", "assets", "json respuestas", "banco_conocimientos.json")
sys.path.insert(0, os.path.join(AQUI, "fuente"))
import common
from common import LEYES, ENTRIES, DOC_IDS

OUT = sys.argv[1] if len(sys.argv) > 1 else SALIDA_POR_DEFECTO
PAGES = tempfile.mkdtemp(prefix="banco_pdf_")
for pdf in sorted(set(v[1] for v in LEYES.values())):
    subprocess.run(["pdftotext", os.path.join(LIBROS, pdf), os.path.join(PAGES, pdf[:-4] + ".txt")],
                   check=True, stderr=subprocess.DEVNULL)

for mod in sorted(glob.glob(os.path.join(AQUI, "fuente", "k*.py"))):
    importlib.import_module(os.path.basename(mod)[:-3])


def norm(s):
    s = unicodedata.normalize("NFKD", s)
    s = "".join(c for c in s if not unicodedata.combining(c))
    s = s.replace("ﬁ", "fi").replace("ﬂ", "fl")
    return re.sub(r"\s+", " ", s.lower())


page_cache = {}
def pages_of(pdf):
    if pdf not in page_cache:
        p = os.path.join(PAGES, pdf[:-4] + ".txt")
        page_cache[pdf] = [norm(x) for x in open(p, encoding="utf-8").read().split("\f")]
    return page_cache[pdf]


def find_article_page(pdf, num):
    pats = [r"\bart(?:iculo|\.)\s*%s\s*\.?\s*-" % re.escape(num.lower()),
            r"\bart(?:iculo|\.)\s*%s\b" % re.escape(num.lower())]
    for pat in pats:
        for i, t in enumerate(pages_of(pdf)):
            if re.search(pat, t):
                return i + 1
    return None


def find_phrase_page(pdf, phrase):
    ph = norm(phrase)
    for i, t in enumerate(pages_of(pdf)):
        if "....." in t:  # saltar el índice
            continue
        if ph in t:
            return i + 1
    # tolerate line breaks/hyphenation: match first 4 words
    words = ph.split()[:4]
    for i, t in enumerate(pages_of(pdf)):
        if "....." not in t and all(w in t for w in words):
            return i + 1
    return None


errors, warnings = [], []
ids = set()
situaciones = []
now = int(time.time() * 1000)
for e in ENTRIES:
    if e["id"] in ids:
        errors.append("ID duplicado: " + e["id"])
    ids.add(e["id"])
    fund, fuentes = [], []
    for code, arts in e["fundamento"]:
        ley, pdf, tipo = LEYES[code]
        items = []
        for num, desc in arts:
            pg = find_article_page(pdf, num)
            if pg is None:
                errors.append(f"{e['id']}: no se encontró {code} Art. {num} en {pdf}")
            items.append({"numero": num, "fuente": desc})
            fuentes.append({
                "nombre": ley, "tipo": tipo, "pagina": str(pg or ""),
                "seccion": f"Art. {num}", "descripcion": desc,
                "documento_relacionado": f"{ley}, Art. {num}",
                "ubicacion_pdf": "libros/" + pdf, "pdf_doc_id": DOC_IDS[code]})
        fund.append({"ley": ley, "articulos": items})
    for phrase, seccion in e["instructivo"]:
        ley, pdf, tipo = LEYES["INS"]
        pg = find_phrase_page(pdf, phrase)
        if pg is None:
            errors.append(f"{e['id']}: frase no encontrada en instructivo: {phrase}")
        fuentes.append({
            "nombre": ley, "tipo": tipo, "pagina": str(pg or ""), "seccion": seccion,
            "descripcion": "Procedimiento operativo oficial del TSE para integrantes de JRV.",
            "documento_relacionado": "Instructivo JRV - Elecciones 2024",
            "ubicacion_pdf": "libros/" + pdf, "pdf_doc_id": DOC_IDS["INS"]})
    for code, phrase, seccion in e["docs"]:
        ley, pdf, tipo = LEYES[code]
        pg = find_phrase_page(pdf, phrase)
        if pg is None:
            errors.append(f"{e['id']}: frase no encontrada en {pdf}: {phrase}")
        fuentes.append({
            "nombre": ley, "tipo": tipo, "pagina": str(pg or ""), "seccion": seccion,
            "descripcion": "Documento oficial del TSE.", "documento_relacionado": ley,
            "ubicacion_pdf": "libros/" + pdf, "pdf_doc_id": DOC_IDS[code]})
    # deduplicate fuentes por (nombre, seccion)
    seen, fu = set(), []
    for f in fuentes:
        k = (f["nombre"], f["seccion"])
        if k not in seen:
            seen.add(k); fu.append(f)
    resp = {
        "situacion_detectada": e["situacion"],
        "que_hacer": e["que_hacer"],
        "que_no_hacer": e["que_no_hacer"],
        "quien_interviene": e["quien_interviene"],
    }
    if e["quien_puede"]: resp["quien_puede"] = e["quien_puede"]
    if e["quien_no_puede"]: resp["quien_no_puede"] = e["quien_no_puede"]
    if e["condiciones"]: resp["condiciones"] = e["condiciones"]
    if e["advertencia"]: resp["advertencia"] = e["advertencia"]
    resp["fundamento_legal"] = fund
    resp["fuentes"] = fu
    for fld in ("actores", "objetos", "acciones", "contextos", "intenciones", "palabras_clave", "que_hacer", "que_no_hacer"):
        if not e.get(fld) and not resp.get(fld):
            errors.append(f"{e['id']}: campo vacío {fld}")
    situaciones.append({
        "id": e["id"], "titulo": e["titulo"], "categoria": e["categoria"],
        "subcategoria": e["subcategoria"], "descripcion_situacion": e["descripcion"],
        "actores": e["actores"], "objetos": e["objetos"], "acciones": e["acciones"],
        "contextos": e["contextos"], "intenciones": e["intenciones"],
        "palabras_clave": e["palabras_clave"], "sinonimos": e["sinonimos"],
        "respuesta": resp,
        "situaciones_relacionadas": e["relacionadas"],
        "nivel_prioridad": e["prioridad"], "estado": "vigente",
        "creadoEn": now, "actualizadoEn": now})

for s in situaciones:
    for r in s["situaciones_relacionadas"]:
        if r not in ids:
            warnings.append(f"{s['id']}: relacionada inexistente {r}")

cats = {}
for s in situaciones:
    cats[s["categoria"]] = cats.get(s["categoria"], 0) + 1
banco = {
    "version": "2.0",
    "nombre": "Banco de Conocimientos - Asistente Electoral SV",
    "formato": "BANCO_CONOCIMIENTOS_ELECTORAL_SV",
    "fechaExportacion": time.strftime("%Y-%m-%d %H:%M:%S"),
    "totalRegistros": len(situaciones),
    "categorias": cats,
    "situaciones": situaciones,
}
json.dump(banco, open(OUT, "w", encoding="utf-8"), ensure_ascii=False, indent=2)
print("situaciones:", len(situaciones), cats)
for w in warnings: print("AVISO", w)
for x in errors: print("ERROR", x)
sys.exit(1 if errors else 0)
