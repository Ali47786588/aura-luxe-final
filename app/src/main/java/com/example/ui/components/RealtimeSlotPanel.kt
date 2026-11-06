package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
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
import com.example.data.model.Language
import com.example.data.model.RealtimeSlot
import com.example.data.model.SlotStatus
import com.example.ui.theme.*
import com.example.ui.util.LocalizationUtils

@Composable
fun RealtimeSlotPanel(
    language: Language,
    slots: List<RealtimeSlot>,
    onToggleSlotStatus: (slotId: String, newStatus: SlotStatus) -> Unit,
    onBookSlot: (RealtimeSlot) -> Unit
) {
    val isFa = language == Language.FA
    var filterAvailableOnly by remember { mutableStateOf(false) }

    val displayedSlots = if (filterAvailableOnly) {
        slots.filter { it.status == SlotStatus.AVAILABLE }
    } else {
        slots
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("realtime_slots_section")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isFa) "ماتریس زنده سوئیت‌ها و تخت‌های درمانی"
                    else "Live Operatory & Suite Matrix",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
                Text(
                    text = if (isFa) "وضعیت لحظه‌ای استریل، تجهیزات و رزرو آنی"
                    else "Real-time sanitization, equipment & instant hourly booking",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryDark,
                    fontSize = 12.sp
                )
            }

            // Available Filter Toggle
            Surface(
                color = if (filterAvailableOnly) GoldChampagne else ObsidianCard,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (filterAvailableOnly) GoldChampagne else ObsidianBorder),
                modifier = Modifier
                    .clickable { filterAvailableOnly = !filterAvailableOnly }
                    .testTag("filter_available_toggle")
            ) {
                Text(
                    text = if (isFa) "فقط آماده پذیرش" else "Available Only",
                    color = if (filterAvailableOnly) ObsidianDark else TextPrimaryDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            displayedSlots.forEach { slot ->
                SlotItemCard(
                    language = language,
                    slot = slot,
                    onToggleStatus = {
                        val nextStatus = when (slot.status) {
                            SlotStatus.AVAILABLE -> SlotStatus.IN_PROCEDURE
                            SlotStatus.IN_PROCEDURE -> SlotStatus.SANITIZING
                            SlotStatus.SANITIZING -> SlotStatus.AVAILABLE
                            SlotStatus.RESERVED -> SlotStatus.IN_PROCEDURE
                        }
                        onToggleSlotStatus(slot.id, nextStatus)
                    },
                    onBook = { onBookSlot(slot) }
                )
            }
        }
    }
}

@Composable
fun SlotItemCard(
    language: Language,
    slot: RealtimeSlot,
    onToggleStatus: () -> Unit,
    onBook: () -> Unit
) {
    val isFa = language == Language.FA
    val isAvailable = slot.status == SlotStatus.AVAILABLE

    Surface(
        color = ObsidianCard,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isAvailable) EmeraldAccent.copy(alpha = 0.35f) else ObsidianBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("slot_item_${slot.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Venue & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isFa) slot.venueNameFa else slot.venueNameEn,
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldChampagne,
                    fontWeight = FontWeight.Bold
                )

                SlotStatusBadge(status = slot.status, language = language)
            }

            Spacer(Modifier.height(4.dp))

            // Room / Chair Name
            Text(
                text = if (isFa) slot.roomNameFa else slot.roomNameEn,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = White
            )

            // Current In-Procedure Info if active
            if (slot.status == SlotStatus.IN_PROCEDURE && slot.currentPractitioner != null) {
                Spacer(Modifier.height(6.dp))
                Surface(
                    color = SkyMuted,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SkyAccent.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = if (isFa) "پزشک مقیم: ${slot.currentPractitioner}"
                            else "Practitioner: ${slot.currentPractitioner}",
                            color = SkyAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (slot.patientProcedure != null) {
                            Text(
                                text = slot.patientProcedure,
                                color = TextPrimaryDark,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // Equipment list
            Spacer(Modifier.height(8.dp))
            Text(
                text = slot.equipment.joinToString(" • "),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondaryDark,
                fontSize = 11.sp,
                maxLines = 1
            )

            Spacer(Modifier.height(8.dp))

            // Hygiene & Sanitization metadata row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = ObsidianSurface,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = TextMutedDark,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (isFa) "استریلیزاسیون: ${LocalizationUtils.formatNumber(slot.lastSanitizedMinutesAgo, language)} دقیقه پیش"
                            else "Sanitized: ${slot.lastSanitizedMinutesAgo}m ago",
                            color = TextMutedDark,
                            fontSize = 10.sp
                        )
                    }
                }

                if (slot.autoclaveReady) {
                    Surface(
                        color = ObsidianSurface,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (isFa) "اتوکلاو کلاس ب" else "Class-B Autoclave",
                            color = TextMutedDark,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Bottom Actions & Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isFa) "نرخ ساعتی:" else "Rate:",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMutedDark,
                        fontSize = 10.sp
                    )
                    Text(
                        text = LocalizationUtils.formatPrice(slot.pricePerHourToman, language),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldChampagne
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Quick status cycle button (for managers)
                    IconButton(
                        onClick = onToggleStatus,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ObsidianSurface)
                            .border(1.dp, ObsidianBorder, RoundedCornerShape(10.dp))
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Cycle Status",
                            tint = GoldChampagne,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Book Unit Button
                    Button(
                        onClick = onBook,
                        enabled = isAvailable,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAvailable) GoldChampagne else ObsidianBorder,
                            contentColor = if (isAvailable) ObsidianDark else TextMutedDark
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            text = if (isAvailable) {
                                if (isFa) "رزرو این سوئیت" else "Book Suite"
                            } else {
                                if (isFa) "اشغال / رزرو" else "Unavailable"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SlotStatusBadge(status: SlotStatus, language: Language) {
    val isFa = language == Language.FA
    val (bgColor, textColor, label) = when (status) {
        SlotStatus.AVAILABLE -> Triple(
            EmeraldMuted,
            EmeraldAccent,
            if (isFa) "آماده پذیرش" else "Available"
        )
        SlotStatus.IN_PROCEDURE -> Triple(
            SkyMuted,
            SkyAccent,
            if (isFa) "در حال درمان" else "In Action"
        )
        SlotStatus.RESERVED -> Triple(
            AmberMuted,
            AmberAccent,
            if (isFa) "رزرو شده" else "Reserved"
        )
        SlotStatus.SANITIZING -> Triple(
            RoseMuted,
            RoseAccent,
            if (isFa) "در حال استریل" else "Sanitizing"
        )
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.4f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Spacer(Modifier.width(5.dp))
            Text(
                text = label,
                color = textColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
