package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.District
import com.example.data.model.Language
import com.example.data.model.UserRole
import com.example.ui.theme.*

@Composable
fun Navbar(
    language: Language,
    userRole: UserRole,
    selectedDistrict: District,
    onToggleLanguage: () -> Unit,
    onOpenLocationModal: () -> Unit,
    onOpenOnboardingModal: () -> Unit,
    onOpenSupportModal: () -> Unit,
    onOpenAdminLoginModal: () -> Unit,
    onLogout: () -> Unit,
    onOpenBookingModal: () -> Unit
) {
    val isFa = language == Language.FA

    Surface(
        color = ObsidianSurface,
        tonalElevation = 6.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Row 1: Brand & Role Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand Monogram & Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(GoldChampagneLight, GoldChampagne, GoldChampagneDark)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "A",
                            color = ObsidianDark,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                    }

                    Column {
                        Text(
                            text = if (isFa) "اورا لوکس" else "AURA LUXE",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                        Text(
                            text = when (userRole) {
                                UserRole.SUPER_ADMIN -> if (isFa) "پنل حاکمیت سیستم (حالت دمو)" else "SUPERADMIN (DEMO)"
                                UserRole.CLINIC_ADMIN -> if (isFa) "پرتال مدیریت کلینیک (دمو)" else "CLINIC PORTAL (DEMO)"
                                UserRole.PUBLIC -> if (isFa) "سوئیت‌های بالینی لوکس • ذخیره آفلاین" else "CLINICAL SUITES • OFFLINE"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldChampagne,
                            fontSize = 9.sp
                        )
                    }
                }

                // Top Right Action Buttons: Language & Auth/Logout
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Language Switcher
                    IconButton(
                        onClick = onToggleLanguage,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ObsidianCard)
                            .border(1.dp, ObsidianBorder, RoundedCornerShape(10.dp))
                            .testTag("lang_toggle_button")
                    ) {
                        Text(
                            text = if (isFa) "EN" else "فا",
                            color = GoldChampagne,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }

                    // Role indicator / Login / Logout
                    if (userRole == UserRole.PUBLIC) {
                        Button(
                            onClick = onOpenAdminLoginModal,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ObsidianCard,
                                contentColor = GoldChampagne
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagneMuted),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("manager_login_button")
                        ) {
                            Icon(
                                Icons.Outlined.Lock,
                                contentDescription = "Login",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = if (isFa) "ورود مدیران" else "Manager",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Button(
                            onClick = onLogout,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RoseMuted,
                                contentColor = RoseAccent
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, RoseAccent.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("logout_button")
                        ) {
                            Icon(
                                Icons.AutoMirrored.Outlined.Logout,
                                contentDescription = "Logout",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = if (isFa) "خروج" else "Exit",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Row 2: Context Bar (District, Register Partner, Support, Book Now)
            if (userRole == UserRole.PUBLIC) {
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Location Pill
                    Surface(
                        color = ObsidianCard,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .clickable { onOpenLocationModal() }
                            .testTag("location_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "District",
                                tint = GoldChampagne,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = if (isFa) selectedDistrict.nameFa else selectedDistrict.nameEn,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimaryDark,
                                maxLines = 1
                            )
                        }
                    }

                    // Onboarding / Register Center Button
                    Surface(
                        color = GoldChampagneMuted,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .height(34.dp)
                            .clickable { onOpenOnboardingModal() }
                            .testTag("onboarding_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        ) {
                            Icon(
                                Icons.Outlined.PersonAdd,
                                contentDescription = "Register",
                                tint = GoldChampagne,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = if (isFa) "ثبت مرکز" else "Register",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldChampagne,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Support Button
                    IconButton(
                        onClick = onOpenSupportModal,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ObsidianCard)
                            .border(1.dp, ObsidianBorder, RoundedCornerShape(10.dp))
                            .testTag("support_button")
                    ) {
                        Icon(
                            Icons.Outlined.Headphones,
                            contentDescription = "Support",
                            tint = TextSecondaryDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Book Direct CTA
                    Button(
                        onClick = onOpenBookingModal,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldChampagne,
                            contentColor = ObsidianDark
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("book_direct_button")
                    ) {
                        Text(
                            text = if (isFa) "درخواست رزرو" else "Book",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
