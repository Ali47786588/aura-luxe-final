import React from 'react';
import { useAuraLuxe } from '../context/AuraLuxeContext';
import { CategoryId, Venue } from '../types';
import { formatPrice } from '../utils/localization';
import {
  Sparkles,
  Star,
  MapPin,
  ShieldCheck,
  Award,
  Layers,
  Clock,
  ArrowRight,
  ArrowLeft,
  CalendarCheck,
} from 'lucide-react';

export const VenueCatalog: React.FC = () => {
  const {
    language,
    venues,
    selectedCategory,
    selectedDistrict,
    setCategory,
    openVenueDetails,
    openBooking,
  } = useAuraLuxe();

  const isFa = language === 'FA';

  const categories: { id: CategoryId; labelFa: string; labelEn: string }[] = [
    { id: 'ALL', labelFa: 'همه دسته‌ها', labelEn: 'All Suites' },
    { id: 'AESTHETICS', labelFa: 'پوست و لیزر', labelEn: 'Aesthetics & Laser' },
    { id: 'DENTAL', labelFa: 'دندانپزشکی و لبخند', labelEn: 'Dental Studios' },
    { id: 'SALONS', labelFa: 'سالن‌های VIP', labelEn: 'VIP Salons' },
  ];

  const filteredVenues = venues.filter((v) => {
    const catMatch = selectedCategory === 'ALL' || v.category === selectedCategory;
    const cityMatch = v.cityId === selectedDistrict.cityId;
    return catMatch && cityMatch;
  });

  return (
    <section className="mx-4 lg:mx-8 py-6">
      {/* Section Header */}
      <div className="flex flex-col sm:flex-row sm:items-end justify-between gap-4 mb-6">
        <div>
          <div className="flex items-center gap-2 text-[#E5C378] text-xs font-bold font-mono uppercase tracking-wider mb-1">
            <Sparkles className="w-4 h-4" />
            <span>{isFa ? 'مراکز برگزیده بالینی' : 'PREMIER CLINICAL CATALOG'}</span>
          </div>
          <h2 className="text-xl sm:text-2xl font-extrabold text-white">
            {isFa
              ? `کلینیک‌های لوکس و سوئیت‌های جراحی در ${selectedDistrict.nameFa}`
              : `Curated Facilities in ${selectedDistrict.nameEn}`}
          </h2>
          <p className="text-xs sm:text-sm text-[#9CA3AF] mt-1">
            {isFa
              ? 'مجهز به استانداردهای کلاس B و دارای تاییدیه فنی معاونت درمان'
              : 'Class-B sterilization verified and compliant with health ministry standards'}
          </p>
        </div>

        {/* Category Filter Pills */}
        <div className="flex items-center gap-1.5 p-1 rounded-2xl bg-[#11131A] border border-[#232736] overflow-x-auto">
          {categories.map((c) => {
            const isSelected = selectedCategory === c.id;
            return (
              <button
                key={c.id}
                onClick={() => setCategory(c.id)}
                className={`px-3.5 py-1.5 rounded-xl text-xs font-semibold whitespace-nowrap transition-all cursor-pointer ${
                  isSelected
                    ? 'bg-[#E5C378] text-[#090A0D] shadow-[0_2px_10px_rgba(229,195,120,0.3)]'
                    : 'text-[#9CA3AF] hover:text-white hover:bg-[#161822]'
                }`}
              >
                {isFa ? c.labelFa : c.labelEn}
              </button>
            );
          })}
        </div>
      </div>

      {/* Venues Grid */}
      {filteredVenues.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredVenues.map((venue) => (
            <VenueCard
              key={venue.id}
              venue={venue}
              isFa={isFa}
              language={language}
              onViewDetails={() => openVenueDetails(venue)}
              onBook={() => openBooking(venue)}
            />
          ))}
        </div>
      ) : (
        <div className="p-12 text-center rounded-3xl bg-[#11131A] border border-[#232736]">
          <Layers className="w-10 h-10 text-[#6B7280] mx-auto mb-3" />
          <h3 className="text-base font-bold text-white mb-1">
            {isFa
              ? 'مرکزی با این فیلتر در این منطقه ثبت نشده است'
              : 'No facilities found matching filter'}
          </h3>
          <p className="text-xs text-[#9CA3AF]">
            {isFa
              ? 'لطفاً دسته دیگری را انتخاب کنید یا منطقه را به شهر تهران/کرج تغییر دهید.'
              : 'Try changing category or switching your active district.'}
          </p>
        </div>
      )}
    </section>
  );
};

interface VenueCardProps {
  venue: Venue;
  isFa: boolean;
  language: 'FA' | 'EN';
  onViewDetails: () => void;
  onBook: () => void;
}

const VenueCard: React.FC<VenueCardProps> = ({
  venue,
  isFa,
  language,
  onViewDetails,
  onBook,
}) => {
  return (
    <div className="group rounded-3xl bg-[#11131A] border border-[#232736] hover:border-[#E5C378]/50 overflow-hidden transition-all duration-300 hover:shadow-[0_8px_30px_rgba(0,0,0,0.5)] flex flex-col justify-between">
      <div>
        {/* Cover Image Container */}
        <div className="relative h-48 overflow-hidden bg-[#161822]">
          <img
            src={venue.image}
            alt={isFa ? venue.nameFa : venue.nameEn}
            className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
          />
          <div className="absolute inset-0 bg-gradient-to-t from-[#11131A] via-transparent to-black/30" />

          {/* Rating Badge */}
          <div className="absolute top-3 left-3 flex items-center gap-1 px-2.5 py-1 rounded-full bg-[#090A0D]/85 backdrop-blur-md border border-[#232736] text-white text-xs font-bold font-mono">
            <Star className="w-3.5 h-3.5 text-[#E5C378] fill-[#E5C378]" />
            <span>{venue.rating.toFixed(2)}</span>
            <span className="text-[#9CA3AF] text-[10px]">({venue.reviewCount})</span>
          </div>

          {/* Active Slots Pill */}
          <div className="absolute top-3 right-3 px-2.5 py-1 rounded-full bg-[#10B981]/20 backdrop-blur-md border border-[#10B981]/40 text-[#10B981] text-[11px] font-semibold">
            {isFa ? `${venue.activeSlots} یونیت آنلاین` : `${venue.activeSlots} Active Units`}
          </div>

          {/* Category Tag */}
          <div className="absolute bottom-3 right-3 px-2 py-0.5 rounded-md bg-[#090A0D]/90 border border-white/10 text-[10px] text-[#E5C378] font-mono uppercase tracking-wider">
            {venue.category}
          </div>
        </div>

        {/* Content Body */}
        <div className="p-5">
          <div className="flex items-center gap-1 text-[11px] text-[#9CA3AF] mb-1.5">
            <MapPin className="w-3 h-3 text-[#E5C378]" />
            <span>{isFa ? venue.districtNameFa : venue.districtNameEn}</span>
            <span>•</span>
            <span className="truncate">{isFa ? venue.addressFa : venue.addressEn}</span>
          </div>

          <h3 className="text-base font-bold text-white group-hover:text-[#E5C378] transition-colors leading-snug mb-1">
            {isFa ? venue.nameFa : venue.nameEn}
          </h3>

          <p className="text-xs text-[#9CA3AF] line-clamp-2 mb-4 leading-relaxed">
            {isFa ? venue.taglineFa : venue.taglineEn}
          </p>

          {/* Key Equipment Pills */}
          <div className="flex flex-wrap gap-1.5 mb-4">
            {venue.equipmentList.slice(0, 3).map((eq, i) => (
              <span
                key={i}
                className="px-2 py-0.5 rounded-md bg-[#161822] border border-[#232736] text-[10px] text-[#9CA3AF]"
              >
                {eq}
              </span>
            ))}
          </div>

          {/* Hourly Rate & Licensing */}
          <div className="pt-3 border-t border-[#232736] flex items-center justify-between text-xs">
            <div>
              <span className="text-[10px] text-[#9CA3AF] block leading-none mb-1">
                {isFa ? 'نرخ پایه ساعتی:' : 'Base Hourly Rate:'}
              </span>
              <span className="font-bold text-[#E5C378] font-mono text-sm">
                {formatPrice(venue.baseHourlyToman, language)}
              </span>
            </div>
            <div className="text-right">
              <span className="text-[10px] text-[#9CA3AF] block leading-none mb-1">
                {isFa ? 'تعداد کل یونیت‌ها:' : 'Total Units:'}
              </span>
              <span className="font-bold text-white font-mono">
                {venue.totalChairs} {isFa ? 'صندلی/تخت' : 'Chairs'}
              </span>
            </div>
          </div>
        </div>
      </div>

      {/* Action Footer */}
      <div className="p-4 pt-0 grid grid-cols-2 gap-2">
        <button
          onClick={onViewDetails}
          className="w-full py-2.5 rounded-xl bg-[#161822] hover:bg-[#232736] border border-[#232736] text-xs font-semibold text-white transition-colors cursor-pointer flex items-center justify-center gap-1.5"
        >
          <span>{isFa ? 'جزییات کامل' : 'Details'}</span>
        </button>

        <button
          onClick={onBook}
          className="w-full py-2.5 rounded-xl bg-gradient-to-r from-[#E5C378] to-[#C5A059] hover:from-[#FBF4E2] hover:to-[#E5C378] text-xs font-bold text-[#090A0D] shadow-md transition-all cursor-pointer flex items-center justify-center gap-1.5 active:scale-95"
        >
          <CalendarCheck className="w-3.5 h-3.5" />
          <span>{isFa ? 'رزرو سوئیت' : 'Book Suite'}</span>
        </button>
      </div>
    </div>
  );
};
