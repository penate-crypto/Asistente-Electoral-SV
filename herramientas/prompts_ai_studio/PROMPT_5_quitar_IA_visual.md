# PROMPT 5 para AI Studio — Quitar toda mención visual de "IA" (Bloque A del plan)

Copia todo lo que está debajo de la línea.

---

Esta app NO usa inteligencia artificial, ni agentes, ni asistentes externos (ni Gemini, ni ChatGPT, ni Claude). Responde solo con un algoritmo de búsqueda local sobre un banco de conocimiento. Necesito que NINGÚN texto visible diga que usa IA. Haz SOLO estos cambios de texto (no cambies lógica, nombres de clases, ni diseño). Cada texto debe quedar exactamente así:

| Archivo (app/src/main/java/com/example/…) | Texto actual | Texto nuevo |
|---|---|---|
| ui/ElectoralMainScreen.kt (2 veces: barra superior y menú lateral) | `Asistente Virtual Electoral` | `Asistente Electoral` |
| ui/ElectoralMainScreen.kt | `App de IA para apoyo electoral` | `App de apoyo electoral` |
| ui/ElectoralMainScreen.kt | `El Salvador - Apoyo electoral IA` | `El Salvador - Apoyo electoral` |
| ui/ElectoralMainScreen.kt (2 etiquetas y 1 contentDescription) | `Asistente IA` | `Asistente` |
| ui/LegalNoticeScreen.kt | `Consulta cómo se procesa la información y la IA.` | `Consulta cómo funciona la aplicación y cómo se tratan tus datos.` |
| ui/CapacitacionScreen.kt | `Consultar a la IA sobre este tema` | `Consultar en el asistente sobre este tema` |
| ui/SimulacionScreen.kt | `Consultar este caso con el Asistente IA` | `Consultar este caso en el asistente` |
| ui/SimuladorScreen.kt | `Consultar dudas de este caso a la IA` | `Consultar dudas de este caso en el asistente` |
| ui/BookInternalSearchScreen.kt | `Preguntar a IA` | `Preguntar al asistente` |
| ui/PasoAPasoScreen.kt | `Consultar detalles a la IA` | `Consultar detalles en el asistente` |
| ui/BibliotecaScreen.kt | `...visor PDF y consulta con IA` | `...visor PDF y consulta en el asistente` |
| ui/BibliotecaScreen.kt | `CONSULTAR IA` (y los comentarios `[ CONSULTAR CON IA ]`) | `CONSULTAR ASISTENTE` |
| ui/LoginScreen.kt | `Capacitador Electoral SV` (título y contentDescription del logo) | `Asistente Electoral SV` |
| MainActivity.kt (2 veces) | `No hay conexión disponible para consultar la IA.` | `El reconocimiento de voz necesita conexión a internet en este teléfono. Puedes escribir tu pregunta.` |
| MainActivity.kt | `Procesando respuesta con IA...` | `Buscando la respuesta...` |

Después de aplicarlos, busca en TODO el proyecto (archivos .kt y .xml, sin contar `data/rag` y `data/api`, que no se usan) las palabras: `IA`, `inteligencia artificial`, `Gemini`, `ChatGPT`, `Claude`, `Virtual Electoral`, `chatbot`. No debe quedar ninguna en un texto que vea el usuario. Dime qué encontraste.

NO toques todavía: `LegalConsentManager.kt` (términos y política; se reescribirán aparte), el permiso INTERNET ni `ACCESS_NETWORK_STATE`, ni el código de `data/rag` y `data/api`.

Pruebas: abre la app y revisa la barra superior ("Asistente Electoral"), el menú lateral, las pestañas inferiores ("Asistente"), Biblioteca (botón "CONSULTAR ASISTENTE"), Capacitación, Simulación, Paso a paso, la pantalla de acceso y la voz sin conexión (debe mostrar el mensaje nuevo).
