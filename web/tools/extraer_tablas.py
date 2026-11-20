#!/usr/bin/env python3
"""Extrae las tablas (sinónimos, patrones, palabras vacías, correcciones) del buscador Kotlin
y genera web/js/engine-data.js. Así la versión web usa exactamente las mismas listas que la app."""
import json, os, re
AQUI = os.path.dirname(os.path.abspath(__file__))
WEB = os.path.abspath(os.path.join(AQUI, ".."))
SRC = os.path.abspath(os.path.join(WEB, "..", "app", "src", "main", "java", "com", "example", "data", "search"))
STR = r'"((?:[^"\\]|\\.)*)"'

def unesc(s):
    return s.encode("utf-8").decode("unicode_escape").encode("latin-1").decode("utf-8") if "\\" in s else s

def block(text, name):
    m = re.search(r'private val %s\b[^=]*=\s*(?:mapOf|setOf|listOf)\(' % name, text)
    if not m: raise SystemExit("no se encontró " + name)
    i = m.end(); depth = 1; instr = False
    j = i
    while depth:
        c = text[j]
        if instr:
            if c == "\\": j += 1
            elif c == '"': instr = False
        else:
            if c == '"': instr = True
            elif c == "(": depth += 1
            elif c == ")": depth -= 1
        j += 1
    return text[i:j-1]

def list_map(text, name):
    b = block(text, name); out = {}
    for m in re.finditer(STR + r'\s+to\s+listOf\((.*?)\)\s*(?:,|$)', b, re.S):
        out[unesc(m.group(1))] = [unesc(x) for x in re.findall(STR, m.group(2))]
    return out

def plain_list(text, name, local=False):
    pat = (r'val %s\b[^=]*=\s*listOf\(' if local else r'private val %s\b[^=]*=\s*listOf\(') % name
    m = re.search(pat, text)
    if not m: raise SystemExit("no se encontró " + name)
    i = m.end(); depth = 1; instr = False; j = i
    while depth:
        c = text[j]
        if instr:
            if c == "\\": j += 1
            elif c == '"': instr = False
        else:
            if c == '"': instr = True
            elif c == "(": depth += 1
            elif c == ")": depth -= 1
        j += 1
    return [unesc(x) for x in re.findall(STR, text[i:j-1])]

read = lambda p: open(os.path.join(SRC, p), encoding="utf-8").read()
norm = read("normalization/ElectoralNormalizer.kt")
syn = read("synonyms/ElectoralSynonymRegistry.kt")
ent = read("entities/ElectoralEntityExtractor.kt")

typo = {}
for m in re.finditer(STR + r'\s+to\s+' + STR, block(norm, "TYPO_MAP")):
    typo[unesc(m.group(1))] = unesc(m.group(2))
stop = [unesc(x) for x in re.findall(STR, block(norm, "STOP_WORDS"))]
data = {
    "STOP_WORDS": stop, "TYPO_MAP": typo,
    "SYNONYM_GROUPS": list_map(syn, "SYNONYM_GROUPS"),
    "ACTOR_PATTERNS": list_map(ent, "ACTOR_PATTERNS"), "OBJECT_PATTERNS": list_map(ent, "OBJECT_PATTERNS"),
    "ACTION_PATTERNS": list_map(ent, "ACTION_PATTERNS"), "PLACE_PATTERNS": list_map(ent, "PLACE_PATTERNS"),
    "MOMENT_PATTERNS": list_map(ent, "MOMENT_PATTERNS"),
}
eng = read("engine/ElectoralSearchEngine.kt")
data["OFF_TOPIC_KEYWORDS"] = plain_list(eng, "OFF_TOPIC_KEYWORDS")
data["SPORTS_KEYWORDS"] = plain_list(eng, "SPORTS_KEYWORDS")
data["ELECTORAL_TERMS"] = plain_list(eng, "electoralTerms", local=True)
for k, v in data.items(): print(k, len(v))
open(os.path.join(WEB, "js", "engine-data.js"), "w", encoding="utf-8").write(
    "// GENERADO por tools/extraer_tablas.py a partir del buscador Kotlin. No editar a mano.\n"
    "export const TABLAS = " + json.dumps(data, ensure_ascii=False, indent=1) + ";\n")
