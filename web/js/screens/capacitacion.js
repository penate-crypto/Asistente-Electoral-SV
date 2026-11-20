// Módulo de capacitación: temas explicados y atajo para consultar cada uno.
import { h } from "../ui.js";
const ICONOS = { MenuBook: "📖", Gavel: "⚖️", Groups: "👥", Visibility: "👁️", HourglassTop: "⏳", HowToVote: "🗳️", HourglassDisabled: "⌛", Percent: "📊", Warning: "⚠️", Quiz: "❓" };

export function renderCapacitacion(ctx, root) {
  root.replaceChildren(h("div", { class: "content pad" },
    h("div", { class: "eyebrow" }, "MÓDULO DE CAPACITACIÓN"), h("div", { class: "h1" }, "Capacitación Electoral"),
    h("p", { class: "muted" }, "Temas esenciales para integrantes de JRV y fiscalizadores. Toca «Preguntar» para ver el procedimiento detallado."),
    ctx.datos.capacitacion.map((t) => h("div", { class: "card" },
      h("div", { class: "eyebrow" }, `${ICONOS[t.icon] || "📌"} ${t.tag.toUpperCase()}`), h("h3", { style: { margin: "2px 0 6px" } }, t.title),
      h("p", { style: { margin: "0 0 10px" } }, t.summary),
      h("button", { class: "btn sm", onclick: () => ctx.preguntar(t.didacticKey) }, "💬 Preguntar al asistente")))));
}
