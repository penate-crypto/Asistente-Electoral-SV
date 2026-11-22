// Arranque y estructura de la aplicación web "Asistente Electoral SV".
import { h, toast } from "./ui.js";
import { store } from "./store.js";
import { cargarTodo } from "./datos.js";
import { construirSituaciones, MotorElectoral } from "./engine.js";
import { Lector, Oyente } from "./voice.js";
import { abrirPdf } from "./pdfreader.js";
import { pantallaIntro, pantallaLogin, pantallaLegal } from "./screens/acceso.js";
import { renderChat } from "./screens/chat.js";
import { renderBiblioteca } from "./screens/biblioteca.js";
import { renderSimulacion } from "./screens/simulacion.js";
import { renderPaso } from "./screens/paso.js";
import { renderCapacitacion } from "./screens/capacitacion.js";
import { renderConfig } from "./screens/config.js";

const app = document.getElementById("app");
const ctx = {
  datos: null, motor: null, tema: store.get("tema", matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light"), escala: store.get("escala", 1),
  usuario: null, libroPreferido: null, consultaPendiente: null, ruta: "chat", onVozEstado: null,
  chat: { q: null, resultado: null }, estadoSim: { tab: "casos", etapa: "Todos", busqueda: "", caso: null, examen: null },
  estadoTest: { i: 0, resp: {}, fin: false, soloErrores: false }, estadoConfig: { sub: null },
};
ctx.lector = new Lector((e) => { if (ctx.onVozEstado) ctx.onVozEstado(e === "PLAYING" ? "SPEAKING" : "IDLE", e === "PAUSED" ? "Audio en pausa" : e === "IDLE" ? "Pulsa el micrófono para hablar" : undefined); });
ctx.oyente = new Oyente();

function aplicarTema() { document.documentElement.dataset.theme = ctx.tema; document.querySelector('meta[name="theme-color"]')?.setAttribute("content", ctx.tema === "dark" ? "#0B1320" : "#00C6D7"); }
function aplicarEscala() { document.documentElement.style.setProperty("--fs", ctx.escala); }
ctx.fijarTema = (t) => { ctx.tema = t; store.set("tema", t); aplicarTema(); };
ctx.fijarEscala = (e) => { ctx.escala = e; store.set("escala", e); aplicarEscala(); };
aplicarTema(); aplicarEscala();

const SECCIONES = [
  ["chat", "Asistente", "💬", "Asistente Electoral"], ["biblioteca", "Biblioteca", "📚", "Biblioteca Electoral (PDF)"],
  ["test", "Test", "📝", "Test Electoral"], ["paso", "Paso a Paso", "🧭", "Paso a Paso"], ["capacitacion", "Capacitación", "🎓", "Capacitación"], ["config", "Ajustes", "⚙️", "Configuración"],
];
const PANTALLAS = { chat: renderChat, biblioteca: renderBiblioteca, test: renderSimulacion, paso: renderPaso, capacitacion: renderCapacitacion, config: renderConfig };
const NAV_INFERIOR = ["chat", "biblioteca", "test", "paso", "config"];

ctx.abrirLibro = (docId, pagina = 1, buscar = false) => {
  const doc = ctx.datos.biblioteca.documentos.find((d) => d.id === docId);
  if (!doc) { toast("Documento no encontrado en la biblioteca"); return; }
  abrirPdf({ doc, pagina, buscar });
};
ctx.nav = (ruta) => { if (location.hash !== "#" + ruta) location.hash = "#" + ruta; else ctx.render(); };
ctx.preguntar = (q) => { ctx.consultaPendiente = q; if (ctx.ruta === "chat" && ctx.consultar) { ctx.consultaPendiente = null; ctx.consultar(q); } else ctx.nav("chat"); };
ctx.cerrarSesion = () => { ctx.lector.detener(); ctx.oyente.detener(); store.del("sesion"); ctx.usuario = null; arrancar(); };

let limpiar = null, main, titulo, subtitulo, bottom, drawer;
ctx.render = () => {
  const ruta = (location.hash.replace(/^#\/?/, "") || "chat").split("?")[0];
  ctx.ruta = PANTALLAS[ruta] ? ruta : "chat";
  limpiar?.(); limpiar = null;
  const sec = SECCIONES.find((s) => s[0] === ctx.ruta);
  titulo.textContent = sec[3]; subtitulo.textContent = "El Salvador · Apoyo electoral";
  bottom.querySelectorAll("button").forEach((b) => b.classList.toggle("on", b.dataset.r === ctx.ruta));
  limpiar = PANTALLAS[ctx.ruta](ctx, main) || null;
};

function pintarHistorial(cont) {
  const hist = store.get("hist", []);
  cont.replaceChildren(...(hist.length ? hist.slice(0, 12).map((x) => h("button", { class: "histitem", title: x.q, onclick: () => { cerrarDrawer(); ctx.preguntar(x.q); } }, x.q)) : [h("p", { class: "muted", style: { padding: "4px 10px" } }, "Aún no hay consultas.")]));
}
function cerrarDrawer() { drawer?.bg.remove(); drawer?.panel.remove(); drawer = null; }
function abrirDrawer() {
  cerrarDrawer();
  const hist = h("div");
  const panel = h("aside", { class: "drawer", "aria-label": "Menú" },
    h("div", { class: "brand" }, h("img", { src: "img/isotipo.png", alt: "" }), h("div", {}, h("b", {}, "Asistente Electoral SV"), h("div", { class: "muted" }, "El Salvador · Apoyo electoral"))),
    h("div", { class: "sect" }, "SECCIONES PRINCIPALES"),
    SECCIONES.map(([r, nombre, ic]) => h("button", { class: "navitem" + (r === ctx.ruta ? " on" : ""), onclick: () => { cerrarDrawer(); ctx.nav(r); } }, h("span", {}, ic), nombre)),
    h("div", { class: "sect" }, "HISTORIAL RECIENTE"), hist,
    h("div", { class: "sect" }, "AVISO"), h("p", { class: "muted" }, "Herramienta independiente de consulta. No es oficial del TSE. Creador: Rodrigo Peñate"));
  const bg = h("div", { class: "drawer-bg", onclick: cerrarDrawer });
  drawer = { bg, panel }; document.body.append(bg, panel); pintarHistorial(hist);
  ctx.refrescarHistorial = () => drawer && pintarHistorial(hist);
}

function montarPrincipal() {
  titulo = h("b"); subtitulo = h("small");
  main = h("main", { class: "main" });
  bottom = h("nav", { class: "bottomnav", "aria-label": "Secciones" }, NAV_INFERIOR.map((r) => { const s = SECCIONES.find((x) => x[0] === r); return h("button", { "data-r": r, onclick: () => ctx.nav(r) }, h("span", { class: "ic" }, s[2]), s[1]); }));
  const shell = h("div", { class: "shell" },
    h("header", { class: "topbar" }, h("button", { class: "iconbtn", "aria-label": "Abrir menú", onclick: abrirDrawer }, "☰"), h("div", { class: "title" }, titulo, subtitulo),
      h("button", { class: "iconbtn", "aria-label": "Cambiar tema claro/oscuro", onclick: () => { ctx.fijarTema(ctx.tema === "dark" ? "light" : "dark"); } }, "◐"),
      h("button", { class: "iconbtn", "aria-label": "Nueva consulta", onclick: () => { ctx.chat.resultado = null; ctx.chat.q = null; ctx.lector.detener(); ctx.nav("chat"); ctx.render(); } }, "✎")),
    main, bottom);
  app.replaceChildren(shell);
  window.onhashchange = ctx.render;
  ctx.render();
}

async function arrancar() {
  limpiar?.(); limpiar = null; window.onhashchange = null; cerrarDrawer();
  if (!ctx.datos) {
    app.replaceChildren(h("div", { class: "center" }, h("div", { class: "splash" }, h("img", { src: "img/logo.png", alt: "Asistente Electoral SV" }), h("p", { class: "muted" }, "Cargando…"))));
    try {
      ctx.datos = await cargarTodo();
      ctx.motor = new MotorElectoral(construirSituaciones(ctx.datos.banco));
    } catch (e) {
      app.replaceChildren(h("div", { class: "center" }, h("div", { class: "box card" }, h("h1", { style: { fontSize: "1.1rem" } }, "No se pudo cargar la aplicación"), h("p", { class: "muted" }, String(e.message || e)),
        h("button", { class: "btn", onclick: () => location.reload() }, "Reintentar"))));
      return;
    }
  }
  const sesion = store.get("sesion");
  const consentimiento = store.get("consentimiento");
  const paso = () => {
    if (!ctx.usuario) { app.replaceChildren(pantallaLogin(ctx, (d) => { ctx.usuario = d; paso(); })); return; }
    if (!consentimientoOk()) { app.replaceChildren(pantallaLegal(ctx, () => { store.set("consentimiento", { v: ctx.datos.legal.CURRENT_LEGAL_VERSION, ts: Date.now() }); paso(); }, () => ctx.cerrarSesion())); return; }
    montarPrincipal();
  };
  const consentimientoOk = () => { const c = store.get("consentimiento"); return !!c && c.v === ctx.datos.legal.CURRENT_LEGAL_VERSION; };
  ctx.usuario = sesion?.dui || null;
  if (!store.get("introVista") && !ctx.usuario && !sessionStorage.getItem("intro")) {
    try { sessionStorage.setItem("intro", "1"); } catch { /* nada */ }
    app.replaceChildren(pantallaIntro(paso));
  } else paso();
}

if ("serviceWorker" in navigator && (location.protocol === "https:" || location.hostname === "localhost")) {
  navigator.serviceWorker.register("sw.js").catch(() => {});
}
arrancar();
