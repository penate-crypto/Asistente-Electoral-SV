# PROMPT 12 para AI Studio — 3 situaciones nuevas en el banco de conocimiento (157 situaciones)

Adjunta 2 archivos: `banco_conocimientos.json` y `sinonimos_globales.json`. Puedes aplicarlo en cualquier momento después del Prompt 1.

---

Actualiza SOLO estos dos archivos de datos. No cambies código.

1) Reemplaza COMPLETO `app/src/main/assets/json respuestas/banco_conocimientos.json` por el archivo adjunto `banco_conocimientos.json` (versión 2.1, 157 situaciones). Cópialo tal cual, sin resumir ni reformatear.

2) Reemplaza COMPLETO `app/src/main/assets/sinonimos/sinonimos_globales.json` por el adjunto (agrega los grupos de sinónimos `mostrar_voto` y `esquina_desprendible`).

Situaciones nuevas:
- `VOT-032` Votante muestra su voto marcado a otras personas (secreto del voto).
- `ESC-026` Papeleta con la esquina desprendible todavía pegada en el escrutinio.
- `VIG-016` Vigilante quiere retirarse o dejar sus funciones en medio del escrutinio.

Pruebas en el chat (deben mostrar esa situación):
- «un votante muestra su voto» y «una señora mostró por quién votó» → VOT-032.
- «en el escrutinio se encuentra un voto que aún tiene la esquina desprendible» → ESC-026.
- «vigilante quiere dejar sus funciones en medio del escrutinio» → VIG-016.
- «se encuentra papeletas de otra JRV» → ESC-025 (ya existía).
- Comprueba que preguntas anteriores siguen igual, por ejemplo «persona bolo» (VOT-005) y «el votante olvidó su DUI» (DUI-001).
