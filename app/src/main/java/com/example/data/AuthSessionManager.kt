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

    // Official Test Account Credentials
    const val TEST_USER_DUI = "12345678-9"
    const val TEST_USER_DUI_DIGITS = "123456789"
    const val TEST_USER_PASSWORD = "Abc123@#$"

    private val _isLoggedInState = MutableStateFlow(false)
    val isLoggedInState: StateFlow<Boolean> = _isLoggedInState.asStateFlow()

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun init(context: Context) {
        val prefs = getPrefs(context)
        val remember = prefs.getBoolean(KEY_REMEMBER_USER, false)
        val loggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        _isLoggedInState.value = remember && loggedIn
    }

    fun isUserLoggedIn(context: Context): Boolean {
        val prefs = getPrefs(context)
        val remember = prefs.getBoolean(KEY_REMEMBER_USER, false)
        val loggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        return remember && loggedIn
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
     * Validates password: flexible (letters, numbers, symbols, pure numbers) with min 8 chars
     */
    fun isValidPasswordFormat(password: String): Boolean {
        return password.length >= 8
    }

    /**
     * Authenticates user with DUI and password.
     * Supports the test account (12345678-9 / Abc123@#$) and standard accounts matching valid format.
     */
    fun authenticate(context: Context, duiInput: String, passwordInput: String, rememberUser: Boolean): AuthResult {
        val digits = extractDuiDigits(duiInput)
        val formattedDui = formatDuiInput(duiInput)

        if (digits.length != 9) {
            return AuthResult.Error("El usuario debe contener exactamente 9 dígitos numéricos.")
        }

        if (passwordInput.length < 8) {
            return AuthResult.Error("La contraseña debe tener al menos 8 caracteres.")
        }

        // Test account validation or valid formatted credential verification
        val isTestAccount = (digits == TEST_USER_DUI_DIGITS && passwordInput == TEST_USER_PASSWORD)
        val isValidGeneralAccount = (digits.length == 9 && passwordInput.length >= 8)

        if (isTestAccount || isValidGeneralAccount) {
            val prefs = getPrefs(context)
            val sessionToken = "sv_session_${digits}_${System.currentTimeMillis()}"
            
            prefs.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putBoolean(KEY_REMEMBER_USER, rememberUser)
                .putString(KEY_SAVED_DUI, if (rememberUser) formattedDui else "")
                .putString(KEY_SESSION_TOKEN, sessionToken)
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
}
