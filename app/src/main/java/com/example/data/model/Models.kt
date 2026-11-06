package com.example.data.model

import androidx.annotation.DrawableRes

enum class Language {
    FA, EN
}

enum class UserRole {
    PUBLIC, CLINIC_ADMIN, SUPER_ADMIN
}

enum class CategoryId {
    ALL, AESTHETICS, SALONS, DENTAL
}

enum class SlotStatus {
    AVAILABLE, RESERVED, IN_PROCEDURE, SANITIZING
}

enum class SlotType {
    CHAIR, BED, LASER_SUITE, OPERATORY
}

enum class BookingRequestStatus {
    PENDING_CONFIRMATION, CONFIRMED, REJECTED
}

enum class EscrowStatus {
    HELD_IN_ESCROW, PATIENT_ARRIVED, PROCEDURE_DONE, RELEASED
}

enum class ApplicationStatus {
    PENDING, APPROVED, REJECTED
}

enum class Severity {
    INFO, WARNING, CRITICAL
}

enum class ToastType {
    SUCCESS, WARNING, INFO, ERROR
}

data class District(
    val id: String,
    val cityId: String, // "tehran" | "karaj"
    val nameEn: String,
    val nameFa: String,
    val popularRoad: String,
    val activeClinicsCount: Int,
    val availableSlotsNow: Int,
    val averageHourlyToman: Long
)

data class Venue(
    val id: String,
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
    @param:DrawableRes val imageResId: Int,
    val taglineEn: String,
    val taglineFa: String,
    val descriptionEn: String,
    val descriptionFa: String,
    val totalChairs: Int,
    val activeSlots: Int,
    val baseHourlyToman: Long,
    val certifications: List<String>,
    val equipmentList: List<String>,
    val amenities: List<String>,
    val medicalDirectorEn: String,
    val medicalDirectorFa: String,
    val medicalLicenseNumber: String
)

data class RealtimeSlot(
    val id: String,
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
    val equipment: List<String>,
    val assistantIncluded: Boolean = true,
    val autoclaveReady: Boolean = true,
    val lastSanitizedMinutesAgo: Int = 10
)

data class ClinicalMetric(
    val label: String,
    val value: String
)

data class BeforeAfterCase(
    val id: String,
    val titleEn: String,
    val titleFa: String,
    val category: CategoryId,
    val clinicNameEn: String,
    val clinicNameFa: String,
    val doctorNameEn: String,
    val doctorNameFa: String,
    val treatmentEn: String,
    val treatmentFa: String,
    @param:DrawableRes val imageResId: Int,
    val metricsEn: List<ClinicalMetric>,
    val metricsFa: List<ClinicalMetric>,
    val notesEn: String,
    val notesFa: String
)

data class StorySlide(
    val id: String,
    val titleFa: String,
    val titleEn: String,
    val doctorNameFa: String,
    val doctorNameEn: String,
    val clinicNameFa: String,
    val clinicNameEn: String,
    @param:DrawableRes val imageResId: Int,
    val treatmentDetailsFa: String,
    val treatmentDetailsEn: String,
    val clinicalTagFa: String,
    val clinicalTagEn: String,
    val durationSeconds: Int = 6
)

data class StoryHighlight(
    val id: String,
    val titleFa: String,
    val titleEn: String,
    @param:DrawableRes val coverResId: Int,
    val slides: List<StorySlide>
)

data class BookingRequest(
    val id: String,
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
    val createdAt: String,
    val updatedAt: String? = null
)

data class EscrowTransaction(
    val id: String,
    val contractCode: String,
    val practitionerName: String,
    val licenseId: String,
    val venueName: String,
    val slotName: String,
    val grossAmountToman: Long,
    val platformFeeToman: Long,
    val clinicPayoutToman: Long,
    val status: EscrowStatus,
    val milestoneStep: Int, // 1: Held, 2: Arrived, 3: Procedure Done, 4: Released
    val createdAt: String,
    val estimatedReleaseTime: String,
    val paymentRefId: String, // Shaparak RRN
    val trackingNumber: String,
    val patientCheckInTimestamp: String? = null,
    val patientNationalIdVerified: Boolean = false,
    val procedureSignOffNotes: String? = null,
    val doctorSignOffTimestamp: String? = null,
    val guaranteeMinutesRemaining: Int = 120,
    val shebaNumber: String = "IR820120000000001829304192",
    val patientNationalId: String = "0074829103"
)

data class OnboardingApplication(
    val id: String,
    val applicantType: String, // "clinic" | "doctor" | "salon"
    val activityTypeFa: String,
    val businessName: String,
    val applicantName: String,
    val phoneNumber: String,
    val cityAndDistrict: String,
    val chairsOrBedsCount: Int,
    val shortDescription: String,
    val medicalCouncilOrGuildId: String = "PENDING-VERIFY",
    val shebaNumber: String = "IR000000000000000000000000",
    val status: ApplicationStatus = ApplicationStatus.PENDING,
    val submittedAt: String
)

data class TicketMessage(
    val id: String,
    val senderName: String,
    val isStaff: Boolean,
    val content: String,
    val timestamp: String
)

data class SupportTicket(
    val id: String,
    val ticketNumber: String,
    val senderName: String,
    val venueName: String,
    val subject: String,
    val category: String, // "escrow" | "technical" | "slot"
    val priority: String, // "urgent" | "high" | "normal"
    val status: String,   // "open" | "answered" | "resolved"
    val createdAt: String,
    val messages: List<TicketMessage>
)

data class SecurityAuditLog(
    val id: String,
    val timestamp: String,
    val type: String,
    val severity: Severity,
    val titleFa: String,
    val titleEn: String,
    val details: String,
    val ip: String
)

data class ToastAlert(
    val id: String,
    val type: ToastType,
    val title: String,
    val message: String
)
