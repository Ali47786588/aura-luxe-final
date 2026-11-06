package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.*
import com.example.data.model.BookingRequestStatus
import com.example.data.model.EscrowStatus
import com.example.data.model.SlotStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments ORDER BY createdAt DESC")
    fun getAllAppointments(): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE id = :id")
    suspend fun getAppointmentById(id: String): AppointmentEntity?

    @Query("SELECT * FROM appointments WHERE trackingCode = :trackingCode LIMIT 1")
    suspend fun getAppointmentByTrackingCode(trackingCode: String): AppointmentEntity?

    @Query("""
        SELECT * FROM appointments 
        WHERE venueId = :venueId 
          AND slotId = :slotId 
          AND suggestedDate = :date 
          AND suggestedTime = :time 
          AND status != 'REJECTED'
        LIMIT 1
    """)
    suspend fun findConflictingAppointment(
        venueId: String,
        slotId: String,
        date: String,
        time: String
    ): AppointmentEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(appointment: AppointmentEntity)

    @Update
    suspend fun update(appointment: AppointmentEntity)

    @Query("UPDATE appointments SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateStatus(id: String, status: BookingRequestStatus, updatedAt: String)

    @Query("DELETE FROM appointments WHERE id = :id")
    suspend fun deleteById(id: String)
}

@Dao
interface ClinicDao {
    @Query("SELECT * FROM clinics ORDER BY rating DESC")
    fun getAllClinics(): Flow<List<ClinicEntity>>

    @Query("SELECT * FROM clinics WHERE id = :id")
    suspend fun getClinicById(id: String): ClinicEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(clinics: List<ClinicEntity>)
}

@Dao
interface ClinicUnitDao {
    @Query("SELECT * FROM clinic_units ORDER BY id ASC")
    fun getAllUnits(): Flow<List<ClinicUnitEntity>>

    @Query("SELECT * FROM clinic_units WHERE venueId = :venueId")
    fun getUnitsForVenue(venueId: String): Flow<List<ClinicUnitEntity>>

    @Query("SELECT * FROM clinic_units WHERE id = :id")
    suspend fun getUnitById(id: String): ClinicUnitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(units: List<ClinicUnitEntity>)

    @Query("UPDATE clinic_units SET status = :status, lastSanitizedMinutesAgo = :sanitizedMin WHERE id = :id")
    suspend fun updateStatus(id: String, status: SlotStatus, sanitizedMin: Int)
}

@Dao
interface EscrowTransactionDao {
    @Query("SELECT * FROM escrow_transactions ORDER BY createdAt DESC")
    fun getAllTransactions(): Flow<List<EscrowTransactionEntity>>

    @Query("SELECT * FROM escrow_transactions WHERE id = :id")
    suspend fun getTransactionById(id: String): EscrowTransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(txs: List<EscrowTransactionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tx: EscrowTransactionEntity)

    @Update
    suspend fun update(tx: EscrowTransactionEntity)

    @Query("""
        UPDATE escrow_transactions 
        SET milestoneStep = 2, status = 'PATIENT_ARRIVED', 
            patientCheckInTimestamp = :timestamp, patientNationalIdVerified = 1, 
            estimatedReleaseTime = :estRelease 
        WHERE id = :id
    """)
    suspend fun markPatientArrived(id: String, timestamp: String, estRelease: String)

    @Query("""
        UPDATE escrow_transactions 
        SET milestoneStep = 3, status = 'PROCEDURE_DONE', 
            procedureSignOffNotes = :notes, doctorSignOffTimestamp = :timestamp, 
            guaranteeMinutesRemaining = 120, estimatedReleaseTime = :estRelease 
        WHERE id = :id
    """)
    suspend fun markProcedureDone(id: String, notes: String, timestamp: String, estRelease: String)

    @Query("UPDATE escrow_transactions SET guaranteeMinutesRemaining = 0 WHERE id = :id")
    suspend fun fastForwardGuarantee(id: String)

    @Query("""
        UPDATE escrow_transactions 
        SET milestoneStep = 4, status = 'RELEASED', 
            guaranteeMinutesRemaining = 0, estimatedReleaseTime = :estRelease 
        WHERE id = :id
    """)
    suspend fun releaseEscrow(id: String, estRelease: String)
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY createdAtMs DESC LIMIT 200")
    fun getRecentLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: AuditLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(logs: List<AuditLogEntity>)
}

@Dao
interface OnboardingApplicationDao {
    @Query("SELECT * FROM onboarding_applications ORDER BY createdAtMs DESC")
    fun getAllApplications(): Flow<List<OnboardingApplicationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(app: OnboardingApplicationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(apps: List<OnboardingApplicationEntity>)

    @Query("UPDATE onboarding_applications SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: com.example.data.model.ApplicationStatus)
}

@Dao
interface SupportTicketDao {
    @Query("SELECT * FROM support_tickets ORDER BY createdAt DESC")
    fun getAllTickets(): Flow<List<SupportTicketEntity>>

    @Query("SELECT * FROM ticket_messages WHERE ticketId = :ticketId ORDER BY createdAtMs ASC")
    fun getMessagesForTicket(ticketId: String): Flow<List<TicketMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTickets(tickets: List<SupportTicketEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: TicketMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<TicketMessageEntity>)
}

@Dao
interface UserSessionDao {
    @Query("SELECT * FROM user_sessions WHERE token = :token LIMIT 1")
    suspend fun getSessionByToken(token: String): UserSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: UserSessionEntity)

    @Query("DELETE FROM user_sessions WHERE token = :token")
    suspend fun deleteSessionByToken(token: String)

    @Query("DELETE FROM user_sessions WHERE lastActivityMs < :expiryThresholdMs")
    suspend fun purgeExpiredSessions(expiryThresholdMs: Long)
}

@Dao
interface SecurityEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: SecurityEventEntity)

    @Query("SELECT * FROM security_events ORDER BY timestampMs DESC LIMIT 100")
    fun getRecentEvents(): Flow<List<SecurityEventEntity>>
}

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE phoneNumber = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserEntity?
}

@Dao
interface DoctorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoctor(doctor: DoctorEntity)

    @Query("SELECT * FROM doctors WHERE clinicId = :clinicId")
    suspend fun getDoctorsByClinic(clinicId: String): List<DoctorEntity>
}

@Dao
interface PatientDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: PatientEntity)

    @Query("SELECT * FROM patients WHERE phoneNumber = :phone LIMIT 1")
    suspend fun getPatientByPhone(phone: String): PatientEntity?
}

@Dao
interface ServiceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(services: List<ServiceEntity>)

    @Query("SELECT * FROM services")
    suspend fun getAllServices(): List<ServiceEntity>
}

@Dao
interface ContractDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContract(contract: ContractEntity)

    @Query("SELECT * FROM contracts WHERE contractCode = :code LIMIT 1")
    suspend fun getContractByCode(code: String): ContractEntity?
}

@Dao
interface PaymentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity)

    @Query("SELECT * FROM payments WHERE escrowId = :escrowId")
    suspend fun getPaymentsForEscrow(escrowId: String): List<PaymentEntity>
}

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>
}
