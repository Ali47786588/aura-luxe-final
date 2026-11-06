package com.example.data.local.converter

import androidx.room.TypeConverter
import com.example.data.model.*
import java.util.Date

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromStringList(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return value.split("|||").filter { it.isNotBlank() }
    }

    @TypeConverter
    fun toStringList(list: List<String>?): String {
        return list?.joinToString("|||") ?: ""
    }

    @TypeConverter
    fun fromUserRole(value: String?): UserRole {
        return value?.let { runCatching { UserRole.valueOf(it) }.getOrNull() } ?: UserRole.PUBLIC
    }

    @TypeConverter
    fun toUserRole(role: UserRole?): String {
        return role?.name ?: UserRole.PUBLIC.name
    }

    @TypeConverter
    fun fromCategoryId(value: String?): CategoryId {
        return value?.let { runCatching { CategoryId.valueOf(it) }.getOrNull() } ?: CategoryId.ALL
    }

    @TypeConverter
    fun toCategoryId(cat: CategoryId?): String {
        return cat?.name ?: CategoryId.ALL.name
    }

    @TypeConverter
    fun fromSlotStatus(value: String?): SlotStatus {
        return value?.let { runCatching { SlotStatus.valueOf(it) }.getOrNull() } ?: SlotStatus.AVAILABLE
    }

    @TypeConverter
    fun toSlotStatus(status: SlotStatus?): String {
        return status?.name ?: SlotStatus.AVAILABLE.name
    }

    @TypeConverter
    fun fromSlotType(value: String?): SlotType {
        return value?.let { runCatching { SlotType.valueOf(it) }.getOrNull() } ?: SlotType.CHAIR
    }

    @TypeConverter
    fun toSlotType(type: SlotType?): String {
        return type?.name ?: SlotType.CHAIR.name
    }

    @TypeConverter
    fun fromBookingStatus(value: String?): BookingRequestStatus {
        return value?.let { runCatching { BookingRequestStatus.valueOf(it) }.getOrNull() } ?: BookingRequestStatus.PENDING_CONFIRMATION
    }

    @TypeConverter
    fun toBookingStatus(status: BookingRequestStatus?): String {
        return status?.name ?: BookingRequestStatus.PENDING_CONFIRMATION.name
    }

    @TypeConverter
    fun fromEscrowStatus(value: String?): EscrowStatus {
        return value?.let { runCatching { EscrowStatus.valueOf(it) }.getOrNull() } ?: EscrowStatus.HELD_IN_ESCROW
    }

    @TypeConverter
    fun toEscrowStatus(status: EscrowStatus?): String {
        return status?.name ?: EscrowStatus.HELD_IN_ESCROW.name
    }

    @TypeConverter
    fun fromAppStatus(value: String?): ApplicationStatus {
        return value?.let { runCatching { ApplicationStatus.valueOf(it) }.getOrNull() } ?: ApplicationStatus.PENDING
    }

    @TypeConverter
    fun toAppStatus(status: ApplicationStatus?): String {
        return status?.name ?: ApplicationStatus.PENDING.name
    }

    @TypeConverter
    fun fromSeverity(value: String?): Severity {
        return value?.let { runCatching { Severity.valueOf(it) }.getOrNull() } ?: Severity.INFO
    }

    @TypeConverter
    fun toSeverity(severity: Severity?): String {
        return severity?.name ?: Severity.INFO.name
    }
}
