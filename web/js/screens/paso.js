// Paso a paso: guía cronológica por integrante de la JRV o de toda la jornada.
import { h } from "../ui.js";

const ROLES = [
  ["PRESIDENTE", "Presidente de JRV", "Presidente", "⚖️"], ["SECRETARIO", "Secretario de JRV", "Secretario", "📝"],
  ["VOCAL_1", "Primer Vocal", "Vocal 1", "🗳️"], ["VOCAL_2", "Segundo Vocal", "Vocal 2", "✅"], ["VOCAL_3", "Tercer Vocal", "Vocal 3", "👥"],
  ["VIGILANTE", "Vigilante de Partido", "Vigilante", "👁️"], ["SUPERVISOR", "Supervisor de Centro", "Supervisor", "🧭"], ["JEFE_DE_CENTRO", "Jefe de Centro de Votación", "Jefe Centro", "🪪"],
];

export function renderPaso(ctx, root) {
  const { roles, resumenes, general } = ctx.datos.pasos;
  let modo = "rol", rol = "PRESIDENTE";
  const cuerpo = h("div"), cab = h("div");
  const etapa = (e, abierto) => h("details", { class: "stage", open: abierto },
    h("summary", {}, h("div", { class: "t" }, h("b", {}, e.stageName), h("small", {}, "🕒 " + e.timeLabel)), "▾"),
    h("div", { class: "body" }, h("p", { class: "muted", style: { marginTop: 0 } }, e.overview),
      e.items.map((i) => h("div", { class: "item" }, h("b", {}, "✔ " + i.title), h("span", {}, i.detail))),
      e.legalRef && h("div", { class: "legal" }, "⚖️ " + e.legalRef),
      e.prohibitedOrWarning && h("div", { class: "warn" }, "⚠️ " + e.prohibitedOrWarning)));
  const pintar = () => {
    cab.replaceChildren(h("div", { class: "tabs" },
      h("button", { class: modo === "rol" ? "on" : "", onclick: () => { modo = "rol"; pintar(); } }, "Por Rol / Integrante"),
      h("button", { class: modo === "jornada" ? "on" : "", onclick: () => { modo = "jornada"; pintar(); } }, "Jornada General")));
    if (modo === "rol") {
      cab.append(h("div", { class: "chips scroll" }, ROLES.map(([k, , corto, ic]) => h("button", { class: "chip" + (k === rol ? " on" : ""), onclick: () => { rol = k; pintar(); } }, `${ic} ${corto}`))));
      const r = ROLES.find((x) => x[0] === rol);
      cuerpo.replaceChildren(h("div", { class: "card" }, h("div", { class: "eyebrow" }, r[1].toUpperCase()), h("p", { style: { margin: "4px 0 0" } }, resumenes[rol])),
        ...(roles[rol] || []).map((e, i) => etapa(e, i === 0)));
    } else {
      cuerpo.replaceChildren(h("div", { class: "card" }, h("div", { class: "eyebrow" }, "JORNADA ELECTORAL COMPLETA"), h("p", { style: { margin: "4px 0 0" } }, "Cronología general desde la llegada de los integrantes hasta la entrega del paquete electoral.")),
        ...general.map((e, i) => etapa(e, i === 0)));
    }
  };
  pintar();
  root.replaceChildren(h("div", { class: "content pad" },
    h("div", { class: "eyebrow" }, "GUÍA CRONOLÓGICA OFICIAL"), h("div", { class: "h1" }, "Paso a Paso"),
    h("p", { class: "muted" }, "Guías para integrantes de JRV, vigilantes, supervisores, jefes de centro y proceso general de la jornada."), cab, cuerpo));
}
