// Visor de PDF (pdf.js) con buscador interno por palabra o número de artículo.
import { h, norm, esc, toast } from "./ui.js";
import { indiceLibro } from "./datos.js";

let pdfjsPromise;
function cargarPdfJs() {
  if (window.pdfjsLib) return Promise.resolve(window.pdfjsLib);
  pdfjsPromise ??= new Promise((ok, fail) => {
    const s = document.createElement("script");
    s.src = new URL("../vendor/pdfjs/pdf.min.js", import.meta.url).href;
    s.onload = () => {
      window.pdfjsLib.GlobalWorkerOptions.workerSrc = new URL("../vendor/pdfjs/pdf.worker.min.js", import.meta.url).href;
      ok(window.pdfjsLib);
    };
    s.onerror = () => fail(new Error("No se pudo cargar el visor de PDF."));
    document.head.append(s);
  });
  return pdfjsPromise;
}

export async function buscarEnLibro(docId, consulta) {
  const paginas = await indiceLibro(docId);
  const q = norm(consulta).trim();
  if (!q) return [];
  const resultados = [];
  const art = /^(?:art(?:iculo|\.)?\s*)?(\d+(?:-[a-z])?)\.?$/.exec(q);
  if (art) {
    const re = new RegExp(`\\bart(?:iculo|\\.)\\s*${art[1].replace(/-/g, "-")}\\s*\\.?\\s*[-–]`, "i");
    paginas.forEach((t, i) => { const n = norm(t); const m = re.exec(n); if (m) resultados.push({ pagina: i + 1, titulo: `Artículo ${art[1].toUpperCase()}`, texto: t.slice(Math.max(0, m.index), m.index + 220), pal: [] }); });
    if (resultados.length) return resultados;
  }
  const palabras = q.split(/\s+/).filter((w) => w.length >= 3);
  const buscar = palabras.length ? palabras : [q];
  paginas.forEach((t, i) => {
    const n = norm(t);
    if (!buscar.every((w) => n.includes(w))) return;
    const idx = n.indexOf(buscar[0]);
    const ini = Math.max(0, idx - 70);
    const puntos = buscar.reduce((a, w) => a + n.split(w).length - 1, 0);
    resultados.push({ pagina: i + 1, titulo: "Coincidencia", texto: t.slice(ini, ini + 220), pal: buscar, puntos });
  });
  return resultados.sort((a, b) => (b.puntos || 0) - (a.puntos || 0) || a.pagina - b.pagina).slice(0, 80);
}

function resaltar(texto, palabras) {
  let html = esc(texto).replace(/\s+/g, " ");
  for (const w of palabras) {
    const re = new RegExp(`(${w.replace(/[.*+?^${}()|[\]\\]/g, "\\$&")})`, "gi");
    html = html.replace(re, "<mark>$1</mark>");
  }
  return html;
}

export async function abrirPdf({ doc, pagina = 1, buscar = false }) {
  document.querySelector(".pdfview")?.remove();
  const url = new URL("../" + doc.assetPath, import.meta.url).href;
  let pdf = null, actual = Math.max(1, pagina), zoom = 1, renderId = 0, total = doc.totalPaginas || 1;

  const lienzo = h("div", { class: "pdfcanvas" });
  const info = h("span", { class: "muted" });
  const inputPag = h("input", { type: "number", min: 1, "aria-label": "Número de página", inputmode: "numeric" });
  const alCambiarTamano = () => pdf && pintar();
  const cerrar = () => { vista.remove(); document.removeEventListener("keydown", teclas); window.removeEventListener("resize", alCambiarTamano); };
  const teclas = (e) => { if (e.key === "Escape") cerrar(); if (e.key === "ArrowRight") ir(actual + 1); if (e.key === "ArrowLeft") ir(actual - 1); };
  document.addEventListener("keydown", teclas);

  async function pintar() {
    const mi = ++renderId;
    if (!pdf) return;
    actual = Math.min(Math.max(1, actual), total);
    inputPag.value = actual; info.textContent = `de ${total}`;
    try {
      const page = await pdf.getPage(actual);
      if (mi !== renderId) return;
      const vp1 = page.getViewport({ scale: 1 });
      const ancho = Math.max(280, lienzo.clientWidth - 20);
      const escala = (ancho / vp1.width) * zoom;
      const dpr = Math.min(window.devicePixelRatio || 1, 2);
      const vp = page.getViewport({ scale: escala * dpr });
      const canvas = h("canvas", { "aria-label": `Página ${actual} de ${doc.title}` });
      canvas.width = Math.floor(vp.width); canvas.height = Math.floor(vp.height);
      canvas.style.width = Math.floor(vp.width / dpr) + "px"; canvas.style.height = Math.floor(vp.height / dpr) + "px";
      await page.render({ canvasContext: canvas.getContext("2d"), viewport: vp }).promise;
      if (mi !== renderId) return;
      lienzo.replaceChildren(canvas); lienzo.scrollTop = 0;
    } catch (e) { if (mi === renderId) lienzo.replaceChildren(h("p", { style: { color: "#fff" } }, "No se pudo mostrar la página. ", h("a", { href: url, target: "_blank", rel: "noopener", style: { color: "#fff" } }, "Abrir el PDF en otra pestaña"))); }
  }
  const ir = (n) => { actual = n; pintar(); };

  const vista = h("div", { class: "pdfview", role: "dialog", "aria-label": doc.title },
    h("div", { class: "pdfbar" },
      h("button", { class: "iconbtn", "aria-label": "Cerrar visor PDF", onclick: cerrar }, "✕"),
      h("div", { class: "t" }, doc.title),
      h("button", { class: "btn sm", onclick: () => modalBusqueda() }, "🔎 Buscar"),
      h("button", { class: "iconbtn", "aria-label": "Alejar", onclick: () => { zoom = Math.max(0.5, zoom - 0.25); pintar(); } }, "−"),
      h("button", { class: "iconbtn", "aria-label": "Acercar", onclick: () => { zoom = Math.min(3, zoom + 0.25); pintar(); } }, "+")),
    lienzo,
    h("div", { class: "pdffoot" },
      h("button", { class: "btn sec sm", onclick: () => ir(actual - 1) }, "◀ Anterior"),
      inputPag, info,
      h("button", { class: "btn sec sm", onclick: () => ir(actual + 1) }, "Siguiente ▶")));
  inputPag.addEventListener("change", () => ir(parseInt(inputPag.value, 10) || 1));
  document.body.append(vista);
  lienzo.append(h("p", { style: { color: "#fff" } }, "Cargando documento…"));

  function modalBusqueda() {
    const campo = h("input", { type: "search", placeholder: "Número de artículo o palabra (ej. 198, padrón)", "aria-label": "Buscar en este documento" });
    const res = h("div", { class: "res" }, h("p", { class: "muted" }, "Escribe un número de artículo o una palabra para saltar directamente a la página correspondiente."));
    const bg = h("div", { class: "modal-bg", onclick: (e) => e.target === bg && bg.remove() },
      h("div", { class: "modal", role: "dialog", "aria-label": "Buscar en este documento" },
        h("div", { style: { display: "flex", gap: "8px", alignItems: "center" } }, h("b", { style: { flex: 1 } }, "Buscar en este documento"), h("button", { class: "iconbtn", "aria-label": "Cerrar búsqueda", onclick: () => bg.remove() }, "✕")),
        h("div", { style: { marginTop: "8px" } }, campo), res));
    document.body.append(bg); campo.focus();
    let t;
    campo.addEventListener("input", () => { clearTimeout(t); t = setTimeout(hacer, 180); });
    campo.addEventListener("keydown", (e) => e.key === "Enter" && hacer());
    async function hacer() {
      const q = campo.value;
      if (!q.trim()) return;
      const r = await buscarEnLibro(doc.id, q);
      res.replaceChildren(r.length
        ? h("p", { class: "muted" }, `${r.length} resultado(s) — toca uno para saltar a la página:`)
        : h("p", { class: "muted" }, `No se encontraron coincidencias en ${doc.title}.`),
        ...r.map((x) => h("button", { onclick: () => { bg.remove(); ir(x.pagina); } },
          h("b", {}, `${x.titulo} · Pág. ${x.pagina}`), h("span", { html: resaltar(x.texto, x.pal) }))));
    }
  }

  try {
    const lib = await cargarPdfJs();
    pdf = await lib.getDocument({ url }).promise;
    total = pdf.numPages; await pintar();
    if (buscar) modalBusqueda();
  } catch (e) {
    lienzo.replaceChildren(h("p", { style: { color: "#fff" } }, "No se pudo abrir el documento. ", h("a", { href: url, target: "_blank", rel: "noopener", style: { color: "#fff" } }, "Intenta abrirlo en otra pestaña.")));
    toast("No se pudo cargar el PDF");
  }
  window.addEventListener("resize", alCambiarTamano, { passive: true });
}
