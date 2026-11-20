// Pantallas de entrada: introducción, inicio de sesión y consentimiento legal.
import { h } from "../ui.js";
import { store } from "../store.js";

export function pantallaIntro(onFin) {
  const barra = h("i"), pct = h("div", { class: "muted" }, "0%");
  const nodo = h("div", { class: "center" }, h("div", { class: "splash" }, h("img", { src: "img/logo.png", alt: "Asistente Electoral SV" }), h("div", { class: "progress" }, barra), pct));
  let p = 0; const t = setInterval(() => { p = Math.min(100, p + 7); barra.style.width = p + "%"; pct.textContent = p + "%"; if (p >= 100) { clearInterval(t); setTimeout(onFin, 150); } }, 90);
  nodo.addEventListener("click", () => { clearInterval(t); onFin(); });
  return nodo;
}

const soloDigitos = (s) => s.replace(/\D/g, "").slice(0, 9);
export const formatoDui = (s) => { const d = soloDigitos(s); return d.length === 9 ? `${d.slice(0, 8)}-${d[8]}` : d; };

export function pantallaLogin(ctx, onOk) {
  const dui = h("input", { type: "text", inputmode: "numeric", autocomplete: "username", placeholder: "Usuario (9 dígitos)", "aria-label": "Usuario (9 dígitos)", value: store.get("duiGuardado", "") });
  const pass = h("input", { type: "password", autocomplete: "current-password", placeholder: "Contraseña", "aria-label": "Contraseña" });
  const rec = h("input", { type: "checkbox", id: "rec", checked: !!store.get("duiGuardado", "") });
  const err = h("div", { class: "err", role: "alert" });
  dui.addEventListener("input", () => { dui.value = formatoDui(dui.value); });
  const ver = h("button", { class: "iconbtn", type: "button", "aria-label": "Mostrar contraseña", onclick: () => { pass.type = pass.type === "password" ? "text" : "password"; } }, "👁");
  const entrar = (ev) => {
    ev?.preventDefault();
    const d = soloDigitos(dui.value);
    if (d.length !== 9) { err.textContent = "El usuario debe contener exactamente 9 dígitos numéricos."; return; }
    if (pass.value.length < 6) { err.textContent = "La contraseña debe tener un mínimo de 6 caracteres."; return; }
    store.set("sesion", { dui: formatoDui(d), ts: Date.now() });
    if (rec.checked) store.set("duiGuardado", formatoDui(d)); else store.del("duiGuardado");
    onOk(formatoDui(d));
  };
  return h("div", { class: "center" }, h("form", { class: "box card", onsubmit: entrar },
    h("div", { style: { textAlign: "center" } }, h("img", { src: "img/logo.png", alt: "Logo Capacitador Electoral SV", style: { width: "220px", maxWidth: "70%" } }),
      h("h1", { style: { fontSize: "1.2rem", marginTop: "6px" } }, "Capacitador Electoral SV"), h("p", { class: "muted" }, "Asistencia Legal y Procedimientos Electorales")),
    h("div", { style: { margin: "14px 0 8px" } }, dui), h("div", { style: { display: "flex", gap: "6px", alignItems: "center" } }, pass, ver),
    h("label", { class: "row", style: { margin: "10px 0" } }, rec, "Recordar usuario"), err,
    h("button", { class: "btn block", type: "submit" }, "Iniciar sesión"),
    h("p", { class: "muted", style: { marginTop: "12px", fontSize: ".75rem" } }, "Versión web de prueba: para ver cómo funciona, usa el usuario de prueba 12345678-9 y la contraseña abc123 (en esta versión cualquier usuario de 9 dígitos con contraseña de 6 o más caracteres puede entrar).")));
}

export function pantallaLegal(ctx, onAceptar, onRechazar) {
  const L = ctx.datos.legal;
  const abrir = (titulo, texto) => {
    const bg = h("div", { class: "modal-bg", onclick: (e) => e.target === bg && bg.remove() }, h("div", { class: "modal" },
      h("div", { style: { display: "flex", alignItems: "center" } }, h("b", { style: { flex: 1 } }, titulo), h("button", { class: "iconbtn", "aria-label": "Cerrar", onclick: () => bg.remove() }, "✕")),
      h("div", { class: "res legalbox" }, texto)));
    document.body.append(bg);
  };
  return h("div", { class: "center" }, h("div", { class: "box card" },
    h("div", { class: "eyebrow" }, "AVISO OBLIGATORIO"), h("h1", { style: { fontSize: "1.2rem", margin: "4px 0 6px" } }, "Consentimiento Requerido"),
    h("p", { class: "muted" }, "Para utilizar Asistente Electoral SV es necesario aceptar los Términos y Condiciones y la Política de Privacidad."),
    h("div", { class: "blk adv legalbox", style: { maxHeight: "34dvh", overflowY: "auto" } }, h("b", {}, L.INITIAL_NOTICE_TITLE + "\n\n"), L.INITIAL_NOTICE_SUMMARY),
    h("div", { class: "eyebrow", style: { margin: "10px 0 6px" } }, "DOCUMENTOS LEGALES COMPLETOS"),
    h("div", { class: "row2", style: { marginTop: 0 } }, h("button", { class: "btn sec sm", onclick: () => abrir("Términos y Condiciones", L.FULL_TERMS_AND_CONDITIONS) }, "📄 Términos y Condiciones"),
      h("button", { class: "btn sec sm", onclick: () => abrir("Política de Privacidad", L.FULL_PRIVACY_POLICY) }, "🔒 Política de Privacidad")),
    h("div", { class: "row2" }, h("button", { class: "btn", onclick: onAceptar }, "ACEPTAR Y CONTINUAR"), h("button", { class: "btn ghost", onclick: onRechazar }, "RECHAZAR"))));
}
