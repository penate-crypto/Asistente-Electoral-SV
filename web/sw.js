// Service worker sencillo: guarda la aplicación y los datos para abrirla más rápido y, tras la primera visita, también sin conexión.
const VERSION = "aesv-web-1";
const BASE = ["./", "index.html", "css/app.css", "manifest.webmanifest", "img/logo.png", "img/isotipo.png"];
self.addEventListener("install", (e) => { e.waitUntil(caches.open(VERSION).then((c) => c.addAll(BASE)).then(() => self.skipWaiting())); });
self.addEventListener("activate", (e) => { e.waitUntil(caches.keys().then((ks) => Promise.all(ks.filter((k) => k !== VERSION).map((k) => caches.delete(k)))).then(() => self.clients.claim())); });
self.addEventListener("fetch", (e) => {
  const req = e.request;
  if (req.method !== "GET" || new URL(req.url).origin !== location.origin) return;
  // Red primero (siempre lo más nuevo); si no hay red, lo guardado.
  e.respondWith(fetch(req).then((r) => { if (r.ok && r.status === 200) { const copia = r.clone(); caches.open(VERSION).then((c) => c.put(req, copia)); } return r; }).catch(() => caches.match(req).then((m) => m || caches.match("index.html"))));
});
