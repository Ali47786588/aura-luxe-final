package com.example.data.repository

import com.example.R
import com.example.data.local.AuraDatabase
import com.example.data.local.entity.*
import com.example.data.local.security.KeystoreEncryptionManager
import com.example.data.model.*
import com.example.security.SecurityManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AuraRepository(
    private val database: AuraDatabase? = defaultDatabase
) : IAuraRepository {

    companion object {
        var defaultDatabase: AuraDatabase? = null
    }

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    override val districts: List<District> = listOf(
        // Tehran
        District("tehran_saadat_abad", "tehran", "Saadat Abad", "سعادت‌آباد", "بلوار شهرداری / میدان کاج", 38, 14, 1850000L),
        District("tehran_elahieh", "tehran", "Elahieh & Fereshteh", "الهیه و فرشته", "خیابان فرشته / مریم", 29, 9, 2400000L),
        District("tehran_zafaraniyeh", "tehran", "Zafaraniyeh", "زعفرانیه", "خیابان مقدس اردبیلی", 22, 7, 2200000L),
        District("tehran_niavaran", "tehran", "Niavaran", "نیاوران", "خیابان مژده / اطلس مال", 26, 11, 1950000L),
        District("tehran_shahrak_gharb", "tehran", "Shahrak-e Gharb", "شهرک غرب", "بلوار دادمان / ایران زمین", 31, 12, 1750000L),
        // Karaj
        District("karaj_gohardasht", "karaj", "Gohardasht", "گوهردشت کرج", "خیابان اصلی و بلوار رستاخیز", 30, 15, 1250000L),
        District("karaj_fardis", "karaj", "Fardis", "فردیس کرج", "فلکه اول / بلوار کانال", 24, 8, 1100000L),
        District("karaj_mehrshahr", "karaj", "Mehrshahr", "مهرشهر کرج", "بلوار شهرداری ۴ باندی", 19, 6, 1450000L),
        District("karaj_azimiyeh", "karaj", "Azimiyeh", "عظیمیه کرج", "میدان مهران / طالقانی", 27, 10, 1350000L)
    )

    override val venues: List<Venue> = listOf(
        Venue(
            id = "v_saadat_derma",
            nameEn = "L Aura Aesthetics Penthouse",
            nameFa = "پنت‌هاوس زیبایی و درماتولوژی لوکس لورا",
            category = CategoryId.AESTHETICS,
            cityId = "tehran",
            districtId = "tehran_saadat_abad",
            districtNameEn = "Saadat Abad",
            districtNameFa = "سعادت‌آباد",
            addressEn = "Sarv West St, Kaj Tower, Floor 14",
            addressFa = "سعادت‌آباد، خیابان سرو غربی، برج کاج، طبقه ۱۴",
            rating = 4.96,
            reviewCount = 142,
            imageResId = R.drawable.img_clinic_hero,
            taglineEn = "Architectural skin suites with Candela GentleMax Pro & Morpheus8",
            taglineFa = "سوئیت‌های جراحی پوست مجهز به کندلا جنتل‌مکس پرو ۲۰۲۵ و مورفئوس۸",
            descriptionEn = "Designed for board-certified dermatologists and aesthetic surgeons. Features negative-pressure laser air filtration, Zeiss surgical loupes, sterile disposables, and dedicated nurse assistant support.",
            descriptionFa = "طراحی شده ویژه متخصصین پوست، مو و جراحان زیبایی. مجهز به تهویه فشار منفی، استرلیزاسیون پیشرفته، وسایل یکبار مصرف جراحی و نرس اختصاصی.",
            totalChairs = 8,
            activeSlots = 3,
            baseHourlyToman = 1850000L,
            certifications = listOf("گواهی ایزو کلین‌روم ۱۳۴۸۵", "مجوز درجه A+ وزارت بهداشت", "مرکز رسمی تاییدیه کندلا"),
            equipmentList = listOf("Candela GentleMax Pro Plus 2025", "InMode Morpheus8 RF", "Fotona Dynamis Pro", "دستگاه سانتریفیوژ PRP"),
            amenities = listOf("پارکینگ اختصاصی ولت بیمار", "نرس و دستیار اتاق عمل", "وای‌فای پرسرعت مدیکال", "لانژ استراحت پزشکان"),
            medicalDirectorEn = "Dr. Arash Radmanesh, MD",
            medicalDirectorFa = "دکتر آرش رادمنش (نظام پزشکی ۱۴۸۹۲۰)",
            medicalLicenseNumber = "MC-148920"
        ),
        Venue(
            id = "v_smile_studio",
            nameEn = "Ceramica Dental & Smile Design Atelier",
            nameFa = "آتلیه دندانپزشکی و طراحی لبخند سرامیکا",
            category = CategoryId.DENTAL,
            cityId = "tehran",
            districtId = "tehran_saadat_abad",
            districtNameEn = "Saadat Abad",
            districtNameFa = "سعادت‌آباد",
            addressEn = "Shahrdari Blvd, Opal Luxury Suites, Suite 802",
            addressFa = "سعادت‌آباد، بلوار شهرداری، مجتمع اپال، واحد ۸۰۲",
            rating = 4.98,
            reviewCount = 189,
            imageResId = R.drawable.img_dental_operatory,
            taglineEn = "Castellini Italian operatories with 3Shape Trios 5 intraoral scanning",
            taglineFa = "یونیت‌های ایتالیایی کاستلینی با اسکنر دیجیتال تریوس ۵ و میکروسکوپ زایس",
            descriptionEn = "The benchmark environment for cosmetic dentists, prosthodontists, and veneer specialists. Seamless digital workflow from digital smile preview to porcelain cementation.",
            descriptionFa = "محیط استاندارد ویژه متخصصین پروتز و دندانپزشکی زیبایی. مسیر یکپارچه اسکن دیجیتال لبخند تا تحویل لمینت‌های سرامیکی در محیط استریل.",
            totalChairs = 6,
            activeSlots = 2,
            baseHourlyToman = 2200000L,
            certifications = listOf("تاییدیه آکادمی ایمپلنت DGI آلمان", "مجوز درجه یک وزارت بهداشت", "اتوکلاو کلاس B تایید شده"),
            equipmentList = listOf("Castellini Skema 8 Operatory", "3Shape Trios 5 Wireless Scanner", "Zeiss Extaro 300 Dental Microscope", "موتور جراحی NSK Surgic Pro"),
            amenities = listOf("لابراتوار دیجیتال سرامیست درون‌مرکزی", "تکنسین استریلیزاسیون آن‌کال", "استودیو عکاسی لبخند", "محیط آرامش‌بخش بیمار"),
            medicalDirectorEn = "Dr. Nima Farhadi, DDS",
            medicalDirectorFa = "دکتر نیما فرهادی (نظام دندانپزشکی ۹۸۳۱۱)",
            medicalLicenseNumber = "DDS-98311"
        ),
        Venue(
            id = "v_gohardasht_salon",
            nameEn = "Maison Noir Luxury Hair & Beauty Salon",
            nameFa = "عمارت زیبایی و سالن تخصصی مزون نوآر",
            category = CategoryId.SALONS,
            cityId = "karaj",
            districtId = "karaj_gohardasht",
            districtNameEn = "Gohardasht",
            districtNameFa = "گوهردشت کرج",
            addressEn = "Main Gohardasht Ave, 7th West St, Villa 12",
            addressFa = "کرج، گوهردشت، خیابان هفتم غربی، ویلای شماره ۱۲",
            rating = 4.92,
            reviewCount = 95,
            imageResId = R.drawable.img_salon_lounge,
            taglineEn = "Bespoke styling chairs & Japanese head spa pods with Dyson Pro stations",
            taglineFa = "صندلی‌های VIP تاکارا بلمونت و پاد هداکستریم اسپا ژاپنی با سشوار دایسون",
            descriptionEn = "Created for master colorists, balayage artists, and high-profile beauty stylists. Includes professional backwash beds with micro-mist steam, daylight CRI 98 lighting, and private client cabines.",
            descriptionFa = "طراحی شده ویژه مستر کالریست‌ها، بالیاژکاران و استایلیست‌های حرفه‌ای. دارای تخت‌های شستشوی مه ژاپنی، نور استاندارد بدون خطا و فضای تولید محتوا.",
            totalChairs = 12,
            activeSlots = 5,
            baseHourlyToman = 980000L,
            certifications = listOf("پروانه صنف آرایشگران ممتاز", "گواهی بهداشت حرفه‌ای محیطی"),
            equipmentList = listOf("Takara Belmont Styling Chairs", "Japanese Head Spa Steam Basin", "Dyson Supersonic Pro Hairdryers", "میکرومیست ازون تراپی مو"),
            amenities = listOf("لابراتوار اختصاصی ترکیب رنگ", "بار پذیرایی و نوشیدنی", "رینگ‌لایت و سافت‌باکس عکاسی", "خدمات لنژری حوله‌های یکبارمصرف"),
            medicalDirectorEn = "Shiva Ghasemi (Creative Director)",
            medicalDirectorFa = "شیوا قاسمی (مدیر هنری و عضو اتحادیه)",
            medicalLicenseNumber = "ART-55104"
        )
    )

    private val _slots = MutableStateFlow<List<RealtimeSlot>>(
        listOf(
            RealtimeSlot("slot_1", "v_saadat_derma", "L Aura Aesthetics Penthouse", "پنت‌هاوس درماتولوژی لورا", "Laser Suite 01 (GentleMax Pro)", "سوئیت لیزر ۰۱ (جنتل‌مکس پرو)", SlotType.LASER_SUITE, SlotStatus.AVAILABLE, null, null, 1850000L, listOf("Candela GentleMax Pro Plus", "Zimmer Cryo 6 Chiller", "دستگاه تصفیه دود جراحی"), true, true, 12),
            RealtimeSlot("slot_2", "v_saadat_derma", "L Aura Aesthetics Penthouse", "پنت‌هاوس درماتولوژی لورا", "VIP Injectables Suite 02", "سوئیت تزریقات VIP شماره ۰۲", SlotType.BED, SlotStatus.IN_PROCEDURE, "دکتر علیرضا رستمی", "کانتورینگ فک و جوانسازی پلی‌نوکلئوتید", 1950000L, listOf("تخت برقی ۴ موتوره جراحی", "چراغ LED سرد جراحی", "رگ‌یاب AccuVein"), true, true, 45),
            RealtimeSlot("slot_3", "v_saadat_derma", "L Aura Aesthetics Penthouse", "پنت‌هاوس درماتولوژی لورا", "Morpheus8 RF Suite 03", "اتاق هایفوتراپی و مورفئوس۸", SlotType.BED, SlotStatus.SANITIZING, null, null, 2100000L, listOf("InMode Morpheus8 RF", "الترافرمر ۳", "دستگاه استریل UV-C"), true, false, 2),
            RealtimeSlot("slot_4", "v_smile_studio", "Ceramica Dental Atelier", "آتلیه دندانپزشکی سرامیکا", "Operatory Alpha (Castellini & Zeiss)", "سوئیت لمینت و جراحی ۱ (کاستلینی)", SlotType.OPERATORY, SlotStatus.AVAILABLE, null, null, 2200000L, listOf("Castellini Skema 8", "Zeiss Extaro 300 Microscope", "3Shape Trios 5 Wireless"), true, true, 8),
            RealtimeSlot("slot_5", "v_smile_studio", "Ceramica Dental Atelier", "آتلیه دندانپزشکی سرامیکا", "Operatory Beta (Laminate Bonding)", "یونیت ونیر کامپوزیت ۲", SlotType.OPERATORY, SlotStatus.RESERVED, "دکتر صبا افشار", "باندینگ ونیر لایه‌ای", 2000000L, listOf("Castellini Skema 6", "لایت کیور Valo Grand", "Airflow Prophylaxis Master"), true, true, 22),
            RealtimeSlot("slot_6", "v_gohardasht_salon", "Maison Noir Beauty Lounge", "عمارت زیبایی مزون نوآر", "Master Color Chair #01", "صندلی مستر رنگ و استایل VIP ۱", SlotType.CHAIR, SlotStatus.AVAILABLE, null, null, 980000L, listOf("Takara Belmont Recliner", "Dyson Supersonic Pro", "نور استاندارد CRI 98"), false, false, 10)
        )
    )
    override val slots: StateFlow<List<RealtimeSlot>> = _slots.asStateFlow()

    private val _bookingRequests = MutableStateFlow<List<BookingRequest>>(
        listOf(
            BookingRequest("req-101", "REQ-2026-8941", "مریم سهرابی", "09123456789", "تزریق فیلر و ژل لب روسی", "2026-11-05", "10:00 - 12:00 (صبح)", "v_saadat_derma", "پنت‌هاوس درماتولوژی لورا", "L Aura Aesthetics Penthouse", "slot_1", "سوئیت لیزر ۰۱", "Laser Suite 01", "نیاز به پک استریل یکبار مصرف", BookingRequestStatus.PENDING_CONFIRMATION, "امروز 10:20"),
            BookingRequest("req-102", "REQ-2026-7819", "دکتر فرهاد کیانی", "09121112233", "رزرو سوئیت جراحی پوست جهت سابسیژن", "2026-11-05", "16:00 - 18:00 (عصر)", "v_saadat_derma", "پنت‌هاوس درماتولوژی لورا", "L Aura Aesthetics Penthouse", "slot_2", "سوئیت تزریقات VIP شماره ۰۲", "VIP Injectables Suite 02", "دستیار جراحی همراه پزشک می‌باشد", BookingRequestStatus.PENDING_CONFIRMATION, "امروز 11:15"),
            BookingRequest("req-103", "REQ-2026-6502", "سحر تهرانی", "09358889900", "لمینت سرامیکی ۱۰ واحد فک بالا", "2026-11-04", "12:00 - 15:00 (ظهر)", "v_smile_studio", "آتلیه دندانپزشکی سرامیکا", "Ceramica Dental Atelier", "slot_4", "سوئیت لمینت و جراحی ۱", "Operatory Alpha", "جلسه تحویل و چسباندن نهایی لمینت‌ها", BookingRequestStatus.CONFIRMED, "دیروز 18:30")
        )
    )
    override val bookingRequests: StateFlow<List<BookingRequest>> = _bookingRequests.asStateFlow()

    private val _escrowTransactions = MutableStateFlow<List<EscrowTransaction>>(
        listOf(
            EscrowTransaction(
                id = "esc-901",
                contractCode = "ESC-2026-901-TH",
                practitionerName = "دکتر نیلوفر مهدوی",
                licenseId = "MC-110482",
                venueName = "پنت‌هاوس زیبایی و درماتولوژی لورا",
                slotName = "جوانسازی فوتونا ۴ بعدی و کانتورینگ",
                grossAmountToman = 18500000L,
                platformFeeToman = 1480000L, // 8%
                clinicPayoutToman = 17020000L,
                status = EscrowStatus.HELD_IN_ESCROW,
                milestoneStep = 1,
                createdAt = "امروز 10:00",
                estimatedReleaseTime = "در انتظار ورود و احراز هویت بیمار (حالت آزمایشی)",
                paymentRefId = "908213847291",
                trackingNumber = "SHP-8923419082",
                shebaNumber = "IR910120000000001829304192",
                patientNationalId = "0019283741"
            ),
            EscrowTransaction(
                id = "esc-902",
                contractCode = "ESC-2026-902-TH",
                practitionerName = "دکتر علیرضا رستمی",
                licenseId = "MC-148920",
                venueName = "پنت‌هاوس زیبایی و درماتولوژی لورا",
                slotName = "اندولیفت غبغب و زاویه فک",
                grossAmountToman = 24000000L,
                platformFeeToman = 1920000L,
                clinicPayoutToman = 22080000L,
                status = EscrowStatus.PATIENT_ARRIVED,
                milestoneStep = 2,
                createdAt = "امروز 09:30",
                estimatedReleaseTime = "در حال انجام فرآیند درمانی (حالت آزمایشی)",
                paymentRefId = "447812930488",
                trackingNumber = "SHP-4478129304",
                patientCheckInTimestamp = "امروز 10:15",
                patientNationalIdVerified = true,
                shebaNumber = "IR910120000000001829304192",
                patientNationalId = "0074829106"
            ),
            EscrowTransaction(
                id = "esc-903",
                contractCode = "ESC-2026-903-TH",
                practitionerName = "دکتر صبا افشار",
                licenseId = "DDS-98311",
                venueName = "آتلیه دندانپزشکی و طراحی لبخند سرامیکا",
                slotName = "۱۰ واحد ونیر کامپوزیت لایه‌ای ماگنه",
                grossAmountToman = 42000000L,
                platformFeeToman = 3360000L,
                clinicPayoutToman = 38640000L,
                status = EscrowStatus.PROCEDURE_DONE,
                milestoneStep = 3,
                createdAt = "امروز 08:30",
                estimatedReleaseTime = "پنجره تضمین ۱۲۰ دقیقه‌ای فعال (شبیه‌ساز)",
                paymentRefId = "992182736412",
                trackingNumber = "SHP-9921827364",
                patientCheckInTimestamp = "امروز 09:00",
                patientNationalIdVerified = true,
                procedureSignOffNotes = "درمان با رضایت کامل بیمار و بدون عارضه پایان یافت.",
                doctorSignOffTimestamp = "امروز 11:30",
                guaranteeMinutesRemaining = 45,
                shebaNumber = "IR480170000000009928172635",
                patientNationalId = "0061928372"
            ),
            EscrowTransaction(
                id = "esc-904",
                contractCode = "ESC-2026-904-KR",
                practitionerName = "مهسا نوری (مستر استایلیست)",
                licenseId = "ART-55104",
                venueName = "عمارت زیبایی و سالن تخصصی مزون نوآر",
                slotName = "بالیاژ روسی و پروتئین فیوژن",
                grossAmountToman = 14000000L,
                platformFeeToman = 1120000L,
                clinicPayoutToman = 12880000L,
                status = EscrowStatus.RELEASED,
                milestoneStep = 4,
                createdAt = "امروز 05:00",
                estimatedReleaseTime = "تسویه به شماره شبا بانک ملت (آزمایشی)",
                paymentRefId = "128472910233",
                trackingNumber = "SHP-1284729102",
                patientCheckInTimestamp = "امروز 05:30",
                patientNationalIdVerified = true,
                procedureSignOffNotes = "تراپی کامل و خدمات استایل به اتمام رسید.",
                doctorSignOffTimestamp = "امروز 07:15",
                guaranteeMinutesRemaining = 0,
                shebaNumber = "IR090560000000008827162534",
                patientNationalId = "0451829484"
            )
        )
    )
    override val escrowTransactions: StateFlow<List<EscrowTransaction>> = _escrowTransactions.asStateFlow()

    private val _applications = MutableStateFlow<List<OnboardingApplication>>(
        listOf(
            OnboardingApplication(
                id = "app-1",
                applicantType = "clinic",
                activityTypeFa = "کلینیک پوست و لیزر",
                businessName = "کلینیک دندانپزشکی دیجیتال مهرشهر کرج",
                applicantName = "دکتر کامران شایگان",
                phoneNumber = "09123456789",
                cityAndDistrict = "کرج - مهرشهر فاز ۱",
                chairsOrBedsCount = 4,
                shortDescription = "مجهز به CBCT سه بعدی Planmeca و یونیت‌های Sirona",
                medicalCouncilOrGuildId = "MED-KRJ-84920",
                shebaNumber = "IR480170000000009928172635",
                status = ApplicationStatus.PENDING,
                submittedAt = "۲ روز پیش"
            ),
            OnboardingApplication(
                id = "app-2",
                applicantType = "salon",
                activityTypeFa = "سالن زیبایی VIP",
                businessName = "مرکز تریکولوژی و مراقبت مو نیاوران",
                applicantName = "الهام صبوری",
                phoneNumber = "09129876543",
                cityAndDistrict = "تهران - نیاوران",
                chairsOrBedsCount = 6,
                shortDescription = "دارای درماتوسکوپ HairMetrix و تخت‌های ماساژ سر ژاپنی",
                medicalCouncilOrGuildId = "GUILD-THR-19284",
                shebaNumber = "IR910120000000001829304192",
                status = ApplicationStatus.APPROVED,
                submittedAt = "۵ روز پیش"
            )
        )
    )
    override val applications: StateFlow<List<OnboardingApplication>> = _applications.asStateFlow()

    private val _tickets = MutableStateFlow<List<SupportTicket>>(
        listOf(
            SupportTicket(
                id = "t-101",
                ticketNumber = "TKT-8902",
                senderName = "پنت‌هاوس درماتولوژی لورا",
                venueName = "L Aura Aesthetics Penthouse",
                subject = "استعلام واریز پایا قرارداد شماره ESC-2026-902-TH",
                category = "escrow",
                priority = "high",
                status = "answered",
                createdAt = "امروز 09:40",
                messages = listOf(
                    TicketMessage("m-1", "مدیریت کلینیک لورا", false, "سلام، با توجه به اتمام پنجره ۲ ساعته تضمین، وضعیت تسویه شبا در چه مرحله‌ای قرار دارد؟", "09:40"),
                    TicketMessage("m-2", "پشتیبانی تسویه اورا لوکس", true, "با سلام و درود؛ سیکل پایای بانک مرکزی در ساعت ۱۳:۴۵ تسویه و کد ارجاع ساتنا در پنل شما درج می‌گردد.", "09:55")
                )
            ),
            SupportTicket(
                id = "t-102",
                ticketNumber = "TKT-8901",
                senderName = "آتلیه دندانپزشکی سرامیکا",
                venueName = "Ceramica Dental Atelier",
                subject = "درخواست کالیبراسیون دوره‌ای اسکنر 3Shape Trios 5",
                category = "technical",
                priority = "normal",
                status = "open",
                createdAt = "دیروز 17:20",
                messages = listOf(
                    TicketMessage("m-3", "دکتر نیما فرهادی", false, "سلام، جهت دقت میکرونی لمینت‌های هفته آینده نیاز به هماهنگی با تکنسین مقیم جهت کالیبره اپتیکال داریم.", "17:20")
                )
            )
        )
    )
    override val tickets: StateFlow<List<SupportTicket>> = _tickets.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<SecurityAuditLog>>(
        listOf(
            SecurityAuditLog("log-1", "2026-11-04 10:50:13", "auth", Severity.INFO, "ورود مجاز مدیر کلینیک (دمو)", "Clinic Director Authorized Login (Demo)", "ورود به پنل با نشست آزمایشی صادر گردید.", "185.192.112.4"),
            SecurityAuditLog("log-2", "2026-11-04 10:16:58", "escrow", Severity.INFO, "احراز هویت بیمار و تایید ورود (دمو)", "Patient Arrival & National ID Verification (Demo)", "تراکنش esc-902، کد ملی 0074829106 توسط منشی پذیرش بررسی و ثبت شد.", "185.192.112.4"),
            SecurityAuditLog("log-3", "2026-11-04 10:06:58", "security", Severity.INFO, "ثبت امن پایگاه‌داده محلی Room", "Local Room Persistence Initialized", "جداول محلی با رمزنگاری AES-GCM و کلید اختصاصی Keystore آماده‌سازی شدند.", "127.0.0.1"),
            SecurityAuditLog("log-4", "2026-11-04 09:12:44", "security", Severity.WARNING, "هشدار محدودسازی تلاش‌های ناموفق ورود", "Brute Force Protection Lockout Active", "آی‌پی ناشناس پس از ۳ بار تلاش با گذرواژه اشتباه به مدت ۶۰ ثانیه بلاک گردید.", "127.0.0.1")
        )
    )
    override val auditLogs: StateFlow<List<SecurityAuditLog>> = _auditLogs.asStateFlow()

    override val beforeAfterCases: List<BeforeAfterCase> = listOf(
        BeforeAfterCase(
            id = "case_dental_01",
            titleEn = "Ultra-thin Porcelain Ceramic Laminates (10 Units)",
            titleFa = "طراحی لبخند و لمینت سرامیکی فوق‌نازک ۱۰ واحدی فک بالا",
            category = CategoryId.DENTAL,
            clinicNameEn = "Ceramica Dental Atelier (Saadat Abad)",
            clinicNameFa = "آتلیه دندانپزشکی سرامیکا (سعادت‌آباد)",
            doctorNameEn = "Dr. Nima Farhadi, DDS",
            doctorNameFa = "دکتر نیما فرهادی",
            treatmentEn = "CAD/CAM E-Max pressed veneers with minimal preparation, micro-incisal translucency and digital gum symmetry alignment.",
            treatmentFa = "ونیرهای سرامیکی فلدسپاتیک و E-Max با آماده‌سازی مینیمال ۰.۳ میلیمتر، ایجاد ترنسلوسنسی طبیعی لبه انسیزال با اسکن دیجیتال تریوس ۵.",
            imageResId = R.drawable.img_dental_laminate,
            metricsEn = listOf(
                ClinicalMetric("Color Transition", "A3.5 Dull -> BL1 Ultra-White"),
                ClinicalMetric("Prep Depth", "0.3mm Ultra-conservative"),
                ClinicalMetric("Units Delivered", "10 Upper Anterior Zone"),
                ClinicalMetric("Operatory Slot", "Suite Alpha (3 Sessions)")
            ),
            metricsFa = listOf(
                ClinicalMetric("تغییر رنگ پایه", "از دندان کدر A3.5 به سفیدی طبیعی BL1"),
                ClinicalMetric("میزان تراش مینا", "۰.۳ میلی‌متر محافظه‌کارانه"),
                ClinicalMetric("تعداد واحدها", "۱۰ دندان ناحیه لبخند قدامی"),
                ClinicalMetric("یونیت استفاده شده", "سوئیت کاستلینی آلفا (۳ جلسه)")
            ),
            notesEn = "Practitioner rented Operatory Alpha with 3Shape Trios intraoral scanning. Complete case finalized within 5 days with zero patient sensitivity.",
            notesFa = "پزشک محترم با اجاره سوئیت آلفا و بهره‌مندی از اسکنر داخل دهانی تریوس ۵ و تکنسین سرامیست، پروسه را در ۵ روز با رضایت قطعی بیمار به اتمام رساندند."
        ),
        BeforeAfterCase(
            id = "case_skin_02",
            titleEn = "Fractional Skin Laser & Polynucleotide Glass-Skin Protocol",
            titleFa = "پروتکل پوست شیشه‌ای (Glass Skin) با لیزر فرکشنال و DNA سالمون",
            category = CategoryId.AESTHETICS,
            clinicNameEn = "L Aura Aesthetics Penthouse (Saadat Abad)",
            clinicNameFa = "پنت‌هاوس زیبایی و درماتولوژی لورا (سعادت‌آباد)",
            doctorNameEn = "Dr. Arash Radmanesh, MD",
            doctorNameFa = "دکتر آرش رادمنش",
            treatmentEn = "Dual-wavelength laser resurfacing paired with Salmon DNA polynucleotide micro-droplet mesotherapy to restore collagen matrix and pore refinement.",
            treatmentFa = "جوانسازی با لیزر دوگانه کندلا و مزوتراپی میکرو قطره‌ای پلی‌نوکلئوتید سالمون جهت بازسازی ماتریکس کلاژن، رفع منافذ باز و ایجاد شفافیت کریستالی.",
            imageResId = R.drawable.img_skin_rejuv,
            metricsEn = listOf(
                ClinicalMetric("Pore Refinement", "+84% Visual Smoothness"),
                ClinicalMetric("Collagen Density", "+62% Elastin Remodeling"),
                ClinicalMetric("Downtime", "Under 36 Hours"),
                ClinicalMetric("Operatory Suite", "Penthouse Laser Suite 01")
            ),
            metricsFa = listOf(
                ClinicalMetric("بهبود منافذ باز", "۸۴٪ صافی یکدست پوست"),
                ClinicalMetric("تراکم کلاژن", "۶۲٪ بازسازی الاستین بافتی"),
                ClinicalMetric("دوران نقاهت", "کمتر از ۳۶ ساعت"),
                ClinicalMetric("سوئیت کلینیک", "سوئیت لیزر فشار منفی ۰۱")
            ),
            notesEn = "Performed in negative-pressure sterile laser suite with continuous cold air cryo cooling. Escrow payment released upon verified patient sign-off.",
            notesFa = "انجام شده در سوئیت لیزر دارای تصفیه فشار منفی و خنک‌کننده هوای سرد کرایو با ترخیص رضایت‌بخش بیمار و تایید دیجیتال جهت تسویه اسکرو."
        )
    )

    override val storyHighlights: List<StoryHighlight> = listOf(
        StoryHighlight(
            id = "hl_skin",
            titleFa = "لیزر و پوست",
            titleEn = "Laser & Skin",
            coverResId = R.drawable.img_skin_rejuv,
            slides = listOf(
                StorySlide("s_skin_1", "پروتکل پوست شیشه‌ای DNA سالمون", "Polynucleotide Salmon DNA Glass Skin", "دکتر آرش رادمنش", "Dr. Arash Radmanesh, MD", "پنت‌هاوس درماتولوژی لورا", "L Aura Aesthetics Penthouse", R.drawable.img_skin_rejuv, "بستن منافذ باز و ۶۲٪ تحریک کلاژن‌سازی", "Refined pore matrix and 62% collagen regeneration through fractional laser resurfacing.", "پروتکل گلس اسکین کندلا", "Glass Skin Protocol - Candela", 6),
                StorySlide("s_skin_2", "سوئیت‌های جراحی لیزر فشار منفی", "Negative-Pressure Surgical Laser Suites", "دکتر بابک سلیمی", "Dr. Babak Salimi, MD", "کلینیک الیسیوم", "Elysium Aesthetic Clinic", R.drawable.img_clinic_hero, "مجهز به کولینگ زیمر آلمان و نرس اختصاصی", "Equipped with Zimmer Cryo 6, 4-motor surgery couch, and full assistant nurse support.", "دارای تاییدیه درجه A+ بهداشت", "Grade A+ Certified", 5)
            )
        ),
        StoryHighlight(
            id = "hl_dental",
            titleFa = "طراحی لبخند",
            titleEn = "Smile Design",
            coverResId = R.drawable.img_dental_laminate,
            slides = listOf(
                StorySlide("s_dent_1", "۱۰ واحد ونیر فلدسپاتیک فوق نازک", "10 Units Ultra-Thin Feldspathic Veneers", "دکتر نیما فرهادی", "Dr. Nima Farhadi, DDS", "آتلیه دندانپزشکی سرامیکا", "Ceramica Dental Atelier", R.drawable.img_dental_laminate, "تراش محافظه‌کارانه ۰.۳ میلیمتر با میکروسکوپ زایس و اسکن ۳Shape", "Ultra-conservative 0.3mm preparation guided by Zeiss microscope and 3Shape Trios 5 scan.", "لمینت سرامیکی E-Max CAD/CAM", "E-Max CAD/CAM Porcelain Laminate", 6),
                StorySlide("s_dent_2", "یونیت‌های ایتالیایی با دید پانوراما", "Castellini Italian Operatories", "دکتر سحر رستمی", "Dr. Sahar Rostami, DDS", "لانژ میکرودنتال فرشته", "Fereshteh Micro-Dental Lounge", R.drawable.img_dental_operatory, "طراحی ارگونومیک، مانیتورینگ فورکی و استریلیزاسیون اتوکلاو کلاس ب", "Ergonomic operatory, in-room 4K broadcast, and class-B autoclave sterilization.", "یونیت کاستلینی Skema 8", "Castellini Skema 8 Operatory", 5)
            )
        ),
        StoryHighlight(
            id = "hl_salon",
            titleFa = "سالن‌های VIP",
            titleEn = "VIP Salons",
            coverResId = R.drawable.img_salon_lounge,
            slides = listOf(
                StorySlide("s_sal_1", "صندلی مستر بالیاژ با نور CRI 98", "Bespoke Balayage Station", "شیوا قاسمی", "Shiva Ghasemi", "عمارت زیبایی مزون نوآر", "Maison Noir Beauty Lounge", R.drawable.img_salon_lounge, "صندلی تاکارا بلمونت، هداسپای ژاپنی و لاین رنگ اختصاصی", "Takara Belmont styling chairs, Japanese head spa micro-mist steam, and Dyson Pro hair stations.", "سوئیت کالریست و بالیاژ", "Master Color & Balayage Suite", 6)
            )
        )
    )

    init {
        // Synchronize with Room Database if provided
        database?.let { db ->
            repositoryScope.launch {
                try {
                    seedDatabaseIfEmpty(db)
                    loadPersistedStateFromRoom(db)
                } catch (_: Exception) {
                    // Fallback to in-memory state gracefully if DB initialization is interrupted
                }
            }
        }
    }

    private suspend fun seedDatabaseIfEmpty(db: AuraDatabase) {
        // Seed clinics
        val clinicEntities = venues.map { v ->
            ClinicEntity(
                id = v.id,
                nameEn = v.nameEn,
                nameFa = v.nameFa,
                category = v.category,
                cityId = v.cityId,
                districtId = v.districtId,
                districtNameEn = v.districtNameEn,
                districtNameFa = v.districtNameFa,
                addressEn = v.addressEn,
                addressFa = v.addressFa,
                rating = v.rating,
                reviewCount = v.reviewCount,
                taglineEn = v.taglineEn,
                taglineFa = v.taglineFa,
                descriptionEn = v.descriptionEn,
                descriptionFa = v.descriptionFa,
                totalChairs = v.totalChairs,
                activeSlots = v.activeSlots,
                baseHourlyToman = v.baseHourlyToman,
                certifications = v.certifications,
                equipmentList = v.equipmentList,
                amenities = v.amenities,
                medicalDirectorEn = v.medicalDirectorEn,
                medicalDirectorFa = v.medicalDirectorFa,
                medicalLicenseNumber = v.medicalLicenseNumber
            )
        }
        db.clinicDao().insertAll(clinicEntities)

        // Seed units
        val unitEntities = _slots.value.map { s ->
            ClinicUnitEntity(
                id = s.id,
                venueId = s.venueId,
                venueNameEn = s.venueNameEn,
                venueNameFa = s.venueNameFa,
                roomNameEn = s.roomNameEn,
                roomNameFa = s.roomNameFa,
                type = s.type,
                status = s.status,
                currentPractitioner = s.currentPractitioner,
                patientProcedure = s.patientProcedure,
                pricePerHourToman = s.pricePerHourToman,
                equipment = s.equipment,
                assistantIncluded = s.assistantIncluded,
                autoclaveReady = s.autoclaveReady,
                lastSanitizedMinutesAgo = s.lastSanitizedMinutesAgo
            )
        }
        db.clinicUnitDao().insertAll(unitEntities)

        // Seed initial appointments
        _bookingRequests.value.forEach { b ->
            db.appointmentDao().insert(
                AppointmentEntity(
                    id = b.id,
                    trackingCode = b.trackingCode,
                    fullName = b.fullName,
                    phoneNumber = b.phoneNumber,
                    desiredService = b.desiredService,
                    suggestedDate = b.suggestedDate,
                    suggestedTime = b.suggestedTime,
                    venueId = b.venueId,
                    venueNameFa = b.venueNameFa,
                    venueNameEn = b.venueNameEn,
                    slotId = b.slotId,
                    slotNameFa = b.slotNameFa,
                    slotNameEn = b.slotNameEn,
                    notes = b.notes,
                    status = b.status,
                    isOfflineLocal = true,
                    isServerSynced = false,
                    createdAt = b.createdAt
                )
            )
        }

        // Seed escrow transactions with AES-GCM encrypted sensitive fields
        val escrowEntities = _escrowTransactions.value.map { tx ->
            EscrowTransactionEntity(
                id = tx.id,
                contractCode = tx.contractCode,
                practitionerName = tx.practitionerName,
                licenseId = tx.licenseId,
                venueName = tx.venueName,
                slotName = tx.slotName,
                grossAmountToman = tx.grossAmountToman,
                platformFeeToman = tx.platformFeeToman,
                clinicPayoutToman = tx.clinicPayoutToman,
                status = tx.status,
                milestoneStep = tx.milestoneStep,
                createdAt = tx.createdAt,
                estimatedReleaseTime = tx.estimatedReleaseTime,
                paymentRefId = tx.paymentRefId,
                trackingNumber = tx.trackingNumber,
                patientCheckInTimestamp = tx.patientCheckInTimestamp,
                patientNationalIdVerified = tx.patientNationalIdVerified,
                procedureSignOffNotes = tx.procedureSignOffNotes,
                doctorSignOffTimestamp = tx.doctorSignOffTimestamp,
                guaranteeMinutesRemaining = tx.guaranteeMinutesRemaining,
                shebaEncrypted = KeystoreEncryptionManager.encrypt(tx.shebaNumber),
                shebaMasked = tx.shebaNumber.take(4) + "****" + tx.shebaNumber.takeLast(4),
                patientNationalIdMasked = tx.patientNationalId.take(3) + "****" + tx.patientNationalId.takeLast(2),
                isDemo = true
            )
        }
        db.escrowTransactionDao().insertAll(escrowEntities)
    }

    private fun loadPersistedStateFromRoom(db: AuraDatabase) {
        repositoryScope.launch {
            db.appointmentDao().getAllAppointments().collect { appointmentEntities ->
                if (appointmentEntities.isNotEmpty()) {
                    _bookingRequests.value = appointmentEntities.map { a ->
                        BookingRequest(
                            id = a.id,
                            trackingCode = a.trackingCode,
                            fullName = a.fullName,
                            phoneNumber = a.phoneNumber,
                            desiredService = a.desiredService,
                            suggestedDate = a.suggestedDate,
                            suggestedTime = a.suggestedTime,
                            venueId = a.venueId,
                            venueNameFa = a.venueNameFa,
                            venueNameEn = a.venueNameEn,
                            slotId = a.slotId,
                            slotNameFa = a.slotNameFa,
                            slotNameEn = a.slotNameEn,
                            notes = a.notes,
                            status = a.status,
                            createdAt = a.createdAt,
                            updatedAt = a.updatedAt
                        )
                    }
                }
            }
        }
    }

    override fun toggleSlotStatus(slotId: String, newStatus: SlotStatus) {
        val currentList = _slots.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == slotId }
        if (index != -1) {
            val old = currentList[index]
            val updated = old.copy(
                status = newStatus,
                lastSanitizedMinutesAgo = if (newStatus == SlotStatus.SANITIZING) 0 else old.lastSanitizedMinutesAgo
            )
            currentList[index] = updated
            _slots.value = currentList

            database?.let { db ->
                repositoryScope.launch {
                    db.clinicUnitDao().updateStatus(slotId, newStatus, updated.lastSanitizedMinutesAgo)
                }
            }

            addAuditLog(
                type = "slot",
                severity = Severity.INFO,
                titleFa = "تغییر وضعیت سوئیت: ${old.roomNameFa}",
                titleEn = "Slot Status Transition: ${old.roomNameEn}",
                details = "وضعیت از ${old.status.name} به ${newStatus.name} تغییر یافت."
            )
        }
    }

    override fun submitBookingRequest(
        fullName: String,
        phoneNumber: String,
        desiredService: String,
        suggestedDate: String,
        suggestedTime: String,
        venueId: String,
        venueNameFa: String,
        venueNameEn: String,
        slotId: String?,
        slotNameFa: String?,
        slotNameEn: String?,
        notes: String
    ): BookingRequest {
        val cleanName = SecurityManager.sanitizeInput(fullName)
        val cleanPhone = SecurityManager.sanitizeInput(phoneNumber)
        val cleanNotes = SecurityManager.sanitizeInput(notes)

        // Duplicate prevention check: check for identical active appointment
        val existingDuplicate = _bookingRequests.value.find { b ->
            b.venueId == venueId &&
            b.slotId == slotId &&
            b.suggestedDate == suggestedDate &&
            b.suggestedTime == suggestedTime &&
            b.status != BookingRequestStatus.REJECTED
        }

        if (existingDuplicate != null) {
            addAuditLog(
                type = "booking_collision",
                severity = Severity.WARNING,
                titleFa = "جلوگیری از ثبت نوبت تکراری",
                titleEn = "Duplicate Appointment Prevented",
                details = "سوئیت در تاریخ $suggestedDate و ساعت $suggestedTime قبلاً رزرو گردیده است."
            )
            return existingDuplicate
        }

        val trackingCode = "REQ-2026-${(1000..9999).random()}"
        val createdAtStr = "امروز " + SimpleDateFormat("HH:mm", Locale.forLanguageTag("fa")).format(Date())
        val reqId = "req-${System.currentTimeMillis()}"

        val req = BookingRequest(
            id = reqId,
            trackingCode = trackingCode,
            fullName = cleanName,
            phoneNumber = cleanPhone,
            desiredService = desiredService,
            suggestedDate = suggestedDate,
            suggestedTime = suggestedTime,
            venueId = venueId,
            venueNameFa = venueNameFa,
            venueNameEn = venueNameEn,
            slotId = slotId,
            slotNameFa = slotNameFa,
            slotNameEn = slotNameEn,
            notes = cleanNotes,
            status = BookingRequestStatus.PENDING_CONFIRMATION,
            createdAt = createdAtStr
        )

        _bookingRequests.value = listOf(req) + _bookingRequests.value

        // Persist to Room local database
        database?.let { db ->
            repositoryScope.launch {
                db.appointmentDao().insert(
                    AppointmentEntity(
                        id = reqId,
                        trackingCode = trackingCode,
                        fullName = cleanName,
                        phoneNumber = cleanPhone,
                        desiredService = desiredService,
                        suggestedDate = suggestedDate,
                        suggestedTime = suggestedTime,
                        venueId = venueId,
                        venueNameFa = venueNameFa,
                        venueNameEn = venueNameEn,
                        slotId = slotId,
                        slotNameFa = slotNameFa,
                        slotNameEn = slotNameEn,
                        notes = cleanNotes,
                        status = BookingRequestStatus.PENDING_CONFIRMATION,
                        isOfflineLocal = true,
                        isServerSynced = false,
                        createdAt = createdAtStr
                    )
                )
            }
        }

        addAuditLog(
            type = "slot",
            severity = Severity.INFO,
            titleFa = "ثبت نوبت محلی (آفلاین): ${req.fullName} ($trackingCode)",
            titleEn = "Local Offline Booking Stored: ${req.fullName} ($trackingCode)",
            details = "ذخیره در پایگاه‌داده محلی Room با وضعیت «در انتظار تایید»."
        )
        return req
    }

    override fun updateBookingRequestStatus(requestId: String, newStatus: BookingRequestStatus) {
        val current = _bookingRequests.value.toMutableList()
        val index = current.indexOfFirst { it.id == requestId }
        if (index != -1) {
            val req = current[index]
            val updatedAtStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ROOT).format(Date())
            val updated = req.copy(
                status = newStatus,
                updatedAt = updatedAtStr
            )
            current[index] = updated
            _bookingRequests.value = current

            if (newStatus == BookingRequestStatus.CONFIRMED && req.slotId != null) {
                toggleSlotStatus(req.slotId, SlotStatus.RESERVED)
            }

            database?.let { db ->
                repositoryScope.launch {
                    db.appointmentDao().updateStatus(requestId, newStatus, updatedAtStr)
                }
            }

            addAuditLog(
                type = "slot",
                severity = Severity.INFO,
                titleFa = "به‌روزرسانی نوبت ${req.trackingCode} به ${if (newStatus == BookingRequestStatus.CONFIRMED) "تایید شده" else "رد شده"}",
                titleEn = "Booking Request ${req.trackingCode} set to ${newStatus.name}",
                details = "متقاضی: ${req.fullName}، مرکز: ${req.venueNameFa}"
            )
        }
    }

    override fun confirmPatientArrival(escrowId: String, receptionistId: String) {
        val current = _escrowTransactions.value.toMutableList()
        val index = current.indexOfFirst { it.id == escrowId }
        if (index != -1) {
            val tx = current[index]
            val checkInTime = "امروز " + SimpleDateFormat("HH:mm", Locale.ROOT).format(Date())
            val updated = tx.copy(
                milestoneStep = 2,
                status = EscrowStatus.PATIENT_ARRIVED,
                patientCheckInTimestamp = checkInTime,
                patientNationalIdVerified = true,
                estimatedReleaseTime = "در انتظار اتمام فرآیند و امضای پزشک (آزمایشی)"
            )
            current[index] = updated
            _escrowTransactions.value = current

            database?.let { db ->
                repositoryScope.launch {
                    db.escrowTransactionDao().markPatientArrived(
                        escrowId,
                        checkInTime,
                        updated.estimatedReleaseTime
                    )
                }
            }

            addAuditLog(
                type = "escrow",
                severity = Severity.INFO,
                titleFa = "احراز هویت و ورود بیمار (دمو): ${tx.patientNationalId}",
                titleEn = "Patient Arrival Verified (Demo): ${tx.patientNationalId}",
                details = "تراکنش ${tx.contractCode} توسط $receptionistId احراز هویت شد."
            )
        }
    }

    override fun signOffProcedure(escrowId: String, notes: String, doctorSignatureSvg: String) {
        val current = _escrowTransactions.value.toMutableList()
        val index = current.indexOfFirst { it.id == escrowId }
        if (index != -1) {
            val tx = current[index]
            val signOffTime = "امروز " + SimpleDateFormat("HH:mm", Locale.ROOT).format(Date())
            val updated = tx.copy(
                milestoneStep = 3,
                status = EscrowStatus.PROCEDURE_DONE,
                procedureSignOffNotes = notes,
                doctorSignOffTimestamp = signOffTime,
                guaranteeMinutesRemaining = 120,
                estimatedReleaseTime = "پایان پنجره تضمین ۱۲۰ دقیقه‌ای (شبیه‌ساز)"
            )
            current[index] = updated
            _escrowTransactions.value = current

            database?.let { db ->
                repositoryScope.launch {
                    db.escrowTransactionDao().markProcedureDone(
                        escrowId,
                        notes,
                        signOffTime,
                        updated.estimatedReleaseTime
                    )
                }
            }

            addAuditLog(
                type = "escrow",
                severity = Severity.INFO,
                titleFa = "ثبت اتمام درمان و امضای دیجیتال (دمو): ${tx.slotName}",
                titleEn = "Clinical Sign-off and Electronic Signature (Demo): ${tx.contractCode}",
                details = "پزشک اقدام به ثبت گواهی ترخیص نمود. تایمر ۱۲۰ دقیقه تضمین در حالت آزمایشی فعال شد."
            )
        }
    }

    override fun fastForwardGuarantee(escrowId: String) {
        val current = _escrowTransactions.value.toMutableList()
        val index = current.indexOfFirst { it.id == escrowId }
        if (index != -1) {
            current[index] = current[index].copy(guaranteeMinutesRemaining = 0)
            _escrowTransactions.value = current

            database?.let { db ->
                repositoryScope.launch {
                    db.escrowTransactionDao().fastForwardGuarantee(escrowId)
                }
            }

            addAuditLog(
                type = "escrow",
                severity = Severity.WARNING,
                titleFa = "تسریع زمان پنجره تضمین (شبیه‌ساز دمو): $escrowId",
                titleEn = "Guarantee Window Fast-Forwarded (Demo Sandbox): $escrowId",
                details = "پنجره ۱۲۰ دقیقه‌ای تضمین در محیط تست محلی صفر گردید."
            )
        }
    }

    override fun releaseEscrowToSheba(escrowId: String) {
        val current = _escrowTransactions.value.toMutableList()
        val index = current.indexOfFirst { it.id == escrowId }
        if (index != -1) {
            val tx = current[index]
            val estRelease = "شبیه‌ساز تسویه به شماره شبا ${tx.shebaNumber} (حالت آزمایشی)"
            val updated = tx.copy(
                milestoneStep = 4,
                status = EscrowStatus.RELEASED,
                guaranteeMinutesRemaining = 0,
                estimatedReleaseTime = estRelease
            )
            current[index] = updated
            _escrowTransactions.value = current

            database?.let { db ->
                repositoryScope.launch {
                    db.escrowTransactionDao().releaseEscrow(escrowId, estRelease)
                }
            }

            addAuditLog(
                type = "escrow",
                severity = Severity.INFO,
                titleFa = "شبیه‌سازی تسویه اسکرو به شماره شبا (آزمایشی): ${tx.shebaNumber}",
                titleEn = "Escrow Disbursement Simulation (Demo): ${tx.shebaNumber}",
                details = "عملیات مالی صرفاً در حالت آزمایشی محلی انجام شد. هیچ وجه واقعی انتقال نیافته است."
            )
        }
    }

    override fun submitOnboardingApplication(app: OnboardingApplication) {
        _applications.value = listOf(app) + _applications.value

        database?.let { db ->
            repositoryScope.launch {
                db.onboardingApplicationDao().insert(
                    OnboardingApplicationEntity(
                        id = app.id,
                        applicantType = app.applicantType,
                        activityTypeFa = app.activityTypeFa,
                        businessName = app.businessName,
                        applicantName = app.applicantName,
                        phoneNumber = app.phoneNumber,
                        cityAndDistrict = app.cityAndDistrict,
                        chairsOrBedsCount = app.chairsOrBedsCount,
                        shortDescription = app.shortDescription,
                        medicalCouncilOrGuildId = app.medicalCouncilOrGuildId,
                        shebaEncrypted = KeystoreEncryptionManager.encrypt(app.shebaNumber),
                        status = app.status,
                        submittedAt = app.submittedAt
                    )
                )
            }
        }

        addAuditLog(
            type = "onboarding",
            severity = Severity.INFO,
            titleFa = "ثبت‌نام درخواست همکاری جدید: ${app.businessName}",
            titleEn = "New Partner Application: ${app.businessName}",
            details = "نوع: ${app.activityTypeFa}، شهر: ${app.cityAndDistrict}، ظرفیت: ${app.chairsOrBedsCount} یونیت."
        )
    }

    override fun updateApplicationStatus(appId: String, newStatus: ApplicationStatus) {
        val current = _applications.value.toMutableList()
        val index = current.indexOfFirst { it.id == appId }
        if (index != -1) {
            val app = current[index]
            current[index] = app.copy(status = newStatus)
            _applications.value = current

            database?.let { db ->
                repositoryScope.launch {
                    db.onboardingApplicationDao().updateStatus(appId, newStatus)
                }
            }

            addAuditLog(
                type = "onboarding",
                severity = Severity.INFO,
                titleFa = "تغییر وضعیت درخواست همکاری: ${app.businessName} به ${newStatus.name}",
                titleEn = "Partner Application ${app.businessName} updated to ${newStatus.name}",
                details = "توسط مدیر ارشد سیستم در حالت آزمایشی بررسی شد."
            )
        }
    }

    override fun replyToTicket(ticketId: String, content: String) {
        val current = _tickets.value.toMutableList()
        val index = current.indexOfFirst { it.id == ticketId }
        if (index != -1) {
            val tkt = current[index]
            val newMsg = TicketMessage(
                id = "msg-${System.currentTimeMillis()}",
                senderName = "مدیر کلینیک",
                isStaff = false,
                content = content,
                timestamp = "اکنون"
            )
            current[index] = tkt.copy(
                status = "open",
                messages = tkt.messages + newMsg
            )
            _tickets.value = current

            database?.let { db ->
                repositoryScope.launch {
                    db.supportTicketDao().insertMessage(
                        TicketMessageEntity(
                            id = newMsg.id,
                            ticketId = ticketId,
                            senderName = newMsg.senderName,
                            isStaff = false,
                            content = content,
                            timestamp = "اکنون"
                        )
                    )
                }
            }
        }
    }

    override fun addAuditLog(type: String, severity: Severity, titleFa: String, titleEn: String, details: String) {
        val logId = "log-${System.currentTimeMillis()}-${(100..999).random()}"
        val timestampStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ROOT).format(Date())
        val log = SecurityAuditLog(
            id = logId,
            timestamp = timestampStr,
            type = type,
            severity = severity,
            titleFa = titleFa,
            titleEn = titleEn,
            details = details,
            ip = "185.192.112.4"
        )
        _auditLogs.value = listOf(log) + _auditLogs.value.take(199)

        database?.let { db ->
            repositoryScope.launch {
                db.auditLogDao().insert(
                    AuditLogEntity(
                        id = logId,
                        timestamp = timestampStr,
                        type = type,
                        severity = severity,
                        titleFa = titleFa,
                        titleEn = titleEn,
                        details = details,
                        ip = "185.192.112.4"
                    )
                )
            }
        }
    }
}
