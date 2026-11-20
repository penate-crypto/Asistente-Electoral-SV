#!/usr/bin/env python3
"""Genera app/src/main/assets/json respuestas/../codigo_electoral/banco_codigo_electoral.json

Pasos: python3 extraer_articulos.py && python3 generar_banco_codigo_electoral.py
El texto literal sale del PDF del Código Electoral que ya está en la app (no se inventa nada).
"""
import json, math, os, re, unicodedata
from collections import Counter

AQUI = os.path.dirname(os.path.abspath(__file__))
REPO = os.path.abspath(os.path.join(AQUI, "..", ".."))
SALIDA = os.path.join(REPO, "app", "src", "main", "assets", "codigo_electoral", "banco_codigo_electoral.json")
BANCO_PRINCIPAL = os.path.join(REPO, "app", "src", "main", "assets", "json respuestas", "banco_conocimientos.json")

MARC = re.compile(r"(?:(?<=\s)|^)(?=(?:CAP[ÍI]TULO|T[ÍI]TULO|SECCI[ÓO]N)\s+(?:[IVXLCDM]+|[ÚU]NICO|PRIMERA|SEGUNDA|TERCERA|CUARTA|QUINTA)\b)")
NOTA = re.compile(r"\(\d+\)")
STOP = set("""para como este esta estos estas esto sobre entre desde hasta cuando donde segun sera seran podra podran deberan debera
 cada otro otra otros otras todo toda todos todas tambien mismo misma mismos mismas dicho dicha dichos dichas cual cuales
 quien quienes aquel aquella presente codigo articulo articulos ley que los las del por con una uno sus son han haya sin
 sera ser sea sean esten estar estara estaran tendra tendran tiene tienen contra bajo ante cuya cuyo salvo mediante""".split())
SIGLAS = ["TSE", "JRV", "JED", "JEM", "JVE", "PNC", "DUI", "FGR", "ONU", "CAPS", "NIT", "DL", "D.L.", "A.", "B.", "C.", "D.", "E.", "F."]
PROPIOS = ["el salvador", "república", "asamblea legislativa", "tribunal supremo electoral", "fiscalía general de la república",
           "policía nacional civil", "diario oficial", "registro nacional de las personas naturales", "parlamento centroamericano",
           "constitución", "corte suprema de justicia", "código electoral", "código penal", "presidente", "vicepresidente",
           "salvadoreño", "salvadoreña", "centroamericano", "san salvador", "tribunal", "estado"]

ETIQUETAS = [  # (regex en el artículo, palabras clave y sinónimos que se añaden)
    (r"junta[s]? receptora[s]? de votos", ["jrv", "junta receptora", "mesa de votacion", "mesa electoral", "la mesa"]),
    (r"junta[s]? electoral[es]* departamental", ["jed", "junta electoral departamental"]),
    (r"junta[s]? electoral[es]* municipal", ["jem", "junta electoral municipal"]),
    (r"junta[s]? de vigilancia", ["jve", "junta de vigilancia", "vigilancia electoral"]),
    (r"tribunal supremo electoral|el tribunal", ["tse", "tribunal supremo electoral", "el tribunal"]),
    (r"ebri|embriag|bebidas alcoh|licor|alcohol", ["ebrio", "borracho", "bolo", "tomado", "alcoholizado", "bebidas alcoholicas", "ley seca", "estado de ebriedad"]),
    (r"\barmas?\b|portar armas|portacion de arma|arma de fuego", ["arma", "armas", "pistola", "armado", "portar armas", "fierro"]),
    (r"papeleta", ["boleta", "papeleta", "voto en papel"]),
    (r"escrutinio|conteo", ["escrutinio", "conteo", "contar los votos", "recuento"]),
    (r"vigilante", ["vigilante", "representante de partido", "delegado de partido"]),
    (r"tinta", ["tinta indeleble", "dedo", "marca en el dedo"]),
    (r"propaganda", ["propaganda electoral", "campana electoral", "publicidad politica"]),
    (r"documento [úu]nico de identidad|\bdui\b", ["dui", "documento de identidad", "cedula", "carnet"]),
    (r"voto nulo|votos nulos|nulidad|\bnulo", ["voto nulo", "anulado", "nulidad"]),
    (r"multa|sanci[óo]n|pena de|prisi[óo]n|delito", ["multa", "sancion", "castigo", "delito", "carcel", "pena"]),
    (r"padr[óo]n|registro electoral", ["padron", "registro electoral", "lista de votantes", "listado electoral"]),
    (r"candidat", ["candidato", "candidatura", "candidatos"]),
    (r"partidos? pol[íi]tico", ["partido politico", "partidos", "partido"]),
    (r"coalici[óo]n", ["coalicion", "alianza de partidos"]),
    (r"urna", ["urna", "caja de votos", "deposito de votacion"]),
    (r"observador|observaci[óo]n electoral", ["observador", "observacion electoral", "veedor"]),
    (r"voto en el exterior|extranjero|exterior", ["voto en el exterior", "extranjero", "diaspora", "migrante"]),
    (r"discapacidad|invalid|ciego|impedid", ["discapacidad", "persona con discapacidad", "voto asistido", "ciego", "silla de ruedas"]),
    (r"secreto", ["voto secreto", "secreto del voto"]),
    (r"cierre de la votaci[óo]n|diecisiete horas", ["hora de cierre", "a que hora cierran", "a que hora termina la votacion", "hasta que hora se vota", "hasta que hora se puede votar", "cuando cierra la votacion", "cierre"]),
    (r"emisi[óo]n del voto", ["emitir el voto", "como se emite el voto", "como se vota", "emision del voto", "votar"]),
    (r"fuerza armada", ["militares", "soldados", "ejercito", "fuerza armada", "ministerio de la defensa"]),
    (r"inhabilidad", ["inhabilidades", "quien no puede votar", "impedidos para votar", "suspension de derechos"]),
    (r"nulidad de elecci", ["nulidad de elecciones", "anular elecciones", "elecciones nulas", "causales de nulidad"]),
    (r"instal", ["instalacion", "instalar la mesa", "abrir la mesa"]),
    (r"recurso|impugna|apelaci[óo]n|revisi[óo]n", ["recurso", "impugnar", "apelar", "queja"]),
    (r"encuesta|sondeo", ["encuesta", "sondeo", "encuestas electorales"]),
    (r"excusa|sustitu|suplente", ["excusa", "suplente", "sustitucion", "reemplazo"]),
]


# Frases como las diría la gente, escritas a mano para los artículos más consultados (revisadas contra el texto de cada artículo).
EXTRA_SIN = {
    "284": ["guaro", "licor", "trago", "tragos", "cerveza", "alcohol", "vender alcohol", "vender licor", "tomar", "beber", "ley seca", "borrachera", "venta de licor"],
    "195": ["a que hora abre", "a que hora empieza", "a que hora abren", "hora de inicio", "a que hora comienza la votacion", "cuando empieza la votacion", "siete de la manana", "abre la votacion"],
    "198": ["a que hora termina", "a que hora se termina de votar", "cuando termina la votacion", "hasta que hora se vota", "cinco de la tarde", "las cinco", "hora de cierre"],
    "206": ["votos que se impugnan", "impugnar un voto", "voto impugnado", "voto cuestionado", "voto en duda"],
    "207": ["voto nulo", "votos nulos", "voto anulado", "papeleta anulada", "voto malo", "voto invalido"],
    "250": ["hacerse pasar por miembro", "suplantar a un miembro", "falso miembro", "persona que no es miembro de la junta", "usurpar cargo", "se sento en la mesa"],
    "232": ["falta un miembro", "miembro que no llega", "inasistencia", "miembro que falta", "no se presenta", "abandona la junta"],
    "242": ["negarse a ser miembro", "no quiere ser miembro", "renunciar a la junta", "rechazar el cargo", "negarse al cargo", "no acepta el cargo"],
    "192": ["acta de instalacion", "acta cuando se instala", "acta de apertura"],
    "191": ["acreditar vigilante", "como se acredita", "credencial de vigilante", "acreditacion de vigilantes"],
    "220": ["cuando queda firme", "resultado definitivo", "eleccion en firme", "declaratoria de eleccion"],
    "175": ["campana antes de tiempo", "propaganda antes de tiempo", "precampana", "campana anticipada", "propaganda fuera de fecha"],
    "177": ["banderas", "banderas del partido", "camisetas del partido", "simbolos del partido", "colores del partido"],
    "241": ["pastor", "pastores", "sacerdote", "sacerdotes", "iglesia", "religioso", "religiosos", "ministro de culto", "cura", "curas"],
    "6": ["con que documento se vota", "documento para votar", "dui para votar", "que se necesita para votar", "que documento llevar", "documento de identidad para votar"],
    "5": ["inscribirse", "como me inscribo", "registrarme para votar", "inscripcion para votar", "inscribirme en el registro"],
    "28": ["muertos", "fallecidos", "defunciones", "personas fallecidas", "difuntos", "gente muerta"],
    "287": ["buses", "bus", "transporte de votantes", "acarrear", "acarreo", "llevar votantes", "camiones", "buses para llevar votantes", "microbuses"],
    "255": ["pagar multa", "pagar una multa", "plazo para pagar", "cuanto tiempo para pagar", "cuando se paga la multa", "cuanto tiempo tengo para pagar"],
    "212": ["escrutinio", "conteo preliminar", "contar los votos", "conteo de votos", "resultados preliminares"],
    "200": ["escrutinio", "conteo de votos en la mesa", "acta de cierre", "contar los votos al cerrar", "cuando cierran la mesa"],
    "7": ["no puede votar", "quien no puede votar", "impedidos para votar", "inhabilitado", "suspendido de derechos"],
    "290": ["pistola", "pistolas", "armas", "arma", "armado", "portar arma", "llevar arma", "llevar pistola"],
    "289": ["militares", "soldados", "ejercito", "fuerza armada"],
    "197": ["como se emite el voto", "como se vota", "emitir el voto", "votar", "pasos para votar"],
    "196": ["como es el proceso de votar", "procedimiento para votar", "pasos para votar"],
    "273": ["anular elecciones", "cuando se anulan las elecciones", "elecciones nulas", "causales de nulidad"],
}


def norm(s):
    s = unicodedata.normalize("NFKD", s.lower())
    return re.sub(r"\s+", " ", "".join(c for c in s if not unicodedata.combining(c)))


def es_mayus(w):
    letras = [c for c in w if c.isalpha()]
    return bool(letras) and all(c.isupper() for c in letras)


CONECT = {"de", "del", "la", "las", "los", "y", "e", "en", "a", "o", "el", "por", "para", "con", "u"}


def dividir_cola(texto):
    """Devuelve (cuerpo, bloque_estructura, encabezado_siguiente).

    En el PDF, el encabezado (nota al margen) de un artículo queda al FINAL del texto del artículo anterior,
    y los títulos/capítulos se anteponen a ese encabezado."""
    bloque, resto = "", texto
    for m in MARC.finditer(texto):
        cola = texto[m.start():]
        if len(cola) < 260 and "." not in cola.replace("N°", ""):
            bloque, resto = cola.strip(), texto[:m.start()].rstrip()
            break
    if bloque:
        pal = bloque.split()
        i = next((k for k, w in enumerate(pal) if k > 1 and not es_mayus(w) and w[0].isupper()), len(pal))
        return resto, " ".join(pal[:i]), " ".join(pal[i:])
    # sin título/capítulo: el encabezado es la última "frase" corta después de un punto o nota "(n)"
    ult = None
    for m in re.finditer(r"(?:[\.;:]|\(\d+\))\s+", texto):
        ult = m
    if ult:
        cola = texto[ult.end():].strip()
        pal = cola.split()
        if 1 <= len(pal) <= 14 and cola[0].isupper() and not re.match(r"^[a-z]\.", cola):
            if cola.endswith("."):
                if all(w[0].isupper() or w.lower() in CONECT for w in pal) and len(pal) <= 12:
                    return texto[:ult.end()].strip(), "", cola.rstrip(".")
            elif "." not in cola and ":" not in cola:
                return texto[:ult.end()].strip(), "", cola
    return texto, "", ""


# Títulos que el PDF imprime en líneas propias (verificados a mano contra el PDF)
TITULOS_PDF = {
    "1": "Objeto de la Ley",
    "91": "Integración y sede de Juntas Electorales Departamentales",
    "95": "Integración y sede de Juntas Electorales Municipales",
    "99": "Propuestas e Integración",
    "117": "Residencia Obligatoria en el Municipio",
    "153": "Votación a Favor de Candidatos y Candidatas a Presidente o Presidenta y Vicepresidente o Vicepresidenta",
    "162": "Candidaturas por Circunscripciones",
    "185": "Papeletas y Formas de Votación",
    "188": "Electores por Juntas Receptoras de Votos",
    "245": "Multa por Propaganda Anticipada, Difusión de Encuestas y Portación de Símbolos Partidarios",
    "293": "Derogatoria",
}


def limpiar_titulo(s):
    s = re.sub(r"\(\d+\)", "", s).replace("*****DECLARADO INCONSTITUCIONAL", "").strip(" .")
    if s and sum(c.isupper() for c in s) > 0.8 * sum(c.isalpha() for c in s):
        peq = {"de", "del", "la", "las", "los", "y", "e", "en", "a", "o", "el", "por", "para"}
        s = " ".join(w.lower() if i and w.lower() in peq else w.capitalize() for i, w in enumerate(s.split()))
    return s


def a_legible(t):
    if sum(c.isupper() for c in t) < 0.6 * max(1, sum(c.isalpha() for c in t)):
        return t
    s = t.lower()
    s = re.sub(r"(^|[\.\:;]\s+|\(\d+\)\s+|\b[a-z]\.\s+)([a-záéíóúñ])", lambda m: m.group(1) + m.group(2).upper(), s)
    for p in PROPIOS:
        s = re.sub(r"\b" + re.escape(p) + r"\b", lambda m: m.group(0).title() if " " in p else m.group(0).capitalize(), s)
    for sg in ["tse", "jrv", "jed", "jem", "jve", "pnc", "dui", "fgr"]:
        s = re.sub(r"\b" + sg + r"\b", sg.upper(), s)
    s = re.sub(r"\bd\.\s*l\.", "D.L.", s)
    return s


def main():
    crudos = json.load(open(os.path.join(AQUI, "articulos_crudos.json"), encoding="utf-8"))
    ultimo = next(x for x in crudos if x["numero"] == "294")
    ultimo["crudo"] = ultimo["crudo"].split(" DADO EN EL SALÓN AZUL")[0]

    # relación artículo -> situaciones del banco principal que lo citan
    rel = {}
    for s in json.load(open(BANCO_PRINCIPAL, encoding="utf-8"))["situaciones"]:
        for f in s["respuesta"]["fundamento_legal"]:
            if f["ley"].startswith("Código Electoral"):
                for a in f["articulos"]:
                    rel.setdefault(str(a["numero"]), []).append(s["id"])

    arts, titulo_est, cap_est, enc_prev = [], "", "", ""
    for x in crudos:
        n = x["numero"]
        cuerpo = re.sub(r"^(?:Art(?:í|i)culo\s+|Art\.\s*)\d+(?:-[A-Z])?", "", x["crudo"]).lstrip(" .-–").strip()
        interp = False
        if n == "116" and " INTERPRETACIÓN AUTÉNTICA DECRETO" in cuerpo:  # el PDF pega aquí un decreto interpretativo
            cuerpo, interp = cuerpo.split(" INTERPRETACIÓN AUTÉNTICA DECRETO")[0].strip(), True
        cuerpo, bloque, enc_sig = dividir_cola(cuerpo)
        titulo = TITULOS_PDF.get(n) or limpiar_titulo(enc_prev)
        inferido = not titulo
        if inferido:
            titulo = " ".join(cuerpo.split()[:8]).rstrip(",;:") + "…"
        arts.append({"x": x, "n": n, "cuerpo": cuerpo, "titulo": titulo, "inferido": inferido,
                     "titulo_est": titulo_est, "cap_est": cap_est, "interp": interp})
        if bloque:  # el bloque aplica desde el SIGUIENTE artículo
            m = re.search(r"(T[ÍI]TULO\s+\S+.*?)(?=CAP[ÍI]TULO|SECCI[ÓO]N|$)", bloque)
            if re.match(r"T[ÍI]TULO", bloque):
                titulo_est = (m.group(1).strip() if m else bloque)
                cap_est = ""
                mc = re.search(r"((?:CAP[ÍI]TULO|SECCI[ÓO]N)\s+.*)$", bloque)
                if mc: cap_est = mc.group(1).strip()
            else:
                cap_est = bloque
        enc_prev = enc_sig

    # IDF para palabras clave
    docs = {}
    for a in arts:
        toks = [t for t in re.findall(r"[a-z]{4,}", norm(a["titulo"] + " " + a["cuerpo"])) if t not in STOP]
        docs[a["n"]] = toks
    df = Counter(t for toks in docs.values() for t in set(toks))
    N = len(arts)

    salida = []
    for a in arts:
        n, cuerpo = a["n"], a["cuerpo"]
        derog = bool(re.match(r"^\W*DEROGAD", cuerpo))
        letras = [c for c in cuerpo if c.isalpha()]
        mayus = bool(letras) and sum(c.isupper() for c in letras) > 0.6 * len(letras)
        ttl = a["titulo"]
        tf = Counter(docs[n])
        tit_toks = [t for t in re.findall(r"[a-z]{4,}", norm(ttl)) if t not in STOP]
        punt = {t: (c * math.log(N / df[t])) for t, c in tf.items()}
        top = [t for t, _ in sorted(punt.items(), key=lambda kv: -kv[1])[:8]]
        claves = []
        for k in ([norm(ttl).rstrip("…")] if not a["inferido"] else []) + tit_toks + top:
            if k and k not in claves:
                claves.append(k)
        sin, sec = [], []
        low = norm(a["titulo"] + " " + cuerpo)
        tn, ini = norm(a["titulo"]), norm(cuerpo[:300])
        for rx, ks in ETIQUETAS:
            r = norm(rx)
            # solo si el tema es el asunto del artículo (título, inicio) o lo repite varias veces
            en_titulo, en_inicio = bool(re.search(r, tn)), bool(re.search(r, ini))
            if en_titulo or en_inicio:
                destino = sin if en_titulo else sec  # título = tema principal; inicio = tema secundario
                for k in ks:
                    if k not in sin and k not in sec:
                        destino.append(k)
        for k in EXTRA_SIN.get(n, []):
            k = norm(k)
            if k not in sin: sin.append(k)
        citados = sorted({m for m in re.findall(r"art[íi]culos?\s+(\d+(?:-[A-Z])?)", cuerpo, flags=re.I)} - {n},
                         key=lambda v: (int(re.match(r"\d+", v).group()), v))
        salida.append({
            "id": "CE-" + n,
            "numero": n,
            "titulo": ttl,
            "titulo_inferido": a["inferido"],
            "titulo_del_codigo": a["titulo_est"],
            "capitulo": a["cap_est"],
            "texto_literal": ("Art. %s.- " % n) + cuerpo,
            "texto_legible": ("Art. %s.- " % n) + a_legible(cuerpo),
            "mayusculas_en_pdf": mayus,
            "estado": "derogado" if derog else "vigente",
            **({"tiene_interpretacion_autentica": True} if a["interp"] else {}),
            "pagina_pdf": a["x"]["pagina_pdf"],
            "ubicacion_pdf": "libros/Codigo-Electoral.pdf",
            "pdf_doc_id": "codigo_electoral_decreto_413",
            "palabras_clave": claves,
            "sinonimos": sin,
            "sinonimos_secundarios": sec,
            "articulos_citados": citados,
            "situaciones_relacionadas": sorted(set(rel.get(n, []))),
        })
    banco = {
        "version": "1.0",
        "nombre": "Banco Código Electoral",
        "fuente": {"documento": "Código Electoral de El Salvador (Decreto Legislativo N° 413, 2013, con reformas)",
                   "ubicacion_pdf": "libros/Codigo-Electoral.pdf", "pdf_doc_id": "codigo_electoral_decreto_413"},
        "nota": "texto_literal es el texto del PDF. texto_legible lo pasa a minúsculas/mayúsculas normales cuando el PDF lo trae todo en MAYÚSCULAS (artículos reformados). Los títulos son las notas al margen del PDF.",
        "total_articulos": len(salida),
        "articulos": salida,
    }
    os.makedirs(os.path.dirname(SALIDA), exist_ok=True)
    json.dump(banco, open(SALIDA, "w", encoding="utf-8"), ensure_ascii=False, indent=1)
    print("Artículos:", len(salida), "| sin título:", sum(a["titulo_inferido"] for a in salida),
          "| derogados:", sum(a["estado"] == "derogado" for a in salida), "->", SALIDA)


if __name__ == "__main__":
    main()
