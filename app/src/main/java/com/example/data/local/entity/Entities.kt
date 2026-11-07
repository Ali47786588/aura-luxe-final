package com.example.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.model.*

/**
 * 1. Users Entity
 */
@Entity(
    tableName = "users",
    indices = [Index(value = ["phoneNumber"], unique = true)]
)
data class UserEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val phoneNumber: String,
    val email: String? = null,
    val role: UserRole = UserRole.PUBLIC,
    val isDemoAccount: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * 2. Clinics Entity
 */
@Entity(tableName = "clinics")
data class ClinicEntity(
    @PrimaryKey val id: String,
    val nameEn: String,
    val nameFa: String,
    val category: CategoryId,
    val cityId: String,
    val districtId: String,
    val districtNameEn: String,
    val districtNameFa: String,
    val addressEn: String,
    val addressFa: String,
    val rating: Double,
    val reviewCount: Int,
    val taglineEn: String,
    val taglineFa: String,
    val descriptionEn: String,
    val descriptionFa: String,
    val totalChairs: Int,
    val activeSlots: Int,
    val baseHourlyToman: Long,
    val certifications: List<String> = emptyList(),
    val equipmentList: List<String> = emptyList(),
    val amenities: List<String> = emptyList(),
    val medicalDirectorEn: String,
    val medicalDirectorFa: String,
    val medicalLicenseNumber: String
)

/**
 * 3. Doctors Entity
 */
@Entity(
    tableName = "doctors",
    indices = [Index(value = ["medicalCouncilCode"], unique = true)]
)
data class DoctorEntity(
    @PrimaryKey val id: String,
    val clinicId: String,
    val fullNameEn: String,
    val fullNameFa: String,
    val specialty: String,
    val medicalCouncilCode: String,
    val isVerified: Boolean = true
)

/**
 * 4. Patients Entity
 */
@Entity(
    tableName = "patients",
    indices = [Index(value = ["phoneNumber"])]
)
data class PatientEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val phoneNumber: String,
    val nationalIdEncrypted: String? = null,
    val nationalIdMasked: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * 5. Appointments Entity
 * Includes unique index on trackingCode for duplicate booking prevention.
 */
@Entity(
    tableName = "appointments",
    indices = [
        Index(value = ["trackingCode"], unique = true),
        Index(value = ["venueId", "slotId", "suggestedDate", "suggestedTime"])
    ]
)
data class AppointmentEntity(
    @PrimaryKey val id: String,
    val trackingCode: String,
    val fullName: String,
    val phoneNumber: String,
    val desiredService: String,
    val suggestedDate: String,
    val suggestedTime: String,
    val venueId: String,
    val venueNameFa: String,
    val venueNameEn: String,
    val slotId: String? = null,
    val slotNameFa: String? = null,
    val slotNameEn: String? = null,
    val notes: String = "",
    val status: BookingRequestStatus = BookingRequestStatus.PENDING_CONFIRMATION,
    @ColumnInfo(defaultValue = "1")
    val isOfflineLocal: Boolean = true,
    @ColumnInfo(defaultValue = "0")
    val isServerSynced: Boolean = false,
    val createdAt: String,
    val updatedAt: String? = null
)

/**
 * 6. Services Entity
 */
@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey val id: String,
    val nameFa: String,
    val nameEn: String,
    val category: CategoryId,
    val basePriceToman: Long,
    val durationMinutes: Int = 60
)

/**
 * 7. Contracts Entity
 */
@Entity(
    tableName = "contracts",
    indices = [Index(value = ["contractCode"], unique = true)]
)
data class ContractEntity(
    @PrimaryKey val id: String,
    val contractCode: String,
    val clinicId: String,
    val doctorName: String,
    val terms: String,
    val isSigned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * 8. EscrowTransactions Entity
 */
@Entity(
    tableName = "escrow_transactions",
    indices = [Index(value = ["contractCode"], unique = true)]
)
data class EscrowTransactionEntity(
    @PrimaryKey val id: String,
    val contractCode: String,
    val practitionerName: String,
    val licenseId: String,
    val venueName: String,
    val slotName: String,
    val grossAmountToman: Long,
    val platformFeeToman: Long,
    val clinicPayoutToman: Long,
    val status: EscrowStatus,
    val milestoneStep: Int,
    val createdAt: String,
    val estimatedReleaseTime: String,
    val paymentRefId: String,
    val trackingNumber: String,
    val patientCheckInTimestamp: String? = null,
    val patientNationalIdVerified: Boolean = false,
    val procedureSignOffNotes: String? = null,
    val doctorSignOffTimestamp: String? = null,
    val guaranteeMinutesRemaining: Int = 120,
    val shebaEncrypted: String? = null,
    val shebaMasked: String = "IR**...4192",
    val patientNationalIdMasked: String = "007***06",
    @ColumnInfo(defaultValue = "1")
    val isDemo: Boolean = true
)

/**
 * 9. Payments Entity
 */
@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey val id: String,
    val escrowId: String,
    val amountToman: Long,
    val gatewayName: String = "SADAD_SHAPARAK_SIMULATOR",
    val trackingRrn: String,
    val status: String = "HELD",
    val isDemo: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * 10. Notifications Entity
 */
@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val type: String = "INFO",
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * 11. SupportTickets Entity
 */
@Entity(
    tableName = "support_tickets",
    indices = [Index(value = ["ticketNumber"], unique = true)]
)
data class SupportTicketEntity(
    @PrimaryKey val id: String,
    val ticketNumber: String,
    val senderName: String,
    val venueName: String,
    val subject: String,
    val category: String,
    val priority: String,
    val status: String,
    val createdAt: String
)

/**
 * 12. TicketMessages Entity
 */
@Entity(
    tableName = "ticket_messages",
    indices = [Index(value = ["ticketId"])]
)
data class TicketMessageEntity(
    @PrimaryKey val id: String,
    val ticketId: String,
    val senderName: String,
    val isStaff: Boolean,
    val content: String,
    val timestamp: String,
    val createdAtMs: Long = System.currentTimeMillis()
)

/**
 * 13. SecurityEvents Entity
 */
@Entity(tableName = "security_events")
data class SecurityEventEntity(
    @PrimaryKey val id: String,
    val eventType: String,
    val severity: Severity,
    val details: String,
    val clientIp: String = "127.0.0.1",
    val timestampMs: Long = System.currentTimeMillis()
)

/**
 * 14. AuditLogs Entity
 */
@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey val id: String,
    val timestamp: String,
    val type: String,
    val severity: Severity,
    val titleFa: String,
    val titleEn: String,
    val details: String,
    val ip: String,
    val createdAtMs: Long = System.currentTimeMillis()
)

/**
 * 15. UserSessions Entity
 */
@Entity(
    tableName = "user_sessions",
    indices = [Index(value = ["token"], unique = true)]
)
data class UserSessionEntity(
    @PrimaryKey val token: String,
    val userId: String = "admin_user",
    val role: UserRole,
    val createdAtMs: Long,
    val lastActivityMs: Long,
    val isDemoSession: Boolean = true,
    @androidx.room.ColumnInfo(defaultValue = "''")
    val encryptedRefreshToken: String = "",
    @androidx.room.ColumnInfo(defaultValue = "0")
    val expiresAtMs: Long = 0L,
    val email: String? = null
)

/**
 * 16. ClinicUnits (RealtimeSlot) Entity
 */
@Entity(tableName = "clinic_units")
data class ClinicUnitEntity(
    @PrimaryKey val id: String,
    val venueId: String,
    val venueNameEn: String,
    val venueNameFa: String,
    val roomNameEn: String,
    val roomNameFa: String,
    val type: SlotType,
    val status: SlotStatus,
    val currentPractitioner: String? = null,
    val patientProcedure: String? = null,
    val pricePerHourToman: Long,
    val equipment: List<String> = emptyList(),
    val assistantIncluded: Boolean = true,
    val autoclaveReady: Boolean = true,
    val lastSanitizedMinutesAgo: Int = 10
)

/**
 * 17. OnboardingApplication Entity
 */
@Entity(tableName = "onboarding_applications")
data class OnboardingApplicationEntity(
    @PrimaryKey val id: String,
    val applicantType: String,
    val activityTypeFa: String,
    val businessName: String,
    val applicantName: String,
    val phoneNumber: String,
    val cityAndDistrict: String,
    val chairsOrBedsCount: Int,
    val shortDescription: String,
    val medicalCouncilOrGuildId: String = "PENDING-VERIFY",
    val shebaEncrypted: String? = null,
    val status: ApplicationStatus = ApplicationStatus.PENDING,
    val submittedAt: String,
    val createdAtMs: Long = System.currentTimeMillis()
)
