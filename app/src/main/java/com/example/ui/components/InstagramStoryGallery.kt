package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Language
import com.example.data.model.StoryHighlight
import com.example.data.model.StorySlide
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun InstagramStoryGallery(
    language: Language,
    highlights: List<StoryHighlight>,
    onOpenHighlight: (StoryHighlight) -> Unit
) {
    val isFa = language == Language.FA

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("story_gallery_section")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = GoldChampagne,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = if (isFa) "استوری و نتایج بالینی زنده" else "CLINICAL CASE STORIES",
                    style = MaterialTheme.typography.labelLarge,
                    color = GoldChampagne,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = if (isFa) "لمس جهت مشاهده" else "Tap to view",
                style = MaterialTheme.typography.labelSmall,
                color = TextMutedDark
            )
        }

        Spacer(Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(highlights) { highlight ->
                StoryHighlightItem(
                    highlight = highlight,
                    isFa = isFa,
                    onClick = { onOpenHighlight(highlight) }
                )
            }
        }
    }
}

@Composable
private fun StoryHighlightItem(
    highlight: StoryHighlight,
    isFa: Boolean,
    onClick: () -> Unit
) {
    // Elegant pulsing gold ring animation
    val infiniteTransition = rememberInfiniteTransition(label = "story_ring")
    val borderWidth by infiniteTransition.animateFloat(
        initialValue = 2f,
        targetValue = 3.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "border_width"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .testTag("story_item_${highlight.id}")
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(
                    Brush.sweepGradient(
                        listOf(
                            GoldChampagneLight,
                            GoldChampagne,
                            GoldChampagneDark,
                            GoldChampagneLight
                        )
                    )
                )
                .padding(borderWidth.dp)
                .clip(CircleShape)
                .background(ObsidianDark)
                .padding(2.dp)
                .clip(CircleShape)
        ) {
            Image(
                painter = painterResource(id = highlight.coverResId),
                contentDescription = if (isFa) highlight.titleFa else highlight.titleEn,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(Modifier.height(6.dp))
        Text(
            text = if (isFa) highlight.titleFa else highlight.titleEn,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = White,
            fontSize = 11.sp
        )
        Text(
            text = if (isFa) "${highlight.slides.size} اسلاید" else "${highlight.slides.size} slides",
            style = MaterialTheme.typography.labelSmall,
            color = TextMutedDark,
            fontSize = 9.sp
        )
    }
}

@Composable
fun StoryViewerDialog(
    language: Language,
    highlight: StoryHighlight,
    slideIndex: Int,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onClose: () -> Unit,
    onBookSlide: (StorySlide) -> Unit,
    onSendInquiry: (doctorName: String, msg: String) -> Unit
) {
    val isFa = language == Language.FA
    val slide = highlight.slides.getOrNull(slideIndex) ?: return
    var replyText by remember { mutableStateOf("") }
    var isPaused by remember { mutableStateOf(false) }

    // Automatic progress timer for active slide
    var progress by remember(slideIndex) { mutableFloatStateOf(0f) }

    LaunchedEffect(slideIndex, isPaused) {
        if (!isPaused) {
            val totalSteps = 100
            val stepDelay = (slide.durationSeconds * 1000L) / totalSteps
            while (progress < 1f) {
                delay(stepDelay)
                progress += 0.01f
            }
            onNext()
        }
    }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = ObsidianDark,
            modifier = Modifier
                .fillMaxSize()
                .testTag("story_viewer_dialog")
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Background Slide Image
                Image(
                    painter = painterResource(id = slide.imageResId),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Scrim Overlays
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    ObsidianDark.copy(alpha = 0.85f),
                                    Color.Transparent,
                                    ObsidianDark.copy(alpha = 0.95f)
                                )
                            )
                        )
                )

                // Invisible touch zones for tap next / prev
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { onPrev() }
                    )
                    Box(
                        modifier = Modifier
                            .weight(2f)
                            .fillMaxHeight()
                            .clickable { isPaused = !isPaused }
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { onNext() }
                    )
                }

                // Top Controls & Segmented Progress Bar
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .padding(top = 40.dp, start = 16.dp, end = 16.dp)
                ) {
                    // Segmented Progress Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        highlight.slides.forEachIndexed { idx, _ ->
                            val barFill = when {
                                idx < slideIndex -> 1f
                                idx == slideIndex -> progress
                                else -> 0f
                            }
                            LinearProgressIndicator(
                                progress = { barFill },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(3.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = GoldChampagne,
                                trackColor = White.copy(alpha = 0.25f)
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Doctor & Clinic Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, GoldChampagne, CircleShape)
                            ) {
                                Image(
                                    painter = painterResource(id = highlight.coverResId),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isFa) slide.doctorNameFa else slide.doctorNameEn,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = White
                                )
                                Text(
                                    text = if (isFa) slide.clinicNameFa else slide.clinicNameEn,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondaryDark
                                )
                            }
                        }

                        IconButton(
                            onClick = onClose,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(ObsidianCard.copy(alpha = 0.7f))
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = White)
                        }
                    }
                }

                // Bottom Content & Quick Actions
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                ) {
                    // Clinical Tag Badge
                    Surface(
                        color = GoldChampagneMuted,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = if (isFa) slide.clinicalTagFa else slide.clinicalTagEn,
                            color = GoldChampagneLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = if (isFa) slide.titleFa else slide.titleEn,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = if (isFa) slide.treatmentDetailsFa else slide.treatmentDetailsEn,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimaryDark,
                        lineHeight = 18.sp
                    )

                    Spacer(Modifier.height(14.dp))

                    // Action Button: Reserve Operatory for this procedure
                    Button(
                        onClick = { onBookSlide(slide) },
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
                            text = if (isFa) "رزرو سوئیت جهت این فرآیند بالینی" else "Book Operatory for Procedure",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // Send Inquiry Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = replyText,
                            onValueChange = { replyText = it },
                            placeholder = {
                                Text(
                                    if (isFa) "پیام مستقیم به پزشک..." else "Inquire with doctor...",
                                    color = TextMutedDark,
                                    fontSize = 12.sp
                                )
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = White,
                                unfocusedTextColor = White,
                                focusedContainerColor = ObsidianCard.copy(alpha = 0.8f),
                                unfocusedContainerColor = ObsidianCard.copy(alpha = 0.8f),
                                focusedBorderColor = GoldChampagne,
                                unfocusedBorderColor = ObsidianBorder
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = {
                                if (replyText.isNotBlank()) {
                                    onSendInquiry(
                                        if (isFa) slide.doctorNameFa else slide.doctorNameEn,
                                        replyText
                                    )
                                    replyText = ""
                                }
                            },
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(GoldChampagne)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = ObsidianDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
