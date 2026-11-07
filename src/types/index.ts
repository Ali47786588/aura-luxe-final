export type Language = 'FA' | 'EN';

export type UserRole = 'PUBLIC' | 'CLINIC_ADMIN' | 'SUPER_ADMIN';

export type CategoryId = 'ALL' | 'AESTHETICS' | 'SALONS' | 'DENTAL';

export type SlotStatus = 'AVAILABLE' | 'RESERVED' | 'IN_PROCEDURE' | 'SANITIZING';

export type SlotType = 'CHAIR' | 'BED' | 'LASER_SUITE' | 'OPERATORY';

export type BookingRequestStatus = 'PENDING_CONFIRMATION' | 'CONFIRMED' | 'REJECTED';

export type EscrowStatus = 'HELD_IN_ESCROW' | 'PATIENT_ARRIVED' | 'PROCEDURE_DONE' | 'RELEASED';

export type ApplicationStatus = 'PENDING' | 'APPROVED' | 'REJECTED';

export type Severity = 'INFO' | 'WARNING' | 'CRITICAL';

export type ToastType = 'SUCCESS' | 'WARNING' | 'INFO' | 'ERROR';

export interface District {
  id: string;
  cityId: 'tehran' | 'karaj' | string;
  nameEn: string;
  nameFa: string;
  popularRoad: string;
  activeClinicsCount: number;
  availableSlotsNow: number;
  averageHourlyToman: number;
}

export interface Venue {
  id: string;
  nameEn: string;
  nameFa: string;
  category: CategoryId;
  cityId: string;
  districtId: string;
  districtNameEn: string;
  districtNameFa: string;
  addressEn: string;
  addressFa: string;
  rating: number;
  reviewCount: number;
  image: string;
  taglineEn: string;
  taglineFa: string;
  descriptionEn: string;
  descriptionFa: string;
  totalChairs: number;
  activeSlots: number;
  baseHourlyToman: number;
  certifications: string[];
  equipmentList: string[];
  amenities: string[];
  medicalDirectorEn: string;
  medicalDirectorFa: string;
  medicalLicenseNumber: string;
}

export interface RealtimeSlot {
  id: string;
  venueId: string;
  venueNameEn: string;
  venueNameFa: string;
  roomNameEn: string;
  roomNameFa: string;
  type: SlotType;
  status: SlotStatus;
  currentPractitioner?: string | null;
  patientProcedure?: string | null;
  pricePerHourToman: number;
  equipment: string[];
  assistantIncluded: boolean;
  autoclaveReady: boolean;
  lastSanitizedMinutesAgo: number;
}

export interface ClinicalMetric {
  label: string;
  value: string;
}

export interface BeforeAfterCase {
  id: string;
  titleEn: string;
  titleFa: string;
  category: CategoryId;
  clinicNameEn: string;
  clinicNameFa: string;
  doctorNameEn: string;
  doctorNameFa: string;
  treatmentEn: string;
  treatmentFa: string;
  image: string;
  metricsEn: ClinicalMetric[];
  metricsFa: ClinicalMetric[];
  notesEn: string;
  notesFa: string;
}

export interface StorySlide {
  id: string;
  titleFa: string;
  titleEn: string;
  doctorNameFa: string;
  doctorNameEn: string;
  clinicNameFa: string;
  clinicNameEn: string;
  image: string;
  treatmentDetailsFa: string;
  treatmentDetailsEn: string;
  clinicalTagFa: string;
  clinicalTagEn: string;
  durationSeconds?: number;
}

export interface StoryHighlight {
  id: string;
  titleFa: string;
  titleEn: string;
  coverImage: string;
  slides: StorySlide[];
}

export interface BookingRequest {
  id: string;
  trackingCode: string;
  fullName: string;
  phoneNumber: string;
  desiredService: string;
  suggestedDate: string;
  suggestedTime: string;
  venueId: string;
  venueNameFa: string;
  venueNameEn: string;
  slotId?: string | null;
  slotNameFa?: string | null;
  slotNameEn?: string | null;
  notes: string;
  status: BookingRequestStatus;
  createdAt: string;
  updatedAt?: string | null;
}

export interface EscrowTransaction {
  id: string;
  contractCode: string;
  practitionerName: string;
  licenseId: string;
  venueName: string;
  slotName: string;
  grossAmountToman: number;
  platformFeeToman: number;
  clinicPayoutToman: number;
  status: EscrowStatus;
  milestoneStep: number; // 1: Held, 2: Arrived, 3: Procedure Done, 4: Released
  createdAt: string;
  estimatedReleaseTime: string;
  paymentRefId: string; // Shaparak RRN
  trackingNumber: string;
  patientCheckInTimestamp?: string | null;
  patientNationalIdVerified?: boolean;
  procedureSignOffNotes?: string | null;
  doctorSignOffTimestamp?: string | null;
  guaranteeMinutesRemaining: number;
  shebaNumber: string;
  patientNationalId: string;
  signatureSvg?: string | null;
}

export interface OnboardingApplication {
  id: string;
  applicantType: 'clinic' | 'doctor' | 'salon' | string;
  activityTypeFa: string;
  businessName: string;
  applicantName: string;
  phoneNumber: string;
  cityAndDistrict: string;
  chairsOrBedsCount: number;
  shortDescription: string;
  medicalCouncilOrGuildId: string;
  shebaNumber: string;
  status: ApplicationStatus;
  submittedAt: string;
}

export interface TicketMessage {
  id: string;
  senderName: string;
  isStaff: boolean;
  content: string;
  timestamp: string;
}

export interface SupportTicket {
  id: string;
  ticketNumber: string;
  senderName: string;
  venueName: string;
  subject: string;
  category: 'escrow' | 'technical' | 'slot' | string;
  priority: 'urgent' | 'high' | 'normal' | string;
  status: 'open' | 'answered' | 'resolved' | string;
  createdAt: string;
  messages: TicketMessage[];
}

export interface SecurityAuditLog {
  id: string;
  timestamp: string;
  type: string;
  severity: Severity;
  titleFa: string;
  titleEn: string;
  details: string;
  ip: string;
}

export interface ToastAlert {
  id: string;
  type: ToastType;
  title: string;
  message: string;
}

export type ActiveModal =
  | 'NONE'
  | 'LOCATION_SWITCHER'
  | 'BOOKING_REQUEST'
  | 'VENUE_DETAILS'
  | 'ADMIN_LOGIN'
  | 'ONBOARDING'
  | 'SUPPORT_TICKETING'
  | 'STORY_VIEWER'
  | 'DIGITAL_SIGNATURE'
  | 'INVOICE_DETAILS';
