package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.BookingRequest
import com.example.data.model.Language
import com.example.data.model.RealtimeSlot
import com.example.data.model.Venue
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingRequestModal(
    language: Language,
    venues: List<Venue>,
    preselectedVenue: Venue?,
    preselectedSlot: RealtimeSlot?,
    submittedRequest: BookingRequest?,
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onSubmitRequest: (
        fullName: String,
        phoneNumber: String,
        desiredService: String,
        suggestedDate: String,
        suggestedTime: String,
        notes: String
    ) -> Unit
) {
    val isFa = language == Language.FA
    val clipboardManager = LocalClipboardManager.current

    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var desiredService by remember { mutableStateOf(if (isFa) "مشاوره و خدمات تخصصی" else "Specialized Consultation") }
    var suggestedDate by remember { mutableStateOf("2026-11-05") }
    var suggestedTime by remember { mutableStateOf("10:00 - 12:00 (صبح)") }
    var notes by remember { mutableStateOf("") }
    var isCopied by remember { mutableStateOf(false) }

    val quickServices = if (isFa) {
        listOf("فیلر و بوتاکس", "لیزر اندولیفت", "لمینت سرامیکی", "کامپوزیت ونیر", "فیشیال VIP", "بالیاژ تخصصی")
    } else {
        listOf("Filler & Botox", "Endolift Laser", "Dental Laminate", "Composite Veneer", "VIP Facial", "Hair Balayage")
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = ObsidianSurface,
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.35f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("booking_request_modal")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Event,
                                contentDescription = null,
                                tint = GoldChampagne,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = if (isFa) "درخواست نوبت مستقیم کلینیک" else "CLINIC APPOINTMENT REQUEST",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldChampagne,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = if (submittedRequest == null) {
                                if (isFa) "ثبت فرم هماهنگی و رزرو" else "Schedule Appointment"
                            } else {
                                if (isFa) "رسید پیگیری درخواست" else "Confirmation Slip"
                            },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ObsidianCard)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryDark)
                    }
                }

                Spacer(Modifier.height(14.dp))

                if (submittedRequest == null) {
                    // FORM MODE
                    // Venue & Slot Info Card
                    Surface(
                        color = ObsidianCard,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (isFa) "مرکز انتخابی:" else "Selected Facility:",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMutedDark
                            )
                            Text(
                                text = if (isFa) (preselectedVenue?.nameFa ?: venues[0].nameFa)
                                else (preselectedVenue?.nameEn ?: venues[0].nameEn),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = White
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = if (isFa) "سوئیت درخواستی: ${preselectedSlot?.roomNameFa ?: "سوئیت جراحی و درمان VIP"}"
                                else "Requested Unit: ${preselectedSlot?.roomNameEn ?: "Primary VIP Operatory"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldChampagne
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Input: Full Name
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text(if (isFa) "نام و نام خانوادگی مراجع:" else "Full Name:") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = White,
                            unfocusedTextColor = White,
                            focusedBorderColor = GoldChampagne,
                            unfocusedBorderColor = ObsidianBorder
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(10.dp))

                    // Input: Phone Number
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text(if (isFa) "شماره تماس همراه:" else "Phone Number:") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = White,
                            unfocusedTextColor = White,
                            focusedBorderColor = GoldChampagne,
                            unfocusedBorderColor = ObsidianBorder
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(10.dp))

                    // Input: Desired Service / Procedure
                    OutlinedTextField(
                        value = desiredService,
                        onValueChange = { desiredService = it },
                        label = { Text(if (isFa) "خدمت یا فرآیند درمانی مورد نظر:" else "Desired Procedure:") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = White,
                            unfocusedTextColor = White,
                            focusedBorderColor = GoldChampagne,
                            unfocusedBorderColor = ObsidianBorder
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Quick-Pick Service Tags
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = if (isFa) "انتخاب سریع خدمت:" else "Quick Select:",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMutedDark,
                        fontSize = 10.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        quickServices.take(3).forEach { srv ->
                            Surface(
                                color = ObsidianCard,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                                modifier = Modifier.clickable { desiredService = srv }
                            ) {
                                Text(
                                    text = srv,
                                    color = TextSecondaryDark,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Row: Date & Time
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = suggestedDate,
                            onValueChange = { suggestedDate = it },
                            label = { Text(if (isFa) "تاریخ پیشنهادی:" else "Date:") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = White,
                                unfocusedTextColor = White,
                                focusedBorderColor = GoldChampagne,
                                unfocusedBorderColor = ObsidianBorder
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = suggestedTime,
                            onValueChange = { suggestedTime = it },
                            label = { Text(if (isFa) "بازه زمانی:" else "Time Slot:") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = White,
                                unfocusedTextColor = White,
                                focusedBorderColor = GoldChampagne,
                                unfocusedBorderColor = ObsidianBorder
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    // Input: Notes
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text(if (isFa) "توضیحات و نیازمندی‌ها (اختیاری):" else "Additional Notes:") },
                        maxLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = White,
                            unfocusedTextColor = White,
                            focusedBorderColor = GoldChampagne,
                            unfocusedBorderColor = ObsidianBorder
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    // Informational Assurance Notice
                    Surface(
                        color = AmberMuted,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AmberAccent.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = AmberAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (isFa) "درخواست شما با وضعیت «در انتظار تایید» در کارتابل مدیریت ثبت شده و مدیریت مرکز جهت تایید نهایی با شما تماس خواهند گرفت."
                                else "Your request will be queued as Pending Confirmation. Clinic management will review and confirm your slot promptly.",
                                color = GoldChampagneLight,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            if (fullName.isNotBlank() && phoneNumber.isNotBlank()) {
                                onSubmitRequest(fullName, phoneNumber, desiredService, suggestedDate, suggestedTime, notes)
                            }
                        },
                          enabled = !isSubmitting,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldChampagne,
                            contentColor = ObsidianDark
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("submit_booking_request_button")
                    ) {
                        Text(
                            text = if (isSubmitting) (if (isFa) "در حال ثبت..." else "Submitting...") else (if (isFa) "ارسال درخواست نوبت (در انتظار تایید)" else "Submit Booking Request"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    // CONFIRMATION SLIP MODE
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(AmberMuted)
                                .border(1.dp, AmberAccent.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.HourglassTop,
                                contentDescription = null,
                                tint = AmberAccent,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        // Status Badge
                        Surface(
                            color = AmberMuted,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AmberAccent.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = if (isFa) "وضعیت: در انتظار تایید مدیریت کلینیک"
                                else "Status: Pending Confirmation",
                                color = AmberAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        Spacer(Modifier.height(14.dp))

                        // Tracking Code Spotlight Card
                        Surface(
                            color = ObsidianCard,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = if (isFa) "کد رهگیری نوبت:" else "Tracking Code:",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMutedDark
                                    )
                                    Text(
                                        text = submittedRequest.trackingCode,
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Black,
                                        color = GoldChampagne,
                                        letterSpacing = 1.sp
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(submittedRequest.trackingCode))
                                        isCopied = true
                                    },
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(GoldChampagneMuted)
                                ) {
                                    Icon(
                                        if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                        contentDescription = "Copy Code",
                                        tint = GoldChampagne
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(14.dp))

                        if (submittedRequest.wasDuplicate) {
                            Surface(
                                color = AmberMuted,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AmberAccent.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (isFa) "این زمان قبلاً درخواست شده است؛ رسید زیر مربوط به درخواست قبلی است و درخواست جدیدی ثبت نشده است." else "This time slot was already requested. The receipt below belongs to the previous request; no new request was created.",
                                    color = AmberAccent,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                            Spacer(Modifier.height(14.dp))
                        }
                        // Request Dossier Receipt
                        Surface(
                            color = ObsidianCard,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ReceiptRow(label = if (isFa) "مرکز درمانی:" else "Facility:", value = submittedRequest.venueNameFa)
                                ReceiptRow(label = if (isFa) "نام مراجع:" else "Applicant:", value = submittedRequest.fullName)
                                ReceiptRow(label = if (isFa) "تلفن تماس:" else "Phone:", value = submittedRequest.phoneNumber)
                                ReceiptRow(label = if (isFa) "خدمت درخواستی:" else "Service:", value = submittedRequest.desiredService)
                                ReceiptRow(label = if (isFa) "زمان پیشنهادی:" else "Schedule:", value = "${submittedRequest.suggestedDate} (${submittedRequest.suggestedTime})")
                                if (!submittedRequest.slotNameFa.isNullOrBlank()) {
                                    ReceiptRow(label = if (isFa) "سوئیت:" else "Slot:", value = submittedRequest.slotNameFa)
                                }
                            }
                        }

                        Spacer(Modifier.height(18.dp))

                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldChampagne,
                                contentColor = ObsidianDark
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Text(
                                text = if (isFa) "بستن و بازگشت به سامانه" else "Close & Return",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextMutedDark
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
        )
    }
}
