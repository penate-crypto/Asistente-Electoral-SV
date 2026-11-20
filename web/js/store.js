// Almacenamiento local seguro (si el navegador lo bloquea, la app sigue funcionando en memoria).
const mem = new Map();
export const store = {
  get(k, def = null) {
    try { const v = localStorage.getItem("aesv." + k); return v === null ? (mem.has(k) ? mem.get(k) : def) : JSON.parse(v); }
    catch { return mem.has(k) ? mem.get(k) : def; }
  },
  set(k, v) { mem.set(k, v); try { localStorage.setItem("aesv." + k, JSON.stringify(v)); } catch { /* sin almacenamiento */ } },
  del(k) { mem.delete(k); try { localStorage.removeItem("aesv." + k); } catch { /* nada */ } },
};
