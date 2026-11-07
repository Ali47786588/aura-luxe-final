import React from 'react';
import { useAuraLuxe } from '../context/AuraLuxeContext';
import { Sparkles, Shield, HeartHandshake, Lock, LifeBuoy } from 'lucide-react';

export const LuxuryFooter: React.FC = () => {
  const { language, openOnboarding, openSupport, openAdminLogin } = useAuraLuxe();
  const isFa = language === 'FA';

  return (
    <footer className="mt-16 border-t border-[#232736] bg-[#090A0D] text-[#9CA3AF] px-6 lg:px-12 py-12">
      <div className="max-w-7xl mx-auto space-y-10">
        <div className="flex flex-col md:flex-row md:items-start justify-between gap-8">
          {/* Brand Info */}
          <div className="space-y-3 max-w-sm">
            <div className="flex items-center gap-2">
              <div className="flex items-center justify-center w-8 h-8 rounded-lg bg-[#E5C378] text-[#090A0D] font-bold font-serif">
                A
              </div>
              <span className="font-serif text-lg font-bold text-white tracking-wider">
                AURA LUXE
              </span>
            </div>
            <p className="text-xs leading-relaxed text-[#6B7280]">
              {isFa
                ? 'پلتفرم مرجع اشتراک‌گذاری سوئیت‌های فوق‌پیشرفته درماتولوژی، اتاق‌های عمل زیبایی و یونیت‌های دندانپزشکی در مناطق دیپلماتیک تهران و کرج.'
                : 'Premier marketplace for luxury clinical suites, laser operatories, and dental ateliers in high-profile districts.'}
            </p>
          </div>

          {/* Quick Links */}
          <div className="grid grid-cols-2 sm:grid-cols-3 gap-8 text-xs">
            <div>
              <h4 className="font-bold text-white uppercase tracking-wider mb-3 font-mono text-[11px]">
                {isFa ? 'دسترسی سریع' : 'Navigation'}
              </h4>
              <ul className="space-y-2">
                <li>
                  <button
                    onClick={openOnboarding}
                    className="hover:text-[#E5C378] transition-colors cursor-pointer"
                  >
                    {isFa ? 'همکاری کلینیک‌ها و مراکز' : 'Partner Sign-Up'}
                  </button>
                </li>
                <li>
                  <button
                    onClick={openSupport}
                    className="hover:text-[#E5C378] transition-colors cursor-pointer"
                  >
                    {isFa ? 'مرکز پشتیبانی و استعلام پایا' : 'Support Desk'}
                  </button>
                </li>
                <li>
                  <button
                    onClick={openAdminLogin}
                    className="hover:text-[#E5C378] transition-colors cursor-pointer"
                  >
                    {isFa ? 'ورود مدیران مراکز و سوپرادمین' : 'Executive Console'}
                  </button>
                </li>
              </ul>
            </div>

            <div>
              <h4 className="font-bold text-white uppercase tracking-wider mb-3 font-mono text-[11px]">
                {isFa ? 'استانداردها' : 'Standards'}
              </h4>
              <ul className="space-y-2 text-[#6B7280]">
                <li>{isFa ? 'ایزو استریلیزاسیون ۱۳۴۸۵' : 'ISO 13485 Cleanroom'}</li>
                <li>{isFa ? 'تاییدیه معاونت درمان وزارت بهداشت' : 'Health Ministry Grade A+'}</li>
                <li>{isFa ? 'پروتکل فشار منفی لیزر' : 'Negative-Pressure Laser Air'}</li>
              </ul>
            </div>

            <div className="col-span-2 sm:col-span-1">
              <h4 className="font-bold text-white uppercase tracking-wider mb-3 font-mono text-[11px]">
                {isFa ? 'امنیت تراکنش' : 'Security'}
              </h4>
              <div className="p-3 rounded-xl bg-[#11131A] border border-[#232736] text-[11px] space-y-1.5">
                <div className="flex items-center gap-1.5 text-emerald-400 font-bold">
                  <Shield className="w-3.5 h-3.5" />
                  <span>{isFa ? 'حفاظت ۱۰۰٪ اسکرو' : '100% Escrow'}</span>
                </div>
                <p className="text-[#6B7280] leading-normal text-[10px]">
                  {isFa
                    ? 'تسویه مالی پس از ترخیص و امضای الکترونیک پزشک'
                    : 'Payout released strictly upon clinical discharge.'}
                </p>
              </div>
            </div>
          </div>
        </div>

        {/* Regulatory Disclaimer & Copyright */}
        <div className="pt-8 border-t border-[#232736]/60 flex flex-col sm:flex-row items-center justify-between gap-4 text-[11px] text-[#6B7280]">
          <p>
            {isFa
              ? '© ۲۰۲۶ تمامی حقوق معنوی و فناوری متعلق به سامانه اورا لوکس می‌باشد.'
              : '© 2026 AURA LUXE. All rights reserved. Architectural Medical Infrastructure.'}
          </p>
          <p className="font-mono text-[10px]">
            {isFa ? 'نسخه ۲.۴.۰ - سیستم یکپارچه وب' : 'v2.4.0 • Enterprise Web Edition'}
          </p>
        </div>
      </div>
    </footer>
  );
};
