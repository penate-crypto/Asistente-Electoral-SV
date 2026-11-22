#!/usr/bin/env python3
"""Valida los bancos de assets/leyes/*.json contra sus PDF: cada artículo (inicio y final de su texto) debe estar
en la página indicada o cerca, los IDs no se repiten y las situaciones relacionadas existen. Salida 1 si hay errores."""
import glob, json, os, re, subprocess, sys, unicodedata

REPO = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
A = os.path.join(REPO, "app", "src", "main", "assets")


def norm(s):
    s = unicodedata.normalize("NFKD", s.lower())
    return re.sub(r"[^a-z0-9]+", "", "".join(c for c in s if not unicodedata.combining(c)))


def main():
    ids_sit = {s["id"] for s in json.load(open(os.path.join(A, "json respuestas", "banco_conocimientos.json"), encoding="utf-8"))["situaciones"]}
    err, total = [], 0
    for f in sorted(glob.glob(os.path.join(A, "leyes", "*.json"))):
        b = json.load(open(f, encoding="utf-8"))
        pdf = os.path.join(A, b["fuente"]["ubicacion_pdf"])
        pags = [norm(p) for p in subprocess.run(["pdftotext", pdf, "-"], capture_output=True, text=True).stdout.split("\f")]
        vistos = set()
        for a in b["articulos"]:
            total += 1
            n, pg = a["numero"], a["pagina_pdf"]
            if a["id"] in vistos: err.append(f"{a['id']}: ID duplicado")
            vistos.add(a["id"])
            cuerpo = re.sub(r"^Art\. \S+?\.-\s*", "", a["texto_literal"])
            sin_nota = re.sub(r"(\s*\(\d+\))+$", "", cuerpo)
            ini, fin = norm(cuerpo[:28]), norm(sin_nota[-22:])
            ventana_ini = "".join(pags[max(0, pg - 2):pg + 1])
            if ("art" + n) not in ventana_ini and ("articulo" + n) not in ventana_ini:
                err.append(f"{a['id']}: no aparece 'Art. {n}' cerca de la página {pg}")
            if ini and ini not in "".join(pags[max(0, pg - 1):pg + 1]):
                err.append(f"{a['id']}: el inicio del texto no está en la página {pg}")
            vent = "".join(pags[max(0, pg - 1):pg + 8])
            ult = [norm(w) for w in sin_nota.split()[-3:]]
            if fin and fin not in vent and not all(w in vent for w in ult):
                err.append(f"{a['id']}: el final del texto no se encuentra en el PDF")
            for r in a["situaciones_relacionadas"]:
                if r not in ids_sit: err.append(f"{a['id']}: situación relacionada inexistente {r}")
        if b["total_articulos"] != len(b["articulos"]): err.append(f"{b['id']}: total_articulos no coincide")
    print("Bancos:", len(glob.glob(os.path.join(A, "leyes", "*.json"))), "| artículos:", total)
    for e in err[:60]: print("ERROR:", e)
    print("OK: bancos válidos." if not err else f"{len(err)} error(es).")
    sys.exit(1 if err else 0)


if __name__ == "__main__":
    main()
