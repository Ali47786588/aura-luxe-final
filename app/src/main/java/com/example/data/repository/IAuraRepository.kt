package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface contract for Aura Luxe Data Layer.
 * Allows switching between local Room storage, mock fixtures, and future cloud gateways
 * without altering UI or ViewModels.
 */
interface IAuraRepository {
    val districts: List<District>
    val venues: List<Venue>
    val beforeAfterCases: List<BeforeAfterCase>
    val storyHighlights: List<StoryHighlight>

    val slots: StateFlow<List<RealtimeSlot>>
    val bookingRequests: StateFlow<List<BookingRequest>>
    val escrowTransactions: StateFlow<List<EscrowTransaction>>
    val applications: StateFlow<List<OnboardingApplication>>
    val tickets: StateFlow<List<SupportTicket>>
    val auditLogs: StateFlow<List<SecurityAuditLog>>

    fun toggleSlotStatus(slotId: String, newStatus: SlotStatus)

    fun submitBookingRequest(
        fullName: String,
        phoneNumber: String,
        desiredService: String,
        suggestedDate: String,
        suggestedTime: String,
        venueId: String,
        venueNameFa: String,
        venueNameEn: String,
        slotId: String?,
        slotNameFa: String?,
        slotNameEn: String?,
        notes: String
    ): BookingRequest

    fun updateBookingRequestStatus(requestId: String, newStatus: BookingRequestStatus)

    fun confirmPatientArrival(escrowId: String, receptionistId: String)

    fun signOffProcedure(escrowId: String, notes: String, doctorSignatureSvg: String)

    fun fastForwardGuarantee(escrowId: String)

    fun releaseEscrowToSheba(escrowId: String)

    fun submitOnboardingApplication(app: OnboardingApplication)

    fun updateApplicationStatus(appId: String, newStatus: ApplicationStatus)

    fun replyToTicket(ticketId: String, content: String)

    fun addAuditLog(type: String, severity: Severity, titleFa: String, titleEn: String, details: String)
}
