package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Headphones
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
import com.example.data.model.Language
import com.example.data.model.SupportTicket
import com.example.ui.theme.*

@Composable
fun SupportTicketingModal(
    language: Language,
    tickets: List<SupportTicket>,
    onDismiss: () -> Unit,
    onSendReply: (ticketId: String, content: String) -> Unit
) {
    val isFa = language == Language.FA
    var selectedTicketId by remember { mutableStateOf(tickets.firstOrNull()?.id ?: "") }
    val activeTicket = tickets.find { it.id == selectedTicketId } ?: tickets.firstOrNull()
    var replyText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = ObsidianSurface,
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("support_ticketing_modal")
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
                            Icon(Icons.Default.Headphones, contentDescription = null, tint = GoldChampagne, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = if (isFa) "پشتیبانی اختصاصی B2B" else "CONFIDENTIAL B2B SUPPORT",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldChampagne,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = if (isFa) "مرکز تیکتینگ و پاسخگویی مراکز" else "Partner Support & Ticketing",
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

                // Ticket Selector Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    tickets.forEach { tkt ->
                        val isSelected = tkt.id == selectedTicketId
                        Surface(
                            color = if (isSelected) GoldChampagneMuted else ObsidianCard,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) GoldChampagne else ObsidianBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTicketId = tkt.id }
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = tkt.ticketNumber,
                                    color = if (isSelected) GoldChampagne else TextSecondaryDark,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = tkt.subject,
                                    color = White,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Thread Message History Box
                if (activeTicket != null) {
                    Surface(
                        color = ObsidianCard,
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(activeTicket.messages) { msg ->
                                Surface(
                                    color = if (msg.isStaff) ObsidianSurface else GoldChampagneMuted,
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (msg.isStaff) ObsidianBorder else GoldChampagne.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = msg.senderName,
                                                color = if (msg.isStaff) GoldChampagne else White,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = msg.timestamp,
                                                color = TextMutedDark,
                                                fontSize = 9.sp
                                            )
                                        }
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = msg.content,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextPrimaryDark,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    // Reply Form
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
                                    if (isFa) "پاسخ خود را بنویسید..." else "Write response...",
                                    color = TextMutedDark,
                                    fontSize = 11.sp
                                )
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = White, unfocusedTextColor = White,
                                focusedBorderColor = GoldChampagne, unfocusedBorderColor = ObsidianBorder
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = {
                                if (replyText.isNotBlank()) {
                                    onSendReply(activeTicket.id, replyText)
                                    replyText = ""
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(GoldChampagne)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = ObsidianDark)
                        }
                    }
                }
            }
        }
    }
}
