package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BeforeAfterCase
import com.example.data.model.Language
import com.example.ui.theme.*

@Composable
fun BeforeAfterSlider(
    language: Language,
    cases: List<BeforeAfterCase>,
    onBookSimilarSuite: (BeforeAfterCase) -> Unit
) {
    val isFa = language == Language.FA
    var selectedCaseId by remember { mutableStateOf(cases.firstOrNull()?.id ?: "") }
    val currentCase = cases.find { it.id == selectedCaseId } ?: cases.first()

    // Slider split percentage (0f to 1f)
    var splitFraction by remember { mutableFloatStateOf(0.5f) }
    var containerWidthPx by remember { mutableFloatStateOf(1000f) }

    // Sepia/Clinical matrix for Before layer
    val beforeColorMatrix = remember {
        ColorMatrix().apply {
            setToSaturation(0.7f)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(ObsidianCard)
            .border(1.dp, ObsidianBorder, RoundedCornerShape(24.dp))
            .padding(16.dp)
            .testTag("before_after_section")
    ) {
        // Section Header & Case Switcher
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = GoldChampagne,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (isFa) "نتایج بالینی و ترنسفورمیشن" else "CINEMATIC OUTCOMES",
                        style = MaterialTheme.typography.labelLarge,
                        color = GoldChampagne,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = if (isFa) "مقایسه قبل و بعد درمان" else "Interactive Before / After",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }

            // Case Switcher Tabs
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(ObsidianSurface)
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                cases.forEach { c ->
                    val isSelected = c.id == selectedCaseId
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) GoldChampagne else Color.Transparent)
                            .clickable {
                                selectedCaseId = c.id
                                splitFraction = 0.5f
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isFa) c.titleFa.split(" ").take(2).joinToString(" ")
                            else c.titleEn.split(" ").take(2).joinToString(" "),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) ObsidianDark else TextSecondaryDark,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Dual-Layer Split Interactive Slider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, GoldChampagneMuted, RoundedCornerShape(20.dp))
                .onSizeChanged { containerWidthPx = it.width.toFloat() }
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        change.consume()
                        val newFraction = change.position.x / containerWidthPx
                        splitFraction = newFraction.coerceIn(0.05f, 0.95f)
                    }
                }
                .testTag("before_after_slider_canvas")
        ) {
            // Layer 1: After Image (Full background)
            Image(
                painter = painterResource(id = currentCase.imageResId),
                contentDescription = "After",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Layer 2: Before Image (Clipped to split fraction with clinical color filter)
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(splitFraction)
                    .clip(RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp))
            ) {
                Image(
                    painter = painterResource(id = currentCase.imageResId),
                    contentDescription = "Before",
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.colorMatrix(beforeColorMatrix),
                    modifier = Modifier
                        .fillMaxHeight()
                        .width((containerWidthPx / 2.6f).dp) // maintain full scale across clip
                )
            }

            // Draggable Hairline Divider & Thumb
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(2.dp)
                    .background(GoldChampagne)
                    .offset { IntOffset(x = (splitFraction * containerWidthPx).toInt(), y = 0) }
            ) {
                // Tactile Circle Thumb
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ObsidianDark)
                        .border(2.dp, GoldChampagne, CircleShape)
                        .align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.CompareArrows,
                        contentDescription = "Slide",
                        tint = GoldChampagne,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Before / After Floating Badges
            Surface(
                color = ObsidianDark.copy(alpha = 0.8f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
            ) {
                Text(
                    text = if (isFa) "قبل (Before)" else "BEFORE",
                    color = TextSecondaryDark,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Surface(
                color = GoldChampagneMuted,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.5f)),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
            ) {
                Text(
                    text = if (isFa) "بعد (After)" else "AFTER",
                    color = GoldChampagneLight,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        // Treatment Title & Procedure Dossier
        Text(
            text = if (isFa) currentCase.titleFa else currentCase.titleEn,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = White
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = if (isFa) currentCase.treatmentFa else currentCase.treatmentEn,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondaryDark,
            lineHeight = 18.sp
        )

        Spacer(Modifier.height(10.dp))

        // Clinical Metrics Grid
        Surface(
            color = ObsidianSurface,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                val metrics = if (isFa) currentCase.metricsFa else currentCase.metricsEn
                metrics.forEach { m ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = m.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondaryDark
                        )
                        Text(
                            text = m.value,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = GoldChampagneLight
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // Doctor Quote & Notes
        Surface(
            color = ObsidianSurface,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = if (isFa) "پزشک معالج: ${currentCase.doctorNameFa}"
                    else "Attending Practitioner: ${currentCase.doctorNameEn}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = GoldChampagne
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = if (isFa) currentCase.notesFa else currentCase.notesEn,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimaryDark,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Book Similar Suite Button
        Button(
            onClick = { onBookSimilarSuite(currentCase) },
            colors = ButtonDefaults.buttonColors(
                containerColor = GoldChampagne,
                contentColor = ObsidianDark
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
        ) {
            Text(
                text = if (isFa) "رزرو سوئیت بالینی مشابه" else "Book Similar Operatory",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}
