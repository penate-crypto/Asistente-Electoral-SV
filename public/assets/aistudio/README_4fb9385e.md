# Banco de conocimientos en JSON — Asistente Electoral SV

La app lee **todas** las situaciones de `app/src/main/assets/json respuestas/*.json`
(clase `JsonKnowledgeLoader.kt`). Si esa carpeta tiene situaciones, el motor usa solo
ese banco. Si está vacía, usa la lista de respaldo escrita en `ElectoralKnowledgeBase.kt`.

## Contenido actual (versión 2.0)

154 situaciones. Cada cita legal se comprobó contra el texto de los PDF de la biblioteca de la app.

| Categoría | Situaciones | Prefijos de ID |
|---|---|---|
| Instalación | 12 | `INST-` |
| Votación | 31 | `VOT-` |
| Identificación (DUI) | 8 | `DUI-` |
| Seguridad | 16 | `ARM-`, `SEG-` |
| Vigilancia (vigilantes, supervisores, jefes de centro) | 15 | `VIG-` |
| Escrutinio | 25 | `ESC-` |
| Roles (paso a paso por cargo, JEM, JED, TSE, FGR, PNC, JVE) | 12 | `ROL-` |
| Organismos electorales (integración, excusas, multas, permisos) | 9 | `OET-` |
| Recursos y nulidades | 7 | `REC-` |
| Delitos electorales | 4 | `DEL-` |
| Proceso electoral (propaganda, encuestas) | 4 | `PRO-` |
| Registro electoral | 4 | `REG-` |
| Marco electoral (cargos, reforma 2025, 44 municipios, voto en el exterior) | 7 | `ELE-` |

Las fuentes usadas son el Código Electoral, la Constitución y la reforma constitucional de 2025,
el Código Penal (Art. 295), el Instructivo JRV del TSE (2024), la Ley Especial de Voto en el
Extranjero, el Reglamento de Observación Electoral, la Ley de Reestructuración Municipal, las
Disposiciones de Candidaturas No Partidarias y el Ciclo Electoral del TSE.

## Correcciones al banco anterior

* **Citas equivocadas.** `ESC-001` atribuía el cierre a las 17:00 al Art. 207. En el PDF, el
  Art. 207 regula los votos nulos y el cierre está en el Art. 198. Las 7 situaciones originales
  se reescribieron con citas verificadas.
* **IDs que el motor no reconocía.** `ElectoralScoringEngine.kt` da puntos extra a IDs concretos:
  `INST-001`, `DUI-002`, `ARM-001`, `ARM-002`, `ARM-010` y `ESC-001`. El banco anterior usaba
  `JRV-001`, por lo que esos puntos extra nunca se aplicaban. Ahora los IDs coinciden.

## Estructura de cada situación

```jsonc
{
  "id": "VOT-002",                       // único. Respete los IDs con puntos extra (ver arriba)
  "titulo": "...",                        // pesa MUCHO (+12 por palabra de la pregunta que contiene)
  "categoria": "Votación", "subcategoria": "Padrón y Documentos",
  "descripcion_situacion": "...",         // +8 por palabra de la pregunta que contiene
  "actores": ["Presidente de JRV", "Votante"],      // +10 c/u (presidente, secretario, vocal, vigilante, pnc, fgr...)
  "objetos": ["DUI", "Padrón de búsqueda"],         // +8 c/u (dui, papeleta, padron, tinta, acta, urna, celular, arma)
  "acciones": ["Denegar voto"],                       // +8 c/u
  "contextos": ["identificacion_elector", "proceso_votacion"], // etiquetas del motor (ver validar_banco.py)
  "intenciones": ["procedimiento", "consulta_permiso_prohibicion"],
  "palabras_clave": ["no aparece en el padron", "no sale en la lista"], // +6 c/u; frases como las diría la gente
  "sinonimos": {"padron": ["lista de votantes", "listado"]},           // +4 c/u
  "respuesta": {
    "situacion_detectada": "...",
    "que_hacer": ["paso 1", "paso 2"],
    "que_no_hacer": ["NO ..."],
    "quien_interviene": ["JEM", "TSE"],
    "quien_puede": [], "quien_no_puede": [], "condiciones": [],   // opcionales
    "advertencia": "...",                                           // opcional: reformas, datos que cambian por elección
    "fundamento_legal": [
      {"ley": "Código Electoral de El Salvador",
       "articulos": [{"numero": "196", "fuente": "qué dice el artículo"}]}  // "numero" SIN "Art."
    ],
    "fuentes": [
      {"nombre": "Código Electoral de El Salvador", "tipo": "Ley", "pagina": "66",
       "seccion": "Art. 196", "descripcion": "...", "documento_relacionado": "...",
       "ubicacion_pdf": "libros/Codigo-Electoral.pdf",
       "pdf_doc_id": "codigo_electoral_decreto_413"}   // id del libro en ElectoralPdfData.kt
    ]
  },
  "situaciones_relacionadas": ["REG-002", "VOT-008"],   // se muestran como temas relacionados
  "nivel_prioridad": "alta", "estado": "vigente"          // "obsoleto" la excluye de la búsqueda
}
```

`pagina` es la página **del PDF** de la biblioteca de la app, calculada automáticamente.

## Cómo agregar o corregir situaciones

**Opción A: editar el JSON directamente** (por ejemplo, desde AI Studio). Después ejecute:

```bash
python3 herramientas/banco_conocimientos/validar_banco.py
```

El validador revisa la estructura, que no haya IDs duplicados, que las situaciones relacionadas
existan y que cada artículo citado aparezca en el PDF de su ley.

**Opción B: usar el generador** (recomendada para un ingeniero). Las fichas están en
`fuente/k*.py`. El generador calcula las páginas y no genera el archivo si alguna cita no se
encuentra en el PDF:

```bash
python3 herramientas/banco_conocimientos/generar_banco.py
```

Requiere `pdftotext` (paquete `poppler-utils`). Si usa el generador, no edite el JSON a mano:
la siguiente generación sobrescribirá esos cambios.

## Probar el motor de búsqueda real

`prueba_motor/` compila los archivos Kotlin del buscador de la app en la JVM, sin necesitar
Android, y pasa baterías de preguntas por el motor:

```bash
cd herramientas/banco_conocimientos/prueba_motor
gradle -q run --args="preguntas/01_regresion.txt"
DBG=1 gradle -q run --args="preguntas/03_parafraseo_b.txt"   # muestra el desglose de puntos
```

Cada línea de pregunta tiene el formato `ID_ESPERADO[,ALTERNATIVO]|pregunta`.
Use `-` como ID esperado cuando la pregunta debe rechazarse por estar fuera de tema.

Resultados con este banco:

| Batería | Banco anterior (7 situaciones) | Banco nuevo (154) |
|---|---|---|
| `01_regresion.txt`: una pregunta por tema principal | 1 / 54 (en las primeras 54) | 140 / 140 |
| `02_parafraseo_a.txt`: preguntas con otras palabras | — | 37 / 37 (después de ajustar) |
| `03_parafraseo_b.txt`: preguntas nuevas | — | 21 / 41 antes de ajustar, 35 / 41 después |

La tercera batería da la medida más honesta: ante frases que no se habían visto, el motor
acertó alrededor de la mitad de las veces antes de los ajustes. Después de cada ajuste,
`01_regresion.txt` se volvió a correr para confirmar que nada dejó de funcionar.

## Límites del motor (código, no datos)

Esto no se arregla con más JSON:

1. **Coincidencia por fragmentos de palabra.** El título suma puntos si contiene una palabra de
   la pregunta aunque sea parte de otra palabra. Por ejemplo, "se **arma** la mesa" suma puntos
   a las situaciones de armas.
2. **Detector de intención.** `ElectoralEntityExtractor.detectIntent` busca la palabra "papel"
   y por eso clasifica cualquier pregunta con "papeleta" como pregunta de funciones.
3. **Filtro de temas ajenos.** Rechaza algunas preguntas electorales válidas. Por ejemplo,
   "música de un partido afuera de la escuela" se descarta por la palabra "música".
4. **Frases nuevas.** Es la limitación principal. La mejor solución es que Gemini reformule la
   pregunta del usuario con el vocabulario del banco antes de buscar (el paso de reestructurar
   la pregunta del "mapa de respuesta"), o agregar búsqueda por similitud de significado
   (*embeddings*).

## Datos que debe revisar en cada elección

`DUI-005` (último número de DUI válido), `DUI-007` (DUI vencido), `INST-008` (hora de voto de la
JRV), `ESC-016` (colores de las copias del acta) y `ELE-002` (reforma de 2025 y elecciones de
2027) tienen una advertencia porque dependen del instructivo o del calendario del TSE vigente.
