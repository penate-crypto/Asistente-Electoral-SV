# PROMPT 11 para AI Studio — Arreglar el micrófono sin conexión (siempre daba error)

Aplica DESPUÉS del Prompt 7. No necesita archivos adjuntos.

---

El botón de micrófono siempre termina en error, aunque el teléfono ya tiene descargado el español para reconocer voz sin conexión. Causas encontradas en `MainActivity.kt`:
- Solo se probaba el idioma `es-SV` y `es-419`: si el paquete descargado del teléfono es otro español (es-US, es-MX, es-ES…) fallaba siempre.
- Se llamaba `cancel()` justo antes de `startListening()`, lo que en muchos teléfonos provoca un error de cliente inmediato.
- Se mandaba `EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE` con un texto («es-SV»), pero ese extra es un booleano: confundía al reconocedor.
- En Android 13+ existe el reconocedor EN EL TELÉFONO (`createOnDeviceSpeechRecognizer`) que permite saber qué idiomas hay instalados; no se usaba.

Solución: cada vez que se pulsa el micrófono se prueban, en orden, varias formas hasta que una funcione:
1. (Android 13+) reconocedor del teléfono con el español que realmente tenga instalado (se le pregunta con `checkRecognitionSupport`).
2. Reconocedor normal del sistema con `es-SV`, `es-419`, `es-US` y, al final, el idioma del teléfono, siempre prefiriendo sin conexión.
3. Último recurso: el cuadro de dictado del propio teléfono.
Si todo falla, el mensaje de error ahora incluye el CÓDIGO del error para saber qué pasó.

## Cambio exacto en `app/src/main/java/com/example/MainActivity.kt` (aplicar este diff)

```diff
diff --git a/app/src/main/java/com/example/MainActivity.kt b/app/src/main/java/com/example/MainActivity.kt
index 58654f5..74b6795 100644
--- a/app/src/main/java/com/example/MainActivity.kt
+++ b/app/src/main/java/com/example/MainActivity.kt
@@ -6,10 +6,13 @@ import android.content.ActivityNotFoundException
 import android.content.Context
 import android.content.Intent
 import android.content.pm.PackageManager
+import android.os.Build
 import android.os.Bundle
 import android.os.Handler
 import android.os.Looper
 import android.speech.RecognitionListener
+import android.speech.RecognitionSupport
+import android.speech.RecognitionSupportCallback
 import android.speech.RecognizerIntent
 import android.speech.SpeechRecognizer
 import android.widget.Toast
@@ -19,6 +22,7 @@ import androidx.activity.compose.setContent
 import androidx.activity.enableEdgeToEdge
 import androidx.activity.result.ActivityResultLauncher
 import androidx.activity.result.contract.ActivityResultContracts
+import androidx.annotation.RequiresApi
 import androidx.compose.foundation.layout.fillMaxSize
 import androidx.compose.material3.MaterialTheme
 import androidx.compose.material3.Surface
@@ -47,7 +51,10 @@ class MainActivity : ComponentActivity() {
 
     private lateinit var ttsManager: ElectoralTtsManager
     private var speechRecognizer: SpeechRecognizer? = null
-    private var voiceRetryDone = false
+    private var voiceAttempts: List<VoiceAttempt> = emptyList()
+    private var voiceAttemptIndex = -1
+    private var voiceRunId = 0
+    private var voiceIntentLauncher: ActivityResultLauncher<Intent>? = null
 
     private lateinit var viewModel: ElectoralViewModel
     private val mainHandler = Handler(Looper.getMainLooper())
@@ -203,79 +210,8 @@ class MainActivity : ComponentActivity() {
     }
 
     private fun initSpeechRecognizerSafely() {
-        try {
-            if (SpeechRecognizer.isRecognitionAvailable(this)) {
-                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this).apply {
-                    setRecognitionListener(object : RecognitionListener {
-                        override fun onReadyForSpeech(params: Bundle?) {
-                            mainHandler.post {
-                                viewModel.startVoiceListening()
-                            }
-                        }
-
-                        override fun onBeginningOfSpeech() {}
-
-                        override fun onRmsChanged(rmsdB: Float) {}
-
-                        override fun onBufferReceived(buffer: ByteArray?) {}
-
-                        override fun onEndOfSpeech() {}
-
-                        override fun onError(error: Int) {
-                            mainHandler.post {
-                                when (error) {
-                                    SpeechRecognizer.ERROR_NO_MATCH,
-                                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
-                                        viewModel.onVoiceError("No pude entender la consulta. Intenta nuevamente.")
-                                    }
-                                    SpeechRecognizer.ERROR_NETWORK,
-                                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT,
-                                    SpeechRecognizer.ERROR_SERVER,
-                                    ERROR_LANGUAGE_NOT_SUPPORTED,
-                                    ERROR_LANGUAGE_UNAVAILABLE -> {
-                                        // Primer intento: es-SV con preferencia sin conexión. Segundo intento: español latino (es-419).
-                                        if (!voiceRetryDone && retryVoiceInLatinSpanish()) {
-                                            return@post
-                                        }
-                                        viewModel.onVoiceError(VOICE_OFFLINE_HELP_MESSAGE)
-                                    }
-                                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> {
-                                        viewModel.onVoiceError("Falta el permiso del micrófono. Actívalo en los ajustes del teléfono.")
-                                    }
-                                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> {
-                                        viewModel.onVoiceError("El reconocimiento de voz está ocupado. Intenta nuevamente en unos segundos.")
-                                    }
-                                    SpeechRecognizer.ERROR_CLIENT -> {
-                                        viewModel.cancelVoiceListening("Escucha cancelada.")
-                                    }
-                                    else -> {
-                                        viewModel.onVoiceError("No pude entender la consulta. Intenta nuevamente.")
-                                    }
-                                }
-                            }
-                        }
-
-                        override fun onResults(results: Bundle?) {
-                            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
-                            val text = matches?.firstOrNull()
-                            mainHandler.post {
-                                if (!text.isNullOrBlank()) {
-                                    viewModel.onVoiceQueryReceived(text)
-                                } else {
-                                    viewModel.onVoiceError("No pude entender la consulta. Intenta nuevamente.")
-                                }
-                            }
-                        }
-
-                        override fun onPartialResults(partialResults: Bundle?) {}
-
-                        override fun onEvent(eventType: Int, params: Bundle?) {}
-                    })
-                }
-            }
-        } catch (e: Exception) {
-            speechRecognizer = null
-        }
+        // El reconocedor ya no se crea al abrir la app: se crea en cada dictado (ver beginVoiceAttempts),
+        // con el idioma que el teléfono tenga realmente disponible (sin conexión o en línea).
     }
 
     private fun handleVoiceButtonInteraction(
@@ -312,64 +248,157 @@ class MainActivity : ComponentActivity() {
         }
     }
 
+    // ------------------------------------------------------------------ Dictado por voz
+    // Idea: probar, en orden, las formas de reconocer la voz que el teléfono pueda tener, hasta que una funcione:
+    //  1) Reconocedor EN EL TELÉFONO (Android 13+) con el español que tenga INSTALADO (se consulta cuál es).
+    //  2) Reconocedor normal del sistema con es-SV, es-419, es-US y el idioma del teléfono (prefiriendo sin conexión).
+    //  3) Último recurso: el cuadro de dictado del propio teléfono (muestra su propia pantalla de escucha).
     private fun startVoiceListeningFlow(speechIntentLauncher: ActivityResultLauncher<Intent>) {
-        // Ya no se bloquea el micrófono cuando no hay internet: el reconocedor del teléfono puede tener el
-        // español descargado y trabajar sin conexión. Si no puede, avisa por onError (VOICE_OFFLINE_HELP_MESSAGE).
-        voiceRetryDone = false
-        val speechIntent = buildSpeechIntent("es-SV")
-
+        voiceIntentLauncher = speechIntentLauncher
+        voiceRunId++
+        val run = voiceRunId
         viewModel.startVoiceListening()
+        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && SpeechRecognizer.isOnDeviceRecognitionAvailable(this)) {
+            probeInstalledSpanish(run)
+        } else {
+            beginVoiceAttempts(run, emptyList())
+        }
+    }
 
-        // Try direct speech recognizer first if available
-        var startedDirectly = false
-        if (speechRecognizer != null) {
-            try {
-                speechRecognizer?.cancel()
-                speechRecognizer?.startListening(speechIntent)
-                startedDirectly = true
-            } catch (e: Exception) {
-                startedDirectly = false
+    /** Pregunta al teléfono qué idiomas tiene instalados para reconocer voz SIN conexión (Android 13+). */
+    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
+    private fun probeInstalledSpanish(run: Int) {
+        try {
+            val probe = SpeechRecognizer.createOnDeviceSpeechRecognizer(this)
+            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
+                .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
+            probe.checkRecognitionSupport(intent, mainExecutor, object : RecognitionSupportCallback {
+                override fun onSupportResult(recognitionSupport: RecognitionSupport) {
+                    val spanish = recognitionSupport.installedOnDeviceLanguages.filter { it.startsWith("es", ignoreCase = true) }
+                    probe.destroy()
+                    if (run == voiceRunId) beginVoiceAttempts(run, spanish)
+                }
+
+                override fun onError(error: Int) {
+                    probe.destroy()
+                    if (run == voiceRunId) beginVoiceAttempts(run, emptyList())
+                }
+            })
+        } catch (e: Exception) {
+            beginVoiceAttempts(run, emptyList())
+        }
+    }
+
+    private fun beginVoiceAttempts(run: Int, installedSpanish: List<String>) {
+        if (run != voiceRunId) return
+        val preferencia = listOf("es-SV", "es-419", "es-US", "es-MX", "es-ES", "es")
+        val attempts = ArrayList<VoiceAttempt>()
+        installedSpanish
+            .sortedBy { tag -> preferencia.indexOfFirst { it.equals(tag, ignoreCase = true) }.let { if (it < 0) 99 else it } }
+            .forEach { attempts.add(VoiceAttempt(onDevice = true, language = it)) }
+        listOf("es-SV", "es-419", "es-US", "").forEach { attempts.add(VoiceAttempt(onDevice = false, language = it)) }
+        voiceAttempts = attempts
+        voiceAttemptIndex = -1
+        nextVoiceAttempt(run, lastError = -1)
+    }
+
+    private fun nextVoiceAttempt(run: Int, lastError: Int) {
+        if (run != voiceRunId) return
+        voiceAttemptIndex++
+        if (voiceAttemptIndex >= voiceAttempts.size) {
+            finishVoiceWithSystemDialog(lastError)
+            return
+        }
+        val attempt = voiceAttempts[voiceAttemptIndex]
+        try {
+            speechRecognizer?.destroy()
+            speechRecognizer = null
+            val recognizer: SpeechRecognizer? = when {
+                attempt.onDevice && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
+                    SpeechRecognizer.createOnDeviceSpeechRecognizer(this)
+                SpeechRecognizer.isRecognitionAvailable(this) -> SpeechRecognizer.createSpeechRecognizer(this)
+                else -> null
+            }
+            if (recognizer == null) {
+                nextVoiceAttempt(run, ERROR_RECOGNIZER_UNAVAILABLE)
+                return
             }
+            recognizer.setRecognitionListener(buildVoiceListener(run))
+            speechRecognizer = recognizer
+            recognizer.startListening(buildSpeechIntent(attempt.language))
+        } catch (e: Exception) {
+            nextVoiceAttempt(run, ERROR_RECOGNIZER_UNAVAILABLE)
         }
+    }
 
-        // If direct speech recognizer is unavailable or failed, launch intent dialog safely
-        if (!startedDirectly) {
-            try {
-                speechIntentLauncher.launch(speechIntent)
-            } catch (e: ActivityNotFoundException) {
-                viewModel.onVoiceError("Reconocimiento de voz no disponible en este dispositivo.")
-            } catch (e: Exception) {
-                viewModel.onVoiceError("No fue posible activar el micrófono. Intenta nuevamente.")
+    private fun buildVoiceListener(run: Int) = object : RecognitionListener {
+        override fun onReadyForSpeech(params: Bundle?) {
+            mainHandler.post { if (run == voiceRunId) viewModel.startVoiceListening() }
+        }
+
+        override fun onBeginningOfSpeech() {}
+        override fun onRmsChanged(rmsdB: Float) {}
+        override fun onBufferReceived(buffer: ByteArray?) {}
+        override fun onEndOfSpeech() {}
+        override fun onPartialResults(partialResults: Bundle?) {}
+        override fun onEvent(eventType: Int, params: Bundle?) {}
+
+        override fun onError(error: Int) {
+            mainHandler.post {
+                if (run != voiceRunId) return@post
+                when (error) {
+                    SpeechRecognizer.ERROR_NO_MATCH,
+                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT ->
+                        viewModel.onVoiceError("No pude entender la consulta. Intenta nuevamente.")
+                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->
+                        viewModel.onVoiceError("Falta el permiso del micrófono. Actívalo en los ajustes del teléfono.")
+                    else ->
+                        // Red, servidor, idioma no disponible, cliente, audio, ocupado...: se prueba la siguiente forma.
+                        nextVoiceAttempt(run, error)
+                }
+            }
+        }
+
+        override fun onResults(results: Bundle?) {
+            val text = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
+            mainHandler.post {
+                if (run != voiceRunId) return@post
+                if (!text.isNullOrBlank()) viewModel.onVoiceQueryReceived(text)
+                else viewModel.onVoiceError("No pude entender la consulta. Intenta nuevamente.")
             }
         }
     }
 
-    /** Intent de dictado que prefiere el reconocimiento SIN conexión (si el teléfono tiene el idioma descargado). */
+    /** Intent de dictado. Con [languageTag] vacío se usa el idioma del teléfono. Siempre prefiere reconocer SIN conexión. */
     private fun buildSpeechIntent(languageTag: String): Intent =
         Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
             putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
-            putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageTag)
-            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languageTag)
-            putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, languageTag)
+            if (languageTag.isNotEmpty()) putExtra(RecognizerIntent.EXTRA_LANGUAGE, languageTag)
+            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
             putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
+            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
             putExtra(RecognizerIntent.EXTRA_PROMPT, "¿Cuál es su consulta electoral?")
             putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
         }
 
-    /** Segundo y último intento con español latino. Devuelve true si logró iniciarlo. */
-    private fun retryVoiceInLatinSpanish(): Boolean {
-        voiceRetryDone = true
-        val recognizer = speechRecognizer ?: return false
-        return try {
-            recognizer.cancel()
-            recognizer.startListening(buildSpeechIntent("es-419"))
-            true
-        } catch (e: Exception) {
-            false
+    /** Último recurso: el cuadro de dictado del propio teléfono. Si tampoco existe, se avisa con el código del error. */
+    private fun finishVoiceWithSystemDialog(lastError: Int) {
+        val launcher = voiceIntentLauncher
+        if (launcher != null) {
+            try {
+                launcher.launch(buildSpeechIntent(""))
+                return
+            } catch (e: ActivityNotFoundException) {
+                // sigue abajo
+            } catch (e: Exception) {
+                // sigue abajo
+            }
         }
+        viewModel.onVoiceError(voiceHelpMessage(lastError))
     }
 
     private fun stopSpeechRecognition() {
+        voiceRunId++ // ignora lo que llegue de intentos anteriores
         try {
             speechRecognizer?.stopListening()
             speechRecognizer?.cancel()
@@ -389,9 +418,11 @@ class MainActivity : ComponentActivity() {
     }
 }
 
-// Códigos de SpeechRecognizer disponibles desde Android 12 (se usan como números para que compile en versiones anteriores).
-private const val ERROR_LANGUAGE_NOT_SUPPORTED = 12
-private const val ERROR_LANGUAGE_UNAVAILABLE = 13
+/** Una forma de reconocer voz: en el teléfono (Android 13+) o con el reconocedor normal, y el idioma ("" = el del teléfono). */
+private data class VoiceAttempt(val onDevice: Boolean, val language: String)
+
+// Códigos propios (los de SpeechRecognizer van de 1 a 13).
+private const val ERROR_RECOGNIZER_UNAVAILABLE = 100
 
-private const val VOICE_OFFLINE_HELP_MESSAGE =
-    "No se pudo reconocer la voz sin internet. Descarga el español sin conexión (Ajustes › Voz sin conexión) o escribe tu pregunta."
+private fun voiceHelpMessage(code: Int): String =
+    "No se pudo reconocer la voz (código $code). Descarga el español sin conexión (Ajustes › Voz sin conexión) o escribe tu pregunta."
```

No cambies nada más. No agregues permisos (no hace falta ACCESS_NETWORK_STATE ni cambiar INTERNET).

## Pruebas (en el teléfono con el español sin conexión ya descargado)
1. Con internet: toca el micrófono, di «a qué hora cierra la votación»: debe transcribir y responder.
2. Activa el MODO AVIÓN y repite: debe transcribir sin internet.
3. Prueba con el teléfono bloqueado a otro idioma (si puedes) o con otro paquete de español: debe seguir funcionando.
4. Si vuelve a dar error, el mensaje trae «(código N)». Anota el número N y el modelo de tu teléfono y mándamelos: con eso se corrige. Códigos útiles: 2 y 1 = red, 4 = servidor, 5 = cliente, 8 = ocupado, 9 = permiso, 12 y 13 = idioma no disponible, 100 = reconocedor no disponible.
5. Pulsa el micrófono dos veces seguidas rápido (empezar y cancelar): no debe trabarse ni quedar «Escuchando…» para siempre.
