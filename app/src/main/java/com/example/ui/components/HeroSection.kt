package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
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
import com.example.R
import com.example.data.model.District
import com.example.data.model.Language
import com.example.ui.theme.*
import com.example.ui.util.LocalizationUtils

@Composable
fun HeroSection(
    language: Language,
    selectedDistrict: District,
    onOpenBookingModal: () -> Unit,
    onScrollToSlots: () -> Unit
) {
    val isFa = language == Language.FA

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("hero_section")
    ) {
        // Luxury Hero Visual Card with Scrim & Floating Badge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, GoldChampagneMuted, RoundedCornerShape(24.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_clinic_hero),
                contentDescription = "L Aura Penthouse Suite",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Scrim Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                ObsidianDark.copy(alpha = 0.5f),
                                ObsidianDark.copy(alpha = 0.95f)
                            )
                        )
                    )
            )

            // Top Left Kicker Badge
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(14.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ObsidianDark.copy(alpha = 0.85f))
                    .border(1.dp, GoldChampagneMuted, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = GoldChampagne,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(Modifier.width(5.dp))
                Text(
                    text = if (isFa) "سوئیت‌های لوکس تایید شده" else "VERIFIED CLINICAL SUITES",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldChampagneLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }

            // Bottom Info Bar
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (isFa) "پنت‌هاوس درماتولوژی لورا" else "L Aura Aesthetics Penthouse",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                    Surface(
                        color = EmeraldMuted,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (isFa) "۳ یونیت فعال" else "3 Units Active",
                            color = EmeraldAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))
                Text(
                    text = if (isFa) "سوئیت‌های جراحی مجهز به کندلا جنتل‌مکس پرو و فشار منفی در سعادت‌آباد"
                    else "Surgical suites equipped with Candela GentleMax Pro & negative pressure",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryDark,
                    maxLines = 2
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Headline & Value Proposition
        Text(
            text = if (isFa) "سامانه جامع رزرو سوئیت‌های بالینی، زیبایی و طراحی لبخند"
            else "Curated Luxury Sanctuaries for Aesthetics, Skin & Smile Design",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = White,
            lineHeight = 28.sp
        )

        Spacer(Modifier.height(6.dp))
        Text(
            text = if (isFa) "رزرو مستقیم ساعتی و شیفتی یونیت‌های دندانپزشکی کاستلینی، اتاق‌های جراحی پوست کندلا و پادهای تخصصی استایل در تهران و کرج"
            else "Explore and instantly reserve premier skin and aesthetic clinics, luxury salons, and smile design ateliers across Tehran and Karaj.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark,
            lineHeight = 18.sp
        )

        Spacer(Modifier.height(14.dp))

        // CTAs Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onOpenBookingModal,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldChampagne,
                    contentColor = ObsidianDark
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("hero_book_cta")
            ) {
                Text(
                    text = if (isFa) "ثبت درخواست نوبت" else "Book Operatory",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            OutlinedButton(
                onClick = onScrollToSlots,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TextPrimaryDark
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("hero_slots_cta")
            ) {
                Text(
                    text = if (isFa) "مشاهده تخت‌های زنده" else "Live Operatories",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        // 4-Column Proof & Statistics Strip
        Surface(
            color = ObsidianCard,
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatItem(
                    label = if (isFa) "مراکز لوکس" else "Centers",
                    value = LocalizationUtils.formatNumber("74+", language)
                )
                StatItem(
                    label = if (isFa) "یونیت فعال" else "Operatories",
                    value = LocalizationUtils.formatNumber("320+", language)
                )
                StatItem(
                    label = if (isFa) "نوبت موفق" else "Bookings",
                    value = LocalizationUtils.formatNumber("4,850+", language)
                )
                StatItem(
                    label = if (isFa) "رضایت بالینی" else "Satisfaction",
                    value = LocalizationUtils.formatNumber("4.96", language)
                )
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = GoldChampagne
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondaryDark,
            fontSize = 10.sp
        )
    }
}
