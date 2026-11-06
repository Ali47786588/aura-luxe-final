package com.example.data.repository

import com.example.data.local.entity.AppointmentEntity
import com.example.data.local.entity.EscrowTransactionEntity
import com.example.data.local.entity.OnboardingApplicationEntity

/**
 * Clean architectural contract for future integration with Supabase Free Tier.
 *
 * CRITICAL SECURITY & BUDGET PRINCIPLES:
 * 1. ZERO COST: Designed strictly for Supabase Free Tier (PostgreSQL + PostgREST + GoTrue).
 * 2. NO SERVICE_ROLE KEY: Never bundle or embed Supabase `service_role` / secret keys in the Android APK.
 *    Only client-facing publishable `anon` keys with PostgreSQL Row-Level Security (RLS) policies
 *    are permissible.
 * 3. NO FAKE CLOUD SYNC: This interface defines the contract for real network synchronization.
 *    Until remote endpoints are actively provisioned and configured, the local Room database remains
 *    the authoritative, offline-first source of truth.
 */
interface ISupabaseSyncGateway {

    sealed class SyncResult<out T> {
        data class Success<out T>(val data: T) : SyncResult<T>()
        data class OfflineLocalOnly(val message: String = "App is running in offline mode. Changes preserved locally in Room.") : SyncResult<Nothing>()
        data class Error(val exception: Throwable, val message: String) : SyncResult<Nothing>()
    }

    /**
     * Future synchronization for client booking requests to Supabase `appointments` table.
     */
    suspend fun syncPendingAppointments(localAppointments: List<AppointmentEntity>): SyncResult<List<String>>

    /**
     * Future synchronization for audit records to Supabase `audit_logs` table.
     */
    suspend fun pushSecurityAuditLogs(logs: List<com.example.data.local.entity.AuditLogEntity>): SyncResult<Int>

    /**
     * Future synchronization for partner registration to Supabase `onboarding_applications`.
     */
    suspend fun syncOnboardingApplication(application: OnboardingApplicationEntity): SyncResult<Boolean>

    /**
     * Read-only remote query for verified clinic directory.
     */
    suspend fun fetchVerifiedClinics(): SyncResult<List<com.example.data.local.entity.ClinicEntity>>
}

/**
 * Default offline implementation that honestly reports offline status without fake cloud sync.
 */
class OfflineSupabaseSyncGateway : ISupabaseSyncGateway {

    override suspend fun syncPendingAppointments(localAppointments: List<AppointmentEntity>): ISupabaseSyncGateway.SyncResult<List<String>> {
        // Honest offline reporting: no fake cloud sync
        return ISupabaseSyncGateway.SyncResult.OfflineLocalOnly("No Supabase endpoint connected. Bookings saved locally.")
    }

    override suspend fun pushSecurityAuditLogs(logs: List<com.example.data.local.entity.AuditLogEntity>): ISupabaseSyncGateway.SyncResult<Int> {
        return ISupabaseSyncGateway.SyncResult.OfflineLocalOnly("Audit logs preserved in local secure SQLite database.")
    }

    override suspend fun syncOnboardingApplication(application: OnboardingApplicationEntity): ISupabaseSyncGateway.SyncResult<Boolean> {
        return ISupabaseSyncGateway.SyncResult.OfflineLocalOnly("Application saved to local offline database.")
    }

    override suspend fun fetchVerifiedClinics(): ISupabaseSyncGateway.SyncResult<List<com.example.data.local.entity.ClinicEntity>> {
        return ISupabaseSyncGateway.SyncResult.OfflineLocalOnly("Serving clinics from bundled local Room dataset.")
    }
}
