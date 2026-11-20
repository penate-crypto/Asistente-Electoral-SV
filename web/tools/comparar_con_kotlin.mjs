// Compara el buscador JavaScript contra un volcado del buscador Kotlin de la app (formato .jsonl).
// Uso: node tools/comparar_con_kotlin.mjs ruta/kotlin_ref.jsonl
import fs from "node:fs";
import { construirSituaciones, MotorElectoral } from "../js/engine.js";
const banco = JSON.parse(fs.readFileSync(new URL("../data/banco_conocimientos.json", import.meta.url), "utf8"));
const motor = new MotorElectoral(construirSituaciones(banco));
const lineas = fs.readFileSync(process.argv[2], "utf8").split("\n").filter((l) => l.trim());
let ok = 0, malos = [];
const t0 = performance.now();
for (const l of lineas) {
  const ref = JSON.parse(l);
  const r = motor.search(ref.q);
  const alts = r.clarificationOptions.map((s) => s.id);
  const dif = [];
  if (r.matchLevel !== ref.level) dif.push(`nivel ${r.matchLevel} vs ${ref.level}`);
  if ((r.topSituation?.id ?? null) !== ref.top) dif.push(`top ${r.topSituation?.id} vs ${ref.top}`);
  if (Math.abs(r.score - ref.score) > 1e-9) dif.push(`score ${r.score} vs ${ref.score}`);
  if (JSON.stringify(alts) !== JSON.stringify(ref.alts)) dif.push(`alts ${alts} vs ${ref.alts}`);
  if (r.isOffTopic !== ref.offTopic) dif.push("offTopic");
  if (r.formattedResponse !== ref.text) dif.push("texto distinto");
  if (JSON.stringify(Object.entries(r.scoreBreakdown)) !== JSON.stringify(Object.entries(ref.bd))) dif.push("desglose distinto");
  if (dif.length) malos.push({ q: ref.q.slice(0, 80), dif }); else ok++;
}
const ms = performance.now() - t0;
console.log(`Coinciden: ${ok} / ${lineas.length}   (${(ms / lineas.length).toFixed(2)} ms por consulta)`);
for (const m of malos.slice(0, 25)) console.log(" -", JSON.stringify(m));
process.exit(malos.length ? 1 : 0);
