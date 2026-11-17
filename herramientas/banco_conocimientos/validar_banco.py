#!/usr/bin/env python3
"""Valida uno o varios archivos JSON del banco de conocimientos de la app.

Úselo SIEMPRE después de editar el JSON a mano (por ejemplo desde AI Studio):

    python3 herramientas/banco_conocimientos/validar_banco.py
    python3 herramientas/banco_conocimientos/validar_banco.py "ruta/a/otro.json"

Revisa:
  * que el JSON sea válido y tenga la estructura que lee JsonKnowledgeLoader.kt;
  * campos obligatorios no vacíos;
  * IDs únicos (también entre varios archivos de la carpeta "json respuestas");
  * que las "situaciones_relacionadas" apunten a IDs existentes;
  * etiquetas de "intenciones" y "contextos" que entiende el motor de búsqueda;
  * que cada artículo citado exista en el PDF de esa ley (si pdftotext está instalado).
Devuelve código de salida 1 si hay errores.
"""
import glob, json, os, re, shutil, subprocess, sys, tempfile, unicodedata

AQUI = os.path.dirname(os.path.abspath(__file__))
REPO = os.path.abspath(os.path.join(AQUI, "..", ".."))
CARPETA = os.path.join(REPO, "app", "src", "main", "assets", "json respuestas")
LIBROS = os.path.join(REPO, "app", "src", "main", "assets", "libros")

# Ley (como se escribe en "fundamento_legal[].ley") -> PDF de la biblioteca
PDF_POR_LEY = {
    "Código Electoral de El Salvador": "Codigo-Electoral.pdf",
    "Constitución de la República de El Salvador": "constitucion.pdf",
    "Código Penal de El Salvador": "Código Penal.pdf",
    "Ley Especial para el Ejercicio del Sufragio en el Extranjero": "LEY ESPECIAL PARA EL EJERCICIO DEL SUFRAGIO EN EL EXTRANJERO.pdf",
    "Reglamento General para la Observación Electoral Nacional e Internacional": "REGLAMENTO-GENERAL-PARA-LA-OBSERVACION-ELECTORAL-NACIONAL-E-INTERNACIONAL-EN-EL-SALVADOR.pdf",
    "Ley Especial para la Reestructuración Municipal": "LEY ESPECIAL PARA LA REESTRUCTURACIÓN MUNICIPAL.pdf",
    "Acuerdo de Reforma Constitucional N° 3 (D.O. N° 143, 31 de julio de 2025)": "ACUERDO LEGISLATIVO - REFORMA - SUPRECIÓN SEGUNDA ELECCIÓN.pdf",
    "Disposiciones Especiales para Candidaturas No Partidarias": "Disposciones-Especiales-Candidaturas-no-partidarias.pdf",
}

# Etiquetas que produce ElectoralEntityExtractor.kt (coincidencia exacta = +8 puntos)
INTENCIONES = {"procedimiento", "consulta_legal_normativa", "consulta_permiso_prohibicion",
               "prohibicion_sancion", "competencia_quien_puede", "quien_no_puede",
               "funciones_atribuciones", "derechos", "procedimiento_emergencia"}
CONTEXTOS = {"seguridad_electoral", "identificacion_elector", "instalacion_jrv", "escrutinio_conteo",
             "proceso_votacion", "inclusion_asistencia", "acreditacion_vigilancia", "derechos_laborales"}

OBLIGATORIOS = ["id", "titulo", "categoria", "subcategoria", "descripcion_situacion", "actores",
                "objetos", "acciones", "contextos", "intenciones", "palabras_clave", "respuesta"]
OBLIGATORIOS_RESP = ["situacion_detectada", "que_hacer", "que_no_hacer", "fundamento_legal", "fuentes"]


def norm(s):
    s = unicodedata.normalize("NFKD", s)
    s = "".join(c for c in s if not unicodedata.combining(c))
    return re.sub(r"\s+", " ", s.lower())


def cargar_pdfs():
    if not shutil.which("pdftotext"):
        return None
    tmp, textos = tempfile.mkdtemp(prefix="validar_banco_"), {}
    for ley, pdf in PDF_POR_LEY.items():
        ruta = os.path.join(LIBROS, pdf)
        if not os.path.exists(ruta):
            continue
        out = os.path.join(tmp, "x.txt")
        subprocess.run(["pdftotext", ruta, out], check=True, stderr=subprocess.DEVNULL)
        textos[ley] = norm(open(out, encoding="utf-8").read())
    return textos


def main():
    archivos = sys.argv[1:] or sorted(glob.glob(os.path.join(CARPETA, "*.json")))
    errores, avisos, situaciones = [], [], []
    for a in archivos:
        try:
            raiz = json.load(open(a, encoding="utf-8"))
        except Exception as e:
            errores.append(f"{a}: JSON inválido: {e}")
            continue
        lista = raiz if isinstance(raiz, list) else raiz.get("situaciones", [])
        if isinstance(raiz, dict) and raiz.get("totalRegistros") not in (None, len(lista)):
            avisos.append(f"{os.path.basename(a)}: totalRegistros={raiz.get('totalRegistros')} pero hay {len(lista)} situaciones")
        situaciones += [(os.path.basename(a), s) for s in lista]

    ids = {}
    for arch, s in situaciones:
        sid = s.get("id", "?")
        if sid in ids:
            errores.append(f"ID duplicado {sid} ({ids[sid]} y {arch})")
        ids[sid] = arch
        for c in OBLIGATORIOS:
            if not s.get(c):
                errores.append(f"{sid}: falta o está vacío '{c}'")
        r = s.get("respuesta") or {}
        for c in OBLIGATORIOS_RESP:
            if not r.get(c):
                errores.append(f"{sid}: respuesta sin '{c}'")
        for i in s.get("intenciones", []):
            if i not in INTENCIONES:
                avisos.append(f"{sid}: intención '{i}' no la genera el motor (no suma puntos)")
        if not any(c in CONTEXTOS for c in s.get("contextos", [])):
            avisos.append(f"{sid}: ningún contexto reconocido por el motor ({sorted(CONTEXTOS)})")
        for f in r.get("fundamento_legal", []):
            for art in f.get("articulos", []):
                n = str(art.get("numero", ""))
                if n.lower().startswith("art"):
                    errores.append(f"{sid}: 'numero' debe ser solo el número (ej. \"198\"), no '{n}'")
    for arch, s in situaciones:
        for rel in s.get("situaciones_relacionadas", []):
            if rel not in ids:
                errores.append(f"{s.get('id')}: situación relacionada inexistente {rel}")

    textos = cargar_pdfs()
    if textos is None:
        avisos.append("pdftotext no está instalado: no se verificaron los artículos contra los PDF")
    else:
        for arch, s in situaciones:
            for f in (s.get("respuesta") or {}).get("fundamento_legal", []):
                t = textos.get(f.get("ley"))
                if t is None:
                    avisos.append(f"{s['id']}: ley sin PDF conocido para verificar: {f.get('ley')}")
                    continue
                for art in f.get("articulos", []):
                    n = re.escape(str(art.get("numero", "")).lower())
                    if not re.search(r"\bart(?:iculo|\.)\s*%s\b" % n, t):
                        errores.append(f"{s['id']}: el Art. {art.get('numero')} no aparece en el PDF de '{f.get('ley')}'")

    print(f"Archivos: {len(archivos)} | Situaciones: {len(situaciones)}")
    for a in avisos:
        print("AVISO:", a)
    for e in errores:
        print("ERROR:", e)
    print("OK: el banco es válido." if not errores else f"{len(errores)} error(es).")
    sys.exit(1 if errores else 0)


if __name__ == "__main__":
    main()
