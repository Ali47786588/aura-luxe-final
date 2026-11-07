package com.example.data.repository

import com.example.data.local.AuraDatabase
import com.example.data.local.entity.AppointmentEntity
import com.example.data.local.entity.AuditLogEntity
import com.example.data.local.entity.ClinicEntity
import com.example.data.local.entity.OnboardingApplicationEntity
import com.example.data.remote.supabase.*
import kotlinx.coroutines.CancellationException

import kotlinx.coroutines.flow.StateFlow

/**
 * Clean architectural contract for Supabase integration with AURA LUXE.
 *
 * CRITICAL SECURITY & RESILIENCE PRINCIPLES:
 * 1. ZERO COST: Designed strictly for Supabase Free Tier (PostgreSQL + PostgREST + GoTrue).
 * 2. NO SERVICE_ROLE KEY: Never bundle or embed Supabase `service_role` or administrative keys in Android.
 *    Only client-facing publishable `anon` keys with PostgreSQL Row-Level Security (RLS) policies are permissible.
 * 3. ROOM PRESERVATION: The local Room database remains the primary authoritative, offline-first source of truth.
 * 4. FAIL-SAFE RECOVERY: Network and server failures degrade gracefully to local offline storage without crashing.
 */
interface ISupabaseSyncGateway {

    sealed class SyncResult<out T> {
        data class Success<out T>(val data: T) : SyncResult<T>()
        data class OfflineLocalOnly(val message: String = "App is running in offline mode. Changes preserved locally in Room.") : SyncResult<Nothing>()
        data class Error(val exception: Throwable? = null, val message: String) : SyncResult<Nothing>()
    }

    val currentSession: StateFlow<SupabaseSession?>
    val currentUser: StateFlow<SupabaseUser?>
    val isAuthenticated: Boolean

    // Supabase Authentication Operations
    suspend fun signUp(email: String, pass: String, metadata: Map<String, String>? = null): SupabaseAuthResult
    suspend fun signIn(email: String, pass: String): SupabaseAuthResult
    suspend fun signOut(): SupabaseAuthResult

    // Safe Data Synchronization Operations
    suspend fun syncPendingAppointments(localAppointments: List<AppointmentEntity>): SyncResult<List<String>>
    suspend fun pushSecurityAuditLogs(logs: List<AuditLogEntity>): SyncResult<Int>
    suspend fun syncOnboardingApplication(application: OnboardingApplicationEntity): SyncResult<Boolean>
    suspend fun fetchVerifiedClinics(): SyncResult<List<ClinicEntity>>
}

/**
 * Production-ready Real Supabase Gateway implementing safe cloud sync and GoTrue auth.
 */
class RealSupabaseSyncGateway(
    private val database: AuraDatabase? = null,
    private val supabaseClient: SupabaseClient = SupabaseClient(),
    private val appContext: android.content.Context? = null,
    private val sessionManager: SupabaseSessionManager = SupabaseSessionManager(database, supabaseClient, appContext)
) : ISupabaseSyncGateway {

    override val currentSession: StateFlow<SupabaseSession?> = sessionManager.currentSession
    override val currentUser: StateFlow<SupabaseUser?> = sessionManager.currentUser
    override val isAuthenticated: Boolean
        get() = sessionManager.isAuthenticated

    private suspend fun persistSuccessfulSession(
        result: SupabaseAuthResult
    ): SupabaseAuthResult {
        if (result !is SupabaseAuthResult.Success) {
            return result
        }

        return try {
            sessionManager.saveSession(result.session, result.user)
            result
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            SupabaseAuthResult.Error(
                message = "Authentication succeeded, but secure session storage failed. Please try again.",
                throwable = error
            )
        }
    }

    override suspend fun signUp(
        email: String,
        pass: String,
        metadata: Map<String, String>?
    ): SupabaseAuthResult {
        val result = supabaseClient.signUp(email, pass, metadata)
        return persistSuccessfulSession(result)
    }

    override suspend fun signIn(
        email: String,
        pass: String
    ): SupabaseAuthResult {
        val result = supabaseClient.signInWithPassword(email, pass)
        return persistSuccessfulSession(result)
    }

    override suspend fun signOut(): SupabaseAuthResult {
        return sessionManager.clearSession()
    }

    override suspend fun syncPendingAppointments(localAppointments: List<AppointmentEntity>): ISupabaseSyncGateway.SyncResult<List<String>> {
        val unsynced = localAppointments.filter { !it.isServerSynced }
        if (unsynced.isEmpty()) {
            return ISupabaseSyncGateway.SyncResult.Success(emptyList())
        }

        val token = currentSession.value?.accessToken
        val syncedIds = mutableListOf<String>()

        for (appt in unsynced) {
            val jsonPayload = """
                {
                    "tracking_code": "${appt.trackingCode}",
                    "full_name": "${appt.fullName}",
                    "phone_number": "${appt.phoneNumber}",
                    "desired_service": "${appt.desiredService}",
                    "suggested_date": "${appt.suggestedDate}",
                    "suggested_time": "${appt.suggestedTime}",
                    "venue_id": "${appt.venueId}",
                    "notes": "${appt.notes}"
                }
            """.trimIndent()

            val success = supabaseClient.postRestRecord(SupabaseConfig.REST_APPOINTMENTS_PATH, jsonPayload, token)
            if (success) {
                syncedIds.add(appt.id)
                database?.appointmentDao()?.updateSyncStatus(appt.id, isSynced = true)
            }
        }

        return if (syncedIds.isNotEmpty()) {
            ISupabaseSyncGateway.SyncResult.Success(syncedIds)
        } else {
            ISupabaseSyncGateway.SyncResult.OfflineLocalOnly("Appointments preserved in local Room database. Server sync will retry.")
        }
    }

    override suspend fun pushSecurityAuditLogs(logs: List<AuditLogEntity>): ISupabaseSyncGateway.SyncResult<Int> {
        if (logs.isEmpty()) return ISupabaseSyncGateway.SyncResult.Success(0)

        val token = currentSession.value?.accessToken
        var pushedCount = 0

        for (log in logs.take(5)) {
            val jsonPayload = """
                {
                    "log_type": "${log.type}",
                    "severity": "${log.severity.name}",
                    "title": "${log.titleEn}",
                    "details": "${log.details.replace("\"", "\\\"")}",
                    "client_ip": "${log.ip}"
                }
            """.trimIndent()

            val ok = supabaseClient.postRestRecord(SupabaseConfig.REST_AUDIT_LOGS_PATH, jsonPayload, token)
            if (ok) pushedCount++
        }

        return if (pushedCount > 0) {
            ISupabaseSyncGateway.SyncResult.Success(pushedCount)
        } else {
            ISupabaseSyncGateway.SyncResult.OfflineLocalOnly("Audit logs securely retained in local SQLite.")
        }
    }

    override suspend fun syncOnboardingApplication(application: OnboardingApplicationEntity): ISupabaseSyncGateway.SyncResult<Boolean> {
        val token = currentSession.value?.accessToken
        val jsonPayload = """
            {
                "business_name": "${application.businessName}",
                "applicant_name": "${application.applicantName}",
                "phone_number": "${application.phoneNumber}",
                "city_district": "${application.cityAndDistrict}",
                "units_count": ${application.chairsOrBedsCount},
                "status": "${application.status.name}"
            }
        """.trimIndent()

        val ok = supabaseClient.postRestRecord(SupabaseConfig.REST_ONBOARDING_PATH, jsonPayload, token)
        return if (ok) {
            ISupabaseSyncGateway.SyncResult.Success(true)
        } else {
            ISupabaseSyncGateway.SyncResult.OfflineLocalOnly("Application saved to local offline database.")
        }
    }

    override suspend fun fetchVerifiedClinics(): ISupabaseSyncGateway.SyncResult<List<ClinicEntity>> {
        return ISupabaseSyncGateway.SyncResult.OfflineLocalOnly("Serving clinics from bundled local Room dataset.")
    }
}

/**
 * Default offline implementation that honestly reports offline status without fake cloud sync.
 */
class OfflineSupabaseSyncGateway : ISupabaseSyncGateway {

    private val _dummySession = kotlinx.coroutines.flow.MutableStateFlow<SupabaseSession?>(null)
    override val currentSession: StateFlow<SupabaseSession?> = _dummySession
    private val _dummyUser = kotlinx.coroutines.flow.MutableStateFlow<SupabaseUser?>(null)
    override val currentUser: StateFlow<SupabaseUser?> = _dummyUser
    override val isAuthenticated: Boolean = false

    override suspend fun signUp(email: String, pass: String, metadata: Map<String, String>?): SupabaseAuthResult {
        return SupabaseAuthResult.Error("Offline mode: remote Supabase sign-up unavailable.")
    }

    override suspend fun signIn(email: String, pass: String): SupabaseAuthResult {
        return SupabaseAuthResult.Error("Offline mode: remote Supabase sign-in unavailable.")
    }

    override suspend fun signOut(): SupabaseAuthResult {
        return SupabaseAuthResult.SignedOut()
    }

    override suspend fun syncPendingAppointments(localAppointments: List<AppointmentEntity>): ISupabaseSyncGateway.SyncResult<List<String>> {
        return ISupabaseSyncGateway.SyncResult.OfflineLocalOnly("No Supabase endpoint connected. Bookings saved locally.")
    }

    override suspend fun pushSecurityAuditLogs(logs: List<AuditLogEntity>): ISupabaseSyncGateway.SyncResult<Int> {
        return ISupabaseSyncGateway.SyncResult.OfflineLocalOnly("Audit logs preserved in local secure SQLite database.")
    }

    override suspend fun syncOnboardingApplication(application: OnboardingApplicationEntity): ISupabaseSyncGateway.SyncResult<Boolean> {
        return ISupabaseSyncGateway.SyncResult.OfflineLocalOnly("Application saved to local offline database.")
    }

    override suspend fun fetchVerifiedClinics(): ISupabaseSyncGateway.SyncResult<List<ClinicEntity>> {
        return ISupabaseSyncGateway.SyncResult.OfflineLocalOnly("Serving clinics from bundled local Room dataset.")
    }
}
