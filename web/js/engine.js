// Buscador electoral local. Traducción fiel a JavaScript del buscador Kotlin de la app Android
// (normalización, sinónimos, extracción de entidades, puntuación y formato de respuesta).
// Funciona 100% en el navegador: sin servidor, sin internet (después de cargar la página) y sin tokens.
import { TABLAS } from "./engine-data.js";

// ---------------------------------------------------------------- Normalizador
export const stripAccents = (s) => (/^[\x00-\x7f]*$/.test(s) ? s : s.normalize("NFD").replace(/[̀-ͯ]+/g, ""));
const isWordChar = (c) => (c >= "a" && c <= "z") || (c >= "0" && c <= "9");

export function containsWholeWord(text, term) {
  const t = stripAccents(term.toLowerCase()).trim();
  if (!t) return false;
  let from = 0;
  for (;;) {
    const idx = text.indexOf(t, from);
    if (idx < 0) return false;
    const end = idx + t.length;
    const before = idx === 0 || !isWordChar(text[idx - 1]);
    const after = end >= text.length || !isWordChar(text[end]);
    if (before && after) return true;
    from = idx + 1;
  }
}
export function containsWordPrefix(text, prefix) {
  const p = stripAccents(prefix.toLowerCase()).trim();
  if (!p) return false;
  let from = 0;
  for (;;) {
    const idx = text.indexOf(p, from);
    if (idx < 0) return false;
    if (idx === 0 || !isWordChar(text[idx - 1])) return true;
    from = idx + 1;
  }
}
const cww = containsWholeWord;

const STOP_WORDS = new Set(TABLAS.STOP_WORDS);
const TYPO_MAP = new Map(Object.entries(TABLAS.TYPO_MAP));
const PHRASE_REPLACEMENTS = [
  [/\balas\s+5\b/gi, "a las 5 de la tarde"],
  [/\balas\b/gi, "a las"],
  [/\bcierre\s+de\s+votos\b/gi, "cierre de votacion"],
  [/\bcerrar\s+los\s+votos\b/gi, "cierre de votacion"],
  [/\bcerrar\s+la\s+mesa\b/gi, "cierre de votacion"],
  [/\bcontar\s+votos\b/gi, "escrutinio conteo de votos"],
  [/\bconteo\s+de\s+votos\b/gi, "escrutinio conteo de votos"],
  [/\bpersona\s+que\s+fue\s+a\s+votar\b/gi, "votante"],
  [/\bpersona\s+que\s+va\s+a\s+votar\b/gi, "votante"],
  [/\bpersona\s+que\s+quiere\s+sufragar\b/gi, "votante"],
  [/\bpersona\s+que\s+sufrago\b/gi, "votante"],
  [/\bciudadano\s+que\s+va\s+a\s+emitir\s+su\s+voto\b/gi, "votante"],
  [/\bcinco\s+de\s+la\s+tarde\b/gi, "5 pm cierre de votacion"],
  [/\b5\s*p\.?m\.?\b/gi, "5 pm"],
  [/\b7\s*a\.?m\.?\b/gi, "7 am"],
  [/\b6\s*a\.?m\.?\b/gi, "6 am"],
  [/\b(?:se\s+)?arma(?:r)?\s+(?:la\s+)?(mesa|jrv)\b/gi, "instalar la $1"],
  [/\bcomo\s+se\s+arma\b/gi, "como se instala"],
];

export function singularize(w) {
  if (w.endsWith("ces")) return w.slice(0, -3) + "z";
  if (w.endsWith("es") && (w.endsWith("nes") || w.endsWith("res") || w.endsWith("les"))) return w.slice(0, -2);
  if (w.endsWith("s") && !w.endsWith("is") && !w.endsWith("us") && w.length > 3) return w.slice(0, -1);
  return w;
}
const limpiar = (s) => s.replace(/[^a-z0-9\s]/g, " ").replace(/\s+/g, " ").trim();
export function tokenizeAndSingularize(cleanText) {
  return cleanText.replace(/[^a-z0-9\s]/g, " ").split(/\s+/).filter((x) => x.trim() !== "").map(singularize);
}
export function normalize(rawQuery) {
  let pre = rawQuery.toLowerCase();
  for (const [re, rep] of PHRASE_REPLACEMENTS) pre = pre.replace(re, rep);
  const clean = limpiar(stripAccents(pre));
  const tokens = clean.split(" ").filter((t) => t.trim() !== "");
  const corrected = tokens.map((t) => (TYPO_MAP.has(t) ? TYPO_MAP.get(t) : singularize(t)));
  const keywords = [...new Set(corrected.filter((t) => !STOP_WORDS.has(t) && t.length > 2))];
  return { raw: rawQuery.trim(), cleanedText: clean, tokens: corrected, keywords };
}

// ---------------------------------------------------------------- Sinónimos
const NORMALIZED_GROUPS = Object.values(TABLAS.SYNONYM_GROUPS).map((g) => g.map((x) => stripAccents(x.toLowerCase())));
const groupsCache = new Map();
function groupsOf(cleanTerm) {
  let r = groupsCache.get(cleanTerm);
  if (!r) {
    r = new Set();
    NORMALIZED_GROUPS.forEach((g, i) => {
      if (g.some((x) => x === cleanTerm || cww(cleanTerm, x))) r.add(i);
    });
    groupsCache.set(cleanTerm, r);
  }
  return r;
}
export function areSynonyms(a, b) {
  const ca = stripAccents(a.toLowerCase()), cb = stripAccents(b.toLowerCase());
  if (ca === cb) return true;
  const ga = groupsOf(ca);
  if (ga.size === 0) return false;
  const gb = groupsOf(cb);
  for (const i of ga) if (gb.has(i)) return true;
  return false;
}
export function findCanonicalConcepts(text) {
  const clean = stripAccents(text.toLowerCase());
  const out = new Set();
  for (const [concept, syns] of Object.entries(TABLAS.SYNONYM_GROUPS)) {
    if (syns.some((s) => cww(clean, s))) out.add(concept);
  }
  return out;
}

// ---------------------------------------------------------------- Extractor de entidades
const anyPattern = (clean, pats) => pats.some((p) => cww(clean, p));
function detectContext(clean, actors, objects, actions, moments) {
  if (objects.includes("arma_de_fuego") || actions.includes("amenaza") || actors.includes("pnc") ||
      cww(clean, "seguridad") || cww(clean, "delito")) return "seguridad_electoral";
  if (objects.includes("dui") || cww(clean, "cedula") || cww(clean, "apariencia") || cww(clean, "identidad")) return "identificacion_elector";
  if (moments.includes("instalacion") || cww(clean, "instalar") || cww(clean, "6 am")) return "instalacion_jrv";
  if (moments.includes("escrutinio") || actions.includes("contar") || objects.includes("acta") || cww(clean, "escrutinio")) return "escrutinio_conteo";
  if (objects.includes("tinta") || cww(clean, "manchado")) return "proceso_votacion";
  if (cww(clean, "discapacidad") || cww(clean, "asistido") || cww(clean, "ciego") || cww(clean, "embarazada")) return "inclusion_asistencia";
  if (actors.includes("vigilante") || objects.includes("credencial")) return "acreditacion_vigilancia";
  if (cww(clean, "permiso") || cww(clean, "trabajo") || cww(clean, "sueldo") || cww(clean, "patrono")) return "derechos_laborales";
  return "proceso_votacion";
}
function detectIntent(clean, actions) {
  const any = (...ts) => ts.some((t) => cww(clean, t));
  if (any("que dice el reglamento", "que dice la ley", "segun el codigo", "fundamento", "articulo")) return "consulta_legal_normativa";
  if (any("quien puede", "quienes pueden", "autorizados")) return "competencia_quien_puede";
  if (any("quien no puede", "quienes no pueden")) return "quien_no_puede";
  if (any("que debo hacer", "que hago", "como actuar", "cual es el procedimiento", "pasos a seguir", "como se hace")) return "procedimiento";
  if (any("se puede", "esta permitido", "es permitido", "puedo", "puede un")) return "consulta_permiso_prohibicion";
  if (any("prohibido", "prohibicion", "sancion", "castigo", "delito", "multa")) return "prohibicion_sancion";
  if (any("funciones", "papel", "atribuciones", "rol")) return "funciones_atribuciones";
  if (any("derechos")) return "derechos";
  if (actions.includes("amenaza")) return "procedimiento_emergencia";
  return "procedimiento";
}
const ARMAS_REALES = ["pistola", "revolver", "rifle", "escopeta", "fusil", "cuchillo", "machete", "arma de fuego", "arma blanca"];
export function extractEntities(normalizedText, rawQuery) {
  const clean = limpiar(stripAccents(rawQuery.toLowerCase()));
  const isArmarMesaQuery = /\b(?:se\s+)?arma(?:r)?\s+(?:la\s+)?(?:mesa|jrv)\b/.test(clean) &&
    !ARMAS_REALES.some((t) => cww(clean, t));
  const collect = (tabla, skip) => {
    const r = [];
    for (const [k, pats] of Object.entries(tabla)) {
      if (skip && skip(k)) continue;
      if (anyPattern(clean, pats)) r.push(k);
    }
    return r;
  };
  const actors = collect(TABLAS.ACTOR_PATTERNS);
  const objects = collect(TABLAS.OBJECT_PATTERNS, (k) => k === "arma_de_fuego" && isArmarMesaQuery);
  const actions = collect(TABLAS.ACTION_PATTERNS);
  const places = collect(TABLAS.PLACE_PATTERNS);
  const moments = collect(TABLAS.MOMENT_PATTERNS);
  return {
    actors, objects, actions, places, moments,
    context: detectContext(clean, actors, objects, actions, moments),
    intent: detectIntent(clean, actions),
    canonicalConcepts: findCanonicalConcepts(normalizedText.trim() === "" ? clean : normalizedText),
  };
}

// ---------------------------------------------------------------- Puntuación
const W = { TITLE: 12, DESC: 8, KEYWORD: 6, SYNONYM: 4, ACTOR: 10, ACTION: 8, OBJECT: 8, CONTEXT: 8, INTENT: 8, CATEGORY: 5, RESPONSE: 6, SYNERGY: 15 };
export const THRESHOLD_HIGH = 35.0, THRESHOLD_MEDIUM = 18.0;

export function scoreSituation(s, nq, ent) {
  let total = 0; const bd = {};
  const qText = nq.cleanedText, qTokens = nq.tokens, qKeywords = nq.keywords;
  const add = (k, v) => { bd[k] = v; total += v; };

  // 1. Título
  const cleanTitle = limpiar(stripAccents(s.titulo.toLowerCase()));
  const titleTokens = new Set(tokenizeAndSingularize(cleanTitle));
  let titleScore = 0;
  for (const kw of qKeywords) if (titleTokens.has(kw) || cww(cleanTitle, kw)) titleScore += W.TITLE;
  if (cleanTitle.length > 10 && (cleanTitle === qText || cww(qText, cleanTitle))) titleScore += 60.0;
  if (titleScore > 0) add("titulo", titleScore);

  // 2. Descripción
  if (s.descripcionSituacion.trim() !== "") {
    const cleanDesc = stripAccents(s.descripcionSituacion.toLowerCase());
    const descTokens = new Set(tokenizeAndSingularize(cleanDesc));
    let sc = 0;
    for (const kw of qKeywords) if (descTokens.has(kw) || cww(cleanDesc, kw)) sc += W.DESC;
    if (sc > 0) add("descripcion", sc);
  }

  // 3. Palabras clave
  let kwScore = 0;
  for (const kw of s.keywords) {
    const ck = stripAccents(kw.toLowerCase());
    if (qKeywords.includes(ck) || qTokens.includes(ck) || cww(qText, ck)) kwScore += W.KEYWORD;
  }
  if (kwScore > 0) add("keywords", kwScore);

  // 4. Sinónimos
  let synScore = 0;
  for (const syn of s.sinonimos) {
    const cs = stripAccents(syn.toLowerCase());
    if (cww(qText, cs) || qTokens.includes(cs) || qKeywords.some((k) => areSynonyms(k, cs))) synScore += W.SYNONYM;
  }
  if (synScore > 0) add("sinonimos", synScore);

  // 5. Actores
  const matchingActors = s.actores.filter((actor) => {
    const ca = stripAccents(actor.toLowerCase());
    return ent.actors.includes(actor) || ent.canonicalConcepts.has(actor) || cww(qText, ca) ||
      (ca.includes("vigilante") && (cww(qText, "vigilante") || cww(qText, "fiscal"))) ||
      (ca.includes("votante") && (cww(qText, "votante") || cww(qText, "elector") || cww(qText, "ciudadano"))) ||
      (ca.includes("presidente") && cww(qText, "presidente")) ||
      (ca.includes("secretario") && cww(qText, "secretario")) ||
      (ca.includes("vocal") && cww(qText, "vocal")) ||
      (ca.includes("pnc") && (cww(qText, "pnc") || cww(qText, "policia"))) ||
      (ca.includes("fiscal") && (cww(qText, "fgr") || cww(qText, "fiscalia"))) ||
      (ca.includes("patrono") && (cww(qText, "patrono") || cww(qText, "jefe") || cww(qText, "empresa") || cww(qText, "trabajo"))) ||
      (ca.includes("observador") && cww(qText, "observador")) ||
      (ca.includes("jefe de centro") && cww(qText, "jefe de centro")) ||
      (ca.includes("jem") && cww(qText, "jem")) ||
      (ca.includes("jed") && cww(qText, "jed")) ||
      (ca.includes("tse") && cww(qText, "tse"));
  });
  if (matchingActors.length) add("actor", matchingActors.length * W.ACTOR);

  // 6. Acciones
  const matchingActions = s.acciones.filter((action) => {
    const ca = stripAccents(action.toLowerCase());
    return ent.actions.includes(action) || cww(qText, ca) ||
      (ca.includes("amenaza") && (containsWordPrefix(qText, "amenaz") || cww(qText, "apunto") || cww(qText, "intimido"))) ||
      (ca.includes("ingresar") && (containsWordPrefix(qText, "ingres") || cww(qText, "entro") || cww(qText, "llego"))) ||
      (ca.includes("portar") && (containsWordPrefix(qText, "porta") || cww(qText, "lleva") || cww(qText, "tenia") || cww(qText, "saco"))) ||
      (ca.includes("votar") && (cww(qText, "votar") || cww(qText, "voto"))) ||
      (ca.includes("cotejo") && (cww(qText, "parece") || cww(qText, "foto") || cww(qText, "fotografia") || cww(qText, "rasgos"))) ||
      (ca.includes("instala") && (containsWordPrefix(qText, "instala") || cww(qText, "abrir") || cww(qText, "inicio"))) ||
      (ca.includes("cerrar") && (cww(qText, "cierre") || cww(qText, "cierra") || cww(qText, "5 pm") || cww(qText, "5 00"))) ||
      (ca.includes("romper") && (containsWordPrefix(qText, "romp") || containsWordPrefix(qText, "dan") || cww(qText, "mancho"))) ||
      (ca.includes("fotografiar") && (cww(qText, "foto") || cww(qText, "fotografia") || cww(qText, "celular") || cww(qText, "camara") || cww(qText, "grabar")));
  });
  if (matchingActions.length) add("accion", matchingActions.length * W.ACTION);

  // 7. Objetos
  const matchingObjects = s.objetos.filter((obj) => {
    const co = stripAccents(obj.toLowerCase());
    return ent.objects.includes(obj) || cww(qText, co) ||
      (cww(co, "arma") && (ent.objects.includes("arma_de_fuego") || cww(qText, "arma") || cww(qText, "pistola") || cww(qText, "revolver") || cww(qText, "cuchillo"))) ||
      (cww(co, "dui") && (cww(qText, "dui") || cww(qText, "cedula") || cww(qText, "identidad") || cww(qText, "foto") || cww(qText, "fotografia"))) ||
      (co.includes("papeleta") && (cww(qText, "papeleta") || cww(qText, "boleta"))) ||
      (co.includes("padron") && (cww(qText, "padron") || cww(qText, "lista"))) ||
      (cww(co, "tinta") && (cww(qText, "tinta") || cww(qText, "manchado"))) ||
      (cww(co, "acta") && (cww(qText, "acta") || cww(qText, "conteo"))) ||
      (cww(co, "urna") && cww(qText, "urna")) ||
      (co.includes("celular") && (cww(qText, "celular") || cww(qText, "telefono") || cww(qText, "camara")));
  });
  if (matchingObjects.length) add("objeto", matchingObjects.length * W.OBJECT);

  // 8. Contextos
  const allContexts = [...s.contextos, ...s.lugares, ...s.momentos, s.contexto].filter((c) => c.trim() !== "");
  for (const ctx of allContexts) {
    const cc = stripAccents(ctx.toLowerCase());
    if (cww(qText, cc) || cc === ent.context) { add("contexto", W.CONTEXT); break; }
  }

  // 9. Intención
  const matchingIntents = s.intenciones.filter((intent) =>
    intent === ent.intent ||
    (intent.includes("quien_puede") && (cww(qText, "quien puede") || cww(qText, "quienes pueden") || cww(qText, "autorizados"))) ||
    (intent.includes("prohibicion") && (cww(qText, "prohibido") || cww(qText, "sancion") || cww(qText, "delito") || cww(qText, "no hacer"))) ||
    (intent.includes("procedimiento") && (cww(qText, "que hacer") || cww(qText, "como se") || cww(qText, "que pasa") || cww(qText, "que se necesita") || cww(qText, "procedimiento"))));
  if (matchingIntents.length) add("intencion", W.INTENT);

  // 10. Categoría
  const cleanCat = stripAccents(s.categoria.toLowerCase()), cleanSub = stripAccents(s.subcategoria.toLowerCase());
  if (cww(qText, cleanCat) || (cleanSub.trim() !== "" && cww(qText, cleanSub))) add("categoria", W.CATEGORY);

  // 11. Contenido de la respuesta
  let rc = s.respuesta.situacion + " ";
  s.respuesta.procedimiento.forEach((x) => (rc += x + " "));
  s.respuesta.noHacer.forEach((x) => (rc += x + " "));
  if (s.respuesta.advertencia != null) rc += s.respuesta.advertencia + " ";
  const cleanResp = stripAccents(rc.toLowerCase());
  const respTokens = new Set(tokenizeAndSingularize(cleanResp));
  let respScore = 0;
  for (const kw of qKeywords) if (respTokens.has(kw) || cww(cleanResp, kw)) respScore += 2.0;
  if (respScore > 0) add("contenido_respuesta", Math.min(respScore, W.RESPONSE * 2));

  // 12. Referencia legal
  const refs = [...s.respuesta.fundamentosDetallados.map((f) => `${f.ley} ${f.articulo} ${f.incisoOrNumeral}`), ...s.respuesta.fundamento];
  for (const ref of refs) {
    const cr = stripAccents(ref.toLowerCase());
    const arts = cr.split(" ").filter((t) => t.length > 2);
    if (arts.some((t) => /[0-9]/.test(t) && cww(qText, t))) { add("referencia_legal", 12.0); break; }
  }

  // 13. Sinergia
  if (matchingActors.length && matchingActions.length && matchingObjects.length) add("sinergia_elementos", W.SYNERGY);

  // 14. Reglas específicas por ficha
  if (s.id === "ARM-010" && cww(qText, "quien puede") && cww(qText, "arma")) add("bonus_quien_puede", 25.0);
  if (s.id === "ARM-001" && cww(qText, "vigilante") && cww(qText, "arma") && !containsWordPrefix(qText, "amenaz")) add("bonus_vigilante_arma", 20.0);
  if (s.id === "ARM-002" && (cww(qText, "votante") || cww(qText, "elector")) && containsWordPrefix(qText, "amenaz")) add("bonus_amenaza_votante", 25.0);
  if (s.id === "DUI-002" && (cww(qText, "foto") || cww(qText, "fotografia")) && (cww(qText, "parece") || cww(qText, "rasgos"))) add("bonus_foto_dui", 35.0);
  if ((s.id === "INST-001" || s.id === "INS-001") && containsWordPrefix(qText, "instala") && (cww(qText, "mesa") || cww(qText, "jrv") || cww(qText, "necesita"))) add("bonus_instalacion_mesa", 30.0);
  if (s.id === "ESC-001" &&
      ((cww(qText, "cierre") && (cww(qText, "voto") || cww(qText, "votacion") || cww(qText, "mesa"))) ||
       (cww(qText, "5") && (cww(qText, "cierre") || cww(qText, "escrutinio") || cww(qText, "tarde") || cww(qText, "pm"))))) add("bonus_cierre_votacion", 35.0);

  return { situation: s, totalScore: total, scoreBreakdown: bd };
}

// ---------------------------------------------------------------- Carga del banco de conocimientos
const strList = (a) => (Array.isArray(a) ? a.map((x) => (x == null ? "" : String(x))).filter((x) => x.trim() !== "") : []);
const first = (...xs) => xs.find((x) => x !== undefined && x !== null);
const optStr = (o, k, d = "") => (o && o[k] !== undefined && o[k] !== null ? String(o[k]) : d);

export function construirSituaciones(raw) {
  const lista = Array.isArray(raw) ? raw : first(raw.situaciones, raw.situacion, raw.registros, raw.banco, raw.items) || [];
  return lista.filter((o) => o && typeof o === "object").map((obj, i) => {
    const descripcion = optStr(obj, "descripcion_situacion") || optStr(obj, "descripcion", "");
    const sinonimosObj = first(obj.sinonimos, obj.synonyms);
    const sinonimos = [];
    if (sinonimosObj && !Array.isArray(sinonimosObj) && typeof sinonimosObj === "object") {
      for (const [k, v] of Object.entries(sinonimosObj)) { sinonimos.push(k); sinonimos.push(...strList(v)); }
    } else sinonimos.push(...strList(sinonimosObj));
    const r = first(obj.respuesta, obj.response) || null;
    const situacion = r ? (optStr(r, "situacion_detectada") || optStr(r, "respuesta") || optStr(r, "situacion", descripcion)) : descripcion;
    const procedimiento = strList(first(r?.que_hacer, r?.procedimiento, r?.pasos));
    const noHacer = strList(first(r?.que_no_hacer, r?.prohibiciones, r?.no_hacer));
    let advertencia = null;
    if (r && r.advertencia !== undefined && r.advertencia !== null) {
      const a = String(r.advertencia);
      advertencia = a.trim() !== "" && a.toLowerCase() !== "null" ? a : null;
    }
    const quienInterviene = strList(r?.quien_interviene), quienPuede = strList(r?.quien_puede),
      quienNoPuede = strList(r?.quien_no_puede), condiciones = strList(r?.condiciones);
    const autorizaciones = quienPuede.length || quienNoPuede.length || condiciones.length || quienInterviene.length
      ? { quienPuede, quienNoPuede, quienInterviene, condiciones } : null;

    const fundamentosDetallados = [], fundamento = [];
    const fundArr = first(r?.fundamento_legal, r?.fundamentos, r?.articulos);
    if (Array.isArray(fundArr)) for (const f of fundArr) {
      if (f && typeof f === "object") {
        const ley = optStr(f, "ley", "Código Electoral");
        if (Array.isArray(f.articulos)) {
          for (const a of f.articulos) if (a && typeof a === "object") {
            const num = optStr(a, "numero", ""), desc = optStr(a, "fuente", "");
            fundamentosDetallados.push({ ley, articulo: `Art. ${num}`, incisoOrNumeral: "", descripcion: desc });
            fundamento.push(`${ley} — Art. ${num}: ${desc}`);
          }
        } else {
          const artNum = optStr(f, "articulo", optStr(f, "numero", "")), desc = optStr(f, "descripcion", optStr(f, "fuente", ""));
          if (artNum.trim() !== "" || desc.trim() !== "") {
            fundamentosDetallados.push({ ley, articulo: /^art/i.test(artNum) ? artNum : `Art. ${artNum}`, incisoOrNumeral: "", descripcion: desc });
            fundamento.push(`${ley} — ${artNum}: ${desc}`);
          }
        }
      } else if (f != null && String(f).trim() !== "") fundamento.push(String(f));
    }

    const fuentesDocumentales = [], fuentes = [];
    const fArr = first(r?.fuentes, r?.sources);
    if (Array.isArray(fArr)) for (const f of fArr) {
      if (f && typeof f === "object") {
        const nombre = optStr(f, "nombre", "TSE"), tipo = optStr(f, "tipo", "Oficial");
        const paginaStr = optStr(f, "pagina", ""), seccion = optStr(f, "seccion", "");
        const pdfDocId = optStr(f, "pdf_doc_id", "").trim() !== "" ? optStr(f, "pdf_doc_id") : "codigo_electoral_decreto_413";
        const digits = paginaStr.replace(/\D/g, "");
        const pagina = digits !== "" ? parseInt(digits, 10) : 1;
        fuentesDocumentales.push({
          documento: nombre, tipoDocumento: tipo, pagina,
          articuloRelacionado: seccion.trim() !== "" ? seccion : null, seccion: seccion.trim() !== "" ? seccion : null, pdfDocId,
        });
        fuentes.push(`${nombre} (${tipo})${seccion.trim() !== "" ? " — " + seccion : ""}${paginaStr.trim() !== "" ? " — Pág. " + paginaStr : ""}`);
      } else if (f != null && String(f).trim() !== "") fuentes.push(String(f));
    }
    const fd = fuentesDocumentales[0];
    const contextos = strList(first(obj.contextos, obj.contexto));
    const prio = optStr(obj, "nivel_prioridad", "normal").toLowerCase();
    const estado = optStr(obj, "estado", "vigente").toLowerCase();
    return {
      id: optStr(obj, "id", `SIT-${i}`),
      categoria: optStr(obj, "categoria") || optStr(obj, "category", "General"),
      subcategoria: optStr(obj, "subcategoria") || optStr(obj, "subcategory", ""),
      titulo: optStr(obj, "titulo") || optStr(obj, "title", ""),
      descripcionSituacion: descripcion,
      keywords: strList(first(obj.palabras_clave, obj.keywords, obj.tags)),
      sinonimos,
      actores: strList(first(obj.actores, obj.actors)),
      acciones: strList(first(obj.acciones, obj.actions)),
      objetos: strList(first(obj.objetos, obj.objects)),
      lugares: contextos, momentos: contextos, contextos,
      intenciones: strList(first(obj.intenciones, obj.intents)),
      contexto: contextos[0] ?? "",
      prioridadJerarquica: prio === "alta" ? 1 : 2,
      estado: estado === "obsoleto" ? "OBSOLETO" : estado === "revisar" ? "REVISAR" : "VIGENTE",
      respuesta: {
        situacion, fundamento, fundamentosDetallados, procedimiento, noHacer, autorizaciones,
        fuentes, fuentesDocumentales, advertencia,
        primaryPdfDocId: fd ? fd.pdfDocId : "codigo_electoral_decreto_413", primaryPdfPage: fd ? fd.pagina : 1,
      },
      respuestasRelacionadas: strList(first(obj.situaciones_relacionadas, obj.relacionadas, obj.related)),
    };
  });
}

// ---------------------------------------------------------------- Formato de la respuesta (texto) y buscador
const MSG_OFF_TOPIC = `ℹ️ **Ámbito de Consulta:**
Asistente Electoral SV está diseñado exclusivamente para consultas electorales, procedimientos de votación, normativa electoral, funciones de organismos electorales (JRV, JED, JEM, TSE, Fiscalía Electoral, PNC) y temas jurídicos electorales de la República de El Salvador.

Por favor formule una pregunta relacionada con el proceso electoral.`;
const upperFirst = (s) => (s.length ? s[0].toUpperCase() + s.slice(1) : s);

export function formatStructuredResponse(sit, level) {
  const resp = sit.respuesta; let sb = "";
  sb += level === "HIGH" ? "✅ **Coincidencia encontrada**\n\n" : level === "MEDIUM" ? "ℹ️ **Coincidencia probable**\n\n" : "⚠️ **Coincidencia baja**\n\n";
  sb += `Tema: ${sit.titulo}\n\n`;
  sb += "SITUACIÓN DETECTADA\n" + `${resp.situacion}\n\n`;
  sb += "¿QUÉ HACER?\n";
  resp.procedimiento.forEach((p, i) => { sb += `${i + 1}. ${p.replace(/^\d+\.\s*/, "")}\n`; });
  sb += "\n";
  if (resp.noHacer.length) {
    sb += "¿QUÉ NO HACER?\n";
    resp.noHacer.forEach((n) => { sb += `- No ${n.replace(/^(NO\s*|•\s*)/i, "")}\n`; });
    sb += "\n";
  }
  const a = resp.autorizaciones;
  if (a) {
    if (a.quienPuede.length) { sb += "¿QUIÉN PUEDE?\n"; a.quienPuede.forEach((x) => (sb += `• ${x}\n`)); sb += "\n"; }
    if (a.quienNoPuede.length) { sb += "¿QUIÉN NO PUEDE?\n"; a.quienNoPuede.forEach((x) => (sb += `• ${x}\n`)); sb += "\n"; }
  }
  let intervienen = [];
  if (a && a.quienInterviene.length) intervienen.push(...a.quienInterviene);
  if (intervienen.length === 0 && (!a || a.quienPuede.length === 0) && sit.actores.length) intervienen.push(...sit.actores.map(upperFirst));
  if (intervienen.length) { sb += "¿QUIÉN INTERVIENE?\n"; [...new Set(intervienen)].forEach((x) => (sb += `• ${x}\n`)); sb += "\n"; }
  if (a && a.condiciones.length) { sb += "CONDICIONES\n"; a.condiciones.forEach((x) => (sb += `• ${x}\n`)); sb += "\n"; }
  sb += "FUNDAMENTO LEGAL\n";
  if (resp.fundamentosDetallados.length) {
    resp.fundamentosDetallados.forEach((f) => {
      sb += `• ${f.ley} — ${f.articulo}${f.incisoOrNumeral.trim() !== "" ? ` (${f.incisoOrNumeral})` : ""}\n`;
      if (f.descripcion.trim() !== "") sb += `  ${f.descripcion}\n`;
    });
  } else if (resp.fundamento.length) resp.fundamento.forEach((f) => (sb += `• ${f}\n`));
  else sb += "• Normativa e instructivos vigentes del Tribunal Supremo Electoral.\n";
  sb += "\n";
  if (resp.advertencia && resp.advertencia.trim() !== "") sb += "ADVERTENCIA\n" + `⚠️ ${resp.advertencia}\n\n`;
  sb += "FUENTES\n";
  if (resp.fuentesDocumentales.length) {
    resp.fuentesDocumentales.forEach((f) => {
      const pag = f.pagina != null && f.pagina > 0 ? ` (Pág. ${f.pagina})` : "";
      const art = f.articuloRelacionado && f.articuloRelacionado.trim() !== "" ? ` — ${f.articuloRelacionado}` : "";
      sb += `• ${f.documento}${pag}${art}\n`;
    });
  } else if (resp.fuentes.length) resp.fuentes.forEach((f) => (sb += `• ${f}\n`));
  else sb += "• Tribunal Supremo Electoral (TSE)\n";
  sb += "\nEsta respuesta proviene exclusivamente del banco oficial de situaciones JSON.";
  return sb.trim();
}

function buildAmbiguityMessage(options) {
  let sb = "ℹ️ **Coincidencia probable**\n\n";
  sb += "Encontré varias opciones en el banco de conocimientos. Por favor seleccione la que mejor describe su caso:\n\n";
  options.forEach((o, i) => { sb += `${i + 1}. **${o.titulo}**\n`; sb += `   ${o.respuesta.situacion}\n\n`; });
  sb += "Toque o escriba el número de opción para ver el procedimiento exacto.";
  return sb.trim();
}
function buildLowMatchMessage(query) {
  let sb = "⚠️ No se encontró una respuesta relacionada con esta consulta en el banco de conocimiento.\n\n";
  sb += `Consulta recibida:\n"${query}"\n\n`;
  sb += "Para proteger la certeza jurídica electoral, la aplicación no inventa respuestas que no existan en su banco oficial.\n\n";
  sb += "💡 **Sugerencia:**\n";
  sb += 'Por favor, formule su pregunta utilizando términos del proceso electoral (por ejemplo: "instalación de mesa", "votante con DUI", "vigilante portando arma", "quién puede portar arma", "cierre de votación", "permisos de trabajo art 113").';
  return sb.trim();
}

export class MotorElectoral {
  constructor(situaciones) { this.situaciones = situaciones; }
  findById(id) { return this.situaciones.find((s) => s.id.toLowerCase() === id.toLowerCase()) || null; }

  isElectoralQuery(clean, ignorePartido = false) {
    return TABLAS.ELECTORAL_TERMS.some((t) => !(ignorePartido && (t === "partido" || t === "partidos")) && cww(clean, t));
  }

  search(rawQuery) {
    const trimmed = rawQuery.trim();
    if (trimmed === "") {
      return { matchLevel: "LOW", topSituation: null, score: 0, scoreBreakdown: {}, clarificationOptions: [], isOffTopic: false, offTopicMessage: null,
        formattedResponse: "Por favor, ingrese una pregunta o describa una situación electoral para realizar la consulta." };
    }
    const cleanRaw = limpiar(stripAccents(trimmed.toLowerCase()));
    const isSports = TABLAS.SPORTS_KEYWORDS.some((k) => cww(cleanRaw, k));
    if (TABLAS.OFF_TOPIC_KEYWORDS.some((k) => cww(cleanRaw, k)) && !this.isElectoralQuery(cleanRaw, isSports)) {
      return { matchLevel: "LOW", topSituation: null, score: 0, scoreBreakdown: {}, clarificationOptions: [], isOffTopic: true,
        offTopicMessage: MSG_OFF_TOPIC, formattedResponse: MSG_OFF_TOPIC };
    }
    const nq = normalize(trimmed);
    const ent = extractEntities(nq.cleanedText, trimmed);
    const scored = this.situaciones.filter((s) => s.estado !== "OBSOLETO")
      .map((s) => scoreSituation(s, nq, ent))
      .sort((a, b) => b.totalScore - a.totalScore || a.situation.prioridadJerarquica - b.situation.prioridadJerarquica);
    const top = scored[0], second = scored[1];
    const topScore = top ? top.totalScore : 0, secondScore = second ? second.totalScore : 0;
    const ambiguous = top && second && topScore >= THRESHOLD_MEDIUM && secondScore >= THRESHOLD_MEDIUM &&
      topScore - secondScore < 5.0 && top.situation.id !== second.situation.id;

    if (ambiguous) {
      const options = scored.slice(0, 3).map((x) => x.situation);
      return { matchLevel: "MEDIUM", topSituation: top.situation, score: topScore, scoreBreakdown: top.scoreBreakdown,
        clarificationOptions: options, isOffTopic: false, offTopicMessage: null, formattedResponse: buildAmbiguityMessage(options) };
    }
    if (top && topScore >= THRESHOLD_HIGH) {
      const related = top.situation.respuestasRelacionadas.map((id) => this.findById(id)).filter(Boolean);
      return { matchLevel: "HIGH", topSituation: top.situation, score: topScore, scoreBreakdown: top.scoreBreakdown,
        clarificationOptions: related.slice(0, 3), isOffTopic: false, offTopicMessage: null,
        formattedResponse: formatStructuredResponse(top.situation, "HIGH") };
    }
    if (top && topScore >= THRESHOLD_MEDIUM) {
      const others = scored.slice(1).filter((x) => x.totalScore >= THRESHOLD_MEDIUM).map((x) => x.situation).slice(0, 3);
      return { matchLevel: "MEDIUM", topSituation: top.situation, score: topScore, scoreBreakdown: top.scoreBreakdown,
        clarificationOptions: others, isOffTopic: false, offTopicMessage: null,
        formattedResponse: formatStructuredResponse(top.situation, "MEDIUM") };
    }
    return { matchLevel: "LOW", topSituation: null, score: topScore, scoreBreakdown: {}, clarificationOptions: [],
      isOffTopic: false, offTopicMessage: null, formattedResponse: buildLowMatchMessage(trimmed) };
  }
}
