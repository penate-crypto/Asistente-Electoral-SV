#!/usr/bin/env python3
"""Genera un banco de artículos por ley en app/src/main/assets/leyes/<id>.json (mismo formato que el del Código Electoral).

Texto literal leído del PDF de la biblioteca; título = encabezado (nota al margen) impreso antes de cada artículo.
Uso: python3 herramientas/bancos_leyes/generar_bancos_leyes.py   (requiere pdftotext)
Código Penal: solo los delitos electorales (en el PDF es únicamente el Art. 295, Fraude Electoral).
"""
import importlib.util, json, math, os, re, subprocess
from collections import Counter

AQUI = os.path.dirname(os.path.abspath(__file__))
REPO = os.path.abspath(os.path.join(AQUI, "..", ".."))
A = os.path.join(REPO, "app", "src", "main", "assets")
SALIDA = os.path.join(A, "leyes")
PRINCIPAL = os.path.join(A, "json respuestas", "banco_conocimientos.json")

def _cargar(ruta, nombre):
    sp = importlib.util.spec_from_file_location(nombre, ruta); m = importlib.util.module_from_spec(sp); sp.loader.exec_module(m); return m
ce = _cargar(os.path.join(REPO, "herramientas", "banco_codigo_electoral", "generar_banco_codigo_electoral.py"), "ce")
bi = _cargar(os.path.join(REPO, "herramientas", "biblioteca", "generar_indice_y_palabras.py"), "bi")

LEYES = [
    # id, prefijo, título, archivo, pdf_doc_id, prefijo de ley en 'fundamento_legal' del banco principal, solo estos artículos
    ("ley_partidos", "LP", "Ley de Partidos Políticos", "ley-de-partidos-politicos.pdf", "ley_de_partidos_politicos", "Ley de Partidos", None),
    ("ley_acceso_informacion", "LA", "Ley de Acceso a la Información Pública", "Ley-de-Acceso-a-la-Informacion-Publica.pdf", "ley_acceso_informacion_publica", "Ley de Acceso", None),
    ("ley_sufragio_exterior", "SE", "Ley Especial para el Ejercicio del Sufragio en el Extranjero", "LEY ESPECIAL PARA EL EJERCICIO DEL SUFRAGIO EN EL EXTRANJERO.pdf", "ley_sufragio_extranjero_542", "Ley Especial para el Ejercicio del Sufragio", None),
    ("ley_reestructuracion_municipal", "RM", "Ley Especial para la Reestructuración Municipal", "LEY ESPECIAL PARA LA REESTRUCTURACIÓN MUNICIPAL.pdf", "ley_reestructuracion_municipal_763", "Ley Especial para la Reestructuración", None),
    ("reglamento_observacion", "RO", "Reglamento General para la Observación Electoral", "REGLAMENTO-GENERAL-PARA-LA-OBSERVACION-ELECTORAL-NACIONAL-E-INTERNACIONAL-EN-EL-SALVADOR.pdf", "reglamento_observacion_electoral", "Reglamento General para la Observación", None),
    ("candidaturas_no_partidarias", "CP", "Disposiciones Especiales para Candidaturas No Partidarias", "Disposciones-Especiales-Candidaturas-no-partidarias.pdf", "disposiciones_candidaturas_no_partidarias", "Disposiciones Especiales", None),
    ("acuerdo_legislativo", "AL", "Acuerdo Legislativo: reforma y supresión de la segunda elección", "ACUERDO LEGISLATIVO - REFORMA - SUPRECIÓN SEGUNDA ELECCIÓN.pdf", "acuerdo_legislativo_reforma_electoral", "Acuerdo de Reforma", None),
    ("codigo_penal_electoral", "PE", "Código Penal: delitos electorales", "Código Penal.pdf", "codigo_penal_delitos_electorales", "Código Penal", {295}),
]
PAT = re.compile(r"^[ \t]*Art(?:[íi]culo|\.)[ \t]*(\d+)[ \t]*(?:[.\-–°º]|$)", re.M | re.I)


def articulos_de(pdf):
    """[(n, pagina, titulo, texto_literal)] del PDF."""
    ruta = os.path.join(A, "libros", pdf)
    pags = subprocess.run(["pdftotext", "-layout", ruta, "-"], capture_output=True, text=True).stdout.split("\f")
    cuerpo, marca, pos = [], [], 0
    for i, p in enumerate(pags, 1):
        marca.append((pos, i)); cuerpo.append(p); pos += len(p) + 1
    t = "\n".join(cuerpo)
    pag_de = lambda o: max([i for (s, i) in marca if s <= o] or [1])
    inicios = []
    vistos = set()
    for m in PAT.finditer(t):
        n = int(m.group(1))
        if n in vistos:
            continue
        vistos.add(n)
        titulo, ini_tit = bi.bloque_titulo(t[:m.end()], m.start())
        inicios.append((n, m.start(), m.end(), titulo, ini_tit))
    out = []
    for k, (n, s, e, titulo, ini_tit) in enumerate(inicios):
        fin = inicios[k + 1][4] if k + 1 < len(inicios) else len(t)
        lineas = [ln for ln in t[e:fin].split("\n") if not bi.RUIDO.match(ln)]
        texto = " ".join(" ".join(lineas).split())
        texto = re.sub(r"^[.\-–\s]+", "", texto)
        out.append((n, pag_de(s), titulo or "Artículo %d" % n, texto))
    return out


def main():
    rel = {}
    for s in json.load(open(PRINCIPAL, encoding="utf-8"))["situaciones"]:
        for f in s["respuesta"]["fundamento_legal"]:
            for a in f["articulos"]:
                rel.setdefault((f["ley"], str(a["numero"])), []).append(s["id"])
    os.makedirs(SALIDA, exist_ok=True)
    resumen = []
    for lid, pref, nombre, pdf, docid, ley_prefijo, solo in LEYES:
        arts = [a for a in articulos_de(pdf) if (solo is None or a[0] in solo)]
        docs = {n: [w for w in re.findall(r"[a-z]{4,}", ce.norm(tit + " " + txt)) if w not in ce.STOP] for n, _, tit, txt in arts}
        df = Counter(w for ws in docs.values() for w in set(ws)); N = max(1, len(arts))
        out = []
        for n, pg, tit, txt in arts:
            letras = [c for c in txt if c.isalpha()]
            mayus = bool(letras) and sum(c.isupper() for c in letras) > 0.6 * len(letras)
            tf = Counter(docs[n])
            top = [w for w, _ in sorted(((w, c * math.log((N + 1) / df[w])) for w, c in tf.items()), key=lambda kv: -kv[1])[:8]]
            tit_toks = [w for w in re.findall(r"[a-z]{4,}", ce.norm(tit)) if w not in ce.STOP]
            claves = []
            for kw in ([ce.norm(tit)] if not tit.startswith("Artículo ") else []) + tit_toks + top:
                if kw and kw not in claves:
                    claves.append(kw)
            sin, sec = [], []
            tn, ini = ce.norm(tit), ce.norm(txt[:300])
            for rx, ks in ce.ETIQUETAS:
                r = ce.norm(rx)
                en_t, en_i = bool(re.search(r, tn)), bool(re.search(r, ini))
                if en_t or en_i:
                    for k2 in ks:
                        if k2 not in sin and k2 not in sec:
                            (sin if en_t else sec).append(k2)
            citados = sorted({int(x) for x in re.findall(r"art[íi]culos?\s+(\d+)", txt, flags=re.I)} - {n})
            relacionadas = sorted({sid for (ley, num), ids in rel.items() if num == str(n) and ley.startswith(ley_prefijo) for sid in ids})
            out.append({
                "id": "%s-%d" % (pref, n), "numero": str(n), "titulo": tit, "titulo_es_oficial": not tit.startswith("Artículo "),
                "titulo_del_codigo": "", "capitulo": "",
                "texto_literal": "Art. %d.- %s" % (n, txt), "texto_legible": "Art. %d.- %s" % (n, ce.a_legible(txt)),
                "mayusculas_en_pdf": mayus, "estado": "derogado" if re.match(r"^\W*DEROGAD", txt) else "vigente",
                "pagina_pdf": pg, "ubicacion_pdf": "libros/" + pdf, "pdf_doc_id": docid,
                "palabras_clave": claves, "sinonimos": sin, "sinonimos_secundarios": sec,
                "articulos_citados": [str(c) for c in citados], "situaciones_relacionadas": relacionadas,
            })
        banco = {"version": "1.0", "id": lid, "nombre": nombre,
                 "fuente": {"documento": nombre, "ubicacion_pdf": "libros/" + pdf, "pdf_doc_id": docid},
                 "nota": "Texto literal leído del PDF de la biblioteca. Los títulos son los encabezados impresos en el PDF; si no hay, dice «Artículo N». Los artículos en MAYÚSCULAS en el PDF (reformados) se muestran en 'texto_legible' en minúsculas normales.",
                 "total_articulos": len(out), "articulos": out}
        json.dump(banco, open(os.path.join(SALIDA, lid + ".json"), "w", encoding="utf-8"), ensure_ascii=False, indent=1)
        resumen.append((lid, len(out), sum(1 for a in out if not a["titulo_es_oficial"])))
    for r in resumen:
        print("%-34s %4d artículos · %3d sin título" % r)


if __name__ == "__main__":
    main()
