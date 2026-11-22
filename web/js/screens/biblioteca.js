// Biblioteca: libros y leyes en PDF, con buscador interno y consulta al asistente.
import { h } from "../ui.js";

export function renderBiblioteca(ctx, root) {
  const { categorias, documentos } = ctx.datos.biblioteca;
  let cat = "Todos";
  const lista = h("div");
  const chips = h("div", { class: "chips scroll" });
  const pintar = () => {
    chips.replaceChildren(...categorias.map((c) => h("button", { class: "chip" + (c === cat ? " on" : ""), onclick: () => { cat = c; pintar(); } }, c)));
    const docs = documentos.filter((d) => cat === "Todos" || d.category === cat);
    lista.replaceChildren(...docs.map((d) => h("div", { class: "card docrow" },
      h("div", { class: "eyebrow" }, d.category.toUpperCase()),
      h("h3", {}, d.title),
      h("div", { class: "auth" }, `${d.authority} · ${d.releaseDate} · ${d.totalPaginas} págs.`),
      h("p", { class: "muted", style: { margin: "6px 0 0" } }, d.summary),
      h("div", { class: "row2" },
        h("button", { class: "btn sm", onclick: () => ctx.abrirLibro(d.id, 1, true) }, "🔎 BUSCADOR DEL LIBRO"),
        h("button", { class: "btn sec sm", onclick: () => ctx.abrirLibro(d.id, 1) }, "📄 ABRIR PDF"),
        h("button", { class: "btn ghost sm", onclick: () => { ctx.libroPreferido = d; ctx.nav("chat"); } }, "💬 CONSULTAR ASISTENTE")))));
    if (!docs.length) lista.replaceChildren(h("p", { class: "muted" }, "No hay documentos en esta categoría."));
  };
  pintar();
  root.replaceChildren(h("div", { class: "content pad" },
    h("div", { class: "eyebrow" }, "BIBLIOTECA"), h("div", { class: "h1" }, "Documentos y Leyes Oficiales"),
    h("p", { class: "muted" }, "Cada libro cuenta con buscador interno exclusivo, visor PDF y consulta con el asistente."), chips, lista));
}
