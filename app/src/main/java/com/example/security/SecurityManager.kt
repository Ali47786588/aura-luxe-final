package com.example.security

import com.example.data.model.UserRole
import java.math.BigInteger
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

/**
 * Enterprise Security Manager - Phase 2 (Honest Security & Demo Separation)
 *
 * CRITICAL ARCHITECTURAL DISCLOSURE:
 * - Client-side role checks and embedded hash comparisons DO NOT constitute real production security.
 * - Any embedded hash or token in an APK can be extracted or bypassed by a determined client decompiler.
 * - This module strictly separates DEMO Sandbox Evaluation from real production authentication.
 * - Until a secure backend (such as Supabase Auth) is deployed, all administrative and financial
 *   actions are explicitly labeled and operated in DEMO/OFFLINE mode. Real financial disbursements
 *   and real banking API calls are permanently DISABLED.
 */
object SecurityManager {

    /**
     * Operational Flags
     */
    const val IS_DEMO_MODE: Boolean = true
    const val IS_REAL_FINANCIAL_OPERATIONS_ENABLED: Boolean = false
    const val IS_REAL_ESCROW_RELEASE_ENABLED: Boolean = false

    private const val DEMO_SALT = "AURA_LUXE_ENTERPRISE_2026_SEC_SALT"
    private val random = SecureRandom()

    // Demo simulation digests for UI sandbox demonstration only.
    // Explicitly labeled as demo sandbox values. NOT used for production authentication.
    private const val DEMO_SUPER_ADMIN_DIGEST_HEX = "0fd4e199e1b2838b8e7abb76ef8354b69fc68e280091252ba14a29d16664e06b"
    private const val DEMO_CLINIC_DIRECTOR_DIGEST_HEX = "2bbade33218cbb95f8fb83431e2b0a15d140d7a47bb07ad38fa7dbf249c7dfa6"

    // Active session store with expiration (30-minute idle TTL)
    private const val SESSION_TTL_MS = 30 * 60 * 1000L
    private val activeSessions = ConcurrentHashMap<String, ActiveSession>()

    // Anti-Brute Force Tracking for Demo Sandbox
    private var failedAttempts = 0
    private var lockoutUntilTimeMs: Long = 0L

    data class ActiveSession(
        val token: String,
        val role: UserRole,
        val createdAtMs: Long,
        val lastActivityMs: Long,
        val isDemoSession: Boolean = true
    )

    data class AuthResult(
        val success: Boolean,
        val role: UserRole? = null,
        val token: String? = null,
        val isLockedOut: Boolean = false,
        val remainingSeconds: Int = 0,
        val errorMessage: String? = null,
        val isDemoAuthentication: Boolean = true
    )

    fun hashWithSalt(input: String): ByteArray {
        val md = MessageDigest.getInstance("SHA-256")
        val salted = "$DEMO_SALT:$input:$DEMO_SALT"
        return md.digest(salted.toByteArray(Charsets.UTF_8))
    }

    private fun hexStringToByteArray(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    private val demoSuperAdminDigest = hexStringToByteArray(DEMO_SUPER_ADMIN_DIGEST_HEX)
    private val demoClinicDirectorDigest = hexStringToByteArray(DEMO_CLINIC_DIRECTOR_DIGEST_HEX)

    /**
     * Demo Sandbox Authenticator
     * Clearly identified as demo authentication for UI evaluation.
     */
    @Synchronized
    fun authenticateDemo(pin: String): AuthResult {
        val currentTime = System.currentTimeMillis()
        if (lockoutUntilTimeMs > currentTime) {
            val remaining = ((lockoutUntilTimeMs - currentTime) / 1000).toInt() + 1
            return AuthResult(
                success = false,
                isLockedOut = true,
                remainingSeconds = remaining,
                errorMessage = "سرویس آزمایشی مسدود است. لطفاً $remaining ثانیه منتظر بمانید.",
                isDemoAuthentication = true
            )
        }

        val cleanPin = pin.trim()
        if (cleanPin.length < 4 || cleanPin.length > 16) {
            recordFailedAttempt(currentTime)
            return AuthResult(
                success = false,
                errorMessage = "طول گذرواژه وارد شده نامعتبر است.",
                isDemoAuthentication = true
            )
        }

        val inputDigest = hashWithSalt(cleanPin)

        // Constant-time check for demo evaluation
        val isSuperAdmin = MessageDigest.isEqual(inputDigest, demoSuperAdminDigest)
        val isClinicDirector = MessageDigest.isEqual(inputDigest, demoClinicDirectorDigest)

        val targetRole = when {
            isSuperAdmin -> UserRole.SUPER_ADMIN
            isClinicDirector -> UserRole.CLINIC_ADMIN
            else -> null
        }

        return if (targetRole != null) {
            failedAttempts = 0
            lockoutUntilTimeMs = 0L
            val token = generateSessionToken(targetRole)
            activeSessions[token] = ActiveSession(
                token = token,
                role = targetRole,
                createdAtMs = currentTime,
                lastActivityMs = currentTime,
                isDemoSession = true
            )
            AuthResult(
                success = true,
                role = targetRole,
                token = token,
                isDemoAuthentication = true
            )
        } else {
            recordFailedAttempt(currentTime)
        }
    }

    // Retain alias for existing call sites while making demo nature explicit
    fun authenticate(pin: String): AuthResult = authenticateDemo(pin)

    private fun recordFailedAttempt(currentTime: Long): AuthResult {
        failedAttempts += 1
        return if (failedAttempts >= 3) {
            lockoutUntilTimeMs = currentTime + 60_000L // 60 seconds lockout
            AuthResult(
                success = false,
                isLockedOut = true,
                remainingSeconds = 60,
                errorMessage = "۳ تلاش ناموفق پیاپی. قفل امنیتی به مدت ۶۰ ثانیه فعال گردید.",
                isDemoAuthentication = true
            )
        } else {
            val attemptsLeft = 3 - failedAttempts
            AuthResult(
                success = false,
                isLockedOut = false,
                errorMessage = "رمز عبور نادرست است. $attemptsLeft تلاش باقی‌مانده.",
                isDemoAuthentication = true
            )
        }
    }

    @Synchronized
    fun isValidSession(token: String?, requiredRole: UserRole): Boolean {
        if (token.isNullOrBlank()) return false
        val session = activeSessions[token] ?: return false
        val now = System.currentTimeMillis()

        if (now - session.lastActivityMs > SESSION_TTL_MS) {
            activeSessions.remove(token)
            return false
        }

        val hasPermission = session.role == UserRole.SUPER_ADMIN || session.role == requiredRole
        if (!hasPermission) return false

        activeSessions[token] = session.copy(lastActivityMs = now)
        return true
    }

    @Synchronized
    fun invalidateSession(token: String?) {
        if (!token.isNullOrBlank()) {
            activeSessions.remove(token)
        }
    }

    private fun generateSessionToken(role: UserRole): String {
        val bytes = ByteArray(32)
        random.nextBytes(bytes)
        val randHex = bytes.joinToString("") { "%02x".format(it) }
        return "aura_demo_${role.name.lowercase(Locale.ROOT)}_$randHex"
    }

    /**
     * Official Iranian National ID (کد ملی) Checksum Algorithm
     * Verifies 10-digit number modulo 11 checksum.
     */
    fun isValidNationalId(nationalId: String, strictChecksum: Boolean = true): Boolean {
        val clean = nationalId.trim().replace("\\s".toRegex(), "")
        if (!clean.matches(Regex("^\\d{10}$"))) return false

        val allSame = clean.all { it == clean[0] }
        if (allSame) return false

        if (!strictChecksum) return true

        var sum = 0
        for (i in 0 until 9) {
            sum += clean[i].digitToInt() * (10 - i)
        }

        val remainder = sum % 11
        val checkDigit = clean[9].digitToInt()

        return if (remainder < 2) {
            checkDigit == remainder
        } else {
            checkDigit == (11 - remainder)
        }
    }

    /**
     * ISO 7064 MOD 97-10 Iranian Sheba (IBAN) Checksum Validator
     * Format: IR + 24 numeric digits = 26 alphanumeric chars.
     */
    fun isValidShebaNumber(sheba: String, strictChecksum: Boolean = true): Boolean {
        val clean = sheba.trim().uppercase(Locale.ROOT).replace("\\s".toRegex(), "")
        if (!clean.matches(Regex("^IR\\d{24}$"))) return false
        if (!strictChecksum) return true

        val numericString = clean.substring(4) + "1827" + clean.substring(2, 4)
        return try {
            val bigInt = BigInteger(numericString)
            bigInt.mod(BigInteger.valueOf(97L)) == BigInteger.ONE
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Sanitizes inputs to prevent script execution, HTML tag injection, or control character abuse.
     */
    fun sanitizeInput(input: String): String {
        return input.trim()
            .replace("<script.*?>.*?</script>".toRegex(RegexOption.IGNORE_CASE), "")
            .replace("[<>&\"']".toRegex(), "")
            .take(500)
    }
}
