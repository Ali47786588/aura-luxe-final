package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.converter.Converters
import com.example.data.local.dao.*
import com.example.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        ClinicEntity::class,
        DoctorEntity::class,
        PatientEntity::class,
        AppointmentEntity::class,
        ServiceEntity::class,
        ContractEntity::class,
        EscrowTransactionEntity::class,
        PaymentEntity::class,
        NotificationEntity::class,
        SupportTicketEntity::class,
        TicketMessageEntity::class,
        SecurityEventEntity::class,
        AuditLogEntity::class,
        UserSessionEntity::class,
        ClinicUnitEntity::class,
        OnboardingApplicationEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AuraDatabase : RoomDatabase() {

    abstract fun appointmentDao(): AppointmentDao
    abstract fun clinicDao(): ClinicDao
    abstract fun clinicUnitDao(): ClinicUnitDao
    abstract fun escrowTransactionDao(): EscrowTransactionDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun onboardingApplicationDao(): OnboardingApplicationDao
    abstract fun supportTicketDao(): SupportTicketDao
    abstract fun userSessionDao(): UserSessionDao
    abstract fun securityEventDao(): SecurityEventDao
    abstract fun userDao(): UserDao
    abstract fun doctorDao(): DoctorDao
    abstract fun patientDao(): PatientDao
    abstract fun serviceDao(): ServiceDao
    abstract fun contractDao(): ContractDao
    abstract fun paymentDao(): PaymentDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        const val DATABASE_NAME = "aura_luxe_local.db"

        /**
         * Schema Migration 1 -> 2:
         * Adds server synchronization state flag to appointments table
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE appointments ADD COLUMN isServerSynced INTEGER NOT NULL DEFAULT 0")
            }
        }

        @Volatile
        private var INSTANCE: AuraDatabase? = null

        fun getInstance(context: Context): AuraDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context.applicationContext).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AuraDatabase {
            return Room.databaseBuilder(
                context,
                AuraDatabase::class.java,
                DATABASE_NAME
            )
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigrationOnDowngrade()
                .build()
        }

        fun createInMemoryDatabase(context: Context): AuraDatabase {
            return Room.inMemoryDatabaseBuilder(
                context,
                AuraDatabase::class.java
            )
                .allowMainThreadQueries()
                .build()
        }
    }
}
