import React, { createContext, useContext, useState, useEffect } from 'react';
import {
  Language,
  UserRole,
  CategoryId,
  SlotStatus,
  BookingRequestStatus,
  ApplicationStatus,
  Severity,
  ToastType,
  District,
  Venue,
  RealtimeSlot,
  BeforeAfterCase,
  StoryHighlight,
  BookingRequest,
  EscrowTransaction,
  OnboardingApplication,
  SupportTicket,
  SecurityAuditLog,
  ToastAlert,
  ActiveModal,
} from '../types';
import {
  initialDistricts,
  initialVenues,
  initialSlots,
  initialBookingRequests,
  initialEscrowTransactions,
  initialApplications,
  initialTickets,
  initialAuditLogs,
  initialBeforeAfterCases,
  initialStoryHighlights,
} from '../data/mockData';
import { SecurityManager, AuthResult } from '../utils/security';

interface AuraLuxeContextType {
  // State
  language: Language;
  userRole: UserRole;
  sessionToken: string | null;
  districts: District[];
  venues: Venue[];
  slots: RealtimeSlot[];
  bookingRequests: BookingRequest[];
  escrowTransactions: EscrowTransaction[];
  applications: OnboardingApplication[];
  tickets: SupportTicket[];
  auditLogs: SecurityAuditLog[];
  beforeAfterCases: BeforeAfterCase[];
  storyHighlights: StoryHighlight[];
  toasts: ToastAlert[];
  isOfflineMode: boolean;

  selectedDistrict: District;
  selectedCategory: CategoryId;
  managedVenue: Venue;
  activeModal: ActiveModal;
  selectedVenueForDetails: Venue | null;
  preselectedBookingVenue: Venue | null;
  preselectedBookingSlot: RealtimeSlot | null;
  submittedBookingRequest: BookingRequest | null;
  activeStoryHighlight: StoryHighlight | null;
  activeStorySlideIndex: number;
  selectedInvoiceTx: EscrowTransaction | null;
  activeEscrowTxForAction: EscrowTransaction | null;

  // Actions
  toggleLanguage: () => void;
  setDistrict: (district: District) => void;
  setCategory: (category: CategoryId) => void;
  setManagedVenue: (venue: Venue) => void;
  openVenueDetails: (venue: Venue) => void;
  openBooking: (venue?: Venue | null, slot?: RealtimeSlot | null) => void;
  openStoryHighlight: (highlight: StoryHighlight) => void;
  nextStorySlide: () => void;
  prevStorySlide: () => void;
  openAdminLogin: () => void;
  openOnboarding: () => void;
  openSupport: () => void;
  openLocationSwitcher: () => void;
  openInvoice: (tx: EscrowTransaction) => void;
  openDigitalSignaturePad: (tx: EscrowTransaction) => void;
  closeModal: () => void;

  submitBookingRequest: (
    fullName: string,
    phoneNumber: string,
    desiredService: string,
    suggestedDate: string,
    suggestedTime: string,
    notes: string
  ) => void;
  loginWithPin: (pin: string) => AuthResult;
  logout: () => void;
  toggleSlotStatus: (slotId: string, newStatus: SlotStatus) => void;
  updateBookingRequestStatus: (requestId: string, status: BookingRequestStatus) => void;
  confirmPatientArrival: (escrowId: string, receptionistId?: string) => void;
  signOffProcedure: (escrowId: string, notes: string, signatureSvg: string) => void;
  fastForwardGuarantee: (escrowId: string) => void;
  releaseEscrow: (escrowId: string) => void;
  submitOnboarding: (app: OnboardingApplication) => void;
  updateApplicationStatus: (appId: string, status: ApplicationStatus) => void;
  replyToTicket: (ticketId: string, content: string) => void;
  showToast: (type: ToastType, title: string, message: string) => void;
  dismissToast: (id: string) => void;
  addAuditLog: (type: string, severity: Severity, titleFa: string, titleEn: string, details: string) => void;
}

const AuraLuxeContext = createContext<AuraLuxeContextType | undefined>(undefined);

export const AuraLuxeProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [language, setLanguage] = useState<Language>('FA');
  const [userRole, setUserRole] = useState<UserRole>('PUBLIC');
  const [sessionToken, setSessionToken] = useState<string | null>(null);
  const [isOfflineMode] = useState<boolean>(true);

  // Entities
  const districts = initialDistricts;
  const venues = initialVenues;
  const beforeAfterCases = initialBeforeAfterCases;
  const storyHighlights = initialStoryHighlights;

  const [slots, setSlots] = useState<RealtimeSlot[]>(() => {
    const saved = localStorage.getItem('aura_slots');
    return saved ? JSON.parse(saved) : initialSlots;
  });

  const [bookingRequests, setBookingRequests] = useState<BookingRequest[]>(() => {
    const saved = localStorage.getItem('aura_booking_requests');
    return saved ? JSON.parse(saved) : initialBookingRequests;
  });

  const [escrowTransactions, setEscrowTransactions] = useState<EscrowTransaction[]>(() => {
    const saved = localStorage.getItem('aura_escrow_txs');
    return saved ? JSON.parse(saved) : initialEscrowTransactions;
  });

  const [applications, setApplications] = useState<OnboardingApplication[]>(() => {
    const saved = localStorage.getItem('aura_applications');
    return saved ? JSON.parse(saved) : initialApplications;
  });

  const [tickets, setTickets] = useState<SupportTicket[]>(() => {
    const saved = localStorage.getItem('aura_tickets');
    return saved ? JSON.parse(saved) : initialTickets;
  });

  const [auditLogs, setAuditLogs] = useState<SecurityAuditLog[]>(() => {
    const saved = localStorage.getItem('aura_audit_logs');
    return saved ? JSON.parse(saved) : initialAuditLogs;
  });

  const [toasts, setToasts] = useState<ToastAlert[]>([]);

  // Selection states
  const [selectedDistrict, setSelectedDistrict] = useState<District>(initialDistricts[0]);
  const [selectedCategory, setSelectedCategory] = useState<CategoryId>('ALL');
  const [managedVenue, setManagedVenue] = useState<Venue>(initialVenues[0]);

  // Modals
  const [activeModal, setActiveModal] = useState<ActiveModal>('NONE');
  const [selectedVenueForDetails, setSelectedVenueForDetails] = useState<Venue | null>(null);
  const [preselectedBookingVenue, setPreselectedBookingVenue] = useState<Venue | null>(null);
  const [preselectedBookingSlot, setPreselectedBookingSlot] = useState<RealtimeSlot | null>(null);
  const [submittedBookingRequest, setSubmittedBookingRequest] = useState<BookingRequest | null>(null);
  const [activeStoryHighlight, setActiveStoryHighlight] = useState<StoryHighlight | null>(null);
  const [activeStorySlideIndex, setActiveStorySlideIndex] = useState<number>(0);
  const [selectedInvoiceTx, setSelectedInvoiceTx] = useState<EscrowTransaction | null>(null);
  const [activeEscrowTxForAction, setActiveEscrowTxForAction] = useState<EscrowTransaction | null>(null);

  // Sync to localStorage
  useEffect(() => {
    localStorage.setItem('aura_slots', JSON.stringify(slots));
  }, [slots]);

  useEffect(() => {
    localStorage.setItem('aura_booking_requests', JSON.stringify(bookingRequests));
  }, [bookingRequests]);

  useEffect(() => {
    localStorage.setItem('aura_escrow_txs', JSON.stringify(escrowTransactions));
  }, [escrowTransactions]);

  useEffect(() => {
    localStorage.setItem('aura_applications', JSON.stringify(applications));
  }, [applications]);

  useEffect(() => {
    localStorage.setItem('aura_tickets', JSON.stringify(tickets));
  }, [tickets]);

  useEffect(() => {
    localStorage.setItem('aura_audit_logs', JSON.stringify(auditLogs));
  }, [auditLogs]);

  // Update HTML lang and dir attributes
  useEffect(() => {
    document.documentElement.lang = language.toLowerCase();
    document.documentElement.dir = language === 'FA' ? 'rtl' : 'ltr';
  }, [language]);

  const showToast = (type: ToastType, title: string, message: string) => {
    const id = `tst-${Date.now()}-${Math.floor(Math.random() * 90 + 10)}`;
    const alert: ToastAlert = { id, type, title, message };
    setToasts((prev) => [alert, ...prev.slice(0, 2)]);
    setTimeout(() => {
      dismissToast(id);
    }, 4500);
  };

  const dismissToast = (id: string) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  };

  const addAuditLog = (type: string, severity: Severity, titleFa: string, titleEn: string, details: string) => {
    const now = new Date();
    const timestamp = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(
      now.getDate()
    ).padStart(2, '0')} ${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(
      2,
      '0'
    )}:${String(now.getSeconds()).padStart(2, '0')}`;
    const log: SecurityAuditLog = {
      id: `log-${Date.now()}`,
      timestamp,
      type,
      severity,
      titleFa,
      titleEn,
      details,
      ip: '185.192.112.4',
    };
    setAuditLogs((prev) => [log, ...prev]);
  };

  const toggleLanguage = () => {
    const next = language === 'FA' ? 'EN' : 'FA';
    setLanguage(next);
    showToast(
      'INFO',
      next === 'FA' ? 'تغییر زبان' : 'Language Changed',
      next === 'FA' ? 'زبان برنامه به فارسی تنظیم شد.' : 'Language set to English.'
    );
  };

  const setDistrict = (district: District) => {
    setSelectedDistrict(district);
    closeModal();
  };

  const setCategory = (category: CategoryId) => {
    setSelectedCategory(category);
  };

  const openVenueDetails = (venue: Venue) => {
    setSelectedVenueForDetails(venue);
    setActiveModal('VENUE_DETAILS');
  };

  const openBooking = (venue?: Venue | null, slot?: RealtimeSlot | null) => {
    setPreselectedBookingVenue(venue || venues[0]);
    setPreselectedBookingSlot(slot || null);
    setSubmittedBookingRequest(null);
    setActiveModal('BOOKING_REQUEST');
  };

  const openStoryHighlight = (highlight: StoryHighlight) => {
    setActiveStoryHighlight(highlight);
    setActiveStorySlideIndex(0);
    setActiveModal('STORY_VIEWER');
  };

  const nextStorySlide = () => {
    if (!activeStoryHighlight) return;
    if (activeStorySlideIndex < activeStoryHighlight.slides.length - 1) {
      setActiveStorySlideIndex((prev) => prev + 1);
    } else {
      closeModal();
    }
  };

  const prevStorySlide = () => {
    if (activeStorySlideIndex > 0) {
      setActiveStorySlideIndex((prev) => prev - 1);
    }
  };

  const openAdminLogin = () => setActiveModal('ADMIN_LOGIN');
  const openOnboarding = () => setActiveModal('ONBOARDING');
  const openSupport = () => setActiveModal('SUPPORT_TICKETING');
  const openLocationSwitcher = () => setActiveModal('LOCATION_SWITCHER');

  const openInvoice = (tx: EscrowTransaction) => {
    setSelectedInvoiceTx(tx);
    setActiveModal('INVOICE_DETAILS');
  };

  const openDigitalSignaturePad = (tx: EscrowTransaction) => {
    setActiveEscrowTxForAction(tx);
    setActiveModal('DIGITAL_SIGNATURE');
  };

  const closeModal = () => {
    setActiveModal('NONE');
  };

  const submitBookingRequest = (
    fullName: string,
    phoneNumber: string,
    desiredService: string,
    suggestedDate: string,
    suggestedTime: string,
    notes: string
  ) => {
    const venue = preselectedBookingVenue || venues[0];
    const slot = preselectedBookingSlot;
    const trackingCode = `REQ-2026-${Math.floor(1000 + Math.random() * 9000)}`;
    const newReq: BookingRequest = {
      id: `req-${Date.now()}`,
      trackingCode,
      fullName: SecurityManager.sanitizeInput(fullName),
      phoneNumber: SecurityManager.sanitizeInput(phoneNumber),
      desiredService: SecurityManager.sanitizeInput(desiredService),
      suggestedDate,
      suggestedTime,
      venueId: venue.id,
      venueNameFa: venue.nameFa,
      venueNameEn: venue.nameEn,
      slotId: slot?.id || null,
      slotNameFa: slot?.roomNameFa || null,
      slotNameEn: slot?.roomNameEn || null,
      notes: SecurityManager.sanitizeInput(notes),
      status: 'PENDING_CONFIRMATION',
      createdAt: 'لحظاتی پیش',
    };

    setBookingRequests((prev) => [newReq, ...prev]);
    setSubmittedBookingRequest(newReq);
    showToast(
      'SUCCESS',
      language === 'FA' ? 'درخواست نوبت ثبت شد' : 'Appointment Requested',
      language === 'FA' ? `کد رهگیری: ${trackingCode}` : `Tracking: ${trackingCode}`
    );
  };

  const loginWithPin = (pin: string): AuthResult => {
    const result = SecurityManager.authenticate(pin);
    if (result.success && result.role) {
      setUserRole(result.role);
      setSessionToken(result.token || null);
      closeModal();
      const isFa = language === 'FA';
      showToast(
        'INFO',
        result.role === 'SUPER_ADMIN'
          ? isFa
            ? 'ورود مدیر ارشد (حالت آزمایشی دمو)'
            : 'SuperAdmin Demo Authorized'
          : isFa
          ? 'ورود مدیر کلینیک (حالت آزمایشی دمو)'
          : 'Clinic Director Demo Authorized',
        isFa ? 'دسترسی در حالت ارزیابی آزمایشی محلی فعال شد.' : 'Local sandbox privileges active.'
      );
      addAuditLog(
        'auth',
        'INFO',
        result.role === 'SUPER_ADMIN' ? 'ورود مدیر ارشد سیستم (دمو)' : 'ورود مدیر کلینیک (دمو)',
        result.role === 'SUPER_ADMIN' ? 'SuperAdmin Demo Login' : 'Clinic Director Demo Login',
        `نشست آزمایشی با توکن ${result.token?.slice(0, 18)} فعال شد.`
      );
    } else {
      showToast(
        'ERROR',
        language === 'FA' ? 'خطای ورود آزمایشی' : 'Demo Auth Failed',
        result.errorMessage || 'رمز عبور نادرست است.'
      );
    }
    return result;
  };

  const logout = () => {
    const prevRole = userRole;
    SecurityManager.invalidateSession(sessionToken);
    setUserRole('PUBLIC');
    setSessionToken(null);
    showToast(
      'INFO',
      language === 'FA' ? 'خروج از حساب' : 'Logged Out',
      language === 'FA' ? 'نشست کاربری با موفقیت بسته شد.' : 'Session terminated.'
    );
    addAuditLog(
      'auth',
      'INFO',
      'خروج کاربر از سیستم',
      'User Logout',
      `خروج از نقش ${prevRole} و ابطال نشست امنیتی.`
    );
  };

  const isAuthorizedFor = (requiredRole: UserRole): boolean => {
    if (!SecurityManager.isValidSession(sessionToken, requiredRole)) {
      const isFa = language === 'FA';
      showToast(
        'ERROR',
        isFa ? 'خطای عدم دسترسی امنیتی' : 'Security Unauthorized',
        isFa
          ? 'نشست اداری شما نامعتبر یا منقضی شده است. دسترسی رد شد.'
          : 'Session expired or unauthorized. Access denied.'
      );
      addAuditLog(
        'security',
        'CRITICAL',
        'تلاش برای دسترسی غیرمجاز به متد اداری',
        'Unauthorized Administrative Action Attempt',
        `نقش فعلی: ${userRole}، نقش الزامی: ${requiredRole}`
      );
      return false;
    }
    return true;
  };

  const toggleSlotStatus = (slotId: string, newStatus: SlotStatus) => {
    if (!isAuthorizedFor('CLINIC_ADMIN')) return;
    setSlots((prev) =>
      prev.map((s) => (s.id === slotId ? { ...s, status: newStatus } : s))
    );
    const isFa = language === 'FA';
    showToast(
      'INFO',
      isFa ? 'وضعیت یونیت تغییر یافت' : 'Unit Status Updated',
      isFa ? `وضعیت به ${newStatus} تغییر یافت.` : `Status altered to ${newStatus}.`
    );
  };

  const updateBookingRequestStatus = (requestId: string, status: BookingRequestStatus) => {
    if (!isAuthorizedFor('CLINIC_ADMIN')) return;
    setBookingRequests((prev) =>
      prev.map((r) => (r.id === requestId ? { ...r, status, updatedAt: 'لحظاتی پیش' } : r))
    );
    const isFa = language === 'FA';
    showToast(
      status === 'CONFIRMED' ? 'SUCCESS' : 'WARNING',
      status === 'CONFIRMED'
        ? isFa
          ? 'نوبت تایید شد'
          : 'Booking Confirmed'
        : isFa
        ? 'نوبت لغو شد'
        : 'Booking Rejected',
      isFa ? 'اطلاع‌رسانی به متقاضی انجام شد.' : 'Applicant notified.'
    );
  };

  const confirmPatientArrival = (escrowId: string, _receptionistId = 'RECEPTIONIST') => {
    if (!isAuthorizedFor('CLINIC_ADMIN') && !isAuthorizedFor('SUPER_ADMIN')) return;
    const tx = escrowTransactions.find((t) => t.id === escrowId);
    const isFa = language === 'FA';

    if (tx && !SecurityManager.isValidNationalId(tx.patientNationalId, true)) {
      showToast(
        'ERROR',
        isFa ? 'خطای اعتبارسنجی کد ملی' : 'Invalid National ID',
        isFa
          ? 'کد ملی بیمار با الگوریتم رسمی مودولو ۱۱ تطبیق ندارد.'
          : 'Patient National ID checksum failed.'
      );
      addAuditLog(
        'security',
        'WARNING',
        'عدم انطباق الگوریتم کد ملی',
        'National ID Checksum Mismatch',
        `تراکنش ${escrowId} با کد ملی ${tx.patientNationalId} تایید نشد.`
      );
      return;
    }

    setEscrowTransactions((prev) =>
      prev.map((t) =>
        t.id === escrowId
          ? {
              ...t,
              status: 'PATIENT_ARRIVED',
              milestoneStep: 2,
              patientNationalIdVerified: true,
              patientCheckInTimestamp: 'هم‌اکنون',
              estimatedReleaseTime: 'در حال انجام فرآیند درمانی (حالت آزمایشی)',
            }
          : t
      )
    );

    showToast(
      'SUCCESS',
      isFa ? 'احراز هویت و ورود بیمار ثبت شد' : 'Patient Check-In Verified',
      isFa ? 'کد ملی تطبیق داده شد و گام دوم اسکرو تکمیل گردید.' : 'National ID verified.'
    );
    addAuditLog(
      'escrow',
      'INFO',
      'احراز هویت و ثبت ورود بیمار',
      'Patient Arrival Verified',
      `تراکنش ${escrowId} گام دوم اسکرو با احراز هویت تایید شد.`
    );
  };

  const signOffProcedure = (escrowId: string, notes: string, signatureSvg: string) => {
    if (!isAuthorizedFor('CLINIC_ADMIN') && !isAuthorizedFor('SUPER_ADMIN')) return;
    setEscrowTransactions((prev) =>
      prev.map((t) =>
        t.id === escrowId
          ? {
              ...t,
              status: 'PROCEDURE_DONE',
              milestoneStep: 3,
              procedureSignOffNotes: SecurityManager.sanitizeInput(notes),
              doctorSignOffTimestamp: 'هم‌اکنون',
              signatureSvg,
              guaranteeMinutesRemaining: 120,
              estimatedReleaseTime: 'پنجره تضمین ۱۲۰ دقیقه‌ای فعال (شبیه‌ساز)',
            }
          : t
      )
    );
    closeModal();
    const isFa = language === 'FA';
    showToast(
      'SUCCESS',
      isFa ? 'ثبت اتمام درمان و امضای الکترونیک' : 'Procedure Done & Signed',
      isFa ? 'پنجره تضمین ۱۲۰ دقیقه‌ای فعال گردید.' : '120m guarantee cooldown active.'
    );
    addAuditLog(
      'escrow',
      'INFO',
      'امضای دیجیتال و ترخیص درمان',
      'Procedure Sign-Off & Digital Seal',
      `پایان درمان تراکنش ${escrowId} با یادداشت بالینی ثبت گردید.`
    );
  };

  const fastForwardGuarantee = (escrowId: string) => {
    if (!isAuthorizedFor('SUPER_ADMIN')) return;
    setEscrowTransactions((prev) =>
      prev.map((t) =>
        t.id === escrowId
          ? {
              ...t,
              guaranteeMinutesRemaining: 0,
              estimatedReleaseTime: 'آماده جهت تسویه به شماره شبا بانک مقصد',
            }
          : t
      )
    );
    const isFa = language === 'FA';
    showToast(
      'WARNING',
      isFa ? 'پایان پنجره تضمین (تست)' : 'Guarantee Elapsed (Demo)',
      isFa ? 'امکان تسویه مستقیم به شماره شبا فعال شد.' : 'Ready for IBAN settlement.'
    );
  };

  const releaseEscrow = (escrowId: string) => {
    if (!isAuthorizedFor('SUPER_ADMIN')) return;
    const tx = escrowTransactions.find((t) => t.id === escrowId);
    const isFa = language === 'FA';

    if (tx && !SecurityManager.isValidShebaNumber(tx.shebaNumber, true)) {
      showToast(
        'ERROR',
        isFa ? 'خطای شماره شبا مقصد' : 'Invalid Sheba IBAN',
        isFa
          ? 'شماره شبا مقصد بر اساس الگوریتم ISO 7064 نامعتبر است.'
          : 'IBAN ISO 7064 checksum verification failed.'
      );
      addAuditLog(
        'security',
        'CRITICAL',
        'رد تسویه مالی به دلیل شماره شبا نامعتبر',
        'Escrow Disbursement Rejected (Invalid IBAN)',
        `تراکنش ${escrowId} به شماره شبا ${tx.shebaNumber} متوقف شد.`
      );
      return;
    }

    setEscrowTransactions((prev) =>
      prev.map((t) =>
        t.id === escrowId
          ? {
              ...t,
              status: 'RELEASED',
              milestoneStep: 4,
              guaranteeMinutesRemaining: 0,
              estimatedReleaseTime: 'تسویه نهایی به شماره شبا (آزمایشی)',
            }
          : t
      )
    );

    showToast(
      'INFO',
      isFa ? 'شبیه‌سازی تسویه اسکرو (حالت دمو)' : 'Escrow Payout Simulated (Demo)',
      isFa
        ? 'تراکنش در محیط شبیه‌ساز انجام شد. انتقال وجه واقعی غیرفعال است.'
        : 'Simulated locally. Real financial transfers are disabled.'
    );
    addAuditLog(
      'escrow',
      'INFO',
      'تسویه نهایی اسکرو به شماره شبا',
      'Escrow Released to Sheba (Demo)',
      `مبلغ ناخالص تراکنش ${escrowId} پس از کسر کارمزد ۸٪ تسویه شد.`
    );
  };

  const submitOnboarding = (app: OnboardingApplication) => {
    setApplications((prev) => [app, ...prev]);
    const isFa = language === 'FA';
    showToast(
      'SUCCESS',
      isFa ? 'درخواست همکاری ثبت شد' : 'Application Queued',
      isFa ? 'درخواست شما جهت بررسی به کارتابل سوپرادمین ارسال شد.' : 'Queued for regulatory verification.'
    );
    addAuditLog(
      'compliance',
      'INFO',
      'ثبت درخواست همکاری مرکز جدید',
      'Partner Onboarding Submission',
      `درخواست همکاری برای مرکز ${app.businessName} به کارتابل افزوده شد.`
    );
  };

  const updateApplicationStatus = (appId: string, status: ApplicationStatus) => {
    if (!isAuthorizedFor('SUPER_ADMIN')) return;
    setApplications((prev) =>
      prev.map((a) => (a.id === appId ? { ...a, status } : a))
    );
    const isFa = language === 'FA';
    showToast(
      status === 'APPROVED' ? 'SUCCESS' : 'WARNING',
      status === 'APPROVED'
        ? isFa
          ? 'درخواست تایید و فعال شد'
          : 'Partner Approved'
        : isFa
        ? 'درخواست رد شد'
        : 'Partner Rejected',
      isFa ? 'وضعیت مرکز در سامانه به‌روزرسانی شد.' : 'Status updated.'
    );
  };

  const replyToTicket = (ticketId: string, content: string) => {
    const clean = SecurityManager.sanitizeInput(content);
    if (!clean) return;

    setTickets((prev) =>
      prev.map((t) => {
        if (t.id === ticketId) {
          const now = new Date();
          const timestamp = `${String(now.getHours()).padStart(2, '0')}:${String(
            now.getMinutes()
          ).padStart(2, '0')}`;
          const newMsg = {
            id: `m-${Date.now()}`,
            senderName: userRole === 'SUPER_ADMIN' ? 'مدیریت نظارت عالیه' : 'مدیر مرکز',
            isStaff: userRole === 'SUPER_ADMIN',
            content: clean,
            timestamp,
          };
          return {
            ...t,
            status: userRole === 'SUPER_ADMIN' ? 'answered' : 'open',
            messages: [...t.messages, newMsg],
          };
        }
        return t;
      })
    );

    const isFa = language === 'FA';
    showToast(
      'SUCCESS',
      isFa ? 'پاسخ ارسال شد' : 'Reply Sent',
      isFa ? 'پیام شما به واحد پشتیبانی ارسال گردید.' : 'Dispatched to support.'
    );
  };

  return (
    <AuraLuxeContext.Provider
      value={{
        language,
        userRole,
        sessionToken,
        districts,
        venues,
        slots,
        bookingRequests,
        escrowTransactions,
        applications,
        tickets,
        auditLogs,
        beforeAfterCases,
        storyHighlights,
        toasts,
        isOfflineMode,
        selectedDistrict,
        selectedCategory,
        managedVenue,
        activeModal,
        selectedVenueForDetails,
        preselectedBookingVenue,
        preselectedBookingSlot,
        submittedBookingRequest,
        activeStoryHighlight,
        activeStorySlideIndex,
        selectedInvoiceTx,
        activeEscrowTxForAction,
        toggleLanguage,
        setDistrict,
        setCategory,
        setManagedVenue,
        openVenueDetails,
        openBooking,
        openStoryHighlight,
        nextStorySlide,
        prevStorySlide,
        openAdminLogin,
        openOnboarding,
        openSupport,
        openLocationSwitcher,
        openInvoice,
        openDigitalSignaturePad,
        closeModal,
        submitBookingRequest,
        loginWithPin,
        logout,
        toggleSlotStatus,
        updateBookingRequestStatus,
        confirmPatientArrival,
        signOffProcedure,
        fastForwardGuarantee,
        releaseEscrow,
        submitOnboarding,
        updateApplicationStatus,
        replyToTicket,
        showToast,
        dismissToast,
        addAuditLog,
      }}
    >
      {children}
    </AuraLuxeContext.Provider>
  );
};

export const useAuraLuxe = () => {
  const context = useContext(AuraLuxeContext);
  if (!context) {
    throw new Error('useAuraLuxe must be used within an AuraLuxeProvider');
  }
  return context;
};
