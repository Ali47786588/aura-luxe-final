import React, { useState } from 'react';
import { useAuraLuxe } from '../../context/AuraLuxeContext';
import { formatPrice } from '../../utils/localization';
import { District } from '../../types';
import { X, MapPin, Check, Building2, Activity, Navigation } from 'lucide-react';

export const LocationSwitcherModal: React.FC = () => {
  const {
    language,
    districts,
    selectedDistrict,
    setDistrict,
    closeModal,
  } = useAuraLuxe();

  const isFa = language === 'FA';
  const [selectedCity, setSelectedCity] = useState<'tehran' | 'karaj'>(
    (selectedDistrict.cityId as 'tehran' | 'karaj') || 'tehran'
  );

  const cityDistricts = districts.filter((d) => d.cityId === selectedCity);

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4 overflow-y-auto animate-fadeIn">
      <div className="relative w-full max-w-lg bg-[#11131A] border border-[#E5C378]/40 rounded-3xl p-6 sm:p-8 shadow-2xl my-8">
        {/* Close Button */}
        <button
          onClick={closeModal}
          className="absolute top-5 left-5 w-8 h-8 rounded-full bg-[#161822] border border-[#232736] flex items-center justify-center text-[#9CA3AF] hover:text-white hover:bg-[#232736] transition-colors cursor-pointer"
        >
          <X className="w-4 h-4" />
        </button>

        {/* Modal Header */}
        <div className="mb-5">
          <div className="flex items-center gap-1.5 text-xs text-[#E5C378] font-mono uppercase mb-1">
            <MapPin className="w-3.5 h-3.5" />
            <span>{isFa ? 'تغییر منطقه فعالیت' : 'GEO-LOCATION SWITCHER'}</span>
          </div>
          <h2 className="text-xl font-bold text-white">
            {isFa ? 'انتخاب منطقه استقرار کلینیک' : 'Select Active Medical District'}
          </h2>
          <p className="text-xs text-[#9CA3AF] mt-1">
            {isFa
              ? 'سوئیت‌های جراحی و اپراتوری‌های در دسترس بر اساس منطقه فیلتر می‌شوند.'
              : 'Live operatories and facilities will adapt to your selected geographic zone.'}
          </p>
        </div>

        {/* City Switcher Tabs */}
        <div className="grid grid-cols-2 gap-2 p-1 rounded-2xl bg-[#090A0D] border border-[#232736] mb-5">
          <button
            onClick={() => setSelectedCity('tehran')}
            className={`py-2 rounded-xl text-xs font-bold transition-all cursor-pointer ${
              selectedCity === 'tehran'
                ? 'bg-[#E5C378] text-[#090A0D] shadow'
                : 'text-[#9CA3AF] hover:text-white'
            }`}
          >
            {isFa ? 'کلانشهر تهران' : 'Tehran Metropolitan'}
          </button>
          <button
            onClick={() => setSelectedCity('karaj')}
            className={`py-2 rounded-xl text-xs font-bold transition-all cursor-pointer ${
              selectedCity === 'karaj'
                ? 'bg-[#E5C378] text-[#090A0D] shadow'
                : 'text-[#9CA3AF] hover:text-white'
            }`}
          >
            {isFa ? 'استان البرز و کرج' : 'Karaj & Alborz'}
          </button>
        </div>

        {/* Districts List */}
        <div className="space-y-2.5 max-h-[380px] overflow-y-auto pr-1">
          {cityDistricts.map((d) => {
            const isCurrent = d.id === selectedDistrict.id;
            return (
              <button
                key={d.id}
                onClick={() => setDistrict(d)}
                className={`w-full p-4 rounded-2xl border text-right transition-all cursor-pointer flex items-center justify-between gap-3 ${
                  isCurrent
                    ? 'bg-[#E5C378]/10 border-[#E5C378]'
                    : 'bg-[#161822] border-[#232736] hover:border-[#E5C378]/50 hover:bg-[#1a1c29]'
                }`}
              >
                <div className="space-y-1">
                  <div className="flex items-center gap-2">
                    <span className="text-sm font-bold text-white">
                      {isFa ? d.nameFa : d.nameEn}
                    </span>
                    {isCurrent && (
                      <span className="px-2 py-0.5 rounded-full bg-[#E5C378] text-[#090A0D] text-[10px] font-bold">
                        {isFa ? 'منطقه فعال' : 'Active'}
                      </span>
                    )}
                  </div>
                  <div className="flex items-center gap-1.5 text-xs text-[#9CA3AF]">
                    <Navigation className="w-3 h-3 text-[#E5C378]" />
                    <span>{d.popularRoad}</span>
                  </div>
                  <div className="flex items-center gap-3 text-[11px] text-[#6B7280] font-mono pt-1">
                    <span>
                      {d.activeClinicsCount} {isFa ? 'مرکز فعال' : 'Centers'}
                    </span>
                    <span>•</span>
                    <span className="text-emerald-400 font-semibold">
                      {d.availableSlotsNow} {isFa ? 'سوئیت خالی' : 'Open Slots'}
                    </span>
                    <span>•</span>
                    <span>{formatPrice(d.averageHourlyToman, language)}/h</span>
                  </div>
                </div>

                <div
                  className={`w-7 h-7 rounded-full flex items-center justify-center border ${
                    isCurrent
                      ? 'bg-[#E5C378] border-[#E5C378] text-[#090A0D]'
                      : 'border-[#232736] text-transparent'
                  }`}
                >
                  <Check className="w-4 h-4 stroke-[3]" />
                </div>
              </button>
            );
          })}
        </div>
      </div>
    </div>
  );
};
