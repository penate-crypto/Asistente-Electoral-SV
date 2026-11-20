// Voz del navegador: lectura de respuestas (speechSynthesis) y reconocimiento de voz (SpeechRecognition).
// El reconocimiento depende del navegador (Chrome/Edge/Safari recientes) y normalmente necesita internet.
const SR = window.SpeechRecognition || window.webkitSpeechRecognition;
export const vozSoportada = { hablar: "speechSynthesis" in window, escuchar: !!SR };

function elegirVoz() {
  const voces = window.speechSynthesis?.getVoices?.() || [];
  const pref = ["es-SV", "es-MX", "es-419", "es-US", "es-ES"];
  for (const p of pref) { const v = voces.find((x) => x.lang.replace("_", "-").toLowerCase() === p.toLowerCase()); if (v) return v; }
  return voces.find((x) => x.lang.toLowerCase().startsWith("es")) || null;
}

export function textoParaVoz(texto) {
  return texto
    .split("\nFUENTES\n")[0]
    .replace(/\*\*/g, "")
    .replace(/[\u{1F300}-\u{1FAFF}\u{2600}-\u{27BF}\u{FE0F}]/gu, "")
    .replace(/^- /gm, "").replace(/•/g, "")
    .replace(/Art\. ?(\d+)/g, "Artículo $1")
    .replace(/\n{2,}/g, ". ").replace(/\n/g, ". ");
}
function trozos(texto, max = 180) {
  const frases = texto.match(/[^.!?;:]+[.!?;:]?\s*/g) || [texto];
  const out = []; let cur = "";
  for (const f of frases) {
    if ((cur + f).length > max && cur) { out.push(cur.trim()); cur = ""; }
    if (f.length > max) { for (let i = 0; i < f.length; i += max) out.push(f.slice(i, i + max).trim()); }
    else cur += f;
  }
  if (cur.trim()) out.push(cur.trim());
  return out.filter(Boolean);
}

export class Lector {
  constructor(onEstado) { this.onEstado = onEstado || (() => {}); this.estado = "IDLE"; this.cola = []; this.i = 0; this.texto = ""; }
  _set(e) { this.estado = e; this.onEstado(e); }
  hablar(texto) {
    if (!vozSoportada.hablar) return false;
    this.detener(true);
    this.texto = texto; this.cola = trozos(texto); this.i = 0; this._set("PLAYING"); this._siguiente();
    return true;
  }
  _siguiente() {
    if (this.estado !== "PLAYING") return;
    if (this.i >= this.cola.length) { this._set("IDLE"); return; }
    const u = new SpeechSynthesisUtterance(this.cola[this.i]);
    const v = elegirVoz(); if (v) { u.voice = v; u.lang = v.lang; } else u.lang = "es-MX";
    u.rate = 1; u.onend = () => { if (this.estado === "PLAYING") { this.i++; this._siguiente(); } };
    u.onerror = () => { if (this.estado === "PLAYING") this._set("IDLE"); };
    speechSynthesis.speak(u);
  }
  pausar() { if (this.estado === "PLAYING") { speechSynthesis.pause(); this._set("PAUSED"); } }
  reanudar() { if (this.estado === "PAUSED") { speechSynthesis.resume(); this._set("PLAYING"); } }
  detener(silencioso) { try { speechSynthesis.cancel(); } catch { /* nada */ } this.cola = []; if (!silencioso || this.estado !== "IDLE") this._set("IDLE"); }
  reiniciar() { if (this.texto) this.hablar(this.texto); }
}

export class Oyente {
  constructor() { this.rec = null; this.activo = false; }
  escuchar({ onTexto, onEstado, onError }) {
    if (!SR) { onError("Tu navegador no permite reconocimiento de voz. Prueba con Chrome, Edge o Safari actualizado, o escribe tu consulta."); return; }
    this.detener();
    const rec = new SR(); this.rec = rec;
    rec.lang = "es-SV"; rec.interimResults = false; rec.maxAlternatives = 1; rec.continuous = false;
    let recibido = false;
    rec.onstart = () => { this.activo = true; onEstado("LISTENING"); };
    rec.onresult = (e) => { const t = e.results?.[0]?.[0]?.transcript?.trim(); if (t) { recibido = true; onTexto(t); } };
    rec.onerror = (e) => {
      const m = { "not-allowed": "Se requiere permiso de micrófono para conversar por voz.", "service-not-allowed": "El navegador no permite usar el micrófono en esta página.",
        "no-speech": "No pude escuchar nada. Intenta nuevamente.", "network": "No hay conexión disponible para el reconocimiento de voz.", "language-not-supported": "El idioma es-SV no está disponible en este navegador." }[e.error];
      if (e.error === "aborted") { onEstado("IDLE"); return; }
      onError(m || "No pude entender la consulta. Intenta nuevamente.");
    };
    rec.onend = () => { this.activo = false; if (!recibido) onEstado("IDLE"); };
    try { rec.start(); } catch { onError("No fue posible activar el micrófono. Intenta nuevamente."); }
  }
  detener() { try { this.rec?.abort(); } catch { /* nada */ } this.activo = false; }
}
