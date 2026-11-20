# Banco Código Electoral

Banco separado con **los 297 artículos del Código Electoral** (Decreto 413), tomados del PDF que ya está
en la biblioteca de la app. Archivo: `app/src/main/assets/codigo_electoral/banco_codigo_electoral.json`.

Cuando el usuario escribe "código electoral", la búsqueda va primero a este banco:

* "en el código electoral dónde aparece lo de la JRV" → artículos del tema (hasta 5 + "ver más").
* "código electoral artículo 198" / "artículos 200 al 203" → esos artículos.
* Si el tema solo tiene un artículo claro, se muestra solo ese.
* Si no hay resultado, la app sigue con el banco normal (154 situaciones).

## Campos de cada artículo

`id` (CE-198), `numero`, `titulo` (nota al margen del PDF), `titulo_del_codigo`, `capitulo`,
`texto_literal` (como está en el PDF), `texto_legible` (los reformados vienen en MAYÚSCULAS en el PDF; aquí se
pasan a minúsculas normales), `estado` (vigente/derogado), `pagina_pdf`, `palabras_clave`,
`sinonimos` (jerga: "bolo", "guaro"...), `sinonimos_secundarios`, `articulos_citados`,
`situaciones_relacionadas` (IDs del banco principal que citan ese artículo).

## Regenerar y validar

```bash
python3 herramientas/banco_codigo_electoral/extraer_articulos.py
python3 herramientas/banco_codigo_electoral/generar_banco_codigo_electoral.py
python3 herramientas/banco_codigo_electoral/validar_banco_codigo_electoral.py
```

El validador comprueba que cada artículo (inicio y final de su texto) aparezca en la página indicada del PDF.
Para agregar palabras como las diría la gente, edite `EXTRA_SIN` en el generador y vuelva a generarlo.

## Probar el buscador (JVM, sin Android)

```bash
cd herramientas/banco_conocimientos/prueba_motor
MAINCLS=CodeMainKt gradle -q run --args="../../banco_codigo_electoral/preguntas/ce4_prueba_ciega.txt"
```

Resultados: ce1 11/11, ce2 50/50, ce3 32/32 (estas tres sirvieron para ajustar), **ce4 33/41 en prueba ciega**
(en 38 de 41 el artículo esperado aparece entre los primeros 5).

## Límites

Es búsqueda por palabras, no entiende frases nuevas del todo. Los artículos reformados mantienen la nota
de reforma "(8)" del PDF. El Art. 116 tiene una interpretación auténtica (Decreto 870) que no se copió aquí;
está en el PDF. Los 3 artículos derogados (103, 104, 291-A) no salen en búsquedas por tema.
