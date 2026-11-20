#!/usr/bin/env python3
"""Pasa a JSON los contenidos que la app Android tiene escritos en código Kotlin
(test de 125 preguntas, casos y soluciones, paso a paso, capacitación, biblioteca, textos legales)."""
import json, os, re, sys
AQUI = os.path.dirname(os.path.abspath(__file__)); WEB = os.path.abspath(os.path.join(AQUI, ".."))
sys.path.insert(0, AQUI)
from kotlin_datos import P, llamadas, limpiar_consts
K = os.path.abspath(os.path.join(WEB, "..", "app", "src", "main", "java", "com", "example"))
rd = lambda p: open(os.path.join(K, p), encoding="utf-8").read()
out = lambda n, d: open(os.path.join(WEB, "data", n), "w", encoding="utf-8").write(json.dumps(d, ensure_ascii=False, indent=1))

# 1. Test de 125 preguntas
qs = [limpiar_consts(q) for q in llamadas(rd("data/ExamBankData.kt"), "ExamQuestion")]
out("examen.json", qs); print("examen", len(qs))

# 2. Casos y soluciones
casos = [limpiar_consts(c) for c in llamadas(rd("data/ElectoralSimulationRepository.kt"), "CaseSolution")]
out("casos.json", casos); print("casos", len(casos))

# 3. Capacitación
cap = [limpiar_consts(c) for c in llamadas(rd("ui/CapacitacionScreen.kt"), "CapacitacionTopic")]
for c in cap: c["icon"] = c.get("icon", "")
out("capacitacion.json", cap); print("capacitacion", len(cap))

# 4. Paso a paso (por rol y jornada general)
t = rd("ui/PasoAPasoScreen.kt")
def arreglar_etapas(etapas):
    for e in etapas:
        its = []
        for it in e.get("items", []):
            pos = it.get("_pos", [])
            its.append({"title": pos[0] if pos else "", "detail": pos[1] if len(pos) > 1 else "",
                        "icon": pos[2] if len(pos) > 2 else (it.get("icon") or "")})
        e["items"] = its
    return etapas
def stages_from(texto):
    p = P(texto); p.ws(); v = p.value()
    return limpiar_consts(v)
i0 = t.index("private fun getStagesForMember"); i1 = t.index("private fun getGeneralJornadaStages")
cuerpo = t[i0:i1]
roles = {}
ms = list(re.finditer(r"JrvMemberRole\.(\w+)\s*->\s*listOf\(", cuerpo))
for m in ms:
    p = P(cuerpo); p.i = m.end() - len("listOf(")
    roles[m.group(1)] = arreglar_etapas(limpiar_consts(p.value()))
resumenes = {}
s0 = t.index("private fun getMemberSummaryRole"); s1 = t.index("private fun getStagesForMember")
for m in re.finditer(r"JrvMemberRole\.(\w+)\s*->\s*(\"(?:[^\"\\]|\\.)*\")", t[s0:s1]):
    resumenes[m.group(1)] = json.loads(m.group(2))
general_txt = t[i1:]
p = P(general_txt); p.i = general_txt.index("listOf(")
general = arreglar_etapas(limpiar_consts(p.value()))
out("pasos.json", {"roles": roles, "resumenes": resumenes, "general": general})
print("pasos roles", {k: len(v) for k, v in roles.items()}, "general", len(general), "resumenes", len(resumenes))

# 5. Biblioteca (catálogo; las páginas reales son los PDF)
lib = rd("data/ElectoralPdfData.kt")
docs = []
for d in llamadas(lib, "PdfDocument"):
    d = limpiar_consts(d); d.pop("pages", None); docs.append(d)
cats = limpiar_consts(P(lib[lib.index("val categories"):]).__class__ and None) if False else None
m = re.search(r"val categories\s*=\s*listOf\((.*?)\)\s*\n", lib, re.S)
cats = re.findall(r'"((?:[^"\\]|\\.)*)"', m.group(1))
out("biblioteca.json", {"categorias": cats, "documentos": docs}); print("biblioteca", len(docs), cats[:2])

# 6. Textos legales
lg = rd("data/LegalConsentManager.kt")
leg = {}
for nombre in ["INITIAL_NOTICE_TITLE", "INITIAL_NOTICE_SUMMARY", "FULL_TERMS_AND_CONDITIONS", "FULL_PRIVACY_POLICY", "CURRENT_LEGAL_VERSION", "APP_VERSION", "LAST_UPDATED_DATE"]:
    mm = re.search(r"const val %s\s*=\s*" % nombre, lg)
    p = P(lg); p.i = mm.end(); leg[nombre] = p.value()
out("legal.json", leg); print("legal", list(leg))

# 7. PDF de la biblioteca (nombres limpios) e índice de texto por página para el buscador
import shutil, subprocess, tempfile
ASSETS = os.path.abspath(os.path.join(WEB, "..", "app", "src", "main", "assets"))
os.makedirs(os.path.join(WEB, "libros"), exist_ok=True); os.makedirs(os.path.join(WEB, "data", "indice"), exist_ok=True)
bib = json.load(open(os.path.join(WEB, "data", "biblioteca.json"), encoding="utf-8"))
for d in bib["documentos"]:
    origen = os.path.join(ASSETS, d["assetPath"])
    destino = os.path.join(WEB, "libros", d["id"] + ".pdf")
    shutil.copyfile(origen, destino)
    tmp = tempfile.mktemp(suffix=".txt")
    subprocess.run(["pdftotext", origen, tmp], check=True, stderr=subprocess.DEVNULL)
    pags = [re.sub(r"[ \t]+", " ", re.sub(r"\n\s*\n+", "\n", x)).strip() for x in open(tmp, encoding="utf-8").read().split("\f")]
    if pags and pags[-1] == "": pags.pop()
    json.dump({"paginas": pags}, open(os.path.join(WEB, "data", "indice", d["id"] + ".json"), "w", encoding="utf-8"), ensure_ascii=False)
    d["assetPath"] = "libros/" + d["id"] + ".pdf"; d["totalPaginas"] = len(pags)
out("biblioteca.json", bib); print("PDF + índice:", len(bib["documentos"]))
