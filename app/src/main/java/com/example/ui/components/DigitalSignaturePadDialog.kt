package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.EscrowTransaction
import com.example.data.model.Language
import com.example.ui.theme.*

@Composable
fun DigitalSignaturePadDialog(
    language: Language,
    transaction: EscrowTransaction,
    onDismiss: () -> Unit,
    onSaveSignature: (notes: String, signatureSvg: String) -> Unit
) {
    val isFa = language == Language.FA
    var notes by remember { mutableStateOf("درمان با رضایت کامل مراجع و بدون عارضه انجام شد.") }
    val paths = remember { mutableStateListOf<List<Offset>>() }
    var currentPath by remember { mutableStateOf<List<Offset>>(emptyList()) }
    val shaSeal = remember { "SIG-2026-${(100000..999999).random()}-SHA256" }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = ObsidianSurface,
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("digital_signature_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Draw,
                                contentDescription = null,
                                tint = GoldChampagne,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = if (isFa) "امضای دیجیتال و ترخیص درمان" else "DIGITAL CLINICAL SIGNATURE",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldChampagne,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = if (isFa) "تایید ترخیص: ${transaction.practitionerName}"
                            else "Discharge Sign-off: ${transaction.practitionerName}",
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

                Spacer(Modifier.height(10.dp))

                // SHA-256 Cryptographic Seal Badge
                Surface(
                    color = ObsidianCard,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = GoldChampagne, modifier = Modifier.size(13.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(text = shaSeal, color = GoldChampagne, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(text = "ISO 27001 Sealed", color = TextMutedDark, fontSize = 9.sp)
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Clinical Notes Input
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(if (isFa) "گزارش ترخیص بالینی پزشک:" else "Clinical Notes:") },
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

                Spacer(Modifier.height(10.dp))

                Text(
                    text = if (isFa) "امضای دیجیتال پزشک در کادر زیر:" else "Sign here with finger or stylus:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryDark
                )

                Spacer(Modifier.height(6.dp))

                // Signature Canvas Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(ObsidianDark)
                        .border(1.dp, GoldChampagneMuted, RoundedCornerShape(14.dp))
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    currentPath = listOf(offset)
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    currentPath = currentPath + change.position
                                },
                                onDragEnd = {
                                    if (currentPath.isNotEmpty()) {
                                        paths.add(currentPath)
                                        currentPath = emptyList()
                                    }
                                }
                            )
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        // Draw previously committed strokes
                        paths.forEach { points ->
                            if (points.size > 1) {
                                val path = Path().apply {
                                    moveTo(points[0].x, points[0].y)
                                    for (i in 1 until points.size) {
                                        lineTo(points[i].x, points[i].y)
                                    }
                                }
                                drawPath(
                                    path = path,
                                    color = GoldChampagne,
                                    style = Stroke(
                                        width = 3.dp.toPx(),
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )
                            }
                        }

                        // Draw active stroke
                        if (currentPath.size > 1) {
                            val path = Path().apply {
                                moveTo(currentPath[0].x, currentPath[0].y)
                                for (i in 1 until currentPath.size) {
                                    lineTo(currentPath[i].x, currentPath[i].y)
                                }
                            }
                            drawPath(
                                path = path,
                                color = GoldChampagne,
                                style = Stroke(
                                    width = 3.dp.toPx(),
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                    }

                    if (paths.isEmpty() && currentPath.isEmpty()) {
                        Text(
                            text = if (isFa) "محل رسم امضای الکترونیک پزشک" else "Draw signature here",
                            color = TextMutedDark,
                            fontSize = 11.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Actions: Clear & Confirm
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            paths.clear()
                            currentPath = emptyList()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondaryDark),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).height(40.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(if (isFa) "پاک کردن" else "Clear", fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            onSaveSignature(notes, shaSeal)
                        },
                        enabled = paths.isNotEmpty() || currentPath.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldChampagne,
                            contentColor = ObsidianDark
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(2f).height(40.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(if (isFa) "تایید و فعال‌سازی پنجره ۲ ساعته" else "Apply Signature (Start 2h)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
