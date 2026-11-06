package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Star
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
import com.example.data.model.CategoryId
import com.example.data.model.District
import com.example.data.model.Language
import com.example.data.model.Venue
import com.example.ui.theme.*
import com.example.ui.util.LocalizationUtils

@Composable
fun VenueCatalog(
    language: Language,
    venues: List<Venue>,
    selectedCategory: CategoryId,
    selectedDistrict: District,
    onSelectCategory: (CategoryId) -> Unit,
    onViewVenueDetails: (Venue) -> Unit,
    onBookVenue: (Venue) -> Unit
) {
    val isFa = language == Language.FA

    val filteredVenues = venues.filter { v ->
        val catMatches = selectedCategory == CategoryId.ALL || v.category == selectedCategory
        val cityMatches = v.cityId == selectedDistrict.cityId
        catMatches && cityMatches
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("venue_catalog_section")
    ) {
        // Section Title
        Text(
            text = if (isFa) "مراکز منتخب درماتولوژی، سالن‌های زیبایی و دندانپزشکی"
            else "Premier Aesthetic & Dental Centers",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = White
        )
        Text(
            text = if (isFa) "سوئیت‌های بالینی مجهز به دستگاه‌های تایید شده در ${selectedDistrict.nameFa}"
            else "Curated medical spaces in ${selectedDistrict.nameEn}",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark,
            fontSize = 12.sp
        )

        Spacer(Modifier.height(12.dp))

        // Category Filter Segmented Control
        CategoryFilterRow(
            language = language,
            selectedCategory = selectedCategory,
            onSelectCategory = onSelectCategory
        )

        Spacer(Modifier.height(14.dp))

        // Venue Cards List
        if (filteredVenues.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                filteredVenues.forEach { venue ->
                    VenueCard(
                        language = language,
                        venue = venue,
                        onViewDetails = { onViewVenueDetails(venue) },
                        onBookVenue = { onBookVenue(venue) }
                    )
                }
            }
        } else {
            // Empty State
            Surface(
                color = ObsidianCard,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = if (isFa) "هیچ مرکزی با فیلتر انتخابی در این منطقه یافت نشد."
                        else "No facilities match the selected criteria.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryDark
                    )
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = { onSelectCategory(CategoryId.ALL) }) {
                        Text(
                            text = if (isFa) "نمایش همه دسته‌ها" else "Reset Category Filter",
                            color = GoldChampagne,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryFilterRow(
    language: Language,
    selectedCategory: CategoryId,
    onSelectCategory: (CategoryId) -> Unit
) {
    val isFa = language == Language.FA
    val categories = listOf(
        Triple(CategoryId.ALL, if (isFa) "همه دسته‌ها" else "All", Icons.Default.GridView),
        Triple(CategoryId.AESTHETICS, if (isFa) "پوست و لیزر" else "Aesthetics", Icons.Default.AutoAwesome),
        Triple(CategoryId.SALONS, if (isFa) "سالن‌های VIP" else "Salons", Icons.Default.ContentCut),
        Triple(CategoryId.DENTAL, if (isFa) "طراحی لبخند" else "Dental", Icons.Default.Mood)
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(categories) { (id, label, icon) ->
            val isSelected = selectedCategory == id
            Surface(
                color = if (isSelected) GoldChampagne else ObsidianCard,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) GoldChampagne else ObsidianBorder
                ),
                modifier = Modifier
                    .height(38.dp)
                    .clickable { onSelectCategory(id) }
                    .testTag("category_${id.name.lowercase()}")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = if (isSelected) ObsidianDark else TextSecondaryDark,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) ObsidianDark else TextPrimaryDark
                    )
                }
            }
        }
    }
}

@Composable
fun VenueCard(
    language: Language,
    venue: Venue,
    onViewDetails: () -> Unit,
    onBookVenue: () -> Unit
) {
    val isFa = language == Language.FA

    Surface(
        color = ObsidianCard,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("venue_card_${venue.id}")
    ) {
        Column {
            // Visual Card Image with Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Image(
                    painter = painterResource(id = venue.imageResId),
                    contentDescription = if (isFa) venue.nameFa else venue.nameEn,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Top badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        color = ObsidianDark.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                    ) {
                        Text(
                            text = if (isFa) venue.districtNameFa else venue.districtNameEn,
                            color = White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        color = EmeraldMuted,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldAccent.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = if (isFa) "${LocalizationUtils.formatNumber(venue.activeSlots, language)} یونیت آماده"
                            else "${venue.activeSlots} Slots Available",
                            color = EmeraldAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Bottom gradient strip with rating
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, ObsidianDark.copy(alpha = 0.9f))
                            )
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (venue.category) {
                            CategoryId.AESTHETICS -> if (isFa) "کلینیک پوست و لیزر" else "Skin & Aesthetics"
                            CategoryId.DENTAL -> if (isFa) "دندانپزشکی و لبخند" else "Dental Atelier"
                            CategoryId.SALONS -> if (isFa) "سالن زیبایی ممتاز" else "Luxury Salon"
                            else -> ""
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldChampagne,
                        fontWeight = FontWeight.Bold
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = AmberAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(3.dp))
                        Text(
                            text = LocalizationUtils.formatNumber(venue.rating, language),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            text = "(${LocalizationUtils.formatNumber(venue.reviewCount, language)})",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMutedDark,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Body Content
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isFa) venue.nameFa else venue.nameEn,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = White,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        Icons.Default.Shield,
                        contentDescription = "Verified",
                        tint = EmeraldAccent,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(Modifier.height(4.dp))
                Text(
                    text = if (isFa) venue.taglineFa else venue.taglineEn,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryDark,
                    fontSize = 12.sp,
                    maxLines = 2
                )

                Spacer(Modifier.height(10.dp))

                // Equipment highlights chips
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    venue.equipmentList.take(2).forEach { eq ->
                        Surface(
                            color = ObsidianSurface,
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder)
                        ) {
                            Text(
                                text = eq,
                                color = TextSecondaryDark,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Price and Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isFa) "نرخ پایه ساعتی:" else "Base Hourly:",
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

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = onViewDetails,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(
                                text = if (isFa) "اطلاعات" else "Details",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextPrimaryDark
                            )
                        }

                        Button(
                            onClick = onBookVenue,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldChampagne,
                                contentColor = ObsidianDark
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(
                                text = if (isFa) "رزرو" else "Reserve",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
