// Ajustes: tema, tamaño del texto, test de 125 preguntas, sesión, textos legales y borrado de historial.
import { h, toast } from "../ui.js";
import { store } from "../store.js";
import { renderTest125 } from "./simulacion.js";

export function renderConfig(ctx, root) {
  const vista = ctx.estadoConfig;
  if (vista.sub === "test") return renderTest125(ctx, root, () => { vista.sub = null; ctx.render(); });
  if (vista.sub === "terminos" || vista.sub === "privacidad") {
    const L = ctx.datos.legal;
    const titulo = vista.sub === "terminos" ? "Términos y Condiciones de Uso" : "Política de Privacidad";
    root.replaceChildren(h("div", { class: "content pad" },
      h("button", { class: "btn ghost sm", onclick: () => { vista.sub = null; ctx.render(); } }, "← Volver a Ajustes"),
      h("div", { class: "h1", style: { margin: "12px 0" } }, titulo),
      h("div", { class: "card legalbox" }, vista.sub === "terminos" ? L.FULL_TERMS_AND_CONDITIONS : L.FULL_PRIVACY_POLICY)));
    return;
  }
  const tema = h("button", { class: "btn sec sm", onclick: () => { ctx.fijarTema(ctx.tema === "dark" ? "light" : "dark"); ctx.render(); } }, ctx.tema === "dark" ? "🌙 Modo oscuro activo — cambiar a claro" : "☀️ Modo claro activo — cambiar a oscuro");
  const tam = h("input", { type: "range", min: 0.85, max: 1.4, step: 0.05, value: ctx.escala, "aria-label": "Tamaño del texto" });
  tam.addEventListener("input", () => ctx.fijarEscala(parseFloat(tam.value)));
  const L = ctx.datos.legal;
  root.replaceChildren(h("div", { class: "content pad" },
    h("div", { class: "eyebrow" }, "AJUSTES Y SISTEMA"), h("div", { class: "h1" }, "Configuración"), h("p", { class: "muted" }, `Versión web de prueba · v${L.APP_VERSION}`),
    h("div", { class: "card" }, h("b", {}, "Tema de la aplicación"), h("div", { style: { marginTop: "8px" } }, tema)),
    h("div", { class: "card" }, h("b", {}, "Tamaño del texto"), h("p", { class: "muted", style: { margin: "2px 0 8px" } }, "Ajusta la legibilidad en toda la aplicación"),
      h("div", { style: { display: "flex", gap: "10px", alignItems: "center" } }, h("span", { style: { fontSize: ".8rem" } }, "A"), tam, h("span", { style: { fontSize: "1.3rem" } }, "A"))),
    h("div", { class: "card" }, h("div", { class: "eyebrow" }, "TEST DE CONOCIMIENTOS"), h("b", {}, "125 preguntas electorales (Estudio y Examen)"),
      h("p", { class: "muted" }, "Accede al banco completo de 125 preguntas oficiales con retroalimentación, fundamentos legales y botón para ver la fuente en PDF."),
      h("button", { class: "btn", onclick: () => { vista.sub = "test"; ctx.render(); } }, "TEST — ABRIR 125 PREGUNTAS")),
    h("div", { class: "card" }, h("b", {}, "Sesión de Usuario"), h("p", { class: "muted" }, "Usuario: " + (ctx.usuario || "—")),
      h("button", { class: "btn danger sm", onclick: () => ctx.cerrarSesion() }, "Cerrar sesión")),
    h("div", { class: "card" }, h("b", {}, "Información Legal y Privacidad"),
      h("div", { class: "row2" }, h("button", { class: "btn sec sm", onclick: () => { vista.sub = "terminos"; ctx.render(); } }, "📄 Términos y Condiciones de Uso"),
        h("button", { class: "btn sec sm", onclick: () => { vista.sub = "privacidad"; ctx.render(); } }, "🔒 Política de Privacidad"))),
    h("div", { class: "card" }, h("b", {}, "Gestión de Datos"), h("p", { class: "muted" }, "El historial se guarda solo en este navegador."),
      h("button", { class: "btn ghost sm", onclick: () => { if (confirm("¿Borrar el historial de consultas de este navegador?")) { store.set("hist", []); ctx.refrescarHistorial?.(); toast("Historial borrado"); } } }, "Borrar Historial de Consultas Locales"))));
}
