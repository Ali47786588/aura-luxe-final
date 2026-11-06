package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Language
import com.example.security.SecurityManager
import com.example.ui.theme.*

@Composable
fun AdminLoginModal(
    language: Language,
    onDismiss: () -> Unit,
    onLogin: (String) -> SecurityManager.AuthResult
) {
    val isFa = language == Language.FA
    var pin by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var lockoutSeconds by remember { mutableIntStateOf(0) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = ObsidianSurface,
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("admin_login_modal")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
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

                // Security Lock Icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(GoldChampagneMuted)
                        .border(1.dp, GoldChampagne.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = GoldChampagne,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    text = if (isFa) "ورود به پرتال مدیریت مراکز" else "Executive Management Access",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = White
                )

                Text(
                    text = if (isFa) "جهت ورود به پنل، گذرواژه امنیتی ۶ رقمی خود را وارد نمایید."
                    else "Enter your management security passcode to proceed.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryDark,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(Modifier.height(16.dp))

                // Passcode Input Field
                OutlinedTextField(
                    value = pin,
                    onValueChange = {
                        pin = it.take(8)
                        errorMessage = null
                    },
                    label = { Text(if (isFa) "گذرواژه امنیتی مدیریت:" else "Passcode:") },
                    singleLine = true,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle password",
                                tint = TextSecondaryDark
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = White,
                        unfocusedTextColor = White,
                        focusedBorderColor = GoldChampagne,
                        unfocusedBorderColor = ObsidianBorder
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_pin_input")
                )

                // Error / Lockout Display
                if (errorMessage != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = errorMessage!!,
                        color = RoseAccent,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Security Advisory Notice
                Text(
                    text = if (isFa) "دسترسی منحصر به مدیران بالینی و مدیران ارشد سیستم تایید شده می‌باشد."
                    else "Access restricted to verified clinical directors and system administrators.",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMutedDark,
                    fontSize = 10.sp
                )

                Spacer(Modifier.height(18.dp))

                // Sign In Button
                Button(
                    onClick = {
                        val result = onLogin(pin)
                        if (!result.success) {
                            errorMessage = result.errorMessage
                            lockoutSeconds = result.remainingSeconds
                        }
                    },
                    enabled = pin.isNotBlank() && lockoutSeconds == 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldChampagne,
                        contentColor = ObsidianDark
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("admin_login_submit_button")
                ) {
                    Text(
                        text = if (isFa) "احراز هویت و ورود" else "Authenticate & Enter",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
