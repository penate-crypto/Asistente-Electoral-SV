# Asistente Electoral SV — versión web (prueba)

Versión web de la app: se abre desde el navegador de cualquier celular o computadora (incluido iPhone).
Es **independiente de la app Android**: no modifica nada de la carpeta `app/`.

## Qué incluye
Las mismas secciones de la app: **Asistente** (chat con el banco de conocimientos), **Biblioteca** (12 PDF con visor y buscador por palabra o artículo),
**Test** (casos y soluciones + examen de 25 preguntas al azar), **Paso a Paso**, **Capacitación**, **Ajustes** (tema, tamaño de texto, test de 125 preguntas, textos legales, borrar historial),
inicio de sesión, aviso legal, modo claro/oscuro, voz (micrófono y lectura en voz alta) y funcionamiento sin conexión después de la primera visita.

El buscador es una traducción a JavaScript del buscador de la app Android. Corre dentro del navegador:
**no usa servidor, no usa Gemini y no gasta tokens.**

## Cómo verla en tu computadora
Necesitas Python 3 (o cualquier servidor de archivos estáticos). Desde esta carpeta:

    python3 -m http.server 8000

y abre http://localhost:8000 . (No funciona abriendo `index.html` con doble clic, porque el navegador bloquea la carga de datos.)

## Cómo publicarla para probarla desde tu celular
Es una carpeta de archivos estáticos: sirve cualquier hosting gratuito.
- **Netlify Drop:** entra a app.netlify.com/drop y arrastra la carpeta `web/`. Te da un enlace.
- **Vercel / Cloudflare Pages / Firebase Hosting:** crea un proyecto y apunta a esta carpeta (sin comando de compilación).
- **GitHub Pages:** en el repositorio, *Settings → Pages*, elige la rama y la carpeta. Si la carpeta `web/` no se puede elegir, copia su contenido a `/docs`.
Debe servirse con **HTTPS** para que el micrófono y la instalación como app funcionen (todos los servicios anteriores lo hacen).

## Cómo se regenera el contenido
Los datos salen del código de la app y de la carpeta de libros; no hay que escribirlos dos veces:

    python3 tools/extraer_tablas.py     # tablas del buscador  -> js/engine-data.js
    python3 tools/extraer_datos.py      # test, casos, paso a paso, biblioteca, textos legales, PDF e índice de texto -> data/ y libros/
    cp "../app/src/main/assets/json respuestas/banco_conocimientos.json" data/banco_conocimientos.json

## Pruebas
- `node tools/comparar_con_kotlin.mjs <(gunzip -c tools/referencia/kotlin_ref.jsonl.gz)` compara el buscador web con el de la app Android en 2,174 preguntas (nivel, ficha, puntaje, opciones y texto). Resultado actual: **2,174 de 2,174 idénticas**.
- `node tools/prueba_navegador.cjs <carpeta_capturas> [url]` recorre toda la app en un navegador real (requiere `playwright`). `tools/prueba_sin_internet.cjs` comprueba el modo sin conexión.

## Limitaciones conocidas
- **Voz:** el reconocimiento de voz depende del navegador (Chrome y Edge funcionan; Safari de iPhone es irregular) y normalmente necesita internet. La lectura en voz alta funciona en casi todos.
- **Inicio de sesión de prueba:** acepta cualquier usuario de 9 dígitos con contraseña de 6 o más caracteres (igual que la app actual). **No protege nada**: todos los archivos de la página son públicos. Para usuarios reales hace falta un servidor.
- **No oficial:** los textos legales son los de la app Android y mencionan Gemini y funciones de Android; hay que revisarlos y adaptarlos antes de publicar la versión web de forma abierta.
- La búsqueda documental de respaldo del buscador Kotlin (páginas de ejemplo de `ElectoralPdfData.kt`) no se incluyó; los libros reales se buscan desde el visor.
- El visor de PDF usa pdf.js 3.11.174 (Apache-2.0), incluido en `vendor/pdfjs/`.
