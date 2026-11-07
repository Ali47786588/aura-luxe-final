package com.example.data.remote.supabase

import com.example.BuildConfig

/**
 * Supabase Project Configuration for AURA LUXE.
 * Only the public Supabase Publishable key is allowed.
 * Never include service_role or secret keys in the Android app.
 */
object SupabaseConfig {

    const val PROJECT_URL = "https://hahirgzdukupgzyxrtvu.supabase.co"

    val ANON_KEY: String
        get() {
            val key = try {
                val field = BuildConfig::class.java.getField("SUPABASE_ANON_KEY")
                field.get(null) as? String
            } catch (e: Exception) {
                throw IllegalStateException(
                    "Supabase Publishable key is missing from BuildConfig.",
                    e
                )
            }

            if (key.isNullOrBlank() || !key.startsWith("sb_publishable_")) {
                throw IllegalStateException(
                    "Supabase Publishable key is missing or invalid."
                )
            }

            return key
        }

    const val AUTH_SIGNUP_PATH = "/auth/v1/signup"
    const val AUTH_TOKEN_PATH = "/auth/v1/token?grant_type=password"
    const val AUTH_LOGOUT_PATH = "/auth/v1/logout"
    const val AUTH_USER_PATH = "/auth/v1/user"
    const val AUTH_REFRESH_PATH = "/auth/v1/token?grant_type=refresh_token"

    const val REST_APPOINTMENTS_PATH = "/rest/v1/appointments"
    const val REST_CLINICS_PATH = "/rest/v1/clinics"
    const val REST_ONBOARDING_PATH = "/rest/v1/onboarding_applications"
    const val REST_AUDIT_LOGS_PATH = "/rest/v1/audit_logs"

    const val TIMEOUT_SECONDS = 15L
}
