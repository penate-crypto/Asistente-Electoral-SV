# PROMPT 10 para AI Studio — Modo oscuro: texto invisible sobre paneles verdes, rojos y naranjas claros

No necesita archivos adjuntos. Puedes aplicarlo ahora (no depende de los Prompts 8 y 9).

---

En tema OSCURO todavía hay textos blancos sobre fondos de color claro fijo (verde `E8F5E9`, rojo `FFEBEE`, naranja `FFF3E0`) que no se leen. Pasa cuando respondes una pregunta (opción correcta o incorrecta que se pinta de verde o rojo claro), en la tarjeta de explicación y en la tarjeta de resultado. La causa: esos fondos NO cambian con el tema, pero el texto usaba `MaterialTheme.colorScheme.onSurface` / `onSurfaceVariant`, que en tema oscuro es blanco o gris claro.

Regla (aplícala en TODA la app, no solo aquí): sobre un fondo claro fijo, el texto debe tener SIEMPRE un color oscuro explícito; nunca `colorScheme.onSurface`, `onSurfaceVariant` ni el color por defecto del `Text`.

## 1) Archivo nuevo
`app/src/main/java/com/example/ui/LightPanelColors.kt`

```kotlin
package com.example.ui

import androidx.compose.ui.graphics.Color

/**
 * Textos sobre paneles de fondo claro FIJO (verde / rojo / naranja claro de correcto, incorrecto y resultado).
 * Esos fondos no cambian con el tema, así que su texto tampoco debe depender del tema:
 * en tema oscuro `colorScheme.onSurface` es blanco y se vería blanco sobre verde claro.
 * Regla: sobre un fondo claro fijo, usar SIEMPRE estos colores (o un color explícito oscuro), nunca `colorScheme.onSurface`.
 */
val OnLightPanel = Color(0xFF1B2733)
val OnLightPanelSoft = Color(0xFF37474F)
val OnLightOk = Color(0xFF1B5E20)
val OnLightBad = Color(0xFFB71C1C)
```

## 2) Cambios (aplicar exactamente estos diffs)

### ui/SimulacionScreen.kt (casos y simulacro)
```diff
diff --git a/app/src/main/java/com/example/ui/SimulacionScreen.kt b/app/src/main/java/com/example/ui/SimulacionScreen.kt
index 5cfb058..3eae766 100644
--- a/app/src/main/java/com/example/ui/SimulacionScreen.kt
+++ b/app/src/main/java/com/example/ui/SimulacionScreen.kt
@@ -1014,7 +1014,12 @@ private fun ActiveExamQuestionView(
                             text = optionText,
                             fontSize = 13.5.sp,
                             fontWeight = if (isSelected || (hasAnswered && isCorrectOption)) FontWeight.Bold else FontWeight.Normal,
-                            color = MaterialTheme.colorScheme.onSurface,
+                            // Fondos verde/rojo claro fijos: texto oscuro también en tema oscuro
+                            color = when {
+                                hasAnswered && isCorrectOption -> OnLightOk
+                                hasAnswered && isSelected && !isCorrectOption -> OnLightBad
+                                else -> MaterialTheme.colorScheme.onSurface
+                            },
                             modifier = Modifier.weight(1f)
                         )
 
@@ -1066,14 +1071,14 @@ private fun ActiveExamQuestionView(
                         text = "Explicación:",
                         fontSize = 12.sp,
                         fontWeight = FontWeight.Bold,
-                        color = MaterialTheme.colorScheme.onSurface
+                        color = OnLightPanel
                     )
 
                     Text(
                         text = currentQuestion.explanation,
                         fontSize = 12.5.sp,
                         lineHeight = 17.sp,
-                        color = MaterialTheme.colorScheme.onSurface
+                        color = OnLightPanel
                     )
 
                     Spacer(modifier = Modifier.height(2.dp))
@@ -1237,7 +1242,7 @@ private fun ExamResultsSummaryView(
                         "Se requiere un mínimo de 70% (18 aciertos de 25) para aprobar.",
                     fontSize = 12.sp,
                     textAlign = TextAlign.Center,
-                    color = MaterialTheme.colorScheme.onSurfaceVariant
+                    color = OnLightPanelSoft
                 )
 
                 Spacer(modifier = Modifier.height(14.dp))
@@ -1249,12 +1254,12 @@ private fun ExamResultsSummaryView(
                 ) {
                     Column(horizontalAlignment = Alignment.CenterHorizontally) {
                         Text(text = "$correctCount/25", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF2E7D32))
-                        Text(text = "Aciertos", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
+                        Text(text = "Aciertos", fontSize = 11.sp, color = OnLightPanelSoft)
                     }
 
                     Column(horizontalAlignment = Alignment.CenterHorizontally) {
                         Text(text = "$incorrectCount/25", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFFC62828))
-                        Text(text = "Fallos", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
+                        Text(text = "Fallos", fontSize = 11.sp, color = OnLightPanelSoft)
                     }
 
                     Column(horizontalAlignment = Alignment.CenterHorizontally) {
```

### ui/TestConocimientosScreen.kt (test de 125 preguntas)
```diff
diff --git a/app/src/main/java/com/example/ui/TestConocimientosScreen.kt b/app/src/main/java/com/example/ui/TestConocimientosScreen.kt
index 6e78b30..8a6c21a 100644
--- a/app/src/main/java/com/example/ui/TestConocimientosScreen.kt
+++ b/app/src/main/java/com/example/ui/TestConocimientosScreen.kt
@@ -842,7 +842,7 @@ fun Exam125ResultsView(
             Card(
                 shape = RoundedCornerShape(14.dp),
                 colors = CardDefaults.cardColors(
-                    containerColor = if (isCorrect) Color(0xFFE8F5E9).copy(alpha = 0.5f) else Color(0xFFFFEBEE).copy(alpha = 0.5f)
+                    containerColor = if (isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                 ),
                 border = BorderStroke(1.dp, if (isCorrect) Color(0xFF81C784) else Color(0xFFE57373)),
                 modifier = Modifier.fillMaxWidth()
@@ -878,7 +878,7 @@ fun Exam125ResultsView(
                         text = q.questionText,
                         fontSize = 13.5.sp,
                         fontWeight = FontWeight.Bold,
-                        color = MaterialTheme.colorScheme.onSurface
+                        color = OnLightPanel
                     )
 
                     val userLetter = if (userSelected != null) optionLetters.getOrElse(userSelected) { "-" } else "Sin responder"
```

## 3) Revisa también que el arreglo anterior del Test siga puesto
En `TestConocimientosScreen.kt`, el `Text(text = optionText, ...)` de cada opción debe tener este color (si no lo tiene, vuelve a ponerlo):

```kotlin
color = when {
    (currentMode == TestMode.ESTUDIO && isCorrectOption) || (isSelected && isCorrectOption) -> Color(0xFF1B5E20)
    isSelected && !isCorrectOption -> Color(0xFFB71C1C)
    else -> MaterialTheme.colorScheme.onSurface
},
```

## 4) Auditoría (por favor hazla y dime el resultado)
Busca en TODOS los archivos de `ui/` cualquier `Card`, `Surface`, `Box` o `.background(...)` con color claro fijo (`Color(0xFF...)` claro: `E8F5E9`, `FFEBEE`, `FFF3E0`, `FFF8E1`, `E3F2FD`, `E0F2F1`, `FFFDE7`, etc.) y revisa que TODO `Text`, `Icon` y `Divider` dentro tenga color oscuro explícito. Los textos que solo son emojis no cuentan. Corrige lo que encuentres y lista cada cambio. No cambies colores del tema (Theme.kt) ni nada más.

## 5) Pruebas (en el teléfono, en TEMA OSCURO y luego en CLARO)
1. Test de 125 preguntas, modo Estudio: la opción correcta (verde claro) se lee bien; el recuadro «Respuesta correcta» y la explicación se leen.
2. Test, modo Examen: responde una opción bien y otra mal; ambas se leen. Termina el examen: la tarjeta de resultado (verde o naranja claro) y la revisión pregunta por pregunta se leen.
3. Casos y simulacro de 25 preguntas: responde correcto e incorrecto; el texto de la opción, «Explicación:» y la explicación se leen. Termina: «Aciertos» y «Fallos» se leen.
4. Repite todo en tema claro: debe verse igual de bien que antes.
