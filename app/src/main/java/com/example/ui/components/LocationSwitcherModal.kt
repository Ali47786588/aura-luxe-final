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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
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
import com.example.data.model.District
import com.example.data.model.Language
import com.example.ui.theme.*
import com.example.ui.util.LocalizationUtils

@Composable
fun LocationSwitcherModal(
    language: Language,
    districts: List<District>,
    selectedDistrict: District,
    onSelectDistrict: (District) -> Unit,
    onDismiss: () -> Unit
) {
    val isFa = language == Language.FA
    var selectedCityTab by remember { mutableStateOf(selectedDistrict.cityId) } // "tehran" | "karaj"
    val filteredDistricts = districts.filter { it.cityId == selectedCityTab }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = ObsidianSurface,
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("location_switcher_modal")
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
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = GoldChampagne, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = if (isFa) "انتخاب محدوده جغرافیایی" else "SELECT CLINICAL DISTRICT",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldChampagne,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = if (isFa) "مناطق تحت پوشش تهران و کرج" else "Tehran & Karaj Hubs",
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

                // City Switcher Segmented Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ObsidianCard)
                        .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedCityTab == "tehran") GoldChampagne else Color.Transparent)
                            .clickable { selectedCityTab = "tehran" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isFa) "تهران (مناطق ۱ و ۲)" else "Tehran (Dist. 1 & 2)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (selectedCityTab == "tehran") ObsidianDark else TextSecondaryDark
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (selectedCityTab == "karaj") GoldChampagne else Color.Transparent)
                            .clickable { selectedCityTab = "karaj" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isFa) "کرج و استان البرز" else "Karaj & Alborz",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (selectedCityTab == "karaj") ObsidianDark else TextSecondaryDark
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Districts List
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    filteredDistricts.forEach { dist ->
                        val isSelected = dist.id == selectedDistrict.id
                        Surface(
                            color = if (isSelected) GoldChampagneMuted else ObsidianCard,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) GoldChampagne else ObsidianBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectDistrict(dist) }
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
                                        text = if (isFa) dist.nameFa else dist.nameEn,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) GoldChampagne else White
                                    )
                                    Text(
                                        text = dist.popularRoad,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondaryDark,
                                        fontSize = 10.sp
                                    )
                                    Text(
                                        text = "${LocalizationUtils.formatNumber(dist.activeClinicsCount, language)} کلینیک فعال • میانگین: ${LocalizationUtils.formatPrice(dist.averageHourlyToman, language)}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMutedDark,
                                        fontSize = 9.sp
                                    )
                                }

                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(GoldChampagne),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = ObsidianDark, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
