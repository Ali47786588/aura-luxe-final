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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
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
import com.example.data.model.*
import com.example.ui.theme.*
import com.example.ui.util.LocalizationUtils

@Composable
fun SuperAdminPanel(
    language: Language,
    applications: List<OnboardingApplication>,
    escrowTransactions: List<EscrowTransaction>,
    auditLogs: List<SecurityAuditLog>,
    onExitSuperAdmin: () -> Unit,
    onUpdateAppStatus: (appId: String, status: ApplicationStatus) -> Unit,
    onConfirmArrival: (escrowId: String) -> Unit,
    onOpenDigitalSignature: (EscrowTransaction) -> Unit,
    onFastForwardGuarantee: (escrowId: String) -> Unit,
    onReleaseEscrow: (escrowId: String) -> Unit,
    onViewInvoice: (EscrowTransaction) -> Unit
) {
    val isFa = language == Language.FA
    var selectedTab by remember { mutableStateOf("approvals") } // "approvals" | "escrow" | "audit"

    val pendingApps = applications.filter { it.status == ApplicationStatus.PENDING }
    val totalGMVToman = 248600000L
    val totalPlatformCutToman = 19888000L

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("super_admin_panel"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Master Console Header
        item {
            Surface(
                color = ObsidianSurface,
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(GoldChampagne),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Security,
                                    contentDescription = null,
                                    tint = ObsidianDark,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isFa) "پنل حاکمیت مرکزی سوپرادمین" else "SUPERADMIN MASTER GOVERNANCE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GoldChampagne,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isFa) "نظارت عالیه بالینی، تسویه اسکرو و ممیزی" else "Master Regulatory & Escrow Engine",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = White
                                )
                            }
                        }

                        IconButton(
                            onClick = onExitSuperAdmin,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(ObsidianCard)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Exit", tint = RoseAccent)
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // 4-Column Platform KPIs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        KpiBox(
                            label = if (isFa) "حجم ناخالص (GMV)" else "Gross Volume",
                            value = LocalizationUtils.formatPrice(totalGMVToman, language),
                            modifier = Modifier.weight(1f)
                        )
                        KpiBox(
                            label = if (isFa) "کارمزد پلتفرم (۸٪)" else "Commission (8%)",
                            value = LocalizationUtils.formatPrice(totalPlatformCutToman, language),
                            modifier = Modifier.weight(1f)
                        )
                        KpiBox(
                            label = if (isFa) "در صف احراز هویت" else "KYC Queue",
                            value = "${pendingApps.size} مرکز",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Sub-Tabs Navigation
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(ObsidianSurface)
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(14.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TabButton(
                    label = if (isFa) "تایید مراکز (${pendingApps.size})" else "Approvals (${pendingApps.size})",
                    isSelected = selectedTab == "approvals",
                    onClick = { selectedTab = "approvals" },
                    modifier = Modifier.weight(1f)
                )
                TabButton(
                    label = if (isFa) "چرخه اسکرو (۴ مرحله)" else "4-Step Escrow",
                    isSelected = selectedTab == "escrow",
                    onClick = { selectedTab = "escrow" },
                    modifier = Modifier.weight(1f)
                )
                TabButton(
                    label = if (isFa) "لاگ‌های امنیتی" else "Security Logs",
                    isSelected = selectedTab == "audit",
                    onClick = { selectedTab = "audit" },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // TAB 1: Partner Approvals Queue
        if (selectedTab == "approvals") {
            item {
                Text(
                    text = if (isFa) "درخواست‌های ثبت‌نام مراکز بالینی در انتظار بررسی:"
                    else "Pending Partner Registration Queue:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }

            items(applications) { app ->
                PartnerApplicationCard(
                    language = language,
                    application = app,
                    onApprove = { onUpdateAppStatus(app.id, ApplicationStatus.APPROVED) },
                    onReject = { onUpdateAppStatus(app.id, ApplicationStatus.REJECTED) }
                )
            }
        }

        // TAB 2: 4-Step Escrow Ledger & Milestone Enforcement
        if (selectedTab == "escrow") {
            item {
                Text(
                    text = if (isFa) "دفتر کل اسکرو و کنترل مراحل ۴‌گانه تسویه:"
                    else "4-Step Escrow Milestone Clearing Ledger:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }

            items(escrowTransactions) { tx ->
                EscrowTransactionCard(
                    language = language,
                    tx = tx,
                    onConfirmArrival = { onConfirmArrival(tx.id) },
                    onOpenDigitalSignature = { onOpenDigitalSignature(tx) },
                    onFastForwardGuarantee = { onFastForwardGuarantee(tx.id) },
                    onReleaseEscrow = { onReleaseEscrow(tx.id) },
                    onViewInvoice = { onViewInvoice(tx) }
                )
            }
        }

        // TAB 3: System Security & Audit Logs
        if (selectedTab == "audit") {
            item {
                Text(
                    text = if (isFa) "گزارش‌های ممیزی امنیتی غیرقابل دستکاری:"
                    else "Immutable Security Audit Records:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }

            items(auditLogs) { log ->
                AuditLogItem(language = language, log = log)
            }
        }
    }
}

@Composable
private fun KpiBox(label: String, value: String, modifier: Modifier) {
    Surface(
        color = ObsidianCard,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextMutedDark, fontSize = 9.sp)
            Spacer(Modifier.height(2.dp))
            Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = GoldChampagne, fontSize = 12.sp)
        }
    }
}

@Composable
private fun TabButton(label: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) GoldChampagne else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) ObsidianDark else TextSecondaryDark,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun PartnerApplicationCard(
    language: Language,
    application: OnboardingApplication,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val isFa = language == Language.FA
    val isPending = application.status == ApplicationStatus.PENDING

    Surface(
        color = ObsidianCard,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isPending) AmberAccent.copy(alpha = 0.35f) else ObsidianBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = application.businessName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = White
                    )
                    Text(
                        text = "مدیر/پزشک: ${application.applicantName} • ${application.phoneNumber}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondaryDark
                    )
                }

                Surface(
                    color = when (application.status) {
                        ApplicationStatus.PENDING -> AmberMuted
                        ApplicationStatus.APPROVED -> EmeraldMuted
                        ApplicationStatus.REJECTED -> RoseMuted
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = when (application.status) {
                            ApplicationStatus.PENDING -> if (isFa) "در صف بررسی" else "Pending"
                            ApplicationStatus.APPROVED -> if (isFa) "تایید شده" else "Approved"
                            ApplicationStatus.REJECTED -> if (isFa) "رد شده" else "Rejected"
                        },
                        color = when (application.status) {
                            ApplicationStatus.PENDING -> AmberAccent
                            ApplicationStatus.APPROVED -> EmeraldAccent
                            ApplicationStatus.REJECTED -> RoseAccent
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = "موقعیت: ${application.cityAndDistrict} • ظرفیت: ${application.chairsOrBedsCount} یونیت",
                style = MaterialTheme.typography.labelSmall,
                color = GoldChampagne
            )

            if (application.shortDescription.isNotBlank()) {
                Text(
                    text = "تجهیزات و مشخصات: ${application.shortDescription}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimaryDark,
                    fontSize = 11.sp
                )
            }

            if (isPending) {
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldChampagne, contentColor = ObsidianDark),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(34.dp)
                    ) {
                        Text(if (isFa) "تایید و اعطای پروانه" else "Approve Center", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = onReject,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = RoseAccent),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RoseAccent.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(34.dp)
                    ) {
                        Text(if (isFa) "رد درخواست" else "Reject", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun EscrowTransactionCard(
    language: Language,
    tx: EscrowTransaction,
    onConfirmArrival: () -> Unit,
    onOpenDigitalSignature: () -> Unit,
    onFastForwardGuarantee: () -> Unit,
    onReleaseEscrow: () -> Unit,
    onViewInvoice: () -> Unit
) {
    val isFa = language == Language.FA

    Surface(
        color = ObsidianCard,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tx.contractCode,
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldChampagne,
                    fontWeight = FontWeight.Bold
                )

                // Milestone step pill
                Surface(
                    color = when (tx.milestoneStep) {
                        1 -> AmberMuted
                        2 -> SkyMuted
                        3 -> Color(0x33A855F7)
                        else -> EmeraldMuted
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = when (tx.milestoneStep) {
                            1 -> if (isFa) "گام ۱: وجوه مسدود در اسکرو" else "1. Held in Escrow"
                            2 -> if (isFa) "گام ۲: احراز هویت و ورود بیمار" else "2. Patient Arrived"
                            3 -> if (isFa) "گام ۳: اتمام درمان و امضای پزشک" else "3. Procedure Done"
                            else -> if (isFa) "گام ۴: تسویه قطعی به شبا" else "4. Released"
                        },
                        color = when (tx.milestoneStep) {
                            1 -> AmberAccent
                            2 -> SkyAccent
                            3 -> Color(0xFFC084FC)
                            else -> EmeraldAccent
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = "${tx.practitionerName} • ${tx.slotName}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = White
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "ناخالص: ${LocalizationUtils.formatPrice(tx.grossAmountToman, language)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondaryDark
                )
                Text(
                    text = "خالص کلینیک: ${LocalizationUtils.formatPrice(tx.clinicPayoutToman, language)}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldAccent
                )
            }

            Spacer(Modifier.height(10.dp))

            // Action based on 4 milestones
            when (tx.milestoneStep) {
                1 -> {
                    Button(
                        onClick = onConfirmArrival,
                        colors = ButtonDefaults.buttonColors(containerColor = SkyAccent, contentColor = ObsidianDark),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(36.dp)
                    ) {
                        Text(if (isFa) "تایید ورود و کد ملی بیمار (گام ۲)" else "Confirm Patient Check-In", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
                2 -> {
                    Button(
                        onClick = onOpenDigitalSignature,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA855F7), contentColor = White),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(36.dp)
                    ) {
                        Text(if (isFa) "ثبت امضای دیجیتال و ترخیص درمان (گام ۳)" else "Doctor Digital Sign-Off", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
                3 -> {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (tx.guaranteeMinutesRemaining > 0) {
                            Text(
                                text = if (isFa) "پنجره تضمین کیفیت: ${tx.guaranteeMinutesRemaining} دقیقه باقی‌مانده"
                                else "Guarantee Window: ${tx.guaranteeMinutesRemaining}m remaining",
                                style = MaterialTheme.typography.labelSmall,
                                color = AmberAccent,
                                fontSize = 10.sp
                            )
                            Button(
                                onClick = onFastForwardGuarantee,
                                colors = ButtonDefaults.buttonColors(containerColor = ObsidianSurface, contentColor = GoldChampagne),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagneMuted),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().height(34.dp)
                            ) {
                                Text(if (isFa) "تسریع اتمام پنجره ۲ ساعته تضمین (تست)" else "Fast-Forward 2h Window (Demo)", fontSize = 10.sp)
                            }
                        } else {
                            Button(
                                onClick = onReleaseEscrow,
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldAccent, contentColor = ObsidianDark),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().height(36.dp)
                            ) {
                                Text(if (isFa) "تسویه وجه اسکرو به شماره شبا بانک (گام ۴)" else "Release Payout to Sheba IBAN", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
                4 -> {
                    OutlinedButton(
                        onClick = onViewInvoice,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldChampagne),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagneMuted),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(34.dp)
                    ) {
                        Text(if (isFa) "مشاهده صورتحساب مالیاتی B2B" else "View Official Tax Invoice", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun AuditLogItem(language: Language, log: SecurityAuditLog) {
    val isFa = language == Language.FA

    Surface(
        color = ObsidianCard,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = when (log.severity) {
                        Severity.INFO -> SkyMuted
                        Severity.WARNING -> AmberMuted
                        Severity.CRITICAL -> RoseMuted
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${log.type.uppercase()} • ${log.severity.name}",
                        color = when (log.severity) {
                            Severity.INFO -> SkyAccent
                            Severity.WARNING -> AmberAccent
                            Severity.CRITICAL -> RoseAccent
                        },
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = "${log.timestamp} • ${log.ip}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMutedDark,
                    fontSize = 9.sp
                )
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = if (isFa) log.titleFa else log.titleEn,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = White
            )

            Text(
                text = log.details,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondaryDark,
                fontSize = 11.sp
            )
        }
    }
}
