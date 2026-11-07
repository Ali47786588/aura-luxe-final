package com.example.data.remote.supabase

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Enterprise Supabase Kotlin Client for Android.
 *
 * Implements:
 * - GoTrue Authentication (Sign-up, Sign-in, Sign-out, Refresh, User Profile).
 * - PostgREST safe synchronization with Row-Level Security (RLS).
 * - Safe offline and network error handling without crashing.
 * - Zero secret keys (uses strictly Supabase publishable anonymous key).
 */
class SupabaseClient(
    private val projectUrl: String = SupabaseConfig.PROJECT_URL,
    private val anonKey: String = SupabaseConfig.ANON_KEY,
    private val okHttpClient: OkHttpClient = defaultHttpClient
) {

    companion object {
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaTypeOrNull()

        private val defaultHttpClient: OkHttpClient by lazy {
            OkHttpClient.Builder()
                .connectTimeout(SupabaseConfig.TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(SupabaseConfig.TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(SupabaseConfig.TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build()
        }

        private val moshi: Moshi by lazy {
            Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        }
    }

    private val sessionAdapter = moshi.adapter(SupabaseSession::class.java)
    private val userAdapter = moshi.adapter(SupabaseUser::class.java)
    private val authReqAdapter = moshi.adapter(SupabaseAuthRequest::class.java)
    private val refreshReqAdapter = moshi.adapter(SupabaseRefreshRequest::class.java)

    /**
     * Supabase User Registration (GoTrue Sign-Up)
     */
    suspend fun signUp(email: String, pass: String, metadata: Map<String, String>? = null): SupabaseAuthResult =
        withContext(Dispatchers.IO) {
            try {
                val reqBodyJson = authReqAdapter.toJson(SupabaseAuthRequest(email, pass, metadata))
                val request = Request.Builder()
                    .url("$projectUrl${SupabaseConfig.AUTH_SIGNUP_PATH}")
                    .addHeader("apikey", anonKey)
                    .addHeader("Content-Type", "application/json")
                    .post(reqBodyJson.toRequestBody(JSON_MEDIA_TYPE))
                    .build()

                okHttpClient.newCall(request).execute().use { response ->
                    val bodyString = response.body?.string() ?: ""
                    if (response.isSuccessful) {
                        val session = try {
                            sessionAdapter.fromJson(bodyString)
                        } catch (_: Exception) {
                            null
                        }
                        val user = try {
                            userAdapter.fromJson(bodyString) ?: session?.user
                        } catch (_: Exception) {
                            session?.user
                        }

                        if (session != null) {
                            SupabaseAuthResult.Success(session, user)
                        } else if (user != null) {
                            SupabaseAuthResult.ConfirmationRequired(user)

                        } else {
                            SupabaseAuthResult.Error("Failed to parse sign up response from Supabase.")
                        }
                    } else {
                        val errorMsg = extractErrorMessage(bodyString, response.code)
                        SupabaseAuthResult.Error(errorMsg, response.code)
                    }
                }
            } catch (e: IOException) {
                SupabaseAuthResult.Error("Network failure connecting to Supabase: ${e.message}", throwable = e)
            } catch (e: Exception) {
                SupabaseAuthResult.Error("Unexpected auth failure: ${e.message}", throwable = e)
            }
        }

    /**
     * Supabase User Authentication (GoTrue Sign-In with Password)
     */
    suspend fun signInWithPassword(email: String, pass: String): SupabaseAuthResult =
        withContext(Dispatchers.IO) {
            try {
                val reqBodyJson = authReqAdapter.toJson(SupabaseAuthRequest(email, pass))
                val request = Request.Builder()
                    .url("$projectUrl${SupabaseConfig.AUTH_TOKEN_PATH}")
                    .addHeader("apikey", anonKey)
                    .addHeader("Content-Type", "application/json")
                    .post(reqBodyJson.toRequestBody(JSON_MEDIA_TYPE))
                    .build()

                okHttpClient.newCall(request).execute().use { response ->
                    val bodyString = response.body?.string() ?: ""
                    if (response.isSuccessful) {
                        val session = sessionAdapter.fromJson(bodyString)
                        if (session != null) {
                            SupabaseAuthResult.Success(session, session.user)
                        } else {
                            SupabaseAuthResult.Error("Empty session returned from Supabase Auth.")
                        }
                    } else {
                        val errorMsg = extractErrorMessage(bodyString, response.code)
                        SupabaseAuthResult.Error(errorMsg, response.code)
                    }
                }
            } catch (e: IOException) {
                SupabaseAuthResult.Error("Network failure: Unable to reach Supabase Auth server.", throwable = e)
            } catch (e: Exception) {
                SupabaseAuthResult.Error("Authentication error: ${e.message}", throwable = e)
            }
        }

    /**
     * Supabase Secure Sign-Out (Session Revocation)
       */
    suspend fun signOut(accessToken: String): SupabaseAuthResult = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$projectUrl${SupabaseConfig.AUTH_LOGOUT_PATH}")
                .addHeader("apikey", anonKey)
                .addHeader("Authorization", "Bearer $accessToken")
                .post("{}".toRequestBody(JSON_MEDIA_TYPE))
                .build()

            okHttpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful || response.code == 401 || response.code == 403) {
                    SupabaseAuthResult.SignedOut()
                } else {
                    SupabaseAuthResult.Error("Sign-out remote error: code ${response.code}")
                }
            }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            SupabaseAuthResult.Error(
                message = "Remote sign-out could not be confirmed.",
                throwable = error
            )
        }
    }

    /**
     * Retrieve Authenticated User Profile
     */
    suspend fun getUserProfile(accessToken: String): SupabaseUser? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$projectUrl${SupabaseConfig.AUTH_USER_PATH}")
                .addHeader("apikey", anonKey)
                .addHeader("Authorization", "Bearer $accessToken")
                .get()
                .build()

            okHttpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: return@withContext null
                    userAdapter.fromJson(body)
                } else {
                    null
                }
            }
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Session Refresh using Refresh Token
     */
    suspend fun refreshSession(refreshToken: String): SupabaseAuthResult = withContext(Dispatchers.IO) {
        try {
            val reqBody = refreshReqAdapter.toJson(SupabaseRefreshRequest(refreshToken))
            val request = Request.Builder()
                .url("$projectUrl${SupabaseConfig.AUTH_REFRESH_PATH}")
                .addHeader("apikey", anonKey)
                .addHeader("Content-Type", "application/json")
                .post(reqBody.toRequestBody(JSON_MEDIA_TYPE))
                .build()

            okHttpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    val session = sessionAdapter.fromJson(bodyString)
                    if (session != null) {
                        SupabaseAuthResult.Success(session, session.user)
                    } else {
                        SupabaseAuthResult.Error("Failed to parse refreshed token.")
                    }
                } else {
                    SupabaseAuthResult.Error("Refresh failed: ${response.code}")
                }
            }
        } catch (e: Exception) {
            SupabaseAuthResult.Error("Refresh token network exception", throwable = e)
        }
    }

    /**
     * PostgREST: Insert record into specified table
     */
    suspend fun postRestRecord(path: String, jsonPayload: String, accessToken: String? = null): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val builder = Request.Builder()
                    .url("$projectUrl$path")
                    .addHeader("apikey", anonKey)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Prefer", "return=minimal")

                if (!accessToken.isNullOrBlank()) {
                    builder.addHeader("Authorization", "Bearer $accessToken")
                }

                val request = builder
                    .post(jsonPayload.toRequestBody(JSON_MEDIA_TYPE))
                    .build()

                okHttpClient.newCall(request).execute().use { response ->
                    response.isSuccessful || response.code in 200..299
                }
            } catch (_: Exception) {
                false
            }
        }

    private fun extractErrorMessage(body: String, code: Int): String {
        return try {
            if (body.contains("\"msg\":")) {
                val regex = "\"msg\"\\s*:\\s*\"([^\"]+)\"".toRegex()
                regex.find(body)?.groupValues?.get(1) ?: "Error $code"
            } else if (body.contains("\"error_description\":")) {
                val regex = "\"error_description\"\\s*:\\s*\"([^\"]+)\"".toRegex()
                regex.find(body)?.groupValues?.get(1) ?: "Error $code"
            } else if (body.contains("\"message\":")) {
                val regex = "\"message\"\\s*:\\s*\"([^\"]+)\"".toRegex()
                regex.find(body)?.groupValues?.get(1) ?: "Error $code"
            } else {
                "Supabase error ($code)"
            }
        } catch (_: Exception) {
            "Supabase request failed with code $code"
        }
    }
}
