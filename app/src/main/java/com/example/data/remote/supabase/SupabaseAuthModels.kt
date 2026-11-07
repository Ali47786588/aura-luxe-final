package com.example.data.remote.supabase

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SupabaseUser(
    val id: String,
    val email: String? = null,
    val phone: String? = null,
    val role: String? = null,
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "user_metadata") val userMetadata: Map<String, String>? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseSession(
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "refresh_token") val refreshToken: String,
    @Json(name = "token_type") val tokenType: String = "bearer",
    @Json(name = "expires_in") val expiresIn: Long = 3600L,
    @Json(name = "expires_at") val expiresAt: Long? = null,
    val user: SupabaseUser? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseAuthRequest(
    val email: String,
    val password: String,
    val data: Map<String, String>? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseRefreshRequest(
    @Json(name = "refresh_token") val refreshToken: String
)

sealed class SupabaseAuthResult {
    data class Success(val session: SupabaseSession, val user: SupabaseUser?) : SupabaseAuthResult()
    data class ConfirmationRequired(
        val user: SupabaseUser?,
        val message: String = "Please confirm your email address before signing in."
    ) : SupabaseAuthResult()
    data class SignedOut(val message: String = "User successfully signed out.") : SupabaseAuthResult()
    data class Error(val message: String, val statusCode: Int? = null, val throwable: Throwable? = null) : SupabaseAuthResult()
}
