// Pantalla principal: consulta al asistente (buscador local del banco de conocimientos).
import { h, lista, mdInline, toast } from "../ui.js";
import { vozSoportada, textoParaVoz } from "../voice.js";
import { store } from "../store.js";

const BANCO_SUGERENCIAS = [
  "¿Cuáles son las funciones del Presidente de JRV?", "¿Cuáles son las funciones del Secretario de JRV?",
  "¿Cuáles son las funciones del Primer Vocal de JRV?", "¿Cuáles son las funciones del Segundo y Tercer Vocal?",
  "¿Con cuántos miembros se instala la JRV y qué pasa si faltan miembros?", "¿A qué hora se instala la mesa electoral y a qué hora abre la votación?",
  "¿Qué permisos de trabajo con goce de sueldo garantiza el Art. 113 del Código Electoral?", "¿Cuál es la diferencia entre voto válido, voto nulo y voto impugnado?",
  "¿Cómo se realiza el escrutinio preliminar paso a paso a partir de las 5:00 PM?", "¿Qué derechos y prohibiciones tienen los vigilantes de partidos políticos?",
  "¿Cuáles son las atribuciones del Tribunal Supremo Electoral (TSE)?", "¿Quiénes están autorizados a portar armas y cómo aplica el Art. 290 del Código Electoral?",
  "¿Cómo protege la ley LEIV (Art. 55) a las mujeres en el proceso electoral?", "¿Qué sanciones contempla la Ley Seca (Art. 284) y los delitos electorales?",
  "¿Qué hacer si un ciudadano no presenta su DUI o no aparece en el padrón?", "¿Cómo se garantiza el voto asistido para personas con discapacidad o adultos mayores?",
  "¿Qué funciones cumplen los Jefes de Centro y Supervisores del TSE?", "¿Qué papel cumple la Policía Nacional Civil (PNC) y la Fiscalía Electoral?",
  "¿Cómo se califica el voto por coalición en el escrutinio preliminar?", "¿Qué son y cómo funcionan las JED y JEM en cada departamento y municipio?",
];
const fuzzy = (kw, target) => kw.length >= 4 && target.includes(kw.substring(0, Math.floor(kw.length * 0.75)));
// Misma lógica de sugerencias "¿Quisiste decir...?" que la app Android.
export function sugerenciasInteligentes(raw) {
  const q = raw.toLowerCase().trim();
  if (!q) return [];
  const kws = q.split(/[ ?¿,.\-_]/).filter((k) => k.length >= 3);
  const out = [];
  for (const s of BANCO_SUGERENCIAS) {
    const sl = s.toLowerCase(); let score = 0;
    for (const kw of kws) { if (sl.includes(kw)) score += 2; else if (fuzzy(kw, sl)) score += 1; }
    if (score > 0) out.push(s);
    if (out.length >= 3) break;
  }
  if (!out.length) {
    const has = (...x) => x.some((k) => q.includes(k));
    if (has("presi", "precidente", "presidete", "jefe de mesa")) out.push("¿Cuáles son las funciones del Presidente de JRV?");
    if (has("secre", "cecretario", "secretaro", "actas")) out.push("¿Cuáles son las funciones del Secretario de JRV?");
    if (has("vocal", "bocal", "vocales", "tinta")) out.push("¿Cuáles son las funciones del Primer Vocal de JRV?", "¿Cuáles son las funciones del Segundo y Tercer Vocal?");
    if (has("tse", "tribunal", "magistrado", "jem", "jed")) out.push("¿Cuáles son las atribuciones del Tribunal Supremo Electoral (TSE)?");
    if (has("arma", "pistola", "cuchillo", "seguridad", "pnc")) out.push("¿Quiénes están autorizados a portar armas y cómo aplica el Art. 290 del Código Electoral?");
    if (has("alcohol", "cerveza", "borracho", "ley seca")) out.push("¿Qué sanciones contempla la Ley Seca (Art. 284) y los delitos electorales?");
    if (has("trabajo", "jefe", "patron", "patrono", "sueldo", "113")) out.push("¿Qué permisos de trabajo con goce de sueldo garantiza el Art. 113 del Código Electoral?");
    if (has("conteo", "escrutinio", "papeletas", "acta", "5 pm")) out.push("¿Cómo se realiza el escrutinio preliminar paso a paso a partir de las 5:00 PM?");
  }
  return [...new Set(out)].slice(0, 3);
}

export function renderChat(ctx, root) {
  const st = ctx.chat;
  const scroll = h("div", { class: "content pad", id: "chatscroll" });
  const campo = h("textarea", { rows: 1, placeholder: "Escribe tu consulta electoral…", "aria-label": "Consulta electoral", maxlength: 600 });
  const pill = h("div", { class: "voicepill hidden", role: "status" });
  const mic = h("button", { class: "mic", "aria-label": "Consultar por voz", title: "Consultar por voz" }, "🎤");
  const enviar = h("button", { class: "send", "aria-label": "Enviar consulta" }, "➤");
  const composer = h("div", { class: "composer" }, campo, mic, enviar);
  root.replaceChildren(scroll, pill, composer);

  const setPill = (msg) => { pill.textContent = msg || ""; pill.classList.toggle("hidden", !msg); };
  ctx.onVozEstado = (estado, msg) => {
    mic.classList.toggle("live", estado === "LISTENING");
    setPill(msg || ({ LISTENING: "Escuchando…", PROCESSING: "Procesando…", SPEAKING: "Respondiendo con voz completa…", ERROR: "", IDLE: "" }[estado] ?? ""));
  };
  const autosize = () => { campo.style.height = "auto"; campo.style.height = Math.min(campo.scrollHeight, 120) + "px"; };
  campo.addEventListener("input", autosize);
  campo.addEventListener("keydown", (e) => { if (e.key === "Enter" && !e.shiftKey) { e.preventDefault(); consultar(campo.value); } });
  enviar.addEventListener("click", () => consultar(campo.value));
  mic.addEventListener("click", () => {
    if (ctx.oyente.activo) { ctx.oyente.detener(); ctx.onVozEstado("IDLE"); return; }
    if (ctx.lector.estado !== "IDLE") { ctx.lector.detener(); ctx.onVozEstado("IDLE"); return; }
    ctx.oyente.escuchar({
      onEstado: (e) => ctx.onVozEstado(e),
      onTexto: (t) => { campo.value = t; ctx.onVozEstado("PROCESSING"); consultar(t, true); },
      onError: (m) => { ctx.onVozEstado("ERROR", m); toast(m); },
    });
  });

  function consultar(texto, porVoz = false) {
    const q = (texto || "").trim().replace(/\s+/g, " ");
    if (!q) return;
    campo.value = ""; autosize();
    const resultado = ctx.motor.search(q);
    st.q = q; st.resultado = resultado; st.porVoz = porVoz;
    const hist = store.get("hist", []).filter((x) => x.q.toLowerCase() !== q.toLowerCase());
    hist.unshift({ q, ts: Date.now() }); store.set("hist", hist.slice(0, 50));
    ctx.refrescarHistorial?.();
    pintar();
    if (porVoz) { ctx.lector.hablar(textoParaVoz(resultado.formattedResponse)); ctx.onVozEstado("SPEAKING"); }
  }
  ctx.consultar = consultar;

  function pintar() {
    scroll.replaceChildren();
    if (!st.resultado) { scroll.append(inicio()); return; }
    scroll.append(h("div", { class: "q" }, st.q), respuesta(st.resultado, st.q));
    scroll.scrollTop = 0;
  }

  function inicio() {
    const libro = ctx.libroPreferido;
    return h("div", {},
      h("div", { class: "hero" }, h("img", { src: "img/logo.png", alt: "Asistente Electoral SV" }),
        h("h1", {}, "Asistente Electoral"), h("p", { class: "muted" }, "El Salvador · Apoyo electoral")),
      libro && h("p", { class: "voicepill" }, `Priorizando: ${libro.title}`),
      h("div", { class: "card" },
        h("div", { class: "eyebrow" }, "ORIENTACIÓN LEGAL ELECTORAL"),
        h("p", {}, "Escribe una situación o una pregunta sobre la jornada electoral (JRV, vigilantes, DUI, escrutinio, delitos…) y recibirás pasos, fundamento legal y la página exacta del libro."),
        h("p", { class: "muted" }, "Esta aplicación es una herramienta de consulta independiente. NO es oficial del TSE. No usa inteligencia artificial: responde con su propio banco de conocimientos, sin internet.")),
      h("div", { class: "eyebrow", style: { margin: "14px 2px 8px" } }, "CONSULTAS RECOMENDADAS"),
      h("div", { class: "chips" }, BANCO_SUGERENCIAS.slice(0, 8).map((s) => h("button", { class: "chip", onclick: () => consultar(s) }, s))));
  }

  function respuesta(r, q) {
    const cont = h("div", { class: "ans" });
    if (r.isOffTopic) {
      cont.append(h("div", { class: "card" }, h("div", { class: "eyebrow" }, "AVISO DE CONSULTA"), h("p", { html: mdInline(r.offTopicMessage).replace(/\n/g, "<br>") })));
      return cont;
    }
    const sugerencias = (() => {
      const smart = sugerenciasInteligentes(q);
      const base = r.clarificationOptions.length ? [...r.clarificationOptions.map((s) => s.titulo), ...smart] : smart;
      return [...new Set(base)].slice(0, 3);
    })();
    const ambigua = r.matchLevel === "MEDIUM" && r.formattedResponse.includes("Encontré varias opciones");
    if (!r.topSituation || ambigua) {
      const lowish = !r.topSituation;
      cont.append(h("div", { class: "card" },
        h("span", { class: `badge ${lowish ? "low" : "med"}` }, lowish ? "No se encontró una coincidencia suficientemente precisa" : "Coincidencia probable"),
        h("p", { html: mdInline(lowish ? r.formattedResponse : "Encontré varias opciones en el banco de conocimientos. Selecciona la que mejor describe tu caso:").replace(/\n/g, "<br>") })));
      if (ambigua) cont.append(h("div", { class: "eyebrow", style: { margin: "6px 2px 8px" } }, "SELECCIONA LA OPCIÓN QUE DESCRIBE TU CASO"),
        ...r.clarificationOptions.map((o) => h("button", { class: "opt", onclick: () => consultar(o.titulo) }, h("span", {}, h("b", {}, o.titulo), h("br"), h("span", { class: "muted" }, o.respuesta.situacion)))));
      else cont.append(h("div", { class: "disc" }, `Prueba con: ${["“instalación de mesa”", "“votante con DUI”", "“cierre de votación”"].join(", ")}.`));
      cont.append(chipsSugerencias(sugerencias));
      return cont;
    }
    const s = r.topSituation, resp = s.respuesta, a = resp.autorizaciones;
    const nivel = r.matchLevel === "HIGH" ? ["high", "Coincidencia encontrada"] : ["med", "Coincidencia probable"];
    cont.append(h("span", { class: `badge ${nivel[0]}` }, nivel[1]), h("div", { class: "eyebrow", style: { marginTop: "8px" } }, "TEMA / SITUACIÓN"), h("h2", {}, s.titulo));
    cont.append(h("div", { class: "blk sit" }, h("h3", {}, "SITUACIÓN DETECTADA"), h("div", { html: mdInline(resp.situacion) })));
    cont.append(h("div", { class: "blk si" }, h("h3", {}, "¿QUÉ HACER? — PROCEDIMIENTO"), lista(resp.procedimiento.map((p) => p.replace(/^\d+\.\s*/, "")), true)));
    if (resp.noHacer.length) cont.append(h("div", { class: "blk no" }, h("h3", {}, "¿QUÉ NO HACER? — ACCIONES PROHIBIDAS"), lista(resp.noHacer.map((n) => "No " + n.replace(/^(NO\s*|•\s*)/i, "")))));
    if (a?.quienPuede.length) cont.append(h("div", { class: "blk" }, h("h3", {}, "¿QUIÉN PUEDE? (FACULTADES)"), lista(a.quienPuede)));
    if (a?.quienNoPuede.length) cont.append(h("div", { class: "blk" }, h("h3", {}, "¿QUIÉN NO PUEDE? (PROHIBICIONES)"), lista(a.quienNoPuede)));
    const intervienen = a?.quienInterviene.length ? a.quienInterviene : (!a || !a.quienPuede.length) ? s.actores : [];
    if (intervienen.length) cont.append(h("div", { class: "blk" }, h("h3", {}, "¿QUIÉN INTERVIENE?"), lista([...new Set(intervienen)])));
    if (a?.condiciones.length) cont.append(h("div", { class: "blk" }, h("h3", {}, "CONDICIONES REQUERIDAS"), lista(a.condiciones)));
    cont.append(h("div", { class: "blk ley" }, h("h3", {}, "FUNDAMENTO LEGAL"),
      resp.fundamentosDetallados.length
        ? h("ul", {}, resp.fundamentosDetallados.map((f) => h("li", {}, h("b", {}, `${f.ley} — ${f.articulo}`), f.descripcion ? h("div", { class: "muted" }, f.descripcion) : null)))
        : lista(resp.fundamento.length ? resp.fundamento : ["Normativa e instructivos vigentes del Tribunal Supremo Electoral."])));
    if (resp.advertencia) cont.append(h("div", { class: "blk adv" }, h("h3", {}, "ADVERTENCIA"), h("div", { html: mdInline("⚠️ " + resp.advertencia) })));
    cont.append(h("div", { class: "blk" }, h("h3", {}, "FUENTES DOCUMENTALES"),
      resp.fuentesDocumentales.length
        ? resp.fuentesDocumentales.map((f) => h("div", { class: "fuente" }, h("span", {}, `${f.documento}${f.pagina > 0 ? ` (Pág. ${f.pagina})` : ""}${f.articuloRelacionado ? ` — ${f.articuloRelacionado}` : ""}`),
          h("button", { class: "btn sm", onclick: () => ctx.abrirLibro(f.pdfDocId, f.pagina) }, "📖 VER FUENTE")))
        : h("p", { class: "muted" }, "Tribunal Supremo Electoral (TSE)")));
    cont.append(h("div", { class: "actions" },
      h("button", { class: "btn sec sm", onclick: () => ctx.lector.hablar(textoParaVoz(r.formattedResponse)) || toast("Tu navegador no puede leer en voz alta.") }, "🔊 Escuchar"),
      h("button", { class: "btn sec sm", onclick: () => ctx.lector.pausar() }, "⏸ Pausa"),
      h("button", { class: "btn sec sm", onclick: () => ctx.lector.reanudar() }, "▶ Seguir"),
      h("button", { class: "btn sec sm", onclick: () => ctx.lector.detener() }, "⏹ Detener"),
      h("button", { class: "btn sec sm", onclick: () => copiar(r.formattedResponse) }, "📋 Copiar"),
      navigator.share && h("button", { class: "btn sec sm", onclick: () => navigator.share({ title: s.titulo, text: r.formattedResponse }).catch(() => {}) }, "↗ Compartir")));
    cont.append(h("div", { class: "disc" }, "Esta respuesta es únicamente orientativa. Para una decisión oficial, consulte la normativa vigente y a la autoridad electoral correspondiente."));
    cont.append(chipsSugerencias(sugerencias));
    return cont;
  }
  function chipsSugerencias(sug) {
    if (!sug.length) return h("span");
    return h("div", {}, h("div", { class: "eyebrow", style: { margin: "6px 2px 8px" } }, "¿QUISISTE DECIR…? (SUGERENCIAS)"),
      h("div", { class: "chips" }, sug.map((x) => h("button", { class: "chip", onclick: () => consultar(x) }, x))));
  }
  async function copiar(t) {
    try { await navigator.clipboard.writeText(t); toast("Respuesta copiada"); } catch { toast("No se pudo copiar"); }
  }

  pintar();
  if (ctx.consultaPendiente) { const q = ctx.consultaPendiente; ctx.consultaPendiente = null; setTimeout(() => consultar(q), 0); }
  return () => { ctx.onVozEstado = null; ctx.consultar = null; };
}
