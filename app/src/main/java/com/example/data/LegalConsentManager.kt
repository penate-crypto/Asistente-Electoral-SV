package com.example.data

import android.content.Context
import android.content.SharedPreferences

object LegalConsentManager {
    private const val PREFS_NAME = "asistente_electoral_legal_prefs"
    private const val KEY_LEGAL_CONSENT_ACCEPTED = "legalConsentAccepted"
    private const val KEY_LEGAL_VERSION = "legalDocumentsVersion"
    private const val KEY_ACCEPTED_TIMESTAMP = "legalConsentAcceptedTimestamp"

    const val CURRENT_LEGAL_VERSION = "1.1"
    const val APP_VERSION = "2.0.26"
    const val LAST_UPDATED_DATE = "22 de noviembre de 2026"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isConsentAccepted(context: Context): Boolean {
        val prefs = getPrefs(context)
        // Si cambia la versión de los textos legales, se vuelve a pedir la aceptación.
        return prefs.getBoolean(KEY_LEGAL_CONSENT_ACCEPTED, false) &&
            prefs.getString(KEY_LEGAL_VERSION, null) == CURRENT_LEGAL_VERSION
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

    const val INITIAL_NOTICE_SUMMARY = """La aplicación Asistente Electoral SV es una herramienta independiente de apoyo, consulta y capacitación complementaria para el personal que participa en los procesos electorales de El Salvador.

Esta aplicación NO es oficial del Tribunal Supremo Electoral de El Salvador (TSE), no representa al TSE ni a ninguna entidad de gobierno, y no sustituye las capacitaciones, instructivos, resoluciones ni decisiones de las autoridades electorales.

La aplicación NO utiliza inteligencia artificial, agentes ni asistentes externos para responder. Funciona con un algoritmo de búsqueda propio que consulta un banco de conocimiento incluido en la aplicación; no envía sus preguntas a ningún servidor. Las preguntas y las respuestas mostradas se guardan en el historial de este teléfono, que usted puede borrar cuando quiera.

La aplicación fue creada con apoyo de herramientas de inteligencia artificial (Google AI Studio, Gemini, ChatGPT, Claude y Claude Code); encontrará el detalle en los Términos y Condiciones.

No introduzca DUI de terceros, nombres de electores, teléfonos, fotografías de documentos ni información que permita identificar cómo votó una persona.

Las respuestas pueden estar incompletas o desactualizadas. Ante una situación real, siempre debe prevalecer la normativa y la instrucción oficial vigente.

Al seleccionar “Aceptar”, confirma que ha leído y comprendido este aviso y acepta consultar los Términos y Condiciones y la Política de Privacidad de la aplicación."""

    const val FULL_TERMS_AND_CONDITIONS = """TÉRMINOS Y CONDICIONES DE USO
ASISTENTE ELECTORAL SV

Versión: 1.1 (Actualizada al 22 de noviembre de 2026)
Aplicación: Asistente Electoral SV - Edición El Salvador

1. NATURALEZA DEL SERVICIO Y CARÁCTER INDEPENDIENTE
Asistente Electoral SV es una aplicación de desarrollo independiente creada exclusivamente como herramienta tecnológica de apoyo, consulta informativa, estudio cívico y capacitación complementaria en materia electoral de la República de El Salvador, dirigida al personal que participa en los procesos electorales.
Esta aplicación NO es una herramienta oficial del Tribunal Supremo Electoral de El Salvador (TSE), no representa de forma alguna a dicho organismo ni a ninguna entidad gubernamental, y NO sustituye las capacitaciones obligatorias, instructivos impresos, acuerdos, decretos o resoluciones jurisdiccionales emitidas por el TSE, las Juntas Electorales Departamentales (JED), las Juntas Electorales Municipales (JEM) o las Juntas Receptoras de Votos (JRV).

2. CÓMO FUNCIONA LA APLICACIÓN
La aplicación NO utiliza inteligencia artificial, modelos de lenguaje, agentes ni asistentes externos (como Gemini, ChatGPT, Claude u otros) para responder las consultas.
Las respuestas se obtienen con un algoritmo de búsqueda propio que se ejecuta en el teléfono: normaliza el texto de la pregunta, identifica palabras clave y las compara con un banco de conocimiento incluido en la aplicación. La aplicación solo muestra lo que existe en ese banco; si no encuentra una coincidencia suficiente, lo indica y no inventa respuestas.
El banco de conocimiento se elaboró a partir de documentos oficiales incluidos en la Biblioteca de la aplicación (Código Electoral, Constitución de la República, leyes, reglamentos e instructivos). Aun así, puede contener errores u omisiones, y los textos legales pueden haber cambiado después de la fecha de los documentos incluidos.
La consulta y la lectura de la Biblioteca funcionan sin conexión a internet. El dictado por voz depende del servicio de reconocimiento de voz del teléfono: funciona sin internet si el español está descargado en el teléfono (ver Ajustes › Voz sin conexión) y, si no, puede requerir internet (ver Política de Privacidad).

3. CÓMO SE CREÓ LA APLICACIÓN
La aplicación se creó con apoyo de herramientas de inteligencia artificial:
- Google AI Studio y Gemini: programación de la aplicación y elaboración del banco de conocimiento.
- ChatGPT y Claude: redacción de instrucciones (prompts), generación de imágenes y trabajo con los documentos PDF.
- Claude Code: actualización del banco de conocimiento y mejoras que continúan realizándose.
Estas herramientas se utilizaron para construir y mantener la aplicación; ninguna de ellas funciona dentro de la aplicación ni recibe las consultas de los usuarios.

4. USO ACEPTABLE Y PROTECCIÓN DE DATOS SENSIBLES
El usuario se compromete formalmente a utilizar la aplicación de buena fe y bajo estricto apego a las leyes de la República de El Salvador.
Queda TERMINANTEMENTE PROHIBIDO ingresar en los campos de consulta:
a) Números de Documento Único de Identidad (DUI) de terceros.
b) Nombres y apellidos completos de electores del padrón.
c) Números telefónicos, direcciones residenciales o datos de contacto personal.
d) Fotografías o copias de documentos de identidad, papeletas marcadas o folios del padrón electoral.
e) Cualquier dato que vulnere el secreto del sufragio o permita revelar el sentido del voto de un ciudadano.

5. ACCESO Y ALMACENAMIENTO LOCAL
La aplicación incluye una pantalla de acceso que solicita un usuario de 9 dígitos y una contraseña. En esta versión, esa pantalla funciona únicamente en el dispositivo: los datos ingresados no se envían a ningún servidor ni se verifican en línea. Si el usuario marca “Recordar usuario”, el número de usuario se guarda en este teléfono.
La aplicación no crea cuentas ni perfiles de usuario en servidores remotos. El consentimiento legal, la sesión y el historial de consultas se guardan de forma local en el dispositivo.
Cuando en una versión futura el acceso se conecte a un servidor, estos Términos y Condiciones y la Política de Privacidad se actualizarán y se solicitará nuevamente su aceptación antes de continuar.

6. PREVALENCIA DE LA AUTORIDAD ELECTORAL EN EL DÍA DE LA ELECCIÓN
Durante la jornada electoral, la máxima autoridad en cada mesa es la Junta Receptora de Votos (JRV) en pleno, bajo la dirección de su Presidente. Las respuestas brindadas por la aplicación tienen fines exclusivamente orientativos y formativos, por lo que no pueden invocarse como fundamento legal vinculante para contradecir decisiones formales de las autoridades electorales.

7. LIMITACIÓN DE RESPONSABILIDAD
Los desarrolladores de Asistente Electoral SV no se responsabilizan por decisiones operativas, omisiones, sanciones o interpretaciones erróneas derivadas del uso de la información suministrada por la aplicación. Es responsabilidad exclusiva del usuario verificar la normativa vigente aplicable a cada caso.

8. MODIFICACIONES Y ACEPTACIÓN
El uso continuado de la aplicación implica la aceptación plena de estos Términos y Condiciones. Cuando se publique una versión actualizada de estos textos, la aplicación solicitará nuevamente la aceptación del usuario."""

    const val FULL_PRIVACY_POLICY = """POLÍTICA DE PRIVACIDAD
ASISTENTE ELECTORAL SV

Versión: 1.1 (Actualizada al 22 de noviembre de 2026)
Aplicación: Asistente Electoral SV - Edición El Salvador

1. COMPROMISO CON LA PRIVACIDAD
En Asistente Electoral SV la privacidad y la confidencialidad son valores primordiales. La aplicación está diseñada bajo el principio de minimización de datos: funciona en el propio teléfono y esta versión no envía las consultas de los usuarios a ningún servidor.

2. QUÉ SE GUARDA EN SU TELÉFONO
- El consentimiento legal y la versión de los textos aceptados (en las preferencias locales del dispositivo).
- El historial de consultas: el texto de cada pregunta y la respuesta mostrada, en una base de datos local (SQLite/Room) en la memoria interna del teléfono.
- Si marca “Recordar usuario” en la pantalla de acceso: el número de usuario y el estado de la sesión.
- Para el control de sesión en este teléfono, un identificador del dispositivo y la fecha de la última sesión asociados al número de usuario que ingresó.
- Preferencias de la aplicación, como el tema claro u oscuro.
Estos datos permanecen en su dispositivo. Android puede incluirlos en las copias de seguridad del teléfono, según la configuración que usted tenga.

3. QUÉ NO RECOPILAMOS
- No recopilamos datos en servidores remotos ni creamos bases de datos que asocien la identidad de las personas con sus consultas.
- No solicitamos correo electrónico ni números de teléfono.
- No recopilamos padrones electorales ni información de votantes.
- No accedemos a su ubicación, contactos, fotografías ni cámara.
- Esta versión no incluye publicidad ni herramientas de analítica o rastreo.

4. CÓMO SE PROCESAN LAS CONSULTAS
- Las consultas se procesan completamente en el teléfono con un algoritmo de búsqueda propio y un banco de conocimiento incluido en la aplicación.
- La aplicación NO utiliza inteligencia artificial, modelos de lenguaje, agentes ni asistentes externos, y NO envía el texto de las preguntas a Gemini, ChatGPT, Claude ni a ningún otro servicio.
- La consulta de las respuestas y la lectura de los documentos de la Biblioteca funcionan sin internet.

5. MICRÓFONO Y RECONOCIMIENTO DE VOZ
- La función de consulta por voz utiliza el servicio de reconocimiento de voz del sistema Android.
- La aplicación pide al reconocedor que trabaje sin conexión cuando el teléfono tiene el español descargado (ver Ajustes › Voz sin conexión); en ese caso el audio puede procesarse en el propio teléfono.
- Si el teléfono no tiene el español descargado, el reconocedor puede necesitar internet y enviar el audio a los servidores de su proveedor (por ejemplo, Google), conforme a las políticas de ese proveedor. La aplicación solo recibe el texto transcrito.
- El micrófono se activa únicamente cuando usted presiona el botón de voz. La aplicación no graba ni guarda el audio.
- La lectura de las respuestas en voz alta usa el servicio de voz del teléfono.

6. ACCESO Y CUENTAS
- La pantalla de acceso de esta versión funciona solo en el dispositivo: el usuario y la contraseña no se envían a ningún servidor.
- Cuando en una versión futura el acceso se conecte a un servidor, se podrán tratar en él datos como el número de usuario y la contraseña. Antes de que eso ocurra, esta política se actualizará y se solicitará nuevamente su aceptación.

7. SUS DERECHOS Y CONTROL
- Puede borrar todo el historial de consultas en cualquier momento desde Ajustes o desde la pantalla de Chat.
- Puede dejar sin marcar la opción “Recordar usuario” para que la aplicación no recuerde su usuario ni mantenga la sesión iniciada al volver a abrirla.
- Puede desinstalar la aplicación en cualquier momento, lo que elimina los datos y preferencias locales almacenados en el teléfono.

8. CONTACTO
Por el momento esta versión de la aplicación no ofrece un canal de contacto. Si se habilita uno, se indicará en esta sección.

9. CAMBIOS EN ESTA POLÍTICA
Cuando se actualice esta política, cambiará su número de versión y la aplicación solicitará nuevamente su aceptación."""
}
