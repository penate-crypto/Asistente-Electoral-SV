package com.example.data

import android.content.Context
import android.content.SharedPreferences

object LegalConsentManager {
    private const val PREFS_NAME = "asistente_electoral_legal_prefs"
    private const val KEY_LEGAL_CONSENT_ACCEPTED = "legalConsentAccepted"
    private const val KEY_LEGAL_VERSION = "legalDocumentsVersion"
    private const val KEY_ACCEPTED_TIMESTAMP = "legalConsentAcceptedTimestamp"

    const val CURRENT_LEGAL_VERSION = "1.0"
    const val APP_VERSION = "2.0.26"
    const val LAST_UPDATED_DATE = "18 de agosto de 2026"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isConsentAccepted(context: Context): Boolean {
        val prefs = getPrefs(context)
        return prefs.getBoolean(KEY_LEGAL_CONSENT_ACCEPTED, false)
    }

    fun setConsentAccepted(context: Context, accepted: Boolean) {
        val prefs = getPrefs(context)
        prefs.edit()
            .putBoolean(KEY_LEGAL_CONSENT_ACCEPTED, accepted)
            .putString(KEY_LEGAL_VERSION, CURRENT_LEGAL_VERSION)
            .putLong(KEY_ACCEPTED_TIMESTAMP, System.currentTimeMillis())
            .apply()
    }

    const val INITIAL_NOTICE_TITLE = "Antes de continuar"

    const val INITIAL_NOTICE_SUMMARY = """Asistente Electoral SV es una herramienta independiente de apoyo, consulta y capacitación complementaria sobre procesos y situaciones electorales.

Esta aplicación NO es oficial del Tribunal Supremo Electoral de El Salvador (TSE), no representa al TSE y no sustituye las capacitaciones, instrucciones, manuales, resoluciones ni decisiones de las autoridades electorales.

La aplicación utiliza herramientas de inteligencia artificial, incluyendo Google Gemini, ChatGPT y Google AI Studio. Algunas consultas pueden ser procesadas por servicios de IA y las conversaciones pueden conservarse en el historial de la aplicación.

No introduzca DUI, nombres de electores, teléfonos, fotografías de documentos, información confidencial ni información que permita identificar cómo votó una persona.

Las respuestas generadas por IA pueden contener errores o estar desactualizadas. Ante una situación real, siempre debe prevalecer la normativa y la instrucción oficial vigente.

Al seleccionar “Aceptar”, confirma que ha leído y comprendido este aviso y acepta consultar los Términos y Condiciones y la Política de Privacidad de la aplicación."""

    const val FULL_TERMS_AND_CONDITIONS = """TÉRMINOS Y CONDICIONES DE USO
ASISTENTE ELECTORAL SV

Versión: 1.0 (Actualizada al 18 de agosto de 2026)
Aplicación: Asistente Virtual Electoral - Edición El Salvador

1. NATURALEZA DEL SERVICIO Y CARÁCTER INDEPENDIENTE
Asistente Electoral SV es una aplicación de desarrollo independiente creada exclusivamente como herramienta tecnológica de apoyo, consulta informativa, estudio cívico y capacitación complementaria en materia electoral de la República de El Salvador.
Esta aplicación NO es una herramienta oficial del Tribunal Supremo Electoral de El Salvador (TSE), no representa de forma alguna a dicho organismo ni a ninguna entidad gubernamental, y NO sustituye las capacitaciones obligatorias, instructivos impresos, acuerdos, decretos o resoluciones jurisdiccionales emitidas por el TSE, las Juntas Electorales Departamentales (JED), las Juntas Electorales Municipales (JEM) o las Juntas Receptoras de Votos (JRV).

2. USO DE TECNOLOGÍAS DE INTELIGENCIA ARTIFICIAL
La aplicación integra modelos de lenguaje natural e inteligencia artificial avanzada (incluyendo Google Gemini y Google AI Studio) para procesar consultas y resumir la normativa electoral salvadoreña (Código Electoral - Decreto 413, Constitución de la República, LEIV y manuales). El usuario reconoce expresamente que las respuestas automatizadas pueden contener imprecisiones involuntarias, interpretaciones no vinculantes o desactualizaciones frente a reformas legales inmediatas. Ante cualquier divergencia, prevalecerá siempre el texto de la ley y las disposiciones emitidas por el TSE.

3. USO ACEPTABLE Y PROTECCIÓN DE DATOS SENSIBLES
El usuario se compromete formalmente a utilizar la aplicación de buena fe y bajo estricto apego a las leyes de la República de El Salvador.
Queda TERMINANTEMENTE PROHIBIDO ingresar en los campos de consulta:
a) Números de Documento Único de Identidad (DUI) de terceros.
b) Nombres y apellidos completos de electores del padrón.
c) Números telefónicos, direcciones residenciales o datos de contacto personal.
d) Fotografías o copias de documentos de identidad, papeletas marcadas o folios del padrón electoral.
e) Cualquier dato que vulnere el secreto del sufragio o permita revelar el sentido del voto de un ciudadano.

4. ALMACENAMIENTO LOCAL Y SIN CUENTAS
La aplicación no requiere la creación de cuentas ni registra perfiles de usuario en servidores remotos. El consentimiento legal y el historial de preguntas se gestionan de forma local en el dispositivo del usuario mediante almacenamiento seguro.

5. PREVALENCIA DE LA AUTORIDAD ELECTORAL EN EL DÍA DE LA ELECCIÓN
Durante la jornada electoral, la máxima autoridad en cada mesa es la Junta Receptora de Votos (JRV) en pleno, bajo la dirección de su Presidente. Las respuestas brindadas por la aplicación tienen fines exclusivamente orientativos y formativos, por lo que no pueden invocarse como fundamento legal vinculante para contradecir decisiones formales de las autoridades electorales.

6. LIMITACIÓN DE RESPONSABILIDAD
Los desarrolladores de Asistente Electoral SV no se responsabilizan por decisiones operativas, omisiones, sanciones o interpretaciones erróneas derivadas del uso de la información suministrada por la aplicación. Es responsabilidad exclusiva del usuario verificar la normativa vigente aplicable a cada caso.

7. MODIFICACIONES Y ACEPTACIÓN
El uso continuado de la aplicación implica la aceptación plena de estos Términos y Condiciones. Cualquier actualización relevante requerirá la conformidad del usuario."""

    const val FULL_PRIVACY_POLICY = """POLÍTICA DE PRIVACIDAD
ASISTENTE ELECTORAL SV

Versión: 1.0 (Actualizada al 18 de agosto de 2026)
Aplicación: Asistente Virtual Electoral - Edición El Salvador

1. COMPROMISO FUNDAMENTAL CON LA PRIVACIDAD
En Asistente Electoral SV la privacidad y confidencialidad son valores primordiales. Nuestra arquitectura está diseñada bajo el principio de minimización de datos y privacidad por diseño (Privacy by Design).

2. DATOS QUE NO RECOPILAMOS
- NO solicitamos registro de usuario, correo electrónico, contraseñas ni números de teléfono.
- NO recopilamos Documentos Únicos de Identidad (DUI), padrones electorales ni información de votantes.
- NO rastreamos la ubicación geográfica precisa del usuario.
- NO creamos bases de datos remotas que asocien la identidad de las personas con las consultas realizadas.

3. ALMACENAMIENTO LOCAL EN EL DISPOSITIVO
- El consentimiento legal otorgado por el usuario se guarda exclusivamente en las preferencias locales (SharedPreferences) de este dispositivo.
- El historial de consultas se almacena en una base de datos local SQLite/Room dentro de la memoria interna del teléfono. El usuario puede borrar todo su historial en cualquier momento desde el menú de Ajustes o la pantalla de Chat.

4. PROCESAMIENTO DE CONSULTAS E INTELIGENCIA ARTIFICIAL
- Cuando el usuario realiza una consulta con conexión a internet activa, el texto de la pregunta es procesado a través de las APIs de Google Gemini / Google AI Studio para estructurar la respuesta orientativa basada en el Código Electoral.
- Dichas consultas se transmiten de manera cifrada (HTTPS/TLS) y no contienen identificadores personales del usuario.
- La aplicación cuenta con un motor de conocimiento local sin conexión para responder preguntas frecuentes sin necesidad de enviar datos a internet.

5. USO DEL MICRÓFONO (RECONOCIMIENTO DE VOZ)
- La función de consulta por voz utiliza el servicio nativo de reconocimiento de voz del sistema operativo Android.
- El micrófono se activa ÚNICAMENTE cuando el usuario presiona voluntariamente el botón de voz y se desactiva de inmediato al concluir la frase.
- El audio no se graba de manera permanente ni se almacena en servidores externos.

6. DERECHOS DEL USUARIO
El usuario tiene el control total sobre sus datos en la aplicación:
- Puede limpiar el historial completo de consultas con un solo toque en Ajustes.
- Puede desinstalar la aplicación en cualquier momento, lo que eliminará automáticamente todos los datos y preferencias locales almacenados.

7. CONTACTO Y ACTUALIZACIONES
Para cualquier consulta respecto a esta política de privacidad, puede revisar la sección de Información legal dentro de Ajustes en la aplicación."""
}
