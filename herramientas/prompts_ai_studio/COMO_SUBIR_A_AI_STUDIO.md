# Cómo subir el Bloque C a AI Studio (sin .zip)

> **Actualización:** el código ya viene escrito DENTRO de los Prompts 8 y 9. Solo hay que subir los archivos de datos (.json): 13 con el Prompt 8 (el índice y los 12 `_pdf.json`) y 10 con el Prompt 9 (8 bancos de leyes más `banco_codigo_electoral.json` y `banco_constitucion.json`). No hace falta subir ningún `.kt`.

AI Studio no acepta archivos .zip. Hay tres formas; la primera es la más fácil:

## Opción 1 (recomendada): por GitHub
Todo ya está en el repositorio, en la rama `claude/electoral-knowledge-base-json-ha2mpw`. Si tu proyecto de AI Studio está conectado a GitHub, trae esa rama (o pasa sus cambios a la rama que usa AI Studio) y AI Studio verá todos los archivos nuevos sin subir nada a mano. Después pega solo los Prompts 8 y 9 para que revise y compile.

## Opción 2: archivos sueltos
Sube los archivos de la carpeta `entrega_bloque_C_planos` (sin subcarpetas) y pega los Prompts 8 y 9. Cada prompt dice qué archivos usar y a qué ruta van. Puedes subirlos en grupos (varios archivos a la vez). Rutas de destino:

| Archivo | Ruta en el proyecto |
|---|---|
| `BookSearch.kt` | `app/src/main/java/com/example/data/BookSearch.kt` |
| `BookInternalSearchEngine.kt` | `app/src/main/java/com/example/data/BookInternalSearchEngine.kt` |
| `BookSearchUi.kt` | `app/src/main/java/com/example/ui/BookSearchUi.kt` |
| `BuscarEnLibros.kt` | `app/src/main/java/com/example/ui/BuscarEnLibros.kt` |
| `ElectoralCodeBank.kt` | `app/src/main/java/com/example/data/search/codigo/ElectoralCodeBank.kt` |
| `ElectoralCodeSearch.kt` | `app/src/main/java/com/example/data/search/codigo/ElectoralCodeSearch.kt` |
| `LawLibrary.kt` | `app/src/main/java/com/example/data/search/codigo/LawLibrary.kt` |
| `libros_indice_completo.json` | `app/src/main/assets/libros_indice_completo.json` |
| `acuerdo_legislativo_reforma_suprecion_segunda_eleccion_pdf.json` | `app/src/main/assets/libros_palabras/acuerdo_legislativo_reforma_suprecion_segunda_eleccion_pdf.json` |
| `ciclo_electoral_salvadoreno_pdf.json` | `app/src/main/assets/libros_palabras/ciclo_electoral_salvadoreno_pdf.json` |
| `codigo_electoral_pdf.json` | `app/src/main/assets/libros_palabras/codigo_electoral_pdf.json` |
| `codigo_penal_pdf.json` | `app/src/main/assets/libros_palabras/codigo_penal_pdf.json` |
| `constitucion_pdf.json` | `app/src/main/assets/libros_palabras/constitucion_pdf.json` |
| `disposciones_especiales_candidaturas_no_partidarias_pdf.json` | `app/src/main/assets/libros_palabras/disposciones_especiales_candidaturas_no_partidarias_pdf.json` |
| `instructivo_jrv_pdf.json` | `app/src/main/assets/libros_palabras/instructivo_jrv_pdf.json` |
| `ley_de_acceso_a_la_informacion_publica_pdf.json` | `app/src/main/assets/libros_palabras/ley_de_acceso_a_la_informacion_publica_pdf.json` |
| `ley_de_partidos_politicos_pdf.json` | `app/src/main/assets/libros_palabras/ley_de_partidos_politicos_pdf.json` |
| `ley_especial_para_el_ejercicio_del_sufragio_en_el_extranjero_pdf.json` | `app/src/main/assets/libros_palabras/ley_especial_para_el_ejercicio_del_sufragio_en_el_extranjero_pdf.json` |
| `ley_especial_para_la_reestructuracion_municipal_pdf.json` | `app/src/main/assets/libros_palabras/ley_especial_para_la_reestructuracion_municipal_pdf.json` |
| `reglamento_general_para_la_observacion_electoral_nacional_e_internacional_en_el_salvador_pdf.json` | `app/src/main/assets/libros_palabras/reglamento_general_para_la_observacion_electoral_nacional_e_internacional_en_el_salvador_pdf.json` |
| `acuerdo_legislativo.json` | `app/src/main/assets/leyes/acuerdo_legislativo.json` |
| `candidaturas_no_partidarias.json` | `app/src/main/assets/leyes/candidaturas_no_partidarias.json` |
| `codigo_penal_electoral.json` | `app/src/main/assets/leyes/codigo_penal_electoral.json` |
| `ley_acceso_informacion.json` | `app/src/main/assets/leyes/ley_acceso_informacion.json` |
| `ley_partidos.json` | `app/src/main/assets/leyes/ley_partidos.json` |
| `ley_reestructuracion_municipal.json` | `app/src/main/assets/leyes/ley_reestructuracion_municipal.json` |
| `ley_sufragio_exterior.json` | `app/src/main/assets/leyes/ley_sufragio_exterior.json` |
| `reglamento_observacion.json` | `app/src/main/assets/leyes/reglamento_observacion.json` |
| `banco_codigo_electoral.json` | `app/src/main/assets/codigo_electoral/banco_codigo_electoral.json` |
| `banco_constitucion.json` | `app/src/main/assets/constitucion/banco_constitucion.json` |

## Opción 3: pegar en el prompt
Los archivos de código (.kt) son pequeños: si AI Studio no te deja subirlos, pégalos como texto en el chat con la ruta de destino. Los JSON grandes (índice, palabras, leyes) sí hay que subirlos como archivos o traerlos por GitHub.
