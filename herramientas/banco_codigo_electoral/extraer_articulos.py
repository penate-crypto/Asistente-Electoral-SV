#!/usr/bin/env python3
"""Extrae los artículos del Código Electoral (PDF de la app) a una lista estructurada.

Uso: python3 extraer_articulos.py  ->  escribe articulos_crudos.json (junto a este archivo)
Requiere pdftotext (poppler-utils).
"""
import json, os, re, subprocess

AQUI = os.path.dirname(os.path.abspath(__file__))
PDF = os.path.join(AQUI, "..", "..", "app", "src", "main", "assets", "libros", "Codigo-Electoral.pdf")

RUIDO = re.compile(r"_{5,}\s*(?:ÍNDICE|INDICE) LEGISLATIVO\s*\d*\s*ASAMBLEA LEGISLATIVA\s*-\s*REPÚBLICA DE EL SALVADOR\s*\d*\s*_{5,}", re.I)
RUIDO2 = re.compile(r"(?:ÍNDICE|INDICE) LEGISLATIVO\s*\d*\s*ASAMBLEA LEGISLATIVA\s*-\s*REPÚBLICA DE EL SALVADOR", re.I)
INI = re.compile(r"^\s*Art(?:í|i)culo\s+(\d+(?:-[A-Z])?)|^\s*Art\.\s*(\d+(?:-[A-Z])?)", re.M)


def paginas():
    txt = subprocess.run(["pdftotext", "-layout", PDF, "-"], capture_output=True, text=True, check=True).stdout
    return txt.split("\f")


def main():
    pags = paginas()
    # texto continuo con marca de página
    partes, marcas = [], []
    pos = 0
    for i, p in enumerate(pags, 1):
        p = re.sub(r"[ \t]+", " ", p)
        marcas.append((pos, i))
        partes.append(p)
        pos += len(p) + 1
    t = "\n".join(partes)

    def pagina_de(offset):
        pg = 1
        for o, i in marcas:
            if o <= offset:
                pg = i
        return pg

    arts, vistos = [], set()
    ms = list(re.finditer(r"\n\s*(?=Art(?:í|i)culo\s+\d+|Art\.\s*\d+)", t))
    for k, m in enumerate(ms):
        fin = ms[k + 1].start() if k + 1 < len(ms) else len(t)
        trozo = t[m.end():fin]
        mm = re.match(r"Art(?:í|i)culo\s+(\d+(?:-[A-Z])?)|Art\.\s*(\d+(?:-[A-Z])?)", trozo)
        if not mm:
            continue
        n = (mm.group(1) or mm.group(2)).rstrip(".-")
        cuerpo = " ".join(trozo.split())
        cuerpo = RUIDO.sub(" ", cuerpo); cuerpo = RUIDO2.sub(" ", cuerpo)
        cuerpo = " ".join(cuerpo.split())
        if n in vistos:  # decretos interpretativos del final: no son artículos del Código
            continue
        vistos.add(n)
        arts.append({"numero": n, "crudo": cuerpo, "pagina_pdf": pagina_de(m.end())})
    json.dump(arts, open(os.path.join(AQUI, "articulos_crudos.json"), "w", encoding="utf-8"), ensure_ascii=False, indent=0)
    print(len(arts), "artículos")


if __name__ == "__main__":
    main()
