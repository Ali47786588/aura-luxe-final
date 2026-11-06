package com.example

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AuraDatabase
import com.example.data.local.entity.AppointmentEntity
import com.example.data.local.security.KeystoreEncryptionManager
import com.example.data.model.*
import com.example.data.repository.AuraRepository
import com.example.data.repository.ISupabaseSyncGateway
import com.example.data.repository.OfflineSupabaseSyncGateway
import com.example.security.SecurityManager
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.security.SecureRandom
import javax.crypto.spec.SecretKeySpec

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class Phase2DatabaseAndSecurityTest {

    private lateinit var context: Context
    private lateinit var database: AuraDatabase
    private lateinit var repository: AuraRepository

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        database = AuraDatabase.createInMemoryDatabase(context)
        repository = AuraRepository(database)
    }

    @After
    fun tearDown() {
        database.close()
    }

    // 1. Room Database Insertion
    @Test
    fun testRoomDatabaseInsertion() {
        runBlocking {
            val appointment = AppointmentEntity(
                id = "test-apt-1",
                trackingCode = "REQ-TEST-0001",
                fullName = "علی محمدی",
                phoneNumber = "09121112233",
                desiredService = "لمینت سرامیکی",
                suggestedDate = "2026-11-10",
                suggestedTime = "10:00 - 12:00",
                venueId = "v_smile_studio",
                venueNameFa = "آتلیه دندانپزشکی سرامیکا",
                venueNameEn = "Ceramica Dental Atelier",
                slotId = "slot_4",
                slotNameFa = "سوئیت لمینت و جراحی ۱",
                slotNameEn = "Operatory Alpha",
                notes = "درخواست نوبت اول",
                status = BookingRequestStatus.PENDING_CONFIRMATION,
                isOfflineLocal = true,
                isServerSynced = false,
                createdAt = "10:00"
            )
            database.appointmentDao().insert(appointment)

            val retrieved = database.appointmentDao().getAppointmentById("test-apt-1")
            assertNotNull("Inserted appointment should not be null", retrieved)
        }
    }

    // 2. Room Database Retrieval
    @Test
    fun testRoomDatabaseRetrieval() {
        runBlocking {
            val trackingCode = "REQ-RETRIEVE-99"
            val appointment = AppointmentEntity(
                id = "test-apt-2",
                trackingCode = trackingCode,
                fullName = "سارا کریمی",
                phoneNumber = "09351234567",
                desiredService = "فیلر لب",
                suggestedDate = "2026-11-12",
                suggestedTime = "14:00 - 16:00",
                venueId = "v_saadat_derma",
                venueNameFa = "پنت‌هاوس لورا",
                venueNameEn = "L Aura Penthouse",
                slotId = "slot_1",
                slotNameFa = "سوئیت لیزر ۱",
                slotNameEn = "Laser Suite 1",
                notes = "پوست حساس",
                status = BookingRequestStatus.CONFIRMED,
                isOfflineLocal = true,
                isServerSynced = false,
                createdAt = "11:00"
            )
            database.appointmentDao().insert(appointment)

            val retrievedByCode = database.appointmentDao().getAppointmentByTrackingCode(trackingCode)
            assertNotNull(retrievedByCode)
            assertEquals("سارا کریمی", retrievedByCode?.fullName)
            assertEquals("09351234567", retrievedByCode?.phoneNumber)
            assertEquals(BookingRequestStatus.CONFIRMED, retrievedByCode?.status)
        }
    }

    // 3. Database Persistence After Application Restart
    @Test
    fun testDatabasePersistenceAfterRestart() {
        runBlocking {
            val dbFile = File(context.filesDir, "test_persistence.db")
            if (dbFile.exists()) dbFile.delete()

            // 1. Open disk database instance and insert record
            val diskDb1 = Room.databaseBuilder(context, AuraDatabase::class.java, dbFile.absolutePath)
                .allowMainThreadQueries()
                .build()

            val appointment = AppointmentEntity(
                id = "persisted-apt-1",
                trackingCode = "REQ-PERSIST-100",
                fullName = "کامران شایگان",
                phoneNumber = "09129998877",
                desiredService = "ایمپلنت دیجیتال",
                suggestedDate = "2026-11-15",
                suggestedTime = "16:00 - 18:00",
                venueId = "v_smile_studio",
                venueNameFa = "سرامیکا",
                venueNameEn = "Ceramica",
                slotId = "slot_4",
                slotNameFa = "سوئیت ۴",
                slotNameEn = "Suite 4",
                notes = "",
                status = BookingRequestStatus.PENDING_CONFIRMATION,
                isOfflineLocal = true,
                isServerSynced = false,
                createdAt = "12:00"
            )
            diskDb1.appointmentDao().insert(appointment)

            // 2. Simulate Application Termination / Restart: Close the database
            diskDb1.close()

            // 3. Re-open database from same SQLite file (simulating app relaunch)
            val diskDb2 = Room.databaseBuilder(context, AuraDatabase::class.java, dbFile.absolutePath)
                .allowMainThreadQueries()
                .build()

            val reloaded = diskDb2.appointmentDao().getAppointmentById("persisted-apt-1")
            assertNotNull("Record must survive process restart", reloaded)
            assertEquals("کامران شایگان", reloaded?.fullName)
            assertEquals("REQ-PERSIST-100", reloaded?.trackingCode)

            diskDb2.close()
            dbFile.delete()
        }
    }

    // 4. Database Migration
    @Test
    fun testDatabaseMigration() {
        // Test MIGRATION_1_2 SQL execution
        val helper = FrameworkSQLiteOpenHelperFactory().create(
            androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(null)
                .callback(object : androidx.sqlite.db.SupportSQLiteOpenHelper.Callback(1) {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        db.execSQL("""
                            CREATE TABLE appointments (
                                id TEXT NOT NULL PRIMARY KEY,
                                trackingCode TEXT NOT NULL,
                                fullName TEXT NOT NULL,
                                phoneNumber TEXT NOT NULL,
                                desiredService TEXT NOT NULL,
                                suggestedDate TEXT NOT NULL,
                                suggestedTime TEXT NOT NULL,
                                venueId TEXT NOT NULL,
                                venueNameFa TEXT NOT NULL,
                                venueNameEn TEXT NOT NULL,
                                slotId TEXT,
                                slotNameFa TEXT,
                                slotNameEn TEXT,
                                notes TEXT NOT NULL,
                                status TEXT NOT NULL,
                                isOfflineLocal INTEGER NOT NULL DEFAULT 1,
                                createdAt TEXT NOT NULL,
                                updatedAt TEXT
                            )
                        """)
                    }
                    override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
                })
                .build()
        )
        val db = helper.writableDatabase

        // Execute migration 1 -> 2
        AuraDatabase.MIGRATION_1_2.migrate(db)

        // Verify that isServerSynced column was successfully added
        val cursor = db.query("SELECT isServerSynced FROM appointments")
        assertNotNull("Column isServerSynced should exist after migration", cursor)
        cursor.close()
        db.close()
    }

    // 5. AES-256-GCM Encryption and Decryption
    @Test
    fun testAesGcmEncryptionDecryption() {
        val originalSheba = "IR910120000000001829304192"
        val originalNationalId = "0074829106"

        val encryptedSheba = KeystoreEncryptionManager.encrypt(originalSheba)
        val encryptedNationalId = KeystoreEncryptionManager.encrypt(originalNationalId)

        assertNotEquals(originalSheba, encryptedSheba)
        assertTrue(encryptedSheba.isNotBlank())

        val decryptedSheba = KeystoreEncryptionManager.decrypt(encryptedSheba)
        val decryptedNationalId = KeystoreEncryptionManager.decrypt(encryptedNationalId)

        assertEquals("Decrypted Sheba must match original", originalSheba, decryptedSheba)
        assertEquals("Decrypted National ID must match original", originalNationalId, decryptedNationalId)
    }

    // 6. Invalid Encryption Keys / Tampered Ciphertext
    @Test
    fun testInvalidEncryptionKeysAndTampering() {
        val plainText = "SecretMedicalRecord_2026"
        val cipherText = KeystoreEncryptionManager.encrypt(plainText)

        // 1. Decrypt with a different / invalid foreign AES key
        val foreignKeyBytes = ByteArray(32)
        SecureRandom().nextBytes(foreignKeyBytes)
        val foreignKey = SecretKeySpec(foreignKeyBytes, "AES")

        val resultWithWrongKey = KeystoreEncryptionManager.decryptWithKey(cipherText, foreignKey)
        assertNull("Decryption with wrong key must fail and return null", resultWithWrongKey)

        // 2. Tampered ciphertext (bit flipping)
        val rawBytes = android.util.Base64.decode(cipherText, android.util.Base64.NO_WRAP)
        rawBytes[rawBytes.size - 2] = (rawBytes[rawBytes.size - 2].toInt() xor 0xFF).toByte()
        val tamperedCipher = android.util.Base64.encodeToString(rawBytes, android.util.Base64.NO_WRAP)

        val tamperedResult = KeystoreEncryptionManager.decrypt(tamperedCipher)
        assertNull("Decryption of tampered ciphertext must fail authentication tag check", tamperedResult)
    }

    // 7. Duplicate Booking Prevention
    @Test
    fun testDuplicateBookingPrevention() {
        runBlocking {
            // First booking request
            val req1 = repository.submitBookingRequest(
                fullName = "مهران مدیری",
                phoneNumber = "09121110000",
                desiredService = "ویزیت تخصصی پوست",
                suggestedDate = "2026-11-20",
                suggestedTime = "10:00 - 12:00",
                venueId = "v_saadat_derma",
                venueNameFa = "پنت‌هاوس لورا",
                venueNameEn = "L Aura Penthouse",
                slotId = "slot_1",
                slotNameFa = "سوئیت لیزر ۱",
                slotNameEn = "Laser Suite 1",
                notes = "بدون یادداشت"
            )
            assertNotNull(req1)

            // Duplicate booking request with identical slot, date and time
            val req2 = repository.submitBookingRequest(
                fullName = "مهران مدیری",
                phoneNumber = "09121110000",
                desiredService = "ویزیت مجدد",
                suggestedDate = "2026-11-20",
                suggestedTime = "10:00 - 12:00",
                venueId = "v_saadat_derma",
                venueNameFa = "پنت‌هاوس لورا",
                venueNameEn = "L Aura Penthouse",
                slotId = "slot_1",
                slotNameFa = "سوئیت لیزر ۱",
                slotNameEn = "Laser Suite 1",
                notes = "تلاش مجدد برای رزرو"
            )

            // Duplicate prevention should return the existing appointment and prevent slot collision
            assertEquals("Duplicate request should return original tracking code", req1.trackingCode, req2.trackingCode)
        }
    }

    // 8. Offline Mode Verification
    @Test
    fun testOfflineModeOperation() {
        runBlocking {
            val req = repository.submitBookingRequest(
                fullName = "بیمار آفلاین",
                phoneNumber = "09127778899",
                desiredService = "خدمات عمومی",
                suggestedDate = "2026-11-22",
                suggestedTime = "09:00 - 11:00",
                venueId = "v_smile_studio",
                venueNameFa = "سرامیکا",
                venueNameEn = "Ceramica",
                slotId = "slot_4",
                slotNameFa = "سوئیت ۴",
                slotNameEn = "Suite 4",
                notes = "در حالت بدون اینترنت"
            )

            // Local bookings must not be shown as server-confirmed
            assertEquals(BookingRequestStatus.PENDING_CONFIRMATION, req.status)

            // Supabase gateway reports offline local status
            val gateway = OfflineSupabaseSyncGateway()
            val syncResult = gateway.syncPendingAppointments(emptyList())
            assertTrue("Gateway must report OfflineLocalOnly without fake sync", syncResult is ISupabaseSyncGateway.SyncResult.OfflineLocalOnly)
        }
    }

    // 9. Data Validation (National ID & Sheba ISO 7064)
    @Test
    fun testDataValidationAlgorithms() {
        // Strict Iranian National ID algorithm
        assertTrue(SecurityManager.isValidNationalId("0019283741", strictChecksum = true))
        assertTrue(SecurityManager.isValidNationalId("0074829106", strictChecksum = true))
        assertFalse(SecurityManager.isValidNationalId("0074829103", strictChecksum = true))
        assertFalse(SecurityManager.isValidNationalId("1111111111", strictChecksum = true))

        // Strict Iranian Sheba (IBAN) ISO 7064 algorithm
        assertTrue(SecurityManager.isValidShebaNumber("IR910120000000001829304192", strictChecksum = true))
        assertTrue(SecurityManager.isValidShebaNumber("IR480170000000009928172635", strictChecksum = true))
        assertFalse(SecurityManager.isValidShebaNumber("IR820120000000001829304192", strictChecksum = true))
        assertFalse(SecurityManager.isValidShebaNumber("invalid_iban", strictChecksum = true))
    }

    // 10. Repository Operations
    @Test
    fun testRepositoryOperations() {
        runBlocking {
            // Toggle slot status
            repository.toggleSlotStatus("slot_1", SlotStatus.IN_PROCEDURE)
            val updatedSlot = repository.slots.value.find { it.id == "slot_1" }
            assertEquals(SlotStatus.IN_PROCEDURE, updatedSlot?.status)

            // Add audit log
            repository.addAuditLog(
                type = "security",
                severity = Severity.INFO,
                titleFa = "تست لاگ امنیتی",
                titleEn = "Security Log Test",
                details = "بررسی متد لاگ"
            )
            val logs = repository.auditLogs.value
            assertTrue(logs.any { it.titleEn == "Security Log Test" })

            // Update booking status
            val req = repository.submitBookingRequest(
                fullName = "تست رزرو",
                phoneNumber = "09120000000",
                desiredService = "سرویس تستی",
                suggestedDate = "2026-11-25",
                suggestedTime = "12:00 - 14:00",
                venueId = "v_saadat_derma",
                venueNameFa = "لورا",
                venueNameEn = "L Aura",
                slotId = null,
                slotNameFa = null,
                slotNameEn = null,
                notes = ""
            )
            repository.updateBookingRequestStatus(req.id, BookingRequestStatus.CONFIRMED)
            val updatedReq = repository.bookingRequests.value.find { it.id == req.id }
            assertEquals(BookingRequestStatus.CONFIRMED, updatedReq?.status)
        }
    }
}
