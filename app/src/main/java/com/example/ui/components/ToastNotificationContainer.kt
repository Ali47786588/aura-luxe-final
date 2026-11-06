package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ToastAlert
import com.example.data.model.ToastType
import com.example.ui.theme.*

@Composable
fun ToastNotificationContainer(
    toasts: List<ToastAlert>,
    onDismiss: (String) -> Unit
) {
    if (toasts.isEmpty()) return

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp, start = 16.dp, end = 16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            toasts.forEach { toast ->
                ToastCard(toast = toast, onDismiss = { onDismiss(toast.id) })
            }
        }
    }
}

@Composable
private fun ToastCard(
    toast: ToastAlert,
    onDismiss: () -> Unit
) {
    val (iconColor, bgColor, borderColor, icon) = when (toast.type) {
        ToastType.SUCCESS -> Quad(EmeraldAccent, EmeraldMuted, EmeraldAccent.copy(alpha = 0.5f), Icons.Default.CheckCircle)
        ToastType.WARNING -> Quad(AmberAccent, AmberMuted, AmberAccent.copy(alpha = 0.5f), Icons.Default.Warning)
        ToastType.ERROR -> Quad(RoseAccent, RoseMuted, RoseAccent.copy(alpha = 0.5f), Icons.Default.Error)
        ToastType.INFO -> Quad(SkyAccent, SkyMuted, SkyAccent.copy(alpha = 0.5f), Icons.Default.Info)
    }

    Surface(
        color = ObsidianSurface,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        tonalElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = toast.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = White,
                    fontSize = 12.sp
                )
                Text(
                    text = toast.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryDark,
                    fontSize = 11.sp
                )
            }
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = TextMutedDark, modifier = Modifier.size(14.dp))
            }
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
