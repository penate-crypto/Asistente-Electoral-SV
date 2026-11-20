// Carga de datos (banco de conocimientos, test, casos, paso a paso, biblioteca, textos legales).
const base = new URL("../data/", import.meta.url);
const get = async (n) => {
  const r = await fetch(new URL(n, base));
  if (!r.ok) throw new Error(`No se pudo cargar ${n} (${r.status})`);
  return r.json();
};
export async function cargarTodo() {
  const [banco, examen, casos, pasos, capacitacion, biblioteca, legal] = await Promise.all(
    ["banco_conocimientos.json", "examen.json", "casos.json", "pasos.json", "capacitacion.json", "biblioteca.json", "legal.json"].map(get));
  return { banco, examen, casos, pasos, capacitacion, biblioteca, legal };
}
const cacheIndice = new Map();
export async function indiceLibro(docId) {
  if (!cacheIndice.has(docId)) cacheIndice.set(docId, get(`indice/${docId}.json`).then((j) => j.paginas));
  return cacheIndice.get(docId);
}
// Busca en qué página del PDF aparece un artículo ("Art. 190", "Arts. 175, 177"...). Si no lo encuentra, usa la página indicada.
export async function paginaDeArticulo(docId, textoArticulo, porDefecto = 1) {
  try {
    const m = /(\d+(?:-[A-Z])?)/.exec(textoArticulo || "");
    if (!m) return porDefecto;
    const num = m[1].replace(/[-.\\^$*+?()|[\]{}]/g, "\\$&");
    const paginas = await indiceLibro(docId);
    const re = new RegExp(`\\bart(?:[ií]culo|\\.)\\s*${num}\\s*\\.?\\s*[-–]`, "i");
    const i = paginas.findIndex((p) => re.test(p));
    return i >= 0 ? i + 1 : porDefecto;
  } catch { return porDefecto; }
}
