package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.EscrowTransaction
import com.example.data.model.Language
import com.example.ui.theme.*
import com.example.ui.util.LocalizationUtils

@Composable
fun InvoiceDialog(
    language: Language,
    transaction: EscrowTransaction,
    onDismiss: () -> Unit
) {
    val isFa = language == Language.FA
    val vatToman = (transaction.platformFeeToman * 0.09).toLong()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = ObsidianSurface,
            shape = RoundedCornerShape(24.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.4f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("invoice_dialog")
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
                            Icon(
                                Icons.Default.Receipt,
                                contentDescription = null,
                                tint = GoldChampagne,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "AURA LUXE ESCROW CLEARING",
                                style = MaterialTheme.typography.labelSmall,
                                color = GoldChampagne,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = if (isFa) "صورتحساب رسمی تسویه مالیاتی B2B"
                            else "Official B2B Tax Invoice",
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

                // Metadata Details
                Surface(
                    color = ObsidianCard,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = if (isFa) "شماره قرارداد:" else "Contract #:", color = TextMutedDark, fontSize = 11.sp)
                            Text(text = transaction.contractCode, color = GoldChampagne, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = if (isFa) "کد پیگیری شاپرک (RRN):" else "Shaparak RRN:", color = TextMutedDark, fontSize = 11.sp)
                            Text(text = transaction.paymentRefId, color = TextPrimaryDark, fontSize = 11.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = if (isFa) "تاریخ صدور صورتحساب:" else "Issue Date:", color = TextMutedDark, fontSize = 11.sp)
                            Text(text = transaction.createdAt, color = TextPrimaryDark, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Parties Grid
                Text(
                    text = if (isFa) "طرفین قرارداد اسکرو و تسویه:" else "Contract Parties:",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldChampagne,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))

                Surface(
                    color = ObsidianCard,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = if (isFa) "طرف اول: شرکت فناوری زیرساخت کلینیکال اورا لوکس (عامل اسکرو)"
                            else "Party 1: AURA LUXE Clinical Escrow Agent",
                            color = TextPrimaryDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        HorizontalDivider(color = ObsidianBorder)
                        Text(
                            text = if (isFa) "طرف دوم ذینفع: ${transaction.venueName}"
                            else "Party 2 Beneficiary: ${transaction.venueName}",
                            color = TextPrimaryDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "پزشک معالج: ${transaction.practitionerName} (پروانه: ${transaction.licenseId})",
                            color = TextSecondaryDark,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "شماره شبا مقصد: ${transaction.shebaNumber}",
                            color = GoldChampagneLight,
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Breakdown Figures
                Surface(
                    color = ObsidianCard,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = if (isFa) "مبلغ کل پرداختی مراجع (مسدود در اسکرو):" else "Gross Escrow Payment:", color = TextSecondaryDark, fontSize = 11.sp)
                            Text(text = LocalizationUtils.formatPrice(transaction.grossAmountToman, language), color = White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = if (isFa) "کارمزد پلتفرم اسکرو (۸٪):" else "Platform Fee (8%):", color = RoseAccent, fontSize = 11.sp)
                            Text(text = "-${LocalizationUtils.formatPrice(transaction.platformFeeToman, language)}", color = RoseAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = if (isFa) "مالیات بر ارزش افزوده کارمزد (۹٪):" else "VAT on Fee (9%):", color = TextMutedDark, fontSize = 10.sp)
                            Text(text = LocalizationUtils.formatPrice(vatToman, language), color = TextMutedDark, fontSize = 10.sp)
                        }
                        HorizontalDivider(color = ObsidianBorder, modifier = Modifier.padding(vertical = 4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = if (isFa) "مبلغ خالص واریزی به شماره شبا:" else "Net Disbursement to Sheba:", color = EmeraldAccent, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                            Text(text = LocalizationUtils.formatPrice(transaction.clinicPayoutToman, language), color = EmeraldAccent, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Done Button
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldChampagne,
                        contentColor = ObsidianDark
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                ) {
                    Text(text = if (isFa) "بستن صورتحساب" else "Close Invoice", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
