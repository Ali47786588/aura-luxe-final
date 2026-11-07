import React from 'react';
import { useAuraLuxe } from '../context/AuraLuxeContext';
import { Building2, Sparkles, ArrowRight, ArrowLeft } from 'lucide-react';

export const PartnerCalloutCard: React.FC = () => {
  const { language, openOnboarding } = useAuraLuxe();
  const isFa = language === 'FA';

  return (
    <div className="mx-4 lg:mx-8 my-6">
      <div className="relative rounded-3xl bg-gradient-to-r from-[#11131A] via-[#161822] to-[#11131A] border border-[#E5C378]/40 p-6 sm:p-10 shadow-2xl overflow-hidden">
        {/* Glow Element */}
        <div className="absolute top-0 right-0 w-80 h-80 bg-[#E5C378]/5 rounded-full filter blur-3xl pointer-events-none" />

        <div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-6">
          <div className="max-w-2xl space-y-3">
            <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-[#E5C378]/15 border border-[#E5C378]/30 text-[#E5C378] text-xs font-bold font-mono">
              <Sparkles className="w-3.5 h-3.5" />
              <span>{isFa ? 'شبکه اختصاصی مراکز و کلینیک‌ها' : 'B2B CLINICAL NETWORK'}</span>
            </div>

            <h3 className="text-xl sm:text-2xl font-extrabold text-white leading-snug">
              {isFa
                ? 'مالک یا مدیر کلینیک زیبایی، اتاق عمل جراحی یا سالن VIP هستید؟'
                : 'Are You a Medical Director or Luxury Aesthetics Facility Owner?'}
            </h3>

            <p className="text-xs sm:text-sm text-[#9CA3AF] leading-relaxed">
              {isFa
                ? 'با اشتراک‌گذاری تایم‌های آزاد یونیت‌های دندانپزشکی، تخت‌های جراحی و سوئیت‌های لیزر با پزشکان فوق‌تخصص تایید شده، ظرفیت خالی مرکز خود را به بالاترین بازدهی اقتصادی با تسویه امن اسکرو تبدیل نمایید.'
                : 'Monetize idle operatory hours and surgical suites with pre-verified aesthetic specialists. Guaranteed escrow settlements, zero paperwork, and full regulatory audit.'}
            </p>
          </div>

          <button
            onClick={openOnboarding}
            className="flex-shrink-0 px-6 py-3.5 rounded-2xl bg-gradient-to-r from-[#E5C378] to-[#C5A059] hover:from-[#FBF4E2] hover:to-[#E5C378] text-[#090A0D] font-bold text-xs sm:text-sm shadow-[0_4px_20px_rgba(229,195,120,0.3)] transition-all cursor-pointer flex items-center justify-center gap-2 active:scale-95"
          >
            <span>{isFa ? 'شروع فرآیند ثبت و اتصال مرکز' : 'Register Your Facility'}</span>
            {isFa ? <ArrowLeft className="w-4 h-4" /> : <ArrowRight className="w-4 h-4" />}
          </button>
        </div>
      </div>
    </div>
  );
};
