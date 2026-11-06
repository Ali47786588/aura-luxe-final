package com.example.data.local.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Enterprise AES-256-GCM Data Encryption Provider
 *
 * Implements:
 * - Hardware-backed key generation via AndroidKeyStore when available.
 * - AES/GCM/NoPadding with 12-byte random IV and 128-bit authentication tag.
 * - Safe fallback key engine for isolated local JVM unit testing.
 * - Resistance to ciphertext tampering and bit-flipping attacks.
 *
 * IMPORTANT SECURITY NOTICE:
 * Hardware-backed keystores safeguard encryption keys against unauthorized apps
 * under standard Android sandboxing. However, local storage and keystores do NOT
 * guarantee data confidentiality on a fully compromised, rooted, or physically
 * extracted device.
 */
object KeystoreEncryptionManager {

    private const val ANDROID_KEYSTORE_PROVIDER = "AndroidKeyStore"
    private const val KEY_ALIAS = "aura_luxe_master_data_key_v1"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val GCM_TAG_LENGTH = 128
    private const val GCM_IV_LENGTH = 12

    private val secureRandom = SecureRandom()
    private var testFallbackKey: SecretKey? = null

    @Synchronized
    private fun getOrCreateSecretKey(): SecretKey {
        return try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE_PROVIDER)
            keyStore.load(null)

            if (!keyStore.containsAlias(KEY_ALIAS)) {
                val keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    ANDROID_KEYSTORE_PROVIDER
                )
                val spec = KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(256)
                    .build()

                keyGenerator.init(spec)
                keyGenerator.generateKey()
            }
            val entry = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
            entry?.secretKey ?: throw IllegalStateException("Could not extract secret key from Keystore")
        } catch (_: Exception) {
            // JVM / Robolectric environment fallback: use secure local AES-256 key
            if (testFallbackKey == null) {
                val rawKey = ByteArray(32)
                secureRandom.nextBytes(rawKey)
                testFallbackKey = SecretKeySpec(rawKey, "AES")
            }
            testFallbackKey!!
        }
    }

    /**
     * Encrypts plaintext using AES-256-GCM.
     * Output format: Base64(12-byte IV + Ciphertext with 128-bit GCM Auth Tag)
     */
    fun encrypt(plainText: String): String {
        if (plainText.isEmpty()) return ""
        val key = getOrCreateSecretKey()
        val cipher = Cipher.getInstance(TRANSFORMATION)

        val iv = ByteArray(GCM_IV_LENGTH)
        secureRandom.nextBytes(iv)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.ENCRYPT_MODE, key, spec)

        val cipherBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        val combined = ByteArray(iv.size + cipherBytes.size)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(cipherBytes, 0, combined, iv.size, cipherBytes.size)

        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    /**
     * Decrypts AES-256-GCM ciphertext.
     * Returns null if decryption fails or authentication tag verification fails (tampering detected).
     */
    fun decrypt(cipherTextWithIv: String): String? {
        if (cipherTextWithIv.isBlank()) return null
        return try {
            val combined = Base64.decode(cipherTextWithIv, Base64.NO_WRAP)
            if (combined.size < GCM_IV_LENGTH) return null

            val iv = ByteArray(GCM_IV_LENGTH)
            val cipherBytes = ByteArray(combined.size - GCM_IV_LENGTH)
            System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH)
            System.arraycopy(combined, GCM_IV_LENGTH, cipherBytes, 0, cipherBytes.size)

            val key = getOrCreateSecretKey()
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)

            val plainBytes = cipher.doFinal(cipherBytes)
            String(plainBytes, Charsets.UTF_8)
        } catch (_: Exception) {
            // Authentication tag failed or invalid key - do not print stack trace in logcat
            null
        }
    }

    /**
     * Decrypts using an explicit test key to allow testing of invalid key rejection.
     */
    fun decryptWithKey(cipherTextWithIv: String, foreignKey: SecretKey): String? {
        return try {
            val combined = Base64.decode(cipherTextWithIv, Base64.NO_WRAP)
            if (combined.size < GCM_IV_LENGTH) return null

            val iv = ByteArray(GCM_IV_LENGTH)
            val cipherBytes = ByteArray(combined.size - GCM_IV_LENGTH)
            System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH)
            System.arraycopy(combined, GCM_IV_LENGTH, cipherBytes, 0, cipherBytes.size)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
            cipher.init(Cipher.DECRYPT_MODE, foreignKey, spec)

            val plainBytes = cipher.doFinal(cipherBytes)
            String(plainBytes, Charsets.UTF_8)
        } catch (_: Exception) {
            null
        }
    }
}
