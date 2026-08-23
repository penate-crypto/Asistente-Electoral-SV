package com.example.data

import org.junit.Assert.*
import org.junit.Test

/**
 * Pruebas unitarias para el sistema de autenticación y validación de DUI/Contraseña.
 */
class AuthSessionManagerTest {

    @Test
    fun testFormatDuiInput_NineDigits() {
        val raw = "123456789"
        val formatted = AuthSessionManager.formatDuiInput(raw)
        assertEquals("12345678-9", formatted)
    }

    @Test
    fun testFormatDuiInput_AlreadyFormatted() {
        val raw = "12345678-9"
        val formatted = AuthSessionManager.formatDuiInput(raw)
        assertEquals("12345678-9", formatted)
    }

    @Test
    fun testFormatDuiInput_FewerDigits() {
        val raw = "12345"
        val formatted = AuthSessionManager.formatDuiInput(raw)
        assertEquals("12345", formatted)
    }

    @Test
    fun testExtractDuiDigits() {
        val formatted = "12345678-9"
        val digits = AuthSessionManager.extractDuiDigits(formatted)
        assertEquals("123456789", digits)
    }

    @Test
    fun testIsValidDuiFormat_Valid() {
        assertTrue("DUI de 9 dígitos debe ser válido", AuthSessionManager.isValidDuiFormat("12345678-9"))
        assertTrue("DUI sin guion debe ser válido", AuthSessionManager.isValidDuiFormat("123456789"))
    }

    @Test
    fun testIsValidDuiFormat_Invalid() {
        assertFalse("DUI menor a 9 dígitos debe ser inválido", AuthSessionManager.isValidDuiFormat("12345"))
        assertFalse("DUI vacío debe ser inválido", AuthSessionManager.isValidDuiFormat(""))
    }

    @Test
    fun testIsValidPasswordFormat() {
        assertTrue("Contraseña de 8 o más caracteres debe ser válida", AuthSessionManager.isValidPasswordFormat("Abc123@#$"))
        assertTrue("Contraseña numérica de 8 o más caracteres debe ser válida", AuthSessionManager.isValidPasswordFormat("12345678"))
        assertFalse("Contraseña menor a 8 caracteres debe ser inválida", AuthSessionManager.isValidPasswordFormat("12345"))
    }
}
