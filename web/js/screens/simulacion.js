// Simulación: casos y soluciones + examen electoral de 25 preguntas; y el test completo de 125 (desde Ajustes).
import { h, shuffle, LETRAS } from "../ui.js";
import { tarjetaPregunta, revision } from "./preguntas.js";

// Misma selección que la app: 25 preguntas al azar, repartidas entre las categorías.
export function elegir25(todas) {
  const grupos = new Map();
  todas.forEach((q) => grupos.set(q.category, [...(grupos.get(q.category) || []), q]));
  const cats = shuffle([...grupos.keys()]);
  const porCat = Math.max(1, Math.floor(25 / Math.max(cats.length, 1)));
  const sel = [];
  for (const c of cats) sel.push(...shuffle(grupos.get(c)).slice(0, porCat));
  if (sel.length < 25) { const resto = shuffle(todas.filter((q) => !sel.includes(q))); sel.push(...resto.slice(0, 25 - sel.length)); }
  return shuffle(sel).slice(0, 25);
}

export function renderSimulacion(ctx, root) {
  const casos = ctx.datos.casos;
  const e = ctx.estadoSim;
  const cont = h("div", { class: "content pad" });
  root.replaceChildren(cont);
  const pintar = () => {
    const tabs = h("div", { class: "tabs" },
      h("button", { class: e.tab === "casos" ? "on" : "", onclick: () => { e.tab = "casos"; e.caso = null; pintar(); } }, "Casos y soluciones"),
      h("button", { class: e.tab === "examen" ? "on" : "", onclick: () => { e.tab = "examen"; pintar(); } }, "Examen Electoral"));
    cont.replaceChildren(h("div", { class: "eyebrow" }, "MÓDULO DE TEST"), h("div", { class: "h1" }, "Test y Examen Electoral"), tabs, e.tab === "casos" ? vistaCasos() : vistaExamen());
    cont.scrollTop = 0;
  };

  // ---- Casos y soluciones
  function vistaCasos() {
    if (e.caso) return detalleCaso(casos.find((c) => c.id === e.caso));
    const etapas = ["Todos", ...new Set(casos.map((c) => c.stage))];
    const lista = h("div");
    const buscador = h("input", { type: "search", placeholder: "Buscar caso por palabra clave (JRV, PNC, DUI...)", value: e.busqueda, "aria-label": "Buscar caso" });
    const filtrar = () => {
      const q = buscador.value.toLowerCase();
      e.busqueda = buscador.value;
      const res = casos.filter((c) => (e.etapa === "Todos" || c.stage === e.etapa) &&
        (!q || [c.title, c.situationDescription, c.whatShouldBeDone, c.stage, ...c.entitiesInvolved.map((x) => x.entityName)].join(" ").toLowerCase().includes(q)));
      lista.replaceChildren(...(res.length ? res.map((c) => h("div", { class: "card" },
        h("div", { class: "eyebrow" }, c.stage.toUpperCase()), h("h3", { style: { margin: "2px 0 6px" } }, c.title),
        h("p", { class: "muted", style: { margin: "0 0 8px" } }, c.situationDescription),
        h("p", { class: "legal", style: { margin: "0 0 8px" } }, "Entidades: " + c.entitiesInvolved.map((x) => x.entityName).join(", ")),
        h("button", { class: "btn sm", onclick: () => { e.caso = c.id; pintar(); } }, "Ver Caso y Solución"))) : [h("p", { class: "muted" }, "No hay casos que coincidan con la búsqueda.")]));
    };
    buscador.addEventListener("input", filtrar);
    const chips = h("div", { class: "chips scroll" }, etapas.map((x) => h("button", { class: "chip" + (e.etapa === x ? " on" : ""), onclick: () => { e.etapa = x; pintar(); } }, x)));
    filtrar();
    return h("div", {}, buscador, h("div", { style: { height: "10px" } }), chips, lista);
  }
  function detalleCaso(c) {
    const sec = (cls, titulo, ...hijos) => h("div", { class: "blk " + cls }, h("h3", {}, titulo), ...hijos);
    return h("div", {},
      h("button", { class: "btn ghost sm", onclick: () => { e.caso = null; pintar(); } }, "← Volver al listado de casos"),
      h("div", { class: "card", style: { marginTop: "10px" } }, h("div", { class: "eyebrow" }, c.stage.toUpperCase()), h("h2", { style: { margin: "4px 0 8px", fontSize: "1.1rem" } }, c.title)),
      sec("sit", "SITUACIÓN", h("p", { style: { margin: 0 } }, c.situationDescription), c.contextDescription && h("p", { class: "muted", style: { margin: "6px 0 0" } }, "Contexto: " + c.contextDescription)),
      sec("", "ENTIDADES INVOLUCRADAS", h("ul", {}, c.entitiesInvolved.map((x) => h("li", {}, h("b", {}, x.entityName), x.specificRole && h("div", { class: "muted" }, "Función: " + x.specificRole), x.whatShouldDo && h("div", {}, x.whatShouldDo))))),
      sec("", "¿QUÉ SE DEBE HACER?", h("p", { style: { margin: 0 } }, c.whatShouldBeDone)),
      sec("si", "PROCEDIMIENTO PASO A PASO", h("ol", {}, c.stepByStepProcedure.map((p) => h("li", {}, p.replace(/^\d+\.\s*/, ""))))),
      sec("no", "ACCIONES QUE NO SE DEBEN HACER", h("ul", {}, c.actionsNotToDo.map((p) => h("li", {}, p)))),
      sec("sit", "SOLUCIÓN CORRECTA", h("p", { style: { margin: 0 } }, c.correctSolution)),
      sec("", "¿POR QUÉ ESA SOLUCIÓN ES LA CORRECTA?", h("p", { style: { margin: 0 } }, c.whySolutionIsCorrect)),
      sec("ley", "FUNDAMENTO LEGAL / ELECTORAL", h("p", { style: { margin: 0 } }, c.legalNormativeRef), h("p", { class: "legal" }, "Fuente en Biblioteca: " + c.libraryDocumentRef)),
      h("div", { class: "actions" }, h("button", { class: "btn", onclick: () => ctx.preguntar(c.title) }, "💬 Consultar este caso con el Asistente")));
  }

  // ---- Examen de 25
  function vistaExamen() {
    const x = e.examen;
    if (!x) return h("div", { class: "card" },
      h("div", { class: "eyebrow" }, "EXAMEN ELECTORAL"), h("h3", { style: { margin: "4px 0 8px" } }, "25 preguntas al azar del banco de 125"),
      h("p", {}, "Cada intento elige 25 preguntas distintas, repartidas entre los temas. Se aprueba con 70% o más (18 de 25)."),
      h("button", { class: "btn", onclick: () => { e.examen = { preguntas: elegir25(ctx.datos.examen), i: 0, resp: {}, fin: false }; pintar(); } }, "Comenzar examen"));
    if (x.fin) return resultados(x);
    const q = x.preguntas[x.i], respondidas = Object.keys(x.resp).length;
    return h("div", {},
      h("div", { class: "bar" }, h("i", { style: { width: (respondidas / x.preguntas.length * 100) + "%" } })),
      tarjetaPregunta(ctx, q, { numero: x.i + 1, total: x.preguntas.length, elegida: x.resp[q.id], inmediato: false, onElegir: (i) => { x.resp[q.id] = i; pintar(); } }),
      h("div", { class: "row2" },
        h("button", { class: "btn sec sm", disabled: x.i === 0, onclick: () => { x.i--; pintar(); } }, "◀ Anterior"),
        x.i < x.preguntas.length - 1 ? h("button", { class: "btn sm", onclick: () => { x.i++; pintar(); } }, "Siguiente ▶")
          : h("button", { class: "btn sm", onclick: () => { if (respondidas < x.preguntas.length && !confirm(`Te faltan ${x.preguntas.length - respondidas} preguntas sin responder. ¿Finalizar de todos modos?`)) return; x.fin = true; pintar(); } }, "Finalizar examen"),
        h("button", { class: "btn ghost sm", onclick: () => { if (confirm("¿Reiniciar el examen?")) { e.examen = null; pintar(); } } }, "Reiniciar")));
  }
  function resultados(x) {
    const items = x.preguntas.map((q) => ({ q, elegida: x.resp[q.id], sin: x.resp[q.id] == null, ok: x.resp[q.id] === q.correctOptionIndex }));
    const buenas = items.filter((i) => i.ok).length, malas = items.length - buenas, pct = Math.round(buenas / items.length * 100);
    const aprobado = pct >= 70;
    return h("div", {},
      h("div", { class: "card", style: { textAlign: "center" } }, h("div", { class: "eyebrow" }, "CALIFICACIÓN"), h("div", { class: "score", style: { color: aprobado ? "#059669" : "#dc2626" } }, pct + "%"),
        h("b", {}, aprobado ? "✅ APROBADO" : "❌ NO APROBADO"), h("p", { class: "muted" }, `Aciertos ${buenas}/25 · Fallos ${malas}/25 (se aprueba con 18 de 25)`)),
      h("div", { class: "row2" },
        h("button", { class: "btn sm", onclick: () => { e.examen = { preguntas: elegir25(ctx.datos.examen), i: 0, resp: {}, fin: false }; pintar(); } }, "Realizar otro examen con preguntas diferentes"),
        h("button", { class: "btn ghost sm", onclick: () => { x.soloErrores = !x.soloErrores; pintar(); } }, x.soloErrores ? "Ver todas" : `Solo errores (${malas})`)),
      h("div", { class: "eyebrow", style: { margin: "14px 2px 8px" } }, "REVISIÓN DETALLADA DE PREGUNTAS"), revision(ctx, items, x.soloErrores));
  }
  pintar();
}

// ---- Test completo de 125 preguntas (estudio con corrección inmediata)
export function renderTest125(ctx, root, volver) {
  const preguntas = ctx.datos.examen;
  const e = ctx.estadoTest;
  const cont = h("div", { class: "content pad" });
  root.replaceChildren(cont);
  const pintar = () => {
    cont.replaceChildren();
    cont.append(h("button", { class: "btn ghost sm", onclick: volver }, "← Volver a Ajustes"),
      h("div", { class: "eyebrow", style: { marginTop: "10px" } }, "TEST DE CONOCIMIENTOS ELECTORALES"), h("div", { class: "h1" }, "Banco Oficial Completo · 125 Preguntas"));
    if (e.fin) { cont.append(resultado()); return; }
    const q = preguntas[e.i], n = Object.keys(e.resp).length;
    cont.append(h("div", { class: "muted" }, `Respondidas: ${n}/${preguntas.length}`), h("div", { class: "bar" }, h("i", { style: { width: (n / preguntas.length * 100) + "%" } })));
    cont.append(tarjetaPregunta(ctx, q, { numero: e.i + 1, total: preguntas.length, elegida: e.resp[q.id], inmediato: true, onElegir: (i) => { e.resp[q.id] = i; pintar(); } }));
    cont.append(h("div", { class: "row2" },
      h("button", { class: "btn sec sm", disabled: e.i === 0, onclick: () => { e.i--; pintar(); } }, "◀ Anterior"),
      h("button", { class: "btn sec sm", disabled: e.i === preguntas.length - 1, onclick: () => { e.i++; pintar(); } }, "Siguiente ▶"),
      h("button", { class: "btn sm", onclick: () => { e.fin = true; pintar(); } }, "Finalizar y ver resultado")));
    cont.append(h("div", { class: "eyebrow", style: { margin: "16px 2px 8px" } }, `ÍNDICE DE PREGUNTAS (1 A ${preguntas.length})`),
      h("div", { class: "grid" }, preguntas.map((p, i) => h("button", { class: (e.resp[p.id] != null ? "done " : "") + (i === e.i ? "cur" : ""), "aria-label": `Pregunta ${i + 1}`, onclick: () => { e.i = i; pintar(); cont.scrollTop = 0; } }, i + 1))));
  };
  function resultado() {
    const items = preguntas.map((q) => ({ q, elegida: e.resp[q.id], sin: e.resp[q.id] == null, ok: e.resp[q.id] === q.correctOptionIndex }));
    const buenas = items.filter((i) => i.ok).length, resp = items.filter((i) => !i.sin).length, pct = resp ? Math.round(buenas / resp * 100) : 0;
    return h("div", {},
      h("div", { class: "card", style: { textAlign: "center" } }, h("div", { class: "eyebrow" }, "RESULTADO DEL TEST"), h("div", { class: "score" }, pct + "%"),
        h("p", { class: "muted" }, `${buenas} correctas de ${resp} respondidas (${preguntas.length - resp} sin responder)`)),
      h("div", { class: "row2" }, h("button", { class: "btn sm", onclick: () => { e.i = 0; e.resp = {}; e.fin = false; pintar(); } }, "Reiniciar test"),
        h("button", { class: "btn ghost sm", onclick: () => { e.fin = false; pintar(); } }, "Seguir respondiendo"),
        h("button", { class: "btn ghost sm", onclick: () => { e.soloErrores = !e.soloErrores; pintar(); } }, e.soloErrores ? "Ver todas" : "Solo errores")),
      h("div", { class: "eyebrow", style: { margin: "14px 2px 8px" } }, "REVISIÓN"), revision(ctx, items.filter((i) => !i.sin), e.soloErrores));
  }
  pintar();
}
