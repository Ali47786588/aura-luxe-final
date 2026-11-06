package com.example

import com.example.data.model.UserRole
import com.example.security.SecurityManager
import org.junit.Assert.*
import org.junit.Test

class SecurityManagerTest {

    @Test
    fun testAuthenticationSuccessAndSessionLifecycle() {
        val authResult = SecurityManager.authenticate("7890")
        assertTrue("Authentication should succeed for valid credentials", authResult.success)
        assertEquals(UserRole.SUPER_ADMIN, authResult.role)
        assertNotNull(authResult.token)

        val token = authResult.token!!
        assertTrue("Session should be valid for SUPER_ADMIN", SecurityManager.isValidSession(token, UserRole.SUPER_ADMIN))
        assertTrue("SuperAdmin should be authorized for CLINIC_ADMIN actions", SecurityManager.isValidSession(token, UserRole.CLINIC_ADMIN))

        // Invalidate session
        SecurityManager.invalidateSession(token)
        assertFalse("Session should be invalid after explicit logout/invalidation", SecurityManager.isValidSession(token, UserRole.SUPER_ADMIN))
    }

    @Test
    fun testAuthenticationClinicDirector() {
        val authResult = SecurityManager.authenticate("2026")
        assertTrue(authResult.success)
        assertEquals(UserRole.CLINIC_ADMIN, authResult.role)
        assertNotNull(authResult.token)

        val token = authResult.token!!
        assertTrue(SecurityManager.isValidSession(token, UserRole.CLINIC_ADMIN))
        assertFalse("Clinic admin cannot access SUPER_ADMIN exclusive role", SecurityManager.isValidSession(token, UserRole.SUPER_ADMIN))
    }

    @Test
    fun testInvalidCredentialsAndLockout() {
        // Authenticate with random invalid pin
        val invalid1 = SecurityManager.authenticate("999999")
        assertFalse(invalid1.success)

        val invalid2 = SecurityManager.authenticate("888888")
        assertFalse(invalid2.success)

        val invalid3 = SecurityManager.authenticate("777777")
        assertFalse(invalid3.success)
        assertTrue("Lockout should be active after 3 failed attempts", invalid3.isLockedOut)
        assertTrue(invalid3.remainingSeconds > 0)
    }

    @Test
    fun testStrictNationalIdVerification() {
        // Valid checksums (Modulo 11)
        assertTrue(SecurityManager.isValidNationalId("0019283741", strictChecksum = true))
        assertTrue(SecurityManager.isValidNationalId("0074829106", strictChecksum = true))
        assertTrue(SecurityManager.isValidNationalId("0061928372", strictChecksum = true))
        assertTrue(SecurityManager.isValidNationalId("0451829484", strictChecksum = true))

        // Invalid checksums
        assertFalse(SecurityManager.isValidNationalId("0074829103", strictChecksum = true))
        assertFalse(SecurityManager.isValidNationalId("0000000000", strictChecksum = true))
        assertFalse(SecurityManager.isValidNationalId("1111111111", strictChecksum = true))
        assertFalse(SecurityManager.isValidNationalId("123", strictChecksum = true))
        assertFalse(SecurityManager.isValidNationalId("abcdefghij", strictChecksum = true))
    }

    @Test
    fun testStrictShebaVerification() {
        // Valid ISO 7064 Sheba numbers
        assertTrue(SecurityManager.isValidShebaNumber("IR910120000000001829304192", strictChecksum = true))
        assertTrue(SecurityManager.isValidShebaNumber("IR480170000000009928172635", strictChecksum = true))
        assertTrue(SecurityManager.isValidShebaNumber("IR090560000000008827162534", strictChecksum = true))

        // Invalid Sheba numbers
        assertFalse(SecurityManager.isValidShebaNumber("IR820120000000001829304192", strictChecksum = true))
        assertFalse(SecurityManager.isValidShebaNumber("IR000000000000000000000000", strictChecksum = true))
        assertFalse(SecurityManager.isValidShebaNumber("IR12345", strictChecksum = true))
        assertFalse(SecurityManager.isValidShebaNumber("US910120000000001829304192", strictChecksum = true))
    }

    @Test
    fun testInputSanitization() {
        val dirty = "  <script>alert('xss')</script>Hello <b>World</b> & 'test'  "
        val clean = SecurityManager.sanitizeInput(dirty)
        assertFalse("Script tag should be removed", clean.contains("<script"))
        assertFalse("HTML angle brackets should be stripped", clean.contains("<") || clean.contains(">"))
        assertFalse("Quotes should be stripped", clean.contains("'") || clean.contains("\""))
        assertTrue(clean.contains("Hello"))
        assertTrue(clean.contains("World"))
    }
}
