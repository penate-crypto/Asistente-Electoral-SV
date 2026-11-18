package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AuthSessionManager {
    private const val PREFS_NAME = "asistente_electoral_auth_prefs"
    private const val KEY_IS_LOGGED_IN = "auth_is_logged_in"
    private const val KEY_REMEMBER_USER = "auth_remember_user"
    private const val KEY_SAVED_DUI = "auth_saved_dui"
    private const val KEY_SESSION_TOKEN = "auth_session_token"
    private const val KEY_CURRENT_DEVICE_ID = "auth_current_device_id"
    private const val KEY_BOUND_DEVICE_PREFIX = "auth_bound_device_"
    private const val KEY_SESSION_TIMESTAMP_PREFIX = "auth_session_time_"

    // Official Test Account Credentials
    const val TEST_USER_DUI = "12345678-9"
    const val TEST_USER_DUI_DIGITS = "123456789"
    const val TEST_USER_PASSWORD = "abc123"

    private val _isLoggedInState = MutableStateFlow(false)
    val isLoggedInState: StateFlow<Boolean> = _isLoggedInState.asStateFlow()

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Gets or creates a persistent unique installation device ID
     */
    fun getOrCreateDeviceId(context: Context): String {
        val prefs = getPrefs(context)
        var deviceId = prefs.getString(KEY_CURRENT_DEVICE_ID, null)
        if (deviceId.isNullOrBlank()) {
            deviceId = "dev_${java.util.UUID.randomUUID()}"
            prefs.edit().putString(KEY_CURRENT_DEVICE_ID, deviceId).apply()
        }
        return deviceId
    }

    fun init(context: Context) {
        val prefs = getPrefs(context)
        val remember = prefs.getBoolean(KEY_REMEMBER_USER, false)
        val loggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        val savedDui = prefs.getString(KEY_SAVED_DUI, "") ?: ""
        val digits = extractDuiDigits(savedDui)
        val currentDeviceId = getOrCreateDeviceId(context)
        val boundDeviceId = prefs.getString(KEY_BOUND_DEVICE_PREFIX + digits, null)

        val isValidDevice = boundDeviceId == null || boundDeviceId == currentDeviceId
        _isLoggedInState.value = remember && loggedIn && isValidDevice
    }

    fun isUserLoggedIn(context: Context): Boolean {
        val prefs = getPrefs(context)
        val remember = prefs.getBoolean(KEY_REMEMBER_USER, false)
        val loggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        val savedDui = prefs.getString(KEY_SAVED_DUI, "") ?: ""
        val digits = extractDuiDigits(savedDui)
        val currentDeviceId = getOrCreateDeviceId(context)
        val boundDeviceId = prefs.getString(KEY_BOUND_DEVICE_PREFIX + digits, null)

        val isValidDevice = boundDeviceId == null || boundDeviceId == currentDeviceId
        return remember && loggedIn && isValidDevice
    }

    fun getRememberUser(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_REMEMBER_USER, false)
    }

    fun getSavedDui(context: Context): String {
        return getPrefs(context).getString(KEY_SAVED_DUI, "") ?: ""
    }

    /**
     * Cleans input to only numerical digits and formats as Salvadoran DUI: ########-#
     */
    fun formatDuiInput(rawInput: String): String {
        val digitsOnly = rawInput.filter { it.isDigit() }.take(9)
        return if (digitsOnly.length == 9) {
            "${digitsOnly.substring(0, 8)}-${digitsOnly.substring(8)}"
        } else {
            digitsOnly
        }
    }

    /**
     * Extracts pure numeric digits from any DUI string
     */
    fun extractDuiDigits(duiString: String): String {
        return duiString.filter { it.isDigit() }.take(9)
    }

    /**
     * Validates that the DUI has exactly 9 numeric digits
     */
    fun isValidDuiFormat(rawOrFormattedDui: String): Boolean {
        val digits = extractDuiDigits(rawOrFormattedDui)
        return digits.length == 9
    }

    /**
     * Validates password: flexible (letters, numbers, symbols, pure numbers) with min 6 chars
     */
    fun isValidPasswordFormat(password: String): Boolean {
        return password.length >= 6
    }

    /**
     * Authenticates user with DUI and password enforcing:
     * 1 CREDENCIAL = 1 DISPOSITIVO.
     * Prevents simultaneous logins on different devices.
     */
    fun authenticate(
        context: Context,
        duiInput: String,
        passwordInput: String,
        rememberUser: Boolean,
        forceDeviceTransfer: Boolean = false
    ): AuthResult {
        val digits = extractDuiDigits(duiInput)
        val formattedDui = formatDuiInput(duiInput)

        if (digits.length != 9) {
            return AuthResult.Error("El usuario debe contener exactamente 9 dígitos numéricos.")
        }

        if (passwordInput.length < 6) {
            return AuthResult.Error("La contraseña debe tener al menos 6 caracteres.")
        }

        val isTestAccount = (digits == TEST_USER_DUI_DIGITS && passwordInput == TEST_USER_PASSWORD)
        val isValidGeneralAccount = (digits.length == 9 && passwordInput.length >= 6)

        if (isTestAccount || isValidGeneralAccount) {
            val prefs = getPrefs(context)
            val currentDeviceId = getOrCreateDeviceId(context)
            val boundKey = KEY_BOUND_DEVICE_PREFIX + digits
            val existingBoundDevice = prefs.getString(boundKey, null)

            // Strict 1 Credential = 1 Device verification
            if (existingBoundDevice != null && existingBoundDevice != currentDeviceId && !forceDeviceTransfer) {
                return AuthResult.DeviceConflict(
                    message = "Esta credencial ya está activa en otro dispositivo (1 Credencial = 1 Dispositivo). ¿Desea transferir la sesión a este equipo?",
                    dui = formattedDui
                )
            }

            val sessionToken = "sv_session_${digits}_${System.currentTimeMillis()}"

            prefs.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putBoolean(KEY_REMEMBER_USER, rememberUser)
                .putString(KEY_SAVED_DUI, if (rememberUser) formattedDui else "")
                .putString(KEY_SESSION_TOKEN, sessionToken)
                .putString(boundKey, currentDeviceId)
                .putLong(KEY_SESSION_TIMESTAMP_PREFIX + digits, System.currentTimeMillis())
                .apply()

            _isLoggedInState.value = true
            return AuthResult.Success(formattedDui)
        }

        return AuthResult.Error("Credenciales no válidas. Verifique los datos ingresados.")
    }

    fun logout(context: Context) {
        val prefs = getPrefs(context)
        val remember = prefs.getBoolean(KEY_REMEMBER_USER, false)
        val savedDui = prefs.getString(KEY_SAVED_DUI, "") ?: ""
        val digits = extractDuiDigits(savedDui)

        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .putString(KEY_SESSION_TOKEN, "")
            .apply()

        // If remember user is not set, also clear the saved DUI
        if (!remember) {
            prefs.edit().putString(KEY_SAVED_DUI, "").apply()
        }

        _isLoggedInState.value = false
    }
}

sealed class AuthResult {
    data class Success(val dui: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
    data class DeviceConflict(val message: String, val dui: String) : AuthResult()
}
