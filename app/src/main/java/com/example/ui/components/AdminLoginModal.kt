package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
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
    onLogin: (String) -> SecurityManager.AuthResult,
    onSupabaseSignIn: ((email: String, pass: String) -> Unit)? = null,
    onSupabaseSignUp: ((email: String, pass: String, name: String) -> Unit)? = null,
    isSupabaseLoading: Boolean = false
) {
    val isFa = language == Language.FA
    var selectedAuthMode by remember { mutableStateOf("pin") } // "pin" or "supabase"

    // PIN state
    var pin by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var lockoutSeconds by remember { mutableIntStateOf(0) }

    // Supabase state
    var email by remember { mutableStateOf("") }
    var supabasePassword by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var isRegisterMode by remember { mutableStateOf(false) }

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

                // Security Lock / Cloud Icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(GoldChampagneMuted)
                        .border(1.dp, GoldChampagne.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (selectedAuthMode == "supabase") Icons.Default.Cloud else Icons.Default.Lock,
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

                // Auth Method Switcher Tabs (PIN vs Supabase Auth)
                Spacer(Modifier.height(12.dp))
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
                            .clip(RoundedCornerShape(9.dp))
                            .background(if (selectedAuthMode == "pin") GoldChampagne else androidx.compose.ui.graphics.Color.Transparent)
                            .clickable { selectedAuthMode = "pin" }
                            .padding(vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isFa) "گذرواژه سریع (PIN)" else "Passcode (PIN)",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selectedAuthMode == "pin") ObsidianDark else TextSecondaryDark,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(9.dp))
                            .background(if (selectedAuthMode == "supabase") GoldChampagne else androidx.compose.ui.graphics.Color.Transparent)
                            .clickable { selectedAuthMode = "supabase" }
                            .padding(vertical = 7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isFa) "احراز هویت Supabase" else "Supabase Cloud",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selectedAuthMode == "supabase") ObsidianDark else TextSecondaryDark,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                if (selectedAuthMode == "pin") {
                    // MODE 1: PIN AUTHENTICATION (ORIGINAL)
                    Text(
                        text = if (isFa) "جهت ورود به پنل، گذرواژه امنیتی ۶ رقمی خود را وارد نمایید."
                        else "Enter your management security passcode to proceed.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryDark,
                        fontSize = 11.sp
                    )

                    Spacer(Modifier.height(14.dp))

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

                    Text(
                        text = if (isFa) "کدهای آزمایشی: 7890 سوپرادمین | 2026 مدیر کلینیک"
                        else "Demo Credentials: 7890 SuperAdmin | 2026 Clinic Director",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMutedDark,
                        fontSize = 10.sp
                    )

                    Spacer(Modifier.height(16.dp))

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
                } else {
                    // MODE 2: SUPABASE AUTHENTICATION
                    Text(
                        text = if (isFa)
                            if (isRegisterMode) "ایجاد حساب کاربری جدید در سرور ابری Supabase" else "ورود با پست الکترونیک و گذرواژه Supabase"
                        else
                            if (isRegisterMode) "Create new account on Supabase Auth" else "Sign in with your Supabase credentials",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondaryDark,
                        fontSize = 11.sp
                    )

                    Spacer(Modifier.height(12.dp))

                    if (isRegisterMode) {
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text(if (isFa) "نام و نام خانوادگی:" else "Full Name:") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = White,
                                unfocusedTextColor = White,
                                focusedBorderColor = GoldChampagne,
                                unfocusedBorderColor = ObsidianBorder
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(if (isFa) "پست الکترونیک (ایمیل):" else "Email Address:") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = White,
                            unfocusedTextColor = White,
                            focusedBorderColor = GoldChampagne,
                            unfocusedBorderColor = ObsidianBorder
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = supabasePassword,
                        onValueChange = { supabasePassword = it },
                        label = { Text(if (isFa) "گذرواژه حساب:" else "Password:") },
                        singleLine = true,
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextButton(onClick = { isRegisterMode = !isRegisterMode }) {
                            Text(
                                text = if (isFa)
                                    if (isRegisterMode) "حساب کاربری دارید؟ ورود" else "حساب کاربری ندارید؟ ثبت‌نام"
                                else
                                    if (isRegisterMode) "Already have an account? Sign In" else "Need an account? Sign Up",
                                color = GoldChampagne,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (isRegisterMode) {
                                onSupabaseSignUp?.invoke(email, supabasePassword, fullName)
                            } else {
                                onSupabaseSignIn?.invoke(email, supabasePassword)
                            }
                        },
                        enabled = email.isNotBlank() && supabasePassword.length >= 6 && !isSupabaseLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldChampagne,
                            contentColor = ObsidianDark
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        if (isSupabaseLoading) {
                            CircularProgressIndicator(
                                color = ObsidianDark,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (isFa)
                                    if (isRegisterMode) "ثبت‌نام در Supabase" else "ورود با Supabase"
                                else
                                    if (isRegisterMode) "Register on Supabase" else "Sign In with Supabase",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
