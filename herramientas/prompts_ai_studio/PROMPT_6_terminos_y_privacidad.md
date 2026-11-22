# PROMPT 6 para AI Studio — Términos, política de privacidad y consentimiento (Bloque B)

Adjunta el archivo `LegalConsentManager.kt`. Hazlo DESPUÉS del Prompt 5.

---

Necesito actualizar los textos legales de la app para que digan la verdad sobre cómo funciona. La app NO usa inteligencia artificial, agentes ni asistentes externos: responde con un algoritmo de búsqueda local sobre un banco de conocimiento. Hay que dejar el login exactamente como está (no lo quites ni lo cambies).

1) Reemplaza COMPLETO el archivo `app/src/main/java/com/example/data/LegalConsentManager.kt` por el archivo adjunto. No lo resumas ni lo reformatees. Los cambios que trae:
- `INITIAL_NOTICE_SUMMARY`, `FULL_TERMS_AND_CONDITIONS` y `FULL_PRIVACY_POLICY` reescritos: explican que no usa IA, cómo funciona el buscador local, cómo se creó la app (AI Studio y Gemini, ChatGPT y Claude, Claude Code), el micrófono, el login local y qué datos se guardan en el teléfono. Ya no mencionan la LEIV, ni afirman que las consultas se procesan con Gemini, ni que no hay pantalla de acceso.
- `CURRENT_LEGAL_VERSION = "1.1"` y `LAST_UPDATED_DATE = "22 de noviembre de 2026"`.
- `isConsentAccepted` ahora compara también la versión guardada con `CURRENT_LEGAL_VERSION`: quien aceptó la versión 1.0 verá el aviso legal otra vez y deberá aceptar. Es intencional.
- NO cambies `APP_VERSION` (se muestra en Ajustes).

2) En `app/src/main/java/com/example/ui/ElectoralMainScreen.kt`, en el menú lateral, elimina el texto visible que dice "Creador: Rodrigo Peñate" (el `Text` completo). Déjalo solo como comentario en el código: `// Creador: Rodrigo Peñate (crédito interno; no se muestra en la aplicación)`. No lo agregues a ninguna otra pantalla ni a los textos legales.

3) Verifica que `LegalNoticeScreen.kt` y `LegalDocumentViewScreen.kt` sigan mostrando los textos (usan `INITIAL_NOTICE_TITLE`, `INITIAL_NOTICE_SUMMARY`, `FULL_TERMS_AND_CONDITIONS`, `FULL_PRIVACY_POLICY`, `CURRENT_LEGAL_VERSION` y `LAST_UPDATED_DATE`) y que los textos largos se puedan desplazar completos sin cortarse.

Pruebas:
- Instala sobre una versión anterior (con el aviso ya aceptado): debe volver a aparecer el aviso legal; al aceptar, no debe volver a aparecer.
- Abre Ajustes → Términos y Condiciones y Política de Privacidad: leer completos; la versión debe decir 1.1 y la fecha 22 de noviembre de 2026.
- El menú lateral ya no muestra "Creador".
- El login sigue igual.
- Busca en el proyecto las palabras `LEIV` y `Virtual Electoral`: no deben aparecer en textos que vea el usuario.
