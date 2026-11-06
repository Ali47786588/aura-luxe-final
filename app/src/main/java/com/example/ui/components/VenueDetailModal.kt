package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Language
import com.example.data.model.Venue
import com.example.ui.theme.*
import com.example.ui.util.LocalizationUtils

@Composable
fun VenueDetailModal(
    language: Language,
    venue: Venue,
    onDismiss: () -> Unit,
    onBookVenue: () -> Unit
) {
    val isFa = language == Language.FA

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = ObsidianSurface,
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("venue_detail_modal")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Hero Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Image(
                        painter = painterResource(id = venue.imageResId),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, ObsidianDark.copy(alpha = 0.9f))
                                )
                            )
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ObsidianDark.copy(alpha = 0.7f))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = White)
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = GoldChampagne, modifier = Modifier.size(13.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = if (isFa) venue.addressFa else venue.addressEn,
                                style = MaterialTheme.typography.labelSmall,
                                color = TextPrimaryDark,
                                fontSize = 10.sp
                            )
                        }
                        Text(
                            text = if (isFa) venue.nameFa else venue.nameEn,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    }
                }

                // Body Dossier
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = if (isFa) venue.descriptionFa else venue.descriptionEn,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimaryDark,
                        lineHeight = 20.sp
                    )

                    Spacer(Modifier.height(14.dp))

                    // Medical Director Banner
                    Surface(
                        color = ObsidianCard,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GoldChampagneMuted),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Medication, contentDescription = null, tint = GoldChampagne, modifier = Modifier.size(20.dp))
                            }
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isFa) "مسئول فنی و سرپرست بالینی:" else "Clinical Director:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMutedDark,
                                    fontSize = 9.sp
                                )
                                Text(
                                    text = if (isFa) venue.medicalDirectorFa else venue.medicalDirectorEn,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = White
                                )
                                Text(
                                    text = "شماره نظام: ${venue.medicalLicenseNumber}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GoldChampagneLight,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // Equipment List
                    Text(
                        text = if (isFa) "تجهیزات و دستگاه‌های بالینی موجود در مرکز:" else "Clinical Equipment On-Site:",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldChampagne,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))
                    venue.equipmentList.forEach { eq ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldAccent, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(text = eq, style = MaterialTheme.typography.bodyMedium, color = TextPrimaryDark, fontSize = 11.sp)
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Certifications
                    Text(
                        text = if (isFa) "مجوزها و تاییدیه‌های استانداردهای بهداشتی:" else "Accreditations & Hygiene Certs:",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldChampagne,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))
                    venue.certifications.forEach { cert ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = GoldChampagne, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(text = cert, style = MaterialTheme.typography.bodyMedium, color = TextPrimaryDark, fontSize = 11.sp)
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Amenities
                    Text(
                        text = if (isFa) "امکانات رفاهی و خدمات پشتیبانی:" else "Amenities & Hospitality:",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldChampagne,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        venue.amenities.take(2).forEach { a ->
                            Surface(
                                color = ObsidianCard,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                            ) {
                                Text(
                                    text = a,
                                    color = TextSecondaryDark,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Bottom Booking Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isFa) "نرخ پایه رزرو:" else "Base Rate:",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMutedDark,
                                fontSize = 10.sp
                            )
                            Text(
                                text = LocalizationUtils.formatPrice(venue.baseHourlyToman, language),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldChampagne
                            )
                        }

                        Button(
                            onClick = {
                                onDismiss()
                                onBookVenue()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldChampagne,
                                contentColor = ObsidianDark
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(42.dp)
                        ) {
                            Text(
                                text = if (isFa) "رزرو این مرکز" else "Book Facility",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
