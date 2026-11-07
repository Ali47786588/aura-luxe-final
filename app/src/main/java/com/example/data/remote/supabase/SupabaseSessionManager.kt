package com.example.data.remote.supabase

import androidx.room.withTransaction
import com.example.data.local.AuraDatabase
import kotlinx.coroutines.CancellationException
import com.example.data.local.entity.UserSessionEntity
import com.example.data.local.security.KeystoreEncryptionManager
import com.example.data.model.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Manages persistent Supabase sessions.
 * Tokens are encrypted before being stored in Room.
 */
class SupabaseSessionManager(
    private val database: AuraDatabase? = null,
    private val supabaseClient: SupabaseClient = SupabaseClient(),
    private val appContext: android.content.Context? = null
) {

    private val authStatePreferences = appContext?.getSharedPreferences(
        "aura_luxe_auth_state",
        android.content.Context.MODE_PRIVATE
    )

    private fun isLogoutPending(): Boolean =
        authStatePreferences?.getBoolean("supabase_logout_pending", false) ?: true

    private fun writeLogoutPending(pending: Boolean): Boolean {
        val preferences = authStatePreferences ?: return false
        val editor = preferences.edit()
        if (pending) {
            editor.putBoolean("supabase_logout_pending", true)
        } else {
            editor.remove("supabase_logout_pending")
        }
        return editor.commit()
    }

    private val managerScope = CoroutineScope(Dispatchers.IO)

    private val _currentSession = MutableStateFlow<SupabaseSession?>(null)
    val currentSession: StateFlow<SupabaseSession?> = _currentSession.asStateFlow()

    private val _currentUser = MutableStateFlow<SupabaseUser?>(null)
    val currentUser: StateFlow<SupabaseUser?> = _currentUser.asStateFlow()

    val isAuthenticated: Boolean
        get() {
            val session = _currentSession.value ?: return false
            val expiry = session.expiresAt ?: return false

            return session.accessToken.isNotBlank() &&
                expiry > System.currentTimeMillis() / 1000L
        }

    init {
        restoreSessionFromLocalDb()
    }

    /**
     * Encrypt and persist both tokens before publishing authenticated state.
     */
    suspend fun saveSession(
        session: SupabaseSession,
        user: SupabaseUser? = null
    ) = withContext(Dispatchers.IO) {

        val db = database
            ?: throw IllegalStateException("Session database is unavailable.")

        val resolvedUser = user ?: session.user
            ?: throw IllegalStateException("Authenticated user information is missing.")

        if (resolvedUser.id.isBlank()) {
            throw IllegalStateException("Authenticated user ID is empty.")
        }

        if (session.accessToken.isBlank() || session.refreshToken.isBlank()) {
            throw IllegalStateException("Supabase session tokens are incomplete.")
        }

        val now = System.currentTimeMillis()
        val nowSeconds = now / 1000L

        val expirySeconds = session.expiresAt
            ?: (nowSeconds + session.expiresIn.coerceAtLeast(1L))

        if (expirySeconds <= nowSeconds) {
            throw IllegalStateException("Supabase access token is already expired.")
        }

        val expiryMs = if (expirySeconds > Long.MAX_VALUE / 1000L) {
            Long.MAX_VALUE
        } else {
            expirySeconds * 1000L
        }

        val safeUser = SupabaseUser(
            id = resolvedUser.id,
            email = resolvedUser.email,
            role = "authenticated"
        )

        val normalizedSession = session.copy(
            expiresAt = expirySeconds,
            expiresIn = (expirySeconds - nowSeconds).coerceAtLeast(1L),
            user = safeUser
        )

        val encryptedAccess = KeystoreEncryptionManager.encrypt(
            normalizedSession.accessToken
        )

        val encryptedRefresh = KeystoreEncryptionManager.encrypt(
            normalizedSession.refreshToken
        )

        val entity = UserSessionEntity(
            token = encryptedAccess,
            userId = safeUser.id,
            role = UserRole.PUBLIC,
            createdAtMs = now,
            lastActivityMs = now,
            isDemoSession = false,
            encryptedRefreshToken = encryptedRefresh,
            expiresAtMs = expiryMs,
            email = safeUser.email
        )

        db.withTransaction {
            val previousSession = db.userSessionDao().getLatestSupabaseSession()
            db.userSessionDao().insertSession(entity)
            if (previousSession != null && previousSession.token != encryptedAccess) {
                db.userSessionDao().deleteSessionByToken(previousSession.token)
            }
        }

        if (!writeLogoutPending(false)) {
            throw IllegalStateException("Unable to clear the persistent logout marker.")
        }

        _currentSession.value = normalizedSession
        _currentUser.value = safeUser
    }

    /**
     * Restore a valid session or refresh an expired access token.
     */
    fun restoreSessionFromLocalDb() {
        val db = database ?: return

        managerScope.launch {
            if (isLogoutPending()) {
                _currentSession.value = null
                _currentUser.value = null
                return@launch
            }
            try {
                val now = System.currentTimeMillis()
                val thirtyDaysAgo = now - (30L * 24 * 60 * 60 * 1000)

                db.userSessionDao().purgeExpiredSessions(thirtyDaysAgo)

                val saved = db.userSessionDao().getLatestSupabaseSession()
                    ?: return@launch

                if (saved.isDemoSession) {
                    return@launch
                }

                val accessToken = KeystoreEncryptionManager.decrypt(saved.token)
                val refreshToken = KeystoreEncryptionManager.decrypt(
                    saved.encryptedRefreshToken
                )

                if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank()) {
                    db.userSessionDao().deleteSessionByToken(saved.token)
                    return@launch
                }

                val nowSeconds = System.currentTimeMillis() / 1000L
                val expirySeconds = saved.expiresAtMs / 1000L

                if (expirySeconds <= nowSeconds) {

                    when (val result = supabaseClient.refreshSession(refreshToken)) {

                        is SupabaseAuthResult.Success -> {
                            saveSession(result.session, result.user)
                        }

                        is SupabaseAuthResult.Error -> {
                            val responseCode = result.statusCode
                                ?: Regex("Refresh failed: (\\d+)")
                                    .find(result.message)
                                    ?.groupValues
                                    ?.getOrNull(1)
                                    ?.toIntOrNull()

                            if (responseCode == 400 ||
                                responseCode == 401 ||
                                responseCode == 403
                            ) {
                                db.userSessionDao().deleteSessionByToken(saved.token)
                            }
                        }

                        else -> {
                            // Keep the stored session for a later retry.
                        }
                    }

                    return@launch
                }

                val restoredUser = SupabaseUser(
                    id = saved.userId,
                    email = saved.email,
                    role = "authenticated"
                )

                val restoredSession = SupabaseSession(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    expiresIn = (expirySeconds - nowSeconds).coerceAtLeast(1L),
                    expiresAt = expirySeconds,
                    user = restoredUser
                )

                _currentSession.value = restoredSession
                _currentUser.value = restoredUser

            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                // Keep local data if recovery fails temporarily.
                // Never publish an unverified session as authenticated.
            }
        }
    }

    suspend fun clearSession(): SupabaseAuthResult = withContext(Dispatchers.IO) {
        var oldAccessToken = _currentSession.value?.accessToken
        _currentSession.value = null
        _currentUser.value = null

        var markerWritten = false
        var cleanupError: Exception? = null

        try {
            markerWritten = writeLogoutPending(true)
            if (!markerWritten) {
                cleanupError = IllegalStateException("Unable to persist logout marker.")
            }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            cleanupError = error
        }

        try {
            val db = database
                ?: throw IllegalStateException("Local session database is unavailable.")

            db.withTransaction {
                val saved = db.userSessionDao().getLatestSupabaseSession()
                if (saved != null) {
                    if (oldAccessToken.isNullOrBlank()) {
                        oldAccessToken = KeystoreEncryptionManager.decrypt(saved.token)
                    }
                    db.userSessionDao().deleteSessionByToken(saved.token)
                }
            }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            if (cleanupError == null) cleanupError = error
        }

        var remoteError: SupabaseAuthResult.Error? = null
        if (!oldAccessToken.isNullOrBlank()) {
            try {
                when (val result = supabaseClient.signOut(oldAccessToken!!)) {
                    is SupabaseAuthResult.Error -> remoteError = result
                    else -> Unit
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                remoteError = SupabaseAuthResult.Error(
                    message = "Remote sign-out could not be confirmed.",
                    throwable = error
                )
            }
        }

        if (cleanupError != null || !markerWritten) {
            return@withContext SupabaseAuthResult.Error(
                message = "Remote sign-out was attempted, but local logout could not be fully confirmed.",
                statusCode = remoteError?.statusCode,
                throwable = cleanupError ?: remoteError?.throwable
            )
        }

        if (remoteError != null) {
            return@withContext SupabaseAuthResult.Error(
                message = "Local session was closed, but remote sign-out could not be confirmed: ${remoteError.message}",
                statusCode = remoteError.statusCode,
                throwable = remoteError.throwable
            )
        }

        SupabaseAuthResult.SignedOut(
            if (oldAccessToken.isNullOrBlank()) {
                "Local session closed. No remote access token was available."
            } else {
                "Local session closed and remote sign-out confirmed."
            }
        )
    }

}
