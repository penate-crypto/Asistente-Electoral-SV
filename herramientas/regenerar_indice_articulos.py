#!/usr/bin/env python3
"""Corrige el índice de artículos de la biblioteca (assets/libros_indice_completo.json).

El índice tenía páginas equivocadas (ej. Art. 195 del Código Electoral en la pág. 101 en vez de la 65) porque
tomaba las citas cruzadas ("conforme al artículo 195") como si fueran el artículo. Este script lee cada PDF,
busca dónde EMPIEZA realmente cada artículo ("Art. N.-" al inicio de línea) y reescribe 'articles'.
Conserva 'pages' y los demás campos. Uso: python3 herramientas/regenerar_indice_articulos.py
Requiere pdftotext.
"""
import json, os, re, subprocess

REPO = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
A = os.path.join(REPO, "app", "src", "main", "assets")
IDX = os.path.join(A, "libros_indice_completo.json")
PAT = re.compile(r"^[ \t]*Art(?:[íi]culo|\.)[ \t]*(\d+)[ \t]*(?:[.\-–°º]|$)", re.M | re.I)

d = json.load(open(IDX, encoding="utf-8"))
ce = {a["numero"]: a["titulo"] for a in json.load(open(os.path.join(A, "codigo_electoral", "banco_codigo_electoral.json"), encoding="utf-8"))["articulos"]}
cambios = 0
for fname, libro in d.items():
    ruta = os.path.join(A, "libros", fname)
    if not os.path.exists(ruta) or not libro.get("articles"):
        continue
    paginas = subprocess.run(["pdftotext", "-layout", ruta, "-"], capture_output=True, text=True).stdout.split("\f")
    viejos = {x["articleNumber"]: x for x in libro["articles"]}
    nuevos, vistos = [], set()
    for i, t in enumerate(paginas, 1):
        for m in PAT.finditer(t):
            n = int(m.group(1))
            if n in vistos:
                continue  # decretos interpretativos pegados al final del PDF
            vistos.add(n)
            cuerpo = " ".join(t[m.end():].split())[:400]
            v = viejos.get(n, {})
            titulo = ce.get(str(n)) if fname == "Codigo-Electoral.pdf" else v.get("title")
            nuevos.append({"article": "Art. %d" % n, "articleNumber": n, "title": titulo or "Artículo %d" % n,
                           "page": i, "snippet": ("- " + cuerpo) if cuerpo else v.get("snippet", "")})
    if len(nuevos) >= 0.8 * len(libro["articles"]):
        cambios += sum(1 for x in nuevos if viejos.get(x["articleNumber"], {}).get("page") != x["page"])
        print("%-60s %4d -> %4d artículos" % (fname[:60], len(libro["articles"]), len(nuevos)))
        libro["articles"] = nuevos
json.dump(d, open(IDX, "w", encoding="utf-8"), ensure_ascii=False)
print("Páginas corregidas o artículos nuevos:", cambios)
