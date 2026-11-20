// Componentes compartidos del Test: papeleta ilustrada, tarjeta de pregunta y revisión.
import { h, esc, LETRAS } from "../ui.js";
import { paginaDeArticulo } from "../datos.js";

export function papeletaSvg(tipo) {
  const W = 360, H = 180, sw = (W - 30) / 2, sh = (H - 30) / 2;
  const s1 = [10, 10], s2 = [sw + 20, 10], s3 = [10, sh + 20], s4 = [sw + 20, sh + 20];
  const colores = [["#E3F2FD", "#1976D2", "#1565C0"], ["#FFEBEE", "#D32F2F", "#C62828"], ["#E8F5E9", "#388E3C", "#2E7D32"], ["#FFF3E0", "#F57C00", "#EF6C00"]];
  let svg = `<svg viewBox="0 0 ${W} ${H}" xmlns="http://www.w3.org/2000/svg" role="img" aria-label="Papeleta de ejemplo"><rect width="${W}" height="${H}" fill="#F5F5F0"/>`;
  [s1, s2, s3, s4].forEach(([x, y], i) => {
    const [bg, borde, bandera] = colores[i];
    svg += `<rect x="${x}" y="${y}" width="${sw}" height="${sh}" fill="${bg}" stroke="${borde}" stroke-width="2"/><rect x="${x + 8}" y="${y + 8}" width="${sw - 16}" height="${sh * 0.45}" fill="${bandera}"/>`;
  });
  const k = "#212121", L = (x1, y1, x2, y2, c = k, w = 7) => `<line x1="${x1}" y1="${y1}" x2="${x2}" y2="${y2}" stroke="${c}" stroke-width="${w}" stroke-linecap="round"/>`;
  const cruz = ([x, y]) => L(x + 20, y + 15, x + sw - 20, y + sh - 15) + L(x + sw - 20, y + 15, x + 20, y + sh - 15);
  switch (tipo) {
    case "NULL_TWO_RIVAL_PARTIES": svg += cruz(s1) + cruz(s2); break;
    case "VALID_CHECKMARK_OVER_FLAG": svg += `<path d="M${s3[0] + 25} ${s3[1] + sh * 0.5} L${s3[0] + sw * 0.45} ${s3[1] + sh - 20} L${s3[0] + sw - 25} ${s3[1] + 18}" fill="none" stroke="${k}" stroke-width="9" stroke-linecap="round" stroke-linejoin="round"/>`; break;
    case "VALID_COALITION_MARK": svg += cruz(s1) + cruz(s3); break;
    case "NULL_OBSCENE_INSULT_TEXT":
      svg += L(10, 10, W - 10, H - 10, "#D32F2F", 10) + L(W - 10, 10, 10, H - 10, "#D32F2F", 10);
      for (let i = 1; i <= 4; i++) svg += L(20 * i, 30 + i * 25, W - 20 * i, 40 + i * 25, "#D32F2F", 3);
      break;
    case "NULL_MUTILATED_BALLOT": svg += `<path d="M${W * 0.5} 0 L${W * 0.55} ${H * 0.3} L${W * 0.45} ${H * 0.6} L${W * 0.52} ${H} L${W} ${H} L${W} 0 Z" fill="#E0E0E0" fill-opacity="0.85" stroke="#B71C1C" stroke-width="3"/>`; break;
    case "VALID_PREFERENTIAL_CROSS_CANDIDATES":
      svg += L(s2[0] + 20, s2[1] + 10, s2[0] + sw - 20, s2[1] + sh * 0.5) + L(s2[0] + sw - 20, s2[1] + 10, s2[0] + 20, s2[1] + sh * 0.5)
        + `<circle cx="${s2[0] + sw * 0.3}" cy="${s2[1] + sh * 0.75}" r="10" fill="none" stroke="${k}" stroke-width="4"/><circle cx="${s2[0] + sw * 0.7}" cy="${s2[1] + sh * 0.75}" r="10" fill="none" stroke="${k}" stroke-width="4"/>`; break;
    case "NULL_CANDIDATE_PLUS_INDEPENDENT_RIVAL": svg += cruz(s1) + cruz(s4); break;
    case "VALID_SLIGHT_OVERFLOW_CROSS": svg += L(s3[0] + 10, s3[1] + 8, s3[0] + sw + 12, s3[1] + sh + 8) + L(s3[0] + sw + 10, s3[1] + 8, s3[0] + 10, s3[1] + sh + 8); break;
    default: svg += cruz(s1);
  }
  return svg + "</svg>";
}

export function papeleta(q) {
  return h("div", { class: "ballot" },
    h("small", {}, h("b", { style: { color: "#0D47A1" } }, "REPÚBLICA DE EL SALVADOR"), " · PAPELETA OFICIAL"),
    h("small", { style: { marginBottom: "6px" } }, "TRIBUNAL SUPREMO ELECTORAL • ELECCIÓN NACIONAL"),
    h("div", { html: papeletaSvg(q.ballotMarkType) }),
    q.ballotVisualDescription && h("div", { class: "cap" }, "Lo que se observa en la papeleta: ", q.ballotVisualDescription),
    h("small", {}, "Simulación visual didáctica para calificación de votos"));
}

export function fuenteTexto(q) {
  const art = q.sourceArticle || q.normativeReference;
  return `${art} (${q.sourceDocument})`;
}
export async function verFuente(ctx, q) {
  const pagina = await paginaDeArticulo(q.sourceDocumentId, q.sourceArticle || q.normativeReference, q.sourcePage || 1);
  ctx.abrirLibro(q.sourceDocumentId, pagina);
}

// Tarjeta de una pregunta. modo "inmediato": muestra la corrección al responder; modo "examen": solo marca la selección.
export function tarjetaPregunta(ctx, q, { numero, total, elegida, onElegir, inmediato }) {
  const respondida = elegida != null;
  const mostrar = respondida && inmediato;
  const cont = h("div", { class: "card" },
    h("div", { class: "eyebrow" }, `Pregunta ${numero} de ${total} · ${q.category}`),
    q.situationContext && h("p", { class: "muted", style: { margin: "6px 0" } }, "Contexto: " + q.situationContext),
    h("h2", { style: { fontSize: "1.02rem", margin: "6px 0 10px" } }, q.questionText),
    q.type === "BALLOT_VALIDITY" && papeleta(q),
    q.options.map((o, i) => h("button", {
      class: "opt" + (mostrar ? (i === q.correctOptionIndex ? " ok" : i === elegida ? " bad" : "") : (i === elegida ? " sel" : "")),
      disabled: mostrar, onclick: () => onElegir(i),
    }, h("span", { class: "ltr" }, LETRAS[i]), o)));
  if (mostrar) {
    cont.append(h("div", { class: "blk " + (elegida === q.correctOptionIndex ? "si" : "no") },
      h("h3", {}, elegida === q.correctOptionIndex ? "✅ RESPUESTA CORRECTA" : "❌ RESPUESTA INCORRECTA"),
      elegida !== q.correctOptionIndex && h("p", { style: { margin: "0 0 6px" } }, `Correcta: ${LETRAS[q.correctOptionIndex]}) ${q.options[q.correctOptionIndex]}`),
      h("p", { style: { margin: 0 } }, "Explicación: " + q.explanation)),
      h("div", { class: "blk ley" }, h("h3", {}, "FUNDAMENTO"), h("p", { style: { margin: "0 0 8px" } }, fuenteTexto(q)),
        h("button", { class: "btn sm", onclick: () => verFuente(ctx, q) }, "📖 VER FUENTE")));
  }
  return cont;
}

export function revision(ctx, items, soloErrores) {
  const lista = items.map((it, i) => ({ ...it, n: i + 1 })).filter((x) => !soloErrores || !x.ok);
  if (!lista.length) return h("p", { class: "muted" }, soloErrores ? "¡No hubo errores!" : "Sin preguntas para revisar.");
  return h("div", {}, lista.map((x) => h("div", { class: "card" },
    h("div", { class: "eyebrow" }, `Pregunta ${x.n} · ${x.q.category} · ${x.sin ? "SIN RESPONDER" : x.ok ? "✅ ACIERTO" : "❌ FALLO"}`),
    h("p", { style: { margin: "6px 0" } }, x.q.questionText),
    !x.sin && h("p", { style: { margin: "2px 0" } }, `Tu respuesta: ${LETRAS[x.elegida]}) ${x.q.options[x.elegida]}`),
    h("p", { style: { margin: "2px 0" } }, `Respuesta correcta: ${LETRAS[x.q.correctOptionIndex]}) ${x.q.options[x.q.correctOptionIndex]}`),
    h("p", { class: "muted", style: { margin: "6px 0" } }, "Explicación: " + x.q.explanation),
    h("p", { class: "legal" }, "Base: " + fuenteTexto(x.q)),
    h("button", { class: "btn sm", onclick: () => verFuente(ctx, x.q) }, "📖 VER FUENTE"))));
}
