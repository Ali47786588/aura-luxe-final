import React from 'react';
import { useAuraLuxe } from '../../context/AuraLuxeContext';
import { formatPrice } from '../../utils/localization';
import {
  X,
  Star,
  MapPin,
  ShieldCheck,
  Award,
  CalendarCheck,
  CheckCircle,
  Building2,
  Stethoscope,
} from 'lucide-react';

export const VenueDetailModal: React.FC = () => {
  const {
    language,
    selectedVenueForDetails,
    closeModal,
    openBooking,
  } = useAuraLuxe();

  const isFa = language === 'FA';
  const venue = selectedVenueForDetails;

  if (!venue) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/85 backdrop-blur-sm p-4 overflow-y-auto animate-fadeIn">
      <div className="relative w-full max-w-2xl bg-[#11131A] border border-[#E5C378]/40 rounded-3xl overflow-hidden shadow-2xl my-8">
        {/* Cover Photo */}
        <div className="relative h-60 w-full overflow-hidden bg-[#161822]">
          <img
            src={venue.image}
            alt={isFa ? venue.nameFa : venue.nameEn}
            className="w-full h-full object-cover"
          />
          <div className="absolute inset-0 bg-gradient-to-t from-[#11131A] via-transparent to-black/40" />

          {/* Close Button */}
          <button
            onClick={closeModal}
            className="absolute top-4 left-4 w-9 h-9 rounded-full bg-black/70 border border-white/20 flex items-center justify-center text-white hover:bg-black transition-colors cursor-pointer"
          >
            <X className="w-5 h-5" />
          </button>

          {/* Rating */}
          <div className="absolute bottom-4 left-4 flex items-center gap-1.5 px-3 py-1 rounded-full bg-[#090A0D]/90 backdrop-blur-md border border-[#232736] text-white text-xs font-bold font-mono">
            <Star className="w-4 h-4 text-[#E5C378] fill-[#E5C378]" />
            <span>{venue.rating.toFixed(2)}</span>
            <span className="text-[#9CA3AF]">({venue.reviewCount} {isFa ? 'نظر' : 'reviews'})</span>
          </div>
        </div>

        {/* Content Body */}
        <div className="p-6 sm:p-8 space-y-6 max-h-[60vh] overflow-y-auto">
          {/* Title & Medical Director */}
          <div>
            <div className="flex items-center gap-1.5 text-xs text-[#E5C378] mb-1">
              <MapPin className="w-3.5 h-3.5" />
              <span>{isFa ? venue.districtNameFa : venue.districtNameEn}</span>
              <span>•</span>
              <span>{isFa ? venue.addressFa : venue.addressEn}</span>
            </div>

            <h2 className="text-xl sm:text-2xl font-extrabold text-white mb-2">
              {isFa ? venue.nameFa : venue.nameEn}
            </h2>

            <div className="p-3 rounded-xl bg-[#161822] border border-[#232736] flex items-center gap-3 text-xs">
              <Stethoscope className="w-4 h-4 text-[#E5C378]" />
              <div>
                <span className="text-white font-semibold block">
                  {isFa ? venue.medicalDirectorFa : venue.medicalDirectorEn}
                </span>
                <span className="text-[11px] text-[#9CA3AF] font-mono">
                  {isFa ? 'شماره پروانه طبابت / مجوز:' : 'License Number:'} {venue.medicalLicenseNumber}
                </span>
              </div>
            </div>
          </div>

          {/* Description */}
          <div>
            <h3 className="text-xs font-bold text-[#E5C378] uppercase font-mono tracking-wider mb-2">
              {isFa ? 'درباره مرکز و زیرساخت بالینی' : 'CLINICAL INFRASTRUCTURE'}
            </h3>
            <p className="text-xs sm:text-sm text-[#9CA3AF] leading-relaxed">
              {isFa ? venue.descriptionFa : venue.descriptionEn}
            </p>
          </div>

          {/* Equipment Inventory */}
          <div>
            <h3 className="text-xs font-bold text-[#E5C378] uppercase font-mono tracking-wider mb-2">
              {isFa ? 'تجهیزات و دستگاه‌های مستقر' : 'EQUIPMENT INVENTORY'}
            </h3>
            <div className="flex flex-wrap gap-2">
              {venue.equipmentList.map((eq, i) => (
                <span
                  key={i}
                  className="px-3 py-1 rounded-xl bg-[#161822] border border-[#232736] text-xs text-white"
                >
                  {eq}
                </span>
              ))}
            </div>
          </div>

          {/* Certifications & Hygiene */}
          <div>
            <h3 className="text-xs font-bold text-[#E5C378] uppercase font-mono tracking-wider mb-2">
              {isFa ? 'مجوزها و تاییدیه‌های استریلیزاسیون' : 'CERTIFICATIONS & COMPLIANCE'}
            </h3>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
              {venue.certifications.map((c, i) => (
                <div
                  key={i}
                  className="p-2.5 rounded-xl bg-[#161822] border border-[#232736] flex items-center gap-2 text-xs text-[#10B981]"
                >
                  <ShieldCheck className="w-4 h-4 flex-shrink-0" />
                  <span>{c}</span>
                </div>
              ))}
            </div>
          </div>

          {/* Amenities */}
          <div>
            <h3 className="text-xs font-bold text-[#E5C378] uppercase font-mono tracking-wider mb-2">
              {isFa ? 'امکانات رفاهی و خدمات پشتیبانی' : 'AMENITIES & ASSISTANCE'}
            </h3>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
              {venue.amenities.map((am, i) => (
                <div
                  key={i}
                  className="p-2 rounded-xl bg-[#161822] border border-[#232736] flex items-center gap-2 text-xs text-[#9CA3AF]"
                >
                  <CheckCircle className="w-3.5 h-3.5 text-[#E5C378]" />
                  <span>{am}</span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Modal Action Footer */}
        <div className="p-6 bg-[#090A0D] border-t border-[#232736] flex items-center justify-between gap-4">
          <div>
            <span className="text-[10px] text-[#9CA3AF] block leading-none mb-1">
              {isFa ? 'تعرفه پایه ساعتی سوئیت:' : 'Base Hourly Suite Rate:'}
            </span>
            <span className="text-base font-bold text-[#E5C378] font-mono">
              {formatPrice(venue.baseHourlyToman, language)}
            </span>
          </div>

          <button
            onClick={() => {
              closeModal();
              openBooking(venue);
            }}
            className="px-6 py-3 rounded-xl bg-gradient-to-r from-[#E5C378] to-[#C5A059] hover:from-[#FBF4E2] hover:to-[#E5C378] text-[#090A0D] font-bold text-xs shadow-lg transition-all cursor-pointer flex items-center gap-2 active:scale-95"
          >
            <CalendarCheck className="w-4 h-4" />
            <span>{isFa ? 'رزرو سوئیت در این مرکز' : 'Book Suite at this Facility'}</span>
          </button>
        </div>
      </div>
    </div>
  );
};
