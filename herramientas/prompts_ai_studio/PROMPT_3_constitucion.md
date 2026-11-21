# PROMPT 3 para AI Studio — Banco Constitución de la República

Hazlo DESPUÉS del Prompt 2 (usa las mismas clases). Adjunta `banco_constitucion.json`.

---

Quiero agregar un TERCER banco: la Constitución de la República (274 artículos), igual que el Código Electoral. Sin internet y sin tokens. No cambies nada más.

1) Crea `app/src/main/assets/constitucion/banco_constitucion.json` con el archivo adjunto, sin modificarlo.

2) Las clases `ElectoralCodeBank.kt` y `ElectoralCodeSearch.kt` del Prompt 2 YA incluyen la Constitución (`ElectoralConstitutionBank` y `ElectoralConstitutionSearch`). Si pegaste una versión anterior, reemplaza ambos archivos por las versiones del Prompt 2 actualizado (ahora el banco es una clase `LawBank` y existen dos búsquedas).

3) En `ElectoralKnowledgeBase.init(context)` agrega:

```kotlin
com.example.data.search.codigo.ElectoralConstitutionBank.init(context)
```

4) Donde la pantalla de chat/voz hace la búsqueda, el orden debe ser:

```kotlin
val codigo = ElectoralCodeSearch.search(pregunta)            // "código electoral ..."
val constitucion = if (codigo == null) ElectoralConstitutionSearch.search(pregunta) else null   // "constitución ..."
val resultadoLey = codigo ?: constitucion
if (resultadoLey != null) { /* mostrar tarjetas de artículos (mismo diseño del Código Electoral) */ }
else { /* flujo normal */ }
```

5) Las tarjetas son las mismas del Código Electoral, cambiando el encabezado: "🏛 Constitución · Art. N". Para la Constitución, `pdfDocId` es "constitucion_republica_1983" y el botón abre `libros/constitucion.pdf` en `paginaPdf`. El aviso final: "Texto tomado de la Constitución de la República. Verifícalo con el PDF de la biblioteca." Importante: la Constitución del PDF no trae títulos al margen; el campo `titulo` es el nombre de la sección o un título corto, y los artículos reformados vienen en MAYÚSCULAS en el PDF y sin tildes (por eso se muestra `textoLegible`, y el enlace "Ver texto original del PDF" muestra `textoLiteral`).

6) Pruebas: "constitución artículo 72" (derechos políticos), "constitución tribunal supremo electoral" (Art. 208), "en la constitución dónde dice los deberes del ciudadano" (Art. 73), "constitución artículos 75 al 77" (3 tarjetas), "constitución pastores no pueden pertenecer a partidos" (Art. 82). Y "código electoral hora de cierre" debe seguir mostrando solo el Art. 198.

Resultados de mis pruebas del motor real: 42/42 en preguntas usadas para ajustar; en prueba ciega de 28 preguntas nuevas, 24 aciertan el artículo en primer lugar.
