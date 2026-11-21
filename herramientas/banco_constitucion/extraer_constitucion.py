#!/usr/bin/env python3
"""Extrae artículos y estructura (Título/Capítulo/Sección) de la Constitución desde el PDF de la app.
Escribe articulos_crudos.json junto a este archivo. Requiere pdftotext."""
import json, os, re, subprocess

AQUI = os.path.dirname(os.path.abspath(__file__))
PDF = os.path.join(AQUI, "..", "..", "app", "src", "main", "assets", "libros", "constitucion.pdf")

ART = re.compile(r"^\s*Art(?:[íi]culo|\.)\s*(\d+)\s*[.\-–]+\s*(.*)$", re.I)
HEAD = re.compile(r"^\s*(T[ÍI]TULO|CAP[ÍI]TULO|SECCI[ÓO]N)\s+([A-ZÁÉÍÓÚÑ]+(?:\s+[IVXL]+)?)\s*$")
RUIDO = re.compile(r"^\s*(_{5,}|[ÍI]NDICE LEGISLATIVO|ASAMBLEA LEGISLATIVA\s*-\s*REP[ÚU]BLICA DE EL SALVADOR.*|\d{1,3})\s*$", re.I)


def main():
    paginas = subprocess.run(["pdftotext", "-layout", PDF, "-"], capture_output=True, text=True).stdout.split("\f")
    estructura = {"TITULO": "", "CAPITULO": "", "SECCION": ""}
    arts, actual, pend_head = [], None, None
    transitorio = False
    for pg, texto in enumerate(paginas, 1):
        lineas = texto.split("\n")
        i = 0
        while i < len(lineas):
            ln = lineas[i]
            if RUIDO.match(ln) or not ln.strip():
                i += 1; continue
            h = HEAD.match(ln.upper())
            if h and ln.strip().upper() == ln.strip():  # encabezados centrados en mayúsculas
                tipo = re.sub("[ÍI]", "I", h.group(1)).replace("Ó", "O")
                tipo = {"TITULO": "TITULO", "CAPITULO": "CAPITULO", "SECCION": "SECCION"}[tipo.replace("Í", "I")]
                nombre = [ln.strip()]
                j = i + 1
                while j < len(lineas) and lineas[j].strip() and not RUIDO.match(lineas[j]) and lineas[j].strip().upper() == lineas[j].strip() and not ART.match(lineas[j]):
                    nombre.append(lineas[j].strip()); j += 1
                etiqueta = " ".join(nombre)
                if tipo == "TITULO": estructura = {"TITULO": etiqueta, "CAPITULO": "", "SECCION": ""}
                elif tipo == "CAPITULO": estructura.update({"CAPITULO": etiqueta, "SECCION": ""})
                else: estructura["SECCION"] = etiqueta
                pass
                actual = None if actual is None else actual
                i = j; continue
            m = ART.match(ln)
            if m:
                actual = {"numero": int(m.group(1)), "partes": [m.group(2)], "pagina_pdf": pg, "estructura": dict(estructura), "transitorio": transitorio}
                arts.append(actual)
            elif actual is not None:
                actual["partes"].append(ln.strip())
            i += 1
    salida = []
    for a in arts:
        txt = " ".join(" ".join(a["partes"]).split())
        salida.append({"numero": a["numero"], "crudo": txt, "pagina_pdf": a["pagina_pdf"], "estructura": a["estructura"], "transitorio": a["transitorio"]})
    json.dump(salida, open(os.path.join(AQUI, "articulos_crudos.json"), "w", encoding="utf-8"), ensure_ascii=False, indent=0)
    nums = [a["numero"] for a in salida if not a["transitorio"]]
    print(len(salida), "artículos |", len(nums), "no transitorios | repetidos:", len(nums) - len(set(nums)))


if __name__ == "__main__":
    main()
