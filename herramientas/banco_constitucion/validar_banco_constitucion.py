#!/usr/bin/env python3
"""Valida banco_constitucion.json contra el PDF de la Constitución.

Comprueba: JSON válido, IDs únicos, que cada artículo exista en la página indicada del PDF,
que el comienzo y el final del texto literal aparezcan en el PDF, y que las situaciones
relacionadas existan en el banco principal. Salida 1 si hay errores.
"""
import json, os, re, subprocess, sys, unicodedata

AQUI = os.path.dirname(os.path.abspath(__file__))
REPO = os.path.abspath(os.path.join(AQUI, "..", ".."))
BANCO = os.path.join(REPO, "app", "src", "main", "assets", "constitucion", "banco_constitucion.json")
PRINCIPAL = os.path.join(REPO, "app", "src", "main", "assets", "json respuestas", "banco_conocimientos.json")
PDF = os.path.join(REPO, "app", "src", "main", "assets", "libros", "constitucion.pdf")


def norm(s):
    s = unicodedata.normalize("NFKD", s.lower())
    s = "".join(c for c in s if not unicodedata.combining(c))
    return re.sub(r"[^a-z0-9]+", "", s)  # sin espacios ni signos: tolera saltos de línea del PDF


def main():
    b = json.load(open(BANCO, encoding="utf-8"))
    ids_sit = {s["id"] for s in json.load(open(PRINCIPAL, encoding="utf-8"))["situaciones"]}
    pags = [norm(p) for p in subprocess.run(["pdftotext", PDF, "-"], capture_output=True, text=True, check=True).stdout.split("\f")]
    err, vistos = [], set()
    for a in b["articulos"]:
        n = a["numero"]
        if a["id"] in vistos: err.append(f"ID duplicado {a['id']}")
        vistos.add(a["id"])
        for c in ("titulo", "texto_literal", "texto_legible", "pagina_pdf", "palabras_clave"):
            if not a.get(c): err.append(f"CN-{n}: falta {c}")
        pg = a["pagina_pdf"]
        cuerpo = re.sub(r"^Art\. \S+?\.-\s*", "", a["texto_literal"])
        ini, fin = norm(cuerpo[:60]), norm(re.sub(r"\s*\(\d+\)$", "", cuerpo)[-22:])
        ventana = "".join(pags[max(0, pg - 2):pg + 1])  # el artículo puede empezar en la página anterior a la marcada
        if ("art" + norm(n)) not in ventana and ("articulo" + norm(n)) not in ventana:
            err.append(f"CN-{n}: no aparece 'Art. {n}' cerca de la página {pg}")
        if ini and ini not in "".join(pags[max(0, pg - 1):pg + 1]):
            err.append(f"CN-{n}: el inicio del texto no está en la página {pg}")
        vent = "".join(pags[max(0, pg - 1):pg + 8])
        ult = [norm(w) for w in re.sub(r"\s*\(\d+\)$", "", cuerpo).split()[-3:]]
        # el PDF a veces separa la última línea de un artículo: se acepta si las 3 últimas palabras están en la zona
        if fin and fin not in vent and not all(w in vent for w in ult):
            err.append(f"CN-{n}: el final del texto no se encuentra en el PDF")
        for r in a["situaciones_relacionadas"]:
            if r not in ids_sit: err.append(f"CN-{n}: situación relacionada inexistente {r}")
    if b["total_articulos"] != len(b["articulos"]): err.append("total_articulos no coincide")
    print("Artículos:", len(b["articulos"]))
    for e in err[:80]: print("ERROR:", e)
    print("OK: banco válido." if not err else f"{len(err)} error(es).")
    sys.exit(1 if err else 0)


if __name__ == "__main__":
    main()
