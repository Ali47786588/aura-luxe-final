import React from 'react';
import { useAuraLuxe } from '../context/AuraLuxeContext';
import { toPersianDigits } from '../utils/localization';
import { Sparkles, Calendar, ChevronDown, ShieldCheck, Activity, Award } from 'lucide-react';

interface HeroSectionProps {
  onScrollToSlots: () => void;
}

export const HeroSection: React.FC<HeroSectionProps> = ({ onScrollToSlots }) => {
  const { language, selectedDistrict, openBooking } = useAuraLuxe();
  const isFa = language === 'FA';

  return (
    <section className="relative rounded-3xl overflow-hidden border border-[#232736] bg-[#11131A] shadow-2xl mx-4 lg:mx-8 mt-4">
      {/* Background Hero Banner with Dimmed Gradient Overlay */}
      <div className="absolute inset-0 z-0">
        <img
          src="/images/img_clinic_hero.jpg"
          alt="Aura Luxe Penthouse Clinic"
          className="w-full h-full object-cover object-center opacity-30 transform scale-105 filter blur-[1px]"
        />
        <div className="absolute inset-0 bg-gradient-to-t from-[#090A0D] via-[#090A0D]/80 to-transparent" />
        <div className="absolute inset-0 bg-gradient-to-r from-[#090A0D]/90 via-[#090A0D]/60 to-transparent" />
      </div>

      <div className="relative z-10 px-6 sm:px-10 py-10 lg:py-16 max-w-4xl">
        {/* Certification Badge */}
        <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-[#E5C378]/15 border border-[#E5C378]/30 mb-5 text-[#E5C378]">
          <Sparkles className="w-4 h-4" />
          <span className="text-xs font-semibold tracking-wide font-mono">
            {isFa
              ? `پلتفرم اختصاصی سوئیت‌های جراحی و زیبایی در ${selectedDistrict.nameFa}`
              : `Curated Surgical & Aesthetic Suites in ${selectedDistrict.nameEn}`}
          </span>
        </div>

        {/* Hero Title */}
        <h1 className="text-2xl sm:text-4xl lg:text-5xl font-extrabold text-white leading-tight tracking-tight mb-4">
          {isFa ? (
            <>
              اجاره آنلاین سوئیت‌های بالینی،{' '}
              <span className="text-transparent bg-clip-text bg-gradient-to-r from-[#FBF4E2] via-[#E5C378] to-[#C5A059]">
                اتاق‌های عمل لیزر
              </span>{' '}
              و یونیت‌های دندانپزشکی
            </>
          ) : (
            <>
              Next-Gen Marketplace for{' '}
              <span className="text-transparent bg-clip-text bg-gradient-to-r from-[#FBF4E2] via-[#E5C378] to-[#C5A059]">
                Aesthetic Operatories
              </span>{' '}
              & Dental Suites
            </>
          )}
        </h1>

        {/* Subtitle */}
        <p className="text-sm sm:text-base text-[#9CA3AF] max-w-2xl mb-8 leading-relaxed">
          {isFa
            ? `زیرساخت مدرن اشتراک فضا و تجهیزات دارای تاییدیه وزارت بهداشت ویژه پزشکان متخصص، دندانپزشکان و مستر استایلیست‌ها با سیستم تسویه امن اسکرو و ممیزی مدارک بالینی.`
            : `Premium on-demand infrastructure for board-certified dermatologists, cosmetic dentists, and master stylists. Fully equipped with ISO-certified sterilizations and regulated escrow settlements.`}
        </p>

        {/* Action Buttons */}
        <div className="flex flex-wrap items-center gap-4 mb-10">
          <button
            onClick={() => openBooking()}
            className="flex items-center gap-2.5 px-6 py-3 rounded-2xl bg-gradient-to-r from-[#E5C378] to-[#C5A059] hover:from-[#FBF4E2] hover:to-[#E5C378] text-[#090A0D] font-bold text-sm shadow-[0_4px_20px_rgba(229,195,120,0.35)] transition-all cursor-pointer active:scale-95"
          >
            <Calendar className="w-4 h-4" />
            <span>{isFa ? 'رزرو مستقیم سوئیت بالینی' : 'Request Clinical Suite'}</span>
          </button>

          <button
            onClick={onScrollToSlots}
            className="flex items-center gap-2 px-5 py-3 rounded-2xl bg-[#161822]/90 hover:bg-[#232736] border border-[#232736] hover:border-[#E5C378]/50 text-white font-medium text-sm transition-all cursor-pointer"
          >
            <Activity className="w-4 h-4 text-[#10B981]" />
            <span>{isFa ? 'مشاهده ماتریس یونیت‌های زنده' : 'View Real-time Matrix'}</span>
            <ChevronDown className="w-4 h-4 text-[#9CA3AF]" />
          </button>
        </div>

        {/* Live Key Metrics */}
        <div className="grid grid-cols-2 sm:grid-cols-3 gap-3 max-w-2xl pt-6 border-t border-[#232736]/80">
          <div className="p-3 rounded-xl bg-[#161822]/60 border border-[#232736]/60 backdrop-blur-sm">
            <div className="flex items-center gap-1.5 text-xs text-[#9CA3AF] mb-1">
              <Award className="w-3.5 h-3.5 text-[#E5C378]" />
              <span>{isFa ? 'مراکز معتبر در منطقه' : 'Verified Clinics'}</span>
            </div>
            <div className="text-lg font-bold text-white font-mono">
              {isFa
                ? `${toPersianDigits(selectedDistrict.activeClinicsCount)} مرکز فعال`
                : `${selectedDistrict.activeClinicsCount} Facilities`}
            </div>
          </div>

          <div className="p-3 rounded-xl bg-[#161822]/60 border border-[#232736]/60 backdrop-blur-sm">
            <div className="flex items-center gap-1.5 text-xs text-[#9CA3AF] mb-1">
              <Activity className="w-3.5 h-3.5 text-[#10B981]" />
              <span>{isFa ? 'یونیت‌های آزاد هم‌اکنون' : 'Available Suites'}</span>
            </div>
            <div className="text-lg font-bold text-[#10B981] font-mono">
              {isFa
                ? `${toPersianDigits(selectedDistrict.availableSlotsNow)} سوئیت آماده`
                : `${selectedDistrict.availableSlotsNow} Instant Ready`}
            </div>
          </div>

          <div className="col-span-2 sm:col-span-1 p-3 rounded-xl bg-[#161822]/60 border border-[#232736]/60 backdrop-blur-sm">
            <div className="flex items-center gap-1.5 text-xs text-[#9CA3AF] mb-1">
              <ShieldCheck className="w-3.5 h-3.5 text-[#38BDF8]" />
              <span>{isFa ? 'امنیت تراکنش' : 'Escrow Security'}</span>
            </div>
            <div className="text-lg font-bold text-[#38BDF8]">
              {isFa ? 'شاپرک و ممیزی' : '100% Regulated'}
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};
