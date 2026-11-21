# PROMPT 4 para AI Studio — Arreglar el texto invisible en el Test (tema oscuro)

---

Hay un bug visual en la pantalla del Test de conocimientos (archivo `app/src/main/java/com/example/ui/TestConocimientosScreen.kt`). En tema oscuro, al responder una opción, el fondo de la opción pasa a verde claro (correcta, `Color(0xFFE8F5E9)`) o rojo claro (incorrecta, `Color(0xFFFFEBEE)`), pero el texto de la opción sigue usando `MaterialTheme.colorScheme.onSurface`, que en tema oscuro es blanco. Resultado: texto blanco sobre fondo verde claro, ilegible.

Arréglalo SOLO ahí, sin cambiar nada más. En la lista de opciones, busca el `Text(text = optionText, ...)` y cambia su `color = ...` por:

```kotlin
color = when {
    // Fondos verde/rojo claro fijos: el texto debe ser oscuro también en tema oscuro
    (currentMode == TestMode.ESTUDIO && isCorrectOption) || (isSelected && isCorrectOption) -> Color(0xFF1B5E20)
    isSelected && !isCorrectOption -> Color(0xFFB71C1C)
    else -> MaterialTheme.colorScheme.onSurface
},
```

Revisa también en esa pantalla cualquier otro `Text` o `Icon` que use `MaterialTheme.colorScheme.onSurface` / `onSurfaceVariant` / `Color.White` encima de un fondo de color fijo claro (verde `E8F5E9`/`C8E6C9`, rojo `FFEBEE`, naranja `FFF3E0`, o fondos con alpha 0.15) y dale un color oscuro explícito (verde 1B5E20, rojo B71C1C, naranja BF360C). Pruébalo en tema oscuro y en tema claro: respuesta correcta, respuesta incorrecta, modo estudio, y pantalla de resultados.
