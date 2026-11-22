# PROMPT 7 para AI Studio — Voz sin conexión + quitar símbolos de IA (bloque de voz)

Hazlo DESPUÉS de los Prompts 5 y 6. Si algún fragmento no coincide exactamente con tu código, adáptalo manteniendo la misma lógica y avísame.

---

Objetivo: que el micrófono pueda funcionar SIN internet cuando el teléfono tenga el español descargado en su reconocimiento de voz, sin bloquear el micrófono por falta de red, y quitar los símbolos que sugieren inteligencia artificial (destellos y cerebro). Haz SOLO estos cambios.

## 1) MainActivity.kt

**a)** Agrega el campo `private var voiceRetryDone = false` junto a `speechRecognizer`.

**b)** En `onError` del `RecognitionListener`, reemplaza la rama de errores de red por esto (son ramas del `when (error)`):

```kotlin
                                    SpeechRecognizer.ERROR_NETWORK,
                                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT,
                                    SpeechRecognizer.ERROR_SERVER,
                                    ERROR_LANGUAGE_NOT_SUPPORTED,
                                    ERROR_LANGUAGE_UNAVAILABLE -> {
                                        // Primer intento: es-SV con preferencia sin conexión. Segundo intento: español latino (es-419).
                                        if (!voiceRetryDone && retryVoiceInLatinSpanish()) {
                                            return@post
                                        }
                                        viewModel.onVoiceError(VOICE_OFFLINE_HELP_MESSAGE)
                                    }
                                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> {
                                        viewModel.onVoiceError("Falta el permiso del micrófono. Actívalo en los ajustes del teléfono.")
                                    }
                                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> {
                                        viewModel.onVoiceError("El reconocimiento de voz está ocupado. Intenta nuevamente en unos segundos.")
                                    }
```

**c)** Reemplaza el comienzo de `startVoiceListeningFlow` (ya NO se bloquea el micrófono por falta de red; se pide el modo sin conexión). Lo que sigue después (`viewModel.startVoiceListening()` y el resto) queda igual y usa `speechIntent`:

```kotlin
    private fun startVoiceListeningFlow(speechIntentLauncher: ActivityResultLauncher<Intent>) {
        // Ya no se bloquea el micrófono cuando no hay internet: el reconocedor del teléfono puede tener el
        // español descargado y trabajar sin conexión. Si no puede, avisa por onError (VOICE_OFFLINE_HELP_MESSAGE).
        voiceRetryDone = false
        val speechIntent = buildSpeechIntent("es-SV")
```

**d)** Agrega estas funciones justo antes de `stopSpeechRecognition()`:

```kotlin
    /** Intent de dictado que prefiere el reconocimiento SIN conexión (si el teléfono tiene el idioma descargado). */
    private fun buildSpeechIntent(languageTag: String): Intent =
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageTag)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languageTag)
            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, languageTag)
            putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "¿Cuál es su consulta electoral?")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

    /** Segundo y último intento con español latino. Devuelve true si logró iniciarlo. */
    private fun retryVoiceInLatinSpanish(): Boolean {
        voiceRetryDone = true
        val recognizer = speechRecognizer ?: return false
        return try {
            recognizer.cancel()
            recognizer.startListening(buildSpeechIntent("es-419"))
            true
        } catch (e: Exception) {
            false
        }
    }
```

**e)** Agrega estas constantes AL FINAL del archivo, fuera de la clase:

```kotlin
// Códigos de SpeechRecognizer disponibles desde Android 12 (se usan como números para que compile en versiones anteriores).
private const val ERROR_LANGUAGE_NOT_SUPPORTED = 12
private const val ERROR_LANGUAGE_UNAVAILABLE = 13

private const val VOICE_OFFLINE_HELP_MESSAGE =
    "No se pudo reconocer la voz sin internet. Descarga el español sin conexión (Ajustes › Voz sin conexión) o escribe tu pregunta."
```

**f)** Elimina la función `isNetworkConnected()` y los imports `android.net.ConnectivityManager` y `android.net.NetworkCapabilities` si ya nadie los usa. NO agregues el permiso ACCESS_NETWORK_STATE.

## 2) ui/ConfiguracionScreen.kt

Agrega esta tarjeta justo ANTES del comentario `// Términos y Condiciones y Privacidad`:

```kotlin
        // Voz sin conexión
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(2.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_voz_sin_conexion")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Mic, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "VOZ SIN CONEXIÓN",
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Para dictar tu consulta sin internet, el teléfono necesita tener el español descargado en su reconocimiento de voz. " +
                        "Los nombres de los menús cambian según la marca del teléfono:",
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "1. Abre los ajustes de voz (botón de abajo) o: Ajustes › Idioma y entrada › Entrada de voz.\n" +
                        "2. Entra a «Reconocimiento de voz de Google» (o el reconocimiento de voz de tu teléfono).\n" +
                        "3. Elige «Reconocimiento sin conexión» y descarga «Español».\n" +
                        "4. Prueba el micrófono con el teléfono en modo avión.",
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Si tu teléfono no lo permite, escribe tu pregunta: la búsqueda en la aplicación funciona siempre sin internet.",
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = {
                        val abrir = { accion: String ->
                            context.startActivity(
                                android.content.Intent(accion).addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                            )
                        }
                        try {
                            abrir(android.provider.Settings.ACTION_VOICE_INPUT_SETTINGS)
                        } catch (e: Exception) {
                            try {
                                abrir(android.provider.Settings.ACTION_SETTINGS)
                            } catch (_: Exception) {
                            }
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_ajustes_voz")
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Abrir ajustes de voz", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
```

## 3) Cambiar iconos (sin tocar nada más)

| Archivo | Icono actual | Icono nuevo |
|---|---|---|
| ui/ElectoralMainScreen.kt (2 lugares, pestaña Test) | `Icons.Default.Psychology` | `Icons.Default.Quiz` |
| ui/ElectoralMainScreen.kt (estado PROCESSING de la voz) | `Icons.Default.AutoAwesome` | `Icons.Default.HourglassTop` |
| ui/ConfiguracionScreen.kt (tarjeta Test) | `Icons.Default.Psychology` | `Icons.Default.Quiz` |
| ui/CapacitacionScreen.kt | `Icons.Default.Psychology` | `Icons.Default.QuestionAnswer` |
| ui/SimulacionScreen.kt, ui/BookInternalSearchScreen.kt, ui/PasoAPasoScreen.kt, ui/BibliotecaScreen.kt | `Icons.Default.AutoAwesome` | `Icons.Default.QuestionAnswer` |

## 4) Textos legales
Reemplaza `LegalConsentManager.kt` por el archivo adjunto (solo cambió la explicación de la voz en términos y política).

## Pruebas (en el teléfono)
1. Con internet: toca el micrófono y dicta "a qué hora cierra la votación": debe transcribir y responder.
2. Descarga el español sin conexión (Ajustes › Voz sin conexión, o Ajustes del teléfono › Entrada de voz › Reconocimiento sin conexión), activa MODO AVIÓN y repite: si el teléfono lo soporta, debe transcribir.
3. En modo avión SIN el paquete descargado: debe mostrar "No se pudo reconocer la voz sin internet. Descarga el español sin conexión (Ajustes › Voz sin conexión) o escribe tu pregunta." y NO quedarse cargando.
4. Ajustes: aparece la tarjeta "VOZ SIN CONEXIÓN" y el botón abre los ajustes de voz (o los ajustes generales).
5. Repite 1 a 3 en al menos 2 teléfonos de marcas distintas y dime el resultado de cada uno.
6. Revisa que ya no haya destellos ni cerebro en los botones de consultar al asistente.
