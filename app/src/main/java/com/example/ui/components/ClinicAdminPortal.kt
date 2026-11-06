package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.util.LocalizationUtils

@Composable
fun ClinicAdminPortal(
    language: Language,
    managedVenue: Venue,
    allVenues: List<Venue>,
    slots: List<RealtimeSlot>,
    bookingRequests: List<BookingRequest>,
    onSwitchVenue: (Venue) -> Unit,
    onExitPortal: () -> Unit,
    onToggleSlotStatus: (slotId: String, newStatus: SlotStatus) -> Unit,
    onUpdateRequestStatus: (requestId: String, status: BookingRequestStatus) -> Unit
) {
    val isFa = language == Language.FA
    val venueSlots = slots.filter { it.venueId == managedVenue.id }

    val pendingRequests = bookingRequests.filter { it.status == BookingRequestStatus.PENDING_CONFIRMATION }
    val confirmedRequests = bookingRequests.filter { it.status == BookingRequestStatus.CONFIRMED }

    val availableCount = venueSlots.count { it.status == SlotStatus.AVAILABLE }
    val inProcedureCount = venueSlots.count { it.status == SlotStatus.IN_PROCEDURE }
    val reservedCount = venueSlots.count { it.status == SlotStatus.RESERVED }

    val occupancyRate = if (venueSlots.isNotEmpty()) {
        ((reservedCount + inProcedureCount) * 100) / venueSlots.size
    } else 65

    val todayRevenueToman = managedVenue.baseHourlyToman * 5
    val monthlyRevenueToman = (managedVenue.baseHourlyToman * 5 * 8.5).toLong()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("clinic_admin_portal"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Executive Portal Header
        item {
            Surface(
                color = ObsidianSurface,
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(GoldChampagne),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Business,
                                    contentDescription = null,
                                    tint = ObsidianDark,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isFa) "پرتال اختصاصی مدیریت کلینیک" else "EXECUTIVE CLINIC PORTAL",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GoldChampagne,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isFa) managedVenue.nameFa else managedVenue.nameEn,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = White
                                )
                            }
                        }

                        IconButton(
                            onClick = onExitPortal,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(ObsidianCard)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Exit", tint = RoseAccent)
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = if (isFa) "مدیر درمانی و فنی: ${managedVenue.medicalDirectorFa}"
                        else "Medical Director: ${managedVenue.medicalDirectorEn}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryDark,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // 2. Section: Chairs & Beds Realtime Allocation (3-Way Toggle)
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isFa) "مدیریت وضعیت یونیت‌ها و تخت‌ها" else "Chairs & Beds Management",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                    Text(
                        text = if (isFa) "${venueSlots.size} یونیت" else "${venueSlots.size} units",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldChampagne
                    )
                }
                Spacer(Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    venueSlots.forEach { slot ->
                        OperatoryManagementCard(
                            language = language,
                            slot = slot,
                            onStatusChange = { newStatus -> onToggleSlotStatus(slot.id, newStatus) }
                        )
                    }
                }
            }
        }

        // 3. Section: Pending Appointment Booking Requests Queue
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isFa) "درخواست‌های نوبت در انتظار تایید" else "Pending Booking Requests",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            color = AmberMuted,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "${pendingRequests.size} جدید",
                                color = AmberAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                if (pendingRequests.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        pendingRequests.forEach { req ->
                            PendingRequestCard(
                                language = language,
                                request = req,
                                onApprove = { onUpdateRequestStatus(req.id, BookingRequestStatus.CONFIRMED) },
                                onReject = { onUpdateRequestStatus(req.id, BookingRequestStatus.REJECTED) }
                            )
                        }
                    }
                } else {
                    Surface(
                        color = ObsidianCard,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isFa) "هیچ درخواست نوبت معلقی وجود ندارد. تمام موارد بررسی شده‌اند."
                            else "All booking requests have been reviewed.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondaryDark,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }
            }
        }

        // 4. Section: Confirmed Appointments Master Schedule
        item {
            Column {
                Text(
                    text = if (isFa) "نوبت‌های قطعی و زمان‌بندی شده" else "Confirmed Schedule",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = White
                )

                Spacer(Modifier.height(8.dp))

                if (confirmedRequests.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        confirmedRequests.forEach { req ->
                            Surface(
                                color = ObsidianCard,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldAccent.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = req.fullName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = White
                                        )
                                        Text(
                                            text = "${req.desiredService} • ${req.suggestedDate}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = GoldChampagne
                                        )
                                        Text(
                                            text = "کد رهگیری: ${req.trackingCode}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextMutedDark,
                                            fontSize = 9.sp
                                        )
                                    }

                                    Surface(
                                        color = EmeraldMuted,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = if (isFa) "تایید قطعی" else "Confirmed",
                                            color = EmeraldAccent,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Section: Revenue & Seat Occupancy Overview
        item {
            Surface(
                color = ObsidianSurface,
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isFa) "آمار مالی و ضریب اشغال تخت‌ها" else "Revenue & Occupancy Analytics",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = ObsidianCard,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (isFa) "درآمد امروز (برآوردی):" else "Today's Revenue:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMutedDark
                                )
                                Text(
                                    text = LocalizationUtils.formatPrice(todayRevenueToman, language),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldChampagne
                                )
                            }
                        }

                        Surface(
                            color = ObsidianCard,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (isFa) "ضریب اشغال یونیت‌ها:" else "Occupancy Rate:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMutedDark
                                )
                                Text(
                                    text = "${LocalizationUtils.formatNumber(occupancyRate, language)}%",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = EmeraldAccent
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Occupancy Progress Bar
                    LinearProgressIndicator(
                        progress = { (occupancyRate / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = GoldChampagne,
                        trackColor = ObsidianBorder
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = if (isFa) "${LocalizationUtils.formatNumber(availableCount, language)} یونیت آماده پذیرش هم‌اکنون"
                        else "$availableCount units available now",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondaryDark,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun OperatoryManagementCard(
    language: Language,
    slot: RealtimeSlot,
    onStatusChange: (SlotStatus) -> Unit
) {
    val isFa = language == Language.FA

    Surface(
        color = ObsidianCard,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isFa) slot.roomNameFa else slot.roomNameEn,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
                SlotStatusBadge(status = slot.status, language = language)
            }

            Spacer(Modifier.height(8.dp))

            // 3-Way Instant Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                StatusPill(
                    label = if (isFa) "آماده" else "Available",
                    isSelected = slot.status == SlotStatus.AVAILABLE,
                    color = EmeraldAccent,
                    onClick = { onStatusChange(SlotStatus.AVAILABLE) },
                    modifier = Modifier.weight(1f)
                )
                StatusPill(
                    label = if (isFa) "رزرو" else "Reserved",
                    isSelected = slot.status == SlotStatus.RESERVED,
                    color = AmberAccent,
                    onClick = { onStatusChange(SlotStatus.RESERVED) },
                    modifier = Modifier.weight(1f)
                )
                StatusPill(
                    label = if (isFa) "در حال کار" else "Action",
                    isSelected = slot.status == SlotStatus.IN_PROCEDURE,
                    color = SkyAccent,
                    onClick = { onStatusChange(SlotStatus.IN_PROCEDURE) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatusPill(
    label: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Surface(
        color = if (isSelected) color.copy(alpha = 0.2f) else ObsidianSurface,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) color else ObsidianBorder
        ),
        modifier = modifier
            .height(32.dp)
            .clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                color = if (isSelected) color else TextSecondaryDark,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun PendingRequestCard(
    language: Language,
    request: BookingRequest,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val isFa = language == Language.FA

    Surface(
        color = ObsidianCard,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AmberAccent.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = request.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
                Surface(
                    color = GoldChampagneMuted,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = request.phoneNumber,
                        color = GoldChampagne,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))
            Text(
                text = if (isFa) "خدمت: ${request.desiredService}" else "Service: ${request.desiredService}",
                style = MaterialTheme.typography.bodyMedium,
                color = GoldChampagneLight,
                fontSize = 12.sp
            )
            Text(
                text = if (isFa) "زمان: ${request.suggestedDate} (${request.suggestedTime})"
                else "Time: ${request.suggestedDate} (${request.suggestedTime})",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondaryDark
            )

            if (request.notes.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = if (isFa) "یادداشت: ${request.notes}" else "Notes: ${request.notes}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMutedDark,
                    fontSize = 11.sp
                )
            }

            Spacer(Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldAccent,
                        contentColor = ObsidianDark
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f).height(34.dp)
                ) {
                    Text(text = if (isFa) "تایید نوبت" else "Approve", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onReject,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseAccent),
                    border = androidx.compose.foundation.BorderStroke(1.dp, RoseAccent.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.weight(1f).height(34.dp)
                ) {
                    Text(text = if (isFa) "رد درخواست" else "Reject", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}
