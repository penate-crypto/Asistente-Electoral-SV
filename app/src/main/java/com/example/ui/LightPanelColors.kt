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
