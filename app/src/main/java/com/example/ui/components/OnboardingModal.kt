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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.ApplicationStatus
import com.example.data.model.Language
import com.example.data.model.OnboardingApplication
import com.example.security.SecurityManager
import com.example.ui.theme.*

@Composable
fun OnboardingModal(
    language: Language,
    onDismiss: () -> Unit,
    onSubmitApplication: (OnboardingApplication) -> Unit
) {
    val isFa = language == Language.FA

    var activityType by remember { mutableStateOf("clinic") }
    var businessName by remember { mutableStateOf("") }
    var applicantName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var cityAndDistrict by remember { mutableStateOf("تهران - سعادت‌آباد") }
    var chairsCount by remember { mutableIntStateOf(2) }
    var description by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = ObsidianSurface,
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("onboarding_modal")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, tint = GoldChampagne, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = if (isFa) "پیوستن به شبکه مراکز لوکس" else "PARTNER ONBOARDING",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldChampagne,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = if (isFa) "ثبت‌نام مرکز یا پزشک مهمان" else "Register Center / Doctor",
                            style = MaterialTheme.typography.titleMedium,
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

                if (!submitted) {
                    // Type selector
                    Text(
                        text = if (isFa) "نوع فعالیت مرکز:" else "Category:",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondaryDark
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            Triple("clinic", if (isFa) "کلینیک پوست" else "Clinic", "پوست و لیزر"),
                            Triple("dental", if (isFa) "دندانپزشکی" else "Dental", "طراحی لبخند"),
                            Triple("salon", if (isFa) "سالن زیبایی" else "Salon", "خدمات VIP")
                        ).forEach { (typeId, label, _) ->
                            val isSelected = activityType == typeId
                            Surface(
                                color = if (isSelected) GoldChampagne else ObsidianCard,
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) GoldChampagne else ObsidianBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { activityType = typeId }
                            ) {
                                Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = label,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (isSelected) ObsidianDark else TextPrimaryDark
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    OutlinedTextField(
                        value = businessName,
                        onValueChange = { businessName = it },
                        label = { Text(if (isFa) "نام مرکز درمانی یا مطب:" else "Facility / Doctor Name:") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = White, unfocusedTextColor = White,
                            focusedBorderColor = GoldChampagne, unfocusedBorderColor = ObsidianBorder
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = applicantName,
                        onValueChange = { applicantName = it },
                        label = { Text(if (isFa) "نام مدیر یا پزشک متقاضی:" else "Director Name:") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = White, unfocusedTextColor = White,
                            focusedBorderColor = GoldChampagne, unfocusedBorderColor = ObsidianBorder
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text(if (isFa) "شماره همراه جهت هماهنگی:" else "Phone Number:") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = White, unfocusedTextColor = White,
                            focusedBorderColor = GoldChampagne, unfocusedBorderColor = ObsidianBorder
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = cityAndDistrict,
                        onValueChange = { cityAndDistrict = it },
                        label = { Text(if (isFa) "شهر و منطقه (مثال: تهران - سعادت‌آباد):" else "City & District:") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = White, unfocusedTextColor = White,
                            focusedBorderColor = GoldChampagne, unfocusedBorderColor = ObsidianBorder
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    // Chairs / Beds Counter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isFa) "تعداد یونیت‌ها یا تخت‌های آماده:" else "Available Units / Beds:",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondaryDark
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { if (chairsCount > 1) chairsCount-- },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ObsidianCard)
                            ) {
                                Text("-", color = White, fontWeight = FontWeight.Bold)
                            }
                            Text(
                                text = "$chairsCount یونیت",
                                color = GoldChampagne,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            IconButton(
                                onClick = { if (chairsCount < 30) chairsCount++ },
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ObsidianCard)
                            ) {
                                Text("+", color = White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(if (isFa) "تجهیزات اصلی و دستگاه‌های موجود:" else "Key Equipment / Notes:") },
                        maxLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = White, unfocusedTextColor = White,
                            focusedBorderColor = GoldChampagne, unfocusedBorderColor = ObsidianBorder
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (businessName.isNotBlank() && phoneNumber.isNotBlank()) {
                                val app = OnboardingApplication(
                                    id = "app-${System.currentTimeMillis()}",
                                    applicantType = activityType,
                                    activityTypeFa = if (activityType == "clinic") "کلینیک پوست" else if (activityType == "dental") "دندانپزشکی" else "سالن زیبایی",
                                    businessName = SecurityManager.sanitizeInput(businessName),
                                    applicantName = SecurityManager.sanitizeInput(applicantName.ifBlank { businessName }),
                                    phoneNumber = SecurityManager.sanitizeInput(phoneNumber),
                                    cityAndDistrict = SecurityManager.sanitizeInput(cityAndDistrict),
                                    chairsOrBedsCount = chairsCount,
                                    shortDescription = SecurityManager.sanitizeInput(description),
                                    status = ApplicationStatus.PENDING,
                                    submittedAt = "اکنون"
                                )
                                onSubmitApplication(app)
                                submitted = true
                            }
                        },
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
                            text = if (isFa) "ارسال فرم درخواست همکاری" else "Submit Application",
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    // Success Screen
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                    ) {
                        Text(
                            text = if (isFa) "درخواست همکاری شما با موفقیت ثبت شد و در صف بررسی سوپرادمین قرار گرفت."
                            else "Your application has been received and queued for SuperAdmin audit.",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = GoldChampagne,
                            lineHeight = 22.sp
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = if (isFa) "تیم نظارت و اعتبارسنجی با شماره $phoneNumber تماس حاصل خواهند نمود."
                            else "Our clinical team will reach out to $phoneNumber.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondaryDark
                        )
                        Spacer(Modifier.height(18.dp))
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldChampagne, contentColor = ObsidianDark),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(42.dp)
                        ) {
                            Text(if (isFa) "بستن" else "Close", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
