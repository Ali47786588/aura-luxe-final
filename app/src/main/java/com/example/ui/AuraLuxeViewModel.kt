package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.remote.supabase.SupabaseAuthResult
import com.example.data.remote.supabase.SupabaseSession
import com.example.data.remote.supabase.SupabaseUser
import com.example.data.repository.AuraRepository
import com.example.data.repository.IAuraRepository
import com.example.data.repository.ISupabaseSyncGateway
import com.example.security.SecurityManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ActiveModal {
    NONE,
    LOCATION_SWITCHER,
    BOOKING_REQUEST,
    VENUE_DETAILS,
    ADMIN_LOGIN,
    ONBOARDING,
    SUPPORT_TICKETING,
    STORY_VIEWER,
    DIGITAL_SIGNATURE,
    INVOICE_DETAILS
}

class AuraLuxeViewModel(
    private val repository: IAuraRepository = AuraRepository()
) : ViewModel() {

    // Offline-first indicator (Free offline mode without paid services)
    private val _isOfflineMode = MutableStateFlow(true)
    val isOfflineMode: StateFlow<Boolean> = _isOfflineMode.asStateFlow()

    val districts = repository.districts
    val venues = repository.venues
    val beforeAfterCases = repository.beforeAfterCases
    val storyHighlights = repository.storyHighlights

    val slots: StateFlow<List<RealtimeSlot>> = repository.slots
    val bookingRequests: StateFlow<List<BookingRequest>> = repository.bookingRequests
    val escrowTransactions: StateFlow<List<EscrowTransaction>> = repository.escrowTransactions
    val applications: StateFlow<List<OnboardingApplication>> = repository.applications
    val tickets: StateFlow<List<SupportTicket>> = repository.tickets
    val auditLogs: StateFlow<List<SecurityAuditLog>> = repository.auditLogs

    val supabaseSession: StateFlow<SupabaseSession?> = repository.supabaseGateway.currentSession
    val supabaseUser: StateFlow<SupabaseUser?> = repository.supabaseGateway.currentUser

    private val _isSupabaseAuthLoading = MutableStateFlow(false)
    val isSupabaseAuthLoading: StateFlow<Boolean> = _isSupabaseAuthLoading.asStateFlow()

    private val _language = MutableStateFlow(Language.FA)
    val language: StateFlow<Language> = _language.asStateFlow()

    private val _userRole = MutableStateFlow(UserRole.PUBLIC)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    private val _sessionToken = MutableStateFlow<String?>(null)
    val sessionToken: StateFlow<String?> = _sessionToken.asStateFlow()

    private val _selectedDistrict = MutableStateFlow(districts[0])
    val selectedDistrict: StateFlow<District> = _selectedDistrict.asStateFlow()

    private val _selectedCategory = MutableStateFlow(CategoryId.ALL)
    val selectedCategory: StateFlow<CategoryId> = _selectedCategory.asStateFlow()

    private val _managedVenue = MutableStateFlow(venues[0])
    val managedVenue: StateFlow<Venue> = _managedVenue.asStateFlow()

    // Modals & Inspection Overlays
    private val _activeModal = MutableStateFlow(ActiveModal.NONE)
    val activeModal: StateFlow<ActiveModal> = _activeModal.asStateFlow()

    private val _selectedVenueForDetails = MutableStateFlow<Venue?>(null)
    val selectedVenueForDetails: StateFlow<Venue?> = _selectedVenueForDetails.asStateFlow()

    private val _preselectedBookingVenue = MutableStateFlow<Venue?>(null)
    val preselectedBookingVenue: StateFlow<Venue?> = _preselectedBookingVenue.asStateFlow()

    private val _preselectedBookingSlot = MutableStateFlow<RealtimeSlot?>(null)
    val preselectedBookingSlot: StateFlow<RealtimeSlot?> = _preselectedBookingSlot.asStateFlow()

    private val _submittedBookingRequest = MutableStateFlow<BookingRequest?>(null)
    val submittedBookingRequest: StateFlow<BookingRequest?> = _submittedBookingRequest.asStateFlow()
    private val _isBookingSubmitting = MutableStateFlow(false)
    val isBookingSubmitting: StateFlow<Boolean> = _isBookingSubmitting.asStateFlow()

    private val _activeStoryHighlight = MutableStateFlow<StoryHighlight?>(null)
    val activeStoryHighlight: StateFlow<StoryHighlight?> = _activeStoryHighlight.asStateFlow()

    private val _activeStorySlideIndex = MutableStateFlow(0)
    val activeStorySlideIndex: StateFlow<Int> = _activeStorySlideIndex.asStateFlow()

    private val _selectedInvoiceTx = MutableStateFlow<EscrowTransaction?>(null)
    val selectedInvoiceTx: StateFlow<EscrowTransaction?> = _selectedInvoiceTx.asStateFlow()

    private val _activeEscrowTxForAction = MutableStateFlow<EscrowTransaction?>(null)
    val activeEscrowTxForAction: StateFlow<EscrowTransaction?> = _activeEscrowTxForAction.asStateFlow()

    private val _toasts = MutableStateFlow<List<ToastAlert>>(emptyList())
    val toasts: StateFlow<List<ToastAlert>> = _toasts.asStateFlow()

    fun toggleLanguage() {
        _language.value = if (_language.value == Language.FA) Language.EN else Language.FA
        showToast(
            ToastType.INFO,
            if (_language.value == Language.FA) "تغییر زبان" else "Language Changed",
            if (_language.value == Language.FA) "زبان برنامه به فارسی تنظیم شد." else "Language set to English."
        )
    }

    fun setDistrict(district: District) {
        _selectedDistrict.value = district
        closeModal()
    }

    fun setCategory(category: CategoryId) {
        _selectedCategory.value = category
    }

    fun setManagedVenue(venue: Venue) {
        _managedVenue.value = venue
    }

    fun openVenueDetails(venue: Venue) {
        _selectedVenueForDetails.value = venue
        _activeModal.value = ActiveModal.VENUE_DETAILS
    }

    fun openBooking(venue: Venue? = null, slot: RealtimeSlot? = null) {
        val selectedVenue = venue ?: venues.firstOrNull()

        if (selectedVenue == null) {
            showToast(
                ToastType.WARNING,
                if (_language.value == Language.FA) "امکان ثبت نوبت وجود ندارد" else "Booking Unavailable",
                if (_language.value == Language.FA) "در حال حاضر مرکزی برای ثبت نوبت موجود نیست." else "No venue is currently available for booking."
            )
            return
        }

        _preselectedBookingVenue.value = selectedVenue
        _preselectedBookingSlot.value = slot
        _submittedBookingRequest.value = null
        _activeModal.value = ActiveModal.BOOKING_REQUEST
    }

    fun openStoryHighlight(highlight: StoryHighlight) {
        _activeStoryHighlight.value = highlight
        _activeStorySlideIndex.value = 0
        _activeModal.value = ActiveModal.STORY_VIEWER
    }

    fun nextStorySlide() {
        val current = _activeStoryHighlight.value ?: return
        if (_activeStorySlideIndex.value < current.slides.size - 1) {
            _activeStorySlideIndex.value += 1
        } else {
            closeModal()
        }
    }

    fun prevStorySlide() {
        if (_activeStorySlideIndex.value > 0) {
            _activeStorySlideIndex.value -= 1
        }
    }

    fun openAdminLogin() {
        _activeModal.value = ActiveModal.ADMIN_LOGIN
    }

    fun openOnboarding() {
        _activeModal.value = ActiveModal.ONBOARDING
    }

    fun openSupport() {
        _activeModal.value = ActiveModal.SUPPORT_TICKETING
    }

    fun openLocationSwitcher() {
        _activeModal.value = ActiveModal.LOCATION_SWITCHER
    }

    fun openInvoice(tx: EscrowTransaction) {
        _selectedInvoiceTx.value = tx
        _activeModal.value = ActiveModal.INVOICE_DETAILS
    }

    fun openDigitalSignaturePad(tx: EscrowTransaction) {
        _activeEscrowTxForAction.value = tx
        _activeModal.value = ActiveModal.DIGITAL_SIGNATURE
    }

    fun closeModal() {
        _activeModal.value = ActiveModal.NONE
    }

    fun submitBookingRequest(
        fullName: String,
        phoneNumber: String,
        desiredService: String,
        suggestedDate: String,
        suggestedTime: String,
        notes: String
    ) {
        if (!_isBookingSubmitting.compareAndSet(false, true)) return

        viewModelScope.launch {
            try {
                val venue = _preselectedBookingVenue.value ?: venues.firstOrNull()
                    ?: throw IllegalStateException("No booking venue is available")
                val slot = _preselectedBookingSlot.value

                val req = repository.submitBookingRequest(
                    fullName = SecurityManager.sanitizeInput(fullName),
                    phoneNumber = SecurityManager.sanitizeInput(phoneNumber),
                    desiredService = SecurityManager.sanitizeInput(desiredService),
                    suggestedDate = suggestedDate,
                    suggestedTime = suggestedTime,
                    venueId = venue.id,
                    venueNameFa = venue.nameFa,
                    venueNameEn = venue.nameEn,
                    slotId = slot?.id,
                    slotNameFa = slot?.roomNameFa,
                    slotNameEn = slot?.roomNameEn,
                    notes = SecurityManager.sanitizeInput(notes)
                )
                _submittedBookingRequest.value = req

                if (req.wasDuplicate) {
                    showToast(
                        ToastType.INFO,
                        if (_language.value == Language.FA) "این زمان قبلاً درخواست شده است" else "This time slot was already requested",
                        if (_language.value == Language.FA) "کد رهگیری درخواست قبلی: ${req.trackingCode}" else "Previous request tracking: ${req.trackingCode}"
                    )
                } else {
                    showToast(
                        ToastType.SUCCESS,
                        if (_language.value == Language.FA) "درخواست نوبت ثبت شد" else "Appointment Requested",
                        if (_language.value == Language.FA) "کد رهگیری: ${req.trackingCode}" else "Tracking: ${req.trackingCode}"
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                showToast(
                    ToastType.WARNING,
                    if (_language.value == Language.FA) "ثبت نوبت ناموفق بود" else "Booking Failed",
                    if (_language.value == Language.FA) "ذخیره درخواست انجام نشد؛ لطفاً دوباره تلاش کن." else "The request could not be saved. Please try again."
                )
            } finally {
                _isBookingSubmitting.value = false
            }
        }
    }
    fun loginWithPin(pin: String): SecurityManager.AuthResult {
        val result = SecurityManager.authenticate(pin)
        if (result.success && result.role != null) {
            _userRole.value = result.role
            _sessionToken.value = result.token
            closeModal()
            val isFa = _language.value == Language.FA
            showToast(
                ToastType.INFO,
                if (result.role == UserRole.SUPER_ADMIN) (if (isFa) "ورود مدیر ارشد (حالت آزمایشی دمو)" else "SuperAdmin Demo Authorized")
                else (if (isFa) "ورود مدیر کلینیک (حالت آزمایشی دمو)" else "Clinic Director Demo Authorized"),
                if (isFa) "دسترسی در حالت ارزیابی آزمایشی محلی فعال شد." else "Local sandbox privileges active."
            )
            repository.addAuditLog(
                type = "auth",
                severity = Severity.INFO,
                titleFa = if (result.role == UserRole.SUPER_ADMIN) "ورود مدیر ارشد سیستم (دمو)" else "ورود مدیر کلینیک (دمو)",
                titleEn = if (result.role == UserRole.SUPER_ADMIN) "SuperAdmin Demo Login" else "Clinic Director Demo Login",
                details = "نشست آزمایشی با توکن ${result.token?.take(18)} فعال شد."
            )
        } else {
            showToast(
                ToastType.ERROR,
                if (_language.value == Language.FA) "خطای ورود آزمایشی" else "Demo Auth Failed",
                result.errorMessage ?: "رمز عبور نادرست است."
            )
        }
        return result
    }
    fun logout() {
        val prevRole = _userRole.value
        val currentToken = _sessionToken.value
        val isDemoLogout = !currentToken.isNullOrBlank()

        if (isDemoLogout) {
            SecurityManager.invalidateSession(currentToken)
            _userRole.value = UserRole.PUBLIC
            _sessionToken.value = null
            showToast(
                ToastType.INFO,
                if (_language.value == Language.FA) "خروج از حساب آزمایشی" else "Demo Logged Out",
                if (_language.value == Language.FA) "نشست آزمایشی بسته شد." else "Demo session terminated."
            )
        } else {
            _userRole.value = UserRole.PUBLIC
            _sessionToken.value = null
            viewModelScope.launch {
                try {
                    when (val result = repository.supabaseSignOut()) {
                        is SupabaseAuthResult.SignedOut -> {
                            showToast(
                                ToastType.INFO,
                                if (_language.value == Language.FA) "خروج از حساب" else "Logged Out",
                                if (_language.value == Language.FA) "نشست کاربری بسته شد." else result.message
                            )
                        }
                        is SupabaseAuthResult.Error -> {
                            showToast(
                                ToastType.WARNING,
                                if (_language.value == Language.FA) "خروج نیازمند بررسی" else "Logout Needs Attention",
                                result.message
                            )
                        }
                        else -> {
                            showToast(
                                ToastType.WARNING,
                                if (_language.value == Language.FA) "وضعیت خروج نامشخص" else "Logout Status Unknown",
                                if (_language.value == Language.FA) "وضعیت خروج از سرور تأیید نشد." else "Server sign-out was not confirmed."
                            )
                        }
                    }
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (error: Exception) {
                    showToast(
                        ToastType.ERROR,
                        if (_language.value == Language.FA) "خطا هنگام خروج" else "Logout Error",
                        error.message ?: "Logout could not be completed."
                    )
                }
            }
        }

        repository.addAuditLog(
            type = "auth",
            severity = Severity.INFO,
            titleFa = "خروج کاربر از سیستم",
            titleEn = "User Logout",
            details = "خروج از نقش ${prevRole.name} و درخواست ابطال نشست."
        )
    }

    fun signInWithSupabase(email: String, pass: String) {
        viewModelScope.launch {
            _isSupabaseAuthLoading.value = true
            val isFa = _language.value == Language.FA

            try {
                val result = repository.supabaseSignIn(email.trim(), pass)

                when (result) {
                    is SupabaseAuthResult.Success -> {
                        val user = result.user
                        SecurityManager.invalidateSession(_sessionToken.value)
                        _sessionToken.value = null
                        _userRole.value = UserRole.PUBLIC
                        closeModal()
                        showToast(
                            ToastType.SUCCESS,
                            if (isFa) "ورود با سوپابیس موفقیت‌آمیز بود" else "Supabase Login Successful",
                            if (isFa) "حساب کاربری ${user?.email ?: ""} متصل شد." else "Connected as ${user?.email ?: ""}."
                        )
                        repository.addAuditLog(
                            type = "auth",
                            severity = Severity.INFO,
                            titleFa = "ورود از طریق سرور سوپابیس",
                            titleEn = "Supabase Cloud Authentication",
                            details = "ورود کاربر ${user?.email} با توکن امنیتی GoTrue."
                        )
                    }
                    is SupabaseAuthResult.ConfirmationRequired -> {
                        showToast(
                            ToastType.INFO,
                            if (isFa) "تأیید ایمیل لازم است" else "Email confirmation required",
                            result.message
                        )
                    }
                    is SupabaseAuthResult.Error -> {
                        showToast(
                            ToastType.ERROR,
                            if (isFa) "خطای ورود با سوپابیس" else "Supabase Auth Failed",
                            result.message
                        )
                    }
                    else -> Unit
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                showToast(
                    ToastType.ERROR,
                    if (isFa) "خطای ارتباط با سوپابیس" else "Supabase Connection Error",
                    error.message ?: if (isFa) "ورود انجام نشد. دوباره تلاش کنید." else "Login failed. Please try again."
                )
            } finally {
                _isSupabaseAuthLoading.value = false
            }
        }
    }

    fun signUpWithSupabase(email: String, pass: String, fullName: String) {
        viewModelScope.launch {
            _isSupabaseAuthLoading.value = true
            val isFa = _language.value == Language.FA
            val metadata = mapOf("full_name" to fullName)

            try {
                val result = repository.supabaseSignUp(email.trim(), pass, metadata)

                when (result) {
                    is SupabaseAuthResult.Success -> {
                        val user = result.user
                        SecurityManager.invalidateSession(_sessionToken.value)
                        _sessionToken.value = null
                        _userRole.value = UserRole.PUBLIC
                        closeModal()
                        showToast(
                            ToastType.SUCCESS,
                            if (isFa) "ثبت‌نام در سوپابیس انجام شد" else "Supabase Registration Complete",
                            if (isFa) "حساب کاربری جدید برای ${user?.email ?: ""} ایجاد گردید." else "Account created for ${user?.email ?: ""}."
                        )
                        repository.addAuditLog(
                            type = "auth",
                            severity = Severity.INFO,
                            titleFa = "ثبت‌نام کاربر جدید در سوپابیس",
                            titleEn = "New User Registration",
                            details = "ایجاد حساب برای ${user?.email} در پروژه Supabase."
                        )
                    }
                    is SupabaseAuthResult.ConfirmationRequired -> {
                        showToast(
                            ToastType.INFO,
                            if (isFa) "تأیید ایمیل لازم است" else "Email confirmation required",
                            result.message
                        )
                    }
                    is SupabaseAuthResult.Error -> {
                        showToast(
                            ToastType.ERROR,
                            if (isFa) "خطای ثبت‌نام در سوپابیس" else "Supabase Sign-Up Error",
                            result.message
                        )
                    }
                    else -> Unit
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                showToast(
                    ToastType.ERROR,
                    if (isFa) "خطای ارتباط هنگام ثبت‌نام" else "Supabase Sign-Up Connection Error",
                    error.message ?: if (isFa) "ثبت‌نام انجام نشد. دوباره تلاش کنید." else "Sign-up failed. Please try again."
                )
            } finally {
                _isSupabaseAuthLoading.value = false
            }
        }
    }


    fun triggerCloudSync() {
        viewModelScope.launch {
            val isFa = _language.value == Language.FA
            showToast(
                ToastType.INFO,
                if (isFa) "همگام‌سازی ابری" else "Cloud Sync Initiated",
                if (isFa) "در حال ارسال داده‌های محلی به سرور سوپابیس..." else "Syncing local Room records to Supabase..."
            )
            val syncResult = repository.triggerSupabaseSync()
            when (syncResult) {
                is ISupabaseSyncGateway.SyncResult.Success -> {
                    showToast(
                        ToastType.SUCCESS,
                        if (isFa) "همگام‌سازی تکمیل شد" else "Sync Complete",
                        if (isFa) "${syncResult.data.size} نوبت با سرور ابری تطبیق یافت." else "${syncResult.data.size} records synced to Supabase."
                    )
                }
                is ISupabaseSyncGateway.SyncResult.OfflineLocalOnly -> {
                    showToast(
                        ToastType.INFO,
                        if (isFa) "حالت آفلاین امن" else "Offline-First Safe",
                        syncResult.message
                    )
                }
                is ISupabaseSyncGateway.SyncResult.Error -> {
                    showToast(
                        ToastType.WARNING,
                        if (isFa) "خطای همگام‌سازی ابری" else "Cloud Sync Warning",
                        syncResult.message
                    )
                }
            }
        }
    }

    private fun isAuthorizedFor(requiredRole: UserRole): Boolean {
        val token = _sessionToken.value
        val currentRole = _userRole.value
        if (!SecurityManager.isValidSession(token, requiredRole)) {
            val isFa = _language.value == Language.FA
            showToast(
                ToastType.ERROR,
                if (isFa) "خطای عدم دسترسی امنیتی" else "Security Unauthorized",
                if (isFa) "نشست اداری شما نامعتبر یا منقضی شده است. دسترسی رد شد."
                else "Session expired or unauthorized. Access denied."
            )
            repository.addAuditLog(
                type = "security",
                severity = Severity.CRITICAL,
                titleFa = "تلاش برای دسترسی غیرمجاز به متد اداری",
                titleEn = "Unauthorized Administrative Action Attempt",
                details = "نقش فعلی: ${currentRole.name}، نقش الزامی: ${requiredRole.name}"
            )
            if (token != null && !SecurityManager.isValidSession(token, currentRole)) {
                logout()
            }
            return false
        }
        return true
    }

    fun toggleSlotStatus(slotId: String, newStatus: SlotStatus) {
        if (!isAuthorizedFor(UserRole.CLINIC_ADMIN)) return
        repository.toggleSlotStatus(slotId, newStatus)
        val isFa = _language.value == Language.FA
        showToast(
            ToastType.INFO,
            if (isFa) "وضعیت یونیت تغییر یافت" else "Unit Status Updated",
            if (isFa) "وضعیت به ${newStatus.name} تغییر یافت." else "Status altered to ${newStatus.name}."
        )
    }

    fun updateBookingRequestStatus(requestId: String, status: BookingRequestStatus) {
        if (!isAuthorizedFor(UserRole.CLINIC_ADMIN)) return
        repository.updateBookingRequestStatus(requestId, status)
        val isFa = _language.value == Language.FA
        showToast(
            if (status == BookingRequestStatus.CONFIRMED) ToastType.SUCCESS else ToastType.WARNING,
            if (status == BookingRequestStatus.CONFIRMED) (if (isFa) "نوبت تایید شد" else "Booking Confirmed")
            else (if (isFa) "نوبت لغو شد" else "Booking Rejected"),
            if (isFa) "اطلاع‌رسانی به متقاضی انجام شد." else "Applicant notified."
        )
    }

    fun confirmPatientArrival(escrowId: String, receptionistId: String) {
        if (!isAuthorizedFor(UserRole.CLINIC_ADMIN)) return
        val tx = escrowTransactions.value.find { it.id == escrowId }
        val isFa = _language.value == Language.FA
        if (tx != null && !SecurityManager.isValidNationalId(tx.patientNationalId, strictChecksum = true)) {
            showToast(
                ToastType.ERROR,
                if (isFa) "خطای اعتبارسنجی کد ملی" else "Invalid National ID",
                if (isFa) "کد ملی بیمار با الگوریتم رسمی مودولو ۱۱ تطبیق ندارد." else "Patient National ID checksum failed."
            )
            repository.addAuditLog(
                type = "security",
                severity = Severity.WARNING,
                titleFa = "عدم انطباق الگوریتم کد ملی",
                titleEn = "National ID Checksum Mismatch",
                details = "تراکنش $escrowId با کد ملی ${tx.patientNationalId} تایید نشد."
            )
            return
        }

        repository.confirmPatientArrival(escrowId, receptionistId)
        showToast(
            ToastType.SUCCESS,
            if (isFa) "احراز هویت و ورود بیمار ثبت شد" else "Patient Check-In Verified",
            if (isFa) "کد ملی تطبیق داده شد و گام دوم اسکرو تکمیل گردید." else "National ID verified."
        )
    }

    fun signOffProcedure(escrowId: String, notes: String, signatureSvg: String) {
        if (!isAuthorizedFor(UserRole.CLINIC_ADMIN)) return
        repository.signOffProcedure(escrowId, notes, signatureSvg)
        closeModal()
        val isFa = _language.value == Language.FA
        showToast(
            ToastType.SUCCESS,
            if (isFa) "ثبت اتمام درمان و امضای الکترونیک" else "Procedure Done & Signed",
            if (isFa) "پنجره تضمین ۱۲۰ دقیقه‌ای فعال گردید." else "120m guarantee cooldown active."
        )
    }

    fun fastForwardGuarantee(escrowId: String) {
        if (!isAuthorizedFor(UserRole.SUPER_ADMIN)) return
        repository.fastForwardGuarantee(escrowId)
        val isFa = _language.value == Language.FA
        showToast(
            ToastType.WARNING,
            if (isFa) "پایان پنجره تضمین (تست)" else "Guarantee Elapsed (Demo)",
            if (isFa) "امکان تسویه مستقیم به شماره شبا فعال شد." else "Ready for IBAN settlement."
        )
    }

    fun releaseEscrow(escrowId: String) {
        if (!isAuthorizedFor(UserRole.SUPER_ADMIN)) return
        val tx = escrowTransactions.value.find { it.id == escrowId }
        val isFa = _language.value == Language.FA
        if (tx != null && !SecurityManager.isValidShebaNumber(tx.shebaNumber, strictChecksum = true)) {
            showToast(
                ToastType.ERROR,
                if (isFa) "خطای شماره شبا مقصد" else "Invalid Sheba IBAN",
                if (isFa) "شماره شبا مقصد بر اساس الگوریتم ISO 7064 نامعتبر است." else "IBAN ISO 7064 checksum verification failed."
            )
            repository.addAuditLog(
                type = "security",
                severity = Severity.CRITICAL,
                titleFa = "رد تسویه مالی به دلیل شماره شبا نامعتبر",
                titleEn = "Escrow Disbursement Rejected (Invalid IBAN)",
                details = "تراکنش $escrowId به شماره شبا ${tx.shebaNumber} متوقف شد."
            )
            return
        }

        repository.releaseEscrowToSheba(escrowId)
        showToast(
            ToastType.INFO,
            if (isFa) "شبیه‌سازی تسویه اسکرو (حالت دمو)" else "Escrow Payout Simulated (Demo)",
            if (isFa) "تراکنش در محیط شبیه‌ساز انجام شد. انتقال وجه واقعی غیرفعال است."
            else "Simulated locally. Real financial transfers are disabled."
        )
    }

    fun submitOnboarding(app: OnboardingApplication) {
        repository.submitOnboardingApplication(app)
        val isFa = _language.value == Language.FA
        showToast(
            ToastType.SUCCESS,
            if (isFa) "درخواست همکاری ثبت شد" else "Application Queued",
            if (isFa) "درخواست شما جهت بررسی به کارتابل سوپرادمین ارسال شد." else "Queued for regulatory verification."
        )
    }

    fun updateApplicationStatus(appId: String, status: ApplicationStatus) {
        if (!isAuthorizedFor(UserRole.SUPER_ADMIN)) return
        repository.updateApplicationStatus(appId, status)
        val isFa = _language.value == Language.FA
        showToast(
            if (status == ApplicationStatus.APPROVED) ToastType.SUCCESS else ToastType.WARNING,
            if (status == ApplicationStatus.APPROVED) (if (isFa) "درخواست تایید و فعال شد" else "Partner Approved")
            else (if (isFa) "درخواست رد شد" else "Partner Rejected"),
            if (isFa) "وضعیت مرکز در سامانه به‌روزرسانی شد." else "Status updated."
        )
    }

    fun replyToTicket(ticketId: String, content: String) {
        repository.replyToTicket(ticketId, SecurityManager.sanitizeInput(content))
        val isFa = _language.value == Language.FA
        showToast(
            ToastType.SUCCESS,
            if (isFa) "پاسخ ارسال شد" else "Reply Sent",
            if (isFa) "پیام شما به واحد پشتیبانی ارسال گردید." else "Dispatched to support."
        )
    }

    fun showToast(type: ToastType, title: String, message: String) {
        val alert = ToastAlert(
            id = "tst-${System.currentTimeMillis()}-${(10..99).random()}",
            type = type,
            title = title,
            message = message
        )
        _toasts.value = listOf(alert) + _toasts.value.take(2)
        viewModelScope.launch {
            delay(4000)
            dismissToast(alert.id)
        }
    }

    fun dismissToast(id: String) {
        _toasts.value = _toasts.value.filter { it.id != id }
    }
}
