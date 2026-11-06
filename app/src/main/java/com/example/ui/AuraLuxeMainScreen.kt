package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Language
import com.example.data.model.UserRole
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AuraLuxeMainScreen(
    viewModel: AuraLuxeViewModel = viewModel()
) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val userRole by viewModel.userRole.collectAsStateWithLifecycle()
    val selectedDistrict by viewModel.selectedDistrict.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val managedVenue by viewModel.managedVenue.collectAsStateWithLifecycle()
    val activeModal by viewModel.activeModal.collectAsStateWithLifecycle()
    val slots by viewModel.slots.collectAsStateWithLifecycle()
    val bookingRequests by viewModel.bookingRequests.collectAsStateWithLifecycle()
    val escrowTransactions by viewModel.escrowTransactions.collectAsStateWithLifecycle()
    val applications by viewModel.applications.collectAsStateWithLifecycle()
    val tickets by viewModel.tickets.collectAsStateWithLifecycle()
    val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()
    val toasts by viewModel.toasts.collectAsStateWithLifecycle()

    val selectedVenueForDetails by viewModel.selectedVenueForDetails.collectAsStateWithLifecycle()
    val preselectedBookingVenue by viewModel.preselectedBookingVenue.collectAsStateWithLifecycle()
    val preselectedBookingSlot by viewModel.preselectedBookingSlot.collectAsStateWithLifecycle()
    val submittedBookingRequest by viewModel.submittedBookingRequest.collectAsStateWithLifecycle()
    val activeStoryHighlight by viewModel.activeStoryHighlight.collectAsStateWithLifecycle()
    val activeStorySlideIndex by viewModel.activeStorySlideIndex.collectAsStateWithLifecycle()
    val selectedInvoiceTx by viewModel.selectedInvoiceTx.collectAsStateWithLifecycle()
    val activeEscrowTxForAction by viewModel.activeEscrowTxForAction.collectAsStateWithLifecycle()

    val isFa = language == Language.FA
    val layoutDirection = if (isFa) LayoutDirection.Rtl else LayoutDirection.Ltr
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Handle Predictive & Android System Back Press
    BackHandler(enabled = activeModal != ActiveModal.NONE || userRole != UserRole.PUBLIC) {
        if (activeModal != ActiveModal.NONE) {
            viewModel.closeModal()
        } else if (userRole != UserRole.PUBLIC) {
            viewModel.logout()
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ObsidianDark)
                .statusBarsPadding()
                .navigationBarsPadding()
                .testTag("aura_luxe_main_root")
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Sticky Navbar
                Navbar(
                    language = language,
                    userRole = userRole,
                    selectedDistrict = selectedDistrict,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onOpenLocationModal = { viewModel.openLocationSwitcher() },
                    onOpenOnboardingModal = { viewModel.openOnboarding() },
                    onOpenSupportModal = { viewModel.openSupport() },
                    onOpenAdminLoginModal = { viewModel.openAdminLogin() },
                    onLogout = { viewModel.logout() },
                    onOpenBookingModal = { viewModel.openBooking() }
                )

                // Main Content View Switching (Role-Driven)
                when (userRole) {
                    UserRole.SUPER_ADMIN -> {
                        SuperAdminPanel(
                            language = language,
                            applications = applications,
                            escrowTransactions = escrowTransactions,
                            auditLogs = auditLogs,
                            onExitSuperAdmin = { viewModel.logout() },
                            onUpdateAppStatus = { id, status -> viewModel.updateApplicationStatus(id, status) },
                            onConfirmArrival = { id -> viewModel.confirmPatientArrival(id, "SUPER-ADMIN-KYC") },
                            onOpenDigitalSignature = { tx -> viewModel.openDigitalSignaturePad(tx) },
                            onFastForwardGuarantee = { id -> viewModel.fastForwardGuarantee(id) },
                            onReleaseEscrow = { id -> viewModel.releaseEscrow(id) },
                            onViewInvoice = { tx -> viewModel.openInvoice(tx) }
                        )
                    }
                    UserRole.CLINIC_ADMIN -> {
                        ClinicAdminPortal(
                            language = language,
                            managedVenue = managedVenue,
                            allVenues = viewModel.venues,
                            slots = slots,
                            bookingRequests = bookingRequests,
                            onSwitchVenue = { v -> viewModel.setManagedVenue(v) },
                            onExitPortal = { viewModel.logout() },
                            onToggleSlotStatus = { id, status -> viewModel.toggleSlotStatus(id, status) },
                            onUpdateRequestStatus = { id, status -> viewModel.updateBookingRequestStatus(id, status) }
                        )
                    }
                    UserRole.PUBLIC -> {
                        // Public Luxury Marketplace
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Hero Section
                            item {
                                HeroSection(
                                    language = language,
                                    selectedDistrict = selectedDistrict,
                                    onOpenBookingModal = { viewModel.openBooking() },
                                    onScrollToSlots = {
                                        coroutineScope.launch {
                                            listState.animateScrollToItem(3)
                                        }
                                    }
                                )
                            }

                            // Instagram Story Highlights
                            item {
                                InstagramStoryGallery(
                                    language = language,
                                    highlights = viewModel.storyHighlights,
                                    onOpenHighlight = { hl -> viewModel.openStoryHighlight(hl) }
                                )
                            }

                            // Venue Catalog
                            item {
                                VenueCatalog(
                                    language = language,
                                    venues = viewModel.venues,
                                    selectedCategory = selectedCategory,
                                    selectedDistrict = selectedDistrict,
                                    onSelectCategory = { cat -> viewModel.setCategory(cat) },
                                    onViewVenueDetails = { v -> viewModel.openVenueDetails(v) },
                                    onBookVenue = { v -> viewModel.openBooking(venue = v) }
                                )
                            }

                            // Realtime Slots Live Matrix
                            item {
                                RealtimeSlotPanel(
                                    language = language,
                                    slots = slots,
                                    onToggleSlotStatus = { id, s -> viewModel.toggleSlotStatus(id, s) },
                                    onBookSlot = { s ->
                                        val v = viewModel.venues.find { it.id == s.venueId }
                                        viewModel.openBooking(venue = v, slot = s)
                                    }
                                )
                            }

                            // Before & After Transformation Slider
                            item {
                                BeforeAfterSlider(
                                    language = language,
                                    cases = viewModel.beforeAfterCases,
                                    onBookSimilarSuite = { c ->
                                        val targetCat = c.category
                                        val v = viewModel.venues.find { it.category == targetCat } ?: viewModel.venues[0]
                                        viewModel.openBooking(venue = v)
                                    }
                                )
                            }

                            // Partner Registration Callout Banner
                            item {
                                PartnerCalloutCard(
                                    language = language,
                                    onRegister = { viewModel.openOnboarding() }
                                )
                            }

                            // Luxury Footer
                            item {
                                LuxuryFooter(
                                    language = language,
                                    onRegister = { viewModel.openOnboarding() },
                                    onSupport = { viewModel.openSupport() },
                                    onLogin = { viewModel.openAdminLogin() }
                                )
                            }
                        }
                    }
                }
            }

            // MODALS LAYER (Guaranteed Mutually Exclusive)
            when (activeModal) {
                ActiveModal.BOOKING_REQUEST -> {
                    BookingRequestModal(
                        language = language,
                        venues = viewModel.venues,
                        preselectedVenue = preselectedBookingVenue,
                        preselectedSlot = preselectedBookingSlot,
                        submittedRequest = submittedBookingRequest,
                        onDismiss = { viewModel.closeModal() },
                        onSubmitRequest = { name, phone, srv, d, t, n ->
                            viewModel.submitBookingRequest(name, phone, srv, d, t, n)
                        }
                    )
                }
                ActiveModal.VENUE_DETAILS -> {
                    selectedVenueForDetails?.let { v ->
                        VenueDetailModal(
                            language = language,
                            venue = v,
                            onDismiss = { viewModel.closeModal() },
                            onBookVenue = {
                                viewModel.closeModal()
                                viewModel.openBooking(venue = v)
                            }
                        )
                    }
                }
                ActiveModal.LOCATION_SWITCHER -> {
                    LocationSwitcherModal(
                        language = language,
                        districts = viewModel.districts,
                        selectedDistrict = selectedDistrict,
                        onSelectDistrict = { d -> viewModel.setDistrict(d) },
                        onDismiss = { viewModel.closeModal() }
                    )
                }
                ActiveModal.ADMIN_LOGIN -> {
                    AdminLoginModal(
                        language = language,
                        onDismiss = { viewModel.closeModal() },
                        onLogin = { pin -> viewModel.loginWithPin(pin) }
                    )
                }
                ActiveModal.ONBOARDING -> {
                    OnboardingModal(
                        language = language,
                        onDismiss = { viewModel.closeModal() },
                        onSubmitApplication = { app -> viewModel.submitOnboarding(app) }
                    )
                }
                ActiveModal.SUPPORT_TICKETING -> {
                    SupportTicketingModal(
                        language = language,
                        tickets = tickets,
                        onDismiss = { viewModel.closeModal() },
                        onSendReply = { id, text -> viewModel.replyToTicket(id, text) }
                    )
                }
                ActiveModal.STORY_VIEWER -> {
                    activeStoryHighlight?.let { hl ->
                        StoryViewerDialog(
                            language = language,
                            highlight = hl,
                            slideIndex = activeStorySlideIndex,
                            onNext = { viewModel.nextStorySlide() },
                            onPrev = { viewModel.prevStorySlide() },
                            onClose = { viewModel.closeModal() },
                            onBookSlide = { slide ->
                                viewModel.closeModal()
                                val v = viewModel.venues.firstOrNull()
                                viewModel.openBooking(venue = v)
                            },
                            onSendInquiry = { doctor, msg ->
                                viewModel.showToast(
                                    com.example.data.model.ToastType.SUCCESS,
                                    if (isFa) "پیام ارسال شد" else "Inquiry Delivered",
                                    if (isFa) "پیام شما به $doctor تحویل داده شد." else "Sent to $doctor"
                                )
                            }
                        )
                    }
                }
                ActiveModal.DIGITAL_SIGNATURE -> {
                    activeEscrowTxForAction?.let { tx ->
                        DigitalSignaturePadDialog(
                            language = language,
                            transaction = tx,
                            onDismiss = { viewModel.closeModal() },
                            onSaveSignature = { notes, sig ->
                                viewModel.signOffProcedure(tx.id, notes, sig)
                            }
                        )
                    }
                }
                ActiveModal.INVOICE_DETAILS -> {
                    selectedInvoiceTx?.let { tx ->
                        InvoiceDialog(
                            language = language,
                            transaction = tx,
                            onDismiss = { viewModel.closeModal() }
                        )
                    }
                }
                ActiveModal.NONE -> { /* No modal */ }
            }

            // Top Floating Toasts Container
            ToastNotificationContainer(
                toasts = toasts,
                onDismiss = { id -> viewModel.dismissToast(id) }
            )
        }
    }
}

@Composable
private fun PartnerCalloutCard(
    language: Language,
    onRegister: () -> Unit
) {
    val isFa = language == Language.FA

    Surface(
        color = ObsidianSurface,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldChampagne.copy(alpha = 0.35f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldChampagne, modifier = Modifier.size(15.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    text = if (isFa) "شبکه همکاران بالینی و پزشکان" else "PARTNER CLINICAL NETWORK",
                    style = MaterialTheme.typography.labelSmall,
                    color = GoldChampagne,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = if (isFa) "مدیر کلینیک پوست، موسس سالن یا پزشک متخصص؟"
                else "Clinic Director, Salon Owner or Specialist Doctor?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = White
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (isFa) "یونیت‌ها و تخت‌های خالی مرکز خود را با بالاترین درآمد ساعتی و ضمانت اسکرو به اشتراک بگذارید."
                else "Monetize vacant operatory suites and welcome esteemed visiting practitioners.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondaryDark,
                lineHeight = 18.sp
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onRegister,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldChampagne,
                    contentColor = ObsidianDark
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(42.dp)
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    text = if (isFa) "ثبت‌نام مرکز یا پزشک مهمان" else "Register Center or Doctor",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun LuxuryFooter(
    language: Language,
    onRegister: () -> Unit,
    onSupport: () -> Unit,
    onLogin: () -> Unit
) {
    val isFa = language == Language.FA

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalDivider(color = ObsidianBorder)
        Spacer(Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(GoldChampagne),
                contentAlignment = Alignment.Center
            ) {
                Text("A", color = ObsidianDark, fontWeight = FontWeight.Black, fontSize = 14.sp)
            }
            Text("AURA LUXE", color = White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text("•", color = TextMutedDark)
            Text(
                if (isFa) "زیرساخت بالینی و بازارگاه سوئیت‌های لوکس" else "Luxury Clinical Infrastructure",
                color = TextSecondaryDark,
                fontSize = 11.sp
            )
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = "تهران: سعادت‌آباد، الهیه، زعفرانیه، نیاوران • کرج: گوهردشت، مهرشهر، فردیس",
            color = TextMutedDark,
            fontSize = 10.sp
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "© 2026 AURA LUXE • All Rights Reserved",
            color = TextMutedDark,
            fontSize = 9.sp
        )
    }
}
