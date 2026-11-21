#!/usr/bin/env python3
"""Genera app/src/main/assets/constitucion/banco_constitucion.json (274 artículos de la Constitución).

Pasos: python3 extraer_constitucion.py && python3 generar_banco_constitucion.py && python3 validar_banco_constitucion.py
El texto sale del PDF de la biblioteca. La Constitución no trae títulos al margen: cada artículo lleva el
nombre de su Sección/Capítulo y, en los artículos más consultados, un título corto escrito a mano (TITULOS).
"""
import importlib.util, json, math, os, re
from collections import Counter

AQUI = os.path.dirname(os.path.abspath(__file__))
REPO = os.path.abspath(os.path.join(AQUI, "..", ".."))
SALIDA = os.path.join(REPO, "app", "src", "main", "assets", "constitucion", "banco_constitucion.json")
PRINCIPAL = os.path.join(REPO, "app", "src", "main", "assets", "json respuestas", "banco_conocimientos.json")

# reutiliza utilidades del generador del Código Electoral
_sp = importlib.util.spec_from_file_location("ce", os.path.join(REPO, "herramientas", "banco_codigo_electoral", "generar_banco_codigo_electoral.py"))
ce = importlib.util.module_from_spec(_sp); _sp.loader.exec_module(ce)

TITULOS = {  # revisados contra el texto de cada artículo en el PDF
    1: "La persona humana y los fines del Estado", 2: "Derechos fundamentales de la persona", 3: "Igualdad ante la ley",
    71: "Quiénes son ciudadanos", 72: "Derechos políticos del ciudadano", 73: "Deberes políticos del ciudadano",
    74: "Suspensión de los derechos de ciudadanía", 75: "Pérdida de los derechos de ciudadano", 76: "Cuerpo electoral",
    77: "Registro electoral y partidos políticos", 78: "Características del voto", 79: "Circunscripciones electorales",
    80: "Elección de Presidente, diputados y concejos municipales", 81: "Propaganda electoral",
    82: "Restricciones a ministros de culto, Fuerza Armada y PNC", 83: "Soberanía", 85: "Gobierno y partidos políticos",
    126: "Requisitos para ser Diputado", 131: "Atribuciones de la Asamblea Legislativa",
    6: "Libertad de expresión", 7: "Libertad de asociación y reunión", 8: "Principio de legalidad", 11: "Debido proceso",
    12: "Presunción de inocencia", 37: "El trabajo", 50: "Seguridad social", 65: "La salud", 86: "El poder público",
    87: "Derecho a la insurrección", 90: "Salvadoreños por nacimiento", 168: "Atribuciones del Presidente de la República",
    174: "Sala de lo Constitucional", 182: "Atribuciones de la Corte Suprema de Justicia", 246: "Principios que no se pueden alterar",
    248: "Reforma de la Constitución",
    208: "Tribunal Supremo Electoral", 209: "Organismos electorales", 210: "Deuda política", 211: "La Fuerza Armada",
}
EXTRA_SIN = {
    71: ["quien es ciudadano", "edad para votar", "mayor de 18", "dieciocho anos", "mayoria de edad", "a que edad se vota"],
    72: ["derechos politicos", "que derechos tiene un ciudadano", "derecho al voto", "derecho a votar", "derecho a ser candidato"],
    73: ["deberes del ciudadano", "obligacion de votar", "el voto es obligatorio", "es obligatorio votar", "deber de votar"],
    74: ["quien pierde el voto", "suspension de ciudadania", "preso puede votar", "presos pueden votar", "enajenado mental", "interdicto", "no puede votar"],
    75: ["quien pierde los derechos de ciudadano", "pierde los derechos de ciudadano", "compra de votos", "vender el voto", "perdida de ciudadania", "perder el derecho al voto", "condenado por delito", "compran votos"],
    76: ["cuerpo electoral", "quienes votan"],
    77: ["inscrito en el registro electoral", "estar inscrito para votar", "registro electoral", "padron", "partidos politicos"],
    78: ["voto secreto", "voto libre", "voto directo", "voto igualitario", "caracteristicas del voto"],
    79: ["circunscripciones", "base del sistema electoral", "por departamento"],
    80: ["elecciones de presidente", "eleccion de diputados", "eleccion de alcaldes", "concejos municipales", "parlacen"],
    81: ["propaganda electoral", "cuando empieza la campana", "cuatro meses antes", "campana electoral", "propaganda"],
    82: ["pastor", "pastores", "ministro de culto", "sacerdote", "militares", "policia", "pnc", "afiliarse a un partido", "pertenecer a un partido"],
    85: ["partidos politicos", "pluralismo", "sistema politico", "gobierno republicano"],
    126: ["requisitos diputado", "para ser diputado", "edad de un diputado"],
    208: ["tse", "magistrados del tse", "cuantos magistrados", "tribunal supremo electoral", "cinco magistrados", "quien elige al tse"],
    209: ["organismos electorales", "juntas receptoras", "recepcion de votos", "recuento de votos", "fiscalizacion de votos"],
    210: ["deuda politica", "financiamiento de partidos", "dinero a los partidos"],
    211: ["fuerza armada", "ejercito", "apolitica", "no deliberante"],
}
ETIQ = [e for e in ce.ETIQUETAS if not re.search("junta|tribunal|vigilante|urna|tinta|papeleta", e[0])] + [
    (r"sufragio|voto", ["voto", "votar", "sufragio", "elecciones"]),
    (r"ciudadan", ["ciudadano", "ciudadania", "ciudadanos"]),
    (r"tribunal supremo electoral", ["tse", "tribunal supremo electoral"]),
    (r"libertad|derecho", ["derecho", "derechos", "garantia"]),
]


def cap_estructura(s):
    s = re.sub(r"\(\d+\)", "", s).strip()
    return re.sub(r"\s+", " ", s)


def nombre_corto(est):
    """'CAPITULO III LOS CIUDADANOS, SUS...' -> 'Los ciudadanos, sus...' """
    for k in ("SECCION", "CAPITULO"):
        v = est.get(k, "")
        if v:
            v = cap_estructura(re.sub(r"^(SECCI[ÓO]N|CAP[ÍI]TULO)\s+(\S+(?:\s+(?:[IVXL]+))?)\s+", lambda m: m.group(0) if m.group(2).isupper() and len(m.group(2)) > 4 and m.group(2) not in ("PRIMERA", "SEGUNDA", "TERCERA", "CUARTA", "QUINTA", "UNICO") else "", v))
            v = re.sub(r"^(PRIMERA|SEGUNDA|TERCERA|CUARTA|QUINTA|UNICO)\s+", "", v)
            if v:
                return v[0] + v[1:].lower()
    t = cap_estructura(est.get("TITULO", ""))
    t = re.sub(r"^T[ÍI]TULO\s+[IVXL]+\s*", "", t)
    return (t[0] + t[1:].lower()) if t else ""


def main():
    crudos = json.load(open(os.path.join(AQUI, "articulos_crudos.json"), encoding="utf-8"))
    crudos[-1]["crudo"] = crudos[-1]["crudo"].split(" DADO EN EL SALON")[0]
    rel = {}
    for s in json.load(open(PRINCIPAL, encoding="utf-8"))["situaciones"]:
        for f in s["respuesta"]["fundamento_legal"]:
            if f["ley"].startswith("Constitución"):
                for a in f["articulos"]:
                    rel.setdefault(str(a["numero"]), []).append(s["id"])
    docs = {}
    for a in crudos:
        a["tit"] = TITULOS.get(a["numero"]) or nombre_corto(a["estructura"]) or "Artículo %d" % a["numero"]
        docs[a["numero"]] = [t for t in re.findall(r"[a-z]{4,}", ce.norm(a["tit"] + " " + a["crudo"])) if t not in ce.STOP]
    df = Counter(t for toks in docs.values() for t in set(toks)); N = len(crudos)
    out = []
    for a in crudos:
        n = a["numero"]; cuerpo = a["crudo"]
        letras = [c for c in cuerpo if c.isalpha()]
        mayus = bool(letras) and sum(c.isupper() for c in letras) > 0.6 * len(letras)
        tf = Counter(docs[n])
        top = [t for t, _ in sorted(((t, c * math.log(N / df[t])) for t, c in tf.items()), key=lambda kv: -kv[1])[:8]]
        tit_toks = [t for t in re.findall(r"[a-z]{4,}", ce.norm(a["tit"])) if t not in ce.STOP]
        claves = []
        for k in [ce.norm(a["tit"])] + tit_toks + top:
            if k and k not in claves: claves.append(k)
        sin, sec = [], []
        tn, ini = ce.norm(a["tit"]), ce.norm(cuerpo[:300])
        for rx, ks in ETIQ:
            r = ce.norm(rx)
            en_t, en_i = bool(re.search(r, tn)), bool(re.search(r, ini))
            if en_t or en_i:
                for k in ks:
                    if k not in sin and k not in sec: (sin if en_t else sec).append(k)
        for k in EXTRA_SIN.get(n, []):
            k = ce.norm(k)
            if k not in sin: sin.append(k)
        citados = sorted({int(m) for m in re.findall(r"art[íi]culos?\s+(\d+)", cuerpo, flags=re.I)} - {n})
        est = a["estructura"]
        out.append({
            "id": "CN-%d" % n, "numero": str(n), "titulo": a["tit"],
            "titulo_es_oficial": False,
            "titulo_del_codigo": cap_estructura(est.get("TITULO", "")),
            "capitulo": cap_estructura(" — ".join(x for x in (est.get("CAPITULO", ""), est.get("SECCION", "")) if x)),
            "texto_literal": "Art. %d.- %s" % (n, cuerpo),
            "texto_legible": "Art. %d.- %s" % (n, ce.a_legible(cuerpo)),
            "mayusculas_en_pdf": mayus, "estado": "derogado" if re.match(r"^\W*DEROGAD", cuerpo) else "vigente",
            "pagina_pdf": a["pagina_pdf"], "ubicacion_pdf": "libros/constitucion.pdf", "pdf_doc_id": "constitucion_republica_1983",
            "palabras_clave": claves, "sinonimos": sin, "sinonimos_secundarios": sec,
            "articulos_citados": [str(c) for c in citados], "situaciones_relacionadas": sorted(set(rel.get(str(n), []))),
        })
    banco = {"version": "1.0", "nombre": "Banco Constitución de la República",
             "fuente": {"documento": "Constitución de la República de El Salvador (Decreto N° 38, 1983, con reformas hasta el texto del PDF)",
                        "ubicacion_pdf": "libros/constitucion.pdf", "pdf_doc_id": "constitucion_republica_1983"},
             "nota": "La Constitución no trae títulos al margen. 'titulo' es el nombre de la sección/capítulo o un título corto escrito a mano. Las reformas posteriores al texto del PDF (ej. 2025) están en el banco principal (ELE-002).",
             "total_articulos": len(out), "articulos": out}
    os.makedirs(os.path.dirname(SALIDA), exist_ok=True)
    json.dump(banco, open(SALIDA, "w", encoding="utf-8"), ensure_ascii=False, indent=1)
    print("Artículos:", len(out), "->", SALIDA)


if __name__ == "__main__":
    main()
