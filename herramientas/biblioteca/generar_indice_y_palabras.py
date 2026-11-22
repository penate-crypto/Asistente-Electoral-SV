#!/usr/bin/env python3
"""Genera, leyendo los PDF reales de la biblioteca:

  1. app/src/main/assets/libros_indice_completo.json   texto de cada página + índice de artículos (con su página real)
  2. app/src/main/assets/libros_palabras/<archivo>.json posición (caja) de cada palabra de cada página, para subrayar en amarillo

El texto de la página y las cajas salen de la MISMA lectura (pdftotext -bbox), por lo que siempre coinciden.
Uso: python3 herramientas/biblioteca/generar_indice_y_palabras.py      (requiere pdftotext / poppler-utils)
"""
import html, json, os, re, subprocess, unicodedata

REPO = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
A = os.path.join(REPO, "app", "src", "main", "assets")
LIBROS = os.path.join(A, "libros")
SALIDA_IDX = os.path.join(A, "libros_indice_completo.json")
SALIDA_PAL = os.path.join(A, "libros_palabras")
PAT_ART = re.compile(r"^[ \t]*Art(?:[íi]culo|\.)[ \t]*(\d+)[ \t]*(?:[.\-–°º]|$)", re.M | re.I)
PAG = re.compile(r'<page width="([\d.]+)" height="([\d.]+)">(.*?)</page>', re.S)
PAL = re.compile(r'<word xMin="([\d.]+)" yMin="([\d.]+)" xMax="([\d.]+)" yMax="([\d.]+)">(.*?)</word>')


def norm(s):
    s = unicodedata.normalize("NFKD", s.lower())
    return re.sub(r"[^a-z0-9]+", " ", "".join(c for c in s if not unicodedata.combining(c))).strip()


RUIDO = re.compile(r"^\s*(_{4,}|[ÍI]NDICE LEGISLATIVO|ASAMBLEA LEGISLATIVA.*|\d{1,3})\s*$", re.I)
PEQ = {"de", "del", "la", "las", "los", "y", "e", "en", "a", "o", "el", "por", "para", "con", "al", "u"}


def bloque_titulo(texto, pos):
    """(título, posición donde empieza su línea) del encabezado impreso justo antes de 'Art. N' (en 'pos').
    Si no hay un encabezado claro devuelve ("", pos)."""
    lineas = texto[:pos].split("\n")
    offs, acc = [], 0
    for ln in lineas:
        offs.append(acc); acc += len(ln) + 1
    juntas, i = [], len(lineas) - 1
    while i >= 0 and (not lineas[i].strip() or RUIDO.match(lineas[i])):
        i -= 1
        if len(lineas) - 1 - i > 3:
            return "", pos
    inicio = pos
    while i >= 0 and lineas[i].strip() and not RUIDO.match(lineas[i]):
        juntas.insert(0, lineas[i].strip()); inicio = offs[i]; i -= 1
        if len(juntas) > 3:
            return "", pos
    s = re.sub(r"\(\d+\)", "", " ".join(juntas)).strip(" .")
    if not s or len(s) > 140 or re.match(r"^(Art|CAP|T[ÍI]T|SECC)", s, re.I) or re.search(r"[.;:,]$", " ".join(juntas).strip()):
        return "", pos
    if sum(c.isupper() for c in s) > 0.8 * sum(c.isalpha() for c in s):
        s = " ".join(w.lower() if k and w.lower() in PEQ else w.capitalize() for k, w in enumerate(s.split()))
    return s, inicio


def titulo_previo(texto, pos):
    return bloque_titulo(texto, pos)[0]


def leer_palabras(ruta):
    xml = subprocess.run(["pdftotext", "-bbox", "-enc", "UTF-8", ruta, "-"], capture_output=True, text=True, check=True).stdout
    paginas = []
    for w, h, cuerpo in PAG.findall(xml):
        w, h = float(w), float(h)
        pals, cajas = [], []
        for x0, y0, x1, y1, t in PAL.findall(cuerpo):
            t = html.unescape(t).strip()
            if not t:
                continue
            x0, y0, x1, y1 = float(x0), float(y0), float(x1), float(y1)
            pals.append(t.replace(" ", ""))
            cajas += [round(x0 / w * 1000), round(y0 / h * 1000), max(1, round((x1 - x0) / w * 1000)), max(1, round((y1 - y0) / h * 1000))]
        paginas.append({"w": w, "h": h, "t": " ".join(pals), "b": cajas})
    return paginas


def main():
    viejo = json.load(open(SALIDA_IDX, encoding="utf-8")) if os.path.exists(SALIDA_IDX) else {}
    ce = {a["numero"]: a["titulo"] for a in json.load(open(os.path.join(A, "codigo_electoral", "banco_codigo_electoral.json"), encoding="utf-8"))["articulos"]}
    cn = {a["numero"]: a["titulo"] for a in json.load(open(os.path.join(A, "constitucion", "banco_constitucion.json"), encoding="utf-8"))["articulos"]}
    os.makedirs(SALIDA_PAL, exist_ok=True)
    indice = {}
    for nombre in sorted(os.listdir(LIBROS)):
        if not nombre.lower().endswith(".pdf"):
            continue
        ruta = os.path.join(LIBROS, nombre)
        paginas = leer_palabras(ruta)
        layout = subprocess.run(["pdftotext", "-layout", ruta, "-"], capture_output=True, text=True).stdout.split("\f")
        previo = viejo.get(nombre, {})
        tit_prev = {x["articleNumber"]: x.get("title") for x in previo.get("articles", []) if isinstance(x, dict)}
        arts, vistos = [], set()
        for i, t in enumerate(layout[:len(paginas)], 1):
            for m in PAT_ART.finditer(t):
                n = int(m.group(1))
                if n in vistos:
                    continue  # decretos interpretativos pegados al final
                vistos.add(n)
                cuerpo = " ".join(t[m.end():].split())[:400]
                titulo = (ce.get(str(n)) if nombre == "Codigo-Electoral.pdf" else cn.get(str(n)) if nombre == "constitucion.pdf" else titulo_previo(t, m.start())) or "Artículo %d" % n
                arts.append({"article": "Art. %d" % n, "articleNumber": n, "title": titulo, "page": i, "snippet": ("- " + cuerpo) if cuerpo else ""})
        if not arts and previo.get("articles"):
            # sin artículos numerados (instructivo): se conservan los temas y se ubican en la página donde de verdad aparecen
            for x in previo["articles"]:
                claves = [p for p in norm(x.get("title", "")).split() if len(p) > 3]
                pag = next((k for k, p in enumerate(paginas, 1) if claves and all(c in norm(p["t"]) for c in claves)), None)
                if pag:  # si el tema no se encuentra en el texto del PDF, se descarta: no se deja una página sin verificar
                    arts.append({**x, "page": pag})
        indice[nombre] = {"fileName": nombre, "totalPages": len(paginas), "articles": arts,
                          "pages": [{"pageNumber": i, "text": p["t"]} for i, p in enumerate(paginas, 1)]}
        json.dump({"pages": [{"w": round(p["w"], 1), "h": round(p["h"], 1), "t": p["t"], "b": p["b"]} for p in paginas]},
                  open(os.path.join(SALIDA_PAL, nombre + ".json"), "w", encoding="utf-8"), ensure_ascii=False, separators=(",", ":"))
        print("%-62s págs %3d · artículos %3d · palabras %6d" % (nombre[:62], len(paginas), len(arts), sum(len(p["b"]) // 4 for p in paginas)))
    for v in indice.values():
        for a in v["articles"]:
            a.pop("_ubicado", None)
    json.dump(indice, open(SALIDA_IDX, "w", encoding="utf-8"), ensure_ascii=False)
    print("índice:", round(os.path.getsize(SALIDA_IDX) / 1e6, 2), "MB · palabras:", round(sum(os.path.getsize(os.path.join(SALIDA_PAL, f)) for f in os.listdir(SALIDA_PAL)) / 1e6, 2), "MB")


if __name__ == "__main__":
    main()
