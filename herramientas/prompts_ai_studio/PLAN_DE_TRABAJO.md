# Plan de trabajo — Asistente Electoral SV

Estado: **A**, **B** y **V** hechos en el repositorio (falta aplicarlos en AI Studio con los Prompts 5, 6 y 7). Pendiente: C.

## Decisiones tomadas
- Subrayado amarillo también **sobre la imagen del PDF** (se genera la posición de cada palabra).
- Código Penal: **solo delitos electorales**. Instructivo y Ciclo Electoral: **no** como bancos del asistente (el Instructivo sí se podrá buscar dentro de su lector PDF).
- **Botón "Buscar en libros"** dentro del chat (sin palabras de activación).
- La Ley de Género no se incluye.
- La app no usa IA: ningún texto visible debe decirlo.

## Bloques
**A. Quitar "IA" de la interfaz** — HECHO en el repositorio (Android y web). Pendiente: aplicar Prompt 5 en AI Studio. Queda para el bloque de limpieza: eliminar el código sin uso `data/rag` y `data/api` (no se borró aún por no poder compilar aquí).

**V. Voz sin conexión** — HECHO en el repositorio (Prompt 7)
1. Pedir al reconocedor del sistema que prefiera el modo sin conexión (`EXTRA_PREFER_OFFLINE`); probar `es-SV`, y si no hay paquete, `es-419`/`es-US`.
2. Declarar `ACCESS_NETWORK_STATE` **solo junto con** este cambio y NO bloquear el micrófono cuando no hay red: intentar primero sin conexión; si falla por red, mostrar el mensaje y ofrecer escribir.
3. Guía en Ajustes: cómo descargar la voz en español sin conexión.
4. Probar en modo avión en 2 o 3 teléfonos de marcas distintas. Si falla seguido, evaluar un reconocedor integrado (Vosk).

**B. Términos y política de privacidad**
1. Decidir el login (quitarlo hasta que exista servidor, recomendado, o documentarlo como pantalla local).
2. Reescribir "cómo funciona": algoritmo local, sin IA, sin agentes, sin enviar preguntas.
3. Sección "Cómo se creó": AI Studio y Gemini (programación y banco), ChatGPT y Claude (prompts, imágenes, PDF), Claude Code (actualización del banco y mejoras), aclarando que ninguna funciona dentro de la app.
4. Voz y datos locales (el dictado usa el reconocedor del sistema; qué se guarda en el teléfono).
5. Quitar la LEIV, unificar nombre/versión/fecha, contacto real, crédito del creador.
6. Cláusula para cuando exista login con servidor.
7. Subir la versión de los textos y hacer que la app pida aceptar de nuevo (`isConsentAccepted` hoy ignora la versión).
8. Revisión legal profesional antes de publicar.

**C. Buscador de PDF y libros**
1. Índice con texto real de 11 PDF (incluye Instructivo; sin Ley de Género).
2. Buscador nuevo: "artículo N" muestra solo ese artículo; palabra completa, sin tildes, sin palabras vacías, en segundo plano.
3. Amarillo en resultados, panel "Texto de la página" y sobre la imagen del PDF.
4. Botón "Buscar en libros" en el chat.
5. Bancos por libro (Partidos, Acceso a la Información, Sufragio en el Exterior, Reestructuración, Reglamento de Observación, Candidaturas No Partidarias, Acuerdo legislativo, Código Penal solo delitos electorales), cargados al usarse.
6. Corregir botones de sugerencia con artículos equivocados (284 y 205) y que el lector respete el libro elegido.

**D. Entrega:** un prompt corto por bloque, con el código exacto y los JSON adjuntos, y una lista de comprobación para probar en el teléfono.
